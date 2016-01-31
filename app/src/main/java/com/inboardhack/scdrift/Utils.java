package com.inboardhack.scdrift;

import java.io.InputStream;

public class Utils {

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

    public static boolean checksum(byte[] data) {
        if(data==null) return false;
        byte curr = data[0];
        for(int i=1; i<data.length; i++) {
            curr = (byte) (curr ^ data[i]);
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

    public static String sanitizeInput(InputStream is) {
        if (is.read() == 0x7B)
            return (sanitizeInput_rec(is, new ArrayList<Byte>()));
        else return (sanitizeInput(is));
    }
    private static String sanitizeInput_rec(InputStream is, ArrayList<Byte> in) {
        byte ascii = is.read();
        if (ascii == 0x7D) {
            if (checksum(in)) {
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
