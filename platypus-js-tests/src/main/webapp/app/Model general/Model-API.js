/**
 * 
 * @author mg
 * @constructor
 */
function ModelAPI() {
    var self = this, 
            model = P.loadModel(this.constructor.name);

    function testArrayLike(expectData) {
        P.Logger.info("model.testData: " + model.testData);
        P.Logger.info("model.testData.length: " + model.testData.length);
        if (expectData) {
            if (!model.testData[0] || !model.testData[1] || !model.testData[2])
                throw "model.testData[0 | 1 | 2] must present while data present";
            P.Logger.info("model.testData[0]: " + model.testData[0]);
            P.Logger.info("model.testData[1]: " + model.testData[1]);
            P.Logger.info("model.testData[2]: " + model.testData[2]);
        }
        P.Logger.info("model.testData.schema: " + model.testData.schema);
        P.Logger.info("model.testData.schema.mdent_id: " + model.testData.schema.mdent_id);
        P.Logger.info("model.testData.schema.mdent_name: " + model.testData.schema.mdent_name);
        P.Logger.info("model.testData.schema.mdent_type: " + model.testData.schema.mdent_type);
        P.Logger.info("model.testData.schema.mdent_id.name: " + model.testData.schema.mdent_id.name);
        P.Logger.info("model.testData.schema.mdent_name.name: " + model.testData.schema.mdent_name.name);
        P.Logger.info("model.testData.schema.mdent_type.name: " + model.testData.schema.mdent_type.name);
        P.Logger.info("model.testData.schema.mdent_id.pk 1: " + model.testData.schema.mdent_id.pk);
        model.testData.schema.mdent_id.pk = false;
        P.Logger.info("model.testData.schema.mdent_id.pk 2: " + model.testData.schema.mdent_id.pk);
        if (expectData) {
            if (!model.testData.cursor)
                throw "model.testData.cursor must present while data present";
            P.Logger.info("model.testData.cursor.mdent_id: " + model.testData.cursor.mdent_id);
            P.Logger.info("model.testData.cursor.mdent_name: " + model.testData.cursor.mdent_name);
            P.Logger.info("model.testData.cursor.mdent_type: " + model.testData.cursor.mdent_type);
        } else {
            if (model.testData.cursor)
                throw "model.testData.cursor must not present while data is empty";
        }
        model.testData.schema.mdent_id.pk = true;
    }

    function testScroll() {
        // Scroll interface
        var scrollCounter = 0;
        model.testData.onScroll = function (event) {
            scrollCounter++;
        };
        var scrollCounterEthalon = model.testData.length;
        for (var i = 0; i < model.testData.length; i++) {
            model.testData.cursor = model.testData[i];
            if (model.testData.cursor !== model.testData[i])
                throw "cursor vs model.testData[] failed";
        }
        model.testData.cursor = null;// before first or after last analog
        if (scrollCounter !== scrollCounterEthalon)
            throw "scrollCounter != scrollCounterEthalon";
        else
            P.Logger.info("scroll of type 2 passed");
        model.testData.onScroll = null;
    }

    function testModify(aOnSuccess) {
        // crud
        model.testData.splice(0, model.testData.length);
        if (model.testData.length > 0)
            throw 'testData delete all test failed';
        P.Logger.info('testData delete all test passed');
        model.testData.requery(function () {
            while (model.testData.length > 0) {
                model.testData.splice(0, 1);
            }
            if (model.testData.length > 0)
                throw 'testData delete row test failed';
            P.Logger.info('testData delete row test passed');
            model.testData.requery(function () {
                for (var i = model.testData.length - 1; i >= 0; i--) {
                    model.testData.splice(i, 1);
                }
                if (model.testData.length > 0)
                    throw 'testData delete all with count down test failed';
                P.Logger.info('testData delete all with count down test passed');
                model.testData.requery(function () {
                    for (var i = model.testData.length - 1; i >= 0; i--) {
                        model.testData.remove(model.testData[i]);
                    }
                    if (model.testData.length > 0)
                        throw 'testData remove(anInstance) test failed';
                    P.Logger.info('testData remove(anInstance) test passed');
                    model.testData.requery(function () {
                        while (model.testData.length > 0) {
                            model.testData.pop();
                        }
                        if (model.testData.length > 0)
                            throw 'testData.pop() test failed';
                        P.Logger.info('testData.pop() test passed');
                        model.testData.requery(function () {
                            while (model.testData.length > 0) {
                                model.testData.shift();
                            }
                            if (model.testData.length > 0)
                                throw 'testData.shift() test failed';
                            P.Logger.info('testData.shift() test passed');
                            model.testData.requery(function () {
                                // insert/push/unshift tests                                
                                model.testData.splice(0, model.testData.length);
                                if (model.testData.length > 0)
                                    throw 'testData.splice(0, model.testData.length) test failed';
                                P.Logger.info('testData.splice(0, model.testData.length) test passed');
                                model.testData.push(
                                        {mdent_id: 45, mdent_name: 'sn', mdent_type: '50'},
                                {mdent_id: '_45', mdent_name: '_sn', mdent_type: '50'});
                                if (model.testData.length !== 2)
                                    throw 'testData.push({...}) test failed 1';
                                if (model.testData[0].mdent_id !== 45 || model.testData[0].mdent_name !== 'sn' || model.testData[0].mdent_type !== '50')
                                    throw 'testData.push({...}) test failed 2';
                                P.Logger.info('testData.push({...}) test passed');
                                model.testData.pop();
                                model.testData.shift();
                                model.testData.unshift(
                                        {mdent_id: 46, mdent_name: 'sm', mdent_type: '55'},
                                {mdent_id: '_46', mdent_name: '_sm', mdent_type: '56'}
                                );
                                if (model.testData.length !== 2)
                                    throw 'testData.unshift({...}) test failed 1';
                                if (model.testData[0].mdent_id !== 46 || model.testData[0].mdent_name !== 'sm' || model.testData[0].mdent_type !== '55')
                                    throw 'testData.unshift({...}) test failed 2';
                                P.Logger.info('testData.unshift({...}) test passed');
                                model.testData.pop();
                                model.testData.shift();
                                model.testData.push({});
                                if (model.testData.length !== 1)
                                    throw 'testData.push() test failed 1';
                                if (!model.testData[0].mdent_id)
                                    throw 'testData.push() test failed 2';
                                P.Logger.info('testData.insert() test passed');
                                model.testData.shift();
                                model.testData.push({mdent_id: 42, mdent_name: 'sk', mdent_type: '60'});
                                if (model.testData.length !== 1)
                                    throw 'testData.insert(...) test failed 1';
                                if (model.testData[0].mdent_id !== 42 || model.testData[0].mdent_name !== 'sk' || model.testData[0].mdent_type !== '60')
                                    throw 'testData.insert(...) test failed 2';
                                P.Logger.info('testData.insert(...) test passed');
                                model.testData.splice(0, 0, {mdent_id: 43, mdent_name: 'ss', mdent_type: '65'});
                                if (model.testData.length !== 2)
                                    throw 'testData.splice(...) test failed 1';
                                if (model.testData[0].mdent_id !== 43 || model.testData[0].mdent_name !== 'ss' || model.testData[0].mdent_type !== '65')
                                    throw 'testData.splice(...) test failed 2';
                                P.Logger.info('testData.splice(...) test passed');
                                model.testData.splice(2, 0, {mdent_id: 44, mdent_name: 'sp', mdent_type: '66'});
                                if (model.testData.length !== 3)
                                    throw 'testData.insertAt(...) test failed 1';
                                if (model.testData[2].mdent_id !== 44 || model.testData[2].mdent_name !== 'sp' || model.testData[2].mdent_type !== '66')
                                    throw 'testData.insertAt(...) test failed 2';
                                P.Logger.info('testData.insertAt(...) test passed');
                                model.testData.cursor = model.testData[model.testData.length - 1];
                                model.testData.cursor.mdent_id = 50;
                                model.testData.cursor.mdent_name = 'sf';
                                model.testData.cursor.mdent_type = 70;
                                if (model.testData[2].mdent_id !== 50 || model.testData[2].mdent_name !== 'sf' || model.testData[2].mdent_type !== 70)
                                    throw 'testData.cursor.<...> = test failed';
                                P.Logger.info('testData.cursor.<...> = test passed');
                                model.testData[0].mdent_id = '60';
                                model.testData[0].mdent_name = 'sw';
                                model.testData[0].mdent_type = 75;
                                if (model.testData[0].mdent_id !== '60' || model.testData[0].mdent_name !== 'sw' || model.testData[0].mdent_type !== 75)
                                    throw 'testData[0].<...> = test failed';
                                P.Logger.info('testData[0].<...> = test passed');
                                testFindSort(aOnSuccess);
                            });
                        });
                    });
                });
            });
        });
    }

    function testFindSort(aOnSuccess) {
        // find by mutiple fields
        // scroll to arbitrary elements
        // three ways of sort (Array, Rowset by fields, Rowset by function)

        model.testData.requery(function () {
            var unfilteredLength = model.testData.length;
            var filtered = model.testData.filter(function (anInstance) {
                return anInstance.mdent_type == 70;
            });
            if (filtered.length === 0)
                throw 'filter no content failed.';
            var filteredLength = filtered.length;
            if (filteredLength >= unfilteredLength)
                throw 'filter test failed' + ' filteredLength:' + filteredLength + '; unfilteredLength: ' + unfilteredLength;
            var foundBadValue = false;
            for (var i = 0; i < filtered.length; i++)
                if (filtered[i].mdent_type !== 70) {
                    foundBadValue = true;
                    break;
                }
            if (foundBadValue)
                throw 'filter test failed';

            var found = model.testData.find({mdent_type: 70});
            if (found.length !== filteredLength)
                throw 'filter/find test failed';

            var unfilteredLength1 = model.testData.length;
            if (unfilteredLength1 !== unfilteredLength)
                throw 'filter test failed 1';
            P.Logger.info('filter test passed');

            var modules = model.testData.find({mdent_type: 20});
            if (!modules || !Array.isArray(modules) || modules.length === 0 || !modules[2])
                throw 'testData.find test failed';
            P.Logger.info('testData.find test passed');
            var module2 = model.testData.findByKey(modules[2].mdent_id);
            if (!module2 || module2 !== modules[2])
                throw 'testData.findByKey test failed';
            P.Logger.info('testData.findById test passed');
            model.testData.execute();
            P.Logger.info('testData.execute() test passed');
            aOnSuccess();
        });
    }

    this.execute = function (aOnSuccess) {
        var cnt = 0;
        P.Logger.info("=== test on empty data ===");
        testArrayLike();
        testScroll();
        function complete(){
            if(++cnt === 2)
                aOnSuccess();
        }
        model.testData.onRequeried = function (event) {
            model.testData.onRequeried = null;
            complete();
        };
        model.requery(function () {
            P.Logger.info("=== test on full data ===");
            if (model.testData.length === 0)
                throw 'Data for tests missing';
            testArrayLike(true);
            testScroll(true);
            testModify(complete);
        }, function (e) {
            P.Logger.severe(e);
        });
    };
}
