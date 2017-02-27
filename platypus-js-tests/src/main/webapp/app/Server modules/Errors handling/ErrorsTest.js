/**
 * 
 * @public
 */
define('ErrorsProducer', [], function () {
    var errorObj = {s: 'StringError', n: 789, b: true, dt: new Date(1458910467244), nl: null};
    function mc() {
        this.stringError = function (onSuccess, onFailure) {
            onFailure('StringError');
        };
        this.numberError = function (onSuccess, onFailure) {
            onFailure(789);
        };
        this.booleanError = function (onSuccess, onFailure) {
            onFailure(true);
        };
        this.dateError = function (onSuccess, onFailure) {
            onFailure(new Date(1458910467244));
        };
        this.nullError = function (onSuccess, onFailure) {
            onFailure(null);
        };
        this.undefinedError = function (onSuccess, onFailure) {
            onFailure(undefined);
        };
        this.objectError = function (onSuccess, onFailure) {
            onFailure(errorObj);
        };
        this.arrayError = function (onSuccess, onFailure) {
            onFailure([errorObj]);
        };
        this.thrownError = function(){
            throw 'Sample thrown error for ErrorsTest';
        };
    }
    return mc;
});