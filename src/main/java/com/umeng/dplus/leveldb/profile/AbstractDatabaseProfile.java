package com.umeng.dplus.leveldb.profile;

import com.umeng.dplus.leveldb.jline.AggregateCompleter;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;

import java.io.PrintWriter;
import java.util.Collection;

public abstract class AbstractDatabaseProfile implements DatabaseProfile {
    protected ConsoleReader reader;
    protected PrintWriter out;
    private AggregateCompleter completer;

    public AbstractDatabaseProfile(ConsoleReader reader) {
        this.reader = reader;
        this.out = new PrintWriter(reader.getOutput(), true);

        Collection<Completer> cs = reader.getCompleters();
        if (cs.size() == 1) {
            Completer c = cs.iterator().next();
            if (c instanceof AggregateCompleter) {
                completer = (AggregateCompleter) c;
            }
        }
    }

    @Override
    public void process(String[] args) {
    }

    @Override
    public void close() {
    }

    protected void addCompleters(Collection<Completer> completers) {
        for (Completer completer : completers) {
            this.completer.addCompleter(completer);
        }
    }

    protected void removeCompleters(Collection<Completer> completers) {
        for (Completer completer : completers) {
            this.completer.removeCompleter(completer);
        }
    }
}
