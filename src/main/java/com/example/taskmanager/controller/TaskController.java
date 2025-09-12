package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.util.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id) {
        Task t = taskService.getById(id);
        return TaskMapper.toResponse(t);
    }

    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) Status status) {
        List<Task> data = (status == null) ? taskService.findAll() : taskService.findByStatus(status);
        return data.stream().map(TaskMapper::toResponse).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody @Valid TaskRequest req) {
        Task created = taskService.create(TaskMapper.toEntity(req));
        return TaskMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest req) {
        Task updated = taskService.update(id, req);
        return TaskMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
