var modelMin = {
    tags: {
        e: "entity",// if edited, LocalModulesPRoxy needs to be checked for consistence
        fe: "fieldsEntity",
        pe: "parametersEntity",
        ps: "parameters",
        p: "parameter",
        pr: "primaryKey",
        r: "relation",
        rr: "referenceRelation"
    }, attributes: {
        ei: "entityId",
        qi: "queryId",// if edited, LocalModulesProxy needs to be checked for consistence
        tbn: "tableDbId",
        tsn: "tableSchemaName",
        tn: "tableName",
        lei: "leftEntityId",
        lef: "leftEntityFieldName",
        lep: "leftEntityParameterName",
        rei: "rightEntityId",
        ref: "rightEntityFieldName",
        rep: "rightEntityParameterName",
        n: "name", // "Name" as well
        s: "schema",
        tl: "table",
        f: "field",
        ta: "tableAlias",
        d: "description",
        t: "type",
        tt: "Title",
        nl: "nullable",
        p: "isPk",
        pm: "parameterMode",
        sf: "selectionForm",
        dv: "defaultValue",
        ch: "classHint",
        spn: "scalarPropertyName",
        cpn: "collectionPropertyName",
        ds: "datasource",
        ddi: "datamodelDbId",
        dsn: "datamodelSchemaName"
    },
    removedAttributes: [
        "entityLocationX",
        "entityLocationY",
        "entityWidth",
        "entityHeight",
        "entityIconified"
    ]
};
print('model tags:');
for (var t in modelMin.tags) {
    print('put("' + modelMin.tags[t] + '","' + t + '");');
}
print('model attributes:');
for (var a in modelMin.attributes) {
    print('put("' + modelMin.attributes[a] + '","' + a + '");');
}
print('put("Name", "n");');
for (var r in modelMin.removedAttributes) {
    print('put("' + modelMin.removedAttributes[r] + '", null);');
}
