/* global Java*/
define(['boxing', 'forms/form', 'forms/index', 'grid/index'], function (B, Form) {
    // core imports
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var Source2XmlDom = Java.type('com.eas.xml.dom.Source2XmlDom');
    // gui imports
    var FormLoaderClass = Java.type('com.eas.client.scripts.ModelFormLoader');

    function loadFormDocument(aDocument, aModel, aTarget) {
        var formFactory = FormLoaderClass.load(aDocument, ScriptedResourceClass.getApp(), arguments[1] ? aModel : null);
        var form = formFactory.form;
        if (aTarget) {
            Form.call(aTarget, null, null, form);
        } else {
            aTarget = new Form(null, null, form);
        }
        form.injectPublished(aTarget);
        var comps = formFactory.getWidgetsList();
        for (var c = 0; c < comps.length; c++) {
            (function () {
                var comp = EngineUtilsClass.unwrap(B.boxAsJs(comps[c]));
                if (comp.name) {
                    Object.defineProperty(aTarget, comp.name, {
                        get: function () {
                            return comp;
                        }
                    });
                }
            })();
        }
        return aTarget;
    }
    function loadForm(aName, aModel, aTarget) {
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        var document = ScriptedResourceClass.getApp().getForms().get(aName, files);
        var form = loadFormDocument(document, aModel, aTarget);
        if (!form.title)
            form.title = aName;
        form.formKey = aName;
        return form;
    }

    function readForm(aContent, aModel, aTarget) {
        var document = Source2XmlDom.transform(aContent);
        return loadFormDocument(document, aModel, aTarget);
    }

    var module = {};
    Object.defineProperty(module, 'loadForm', {
        enumerable: true,
        value: loadForm
    });
    Object.defineProperty(module, 'readForm', {
        enumerable: true,
        value: readForm
    });
    return module;
});