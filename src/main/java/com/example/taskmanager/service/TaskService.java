package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.util.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepo;

    public TaskService(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    // -------- 1) list(Status) -> List<TaskResponse> --------
    public List<TaskResponse> list(@Nullable Status status) {
        List<Task> entities = (status == null)
                ? taskRepo.findAll()
                : taskRepo.findByStatus(status); // add this repo method if missing
        return entities.stream().map(this::toResponse).toList();
    }

    // -------- 2) get(id) -> TaskResponse --------
    public TaskResponse get(long id) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Task " + id + " not found"));
        return toResponse(t);
    }

    // -------- 3) create(TaskRequest) -> long (id) --------
    public Long create(@Valid TaskRequest req) {
        Task entity = fromCreate(req);
        Task saved = taskRepo.save(entity);
        return saved.getId();
    }

    // (Optional) keep/update existing methods; add these helpers:

    private TaskResponse toResponse(Task t) {
        return TaskMapper.toResponse(t);
    }

    private Task fromCreate(TaskRequest req) {
        return TaskMapper.toEntity(req);
    }

    // If you also need update(id, TaskUpdateRequest):
    public void update(long id, @Valid TaskUpdateRequest req) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Task " + id + " not found"));
        if (req.getTitle() != null) t.setTitle(req.getTitle());
        if (req.getDescription() != null) t.setDescription(req.getDescription());
        if (req.getStatus() != null) t.setStatus(req.getStatus());
        if (req.getDeadline() != null) t.setDeadline(req.getDeadline());
        // If you manually manage version/updatedAt, set them here
        taskRepo.save(t);
    }

    public void delete(long id) {
        taskRepo.deleteById(id);
    }
}
