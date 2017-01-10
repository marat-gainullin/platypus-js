/**
 * @name test.ambigous.changes
 * @public
*/
select g.ID gid, g.NAME gname, k.ID kid, k.NAME kname, t.ID tid, t.NAME tname from ASSET_GROUPS g, ASSET_KINDS k, ASSET_TYPES t where g.ID = k.ID and g.ID = t.ID