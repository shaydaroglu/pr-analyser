package org.turquase.pranalyser.infrastructure.github.api;

import org.turquase.pranalyser.infrastructure.github.dtos.responses.Comment;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.PullRequest;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Review;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GithubApi {

    @GET("/repos/{owner}/{repo}/pulls")
    @Headers({"Content-Type: application/json", "Accept: application/vnd.github+json", "Accept-Charset: utf-8"})
    Call<List<PullRequest>> getResultFiles(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("state") String state,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/comments")
    @Headers({"Content-Type: application/json", "Accept: application/vnd.github+json", "Accept-Charset: utf-8"})
    Call<List<Comment>> getPullRequestComments(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("pull_number") int pullNumber,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("/repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    @Headers({"Content-Type: application/json", "Accept: application/vnd.github+json", "Accept-Charset: utf-8"})
    Call<List<Review>> getPullRequestReviews(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("pull_number") int pullNumber,
            @Query("page") int page,
            @Query("per_page") int perPage
    );
}
