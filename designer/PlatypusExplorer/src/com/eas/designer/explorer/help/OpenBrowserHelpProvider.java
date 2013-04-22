/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.help;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.ChangeListener;
import org.netbeans.api.javahelp.Help;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author vv
 */
@ServiceProviders({
    @ServiceProvider(service=Help.class, supersedes="org.netbeans.modules.javahelp.JavaHelp"), //NOI18N
    @ServiceProvider(service=HelpCtx.Displayer.class, supersedes="org.netbeans.modules.javahelp.JavaHelp") //NOI18N
})
public class OpenBrowserHelpProvider  extends Help implements HelpCtx.Displayer{

    @Override
    public Boolean isValidID(String id, boolean force) {
        return true;
    }

    @Override
    public void showHelp(HelpCtx ctx, boolean showmaster) {
        launchBrowser();
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public boolean display(HelpCtx help) {
        launchBrowser();
        return true;
    }
    
    private void launchBrowser() {
        try { 
            URLDisplayer.getDefault().showURL(new URL("http://altsoft.biz/")); //NOI18N
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
