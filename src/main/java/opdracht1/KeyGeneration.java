/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opdracht1;

import EncryptionAPI.EncryptionManager;
import java.io.IOException;

/**
 *
 * @author Kargathia
 */
public class KeyGeneration {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EncryptionManager manager = new EncryptionManager();
        try {
            System.out.println("Keys generated: " + manager.generateKeys("files"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
