/**
 * 
 * @author mg
 * @name LabelTest
 */

function LabelTest() {
    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);


    form.btutton = function (evt) {
        form.label.icon = form.label.icon != null ? null : P.Icon.load("http://us.123rf.com/400wm/400/400/abluecup/abluecup1209/abluecup120902762/15458079-arrow-run-a-man-running-along-a-line-of-arrows.jpg");
    }

    self.show = function () {
        form.show();
    };

}