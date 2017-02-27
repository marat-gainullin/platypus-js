/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda;

import java.util.Date;

/**
 *
 * @author pk
 */
public class HistoryItem
{
    protected final String itemID;
    protected final int clientHandle;
    protected Integer serverHandle;
    protected Object userData;

    public HistoryItem(String itemID, int clientHandle)
    {
        this.itemID = itemID;
        this.clientHandle = clientHandle;
    }

    public int getClientHandle()
    {
        return clientHandle;
    }

    public String getItemID()
    {
        return itemID;
    }

    public Object getUserData()
    {
        return userData;
    }

    public void setUserData(Object userData)
    {
        this.userData = userData;
    }

    public Integer getServerHandle()
    {
        return serverHandle;
    }

    protected void setServerHandle(int serverHandle)
    {
        this.serverHandle = serverHandle;
    }

    @Override
    public String toString()
    {
        return String.format("[HistoryItem itemID=%s, clientHandle=%d, serverHandle=%s]", itemID, clientHandle, String.valueOf(serverHandle));
    }

    public static class History
    {
        private final Date startTime;
        private final Date endTime;
        private final HistoryItem item;
        private final int aggregate;
        private final Record[] records;

        public History(Date startTime, Date endTime, HistoryItem item, int aggregate, Record[] records)
        {
            this.startTime = startTime;
            this.endTime = endTime;
            this.item = item;
            this.aggregate = aggregate;
            this.records = records;
        }

        public int getAggregate()
        {
            return aggregate;
        }

        public Date getEndTime()
        {
            return endTime;
        }

        public HistoryItem getItem()
        {
            return item;
        }

        public Record[] getRecords()
        {
            return records;
        }

        public Date getStartTime()
        {
            return startTime;
        }

        public static class Record
        {
            private final Date timeStamp;
            private final int quality;
            private final Object value;

            public Record(Date timeStamp, int quality, Object value)
            {
                this.timeStamp = timeStamp;
                this.quality = quality;
                this.value = value;
            }

            public int getQuality()
            {
                return quality;
            }

            public Date getTimeStamp()
            {
                return timeStamp;
            }

            public Object getValue()
            {
                return value;
            }
        }
    }
}
