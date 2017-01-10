/**
 * 
 * @author mg
 * @name uploadTest
 */
function uploadTest() {

    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    var file;
    var loading;

    form.btnSelectFile.onActionPerformed = function (event) {
        P.selectFile(function (aFile) {
            file = aFile;
            P.Logger.info(file);
            P.Logger.info(file.name);
            form.lblFileName.text = file.name;
        });
        
    };
    
    form.btnSelectFileMask.onActionPerformed = function (event) {
        P.selectFile(function (aFile) {
            file = aFile;
            P.Logger.info(file);
            P.Logger.info(file.name);
            form.lblFileName.text = file.name;
        },form.txtMask.text);
        
    };


    form.btnStartUpload.onActionPerformed = function (event) {
        if (loading == null) {
            if (file != null) {
                loading = P.Resource.upload(file,file.name,
                        function (aUrl) {
                            loading = null;
                            form.progressBar.value = 0;
                            form.progressBar.text = "";
                            P.Logger.info(aUrl);
                            alert("File is uploaded successfully and accessible at:\n" + aUrl);
                        },
                        function (aEvent) {
                            if (loading != null) {
                                form.progressBar.value = aEvent.loaded / aEvent.total * 100;
                                form.progressBar.text = form.progressBar.value + "%";
                            }
                        },
                        function (aError) {
                            loading = null;
                            form.progressBar.value = 0;
                            form.progressBar.text = "";
                            alert("Uploading is aborted with message: " + aError);
                        }
                );
            } else
                alert("Select a file please...");
        } else
            alert("Wait please while current upload ends!");
    };

    form.btnAbort.onActionPerformed = function (event) {
        if (loading != null)
            loading.abort();
    };


}