/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

/**
 *
 * @author mg
 */
public interface ResourcePool<T> {

    public T achieveResource()throws Exception;

    public void returnResource(T aResource);
}
