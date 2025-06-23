package org.montadhahri.taskmanager.service;

import jakarta.annotation.Nullable;
import org.montadhahri.taskmanager.dto.PageDto;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.exception.DuplicateResourceException;

/**
 * Handle Task business operations
 * @author mdh
 */
public interface TaskService {

    /**
     * get all active tasks.
     * @return page of tasks
     */
    PageDto<TaskResponseDto> getAllTasks(Integer pageIndex, Integer offset, @Nullable TaskStatus status);

    /**
     * Get a task by ID.
     * @param id Task ID
     * @return task DTO
     */
    TaskResponseDto getTaskById(Long id);

    /**
     * Creates a new task.
     * @param taskRequestDto task request DTO
     * @return created task response DTO
     * @exception DuplicateResourceException: Task title already exists
     */
    TaskResponseDto createTask(TaskRequestDto taskRequestDto);

    /**
     * Updates an exist task
     * @param id task ID
     * @param taskRequestDto task request DTO
     * @return updated task response DTO
     */
    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto);

    /**
     * Changes the status of task.
     * @param id task ID
     * @param status new task status
     * @return updated task response DTO
     */
    TaskResponseDto updateTaskStatus(Long id, TaskStatus status);

    /**
     * Soft deletes a task
     * @param id task ID
     */
    void softDeleteTask(Long id);
}
