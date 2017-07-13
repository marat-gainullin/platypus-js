/**
 *
 * @author mgainullin
 * @name pet-of-owner
 * @public 
 */ 
Select * 
From PETS t1
 Where :petKey = t1.pets_id
 and :ownerKey = t1.owner_id