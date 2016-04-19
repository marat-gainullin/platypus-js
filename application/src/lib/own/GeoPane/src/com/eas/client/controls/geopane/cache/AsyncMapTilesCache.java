/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.geopane.cache;

import com.eas.client.controls.geopane.TilesBoundaries;
import com.eas.concurrent.PlatypusThreadFactory;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 *
 * @author mg
 */
public class AsyncMapTilesCache extends MapTilesCache {

    public class AsyncRenderingTask extends RenderingTask {

        protected Thread executingThread;
        private boolean rendered = false;
                
        public AsyncRenderingTask(Point aTilePoint) throws NoninvertibleTransformException {
            super(aTilePoint);
        }

        @Override
        public void run() {
            mapContextLock.readLock().lock();
            try {
                executingThread = Thread.currentThread();
                if (!isStopped() && isActual()) {
                    try {
                        super.run();
                        setRendered(true);
                    } finally {
                        offeredTasks.remove(tilePoint);
                        // fireRenderingTaskCompleted() must be called strictly after offeredTasks.remove() !
                        fireRenderingTaskCompleted(this);
                    }
                } else {
                    offeredTasks.remove(tilePoint);
                }
            } finally {
                mapContextLock.readLock().unlock();
            }
        }

        @Override
        protected boolean isActual() {
            return getActualArea().contains(tilePoint);
        }

        @Override
        protected GTRenderer archieveRenderer() {
            return new StreamingRenderer();
        }

        public void stop() throws InterruptedException {
            assert taskRenderer != null;
            taskRenderer.stopRendering();
            setStopped(true);
        }

        /**
         * @return the rendered
         */
        public synchronized boolean isRendered() {
            return rendered;
        }

        /**
         * @param aRendered the rendered to set
         */
        public synchronized void setRendered(boolean aRendered) {
            rendered = aRendered;
        }
    }
    protected Map<Point, AsyncRenderingTask> offeredTasks = new ConcurrentHashMap<>();
    protected ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new PlatypusThreadFactory());
    protected Set<RenderingTaskListener> listeners = new HashSet<>();
    protected ReadWriteLock mapContextLock;

    public AsyncMapTilesCache(int aCacheSize, MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aCacheSize, aDisplayContext, aTransform);
        mapContextLock = aMapContextLock;
    }

    public AsyncMapTilesCache(MapContent aDisplayContext, ReadWriteLock aMapContextLock, AffineTransform aTransform) {
        super(aDisplayContext, aTransform);
        mapContextLock = aMapContextLock;
    }

    @Override
    protected Image renderTile(Point ptKey) {
        try {
            AsyncRenderingTask task = new AsyncRenderingTask(ptKey);
            assert !executor.isShutdown();
            offeredTasks.put(ptKey, task);
            executor.execute(task);
            return null;
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(AsyncMapTilesCache.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void shutdown() {
        try2StopSomeTasks();
        wait4AllTasksCompleted();
        executor.shutdown();
        super.clear();
    }

    @Override
    public void clear() {
        // all tasks are invalid and cache entries to.
        // hint some tasks to quikly complete their's work.
        try2StopSomeTasks();
        // wait while ALL tasks complete (hinted to shutdown and all others)
        wait4AllTasksCompleted();
        // clear the cache
        super.clear();
    }

    @Override
    public synchronized Image get(Point ptKey) {
        if (offeredTasks.containsKey(ptKey)) {
            // may be one of the working rendering tasks alreadey have put results in the cache.
            return cache.get(ptKey);
        }
        // There is no task rendering ptKey tile, and so let's relay on the super get/offer mechanism
        return super.get(ptKey);
    }

    @Override
    public void setActualArea(TilesBoundaries aActualArea) {
        // let's hint invalid, but already offered tasks.
        try2StopSomeUnactualTasks(aActualArea);
        super.setActualArea(aActualArea);
    }

    /**
     * Makes to executing and queued tasks a hint to shutdown any work and remove thereselfs from
     * offeredTasks collection.
     * This is only hint because of multithreaded environment. Not all the tasks will undestand it.
     */
    protected void try2StopSomeTasks() {
        try2StopSomeUnactualTasks(TilesBoundaries.EMPTY);
    }

    protected void try2StopSomeUnactualTasks(TilesBoundaries aActualArea) {
        for (Point taskPoint : offeredTasks.keySet()) {
            if (!aActualArea.contains(taskPoint)) {
                AsyncRenderingTask task = offeredTasks.get(taskPoint);
                if (task != null && task.isRendered()) {
                    try {
                        task.stop();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AsyncMapTilesCache.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    protected void wait4AllTasksCompleted() {
        // wait while all tasks complete execution
        while (!offeredTasks.isEmpty()) {
            try {
                assert !executor.isShutdown();
                Thread.sleep(4);
            } catch (InterruptedException ex) {
                Logger.getLogger(AsyncMapTilesCache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void scaleChanged() {
        // clear the cache.
        clear();
    }

    public synchronized void addRenderingTaskListener(RenderingTaskListener l) {
        listeners.add(l);
    }

    public synchronized void removeRenderingTaskListener(RenderingTaskListener l) {
        listeners.remove(l);
    }

    /**
     * Fires an event that rendering task had completed
     * WARNING!!! This method is called from executor service thread and it
     * have to use all appropriate synchronization.
     */
    protected synchronized void fireRenderingTaskCompleted(AsyncRenderingTask aTask) {
        for (RenderingTaskListener l : listeners) {
            l.taskCompleted(aTask);
        }
    }
}
