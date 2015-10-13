if (!this.P) {
    /** 
     * Platypus library namespace global variable.
     * @namespace P
     */
    var P;
    P.HTML5 = "";
    P.J2SE = "";
    P.agent = "";

    /**
     * Dependencies resolver function.
     * Performs automatic dependencies resolving of modules mentioned in first parameter.
     * Can be used as synchronous or asynchronous dependencies resolver.
     * Module names may be platypus module name, e.g. 'Calculator'.
     * Platypus modules names do not depend on location of modules' files in project. Platypus modules names are global script names.
     * Module names may be plain scripts names in the following form: libs/leaflet.js from the root of the project's source.
     * Platypus project's source root is the "app" folder. Relative paths to plain scripts are not supported.
     * 
     * @param {Array} aDeps Array of modules names or a single module name.
     * @param {Function} aOnSuccess Success callback. Optional. If omitted, synchronous version of code will be used.
     * @param {Function} aOnFailure Failure callback. Optional. If omitted, no error information will be provided.
     * @returns {undefined}
     */
    P.require = function (aDeps, aOnSuccess, aOnFailure) {
    };
    /**
     * Classic js extend function.
     * @param {Function} aChild
     * @param {Function} aParent
     * @returns {undefined}
     */
    P.extend = function (aChild, aParent) {
    };

    /**
     * Loads entities definitions for ORM parts of a data model (entity manager) from server.
     * Caches loaded information. No additional network activity will take place, if definitions of
     * entities were already loaded. If entities already were loaded
     * while dependencies resolution process, initiated with P.require() function,
     * They will be accessible and there is no necessity to load them manually.
     * @param {type} aEntities Names of entities, definitions will be loaded for.
     * It may be a single entity name or an array of entities names or a model definition xml string.
     * If it will be a model definition xml string, P.requireEntities() will find entities names automatically.
     * @param {type} aOnSuccess Success callback. If omitted, than in JavaSE environment, synchrionous call will be performed.
     * @param {type} aOnFailure Failure callback. Accepts information about any problem occured while loading.
     * @returns {undefined}
     */
    P.requireEntities = function (aEntities, aOnSuccess, aOnFailure) {
    };
    /**
     * Loads sever modules methods lists from server.
     * Such information is used while RPC proxy generation by P.ServerModule constructor.
     * It caches loaded information, and so, no additional network activity will take place, if methods lists were already loaded.
     * @param {type} aRemotesNames Names of server modules, methods lists will be loaded for.
     * If methods lists already were loaded while dependencies resolution process, initiated with P.require() function,
     * They will be accessible and there is no necessity to load them manually.
     * It mey be a single string or an array of strings.
     * @param {type} aOnSuccess Success callback. If omitted, than in JavaSE environment, synchrionous call will be performed.
     * @param {type} aOnFailure Failure callback. Accepts information about any problem occured while loading.
     * @returns {undefined} 
     */
    P.requireRemotes = function (aRemotesNames, aOnSuccess, aOnFailure) {
    };
    /**
     * Parses *.model files and creates data model (entity manager) of a module.
     * @param aModuleName Name of the module, the data model will be loaded for.
     * @returns Model instance.
     */
    P.loadModel = function (aModuleName) {
        return {};
    };
    /**
     * Parses a string with xml definition of data model (entity manager).
     * Returns data model instance ready for use as entity manager.
     * Note! It does not resolve entities dependencies. For entities resolution
     * use <code>P.requireEntities()</code> function please. If entities already were loaded
     * while dependencies resolution process, initiated with P.require() function,
     * They will be accessible and there is no necessity to load them manually.
     * @param aContent String with xml definition of data model (entity manager).
     * @returns Model instance.
     */
    P.readModel = function (aContent) {
        return {};
    };
    /**
     * Parses *.layout files and creates a view of a form module.
     * @param aModuleName Name of the module, the form will be loaded for.
     * @param aData Script data object, model widgets to be bound to. Currently is may be only data model.
     * @returns P.Form instance.
     */
    P.loadForm = function (aModuleName, aData) {
    };
    /**
     * Parses a string with xml definition of a form and creates a view .
     * @param aContent String with xml definition of a form.
     * @param aData Script data object, model widgets to be bound to. Currently is may be only data model.
     * @returns P.Form instance.
     */
    P.readForm = function (aContent, aData) {
    };
    /**
     * Parses *.xlsx files and creates report template of a module if it is a report module.
     * @param aModuleName Name of the module, the template will be loaded for.
     * @param aData Script data object, that will be used while report generating.
     * @returns P.ReportTemplate instance.
     */
    P.loadTemplate = function (aModuleName, aData) {
    };
    /**
     * Consists of show forms.
     * @type Array
     */
    P.Form.shown = [];
    /**
     * Fast scan of P.Form.shown.
     * @param aFormKey form key value.
     * @returns {P.Form} instance
     */
    P.Form.getShownForm = function (aFormKey) {
    };
    /**
     * P.Form.shown change handler.
     * @returns {Function}
     */
    P.Form.onChange = function () {
    };
    /**
     * Constructs server module network proxy.
     * @constructor
     */
    P.ServerModule = function () {
    };
    /**
     * Utility class with resource load/upload/location methods.
     */
    P.Resource = {
        /**
         * Loads some plain resource from project or from network via http.
         * @param {String} aResName Platypus project's reource name e.g. "some-project-folder/some-resource.sr" or http url of the resource.
         * @param {Function} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
         * @param {Function} onFailure Failure callback. Optional. If omitted no error information will provided.
         * @returns Loaded content if synchronous version of code is used or null otherwise.
         */
        load: function (aResName, onSuccess, onFailure) {
        },
        /**
         * Points to application source root directory "app" in local filesystem.
         * To make full absolute path to some platypus project's resource in local filesystem, 
         * one should write such code: <code> var fullPath = P.applicationPath + "/" + "some-project-folder/some-resource.sr"</code>
         * Note, that folders separator char may vary in various operating systems.
         * @type String
         */
        applicationPath: "",
        /**
         * Available only in browser environment.
         * @param {Object} aFile
         * @param {String} aName
         * @param {Function} aCompleteCallback
         * @param {Function} aProgressCallback
         * @param {Function} aAbortCallback
         * @returns {undefined}
         */
        upload: function (aFile, aName, aCompleteCallback, aProgressCallback, aAbortCallback) {
        }
    };
    /**
     * Invalidates user's session at the connected server.
     * Available only in client environment.
     * In server environment does nothing.
     * @param {Function} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
     * @param {Function} onFailure Failure callback. Optional. If omitted no error information will provided.
     * @returns {undefined}
     */
    P.logout = function (onSuccess, onFailure) {
    };
    /**
     * Current logged in principal. ay system principal if called in resident module code context.
     */
    P.principal = {
        name: ""
    };
    /**
     * Utility class to maange icons and theirs underlying data.
     */
    P.Icon = {
        /**
         * Loads an icon from platypus project's plain resource or from a network via http.
         * @param {type} aResName Platypus project's reource name e.g. "some-project-folder/some-icon.png" or http url of the image.
         * @param {type} onSuccess Success callback. Optional. If omitted sycnhronous version of code will be used.
         * @param {type} onFailure Failure callback. Optional. If omitted no error information will provided.
         * @returns Loaded icon if synchronous version of code is used or null otherwise.
         * @returns {undefined}
         */
        load: function (aResName, onSuccess, onFailure) {
        }
    };
    /**
     * Id generator.
     * @type 
     */
    P.ID = {
        /**
         * Generates an id and returns it as a string comprised of numbers.
         * @returns {String} Generated id string
         */
        generate: function () {
            return "";
        }
    };

    /**
     * Md5 hash generator
     * @type type
     */
    P.MD5 = {
        /**
         * Generates MD5 hash for given value. 
         * @param aValue Value the hash is generated for. Converted to string.
         * @return Generated MD5 hash
         */
        generate: function (aValue) {
            return "";
        }
    };
    P.Logger = {};
    P.VK_ALT = 0;
    P.VK_BACKSPACE = 0;
    P.VK_DELETE = 0;
    P.VK_DOWN = 0;
    P.VK_END = 0;
    P.VK_ENTER = 0;
    P.VK_ESCAPE = 0;
    P.VK_HOME = 0;
    P.VK_LEFT = 0;
    P.VK_PAGEDOWN = 0;
    P.VK_PAGEUP = 0;
    P.VK_RIGHT = 0;
    P.VK_SHIFT = 0;
    P.VK_TAB = 0;
    P.VK_UP = 0;
    P.selectFile = function () {
    };
    P.selectDirectory = function () {
    };
    P.selectColor = function () {
    };
    P.readString = function () {
    };
    P.writeString = function () {
    };
    P.msgBox = function () {
    };
    P.warn = function () {
    };
    P.prompt = function () {
    };
    P.HorizontalPosition = {};
    P.VerticalPosition = {};
    P.Orientation = {};
    P.FontStyle = {};
    /**
     * Utility function, that allows some application code to become asynchronous in terms of platypus async io model.
     * WARNING!!! When one uses this function, aWorkerFunc will be run in a separate thread.
     * So make sure, that there are no any shared data will be used by aWorkerFunc's code.      
     * This method intended generally for input/output libraries writers.
     * The callbacks provided to the async function are guaranteed to be called according to Platypus.js parallelism levels.
     * @param {Function} aWorkerFunc A function with application logic, input, output, etc.
     * @param {Function} aOnSuccess Success callback to be called when aWorkerFunc will complete its work.
     * @param {Function} aOnFailure Failure callback to be called while aWorkerFunc will raise an exception.
     * @returns {undefined}
     */
    P.async = function (aWorkerFunc, aOnSuccess, aOnFailure) {
    };
    /**
     * Posts an execution request into browser's event loop.
     * @param {Function} aCallback
     * @returns {undefined}
     */
    P.invokeLater = function (aCallback) {
    };
    /**
     * Utilizes browser's setTimeput in a safe way to calls aCallback after some period of time.
     * @param {Number} aTimeout Period of time to delay.
     * @param {Function} aCallback
     * @returns {undefined}
     */
    P.invokeDelayed = function (aTimeout, aCallback) {
    };
}
