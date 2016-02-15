/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.index;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.FormUtils.Panel;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportManager;
import com.eas.designer.application.module.index.TemplateInterceptor;
import java.util.Collection;
import java.util.regex.Pattern;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class LoadFormInterceptor extends FormInterceptor {

    private static final String LOAD_FORM_NAME = "loadForm";
    private static final Pattern PATTERN = Pattern.compile(".+\\." + LOAD_FORM_NAME);

    public LoadFormInterceptor() {
        super(LOAD_FORM_NAME);
    }

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    protected void fillJsForm(FileObject fo, ModelElementFactory factory, int loadFormOffset, JsObject jsForm) throws DataObjectNotFoundException, Exception {
        DataObject dataObject = DataObject.find(fo);
        if (dataObject instanceof PlatypusFormDataObject) {
            PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) dataObject;
            PlatypusFormSupport formContainer = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
            assert formContainer != null;
            formContainer.loadForm();
            FormModel model = formContainer.getFormModel();
            for (RADComponent<?> radComp : model.getOrderedComponentList()) {
                if (radComp.getName() != null && !radComp.getName().isEmpty()) {
                    JsObject jsWidget = factory.newObject(jsForm, radComp.getName(), new OffsetRange(loadFormOffset, loadFormOffset), false);
                    String beanCalssShortName = radComp.getBeanClass().getSimpleName();
                    String widgetModuleName;
                    if (Panel.class.getSimpleName().equals(beanCalssShortName)) {
                        RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp;
                        LayoutSupportManager layoutSupportManager = radCont.getLayoutSupport();
                        if (layoutSupportManager != null && layoutSupportManager.getLayoutDelegate() != null) {
                            Class<?> layoutedContainerClass = FormUtils.getPlatypusConainerClass(layoutSupportManager.getLayoutDelegate().getSupportedClass());
                            widgetModuleName = dashify(layoutedContainerClass.getSimpleName());
                        } else {
                            widgetModuleName = dashify(beanCalssShortName);
                        }
                    } else {
                        widgetModuleName = dashify(beanCalssShortName);
                    }
                    Collection<TypeUsage> apiWidgetTypes = TemplateInterceptor.getModuleExposedTypes(fo, loadFormOffset, factory, widgetModuleName);
                    apiWidgetTypes.stream().forEach((apiEntityType) -> {
                        jsWidget.addAssignment(apiEntityType, apiEntityType.getOffset());
                    });
                    jsForm.addProperty(jsWidget.getName(), jsWidget);
                }
            }
        }
    }

    private static String dashify(String aCamelCaseName) {
        char[] chars = aCamelCaseName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < aCamelCaseName.length(); i++) {
            sb.append(Character.toLowerCase(chars[i]));
            if (i < aCamelCaseName.length() - 1 && Character.isLowerCase(chars[i]) && Character.isUpperCase(chars[i + 1])) {
                sb.append('-');
            }
        }
        return sb.toString();
    }
}
