/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import java.util.List;
import javax.swing.ImageIcon;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.util.FileUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author vv
 */
public class AppElementConstructorCompletionItem extends JsFunctionCompletionItem {

    protected static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(PlatypusModuleDataObject.class.getResource("module.png")); //NOI18N
    private FileObject fileObject;
    private static final int SORT_PRIORITY = 10;

    public AppElementConstructorCompletionItem(String name, String rightText, List<String> params, FileObject aFileObject, int aStartOffset, int aEndOffset) {
        super(name, rightText, params, null, aStartOffset, aEndOffset);
        fileObject = aFileObject;
    }

    @Override
    public ImageIcon getIcon() {
        return CONSTRUCTOR_ICON;
    }

    @Override
    public String getInfomationText() {
        if (informationText == null) {
            String jsDocContent = null;
            try {
                String fileContent = null;
                fileContent = FileUtils.readString(FileUtil.toFile(fileObject), PlatypusFiles.DEFAULT_ENCODING);
                jsDocContent = PlatypusFilesSupport.getMainJsDocBody(fileContent);
            } catch (IOException ex) {
                Logger.getLogger(AppElementConstructorCompletionItem.class.getName()).log(Level.SEVERE, "Read file error: " + fileObject.getPath(), ex);//NOI18N
            }
            if (jsDocContent != null && !jsDocContent.isEmpty()) {
                JsCommentFormatter formatter = new JsCommentFormatter(CompletionSupport.getComments(jsDocContent));
                informationText = formatter.toHtml();
            } else {
                informationText = "";//NOI18N
            }
        }
        return informationText;
    }

    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
    
    
}
