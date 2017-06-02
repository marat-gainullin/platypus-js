package com.eas.widgets.boxes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mgainullin
 */
public class Localization {

    // TODO: covert to JSON
    private static final Map<String, String> data = new HashMap<String, String>() {
        {
            put("week.monday", "M");
            put("week.tuesday", "T");
            put("week.wensday", "W");
            put("week.thursday", "T");
            put("week.friday", "F");
            put("week.saturday", "S");
            put("week.sunday", "S");
            put("bold", "Bold");
            put("italic", "Italic");
            put("underline", "Underline");
            put("subscript", "Subscript");
            put("superscript", "Superscript");
            put("justify.left", "Justify left");
            put("justify.center", "Justify center");
            put("justify.right", "Justify right");
            put("cut", "Cut");
            put("copy", "Copy");
            put("paste", "Paste");
            put("undo", "Undo");
            put("redo", "Redo");
            put("strike.through", "Strike through");
            put("indent", "Indent");
            put("outdent", "Unindent");
            put("hr", "Horizontal ruler");
            put("ol", "Ordered list");
            put("ul", "Unordered list");
            put("insert.image", "Insert image");
            put("upload.image", "Upload image");
            put("create.link", "Create link");
            put("remove.link", "Remove link");
            put("remove.format", "Remove format");
            put("background.color", "Background color");
            put("foreground.color", "Foreground color");
            put("font", "Font name");
            put("size", "Font size");
            put("link.url", "Link url");
            put("image.url", "Image url");
            put("xxsmall", "Extra small");
            put("xsmall", "Smaller");
            put("small", "Small");
            put("medium", "Medium");
            put("large", "Large");
            put("xlarge", "Lager");
            put("xxlarge", "Extra large");
            put("time", "Time");
            put("date", "Date");
        }
    };

    public static String get(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return key;
        }
    }
}
