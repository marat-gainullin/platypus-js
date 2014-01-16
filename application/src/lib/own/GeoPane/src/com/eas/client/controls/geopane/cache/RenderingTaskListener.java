/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.geopane.cache;

import com.eas.client.controls.geopane.cache.MapTilesCache.RenderingTask;

/**
 *
 * @author mg
 */
public interface RenderingTaskListener {

    /**
     * Occurs while firing an event of the rendering task have been completed.
     * WARNING!!! This method is called from executor service thread and it
     * implementors have to use all appropriate synchronization.
     * @param aTask
     */
    public void taskCompleted(RenderingTask aTask);
}
