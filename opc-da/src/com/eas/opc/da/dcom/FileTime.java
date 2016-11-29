/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIStruct;

/**
 *
 * @author pk
 */
public class FileTime
{
    private JIStruct struct;

    public FileTime() throws JIException
    {
        this.struct = new JIStruct();
        struct.addMember(Integer.class); //lowTime
        struct.addMember(Integer.class); //highTime
    }

    public FileTime(JIStruct outStruct)
    {
        this.struct = outStruct;
    }

    public FileTime(Date date) throws JIException
    {
        long milliseconds = date.getTime() + 11644473600000L;
        long numberOf100NanosecondIntervals = milliseconds * 10000;

        int highDateTime = (int) ((numberOf100NanosecondIntervals >> 32) & 0x00000000FFFFFFFFL);
        int lowDateTime = (int) (numberOf100NanosecondIntervals & 0x00000000FFFFFFFFL);

        struct = new JIStruct();
        struct.addMember(lowDateTime);
        struct.addMember(highDateTime);
    }

    public int getLowTime()
    {
        return (Integer) struct.getMember(0);
    }

    public int getHighTime()
    {
        return (Integer) struct.getMember(1);
    }

    public JIStruct getStruct()
    {
        return struct;
    }

    public Date getTime()
    {
        Calendar c = Calendar.getInstance();

        /*
         * The following "strange" stuff is needed since we miss a ulong type
         */
        long i = 0xFFFFFFFFL & ((long) getHighTime());
        i = i << 32;
        BigDecimal d1 = new BigDecimal(0xFFFFFFFFFFFFFFFFL & i);

        i = 0xFFFFFFFFL & ((long) getLowTime());
        d1 = d1.add(new BigDecimal(i));
        d1 = d1.divide(new BigDecimal(10000L));
        d1 = d1.subtract(new BigDecimal(11644473600000L));

        c.setTimeInMillis(d1.longValue());

        return c.getTime();
    }
}
