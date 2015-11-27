package packege;
import javax.xml.ws.Endpoint;


public class WebServicePublisher {
    public static void main(String... args) {
        Endpoint.publish("http://localhost:1986/wss/file", new WebServiceImpl());
    }
}