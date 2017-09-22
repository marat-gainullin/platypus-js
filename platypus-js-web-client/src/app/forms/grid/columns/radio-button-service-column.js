define([
    '../../../id',
    '../../../extend',
    '../../../ui',
    '../column'
], function (
        Id,
        extend,
        Ui,
        Column) {
    function RadioButtonServiceColumn(node) {
        Column.call(this, node);
        var self = this;
        var radioGroup = 'p-grid-group-' + Id.generate();

        this.width = 22;
        this.readonly = true;

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
    extend(RadioButtonServiceColumn, Column);
    return RadioButtonServiceColumn;
});
