package com.umeng.dplus.leveldb;

import com.umeng.dplus.leveldb.jline.AggregateCompleter;
import com.umeng.dplus.leveldb.profile.ApptrackDatabaseProfile;
import com.umeng.dplus.leveldb.profile.DatabaseProfile;
import com.umeng.dplus.leveldb.profile.EmptyDatabaseProfile;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import java.io.IOException;

public class LevelDbCli {
    public static void main(String... args) throws IOException {
        ConsoleReader reader = new ConsoleReader();
        AggregateCompleter aggregateCompleter = new AggregateCompleter();
        aggregateCompleter.addCompleter(new ArgumentCompleter(new StringsCompleter("use"),
                new StringsCompleter("apptrack"), new NullCompleter()));
        aggregateCompleter.addCompleter(new ArgumentCompleter(new StringsCompleter("clear", "exit", "quit"),
                new NullCompleter()));
        reader.addCompleter(aggregateCompleter);

        DatabaseProfile profile = new EmptyDatabaseProfile(reader);
        String line;

        while ((line = reader.readLine()) != null) {
            String[] commandArgs = parseCommandLine(line);
            if (commandArgs.length == 0) {
                continue;
            }

            if (commandArgs[0].equals("use")) {
                if (commandArgs.length != 2) {
                    continue;
                }
                DatabaseProfile p;
                if ((p = changeDatabase(commandArgs[1], reader)) != null) {
                    profile.close();
                    profile = p;
                }

            } else if (commandArgs[0].equals("clear")) {
                if (commandArgs.length != 1) {
                    continue;
                }
                reader.clearScreen();

            } else if (commandArgs[0].equals("exit") || commandArgs[0].equals("quit")) {
                if (commandArgs.length != 1) {
                    continue;
                }
                profile.close();
                break;
            } else {
                profile.process(commandArgs);

            }
        }
    }

    private static DatabaseProfile changeDatabase(String database, ConsoleReader reader) {
        if (database.equals("apptrack")) {
            return new ApptrackDatabaseProfile(reader);
        } else {
            return null;
        }
    }

    private static String[] parseCommandLine(String line) {
        return line.toLowerCase().split("\\s");  // TODO parse command line, escape characters, etc.
    }
}
