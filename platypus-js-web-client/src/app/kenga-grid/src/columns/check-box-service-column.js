define([
    'core/extend',
    'ui/utils',
    '../service-column'
], function (
        extend,
        Ui,
        ServiceColumn) {
    function CheckBoxServiceColumn(node) {
        ServiceColumn.call(this, node);
        var self = this;

        function getValue(dataRow) {
            return self.grid.isSelected(dataRow);
        }
        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            var checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = self.grid.isSelected(dataRow);
            Ui.on(checkbox, Ui.Events.CHANGE, function (event) {
                if (checkbox.checked) {
                    self.grid.select(dataRow);
                } else {
                    self.grid.unselect(dataRow);
                }
                self.grid.focus();
            });
            viewCell.appendChild(checkbox);
            viewCell.classList.add('p-grid-cell-check-box');
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
    extend(CheckBoxServiceColumn, ServiceColumn);
    return CheckBoxServiceColumn;
});
