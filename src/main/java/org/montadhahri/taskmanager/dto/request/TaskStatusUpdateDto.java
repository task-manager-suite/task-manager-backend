package org.montadhahri.taskmanager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.montadhahri.taskmanager.enumeration.TaskStatus;

/**
 * Dto for updating a task status.
 * @author mdh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateDto {

    @NotNull(message = "Status must not be null")
    private TaskStatus status;
}
