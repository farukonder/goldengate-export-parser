package com.gg.parser.option2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TailMain {

    protected static String watchDirectory = "/home/faruk/IdeaProjects/goldengate-export-parser/src/main/resources/xml";
    static FileInputStream fileStream;

    static List<String> filesInFolder = Collections.synchronizedList(new LinkedList<String>());
    static List<ByteArrayInputStream> streamsToParse = Collections.synchronizedList(new LinkedList<ByteArrayInputStream>());

    public static void main(String[] args) {
        try {


            // first start watching new file creations
            WatchDogForNewFiles watchDogForNewFiles = new WatchDogForNewFiles();
            watchDogForNewFiles.start();


            //then list the existing files. this must be done after watcher started.
            // otherwise we might miss the new created files
            // TODO possibly same file can be added very small risk. we might check the uniq file name in list
            WatchDogForNewFiles.traverseExistingFiles();


            WatchDogForParseBuffers watchDogForParseBuffers = new WatchDogForParseBuffers();

            watchDogForParseBuffers.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
