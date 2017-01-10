/**
 * 
 * @author mg
 */
function design_binding_test() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function() {
        form.show();
    };

    form.modelGrid.colMDENT_NAME.onRender = function(ev) {
        if (ev.object.MDENT_TYPE > 60) {
            var s = new P.Style();
            s.background = P.Colors.CYAN;
            ev.cell.style = s;
            ev.cell.style.icon = P.Icon.load('http://favicon.yandex.net/favicon/rm.tvigle.ru');
        }
    };
    form.modelGrid.colMDENT_TYPE.onRender = function(ev) {
        if (ev.object.MDENT_TYPE > 60) {
            ev.cell.style.background = P.Colors.CYAN;   
        }
    };

    form.modelGrid.colMDENT_TYPE.onSelect = function(aEditor) {
        var selector = new ConstantSelector();
        selector.showModal(function(aSelected){
            aEditor.value = aSelected;
        });
    };

    self.showOnDesktop = function(aDesktop) {
        form.showInternalFrame(aDesktop);
    };

    self.showOn = function(aElementOrId) {
        form.view.showOn(aElementOrId);
    };
    self.showDateOn = function(aElementOrId) {
        form.modelDate.showOn(aElementOrId);
    };

    model.requery();

    form.modelSpin.onSelect = function(aEditor) {
    };

    form.btnSelectionTest.onActionPerformed = function(event) {
        var selected = form.modelGrid.selected;
        for (var s in selected) {
            P.Logger.info(selected[s].MDENT_NAME);
        }
        model.appElements.requery();
    };
    form.btnSelectAll.onActionPerformed = function(event) {
        model.appElements.forEach(function(aElement) {
            form.modelGrid.select(aElement);
        });
    };
    form.btnUnselectAll.onActionPerformed = function(event) {
        model.appElements.forEach(function(aElement) {
            form.modelGrid.unselect(aElement);
        });
    };
    form.btnMakeVisible.onActionPerformed = function(event) {
        model.appElements.forEach(function(aElement) {
            form.modelGrid.makeVisible(aElement, true);
        });
    };
    form.button.onActionPerformed = function(event) {
        form.minimize();
    };
    form.button1.onActionPerformed = function(event) {
        form.maximize();
    };
    form.button2.onActionPerformed = function(event) {
        form.restore();
    };
}
