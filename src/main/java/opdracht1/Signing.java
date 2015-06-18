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
public class Signing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String fileName, destFileName;
        
        while (true) {
            System.out.println("File to sign?");
            fileName = sc.nextLine();

            if (fileName.trim().isEmpty() || new File(fileName).length() == 0) {
                System.out.println("Error: invalid filename, or file not found");
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Output file?");
            destFileName = sc.nextLine();

            if (destFileName.trim().isEmpty()) {
                System.out.println("Error: invalid file name");
            } else {
                break;
            }
        }

        EncryptionManager manager = new EncryptionManager();
        File privateKey = new File("files/private.key");
        try {
            manager.readPrivateKey(privateKey);
            System.out.println("Signed file saved as: "
                    + manager.generateSignature(fileName, destFileName).getAbsolutePath());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
