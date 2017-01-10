require('Dependencies/AMD self require', function (selfTest) {
    if (!selfTest)
        throw 'require("slef-test") violation';
    if (selfTest.self !== 'test')
        throw 'selfTest.self violation';
    AMDSelfTest.onAMDSelfTestRequired();
});
define(function () {
    return {self: 'test'};
});