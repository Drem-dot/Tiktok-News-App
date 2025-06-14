package com.example.news.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    // --- Methods for Keyword ---

    @Query("SELECT * FROM keywords WHERE keyword = :keyword")
    KeywordEntity findKeyword(String keyword);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateKeyword(KeywordEntity keyword);

    @Query("SELECT * FROM keywords ORDER BY score DESC LIMIT 5")
    LiveData<List<KeywordEntity>> getTopKeywords();

    /**
     * Lấy 10 từ khóa có điểm số cao nhất (không dùng LiveData).
     * Phải gọi trên background thread.
     */
    @Query("SELECT * FROM keywords ORDER BY score DESC LIMIT 10")
    List<KeywordEntity> getTopKeywordsNonLive();

    // --- Methods for SavedArticle ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveArticle(ArticleInteractionEntity article);

    @Query("DELETE FROM user_interactions WHERE url = :articleUrl")
    void unsaveArticle(String articleUrl);

    @Query("SELECT * FROM user_interactions WHERE isSaved = 1 ORDER BY title ASC")
    LiveData<List<ArticleInteractionEntity>> getAllSavedArticles();
    @Query("SELECT * FROM user_interactions WHERE url = :articleUrl")
    LiveData<ArticleInteractionEntity> isArticleSaved(String articleUrl);

    // =======================================================
    // ** NEW METHODS ADDED HERE **
    // =======================================================

    /**
     * Find a saved article (not using LiveData).
     * This method must be called on a background thread.
     * Useful for checking and updating an article in the same background task.
     * @param articleUrl URL of the article to find.
     * @return ArticleInteractionEntity object if found, otherwise null.
     */
    @Query("SELECT * FROM user_interactions WHERE url = :articleUrl")
    ArticleInteractionEntity findArticleNonLive(String articleUrl);

    /**
     * Update an existing article in the database.
     * Room will find the record with the same primary key (url) as the passed object and update other fields.
     * This method must be called on a background thread.
     * @param article The article object with updated content.
     */
    @Update
    void updateSavedArticle(ArticleInteractionEntity article);
}