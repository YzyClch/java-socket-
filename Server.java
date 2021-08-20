package com.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Yzy
 * Date: 2021-08-19
 * Time: 14:54
 */
public class Server {
    private ServerSocket socket;
    private volatile List<Socket>socketList=new ArrayList<>();
    private volatile HashMap<Socket,String>nickNameMap=new HashMap<>();
    private static final String ln = "\n";
    public List<Socket> getSocketList() {
        return socketList;
    }
    public Server(int port) throws IOException {
        this.socket=new ServerSocket(port);
    }
    public ServerSocket getSocket(){
        return socket;
    }
    private void start() throws IOException {
        //开启连接检测线程
        new Thread(new Server.heartbeatTask(this)).start();
        for (;;){
            Socket accept = socket.accept();
            socketList.add(accept);
            System.out.println(accept+"已连接！");
            new Thread(new Server.Task(this,accept)).start();
        }


    }


    public static void main(String[] args) throws IOException {
        new Server(9999).start();
    }

    public static class heartbeatTask implements Runnable{
        private Server server;
        public heartbeatTask(Server server) {
            this.server=server;
        }

        @Override
        public void run() {
            for (;;){
                for (int i=0;i<Integer.MAX_VALUE;i++){
                    if (i ==Integer.MAX_VALUE/2){
                        System.out.println("current socket size "+server.socketList.size());
                    }
                }
            }
        }
    }

    public static class Task implements Runnable{
        private InputStream in;
        private OutputStream out;
        private Socket socket;
        private Server server;
        public Task(Server server, Socket socket) throws IOException {
            this.server=server;
            this.socket=socket;
            this.in=socket.getInputStream();
            this.out=socket.getOutputStream();
        }


        @Override
        public void run(){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while (true) {
                    String s = br.readLine();
                    if (s!=null && s.length()>0){
                        String message = new Message(s, 0).get();
                        System.out.println(socket.getInetAddress()+" ："+message);
                        inform(message);
                    }
                }
            } catch (SocketException e) {
//                e.printStackTrace();
                server.socketList.removeIf(socket::equals);
                System.out.println(socket.getInetAddress()+" 断开了连接");
                System.out.println("当前会话数量["+server.socketList.size()+"]");
            }catch (IOException e){

            }
        }

        private void inform(String message){
            List<Socket> socketList = server.getSocketList();
            List<Socket> informList = socketList.stream().filter(item -> !socket.equals(item)).collect(Collectors.toList());
            if (!informList.isEmpty()){
                informList.forEach(soc->{
                    try {
                        soc.getOutputStream().write(new Message(message+ln,1).getByte());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            System.out.println("inform end");
        }

    }



}
