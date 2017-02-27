/**
 * @name 128082898425059
*/
Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER 
From GOODORDER goodOrder
 Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)
 and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)
 Where :PARAM1 = goodOrder.GOOD