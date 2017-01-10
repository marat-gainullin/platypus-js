/**
 * 
 * @author mg
 */
function tabs_cards_selection_events_tests() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
    
    self.show = function() {
        form.show();
    };

    var itemSelected = function(event){
        P.Logger.info("itemSelected on " + event.source.name);
    };
    
    form.buttonGroup.onItemSelected = itemSelected;
    form.pnlCards.onItemSelected = itemSelected;
    form.tabs.onItemSelected = itemSelected;
    
    form.btnShowCard1.onActionPerformed = function(event) {
        form.pnlCards.show('card1');
    };
    
    form.btnShowCard2.onActionPerformed = function(event) {
        form.pnlCards.show('card2');
    };
    
    form.btnShowCard3.onActionPerformed = function(event) {
        form.pnlCards.show('card3');
    };
}
