
USE GoogleDrive;
GO

-- Step 1: Disable all CHECK and FOREIGN KEY constraints
EXEC sp_msforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL';
GO

-- Step 2: Truncate all tables in dependency order
TRUNCATE TABLE FileContent;
TRUNCATE TABLE SettingUser;
TRUNCATE TABLE [Session];
TRUNCATE TABLE SearchHistory;
TRUNCATE TABLE Recent;
TRUNCATE TABLE FavoriteObject;
TRUNCATE TABLE BannedUser;
TRUNCATE TABLE UserProduct;
TRUNCATE TABLE SharedUser;
TRUNCATE TABLE FileVersion;
TRUNCATE TABLE Trash;
TRUNCATE TABLE SearchIndex;
TRUNCATE TABLE TermIDF;
TRUNCATE TABLE Share;
TRUNCATE TABLE [File];
TRUNCATE TABLE Folder;
TRUNCATE TABLE FileType;
TRUNCATE TABLE Permission;
TRUNCATE TABLE Promotion;
TRUNCATE TABLE [Product];
TRUNCATE TABLE Setting;
TRUNCATE TABLE ObjectType;
TRUNCATE TABLE [User];
GO

-- Step 3: Re-enable all constraints (with validation, as tables are empty)
EXEC sp_msforeachtable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL';
GO

-- 1. Populate User table (1000 rows)
INSERT INTO [User] (Name, Email, PasswordHash, CreatedAt, LastLogin, UsedCapacity, Capacity)
SELECT TOP 1000
    'User' + CAST(n AS NVARCHAR(255)),
    'user' + CAST(n AS NVARCHAR(255)) + '@example.com',
    HASHBYTES('SHA2_256', CAST(NEWID() AS NVARCHAR(255))),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()),
    ABS(CHECKSUM(NEWID()) % 9000000000) + 100000000,
    10000000000
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
WHERE n <= 1000;
GO

-- 2. Populate Permission table (3 rows)
INSERT INTO Permission (Name)
VALUES ('reader'), ('contributor'), ('owner');
GO

-- 3. Populate ObjectType table (2 rows)
INSERT INTO ObjectType (Name)
VALUES ('folder'), ('file');
GO

-- 4. Populate Folder table (1000 rows with up to 3-4 subfolders)
IF OBJECT_ID('tempdb..#TempFolder') IS NOT NULL DROP TABLE #TempFolder;
CREATE TABLE #TempFolder (
    Id INT,
    ParentId INT,
    OwnerId INT,
    Name NVARCHAR(255),
    CreatedAt DATETIME,
    UpdatedAt DATETIME,
    Path NVARCHAR(255),
    Status NVARCHAR(50),
    Size BIGINT,
    Level INT
);

-- Insert top-level folders (200 rows)
INSERT INTO #TempFolder (Id, ParentId, OwnerId, Name, CreatedAt, UpdatedAt, Path, Status, Size, Level)
SELECT TOP 200
    ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS Id,
    NULL,
    u.Id,
    'Folder' + CAST(ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS NVARCHAR(255)),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()),
    '/' + CAST(ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS NVARCHAR(255)),
    CASE WHEN ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) % 10 = 0 THEN 'archived' ELSE 'active' END,
    0,
    1
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 200;

-- Insert subfolders (~800 rows, 1-4 per parent, balanced)
WITH ParentFolders AS (
    SELECT 
        f.Id,
        f.Level,
        ROW_NUMBER() OVER (ORDER BY NEWID()) AS rn,
        ABS(CHECKSUM(NEWID()) % 4) + 1 AS SubfolderCount -- Random 1-4 subfolders
    FROM #TempFolder f
    WHERE f.Status = 'active' AND f.Level < 4
),
SubfolderAssignments AS (
    SELECT 
        ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) + 200 AS Id,
        p.Id AS ParentId,
        u.Id AS OwnerId,
        'Folder' + CAST((ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) + 200) AS NVARCHAR(255)) AS Name,
        DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()) AS CreatedAt,
        DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()) AS UpdatedAt,
        '' AS Path,
        CASE WHEN ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) % 10 = 0 THEN 'archived' ELSE 'active' END AS Status,
        0 AS Size,
        p.Level + 1 AS Level
    FROM ParentFolders p
    CROSS JOIN [User] u
    CROSS APPLY (
        SELECT TOP (p.SubfolderCount) n
        FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
              FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
        WHERE n <= 4
    ) sub
    WHERE u.Id <= 1000
)
INSERT INTO #TempFolder (Id, ParentId, OwnerId, Name, CreatedAt, UpdatedAt, Path, Status, Size, Level)
SELECT TOP 800
    Id,
    ParentId,
    OwnerId,
    Name,
    CreatedAt,
    UpdatedAt,
    Path,
    Status,
    Size,
    Level
FROM SubfolderAssignments
ORDER BY Id;

-- Update paths for all folders
WITH FolderHierarchy AS (
    SELECT 
        Id,
        ParentId,
        Path,
        Level
    FROM #TempFolder
    WHERE ParentId IS NULL
    UNION ALL
    SELECT 
        t.Id,
        t.ParentId,
        CAST(f.Path + '/' + CAST(t.Id AS NVARCHAR(255)) AS NVARCHAR(255)),
        t.Level
    FROM #TempFolder t
    INNER JOIN FolderHierarchy f ON t.ParentId = f.Id
)
UPDATE tf
SET Path = fh.Path
FROM #TempFolder tf
INNER JOIN FolderHierarchy fh ON tf.Id = fh.Id;

-- Enable IDENTITY_INSERT for Folder table
SET IDENTITY_INSERT Folder ON;

INSERT INTO Folder (Id, ParentId, OwnerId, Name, CreatedAt, UpdatedAt, Path, Status, Size)
SELECT Id, ParentId, OwnerId, Name, CreatedAt, UpdatedAt, Path, Status, Size
FROM #TempFolder;

SET IDENTITY_INSERT Folder OFF;

DROP TABLE #TempFolder;
GO

-- 5. Populate FileType table (4 rows)
INSERT INTO FileType (Name, Icon)
VALUES 
    ('docx', 'docx.png'),
    ('excel', 'excel.png'),
    ('image', 'image.png'),
    ('video', 'video.png');
GO

-- 6. Populate [File] table (1000 rows)
INSERT INTO [File] (FolderId, OwnerId, Size, Name, Path, FileTypeId, ModifiedDate, Status, CreatedAt)
SELECT TOP 1000
    f.Id,
    u.Id,
    ABS(CHECKSUM(NEWID()) % 1000000000),
    'File' + CAST(n AS NVARCHAR(255)),
    f.Path + '/file' + CAST(n AS NVARCHAR(255)),
    ABS(CHECKSUM(NEWID()) % 4) + 1,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()),
    CASE WHEN n % 10 = 0 THEN 'deleted' ELSE 'active' END,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
CROSS JOIN Folder f
WHERE u.Id <= 1000 AND f.Id <= 1000 AND f.Status = 'active' AND n <= 1000;
GO

-- Update Folder.Size based on sum of File.Size
UPDATE Folder
SET Size = (
    SELECT COALESCE(SUM(f.Size), 0)
    FROM [File] f
    WHERE f.FolderId = Folder.Id
      AND f.Status = 'active'
)
WHERE Status = 'active';
GO

-- 7. Populate Share table (1000 rows)
INSERT INTO Share (Sharer, ObjectId, ObjectTypeId, CreatedAt, ExpiresAt)
SELECT TOP 1000
    u.Id,
    (SELECT TOP 1 Id FROM (
        SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
        UNION
        SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
    ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END 
    ORDER BY NEWID()) AS ObjectId,
    CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()),
    DATEADD(DAY, ABS(CHECKSUM(NEWID()) % 30), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000
    AND EXISTS (
        SELECT 1 FROM (
            SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
            UNION
            SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
        ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END
    );
GO

-- 8. Populate SharedUser table (1000 rows)
INSERT INTO SharedUser (ShareId, UserId, PermissionId)
SELECT TOP 1000
    s.Id,
    u.Id,
    ABS(CHECKSUM(NEWID()) % 3) + 1
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN Share s
CROSS JOIN [User] u
WHERE s.Id <= 1000 AND u.Id != s.Sharer AND n <= 1000;
GO

-- 9. Populate FileVersion table (1000 rows)
INSERT INTO FileVersion (FileId, Version, Path, CreatedAt, UpdateBy, IsCurrent, VersionFile, Size)
SELECT TOP 1000
    f.Id,
    ABS(CHECKSUM(NEWID()) % 5) + 1,
    f.Path + '/v' + CAST(ABS(CHECKSUM(NEWID()) % 5) + 1 AS NVARCHAR(255)),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()),
    u.Id,
    CASE WHEN n % 5 = 0 THEN 1 ELSE 0 END,
    f.Path + '_v' + CAST(ABS(CHECKSUM(NEWID()) % 5) + 1 AS NVARCHAR(255)) + '.bak',
    ABS(CHECKSUM(NEWID()) % 1000000000)
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [File] f
CROSS JOIN [User] u
WHERE f.Id <= 1000 AND u.Id <= 1000 AND n <= 1000;
GO

-- 10. Populate Trash table (1000 rows)
INSERT INTO Trash (ObjectId, ObjectTypeId, RemovedDatetime, UserId, IsPermanent)
SELECT TOP 1000
    (SELECT TOP 1 Id FROM (
        SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
        UNION
        SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
    ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END 
    ORDER BY NEWID()) AS ObjectId,
    CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()),
    u.Id,
    CASE WHEN n % 10 = 0 THEN 1 ELSE 0 END
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000
    AND EXISTS (
        SELECT 1 FROM (
            SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
            UNION
            SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
        ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END
    );
GO

-- 11. Populate Product table (4 rows)
INSERT INTO [Product] (Name, Cost, Duration)
VALUES 
    ('30gb', 19.00, 30),
    ('30gb', 190.00, 365),
    ('100gb', 45.00, 30),
    ('100gb', 540.00, 365);
GO

-- 12. Populate Promotion table (4 rows)
INSERT INTO Promotion (Name, Discount, IsPercent)
VALUES 
    ('First Month 30gb', 5.00, 0),
    ('First Year 30gb', 15.00, 0),
    ('First Month 100gb', 11.25, 0),
    ('First Year 100gb', 36.00, 0);
GO

-- 13. Populate UserProduct table (1000 rows)
INSERT INTO UserProduct (UserId, ProductId, PayingDatetime, IsFirstPaying, PromotionId, EndDatetime)
SELECT TOP 1000
    u.Id,
    ABS(CHECKSUM(NEWID()) % 4) + 1,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()),
    CASE WHEN n % 2 = 0 THEN 1 ELSE 0 END,
    ABS(CHECKSUM(NEWID()) % 4) + 1,
    DATEADD(DAY, ABS(CHECKSUM(NEWID()) % 365), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000;
GO

-- 14. Populate BannedUser table (1000 rows)
INSERT INTO BannedUser (UserId, BannedUserId, BannedAt)
SELECT TOP 1000
    u1.Id,
    u2.Id,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u1
CROSS JOIN [User] u2
WHERE u1.Id <= 1000 AND u2.Id <= 1000 AND u1.Id != u2.Id AND n <= 1000;
GO

-- 15. Populate FavoriteObject table (1000 rows)
INSERT INTO FavoriteObject (OwnerId, ObjectId, ObjectTypeId)
SELECT TOP 1000
    u.Id,
    (SELECT TOP 1 Id FROM (
        SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
        UNION
        SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
    ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END 
    ORDER BY NEWID()) AS ObjectId,
    CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000
    AND EXISTS (
        SELECT 1 FROM (
            SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
            UNION
            SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
        ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END
    );
GO

-- 16. Populate Recent table (1000 rows)
INSERT INTO Recent (UserId, ObjectId, ObjectTypeId, Log, DateTime)
SELECT TOP 1000
    u.Id,
    (SELECT TOP 1 Id FROM (
        SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
        UNION
        SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
    ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END 
    ORDER BY NEWID()) AS ObjectId,
    CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END,
    CASE 
        WHEN n % 2 = 0 THEN 'Accessed folder: Folder' + CAST((SELECT TOP 1 Id FROM Folder WHERE Id <= 1000 AND Status = 'active' ORDER BY NEWID()) AS NVARCHAR(255))
        ELSE 'Accessed file: File' + CAST((SELECT TOP 1 Id FROM [File] WHERE Id <= 1000 AND Status = 'active' ORDER BY NEWID()) AS NVARCHAR(255))
    END,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000
    AND EXISTS (
        SELECT 1 FROM (
            SELECT Id, 1 AS ObjectTypeId FROM Folder WHERE Id <= 1000 AND Status = 'active'
            UNION
            SELECT Id, 2 AS ObjectTypeId FROM [File] WHERE Id <= 1000 AND Status = 'active'
        ) Objects WHERE ObjectTypeId = CASE WHEN n % 2 = 0 THEN 1 ELSE 2 END
    );
GO

-- 17. Populate SearchHistory table (1000 rows)
INSERT INTO SearchHistory (UserId, SearchToken, SearchDatetime)
SELECT TOP 1000
    u.Id,
    'SearchTerm' + CAST(n AS NVARCHAR(255)),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000;
GO

-- 18. Populate [Session] table (1000 rows)
INSERT INTO [Session] (UserId, Token, CreatedAt, ExpiresAt)
SELECT TOP 1000
    u.Id,
    NEWID(),
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 30), GETDATE()),
    DATEADD(DAY, ABS(CHECKSUM(NEWID()) % 30), GETDATE())
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000;
GO

-- 19. Populate Setting table (30 rows)
INSERT INTO Setting (SettingKey, SettingValue, Decription)
VALUES 
    ('Layout', 'List', 'view as list'),
    ('Layout', 'Board', 'view as board'),
    ('ViewSetting', 'Name', ''),
    ('ViewSetting', 'ModifiedDatetime', ''),
    ('ViewSetting', 'ModifiedDatetimeByMe', ''),
    ('ViewSetting', 'ViewDateTiemByMe', ''),
    ('ArrangementText', 'A to Z', ''),
    ('ArrangementText', 'Z to A', ''),
    ('ArrangementDate', 'recent to last', ''),
    ('ArrangementDate', 'last to recent', ''),
    ('StartPage', 'Home', ''),
    ('StartPage', 'My Drive', ''),
    ('Theme', 'Light', ''),
    ('Theme', 'Dark', ''),
    ('Theme', 'System', ''),
    ('Density', 'Comfortable', ''),
    ('Density', 'Cozy', ''),
    ('Density', 'Compact', ''),
    ('OpenPDFs', 'New tab', ''),
    ('OpenPDFs', 'Preview', ''),
    ('Upload', 'Yes', ''),
    ('Upload', 'No', ''),
    ('PreviewCards', 'Yes', ''),
    ('PreviewCards', 'No', ''),
    ('Sound', 'Yes', ''),
    ('Sound', 'No', ''),
    ('NotificationBrowser', 'Yes', ''),
    ('NotificationBrowser', 'No', ''),
    ('NotificationEmail', 'Yes', ''),
    ('NotificationEmail', 'No', '');
GO

-- 20. Populate SettingUser table (1000 rows)
INSERT INTO SettingUser (UserId, SettingId)
SELECT TOP 1000
    u.Id,
    ABS(CHECKSUM(NEWID()) % 30) + 1
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [User] u
WHERE u.Id <= 1000 AND n <= 1000;
GO

-- 21. Populate FileContent table (1000 rows) with specific content
INSERT INTO FileContent (FileId, ContentChunk, ChunkIndex, CreatedAt)
SELECT TOP 1000
    f.Id,
    CASE f.FileTypeId
        WHEN 1 THEN -- docx
            'Report for File' + CAST(f.Id AS NVARCHAR(255)) + ', Section ' + CAST(ABS(CHECKSUM(NEWID()) % 10) + 1 AS NVARCHAR(10)) + 
            ': This document contains project details, including objectives and outcomes. Key points include analysis of data trends and recommendations for next steps.'
        WHEN 2 THEN -- excel
            'Date,Value,Sales\n' + 
            CAST(DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()) AS NVARCHAR(255)) + ',' + 
            CAST(ABS(CHECKSUM(NEWID()) % 1000) AS NVARCHAR(255)) + ',' + 
            CAST(ABS(CHECKSUM(NEWID()) % 5000) AS NVARCHAR(255))
        WHEN 3 THEN -- image
            'Image' + CAST(f.Id AS NVARCHAR(255)) + ': Resolution 1920x1080, Type: ' + 
            CASE ABS(CHECKSUM(NEWID()) % 3) 
                WHEN 0 THEN 'Landscape' 
                WHEN 1 THEN 'Portrait' 
                ELSE 'Square' 
            END + ', Tags: ' + 
            CASE ABS(CHECKSUM(NEWID()) % 3) 
                WHEN 0 THEN 'nature, sunset' 
                WHEN 1 THEN 'city, skyline' 
                ELSE 'abstract, art' 
            END
        WHEN 4 THEN -- video
            'Video' + CAST(f.Id AS NVARCHAR(255)) + ': Duration ' + 
            CAST(ABS(CHECKSUM(NEWID()) % 300) + 60 AS NVARCHAR(255)) + 's, Resolution: ' + 
            CASE ABS(CHECKSUM(NEWID()) % 2) 
                WHEN 0 THEN '4K' 
                ELSE '1080p' 
            END + ', Description: Tutorial on ' + 
            CASE ABS(CHECKSUM(NEWID()) % 3) 
                WHEN 0 THEN 'SQL programming' 
                WHEN 1 THEN 'cloud storage' 
                ELSE 'data analysis' 
            END
    END AS ContentChunk,
    ABS(CHECKSUM(NEWID()) % 10) + 1 AS ChunkIndex,
    DATEADD(DAY, -ABS(CHECKSUM(NEWID()) % 365), GETDATE()) AS CreatedAt
FROM (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n 
      FROM sys.objects s1 CROSS JOIN sys.objects s2) AS nums
CROSS JOIN [File] f
WHERE f.Id <= 1000 AND f.Status = 'active' AND n <= 1000;
GO


