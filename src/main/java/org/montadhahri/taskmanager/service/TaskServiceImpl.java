package org.montadhahri.taskmanager.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.montadhahri.taskmanager.dto.PageDto;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.entity.Task;
import org.montadhahri.taskmanager.exception.BadRequestException;
import org.montadhahri.taskmanager.exception.DuplicateResourceException;
import org.montadhahri.taskmanager.exception.ResourceNotFoundException;
import org.montadhahri.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

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
    public PageDto<TaskResponseDto> getAllTasks(Integer pageIndex, Integer offset, @Nullable TaskStatus status) {
        log.info("Fetching tasks with status={} and pageIndex={}, offset={}", status, pageIndex, offset);
        if (pageIndex == null || pageIndex <= 0) {
            throw new BadRequestException("pageIndex must be greater than or equal to 1");
        }
        if (offset == null || offset <= 0) {
            throw new BadRequestException("offset must be greater than 0");
        }
        Pageable pageable = PageRequest.of(pageIndex - 1, offset, Sort.by("createdAt").ascending());
        Page<Task> tasksPage = (status != null)
                ? taskRepository.findByStatusAndIsEnabledTrue(status, pageable)
                : taskRepository.findByIsEnabledTrue(pageable);

        PageDto<TaskResponseDto> pageDto = new PageDto<>();
        pageDto.setItems(tasksPage.getContent().stream()
                .map(task -> modelMapper.map(task, TaskResponseDto.class))
                .toList());
        pageDto.setCount(tasksPage.getTotalElements());

        return pageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return modelMapper.map(task, TaskResponseDto.class);
    }
}
