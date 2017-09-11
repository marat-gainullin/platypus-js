deinfe([
    '../../../extend',
    '../column'
], function(
        extend,
        Column){
    function CheckBoxServiceColumn(){
        Column.call(this);
        var self = this;
   
        this.width = 22;
        
        function getValue(dataRow){
            return self.grid.isSelected(dataRow);
        }
        function render(viewIndex, dataRow, viewCell) {
            // TODO: Add grid.isSelected() driven rendering
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
