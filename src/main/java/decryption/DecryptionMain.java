/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decryption;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Scanner;
import javax.crypto.Cipher;
import utils.Utils;

/**
 *
 * @author Kargathia
 */
public class DecryptionMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Name?");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();

        try (FileChannel inChannel = new RandomAccessFile(
                "input(Signed by " + name + ").txt", "r").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
            inChannel.read(buffer, 0);
            System.out.println("buffer size: " + buffer.limit());

            buffer.position(0);
            int sigLength = buffer.getInt();
            System.out.println("sigLength: " + sigLength);
            byte[] sigBytes = new byte[sigLength];
            buffer.get(sigBytes);
            byte[] messageBytes = new byte[buffer.limit() - buffer.position()];
            buffer.get(messageBytes);

            // verify signature
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(Utils.readPublicKey("public.key"));
            signature.update(messageBytes);
            System.out.println("signature verified: " + signature.verify(sigBytes));

            System.out.println("message: " + new String(messageBytes));
        }
    }

    public static byte[] decrypt(ByteBuffer inBuffer) throws Exception {
        PrivateKey privKey = Utils.readPrivateKey("private.key");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        ByteBuffer outBuffer = ByteBuffer.allocate(inBuffer.remaining());
        System.out.println("outbuffer size: " + outBuffer.limit());
        cipher.doFinal(inBuffer, outBuffer);
        return outBuffer.array();
    }

}
