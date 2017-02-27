/**
 * 
 * @author user
 */
function scrollTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);

    self.show = function () {
        form.show();
    };

    // TODO : place your code here

    model.requery(function () {
        // TODO : place your code here
    });

    var pnl = new P.BorderPane();
    var lbl = new P.Label("hello");
    lbl.width = 50;
    lbl.height = 50;
//
    pnl.add(lbl);

    var scroll = new P.ScrollPane(pnl);
    var redColor = Math.round(Math.random() * 255);
    var greenColor = Math.round(Math.random() * 255);
    var blueColor = Math.round(Math.random() * 255);
    pnl.background = new P.Color(redColor, greenColor, blueColor);
    
    var scroll = new P.ScrollPane();
    redColor = Math.round(Math.random() * 255);
    greenColor = Math.round(Math.random() * 255);
    blueColor = Math.round(Math.random() * 255);
    scroll.background = new P.Color(redColor, greenColor, blueColor);
    pnl.width = 200;
    pnl.height = 200;
    scroll.width = 80;
    scroll.height = 80;
    scroll.add(pnl);
    
//    var pnl1 = new P.BorderPane();
//    var lbl1 = new P.Label("hello1");
//    lbl1.width = 50;
//    lbl1.height = 50;
//
    
    
//    pnl.add(pnl1);
    form.panel.add(scroll);

}
