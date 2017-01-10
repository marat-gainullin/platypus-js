/**
 * @name 124832514140608864
 * @public
*/ 
Select entities.MDENT_ID, entities.MDENT_NAME, entities.MDENT_TYPE
, entities.TAG1, entities.TAG2, entities.TAG3
, entities.MDENT_PARENT_ID, entities.MDENT_ORDER 
From MTD_ENTITIES entities
 Where ((:PARENT is null)
 and (entities.MDENT_PARENT_ID is null)) or (:PARENT = entities.MDENT_PARENT_ID)