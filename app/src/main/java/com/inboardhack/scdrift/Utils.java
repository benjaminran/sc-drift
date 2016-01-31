package com.inboardhack.scdrift;

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

    public static boolean checksum2(byte[] data) {
        byte delimiter = '_';
        int sum = 0;
        int i=0;
        for(; data[i] != delimiter; i++){
            sum += data[i];
        }
        return false;
    }

    public static boolean checksum(byte[] data) {
        byte curr = data[0];
        for(int i=1; i<data.length; i++) {
            curr = (byte) (curr ^ data[i]);
        }
        return curr==0;
    }
}
