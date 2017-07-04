define(function () {
    function extend(Child, Parent) {
        var prevChildProto = {};
        for (var m in Child.prototype) {
            var member = Child.prototype[m];
            if (typeof member === 'function') {
                prevChildProto[m] = member;
            }
        }
        var F = function () {
        };
        F.prototype = Parent.prototype;
        Child.prototype = new F();
        for (var m in prevChildProto)
            Child.prototype[m] = prevChildProto[m];
        Child.prototype.constructor = Child;
        Child.superclass = Parent.prototype;
    }
    return extend;
});