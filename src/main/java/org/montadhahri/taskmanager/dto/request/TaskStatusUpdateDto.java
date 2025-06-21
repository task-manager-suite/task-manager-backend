package org.montadhahri.taskmanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.montadhahri.taskmanager.enumeration.TaskStatus;

/**
 * Dto for updating a task status.
 * @author mdh
 */
@Data
public class TaskStatusUpdateDto {

    @NotNull(message = "Status must not be null")
    private TaskStatus status;
}
