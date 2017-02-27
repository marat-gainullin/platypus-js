/**
 * 
 * @author mg
 * @module
 * @constructor
 */
function FormattingTest() {
    var self = this;

self.execute = function () {
   
};


    var comps = [
        new P.FormattedField(),
        new P.HtmlArea(),
        new P.ModelCombo(),
        new P.ModelDate(),
        new P.ModelFormattedField(),
        new P.ModelSpin(),
        new P.ModelTextArea(),
        new P.PasswordField(),
        new P.ProgressBar(),
        new P.Slider(),
        new P.TextArea(),
        new P.TextField()
    ];

    for (var c in comps) {
        var comp = comps[c];
        if (comp instanceof P.ModelDate) {
            var compValue = new Date();
            comp.value = compValue;
            comp.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            comp.value = new Date(compValue.getTime() + compValue.getTimezoneOffset() * 60 * 1000);
            var ethalon = JSON.parse(JSON.stringify(compValue));
            if (comp.text !== ethalon) {
                throw 'Dates. Formatting mismatch';
            }
            comp.text = "2014-06-11T10:28:44.162Z";
            var timestampFromText = Date.parse(comp.text);
            if (comp.value.getTime() !== (comp.value.getTimezoneOffset() * 60 * 1000 + timestampFromText))
                throw 'Dates. comp.value mismatch'
            var beforeTimestamp = comp.value.getTime();
            comp.dateFormat = "yyyy-MM-dd'T'HH:mm:'Z'";
            var afterTimestamp = comp.value.getTime();
            if (beforeTimestamp !== afterTimestamp)
                throw 'Dates. format change leads to value change'
        } else if (comp instanceof P.ModelCombo) {
            continue;
        } else if (comp instanceof P.HtmlArea) {
            if (P.agent === P.HTML5) {
                comp.value = '<b>bold</b>plain';
                if (comp.text !== 'boldplain')
                    throw 'P.HtmlArea. comp.text mismatch';
            } else {
                continue;
            }
        } else if (comp instanceof P.ModelSpin) {
            var ethalon = 100.56;
            comp.value = ethalon;
            if (comp.value !== ethalon)
                throw 'ModelSpin. comp.step/value mismatch';
            comp.step = 10;
            if (comp.value !== ethalon)
                throw 'ModelSpin. comp.step/value mismatch';
            comp.step = 0.001;
            if (comp.value !== ethalon)
                throw 'ModelSpin. comp.step/value mismatch';
            comp.step = 1;
            if (comp.value !== ethalon)
                throw 'ModelSpin. comp.step/value mismatch';
            comp.step = 0.5;
            if (comp.value !== ethalon)
                throw 'ModelSpin. comp.step/value mismatch';
            if (comp.text !== '100.56')
                throw 'ModelSpin. comp.text from text mismatch';
            comp.text = '101.56';
            if (comp.value !== 101.56)
                throw 'ModelSpin. comp.step/value from text mismatch';
        } else {
            if (comp instanceof P.Slider) {
                comp.minimum = 0;
                comp.maximum = 100;
            }
            comp.value = 100;
            if (!(comp instanceof P.ProgressBar) && comp.text !== '100') {
                throw 'comp.text mismatch';
            }
        }
    }

    // Dates
    var formattingComps = [
        new P.FormattedField(),
        new P.ModelFormattedField()
    ];
    for (var c in formattingComps) {
        var comp = formattingComps[c];
        var compValue = new Date();
        comp.value = compValue;
        comp.format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        comp.value = new Date(compValue.getTime() + compValue.getTimezoneOffset() * 60 * 1000);
        var ethalon = JSON.parse(JSON.stringify(compValue));
        if (comp.text !== ethalon) {
            throw 'Dates. comp.text mismatch';
        }
        comp.text = "2014-06-11T10:28:44.162Z";
        var timestampFromText = Date.parse(comp.text);
        if (comp.value.getTime() !== (comp.value.getTimezoneOffset() * 60 * 1000 + timestampFromText)) {
            throw 'Dates. comp.value mismatch'
        }
    }
    // Booleans
    var formattingComps = [
        new P.FormattedField(),
        new P.ModelFormattedField()
    ];
    for (var c in formattingComps) {
        var comp = formattingComps[c];
        comp.value = true;
        if (comp.text !== 'true')
            throw 'Boolean component.text mismatch';
        comp.value = false;
        if (comp.text !== 'false')
            throw 'Boolean component.text mismatch';
        comp.text = 'true';
        if (!comp.value)
            throw 'Boolean component.value mismatch';
    }
    // Numbers
    var formattingComps = [
        new P.FormattedField(),
        new P.ModelFormattedField()
    ];
    for (var c in formattingComps) {
        var comp = formattingComps[c];
        comp.value = 0;
        comp.format = '##.##';
        comp.value = 45.899;
        if (comp.text !== '45.9' && comp.text !== '45,9') {
            throw 'Numbers. comp.text mismatch';
        }
        if (comp.value !== 45.899)
            throw 'Numbers. comp.value/format mismatch';
        comp.text = '46.8';
        if (comp.value !== 46.8) {
            comp.text = '46,8';
            if (comp.value !== 46.8) {
                throw 'Numbers. comp.value mismatch';
            }
        }
    }
    // Strings
    var formattingComps = [
        new P.ModelFormattedField(),
        new P.FormattedField()
    ];
    for (var c in formattingComps) {
        var comp = formattingComps[c];
        comp.value = '';
        comp.format = "###-*'L*'L*";
        comp.value = '555-oLoLo';
        if (comp.text !== '555-oLoLo') {
            throw 'Strings. comp.text mismatch'
        }
        comp.text = '676-%L%L%';
        if (comp.value !== '676-%L%L%') {
            throw 'Strings. comp.value mismatch'
        }
    }

    (function() {
        if (self.onSuccess) {
            self.onSuccess();
        } else {
            P.Logger.severe("self.onSuccess is absent. So unable to report about test's result");
        }
    }).invokeLater();
}
