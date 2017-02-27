/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public class ColumnsIndicies {

    private final Map<String, Integer> indicies = new HashMap<>();

    public ColumnsIndicies(ResultSetMetaData aRsmd) throws SQLException {
        super();
        for (int i = 1; i <= aRsmd.getColumnCount(); i++) {
            String asName = aRsmd.getColumnLabel(i);
            String name = asName != null && !asName.isEmpty() ? asName : aRsmd.getColumnName(i);
            indicies.put(name.toLowerCase(), i);
        }
    }

    public int find(String aName) {
        Integer idx = indicies.get(aName.toLowerCase());
        return idx != null ? idx : 0;
    }
}
