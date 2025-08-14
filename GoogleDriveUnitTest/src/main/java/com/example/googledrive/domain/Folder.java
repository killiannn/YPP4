package com.example.googledrive.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Folder {
    private Integer Id;
    private Integer ParentId;
    private Integer OwnerId;
    private String Name;
    private String Path;
    private String Status;
    private int Size;
    private Instant CreatedAt;
    private Instant UpdatedAt;
    
    // Constructor for convenience
    public Folder(int Id, int ParentId, int OwnerId, String Name, String Path, String Status, int Size, Instant CreatedAt, Instant UpdatedAt) {
        this.Id = Id;
        this.ParentId = ParentId;
        this.OwnerId = OwnerId;
        this.Name = Name;
        this.Path = Path;
        this.Status = Status;
        this.Size = Size;
        this.CreatedAt = CreatedAt;
        this.UpdatedAt = UpdatedAt;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }

    public int getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(int ownerId) {
        OwnerId = ownerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public Instant getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        CreatedAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        UpdatedAt = updatedAt;
    }
}