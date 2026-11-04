package com.hourlyhire.workforce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVisibilityRequest {
    @NotNull(message = "Discoverable status is required")
    private Boolean discoverable;
}
