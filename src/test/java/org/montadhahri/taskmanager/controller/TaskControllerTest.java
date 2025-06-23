package org.montadhahri.taskmanager.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.montadhahri.taskmanager.dto.PageDto;
import org.montadhahri.taskmanager.dto.request.TaskRequestDto;
import org.montadhahri.taskmanager.dto.request.TaskStatusUpdateDto;
import org.montadhahri.taskmanager.dto.response.TaskResponseDto;
import org.montadhahri.taskmanager.enumeration.TaskStatus;
import org.montadhahri.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
        responseDto.setDescription("Description");
        responseDto.setStatus(TaskStatus.TODO);
    }

    @Test
    void createTask_returnsCreated() throws Exception {
        TaskRequestDto request = new TaskRequestDto("Test Task", "Description");

        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).createTask(any(TaskRequestDto.class));
    }

    @Test
    void getTaskById_returnsTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void updateTask_returnsUpdatedTask() throws Exception {
        TaskRequestDto updateRequest = new TaskRequestDto("Updated Task", "Updated Description");

        responseDto.setTitle("Updated Task");
        responseDto.setDescription("Updated Description");

        when(taskService.updateTask(eq(1L), any(TaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));

        verify(taskService).updateTask(eq(1L), any(TaskRequestDto.class));
    }

    @Test
    void updateTaskStatus_returnsUpdatedTask() throws Exception {
        TaskStatusUpdateDto statusDto = new TaskStatusUpdateDto();
        statusDto.setStatus(TaskStatus.DONE);

        responseDto.setStatus(TaskStatus.DONE);

        when(taskService.updateTaskStatus(1L, TaskStatus.DONE)).thenReturn(responseDto);

        mockMvc.perform(patch("/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        verify(taskService).updateTaskStatus(1L, TaskStatus.DONE);
    }

    @Test
    void softDeleteTask_returnsNoContent() throws Exception {
        doNothing().when(taskService).softDeleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).softDeleteTask(1L);
    }

    @Test
    void getAllTasks_withStatus_returnsPageDto() throws Exception {

        PageDto<TaskResponseDto> pageDto = new PageDto<>();
        pageDto.setItems(List.of(responseDto));
        pageDto.setCount(1L);

        when(taskService.getAllTasks(1, 10, TaskStatus.TODO)).thenReturn(pageDto);

        mockMvc.perform(get("/tasks")
                        .param("page", "1")
                        .param("offset", "10")
                        .param("status", "TODO")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].status").value("TODO"))
                .andExpect(jsonPath("$.items[0].id").value(1));

        verify(taskService, times(1)).getAllTasks(1, 10, TaskStatus.TODO);
    }

    @Test
    void getAllTasks_withoutStatus_returnsPageDto() throws Exception {

        PageDto<TaskResponseDto> pageDto = new PageDto<>();
        pageDto.setItems(List.of(responseDto));
        pageDto.setCount(1L);

        when(taskService.getAllTasks(2, 5, null)).thenReturn(pageDto);

        mockMvc.perform(get("/tasks")
                        .param("page", "2")
                        .param("offset", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].status").value("TODO"))
                .andExpect(jsonPath("$.items[0].id").value(1));

        verify(taskService, times(1)).getAllTasks(2, 5, null);
    }
}
