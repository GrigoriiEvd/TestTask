package packege;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class WebServiceClient {
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://localhost:1986/wss/file?wsdl");

        QName qname = new QName("http://packege/", "WebServiceImplService");

        Service service = Service.create(url, qname);
        FileWebService server = service.getPort(FileWebService.class);

        String s = "";
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Enter a 'search' for search file, 'download' for download file, 'load' for loading file, 'delete' for delete file, 'end' for Exit");
            String s1=scanner.next();
            String s2=scanner.next();
            switch (s1) {
                case "search":
                    ListNameIdentifier list = server.search(s2);
                    for (int j = 0; j < list.getList().size(); j++) {
                        System.out.println(list.getList().get(j).getName() + " " + list.getList().get(j).getIdentifier());
                    }
                    break;
                case "download":
                    String identifier= s2;
                    String saveCatalog = scanner.next()+"/" + server.getName(identifier);

                    DataHandler dh = server.downloadFile(identifier);

                    try (FileOutputStream outputStream = new FileOutputStream(saveCatalog)) {
                        dh.writeTo(outputStream);
                    }
                    System.out.println("Download Successful");
                    break;
                case "load":
                    File file = new File(s2);
                    if (file.isFile()) {
                        String s3 = s2.substring(s2.lastIndexOf('/') + 1, s2.length());
                        FileDataSource dataSource = new FileDataSource(s2);
                        DataHandler fileDataHandler = new DataHandler(dataSource);
                            if (server.loadFile(fileDataHandler, s3)) {
                                System.out.println("Loading Successful");
                            } else {
                                System.out.println("Loading error");
                            }
                    }else{
                        System.out.println("File not found");
                    }
                    break;
                case "delete":
                    if (server.delete(s2)) {
                        System.out.println("Removal completed successfully");
                    } else {
                        System.out.println("Error while deleting");
                    }
                    break;
            }
        } while (!s.equals("end"));
    }
}