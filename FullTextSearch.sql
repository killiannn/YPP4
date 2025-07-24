
USE GoogleDrive;
GO

CREATE OR ALTER TRIGGER trg_FileIndexing
ON [File]
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Delete existing SearchIndex rows for the inserted/updated files
    DELETE FROM SearchIndex
    WHERE ObjectId IN (SELECT FileId FROM inserted)
    AND ObjectTypeId = 1; -- File

    -- Insert new SearchIndex rows with tokenized data from name and content
    WITH Tokenized AS (
        SELECT 
            i.FileId AS ObjectId,
            1 AS ObjectTypeId, -- File
            LOWER(t.value) AS Term, -- Case-insensitive terms
            COUNT(*) AS TermFrequency,
            STRING_AGG(CAST(t.rn AS NVARCHAR), ',') AS TermPositions,
            t.DocLength AS DocumentLength
        FROM inserted i
        CROSS APPLY (
            -- Tokenize file name (split on spaces, periods, hyphens)
            SELECT 
                value, 
                ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS rn,
                (SELECT COUNT(*) FROM STRING_SPLIT(REPLACE(REPLACE(i.Name, '.', ' '), '-', ' '), ' ') WHERE value <> '') +
                ISNULL((SELECT SUM(CAST(LEN(fc.ContentChunk) - LEN(REPLACE(fc.ContentChunk, ' ', '')) + 1 AS BIGINT))
                        FROM FileContent fc
                        WHERE fc.FileId = i.FileId
                        AND fc.ContentChunk IS NOT NULL), 0) AS DocLength
            FROM STRING_SPLIT(REPLACE(REPLACE(i.Name, '.', ' '), '-', ' '), ' ')
            WHERE value <> ''
            UNION ALL
            -- Tokenize file content from FileContent table (split on spaces, periods, commas, hyphens)
            SELECT 
                value, 
                ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS rn,
                (SELECT COUNT(*) FROM STRING_SPLIT(REPLACE(REPLACE(i.Name, '.', ' '), '-', ' '), ' ') WHERE value <> '') +
                ISNULL((SELECT SUM(CAST(LEN(fc2.ContentChunk) - LEN(REPLACE(fc2.ContentChunk, ' ', '')) + 1 AS BIGINT))
                        FROM FileContent fc2
                        WHERE fc2.FileId = i.FileId
                        AND fc2.ContentChunk IS NOT NULL), 0) AS DocLength
            FROM FileContent fc
            CROSS APPLY STRING_SPLIT(REPLACE(REPLACE(REPLACE(fc.ContentChunk, '.', ' '), ',', ' '), '-', ' '), ' ')
            WHERE fc.FileId = i.FileId
            AND fc.ContentChunk IS NOT NULL
            AND value <> ''
        ) t
        GROUP BY i.FileId, LOWER(t.value), t.DocLength
    )
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions
    FROM Tokenized;
END;
GO

CREATE OR ALTER TRIGGER trg_FolderIndexing
ON Folder
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Delete existing SearchIndex rows for the inserted/updated folders
    DELETE FROM SearchIndex
    WHERE ObjectId IN (SELECT FolderId FROM inserted)
    AND ObjectTypeId = 2; -- Folder

    -- Insert new SearchIndex rows with tokenized data
    WITH Tokenized AS (
        SELECT 
            i.FolderId AS ObjectId,
            2 AS ObjectTypeId, -- Folder
            LOWER(t.value) AS Term, -- Case-insensitive terms
            COUNT(*) AS TermFrequency,
            STRING_AGG(CAST(t.rn AS NVARCHAR), ',') AS TermPositions,
            t.DocLength AS DocumentLength
        FROM inserted i
        CROSS APPLY (
            SELECT 
                value, 
                ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS rn,
                (SELECT COUNT(*) FROM STRING_SPLIT(i.Name, ' ') WHERE value <> '') AS DocLength
            FROM STRING_SPLIT(i.Name, ' ')
            WHERE value <> ''
        ) t
        GROUP BY i.FolderId, LOWER(t.value), t.DocLength
    )
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions
    FROM Tokenized;
END;
GO


