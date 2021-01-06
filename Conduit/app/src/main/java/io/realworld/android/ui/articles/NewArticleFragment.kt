package io.realworld.android.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.realworld.android.databinding.FragmentUserArtcleBinding

class NewArticleFragment : Fragment() {

    private var _binding: FragmentUserArtcleBinding? = null
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserArtcleBinding.inflate(inflater, container, false)
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}