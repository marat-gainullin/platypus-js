/**
 * 
 * @author mg
 * @public
 * @stateless
 */
function StoredProcedureCallerTest() {

    var self = this, model = P.loadModel(this.constructor.name);

    this.achiveProcedureResult = function (aEmpId, aSalary) {
        model.storedProcedureTest.params.emp_id = aEmpId;
        model.storedProcedureTest.params.salary = aSalary;
        model.storedProcedureTest.requery();
        return model.storedProcedureTest.params.res;
    };
    
    this.achiveFunctionResult = function (aFirst, aSecond) {
        model.storedFunctionTest.params.first = aFirst;
        model.storedFunctionTest.params.second = aSecond;
        model.storedFunctionTest.requery();
        return model.storedFunctionTest.cursor.res;        
    };
    
    this.execute = function(aOnSuccess){
        /*
        // Currently H2 Database doecn't support out parameters in stored procedures, and so, this case is currently commented out.
        var sum1 = self.achiveProcedureResult(8, 9);
        if(sum1 !== 8 + 9)
            throw 'Stored procedure sum violation 1'
        */
        var sum2 = self.achiveFunctionResult(45, 78);
        if(sum2 !== 45 + 78)
            throw 'Stored procedure sum violation 2'
        aOnSuccess();
    };
}