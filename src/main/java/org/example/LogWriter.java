package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {

    private static final String LOG_FILE = "diagnostics.log";
    private static PrintWriter writer;
    private static final Object lock = new Object();
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true)); // append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String msg) {
        String timestamp = df.format(new Date());
        String formatted = timestamp + " | " + msg;

        synchronized (lock) {
            // print to console
            System.out.println(formatted);

            // write to log file
            writer.println(formatted);
            writer.flush();
        }
    }

    public static void close() {
        synchronized (lock) {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}