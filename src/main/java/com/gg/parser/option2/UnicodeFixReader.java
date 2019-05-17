package com.gg.parser.option2;

import java.io.BufferedReader;
import java.io.IOException;

public class UnicodeFixReader extends BufferedReader {

    public UnicodeFixReader(BufferedReader bufferedReader) {
        super(bufferedReader);
    }


    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
            int c = super.read(cbuf, off, len);
            if (c != -1) {
                processCharBuffer(cbuf);
            }
        return c;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }


    private void processCharBuffer(char[] cbuf) {

        //TODO at this point line processing can be done by Selamn :)
        for (int i = 0; i < cbuf.length; i++) {
            // TODO debug
            // System.out.println("char :" + cbuf[i] + " int :" + (int)cbuf[i] + " hex :" + String.format("%04x", (int) cbuf[i]));
            if ("001c".equals(String.format("%04x", (int) cbuf[i])) ||
            "001e".equals(String.format("%04x", (int) cbuf[i]))){
                cbuf[i] = '?';
            }
        }
        // TODO debug
        // System.out.println("processLine          :" + new String(cbuf));

    }
}
