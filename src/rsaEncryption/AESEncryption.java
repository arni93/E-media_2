package rsaEncryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Arnold on 2015-11-23.
 */
public class AESEncryption {
    public static final String ALGORITHM = "AES";

    public static final String KEY_FILE = "/key/AES.key";

    public static SecretKey generateKey() {
        SecretKey key2 = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom rand = new SecureRandom();
            keyGen.init(rand);
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            key2 = secretKey;

            File keyFile = new File(System.getProperty("user.dir") + KEY_FILE);
            if (keyFile.getParentFile() != null)
                keyFile.getParentFile().mkdirs();

            ObjectOutputStream outputStream;
            outputStream = new ObjectOutputStream(new FileOutputStream(keyFile));
            outputStream.writeObject(secretKey);
            outputStream.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return key2;

    }

    public  static boolean isKeyPresent(){
        String filePath = System.getProperty("user.dir") + KEY_FILE;
        File keyFile = new File(filePath);
        return keyFile.exists();
    }

    public static byte[] encrypt(byte[] data, SecretKey key){
        byte[] encryptedData = null;
        try{
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            encryptedData = cipher.doFinal(data);

        } catch (Exception e){
            e.printStackTrace();
        }
        return encryptedData;
    }

    public static byte[] decrypt(byte[] data, SecretKey key){
        byte[] decryptedData = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,key);
            decryptedData = cipher.doFinal(data);
        } catch (Exception e){
            e.printStackTrace();
        }
        return decryptedData;
    }


}
