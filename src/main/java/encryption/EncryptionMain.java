/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.crypto.Cipher;
import utils.Utils;

/**
 *
 * @author Kargathia
 */
public class EncryptionMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
            throws Exception {
        System.out.println("Name?");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(Utils.readPrivateKey("private.key"), new SecureRandom());
        
        byte[] message = Files.readAllBytes(new File("input.txt").toPath());
        signature.update(message);

        byte[] sigBytes = signature.sign();
        System.out.println("sigBytes length: " + sigBytes.length);
        byte[] sigLength = ByteBuffer.allocate(4).putInt(sigBytes.length).array();
//        byte[] encrypted = encrypt(message);

        Utils.saveToFile("input(Signed by " + name + ").txt",
                sigLength, sigBytes, message);
        
        // testing
//        byte[] intbytes = ByteBuffer.allocate(4).putInt(9001).array();
//        Utils.saveToFile("input(Signed by " + name + ").txt",
//                intbytes);

        // verifies - for testing
        signature.initVerify(Utils.readPublicKey("public.key"));
        signature.update(message);
        System.out.println("signature verified: " + signature.verify(sigBytes));
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        PublicKey pubKey = Utils.readPublicKey("public.key");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        ByteBuffer inBuffer = ByteBuffer.allocate(data.length);
        ByteBuffer outBuffer = ByteBuffer.allocate(data.length);
        inBuffer.put(data);
        cipher.doFinal(inBuffer, outBuffer);
        return outBuffer.array();
    }


}
