USE GoogleDrive;
GO


--get user informations
select 
	u.id,
	u.Name as UserName,
	u.Email as UserEmail,
	u.LastLogin as UserLastLogin
from Users u
where u.id = 1;

--get user settings
select 
	u.Name as UserName,
	s.SettingKey,
	s.SettingValue
from SettingUser su
join Users u on su.UserId = u.Id
join Setting s on su.SettingId = s.Id
where u.Id =1

--Home
--get suggested folders/files of an user
select top 3
	r.Id,
	ot.Name as [Type],
	r.ObjectId,
	case
		when r.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
	r.Log,
	r.DateTime
from Recent r
left join Folder f on f.Id = r.ObjectId and r.ObjectTypeId = 1
left join Files fi on fi.Id = r.ObjectId and r.ObjectTypeId = 2
join Users u on r.UserId = u.Id
join ObjectType ot on r.ObjectTypeId = ot.Id
where u.Id = 1
order by r.DateTime DESC;

--My Drive
--get folders of an user
select
	f.id,
	f.Name as FolderName,
	f.Path as FolderPath,
	f.UpdatedAt as FolderUpdateTime,
	f.Size
from Folder f
join Users u on f.OwnerId = u.Id
where u.id = 1
	and f.Status = 'active'
order by f.UpdatedAt DESC;

--get files of an user
select 
	f.id,
	f.Name as [FileName],
	f.Path as FilePath,
	f.ModifiedDate as FileUpdateTime,
	ft.Name as FileType,
	f.Size
from Files f
join Users u on f.OwnerId = u.Id
join FileType ft on f.FileTypeId = ft.Id
where u.id = 1
	and f.Status = 'active'
order by f.ModifiedDate;

--Starred
--get starred files of an user
select
	fo.id,
    fo.ObjectId,
    fo.ObjectTypeId,
    ot.Name as ObjectType,
	case
		when fo.ObjectTypeId = 1 then f.Name
		else fi.Name 
	end as [Name]
from FavoriteObject fo
join Users u on fo.OwnerId = u.Id
join ObjectType ot on fo.ObjectTypeId = ot.Id
left join Folder f on fo.ObjectId = f.Id AND fo.ObjectTypeId = 1
left join  Files fi on fo.ObjectId = fi.Id AND fo.ObjectTypeId = 2
where fo.OwnerId = 1
order by ot.Name;

--Recent
--get recent files of an user
select
    r.Id,
    case
		when r.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
	case
		when r.ObjectTypeId = 1 then f.Path
		else fi.Path
	end as [Name],
    ft.Name as FileType,
    r.DateTime as LastAccessed
from Recent r
join Users u on r.Id = u.Id
left join Folder f on f.Id = r.ObjectId and r.ObjectTypeId = 1
left join Files fi on fi.Id = r.ObjectId and r.ObjectTypeId =2
join FileType ft on fi.FileTypeId = ft.Id
where r.UserId = 1
order by r.DateTime DESC;

--Shared with me
--get shared objects with an user
select 
    s.ObjectId,
    s.ObjectTypeId,
    ot.Name as ObjectType,
	case
		when s.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name],
    p.Name as Permission,
    u2.Name as SharerName,
    s.CreatedAt as ShareCreated,
    s.ExpiresAt as ShareExpires
from SharedUser su
join Share s on su.ShareId = s.Id
join Users u on su.Id = u.Id
join Users u2 on s.Sharer = u2.Id
join ObjectType ot on s.ObjectTypeId = ot.Id
join Permission p on su.PermissionId = p.Id
left join Folder f on s.ObjectId = f.Id and s.ObjectTypeId = 1
left join Files fi on s.ObjectId = fi.Id and s.ObjectTypeId = 2
where su.UserId = 1
  and s.ExpiresAt > GETDATE()
order by s.CreatedAt DESC;

--Trash
--get trash objects of an user
select 
	t.id,
	t.ObjectId,
	t.ObjectTypeId,
	t.RemovedDatetime,
	case
		when t.ObjectTypeId = 1 then f.Name
		else fi.Name
	end as [Name]
from Trash t
join Users u on t.UserId = u.Id
join ObjectType ot on t.ObjectTypeId = ot.id
left join Folder f on t.ObjectId = f.Id and t.ObjectTypeId = 1
left join Files fi on t.ObjectId = fi.Id and t.ObjectTypeId =2
where t.UserId = 1
order by t.RemovedDateTime DESC;

--Storage	
--get storage information of an user
select
	u.Capacity,
	u.UsedCapacity,
	u.Capacity - u.UsedCapacity as RemainingCapacity
from Users u
where u.Id = 1
order by RemainingCapacity DESC;

--get list files by size
select
	f.Name as FileName,
	f.Size
from Files f
join Users u on f.OwnerId = u.Id
where u.id = 1
order by f.Size DESC;

--get products bought by an user 
select
	p.Id,
	p.Name as ProductName,
	u.Name as UserName,
	up.PayingDatetime,
	up.EndDatetime,
	pr.Name as PromotionName
from UserProduct up
join Products p on up.ProductId = p.Id
join Users u on up.UserId = u.Id
join Promotion pr on up.PromotionId = pr.Id
where u.Id = 199

--get users banned from an user
select 
    bu.BannedUserId,
    u.Name as BannedUserName,
    u.Email as BannedUserEmail,
    bu.BannedAt
from BannedUser bu
JOIN Users u on bu.BannedUserId = u.Id
where bu.UserId = 1
order by bu.BannedAt DESC;

--get all subfolder of folder id 161
select * 
from Folder f
where f.Path like '/54%' and f.Status = 'active'

--get 10 most relevant files from search key word 'Report'
SELECT TOP 5 s.ObjectTypeId, s.ObjectId,  s.Term, t.BM25
FROM SearchIndex s
join TermBM25 t on s.Term = t.Term
WHERE s.Term IN ('Report')
ORDER BY t.BM25 DESC;

select * from Folder;