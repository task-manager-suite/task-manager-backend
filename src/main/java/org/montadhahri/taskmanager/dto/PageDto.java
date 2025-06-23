package org.montadhahri.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

    @Schema(description = "The page content")
    private List<T> items;

    @Schema(description = "Total amount of elements")
    private long count;
}