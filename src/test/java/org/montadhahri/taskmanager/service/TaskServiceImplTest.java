package org.montadhahri.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.entity.Task;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.exception.DuplicateResourceException;
import org.montadhahri.taskmanager.exception.ResourceNotFoundException;
import org.montadhahri.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private ModelMapper modelMapper;

    private Task taskEntity;
    private TaskRequestDto requestDto;
    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        taskEntity = new Task();
        taskEntity.setId(1L);
        taskEntity.setTitle("Task 1");
        taskEntity.setDescription("description");
        taskEntity.setStatus(TaskStatus.TODO);
        taskEntity.setEnabled(true);

        requestDto = new TaskRequestDto();
        requestDto.setTitle("Task 1");
        requestDto.setDescription("description");

        responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Task 1");
        responseDto.setDescription("description");
        responseDto.setStatus(TaskStatus.TODO);
    }

    @Test
    void createTask_success() {
        when(taskRepository.findByTitleAndIsEnabledTrue(requestDto.getTitle()))
                .thenReturn(Optional.empty());
        when(modelMapper.map(requestDto, Task.class)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(modelMapper.map(taskEntity, TaskResponseDto.class)).thenReturn(responseDto);

        TaskResponseDto result = taskService.createTask(requestDto);

        assertNotNull(result);
        assertEquals(responseDto.getTitle(), result.getTitle());
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void createTask_duplicateTitle_throwsException() {
        when(taskRepository.findByTitleAndIsEnabledTrue(requestDto.getTitle()))
                .thenReturn(Optional.of(taskEntity));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            taskService.createTask(requestDto);
        });

        assertTrue(exception.getMessage().contains("Task title already exists"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_success() {
        Long id = 1L;
        TaskRequestDto updateDto = new TaskRequestDto();
        updateDto.setTitle("Updated Task");
        updateDto.setDescription("Updated description");

        Task updatedTask = new Task();
        updatedTask.setId(id);
        updatedTask.setTitle(updateDto.getTitle());
        updatedTask.setDescription(updateDto.getDescription());
        updatedTask.setStatus(TaskStatus.TODO);
        updatedTask.setEnabled(true);

        TaskResponseDto updatedResponseDto = new TaskResponseDto();
        updatedResponseDto.setId(id);
        updatedResponseDto.setTitle(updateDto.getTitle());
        updatedResponseDto.setDescription(updateDto.getDescription());
        updatedResponseDto.setStatus(TaskStatus.TODO);

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.findByTitleAndIsEnabledTrue(updateDto.getTitle())).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(modelMapper.map(updatedTask, TaskResponseDto.class)).thenReturn(updatedResponseDto);

        TaskResponseDto result = taskService.updateTask(id, updateDto);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_duplicateTitle_throwsException() {
        Long id = 1L;
        TaskRequestDto updateDto = new TaskRequestDto();
        updateDto.setTitle("Duplicate Title");
        updateDto.setDescription("Desc");

        Task otherTask = new Task();
        otherTask.setId(2L);
        otherTask.setTitle("Duplicate Title");
        otherTask.setEnabled(true);

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.findByTitleAndIsEnabledTrue(updateDto.getTitle())).thenReturn(Optional.of(otherTask));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            taskService.updateTask(id, updateDto);
        });

        assertTrue(exception.getMessage().contains("Task title already exists"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_notFound_throwsException() {
        Long id = 1L;
        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(id, requestDto);
        });

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void updateTaskStatus_success() {
        Long id = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        Task updatedTask = new Task();
        updatedTask.setId(id);
        updatedTask.setTitle(taskEntity.getTitle());
        updatedTask.setDescription(taskEntity.getDescription());
        updatedTask.setStatus(newStatus);
        updatedTask.setEnabled(true);

        TaskResponseDto updatedResponseDto = new TaskResponseDto();
        updatedResponseDto.setId(id);
        updatedResponseDto.setTitle(taskEntity.getTitle());
        updatedResponseDto.setDescription(taskEntity.getDescription());
        updatedResponseDto.setStatus(newStatus);

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(modelMapper.map(updatedTask, TaskResponseDto.class)).thenReturn(updatedResponseDto);

        TaskResponseDto result = taskService.updateTaskStatus(id, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void softDeleteTask_success() {
        Long id = 1L;

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(Task.class))).thenReturn(taskEntity);

        assertDoesNotThrow(() -> taskService.softDeleteTask(id));

        verify(taskRepository).save(argThat(task -> !task.isEnabled()));
    }

    @Test
    void softDeleteTask_notFound_throwsException() {
        Long id = 1L;

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.softDeleteTask(id);
        });

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void getAllTasks_returnsList() {
        when(taskRepository.findByIsEnabledTrue()).thenReturn(List.of(taskEntity));
        when(modelMapper.map(taskEntity, TaskResponseDto.class)).thenReturn(responseDto);

        List<TaskResponseDto> results = taskService.getAllTasks();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(taskEntity.getTitle(), results.getFirst().getTitle());
    }

    @Test
    void getTasksByStatus_returnsFilteredList() {
        TaskStatus status = TaskStatus.TODO;

        when(taskRepository.findByStatusAndIsEnabledTrue(status)).thenReturn(List.of(taskEntity));
        when(modelMapper.map(taskEntity, TaskResponseDto.class)).thenReturn(responseDto);

        List<TaskResponseDto> results = taskService.getTasksByStatus(status);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(status, results.getFirst().getStatus());
    }

    @Test
    void getTaskById_success() {
        Long id = 1L;

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.of(taskEntity));
        when(modelMapper.map(taskEntity, TaskResponseDto.class)).thenReturn(responseDto);

        TaskResponseDto result = taskService.getTaskById(id);

        assertNotNull(result);
        assertEquals(taskEntity.getTitle(), result.getTitle());
    }

    @Test
    void getTaskById_notFound_throwsException() {
        Long id = 1L;

        when(taskRepository.findByIdAndIsEnabledTrue(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(id);
        });

        assertTrue(exception.getMessage().contains("Task not found"));
    }
}
