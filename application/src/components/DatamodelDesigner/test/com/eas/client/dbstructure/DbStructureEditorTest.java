/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.eas.client.Client;
import com.eas.client.ClientFactory;
import com.eas.client.DbClient;
import com.eas.client.dbstructure.gui.DbSchemeEditorView;
import com.eas.client.dbstructure.gui.edits.ChangeRelationTagsEdit;
import com.eas.client.dbstructure.gui.edits.CreateFkEdit;
import com.eas.client.dbstructure.gui.edits.CreateTableEdit;
import com.eas.client.dbstructure.gui.edits.DropFkEdit;
import com.eas.client.dbstructure.gui.edits.DropTableEdit;
import com.eas.client.dbstructure.gui.edits.RecreateFkEdit;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.DeleteEntityEdit;
import com.eas.client.model.gui.edits.DeleteRelationEdit;
import com.eas.client.model.gui.edits.NewEntityEdit;
import com.eas.client.model.gui.view.model.DbSchemeModelView;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.model.gui.view.fields.TableFieldsView;
import com.eas.client.model.gui.view.model.DbSchemeModelView.DropFkRemoveTableAction;
import com.eas.client.model.gui.view.model.ModelView;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class DbStructureEditorTest {

    protected DbSchemeModel model;
    protected JFrame fr = null;
    protected DbSchemeEditorView view = null;

    private void clearCache(String aTableName) throws Exception {
        model.getClient().dbTableChanged(model.getDbId(), model.getSchema(), aTableName);
    }

    @Before
    public void setUp() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@//asvr:1521/adb");
        Properties props = new Properties();
        props.put("user", "eas");
        props.put("password", "eas");
        props.put("schema", "eas");
        settings.setInfo(props);
        //String platHome = System.getProperty(ClientConstants.PLATYPUS_HOME_PROP_NAME);
        Client lclient = ClientFactory.getInstance(settings);
        assertTrue(lclient instanceof DbClient);
        DbClient client = (DbClient) lclient;
        //System.setProperty("java.security.auth.login.config", StringUtils.join(File.separator, platHome, "lib", "client", "settings", "login.config"));
        fr = new JFrame("test");
        fr.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        fr.getContentPane().setLayout(new BorderLayout());
        model = new DbSchemeModel(client);

        view = new DbSchemeEditorView(model, null, new UndoManager(), null);
        //DbSchemaDocumentProvider provider = DbSchemaDocumentProvider.getInstance(null, new DocumentsContainerStub());
        fr.getContentPane().add(view, BorderLayout.CENTER);
        fr.setSize(600, 540);
        fr.setVisible(true);
        //for (int i = 0; i < 3; i++) {
        try {
            if (isTablePresent(client, LEFT_TEST_TABLE_NAME)) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, (String) null, "drop table " + LEFT_TEST_TABLE_NAME);
                q.enqueueUpdate();
                model.getClient().commit(null);
            }
        } catch (Exception ex) {
        }
        try {
            if (isTablePresent(client, RIGHT_TEST_TABLE_NAME)) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, (String) null, "drop table " + RIGHT_TEST_TABLE_NAME);
                q.enqueueUpdate();
                model.getClient().commit(null);
            }
        } catch (Exception ex) {
        }
        try {
            if (isTablePresent(client, CENTER_TEST_TABLE_NAME)) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, (String) null, "drop table " + CENTER_TEST_TABLE_NAME);
                q.enqueueUpdate();
                model.getClient().commit(null);
            }
        } catch (Exception ex) {
        }
        //}
    }

    @After
    public void tearDown() {
        fr.setVisible(false);
        fr.dispose();
    }
    protected static final String LEFT_TEST_TABLE_NAME = "left_table_4autotests";
    protected static final String CENTER_TEST_TABLE_NAME = "center_table_4autotests";
    protected static final String RIGHT_TEST_TABLE_NAME = "right_table_4autotests";

    protected boolean isTablePresent(DbClient aClient, String aTableName) {
        try {
            SqlCompiledQuery q = new SqlCompiledQuery(aClient, "select * from " + aTableName);
            Rowset rs = q.executeQuery();
            assertTrue(rs.isEmpty());
            return true;
        } catch (Exception ex1) {
            return false;
        }
    }

    @Test
    public void testCreateTable() throws Exception {
        CreateTableEdit edit = new CreateTableEdit(view.getSqlController(), LEFT_TEST_TABLE_NAME);
        edit.redo();
        model.getClient().commit(null);
        assertTrue(isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        edit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        edit.redo();
        model.getClient().commit(null);
        assertTrue(isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        edit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
    }

    @Test
    public void testAddTable() throws Exception {
        CreateTableEdit edit = new CreateTableEdit(view.getSqlController(), LEFT_TEST_TABLE_NAME);
        edit.redo();
        model.getClient().commit(null);
        // begin test add table capability
        FieldsEntity ent = model.newGenericEntity();
        ent.setTableName(LEFT_TEST_TABLE_NAME);
        NewEntityEdit<FieldsEntity, DbSchemeModel> edit1 = new NewEntityEdit<>(model, ent);
        edit1.redo();
        assertTrue(model.getEntities().size() == 1);
        edit1.undo();
        assertTrue(model.getEntities().isEmpty());
        // cleanup
        edit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
    }

    @Test
    public void testCreateAndAddTable() throws Exception {
        CreateTableEdit cEdit = null;
        CompoundEdit section = new CompoundEdit();
        try {
            cEdit = new CreateTableEdit(view.getSqlController(), LEFT_TEST_TABLE_NAME);
            section.addEdit(cEdit);
            cEdit.redo();
            model.getClient().commit(null);
        } catch (Exception ex) {
            model.getClient().rollback(null);
            section = null;
            assertTrue(false);
        }
        NewEntityEdit<FieldsEntity, DbSchemeModel> edit1 = null;
        try {
            FieldsEntity ent = model.newGenericEntity();
            ent.setTableName(LEFT_TEST_TABLE_NAME);
            edit1 = new NewEntityEdit<>(model, ent);
            edit1.redo();
        } catch (Exception ex) {
            section = null;
            assertTrue(false);
            return;
        }
        section.addEdit(edit1);
        section.end();

        section.undo();
        assertTrue(model.getEntities().isEmpty());
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        section.redo();
        assertTrue(!model.getEntities().isEmpty());
        assertTrue(isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        section.undo();
        assertTrue(model.getEntities().isEmpty());
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        section.redo();
        assertTrue(!model.getEntities().isEmpty());
        assertTrue(isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
        section.undo();
        assertTrue(model.getEntities().isEmpty());
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
    }

    @Test
    public void testRemoveTable() throws Exception {
        // setup the environment
        CreateTableEdit edit = new CreateTableEdit(view.getSqlController(), LEFT_TEST_TABLE_NAME);
        edit.redo();
        model.getClient().commit(null);
        FieldsEntity ent = model.newGenericEntity();
        ent.setTableName(LEFT_TEST_TABLE_NAME);
        NewEntityEdit<FieldsEntity, DbSchemeModel> edit1 = new NewEntityEdit<>(model, ent);
        edit1.redo();
        assertTrue(model.getEntities().size() == 1);
        // begin test remove table capability
        DeleteEntityEdit rEdit = new DeleteEntityEdit(model, ent);
        rEdit.redo();
        assertTrue(model.getEntities().isEmpty());
        rEdit.undo();
        assertTrue(!model.getEntities().isEmpty());
        rEdit.redo();
        assertTrue(model.getEntities().isEmpty());
        rEdit.undo();
        assertTrue(!model.getEntities().isEmpty());
        // cleanup
        edit1.undo();
        assertTrue(model.getEntities().isEmpty());
        edit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
    }
    private CreateTableEdit leftTableCreateEdit;
    private NewEntityEdit<FieldsEntity, DbSchemeModel> leftTableAddEdit;
    private CreateTableEdit centerTableCreateEdit;
    private NewEntityEdit<FieldsEntity, DbSchemeModel> centerTableAddEdit;

    private void createLeftAndCenterTables() throws Exception {
        leftTableCreateEdit = new CreateTableEdit(view.getSqlController(), LEFT_TEST_TABLE_NAME);
        leftTableCreateEdit.redo();
        model.getClient().commit(null);
        FieldsEntity ent = model.newGenericEntity();
        ent.setTableName(LEFT_TEST_TABLE_NAME);
        leftTableAddEdit = new NewEntityEdit<>(model, ent);
        leftTableAddEdit.redo();
        assertTrue(model.getEntities().size() == 1);
        centerTableCreateEdit = new CreateTableEdit(view.getSqlController(), CENTER_TEST_TABLE_NAME);
        centerTableCreateEdit.redo();
        model.getClient().commit(null);
        FieldsEntity centerEnt = model.newGenericEntity();
        centerEnt.setTableName(CENTER_TEST_TABLE_NAME);
        centerTableAddEdit = new NewEntityEdit<>(model, centerEnt);
        centerTableAddEdit.redo();
        assertTrue(model.getEntities().size() == 2);
    }

    private void removeLeftAndCenterTables() throws Exception {
        centerTableAddEdit.undo();
        assertTrue(model.getEntities().size() == 1);
        centerTableCreateEdit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), CENTER_TEST_TABLE_NAME));
        leftTableAddEdit.undo();
        assertTrue(model.getEntities().isEmpty());
        leftTableCreateEdit.undo();
        model.getClient().commit(null);
        assertTrue(!isTablePresent(model.getClient(), LEFT_TEST_TABLE_NAME));
    }

    @Test
    public void testAddFk() throws Exception {
        // setup the environment
        createLeftAndCenterTables();
        try {
            // perform the test
            UndoManager schemeEditor = new UndoManager();
            final CompoundEdit section1 = new CompoundEdit();
            try {
                ForeignKeySpec fkSpec = new ForeignKeySpec((String) null, LEFT_TEST_TABLE_NAME, LEFT_TEST_TABLE_NAME + "_id", "trest_fk1", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, true, (String) null, CENTER_TEST_TABLE_NAME, CENTER_TEST_TABLE_NAME + "_id", null);
                CreateFkEdit fkEdit = new CreateFkEdit(view.getSqlController(), fkSpec, null);
                fkEdit.redo();
                model.getClient().commit(null);
                section1.addEdit(fkEdit);
                clearCache(LEFT_TEST_TABLE_NAME);
                clearCache(CENTER_TEST_TABLE_NAME);
                clearCache(RIGHT_TEST_TABLE_NAME);
                view.getDmView().addUndoableEditListener(new UndoableEditListener() {
                    @Override
                    public void undoableEditHappened(UndoableEditEvent e) {
                        section1.addEdit(e.getEdit());
                    }
                });
                view.getDmView().addFkRelations(false, CENTER_TEST_TABLE_NAME);
            } finally {
                section1.end();
                schemeEditor.addEdit(section1);
            }
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().size() == 1);
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            schemeEditor.redo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().size() == 1);
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
        } finally {
            // cleanup
            removeLeftAndCenterTables();
        }
    }

    @Test
    public void testRemoveFk() throws Exception {
        // setup the environment
        createLeftAndCenterTables();
        try {
            ForeignKeySpec fkSpec = new ForeignKeySpec((String) null, LEFT_TEST_TABLE_NAME, LEFT_TEST_TABLE_NAME + "_id", "trest_fk1", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, true, (String) null, CENTER_TEST_TABLE_NAME, CENTER_TEST_TABLE_NAME + "_id", null);
            UndoManager schemeEditor = new UndoManager();
            final CompoundEdit section1 = new CompoundEdit();
            try {
                CreateFkEdit fkEdit = new CreateFkEdit(view.getSqlController(), fkSpec, null);
                fkEdit.redo();
                model.getClient().commit(null);
                section1.addEdit(fkEdit);
                clearCache(LEFT_TEST_TABLE_NAME);
                clearCache(CENTER_TEST_TABLE_NAME);
                clearCache(RIGHT_TEST_TABLE_NAME);

                view.getDmView().addUndoableEditListener(new UndoableEditListener() {
                    @Override
                    public void undoableEditHappened(UndoableEditEvent e) {
                        section1.addEdit(e.getEdit());
                    }
                });
                view.getDmView().addFkRelations(false, CENTER_TEST_TABLE_NAME);
            } finally {
                section1.end();
                schemeEditor.addEdit(section1);
            }
            // perform the test
            CompoundEdit section2 = new CompoundEdit();
            try {
                DropFkEdit fkEdit = new DropFkEdit(view.getSqlController(), fkSpec, null);
                fkEdit.redo();
                model.getClient().commit(null);
                section2.addEdit(fkEdit);
                DeleteRelationEdit delRelEdit = new DeleteRelationEdit(model.getRelations().iterator().next());
                delRelEdit.redo();
                section2.addEdit(delRelEdit);
            } finally {
                section2.end();
                schemeEditor.addEdit(section2);
            }
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().size() == 1);
            schemeEditor.redo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().size() == 1);

            // previous undo section
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            schemeEditor.redo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().size() == 1);
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
        } finally {
            // cleanup
            removeLeftAndCenterTables();
        }
    }

    @Test
    public void testModifyFk() throws Exception {
        // setup the environment
        createLeftAndCenterTables();
        try {
            ForeignKeySpec fkSpec = new ForeignKeySpec((String) null, LEFT_TEST_TABLE_NAME, LEFT_TEST_TABLE_NAME + "_id", "trest_fk1", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, (String) null, CENTER_TEST_TABLE_NAME, CENTER_TEST_TABLE_NAME + "_id", null);

            ForeignKeySpec newFkSpec = (ForeignKeySpec) fkSpec.copy();
            newFkSpec.setFkDeferrable(false);
            newFkSpec.setFkDeleteRule(ForeignKeySpec.ForeignKeyRule.SETDEFAULT);
            newFkSpec.setFkUpdateRule(ForeignKeySpec.ForeignKeyRule.SETDEFAULT);

            UndoManager schemeEditor = new UndoManager();
            final CompoundEdit section1 = new CompoundEdit();
            try {
                CreateFkEdit fkEdit = new CreateFkEdit(view.getSqlController(), fkSpec, null);
                fkEdit.redo();
                model.getClient().commit(null);
                section1.addEdit(fkEdit);
                clearCache(LEFT_TEST_TABLE_NAME);
                clearCache(CENTER_TEST_TABLE_NAME);
                clearCache(RIGHT_TEST_TABLE_NAME);
                UndoableEditListener l1 = new UndoableEditListener() {
                    @Override
                    public void undoableEditHappened(UndoableEditEvent e) {
                        section1.addEdit(e.getEdit());
                    }
                };
                view.getDmView().addUndoableEditListener(l1);
                view.getDmView().addFkRelations(false, CENTER_TEST_TABLE_NAME);
                view.getDmView().removeUndoableEditListener(l1);
            } finally {
                section1.end();
                schemeEditor.addEdit(section1);
            }
            // perform the test
            Relation<FieldsEntity> relation = model.getRelations().iterator().next();

            final CompoundEdit section2 = new CompoundEdit();
            try {
                RecreateFkEdit fkEdit = new RecreateFkEdit(view.getSqlController(), fkSpec, newFkSpec);
                fkEdit.redo();
                section2.addEdit(fkEdit);
                ChangeRelationTagsEdit relsEdit = new ChangeRelationTagsEdit(relation);
                relsEdit.recordBeforeState(relation);
                relation.setFkUpdateRule(ForeignKeySpec.ForeignKeyRule.SETDEFAULT);
                relation.setFkDeleteRule(ForeignKeySpec.ForeignKeyRule.SETDEFAULT);
                relation.setFkDeferrable(true);
                relation.setFkName("trest_fk2");
                relsEdit.recordAfterState(relation);
                section2.addEdit(relsEdit);
            } finally {
                section2.end();
                schemeEditor.addEdit(section2);
            }
            assertTrue(!model.getRelations().isEmpty());
            schemeEditor.undo(); // section2
            assertEquals(ForeignKeySpec.ForeignKeyRule.CASCADE, relation.getFkUpdateRule());
            assertEquals(ForeignKeySpec.ForeignKeyRule.CASCADE, relation.getFkDeleteRule());
            assertFalse(relation.isFkDeferrable());
            assertEquals("trest_fk1", relation.getFkName().toLowerCase());
            schemeEditor.redo(); // section2
            assertTrue(!model.getRelations().isEmpty());
            assertEquals(ForeignKeySpec.ForeignKeyRule.SETDEFAULT, relation.getFkUpdateRule());
            assertEquals(ForeignKeySpec.ForeignKeyRule.SETDEFAULT, relation.getFkDeleteRule());
            assertTrue(relation.isFkDeferrable());
            assertEquals("trest_fk2", relation.getFkName().toLowerCase());
            schemeEditor.undo(); // section2
            assertEquals(ForeignKeySpec.ForeignKeyRule.CASCADE, relation.getFkUpdateRule());
            assertEquals(ForeignKeySpec.ForeignKeyRule.CASCADE, relation.getFkDeleteRule());
            assertFalse(relation.isFkDeferrable());
            assertEquals("trest_fk1", relation.getFkName().toLowerCase());
            // cleanup
            schemeEditor.undo(); // section1
            assertTrue(model.getRelations().isEmpty());
        } finally {
            removeLeftAndCenterTables();
        }
    }

    @Test
    public void testRemoveTableWithFks() throws Exception {
        // setup the environment
        createLeftAndCenterTables();
        try {
            model.getClient().commit(null);
            Fields fields = new Fields();
            Field field = new Field(RIGHT_TEST_TABLE_NAME + "_id", "тестовое поле - ключ тестовой таблицы");
            field.setPk(true);
            field.setNullable(false);
            field.setTypeInfo(DataTypeInfo.INTEGER);
            field.setSize(10);
            field.setPrecision(10);
            field.setTableName(RIGHT_TEST_TABLE_NAME);
            fields.add(field);

            Field field1 = new Field("fk1", "тестовое поле - ключ левой таблицы");
            field1.setTypeInfo(DataTypeInfo.INTEGER);
            field1.setSize(10);
            field1.setPrecision(10);
            //PrimaryKeySpec pkSpec = new PrimaryKeySpec(null, null, LEFT_TEST_TABLE_NAME, LEFT_TEST_TABLE_NAME+"_id", LEFT_TEST_TABLE_NAME+"_fk1");
            //field1.setFk(pkSpec);
            field1.setTableName(RIGHT_TEST_TABLE_NAME);
            fields.add(field1);

            Field field2 = new Field("fk2", "тестовое поле - ключ центральной таблицы");
            field2.setTypeInfo(DataTypeInfo.INTEGER);
            field2.setSize(10);
            field2.setPrecision(10);
            //PrimaryKeySpec pkSpec1 = new PrimaryKeySpec(null, null, CENTER_TEST_TABLE_NAME, CENTER_TEST_TABLE_NAME+"_id", CENTER_TEST_TABLE_NAME+"_fk2");
            //field2.setFk(pkSpec1);
            field2.setTableName(RIGHT_TEST_TABLE_NAME);
            fields.add(field2);

            DropTableEdit edit = new DropTableEdit(view.getSqlController(), RIGHT_TEST_TABLE_NAME, fields, null);
            edit.undo();
            model.getClient().commit(null);
            ForeignKeySpec fk1Spec = new ForeignKeySpec((String) null, LEFT_TEST_TABLE_NAME, LEFT_TEST_TABLE_NAME + "_id", LEFT_TEST_TABLE_NAME + "_fk1", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, (String) null, RIGHT_TEST_TABLE_NAME, RIGHT_TEST_TABLE_NAME + "_id", null);
            CreateFkEdit fk1Edit = new CreateFkEdit(view.getSqlController(), fk1Spec, null);
            fk1Edit.redo();
            model.getClient().commit(null);
            ForeignKeySpec fk2Spec = new ForeignKeySpec((String) null, RIGHT_TEST_TABLE_NAME, RIGHT_TEST_TABLE_NAME + "_id", RIGHT_TEST_TABLE_NAME + "_fk1", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, (String) null, CENTER_TEST_TABLE_NAME, CENTER_TEST_TABLE_NAME + "_id", null);
            CreateFkEdit fk2Edit = new CreateFkEdit(view.getSqlController(), fk2Spec, null);
            fk2Edit.redo();

            model.getClient().commit(null);
            FieldsEntity rightTableEntity = model.newGenericEntity();
            rightTableEntity.setTableName(RIGHT_TEST_TABLE_NAME);
            NewEntityEdit<FieldsEntity, DbSchemeModel> rightTableEntityEdit = new NewEntityEdit<>(model, rightTableEntity);
            rightTableEntityEdit.redo();
            /*
             Entity leftTableEntity = model.newGenericEntity();
             rightTableEntity.setTableName(LEFT_TEST_TABLE_NAME);
             NewEntityEdit<FieldsEntity, FieldsEntity, FieldsEntity, DbSchemeModel> leftTableEntityEdit = new NewEntityEdit<>(view.getEditor(), leftTableEntity);
             leftTableEntityEdit.redo();
             Entity centerTableEntity = model.newGenericEntity();
             centerTableEntity.setTableName(CENTER_TEST_TABLE_NAME);
             NewEntityEdit<FieldsEntity, FieldsEntity, FieldsEntity, DbSchemeModel> centerTableEntityEdit = new NewEntityEdit<>(view.getEditor(), centerTableEntity);
             centerTableEntityEdit.redo();
             */
            clearCache(LEFT_TEST_TABLE_NAME);
            clearCache(CENTER_TEST_TABLE_NAME);
            clearCache(RIGHT_TEST_TABLE_NAME);
            view.getDmView().addFkRelations(true, null);


            // perform the test
            view.getDmView().selectView(view.getDmView().getEntityView(rightTableEntity));
            DbSchemeModelView.DropTableAction action = (DbSchemeModelView.DropTableAction) view.getDmView().getActionMap().get(DbSchemeModelView.DropTableAction.class.getSimpleName());      
            DropFkRemoveTableAction removeAction = view.getDmView().new DropFkRemoveTableAction() {
                @Override
                protected int getDeleteTypeOption() { 
                    return JOptionPane.YES_OPTION;
                }
            };
            view.getDmView().getActionMap().put(ModelView.Delete.class.getSimpleName(), removeAction);
            action.editDbDiagram(rightTableEntity, view.getSqlController(), new ActionEvent(view, 0, null));
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            assertTrue(!isTablePresent(model.getClient(), RIGHT_TEST_TABLE_NAME));
            UndoManager schemeEditor = view.getUndo();
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 3);
            assertTrue(model.getRelations().size() == 2);
            assertTrue(isTablePresent(model.getClient(), RIGHT_TEST_TABLE_NAME));
            schemeEditor.redo();
            assertTrue(model.getEntities().size() == 2);
            assertTrue(model.getRelations().isEmpty());
            assertTrue(!isTablePresent(model.getClient(), RIGHT_TEST_TABLE_NAME));
            schemeEditor.undo();
            assertTrue(model.getEntities().size() == 3);
            assertTrue(model.getRelations().size() == 2);
            assertTrue(isTablePresent(model.getClient(), RIGHT_TEST_TABLE_NAME));
            // cleanup
            // remove entity
            schemeEditor.redo();
        } finally {
            // other cleanup
            removeLeftAndCenterTables();
        }
    }
}
