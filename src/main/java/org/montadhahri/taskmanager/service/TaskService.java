package org.montadhahri.taskmanager.service;

import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;

import java.util.List;

/**
 * Handle Task business operations
 * @author mdh
 */
public interface TaskService {

    /**
     * get all active tasks.
     * @return list of tasks
     */
    List<TaskResponseDto> getAllTasks();

    /**
     * Get a task by ID.
     * @param id Task ID
     * @return task DTO
     */
    TaskResponseDto getTaskById(Long id);

    /**
     * get all active tasks by Status.
     * @return list of tasks
     */
    List<TaskResponseDto> getTasksByStatus(TaskStatus status);

    /**
     * Creates a new task.
     * @param taskRequestDto task request DTO
     * @return created task response DTO
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
