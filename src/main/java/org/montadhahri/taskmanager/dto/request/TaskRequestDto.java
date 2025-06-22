package org.montadhahri.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for creating or updating a task.
 * @author mdh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

    @NotBlank(message = "Task title must not be blank")
    @Size(max = 100, message = "Task title must not exceed 100 characters")
    private String title;

    private String description;
}
