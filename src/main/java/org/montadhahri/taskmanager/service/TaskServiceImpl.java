package org.montadhahri.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.entity.Task;
import org.montadhahri.taskmanager.exception.DuplicateResourceException;
import org.montadhahri.taskmanager.exception.ResourceNotFoundException;
import org.montadhahri.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    private Task findTaskById(Long id) {
        log.info("Find task by ID: {}", id);
        return taskRepository.findByIdAndIsEnabledTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto) {
        log.info("Create task with title: {}", dto.getTitle());
        taskRepository.findByTitleAndIsEnabledTrue(dto.getTitle()).ifPresent(t -> {
            throw new DuplicateResourceException("Task title already exists: " + dto.getTitle());
        });

        Task task = modelMapper.map(dto, Task.class);
        task.setStatus(TaskStatus.TODO);
        task.setEnabled(true);
        Task saved = taskRepository.save(task);
        return modelMapper.map(saved, TaskResponseDto.class);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto) {
        log.info("Update task with ID: {}", id);
        Task task = findTaskById(id);

        if (!task.getTitle().equals(dto.getTitle())) {
            taskRepository.findByTitleAndIsEnabledTrue(dto.getTitle()).ifPresent(t -> {
                throw new DuplicateResourceException("Task title already exists: " + dto.getTitle());
            });
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());

        Task updated = taskRepository.save(task);
        return modelMapper.map(updated, TaskResponseDto.class);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskStatus(Long id, TaskStatus status) {
        log.info("Update status for task with ID: {}", id);
        Task task = findTaskById(id);

        task.setStatus(status);
        Task updated = taskRepository.save(task);
        return modelMapper.map(updated, TaskResponseDto.class);
    }

    @Override
    @Transactional
    public void softDeleteTask(Long id) {
        log.info("Delete task with ID: {}", id);
        Task task = findTaskById(id);

        task.setEnabled(false);
        taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        log.info("Find all tasks");
        return taskRepository.findByIsEnabledTrue().stream()
                .map(t -> modelMapper.map(t, TaskResponseDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusAndIsEnabledTrue(status).stream()
                .map(t -> modelMapper.map(t, TaskResponseDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return modelMapper.map(task, TaskResponseDto.class);
    }
}
