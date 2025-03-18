package org.turquase.pranalyser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserStatistic {
    private String contributor;
    private Long raisedPrCount;
    private Double receivedCommentPerPr;
    private Long reviewedPrCount;
}
