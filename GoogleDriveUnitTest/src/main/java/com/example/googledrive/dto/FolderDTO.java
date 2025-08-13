package com.example.googledrive.dto;

import lombok.*;

import java.time.Instant;

import com.example.googledrive.domain.Folder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderDTO {
    private int id;
    private String Name;
    private int parentId;
    private int ownerId;
    private String path;
    private String status;
    private int size;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructor to create FolderDTO from Folder entity
    public FolderDTO(int id, int parentId, int ownerId, String name, int size, String path, String status,  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.parentId = parentId;
        this.ownerId = ownerId;
        this.Name = name;
        this.path = path;
        this.status = status;
        this.size = size;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    //Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public String getName() { return Name; }
    public void setName(String name) { this.Name = name; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
}
