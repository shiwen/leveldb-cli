package com.umeng.dplus;

public class EmptyDatabaseProfile implements DatabaseProfile {
    @Override
    public String process(String line) {
        return null;
    }
}
