package com.example.news.data; // Thay thế bằng package của bạn

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity này đại diện cho một bản ghi trong bảng "user_interactions".
 * Nó lưu trữ tất cả thông tin về một bài báo và trạng thái tương tác của người dùng với nó (Thích, Lưu).
 */
@Entity(tableName = "user_interactions")
public class ArticleInteractionEntity {

    /**
     * URL của bài báo, dùng làm khóa chính để đảm bảo mỗi bài báo chỉ có một bản ghi tương tác duy nhất.
     */
    @PrimaryKey
    @NonNull
    public String url;

    /**
     * Tiêu đề của bài báo.
     */
    public String title;

    /**
     * Mô tả/Tóm tắt của bài báo.
     */
    public String description;

    /**
     * Đường dẫn đến ảnh thumbnail của bài báo.
     */
    public String urlToImage;

    /**
     * Cờ (flag) để xác định xem người dùng có "Thích" bài báo này hay không.
     * Mặc định là false (chưa thích).
     */
    public boolean isLiked = false;

    /**
     * Cờ (flag) để xác định xem người dùng có "Lưu để đọc sau" bài báo này hay không.
     * Mặc định là false (chưa lưu).
     */
    public boolean isSaved = false;

    /**
     * Constructor mặc định (rỗng) là bắt buộc để Room có thể khởi tạo đối tượng Entity.
     */
    public ArticleInteractionEntity() {
    }
}