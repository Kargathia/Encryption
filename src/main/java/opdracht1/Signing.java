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
public class Signing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        System.out.println("Name?");
        Scanner sc = new Scanner(System.in);
//        String name = sc.nextLine();
        System.out.println("File to sign?");
        String fileName = sc.nextLine();
        System.out.println("Output file?");
        String destFileName = sc.nextLine();

        EncryptionManager manager = new EncryptionManager();
        File privateKey = new File("files/private.key");
        try {
            manager.readPrivateKey(privateKey);
            System.out.println("Signed file saved as: " +
                    manager.generateSignature(fileName, destFileName).getAbsolutePath());
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
