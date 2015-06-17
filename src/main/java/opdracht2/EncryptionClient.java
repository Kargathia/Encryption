/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opdracht2;

import EncryptionAPI.EncryptionManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class EncryptionClient extends Application {

    private Stage stage;
    private StringProperty messageProp;
    private EncryptionManager manager;
    private ObservableList<String> events;
    private SimpleDateFormat sdf;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.manager = new EncryptionManager();

        this.messageProp = new SimpleStringProperty("");
        this.events = FXCollections.observableArrayList(new ArrayList<>());
        this.sdf = new SimpleDateFormat("HH:mm:ss");

        this.stage = primaryStage;
        this.stage.setResizable(false);

        Platform.runLater(() -> {
            try {
                GridPane gp = new GridPane();
                gp.setAlignment(Pos.TOP_CENTER);
                gp.setHgap(10);
                gp.setVgap(10);
                gp.setPadding(new Insets(25, 25, 25, 25));

                // row 4
                Label lblPassword = new Label("Password");
                gp.add(lblPassword, 0, 3);
                PasswordField pwfPassword = new PasswordField();
                pwfPassword.setMaxWidth(200);
                gp.add(pwfPassword, 1, 3);

                // row 5
                Button btnEncrypt = new Button("Encrypt");
                gp.add(btnEncrypt, 1, 4);
                Button btnDecrypt = new Button("Decrypt");
                gp.add(btnDecrypt, 1, 4);
                GridPane.setHalignment(btnDecrypt, HPos.RIGHT);

                // row 6
                Label lblMessage = new Label("Message");
                gp.add(lblMessage, 0, 5);
                TextArea taMessage = new TextArea();
                taMessage.textProperty().bindBidirectional(messageProp);
                gp.add(taMessage, 1, 5, 2, 5);

                // row 8
                Label lblLog = new Label("Event Log");
                gp.add(lblLog, 0, 11);
                ListView<String> lvLog = new ListView<>(this.events);
                lvLog.setMaxHeight(200);
                gp.add(lvLog, 1, 11, 2, 4);

                // eventhandlers
                btnEncrypt.setOnAction((ActionEvent event) -> {
                    this.encrypt(pwfPassword.getText().toCharArray());
                });

                btnDecrypt.setOnAction((ActionEvent event) -> {
                    this.decrypt(pwfPassword.getText().toCharArray());
                });

                // adds everything to root, launches scene
                Group root = new Group();
                Scene scene = new Scene(root, 600, 550);
                root.getChildren().add(gp);
                stage.setScene(scene);
                stage.setTitle("Encryption Client");

                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Encrypts data currently present in messageProp. <br>
     * Saves the Encrypted data in a file of user's choosing.
     * @param password
     */
    private void encrypt(char[] password) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try {
            manager.encrypt(password, this.messageProp.get().getBytes(),
                    file.getAbsolutePath());
            this.events.add(0, sdf.format(new Date()) 
                    + " - Successfully encrypted file " + file.getName());
        } catch (Exception ex) {
            System.out.println("Exception caught encrypting: " + ex.getMessage());
            this.events.add(0,sdf.format(new Date()) 
                    + " - Unable to encrypt file " + file.getName());
        }
    }

    /**
     * Decrypts data in opened file, and puts results in messageProp.
     * @param password
     */
    private void decrypt(char[] password) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        try {
            byte[] decryptedData = manager.decrypt(password, file.getAbsolutePath());
            this.messageProp.set(new String(decryptedData));
            this.events.add(0, sdf.format(new Date()) 
                    + " - Successfully decrypted file " + file.getName());
        } catch (Exception ex) {
            System.out.println("Exception caught decrypting: " + ex.getMessage());
            this.events.add(0, sdf.format(new Date()) 
                    + " - Unable to decrypt file " + file.getName());
            this.messageProp.set("");
        }
    }
}
