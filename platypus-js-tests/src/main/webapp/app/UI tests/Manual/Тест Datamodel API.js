/**
 * Platypus module script.
 * @name DatamodelAPI
 */

function DatamodelAPI() {

    var self = this;

    function test_a() {
        // some rowsets
        var m = self.measurands;
        var prm = params;
        // parameters
        var p1 = self.Param1;
        p1 = params.Param1;//default row syntax
        p1 = params[0].Param1;// array-like syntax    
        // metadata
        var prmMd = md;
        prmMd = params.md;
        var p1Md = md.Param1;
        p1Md = params.md.Param1;
        p1Md = params.md[0];
        Logger.info("Test 'a' passed");
    }

    function test_u() {
        test();
        var u = self.units;
        var uMd = u.md;
        var uMdIsBasic = u.md.ISBASIC;
        uMdIsBasic = u.md[5];
        var uMdLength = u.md.length;
        Logger.info("Test 'u' passed");
        return uMdLength;
    }
    function test_b() {
        /*
         var a = self.assets;
         a.first();
         var a1Field4Value = a.FIELD4;//default row syntax
         var a1 = a.getRow(1);//table-pattern row syntax
         a1Field4Value = a1.FIELD4; 
         a1 = a[0];// array-like syntax
         a1Field4Value = a1.FIELD4; 
         var aLenth = a.length;
         
         a.insert(4, 'true');
         a.insert(4, 0.56);
         a.insert(4, false);
         a.insert(4, new Date());
         a.insert(4, null);
         */
        /*
         a.insert(a.md.FIELD4, 'true');
         a.insert(a.md.FIELD4, 0.56);
         a.insert(a.md.FIELD4, false);
         a.insert(a.md.FIELD4, new Date());
         a.insert(a.md.FIELD4, null);
         */


        /*
         var oneFieldDefDirection = self.assets.createSorting(a.md.FIELD5);
         a.sort(oneFieldDefDirection);
         var oneField = self.assets.createSorting(a.md.FIELD5, false);
         a.sort(oneField);
         var oneColIndexDefDirection = self.assets.createSorting(5);
         a.sort(oneColIndexDefDirection);
         var oneColIndex = self.assets.createSorting(5, false);
         a.sort(oneColIndex);
         a.sort(function(asset1, asset2){ return 0;});
         */

        /*
         var lengthBefore = self.assets.length;
         var f = self.assets.createFilter(self.assets.md.FIELD4, self.assets.md.FIELD6);
         f.apply("rmrjtjgicbxbdk", 877834.444);
         var lengthAfter = self.assets.length;
         if(f.isApplied())
         f.cancel();
         var lengthAfterAfter = self.assets.length;
         return [lengthBefore, lengthAfter, lengthAfterAfter].toString();
         */
        /*
         var foundIds = [];
         var loc = self.assets.createLocator(self.assets.md.FIELD4);
         var found = loc.find('fg67');
         if(found)
         {
         var i=0;
         while(loc.next())
         {
         var assetsId1 = self.assets.ID;
         var assetsRow = loc.getRow(i++);
         var assetsId2 = assetsRow.ID;
         foundIds.push(" assetsId1: "+assetsId1 + " assetsId2: "+assetsId2);           
         }
         }
         return foundIds.join("\n");
         */
        /*
         var found = self.assets.find(self.assets.md.FIELD4, 'fg67');
         var s = found.toString();
         return 'found.length: ' + found.length+' found[0]:'+found[0]+' found[0].ID:'+found[0].ID;
         */
        /*  
         var found = self.assets.findById('128015357440672');// long values can't be processed by js. Rely on rowset converter.
         return 'found.length: ' + found.length+' found[0]:'+found[0]+' found.ID:'+found.ID;
         */
        /*
         // Matrix-like syntax test
         return 'assets[0][1]: '+self.assets[0][1]+' self.assets[1][1]: '+self.assets[1][1];
         */

        /*
         // scroll to method test
         self.assets.beforeFirst();
         self.assets.scrollTo(self.assets[1]);
         return 'assets.rowIndex: ' + self.assets.rowIndex;
         */

        /*
         // substitues tests
         self.assets.substitute = self.units;   
         self.assets[0].NAME = null;
         return 'assets[0].NAME: ' + self.assets.NAME;// only at rowset's level
         */

        // Array methods tests

        /*
         var lengthBefore = self.assets.length;
         self.assets.pop();
         var lengthAfter = self.assets.length;
         self.assets.shift();
         var lengthAfter1 = self.assets.length;
         return 'lengthBefore: ' + lengthBefore + '; lengthAfter: ' + lengthAfter + '; lengthAfter1: ' + lengthAfter1;
         */
        /*
         var lengthBefore = self.assets.length;
         self.assets.push(
         {ID:57950, NAME: "Только что вставленная запись 0"},
         {ID:57951, NAME: "Только что вставленная запись 1"},
         {ID:57952, NAME: "Только что вставленная запись 2"}
         );
         var lengthAfter = self.assets.length;
         self.assets.unshift(
         {ID:57953, NAME: "Только что вставленная запись 3"},
         {ID:57954, NAME: "Только что вставленная запись 4"},
         {ID:57955, NAME: "Только что вставленная запись 5"}
         );
         var lengthAfter1 = self.assets.length;
         return 'lengthBefore: ' + lengthBefore + '; lengthAfter: ' + lengthAfter + '; lengthAfter1: ' + lengthAfter1;
         */

        /*
         var sBefore = self.assets.toString();
         self.assets.splice(0);
         var sAfter = self.assets.toString();
         return 'sBefore: '+sBefore+'; sAfter: '+sAfter;
         */

        var sBefore = self.assets.toString();
        var purged = self.assets.splice(0, self.assets.length,
                {
                    ID: 57950,
                    NAME: "Только что вставленная запись 0"
                },
        {
            ID: 57951,
            NAME: "Только что вставленная запись 1"
        },
        {
            ID: 57952,
            NAME: "Только что вставленная запись 2"
        }
        );
        var sAfter = self.assets.toString();
        return 'sBefore: ' + sBefore + '; sAfter: ' + sAfter;

        /*
         var sBefore = self.assets + "";//.toString();
         self.assets.sort(function(r1, r2){
         if(r1.NAME > r2.NAME) 
         return 1;
         else if(r1.NAME < r2.NAME)
         return -1;
         else
         return 0;
         });
         var sAfter = self.assets.toString();
         return 'sBefore: '+sBefore+'; sAfter: '+sAfter;
         */
        /*
         var concated = self.assets.concat(
         ["Только что вставленная запись 0",
         "Только что вставленная запись 1",
         "Только что вставленная запись 2"]
         );
         return concated.toString();
         */

        /*
         return self.assets.lastIndexOf(self.assets[1]) + "";
         */
        /*  
         var filtered = self.assets.filter(
         function(asset, index, arr){
         return asset.NAME == 'building1';
         });
         return 'filtered.length: '+filtered.length;
         */
        /*
         var assetsNames = "";
         self.assets.forEach(function(asset, index, arr){
         assetsNames += ", "+asset.NAME;
         });
         return assetsNames;
         */
        /*
         var res = self.assets.every(function(asset, index, arr){
         return asset.NAME == 'building1' || asset.NAME == 'building2';      
         }) + "";
         return res;
         */

        /*
         var res = self.assets.some(function(asset, index, arr){
         return asset.NAME == 'building1';      
         }) + "";
         return res;
         */
        /*
         var mapped = self.assets.map(function(asset, index, arr){
         if(asset.NAME == 'building1')
         return "b1111";
         else if (asset.NAME == 'building2')
         return "b2222";
         else
         return "unknown";
         }).join(", ");
         return mapped;
         */
        /*
         var reduced = self.assets.reduce(function(previousValue, currentValue, index, array){
         return {NAME: previousValue.NAME + " " + currentValue.NAME};
         } , {NAME: "init"}).NAME;
         return reduced;
         */
        /*
         var reduced = self.assets.reduceRight(function(previousValue, currentValue, index, array){
         return {NAME: previousValue.NAME + " " + currentValue.NAME};
         } , {NAME: "init"}).NAME;
         return reduced;
         */
    }
}