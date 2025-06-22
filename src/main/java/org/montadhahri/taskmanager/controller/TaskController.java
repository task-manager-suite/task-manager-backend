package org.montadhahri.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.request.TaskStatusUpdateDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "tasks", description = "Task management APIs")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "409", description = " Conflict: Task title already exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto dto) {
        TaskResponseDto created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get All tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        List<TaskResponseDto> tasks = (status == null)
                ? taskService.getAllTasks()
                : taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Find task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto dto = taskService.getTaskById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "409", description = " Conflict: Task title already exists"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskRequestDto dto) {
        TaskResponseDto updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Update task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(@PathVariable Long id,
                                                            @Valid @RequestBody TaskStatusUpdateDto dto) {
        TaskResponseDto updated = taskService.updateTaskStatus(id, dto.getStatus());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long id) {
        taskService.softDeleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
