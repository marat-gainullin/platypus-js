/*
 * CoreTags.java
 *
 * Created on 18 September 2009, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.eas.proto;

/**
 * Core tags of platypus protocol.
 *
 * @author mg
 */
public class CoreTags {

    public static final int SIGNATURE_SIZE = 11;
    /**
     * Check for this constant when reading streams with TaggedReader to notice
     * the end of a stream.
     */
    public static final int TAG_EOF = 0;
    public static final int TAG_SIGNATURE = 201;
    /**
     * This tag by a convention contains substreams. If you would like to put a
     * substream inside some TAG_FOOBAR, make TAG_FOOBAR empty, and write
     * TAG_STREAM with actual substream just after TAG_FOOBAR.
     */
    public static final int TAG_STREAM = 202;
    /**
     * Same as TAG_STREAM, but it's content is zip-compressed.
     */
    public static final int TAG_COMPRESSED_STREAM = 203;
    /**
     * This tag contains server session ticket.
     */
    public static final int TAG_SESSION_TICKET = 204;
    /**
     * This tag contains user name.
     */
    public static final int TAG_USER_NAME = 205;
    /**
     * This tag contains password.
     */
    public static final int TAG_PASSWORD = 206;
    /**
     * Indicates that the tag contains long value, which size is greater than
     * 255 bytes.
     */
    public static final int TAG_LONG = 255;
}
