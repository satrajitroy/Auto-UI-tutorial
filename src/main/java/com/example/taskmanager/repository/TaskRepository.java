package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Status status);

    @Query("select t from Task t where t.deadline < :today")
    List<Task> findOverdue(@Param("today") LocalDate today);

    @EntityGraph(attributePaths = {}) // placeholder in case we add associations later
    @Query("select t from Task t where t.id = :id")
    Task findWithGraphById(@Param("id") Long id);
}
