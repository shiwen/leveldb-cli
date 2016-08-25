package com.umeng.dplus.leveldb.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolicLinkUtils {
    public static void createLink(File src, File dest, String... exclusions) throws IOException {
        if (!src.isDirectory()) {
            return;
        }

        FileUtils.forceMkdir(dest);

        List<String> exclusionList = Arrays.asList(exclusions);
        File[] files = src.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            String fileName = file.getName();
            if (exclusionList.contains(fileName)) {
                continue;
            }

            File target = new File(dest, fileName);
            if (target.exists()) {
                continue;
            }

            List<String> cmd = new ArrayList<String>();
            cmd.addAll(Arrays.asList("ln", "-s", file.getAbsolutePath(), target.getAbsolutePath()));

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
}
