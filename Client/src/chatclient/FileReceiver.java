/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TanmoyBanik
 */
public class FileReceiver implements Runnable {

    String address;

    public FileReceiver(String getAddress) {

        address = getAddress.replace('/', ' ').trim();
        System.out.println(address);
    }

    @Override
    public void run() {
        try {
            int filesize = 6022386; // filesize temporary hardcoded
            long start = System.currentTimeMillis();
            int bytesRead;
            int current = 0;
            try (
                Socket sock = new Socket(address, 13267)) {
                System.out.println("Connecting...");
                // receive file
                byte[] mybytearray = new byte[filesize];
                InputStream is = sock.getInputStream();
                FileOutputStream fos = new FileOutputStream("image.jpg");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray, 0, mybytearray.length);
                current = bytesRead;
                do {
                    bytesRead = is.read(mybytearray, current, mybytearray.length - current);
                    if (bytesRead >= 0) {
                        current += bytesRead;
                    }
                } while (bytesRead > -1);
                bos.write(mybytearray, 0, current);
                bos.flush();
                long end = System.currentTimeMillis();
                System.out.println(end - start);
                bos.close();
                System.out.println("Receiving finished");
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
