/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author vv
 */
public class MultipartConfig implements ElementConvertable {
    public static final String TAG_NAME = "multipart-config"; //NOI18N
    public static final String LOCATION_TAG_NAME = "location"; //NOI18N
    public static final String MAX_FILE_SIZE_TAG_NAME = "max-file-size"; //NOI18N
    public static final String MAX_REQUEST_SIZE_TAG_NAME = "max-request-size"; //NOI18N
    public static final String FILE_SIZE_THRESHOLD_TAG_NAME = "file-size-threshold"; //NOI18N
    
    private String location;
    private String maxFileSize;
    private String maxRequestSize;
    private String fileSizeThreshold;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public String getFileSizeThreshold() {
        return fileSizeThreshold;
    }

    public void setFileSizeThreshold(String fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        if (location != null) {
            Element locationElement = aDoc.createElement(LOCATION_TAG_NAME);
            locationElement.setTextContent(location);
            element.appendChild(locationElement);
        }
        if (maxFileSize != null) {
            Element maxFileSizeElement = aDoc.createElement(MAX_FILE_SIZE_TAG_NAME);
            maxFileSizeElement.setTextContent(maxFileSize);
            element.appendChild(maxFileSizeElement);
        }
        if (maxRequestSize != null) {
            Element maxRequestSizeElement = aDoc.createElement(MAX_REQUEST_SIZE_TAG_NAME);
            maxRequestSizeElement.setTextContent(maxRequestSize);
            element.appendChild(maxRequestSizeElement);
        }
        if (fileSizeThreshold != null) {
            Element fileSizeThresholdElement = aDoc.createElement(FILE_SIZE_THRESHOLD_TAG_NAME);
            fileSizeThresholdElement.setTextContent(fileSizeThreshold);
            element.appendChild(fileSizeThresholdElement);
        }
        return element;
    }
    
}
