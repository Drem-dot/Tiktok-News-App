package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "EXTRA_URL"; // Key để lấy URL từ Intent

    // 1. Khai báo các thuộc tính
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2a. Liên kết với layout
        setContentView(R.layout.activity_article_detail);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.detailProgressBar);

        // 2b. Lấy URL từ Intent
        String url = getIntent().getStringExtra(EXTRA_URL);

        // 2c. Kiểm tra URL có hợp lệ không
        if (url != null && !url.isEmpty()) {
            // 2d. Cấu hình WebView
            webView.getSettings().setJavaScriptEnabled(true);

            // 2e. Thiết lập WebViewClient tùy chỉnh
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    // Hiển thị ProgressBar khi trang bắt đầu tải
                    progressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE); // Dùng INVISIBLE để WebView giữ nguyên vị trí
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Ẩn ProgressBar và hiện WebView khi trang tải xong
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            });

            // 2f. Bắt đầu tải URL
            webView.loadUrl(url);

        } else {
            // Xử lý trường hợp URL không hợp lệ
            Toast.makeText(this, "Không thể mở bài viết. Đường dẫn không hợp lệ.", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity nếu không có URL
        }
    }

    // 3. Xử lý nút Back của hệ thống
    @Override
    public void onBackPressed() {
        // Nếu WebView có thể quay lại trang trước trong lịch sử của nó
        if (webView.canGoBack()) {
            // thì thực hiện việc quay lại đó
            webView.goBack();
        } else {
            // Nếu không, thực hiện hành động Back mặc định (đóng Activity)
            super.onBackPressed();
        }
    }
}