package com.example.googledrive.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SharedUser")
@Data
@NoArgsConstructor
public class SharedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ShareId")
    private Integer shareId;

    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "PermissionId")
    private Integer permissionId;

    // Constructors, if needed
    public SharedUser(Integer shareId, Integer userId, Integer permissionId) {
        this.shareId = shareId;
        this.userId = userId;
        this.permissionId = permissionId;
    }

    public int getSharedUserByPermissionId(int i) {
        if (i == this.permissionId) {
            return this.userId != null ? this.userId : -1; 
        }
        return -1;
    }

    public int getUserId() {
        return this.userId != null ? this.userId : -1; 
    }
}