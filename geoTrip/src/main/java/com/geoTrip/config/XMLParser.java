package com.geoTrip.config;

import com.geoTrip.model.Point;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class XMLParser {


    public List<Point> parseFile(MultipartFile file) throws Exception {
        InputStream inputStream =  new BufferedInputStream(file.getInputStream());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("trkpt");

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i+=2) {
            Element element = (Element) nodeList.item(i);

            String lat = element.getAttribute("lat");
            String lon = element.getAttribute("lon");

            String time = element.getElementsByTagName("time")
                    .item(0).getTextContent();

            OffsetDateTime odt = OffsetDateTime.parse(time);
            LocalDateTime dateTime = odt.toLocalDateTime();

            points.add(new Point(UUID.randomUUID(), Double.valueOf(lat), Double.valueOf(lon), dateTime, null));
        }
        return points;
    }
}
