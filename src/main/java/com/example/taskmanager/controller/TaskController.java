package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.model.PagedResponse;
import com.example.taskmanager.service.TaskService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService; // your existing concrete class

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ---- LIST -> { items, page, size, total }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<TaskResponse> list(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Status status) {

        // Get everything from your existing service (adjust method name as needed)
        List<TaskResponse> all = taskService.list(status); // or taskService.findAll(status)

        final int from = (int) pageable.getOffset();
        final int to = Math.min(from + pageable.getPageSize(), all.size());
        final List<TaskResponse> slice =
                (from >= all.size()) ? List.of() : all.subList(from, to);

        return new PagedResponse<>(slice,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                all.size());
    }

    // ---- GET BY ID (unchanged)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResponse get(@PathVariable long id) {
        return taskService.get(id);
    }

    // ---- CREATE -> text/plain id
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<Long> create(@Valid @RequestBody TaskRequest req) {
        // If your service already returns an id
        Long id = taskService.create(req);

        // If your service returns a TaskResponse resp instead:
        // long id = taskService.create(req).getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(id);
    }

    // ---- UPDATE -> text/plain id
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<String> update(@PathVariable long id, @Valid @RequestBody TaskUpdateRequest req) {
        // If your service returns void:
        taskService.update(id, req);

        // If your service returns a TaskResponse resp:
        // taskService.update(id, req);

        return ResponseEntity.ok(Long.toString(id));
    }

    // ---- DELETE (unchanged)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        taskService.delete(id);
    }
}
