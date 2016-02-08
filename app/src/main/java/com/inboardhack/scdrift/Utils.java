package com.inboardhack.scdrift;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<Byte> convert(byte[] bytes) {
        ArrayList<Byte> ret = new ArrayList<>(bytes.length);
        for(byte b : bytes) ret.add(b);
        return ret;
    }

    public static String join(String sep, double[] values) {
        String ret = "";
        for(int i = 0; i<values.length; i++) {
            ret += (i==0) ? values[i] : sep + values[i];
        }
        return ret;
    }

    public static String join(String sep, float[] values) {
        String ret = "";
        for(int i = 0; i<values.length; i++) {
            ret += (i==0) ? values[i] : sep + values[i];
        }
        return ret;
    }

    public static boolean checksum(ArrayList<Byte> data) {
        if(data==null || data.size()==0) return false;
        byte curr = data.get(0);
        for(int i=1; i<data.size(); i++) {
            curr = (byte) (curr ^ data.get(i));
        }
        return curr==0;
    }

    public static byte[] checksumTransform(byte[] data) {
        byte[] ret = new byte[data.length+1];
        byte curr = data[0];
        for(int i=1; i<data.length; i++) {
            ret[i] = data[i];
            curr = (byte) (curr ^ data[i]);
        }
        ret[ret.length-1] = curr;
        return ret;
    }

    public static String sanitizeInput(InputStream is) throws IOException {
        if(is==null) return null;
        int read = is.read();
        if (read == 123)

            return (sanitizeInput_rec(is, new ArrayList<Byte>()));
        else if (read < 0)
            return null;
        else
            return (sanitizeInput(is));
    }
    private static String sanitizeInput_rec(InputStream is, ArrayList<Byte> in) throws IOException {
        int read = is.read();
        if (read < 0) {
            return null;
        }
        byte ascii = (byte) read;
        if (read == 125) {
            if (true){//checksum(in)) {
                byte[] inarr = new byte[in.size()];
                for (int i = 0; i < in.size(); i++) {
                    inarr[i] = in.get(i).byteValue();
                }
                return new String(inarr, "UTF-8");
            } else {
                return null;
            }
        } else {
            in.add(ascii);
            return sanitizeInput_rec(is, in);
        }
    }
}
