package packege;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface FileWebService {
    @WebMethod
    public ListNameIdentifier search(String name);
    @WebMethod
    public boolean delete(String identifier);
    @WebMethod
    public DataHandler downloadFile(String identifier);
    @WebMethod
    public boolean loadFile(DataHandler file, String nameFile);
    @WebMethod
    public String getName(String identifier);
}