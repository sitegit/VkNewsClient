package com.example.vknewsclient.data.repository

import android.app.Application
import android.util.Log
import com.example.vknewsclient.data.mapper.NewsFeedMapper
import com.example.vknewsclient.data.network.ApiFactory
import com.example.vknewsclient.domain.FeedPost
import com.example.vknewsclient.domain.PostComment
import com.example.vknewsclient.domain.StatisticItem
import com.example.vknewsclient.domain.StatisticType
import com.example.vknewsclient.extentions.mergeWith
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn

// Класс репозитория для работы с новостной лентой
class NewsFeedRepository(application: Application) {

    // Маппер для преобразования данных ответа API в модели данных приложения
    private val mapper = NewsFeedMapper()

    // Сервис для выполнения API запросов
    private val apiService = ApiFactory.apiService

    // Хранилище для ключей и значений, используемое для работы с токенами VK
    private val storage = VKPreferencesKeyValueStorage(application)

    // Восстановление токена доступа VK из хранилища
    private val token = VKAccessToken.restore(storage)

    // Область видимости для корутин, использующая диспетчер по умолчанию
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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

    // Внутренний список постов новостной ленты
    private val _feedPosts = mutableListOf<FeedPost>()

    // Публичный список постов новостной ленты
    private val feedPosts: List<FeedPost>
        get() = _feedPosts.toList()

    // Переменная для отслеживания позиции следующей загрузки данных
    private var nextFrom: String? = null

    // Поток состояния для предоставления рекомендаций
    val recommendations: StateFlow<List<FeedPost>> = loadedListFlow
        .mergeWith(refreshedListFlow)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = feedPosts
        )

    // Функция для загрузки следующего блока данных
    suspend fun loadNextData() {
        nextDataNeededEvents.emit(Unit)
    }

    // Функция для удаления поста из ленты
    suspend fun deletePost(feedPost: FeedPost) {
        apiService.ignorePost(
            token = getAccessToken(),
            ownerId = feedPost.communityId,
            postId = feedPost.id
        )
        _feedPosts.remove(feedPost)
        refreshedListFlow.emit(feedPosts)
    }

    // Функция для получения комментариев к посту
    fun getComments(feedPost: FeedPost): Flow<List<PostComment>> = flow {
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
    }


    // Функция для изменения статуса лайка у поста
    suspend fun changeLikeStatus(feedPost: FeedPost) {
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
