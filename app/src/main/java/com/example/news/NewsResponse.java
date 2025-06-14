package com.example.news;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Lớp này đại diện cho cấu trúc phản hồi JSON từ NewsAPI.
 * Nó được thiết kế để sử dụng với thư viện Gson để tự động phân tích cú pháp (parse).
 */
public class NewsResponse {

    /**
     * Thuộc tính này chứa danh sách các bài báo.
     * @SerializedName("articles") đảm bảo rằng Gson sẽ tìm kiếm key "articles"
     * trong chuỗi JSON và ánh xạ dữ liệu của nó vào danh sách này.
     */
    @SerializedName("articles")
    private List<Article> articles;

    // Getter cho danh sách bài báo
    public List<Article> getArticles() {
        return articles;
    }

    // Setter cho danh sách bài báo
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}