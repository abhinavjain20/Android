package io.realworld.android.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.realworld.android.AuthViewModel
import io.realworld.android.R
import io.realworld.android.databinding.FragmentArticlesBinding
import io.realworld.android.ui.extensions.loadImage
import io.realworld.android.ui.extensions.timeStamp

class ArticlesFragment : Fragment() {

    private var _binding: FragmentArticlesBinding? = null
    private lateinit var articleViewModel: ArticleViewModel
    private var articleId: String? = null
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            articleId = it.getString(resources.getString(R.string.arg_article_id))
        }
        articleId?.let {
            articleViewModel.fetchArticle(it)
        }
        val followButton = _binding?.followButton

        authViewModel.user.observe({ lifecycle }) {
            it?.token?.let {
                articleViewModel.article.observe({ lifecycle }) {
                    _binding?.followButton?.setOnClickListener { v ->
                        articleViewModel.followProfile(it.author.username)
                        followButton?.text = "Unfollow ${it.author.username}"
                    }
                }
            } ?: run {
                _binding?.followButton?.setOnClickListener {
                    findNavController().navigate(R.id.action_follow_to_auth)
                }
            }
        }

        articleViewModel.article.observe({ lifecycle }) {
            _binding?.apply {
                titleTextView.text = it.title
                authorTextView.text = it.author.username
                bodyTextView.text = it.body
                dateTextView.timeStamp = it.createdAt
                avatarImageView.loadImage(it.author.image, true)
                followButton?.isVisible = true
                followButton?.text = "Follow ${it.author.username}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}