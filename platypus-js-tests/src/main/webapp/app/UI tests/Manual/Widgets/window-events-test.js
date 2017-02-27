/**
 * 
 * @author mg
 */
function window_events_test() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };

    form.onWindowActivated = function(event) {
        P.Logger.info("windowActivated on " + event.source);
    };
    form.onWindowDeactivated = function(event) {
        P.Logger.info("windowDeactivated on " + event.source);
    };
    form.onWindowClosing = function(event) {
        P.Logger.info("windowClosing on " + event.source);
        return confirm("Really close the window?");
    };
    form.onWindowClosed = function(event) {
        P.Logger.info("windowClosed on " + event.source);
    };
    form.onWindowMaximized = function(event) {
        P.Logger.info("windowMaximized on " + event.source);
    };
    form.onWindowMinimized = function(event) {
        P.Logger.info("windowMinimized on " + event.source);
    };
    form.onWindowRestored = function(event) {
        P.Logger.info("windowRestored on " + event.source);
    };
    form.onWindowOpened = function(event) {
        P.Logger.info("windowOpened on " + event.source);
    };
    form.view.onComponentResized = function(event) {
        P.Logger.info("view resized on " + event.source.name);
    };


}
