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

    private final String PWC_DIRECTORY = "bin/pwc/";
    private String hostToFiles = "";
    private String curDir = "";
    private UpdProgress updVis = null;

    /**
     * Creator
     *
     * @param host
     */
    public FileUpdater(String host) {
        hostToFiles = host;
        curDir = "";
    }

    /**
     * Creator
     *
     * @param host
     * @param path
     */
    public FileUpdater(String host, String path) {
        hostToFiles = host;
        curDir = path;
    }

    /**
     * Fixes the file sperator char for the target platform using the following
     * replacement.
     *
     * <ul> <li> '/' ==> File.separatorChar <li> '\\' ==> File.separatorChar
     * </ul>
     *
     * @param arg the argument to fix
     * @return the transformed argument
     */
    public static String fixFileSeparatorChar(String arg) {
        return arg.replace(UpdaterConstants.SLASH_CHAR, File.separatorChar).replace(UpdaterConstants.BACKSLASH_CHAR, File.separatorChar);
    }

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new IOException("Failed to delete file: " + f); // NOI18N
        }
    }

    /**
     *
     * @param aZipFileName
     * @return
     */
    public boolean unPackZip(String aZipFileName) {
        try {
            File f = new File(aZipFileName);
            if (f.exists()) {
                boolean res = true;
                try (ZipFile zf = new ZipFile(aZipFileName)) {
                    String curFName;
                    Enumeration entries = zf.entries();
                    int cnt = 0;
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
                    String pwcDirName = fixFileSeparatorChar(curDir + PWC_DIRECTORY);
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) entries.nextElement();
                        curFName = curDir + entry.getName();
                        curFName = fixFileSeparatorChar(curFName);
                        if (!entry.isDirectory()) {
                            File ff = new File(curFName);
                            if ((ff.exists()) && (!ff.canWrite())) {
                                res = false;
                                cnt++;
                                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("couldNotCreateFile"), ff.getName()));
                                continue;
                            }
                            if (ff.getPath().contains(pwcDirName)) {
                                ff.getParentFile().mkdirs();
                                if (ff.createNewFile()) {
                                    extractEntry(zf, entry, curFName);
                                } else {
                                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("couldNotCreateFile"), ff.getName()));
                                }
                            } else {
                                if (curFName.contains(UpdaterConstants.UPDATER_FIND_LABEL)) {
                                    ff.createNewFile();
                                }
                                if (ff.exists()) {
                                    extractEntry(zf, entry, curFName);
                                }
                            }
                        } else {
                            if (curFName.equalsIgnoreCase(pwcDirName)) {
                                File pwcDir = new File(curFName);
                                if (pwcDir.exists()) {
                                    for (File c : pwcDir.listFiles()) {
                                        delete(c);
                                    }
                                }
                            }
                        }
                        cnt++;
                        if (updVis != null) {
                            updVis.getProgress().setValue(cnt);
                        }
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
            File f = new File(aZipFileName);
            if (f.exists()) {
                f.delete();
            }
            if (updVis != null) {
                updVis.getProgress().setValue(0);
            }
        }
    }

    private void extractEntry(ZipFile aZipFile, ZipEntry aEntry, String aDestFileName) throws IOException {
        try (InputStream source = aZipFile.getInputStream(aEntry); OutputStream destination = new BufferedOutputStream(new FileOutputStream(aDestFileName))) {
            byte[] buffer = new byte[UpdaterConstants.BUFFER_SIZE];
            int len;
            while ((len = source.read(buffer)) >= 0) {
                destination.write(buffer, 0, len);
            }
        }

    }

    /**
     *
     * @param aPakageLocalFileName File name to download zip file with update
     * files to.
     * @return
     */
    public boolean update(String aPakageLocalFileName) {
        try {
            if (aPakageLocalFileName == null || aPakageLocalFileName.isEmpty()) {
                aPakageLocalFileName = UpdaterConstants.TMP_FILE;
            }
            if (updVis != null) {
                updVis.getProgress().setString(Updater.res.getString("operationDownload"));
                updVis.getStep().setText("1/2");
            }
            DownloadFile df = new DownloadFile(hostToFiles, aPakageLocalFileName);
            if (updVis != null) {
                df.setUpdVis(updVis);
            }
            df.setShowReplaceDlg(false);
            df.setShowProgress(true);
            boolean res = df.downloadFileHttpLink();
            if (res) {
                if (updVis != null) {
                    updVis.getProgress().setString(Updater.res.getString("operationUnZip"));
                    updVis.getStep().setText("2/2");
                }
                return unPackZip(aPakageLocalFileName);
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
