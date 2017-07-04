/* global expect, platypusjs */

define(function (aModuleName) {
    expect(platypusjs.documents.has(aModuleName + '.model')).toBeTruthy();
    expect(platypusjs.documents.has(aModuleName + '.layout')).toBeTruthy();
    return {
        model: platypusjs.documents.get(aModuleName + '.model'),
        layout: platypusjs.documents.get(aModuleName + '.layout')
    };
});