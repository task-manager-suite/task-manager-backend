package org.montadhahri.taskmanager.dto.response;

import lombok.Data;
import org.montadhahri.taskmanager.enumeration.TaskStatus;

/**
 * Dto for returning task details
 * @author mdh
 */
@Data
public class TaskResponseDto {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;
}
