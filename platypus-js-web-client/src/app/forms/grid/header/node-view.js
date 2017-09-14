define([
    '../../../ui',
    '../columns/drag'
], function (
        Ui,
        ColumnDrag
        ) {

    var HEADER_VIEW = 'header-view';

    function NodeView(aTitle, columnNode) {
        var self = this;
        var th = document.createElement('th');
        var thResizer = document.createElement('div');
        thResizer.className = 'p-grid-column-resizer';
        var background = null;
        var foreground = null;
        var font = null;
        var moveable = true;
        var resizable = true;
        th.draggable = true;

        th[HEADER_VIEW] = this;
        th.innerText = aTitle;
        th.appendChild(thResizer);
        Ui.on(th, Ui.Events.DRAGSTART, function (event) {
            event.stopPropagation();
            if (moveable) {
                event.dataTransfer.effectAllowed = 'move';
                ColumnDrag.instance = new ColumnDrag(self, event.target, 'move');
                event.dataTransfer.setData('Text', 'column-moved');
            } else {
                event.dataTransfer.effectAllowed = 'none';
                ColumnDrag.instance = null;
            }
        });
        Ui.on(thResizer, Ui.Events.DRAGSTART, function (event) {
            event.stopPropagation();
            if (resizable) {
                event.dataTransfer.effectAllowed = 'move';
                ColumnDrag.instance = new ColumnDrag(self, event.target, 'resize');
                event.dataTransfer.setData('Text', 'column-resized');
            } else {
                event.dataTransfer.effectAllowed = 'none';
                ColumnDrag.instance = null;
            }
        });

        Object.defineProperty(this, 'element', {
            get: function () {
                return th;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return th.innerText;
            },
            set: function (aValue) {
                th.innerText = aValue;
                th.appendChild(thResizer);
            }
        });

        Object.defineProperty(this, 'column', {
            get: function () {
                return getRightMostColumn();
            }
        });

        Object.defineProperty(this, 'headerNode', {
            get: function () {
                return columnNode;
            }
        });

        Object.defineProperty(this, 'background', {
            get: function () {
                return background;
            },
            set: function (aValue) {
                background = aValue;
            }
        });

        Object.defineProperty(this, 'foreground', {
            get: function () {
                return foreground;
            },
            set: function (aValue) {
                foreground = aValue;
            }
        });

        Object.defineProperty(this, 'font', {
            get: function () {
                return font;
            },
            set: function (aValue) {
                font = aValue;
            }
        });

        Object.defineProperty(this, 'resizable', {
            get: function () {
                return resizable;
            },
            set: function (aValue) {
                if (resizable !== aValue) {
                    resizable = aValue;
                    th.draggable = moveable || resizable;
                }
            }
        });

        Object.defineProperty(this, 'moveable', {
            get: function () {
                return moveable;
            },
            set: function (aValue) {
                if (moveable !== aValue) {
                    moveable = aValue;
                    th.draggable = moveable || resizable;
                }
            }
        });

        function getRightMostColumn() {
            var node = columnNode;
            while (!node.column && !node.children.length === 0) {
                node = node.children[node.children.length - 1];
            }
            return node.column;
        }

        /*
         public interface GridResources extends ClientBundle {
         
         static final GridResources instance = GWT.create(GridResources.class);
         
         public GridStyles header();
         
         }
         
         public interface GridStyles extends CssResource {
         
         public String gridHeaderMover();
         
         public String gridHeaderResizer();
         
         }
         
         public static GridStyles headerStyles = GridResources.instance.header();
         
         private static class HeaderCell extends AbstractCell<String> {
         
         public HeaderCell() {
         super(BrowserEvents.DRAGSTART);
         }
         
         @Override
         public void render(Context context, String value, SafeHtmlBuilder sb) {
         headerStyles.ensureInjected();// ondragenter=\"event.preventDefault();\"
         // ondragover=\"event.preventDefault();\"
         // ondrop=\"event.preventDefault();\"
         sb.append(SafeHtmlUtils.fromTrustedString("<div class=\"grid-column-header-content\"; style=\"position:relative;\">"))
         .append(value.startsWith("<html>") ? SafeHtmlUtils.fromTrustedString(value.substring(6)) : SafeHtmlUtils.fromString(value)).append(SafeHtmlUtils.fromTrustedString("</div>"))
         .append(SafeHtmlUtils.fromTrustedString("<span draggable=\"true\" class=\"" + headerStyles.gridHeaderMover() + " grid-header-mover\"></span>"))
         .append(SafeHtmlUtils.fromTrustedString("<span draggable=\"true\" class=\"" + headerStyles.gridHeaderResizer() + " grid-header-resizer\"></span>"));
         }
         
         }
         */
    }
    Object.defineProperty(NodeView, 'HEADER_VIEW', {
        get: function () {
            return HEADER_VIEW;
        }
    });
    return NodeView;
});