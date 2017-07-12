/**
 * @public
 */
define(['http-context'], function(HttpContext){
    function FormAcceptor(){
        /**
         * @gEt /test-form
         */
        this.accept = function(){
            var request = HttpContext.getRequest();
            return request.params.name + request.params.age;
        };
    }
    return FormAcceptor;
});