package com.umeng.dplus;

import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LevelDBCli {
    public static void main(String... args) throws IOException {
        ConsoleReader reader = new ConsoleReader();
        reader.setPrompt("leveldb> ");
        //        reader.addCompleter(new AggregateCompleter(
        ////                        new ArgumentCompleter(new StringsCompleter("show"), new NullCompleter()),
        //                        new ArgumentCompleter(new StringsCompleter("show"), new StringsCompleter("aaa",
        //                                "access-expression", "access-lists", "accounting", "adjancey"), new
        // NullCompleter()),
        ////                        new ArgumentCompleter(new StringsCompleter("show"), new StringsCompleter("ip"), new
        ////                                StringsCompleter("access-lists", "accounting", "admission", "aliases",
        // "arp"), new
        ////                                NullCompleter()),
        ////                        new ArgumentCompleter(new StringsCompleter("show"), new StringsCompleter("ip"), new
        ////                                StringsCompleter("interface"), new StringsCompleter("ATM", "Async",
        // "BVI"), new
        ////                                NullCompleter()),
        //                        new ArgumentCompleter(new StringsCompleter("set"), new NullCompleter())
        //                )
        //        );
        ArgumentCompleter completer = new ArgumentCompleter(new StringsCompleter("set", "get"), new NullCompleter(), new StringsCompleter("aaa", "bbb"));
        completer.setStrict(false);
        reader.addCompleter(completer);

//        reader.addCompleter(new AggregateCompleter(new FileNameCompleter(),
//            new CustomCompleter(),
//            new ArgumentCompleter(new StringsCompleter("set", "get"), new NullCompleter(), new StringsCompleter("a", "b"))));


        String line;
        PrintWriter out = new PrintWriter(reader.getOutput());

        while ((line = reader.readLine()) != null) {
            out.println("======>\"" + line + "\"");
            out.flush();

            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                break;
            }
            if (line.equalsIgnoreCase("clear")) {
                reader.clearScreen();
            }
        }
    }
}

class CustomCompleter implements Completer {
    public int complete(String s, int i, List<CharSequence> list) {
        System.err.println("###DEBUG### s: " + s);
        System.err.println("###DEBUG### i: " + i);
        return -1;
    }
}
