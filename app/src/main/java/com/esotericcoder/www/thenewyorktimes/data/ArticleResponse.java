package com.esotericcoder.www.thenewyorktimes.data;

import com.esotericcoder.www.thenewyorktimes.model.Article;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArticleResponse {
    @SerializedName("status")
    String status;
    @SerializedName("copyright")
    String copyright;
    @SerializedName("response")
    Response response;

    public List<Article> getResults() {
        return response.getDocs();
    }

    public static class Response {
        @SerializedName("docs")
        List<Article> docs;

        public List<Article> getDocs() {
            return docs;
        }
    }
}
