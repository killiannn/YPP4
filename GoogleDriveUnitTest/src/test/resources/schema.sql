-- Create necessary tables
CREATE TABLE [User] (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    LastLogin DATETIME,
    UsedCapacity BIGINT,
    Capacity BIGINT
);

CREATE TABLE Permission (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL
);

CREATE TABLE ObjectType (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL
);

CREATE TABLE Folder (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ParentId INT,
    OwnerId INT NOT NULL,
    Name NVARCHAR(255) NOT NULL,
    Size BIGINT,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    UpdatedAt DATETIME,
    Path NVARCHAR(255),
    Status NVARCHAR(50),
    FOREIGN KEY (ParentId) REFERENCES Folder(Id),
    FOREIGN KEY (OwnerId) REFERENCES [User](Id)
);

CREATE TABLE Share (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Sharer INT NOT NULL,
    ObjectId INT NOT NULL,
    ObjectTypeId INT NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    ExpiresAt DATETIME,
    FOREIGN KEY (Sharer) REFERENCES [User](Id),
    FOREIGN KEY (ObjectTypeId) REFERENCES ObjectType(Id)
);

CREATE TABLE SharedUser (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ShareId INT,
    UserId INT,
    PermissionId INT,
    FOREIGN KEY (ShareId) REFERENCES Share(Id),
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (PermissionId) REFERENCES Permission(Id)
);

-- Create stored procedure
CREATE PROCEDURE dbo.sp_PropagateFolderPermissions
    @ShareId INT,
    @UserId INT,
    @PermissionId INT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM Share WHERE Id = @ShareId AND ObjectTypeId = 1)
        BEGIN
            THROW 50001, 'Invalid ShareId or Share is not for a folder.', 1;
        END

        IF NOT EXISTS (SELECT 1 FROM [User] WHERE Id = @UserId)
        BEGIN
            THROW 50002, 'Invalid UserId.', 1;
        END

        IF NOT EXISTS (SELECT 1 FROM Permission WHERE Id = @PermissionId)
        BEGIN
            THROW 50003, 'Invalid PermissionId.', 1;
        END

        DECLARE @FolderId INT, @SharerId INT;
        SELECT @FolderId = ObjectId, @SharerId = Sharer
        FROM Share
        WHERE Id = @ShareId;

        WITH SubFolders AS (
            SELECT Id, ParentId, 1 AS Level
            FROM Folder
            WHERE Id = @FolderId AND Status = 'active'
            UNION ALL
            SELECT f.Id, f.ParentId, sf.Level + 1
            FROM Folder f
            INNER JOIN SubFolders sf ON f.ParentId = sf.Id
            WHERE f.Status = 'active' AND sf.Level < 4
        )
        SELECT Id INTO #SubFolders FROM SubFolders WHERE Id != @FolderId;

        DECLARE @SubFolderId INT;
        DECLARE @ExistingShareId INT;

        DECLARE subfolder_cursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT Id FROM #SubFolders;

        OPEN subfolder_cursor;
        FETCH NEXT FROM subfolder_cursor INTO @SubFolderId;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            SELECT @ExistingShareId = Id
            FROM Share
            WHERE ObjectId = @SubFolderId
                AND ObjectTypeId = 1
                AND Sharer = @SharerId;

            IF @ExistingShareId IS NOT NULL
            BEGIN
                IF EXISTS (SELECT 1 FROM SharedUser WHERE ShareId = @ExistingShareId AND UserId = @UserId)
                BEGIN
                    UPDATE SharedUser
                    SET PermissionId = @PermissionId
                    WHERE ShareId = @ExistingShareId AND UserId = @UserId;
                END
                ELSE
                BEGIN
                    INSERT INTO SharedUser (ShareId, UserId, PermissionId)
                    VALUES (@ExistingShareId, @UserId, @PermissionId);
                END
            END
            ELSE
            BEGIN
                INSERT INTO Share (Sharer, ObjectId, ObjectTypeId, CreatedAt, ExpiresAt)
                VALUES (@SharerId, @SubFolderId, 1, GETDATE(), NULL);

                SET @ExistingShareId = SCOPE_IDENTITY();

                INSERT INTO SharedUser (ShareId, UserId, PermissionId)
                VALUES (@ExistingShareId, @UserId, @PermissionId);
            END

            FETCH NEXT FROM subfolder_cursor INTO @SubFolderId;
        END

        CLOSE subfolder_cursor;
        DEALLOCATE subfolder_cursor;

        DROP TABLE #SubFolders;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
        DECLARE @ErrorState INT = ERROR_STATE();
        THROW @ErrorSeverity, @ErrorMessage, @ErrorState;
    END CATCH
END;