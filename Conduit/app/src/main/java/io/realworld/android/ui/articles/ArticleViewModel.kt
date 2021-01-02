package io.realworld.android.ui.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realworld.android.data.ArticlesRepo
import io.realworld.api.models.entities.Article
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {

    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article> = _article

    fun fetchArticle(slug: String) = viewModelScope.launch {
        ArticlesRepo.fetchArticle(slug)?.let {
            _article.postValue(it)
        }
    }
}