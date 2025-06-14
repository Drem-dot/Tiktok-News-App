package com.example.news.adapter; // Thay thế bằng package của bạn

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.ArticleDetailActivity;
import com.example.news.R;
import com.example.news.data.ArticleInteractionEntity;

import java.util.List;

public class SavedArticlesAdapter extends RecyclerView.Adapter<SavedArticlesAdapter.ViewHolder> {

    private List<ArticleInteractionEntity> savedArticles;

    public SavedArticlesAdapter(List<ArticleInteractionEntity> savedArticles) {
        this.savedArticles = savedArticles;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final TextView summaryTextView;
        final ImageView likeButton;
        final ImageView saveButton;
        final TextView readMoreTextView;
        final View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            readMoreTextView = itemView.findViewById(R.id.readMoreTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    // =======================================================
    // ** ONBINDVIEWHOLDER ĐÃ ĐƯỢC CẬP NHẬT Ở ĐÂY **
    // =======================================================
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleInteractionEntity article = savedArticles.get(position);
        if (article == null) return;

        // Gán dữ liệu văn bản
        holder.titleTextView.setText(article.title);
        holder.summaryTextView.setText(article.description);

        // Ẩn các nút không cần thiết
        holder.likeButton.setVisibility(View.GONE);
        holder.saveButton.setVisibility(View.GONE);

        // Hiển thị nút "Đọc bài viết gốc" để người dùng có thể click
        holder.readMoreTextView.setVisibility(View.VISIBLE);

        // 1. Xóa bỏ OnClickListener của itemView
        // Đặt một listener rỗng hoặc null để đảm bảo nó không còn hành vi cũ (nếu cần)
        holder.itemView.setOnClickListener(null);

        // 2. Thiết lập OnClickListener cho readMoreTextView
        holder.readMoreTextView.setOnClickListener(v -> {
            // 3. Giữ nguyên logic mở ArticleDetailActivity
            String url = article.url;
            if (url != null && !url.isEmpty()) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.EXTRA_URL, url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedArticles != null ? savedArticles.size() : 0;
    }

    public void setArticles(List<ArticleInteractionEntity> articles) {
        this.savedArticles = articles;
        notifyDataSetChanged();
    }
}