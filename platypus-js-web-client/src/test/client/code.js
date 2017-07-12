/* global expect */

describe('Platypus.js AJAX requests', function () {
    it('requestCommit', function (done) {
        pending('Till data entities on server');
        done();
    });
    it('requestData', function (done) {
        pending('Till data entities on server');
        done();
    });
    it('requestLoggedInUser.success', function (done) {
        require('client', function (Client) {
            var request = Client.requestLoggedInUser(function (princiaplName) {
                expect(princiaplName).toBeDefined();
                expect(princiaplName).toContain('anonymous-');
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('requestLoggedInUser.failure', function (done) {
        require('client', function (Client) {
            var apiUri = window.platypusjs.config.apiUri;
            window.platypusjs.config.apiUri = 'absent-uri';
            try {
                var request = Client.requestLoggedInUser(function (princiapalName) {
                    fail("Invalid 'loggedInUser' request shoiuld lead to error");
                }, function (e) {
                    expect(e).toBeDefined();
                    done();
                });
                expect(request).toBeDefined();
                expect(request.cancel).toBeDefined();
            } finally {
                window.platypusjs.config.apiUri = apiUri;
            }
        });
    });
    it('requestLogout.success', function (done) {
        require('client', function (Client) {
            var request = Client.requestLogout(function (xhr) {
                expect(xhr).toBeDefined();
                expect(xhr.status).toEqual(200);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('requestLogout.failure', function (done) {
        require('client', function (Client) {
            var apiUri = window.platypusjs.config.apiUri;
            window.platypusjs.config.apiUri = 'absent-uri';
            try {
                var request = Client.requestLogout(function (xhr) {
                    fail("Invalid 'logout' request shoiuld lead to error");
                }, function (e) {
                    expect(e).toBeDefined();
                    done();
                });
                expect(request).toBeDefined();
                expect(request.cancel).toBeDefined();
            } finally {
                window.platypusjs.config.apiUri = apiUri;
            }
        });
    });
    it('requestServerMethodExecution.success', function (done) {
        require('client', function (Client) {
            var request = Client.requestServerMethodExecution('assets/server-modules/test-sever-module', 'echo', [
                JSON.stringify(0),
                JSON.stringify(1),
                JSON.stringify(2),
                JSON.stringify(3)], function (echo) {
                expect(echo).toBeDefined();
                expect(JSON.parse(echo)).toEqual('0 - 1 - 2 - 3');
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('requestServerMethodExecution.failure', function (done) {
        require('client', function (Client) {
            var request = Client.requestServerMethodExecution('absent-sever-module', 'echo', [
                JSON.stringify(0),
                JSON.stringify(1),
                JSON.stringify(2),
                JSON.stringify(3)], function (echo) {
                fail('absent server module shuold request should lead to an error');
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('submitForm.success', function (done) {
        require(['client', 'internals'], function (Client, Utils) {
            var request = Client.submitForm(Utils.remoteApi() + window.platypusjs.config.apiUri + '/test-form', 'get', {
                name: 'Jane',
                age: 22
            }, function (xhr) {
                expect(xhr).toBeDefined();
                expect(xhr.responseText).toBeDefined();
                expect(JSON.parse(xhr.responseText)).toEqual('Jane22');
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('submitForm.failure', function (done) {
        require(['client', 'internals'], function (Client, Utils) {
            var request = Client.submitForm(Utils.remoteApi() + window.platypusjs.config.apiUri + '/absent-form', 'get', {
                name: 'Jane',
                age: 22
            }, function (xhr) {
                fail('Form submission to absent endpoint should lead to error');
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('loadText.abs.success', function (done) {
        require('resource', function (Resource) {
            var request = Resource.loadText('assets/text-content.xml', function (loaded) {
                expect(loaded).toBeDefined();
                expect(loaded.length).toBeDefined();
                expect(loaded.length).toEqual(59);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load text as binary.abs.success', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('assets/text-content.xml', function (buffer) {
                expect(buffer).toBeDefined();
                expect(buffer.length).toBeDefined();
                expect(buffer.length).toEqual(59);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.abs.success', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('assets/binary-content.png', function (buffer) {
                expect(buffer).toBeDefined();
                expect(buffer.length).toBeDefined();
                expect(buffer.length).toEqual(564);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.abs.failure', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('assets/absent-content.png', function (buffer) {
                fail('Loading of absent content should lea dto an error');
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.relative.success', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('../../app/assets/binary-content.png', function (buffer) {
                expect(buffer).toBeDefined();
                expect(buffer.length).toBeDefined();
                expect(buffer.length).toEqual(564);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.relative.failure', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('../../app/assets/absent-content.png', function (buffer) {
                fail('Loading of absent content should lead to an error');
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.global.success', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('http://localhost:8085/platypus-js-tests/app/assets/binary-content.png', function (buffer) {
                expect(buffer).toBeDefined();
                expect(buffer.length).toBeDefined();
                expect(buffer.length).toEqual(564);
                done();
            }, function (e) {
                fail(e);
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    it('load.global.failure', function (done) {
        require('resource', function (Resource) {
            var request = Resource.load('http://localhost:8085/platypus-js-tests/app/assets/absent-content.png', function (buffer) {
                fail('Loading of absent content should lead to an error');
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
            expect(request).toBeDefined();
            expect(request.cancel).toBeDefined();
        });
    });
    /*
     it('upload.success', function (done) {
     require(['resource', 'invoke'], function (Resource, Invoke) {
     var uploadedTotal = 0;
     var fileInput = document.createElement('input');
     fileInput.type = 'file';
     fileInput.onchange = function () {
     var file = fileInput.files[0];
     document.body.removeChild(fileInput);
     var request = Resource.upload(file, 'test-uploaded-resource.bin', function (uploaded) {
     expect(uploaded).toBeDefined();
     expect(Array.isArray(uploaded)).toBeTruthy();
     expect(uploaded.length).toEqual(1);
     expect(uploadedTotal).toBeGreaterThan(0);
     done();
     }, function (progress) {
     uploadedTotal = progress.total;
     }, function (e) {
     fail(e);
     });
     expect(request).toBeDefined();
     expect(request.cancel).toBeDefined();
     };
     document.body.appendChild(fileInput);
     Invoke.later(function(){
     fileInput.click();
     });
     });
     });
     */
});