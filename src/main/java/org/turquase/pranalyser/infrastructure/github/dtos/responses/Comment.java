package org.turquase.pranalyser.infrastructure.github.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private Long id;
    @JsonProperty("pull_request_review_id")
    private Long pullRequestReviewId;
    private User user;
    private String body;
    @JsonProperty("author_association")
    private String authorAssociation;
}
