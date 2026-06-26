package testcode.mydecoder;

import java.beans.XMLDecoder;
import java.io.InputStream;

public class MyXmlDecodeUtil {

    public static Object handleXml(InputStream in) {
        XMLDecoder d = new XMLDecoder(in);
        try {
            return d.readObject();
        } finally {
            d.close();
        }
    }
}
