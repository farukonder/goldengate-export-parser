package com.gg.parser.option1;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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


            // WatchDogForParseBuffers monitoring from streamsToParse list.
            // then sending those as input stream to stax parser as SequenceInputStream
            // with this way when read method on inputStream reach EOF, hasMoreElements on enumeration interfaces is being called by stax
            // at this point we are waiting new buffers to create and added to streamsToParse
            WatchDogForParseBuffers watchDogForParseBuffers = new WatchDogForParseBuffers();

            watchDogForParseBuffers.start();


            String line;
            BufferedReader br = Util.getBufferedReaderFirstTime();

            boolean retry = false;

            while (true) {
                if (br != null) {
                    line = br.readLine();
                } else {
                    line = null;
                }
                boolean eos = line == null;
                if (!eos) {
                   addLinesToBuffers(line);
                } else if (eos) {
                    br = Util.getBufferedReader(br);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static long start = System.nanoTime();
    static int bufferSize = 0;
    static StringBuilder bufferString = new StringBuilder();

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static void addLinesToBuffers(String line) {

        //TODO at this point line processing can be done by Selamn :)
        System.out.println("addLinesToBuffers          :" + line);
        line = line.replaceAll("\\p{C}", "?");
        System.out.println("addLinesToBuffers filtered :" + line);

        bufferSize++;
        bufferString.append(line);



        int elapsedMiliSec =  (int)((System.nanoTime() - start) / 1000000.0);


        // to not to get an exception in stax parser 90 is given since in sample xml file 01 has 90 lines of xml
        // if the file input stream is not giving 90 lines of string in 500 milisec then let continue
        if (bufferSize == 90 || elapsedMiliSec > 50){

            System.out.println(sdf.format(new Date()) + "############################## start to consruct the buffer with : bufferSize :" + bufferSize + ", elapsedMiliSec :" + elapsedMiliSec + " ##############################");

            bufferSize =0;
            start = System.nanoTime();
            byte[] byteArray = new byte[0];
            try {
                byteArray = bufferString.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            streamsToParse.add(inputStream);

            bufferString = new StringBuilder();

        }

    }
}
