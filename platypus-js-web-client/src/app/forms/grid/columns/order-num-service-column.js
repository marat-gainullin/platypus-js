define([
    '../../../extend',
    '../../../ui',
    '../column'
], function (
        extend,
        Ui,
        Column) {
    function OrderNumServiceColumn(node) {
        Column.call(this, node);
        var self = this;

        this.width = 22;

        function getValue(dataRow) {
            return dataRow;
        }
        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            viewCell.innerText = (viewRowIndex + 1) + '';
            Ui.on(viewCell, Ui.Events.CLICK, function(event){
                self.grid.unselectAll(false);
                self.grid.select(dataRow, true);
                self.grid.focusCell(viewRowIndex, viewColumnIndex);
            });
        }
        Object.defineProperty(this, 'render', {
            get: function () {
                return render;
            }
        });
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
    extend(OrderNumServiceColumn, Column);
    return OrderNumServiceColumn;
});
