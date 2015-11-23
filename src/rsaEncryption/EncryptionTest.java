package rsaEncryption;

import imageProcessing.PNGProcesser;
import org.opencv.core.Core;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Created by Arnold on 2015-11-23.
 */
public class EncryptionTest {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String path = "C:\\IdeaProjects\\java\\E-media 2\\obraz1.png";
        String path2 = path + "(2)";
        byte[] imageData;
        ObjectInputStream publicKeyInput = null;
        ObjectInputStream privateKeyInput = null;
        PrivateKey privateKey;
        PublicKey publicKey;
        String publicKeyPath = System.getProperty("user.dir") + RSAEncryption.PUBLIC_KEY_FILE;
        String privateKeyPath = System.getProperty("user.dir") + RSAEncryption.PRIVATE_KEY_FILE;

        try {
            PNGProcesser pngProcesser = new PNGProcesser(path);
            publicKeyInput = new ObjectInputStream(new FileInputStream(publicKeyPath));
            publicKey = (PublicKey) publicKeyInput.readObject();
            privateKeyInput = new ObjectInputStream(new FileInputStream(privateKeyPath));
            privateKey = (PrivateKey) privateKeyInput.readObject();

            imageData = pngProcesser.getImageValueArray();
            int loopEnd = imageData.length / 117;
            byte[] buffor = new byte[129];

            byte[] encryptedImage = RSAEncryption.encryptImage(imageData, publicKey);
            byte[] decryptedImage = RSAEncryption.decryptImage(encryptedImage, privateKey);

            System.out.println(imageData.length);
            System.out.println(decryptedImage.length);
            System.out.println(encryptedImage.length);
            System.out.println(imageData.length % 117);
            System.out.println(encryptedImage.length % 129);
            pngProcesser.setImageMatValues(encryptedImage);
            //pngProcesser.saveImage(path2);
            System.err.println();
            /*
            for (int i = 0; i < loopEnd; i++) {
                //szyfrowanie


                byte[] usedDataSegment = Arrays.copyOfRange(imageData, i * 117, (i + 1) * 117);
                byte[] encryptedData = RSAEncryption.encrypt(usedDataSegment, publicKey);
                System.out.println("Iteration: " + i);
                System.out.println("UsedDataSegmentlength: " + usedDataSegment.length);
                System.out.println("Encryptedbuffor lenght: " + encryptedData.length);
                byte[] decryptedData = RSAEncryption.decrypt(encryptedData, privateKey);
                System.out.println("DecryptedBuffor Length: " + decryptedData.length);
                System.out.println("czy zgodne: " + Arrays.equals(usedDataSegment, decryptedData));

            }
            */

        } catch (Exception exeption) {
            exeption.printStackTrace();
        }

    }
}
