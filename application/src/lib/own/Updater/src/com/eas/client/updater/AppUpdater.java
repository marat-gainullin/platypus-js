/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.awt.HeadlessException;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author AB
 */
public class AppUpdater {

    private String fNameConfig = "";
    private String configUrl = "";
    private String appFilesUrl = "";
    private String runAppParams;
    private String fileTmpUpdate = "";
    private String startDir = "";
    private ProgressView progressView;

    /**
     *
     * @param fNameCfg
     * @param cfgUrl
     * @param appFlsUrl public AppUpdater(String fNameCfg, String cfgUrl, String
     * appFlsUrl) { fNameConfig = FileUpdater.fixFileSeparatorChar(fNameCfg);
     * configUrl = cfgUrl; appFilesUrl = appFlsUrl; fileTmpUpdate = ""; startDir
     * = ""; }
     */
    /**
     *
     * @param fNameCfg
     * @param cfgUrl
     * @param appFlsUrl
     * @param homeDir
     * @param tmpFileName
     */
    public AppUpdater(String fNameCfg, String cfgUrl, String appFlsUrl, String homeDir, String tmpFileName) {
        fNameConfig = FileUpdater.fixFileSeparatorChar(fNameCfg);
        configUrl = cfgUrl;
        appFilesUrl = appFlsUrl;
        fileTmpUpdate = tmpFileName;
        startDir = homeDir;
    }

    public AppUpdater(String fNameCfg, String cfgUrl, String appFlsUrl, String homeDir, String tmpFileName, ProgressView aPView) {
        this(fNameCfg, cfgUrl, appFlsUrl, homeDir, tmpFileName);
        progressView = aPView;
    }

    /**
     * @return The fnameconfig.
     */
    public String getFNameConfig() {
        return fNameConfig;
    }

    /**
     * @param aValue The fname config to set.
     */
    public void setFNameConfig(String aValue) {
        this.fNameConfig = FileUpdater.fixFileSeparatorChar(aValue);
    }

    /**
     * @return The config url.
     */
    public String getConfigUrl() {
        return configUrl;
    }

    /**
     * @param aValue The config url to set.
     */
    public void setConfigUrl(String aValue) {
        configUrl = aValue;
    }

    /**
     * @return the appfilesurl
     */
    public String getAppFilesUrl() {
        return appFilesUrl;
    }

    /**
     * @param aValue The application files url to set.
     */
    public void setAppFilesUrl(String aValue) {
        appFilesUrl = aValue;
    }

    /**
     *
     * @return
     */
    public byte checkNewVersion() {
        if ((!"".equals(fNameConfig)) && (!"".equals(configUrl))) {
            try {
                File f = new File(fNameConfig);
                if (f.exists()) {
                    DownloadFile df = new DownloadFile(configUrl, "");
                    df.setShowReplaceDlg(false);
                    df.setShowProgress(false);
                    InputStream in = df.getStreamVersionInfo();
                    if (in != null) {// Successfull version file download.
                        Document docto = XMLVersion.getDocumentFile(fNameConfig);
                        Document docfrom = XMLVersion.getDocumentStream(in);
                        return XMLVersion.compareDocumentsNodeEx(docto, docfrom);
                    } else {// download failed
                        Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, String.format(Updater.res.getString("fileNotLoad"), configUrl));
                        return UpdaterConstants.ERROR;
                    }
                } else {
                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, String.format(Updater.res.getString("fileNotFound"), fNameConfig));
                    return UpdaterConstants.ERROR;
                }
            } catch (HeadlessException e) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
                return UpdaterConstants.ERROR;
            }
        } else {
            return UpdaterConstants.ERROR;
        }
    }

    /**
     *
     */
    public void update() {
        if ((!"".equals(fNameConfig)) && (!"".equals(configUrl)) && (!"".equals(appFilesUrl))) {
            try {
                Document docto = XMLVersion.getDocumentFile(fNameConfig);
                DownloadFile df = new DownloadFile(configUrl, "");
                df.setShowReplaceDlg(false);
                df.setShowProgress(false);
                InputStream in = df.getStreamVersionInfo();
                if (in != null) {// Successfull version file download.
                    Document docfrom = XMLVersion.getDocumentStream(in);
                    Version vLocal = XMLVersion.getDocVersion(docto);
                    Version vRemote = XMLVersion.getDocVersion(docfrom);
                    if (progressView != null && vLocal != null && vRemote != null) {
                        byte vDifference = vRemote.compareTo(vLocal);
                        progressView.getVersion().setText(String.format(Updater.res.getString("captionDetail"), vLocal.toString(), vRemote.toString()));
                        progressView.setVisible(true);
                        FileUpdater fu = new FileUpdater(appFilesUrl, startDir, vDifference == UpdaterConstants.MINOR_NOT_EQUALS);
                        if (progressView != null) {
                            fu.setUpdVis(progressView);
                        }
                        if (fu.update(fileTmpUpdate)) {
                            df.saveDocument(docfrom, fNameConfig);
                        }
                        progressView.dispose();
                    } else {
                        Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, "One of version object is null!!!");
                    }
                } else {
                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, String.format(Updater.res.getString("fileNotLoad"), configUrl));
                }
            } catch (Exception e) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * @return the runappparams
     */
    public String getRunAppParams() {
        return runAppParams;
    }

    /**
     * @param aValue the runappparams to set
     */
    public void setRunAppParams(String aValue) {
        runAppParams = aValue;
    }

    /**
     * @return the filetmpupdate
     */
    public String getFileTmpUpdate() {
        return fileTmpUpdate;
    }

    /**
     * @param aValue the filetmpupdate to set
     */
    public void setFileTmpUpdate(String aValue) {
        fileTmpUpdate = aValue;
    }

    /**
     * @return the startdir
     */
    public String getStartDir() {
        return startDir;
    }

    /**
     * @param aValue the startdir to set
     */
    public void setStartDir(String aValue) {
        startDir = aValue;
    }

    /**
     * @return the updvis
     */
    public ProgressView getProgressView() {
        return progressView;
    }

}
