package com.gg.parser.option2;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Iterator;

public class WatchDogForParseBuffers extends Thread{

    static BufferedReader br= null;

    @Override
    public void run() {

        try {

            //wait until getting the first buffered reader
            br = Util.getBufferedReaderFirstTime();

            //Get XMLInputFactory instance.
            XMLInputFactory xmlInputFactory =  XMLInputFactory.newInstance();

            //Create XMLEventReader object.
            XMLEventReader xmlEventReader =  xmlInputFactory.createXMLEventReader(new UnicodeFixReader(br));

            //Iterate through events.
            //TODO this part should be handled by Selman. since could not figure out how to solve how stax to work with after exception :)
            while (true) {
                if (xmlEventReader.hasNext()){
                    //Get next event.
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();
                    //Check if event is the start element.
                    if (xmlEvent.isStartElement()) {
                        //Get event as start element.
                        StartElement startElement = xmlEvent.asStartElement();
                        System.out.println("Inside Stax Parser : Start Element: " + startElement.getName());

                        //Iterate and process attributes.
                        Iterator iterator = startElement.getAttributes();
                        while (iterator.hasNext()) {
                            Attribute attribute = (Attribute) iterator.next();
                            QName name = attribute.getName();
                            String value = attribute.getValue();
                            System.out.println("Inside Stax Parser : Attribute name: " + name);
                            System.out.println("Inside Stax Parser : Attribute value: " + value);
                        }
                    }

                    //Check if event is the end element.
                    if (xmlEvent.isEndElement()) {
                        //Get event as end element.
                        EndElement endElement = xmlEvent.asEndElement();
                        System.out.println("Inside Stax Parser : End Element: " + endElement.getName());
                    }
                }else{
                    BufferedReader brNew = Util.getBufferedReader(br);
                    if (brNew != br){
                        br = brNew;
                        // TODO key point is here
                        xmlEventReader =  xmlInputFactory.createXMLEventReader(new UnicodeFixReader(br));
                    }else{
                        System.out.println("lets wait more in same file");
                    }
                }

            }
           } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
