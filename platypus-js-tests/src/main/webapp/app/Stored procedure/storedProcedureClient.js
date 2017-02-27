/**
 * 
 * @author mg
 */
function StoredProcedureTestClient() {

    this.execute = function (aOnSuccess, aOnFailure) {
        var proxy = new P.ServerModule('StoredProcedureCallerTest');
        proxy.execute(function () {
            aOnSuccess();
        },function () {
            aOnFailure();
        });
    };
}