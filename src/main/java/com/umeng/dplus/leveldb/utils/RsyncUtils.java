package com.umeng.dplus.leveldb.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RsyncUtils {
    public static void sync(File src, File dest, String... exclusions) throws IOException {
        List<String> cmd = new ArrayList<String>();
        cmd.addAll(Arrays.asList("rsync", "-rz", src.getAbsolutePath() + "/", dest.getAbsolutePath()));
        for (String exclusion : exclusions) {
            cmd.add("--exclude");
            cmd.add(exclusion);
        }
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();

        try {
            if (p.waitFor() != 0) {
                throw new IOException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
