package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.stomp.StompServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class STOMPClient {
    public static void main(String[] args) {

        if (args.length == 0) {
            args = new String[]{"localhost", "hello"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }
        try (Socket sock = new Socket(args[0], 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server");
            Scanner sc = new Scanner(System.in);

            ///////////////////////////////////////////////////////////
            String out1 ="CONNECT\n" +
                    "accept - version :1.2\n" +
                    "host : stomp . cs . bgu . ac . il\n" +
                    "login : meni\n" +
                    "passcode : films";

            out1+="\n"+"\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            String line= "";

            while (!line.equals("b")){
                line = in.readLine();
                System.out.println(line);
            }


            /////////////////////////////////////////////////////////////////////

            out1 ="SUBSCRIBE\n" +
                    "destination :/ spain_japan\n" +
                    "id :17\n" +
                    "receipt :73";

            out1+="\n"+"\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            line= "";

            while (!line.equals("b")){
                line = in.readLine();
                System.out.println(line);
            }

            /////////////////////////////////////////////////////////////////////
            out1 =
                    "SEND\n" +
                            "destination :/ spain_japan\n" +
                            "user : meni\n" +
                            "team a : spain\n" +
                            "team b : japan\n" +
                            "event name : kickoff\n" +
                            "time : 0\n" +
                            "general game updates :\n" +
                            "\tactive : true\n" +
                            "\tbefore halftime : true\n" +
                            "team a updates :\n" +
                            "\ta : b\n" +
                            "\tc : d\n" +
                            "\te : f\n" +
                            "team b updates :\n" +
                            "\ta : b\n" +
                            "\tc : d\n" +
                            "\te : f\n" +
                            "description :\n" +
                            "And we ’ re off !";

            out1+="\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            line= "";

            while (!line.equals("b")){
                line = in.readLine();
                System.out.println(line);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            out1 =
                    "UNSUBSCRIBE\n" +
                            "id :17\n" +
                            "receipt :82\n";

            out1+="\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            line= "";

            while (!line.equals("b")){
                line = in.readLine();
                System.out.println(line);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            out1 =
                    "DISCONNECT\n" +
                    "receipt :77\n";

            out1+="\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            line= "";

            while (!line.equals("b")){
                line = in.readLine();
                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
