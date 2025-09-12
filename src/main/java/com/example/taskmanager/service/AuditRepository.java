package com.example.taskmanager.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public class AuditRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void log(String message) {
        jdbcTemplate.update(
                "INSERT INTO audit_log(message, log_time) VALUES(?, ?)",
                message, OffsetDateTime.now()
        );
    }
}
