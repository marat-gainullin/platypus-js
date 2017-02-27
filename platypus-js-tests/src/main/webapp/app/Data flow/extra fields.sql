/**
 * 
 * @author mg
 * @name extra_fields
 * @public
 */
Select t2.*, t1.*, 'some text' extra1, 52 extra2 From GOODORDER t2 Inner Join GOOD t1 on t2.GOOD = t1.GOOD_ID where t2.ORDER_ID = :aOrderId