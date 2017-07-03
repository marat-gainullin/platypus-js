/* global expect */

it('AMD Configuration', function () {
    expect(window.define).toBeDefined();
    expect(window.define.amd).toBeDefined();
    expect(window.require).toBeDefined();
    expect(window.platypusjs).toBeDefined();
    expect(window.platypusjs.config).toBeDefined();
    var config = window.platypusjs.config;
    expect(config.prefetch).toBeTruthy();
    expect(config.remoteApi).toEqual('test-remote-api');
    expect(config.apiUri).toEqual('/test-api-uri');
    expect(config.sourcePath).toEqual('/test-source-path/');
});
