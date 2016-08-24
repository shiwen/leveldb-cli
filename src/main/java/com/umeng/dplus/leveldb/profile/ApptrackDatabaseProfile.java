package com.umeng.dplus.leveldb.profile;

import com.umeng.dplus.leveldb.jline.MatchAllCompleter;
import com.umeng.dplus.leveldb.utils.ApptrackLevelDbSnapshot;
import com.umeng.dplus.leveldb.utils.LevelDbSnapshot;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApptrackDatabaseProfile extends AbstractDatabaseProfile implements DatabaseProfile {
    private static final String KEY_FLAG_IDFA = "#a#";
    private static final String KEY_FLAG_IDFV = "#v#";
    private static final String KEY_FLAG_IMEI = "#i#";
    private static final String KEY_FLAG_ANDROID_ID = "#n#";
    private static final String KEY_FLAG_MAC = "#m#";
    private static final String KEY_FLAG_IP = "#p#";
    private static final String KEY_FLAG_COOKIE = "#c#";

    private static final List<Completer> COMMON_COMPLETERS = new ArrayList<Completer>();
    private static final List<Completer> CLICK_COMPLETERS = new ArrayList<Completer>();
    private static final List<Completer> INSTALL_COMPLETERS = new ArrayList<Completer>();
    private static final List<Completer> COOKIE_COMPLETERS = new ArrayList<Completer>();

    static {
        ArgumentCompleter completer;

        completer = new ArgumentCompleter(new StringsCompleter("table"),
                new StringsCompleter("click", "install", "cookie"), new NullCompleter());
        COMMON_COMPLETERS.add(completer);

        completer = new ArgumentCompleter(new StringsCompleter("get"), new MatchAllCompleter(),
                new StringsCompleter("idfa", "idfv", "imei", "android_id", "mac", "ip"), new NullCompleter());
        CLICK_COMPLETERS.add(completer);

        completer = new ArgumentCompleter(new StringsCompleter("get"), new MatchAllCompleter(),
                new StringsCompleter("idfa", "idfv", "imei", "android_id", "mac"), new NullCompleter());
        INSTALL_COMPLETERS.add(completer);

        completer = new ArgumentCompleter(new StringsCompleter("get"), new MatchAllCompleter(),
                new StringsCompleter("ip"), new MatchAllCompleter(), new StringsCompleter("cookie"),
                new NullCompleter());
        COOKIE_COMPLETERS.add(completer);
    }

    private Table table = Table.UNKNOWN;

    public ApptrackDatabaseProfile(ConsoleReader reader) {
        super(reader);
        reader.setPrompt("apptrack> ");
        addCompleters(COMMON_COMPLETERS);
    }

    @Override
    public void process(String[] args) {
        if (args[0].equals("table")) {
            if (args.length != 2) {
                return;
            }

            if (table == Table.CLICK) {
                removeCompleters(CLICK_COMPLETERS);
            } else if (table == Table.INSTALL) {
                removeCompleters(INSTALL_COMPLETERS);
            } else if (table == Table.COOKIE) {
                removeCompleters(COOKIE_COMPLETERS);
            }

            if (args[1].equals("click")) {
                table = Table.CLICK;
                reader.setPrompt("apptrack[click]> ");
                addCompleters(CLICK_COMPLETERS);
            } else if (args[1].equals("install")) {
                table = Table.INSTALL;
                reader.setPrompt("apptrack[install]> ");
                addCompleters(INSTALL_COMPLETERS);
            } else if (args[1].equals("cookie")) {
                table = Table.COOKIE;
                reader.setPrompt("apptrack[cookie]> ");
                addCompleters(COOKIE_COMPLETERS);
            } else {
                table = Table.UNKNOWN;
                reader.setPrompt("apptrack> ");
            }

        } else if (args[0].equals("get")) {
            if (table == Table.UNKNOWN) {
                out.println("Error: table not set");
                return;
            }

            String key = null;
            if (table == Table.CLICK) {
                key = getClickKey(args);
            } else if (table == Table.INSTALL) {
                key = getInstallKey(args);
            } else if (table == Table.COOKIE) {
                key = getCookieKey(args);
            }
            if (key == null) {
                return;
            }

            for (String date : getTimeSpan()) {
                LevelDbSnapshot snapshot = ApptrackLevelDbSnapshot.getSnapshot(table.getName(), date);
                if (snapshot != null) {
                    String value = null;
                    try {
                        value = snapshot.get(key);
                    } catch (IOException e) {
                        e.printStackTrace();  // TODO all exceptions to log4j
                    }
                    if (value != null) {
                        out.println(value);
                        break;
                    }
                }
            }
        }
    }

    private String getClickKey(String[] args) {
        if (args.length != 4) {
            return null;
        }

        String key = args[1];

        if (args[2].equals("idfa")) {
            key += KEY_FLAG_IDFA;
        } else if (args[2].equals("idfv")) {
            key += KEY_FLAG_IDFV;
        } else if (args[2].equals("imei")) {
            key += KEY_FLAG_IMEI;
        } else if (args[2].equals("android_id")) {
            key += KEY_FLAG_ANDROID_ID;
        } else if (args[2].equals("mac")) {
            key += KEY_FLAG_MAC;
        } else if (args[2].equals("ip")) {
            key += KEY_FLAG_IP;
        } else {
            return null;
        }

        key += args[3];
        return key;
    }

    private String getInstallKey(String[] args) {
        if (args.length != 4) {
            return null;
        }

        String key = args[1];

        if (args[2].equals("idfa")) {
            key += KEY_FLAG_IDFA;
        } else if (args[2].equals("idfv")) {
            key += KEY_FLAG_IDFV;
        } else if (args[2].equals("imei")) {
            key += KEY_FLAG_IMEI;
        } else if (args[2].equals("android_id")) {
            key += KEY_FLAG_ANDROID_ID;
        } else if (args[2].equals("mac")) {
            key += KEY_FLAG_MAC;
        } else {
            return null;
        }

        key += args[3];
        return key;
    }

    private String getCookieKey(String[] args) {
        if (args.length != 6 || !args[2].equals("ip") || !args[4].equals("cookie")) {
            return null;
        }
        return args[1] + KEY_FLAG_IP + args[3] + KEY_FLAG_COOKIE + args[5];
    }

    private List<String> getTimeSpan() {
        List<String> timeSpan = new ArrayList<String>();
        LocalDate date = new LocalDate();
        for (int i = 0; i < 15; i++) {
            timeSpan.add(date.toString());
            date = date.minusDays(1);
        }
        return timeSpan;
    }

    @Override
    public void close() {
        reader.setPrompt("> ");
        removeCompleters(COMMON_COMPLETERS);
    }

    private enum Table {
        CLICK("click"),
        INSTALL("installation"),
        COOKIE("cookie"),
        UNKNOWN("");

        private final String name;

        Table(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }
}
