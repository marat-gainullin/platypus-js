/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.sensors.positioning.PositioningPacket;
import com.eas.util.StringUtils;
import java.io.File;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xtreemfs.babudb.BabuDBFactory;
import org.xtreemfs.babudb.api.BabuDB;
import org.xtreemfs.babudb.api.DatabaseManager;
import org.xtreemfs.babudb.api.database.Database;
import org.xtreemfs.babudb.api.database.DatabaseRequestResult;
import org.xtreemfs.babudb.api.database.ResultSet;
import org.xtreemfs.babudb.api.exception.BabuDBException;
import org.xtreemfs.babudb.config.BabuDBConfig;
import org.xtreemfs.babudb.log.DiskLogger.SyncMode;

/**
 * @author ab
 */
public class PositioningPacketStorage {

    public final static String NAME_PACKET_CASHE = "posPacketDB";
    public final static String NAME_CASHE_FOLDER = "packetDB";
    public final static String NAME_NOT_VALID_CASHE_FOLDER = "notValidPacketDB";
    public final static String NAME_LOGS_FOLDER = "packetDB/logs";
    public final static int COUNT_STORAGES = 1;
    public final static Object CONTEXT = null;
    private BabuDB currentBase;
    private Database currentStorage;

    public PositioningPacketStorage() {
        this(true);
    }

    public PositioningPacketStorage(boolean validPackeges) {
        try {
            currentStorage = constructLocalStorage(validPackeges);
        } catch (BabuDBException ex) {
            Logger.getLogger(PositioningPacketStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getCasheDirectory(boolean validPackeges) {
        String path = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (path != null) {
            String dir = (validPackeges) ? NAME_CASHE_FOLDER : NAME_NOT_VALID_CASHE_FOLDER;
            File casheDir = new File(StringUtils.join(File.separator, path, ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, dir));
            if (!casheDir.exists()) {
                casheDir.mkdirs();
            }
            return casheDir.getAbsolutePath();
        } else {
            return null;
        }
    }

    private static String getLogsDirectory() {
        String path = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (path != null) {
            File logsDir = new File(StringUtils.join(File.separator, path, ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, NAME_LOGS_FOLDER));
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            return logsDir.getAbsolutePath();
        } else {
            return null;
        }
    }

    private Database constructLocalStorage(boolean validPackeges) throws BabuDBException {
        currentBase = BabuDBFactory.createBabuDB(new BabuDBConfig(getCasheDirectory(validPackeges), getLogsDirectory(), 4, 1024 * 1024 * 16, 5 * 60, SyncMode.FSYNC, 50, 0, false, 16, 1024 * 1024 * 512));
        DatabaseManager dbm = currentBase.getDatabaseManager();
        Database db;
        try {
            db = dbm.getDatabase(NAME_PACKET_CASHE);
        } catch (Exception e) {
            db = dbm.createDatabase(NAME_PACKET_CASHE, COUNT_STORAGES);
        }
        return db;
    }

    public synchronized void put(String aKey, PositioningPacket aValue) {
        currentStorage.singleInsert(0, aKey.getBytes(), aValue.writePacket(), CONTEXT);
    }

    public synchronized PositioningPacket get(String aKey) throws Exception {
        DatabaseRequestResult<byte[]> res = currentStorage.lookup(0, aKey.getBytes(), CONTEXT);
        if (res != null) {
            PositioningPacket pos = new PositioningPacket();
            pos.readPacket(res.get());
            return pos;
        } else {
            throw new Exception(String.format("Key %s does not exist.", aKey));
        }
    }

    public synchronized boolean containsKey(String aKey) {
        DatabaseRequestResult<byte[]> res = currentStorage.lookup(0, aKey.getBytes(), CONTEXT);
        if (res != null) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void remove(String aKey) {
        currentStorage.singleInsert(0, aKey.getBytes(), null, CONTEXT);
    }

    public synchronized boolean isEmpty() {
        DatabaseRequestResult<ResultSet<byte[], byte[]>> res = currentStorage.prefixLookup(0, null, CONTEXT);
        if (res != null) {
            try {
                return !res.get().hasNext();
            } catch (BabuDBException ex) {
                Logger.getLogger(PositioningPacketStorage.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
        } else {
            return true;
        }
    }

    public static String constructKey(PositioningPacket aPacket) {
        if (aPacket != null) {
            if (aPacket.getTime() != null && aPacket.getImei() != null && !"".equals(aPacket.getImei())) {
                return String.valueOf(aPacket.getTime().getTime()) + aPacket.getImei();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void destructLocalStorage() throws BabuDBException {
        currentStorage.shutdown();
        currentBase.shutdown();
    }
    /*
     private Iterator<Map.Entry<String, PositioningPacket>> getIterator() {
     DatabaseRequestResult<ResultSet<byte[], byte[]>> res = currentStorage.prefixLookup(0, null, CONTEXT);
     if (res != null) {
     try {
     return new StorageRecordIterator(res.get());
     } catch (BabuDBException ex) {
     Logger.getLogger(PositioningPacketStorage.class.getName()).log(Level.SEVERE, null, ex);
     return null;
     }
     } else {
     return null;
     }
     }

     public void saveStorageToBase(PacketReciever aReciver) {
     if (lck.tryLock()) {// Review synchronized methods and this "lck" lock.
     try {
     Iterator<Map.Entry<String, PositioningPacket>> itr = getIterator();
     if (itr.hasNext() && aReciver != null) {
     Entry<String, PositioningPacket> ent;
     BabuDBInsertGroup group = (BabuDBInsertGroup) currentStorage.createInsertGroup();
     while (itr.hasNext()) {
     ent = itr.next();
     aReciver.received(ent.getValue());
     Logger.getLogger(PositioningPacketStorage.class.getName()).log(Level.INFO, ent.getKey());
     group.addDelete(0, ent.getKey().getBytes());
     }
     currentStorage.insert(group, CONTEXT);
     }
     } finally {
     lck.unlock();
     }
     }
     }
     */

    public synchronized void closeStorage() {
        try {
            destructLocalStorage();
        } catch (BabuDBException ex) {
            Logger.getLogger(PositioningPacketStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class StorageRecordIterator implements Iterator<Map.Entry<String, PositioningPacket>> {

        private ResultSet<byte[], byte[]> currentSet;

        public StorageRecordIterator(ResultSet<byte[], byte[]> aData) {
            currentSet = aData;
        }

        @Override
        public boolean hasNext() {
            return currentSet.hasNext();
        }

        @Override
        public void remove() {
            currentSet.remove();
        }

        @Override
        public Map.Entry<String, PositioningPacket> next() {
            Map.Entry<byte[], byte[]> ent = currentSet.next();
            PositioningPacket pos = new PositioningPacket();
            pos.readPacket(ent.getValue());
            String key = new String(ent.getKey());
            Map.Entry<String, PositioningPacket> res = new AbstractMap.SimpleEntry<>(key, pos);
            return res;
        }
    }
}
