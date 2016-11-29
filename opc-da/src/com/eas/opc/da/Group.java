/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

import com.eas.opc.da.Item.State;
import com.eas.opc.da.dcom.IOPCAsyncIO2;
import com.eas.opc.da.dcom.IOPCAsyncIO2.ReadResult;
import com.eas.opc.da.dcom.IOPCGroupStateMgt;
import com.eas.opc.da.dcom.IOPCGroupStateMgt.GroupState;
import com.eas.opc.da.dcom.IOPCItemMgt;
import com.eas.opc.da.dcom.IOPCSyncIO;
import com.eas.opc.da.dcom.OPCDataListener;
import com.eas.opc.da.dcom.OPCITEMDEF;
import com.eas.opc.da.dcom.OPCITEMRESULT;
import com.eas.opc.da.dcom.OPCITEMSTATE;
import com.eas.opc.da.dcom.ResultTable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class Group {

    private String name;
    private boolean active;
    private int requestedUpdateRate;
    private int revisedUpdateRate;
    private int clientHandle;
    private int serverHandle;
    private Integer timeBias;
    private Float percentDeadBand;
    private int localeID;
    protected final IOPCGroupStateMgt groupStateMgt;
    protected final IOPCItemMgt itemMgt;
    protected final IOPCSyncIO syncIO;
    protected final IOPCAsyncIO2 asyncIO;
    private final List<Item> knownItems = new ArrayList<>();
    private final Map<Integer, Item> knownItemsByClientHandle = new HashMap<>();
    private final List<DataListener> dataListeners = new ArrayList<>();
    private final InternalDataListener internalListener = new InternalDataListener();

    protected Group(String name, boolean active, int requestedUpdateRate, int revisedUpdateRate, int clientHandle, int serverHandle, Integer timeBias, Float percentDeadBand, int localeID, IOPCGroupStateMgt groupStateMgt) throws JIException {
        this.name = name;
        this.active = active;
        this.requestedUpdateRate = requestedUpdateRate;
        this.revisedUpdateRate = revisedUpdateRate;
        this.clientHandle = clientHandle;
        this.serverHandle = serverHandle;
        this.timeBias = timeBias;
        this.percentDeadBand = percentDeadBand;
        this.localeID = localeID;
        this.groupStateMgt = groupStateMgt;
        this.itemMgt = new IOPCItemMgt(groupStateMgt.queryInterface(IOPCItemMgt.IID_IOPCItemMgt));
        this.syncIO = new IOPCSyncIO(groupStateMgt.queryInterface(IOPCSyncIO.IID_IOPCSyncIO));
        this.asyncIO = new IOPCAsyncIO2(groupStateMgt.queryInterface(IOPCAsyncIO2.IID_IOPCAsyncIO2));
        this.asyncIO.addDataListener(internalListener);
    }

    public void addDataListener(DataListener l) {
        synchronized (dataListeners) {
            dataListeners.add(l);
        }
    }

    public void removeDataListener(DataListener l) {
        synchronized (dataListeners) {
            dataListeners.remove(l);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) throws JIException {
        if (this.active != active) {
            final boolean oldValue = this.active;
            this.active = active;
            try {
                updateStateOnServer();
            } catch (JIException ex) {
                this.active = oldValue;
                throw ex;
            }
        }
    }

    public int getClientHandle() {
        return clientHandle;
    }

    public void setClientHandle(int clientHandle) throws JIException {
        if (this.clientHandle != clientHandle) {
            final int oldValue = this.clientHandle;
            this.clientHandle = clientHandle;
            try {
                updateStateOnServer();
            } catch (JIException ex) {
                this.clientHandle = oldValue;
                throw ex;
            }
        }
    }

    public int getLocaleID() {
        return localeID;
    }

    public void setLocaleID(int localeID) throws JIException {
        if (this.localeID != localeID) {
            final int oldValue = this.localeID;
            this.localeID = localeID;
            try {
                updateStateOnServer();
            } catch (JIException ex) {
                this.localeID = oldValue;
                throw ex;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws JIException {
        groupStateMgt.setName(name);
        this.name = name;
    }

    public Float getPercentDeadBand() {
        return percentDeadBand;
    }

    public void setPercentDeadBand(Float percentDeadBand) throws JIException {
        if (this.percentDeadBand == null && percentDeadBand != null || this.percentDeadBand != null && !this.percentDeadBand.equals(percentDeadBand)) {
            final Float oldValue = this.percentDeadBand;
            this.percentDeadBand = percentDeadBand;
            try {
                updateStateOnServer();

            } catch (JIException ex) {
                this.percentDeadBand = oldValue;
                throw ex;
            }
        }
    }

    public int getRequestedUpdateRate() {
        return requestedUpdateRate;
    }

    public void setRequestedUpdateRate(int requestedUpdateRate) throws JIException {
        if (this.requestedUpdateRate != requestedUpdateRate) {
            final int oldValue = this.requestedUpdateRate;
            this.requestedUpdateRate = requestedUpdateRate;
            try {
                updateStateOnServer();
            } catch (JIException ex) {
                this.requestedUpdateRate = oldValue;
                throw ex;
            }
        }
    }

    public int getRevisedUpdateRate() {
        return revisedUpdateRate;
    }

    public int getServerHandle() {
        return serverHandle;
    }

    public Integer getTimeBias() {
        return timeBias;
    }

    public void setTimeBias(Integer timeBias) throws JIException {
        if (this.timeBias == null && timeBias != null || this.timeBias != null && !this.timeBias.equals(timeBias)) {
            final Integer oldValue = this.timeBias;
            this.timeBias = timeBias;
            try {
                updateStateOnServer();
            } catch (JIException ex) {
                this.timeBias = oldValue;
                throw ex;
            }
        }
    }

    public void updateState() throws JIException {
        final GroupState state = groupStateMgt.getState();
        name = state.getName();
        clientHandle = state.getClientHandle();
        localeID = state.getLocaleId();
        percentDeadBand = state.getPercentDeadband();
        serverHandle = state.getServerHandle();
        timeBias = state.getTimeBias();
        revisedUpdateRate = state.getUpdateRate();
    }

    public int[] addItems(Item... items) throws JIException {
        synchronized (this) {
            for (Item i : items) {
                if (knownItems.contains(i)) {
                    throw new IllegalArgumentException(String.format("Item %s is already added.", i.getItemID()));
                }
            }
        }
        final OPCITEMDEF[] itemsDefs = buildItemsDefs(items);
        final ResultTable<OPCITEMDEF, OPCITEMRESULT> result = itemMgt.addItems(itemsDefs);
        final int[] errors = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            errors[i] = result.errorCode(itemsDefs[i]);
            final OPCITEMRESULT itemResult = result.get(itemsDefs[i]);
            items[i].setAccessRights(itemResult.getAccessRights());
            items[i].setCanonicalDataType(DataType.getDataType(itemResult.getCanonicalDataType()));
            items[i].setReserved(itemResult.getReserved());
            items[i].setServerHandle(itemResult.getServerHandle());
        }
        synchronized (this) {
            for (Item i : items) {
                knownItems.add(i);
                knownItemsByClientHandle.put(i.getClientHandle(), i);
                i.setGroup(this);
            }
        }
        return errors;
    }

    public int[] validateItems(Item... items) throws JIException {
        final OPCITEMDEF[] itemsDefs = buildItemsDefs(items);
        final ResultTable<OPCITEMDEF, OPCITEMRESULT> result = itemMgt.validateItems(itemsDefs, false);
        final int[] errors = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            errors[i] = result.errorCode(itemsDefs[i]);
            final OPCITEMRESULT itemResult = result.get(itemsDefs[i]);
            items[i].setAccessRights(itemResult.getAccessRights());
            items[i].setCanonicalDataType(DataType.getDataType(itemResult.getCanonicalDataType()));
            items[i].setReserved(itemResult.getReserved());
            items[i].setServerHandle(itemResult.getServerHandle());
        }
        return errors;
    }

    public int[] removeItems(Item... items) throws JIException {
        synchronized (this) {
            for (Item i : items) {
                if (!knownItems.contains(i)) {
                    throw new IllegalArgumentException(String.format("Item %s is not added.", i.getItemID()));
                }
            }
        }
        final Integer[] serverHandles = new Integer[items.length];
        for (int i = 0; i < items.length; i++) {
            serverHandles[i] = items[i].getServerHandle();
        }
        final Map<Integer, Integer> result = itemMgt.removeItems(serverHandles);
        final int[] errors = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            errors[i] = result.get(items[i].getServerHandle());
        }
        synchronized (this) {
            for (Item i : items) {
                knownItems.remove(i);
                knownItemsByClientHandle.remove(i.getClientHandle());
            }
        }
        return errors;
    }

    public int[] removeAllItems() throws JIException {
        Item[] items;
        synchronized (this) {
            items = new Item[knownItems.size()];
            items = knownItems.toArray(items);
        }
        return removeItems(items);
    }

    public Item[] getItems() {
        Item[] result;
        synchronized (this) {
            result = new Item[knownItems.size()];
            result = knownItems.toArray(result);
        }
        return result;
    }

    public Item.State[] read(DataSource ds, Item... items) throws JIException {
        final int[] serverHandles = new int[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (!knownItemsByClientHandle.containsKey(items[i].getClientHandle())) {
                    throw new IllegalArgumentException("Group does not contain item " + items[i]);
                } else if (items[i].getServerHandle() == null) {
                    throw new IllegalArgumentException("" + items[i] + " has not been added to any group.");
                } else {
                    serverHandles[i] = items[i].getServerHandle();
                }
            }
        }
        final ResultTable<Integer, OPCITEMSTATE> out = syncIO.read(ds.getValue(), serverHandles);
        final Item.State[] result = new Item.State[items.length];
        for (int i = 0; i < items.length; i++) {
            final OPCITEMSTATE state = out.get(items[i].getServerHandle());
            result[i] = new Item.State(items[i], state.getTimeStamp().getTime(),
                    state.getQuality(), (short) 0, state.getDataValue().getObject(),
                    out.errorCode(items[i].getServerHandle()));
        }
        return result;
    }

    public Item.State[] read(DataSource ds) throws JIException {
        Item[] items;
        synchronized (this) {
            items = new Item[knownItems.size()];
            items = knownItems.toArray(items);
        }
        return read(ds, items);
    }

    public void readRawHistory(Date startTime, Date endTime, int numValues, boolean withBounds, Item... items) {
        final Integer[] serverHandles = new Integer[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (!knownItemsByClientHandle.containsKey(items[i].getClientHandle())) {
                    throw new IllegalArgumentException("Group does not contain item " + items[i]);
                } else if (items[i].getServerHandle() == null) {
                    throw new IllegalArgumentException("" + items[i] + " has not been added to any group.");
                } else {
                    serverHandles[i] = items[i].getServerHandle();
                }
            }
        }
    }

    public int[] write(Object... data) throws JIException {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("syntax: write(item1, value1, item2, value2, ...");
        }
        final int[] serverHandles = new int[data.length / 2];
        final JIVariant[] values = new JIVariant[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            if (!(data[i] instanceof Item)) {
                throw new IllegalArgumentException("syntax: write(item1, value1, item2, value2, ...");
            }
            Item item = (Item) data[i * 2];
            if (item.getServerHandle() == null || item.getGroup() != this) {
                throw new IllegalArgumentException("" + item + " not in this group.");
            }
            serverHandles[i] = item.getServerHandle();
            values[i] = new JIVariant(data[i * 2 + 1], false);
        }
        final Map<Integer, Integer> result = syncIO.write(serverHandles, values);
        final int[] errors = new int[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            errors[i] = result.get(serverHandles[i]);
        }
        return errors;
    }

    public AsyncOperationInfo readAsync(int transactionID, Item... items) throws JIException {
        final int[] serverHandles = new int[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (!knownItemsByClientHandle.containsKey(items[i].getClientHandle())) {
                    throw new IllegalArgumentException("Group does not contain item " + items[i]);
                } else if (items[i].getServerHandle() == null) {
                    throw new IllegalArgumentException("" + items[i] + " has not been added to any group.");
                } else {
                    serverHandles[i] = items[i].getServerHandle();
                }
            }
        }
        final ReadResult r = asyncIO.read(serverHandles, transactionID);
        final int[] errors = new int[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            errors[i] = r.getErrorCodes().get(serverHandles[i]);
        }
        return new AsyncOperationInfo(r.getCancelId(), errors);
    }

    public AsyncOperationInfo writeAsync(int transactionID, Object... data) throws JIException {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("syntax: writeAsync(transid, item1, value1, item2, value2, ...");
        }
        final int[] serverHandles = new int[data.length / 2];
        final JIVariant[] values = new JIVariant[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            if (!(data[i] instanceof Item)) {
                throw new IllegalArgumentException("syntax: writeAsync(transid, item1, value1, item2, value2, ...");
            }
            Item item = (Item) data[i * 2];
            if (item.getServerHandle() == null || item.getGroup() != this) {
                throw new IllegalArgumentException("" + item + " not in this group.");
            }
            serverHandles[i] = item.getServerHandle();
            values[i] = new JIVariant(data[i * 2 + 1], false);
        }
        final ReadResult r = asyncIO.write(serverHandles, values, transactionID);
        final int[] errors = new int[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            errors[i] = r.getErrorCodes().get(serverHandles[i]);
        }
        return new AsyncOperationInfo(r.getCancelId(), errors);
    }

    public AsyncOperationInfo refreshAsync(DataSource ds, int transactionID) throws JIException {
        final int cancelID = asyncIO.refresh2(ds.getValue(), transactionID);
        return new AsyncOperationInfo(cancelID, new int[0]);
    }

    public void cancelAsync(AsyncOperationInfo info) throws JIException {
        asyncIO.cancel2(info.getCancelID());
    }

    public boolean isSubscriptionEnabled() throws JIException {
        return asyncIO.getEnable();
    }

    public void setSubscriptionEnabled(boolean enabled) throws JIException {
        asyncIO.setEnable(enabled);
    }

    @Override
    public String toString() {
        return String.format("[OPC DA group %s, clientHandle=%d, serverHandle=%d]", name, clientHandle, serverHandle);
    }

    protected void cleanup() throws JIException {
        synchronized (this) {
            asyncIO.removeDataListener(internalListener);
            removeAllItems();
        }
    }

    private void updateStateOnServer() throws JIException {
        final GroupState state = new GroupState();
        state.setActive(active);
        state.setClientHandle(clientHandle);
        state.setLocaleId(localeID);
        state.setName(name);
        state.setPercentDeadband(percentDeadBand);
        state.setServerHandle(serverHandle);
        state.setTimeBias(timeBias);
        state.setUpdateRate(requestedUpdateRate);
        this.revisedUpdateRate = groupStateMgt.setState(state);
    }

    private OPCITEMDEF[] buildItemsDefs(Item[] items) {
        final OPCITEMDEF[] itemsDefs = new OPCITEMDEF[items.length];
        for (int i = 0; i < items.length; i++) {
            itemsDefs[i] = new OPCITEMDEF();
            itemsDefs[i].setAccessPath(items[i].getAccessPath());
            itemsDefs[i].setActive(items[i].isActive());
            itemsDefs[i].setClientHandle(items[i].getClientHandle());
            itemsDefs[i].setItemID(items[i].getItemID());
            itemsDefs[i].setRequestedDataType(items[i].getRequestedDataType().getTypeID());
            itemsDefs[i].setReserved(items[i].getReserved());
        }
        return itemsDefs;
    }

    public static class AsyncOperationInfo {

        private final int[] errors;
        private final int cancelID;

        public AsyncOperationInfo(int cancelID, int[] errors) {
            this.errors = errors;
            this.cancelID = cancelID;
        }

        public int getCancelID() {
            return cancelID;
        }

        public int[] getErrors() {
            return errors;
        }
    }

    private class InternalDataListener implements OPCDataListener {

        private DataListener[] getListeners() {
            DataListener[] result;
            synchronized (Group.this.dataListeners) {
                result = new DataListener[Group.this.dataListeners.size()];
                result = Group.this.dataListeners.toArray(result);
            }
            return result;
        }

        private Item.State[] getResult(Integer[] clientHandles, Object[] values, Short[] qualities, Date[] timeStamps, Integer[] errors) {
            final List<State> states = new ArrayList<>(clientHandles.length);
            for (int i = 0; i < clientHandles.length; i++) {
                final Item item = Group.this.knownItemsByClientHandle.get(clientHandles[i]);
                if (item != null) {
                    states.add(new Item.State(item, timeStamps == null ? null : timeStamps[i], qualities == null ? 0 : qualities[i],
                            (short) 0, values == null ? null : values[i], errors == null ? 0 : errors[i]));
                }
            }
            State[] result = new Item.State[states.size()];
            result = states.toArray(result);
            return result;
        }

        public void dataChanged(Integer transid, Integer group, Integer masterQuality, Integer masterError, Integer[] clientHandles, Object[] values, Short[] qualities, Date[] timeStamps, Integer[] errors) {
            final DataListener[] listeners = getListeners();
            final Item.State[] result = getResult(clientHandles, values, qualities, timeStamps, errors);
            for (DataListener l : listeners) {
                l.dataChanged(transid, Group.this, masterQuality, masterError, result);
            }
        }

        public void readCompleted(Integer transid, Integer group, Integer masterQuality, Integer masterError, Integer[] clientHandles, Object[] values, Short[] qualities, Date[] timeStamps, Integer[] errors) {
            final DataListener[] listeners = getListeners();
            final Item.State[] result = getResult(clientHandles, values, qualities, timeStamps, errors);
            for (DataListener l : listeners) {
                l.readCompleted(transid, Group.this, masterQuality, masterError, result);
            }
        }

        public void writeCompleted(Integer transid, Integer group, Integer masterError, Integer[] clientHandles, Integer[] errors) {
            final DataListener[] listeners = getListeners();
            final Item.State[] result = getResult(clientHandles, null, null, null, errors);
            for (DataListener l : listeners) {
                l.writeCompleted(transid, Group.this, masterError, result);
            }
        }

        public void cancelCompleted(Integer transid, Integer group) {
            final DataListener[] listeners = getListeners();
            for (DataListener l : listeners) {
                l.cancelCompleted(transid, Group.this);
            }
        }
    }
}
