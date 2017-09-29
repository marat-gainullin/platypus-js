define([
    'core/extend',
    'ui/utils',
    '../service-column'
], function (
        extend,
        Ui,
        ServiceColumn) {
    function OrderNumServiceColumn(node) {
        ServiceColumn.call(this, node);
        var self = this;

        function getValue(dataRow) {
            return dataRow;
        }
        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            viewCell.innerText = (viewRowIndex + 1) + '';
            Ui.on(viewCell, Ui.Events.CLICK, function (event) {
                self.grid.unselectAll(false);
                self.grid.select(dataRow, true);
                self.grid.focusCell(viewRowIndex, viewColumnIndex);
            });
            viewCell.classList.add('p-grid-cell-service');
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
    }
    extend(OrderNumServiceColumn, ServiceColumn);
    return OrderNumServiceColumn;
});
