define([
    '../../../extend',
    '../column'
], function (
        extend,
        Column) {
    function MarkerServiceColumn() {
        Column.call(this);
        var self = this;

        this.width = 22;

        function render(viewIndex, dataRow, viewCell) {
            // TODO: Add data cursor and data changes driven data rendering
            var content = document.createElement('div');
            if (self.grid.cursorProperty && self.grid.rows && self.grid.rows[self.grid.cursorProperty] === dataRow) {
                content.className = 'p-grid-marker-cell-cursor';
            }
            viewCell.appendChild(content);
            /*
             if (value.inserted)
             content.className = 'grid-marker-inserted';
             else if (value.updated)
             content.className = 'grid-marker-cell-dirty';
             */
        }
        Object.defineProperty(this, 'render', {
            get: function () {
                return render;
            }
        });
        function getValue(dataRow) {
            return dataRow;
        }
        Object.defineProperty(this, 'getValue', {
            get: function () {
                return getValue;
            }
        });
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
    extend(MarkerServiceColumn, Column);
    return MarkerServiceColumn;
});
