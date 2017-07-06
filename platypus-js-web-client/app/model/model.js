define(['logger'], function (Logger) {
    var relations = new Set();
    var referenceRelations = new Set();
    var entities = new Map();
    var changeLog = [];
    var process;

    function RequeryProcess(onSuccess, onFailure) {
        var errors = new Map();

        if (!onSuccess)
            "onSuccess callback is required.";

        this.cancel = function () {
            onFailure("Cancelled");
        };

        this.success = function () {
            onSuccess(null);
        };

        this.failure = function () {
            onFailure(errors);
        };

        this.end = function () {
            if (errors.size === 0) {
                success();
            } else {
                failure();
            }
        };
    }

    function Relation(leftEntity,
            leftItem,
            leftParameter, // Flag. It is true if leftItem is a parameter.
            rightEntity,
            rightItem,
            rightParameter // Flag. It is true if rightItem is a parameter.
            ) {
        this.leftEntity = leftEntity;
        this.leftItem = leftItem;
        this.leftParameter = leftParameter;
        this.rightEntity = rightEntity;
        this.rightItem = rightItem;
        this.rightParameter = rightParameter;
    }

    function ReferenceRelation(
            leftEntity,
            leftField,
            rightEntity,
            rightField,
            scalarPropertyName,
            collectionPropertyName
            ) {
        this.leftEntity = leftEntity;
        this.leftField = leftField;
        this.rightEntity = rightEntity;
        this.rightField = rightField;
        this.scalarPropertyName = scalarPropertyName;
        this.collectionPropertyName = collectionPropertyName;
    }

    function Entity(){
        
    }

});