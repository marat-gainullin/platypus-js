/*
 * ProtoWritable.java
 *
 * Created on 1 September 2009, 11:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.eas.proto;

/**
 * Provides interface for classes to save their instances in a platypus protocl streams.
 *
 * @author pl
 */
public interface ProtoWritable
{
    void writeAsTag(int tag, ProtoWriter writer);
}
