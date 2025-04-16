package org.turquase.pranalyser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class UserStatistic {
    private String contributor;
    private Long raisedPrCount;
    private Double receivedCommentPerPr;
    private Long reviewedPrCount;

    public void incrementRaisedPrCount() {
        this.raisedPrCount = Objects.requireNonNullElse(this.raisedPrCount, 0L) + 1;
    }

    public void incrementReviewedPrCount() {
        this.reviewedPrCount = Objects.requireNonNullElse(this.raisedPrCount, 0L) + 1;
    }
}
