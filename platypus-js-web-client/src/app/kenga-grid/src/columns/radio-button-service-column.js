define([
    'core/id',
    'core/extend',
    '../service-column'
], function (
        Id,
        extend,
        ServiceColumn) {
    function RadioButtonServiceColumn(node) {
        ServiceColumn.call(this, node);
        var self = this;
        var radioGroup = 'p-grid-group-' + Id.generate();

        function getValue(dataRow) {
            return self.grid.isSelected(dataRow);
        }
        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            var radio = document.createElement('input');
            radio.type = 'radio';
            radio.name = radioGroup;
            radio.checked = self.grid.isSelected(dataRow);
            radio.onchange = function (event) {
                if (radio.checked) {
                    self.grid.unselectAll();
                    self.grid.select(dataRow);
                }
                self.grid.focus();
            };
            viewCell.appendChild(radio);
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
    extend(RadioButtonServiceColumn, ServiceColumn);
    return RadioButtonServiceColumn;
});
