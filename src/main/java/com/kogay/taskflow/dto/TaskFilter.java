package com.kogay.taskflow.dto;

import com.kogay.taskflow.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
public record TaskFilter(
        Status status,
        @Schema(description = "ID of the owner of the task", example = "12")
        Integer ownerId,
        @Schema(description = "A list of user IDs to filter tasks by assignees. The tasks should be assigned to at least one user in this list", example = "11, 12")
        List<Integer> assigneeIds
) {
    public static TaskFilter empty() {
        return new TaskFilter(null, null, null);
    }
}
