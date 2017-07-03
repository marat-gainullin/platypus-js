/* global expect */
describe('', function () {
    it('Three simple modules', function (done) {
        require(['a', 'b'], function(a, b){
            expect(a).toBeDefined();
            expect(a.moduleName).toEqual('a module');
            expect(b).toBeDefined();
            expect(b.moduleName).toEqual('b module');
            done();
        });
    });
    it('Modules with relative names', function (done) {
        require(['./a', '../amd/b'], function(a, b){
            expect(a).toBeDefined();
            expect(a.moduleName).toEqual('a module');
            expect(b).toBeDefined();
            expect(b.moduleName).toEqual('b module');
            done();
        });
    });
    it('Default module name', function (done) {
        require('default-module-name/d', function(d){
            expect(d).toBeDefined();
            expect(d.moduleName).toEqual('default-module-name/d');
            done();
        });
    });
    it('Buggy module definer', function (done) {
        require('buggy-module-definer/a', function(a){
            expect(a).toBeUndefined();
            var c = require('buggy-module-definer/c');
            expect(c).toBeDefined();
            expect(c.moduleName).toEqual('buggy-module-definer/c');
            done();
        });
    });
    /*
    it('Multiple modules in one file', function (done) {
        require(['short-name-m1', 'short-name-m2'], function(snm1, snm2){
            expect(snm1).toBeDefined();
            expect(snm2).toBeDefined();
            done();
        });
    });
    it('Prefetched resources of modules', function (done) {
        require([''], function(snm1){
            expect(snm2).toBeDefined();
            done();
        });
    });
    it('Global modules client dependencies', function (done) {
        require([''], function(snm1){
            expect(snm2).toBeDefined();
            done();
        });
    });
    it('Global modules server dependencies', function (done) {
        require([''], function(snm1){
            expect(snm2).toBeDefined();
            done();
        });
    });
    it('Global modules entity dependencies', function (done) {
        require([''], function(snm1){
            expect(snm2).toBeDefined();
            done();
        });
    });
    */
});
