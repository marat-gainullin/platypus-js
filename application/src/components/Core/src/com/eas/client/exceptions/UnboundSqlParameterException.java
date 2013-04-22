/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.exceptions;

/** A parameter name referenced in SQL query text is missing from parameters
 * names-to-values map.
 *
 * @author pk
 */
public class UnboundSqlParameterException extends Exception {
    private String paramName;
    private String sql;

    /** Creates an instance for given parameter name and SQL query text.
     *
     * @param paramName parameter name.
     * @param sql SQL query text.
     */
    public UnboundSqlParameterException(String paramName, String sql)
    {
        this.paramName = paramName;
        this.sql = sql;
    }

    @Override
    public String toString()
    {
        return String.format("Unbound parameter '%s' in query '%s'", this.paramName, this.sql);
    }

}
