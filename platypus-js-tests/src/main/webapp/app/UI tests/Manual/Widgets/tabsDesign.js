/**
 * 
 * @author mg
 */
function tabsDesign() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };
    // TODO : place your code here
    form.menuItem.onActionPerformed = function(event) {
        form.menuItem.icon = P.Icon.load('http://favicon.yandex.net/favicon/rm.tvigle.ru');
    };
}
