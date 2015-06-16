/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keygeneration;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.Utils.saveToFile;

/**
 *
 * @author Kargathia
 */
public class KeyGenerationMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();

            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                    RSAPrivateKeySpec.class);

            saveToFile("public.key", pub.getModulus().toByteArray(),
                    pub.getPublicExponent().toByteArray());
            saveToFile("private.key", priv.getModulus().toByteArray(),
                    priv.getPrivateExponent().toByteArray());

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            System.out.println("InvalidKeySpecException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    

}
