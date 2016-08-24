package com.umeng.dplus.leveldb.utils;

import java.io.File;
import java.io.IOException;

public class ApptrackLevelDbSnapshot {
    // TODO config file
    private static final String DATA_PATH = "/home/admin/processserver/data/leveldb/apptrack";

    public static LevelDbSnapshot getSnapshot(String table, String date) {
        return LevelDbSnapshot.getSnapshot("apptrack_" + table + "_" + date,
                new File(new File(DATA_PATH, table), date));
    }
}
