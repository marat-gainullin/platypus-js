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
     * Test of update method, of class FileUpdater.
     */
    @Test
    public void testUpdateFile_String() {
        System.out.println("Download and unzip files");
        String link = "http://research.office.altsoft.biz/platypus/client/updates/application.zip";
        String fname = "app.zip";
        DownloadFile df=new DownloadFile(link, fname);
        df.setShowReplaceDlg(false);
        df.setShowProgress(false);
        df.downloadFileHttpLink();
        FileUpdater instance = new FileUpdater(link, "");
        boolean expResult = true;
        File updater = new File("lib\\own\\Updater-new.jar");
        updater.mkdirs();
        boolean result = instance.unPackZip(fname);
        File f = new File("app.txt");
        f.delete();
        updater.delete();
        assertEquals(expResult, result);
    }
}
