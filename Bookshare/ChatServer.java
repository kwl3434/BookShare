package Bookshare;

import java.io.*;
import java.net.*;

public class ChatServer
{
   public static final int cs_port = 2777;
   public static final int cs_maxclient=10;

// 클라이언트로부터 접속요청을 기다리고, 소켓을 생성한다.
   public static void main(String args[]){
      try{
         ServerSocket ss_socket = new ServerSocket(cs_port);
         while(true){
            Socket sock = null;
            ServerThread client = null; //클라이언트와 통신할 서버소켓
            try{
               sock = ss_socket.accept(); // 클라이언트의 접속을 기다린다.
               client = new ServerThread(sock); 
               client.start();
            }catch(IOException e){
               System.out.println(e);
               try{
                  if(sock != null)
                     sock.close();
               }catch(IOException e1){
                  System.out.println(e);
               }finally{
                  sock = null;
               }
            }
         }
      }catch(IOException e){
         // 서버가 정상적으로 동작하지 않는 경우를 처리한다.
      }
   }
}