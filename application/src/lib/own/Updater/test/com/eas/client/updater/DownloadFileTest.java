/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.File;
import java.io.InputStream;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;


/**
 *
 * @author AB
 */
public class DownloadFileTest {
    
    /**
     * Test of downloadFileHttpLink method, of class DownloadFile.
     */
    @Test
    public void testDownloadFileHTTPLink_3args()  {
        try {
            System.out.println("downloadFileHTTPLink");
            String link = "http://research/platypus/client/updates/NightlyBuild/update.xml";
            String fname = "update.xml";
            boolean repdlg = false;
            boolean expResult = true;
            DownloadFile df=new DownloadFile(link, fname);
            df.setShowReplaceDlg(false);
            df.setShowProgress(false);
            boolean result = df.downloadFileHttpLink();
            File f = new File (fname);
            f.delete();
            assertEquals(result, expResult);
        } catch (Exception ex) {
             System.out.println(ex.getStackTrace());
        }
    }
    
    @Test
    public void testgetContentHTTPLink_3args()  {
        try {
            System.out.println("downloadStreamHTTPLink and GetDocum from this stream");
            String link = "http://research/platypus/client/updates/NightlyBuild/update.xml";
            String fname = "update.xml";
            boolean repdlg = false;
            boolean expResult = true;
            DownloadFile df=new DownloadFile(link, fname);
            df.setShowReplaceDlg(false);
            df.setShowProgress(false);
            InputStream in = df.getStreamVersionInfo();
            Document doc = XMLVersion.getDocumentStream(in);
            assertNotNull("DOM was not created", doc);
            Version v = XMLVersion.getDocVersion(doc);
            assertNotNull(v);
            assertTrue(!"".equals(v.toString()));
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}
