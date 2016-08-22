package com.umeng.dplus;

import jline.console.ConsoleReader;

import java.io.PrintWriter;

public interface DatabaseProfile {

    String process(String line, ConsoleReader reader, PrintWriter out);

    void onExit();

}
