package com.example.bookshelf;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksService {
    @GET("volumes")
    Call<BookResponse> getRandomBook(
            @Query("q") String query,
            @Query("maxResults") int maxResults
    );
}