package org.turquase.pranalyser.port.out;

import org.turquase.pranalyser.infrastructure.github.dtos.responses.Comment;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.PullRequest;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Review;

import java.time.LocalDate;
import java.util.List;

public interface GitRepoServicePort {
    List<PullRequest> getPullRequests(String owner, String repo, String state);
    List<PullRequest> getPullRequestInDateRange(String owner, String repo, String state, LocalDate startDate, LocalDate endDate);
    List<Comment> getPullRequestComments(String owner, String repo, int pullNumber);
    List<Review> getPullRequestReviews(String owner, String repo, int pullNumber);
}
