package com.example.news.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Article;
import com.example.news.ArticleDetailActivity;
import com.example.news.R;
import com.example.news.data.AppDao;
import com.example.news.data.AppDatabase;
import com.example.news.data.ArticleInteractionEntity;
import com.example.news.data.KeywordEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private final List<Article> articleList;
    private final AppDao appDao;

    public ArticlesAdapter(Context context, List<Article> articleList) {
        this.articleList = articleList;
        this.appDao = AppDatabase.getDatabase(context).appDao();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        if (article == null) return;

        holder.titleTextView.setText(article.getTitle());
        holder.summaryTextView.setText(article.getSummary());

        // --- Xử lý mở bài viết chi tiết ---
        holder.readMoreTextView.setOnClickListener(v -> {
            // ... (code không đổi)
        });

        // =======================================================
        // 1. OBSERVE DATABASE ĐỂ CẬP NHẬT GIAO DIỆN
        // =======================================================
        LifecycleOwner lifecycleOwner = (LifecycleOwner) holder.itemView.getContext();

        // Giả sử isArticleSaved giờ trả về LiveData<ArticleInteractionEntity>
        appDao.isArticleSaved(article.getUrl()).observe(lifecycleOwner, interaction -> {
            // --- Cập nhật nút THÍCH (Like) ---
            if (interaction != null && interaction.isLiked) {
                holder.likeButton.setImageResource(R.drawable.ic_heart_filled);
                holder.likeButton.setTag("liked");
            } else {
                holder.likeButton.setImageResource(R.drawable.ic_heart_outline);
                holder.likeButton.setTag("unliked");
            }

            // --- Cập nhật nút LƯU (Save) ---
            if (interaction != null && interaction.isSaved) {
                holder.saveButton.setImageResource(R.drawable.ic_bookmark_filled);
                holder.saveButton.setTag("saved");
            } else {
                holder.saveButton.setImageResource(R.drawable.ic_bookmark_outline);
                holder.saveButton.setTag("unsaved");
            }
        });

        // =======================================================
        // 2. ONCLICKLISTENER CHO NÚT LIKE (ĐỘC LẬP)
        // =======================================================
        holder.likeButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                ArticleInteractionEntity interaction = appDao.findArticleNonLive(article.getUrl());

                // Nếu chưa có tương tác nào với bài này
                if (interaction == null) {
                    interaction = new ArticleInteractionEntity();
                    interaction.url = article.getUrl();
                    interaction.title = article.getTitle();
                    interaction.description = article.getSummary();
                    interaction.urlToImage = article.getUrlToImage();
                    interaction.isLiked = true;  // Hành động là Like
                    interaction.isSaved = false; // Không ảnh hưởng đến Save
                    appDao.saveArticle(interaction); // Dùng insert (OnConflict.REPLACE)
                    updateKeywordScores(article.getTitle(), true); // Tăng điểm
                } else {
                    // Nếu đã có tương tác, chỉ cần đảo ngược trạng thái Like
                    interaction.isLiked = !interaction.isLiked;
                    appDao.updateSavedArticle(interaction); // Cập nhật bản ghi

                    // Cập nhật điểm từ khóa dựa trên trạng thái mới
                    updateKeywordScores(article.getTitle(), interaction.isLiked);
                }
            });
        });

        // =======================================================
        // 3. ONCLICKLISTENER CHO NÚT SAVE (ĐỘC LẬP)
        // =======================================================
        holder.saveButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                ArticleInteractionEntity interaction = appDao.findArticleNonLive(article.getUrl());

                // Nếu chưa có tương tác nào với bài này
                if (interaction == null) {
                    interaction = new ArticleInteractionEntity();
                    interaction.url = article.getUrl();
                    interaction.title = article.getTitle();
                    interaction.description = article.getSummary();
                    interaction.urlToImage = article.getUrlToImage();
                    interaction.isSaved = true;  // Hành động là Save
                    interaction.isLiked = false; // Không ảnh hưởng đến Like
                    appDao.saveArticle(interaction); // Dùng insert (OnConflict.REPLACE)
                } else {
                    // Nếu đã có tương tác, chỉ cần đảo ngược trạng thái Save
                    interaction.isSaved = !interaction.isSaved;
                    appDao.updateSavedArticle(interaction); // Cập nhật bản ghi
                }
            });
        });

        holder.readMoreTextView.setOnClickListener(v -> {
            // 1. Lấy đối tượng Article hiện tại (đã có)

            // 2. Lấy url từ đối tượng đó
            String url = article.getUrl();

            // Kiểm tra url có hợp lệ không
            if (url != null && !url.trim().isEmpty()) {
                Context context = holder.itemView.getContext();

                // 3. Tạo một Intent để mở ArticleDetailActivity
                Intent intent = new Intent(context, ArticleDetailActivity.class);

                // 4. Đính kèm url vào Intent với key là "EXTRA_URL"
                // Sử dụng hằng số từ ArticleDetailActivity là tốt nhất
                intent.putExtra(ArticleDetailActivity.EXTRA_URL, url);

                // 5. Gọi startActivity để mở màn hình chi tiết
                context.startActivity(intent);
            }
        });
    }

    private void updateKeywordScores(String title, boolean increment) {
        if (title == null || title.trim().isEmpty()) return;

        String[] keywords = title.toLowerCase().split("\\s+");
        for (String keyword : keywords) {
            String cleanKeyword = keyword.replaceAll("[^a-zA-Z0-9]", "");
            if (cleanKeyword.length() > 3) {
                KeywordEntity existingKeyword = appDao.findKeyword(cleanKeyword);
                if (existingKeyword != null) {
                    if (increment) {
                        existingKeyword.score++;
                    } else {
                        if (existingKeyword.score > 0) existingKeyword.score--;
                    }
                    appDao.insertOrUpdateKeyword(existingKeyword);
                } else if (increment) {
                    appDao.insertOrUpdateKeyword(new KeywordEntity(cleanKeyword, 1));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImageView, likeButton, saveButton;
        TextView titleTextView, summaryTextView, readMoreTextView;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImageView = itemView.findViewById(R.id.articleImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            readMoreTextView = itemView.findViewById(R.id.readMoreTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}