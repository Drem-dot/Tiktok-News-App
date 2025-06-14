package com.example.news.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keywords")
public class KeywordEntity {

    @PrimaryKey
    @NonNull
    public String keyword;

    public int score;

    public KeywordEntity(@NonNull String keyword, int score) {
        this.keyword = keyword;
        this.score = score;
    }
}