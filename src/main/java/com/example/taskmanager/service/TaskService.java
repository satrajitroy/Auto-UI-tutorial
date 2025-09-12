package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.util.TaskMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final AuditRepository auditRepository;

    public TaskService(TaskRepository taskRepository, AuditRepository auditRepository) {
        this.taskRepository = taskRepository;
        this.auditRepository = auditRepository;
    }

    @Cacheable(value = "taskCache", key = "#id")
    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public Task create(Task task) {
        task.setId(null);
        Task saved = taskRepository.save(task);
        auditRepository.log("Created task id=" + saved.getId());
        return saved;
    }

    @CachePut(value = "taskCache", key = "#id")
    public Task update(Long id, TaskUpdateRequest req) {
        Task existing = getById(id);

        if (req.getVersion() != null && !req.getVersion().equals(existing.getVersion())) {
            throw new OptimisticLockingFailureException(
                "Version mismatch for Task id=" + id + ": expected " + existing.getVersion() + ", got " + req.getVersion()
            );
        }

        TaskMapper.apply(req, existing);
        Task saved = taskRepository.save(existing);
        auditRepository.log("Updated task id=" + saved.getId());
        return saved;
    }

    @CacheEvict(value = "taskCache", key = "#id")
    public void delete(Long id) {
        taskRepository.deleteById(id);
        auditRepository.log("Deleted task id=" + id);
    }

    @Transactional(readOnly = true)
    public List<Task> findByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Task> overdue(LocalDate today) {
        return taskRepository.findOverdue(today);
    }
}
