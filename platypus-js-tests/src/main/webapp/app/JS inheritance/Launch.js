/**
 * 
 * @author mg
 * @name Launch
 */

function InheritanceLaunch() {
    var self = this,
            model = P.loadModel(this.constructor.name),
            form = P.loadForm(this.constructor.name);

    // Won't work: Rabbit.prototype - readonly property, because of resource nature of Rabbit (data self.model with javascript interface)
    Animal.prototype.sum = function (a, b) {
        return a + b;
    };
    P.extend(Rabbit, Animal);
    form.btnRun.onActionPerformed = function (event) {
        var r = new Rabbit(45, 89);
        alert(r.sum(4, 5));
    };
}