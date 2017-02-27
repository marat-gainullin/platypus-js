/**
 * 
 * @author mg
 * @name LeaksTest
 */
function LeaksTest() {

    var self = this;

    var tries = 0;

    function start() {
        if (tries++ < 1000) {
            var f = new FormsAPI();
            f.show();
            (function() {
                f.close();
                f = null;
                start.invokeLater();
            }).invokeLater();
        }
    }
}