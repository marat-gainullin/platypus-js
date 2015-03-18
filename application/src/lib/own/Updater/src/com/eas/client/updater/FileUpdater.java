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
    private String urlToFiles = "";
    private String curDir = "";
    private ProgressView progressView;
    private boolean replaceMode;

    /**
     *
     * @param aUrlToFiles
     * @param aLocalPath
     */
    public FileUpdater(String aUrlToFiles, String aLocalPath) {
        super();
        urlToFiles = aUrlToFiles;
        curDir = aLocalPath;
    }

    public FileUpdater(String host, String path, boolean aReplaceMode) {
        this(host, path);
        replaceMode = aReplaceMode;
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
                    if (progressView != null) {
                        progressView.getProgress().setValue(0);
                        progressView.getProgress().setMinimum(0);
                        Enumeration entr = zf.entries();
                        while (entr.hasMoreElements()) {
                            cnt++;
                            entr.nextElement();
                        }
                        progressView.getProgress().setMaximum(cnt);
                    }
                    cnt = 0;
                    String pwcDirName = fixFileSeparatorChar(curDir + PWC_DIRECTORY);
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) entries.nextElement();
                        curFName = curDir + entry.getName();
                        curFName = fixFileSeparatorChar(curFName);
                        if (!entry.isDirectory()) {
                            File ff = new File(curFName);
                            if (ff.exists() && !ff.canWrite()) {
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
                                if (curFName.contains(UpdaterConstants.UPDATER_FIND_LABEL) && curFName.endsWith(".jar")) {
                                    String updaterJarPath = ff.getPath();
                                    File toBeDeleted = new File(updaterJarPath + ".old");
                                    toBeDeleted.delete();
                                    File aff = new File(ff.getAbsolutePath());
                                    ff.renameTo(toBeDeleted);
                                    ff.deleteOnExit();
                                    ff = aff;
                                }
                                if (replaceMode || curFName.contains(UpdaterConstants.UPDATER_FIND_LABEL)) {
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
                        if (progressView != null) {
                            progressView.getProgress().setValue(cnt);
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
            if (progressView != null) {
                progressView.getProgress().setValue(0);
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
     * @param aPakageLocalFileName A file name to download zip to.
     * @return
     */
    public boolean update(String aPakageLocalFileName) {
        try {
            if (aPakageLocalFileName == null || aPakageLocalFileName.isEmpty()) {
                aPakageLocalFileName = UpdaterConstants.TMP_FILE;
            }
            if (progressView != null) {
                progressView.getProgress().setString(Updater.res.getString("operationDownload"));
                progressView.getStep().setText("1/2");
            }
            DownloadFile df = new DownloadFile(urlToFiles, aPakageLocalFileName);
            if (progressView != null) {
                df.setUpdVis(progressView);
            }
            df.setShowReplaceDlg(false);
            df.setShowProgress(true);
            boolean res = df.downloadFileHttpLink();
            if (res) {
                if (progressView != null) {
                    progressView.getProgress().setString(Updater.res.getString("operationUnZip"));
                    progressView.getStep().setText("2/2");
                }
                return unPackZip(aPakageLocalFileName);
            } else {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, String.format(Updater.res.getString("fileNotLoad"), urlToFiles));
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
    public String getUrlToFiles() {
        return urlToFiles;
    }

    /**
     * @param aValue the hosttofiles to set
     */
    public void setUrlToFiles(String aValue) {
        urlToFiles = aValue;
    }

    /**
     * @return the curdir
     */
    public String getCurDir() {
        return curDir;
    }

    /**
     * @param aValue the curdir to set
     */
    public void setCurDir(String aValue) {
        curDir = aValue;
    }

    /**
     * @param aValue the updvis to set
     */
    public void setProgressView(ProgressView aValue) {
        progressView = aValue;
    }
}
