package packege;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by EGS on 26.11.2015.
 */
public class Utils {
    public static String getIdentifier(InputStream fis) throws IOException, NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            ;
            byte[] mdbytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            fis.close();
            return null;
        }
    }
}
