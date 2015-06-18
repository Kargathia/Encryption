/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EncryptionAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 *
 * @author Kargathia
 */
public class EncryptionManager {

    public static final String PUBLIC_KEY_FILENAME = "public.key",
            PRIVATE_KEY_FILENAME = "private.key",
            KEY_ALGORITHM = "RSA",
            SIGNATURE_ALGORITHM = "SHA1withRSA";

    public static final int BITSIZE = 1024;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Generates two files in given path - names match PUBLIC_KEY_FILENAME, and
     * PRIVATE_KEY_FILENAME
     *
     * @param path
     */
    public boolean generateKeys(String path) throws IOException {
        String dir = this.formatPath(path);
        try {
            // starts generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            kpg.initialize(BITSIZE);
            KeyPair kp = kpg.genKeyPair();

            // saves local keys
            this.privateKey = kp.getPrivate();
            this.publicKey = kp.getPublic();

            // gets keys
            KeyFactory fact = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                    RSAPrivateKeySpec.class);

            // saves keys
            saveToFile(dir + PUBLIC_KEY_FILENAME,
                    pub.getModulus().toByteArray(),
                    pub.getPublicExponent().toByteArray());
            saveToFile(dir + PRIVATE_KEY_FILENAME,
                    priv.getModulus().toByteArray(),
                    priv.getPrivateExponent().toByteArray());

            return true;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            System.out.println("InvalidKeySpecException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Adds signature to given file, and saves it as separate file. <br>
     * Private key needs to be set first by calling setPrivateKey() or
     * generateKeys(). Will throw an IOException otherwise.
     *
     * @param name
     * @param messageFile
     * @throws IOException
     */
    public File generateSignature(String messageFile, String destFile)
            throws IOException {
        if (messageFile == null || messageFile.length() == 0) {
            throw new IOException("file was null or empty");
        }
        if (this.privateKey == null) {
            throw new IOException("no known private key");
        }

        try {
            // initiates signature
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(this.privateKey, new SecureRandom());

            // reads input file
            byte[] message = Files.readAllBytes(new File(messageFile).toPath());
            signature.update(message);

            // signs and generates signed file
            byte[] sigBytes = signature.sign();
            byte[] sigLength = ByteBuffer.allocate(4).putInt(sigBytes.length).array();
            return this.saveToFile(destFile,
                    sigLength, sigBytes, message);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println("Exception in generateSignature(): " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Reads file. Assumption is that file matches protocol from
     * generateSignature(). <br> <br>
     *
     * Requires public key to be set.
     *
     * @param fileName
     * @return file message, if able to verify signature. Null if unable
     * @throws IOException
     */
    public boolean verifySignature(String fileName, String destFileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IOException("fileName was null or empty");
        }
        if (destFileName == null || destFileName.trim().isEmpty()) {
            throw new IOException("destFileName was null or empty");
        }
        if (this.publicKey == null) {
            throw new IOException("No known public key");
        }

        try (FileChannel inChannel = new RandomAccessFile(
                fileName, "r").getChannel()) {
            // reads file into buffer
            ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
            inChannel.read(buffer, 0);

            // reads signature length, signature, and message
            buffer.position(0);
            int sigLength = buffer.getInt();
            byte[] sigBytes = new byte[sigLength];
            buffer.get(sigBytes);
            byte[] messageBytes = new byte[buffer.limit() - buffer.position()];
            buffer.get(messageBytes);

            // verify signature
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(this.publicKey);
            signature.update(messageBytes);
            if (signature.verify(sigBytes)) {
                this.saveToFile(destFileName, messageBytes);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println("Verify exception: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Encrypts given byte array.
     *
     * @param data
     * @return
     * @throws Exception
     */
    public void encrypt(char[] password, byte[] data, String fileName)
            throws Exception {

        Cipher cipher = this.initCipher(password, Cipher.ENCRYPT_MODE);
        byte[] encryptedData = cipher.doFinal(data);
        this.saveToFile(fileName, encryptedData);
    }

    /**
     * Decrypts given byte array.
     *
     * @param inBuffer
     * @return
     * @throws Exception
     */
    public byte[] decrypt(char[] password, String fileName)
            throws Exception {
        try (FileChannel inChannel = new RandomAccessFile(
                fileName, "r").getChannel()) {
            // reads file into buffer
            ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
            inChannel.read(buffer, 0);

            Cipher cipher = this.initCipher(password, Cipher.DECRYPT_MODE);
            byte[] decryptedData = cipher.doFinal(buffer.array());
            return decryptedData;
        }
    }

    /**
     *
     * @param password
     * @return
     * @throws Exception
     */
    private Cipher initCipher(char[] password, int opMode) throws Exception {
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;

        // Salt
        byte[] salt = {
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
        };

        // Iteration count
        int count = 20;

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);
        pbeKeySpec = new PBEKeySpec(password);
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(opMode, pbeKey, pbeParamSpec);
        return pbeCipher;
    }

    /**
     * Reads private key from given file.
     *
     * @param keyFile
     * @return
     * @throws IOException
     */
    public PrivateKey readPrivateKey(File keyFile) throws IOException {
        try {
            BigInteger[] values = this.readKeyFile(keyFile);

            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(values[0], values[1]);
            KeyFactory fact = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privKey = fact.generatePrivate(keySpec);
            this.privateKey = privKey;
            return privKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println("Error in readPrivateKey: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Reads public key from given file
     *
     * @param keyFile
     * @return
     * @throws IOException
     */
    public PublicKey readPublicKey(File keyFile) throws IOException {
        try {
            BigInteger[] values = this.readKeyFile(keyFile);

            // generates key
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(values[0], values[1]);
            KeyFactory fact = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey pubKey = fact.generatePublic(keySpec);
            this.publicKey = pubKey;
            return pubKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println("Error in readPublicKey: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Reads two BigIntegers from given file.
     *
     * @param keyFile
     * @return an array containing two BigIntegers. <br>
     * [0] = modulo <br>
     * [1] = exponent
     * @throws IOException
     */
    private BigInteger[] readKeyFile(File keyFile) throws IOException {
        try (FileChannel inChannel = new RandomAccessFile(
                keyFile, "r").getChannel()) {
            // reads file into buffer
            ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
            inChannel.read(buffer, 0);

            // reads modulo
            buffer.flip();
            byte[] array = new byte[(BITSIZE / 8) + 1];
            buffer.get(array);
            BigInteger mod = new BigInteger(array);

            // reads exponent
            array = new byte[buffer.remaining()];
            buffer.get(array);
            BigInteger exp = new BigInteger(array);

            return new BigInteger[]{mod, exp};
        }
    }

    /**
     * Saves all given byte arrays to a single file of given name.
     *
     * @param fileName
     * @param objects
     * @throws IOException
     */
    private File saveToFile(String fileName, byte[]... objects) throws IOException {
        try (FileChannel channel = new FileOutputStream(fileName).getChannel()) {
            ByteBuffer byteBuffer = null;
            for (byte[] array : objects) {
                byteBuffer = ByteBuffer.wrap(array);
                channel.write(byteBuffer);
            }
            return new File(fileName);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        }
    }

    /**
     * Cleans given path to avoid simple IO problems to do with trailing file
     * separators, and allow null input.
     *
     * @param path
     * @return
     */
    private String formatPath(String path) {
        String output = null;
        if (path == null || path.trim().isEmpty()) {
            output = "";
        } else if (!path.substring(path.length() - 1).matches("[\\/]")) {
            output = path.concat("\\");
        }
        return output;
    }

}
