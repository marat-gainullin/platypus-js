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
            done();
        });
    });
    it('Model creation based on absent fetched document', function (done) {
        require(['orm', 'model', 'entity'], function (Orm, Model, Entity) {
            try {
                new Orm.createModel('absent-module');
            } catch (e) {
                expect(e).toBeTruthy();
            }
            done();
        }, function (e) {
            fail(e);
            done();
        });
    });
    it('Model creation based on fetched document', function (done) {
        require('assets/full-module', function (FullModule) {
            var m = new FullModule();
            expect(m.model).toBeDefined();
            expect(m.model1).toBeDefined();
            done();
        }, function (reason) {
            fail(reason);
            done();
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
            done();
        });
    });
    it('requireEntities.success', function (done) {
        require('orm', function (Orm) {
            Orm.requireEntities(['pets', 'all-owners'], function (petsEntity, ownersEntity) {
                expect(petsEntity).toBeDefined();
                expect(petsEntity.fields).toBeDefined();
                expect(petsEntity.fields.length).toBeDefined();
                expect(petsEntity.fields.length).toEqual(5);
                expect(petsEntity.parameters).toBeDefined();
                expect(petsEntity.parameters.length).toBeDefined();
                expect(petsEntity.title).toBeDefined();
                expect(ownersEntity).toBeDefined();
                expect(ownersEntity.fields).toBeDefined();
                expect(ownersEntity.fields.length).toBeDefined();
                expect(ownersEntity.fields.length).toEqual(7);
                expect(ownersEntity.parameters).toBeDefined();
                expect(ownersEntity.parameters.length).toBeDefined();
                expect(ownersEntity.title).toBeDefined();
                done();
            }, function (e) {
                fail(e);
                done();
            });
        });
    });
    it('requireEntities.failure', function (done) {
        require('orm', function (Orm) {
            Orm.requireEntities(['absent-entity'], function () {
                fail("'Orm.requireEntities' against absent entity should lead to an error");
                done();
            }, function (e) {
                expect(e).toBeDefined();
                done();
            });
        });
    });
    it("'Entity.requery()' -> Some changes -> 'Entity.revert()'", function (done) {
        withRemotePetsModel(function (model, Id) {
            model.requery(function () {
                var oldLength = model.owners.length;
                expect(oldLength).toBeGreaterThan(1);
                var oldValue = model.owners[0].firstname;
                expect(model.changeLog).toBeDefined();
                expect(model.changeLog.length).toBeDefined();
                expect(model.changeLog.length).toEqual(0);
                model.owners[0].firstname += Id.generate();
                expect(model.changeLog.length).toEqual(1);
                model.owners.push({
                    owners_id: Id.generate()
                });
                expect(model.changeLog.length).toEqual(2);
                model.revert();
                expect(model.owners.length, oldLength);
                expect(model.owners[0].firstname).toEqual(oldValue);
                expect(model.changeLog.length).toEqual(0);
                done();
            }, function (e) {
                fail(e);
                done();
            });
        }, function (e) {
            fail(e);
            done();
        });
    });
    it("Requery graph", function (done) {
        withRemotePetsModel(function (model, Id, Invoke) {
            var ownersRequeried = 0;
            model.owners.onRequeried = function () {
                ownersRequeried++;
            };
            var petsOfOwnerRequeried = 0;
            model.petsOfOwner.onRequeried = function () {
                petsOfOwnerRequeried++;
            };
            var petOfOwnerRequeried = 0;
            model.petOfOwner.onRequeried = function () {
                petOfOwnerRequeried++;
            };
            model.requery(function () {
                Invoke.later(function () {
                    expect(ownersRequeried).toEqual(1);
                    expect(petsOfOwnerRequeried).toEqual(1);
                    expect(petOfOwnerRequeried).toEqual(1);
                    done();
                });
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("Requery partial graph with 'Entity.requery() with dependents'", function (done) {
        withRemotePetsModel(function (model, Id, Invoke) {
            var ownersRequeried = 0;
            model.owners.onRequeried = function () {
                ownersRequeried++;
            };
            var petsOfOwnerRequeried = 0;
            model.petsOfOwner.onRequeried = function () {
                petsOfOwnerRequeried++;
            };
            var petOfOwnerRequeried = 0;
            model.petOfOwner.onRequeried = function () {
                petOfOwnerRequeried++;
            };
            model.owners.requery(function () {
                Invoke.later(function () {
                    expect(ownersRequeried).toEqual(1);
                    expect(petsOfOwnerRequeried).toEqual(1);
                    expect(petOfOwnerRequeried).toEqual(1);
                    done();
                });
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("Requery graph after 'Model.cancel()' after one entity requeried", function (done) {
        withRemotePetsModel(function (model, Id, Invoke) {
            var ownersRequeried = 0;
            model.owners.onRequeried = function () {
                ownersRequeried++;
                model.cancel();
            };
            var petsOfOwnerRequeried = 0;
            model.petsOfOwner.onRequeried = function () {
                petsOfOwnerRequeried++;
            };
            var petOfOwnerRequeried = 0;
            model.petOfOwner.onRequeried = function () {
                petOfOwnerRequeried++;
            };
            model.requery(function () {
                fail("Success callbck of requery shouldn't be called after 'Model.cancel()'");
                done();
            }, function (reason) {
                expect(reason).toBeDefined();
                expect(ownersRequeried).toEqual(1);
                expect(model.owners.length).toBeGreaterThan(0);
                expect(petsOfOwnerRequeried).toEqual(0);
                expect(model.petsOfOwner.length).toEqual(0);
                expect(petOfOwnerRequeried).toEqual(0);
                expect(model.petOfOwner.length).toEqual(0);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("Requery graph after 'Model.cancel()'.immediate", function (done) {
        withRemotePetsModel(function (model, Id, Invoke) {
            var ownersRequeried = 0;
            model.owners.onRequeried = function () {
                ownersRequeried++;
            };
            var petsOfOwnerRequeried = 0;
            model.petsOfOwner.onRequeried = function () {
                petsOfOwnerRequeried++;
            };
            var petOfOwnerRequeried = 0;
            model.petOfOwner.onRequeried = function () {
                petOfOwnerRequeried++;
            };
            model.requery(function () {
                fail("Success callbck of requery shouldn't be called after 'Model.cancel()'");
                done();
            }, function (reason) {
                expect(reason).toBeDefined();
                expect(ownersRequeried).toEqual(0);
                expect(model.owners.length).toEqual(0);
                expect(petsOfOwnerRequeried).toEqual(0);
                expect(model.petsOfOwner.length).toEqual(0);
                expect(petOfOwnerRequeried).toEqual(0);
                expect(model.petOfOwner.length).toEqual(0);
                done();
            });
            model.cancel();
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("Entities' events", function (done) {
        withRemotePetsModel(function (model, Id) {
            var onRequiredCalled = 0;
            model.owners.onRequeried = function () {
                onRequiredCalled++;
            };
            var onChangeCalled = 0;
            model.owners.onChange = function (event) {
                onChangeCalled++;
                expect(event).toBeDefined();
                expect(event.source).toBeDefined();
                expect(event.propertyName).toBeDefined();
                expect(event.oldValue).toBeDefined();
                expect(event.newValue).toBeDefined();
            };
            var onInsertCalled = 0;
            model.owners.onInsert = function (event) {
                onInsertCalled++;
                expect(event).toBeDefined();
                expect(event.source).toBeDefined();
                expect(event.items).toBeDefined();
            };
            var onDeleteCalled = 0;
            model.owners.onDelete = function (event) {
                onDeleteCalled++;
                expect(event).toBeDefined();
                expect(event.source).toBeDefined();
                expect(event.items).toBeDefined();
            };
            expect(model.owners.onRequery === model.owners.onRequeried).toBeTruthy();
            model.requery(function () {
                expect(onRequiredCalled).toEqual(1);
                var newOwner = {
                    // Note! Only changes of those properties, that are in push/splice/unshift will be observable
                    // for changeLog and for onChange events!
                    owners_id: Id.generate(),
                    firstname: 'test-owner'
                };
                model.owners.push(newOwner);
                expect(onInsertCalled).toEqual(1);
                newOwner.firstname = 'test-owner-edited';
                expect(onChangeCalled).toEqual(1);
                model.owners.remove(newOwner);
                expect(onDeleteCalled).toEqual(1);
                done();
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("Requery of model with command like entities", function (done) {
        withRemotePetsModel(function (model, Id) {
            var Entity = model.owners.constructor;
            model.addEntity(new Entity('add-pet'));
            model.requery(function () {
                fail("'model.requery()' with command like entity inside, should lead to an error");
                done();
            }, function (reason) {
                expect(reason).toBeDefined();
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });

    it("Extra and unknown to a backend properties", function (done) {
        withRemotePetsModel(function (model, Id) {
            model.requery(function () {
                model.owners[0].name += '-edited by test';
                var testOwner = {
                    owners_id: Id.generate(),
                    unknownproperty: 'should not be considered while translating to database'
                };
                model.owners.push(testOwner);
                model.save(function (result) {
                    expect(result).toBeDefined();
                    expect(result).toBeGreaterThanOrEqual(1);
                    done();
                }, function (reason) {
                    fail(reason);
                    done();
                });
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("'Entity.query()' -> 'Entity.append()' chain", function (done) {
        withRemotePetsModel(function (model) {
            model.owners.query({}, function (owners) {
                expect(owners).toBeDefined();
                expect(owners.length).toBeDefined();
                expect(owners.length).toBeGreaterThan(1);
                expect(model.owners.length).toEqual(0);
                model.owners.append(owners);
                expect(model.owners.length).toBeGreaterThan(1);
                done();
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("'Entity.update()'.params in order", function (done) {
        withRemotePetsModel(function (model, Id) {
            var Entity = model.owners.constructor;
            var addPet = new Entity('add-pet');
            model.addEntity(addPet);
            addPet.update({
                id: Id.generate(),
                ownerId: 142841834950629,
                typeId: 142841300122653,
                name: 'test-pet-1'
            }, function (result) {
                expect(result).toBeDefined();
                done();
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("'Entity.update()'.params out of order", function (done) {
        withRemotePetsModel(function (model, Id) {
            var Entity = model.owners.constructor;
            var addPet = new Entity('add-pet');
            model.addEntity(addPet);
            addPet.update({
                id: Id.generate(),
                typeId: 142841300122653,
                ownerId: 142841834950629,
                name: 'test-pet-2'
            }, function (result) {
                expect(result).toBeDefined();
                done();
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("'Entity.enqueueUpdate()'", function (done) {
        withRemotePetsModel(function (model, Id) {
            var Entity = model.owners.constructor;
            var addPet = new Entity('add-pet');
            model.addEntity(addPet);
            addPet.enqueueUpdate({
                id: Id.generate(),
                ownerId: 142841834950629,
                typeId: 142841300122653,
                name: 'test-pet-3'
            });
            model.save(function (result) {
                expect(result).toBeDefined();
                done();
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });
    it("'Model.save()'", function (done) {
        withRemotePetsModel(function (model, Id) {
            model.requery(function () {
                var newOwnerId = Id.generate();
                model.owners[0].name += '-edited by test';
                var testOwner = {
                    owners_id: newOwnerId,
                    firstname: 'test-owner-name',
                    lastname: 'test-owner-surname-' + newOwnerId,
                    email: 'john@doe.com'
                };
                model.owners.push(testOwner);
                model.save(function (result) {
                    expect(result).toBeDefined();
                    expect(result).toBeGreaterThanOrEqual(1);
                    model.requery(function () {
                        var justAddedOwner = model.owners.findByKey(newOwnerId);
                        expect(justAddedOwner).toBeDefined();
                        expect(justAddedOwner.lastname).toEqual('test-owner-surname-' + newOwnerId);
                        done();
                    }, function (reason) {
                        fail(reason);
                        done();
                    });
                }, function (reason) {
                    fail(reason);
                    done();
                });
            }, function (reason) {
                fail(reason);
                done();
            });
        }, function (reason) {
            fail(reason);
            done();
        });
    });

    function withRemotePetsModel(onSuccess, onFailure) {
        require(['remote/resource', 'orm', 'core/id', 'core/invoke'], function (Resource, Orm, Id, Invoke) {
            Resource.loadText('assets/entities/model-graph.model', function (loaded) {
                var model = Orm.readModel(loaded);
                model.owners.keysNames.add('owners_core/id');
                onSuccess(model, Id, Invoke);
            }, onFailure);
        });
    }

    function withLocalPetsModel(Id, Model, Entity, onPrepared) {
        var pets = new Entity('pets');
        var owners = new Entity('owners');
        var model = new Model();
        model.addEntity(pets);
        model.addEntity(owners);
        model.addAssociation({
            leftEntity: pets,
            leftField: 'owner_core/id',
            rightEntity: owners,
            rightField: 'core/id',
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
        require(['core/id', 'model', 'entity'], function (Id, Model, Entity) {
            withLocalPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
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
        require(['core/id', 'model', 'entity'], function (Id, Model, Entity) {
            withLocalPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
                christy.pets.forEach(function (pet) {
                    pet.owner = jenny;
                });
                expect(christy.pets.length).toEqual(0);
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner).toBeDefined();
                    expect(pet.owner).toEqual(jenny);
                });
                expect(jenny.pets.length).toEqual(4);
                jenny.pets.forEach(function (pet) {
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
        require(['core/id', 'model', 'entity'], function (Id, Model, Entity) {
            withLocalPetsModel(Id, Model, Entity, function (pets, owners, jenny, christy) {
                christy.pets.splice(0, christy.pets.length);
                expect(christy.pets.length).toEqual(0);
                pets.forEach(function (pet) {
                    expect(pet).toBeDefined();
                    expect(pet.owner === null || pet.owner === jenny).toBeTruthy();
                    if (!pet.owner) {
                        jenny.pets.push(pet);
                    }
                });
                expect(jenny.pets.length).toEqual(4);
                done();
            });
        });
    });
});