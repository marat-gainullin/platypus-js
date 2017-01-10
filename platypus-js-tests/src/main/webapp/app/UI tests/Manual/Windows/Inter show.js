function LargeView() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    var pureRuntimeForm = null;

    function checkSmallForm() {
        if (!pureRuntimeForm) {
            pureRuntimeForm = new P.Form(new P.AbsolutePane());
            pureRuntimeForm.title = "Маленькая";
            pureRuntimeForm.width = 300;
            pureRuntimeForm.height = 250;
            pureRuntimeForm.onWindowActivated = function (e) {
                P.Logger.info("Window activated");
            };
            pureRuntimeForm.onWindowClosed = function (e) {
                P.Logger.info("Window closed");
            };
            pureRuntimeForm.onWindowClosing = function (e) {
                P.Logger.info("Window is closing");
                return confirm('Really close?');
            };
            pureRuntimeForm.onWindowDeactivated = function (e) {
                P.Logger.info("Window deactivated");
            };
            pureRuntimeForm.onWindowMaximized = function (e) {
                P.Logger.info("Window maximized");
            };
            pureRuntimeForm.onWindowMinimized = function (e) {
                P.Logger.info("Window minimized");
            };
            pureRuntimeForm.onWindowOpened = function (e) {
                P.Logger.info("Window opened");
            };
            pureRuntimeForm.onWindowRestored = function (e) {
                P.Logger.info("Window restored");
            };
            var btnClose = new P.Button("close");
            btnClose.onActionPerformed = function () {
                pureRuntimeForm.close();
            };
            btnClose.left = 30;
            btnClose.top = 20;
            btnClose.width = 70;
            btnClose.height = 40;
            pureRuntimeForm.view.add(btnClose);
        }
    }

    form.btnTest2.onActionPerformed = function (event) {
        checkSmallForm();
        pureRuntimeForm.showInternalFrame(self.pnlDesktop);
    };
    form.btnShowOnPanel.onActionPerformed = function (event) {
        checkSmallForm();
        pureRuntimeForm.showOnPanel(self.pnlContainer);
    };
    form.btnTest1.onActionPerformed = function (event) {
        checkSmallForm();
        if (form.cbModal1.selected){
            pureRuntimeForm.showModal();
        } else {
            pureRuntimeForm.show();
        }
    };
    form.btnMinnimize.onActionPerformed = function (event) {
        form.pnlDesktop.minimizeAll();
    };
    form.btnRestore.onActionPerformed = function (event) {
        form.pnlDesktop.restoreAll();
    };
    form.btnMaximize.onActionPerformed = function (event) {
        form.pnlDesktop.maximizeAll();
    };
    form.button.onActionPerformed = function (event) {
        form.pnlDesktop.closeAll();
    };
    form.button1.onActionPerformed = function (event) {
        alert(form.pnlDesktop.forms);
    };
}