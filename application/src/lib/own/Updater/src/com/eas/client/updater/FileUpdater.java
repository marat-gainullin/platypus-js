/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author AB
 */
public class FileUpdater {

    private String hostToFiles = "";
    private String curDir = "";
    private UpdProgress updVis;

    /**
     * Creator
     * @param host 
     */
    public FileUpdater(String host) {
        hostToFiles = host;
        curDir = "";
    }

    /**
     * Creator
     * @param host
     * @param path  
     */
    public FileUpdater(String host, String path) {
        hostToFiles = host;
        curDir = path;
    }

    /**
     * 
     * @param in input stream with data
     * @param out output stream to save data to file
     * @throws IOException
     */
    public static void write(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[UpdaterConstants.BUFFER_SIZE];
        int len = 0;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * Fixes the file sperator char for the target platform
     * using the following replacement.
     * 
     * <ul>
     *  <li> '/' ==>  File.separatorChar
     *  <li> '\\' ==>  File.separatorChar
     * </ul>
     *
     * @param arg the argument to fix
     * @return the transformed argument 
     */
    public static String fixFileSeparatorChar(String arg) {
        return arg.replace(UpdaterConstants.SLASH_CHAR, File.separatorChar).replace(UpdaterConstants.BACKSLASH_CHAR, File.separatorChar);
    }

    /**
     * 
     * @param zfn
     * @return
     */
    public boolean unPackZip(String zfn) {
        ZipFile zf = null;
        try {
            File f =new File(zfn);  
            if (f.exists()) {
                zf = new ZipFile(zfn);
                boolean res = false;
                String curFName = "";
                String curPath = "";
                File ff = null;
                File fd = null;
                Enumeration entries = zf.entries();
                int cnt = 0;
                res = true;
                if (updVis != null) {
                    updVis.getProgress().setValue(0);
                    updVis.getProgress().setMinimum(0);
                    Enumeration entr = zf.entries();
                    while (entr.hasMoreElements()) {
                        cnt++;
                        entr.nextElement();
                    }
                    updVis.getProgress().setMaximum(cnt);
                }
                cnt = 0;
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    curFName = curDir + entry.getName();
                    curFName = fixFileSeparatorChar(curFName);
                    if (!entry.isDirectory()) {
                        ff = new File(curFName);
                        if ((!ff.exists()) && (ff.getParent() != null) && (!ff.getName().contains(UpdaterConstants.UPDATER_FIND_LABEL))) {
                            cnt++;
                             Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("fileNotFound"), ff.getName())+"|"+curFName);
                            continue;
                            /*curPath = ff.getParent();
                            fd = new File(curPath);
                            if (!fd.exists()) {
                                boolean mkDirs = fd.mkdirs();
                                if (!mkDirs) {
                                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("couldNotCreateDir"), fd.getName()));
                                    return false;
                                }
                            }*/
                        } else {
                            if ((ff.exists()) && (!ff.canWrite())) {
                                res = false;
                                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("couldNotCreateFile"), ff.getName()));
                                continue;
                            }
                        }
                        write(zf.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(curFName)));
                    }
                    cnt++;
                    if (updVis != null) {
                        updVis.getProgress().setValue(cnt);
                    }
                }
                return res;
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
            return false;
        } finally {
            try {
                zf.close();
                File f = new File(zfn);
                if (f.exists()) {
                    f.delete();
                }
                if (updVis != null) {
                    updVis.getProgress().setValue(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * 
     * @param fn file name to download zip file with update files
     * @return
     */
    public boolean updateFile(String fn) {
        try {
            boolean res = false;
            String str = "";
            if ("".equals(fn)) {
                str = UpdaterConstants.TMP_FILE;
            } else {
                str = fn;
            }
            if (updVis != null) {
                updVis.getProgress().setString(Updater.res.getString("operationDownload"));
                updVis.getStep().setText("1/2");
            }
            DownloadFile df = new DownloadFile(hostToFiles, str);
            if (updVis != null) {
                df.setUpdVis(updVis);
            }
            df.setShowReplaceDlg(false);
            df.setShowProgress(true);
            res = df.downloadFileHttpLink();
            if (res) {
                if (updVis != null) {
                    updVis.getProgress().setString(Updater.res.getString("operationUnZip"));
                    updVis.getStep().setText("2/2");
                }
                return unPackZip(str);
            } else {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("fileNotLoad"), hostToFiles));
                return res;
            }
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * @return the hosttofiles
     */
    public String getFtpHostToFiles() {
        return hostToFiles;
    }

    /**
     * @param hostToFiles the hosttofiles to set
     */
    public void setFtpHostToFiles(String hostToFiles) {
        this.hostToFiles = hostToFiles;
    }

    /**
     * @return the curdir
     */
    public String getCurDir() {
        return curDir;
    }

    /**
     * @param curDir the curdir to set
     */
    public void setCurDir(String curDir) {
        this.curDir = curDir;
    }

    /**
     * @return the updvis
     */
    public UpdProgress getUpdVis() {
        return updVis;
    }

    /**
     * @param updVis the updvis to set
     */
    public void setUpdVis(UpdProgress updVis) {
        this.updVis = updVis;
    }
}
