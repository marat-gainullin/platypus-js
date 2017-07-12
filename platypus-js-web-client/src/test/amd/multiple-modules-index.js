/* global define */

define.amd['modules-index'] = {
    "multiple-modules.js": {
        "modules":[
            "short-name-m1",
            "short-name-m2"
        ]
        //"prefetched":[],
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "prefetched/a.js": {
        "modules":[
            "prefetched/a"
        ],
        "prefetched":[
            "prefetched/a.model",
            "prefetched/a.layout"
        ]
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "global-dependencies/a.js": {
        "modules": [
            "GlobalA"
        ],
        //"prefetched":[],
        "global-deps":[
            "GlobalB"            
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "global-dependencies/b.js": {
        "modules": [
            "GlobalB"
        ]
        //"prefetched":[],
        //"global-deps":[],
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/a.js": {
        "modules": [
            "CycleA"
        ],
        //"prefetched":[],
        "global-deps":[
            "CycleB"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/b.js": {
        "modules": [
            "CycleB"
        ],
        //"prefetched":[],
        "global-deps":[
            "CycleC"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    },
    "cyclic-global-dependencies/c.js": {
        "modules": [
            "CycleC"
        ],
        //"prefetched":[],
        "global-deps":[
            "CycleA"
        ]
        //"entities":[],
        //"rpc-stubs":[]
    }
};