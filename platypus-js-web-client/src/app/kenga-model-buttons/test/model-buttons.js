/* global expect */
/* global NaN */

describe('Model buttons Api', function () {

    var house1 = {name: 'Angelville'};
    var house2 = {name: 'Enville'};

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

    it('ModelCheckBox.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-buttons/model-check-box'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelCheckBox) {

            var properyName = 'hungry';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelCheckBox(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelCheckBox(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelCheckBox(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelCheckBox(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelRadioButton.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-buttons/model-radio-button'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelRadioButton) {

            var properyName = 'hungry';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelRadioButton(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelRadioButton(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelRadioButton(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelRadioButton(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
    it('ModelToggleButton.Structure', function (done) {
        require([
            'model/model',
            'model/entity',
            'core/id', 'ui/color',
            'core/invoke',
            'forms/model-buttons/model-toggle-button'], function (
                Model,
                Entity,
                Id, Color,
                Invoke,
                ModelToggleButton) {

            var properyName = 'hungry';

            expectPathReverseBinding(Model, Entity, Id, Color, new ModelToggleButton(), properyName);
            expectPlainReverseBinding(Model, Entity, Id, Color, new ModelToggleButton(), properyName);

            expectPathForwardBinding(Model, Entity, Id, Color, new ModelToggleButton(), Invoke, properyName, function () {
                expectPlainForwardBinding(Model, Entity, Id, Color, new ModelToggleButton(), Invoke, properyName, function () {
                    done();
                });
            });

        });
    });
});
