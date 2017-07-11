/* global expect, GlobalA, CycleA */
describe('AMD loader tests', function () {
    it('Simple modules', function (done) {
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
    it('Absent modules', function (done) {
        require(['absent-m1', 'absent-m2'], function(am1, am2){
            fail("Absent module success callback shouldn't be called");
            done();
        }, function(reasons){
            expect(reasons).toBeDefined();
            done();
        });
    });
    it('Multiple modules in one file', function (done) {
        require(['short-name-m1', 'short-name-m2'], function(snm1, snm2){
            expect(snm1).toBeDefined();
            expect(snm2).toBeDefined();
            done();
        });
    });
    it('AMD cyclic dependencies', function (done) {
        pending('Till cycles in AMD dependencies analysis');
        require(['cyclic-amd-dependencies/a'], function(a){
            fail("Success callback of module from AMD dependencies cycle shouldn't be called");
            done();
        }, function(reasons){
            expect(reasons).toBeDefined();
            done();
        });
    });
    it('Prefetched resources of modules', function (done) {
        pending('Till tests with server');
        require(['prefetched/a'], function(pa){
            expect(pa).toBeDefined();
            expect(pa.model).toBeTruthy();
            expect(pa.layout).toBeTruthy();
            done();
        });
    });
    it('Global dependencies', function (done) {
        require(['GlobalA'], function(ga){
            expect(ga).toBeDefined();
            expect(GlobalA).toBeDefined();
            expect(ga).toEqual(GlobalA);
            done();
        });
    });
    it('Global cyclic dependencies', function (done) {
        require(['CycleA'], function(ca){
            expect(ca).toBeTruthy();
            expect(CycleA).toBeTruthy();
            done();
        });
    });
});
