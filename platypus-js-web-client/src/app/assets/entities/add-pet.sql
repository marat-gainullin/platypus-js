/**
 *
 * @author mgainullin
 * @name add-pet
 * @public 
 */ 
Insert into PETS
    (pets_id, owner_id, type_id, name)
values
    (:id,    :ownerId, :typeId, :name)