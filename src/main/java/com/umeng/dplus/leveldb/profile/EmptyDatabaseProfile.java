package com.umeng.dplus.leveldb.profile;

import jline.console.ConsoleReader;

public class EmptyDatabaseProfile extends AbstractDatabaseProfile implements DatabaseProfile {
    public EmptyDatabaseProfile(ConsoleReader console) {
        super(console);
        console.setPrompt("> ");
    }
}
