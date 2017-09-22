define([
    '../../../extend',
    '../../../ui',
    '../column'
], function (
        extend,
        Ui,
        Column) {
    function MarkerServiceColumn(node) {
        Column.call(this, node);
        var self = this;

        this.width = 22;

        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            Ui.on(viewCell, Ui.Events.CLICK, function (event) {
                self.grid.setCursorOn(dataRow);
                self.grid.focusCell(viewRowIndex, viewColumnIndex);
            });
            if (self.grid.cursorProperty && self.grid.rows && self.grid.rows[self.grid.cursorProperty] === dataRow) {
                viewCell.classList.add('p-grid-cell-cursor');
            }
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
