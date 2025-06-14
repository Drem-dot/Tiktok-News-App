package com.example.news;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface này định nghĩa các điểm cuối (endpoints) của NewsAPI
 * để sử dụng với thư viện Retrofit.
 */
public interface NewsApiService {

    /**
     * Lấy các tin tức hàng đầu (top headlines) từ một quốc gia cụ thể.
     *
     * @param country Mã quốc gia (ví dụ: "us", "gb", "vn").
     * @param apiKey  Khóa API của bạn để xác thực với NewsAPI.
     * @return một đối tượng Call chứa phản hồi được bao bọc trong lớp NewsResponse.
     */
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );
}