/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AB
 */
public class FileUpdaterTest {

    /**
     * Test of updateFile method, of class FileUpdater.
     */
    @Test
    public void testUpdateFile_String() {
        System.out.println("Download and unzip files");
        String link = "http://olympic.altsoft.biz/platypus/client/updates/app.zip";
        String fname = "app.zip";
        boolean repdlg = false;
        DownloadFile df=new DownloadFile(link, fname);
        df.setShowReplaceDlg(false);
        df.setShowProgress(false);
        df.downloadFileHttpLink();
        FileUpdater instance = new FileUpdater("http://olympic/platypus/client/updates/app.zip","");
        boolean expResult = true;
        boolean result = instance.unPackZip(fname);
        File f=new File("app.txt");
        f.delete();
        assertEquals(expResult, result);
    }
}
