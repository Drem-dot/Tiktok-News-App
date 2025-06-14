package com.example.news; // Thay thế bằng package của bạn

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.news.adapter.SavedArticlesAdapter;
import com.example.news.data.AppDao;
import com.example.news.data.AppDatabase;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class SavedArticlesActivity extends AppCompatActivity {

    // 1. Khai báo các thuộc tính
    private RecyclerView recyclerView;
    private SavedArticlesAdapter adapter;
    private TextView emptyView;
    private AppDao appDao;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        // 2a. Liên kết các View
        toolbar = findViewById(R.id.savedToolbar);
        recyclerView = findViewById(R.id.savedRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // 2b. Cấu hình Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Bài Viết Đã Lưu");
        }

        // 2c. Khởi tạo RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2d. Khởi tạo Adapter và gán cho RecyclerView
        // Bắt đầu với một danh sách rỗng
        adapter = new SavedArticlesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2e. Lấy instance của AppDao
        appDao = AppDatabase.getDatabase(getApplicationContext()).appDao();

        // 2f. Quan sát dữ liệu từ database
        observeSavedArticles();
    }

    private void observeSavedArticles() {
        // Giả sử phương thức này trả về LiveData<List<ArticleInteractionEntity>>
        appDao.getAllSavedArticles().observe(this, savedArticles -> {
            // 2g. Cập nhật UI dựa trên dữ liệu
            if (savedArticles == null || savedArticles.isEmpty()) {
                // Nếu danh sách rỗng, hiển thị thông báo
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // Nếu có dữ liệu, hiển thị RecyclerView
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                // Cập nhật dữ liệu cho adapter
                adapter.setArticles(savedArticles);
            }
        });
    }

    // 3. Xử lý sự kiện click trên các item của Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý sự kiện click nút Back (Home)
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại và quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}