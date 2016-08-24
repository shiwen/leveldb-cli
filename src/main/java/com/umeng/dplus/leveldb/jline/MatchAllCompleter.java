package com.umeng.dplus.leveldb.jline;

import jline.console.completer.Completer;

import java.util.List;

public class MatchAllCompleter implements Completer {
    @Override
    public int complete(String s, int i, List<CharSequence> list) {
        if (s != null) {
            list.add(s);
        }
        return 0;
    }
}
