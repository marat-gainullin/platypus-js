/**
 * Contains the basic dependencies loading.
 */
try {
    P.require('common-utils/index.js');
    print('common-utils API loaded.');
} catch (e) {
    print('common-utils API skipped.');
}

try {
    P.require('core/index.js');
    print('core API loaded.');
} catch (e) {
    print('core API skipped.');
}

try {
    P.require('datamodel/index.js');
    print('datamodel API loaded.');
} catch (e) {
    print('datamodel API skipped.');
}

try {
    P.require('forms/index.js');
    print('forms API loaded.');
} catch (e) {
    print('forms API skipped.');
}

try {
    P.require('grid/index.js');
    print('grid API loaded.');
} catch (e) {
    print('grid API skipped.');
}

try {
    P.require('reports/index.js');
    print('reports API loaded.');
} catch (e) {
    print('reports API skipped.');
}
