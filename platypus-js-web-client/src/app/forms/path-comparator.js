/* global Infinity */
define([
    './bound'
], function (
        Bound
        ) {
    function PathComparator(field, ascending) {
        if(arguments.length < 2)
            ascending = true;
        Object.defineProperty(this, 'ascending', {
            get: function(){
                return ascending;
            }
        });
        function oCompare(od1, od2){
            if(od1 == null && od2 == null)
                return 0;
            else if(od1 == null)
                return 1;
            else if(od2 == null)
                return -1;
            if(typeof od1 === 'string')
                od1 = od1.toLowerCase();	
            if(typeof od2 === 'string')
                od2 = od2.toLowerCase();	
            if(od1 === od2)
                return 0;
            else if(od1 > od2)
                return 1;
            else
                return -1;
        }

        function compare(o1, o2) {
            var oData1 = Bound.getPathData(o1, field);
            var oData2 = Bound.getPathData(o2, field);
            var res = oCompare(oData1, oData2);
            return ascending ? res : -res;
        }
        Object.defineProperty(this, 'compare', {
            get: function(){
                return compare;
            }
        });
    }
    return PathComparator;
});