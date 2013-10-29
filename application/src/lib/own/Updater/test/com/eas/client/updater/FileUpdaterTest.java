/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

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
        String link = "http://research.office.altsoft.biz/platypus/client/updates/NightlyBuild/application.zip";
        String fname = "app.zip";
        DownloadFile df=new DownloadFile(link, fname);
        df.setShowReplaceDlg(false);
        df.setShowProgress(false);
        df.downloadFileHttpLink();
        FileUpdater instance = new FileUpdater(link, "");
        File updater = new File("lib/own");
        updater.mkdirs();
        boolean result = instance.unPackZip(fname);
        File f = new File("app.txt");
        f.delete();
        updater = new File("lib/own/Updater-new.jar");
        updater.delete();
        assertTrue(result);
    }
}
