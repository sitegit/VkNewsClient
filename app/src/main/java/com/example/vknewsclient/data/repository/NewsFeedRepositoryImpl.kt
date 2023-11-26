package com.example.vknewsclient.data.repository

import android.util.Log
import com.example.vknewsclient.data.mapper.NewsFeedMapper
import com.example.vknewsclient.data.network.ApiService
import com.example.vknewsclient.domain.entity.AuthState
import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.entity.PostComment
import com.example.vknewsclient.domain.entity.StatisticItem
import com.example.vknewsclient.domain.entity.StatisticType
import com.example.vknewsclient.domain.repository.NewsFeedRepository
import com.example.vknewsclient.extentions.mergeWith
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class NewsFeedRepositoryImpl @Inject constructor(
    private val mapper: NewsFeedMapper,
    private val apiService: ApiService,
    private val storage: VKPreferencesKeyValueStorage
) : NewsFeedRepository {

    // Восстановление токена доступа VK из хранилища
    private val token = VKAccessToken.restore(storage)

    // Область видимости для корутин, использующая диспетчер по умолчанию
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // Внутренний список постов новостной ленты
    private val _feedPosts = mutableListOf<FeedPost>()

    // Публичный список постов новостной ленты
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    // Переменная для отслеживания позиции следующей загрузки данных
    private var nextFrom: String? = null

    // Поток для управления событиями запроса новых данных
    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)

    // Поток для обновленного списка постов
    private val refreshedListFlow = MutableSharedFlow<List<FeedPost>>()

    // Поток для управления загруженным списком постов
    private val loadedListFlow = flow {
        // Триггер для начала загрузки данных
        nextDataNeededEvents.emit(Unit)
        // Обработка событий запроса новых данных
        nextDataNeededEvents.collect {
            val startFrom = nextFrom

            // Проверка на необходимость загрузки новых данных
            if (startFrom == null && feedPosts.isNotEmpty()) {
                emit(feedPosts)
                return@collect
            }

            // Загрузка рекомендаций из API
            val response = if (startFrom == null) {
                apiService.loadRecommendations(getAccessToken())
            } else {
                apiService.loadRecommendations(getAccessToken(), startFrom)
            }

            // Обновление позиции для следующей загрузки
            nextFrom = response.newsFeedContent.nextFrom

            // Преобразование данных ответа в список постов
            val posts = mapper.mapResponseToPosts(response)
            _feedPosts.addAll(posts)
            emit(feedPosts)
        }
    }.retry {
        // Задержка перед повторной попыткой запроса в случае возникновения исключения
        delay(RETRY_TIMEOUT_MILLIS)
        // Логирование информации об исключении
        Log.i("Throwable", it.toString())
        // Возврат true для повторения запроса
        true
    }

    private val checkAuthStateEvents = MutableSharedFlow<Unit>(replay = 1)

    private val authStateFlow = flow {
        checkAuthStateEvents.emit(Unit)
        checkAuthStateEvents.collect {
            val currentToken = token
            val loggedIn = currentToken != null && currentToken.isValid
            val authState = if (loggedIn) AuthState.Authorized else AuthState.NotAuthorized
            emit(authState)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = AuthState.Initial
    )

    // Поток состояния для предоставления рекомендаций
    private val recommendations: StateFlow<List<FeedPost>> = loadedListFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = feedPosts
        )

    override fun getAuthStateFlow() = authStateFlow

    override fun getRecommendations() = recommendations

    // Функция для загрузки следующего блока данных
    override suspend fun loadNextData() {
        nextDataNeededEvents.emit(Unit)
    }

    override suspend fun checkAuthState() {
        checkAuthStateEvents.emit(Unit)
    }

    // Функция для удаления поста из ленты
    override suspend fun deletePost(feedPost: FeedPost) {
        apiService.ignorePost(
            token = getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        _feedPosts.remove(feedPost)
        refreshedListFlow.emit(feedPosts)
    }

    // Функция для получения комментариев к посту
    override fun getComments(feedPost: FeedPost): StateFlow<List<PostComment>> = flow {
        // Вызов API для получения комментариев к конкретному посту
        val comments = apiService.getComments(
            token = getAccessToken(), // Получение токена доступа
            ownerId = feedPost.communityId, // ID владельца поста
            postId = feedPost.id // ID поста
        )
        // Эмиссия списка комментариев, преобразованных из ответа API
        emit(mapper.commentsResponseToPostComments(comments))
    }.retry {
        // Задержка перед повторной попыткой запроса в случае возникновения исключения
        delay(RETRY_TIMEOUT_MILLIS)
        // Логирование информации об исключении
        Log.i("Throwable", it.toString())
        // Возврат true для повторения запроса
        true
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )


    // Функция для изменения статуса лайка у поста
    override suspend fun changeLikeStatus(feedPost: FeedPost) {
        val response = if (feedPost.isLiked) {
            apiService.deleteLike(
                token = getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        } else {
            apiService.addLike(
                token = getAccessToken(),
                ownerId = feedPost.communityId,
                postId = feedPost.id
            )
        }

        // Обновление количества лайков и статистики поста
        val newLikesCount = response.likes.count
        val newStatistics = feedPost.statistics.toMutableList().apply {
            removeIf { it.type == StatisticType.LIKES }
            add(StatisticItem(type = StatisticType.LIKES, count = newLikesCount))
        }
        val newPost = feedPost.copy(statistics = newStatistics, isLiked = !feedPost.isLiked)
        val postIndex = _feedPosts.indexOf(feedPost)
        _feedPosts[postIndex] = newPost
        refreshedListFlow.emit(feedPosts)
    }

    // Функция для получения токена доступа
    private fun getAccessToken(): String {
        return token?.accessToken ?: throw IllegalStateException("Token is null")
    }

    companion object {
        const val RETRY_TIMEOUT_MILLIS: Long = 3000
    }
}
