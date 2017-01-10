/**
 * 
 * @author Andrew
 * @resident
 * @constructor
 */
function Rezident() {
    var self = this, model = P.loadModel(this.constructor.name);
    
    var HashMapClass = Java.type('java.util.HashMap');
    function copy(aValue, aMapping) {
        if (!aMapping)
            aMapping = new HashMapClass();
        if (aValue === null || aValue === undefined)
            return null;
        else {
            var type = typeof aValue;
            if (type === 'number')
                return +aValue;
            else if (type === 'string')
                return aValue + '';
            else if (type === 'boolean')
                return !!aValue;
            else if (type === 'object') {
                if (aValue instanceof Date) {
                    return new Date(aValue.getTime());
                } else if (aValue instanceof Number) {
                    return +aValue;
                } else if (aValue instanceof String) {
                    return aValue + '';
                } else if (aValue instanceof Boolean) {
                    return !!aValue;
                } else {
                    var copied = aValue instanceof Array ? [] : {};
                    aMapping.put(aValue, copied);
                    for (var p in aValue) {
                        var pValue = aValue[p];
                        if (typeof pValue !== 'function') {
                            var met = aMapping.get(pValue);
                            if (met)
                                copied[p] = met;
                            else
                                copied[p] = copy(pValue, aMapping);
                        }
                    }
                    return copied;
                }
            }
        }
    }
    
    var source1 = 56;
    var source2 = 'kjhdkjd8';
    var source3 = true;
    var source4 = new Date();
    var source5 = {s: "jgjgj", n: 90, b: false, nu: null, u: undefined, a:[56, "", true, new Date()], o:{s:"wwww"}};
    source5.o.o = source5;
    
    
    var copied1 = copy(source1, null);
    var copied2 = copy(source2, null);
    var copied3 = copy(source3, null);
    var copied4 = copy(source4, null);
    var copied5 = copy(source5, null);
    
    P.Logger.info('source1: ' + JSON.stringify(source1) + '; copied1: ' + JSON.stringify(copied1));
    P.Logger.info('source2: ' + JSON.stringify(source2) + '; copied2: ' + JSON.stringify(copied2));
    P.Logger.info('source3: ' + JSON.stringify(source3) + '; copied3: ' + JSON.stringify(copied3));
    P.Logger.info('source4: ' + JSON.stringify(source4) + '; copied4: ' + JSON.stringify(copied4));
    P.Logger.info('source5.o.o === source5: ' + (source5.o.o === source5) + '; copied5.o.o === copied5: ' + (copied5.o.o === copied5));
}