/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.reports.ExcelReport;
import com.eas.designer.application.PlatypusUtils;
import com.eas.util.BinaryUtils;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class ReportDesignerPanel extends JPanel {

    protected JPanel reportTemplatePanel;
    protected ExcelReport xlReport;
    protected Runnable modifiedRunnable;
    protected static final String RESOURCE_PREFIX = "/com/eas/designer/application/report/resources/";
    protected static final String PLACEHOLDER_RESOURCE_NAME = RESOURCE_PREFIX + NbBundle.getMessage(ReportDesignerPanel.class, "htmlReportPlaceholder"); // NOI18N
    protected static final String BASE_HREF_NAME = "reportTemplateBaseHref";
    protected static final String EDITING_HREF_NAME = "playpus://edit_report_template";

    public ReportDesignerPanel(Runnable aModifiedRunnable) throws Exception {
        super(new BorderLayout());
        modifiedRunnable = aModifiedRunnable;
        initReportPanel();
        add(reportTemplatePanel, BorderLayout.CENTER);
    }

    private void initReportPanel() throws IOException {
        reportTemplatePanel = new JPanel();
        JEditorPane htmlPage = new JEditorPane();
        htmlPage.setContentType("text/html");
        byte[] htmlData = BinaryUtils.readStream(ReportDesignerPanel.class.getResourceAsStream(PLACEHOLDER_RESOURCE_NAME), -1);
        String html = new String(htmlData, PlatypusUtils.COMMON_ENCODING_NAME);
        URL urlBase = ReportDesignerPanel.class.getResource(RESOURCE_PREFIX);
        html = html.replaceAll(BASE_HREF_NAME, urlBase.toString());
        htmlPage.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getDescription().equals(EDITING_HREF_NAME)) {
                    try {
                        if (xlReport != null) {
                            xlReport.edit();
                        }
                        modifiedRunnable.run();
                    } catch (Exception ex) {
                        Logger.getLogger(ReportDesignerPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        htmlPage.setText(html);
        htmlPage.setEditable(false);
        reportTemplatePanel.setLayout(new BorderLayout());
        reportTemplatePanel.add(new JScrollPane(htmlPage), BorderLayout.CENTER);
    }

    public void setData(ExcelReport aReport) throws Exception {
        try {
            xlReport = aReport;
        } catch (Exception ex) {
            Logger.getLogger(ReportDesignerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ExcelReport getXlReport() {
        return xlReport;
    }

}
