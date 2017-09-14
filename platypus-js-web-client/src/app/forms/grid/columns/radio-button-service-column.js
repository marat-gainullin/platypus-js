define([
    '../../../id',
    '../../../extend',
    '../column'
], function(
        Id,
        extend,
        Column){
    function RadioServiceColumn(){
        Column.call(this);
        var self = this;
        var radioGroup = 'p-grid-group-' + Id.generate();
   
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
    extend(RadioServiceColumn, Column);
    return RadioServiceColumn;
});
