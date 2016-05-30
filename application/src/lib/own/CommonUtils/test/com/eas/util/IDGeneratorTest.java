/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author mg
 */
public class IdGeneratorTest {

    private static final ExecutorService GP = Executors.newCachedThreadPool();
    private static final int NUM_PARALLEL_TASKS = Runtime.getRuntime().availableProcessors();

    @Test
    public void shortCollisionsTest() {
        perform(IdGenerator::genId);
    }

    @Test
    public void longCollisionsTest() {
        perform(IdGenerator::genLongId);
    }

    @Test
    public void stringCollisionsTest() {
        perform(IdGenerator::genStringId);
    }

    private <T> void perform(Callable<T> aGenerator) {
        Callable<T[]> taskBody = () -> {
            Object[] generated = new Object[100000];
            for (int i = 0; i < generated.length; i++) {
                generated[i] = aGenerator.call();
            }
            return (T[]) generated;
        };
        List<Future<T[]>> tasks = new ArrayList<>(NUM_PARALLEL_TASKS);
        for (int i = 0; i < NUM_PARALLEL_TASKS; i++) {
            tasks.add(GP.submit(taskBody));
        }

        tasks.forEach((Future<T[]> aResult) -> {
            try {
                aResult.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(IdGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Set<T> collisions = new HashSet<>();
        tasks.forEach((Future<T[]> aResult) -> {
            try {
                for (T id : aResult.get()) {
                    assertFalse(collisions.contains(id));
                    collisions.add(id);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(IdGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
