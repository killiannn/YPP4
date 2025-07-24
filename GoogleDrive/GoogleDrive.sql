
USE master;
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'GoogleDrive')
BEGIN
    ALTER DATABASE GoogleDrive SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GoogleDrive;
END;
GO

-- Create database
CREATE DATABASE GoogleDrive;
GO

USE GoogleDrive;
GO

IF OBJECT_ID('dbo.FileContent', 'U') IS NOT NULL DROP TABLE dbo.FileContent;
IF OBJECT_ID('dbo.TermIDF', 'U') IS NOT NULL DROP TABLE dbo.TermIDF;
IF OBJECT_ID('dbo.SearchIndex', 'U') IS NOT NULL DROP TABLE dbo.SearchIndex;
IF OBJECT_ID('dbo.SearchHistory', 'U') IS NOT NULL DROP TABLE dbo.SearchHistory;
IF OBJECT_ID('dbo.Recent', 'U') IS NOT NULL DROP TABLE dbo.Recent;
IF OBJECT_ID('dbo.FavoriteObject', 'U') IS NOT NULL DROP TABLE dbo.FavoriteObject;
IF OBJECT_ID('dbo.BannedUser', 'U') IS NOT NULL DROP TABLE dbo.BannedUser;
IF OBJECT_ID('dbo.Promotion', 'U') IS NOT NULL DROP TABLE dbo.Promotion;
IF OBJECT_ID('dbo.UserProduct', 'U') IS NOT NULL DROP TABLE dbo.UserProduct;
IF OBJECT_ID('dbo.Product', 'U') IS NOT NULL DROP TABLE dbo.Product;
IF OBJECT_ID('dbo.Trash', 'U') IS NOT NULL DROP TABLE dbo.Trash;
IF OBJECT_ID('dbo.FileVersion', 'U') IS NOT NULL DROP TABLE dbo.FileVersion;
IF OBJECT_ID('dbo.SharedUser', 'U') IS NOT NULL DROP TABLE dbo.SharedUser;
IF OBJECT_ID('dbo.Share', 'U') IS NOT NULL DROP TABLE dbo.Share;
IF OBJECT_ID('dbo.FileType', 'U') IS NOT NULL DROP TABLE dbo.FileType;
IF OBJECT_ID('dbo.[File]', 'U') IS NOT NULL DROP TABLE dbo.[File];
IF OBJECT_ID('dbo.Folder', 'U') IS NOT NULL DROP TABLE dbo.Folder;
IF OBJECT_ID('dbo.Permission', 'U') IS NOT NULL DROP TABLE dbo.Permission;
IF OBJECT_ID('dbo.ObjectType', 'U') IS NOT NULL DROP TABLE dbo.ObjectType;
IF OBJECT_ID('dbo.[Session]', 'U') IS NOT NULL DROP TABLE dbo.[Session];
IF OBJECT_ID('dbo.[User]', 'U') IS NOT NULL DROP TABLE dbo.[User];
IF OBJECT_ID('dbo.[Setting]', 'U') IS NOT NULL DROP TABLE dbo.[Setting];
IF OBJECT_ID('dbo.[SettingUser]', 'U') IS NOT NULL DROP TABLE dbo.[SettingUser];
GO

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
GO

CREATE TABLE Permission (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE ObjectType (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE Folder (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ParentId INT,
    OwnerId INT NOT NULL,
    Name NVARCHAR(255) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    UpdatedAt DATETIME,
    Path VARCHAR(255),
    Status VARCHAR(50),
    FOREIGN KEY (ParentId) REFERENCES Folder(Id),
    FOREIGN KEY (OwnerId) REFERENCES [User](Id)
);
GO

CREATE TABLE FileType (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    Icon NVARCHAR(255)
);
GO

CREATE TABLE [File] (
    Id INT PRIMARY KEY IDENTITY(1,1),
    FolderId INT,
    OwnerId INT NOT NULL,
    Size BIGINT,
    Name NVARCHAR(255) NOT NULL,
    Path NVARCHAR(MAX),
    FileTypeId INT,
    ModifiedDate DATETIME,
    Status NVARCHAR(50),
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (FolderId) REFERENCES Folder(Id),
    FOREIGN KEY (OwnerId) REFERENCES [User](Id),
    FOREIGN KEY (FileTypeId) REFERENCES FileType(Id)
);
GO

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
GO

CREATE TABLE SharedUser (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ShareId INT,
    UserId INT,
    PermissionId INT,
    FOREIGN KEY (ShareId) REFERENCES Share(Id),
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (PermissionId) REFERENCES Permission(Id)
);
GO

CREATE TABLE FileVersion (
    Id INT PRIMARY KEY IDENTITY(1,1),
    FileId INT,
    Version INT NOT NULL,
    Path NVARCHAR(MAX),
    CreatedAt DATETIME2,
    UpdateBy INT,
    IsCurrent BIT,
    VersionFile NVARCHAR(MAX),
    Size BIGINT,
    FOREIGN KEY (FileId) REFERENCES [File](Id),
    FOREIGN KEY (UpdateBy) REFERENCES [User](Id)
);
GO

CREATE TABLE Trash (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ObjectId INT NOT NULL,
    ObjectTypeId INT NOT NULL,
    RemovedDatetime DATETIME2,
    UserId INT,
    IsPermanent BIT DEFAULT 0,
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (ObjectTypeId) REFERENCES ObjectType(Id)
);
GO

CREATE TABLE [Product] (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    Cost DECIMAL(10,2) NOT NULL,
    Duration INT NOT NULL
);
GO

CREATE TABLE Promotion (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    Discount DECIMAL(5,2) NOT NULL,
    IsPercent BIT NOT NULL
);
GO

CREATE TABLE UserProduct (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT,
    ProductId INT,
    PayingDatetime DATETIME2,
    IsFirstPaying BIT,
    PromotionId INT,
    EndDatetime DATETIME2,
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (ProductId) REFERENCES [Product](Id),
    FOREIGN KEY (PromotionId) REFERENCES Promotion(Id)
);
GO

CREATE TABLE BannedUser (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT,
    BannedAt DATETIME2,
    BannedUserId INT,
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (BannedUserId) REFERENCES [User](Id)
);
GO

CREATE TABLE FavoriteObject (
    Id INT PRIMARY KEY IDENTITY(1,1),
    OwnerId INT,
    ObjectId INT NOT NULL,
    ObjectTypeId INT NOT NULL,
    FOREIGN KEY (OwnerId) REFERENCES [User](Id),
    FOREIGN KEY (ObjectTypeId) REFERENCES ObjectType(Id)
);
GO

CREATE TABLE Recent (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT,
    FileId INT,
    Log NVARCHAR(MAX),
    DateTime DATETIME2,
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (FileId) REFERENCES [File](Id)
);
GO

CREATE TABLE SearchHistory (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT,
    SearchToken NVARCHAR(MAX),
    SearchDatetime DATETIME,
    FOREIGN KEY (UserId) REFERENCES [User](Id)
);
GO

CREATE TABLE [Session] (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT,
    Token NVARCHAR(255) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    ExpiresAt DATETIME,
    FOREIGN KEY (UserId) REFERENCES [User](Id)
);
GO

CREATE TABLE Setting (
    Id INT PRIMARY KEY IDENTITY(1,1),
    SettingKey VARCHAR(MAX),
    SettingValue VARCHAR(MAX),
    Decription VARCHAR(MAX)
);
GO

CREATE TABLE SettingUser (
    SettingUserId INT PRIMARY KEY IDENTITY(1,1),
    SettingId INT,
    UserId INT,
    FOREIGN KEY (UserId) REFERENCES [User](Id),
    FOREIGN KEY (SettingId) REFERENCES [Setting](Id)
);
GO

CREATE INDEX idx_file_name ON [File](Name);
CREATE INDEX idx_folder_name ON Folder(Name);
GO

CREATE TABLE SearchIndex (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ObjectId INT NOT NULL,
    ObjectTypeId INT NOT NULL,
    Term NVARCHAR(255) NOT NULL,
    TermFrequency INT NOT NULL,
    DocumentLength INT NOT NULL,
    TermPositions NVARCHAR(MAX),
    FOREIGN KEY (ObjectTypeId) REFERENCES ObjectType(Id),
    CONSTRAINT UC_SearchIndex UNIQUE (ObjectId, ObjectTypeId, Term)
);
GO

CREATE NONCLUSTERED INDEX IX_SearchIndex_Term ON SearchIndex (Term, ObjectTypeId);
GO

CREATE TABLE TermIDF (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Term NVARCHAR(255) NOT NULL UNIQUE,
    IDF FLOAT NOT NULL,
    LastUpdated DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT CHK_IDF_NonNegative CHECK (IDF >= 0)
);
GO

CREATE NONCLUSTERED INDEX IX_TermIDF_Term ON TermIDF (Term);
GO

CREATE TABLE FileContent (
    Id INT PRIMARY KEY IDENTITY(1,1),
    FileId INT NOT NULL,
    FOREIGN KEY (FileId) REFERENCES [File](Id),
    ContentChunk NVARCHAR(MAX),
    ChunkIndex INT NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO
