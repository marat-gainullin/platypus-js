/* global expect */

function GlobalA() {
}

(function () {
    expect(GlobalB).toBeDefined();
    var data = GlobalB();
    expect(data).toEqual('GlobalB');
}());