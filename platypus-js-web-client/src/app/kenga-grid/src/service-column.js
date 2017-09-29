define([
    'core/extend',
    '../column'
], function (
        extend,
        Column) {
    function ServiceColumn(node) {
        Column.call(this, node);
        var self = this;

        this.width = 22;
        this.readonly = true;

        Object.defineProperty(this, 'minWidth', {
            get: function () {
                return self.width;
            }
        });

        Object.defineProperty(this, 'maxWidth', {
            get: function () {
                return self.width;
            }
        });
    }
    extend(ServiceColumn, Column);
    return ServiceColumn;
});
