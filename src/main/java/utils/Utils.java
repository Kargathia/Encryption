/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 *
 * @author Kargathia
 */
public class Utils {

    public static void saveToFile(String fileName,
            byte[]... objects) throws IOException {
        try (FileChannel channel = new FileOutputStream(fileName).getChannel()) {
            ByteBuffer byteBuffer = null;
            for (byte[] o : objects) {
                byteBuffer = ByteBuffer.wrap(o);
                channel.write(byteBuffer);
            }
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        }
    }

    public static PublicKey readPublicKey(String keyFileName) throws IOException {
        ObjectInputStream oin
                = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream(keyFileName)));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    public static PrivateKey readPrivateKey(String keyFileName) throws IOException {
        ObjectInputStream oin
                = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream(keyFileName)));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privKey = fact.generatePrivate(keySpec);
            return privKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

}
