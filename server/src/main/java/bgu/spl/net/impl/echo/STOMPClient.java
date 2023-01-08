package bgu.spl.net.impl.echo;

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
            String out1 ="CONNECT\n" +
                    "accept - version :1.2\n" +
                    "host : stomp . cs . bgu . ac . il\n" +
                    "login : meni\n" +
                    "passcode : films";

            out1+="\n"+'\u0000';
            out.write(out1);
            out.flush();

            System.out.println("awaiting response");
            String line= "";
            while (true)
            {
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
