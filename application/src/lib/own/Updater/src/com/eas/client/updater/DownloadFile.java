/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.updater;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author AB
 */
public class DownloadFile {

    private boolean showReplaceDlg = true;
    private String host = "";
    private String contentName = "";
    private String fName = "";
    private String link = "";
    private int contSize = 0;
    private boolean showProgress = true;
    private UpdProgress updVis;

    public DownloadFile(String aSourceUrl, String aDestFileName) {
        link = aSourceUrl;
        fName = FileUpdater.fixFileSeparatorChar(aDestFileName);
    }

    /**
     * 
     * @return
     */
    public boolean downloadFileHttpLink() {
        if ("".equals(getLink())) {
            setLink("http://" + getHost() + "/" + getContentName());
        }
        InputStream in = null;
        try {
            if (getLink().indexOf("http") == -1) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, Updater.res.getString("badURL"));
                return false;
            }
            URL nurl = new URL(getLink());
            URLConnection uc = nurl.openConnection();
            uc.setRequestProperty("Content-Type", "application/x-jar");
            uc.connect();
            setContSize(uc.getContentLength());
            in = uc.getInputStream();
            return saveFile(in);
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * 
     * @return Return InputStream with version file. It is XML file.
     */
    public InputStream getStreamVersionInfo() {
        if ("".equals(getLink())) {
            setLink("http://" + getHost() + "/" + getFName());
        }
        InputStream in = null;
        try {
            if (getLink().indexOf("http") == -1) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, Updater.res.getString("badURL"));
                return null;
            }
            URL nurl = new URL(getLink());
            URLConnection uc = nurl.openConnection();
            uc.setRequestProperty("Content-Type", "xml");
            uc.connect();
            return uc.getInputStream();
        } catch (Exception e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
            return null;
        }
    }
    
    /**
     * 
     * @param aDoc DOM document which need to save.
     * @param aFileName File name in which docement will be save.
     * @return Return true if operation was complited successfully.
     */
    public boolean saveDocument(Document aDoc,String aFileName) {
        try {   
        Transformer t=TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(aDoc), new StreamResult(new FileOutputStream(aFileName)));
            return true;
        } catch (TransformerFactoryConfigurationError | FileNotFoundException | TransformerException ex) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return false;
        }   
    }
    
    /**
     * 
     * @param in Input stream with data.
     * @return true if operation complited successfully.
     */
    public boolean saveFile(InputStream in) {
        FileOutputStream out = null;
        try {
            File tmpF = new File(getFName());
            if (tmpF.exists()) {
                if (!tmpF.canWrite()) {
                    Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.WARNING, Updater.res.getString("permissionsDenied"));
                    return false;
                }

                if (showReplaceDlg) {
                    int selection = JOptionPane.showConfirmDialog(null, String.format(Updater.res.getString("confirmFileReplace"), getFName()), Updater.res.getString("confirmCaption"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (selection != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
                tmpF.delete();
            }



            if (showProgress) {
                if (updVis != null) {
                    updVis.getProgress().setValue(0);
                    updVis.getProgress().setMinimum(0);
                    updVis.getProgress().setMaximum(getContSize());
                }
            }
            out = new FileOutputStream(getFName());
            int cnt = 0;
            int val = 0;
            byte[] data = new byte[UpdaterConstants.BUFFER_SIZE]; 
            while ((cnt = in.read(data, 0, data.length)) != -1) {
                val += cnt;
                if (showProgress) {
                    if (updVis != null) {
                        updVis.getProgress().setValue(val);
                    }
                }
                out.write(data, 0, cnt);
            }
        } catch (HeadlessException | IOException e) {
            Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            try {
                in.close();
                out.close();
                if (showProgress) {
                    if (updVis != null) {
                        updVis.getProgress().setValue(0);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(UpdaterConstants.LOGGER_NAME).log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
        return true;
    }

    /**
     * @return the showreplacedlg
     */
    public boolean isShowReplaceDlg() {
        return showReplaceDlg;
    }

    /**
     * @param showReplaceDlg the showreplacedlg to set
     */
    public void setShowReplaceDlg(boolean showReplaceDlg) {
        this.showReplaceDlg = showReplaceDlg;
    }
 
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the contentname
     */
    public String getContentName() {
        return contentName;
    }

    /**
     * @param contentName the contentname to set
     */
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * @return the fname
     */
    public String getFName() {
        return fName;
    }

    /**
     * @param fName the fname to set
     */
    public void setFName(String fName) {
        this.fName = FileUpdater.fixFileSeparatorChar(fName);
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the contsize
     */
    public int getContSize() {
        return contSize;
    }

    /**
     * @param contSize the contsize to set
     */
    public void setContSize(int contSize) {
        this.contSize = contSize;
    }

    /**
     * @return the showprogress
     */
    public boolean isShowProgress() {
        return showProgress;
    }

    /**
     * @param showProgress the showprogress to set
     */
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
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
