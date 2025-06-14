package com.example.news;
import com.google.gson.annotations.SerializedName;

public class Article {

    public int relevanceScore = 0; // Điểm số liên quan, mặc định là 0
    // Các thuộc tính private
    @SerializedName("title") // Thêm annotation cho các trường khác cũng là một thói quen tốt
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("urlToImage")
    private String urlToImage;

    /**
     * Thuộc tính này sẽ được ánh xạ từ key "description" trong JSON
     * nhờ vào annotation @SerializedName.
     */
    @SerializedName("description")
    private String summary;

    // Constructor nhận vào tất cả các thuộc tính
    public Article(String title, String url, String urlToImage, String summary) {
        this.title = title;
        this.url = url;
        this.urlToImage = urlToImage;
        this.summary = summary;
    }

    // Các phương thức Getter và Setter

    // Getter và Setter cho 'title'
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho 'url'
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter và Setter cho 'urlToImage'
    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    // Getter và Setter cho 'summary'
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}