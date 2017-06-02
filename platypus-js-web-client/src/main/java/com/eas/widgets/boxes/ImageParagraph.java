package com.eas.widgets.boxes;

import com.eas.ui.HasImageParagraph;
import com.eas.ui.Widget;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;

public abstract class ImageParagraph extends Widget implements HasText, HasHTML, HasImageParagraph {

    // forms api
    protected String text;
    protected boolean html;
    protected int horizontalTextPosition = RIGHT;
    protected int verticalTextPosition = CENTER;
    protected int iconTextGap = 4;
    protected int horizontalAlignment = LEFT;
    protected int verticalAlignment = CENTER;
    protected String image;
    protected Element content;
    protected Element aligner;

    protected ImageParagraph(Element aContainer, String aTitle, boolean asHtml) {
        this(aContainer, aTitle, asHtml, null);
    }

    protected ImageParagraph(Element aContainer, String aTitle, boolean asHtml, String aImage) {
        super(aContainer);
        text = aTitle;
        html = asHtml;
        image = aImage;
        element.getStyle().setPosition(Style.Position.RELATIVE);
        element.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        element.getStyle().setPadding(0, Style.Unit.PX);
        //
        content = Document.get().createPElement();
        content.getStyle().setMargin(0, Style.Unit.PX);
        content.getStyle().setPosition(Style.Position.RELATIVE);
        content.getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        aligner = Document.get().createDivElement();
        aligner.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        aligner.getStyle().setPosition(Style.Position.RELATIVE);
        aligner.getStyle().setHeight(100, Style.Unit.PCT);
        aligner.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        aligner.getStyle().setVisibility(Style.Visibility.HIDDEN);
        //
        element.insertFirst(content);
        // aligner must go after content because of Gecko's craziness.
        element.insertAfter(aligner, content);
    }

    @Override
    public String getImageResource() {
        return image;
    }

    private void format() {
        if (isAttached()) {
            organizeText();
            organizeImage();
            organizeHorizontalAlignment();
            organizeVerticalAlignment();
        }
    }

    protected void organizeHorizontalAlignment() {
        final Style containerStyle = element.getStyle();
        final Style contentStyle = content.getStyle();
        switch (horizontalAlignment) {
            case LEFT:
            case LEADING:
                contentStyle.setLeft(0, Style.Unit.PX);
                contentStyle.clearRight();
                contentStyle.setTextAlign(Style.TextAlign.LEFT);
                containerStyle.setTextAlign(Style.TextAlign.LEFT);
                break;
            case RIGHT:
            case TRAILING:
                contentStyle.clearLeft();
                contentStyle.setRight(0, Style.Unit.PX);
                contentStyle.setTextAlign(Style.TextAlign.RIGHT);
                containerStyle.setTextAlign(Style.TextAlign.RIGHT);
                break;
            case CENTER:
                // contentStyle.setLeft(0, Style.Unit.PX);
                // contentStyle.setRight(0, Style.Unit.PX);
                contentStyle.setTextAlign(Style.TextAlign.CENTER);
                containerStyle.setTextAlign(Style.TextAlign.CENTER);
                break;
        }
    }

    protected void organizeVerticalAlignment() {
        final Style contentStyle = content.getStyle();
        final Style alignerStyle = aligner.getStyle();
        switch (verticalAlignment) {
            case TOP:
                contentStyle.setVerticalAlign(Style.VerticalAlign.TOP);
                break;
            case BOTTOM: {
                contentStyle.setVerticalAlign(Style.VerticalAlign.BOTTOM);
                break;
            }
            case CENTER: {
                contentStyle.setVerticalAlign(Style.VerticalAlign.MIDDLE);
                break;
            }
        }
    }

    protected void organizeText() {
        if (html) {
            content.setInnerHTML(text != null ? text : "");
        } else {
            content.setInnerText(text != null ? text : "");
        }
    }

    protected void organizeImage() {
        Style es = content.getStyle();
        int textGap = text != null && !text.isEmpty() ? iconTextGap : 0;
        if (image != null) {
            if (horizontalTextPosition == LEFT || horizontalTextPosition == LEADING) {
                /*
                backgroundPosition = "right";
                es.setPaddingLeft(0, Style.Unit.PX);
                es.setPaddingRight(textGap + image.getWidth(), Style.Unit.PX);
                */
            } else if (horizontalTextPosition == RIGHT || horizontalTextPosition == TRAILING) {
                /*
                backgroundPosition = "left";
                es.setPaddingLeft(textGap + image.getWidth(), Style.Unit.PX);
                es.setPaddingRight(0, Style.Unit.PX);
                */
            } else {
                if (text == null || text.isEmpty()) {
                    /*
                    int imageWidth = image.getWidth();
                    es.setPaddingLeft(imageWidth / 2, Style.Unit.PX);
                    es.setPaddingRight(imageWidth / 2, Style.Unit.PX);
                    */
                }
                //backgroundPosition = "center";
            }
            //backgroundPosition += " ";
            if (verticalTextPosition == TOP || verticalTextPosition == LEADING) {
                /*
                backgroundPosition += "bottom";
                es.setPaddingTop(0, Style.Unit.PX);
                es.setPaddingBottom(textGap + image.getHeight(), Style.Unit.PX);
                */
            } else if (verticalTextPosition == BOTTOM || verticalTextPosition == TRAILING) {
                /*
                backgroundPosition += "top";
                es.setPaddingTop(textGap + image.getHeight(), Style.Unit.PX);
                es.setPaddingBottom(0, Style.Unit.PX);
                */
            } else {
                if (text == null || text.isEmpty()) {
                    /*
                    int imageHeight = image.getHeight();
                    es.setPaddingTop(imageHeight / 2, Style.Unit.PX);
                    es.setPaddingBottom(imageHeight / 2, Style.Unit.PX);
                    */
                }
                //backgroundPosition += "center";
            }
            //es.setProperty("background", "url(" + image + ")" + " no-repeat " + backgroundPosition);
        }
    }

    @Override
    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    @Override
    public void setVerticalAlignment(int aValue) {
        if (verticalAlignment != aValue) {
            verticalAlignment = aValue;
            format();
        }
    }

    @Override
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    @Override
    public void setHorizontalAlignment(int aValue) {
        if (horizontalAlignment != aValue) {
            horizontalAlignment = aValue;
            format();
        }
    }

    @Override
    public String getText() {
        return !html ? text : null;
    }

    @Override
    public void setText(String aValue) {
        html = false;
        text = aValue;
        format();
    }

    @Override
    public String getHTML() {
        return html ? text : null;
    }

    @Override
    public void setHTML(String aValue) {
        html = true;
        text = aValue;
        format();
    }

    @Override
    public int getIconTextGap() {
        return iconTextGap;
    }

    @Override
    public void setIconTextGap(int aValue) {
        if (iconTextGap != aValue) {
            iconTextGap = aValue;
            format();
        }
    }

    @Override
    public int getHorizontalTextPosition() {
        return horizontalTextPosition;
    }

    @Override
    public void setHorizontalTextPosition(int aValue) {
        if (horizontalTextPosition != aValue) {
            horizontalTextPosition = aValue;
            format();
        }
    }

    @Override
    public int getVerticalTextPosition() {
        return verticalTextPosition;
    }

    @Override
    public void setVerticalTextPosition(int aValue) {
        if (verticalTextPosition != aValue) {
            verticalTextPosition = aValue;
            format();
        }
    }

    @Override
    public void setImageResource(String aValue) {
        image = aValue;
        format();
    }
}
