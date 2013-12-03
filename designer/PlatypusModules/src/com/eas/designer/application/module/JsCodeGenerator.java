    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.cache.PlatypusFilesSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.InteriorSection;
import org.openide.nodes.Node.Cookie;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class JsCodeGenerator implements Cookie {

    private static final String EVT_SECTION_PREFIX = "event_"; // NOI18N
    private static final String EVT_VARIABLE_NAME = "evt"; // NOI18N
    private PlatypusModuleDataObject dataObject;
    private PlatypusModuleSupport editorSupport;
    private boolean canPosition = true;

    public JsCodeGenerator(PlatypusModuleDataObject aDataObject) {
        super();
        dataObject = aDataObject;
        editorSupport = dataObject.getLookup().lookup(PlatypusModuleSupport.class);
    }

    public boolean isCanPosition() {
        return canPosition;
    }

    public void setCanPosition(boolean aValue) {
        canPosition = aValue;
    }

    private void clearTextUndo() {
        editorSupport.getUndoRedoManager().discardAllEdits();
    }

    public void eventHandlerAdded(Method aListenerMethod, String handlerName, String handlerText, String annotationText, boolean newHandler) {
        if (newHandler) {
            generateEventHandler(handlerName, aListenerMethod, annotationText);
        }
        gotoEventHandler(handlerName);
    }

    public void eventHandlerRenamed(String oldHandlerName, String newHandlerName) {
        renameEventHandler(oldHandlerName, newHandlerName);
    }

    public void eventHandlerRemoved(String handlerName, boolean empty) {
        deleteEventHandler(handlerName);
    }

    /**
     * Generates the specified event handler.
     */
    public void generateEventHandler(String handlerName,
            Method originalMethod,
            String annotationText) {

        InteriorSection sec = getEventHandlerSection(handlerName);
        // will generate only non-existent handlers
        if (sec == null) {
            StringWriter buffer = new StringWriter();
            try {
                if (sec == null) {
                    sec = insertEventHandlerSection(handlerName);
                }
                int i0, i1, i2;

                if (annotationText != null) {
                    buffer.write(annotationText);
                    buffer.flush();
                }
                i0 = buffer.getBuffer().length();
                generateListenerMethodHeader(handlerName, originalMethod, buffer);
                buffer.flush();
                i1 = buffer.getBuffer().length();
                String bodyText = getDefaultEventBody();
                buffer.write("        " + bodyText);
                buffer.flush();
                i2 = buffer.getBuffer().length();
                buffer.write("    }\n"); // footer with new line // NOI18N
                buffer.flush();

                if (i0 != 0) {
                    editorSupport.getDocument().insertString(sec.getStartPosition().getOffset(), annotationText, null);
                }
                sec.setHeader(buffer.getBuffer().substring(i0, i1));
                sec.setBody(buffer.getBuffer().substring(i1, i2));
                sec.setFooter(buffer.getBuffer().substring(i2));

                buffer.close();
                clearTextUndo();
            } catch (javax.swing.text.BadLocationException | java.io.IOException ex) {
                Logger.getLogger(JsCodeGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    private String getDefaultEventBody() {
        return NbBundle.getMessage(JsCodeGenerator.class, "MSG_EventHandlerBody"); // NOI18N
    }

    /**
     * Removes the specified event handler - removes the whole method together
     * with the user code!
     *
     * @param handlerName The name of the event handler
     */
    private boolean deleteEventHandler(String handlerName) {
        InteriorSection section = getEventHandlerSection(handlerName);
        if (section == null) {
            return false;
        }
        // if there is another guarded section right before or after without
        // a gap, the neighbor sections could merge strangely - prevent this by
        // inserting an empty line (#94165)
        int startPos = section.getStartPosition().getOffset();
        int endPos = section.getEndPosition().getOffset();
        for (GuardedSection sec : editorSupport.getGuardedSectionManager().getGuardedSections()) {
            if (sec.getEndPosition().getOffset() + 1 == startPos) { // close section before
                try {
                    editorSupport.getDocument().insertString(startPos, "\n", null); // NOI18N
                } catch (javax.swing.text.BadLocationException ex) {
                } // should not happen, ignore
            } else if (sec.getStartPosition().getOffset() == endPos + 1) { // close section after
                try {
                    editorSupport.getDocument().insertString(endPos + 1, "\n", null); // NOI18N
                } catch (javax.swing.text.BadLocationException ex) {
                } // should not happen, ignore
            }
        }
        section.deleteSection();
        clearTextUndo();
        return true;
    }

    protected String[] generateParametersNames(Method aMethod) {
        Class[] parametersTypes = aMethod.getParameterTypes();
        String[] parametersNames;
        if (parametersTypes.length == 1) {
            /*
             && (EventObject.class.isAssignableFrom(parametersTypes[0])
             || RowsetEvent.class.isAssignableFrom(parametersTypes[0]))) {
             if (RowsetEvent.class.isAssignableFrom(parametersTypes[0])) {
             parametersNames = generateRowsetEventHeaderParameters(aMethod, parametersTypes[0]);
             } else {
             parametersNames = new String[]{EVT_VARIABLE_NAME};
             }
             */
            parametersNames = new String[]{EVT_VARIABLE_NAME};
        } else {
            parametersNames = new String[parametersTypes.length];
            for (int i = 0; i < parametersTypes.length; i++) {
                parametersNames[i] = "param" + i; // NOI18N
            }
        }
        return parametersNames;
    }

    private String[] generateListenerMethodHeader(String methodName,
            Method originalMethod,
            Writer writer)
            throws IOException {
        String[] paramNames = generateParametersNames(originalMethod);
        // generate the method
        writer.write("    function "); // NOI18N
        writer.write(methodName != null ? methodName : originalMethod.getName());
        writer.write("("); // NOI18N

        for (int i = 0; i < paramNames.length; i++) {
            writer.write(paramNames[i]);
            if (i + 1 < paramNames.length) {
                writer.write(", "); // NOI18N
            }
        }
        writer.write(")"); // NOI18N
        writer.write(" {\n"); // NOI18N
        return paramNames;
    }

    /**
     * Focuses the specified event handler in the editor.
     */
    private void gotoEventHandler(String handlerName) {
        if (canPosition) {
            InteriorSection sec = getEventHandlerSection(handlerName);
            if (sec != null) {
                editorSupport.openAt(sec.getCaretPosition());
            }
        }
    }

    /**
     * Gets the body (text) of event handler of given name.
     */
    public String getEventHandlerText(String handlerName) {
        InteriorSection section = getEventHandlerSection(handlerName);
        return (section == null) ? null : section.getBody();
    }

    // sections acquirement
    private InteriorSection getEventHandlerSection(String handlerName) {
        return editorSupport.getGuardedSectionManager().findInteriorSection(getEventSectionName(handlerName));
    }

    /**
     * Renames the specified event handler to the given new name.
     *
     * @param oldHandlerName The old name of the event handler
     * @param newHandlerName The new name of the event handler
     */
    private void renameEventHandler(String oldHandlerName, String newHandlerName) {
        InteriorSection sec = getEventHandlerSection(oldHandlerName);
        if (sec == null) {
            return;
        }

        // find the old handler name in the handler method header and replace
        // it with the new name
        String header = sec.getHeader();
        int index = header.indexOf('(');
        if (index < 0) {
            return; // should not happen unless the handler code is corrupted
        }
        index = header.substring(0, index).lastIndexOf(oldHandlerName);
        if (index < 0) {
            return; // old name not found; should not happen
        }
        try {
            sec.setHeader(header.substring(0, index) + newHandlerName + header.substring(index + oldHandlerName.length()));
            sec.setName(getEventSectionName(newHandlerName));
            clearTextUndo();
        } catch (java.beans.PropertyVetoException e) {
            // no op
        }
    }

    private InteriorSection insertEventHandlerSection(String handlerName) throws javax.swing.text.BadLocationException {
        int endPos = 0;
        AstRoot jsRoot = ((PlatypusModuleDataObject)editorSupport.getDataObject()).getAst();
        FunctionNode firstFunction = PlatypusFilesSupport.extractFirstFunction(jsRoot);
        if (firstFunction != null) {
            endPos = firstFunction.getAbsolutePosition() + firstFunction.getLength() - 2;// because of the following inserting code...
        } else {// if we have no valid ast
            int sectionsCount = 0;
            // find last event handler
            for (GuardedSection sec : editorSupport.getGuardedSectionManager().getGuardedSections()) {
                if (sec instanceof InteriorSection) {
                    sectionsCount++;
                    int pos = sec.getEndPosition().getOffset();
                    if (pos > endPos) {
                        endPos = pos;
                    }
                }
            }
            if (sectionsCount == 0) {
                int docLength = editorSupport.getDocument().getLength();
                endPos = docLength > 0 ? docLength - 1 : 0;
            }
        }
        // if there is another guarded section following with no gap, insert empty line (#109242)
        for (GuardedSection sec : editorSupport.getGuardedSectionManager().getGuardedSections()) {
            if (sec.getStartPosition().getOffset() == endPos + 1) {
                editorSupport.getDocument().insertString(endPos + 1, "\n", null); // NOI18N
                break;
            }
        }
        // create the new guarded section
        return editorSupport.getGuardedSectionManager().createInteriorSection(
                editorSupport.getDocument().createPosition(endPos + 1),
                getEventSectionName(handlerName));
    }

    // other
    private String getEventSectionName(String handlerName) {
        return EVT_SECTION_PREFIX + handlerName;
    }

    /*
     private String[] generateRowsetEventHeaderParameters(Method aMethod, Class<?> aEventClass) {
     if (RowChangeEvent.class.isAssignableFrom(aEventClass)) {
     return new String[]{"aField", "aOldValue", "aNewValue", "aObj"};
     } else if (RowsetScrollEvent.class.isAssignableFrom(aEventClass)) {
     if (aMethod.getName().startsWith("will")) {
     return new String[]{"aNewIndex"};
     } else {
     return new String[]{"aOldIndex", "aNewIndex"};
     }
     }
     return new String[0];
     }
     */
}
