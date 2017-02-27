/**
 * 
 * @author mg
 * @name sub_query_compile
 */
SELECT T0.ORDER_NO, 'Some text' AS VALUE_FIELD_1, TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2, namedQuery4Tests T0 WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)
