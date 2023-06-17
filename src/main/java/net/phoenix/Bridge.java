package net.phoenix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bridge {

    private final ProcessBuilder pb;
    private final String guildName;
    private Process process;
    private File log;
    private boolean killed = false;

    public Bridge(String startCommand, String guildName) {
        this.guildName = guildName;
        pb = new ProcessBuilder("bash", "-c", startCommand);
        log = new File(String.format("./%s.log", guildName));
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
        try {
            process = pb.start();
        } catch (IOException e) {
            try (FileWriter writer = new FileWriter(log)) {
                writer.write(e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

        restart();
    }

    public Process getProcess() {
        return process;
    }

    public void kill() {
        process.destroy();
        String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        File rename = new File(String.format("./%s-%s.log", guildName, date));
        log.renameTo(rename);
        killed = true;
    }

    public void restart() {
        if (killed) return;
        try {
            process.waitFor();
            String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
            File rename = new File(String.format("./%s-%s.log", guildName, date));
            boolean success = log.renameTo(rename);
            log = new File(String.format("%s", guildName));
            process = pb.start();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
