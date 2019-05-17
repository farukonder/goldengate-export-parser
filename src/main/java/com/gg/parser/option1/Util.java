package com.gg.parser.option1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Util {

    protected static BufferedReader getBufferedReaderFirstTime() throws Exception {

        // sleep only if there is no input
        while (true) {

            String fileToProcess = !TailMain.filesInFolder.isEmpty() ? TailMain.filesInFolder.get(0) : null;

            if (fileToProcess != null){

                //remove this from this list
                TailMain.filesInFolder.remove(0);

                System.out.println(fileToProcess);

                TailMain.fileStream = new FileInputStream(fileToProcess);

                return new BufferedReader(new InputStreamReader(TailMain.fileStream, "UTF-8"));

            }else{


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }

        return null;


    }

    protected static BufferedReader getBufferedReader(BufferedReader br) throws Exception {

        boolean weHaveAnewFile = false;
        // sleep only if there is no input
        while (!weHaveAnewFile) {

            String fileToProcess = !TailMain.filesInFolder.isEmpty() ? TailMain.filesInFolder.get(0) : null;

            if (fileToProcess != null){

                //remove this from this list
                TailMain.filesInFolder.remove(0);

                weHaveAnewFile = true;

                try {
                    TailMain.fileStream.close();
                } catch (Exception e) {
                }
                try {
                    br.close();
                } catch (Exception e) {
                }

                // this is to help GC
                TailMain.fileStream = null;
                br = null;

                System.out.println(fileToProcess);

                TailMain.fileStream = new FileInputStream(fileToProcess);

                return new BufferedReader(new InputStreamReader(TailMain.fileStream, "UTF-8"));

            }else{


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                return br;
            }
        }

        return br;
    }
}
