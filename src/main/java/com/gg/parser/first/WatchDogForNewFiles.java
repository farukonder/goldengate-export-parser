package com.gg.parser.first;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WatchDogForNewFiles extends Thread{

    protected static void traverseExistingFiles(){
        try  {
            Stream<Path> walk = Files.walk(Paths.get(TailMain.watchDirectory));
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            result.forEach(System.out::println);
            TailMain.filesInFolder.addAll(result);

            System.out.println("Initial List: "+ TailMain.filesInFolder);
            Collections.sort(TailMain.filesInFolder);
            System.out.println("Sorted List: "+ TailMain.filesInFolder);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(TailMain.watchDirectory);

            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            while (true) {


                WatchKey key;
                try {
                    // wait for a key to be available
                    key = watchService.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    break;
                }

                if (key != null) {
                    Path pathToReload = path;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent<Path> we = (WatchEvent<Path>) event;
                        Path checkDirectory = we.context();
                        File checkFile = pathToReload.resolve(checkDirectory).toFile();
                        System.out.println(
                                "Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                        System.out.println(
                                "Event kind:" + event.kind() + ". File affected: " + checkFile + ".");
                        TailMain.filesInFolder.add(checkFile.toString());
                    }

                    // the key must be reset after processed
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
