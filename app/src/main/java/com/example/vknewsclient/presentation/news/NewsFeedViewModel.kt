package com.example.vknewsclient.presentation.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.usecase.ChangeLikeStatusUseCase
import com.example.vknewsclient.domain.usecase.DeletePostUseCase
import com.example.vknewsclient.domain.usecase.GetRecommendationsUseCase
import com.example.vknewsclient.domain.usecase.LoadNextDataUseCase
import com.example.vknewsclient.extentions.mergeWith
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsFeedViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val loadNextDataUseCase: LoadNextDataUseCase,
    private val changeLikeStatusUseCase: ChangeLikeStatusUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    // Обработчик исключений для корутин, логирующий исключения
    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        Log.i("Throwable", "Exception caught by exception handler")
    }

    // Поток рекомендаций из репозитория
    private val recommendationsFlow = getRecommendationsUseCase()

    // Поток для управления состоянием загрузки данных
    private val loadNextDataFlow = MutableSharedFlow<NewsFeedScreenState>()

    // Поток состояния экрана, обрабатывающий новостную ленту и загрузку данных
    val screenState = recommendationsFlow
        .filter { it.isNotEmpty() } // Фильтрация пустых результатов
        .map { NewsFeedScreenState.Posts(posts = it) as NewsFeedScreenState } // Преобразование данных в состояние экрана
        .onStart { emit(NewsFeedScreenState.Loading) } // Инициализация состояния загрузки
        .mergeWith(loadNextDataFlow) // Объединение с потоком загрузки данных

    // Функция для загрузки следующих рекомендаций
    fun loadNextRecommendations() {
        viewModelScope.launch {
            // Эмитирование состояния загрузки следующих данных
            loadNextDataFlow.emit(
                NewsFeedScreenState.Posts(
                    posts = recommendationsFlow.value,
                    nextDataIsLoading = true
                )
            )
            // Запрос на загрузку следующих данных через репозиторий
            loadNextDataUseCase()
        }
    }

    // Функция для изменения статуса лайка у поста
    fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            // Делегирование операции изменения лайка репозиторию
            changeLikeStatusUseCase(feedPost)
        }
    }

    // Функция для удаления поста из ленты
    fun removePost(feedPost: FeedPost) {
        viewModelScope.launch(exceptionHandler) {
            // Делегирование операции удаления поста репозиторию
            deletePostUseCase(feedPost)
        }
    }
}
