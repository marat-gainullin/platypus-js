/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

import java.util.Date;
import java.util.Map;
import org.jinterop.dcom.common.JIException;

/**
 *
 * @author pk
 */
public class Item {

    private final String itemID;
    private String accessPath;
    private boolean active;
    private int clientHandle;
    private final byte[] blob;
    private DataType requestedDataType;
    private short reserved;
    //
    private Integer accessRights;
    private DataType canonicalDataType;
    private Integer serverHandle;
    //
    private Group group;
    //
    private Object userData;

    public Item(String itemID, String accessPath, boolean active, int clientHandle, byte[] blob, DataType requestedDataType, short reserved) {
        if (itemID == null) {
            throw new NullPointerException("itemID cannot be null.");
        }
        if (requestedDataType == null) {
            throw new NullPointerException("requestedDataType cannot be null.");
        }
        this.itemID = itemID;
        this.accessPath = accessPath;
        this.active = active;
        this.clientHandle = clientHandle;
        this.blob = blob;
        this.requestedDataType = requestedDataType;
        this.reserved = reserved;
    }

    public Item(String itemID, boolean active, int clientHandle) {
        this(itemID, null, active, clientHandle, new byte[0], DataType.DEFAULT, (short) 0);
    }

    public String getAccessPath() {
        return accessPath;
    }

    protected void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) throws JIException {
        if (this.active != active) {
            if (serverHandle != null && group != null) {
                final Map<Integer, Integer> errors = group.itemMgt.setActiveState(active, new int[]{
                            serverHandle
                        });
                final Integer errorCode = errors.get(serverHandle);
                if (errorCode != 0) {
                    throw new JIException(errorCode);
                }
            }
            this.active = active;
        }
    }

    public byte[] getBlob() {
        return blob;
    }

    public int getClientHandle() {
        return clientHandle;
    }

    public void setClientHandle(int clientHandle) throws JIException {
        if (this.clientHandle != clientHandle) {
            if (serverHandle != null && group != null) {
                final Map<Integer, Integer> results = group.itemMgt.setClientHandles(new Integer[]{
                            serverHandle
                        }, new Integer[]{
                            clientHandle
                        });
                final Integer errorCode = results.get(serverHandle);
                if (errorCode != 0) {
                    throw new JIException(errorCode);
                }
            }
            this.clientHandle = clientHandle;
        }
        this.clientHandle = clientHandle;
    }

    public String getItemID() {
        return itemID;
    }

    public DataType getRequestedDataType() {
        return requestedDataType;
    }

    public void setRequestedDataType(DataType aValue) throws JIException {
        if (aValue == null) {
            throw new NullPointerException("Cannot set requested data type to null.");
        }
        if (requestedDataType != aValue) {
            if (serverHandle != null && group != null) {
                final Map<Integer, Integer> errors = group.itemMgt.setDatatypes(new Integer[]{
                            serverHandle
                        }, new Short[]{
                            aValue.getTypeID()
                        });
                final Integer errorCode = errors.get(serverHandle);
                if (errorCode != 0) {
                    throw new JIException(errorCode);
                }
            }
            this.requestedDataType = aValue;
        }
    }

    public short getReserved() {
        return reserved;
    }

    protected void setReserved(short reserved) {
        this.reserved = reserved;
    }

    public Integer getAccessRights() {
        return accessRights;
    }

    protected void setAccessRights(Integer accessRights) {
        if (accessRights == null) {
            throw new NullPointerException("accessRights may not be set to null.");
        }
        this.accessRights = accessRights;
    }

    public DataType getCanonicalDataType() {
        return canonicalDataType;
    }

    protected void setCanonicalDataType(DataType canonicalDataType) {
        this.canonicalDataType = canonicalDataType;
    }

    public Integer getServerHandle() {
        return serverHandle;
    }

    protected void setServerHandle(Integer serverHandle) {
        if (serverHandle == null) {
            throw new NullPointerException("serverHandle may not be set to null.");
        }
        this.serverHandle = serverHandle;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        if (serverHandle != null && group != null) {
            return "[Item " + itemID + ", clientHandle=" + clientHandle + ", serverHandle=" + serverHandle + ", group=" + group + "]";
        } else {
            return "[Item " + itemID + ", clientHandle=" + clientHandle + "]";
        }
    }

    protected void setGroup(Group group) {
        this.group = group;
    }

    public static class State {

        private final Item item;
        private final Date timeStamp;
        private final short quality;
        private final short reserved;
        private final Object value;
        private final int error;

        protected State(Item item, Date timeStamp, short quality, short reserved, Object value, int error) {
            this.item = item;
            this.timeStamp = timeStamp;
            this.quality = quality;
            this.reserved = reserved;
            this.value = value;
            this.error = error;
        }

        public short getQuality() {
            return quality;
        }

        public short getReserved() {
            return reserved;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public Object getValue() {
            return value;
        }

        public int getError() {
            return error;
        }

        public Item getItem() {
            return item;
        }

        @Override
        public String toString() {
            return String.format("[Item.State item=%s, timeStamp=%s, value=%s, quality=%s, error=0x%x]",
                    String.valueOf(item), String.valueOf(timeStamp), String.valueOf(value), String.valueOf(quality), error);
        }
    }
}
