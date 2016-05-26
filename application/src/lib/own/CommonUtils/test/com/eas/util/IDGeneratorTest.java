/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class IDGeneratorTest {

    private static final ExecutorService GP = Executors.newCachedThreadPool();
    private static final int NUM_TREADS = Runtime.getRuntime().availableProcessors();

    @Test
    public void collisionsTest() {
        Callable<long[]> taskBody = () -> {
            long[] generated = new long[10000];
            for (int i = 0; i < generated.length; i++) {
                generated[i] = IDGenerator.genID();
            }
            return generated;
        };
        List<Future<long[]>> tasks = new ArrayList<>(NUM_TREADS);
        for (int i = 0; i < NUM_TREADS; i++) {
            tasks.add(GP.submit(taskBody));
        }

        List<Long> generated = new LinkedList<>();
        tasks.forEach((Future<long[]> aResult) -> {
            try {
                for (long id : aResult.get()) {
                    generated.add(id);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(IDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Set<Long> collisions = new HashSet<>();
        for (int i = 0; i < generated.size(); i++) {
            assertFalse(collisions.contains(generated.get(i)));
            collisions.add(generated.get(i));
        }
    }
}
