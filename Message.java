package com.chat;


import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Yzy
 * Date: 2021-08-19
 * Time: 17:32
 */
public class Message  {

    private String origin;
    private byte[] transform;
    private char[]charCache;
    private String keyCache;
    private static final String BASE="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random r=new Random();
    private static final String KEY_PREFIX="<key>";
    private static final String KEY_SUFFIX="</key>";
    private int mode;

    public String get(){
        if (mode==1){
            decode();
        }
        return origin;
    }

    public byte[]getByte(){
        return transform;
    }

    public Message(String origin,int mode){
        this.mode=mode;
        if (mode==1){
            this.origin=origin;
            generateKey();
            encode();
        }else if (mode==0){
            byte[] bytes = origin.getBytes();
            this.transform=new byte[bytes.length-1];
            for (int i = 0; i < bytes.length-1; i++) {
                transform[i]=bytes[i];
            }
            generateKey();
            this.origin=origin.substring((keyCache.length()+KEY_PREFIX.length()+KEY_SUFFIX.length()));
            decode();
        }

    }

    public static void main(String[] args) {
        for (byte aByte : "\n".getBytes()) {
            System.out.println((char)aByte);
        }
        System.out.println("\n".getBytes().length);
    }

    private void generateKey(){
        if (mode==1){
            StringBuilder s=new StringBuilder();
            for (int i = 0; i < 15; i++) {
                int num=r.nextInt(BASE.length()-1);
                s.append(BASE.charAt(num));
            }
            this.keyCache=s.toString();
        }else {
            this.keyCache="";
            for (int i = 5; i < 20; i++) {
                keyCache+=(char)transform[i];
            }
        }

    }

    private void encode(){
        char[] p = this.keyCache.toCharArray();
        char[] c = origin.toCharArray();
        for (int k = 0; k < c.length; k++) {
            int mima = c[k] + p[k / p.length];
            c[k] = (char) mima;
        }
        String ss="";
        for (int i = 0; i < c.length; i++) {
            ss+=c[i];
        }
        this.transform= (KEY_PREFIX + keyCache + KEY_SUFFIX + ss+"\n").getBytes();
    }

    private void decode(){
        char[] p = keyCache.toCharArray();
        char[] c = origin.toCharArray();
        for (int k = 0; k < c.length; k++) {
            int mima = c[k] - p[k / p.length];
            c[k] = (char) mima;
        }
        origin="";
        for (int i = 0; i < c.length; i++) {
            origin+=c[i];
        }
    }


}
