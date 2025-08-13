package com.example.googledrive.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Folder {
    private Integer id;
    private Integer parentId;
    private Integer ownerId;
    private String name;
    private String path;
    private String status;
    private int size;
    private Instant CreatedAt;
    private Instant UpdatedAt;
    
    // Constructor
    public Folder(Integer id, Integer parentId, Integer ownerId, String name, int size,
                   String path, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.parentId = parentId;
        this.ownerId = ownerId;
        this.name = name;
        this.size = size;
        this.CreatedAt = createdAt;
        this.UpdatedAt = updatedAt;
        this.path = path;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return CreatedAt; }
    public void setCreatedAt(Instant createdAt) { this.CreatedAt = createdAt; }
    public Instant getUpdatedAt() { return UpdatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.UpdatedAt = updatedAt; }
    
}