package net.phoenix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Bridge> bridges = new ArrayList<>();
        File file = new File(args[0]);
        List<String> bridgeCommands = Files.readAllLines(file.toPath());
        for (String bridgeCommand : bridgeCommands) {
            Thread thread = new Thread(() -> {
                Bridge bridge = new Bridge(bridgeCommand, "placeholder");
                bridges.add(bridge);
            });

            thread.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Bridge bridge : bridges) {
                bridge.kill();
            }
        }));
    }
}