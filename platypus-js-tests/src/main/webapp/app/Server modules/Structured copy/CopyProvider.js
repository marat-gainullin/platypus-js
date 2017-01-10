/**
 * @public
 * @stateless 
 */
define('rpc', function (Lpc) {
    var dateSample = new Date();
    var objectSample = {n: null, u: undefined, nmb: 8, s: 'sample text', b: true, d: dateSample};
    var arraySample = [null, undefined, 8, 'sample text', objectSample, true, dateSample];
    objectSample.a = arraySample;
    var tests = [
        {data: null, verify: function (aCopy) {
                return aCopy === null;
            }},
        {data: undefined, verify: function (aCopy) {
                return aCopy === null;
            }},
        {data: '', verify: function (aCopy) {
                return aCopy === '';
            }},
        {data: 8, verify: function (aCopy) {
                return aCopy === 8;
            }},
        {data: 'sample text', verify: function (aCopy) {
                return aCopy === 'sample text';
            }},
        {data: true, verify: function (aCopy) {
                return aCopy === true;
            }},
        {data: dateSample, verify: function (aCopy) {
                return aCopy.getTime() == dateSample.getTime();
            }},
        {data: {n: null, u: undefined, nmb: 8, s: 'sample text', a: arraySample, b: true, d: dateSample}, verify: function (aCopy) {
                if(aCopy.n != null)
                    return false;
                if(aCopy.u != undefined)
                    return false;
                if(aCopy.nmb != 8)
                    return false;
                if(aCopy.s != 'sample text')
                    return false;
                if(!aCopy.a)
                    return false;
                if(!aCopy.a[4])
                    return false;
                if(!aCopy.a[4].a)
                    return false;
                if(!aCopy.a[4].a[4])
                    return false;
                if(aCopy.a !== aCopy.a[4].a)
                    return false;
                if(aCopy.a[4] !== aCopy.a[4].a[4])
                    return false;
                if(!aCopy.b)
                    return false;
                if(aCopy.d.getTime() != dateSample.getTime())
                    return false;
                return true;
        }},
        {data: [null, undefined, 8, 'sample text', objectSample, true, dateSample], verify: function (aCopy) {
                if(aCopy[0] != null)
                    return false;
                if(aCopy[1] != undefined)
                    return false;
                if(aCopy[2] != 8)
                    return false;
                if(aCopy[3] != 'sample text')
                    return false;
                if(!aCopy[4])// objectSample
                    return false;
                var objSample = aCopy[4];
                if(!objSample.a)
                    return false;
                if(!objSample.a[4])
                    return false;
                if(!objSample.a[4].a)
                    return false;
                if(!objSample.a[4].a[4])
                    return false;
                if(objSample.a[4].a !== objSample.a)
                    return false;
                if(objSample.a[4].a[4] !== objSample.a[4])
                    return false;
                if(!aCopy[5])
                    return false;
                if(aCopy[6].getTime() != dateSample.getTime())
                    return false;
                return true;
        }}
    ];
    function mc() {
        var customer = new Lpc.Proxy('Server modules/Structured copy/CopyCustomer');

        this.startCopyTest = function (aOnSuccess) {
            var completed = 0;
            function tryComplete() {
                if (++completed === tests.length) {
                    aOnSuccess();
                }
            }
            tests.forEach(function (aTest, aIndex) {
                customer.copyTest(aTest.data, function (copied) {
                    if (aTest.verify(copied)) {
                        tryComplete();
                    } else {
                        throw 'Structured copy violation ' + aIndex + '.';
                    }
                });
            });
        };
    }
    return mc;
});