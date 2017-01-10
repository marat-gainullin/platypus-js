/**
 *
 * @author AB
 * @public
 * @name reportQuery
 */ 
Select * 
From MEASURANDS t1
 Where :idValue = t1.ID or :idValue is null