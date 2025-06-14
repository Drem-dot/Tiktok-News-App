package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

// Import cần thiết cho Content và LỚP CỤ THỂ TextPart
import com.example.news.adapter.ArticlesAdapter;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.TextPart; // IMPORT QUAN TRỌNG
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.news.data.AppDao;
import com.example.news.data.AppDatabase;
import com.example.news.data.KeywordEntity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "https://newsapi.org/";

    // View Components
    private ViewPager2 viewPagerArticles;
    private ProgressBar progressBar;

    // Data & Adapter
    private ArticlesAdapter adapter;
    private List<Article> articleList;

    // API Services
    private NewsApiService apiService;
    private GenerativeModel generativeModel;

    // Executor for Gemini API callbacks
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    // Room Database
    private AppDao appDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Toolbar setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.inflateMenu(R.menu.main_menu);

        // --- View Initialization ---
        viewPagerArticles = findViewById(R.id.viewPagerArticles);
        progressBar = findViewById(R.id.progressBar);

        // --- Adapter and Data Initialization ---
        articleList = new ArrayList<>();
        adapter = new ArticlesAdapter(this, articleList);
        viewPagerArticles.setAdapter(adapter);

        // --- Retrofit (NewsAPI) Initialization ---
        Interceptor userAgentInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build();
            return chain.proceed(requestWithUserAgent);
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(userAgentInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NewsApiService.class);

        // --- Gemini AI Initialization ---
        String geminiApiKey = "AIzaSyBDwPxcqmX0vrbM6rHtetR7QdXhwvRHOYw";
        generativeModel = new GenerativeModel("gemini-2.0-flash", geminiApiKey);

        // --- AppDao Initialization ---
        appDao = AppDatabase.getDatabase(getApplicationContext()).appDao();

        // --- Start a data fetching process ---
        fetchRealArticles();
    }

    private void fetchRealArticles() {
        progressBar.setVisibility(View.VISIBLE);
        viewPagerArticles.setVisibility(View.GONE);

        String newsApiKey = "310c49f0eb0e47bca6ef288316418c9a";
        apiService.getTopHeadlines("us", newsApiKey).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> fetchedArticles = response.body().getArticles();
                    if (fetchedArticles == null || fetchedArticles.isEmpty()) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Không có tin tức mới.", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    // Chạy logic đề xuất trên luồng nền
                    runRecommendationEngine(fetchedArticles);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "NewsAPI Call Failed", t);
                Toast.makeText(MainActivity.this, "Lỗi kết nối mạng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void runRecommendationEngine(List<Article> articles) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<KeywordEntity> topKeywords = appDao.getTopKeywordsNonLive();

            if (topKeywords != null && !topKeywords.isEmpty()) {
                for (Article article : articles) {
                    if (article.getTitle() == null) continue;
                    for (KeywordEntity keyword : topKeywords) {
                        if (article.getTitle().toLowerCase().contains(keyword.keyword)) {
                            article.relevanceScore += keyword.score;
                        }
                    }
                }
            }

            articles.sort((a1, a2) -> Integer.compare(a2.relevanceScore, a1.relevanceScore));

            // Sau khi sắp xếp, chuyển đến bước tóm tắt bằng Gemini
            summarizeArticlesWithGemini(articles);
        });
    }

    private void summarizeArticlesWithGemini(List<Article> articlesToSummarize) {
        if (articlesToSummarize == null || articlesToSummarize.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Không có bài báo nào được tìm thấy.", Toast.LENGTH_LONG).show();
            return;
        }

        AtomicInteger pendingSummaries = new AtomicInteger(articlesToSummarize.size());
        for (Article article : articlesToSummarize) {
            generateSummaryForArticle(article, pendingSummaries, articlesToSummarize);
        }
    }

    private void generateSummaryForArticle(Article article, AtomicInteger counter, List<Article> originalList) {
        String originalDescription = article.getSummary();
        if (originalDescription == null || originalDescription.trim().isEmpty()) {
            article.setSummary("Không có nội dung tóm tắt cho bài báo này.");
            checkIfAllSummariesAreDone(counter, originalList);
            return;
        }

        String prompt = "Tóm tắt đoạn văn sau thành một câu duy nhất thật hấp dẫn và lôi cuốn, bằng tiếng Việt: \"" + originalDescription + "\"";

        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(generativeModel);

        // =======================================================
        // ĐÂY LÀ PHIÊN BẢN SỬA LỖI CUỐI CÙNG
        // =======================================================
        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> responseFuture = modelFutures.generateContent(content);

        Futures.addCallback(responseFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result.getText() != null) {
                    article.setSummary(result.getText());
                } else {
                    article.setSummary(originalDescription);
                }
                checkIfAllSummariesAreDone(counter, originalList);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Lỗi khi tóm tắt bằng Gemini", t);
                article.setSummary(originalDescription);
                checkIfAllSummariesAreDone(counter, originalList);
            }
        }, backgroundExecutor);
    }

    private void checkIfAllSummariesAreDone(AtomicInteger counter, List<Article> finalizedArticles) {
        if (counter.decrementAndGet() == 0) {
            runOnUiThread(() -> {
                articleList.clear();
                articleList.addAll(finalizedArticles);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                viewPagerArticles.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Đã tải xong tin tức!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void handleApiError(Response<?> response) {
        progressBar.setVisibility(View.GONE);
        String errorBodyString = "Không đọc được nội dung lỗi.";
        if (response.errorBody() != null) {
            try {
                errorBodyString = response.errorBody().string();
            } catch (IOException e) {
                Log.e(TAG, "Không thể đọc errorBody", e);
            }
        }
        Log.e(TAG, "Yêu cầu API thất bại. Mã lỗi: " + response.code() + ". Nội dung: " + errorBodyString);
        Toast.makeText(MainActivity.this, "Không thể tải dữ liệu. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_saved_articles) {
            Intent intent = new Intent(this, SavedArticlesActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}