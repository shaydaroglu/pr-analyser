package org.turquase.pranalyser.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.application.ServiceFactory;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Comment;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.PullRequest;
import org.turquase.pranalyser.infrastructure.github.dtos.responses.Review;
import org.turquase.pranalyser.port.in.PrStatisticCalculatorPort;
import org.turquase.pranalyser.domain.model.UserStatistic;
import org.turquase.pranalyser.port.out.GitRepoServicePort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PrStatisticCalculator implements PrStatisticCalculatorPort {
    private static final String OWNER = "OWNER";

    @Override
    public List<UserStatistic> calculateUserPrStatistics(String repoName, String owner, String accessToken, LocalDate startDate, LocalDate endDate) {
        GitRepoServicePort gitRepoServicePort = ServiceFactory.createGithubServicePort(accessToken);
        HashMap<Long, UserStatistic> userStatistics = new HashMap<>();

        List<PullRequest> pullRequests =
                gitRepoServicePort.getPullRequestInDateRange(owner, repoName, null, startDate, endDate);

        if (pullRequests.isEmpty()) {
            log.error("No pull requests found for the given date range.");
            return new ArrayList<>();
        }

        pullRequests.forEach(pullRequest -> {
            Long prOwnerId = pullRequest.getUser().getId();
            calculatePullRequestStatistics(userStatistics, pullRequest, prOwnerId);

            List<Comment> comments = gitRepoServicePort.getPullRequestComments(owner, repoName, pullRequest.getNumber());
            calculateUserCommentStatistics(userStatistics, comments, prOwnerId);

            List<Review> reviews = gitRepoServicePort.getPullRequestReviews(owner, repoName, pullRequest.getNumber());
            calculateReviewerStatistics(userStatistics, comments, reviews, prOwnerId);
        });

        return userStatistics.values().stream().toList();
    }

    private void calculatePullRequestStatistics(HashMap<Long, UserStatistic> userStatistics, PullRequest pullRequest, Long prOwnerId) {
        boolean existsInUserContributorsList = userStatistics.containsKey(prOwnerId);

        UserStatistic userStatistic;
        if (existsInUserContributorsList) {
            userStatistic = userStatistics.get(prOwnerId);
            userStatistic.setRaisedPrCount(userStatistic.getRaisedPrCount() + 1);
        } else {
            userStatistic = UserStatistic.builder()
                    .contributor(pullRequest.getUser().getLogin())
                    .raisedPrCount(1L)
                    .receivedCommentPerPr(0.0)
                    .reviewedPrCount(0L)
                    .build();
        }
        userStatistics.put(prOwnerId, userStatistic);
    }

    private void calculateUserCommentStatistics(HashMap<Long, UserStatistic> userStatistics, List<Comment> comments, Long prOwnerId) {
        long receivedCommentCount = comments.stream()
                .filter(c -> !Objects.equals(c.getUser().getId(), prOwnerId))
                .count();
        UserStatistic prOwnerStatistic = userStatistics.get(prOwnerId);

        if(prOwnerStatistic.getRaisedPrCount() == 0) {
            log.error("PR owner has no PRs raised");
            throw new RuntimeException("PR owner has no PRs raised");
        } else if(prOwnerStatistic.getRaisedPrCount() == 1) {
            prOwnerStatistic.setReceivedCommentPerPr((double) receivedCommentCount);
            userStatistics.put(prOwnerId, prOwnerStatistic);
            return;
        } else {
            // Subtract 1 because we are adding the current PR's comment count
            double totalComments = prOwnerStatistic.getReceivedCommentPerPr() * (prOwnerStatistic.getRaisedPrCount() - 1);
            totalComments += receivedCommentCount;
            Double calculatedCommentPerPr = totalComments / prOwnerStatistic.getRaisedPrCount();
            prOwnerStatistic.setReceivedCommentPerPr(calculatedCommentPerPr);
            userStatistics.put(prOwnerId, prOwnerStatistic);
        }
    }

    private void calculateReviewerStatistics(HashMap<Long, UserStatistic> userStatistics, List<Comment> comments, List<Review> reviews, Long prOwnerId) {
        List<Long> reviewerUserIds = new ArrayList<>();
        comments.forEach(
                comment -> {
                    boolean existsInUserContributorsList = userStatistics.containsKey(comment.getUser().getId());

                    if (!existsInUserContributorsList) {
                        UserStatistic userStatistic = UserStatistic.builder()
                                .contributor(comment.getUser().getLogin())
                                .raisedPrCount(0L)
                                .receivedCommentPerPr(0.0)
                                .reviewedPrCount(1L)
                                .build();
                        userStatistics.put(comment.getUser().getId(), userStatistic);
                        return;
                    }

                    if (!Objects.equals(comment.getAuthorAssociation(), OWNER) && !reviewerUserIds.contains(comment.getUser().getId())) {
                        UserStatistic userStatistic = userStatistics.get(comment.getUser().getId());
                        userStatistic.setReviewedPrCount(userStatistic.getReviewedPrCount() + 1);
                        userStatistics.put(comment.getUser().getId(), userStatistic);
                        reviewerUserIds.add(comment.getUser().getId());
                    }
                }
        );

        reviews.stream()
                .filter(review -> !Objects.equals(review.getUser().getId(), prOwnerId))
                .forEach(review -> {
                    if (reviewerUserIds.contains(review.getUser().getId())) {
                        return;
                    }

                    if (!userStatistics.containsKey(review.getUser().getId())) {
                        UserStatistic userStatistic = UserStatistic.builder()
                                .contributor(review.getUser().getLogin())
                                .raisedPrCount(0L)
                                .receivedCommentPerPr(0.0)
                                .reviewedPrCount(1L)
                                .build();
                        userStatistics.put(review.getUser().getId(), userStatistic);
                        reviewerUserIds.add(review.getUser().getId());
                        return;
                    }

                    UserStatistic userStatistic = userStatistics.get(review.getUser().getId());
                    userStatistic.setReviewedPrCount(userStatistic.getReviewedPrCount() + 1);
                    userStatistics.put(review.getUser().getId(), userStatistic);
                    reviewerUserIds.add(review.getUser().getId());
                });
    }

}
