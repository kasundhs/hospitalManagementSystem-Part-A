package org.example;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenrator {
    private static String reportName;
    private static PrintWriter writer;
    private static final Object lock = new Object();
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public ReportGenrator(TestOrder order){

        File folder = new File("Test Report");
        if(!folder.exists()){
            folder.mkdirs();   // create folder
        }

        // Set report file path inside folder
        this.reportName = "Test Report/" + order.toString() + " Report.log";

        try {
            writer = new PrintWriter(new FileWriter(reportName, true)); // append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reportDetails(TestOrder order) {
        String timestamp = df.format(new Date());
        String formatted = " Time: "+timestamp + "\n Order ID: " + order.toString() + "\n Order Priority: "+order.priority;

        synchronized (lock) {
            // write to log file
            writer.println(formatted);
            writer.flush();
        }
    }
}
