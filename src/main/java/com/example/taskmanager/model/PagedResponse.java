package com.example.taskmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;

    public PagedResponse() {}
    public PagedResponse(List<T> items, int page, int size, long total) {
        this.items = items; this.page = page; this.size = size; this.total = total;
    }

    public void setItems(List<T> items) { this.items = items; }
    public void setPage(int page) { this.page = page; }
    public void setSize(int size) { this.size = size; }
    public void setTotal(long total) { this.total = total; }
}