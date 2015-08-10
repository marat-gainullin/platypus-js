/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.model.Model;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.Scripts;
import java.beans.PropertyEditorSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.openide.util.Exceptions;

/**
 *
 * @author vv
 */
public class CommonTypesEditor extends PropertyEditorSupport {

    private final String[] values;

    private CommonTypesEditor(String[] aValues) {
        super();
        values = aValues;
    }

    protected static Map<String, Collection<String>> genericRDBMSTypes = new HashMap<>();

    public static synchronized CommonTypesEditor getNewInstanceFor(Model model) {
        try {
            if (model instanceof DbSchemeModel) {
                Collection<String> types;
                DatabasesClient dbClient = ((DbSchemeModel) model).getBasesProxy();
                String datasourceName = ((DbSchemeModel) model).getDatasourceName();
                SqlDriver sqlSriver = dbClient.getMetadataCache(datasourceName).getDatasourceSqlDriver();
                TypesResolver resolver = sqlSriver.getTypesResolver();
                types = resolver.getSupportedTypes();
                if (types == null) {
                    types = genericRDBMSTypes.get(datasourceName);
                    if (types == null) {
                        types = new ArrayList<>();
                        DataSource ds = dbClient.obtainDataSource(datasourceName);
                        try (Connection conn = ds.getConnection()) {
                            try (ResultSet r = conn.getMetaData().getTypeInfo()) {
                                ColumnsIndicies i = new ColumnsIndicies(r.getMetaData());
                                int colIndex = i.find(ClientConstants.JDBCCOLS_TYPE_NAME);
                                while (r.next()) {
                                    String typeName = r.getString(colIndex);
                                    types.add(typeName);
                                }
                            }
                        }
                        genericRDBMSTypes.put(datasourceName, types);
                    }
                }
                return new CommonTypesEditor(types.toArray(new String[]{}));
            } else {
                return new CommonTypesEditor(new String[]{
                    Scripts.STRING_TYPE_NAME,
                    Scripts.NUMBER_TYPE_NAME,
                    Scripts.BOOLEAN_TYPE_NAME,
                    Scripts.DATE_TYPE_NAME,
                    Scripts.GEOMETRY_TYPE_NAME, ""});
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return new CommonTypesEditor(new String[]{ex.toString()});
        }
    }

    @Override
    public String getAsText() {
        return (String) (getValue() != null ? getValue() : "");
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setValue(text == null || text.isEmpty() ? null : text);
    }

    @Override
    public String[] getTags() {
        return values;
    }

}
