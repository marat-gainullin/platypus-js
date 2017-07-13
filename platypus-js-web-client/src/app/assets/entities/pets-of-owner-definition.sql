/**
 *
 * @author mgainullin
 * @name pets-of-owner
 * @public 
 */ 
Select * 
From PETS t1
 Where :ownerKey = t1.owner_id