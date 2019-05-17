package com.gg.parser.option1;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Iterator;

public class WatchDogForParseBuffers extends Thread{

    @Override
    public void run() {

        try {

            Enumeration<InputStream> enumerationBufferList = new Enumeration<InputStream>() {
                @Override
                public boolean hasMoreElements() {

                    while(true){
                        if (!TailMain.streamsToParse.isEmpty())
                            return true;
                        else{
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }

                    return false;
                }

                @Override
                public InputStream nextElement() {
                    ByteArrayInputStream inputStream = TailMain.streamsToParse.get(0);
                    TailMain.streamsToParse.remove(0);
                    return inputStream;
                }
            };

            SequenceInputStream sis = new SequenceInputStream(enumerationBufferList);


            //Get XMLInputFactory instance.
            XMLInputFactory xmlInputFactory =  XMLInputFactory.newInstance();

            //Create XMLEventReader object.
            XMLEventReader xmlEventReader =  xmlInputFactory.createXMLEventReader(sis);

            //Iterate through events.
            //TODO this part should be handled by Selman. since could not figure out how to solve how stax to work with after exception :)
            while (xmlEventReader.hasNext()) {
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
            }
           } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
