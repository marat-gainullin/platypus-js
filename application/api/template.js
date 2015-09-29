define(['core/report', 'reports/report-template'], function(Report, ReportTemplate){
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.ScriptedResource");
    
    return function (aName, aData, aTarget) {
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
    };
});