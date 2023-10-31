package hk231.rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.crypto.SecretKey;

import hk231.rsa.RSACryptoSystem;
public class Example {
    private static void example1(RSACryptoSystem rsa) {

        /**
         * Example 1: Encrypt and decrypt a message
         */

        String message = "Hello, RSA!";
        BigInteger encrypted = rsa.encrypt(new BigInteger(message.getBytes()));
        BigInteger decrypted = rsa.decrypt(encrypted);
        System.out.println("Original message: " + message);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + new String(decrypted.toByteArray()));

    }

    private static void example2(RSACryptoSystem rsa) {
        /**
         * Example 2: Encrypt and decrypt a file
         */

        String inputFileName = "input.txt";
        String encryptedFileName = "encrypted.txt";
        String decryptedFileName = "decrypted.txt";

        // Read input file
        try (
                FileInputStream fis = new FileInputStream(inputFileName);
                FileOutputStream fos = new FileOutputStream(encryptedFileName);) {
            BigInteger message = new BigInteger(fis.readAllBytes());
            BigInteger encrypted = rsa.encrypt(message);
            fos.write(encrypted.toByteArray());
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }

        // Read encrypted file
        try (
                FileInputStream fis = new FileInputStream(encryptedFileName);
                FileOutputStream fos = new FileOutputStream(decryptedFileName);) {
            BigInteger encrypted = new BigInteger(fis.readAllBytes());
            BigInteger decrypted = rsa.decrypt(encrypted);
            fos.write(decrypted.toByteArray());
        } catch (Exception e) {
            System.out.println("Error reading encrypted file");
            e.printStackTrace();

        }
        // Print content of input file
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            System.out.println("Content of input file:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }

        // Print content of decrypted file
        try (BufferedReader br = new BufferedReader(new FileReader(decryptedFileName))) {
            String line;
            System.out.println("Decrypted file:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }
    }

    private static void example3(RSACryptoSystem rsa) {
        /**
         * Example 3: Encrypt and decrypt a file using public and private key files
         */

        String pubKeyName = "public.der";
        String prvKeyName = "private.der";
        String inputFileName = "input.txt";
        String encryptedFileName = "encrypted.bin";
        String decryptedFileName = "decrypted.txt";

        // Read public key file
        try {
            FileInputStream fis = new FileInputStream(pubKeyName);
            byte[] pubKeyBytes = fis.readAllBytes();
            fis.close();

            rsa.getPublicKey().parsePublicKey(pubKeyBytes);
        } catch (Exception e) {
            System.out.println("Error reading public key file");
            e.printStackTrace();
        }

        // Read private key file
        try {
            FileInputStream fis = new FileInputStream(prvKeyName);
            byte[] prvKeyBytes = fis.readAllBytes();
            fis.close();

            rsa.getPrivateKey().parsePrivateKey(prvKeyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Read input file
        try (
                FileInputStream fis = new FileInputStream(inputFileName);
                FileOutputStream fos = new FileOutputStream(encryptedFileName);) {
            BigInteger message = new BigInteger(fis.readAllBytes());
            BigInteger encrypted = rsa.encrypt(message);
            fos.write(encrypted.toByteArray());
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }

        // Read encrypted file
        try (
                FileInputStream fis = new FileInputStream(encryptedFileName);
                FileOutputStream fos = new FileOutputStream(decryptedFileName);) {
            BigInteger encrypted = new BigInteger(fis.readAllBytes());
            BigInteger decrypted = rsa.decrypt(encrypted);
            fos.write(decrypted.toByteArray());
        } catch (Exception e) {
            System.out.println("Error reading encrypted file");
            e.printStackTrace();
        }

        // Print content of input file
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            System.out.println("Content of input file:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }

        // Print content of decrypted file
        try (BufferedReader br = new BufferedReader(new FileReader(decryptedFileName))) {
            String line;
            System.out.println("Decrypted file:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Error reading input file");
            e.printStackTrace();
        }
    }
}
