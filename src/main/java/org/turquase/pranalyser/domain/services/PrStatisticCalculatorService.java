package org.turquase.pranalyser.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.application.ServiceFactory;
import org.turquase.pranalyser.domain.exceptions.PrStatisticCalculatorServiceException;
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
public class PrStatisticCalculatorService implements PrStatisticCalculatorPort {
    private static final String OWNER = "OWNER";
    private final GitRepoServicePort gitRepoServicePort;

    public PrStatisticCalculatorService(String accessToken) {
        this.gitRepoServicePort = ServiceFactory.createGithubServicePort(accessToken);
    }

    @Override
    public List<UserStatistic> calculateUserPrStatistics(String repoName, String owner, String accessToken, LocalDate startDate, LocalDate endDate) {
        HashMap<Long, UserStatistic> userIdUserStatisticsMap = new HashMap<>();

        // #1 Add 'PR state' feature to app
        List<PullRequest> pullRequests =
                gitRepoServicePort.getPullRequestInDateRange(owner, repoName, null, startDate, endDate);

        if (pullRequests.isEmpty()) {
            log.error("No pull requests found for the given date range.");
            return new ArrayList<>();
        }

        pullRequests.forEach(pullRequest -> {
            Long prOwnerId = pullRequest.getUser().getId();
            calculatePullRequestStatistics(userIdUserStatisticsMap, pullRequest, prOwnerId);

            List<Comment> comments = gitRepoServicePort.getPullRequestComments(owner, repoName, pullRequest.getNumber());
            calculateUserCommentStatistics(userIdUserStatisticsMap, comments, prOwnerId);

            List<Review> reviews = gitRepoServicePort.getPullRequestReviews(owner, repoName, pullRequest.getNumber());
            calculateReviewerStatistics(userIdUserStatisticsMap, comments, reviews, prOwnerId);
        });

        return userIdUserStatisticsMap.values().stream().toList();
    }

    private void calculatePullRequestStatistics(HashMap<Long, UserStatistic> userStatistics, PullRequest pullRequest, Long prOwnerId) {
        if (userStatistics.containsKey(prOwnerId)) {
            userStatistics.get(prOwnerId).incrementRaisedPrCount();
            return;
        }

        UserStatistic newUserStatistic = UserStatistic.builder()
                .contributor(pullRequest.getUser().getLogin())
                .raisedPrCount(1L)
                .receivedCommentPerPr(0.0)
                .reviewedPrCount(0L)
                .build();
        userStatistics.put(prOwnerId, newUserStatistic);
    }

    private void calculateUserCommentStatistics(HashMap<Long, UserStatistic> userStatistics, List<Comment> comments, Long prOwnerId) {
        long receivedCommentCount = comments.stream()
                .filter(c -> !Objects.equals(c.getUser().getId(), prOwnerId))
                .count();
        UserStatistic prOwnerStatistic = userStatistics.get(prOwnerId);

        if (prOwnerStatistic.getRaisedPrCount() == 0) {
            log.error("PR owner has no PRs raised");
            throw new PrStatisticCalculatorServiceException("PR owner has no PRs raised");
        }

        if (prOwnerStatistic.getRaisedPrCount() == 1) {
            prOwnerStatistic.setReceivedCommentPerPr((double) receivedCommentCount);
            userStatistics.put(prOwnerId, prOwnerStatistic);
            return;
        }

        // Subtract 1 because we are adding the current PR's comment count
        double totalComments = prOwnerStatistic.getReceivedCommentPerPr() * (prOwnerStatistic.getRaisedPrCount() - 1);
        totalComments += receivedCommentCount;
        Double calculatedCommentPerPr = totalComments / prOwnerStatistic.getRaisedPrCount();
        prOwnerStatistic.setReceivedCommentPerPr(calculatedCommentPerPr);
        userStatistics.put(prOwnerId, prOwnerStatistic);
    }

    private void calculateReviewerStatistics(HashMap<Long, UserStatistic> userStatistics, List<Comment> comments, List<Review> reviews, Long prOwnerId) {
        List<Long> reviewerUserIds = new ArrayList<>();
        comments.forEach(
                comment -> {
                    Long commentUserId = comment.getUser().getId();

                    boolean existsInUserContributorsList = userStatistics.containsKey(commentUserId);

                    if (!existsInUserContributorsList) {
                        UserStatistic userStatistic = UserStatistic.builder()
                                .contributor(comment.getUser().getLogin())
                                .raisedPrCount(0L)
                                .receivedCommentPerPr(0.0)
                                .reviewedPrCount(1L)
                                .build();
                        userStatistics.put(commentUserId, userStatistic);
                        return;
                    }

                    if (!Objects.equals(comment.getAuthorAssociation(), OWNER) && !reviewerUserIds.contains(commentUserId)) {
                        UserStatistic userStatistic = userStatistics.get(commentUserId);
                        userStatistic.incrementReviewedPrCount();
                        userStatistics.put(commentUserId, userStatistic);
                        reviewerUserIds.add(commentUserId);
                    }
                }
        );

        reviews.stream()
                .filter(review -> !Objects.equals(review.getUser().getId(), prOwnerId))
                .forEach(review -> {
                    Long reviewerId = review.getUser().getId();

                    if (reviewerUserIds.contains(reviewerId)) {
                        return;
                    }

                    if (!userStatistics.containsKey(reviewerId)) {
                        UserStatistic userStatistic = UserStatistic.builder()
                                .contributor(review.getUser().getLogin())
                                .raisedPrCount(0L)
                                .receivedCommentPerPr(0.0)
                                .reviewedPrCount(1L)
                                .build();
                        userStatistics.put(reviewerId, userStatistic);
                        reviewerUserIds.add(reviewerId);
                        return;
                    }

                    UserStatistic userStatistic = userStatistics.get(reviewerId);
                    userStatistic.incrementReviewedPrCount();
                    userStatistics.put(reviewerId, userStatistic);
                    reviewerUserIds.add(reviewerId);
                });
    }

}
