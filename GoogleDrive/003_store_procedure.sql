USE GoogleDrive;
GO

CREATE OR ALTER PROCEDURE dbo.sp_PropagateFolderPermissions
    @ShareId INT,
    @UserId INT,
    @PermissionId INT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        -- Start a transaction to ensure atomicity
        BEGIN TRANSACTION;

        -- Validate input parameters
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

        -- Get the folder and sharer associated with the ShareId
        DECLARE @FolderId INT, @SharerId INT;
        SELECT @FolderId = ObjectId, @SharerId = Sharer
        FROM Share
        WHERE Id = @ShareId;

        -- Find all subfolders recursively (up to 4 levels)
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
        -- Process each subfolder
        SELECT Id INTO #SubFolders FROM SubFolders WHERE Id != @FolderId;

        -- Declare variables for cursor
        DECLARE @SubFolderId INT;
        DECLARE @ExistingShareId INT;

        -- Cursor to iterate over subfolders
        DECLARE subfolder_cursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT Id FROM #SubFolders;

        OPEN subfolder_cursor;
        FETCH NEXT FROM subfolder_cursor INTO @SubFolderId;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            -- Check if a share already exists for this subfolder and sharer
            SELECT @ExistingShareId = Id
            FROM Share
            WHERE ObjectId = @SubFolderId
                AND ObjectTypeId = 1
                AND Sharer = @SharerId;

            IF @ExistingShareId IS NOT NULL
            BEGIN
                -- Update existing SharedUser entry if it exists, or insert a new one
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
                -- Create a new Share entry for the subfolder
                INSERT INTO Share (Sharer, ObjectId, ObjectTypeId, CreatedAt, ExpiresAt)
                VALUES (@SharerId, @SubFolderId, 1, GETDATE(), NULL);

                -- Get the new ShareId
                SET @ExistingShareId = SCOPE_IDENTITY();

                -- Insert SharedUser entry for the new share
                INSERT INTO SharedUser (ShareId, UserId, PermissionId)
                VALUES (@ExistingShareId, @UserId, @PermissionId);
            END

            FETCH NEXT FROM subfolder_cursor INTO @SubFolderId;
        END

        CLOSE subfolder_cursor;
        DEALLOCATE subfolder_cursor;

        -- Drop temporary table
        DROP TABLE #SubFolders;

        -- Commit the transaction
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        -- Rollback transaction on error
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        -- Throw error details
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
        DECLARE @ErrorState INT = ERROR_STATE();
        THROW @ErrorSeverity, @ErrorMessage, @ErrorState;
    END CATCH
END;
GO

