/**
 * @name 128032324209306
 * @public
*/
Select measurands.ID mId, measurands.NAME mName, units.* 
From MEASURANDS measurands
 Inner Join q128032313214099 units on measurands.ID = units.MEASURAND