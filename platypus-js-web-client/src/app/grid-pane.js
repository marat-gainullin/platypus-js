define([
    '../extend',
    './container'], function (
        extend,
        Container) {
    /**
     * A container with Grid Layout.
     * @param rows the number of grid rows.
     * @param cols the number of grid columns.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor GridPane GridPane
     */
    function GridPane(rows, cols, hgap, vgap) {
        Container.call(this);
        var self = this;
        
        if(arguments.length < 1)
            rows = 1;
        if(arguments.length < 2)
            cols = 1;
        if(arguments.length < 3)
            hgap = 0;
        if(arguments.length < 3)
            vgap = 0;
        
        grid = new Widget[rows][0];
        for (int r = 0; r < rows; r++) {
            grid[r] = new Widget[columns];
        }
        formatCells();

    public int getRowCount() {
        return rows;
    }

    public int getColumnCount() {
        return columns;
    }

    public int getHgap() {
        return hgap;
    }

    public final void setHgap(int aValue) {
        hgap = aValue;
        formatCells();
    }

    public int getVgap() {
        return vgap;
    }

    public final void setVgap(int aValue) {
        vgap = aValue;
        formatCells();
    }

    protected void formatCells() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                formatCell(i, j);
            }
        }
    }

    protected void formatCell(int row, int column) {
        Widget w = getWidget(row, column);
        if (w != null) {
            Style ws = w.element.style;

            ws.position = 'absolute';
            ws.setLeft((100f / columns) * column + '%');
            ws.top =(100f / rows) * row + '%');
            ws.width =100f / columns + '%');
            ws.height =100f / rows + '%');
            ws.setMargin(0+ 'px');
            ws.setPaddingLeft(Math.floor(hgap / 2f)+ 'px');
            ws.setPaddingRight(Math.ceil(hgap / 2f)+ 'px');
            ws.setPaddingTop(Math.floor(vgap / 2f)+ 'px');
            ws.setPaddingBottom(Math.ceil(vgap / 2f)+ 'px');
        }
    }

    @Override
    public void add(Widget w) {
        w.element.classList.add(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
        addToFreeCell(w);
    }

    @Override
    public void add(Widget w, int beforeIndex) {
        add(w);
    }

    public boolean addToFreeCell(Widget aWidget) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Widget w = getWidget(row, col);
                if (w == null) {
                    setWidget(row, col, aWidget);
                    return true;
                }
            }
        }
        return false;
    }

    public void setWidget(int row, int column, Widget w) {
        if (row >= 0 && row < grid.length
                && column >= 0 && column < grid[row].length) {
            Widget old = grid[row][column];
            if (old != null) {
                remove(old);
            }
            grid[row][column] = w;
            formatCell(row, column);
            super.add(w);
        }
    }

    public Widget getWidget(int row, int column) {
        if (row >= 0 && row < grid.length
                && column >= 0 && column < grid[row].length) {
            return grid[row][column];
        } else {
            return null;
        }
    }

    @Override
    public boolean remove(Widget w) {
        checkCells(w);
        return super.remove(w);
    }

    private void checkCells(Widget w) {
        if (w == null) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == w) {
                        grid[i][j] = null;
                    }
                }
            }
        }
    }

    @Override
    public Widget remove(int index) {
        Widget w = super.remove(index);
        checkCells(w);
        return w;
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        published.add = function(toAdd, aRow, aCol){
            if(toAdd && toAdd.unwrap){
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                if(arguments.length < 3)
                    throw 'aRow and aCol are required parameters';				
                aWidget.@com.eas.widgets.GridPane::setWidget(IILcom/google/gwt/user/client/ui/Widget;)(1 * aRow, 1 * aCol, toAdd.unwrap());
            }
        }
        published.remove = function(aChild) {
            if (aChild && aChild.unwrap) {
                aWidget.@com.eas.widgets.GridPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
            }
        };
        published.child = function(aRow, aCol) {
            if (arguments.length > 1) {
                var widget = aWidget.@com.eas.widgets.GridPane::getWidget(II)(1 * aRow, 1 * aCol);
                return !!widget ? @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget) : null;
            }else
                return null;
        };
        Object.defineProperty(published, "hgap", {
            get : function(){
                return aWidget.@com.eas.widgets.GridPane::getHgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.GridPane::setHgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "vgap", {
            get : function(){
                return aWidget.@com.eas.widgets.GridPane::getVgap()();
            },
            set : function(aValue){
                aWidget.@com.eas.widgets.GridPane::setVgap(I)(aValue);
            }
        });
        Object.defineProperty(published, "rows", {
            get : function(){
                return aWidget.@com.eas.widgets.GridPane::getRowCount()();
            }
        });
        Object.defineProperty(published, "columns", {
            get : function(){
                return aWidget.@com.eas.widgets.GridPane::getColumnCount()();
            }
        });
        Object.defineProperty(published, "children", {
            value: function(){
                var ch = [];
                for(var r = 0; r < published.rows; r++){
                    for(var c = 0; c < published.columns; c++){
                        var index = published.columns * r + c;
                        var comp = published.child(r, c);
                        if(comp != null){
                            ch.push(comp);
                        }
                    }
                }
                return ch;
            }
        });
        Object.defineProperty(published, "count", {
            get : function(){
                var ch = published.children;
                return ch.length;
            }
        });
    }-*/;

    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.parent == this : "widget should be a child of this container";
        return aWidget.element.getParentElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.parent == this : "widget should be a child of this container";
        return aWidget.element.getParentElement().getOffsetLeft();
    }
        
    }
    extend(GridPane, Container);
    return GridPane;
});
