/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.designer.application.dbdiagram.nodes.TableIndexChildren.DbTableIndexColumnKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author vv
 */
public class TableIndexChildren extends Children.Keys<DbTableIndexColumnKey> {

    DbTableIndexSpec index;
    FieldsEntity tableEntity;
    java.util.Map<DbTableIndexColumnSpec, DbTableIndexColumnKey> keysMap = new IdentityHashMap<>();

    public TableIndexChildren(DbTableIndexSpec anIndex, FieldsEntity aTableEntity) {
        super();
        index = anIndex;
        tableEntity = aTableEntity;
    }

    public void update() {
        setKeys(getKeys());
    }

    @Override
    protected void addNotify() {
        setKeys(getKeys());
        super.addNotify();
    }

    @Override
    protected void removeNotify() {
        setKeys(Collections.EMPTY_LIST);
        super.removeNotify();
    }

    @Override
    protected Node[] createNodes(DbTableIndexColumnKey key) {
        Node columnNode = new TableIndexColumnNode(key.column, tableEntity);
        return new Node[]{columnNode};
    }

    protected List<DbTableIndexColumnKey> getKeys() {
        index.sortColumns();
        java.util.Map<DbTableIndexColumnSpec, DbTableIndexColumnKey> km = new IdentityHashMap<>();
        List<DbTableIndexColumnKey> l = new ArrayList<>();
        for (DbTableIndexColumnSpec c : index.getColumns()) {
            DbTableIndexColumnKey key;
            if (keysMap.get(c) == null) {
                key = new DbTableIndexColumnKey(c);
            } else {
                key = keysMap.get(c);
            }
            l.add(key);
            km.put(c, key);
        }
        keysMap = km;
        return l;
    }

    public class DbTableIndexColumnKey {

        public final DbTableIndexColumnSpec column;

        public DbTableIndexColumnKey(DbTableIndexColumnSpec aColumn) {
            column = aColumn;
        }
    }
}
