/**
 * @name AttainabilityTestView
 */

function AttainabilityTestView() {

    var self = this
            , model = P.loadModel(this.constructor.name)
            , form = P.loadForm(this.constructor.name, model);
            
    self.show = function() {
        form.show();
    };

function ds11OnRequeried(evt) {//GEN-FIRST:event_ds11OnRequeried
        P.Logger.info("ds11OnRequeried");
}//GEN-LAST:event_ds11OnRequeried

function ds11OnFiltered(evt) {//GEN-FIRST:event_ds11OnFiltered
        P.Logger.info("ds11OnFiltered");
}//GEN-LAST:event_ds11OnFiltered

function ds21OnRequeried(evt) {//GEN-FIRST:event_ds21OnRequeried
        P.Logger.info("ds21OnRequeried");
}//GEN-LAST:event_ds21OnRequeried

function ds21OnFiltered(evt) {//GEN-FIRST:event_ds21OnFiltered
        P.Logger.info("ds21OnFiltered");
}//GEN-LAST:event_ds21OnFiltered

function ds22OnRequeried(evt) {//GEN-FIRST:event_ds22OnRequeried
        P.Logger.info("ds22OnRequeried");
}//GEN-LAST:event_ds22OnRequeried

function ds22OnFiltered(evt) {//GEN-FIRST:event_ds22OnFiltered
        P.Logger.info("ds22OnFiltered");
}//GEN-LAST:event_ds22OnFiltered

function ds31OnRequeried(evt) {//GEN-FIRST:event_ds31OnRequeried
        P.Logger.info("ds31OnRequeried");
}//GEN-LAST:event_ds31OnRequeried

function ds31OnFiltered(evt) {//GEN-FIRST:event_ds31OnFiltered
        P.Logger.info("ds31OnFiltered");
}//GEN-LAST:event_ds31OnFiltered

function ds32OnRequeried(evt) {//GEN-FIRST:event_ds32OnRequeried
        P.Logger.info("ds32OnRequeried");
}//GEN-LAST:event_ds32OnRequeried

function ds32OnFiltered(evt) {//GEN-FIRST:event_ds32OnFiltered
        P.Logger.info("ds32OnFiltered");
}//GEN-LAST:event_ds32OnFiltered

function ds33OnRequeried(evt) {//GEN-FIRST:event_ds33OnRequeried
        P.Logger.info("ds33OnRequeried");
}//GEN-LAST:event_ds33OnRequeried

function ds33OnFiltered(evt) {//GEN-FIRST:event_ds33OnFiltered
        P.Logger.info("ds33OnFiltered");
}//GEN-LAST:event_ds33OnFiltered

function ds41OnFiltered(evt) {//GEN-FIRST:event_ds41OnFiltered
        P.Logger.info("ds41OnFiltered");
}//GEN-LAST:event_ds41OnFiltered

function ds41OnRequeried(evt) {//GEN-FIRST:event_ds41OnRequeried
        P.Logger.info("ds41OnRequeried");
}//GEN-LAST:event_ds41OnRequeried

    form.btnModelRequery.onActionPerformed = function(evt) {
            model.requery(function() {
                P.Logger.info("model.requery succeded");
            }, function(aError) {
                P.Logger.info("model.requery failed: " + aError);
            });
            //Logger.info("model.requery succeded");    
    }

    form.btnRequery1.onActionPerformed = function(evt) {
    }

    form.btnEntityRequery.onActionPerformed = function(evt) {
            model.ds21.requery(function() {
                P.Logger.info("ds21.requery Succeded");
            }, function(aError) {
                P.Logger.info("ds21.requery Failed: " + aError);
            });
            //Logger.info("ds21.requery Succeded");
    }

    form.btnModelExecute.onActionPerformed = function(evt) {
            model.execute(function() {
                P.Logger.info("model.execute succeded");
            }, function(aError) {
                P.Logger.info("model.execute failed: " + aError);
            });
    }

    form.btnEntityExecute.onActionPerformed = function(evt) {
            model.ds21.execute(function() {
                P.Logger.info("ds21.execute Succeded");
            }, function(aError) {
                P.Logger.info("ds21.execute Failed: " + aError);
            });
    }

}