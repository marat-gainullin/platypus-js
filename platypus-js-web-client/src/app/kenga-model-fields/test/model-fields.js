/* global expect */
/* global NaN */

describe('Widgets Api', function () {

    var house1 = {name: 'Angelville'};
    var house2 = {name: 'Enville'};
    var houses = [house1, house2];

    function fill(entity, Id, Color) {
        entity.push(
                {id: Id.generate(), name: 'Joe', legs: 2, hungry: false, birth: new Date(), color: new Color('#fcfcfc'), house: house1},
                {id: Id.generate(), name: 'Jane', legs: 2, hungry: false, birth: new Date(), color: new Color('#fafafa'), house: house1},
                {id: Id.generate(), name: 'Nick', legs: 2, hungry: false, birth: new Date(), color: new Color('#fbfbfb'), house: house1},
                {id: Id.generate(), name: 'Jack', legs: 2, hungry: false, birth: new Date(), color: new Color('#acacac'), house: house1},
                {id: Id.generate(), name: 'Gala', legs: 2, hungry: false, birth: new Date(), color: new Color('#aaaaaa'), house: house1},
                {id: Id.generate(), name: 'Nikolai', legs: 2, hungry: false, birth: new Date(), color: new Color('#ababab'), house: house1},
                {id: Id.generate(), name: 'Shusha', legs: 4, hungry: false, birth: new Date(), color: new Color('#acacac'), house: house1}
        );
        expect(entity.cursor).toBe(entity[6]);
    }

    function expectPathReverseBinding(Model, Entity, Id, Color, instance, propertyName) {
        var model = new Model();
        var entity = new Entity();
        model.addEntity(entity);
        fill(entity, Id, Color);

        expect(instance.value).toBeNull();
        instance.data = entity;
        instance.field = 'cursor.' + propertyName;

        if (propertyName === 'name')
            entity[6][propertyName] += ' 1';
        else if (propertyName === 'legs')
            entity[6][propertyName] += 1;
        else if (propertyName === 'birth')
            entity[6][propertyName] = new Date();
        else if (propertyName === 'hungry')
            entity[6][propertyName] = !entity[6][propertyName];
        else if (propertyName === 'color')
            entity[6][propertyName] = new Color('#0c0c0c');
        else if (propertyName === 'house')
            entity[6][propertyName] = house2;
        else
            throw "Unknown property '" + propertyName + "'";
        expect(instance.value).toBe(entity[6][propertyName]);

        entity.cursor = entity[0];
        expect(instance.value).toBe(entity[0][propertyName]);

        entity.cursor = entity[5];
        expect(instance.value).toBe(entity[5][propertyName]);

        entity.cursor = entity[6];
        expect(instance.value).toBe(entity[6][propertyName]);

        entity.cursor = null;
        expect(instance.value).toBeNull();

        entity.cursor = entity[2];
        expect(instance.value).toBe(entity[2][propertyName]);

        entity.cursor = undefined;
        expect(instance.value).toBeNull();
    }

    function expectPlainReverseBinding(Model, Entity, Id, Color, instance, propertyName) {
        var model = new Model();
        var entity = new Entity();
        model.addEntity(entity);
        fill(entity, Id, Color);

        expect(instance.value).toBeNull();
        instance.data = entity.cursor;
        instance.field = propertyName;

        expect(entity.cursor).toBe(entity[6]);
        if (propertyName === 'name')
            entity[6][propertyName] += ' 1';
        else if (propertyName === 'legs')
            entity[6][propertyName] += 1;
        else if (propertyName === 'birth')
            entity[6][propertyName] = new Date();
        else if (propertyName === 'hungry')
            entity[6][propertyName] = !entity[6][propertyName];
        else if (propertyName === 'color')
            entity[6][propertyName] = new Color('#0c0c0c');
        else if (propertyName === 'house')
            entity[6][propertyName] = house2;
        else
            throw "Unknown property '" + propertyName + "'";
        expect(instance.value).toBe(entity[6][propertyName]);
    }

    function expectPathForwardBinding(Model, Entity, Id, Color, instance, Invoke, propertyName, done) {
        var model = new Model();
        var entity = new Entity();
        model.addEntity(entity);
        fill(entity, Id, Color);

        expect(instance.value).toBeNull();
        instance.data = entity;
        instance.field = 'cursor.' + propertyName;

        if (propertyName === 'name')
            instance.value += ' 1';
        else if (propertyName === 'legs')
            instance.value += 1;
        else if (propertyName === 'birth')
            instance.value = new Date();
        else if (propertyName === 'hungry')
            instance.value = !instance.value;
        else if (propertyName === 'color')
            instance.value = new Color('#0c0c0c');
        else if (propertyName === 'house')
            instance.value = house2;
        else
            throw "Unknown property '" + propertyName + "'";
        Invoke.later(function () {
            expect(entity[6][propertyName]).toBe(instance.value);
            done();
        });
    }

    function expectPlainForwardBinding(Model, Entity, Id, Color, instance, Invoke, propertyName, done) {
        var model = new Model();
        var entity = new Entity();
        model.addEntity(entity);
        fill(entity, Id, Color);

        expect(instance.value).toBeNull();
        instance.data = entity.cursor;
        instance.field = propertyName;

        if (propertyName === 'name')
            instance.value += ' 1';
        else if (propertyName === 'legs')
            instance.value += 1;
        else if (propertyName === 'birth')
            instance.value = new Date();
        else if (propertyName === 'hungry')
            instance.value = !instance.value;
        else if (propertyName === 'color')
            instance.value = new Color('#0c0c0c');
        else if (propertyName === 'house')
            instance.value = house2;
        else
            throw "Unknown property '" + propertyName + "'";
        Invoke.later(function () {
            expect(entity[6][propertyName]).toBe(instance.value);
            done();
        });
    }

    it('ModelColorField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-color-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelColorField) {

            var properyName = 'color';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelColorField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelColorField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelColorField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelColorField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelDateField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-date-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelDateField) {

            var properyName = 'birth';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelDateField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelDateField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelDateField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelDateField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelDateTimeField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-date-time-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelDateTimeField) {

            var properyName = 'birth';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelDateTimeField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelDateTimeField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelDateTimeField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelDateTimeField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelDropDownField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-drop-down-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelDropDownField) {

            var properyName = 'house';

            var f1 = new ModelDropDownField();
            f1.displayList = houses;
            f1.displayField = 'name';
            expectPathReverseBinding(Model, Entity, Id, Color, f1, properyName);
            var f2 = new ModelDropDownField();
            f2.displayList = houses;
            f2.displayField = 'name';
            expectPlainReverseBinding(Model, Entity, Id, Color, f2, properyName);

            var f3 = new ModelDropDownField();
            f3.displayList = houses;
            f3.displayField = 'name';
            expectPathForwardBinding(Model, Entity, Id, Color, f3, Invoke, properyName, function () {
                var f4 = new ModelDropDownField();
                f4.displayList = houses;
                f4.displayField = 'name';
                expectPlainForwardBinding(Model, Entity, Id, Color, f4, Invoke, properyName, function () {
                    done();
                });
            });
        });
    });
    it('ModelEMailField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-email-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelEMailField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelEMailField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelEMailField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelEMailField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelEMailField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelFormattedField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-formatted-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelFormattedField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelFormattedField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelFormattedField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelFormattedField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelFormattedField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelMeterField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-meter-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelMeterField) {

            var properyName = 'legs';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelMeterField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelMeterField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelMeterField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelMeterField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelNumberField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-number-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelNumberField) {

            var properyName = 'legs';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelNumberField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelNumberField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelNumberField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelNumberField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelPasswordField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-password-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelPasswordField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelPasswordField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelPasswordField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelPasswordField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelPasswordField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelPhoneField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-phone-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelPhoneField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelPhoneField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelPhoneField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelPhoneField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelPhoneField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelProgressField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-progress-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelProgressField) {

            var properyName = 'legs';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelProgressField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelProgressField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelProgressField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelProgressField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelRichTextArea.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-rich-text-area'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelRichTextArea) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelRichTextArea(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelRichTextArea(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelRichTextArea(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelRichTextArea(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelSliderField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-slider'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelSlider) {

            var properyName = 'legs';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelSlider(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelSlider(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelSlider(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelSlider(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelTextArea.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-text-area'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelTextArea) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelTextArea(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelTextArea(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelTextArea(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelTextArea(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelTextField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-text-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelTextField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelTextField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelTextField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelTextField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelTextField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelTimeField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-time-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelTimeField) {

            var properyName = 'legs';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelTimeField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelTimeField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelTimeField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelTimeField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelUrlField.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-fields/model-url-field'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelUrlField) {

            var properyName = 'name';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelUrlField(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelUrlField(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelUrlField(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelUrlField(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
});
