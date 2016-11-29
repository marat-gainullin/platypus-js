package com.eas.opc.hda;

import com.eas.opc.OPCCommon;
import com.eas.opc.da.dcom.FileTime;
import com.eas.opc.da.dcom.ResultTable;
import com.eas.opc.hda.dcom.IOPCHDA_Server;
import com.eas.opc.hda.dcom.IOPCHDA_SyncRead;
import com.eas.opc.hda.dcom.OPCHDA_ITEM;
import com.eas.opc.hda.dcom.OPCHDA_TIME;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pk
 */
public class OPCHDAServerConnection extends OPCCommon {

    protected IOPCHDA_Server opchdaServer;
    private final List<HistoryItem> knownItems = new ArrayList<>();
    private IOPCHDA_SyncRead syncRead;

    @Override
    public void connect(String progID, String domain, String hostname, String username, String password) throws JIException, UnknownHostException {
        super.connect(progID, domain, hostname, username, password);
        this.opchdaServer = new IOPCHDA_Server(opcCommon.queryInterface(IOPCHDA_Server.IID_IOPCHDA_Server));
        this.syncRead = new IOPCHDA_SyncRead(opcCommon.queryInterface(IOPCHDA_SyncRead.IID_IOPCHDA_SyncRead));
    }

    @Override
    public void disconnect() {
        cleanup();
        opchdaServer = null;
        syncRead = null;
        super.disconnect();
    }

    public int[] addItems(HistoryItem... items) throws JIException {
        final String[] itemIDs = new String[items.length];
        final Integer[] clientHandles = new Integer[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (knownItems.contains(items[i])) {
                    throw new IllegalArgumentException(String.format("Item %s is already added.", items[i].getItemID()));
                }
                itemIDs[i] = items[i].getItemID();
                clientHandles[i] = items[i].getClientHandle();
            }
        }
        final ResultTable<Integer, Integer> r = opchdaServer.getItemHandles(itemIDs, clientHandles);
        final int[] errors = new int[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                errors[i] = r.errorCode(items[i].getClientHandle());
                items[i].setServerHandle(r.get(items[i].getClientHandle()));
                if (errors[i] == 0 /* S_OK */) {
                    knownItems.add(items[i]);
                }
            }
        }
        return errors;
    }

    public int[] removeItems(HistoryItem... items) throws JIException {
        final Integer[] serverHandles = new Integer[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (!knownItems.contains(items[i]) || items[i].getServerHandle() == null) {
                    throw new IllegalArgumentException(String.format("Item %s is not added.", items[i].getItemID()));
                }
                serverHandles[i] = items[i].getServerHandle();
            }
        }
        final ResultTable<Integer, Void> r = opchdaServer.releaseItemHandles(serverHandles);
        final int[] errors = new int[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                errors[i] = r.errorCode(items[i].getServerHandle());
                knownItems.remove(items[i]);
            }
        }
        return errors;
    }

    public HistoryItem.History[] readRaw(Date startTime, Date endTime, int numValues, boolean withBounds, HistoryItem... items) throws JIException {
        final OPCHDA_TIME sTime = new OPCHDA_TIME(false, "", new FileTime(startTime)),
                eTime = new OPCHDA_TIME(false, "", new FileTime(endTime));
        final Integer[] serverHandles = new Integer[items.length];
        synchronized (this) {
            for (int i = 0; i < items.length; i++) {
                if (!knownItems.contains(items[i]) || items[i].getServerHandle() == null) {
                    throw new IllegalArgumentException(String.format("Item %s is not added.", items[i].getItemID()));
                }
                serverHandles[i] = items[i].getServerHandle();
            }
        }
        final IOPCHDA_SyncRead.ReadResult<OPCHDA_ITEM> r = syncRead.readRaw(sTime, eTime, numValues, withBounds, serverHandles);
        final HistoryItem.History[] histories = new HistoryItem.History[items.length];
        for (int i = 0; i < items.length; i++) {
            final OPCHDA_ITEM results = r.getResults().get(items[i].getServerHandle());
            final Date hStartTime = r.getStartTime().getTime().getTime();
            final Date hEndTime = r.getEndTime().getTime().getTime();
            final int aggregate = results.getAggregate();
            if (results.getClientHandle() == items[i].getClientHandle()) {
                final HistoryItem.History.Record[] records = new HistoryItem.History.Record[results.getCount()];
                for (int j = 0; j < records.length; j++) {
                    records[j] = new HistoryItem.History.Record(results.getTimeStamps()[j].getTime(), results.getQualities()[j], results.getValues()[j]);
                }
                histories[i] = new HistoryItem.History(hStartTime, hEndTime, items[i], aggregate, records);
            } else {
                Logger.getLogger(OPCHDAServerConnection.class.getName()).log(Level.WARNING, "OPCHDA_ITEM itemClientHandle={0}, OPCHDA_ITEM clientHandle={1}, serverHandle={2} at {3}", new Object[]{items[i].getClientHandle(), results.getClientHandle(), items[i].getServerHandle(), i});
            }
        }
        return histories;
    }

    public HistoryItem.History[] readRaw(Date startTime, Date endTime, int numValues, boolean withBounds) throws JIException {
        HistoryItem[] items;
        synchronized (this) {
            items = new HistoryItem[knownItems.size()];
            items = knownItems.toArray(items);
        }
        return readRaw(startTime, endTime, numValues, withBounds, items);
    }

    protected void cleanup() {
        synchronized (this) {
            HistoryItem[] items = new HistoryItem[knownItems.size()];
            items = knownItems.toArray(items);
            try {
                removeItems(items);
            } catch (JIException ex) {
                Logger.getLogger(OPCHDAServerConnection.class.getName()).log(Level.SEVERE, "error removing items on cleanup.", ex);
            }
            knownItems.clear();
        }
    }
}
