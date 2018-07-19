package com.esotericcoder.www.thenewyorktimes.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsfeedInterface {
    @GET("svc/search/v2/articlesearch.json")
    Call<ArticleResponse> listArticles(@Query("api-key") String apiKey,
                                  @Query("begin_date") int beginDate,
                                  @Query("sort") String sort,
                                  @Query("q") String query,
                                  @Query("page") int page,
                                  @Query("fq") String newsDesk,
                                  @Query("fl") List<String> flValues);

}