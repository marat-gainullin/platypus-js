package com.bearsoft.org.netbeans.modules.form;

import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelScalarComponent;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.editor.guards.GuardedSection;
import org.netbeans.api.editor.guards.InteriorSection;

/**
 * FormsJsCodeGenerator is the default code generator which produces a js source
 * for the form components.
 *
 * @author Ian Formanek, Jan Stola, mg
 */
class FormsJsCodeGenerator extends CodeGenerator {

    private static final String EVT_SECTION_PREFIX = "event_"; // NOI18N
    private static final String EVT_VARIABLE_NAME = "evt"; // NOI18N
    // scalar model-aware components
    public static Method selectValueMethod = null;
    public static Method handleValueMethod = null;
    // model aware grid
    public static Method handleCellMethod = null;
    // grid columns
    public static Method columnSelectValueMethod = null;
    public static Method columnHandleValueMethod = null;

    static {
        try {
            selectValueMethod = RADModelScalarComponent.ValueHostListener.class.getMethod("onSelect", new Class<?>[]{Object.class});
            handleValueMethod = RADModelScalarComponent.ValueHostListener.class.getMethod("onRender", new Class<?>[]{Object.class});
            handleCellMethod = RADModelGrid.ModelGridListener.class.getMethod("onRender", new Class<?>[]{Object.class});
            columnSelectValueMethod = RADModelGridColumn.ValueHostListener.class.getMethod("onSelect", new Class<?>[]{Object.class});
            columnHandleValueMethod = RADModelGridColumn.ValueHostListener.class.getMethod("onRender", new Class<?>[]{Object.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(FormsJsCodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private boolean initialized = false;
    private boolean canGenerate = true;
    private FormModel formModel;
    private PlatypusFormSupport formEditorSupport;

    @Override
    public void initialize(FormModel aModel) {
        if (!initialized) {
            formModel = aModel;
            PlatypusFormDataObject formDo = formModel.getDataObject();
            formEditorSupport = formDo.getLookup().lookup(PlatypusFormSupport.class);

            formModel.addFormModelListener(new FormListener());

            if (formEditorSupport.getGuardedSectionManager() == null) {
                System.err.println("ERROR: Cannot initialize guarded sections... code generation will malfunction."); // NOI18N
            }

            initialized = true;
        }
    }

    /**
     * Generates the specified event handler.
     */
    private void generateEventHandler(String handlerName,
            Method originalMethod,
            String bodyText,
            String annotationText) {
        if (!initialized || !canGenerate) {
            return;
        }

        InteriorSection sec = getEventHandlerSection(handlerName);
        if (sec != null && bodyText == null) {
            return; // already exists, no need to generate
        }
        StringWriter buffer = new StringWriter();

        try {
            if (sec == null) {
                sec = insertEvendHandlerSection(handlerName);
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
            if (bodyText == null) {
                bodyText = getDefaultEventBody();
            }
            buffer.write(bodyText);
            buffer.flush();
            i2 = buffer.getBuffer().length();
            buffer.write("}\n"); // footer with new line // NOI18N
            buffer.flush();

            if (i0 != 0) {
                formEditorSupport.getDocument().insertString(sec.getStartPosition().getOffset(), annotationText, null);
            }
            sec.setHeader(buffer.getBuffer().substring(i0, i1));
            sec.setBody(buffer.getBuffer().substring(i1, i2));
            sec.setFooter(buffer.getBuffer().substring(i2));

            buffer.close();
            clearUndo();
        } catch (javax.swing.text.BadLocationException | java.io.IOException e) {
        }
    }

    /**
     * Removes the specified event handler - removes the whole method together
     * with the user code!
     *
     * @param handlerName The name of the event handler
     */
    private boolean deleteEventHandler(String handlerName) {
        InteriorSection section = getEventHandlerSection(handlerName);
        if (section == null || !initialized || !canGenerate) {
            return false;
        }

        // if there is another guarded section right before or after without
        // a gap, the neighbor sections could merge strangely - prevent this by
        // inserting an empty line (#94165)
        int startPos = section.getStartPosition().getOffset();
        int endPos = section.getEndPosition().getOffset();
        for (GuardedSection sec : formEditorSupport.getGuardedSectionManager().getGuardedSections()) {
            if (sec.getEndPosition().getOffset() + 1 == startPos) { // close section before
                try {
                    formEditorSupport.getDocument().insertString(startPos, "\n", null); // NOI18N
                } catch (javax.swing.text.BadLocationException ex) {
                } // should not happen, ignore
            } else if (sec.getStartPosition().getOffset() == endPos + 1) { // close section after
                try {
                    formEditorSupport.getDocument().insertString(endPos + 1, "\n", null); // NOI18N
                } catch (javax.swing.text.BadLocationException ex) {
                } // should not happen, ignore
            }
        }
        section.deleteSection();
        clearUndo();
        return true;
    }

    private String getDefaultEventBody() {
        return "\t" + FormUtils.getBundleString("MSG_EventHandlerBody"); // NOI18N
    }

    /**
     * Renames the specified event handler to the given new name.
     *
     * @param oldHandlerName The old name of the event handler
     * @param newHandlerName The new name of the event handler
     */
    private void renameEventHandler(String oldHandlerName,
            String newHandlerName) {
        InteriorSection sec = getEventHandlerSection(oldHandlerName);
        if (sec == null || !initialized || !canGenerate) {
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
            clearUndo();
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    private InteriorSection insertEvendHandlerSection(String handlerName) throws javax.swing.text.BadLocationException {
        int endPos = 0;
        int sectionsCount = 0;
        // find last event handler
        for (GuardedSection sec : formEditorSupport.getGuardedSectionManager().getGuardedSections()) {
            if (sec instanceof InteriorSection) {
                sectionsCount++;
                int pos = sec.getEndPosition().getOffset();
                if (pos > endPos) {
                    endPos = pos;
                }
            }
        }
        if (sectionsCount == 0) {
            int docLength = formEditorSupport.getDocument().getLength();
            endPos = docLength > 0 ? docLength - 1 : 0;
        }
        // if there is another guarded section following with no gap, insert empty line (#109242)
        for (GuardedSection sec : formEditorSupport.getGuardedSectionManager().getGuardedSections()) {
            if (sec.getStartPosition().getOffset() == endPos + 1) {
                formEditorSupport.getDocument().insertString(endPos + 1, "\n", null); // NOI18N
                break;
            }
        }
        // create the new guarded section
        return formEditorSupport.getGuardedSectionManager().createInteriorSection(
                formEditorSupport.getDocument().createPosition(endPos + 1),
                getEventSectionName(handlerName));
    }

    // other
    private String getEventSectionName(String handlerName) {
        return EVT_SECTION_PREFIX + handlerName;
    }

    private String[] generateListenerMethodHeader(String methodName,
            Method aMethod,
            Writer writer)
            throws IOException {
        Class<?>[] parametersTypes = aMethod.getParameterTypes();
        String[] parametersNames;

        if (parametersTypes.length == 1) {
            if (columnSelectValueMethod.equals(aMethod) || selectValueMethod.equals(aMethod)) {
                parametersNames = new String[]{"aEditor"};
            } else {
                parametersNames = new String[]{EVT_VARIABLE_NAME};
            }
            /*
             } else if (handleCellMethod.equals(aMethod)) {
             parametersNames = new String[]{"aId", "aColumnId", "aCell", "aObj"};
             } else if (selectValueMethod.equals(aMethod)) {
             parametersNames = new String[]{"aEditor"};
             } else if (columnHandleValueMethod.equals(aMethod)) {
             parametersNames = new String[]{"aId", "aColumnId", "aCell", "aObj"};
             } else if (columnSelectValueMethod.equals(aMethod)) {
             parametersNames = new String[]{"aEditor"};
             } else if (handleValueMethod.equals(aMethod)) {
             parametersNames = new String[]{"aId", "aCell", "aObj"};
             */
        } else {
            parametersNames = new String[parametersTypes.length];
            for (int i = 0; i < parametersTypes.length; i++) {
                parametersNames[i] = "param" + i; // NOI18N
            }
        }
        // generate the method
        writer.write("function "); // NOI18N
        writer.write(methodName != null ? methodName : aMethod.getName());
        writer.write("("); // NOI18N

        for (int i = 0; i < parametersTypes.length; i++) {
            writer.write(parametersNames[i]);
            if (i + 1 < parametersTypes.length) {
                writer.write(", "); // NOI18N
            }
        }
        writer.write(")"); // NOI18N
        writer.write(" {\n"); // NOI18N
        return parametersNames;
    }

    /**
     * Focuses the specified event handler in the editor.
     */
    private void gotoEventHandler(String handlerName) {
        InteriorSection sec = getEventHandlerSection(handlerName);
        if (sec != null && initialized) {
            formEditorSupport.openAt(sec.getCaretPosition());
        }
    }

    /**
     * Gets the body (text) of event handler of given name.
     */
    public String getEventHandlerText(String handlerName) {
        InteriorSection section = getEventHandlerSection(handlerName);
        return (section == null) ? null : section.getBody();
    }

    private String getEventHandlerAnnotation(String handlerName, boolean removeAnnotations) {
        return null;
    }

    // ------------------------------------------------------------------------------------------
    // Private methods
    /**
     * Clears undo buffer after code generation
     */
    private void clearUndo() {
        formEditorSupport.discardEditorUndoableEdits();
    }

    // sections acquirement
    private InteriorSection getEventHandlerSection(String handlerName) {
        return formEditorSupport.getGuardedSectionManager().findInteriorSection(getEventSectionName(handlerName));
    }

    //
    // {{{ FormListener
    //
    private class FormListener implements FormModelListener {

        @Override
        public void formChanged(FormModelEvent[] events) {
            if (events != null) {
                for (int i = 0; i < events.length; i++) {
                    FormModelEvent ev = events[i];

                    if (ev.getChangeType() == FormModelEvent.EVENT_HANDLER_ADDED) {
                        String handlerName = ev.getEventHandler();
                        String bodyText = ev.getNewEventHandlerContent();
                        String annotationText = ev.getNewEventHandlerAnnotation();
                        if ((ev.getCreatedDeleted() || bodyText != null) && ev.getComponent().isInModel()) {
                            if (!ev.getCreatedDeleted()) {
                                ev.setOldEventHandlerContent(getEventHandlerText(handlerName));
                            }
                            generateEventHandler(handlerName,
                                    (ev.getComponentEvent() == null)
                                    ? formModel.getFormEvents().getOriginalListenerMethod(handlerName)
                                    : ev.getComponentEvent().getListenerMethod(),
                                    bodyText, annotationText);
                        }
                        if (events.length == 1 && bodyText == null) {
                            gotoEventHandler(handlerName);
                        }
                    } else if (ev.getChangeType() == FormModelEvent.EVENT_HANDLER_REMOVED) {
                        if (ev.getCreatedDeleted()) {
                            String handlerName = ev.getEventHandler();
                            ev.setOldEventHandlerContent(getEventHandlerText(handlerName));
                            ev.setOldEventHandlerAnnotation(getEventHandlerAnnotation(handlerName, true));
                            deleteEventHandler(handlerName);
                        }
                    } else if (ev.getChangeType() == FormModelEvent.EVENT_HANDLER_RENAMED) {
                        renameEventHandler(ev.getOldEventHandler(),
                                ev.getNewEventHandler());
                    } else if (ev.getChangeType() == FormModelEvent.FORM_TO_BE_SAVED) {
                    } else if (ev.getChangeType() == FormModelEvent.FORM_TO_BE_CLOSED) {
                    }
                }
            }
        }
    }
}
