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

public class RSACryptoSystem {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private BigInteger bitLength;

    public RSACryptoSystem(int keyBitLength) {
        SecureRandom random = new SecureRandom();
        bitLength = BigInteger.valueOf(keyBitLength);

        // Generate p and q, two distinct strong primes
        BigInteger p = PrimeGeneration.generate(keyBitLength / 2);
        BigInteger q = PrimeGeneration.generate(keyBitLength / 2);

        // Calculate n = p * q
        BigInteger n = p.multiply(q);

        // Calculate phi(n) = (p - 1) * (q - 1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Choose e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), random);
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !Math.gcd(e, phi).equals(BigInteger.ONE));

        // Calculate d = e^(-1) mod phi(n)
        BigInteger d = Math.modInverse(e, phi);

//        System.out.println("p: " + p);
//        System.out.println("q: " + q);
//        System.out.println("n: " + n);
//        System.out.println("phi: " + phi);
//        System.out.println("e: " + e);
//        System.out.println("d: " + d);

        publicKey = new PublicKey(n, e);
        privateKey = new PrivateKey(n, d);
    }

    public BigInteger encrypt(BigInteger message) {
        return Math.modPow(message, publicKey.getPublicExponent(), publicKey.getModulus());
    }

    public BigInteger decrypt(BigInteger encryptedMessage) {
        return Math.modPow(encryptedMessage, privateKey.getPrivateExponent(), privateKey.getModulus());
    }

    public void generateKeyFiles() {
        System.out.println("Generating public and private keys...");

        // Write to file
        String publicKeyFile = "public.der";
        try {
            // if file doesnt exists, then create it
            File file = new File(publicKeyFile);
            if (!file.exists()) {
                file.createNewFile();
                // print file path
            }

            FileOutputStream fos = new FileOutputStream(publicKeyFile);
            fos.write(publicKey.getEncoded());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error writing public key to file");
            e.printStackTrace();
        }

        String privateKeyFile = "private.der";
        try {

            File file = new File(privateKeyFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(privateKeyFile);
            fos.write(privateKey.getEncoded());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error writing private key to file");
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) {
       if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("-example <n> : runs prewritten example");
            System.out.println("-generate : generates public and private keys");
            System.out.println("-encrypt <input> <output> : encrypt a file");
            System.out.println("-decrypt <input> <output> : decrypt a file");
            return;
        }

        // CLI application
        /**
         * Options:
         * -example <n> : runs prewritten example
         * -generate : generates public and private keys
         * -encrypt <input> <output> <public_key> : encrypt a file
         * -decrypt <input> <output> <private_key> : decrypt a file
         *
         */

        RSACryptoSystem rsa = new RSACryptoSystem(2048);
        String option = args[0];

        switch (option) {
            case "-example":
                runExamples(args, rsa);
                break;
            case "-generate":
                rsa.generateKeyFiles();
                break;
            case "-encrypt":
                startEncrypt(args, rsa);
                break;
            case "-decrypt":
                startDecrypt(args, rsa);
                break;
            default:
                System.out.println("Invalid option.");
                break;
        }

    }

    private static void runExamples(String[] args, RSACryptoSystem rsa) {
        if (args.length < 2) {
            System.out.println("Please specify an example number.");
            return;
        }
        //rsa.generateKeyFiles();
        int exampleNumber = Integer.parseInt(args[1]);

        switch (exampleNumber) {
            case 1:
                Example.example1(rsa);
                break;
            case 2:
                Example.example2(rsa);
                break;
            case 3:
                Example.example3(rsa);
                break;

            default:
                System.out.println("Invalid number.");
                break;
        }

    }

    private static void startEncrypt(String[] args, RSACryptoSystem rsa) {
        if (args.length < 3) {
            System.out.println("Please specify input and output files.");
            return;
        }
        //rsa.generateKeyFiles();
        String inputFile = args[1];
        String outputFile = args[2];
        String publicKeyFile = args[3];

        // read file
        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Paths.get(inputFile));
        } catch (IOException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }

        // read public key
        try {
            File file = new File(publicKeyFile);
            FileInputStream fis = new FileInputStream(file);
            byte[] encodedPublicKey = new byte[(int) file.length()];
            fis.read(encodedPublicKey);
            fis.close();
            rsa.getPublicKey().parsePublicKey(encodedPublicKey);
        } catch (Exception e) {
            System.out.println("Error reading private key from file");
            e.printStackTrace();
        }


        // encrypt
        BigInteger encrypted = rsa.encrypt(new BigInteger(fileBytes));

        // write to file
        try {
            // if file doesnt exists, then create it
            File file = new File(outputFile);
            if (!file.exists()) {
                file.createNewFile();
                // print file path
                System.out.println("Encrypted file created: " + file.getAbsolutePath());
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(encrypted.toByteArray());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
        }
    }

    private static void startDecrypt(String[] args, RSACryptoSystem rsa) {
        if (args.length < 3) {
            System.out.println("Please specify input and output files.");
            return;
        }
        String inputFile = args[1];
        String outputFile = args[2];
        String privateKeyFile = args[3];

        // read private key
        try {
            File file = new File(privateKeyFile);
            FileInputStream fis = new FileInputStream(file);
            byte[] encodedPrivateKey = new byte[(int) file.length()];
            fis.read(encodedPrivateKey);
            fis.close();
            rsa.getPrivateKey().parsePrivateKey(encodedPrivateKey);
        } catch (Exception e) {
            System.out.println("Error reading private key from file");
            e.printStackTrace();
        }

        // read file
        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Paths.get(inputFile));
        } catch (IOException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }

        // decrypt
        BigInteger decrypted = rsa.decrypt(new BigInteger(fileBytes));

        // write to file
        try {
            // if file doesnt exists, then create it
            File file = new File(outputFile);
            if (!file.exists()) {
                file.createNewFile();
                // print file path
                System.out.println("Decrypted file created: " + file.getAbsolutePath());
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(decrypted.toByteArray());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
        }
    }
}
