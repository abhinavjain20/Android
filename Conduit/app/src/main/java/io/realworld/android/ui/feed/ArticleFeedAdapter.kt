package io.realworld.android.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.realworld.android.databinding.ListItemArticleBinding
import io.realworld.android.ui.extensions.loadImage
import io.realworld.android.ui.extensions.timeStamp
import io.realworld.api.models.entities.Article

class ArticleFeedAdapter(
    val onArticlesClick: (slug: String) -> Unit
) :
    ListAdapter<Article, ArticleFeedAdapter.ArticleViewHolder>(ArticleDiffUtilCallback()) {
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class ArticleDiffUtilCallback :
        DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ListItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        ListItemArticleBinding.bind(holder.itemView).apply {
            val article = getItem(position)
            authorTextView.text = article.author.username
            titleTextView.text = article.title
            bodySnippetTextView.text = article.body
            dateTextView.timeStamp = article.createdAt
            avatarImageView.loadImage(article.author.image, true)

            root.setOnClickListener { onArticlesClick(article.slug) }
        }
    }
}

