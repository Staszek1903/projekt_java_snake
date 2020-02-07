package common;

import java.io.*;
import java.util.Base64;

public class Serialization {
    public static byte[] objToBytes(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();

        return baos.toByteArray();
    }

    public static Serializable bytesToObj(byte [] data) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  data ) );
        Serializable o  = (Serializable)ois.readObject();
        ois.close();
        return o;
    }

    public static String bytesToStr(byte [] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte [] strToBytes(String s){
        return Base64.getDecoder().decode( s );
    }
}
