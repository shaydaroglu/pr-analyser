package org.turquase.pranalyser.infrastructure.github;

import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.infrastructure.github.api.GithubApi;
import org.turquase.pranalyser.infrastructure.github.client.RetrofitClient;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Comment;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.PullRequest;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Review;
import org.turquase.pranalyser.infrastructure.github.exceptions.GithubApiException;
import org.turquase.pranalyser.infrastructure.github.exceptions.GithubServiceException;
import org.turquase.pranalyser.port.out.GitRepoServicePort;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GithubService implements GitRepoServicePort {
    private static final String STATE_ALL = "all";
    private static final int PER_PAGE = 100;
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final String API_ERROR = "Github API returned error: ";

    private final GithubApi githubApi;

    public GithubService(String authToken) {
        RetrofitClient retrofitClient = new RetrofitClient();
        this.githubApi = retrofitClient.getRetrofitClient(authToken).create(GithubApi.class);
    }

    @Override
    public List<PullRequest> getPullRequests(String owner, String repo, String state) {
        String queryState = (state == null) ? STATE_ALL : state;

        int page = 1;
        boolean hasMorePr = true;

        List<PullRequest> feedback = new ArrayList<>();

        while (hasMorePr) {
            List<PullRequest> prResponse = null;
            try {
                var response = githubApi.getResultFiles(owner, repo, queryState, page, PER_PAGE).execute();
                if(!response.isSuccessful()) {
                    throw new GithubApiException(API_ERROR + response);
                }

                prResponse = response.body();
                hasMorePr = (prResponse != null && !prResponse.isEmpty());
            } catch (IOException | GithubApiException e) {
                log.error("Failed to get pull requests.", e);
                throw new GithubServiceException("Failed to get pull requests.", e);
            }

            if (prResponse != null && !prResponse.isEmpty()) {
                feedback.addAll(prResponse);
                page++;
            }
        }

        return feedback;
    }

    @Override
    public List<PullRequest> getPullRequestInDateRange(String owner, String repo, String state, LocalDate startDate, LocalDate endDate) {
        ZonedDateTime zonedStartDate = startDate.atStartOfDay().atZone(UTC);
        ZonedDateTime zonedEndDate = endDate.atTime(LocalTime.MAX).atZone(UTC);

        return getPullRequests(owner, repo, state).stream()
                .filter(pullRequest ->
                        pullRequest.getCreatedAt().isAfter(zonedStartDate) &&
                        pullRequest.getCreatedAt().isBefore(zonedEndDate))
                .toList();
    }

    @Override
    public List<Comment> getPullRequestComments(String owner, String repo, int pullNumber) {
        int page = 1;
        boolean hasMoreComments = true;

        List<Comment> feedback = new ArrayList<>();

        while (hasMoreComments) {
            List<Comment> commentsResponse = null;
            try {
                var response = githubApi.getPullRequestComments(owner, repo, pullNumber, page, PER_PAGE).execute();
                if(!response.isSuccessful()) {
                    throw new GithubApiException(API_ERROR + response);
                }

                commentsResponse = response.body();
                hasMoreComments = (commentsResponse != null && !commentsResponse.isEmpty());
            } catch (IOException e) {
                log.error("Failed to get pull request comments", e);
                throw new GithubServiceException("Failed to get pull request comments", e);
            }

            if (commentsResponse != null && !commentsResponse.isEmpty()) {
                feedback.addAll(commentsResponse);
                page++;
            }
        }

        return feedback;
    }

    @Override
    public List<Review> getPullRequestReviews(String owner, String repo, int pullNumber) {
        int page = 1;
        boolean hasMoreReviews = true;

        List<Review> feedback = new ArrayList<>();

        while (hasMoreReviews) {
            List<Review> reviewsResponse = null;
            try {

                var response = githubApi.getPullRequestReviews(owner, repo, pullNumber, page, PER_PAGE).execute();
                if(!response.isSuccessful()) {
                    throw new GithubApiException(API_ERROR + response);
                }

                reviewsResponse = response.body();
                hasMoreReviews = (reviewsResponse != null && !reviewsResponse.isEmpty());
            } catch (IOException e) {
                log.error("Failed to get pull request reviews", e);
                throw new GithubServiceException("Failed to get pull request reviews", e);
            }

            if (reviewsResponse != null && !reviewsResponse.isEmpty()) {
                feedback.addAll(reviewsResponse);
                page++;
            }
        }

        return feedback;
    }
}
