/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
P.Logger.info('ftest passed');
P.require('../btest.js', function () {
}, function (e) {
    P.Logger.severe(e);
});

