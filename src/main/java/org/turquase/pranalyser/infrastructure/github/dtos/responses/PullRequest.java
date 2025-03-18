package org.turquase.pranalyser.infrastructure.github.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
    private Long id;
    private Integer number;
    private User user;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}
