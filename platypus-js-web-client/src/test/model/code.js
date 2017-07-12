/* global expect */

describe('Model orm, requey of graph, Orm navigation properties and orderers', function () {
    it('Creation Api', function (done) {
        require(['orm', 'model', 'entity'], function (Orm, Model, Entity) {
            var model = new Model();
            var model1 = new Orm.createModel();
            var entity = new Entity();
            expect(model).toBeDefined();
            expect(model instanceof Model).toBeTruthy();
            expect(model1).toBeDefined();
            expect(model1 instanceof Model).toBeTruthy();
            expect(entity).toBeDefined();
            expect(entity instanceof Array).toBeTruthy();
            done();
        }, function (e) {
            fail(e);
        });
    });
    it('Model creation based on absent prefetched document', function (done) {
        require(['orm', 'model', 'entity'], function (Orm, Model, Entity) {
            try {
                new Orm.createModel('absent-module');
            } catch (e) {
                expect(e).toBeTruthy();
            }
            done();
        }, function (e) {
            fail(e);
        });
    });
    it('Model reading', function (done) {
        require(['orm'], function (Orm) {
            var model = new Orm.readModel('<?xml version="1.0" encoding="UTF-8"?><datamodel>'
                    + '<entity entityId="id1" Name="e1" queryId="q1"></entity>'
                    + '<entity entityId="id2" Name="e2" queryId="q2"></entity>'
                    + '<entity entityId="id3" Name="e3" queryId="q3"></entity>'
                    + '<relation></relation>'
                    + '<referenceRelation></referenceRelation>'
                    + '</datamodel>');
            expect(model).toBeDefined();
            expect(model.e1).toBeDefined();
            expect(model.e2).toBeDefined();
            expect(model.e3).toBeDefined();
            done();
        }, function (e) {
            fail(e);
        });
    });
    it('createModel', function (done) {
        pending('Till tests with server');
        done();
    });
    it('loadModel', function (done) {
        pending('Till tests with server');
        done();
    });
    it('requireEntities', function (done) {
        pending('Till tests with server');
        done();
    });
    it("Snapshots of entities' data", function (done) {
        // TODO: Add requery -> revert test.
        pending('Till tests with server');
        done();
    });
    it("'Entity.requery()' -> Some changes -> 'Entity.revert()'", function (done) {
        pending('Till tests with server');
        done();
    });
    it("Requery graph", function (done) {
        pending('Till tests with server');
        done();
    });
    it("Requery partial graph", function (done) {
        pending('Till tests with server');
        done();
    });
    it("Requery graph after crash", function (done) {
        pending('Till tests with server');
        done();
    });
    it("Requery graph due to cursor changes (aka scrolling)", function (done) {
        pending('Till tests with server');
        done();
    });
    it("Extra and unknown to a backend properties", function (done) {
        pending('Till tests with server');
        done();
    });
    it("'Entity.query()' -> 'Entity.append()' chain", function (done) {
        pending('Till tests with server');
        done();
    });
    it("'Entity.update()'", function (done) {
        pending('Till tests with server');
        done();
    });
    it("'Entity.enqueueUpdate()'", function (done) {
        pending('Till tests with server');
        done();
    });
    it("'Model.save()'", function (done) {
        pending('Till tests with server');
        done();
    });

    function withPetsModel(Id, Model, Entity, onPrepared) {
        var pets = new Entity('pets');
        var owners = new Entity('owners');
        var model = new Model();
        model.addEntity(pets);
        model.addEntity(owners);
        model.addAssociation({
            leftEntity: pets,
            leftField: 'owner_id',
            rightEntity: owners,
            rightField: 'id',
            scalarPropertyName: 'owner',
            collectionPropertyName: 'pets'
        });
        model.processAssociations();
        var christy = {id: Id.generate(), name: 'Christy'};
        var jenny = {id: Id.generate(), name: 'Jenny'};
        owners.push.apply(owners, [
            christy,
            jenny
        ]);
        pets.push.apply(pets, [
            {id: Id.generate(), name: 'Spike', owner_id: christy.id},
            {id: Id.generate(), name: 'Pick', owner_id: jenny.id},
            {id: Id.generate(), name: 'Tom', owner_id: jenny.id},
            {id: Id.generate(), name: 'Jerry', owner_id: christy.id}
        ]);
        onPrepared(pets, owners, jenny, christy);
    }

    it('Navigation properties view', function (done) {
        require(['id', 'model', 'entity'], function (Id, Model, Entity) {
            withPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner).toBeDefined();
                    if (pet.owner === christy) {
                        expect(christy.pets.indexOf(pet) > -1).toBeTruthy();
                    } else if (pet.owner === jenny) {
                        expect(jenny.pets.indexOf(pet) > -1).toBeTruthy();
                    }
                });
                owners.forEach(function (owner) {
                    expect(owner).toBeDefined();
                    expect(owner.pets).toBeDefined();
                    expect(owner.pets.length).toEqual(2);
                    if (owner === christy) {
                        expect(owner.pets).toEqual(christy.pets);
                    } else if (owner === jenny) {
                        expect(owner.pets).toEqual(jenny.pets);
                    }
                });
                done();
            });
        });
    });
    it('Navigation scalar properties edit', function (done) {
        require(['id', 'model', 'entity'], function (Id, Model, Entity) {
            withPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
                christy.pets.forEach(function(pet){
                    pet.owner = jenny;
                });
                expect(christy.pets.length).toEqual(0);
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner).toBeDefined();
                    expect(pet.owner).toEqual(jenny);
                });
                expect(jenny.pets.length).toEqual(4);
                jenny.pets.forEach(function(pet){
                    pet.owner = christy;
                });
                expect(jenny.pets.length).toEqual(0);
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner).toBeDefined();
                    expect(pet.owner).toEqual(christy);
                });
                expect(christy.pets.length).toEqual(4);
                done();
            });
        });
    });
    it('Navigation collection properties edit', function (done) {
        require(['id', 'model', 'entity'], function (Id, Model, Entity) {
            withPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
                christy.pets.splice(0, christy.pets.length);
                expect(christy.pets.length).toEqual(0);
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner === null || pet.owner === jenny).toBeTruthy();
                    if(!pet.owner){
                        jenny.pets.push(pet);
                    }
                });
                expect(jenny.pets.length).toEqual(4);
                done();
            });
        });
    });
});