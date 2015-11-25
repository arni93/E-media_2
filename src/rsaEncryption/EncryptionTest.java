package rsaEncryption;

import imageProcessing.PNGProcesser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
        String path2 = "C:\\IdeaProjects\\java\\E-media 2\\obraz1_encrypted.png";
        ;
        byte[] imageData;
        ObjectInputStream publicKeyInput = null;
        ObjectInputStream privateKeyInput = null;
        PrivateKey privateKey;
        PublicKey publicKey;
        String publicKeyPath = System.getProperty("user.dir") + RSAEncryption.PUBLIC_KEY_FILE;
        String privateKeyPath = System.getProperty("user.dir") + RSAEncryption.PRIVATE_KEY_FILE;


        try {
            Mat image = Imgcodecs.imread(path);
            Mat encryptedImage;
            Mat decryptedImage;
            publicKeyInput = new ObjectInputStream(new FileInputStream(publicKeyPath));
            publicKey = (PublicKey) publicKeyInput.readObject();
            privateKeyInput = new ObjectInputStream(new FileInputStream(privateKeyPath));
            privateKey = (PrivateKey) privateKeyInput.readObject();

            encryptedImage = RSAEncryption.encryptImage(image, publicKey);
            decryptedImage = RSAEncryption.decryptImage(encryptedImage, privateKey);
            System.out.println(PNGProcesser.getImageValueArray(image).length);
            System.out.println(PNGProcesser.getImageValueArray(decryptedImage).length);
            System.out.println(Arrays.equals(PNGProcesser.getImageValueArray(image),PNGProcesser.getImageValueArray(decryptedImage)));
            //Imgcodecs.imwrite(path2,encryptedImage);


        } catch (Exception exeption) {
            exeption.printStackTrace();
        }

    }
}
