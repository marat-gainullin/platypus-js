package com.eas.grid;

import com.eas.core.XElement;
import com.eas.grid.columns.Column;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.eas.ui.XDataTransfer;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.user.client.ui.HasText;

public class HeaderView implements HasColumn, HasText {

    public static final String HEADER_VIEW = "header-view";
    
    protected TableCellElement element = Document.get().createTHElement();
    protected PublishedColor background;
    protected PublishedColor foreground;
    protected PublishedFont font;
    protected HeaderNode headerNode;
    protected boolean moveable = true;
    protected boolean resizable = true;

    public HeaderView(String aTitle, HeaderNode aHeaderNode) {
        super();
        element.setPropertyObject(HEADER_VIEW, this);
        element.setInnerText(aTitle);
        headerNode = aHeaderNode;
        headerNode.setHeader(this);
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGSTART, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                event.stopPropagation();
                EventTarget et = event.getEventTarget();
                if (Element.is(et)) {
                    ColumnDrag col = new ColumnDrag(HeaderView.this, Element.as(et));
                    if ((col.isMove() && moveable) || (col.isResize() && resizable)) {
                        event.getDataTransfer().<XDataTransfer>cast().setEffectAllowed("move");
                        ColumnDrag.instance = col;
                        event.getDataTransfer().setData("Text", "column-" + (ColumnDrag.instance.isMove() ? "moved" : "resized"));
                    } else {
                        event.getDataTransfer().<XDataTransfer>cast().setEffectAllowed("none");
                    }
                }
            }

        });
    }

    public Element getElement(){
        return element;
    }   
    
    @Override
    public String getText() {
        return element.getInnerText();
    }

    @Override
    public void setText(String text) {
        element.setInnerText(text);
    }
    
    @Override
    public Column getColumn() {
        return getRightMostColumn();
    }

    public HeaderNode getHeaderNode() {
        return headerNode;
    }

    public PublishedColor getBackground() {
        return background;
    }

    public void setBackground(PublishedColor aValue) {
        background = aValue;
    }

    public PublishedColor getForeground() {
        return foreground;
    }

    public void setForeground(PublishedColor aValue) {
        foreground = aValue;
    }

    public PublishedFont getFont() {
        return font;
    }

    public void setFont(PublishedFont aValue) {
        font = aValue;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean aValue) {
        resizable = aValue;
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean aValue) {
        moveable = aValue;
    }

    protected Column getRightMostColumn(){
        HeaderNode node = headerNode;
        while(node.getColumn() == null && !node.getChildren().isEmpty()){
            node = node.getChildren().get(node.getChildren().size() - 1);
        }
        return node.getColumn();
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
