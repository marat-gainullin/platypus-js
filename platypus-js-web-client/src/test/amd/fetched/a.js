/* global expect, platypusjs */

define(function (aModuleName) {
    expect(window.platypusjs.getModelDocument(aModuleName)).toBeTruthy();
    expect(window.platypusjs.getFormDocument(aModuleName)).toBeTruthy();
    return {
        model: window.platypusjs.getModelDocument(aModuleName),
        layout: window.platypusjs.getFormDocument(aModuleName)
    };
});