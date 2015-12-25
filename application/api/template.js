define(['core/report', 'reports/report-template'], function(Report, ReportTemplate){
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    
    /**
     * Locates a prefetched resource by module name and reads a from from it.
     * Can be used only in server environment.
     * @param {String} aName Name of module with prefetched resource (*.xlsx file)
     * @param {Object} aData Object with data to be rendered while report generation.
     * @param {Object} aTarget Object to be used as report template properties and methods host. Defaulrs to new Object(). (Optional). Deprecated. 
     * @returns {ReportTemplate}
     */
    function loadTemplate(aName, aData, aTarget) {
        var files = ScriptedResourceClass.getApp().getModules().nameToFiles(aName);
        if (files) {
            var reportConfig = ScriptedResourceClass.getApp().getReports().get(aName, files);
            if (aTarget) {
                ReportTemplate.call(aTarget, reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            } else {
                aTarget = new ReportTemplate(reportConfig.getTemplateContent(), reportConfig.getNameTemplate(), reportConfig.getFormat(), aData);
            }
            return aTarget;
        } else {
            throw "Report template '" + aName + "' missing.";
        }
    }
    return loadTemplate;
});