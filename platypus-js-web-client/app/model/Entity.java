package com.eas.model;

/**
 *
 * @author mg
 */
public class Entity implements HasPublished {

    public Entity() {
        super();
    }

    public Entity(Model aModel) {
        this();
        model = aModel;
    }

    public Entity(String aQueryName) {
        this();
        queryName = aQueryName;
    }

    public void putOrmScalarDefinition(String aName, Fields.OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Fields.OrmDef> defs = getFields().getOrmScalarDefinitions();
            if (!defs.has(aName)) {
                getFields().putOrmScalarDefinition(aName, aDefinition);
            } else {
                Logger.fine(
                        "ORM property " + aName + " redefinition attempt on entity " + name != null && !name.isEmpty() ? name : "" + " " + title != null && !title.isEmpty() ? "[" + title + "]" : ""
                        + ".");
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmScalarDefinitions() {
        return getFields().getOrmScalarDefinitions();
    }

    public void putOrmCollectionDefinition(String aName, Fields.OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Fields.OrmDef> defs = getFields().getOrmCollectionsDefinitions();
            if (!defs.has(aName)) {
                getFields().putOrmCollectionDefinition(aName, aDefinition);
            } else {
                Logger.fine(
                        "ORM property " + aName + " redefinition attempt on entity " + name != null && !name.isEmpty() ? name : "" + " " + title != null && !title.isEmpty() ? "[" + title + "]" : ""
                        + ".");
            }
        }
    }

    public Map<String, Fields.OrmDef> getOrmCollectionsDefinitions() {
        return getFields().getOrmCollectionsDefinitions();
    }

    public Fields getFields() {
        if (query != null) {
            return query.getFields();
        } else {
            return null;
        }
    }

    public boolean removeOutRelation(Relation aRelation) {
        return outRelations.remove(aRelation);
    }

    public boolean removeInRelation(Relation aRelation) {
        return inRelations.remove(aRelation);
    }

    public boolean addOutRelation(Relation aRelation) {
        if (!(aRelation instanceof ReferenceRelation)) {
            return outRelations.add(aRelation);
        } else {
            return false;
        }
    }

    public boolean addInRelation(Relation aRelation) {
        if (!(aRelation instanceof ReferenceRelation)) {
            return inRelations.add(aRelation);
        } else {
            return false;
        }
    }

    public Set<Relation> getInRelations() {
        return inRelations;
    }

    public Set<Relation> getOutRelations() {
        return outRelations;
    }

    public Set<Relation> getInOutRelations() {
        Set<Relation> lInOutRelations = new HashSet<Relation>();
        lInOutRelations.addAll(inRelations);
        lInOutRelations.addAll(outRelations);
        return lInOutRelations;
    }

    public void takeSnapshot() {
        if (snapshotProducer != null) {
            lastSnapshot = (JavaScriptObject) snapshotProducer.<Utils.JsObject>cast().call(null, null);
        }
    }

    public void applyLastSnapshot() {
        applySnapshot(lastSnapshot);
    }

    public void applySnapshot(JavaScriptObject aValue) {
        lastSnapshot = aValue;
        // Apply aValue as a snapshot. Be aware of change log!
        if (snapshotConsumer != null) {// snapshotConsumer is null in designer
            snapshotConsumer.<Utils.JsObject>cast().call(null, aValue, true);
        }
    }

    protected void refreshRowset(final Callback<JavaScriptObject, String> aCallback) throws Exception {
        final CancellableContainer f = new CancellableContainer();
        f.future = query.execute(new CallbackAdapter<JavaScriptObject, String>() {

            @Override
            public void doWork(JavaScriptObject aResult) throws Exception {
                if (pending == f.future) {
                    // Apply aResult as a snapshot. Be aware of change log!
                    applySnapshot(aResult);
                    valid = true;
                    pending = null;
                    model.terminateProcess(Entity.this, null);
                    if (onRequeried != null) {
                        try {
                            Utils.JsObject event = JavaScriptObject.createObject().cast();
                            event.setJs("source", jsPublished);
                            onRequeried.<Utils.JsObject>cast().call(jsPublished, event);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }
                    if (aCallback != null) {
                        aCallback.onSuccess(jsPublished);
                    }
                }
            }

            @Override
            public void onFailure(String aMessage) {
                if (pending == f.future) {
                    valid = true;
                    pending = null;
                    model.terminateProcess(Entity.this, aMessage);
                    if (aCallback != null) {
                        aCallback.onFailure(aMessage);
                    }
                }
            }

        });
        pending = f.future;
    }

    public boolean isPending() {
        return pending != null;
    }

    public void requery(final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
        requery(new CallbackAdapter<JavaScriptObject, String>() {
            @Override
            protected void doWork(JavaScriptObject result) throws Exception {
                if (aOnSuccess != null) {
                    aOnSuccess.<Utils.JsObject>cast().apply(jsPublished, JavaScriptObject.createArray());
                }
            }

            @Override
            public void onFailure(String reason) {
                if (aOnFailure != null) {
                    aOnFailure.<Utils.JsObject>cast().call(jsPublished, reason);
                }
            }

        });
    }

    public void query(JavaScriptObject aParams, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
        Query copied = query.copy();
        if (aParams != null) {
            Utils.JsObject params = aParams.cast();
            JsArrayString keys = params.keys();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.get(i);
                Object pValue = params.getJava(key);
                Parameter p = copied.getParameters().get(key);
                if (p != null) {
                    p.setValue(Utils.toJava(pValue));
                }
            }
        }
        copied.execute(new CallbackAdapter<JavaScriptObject, String>() {
            @Override
            protected void doWork(JavaScriptObject result) throws Exception {
                if (aOnSuccess != null) {
                    aOnSuccess.<Utils.JsObject>cast().call(jsPublished, result);
                }
            }

            @Override
            public void onFailure(String reason) {
                if (aOnFailure != null) {
                    aOnFailure.<Utils.JsObject>cast().call(jsPublished, reason);
                }
            }

        });
    }

    public void requery(final Callback<JavaScriptObject, String> aCallback) throws Exception {
        if (model != null) {
            invalidate();
            execute(aCallback);
        }
    }

    public void append(JavaScriptObject aData) throws Exception {
        if (snapshotConsumer != null) {
            snapshotConsumer.<Utils.JsObject>cast().call(null, aData, false);
        }
    }

    public void enqueueUpdate(JavaScriptObject aParams) throws Exception {
        Utils.JsObject changeLog = model.getChangeLog().<Utils.JsObject>cast();
        Query copied = query.copy();
        if (aParams != null) {
            Utils.JsObject params = aParams.cast();
            JsArrayString keys = params.keys();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.get(i);
                Object pValue = params.getJava(key);
                Parameter p = copied.getParameters().get(key);
                if (p != null) {
                    p.setValue(Utils.toJava(pValue));
                }
            }
        }
        changeLog.setSlot(changeLog.length(), copied.prepareCommand());
    }

    /**
     * Legacy method.
     *
     * @param onSuccess
     * @param onFailure
     * @throws Exception
     */
    public void executeUpdate(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
        Utils.JsObject changeLog = JavaScriptObject.createArray(1).cast();
        changeLog.setSlot(0, query.prepareCommand());
        model.client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

            @Override
            protected void doWork(Void aVoid) throws Exception {
                if (onSuccess != null) {
                    Utils.invokeJsFunction(onSuccess);
                }
            }

            @Override
            public void onFailure(String aReason) {
                try {
                    if (onFailure != null) {
                        Utils.executeScriptEventVoid(jsPublished, onFailure, aReason);
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

        });
    }

    /**
     * Executes a dml query of the entity. Uses first argument JavaScript object
     * as parameters.
     *
     * @param aOnSuccess
     * @param aOnFailure
     * @throws Exception
     */
    public void update(final JavaScriptObject aParams, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) throws Exception {
        Query copied = query.copy();
        if (aParams != null) {
            Utils.JsObject params = aParams.cast();
            JsArrayString keys = params.keys();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.get(i);
                Object pValue = params.getJava(key);
                Parameter p = copied.getParameters().get(key);
                if (p != null) {
                    p.setValue(Utils.toJava(pValue));
                }
            }
        }
        Utils.JsObject changeLog = JavaScriptObject.createArray(1).cast();
        changeLog.setSlot(0, copied.prepareCommand());
        model.client.requestCommit(changeLog, new CallbackAdapter<Void, String>() {

            @Override
            protected void doWork(Void aVoid) throws Exception {
                if (aOnSuccess != null) {
                    Utils.invokeJsFunction(aOnSuccess);
                }
            }

            @Override
            public void onFailure(String aReason) {
                try {
                    if (aOnFailure != null) {
                        Utils.executeScriptEventVoid(jsPublished, aOnFailure, aReason);
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

        });
    }

    public void execute(final JavaScriptObject onSuccess, final JavaScriptObject onFailure) throws Exception {
        execute(new CallbackAdapter<JavaScriptObject, String>() {

            @Override
            protected void doWork(JavaScriptObject aResult) throws Exception {
                if (onSuccess != null) {
                    Utils.invokeJsFunction(onSuccess);
                }
            }

            @Override
            public void onFailure(String reason) {
                if (onFailure != null) {
                    try {
                        Utils.executeScriptEventVoid(jsPublished, onFailure, reason);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
    }

    public void execute(final Callback<JavaScriptObject, String> aCallback) throws Exception {
        if (model != null) {
            if (getOutRelations().isEmpty()) {
                internalExecute(aCallback);
            } else {
                model.inNewProcess(new Callable() {
                    @Override
                    public void call() throws Exception {
                        internalExecute(null);
                    }
                }, new Callback<JavaScriptObject, String>() {
                    @Override
                    public void onSuccess(JavaScriptObject result) {
                        if (aCallback != null) {
                            aCallback.onSuccess(jsPublished);
                        }
                    }

                    @Override
                    public void onFailure(String reason) {
                        if (aCallback != null) {
                            aCallback.onFailure(reason);
                        }
                    }
                });
            }
        }
    }

    protected void internalExecute(final Callback<JavaScriptObject, String> aCallback) throws Exception {
        assert query != null : QUERY_REQUIRED;
        bindQueryParameters();
        if (isValid()) {
            if (aCallback != null) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                    @Override
                    public void execute() {
                        aCallback.onSuccess(null);
                    }
                });
            }
        } else {
            // Requery if query parameters values have been changed while
            // bindQueryParameters() call
            // or we are forced to refresh the data via requery() call.
            silentUnpend();
            refreshRowset(aCallback);
            assert pending != null || (aCallback == null && model.process == null);
            // filtering will be done while processing onRequeried event in
            // ApplicationEntity code
        }
    }

    public void unpend() {
        if (pending != null) {
            pending.cancel();
            pending = null;
        }
    }

    protected void silentUnpend() {
        Model.RequeryProcess lprocess = model.process;
        model.process = null;
        try {
            unpend();
        } finally {
            model.process = lprocess;
        }
    }

    protected void internalExecuteChildren(boolean refresh) throws Exception {
        Set<Relation> rels = getOutRelations();
        if (rels != null) {
            Set<Entity> toExecute = new Set();
            for (Relation outRel : rels) {
                if (outRel != null) {
                    Entity rEntity = outRel.getRightEntity();
                    if (rEntity != null) {
                        toExecute.add(rEntity);
                    }
                }
            }
            model.executeEntities(refresh, toExecute);
        }
    }

    protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
        Set<Relation> rels = getOutRelations();
        if (rels != null) {
            Field onlyField = getFields().get(aOnlyFieldIndex);
            Set<Entity> toExecute = new Set();
            for (Relation outRel : rels) {
                if (outRel != null) {
                    Entity rEntity = outRel.getRightEntity();
                    if (rEntity != null && outRel.getLeftField() == onlyField) {
                        toExecute.add(rEntity);
                    }
                }
            }
            model.executeEntities(refresh, toExecute);
        }
    }

    public void bindQueryParameters() throws Exception {
        Query selfQuery = getQuery();
        Parameters selfParameters = selfQuery.getParameters();
        boolean parametersModified = false;
        Set<Relation> inRels = getInRelations();
        if (inRels != null && !inRels.isEmpty()) {
            for (Relation relation : inRels) {
                if (relation != null && relation.isRightParameter()) {
                    Entity leftEntity = relation.getLeftEntity();
                    if (leftEntity != null) {
                        Object pValue = null;
                        if (relation.isLeftField()) {
                            // There might be entities - parameters values
                            // sources, with no
                            // data in theirs rowsets, so we can't bind query
                            // parameters to proper values. In the
                            // such case we initialize parameters values with
                            // null
                            JavaScriptObject leftRowset = leftEntity.getPublished();
                            if (leftRowset != null && leftRowset.<JsObject>cast().getJs("cursor") != null) {
                                JavaScriptObject jsCursor = leftRowset.<JsObject>cast().getJs("cursor");
                                pValue = jsCursor.<JsObject>cast().getJava(relation.getLeftField().getName());
                            } else {
                                pValue = null;
                            }
                        } else {
                            Parameter leftParameter = relation.getLeftParameter();
                            if (leftParameter != null) {
                                pValue = leftParameter.getValue();
                                if (pValue == null) {
                                    pValue = leftParameter.getDefaultValue();
                                }
                            } else {
                                Logger.severe("Parameter of left query must present (Relation points to query parameter in entity: " + getTitle() + " [" + getEntityId()
                                        + "], but query parameter is absent)");
                            }
                        }
                        Parameter selfPm = relation.getRightParameter();
                        if (selfPm != null) {
                            Object selfValue = selfPm.getValue();
                            if ((selfValue == null && pValue != null) || (selfValue != null && !selfValue.equals(pValue))) {
                                selfPm.setValue(pValue);
                            }
                        }
                    } else {
                        Logger.severe("Relation has no left entity");
                    }
                }
            }
        }
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter param = selfParameters.get(i);
            if (param.isModified()) {
                parametersModified = true;
                param.setModified(false);
            }
        }
        if (parametersModified) {
            invalidate();
        }
    }

    protected void resignOnCursor() {
        if (cursorListener != null) {
            cursorListener.removeHandler();
            cursorListener = null;
        }
        JavaScriptObject jsCursor = jsPublished.<JsObject>cast().getJs("cursor");
        if (jsCursor != null) {
            cursorListener = Utils.listenPath(jsCursor, "", new Utils.OnChangeHandler() {

                @Override
                public void onChange(JavaScriptObject anEvent) {
                    try {
                        internalExecuteChildren(false);
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            });
        }
    }

    public void setPublished(JavaScriptObject aPublished) {
        if (jsPublished != aPublished) {
            jsPublished = aPublished;
            if (jsPublished != null) {
                publishFacade(this, jsPublished);
                Utils.listenPath(jsPublished, "cursor", new Utils.OnChangeHandler() {

                    @Override
                    public void onChange(JavaScriptObject anEvent) {
                        try {
                            resignOnCursor();
                            internalExecuteChildren(false);
                        } catch (Exception ex) {
                            Logger.severe(ex);
                        }
                    }
                });
            }
        }
    }
}
