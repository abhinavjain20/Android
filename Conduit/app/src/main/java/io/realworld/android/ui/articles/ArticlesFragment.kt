package io.realworld.android.ui.articles

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.realworld.android.R
import io.realworld.android.databinding.FragmentArticlesBinding

class ArticlesFragment : Fragment() {
    private var _binding: FragmentArticlesBinding? = null
    private lateinit var articleViewModel: ArticleViewModel
    private var articleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        arguments?.let {
            articleId = it.getString(resources.getString(R.string.arg_article_id))
        }

        articleId?.let { articleViewModel.fetchArticle(it) }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleViewModel.article.observe({ lifecycle }) {
            _binding?.apply {
                titleTextView.text = it.title
                authorTextView.text = it.author.username
                bodyTextView.text = it.body
                dateTextView.text = it.createdAt //TODO ::  format Date
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}