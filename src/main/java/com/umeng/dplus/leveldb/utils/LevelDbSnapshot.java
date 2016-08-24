package com.umeng.dplus.leveldb.utils;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

public class LevelDbSnapshot implements Closeable {
    // TODO config file
    private static final String SNAPSHOT_PATH = "/home/admin/tmp/leveldb_snapshot";
    private static final long LEVEL_DB_CACHE_SIZE = 200 * 1048576L;

    private static final Map<String, LevelDbSnapshot> DB_MAP = new HashMap<String, LevelDbSnapshot>();
    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.createIfMissing(false);
        OPTIONS.cacheSize(LEVEL_DB_CACHE_SIZE);
    }

    private DB db;
    private File dataPath;
    private File snapshotPath;
    private boolean closed = false;

    public static LevelDbSnapshot getSnapshot(String tag) {
        return DB_MAP.get(tag);
    }

    public static LevelDbSnapshot getSnapshot(String tag, File dataPath) {
        LevelDbSnapshot snapshot = DB_MAP.get(tag);
        try {
            if (snapshot == null || snapshot.closed) {
                snapshot = new LevelDbSnapshot(tag, dataPath);
                DB_MAP.put(tag, snapshot);
            } else if (!snapshot.dataPath.equals(dataPath)) {
                try {
                    snapshot.close();
                } catch (IOException e) {
                    // ignore this
                }
                snapshot = new LevelDbSnapshot(tag, dataPath);
                DB_MAP.put(tag, snapshot);
            }
            return snapshot;
        } catch (IOException e) {
            return null;
        }
    }

    private LevelDbSnapshot(String tag, File dataPath) throws IOException {
        this.dataPath = dataPath;
        this.snapshotPath = new File(SNAPSHOT_PATH, tag);
        this.db = getDB();
    }

    private DB getDB() throws IOException {
        if (!dataPath.exists()) {
            throw new IOException();
        }

        RsyncUtils.sync(dataPath, snapshotPath, "LOCK");

        DB db;
        try {
            db = factory.open(snapshotPath, OPTIONS);
        } catch (IOException e) {
            System.err.println("DB open failed, trying to repair. Snapshot path: " + snapshotPath);  // TODO log4j
            factory.repair(snapshotPath, OPTIONS);
            db = factory.open(snapshotPath, OPTIONS);
        }
        return db;
    }

    public String get(String key) throws IOException {
        RsyncUtils.sync(dataPath, snapshotPath, "LOCK");

        byte[] value;
        try {
            value = db.get(key.getBytes());
        } catch (DBException e) {
            factory.repair(snapshotPath, OPTIONS);
            try {
                value = db.get(key.getBytes());
            } catch (DBException ex) {
                throw new IOException(ex);
            }
        }

        if (value == null) {
            return null;
        } else {
            String s = new String(value);
            return s.length() == 0 ? "(empty)" : s;
        }
    }

    public void close() throws IOException {
        // TODO delete snapshot directory
        db.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
