/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

/**
 *
 * @author mg
 */
public class ReportConfig {

    private final String nameTemplate;
    private final byte[] templateContent;
    private final String format;

    public ReportConfig(String aNameTemplate, String aFormat, byte[] aTemplateContent) {
        super();
        nameTemplate = aNameTemplate;
        format = aFormat;
        templateContent = aTemplateContent;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public String getFormat() {
        return format;
    }

    public byte[] getTemplateContent() {
        return templateContent;
    }

}
