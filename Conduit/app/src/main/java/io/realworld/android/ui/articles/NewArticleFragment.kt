package io.realworld.android.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.realworld.android.R
import io.realworld.android.databinding.FragmentPublishArtcleBinding

class NewArticleFragment : Fragment() {

    private var _binding: FragmentPublishArtcleBinding? = null
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublishArtcleBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            publishButton.setOnClickListener {
                articleViewModel.createArticle(
                    title = newArticleTitle.text.toString().takeIf { it.isNotBlank() },
                    description = newArticleBrief.text.toString(),
                    body = newArticleBrief.text.toString()
                )
                findNavController().navigate(
                    R.id.action_nav_new_article_to_nav_feed
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}