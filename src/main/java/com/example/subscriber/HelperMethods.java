package com.example.subscriber;

public class HelperMethods {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String hexToAscii(String hexString){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < hexString.length(); i += 2 ){
            String str = hexString.substring(i, i+2);
            sb.append((char) Integer.parseInt(str,16));
        }
        return sb.toString();
    }
}
