package com.umeng.dplus;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LevelDBCli {
    public static void main(String... args) throws IOException {
        ConsoleReader reader = new ConsoleReader();
        reader.setPrompt("leveldb> ");
        ArgumentCompleter completer = new ArgumentCompleter(new StringsCompleter("set", "get"), new NullCompleter(), new StringsCompleter("aaa", "bbb"));
        completer.setStrict(false);
        reader.addCompleter(completer);

        String line;
        PrintWriter out = new PrintWriter(reader.getOutput());

        DatabaseProfile profile = new EmptyDatabaseProfile(); // TODO empty database profile, 'use' completion

        while ((line = reader.readLine()) != null) {
            DatabaseProfile p = useDatabase(line);
            if (p != null) {
                profile = p;
            }

            profile.process(line, reader, out);
//            if (result != null) {
//                out.println(result);
//                out.flush();
//            } else if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
//                profile.onExit();
//                break;
//            } else if (line.equalsIgnoreCase("clear")) {
//                reader.clearScreen();
//            }
        }
    }

    private static DatabaseProfile useDatabase(String line) {
        return null;
    }
}

class CustomCompleter implements Completer {
    public int complete(String s, int i, List<CharSequence> list) {
        System.err.println("###DEBUG### s: " + s);
        System.err.println("###DEBUG### i: " + i);
        return -1;
    }
}
