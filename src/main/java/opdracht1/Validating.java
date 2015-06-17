/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opdracht1;

import EncryptionAPI.EncryptionManager;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kargathia
 */
public class Validating {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("File to validate?");
            Scanner sc = new Scanner(System.in);
            String signedFile = sc.nextLine();
            System.out.println("Save file as?");
            String destFile = sc.nextLine();

            EncryptionManager manager = new EncryptionManager();
            File publicKey = new File("files/public.key");
            manager.readPublicKey(publicKey);
            System.out.println("Signature verified: " + 
                    manager.verifySignature(signedFile, destFile));

//            String message = new String(messageBytes);
//            System.out.println("Message: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
