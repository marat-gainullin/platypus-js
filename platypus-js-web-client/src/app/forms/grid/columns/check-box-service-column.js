define([
    '../../../extend',
    '../../../ui',
    '../column'
], function (
        extend,
        Ui,
        Column) {
    function CheckBoxServiceColumn(node) {
        Column.call(this, node);
        var self = this;

        this.width = 22;

        function getValue(dataRow) {
            return self.grid.isSelected(dataRow);
        }
        function render(viewRowIndex, viewColumnIndex, dataRow, viewCell) {
            var checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = self.grid.isSelected(dataRow);
            Ui.on(checkbox, Ui.Events.CHANGE, function (event){
                if (checkbox.checked) {
                    self.grid.select(dataRow);
                } else {
                    self.grid.unselect(dataRow);
                }
                self.grid.focus();
            });
            viewCell.appendChild(checkbox);
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
    extend(CheckBoxServiceColumn, Column);
    return CheckBoxServiceColumn;
});
