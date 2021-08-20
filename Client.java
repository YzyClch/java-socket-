package com.chat;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Yzy
 * Date: 2021-08-19
 * Time: 14:54
 */
public class Client implements Runnable {

    private static final String ln = "\n";
    private static final String address = "";
    private static final int port=9999;
    private static final Scanner sc = new Scanner(System.in);
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private String nikeName;
    public Client(String nickName) throws UnknownHostException {
        this.nikeName=nickName;
        InetAddress inet = InetAddress.getByName(address);
        try {
            System.out.println("连接中...");
            socket = new Socket(inet, port);
            System.out.println("连接成功！");
            new Thread(this).start();
            System.out.print("请输入：");
            while (sc.hasNext()) {
                System.out.print("请输入：");
                String strMessage = sc.next();
                out = socket.getOutputStream();
                out.write(new Message("来自"+nikeName+": "+strMessage,1).getByte());
                out.flush();
            }
        } catch (IOException e) {
            if (e instanceof ConnectException){
                System.out.println("连接失败！");
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void run() {
        // 接收服务器消息
        try {
            in=socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while (true){
                System.out.println();
                System.out.println(new Message(br.readLine(),0).get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("请输入昵称");
        String nickName = sc.next();
        new Thread(new Client(nickName)).start();
    }
}
