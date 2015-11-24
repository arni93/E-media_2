package rsaEncryption;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by Arnold on 2015-11-22.
 */
public class RSAEncryption {
    public static final String ALGORITHM = "RSA";
    public static final String PRIVATE_KEY_FILE = "/keys/private.key";
    public static final String PUBLIC_KEY_FILE = "/keys/public.key";
    private static final int DATA_SEGMENT = 117;
    private static final int ENCRYPTED_DATA_SEGMENT = 128;

    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(System.getProperty("user.dir") + PRIVATE_KEY_FILE);
            File publicKeyFile = new File(System.getProperty("user.dir") + PUBLIC_KEY_FILE);
            // Create files to store public and private key

            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean areKeysPresent() {
        String privateKeyFilePath = System.getProperty("user.dir") + PRIVATE_KEY_FILE;
        String publicKeyFilePath = System.getProperty("user.dir") + PUBLIC_KEY_FILE;

        File privateKey = new File(privateKeyFilePath);
        File publicKey = new File(publicKeyFilePath);

        return (privateKey.exists() && publicKey.exists());
    }

    public static byte[] encrypt(byte[] data, PublicKey key) {
        byte[] encryptedData = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedData = cipher.doFinal(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return encryptedData;
    }

    public static byte[] encrypt(byte[] data, int start, int offset, PublicKey key) {
        byte[] encryptedData = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedData = cipher.doFinal(data, start, offset);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return encryptedData;
    }

    public static byte[] encryptImage(byte[] data, PublicKey key) {
        int loopEnd = data.length / RSAEncryption.DATA_SEGMENT;
        int endingDataLength = data.length % RSAEncryption.DATA_SEGMENT;
        //count how big byte[] array we need
        int bufforSize = (loopEnd + 1) * (ENCRYPTED_DATA_SEGMENT + 1);
        if (endingDataLength == 0) {
            bufforSize = loopEnd * (ENCRYPTED_DATA_SEGMENT + 1);
        }

        //keeps portion of encrypted data and add 0 at End
        byte[] encryptedDataSegment; //of length 128
        //Data segment of data array
        byte[] DataSegment; //length DATA_SEGMENT or other at the end
        byte[] encryptedImage = new byte[bufforSize]; //whole encrypted Image

        for (int i = 0; i < loopEnd; i++) {
            DataSegment = Arrays.copyOfRange(data, i * DATA_SEGMENT, (i + 1) * DATA_SEGMENT);
            encryptedDataSegment = encrypt(DataSegment, key);
            //copy result to global result
            for (int k = 0; k < ENCRYPTED_DATA_SEGMENT; k++) {
                encryptedImage[i * (ENCRYPTED_DATA_SEGMENT + 1) + k] = encryptedDataSegment[k];
            }
            //add zero at end
            encryptedImage[(i + 1) * (ENCRYPTED_DATA_SEGMENT + 1) - 1] = 0;
        }
        //add ending
        if (endingDataLength != 0) {
            byte[] endingSegment = Arrays.copyOfRange(data, DATA_SEGMENT * loopEnd, data.length);
            encryptedDataSegment = encrypt(endingSegment, key);
            for (int k = 0; k < DATA_SEGMENT; k++) {
                encryptedImage[loopEnd * (ENCRYPTED_DATA_SEGMENT + 1) + k] = encryptedDataSegment[k];
            }
            //add zero
            encryptedImage[(loopEnd + 1) * (ENCRYPTED_DATA_SEGMENT + 1) - 1] = 0;
        }
        return encryptedImage;
    }

    private static byte[] getByteArray(int width, int height, int colorType, int additionalDataLength) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.putInt(colorType);
        buffer.putInt(additionalDataLength);
        return buffer.array();
    }

    private static int[] getIntArray(byte[] data) {
        int intCount = data.length / 4;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int[] intArray = new int[intCount];
        for (int i = 0; i < intCount; i++){
           intArray[i] = buffer.getInt();
        }
        return intArray;
    }

    public static byte[] decryptImage(byte[] data, PrivateKey key) {
        int segmentsNumber = data.length / (ENCRYPTED_DATA_SEGMENT + 1);
        int bufforSize;
        byte[] lastBlock = Arrays.copyOfRange(data,
                (segmentsNumber - 1) * (ENCRYPTED_DATA_SEGMENT + 1), segmentsNumber * (ENCRYPTED_DATA_SEGMENT));
        //odkoduj koniec pliku, zeby wiedziec ile pamieci dokladnie zaalokowac dla rezultatu globalnego
        byte[] endingBytes = RSAEncryption.decrypt(lastBlock, key);
        bufforSize = (segmentsNumber - 1) * DATA_SEGMENT + endingBytes.length;
        byte[] decryptedImage = new byte[bufforSize];


        return decryptedImage;
    }

    public static byte[] decrypt(byte[] data, PrivateKey key) {
        byte[] decryptedData = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedData = cipher.doFinal(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return decryptedData;
    }

}
