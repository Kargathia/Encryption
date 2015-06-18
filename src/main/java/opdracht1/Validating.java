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
            Scanner sc = new Scanner(System.in);
            String signedFile, destFile;

            while (true) {
                System.out.println("File to validate?");
                signedFile = sc.nextLine();
                if (signedFile.trim().isEmpty() || new File(signedFile).length() == 0) {
                    System.out.println("Error: invalid filename or file not found");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.println("Save file as?");
                destFile = sc.nextLine();
                if (destFile.trim().isEmpty()) {
                    System.out.println("Error: invalid filename");
                } else {
                    break;
                }
            }

            EncryptionManager manager = new EncryptionManager();
            File publicKey = new File("files/public.key");
            manager.readPublicKey(publicKey);
            System.out.println("Signature verified: "
                    + manager.verifySignature(signedFile, destFile));

//            String message = new String(messageBytes);
//            System.out.println("Message: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
