/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

/**
 *
 * @author pk
 */
public class Qualities
{
    public final static int OPC_QUALITY_MASK = 0xC0;
    public final static int OPC_STATUS_MASK = 0xFC;
    public final static int OPC_LIMIT_MASK = 0x03;
    // Values for QUALITY_MASK bit field
    public final static int OPC_QUALITY_BAD = 0x00;
    public final static int OPC_QUALITY_UNCERTAIN = 0x40;
    public final static int OPC_QUALITY_GOOD = 0xc0;
    // STATUS_MASK Values for Quality = BAD
    public final static int OPC_QUALITY_CONFIG_ERROR = 0x04;
    public final static int OPC_QUALITY_NOT_CONNECTED = 0x08;
    public final static int OPC_QUALITY_DEVICE_FAILURE = 0x0c;
    public final static int OPC_QUALITY_SENSOR_FAILURE = 0x10;
    public final static int OPC_QUALITY_LAST_KNOWN = 0x14;
    public final static int OPC_QUALITY_COMM_FAILURE = 0x18;
    public final static int OPC_QUALITY_OUT_OF_SERVICE = 0x1c;
    // STATUS_MASK Values for Quality = UNCERTAIN
    public final static int OPC_QUALITY_LAST_USABLE = 0x44;
    public final static int OPC_QUALITY_SENSOR_CAL = 0x50;
    public final static int OPC_QUALITY_EGU_EXCEEDED = 0x54;
    public final static int OPC_QUALITY_SUB_NORMAL = 0x58;
    // STATUS_MASK Values for Quality = GOOD
    public final static int OPC_QUALITY_LOCAL_OVERRIDE = 0xd8;
    // Values for Limit Bitfield
    public final static int OPC_LIMIT_OK = 0x00;
    public final static int OPC_LIMIT_LOW = 0x01;
    public final static int OPC_LIMIT_HIGH = 0x02;
    public final static int OPC_LIMIT_CONST = 0x03;
}
