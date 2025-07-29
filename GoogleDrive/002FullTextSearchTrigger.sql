USE GoogleDrive;
GO

-- Step 1: Create fn_TokenizeText function
CREATE OR ALTER FUNCTION dbo.fn_TokenizeText
(
    @InputText NVARCHAR(MAX)
)
RETURNS TABLE
AS
RETURN
(
    WITH SplitTerms AS (
        SELECT 
            LOWER(TRIM(value)) AS Term,
            ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS Position
        FROM STRING_SPLIT(
            REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(@InputText, '.', ' '), 
                        ',', ' '
                    ), 
                    '-', ' '
                ), 
                ':', ' '
            ), 
            ' '
        )
        WHERE TRIM(value) <> ''
    )
    SELECT 
        Term,
        COUNT(*) AS TermFrequency,
        STRING_AGG(CAST(Position AS NVARCHAR), ',') AS TermPositions,
        (SELECT COUNT(*) FROM SplitTerms) AS DocumentLength
    FROM SplitTerms
    GROUP BY Term
    HAVING Term <> ''
);
GO

-- Step 2: Create fn_CalculateBM25BM25 function
CREATE OR ALTER FUNCTION dbo.fn_CalculateBM25BM25
(
    @Term NVARCHAR(255),
    @ObjectTypeId INT
)
RETURNS FLOAT
AS
BEGIN
    DECLARE @TotalDocuments FLOAT;
    DECLARE @DocumentFrequency FLOAT;
    DECLARE @BM25 FLOAT;

    -- Count total documents (files or folders) based on ObjectTypeId, only active
    SELECT @TotalDocuments = COUNT(*)
    FROM (
        SELECT Id FROM [File] WHERE @ObjectTypeId = 2 AND Status = 'active'
        UNION
        SELECT Id FROM Folder WHERE @ObjectTypeId = 1 AND Status = 'active'
    ) AS Documents;

    -- Count documents containing the term
    SELECT @DocumentFrequency = COUNT(DISTINCT ObjectId)
    FROM SearchIndex
    WHERE Term = @Term
        AND ObjectTypeId = @ObjectTypeId;

    -- Calculate BM25 score, ensuring non-negative result
    SET @BM25 = CASE 
        WHEN @DocumentFrequency = 0 OR @TotalDocuments = 0 THEN 0
        ELSE MAX(0, LOG((@TotalDocuments - @DocumentFrequency + 0.5) / (@DocumentFrequency + 0.5)))
    END;

    RETURN @BM25;
END;
GO

-- Step 3: Create triggers
CREATE OR ALTER TRIGGER trg_FileIndexing
ON [File]
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Delete existing SearchIndex rows for the inserted/updated files
    DELETE FROM SearchIndex
    WHERE ObjectId IN (SELECT Id FROM inserted)
        AND ObjectTypeId = 2;

    -- Insert new SearchIndex rows for file names
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT 
        i.Id AS ObjectId,
        2 AS ObjectTypeId,
        t.Term,
        t.TermFrequency,
        t.DocumentLength,
        t.TermPositions
    FROM inserted i
    CROSS APPLY dbo.fn_TokenizeText(i.Name) t;

    -- Insert new SearchIndex rows for file content
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT 
        fc.FileId AS ObjectId,
        2 AS ObjectTypeId,
        t.Term,
        t.TermFrequency,
        t.DocumentLength,
        t.TermPositions
    FROM inserted i
    JOIN FileContent fc ON fc.FileId = i.Id
    CROSS APPLY dbo.fn_TokenizeText(fc.ContentChunk) t
    WHERE fc.ContentChunk IS NOT NULL;

    -- Update TermBM25 for affected terms (files only, ObjectTypeId = 2)
    MERGE INTO TermBM25 AS target
    USING (
        SELECT DISTINCT Term
        FROM SearchIndex
        WHERE ObjectId IN (SELECT Id FROM inserted)
            AND ObjectTypeId = 2
    ) AS source
    ON target.Term = source.Term
    WHEN MATCHED THEN
        UPDATE SET 
            BM25 = dbo.fn_CalculateBM25BM25(source.Term, 2),
            LastUpdated = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (Term, BM25, LastUpdated)
        VALUES (source.Term, dbo.fn_CalculateBM25BM25(source.Term, 2), GETDATE());
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
    WHERE ObjectId IN (SELECT Id FROM inserted)
        AND ObjectTypeId = 1;

    -- Insert new SearchIndex rows for folder names
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT 
        i.Id AS ObjectId,
        1 AS ObjectTypeId,
        t.Term,
        t.TermFrequency,
        t.DocumentLength,
        t.TermPositions
    FROM inserted i
    CROSS APPLY dbo.fn_TokenizeText(i.Name) t;

    -- Update TermBM25 for affected terms (folders only, ObjectTypeId = 1)
    MERGE INTO TermBM25 AS target
    USING (
        SELECT DISTINCT Term
        FROM SearchIndex
        WHERE ObjectId IN (SELECT Id FROM inserted)
            AND ObjectTypeId = 1
    ) AS source
    ON target.Term = source.Term
    WHEN MATCHED THEN
        UPDATE SET 
            BM25 = dbo.fn_CalculateBM25BM25(source.Term, 1),
            LastUpdated = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (Term, BM25, LastUpdated)
        VALUES (source.Term, dbo.fn_CalculateBM25BM25(source.Term, 1), GETDATE());
END;
GO

CREATE OR ALTER TRIGGER trg_FileContentIndexing
ON FileContent
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Delete existing SearchIndex rows for the affected files
    DELETE FROM SearchIndex
    WHERE ObjectId IN (SELECT FileId FROM inserted)
        AND ObjectTypeId = 2;

    -- Insert new SearchIndex rows for file content
    INSERT INTO SearchIndex (ObjectId, ObjectTypeId, Term, TermFrequency, DocumentLength, TermPositions)
    SELECT 
        i.FileId AS ObjectId,
        2 AS ObjectTypeId,
        t.Term,
        t.TermFrequency,
        t.DocumentLength,
        t.TermPositions
    FROM inserted i
    CROSS APPLY dbo.fn_TokenizeText(i.ContentChunk) t
    WHERE i.ContentChunk IS NOT NULL;

    -- Update TermBM25 for affected terms (files only, ObjectTypeId = 2)
    MERGE INTO TermBM25 AS target
    USING (
        SELECT DISTINCT Term
        FROM SearchIndex
        WHERE ObjectId IN (SELECT FileId FROM inserted)
            AND ObjectTypeId = 2
    ) AS source
    ON target.Term = source.Term
    WHEN MATCHED THEN
        UPDATE SET 
            BM25 = dbo.fn_CalculateBM25BM25(source.Term, 2),
            LastUpdated = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (Term, BM25, LastUpdated)
        VALUES (source.Term, dbo.fn_CalculateBM25BM25(source.Term, 2), GETDATE());
END;
GO