package com.inboardhack.scdrift;

import java.io.InputStream;
import java.util.ArrayList;

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

    public static String sanitizeInput(InputStream is){
        return null;
    }
}
