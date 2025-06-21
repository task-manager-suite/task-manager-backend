package org.montadhahri.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Dto for API error response body.
 * @author mdh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDto {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> fieldErrors;
}
