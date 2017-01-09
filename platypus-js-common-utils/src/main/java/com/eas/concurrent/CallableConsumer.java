/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.concurrent;

/**
 *
 * @author mg
 */
@FunctionalInterface
public interface CallableConsumer<R, A> {

    public R call(A aArgument) throws Exception;
}
