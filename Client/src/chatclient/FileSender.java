/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author TanmoyBanik
 */
public class FileSender implements Runnable {

    File file;

    public FileSender(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            ServerSocket servsock = new ServerSocket(13267);
            System.out.println("Waiting...");
            try (Socket sock = servsock.accept()) {
                System.out.println("Accepted connection : " + sock);
                byte[] mybytearray = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = sock.getOutputStream();
                System.out.println("Sending...");
                os.write(mybytearray, 0, mybytearray.length);
                os.flush();
                System.out.println("Sending finished");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
