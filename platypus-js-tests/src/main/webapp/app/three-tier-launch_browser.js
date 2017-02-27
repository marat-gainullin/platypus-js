/**
 * 
 * @author mg
 */
function ThreeTierBrowserLauncher() {
    var self = this
            , form = P.loadForm(this.constructor.name);

    self.show = function () {
        form.show();
    };

    form.btnRun.onActionPerformed = function (event) {

        var tests = [
            new select_stateless_test()
                    , new EasHRValidatorTest()
                    , new append_test()
                    , new ambigous_changes_semi_writable()
                    , new ambigous_changes()
                    , new ModelModyfiedTest()
                    , new ModelModyfiedTestClient()
                    , new extra_fields_insert_update()
                    , new SqlUpdateTestClient()
                    , new SqlExecuteUpdateTestClient()
                    , new SqlUpdateTest()
                    , new SqlEnqueueUpdateTest()
                    , new DependenciesTest()
                    , new ParallelRequireTest()
                    , new AMDSelfTest()
                    , new CreateEntityTestClient()
                    , new LoadEntityTestClient()
                    , new ModelAPI()
                    , new MultiSourceTest()
                    , new MultiSourceWithErrorTest()
                    , new ORM_Relations_Test()
                    , new ORM_properties_names_calc()
                    , new TestReportClient()
                    , new IconLoadTest()
                    , new ResourceLoadTest()
                    , new StoredProcedureTestClient()
                    , new SecureServerModulesClient()
                    , new SecureDataSourcesTest()
                    , new LPCCallbacksTest()
                    , new SyncServerModulesTest()
                    , new AsyncServerModulesTest()
                    , new HttpContextTestClient()
                    , new HttpPostTestClient()
                    , new PrincipalTestClient()
                    , new AccounterClient()
                    , new InvokeLaterDelayedClient()
                    , new InvokeLaterDelayedTest()
                    , new WebSocketTestClient()
                    , new StructuredCopyTestClient()
                    , new ErrorsTestClient()
        ];
        form.progress.minimum = 0;
        form.progress.maximum = tests.length;
        form.btnRun.enabled = false;
        form.progress.value = 0;
        form.txtLog.text = '';
        var testidx = 0;
        function performTest() {
            if (testidx < tests.length) {
                var test = tests[testidx];
                test.execute(function () {
                    form.progress.value++;
                    var msg = test.constructor.name + " - passed";
                    if (form.txtLog.text)
                        form.txtLog.text += '\n';
                    form.txtLog.text += msg;
                    P.Logger.info(msg);
                    testidx++;
                    performTest();
                });
            } else {
                form.btnRun.enabled = true;
            }
        }
        performTest();
    };
}
