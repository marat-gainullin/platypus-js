define([
    '../../../ui',
    '../columns/drag'
], function (
        Ui,
        ColumnDrag
        ) {

    var HEADER_VIEW = 'header-view';

    function HeaderView(aTitle, aHeaderNode) {

        var self = this;
        var th = document.createElement('th');
        var background = null;
        var foreground = null;
        var font = null;
        var headerNode = null;
        var moveable = true;
        var resizable = true;

        th[HEADER_VIEW] = this;
        th.innerText = aTitle;
        headerNode = aHeaderNode;
        headerNode.header = this;
        Ui.on(th, Ui.Events.DRAGSTART, function (event) {
            event.stopPropagation();
            var col = new ColumnDrag(self, event.target);
            if ((col.move && moveable) || (col.resize && resizable)) {
                event.dataTransfer.effectAllowed = 'move';
                ColumnDrag.instance = col;
                event.dataTransfer.setData('Text', 'column-' + (ColumnDrag.instance.move ? 'moved' : 'resized'));
            } else {
                event.dataTransfer.effectAllowed = 'none';
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
            }
        });

        Object.defineProperty(this, 'column', {
            get: function () {
                return getRightMostColumn();
            }
        });

        Object.defineProperty(this, 'headerNode', {
            get: function () {
                return headerNode;
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
                resizable = aValue;
            }
        });

        Object.defineProperty(this, 'moveable', {
            get: function () {
                return moveable;
            },
            set: function (aValue) {
                moveable = aValue;
            }
        });

        function getRightMostColumn() {
            var node = headerNode;
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
    Object.defineProperty(HeaderView, 'HEADER_VIEW', {
        get: function () {
            return HEADER_VIEW;
        }
    });
});