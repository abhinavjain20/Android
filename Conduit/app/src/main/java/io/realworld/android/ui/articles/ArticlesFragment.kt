package io.realworld.android.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.realworld.android.R
import io.realworld.android.databinding.FragmentArticlesBinding
import io.realworld.android.ui.extensions.loadImage
import io.realworld.android.ui.extensions.timeStamp

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

        articleId?.let {
            articleViewModel.fetchArticle(it)
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val followButton = _binding?.followButton
        articleViewModel.article.observe({ lifecycle }) {
            _binding?.followButton?.setOnClickListener { view ->
                articleViewModel.followProfile(it.author.username)
                followButton?.text = "Unfollow"
            }
            _binding?.apply {
                titleTextView.text = it.title
                authorTextView.text = it.author.username
                bodyTextView.text = it.body
                dateTextView.timeStamp = it.createdAt
                avatarImageView.loadImage(it.author.image, true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}