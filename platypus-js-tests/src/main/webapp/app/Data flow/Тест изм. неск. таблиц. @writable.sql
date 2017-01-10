/**
 * @name test.ambigous.changes.mutatable
 * @writable Asset_Kinds, AssET_TYPES
 * @public
 */ 
Select g.ID gid, g.NAME gname, k.ID kid
, k.NAME kname, t.ID tid, t.NAME tname 
From ASSET_KINDS k
 inner join ASSET_TYPES t on (k.ID = t.ID)
 left outer join ASSET_GROUPS g on (k.ID = g.ID)