define(['core/report', 'reports/report-template'], function (Report, ReportTemplate) {
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    var FileUtils = Java.type("com.eas.util.FileUtils");

    /**
     * Locates a prefetched resource by module name and reads a from from it.
     * Can be used only in server environment.
     * @param {String} aModuleName Name of module with prefetched resource (*xls or *.xlsx file)
     * @param {Object} aData Object with data to be rendered while report generation.
     * @param {Object} aTarget Object to be used as report template properties and methods host. Defaulrs to new Object(). (Optional). Deprecated. 
     * @returns {ReportTemplate}
     */
    function loadTemplate(aModuleName, aData, aTarget) {
        var file = FileUtils.findBrother(ScriptedResourceClass.getApp().getModules().nameToFile(aModuleName), "xlsx");
        if (!file) {
            file = FileUtils.findBrother(ScriptedResourceClass.getApp().getModules().nameToFile(aModuleName), "xls");
        }
        if (file) {
            var nameTemplate = aModuleName;
            var lastSlash = nameTemplate.lastIndexOf('/');
            if (lastSlash !== -1) {
                nameTemplate = nameTemplate.substring(lastSlash + 1, nameTemplate.length);
            }
            var reportConfig = ScriptedResourceClass.getApp().getReports().get(nameTemplate, file);
            if (aTarget) {
                ReportTemplate.call(aTarget, reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            } else {
                aTarget = new ReportTemplate(reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            }
            return aTarget;
        } else {
            throw "Report template '" + aModuleName + "' missing.";
        }
    }
    return loadTemplate;
});