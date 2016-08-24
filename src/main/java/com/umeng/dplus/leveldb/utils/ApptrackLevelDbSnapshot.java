package com.umeng.dplus.leveldb.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ApptrackLevelDbSnapshot {
    // TODO config file
    private static final String DATA_PATH = "/home/admin/processserver/data/leveldb/apptrack";

    private static final Set<String> TAGS = new HashSet<String>();

    public static LevelDbSnapshot getSnapshot(String table, String date) {
        LevelDbSnapshot snapshot = LevelDbSnapshot.getSnapshot("apptrack_" + table + "_" + date,
                new File(new File(DATA_PATH, table), date));
        if (snapshot != null) {
            TAGS.add("apptrack_" + table + "_" + date);
        }
        return snapshot;
    }

    public static void close() {
        for (String tag : TAGS) {
            LevelDbSnapshot snapshot = LevelDbSnapshot.getSnapshot(tag);
            if (snapshot != null && !snapshot.isClosed()) {
                try {
                    snapshot.close();
                } catch (IOException e) {
                    e.printStackTrace();  // TODO all exceptions to log4j
                }
            }
        }
    }
}
