package rsaEncryption;

import imageProcessing.PNGProcesser;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

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


    public static Mat encryptImage(Mat mat, PublicKey key) {
        byte[] data = PNGProcesser.getImageValueArray(mat);
        int channels = mat.channels(); //channels number
        int loopEnd = (data.length / RSAEncryption.DATA_SEGMENT);
        int blocksNumber = loopEnd + 1; //+1 is from header information
        int endingDataLength = data.length % RSAEncryption.DATA_SEGMENT;
        if (endingDataLength != 0)
            blocksNumber++;
        int bufforSize = blocksNumber * (ENCRYPTED_DATA_SEGMENT);
        HeaderInfo encryptedImageInfo = getHeaderInfo(bufforSize, channels);
        int additionalBytes = encryptedImageInfo.getAdditionalBytes();
        bufforSize += additionalBytes;


        byte[] header = getByteArray(mat.cols(), mat.rows(), mat.channels(), additionalBytes);
        byte[] encryptedDataSegment = null; //of length 128
        byte[] dataSegment = null; //length DATA_SEGMENT or less at end
        byte[] encryptedImage = new byte[bufforSize];
        //add encrypted header info
        encryptedDataSegment = encrypt(header, key);
        for (int k = 0; k < ENCRYPTED_DATA_SEGMENT; k++)
            encryptedImage[k] = encryptedDataSegment[k];
        //add full blocks
        for (int i = 0; i < loopEnd; i++) {
            dataSegment = Arrays.copyOfRange(data, i * DATA_SEGMENT, (i + 1) * DATA_SEGMENT);
            encryptedDataSegment = encrypt(dataSegment, key);
            //copy result to global result
            for (int k = 0; k < ENCRYPTED_DATA_SEGMENT; k++) {
                encryptedImage[(i + 1) * ENCRYPTED_DATA_SEGMENT + k] = encryptedDataSegment[k];
            }
        }
        //add ending if exist
        if (endingDataLength != 0) {
            byte[] endingSegment = Arrays.copyOfRange(data, DATA_SEGMENT * loopEnd, data.length);
            encryptedDataSegment = encrypt(endingSegment, key);
            for (int k = 0; k < DATA_SEGMENT; k++) {
                encryptedImage[(loopEnd + 1) * ENCRYPTED_DATA_SEGMENT + k] = encryptedDataSegment[k];
            }
        }
        //add remaining bytes
        for (int i = bufforSize - additionalBytes; i < bufforSize; i++) {
            encryptedImage[i] = (byte) (255 * Math.random());
        }
        int imageType;
        if (mat.channels() == 3)
            imageType = CvType.CV_8UC3;
        else imageType = CvType.CV_8UC1;

        Mat encryptedImageMatrix = new Mat(encryptedImageInfo.getHeight(), encryptedImageInfo.getWidth(), imageType);
        encryptedImageMatrix.put(0, 0, encryptedImage);
        return encryptedImageMatrix;
    }


    public static Mat decryptImage(Mat mat, PrivateKey key) {
        byte[] data = PNGProcesser.getImageValueArray(mat);

        byte[] encryptedDataSegment;
        byte[] decryptedDataSegment;
        byte[] decryptedImage;
        //decrypt first block of data to know image resolution
        encryptedDataSegment = Arrays.copyOfRange(data, 0, ENCRYPTED_DATA_SEGMENT);
        int[] encodedImageInfo = getIntArray(RSAEncryption.decrypt(encryptedDataSegment, key));
        int width = encodedImageInfo[0];
        int height = encodedImageInfo[1];
        int channels = encodedImageInfo[2];
        int additionalBytes = encodedImageInfo[3];
        int bufforLength = width * height * channels;
        decryptedImage = new byte[bufforLength];
        int encryptedBlocks = (data.length - (ENCRYPTED_DATA_SEGMENT + additionalBytes)) / ENCRYPTED_DATA_SEGMENT;
        for (int i = 0; i < encryptedBlocks -1 ; i++) {
            encryptedDataSegment = Arrays.copyOfRange(data, (i + 1) * ENCRYPTED_DATA_SEGMENT, (i + 2) * ENCRYPTED_DATA_SEGMENT);
            decryptedDataSegment = RSAEncryption.decrypt(encryptedDataSegment, key);
            for (int k = 0; k < DATA_SEGMENT; k++) {
                decryptedImage[i * DATA_SEGMENT + k] = decryptedDataSegment[k];
            }
        }

        int imageType;
        if (channels == 1)
            imageType = CvType.CV_8UC1;
        else imageType = CvType.CV_8UC3;

        Mat decryptedImageMat = new Mat(width,height,imageType);
        decryptedImageMat.put(0,0,decryptedImage);

        return decryptedImageMat;
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

    private static byte[] getByteArray(int width, int height, int channels, int additionalDataLength) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.putInt(channels);
        buffer.putInt(additionalDataLength);
        return buffer.array();
    }

    private static int[] getIntArray(byte[] data) {
        int intCount = data.length / 4;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int[] intArray = new int[intCount];
        for (int i = 0; i < intCount; i++) {
            intArray[i] = buffer.getInt();
        }
        return intArray;
    }


    private static HeaderInfo getHeaderInfo(int bufforSize, int channels) {
        int newWidth;
        int newHeight;
        int additionalBytes;
        if (channels == 3) {
            int n, m;
            int minPixelNumber = (bufforSize / 3) + 1;
            n = ((int) Math.sqrt(minPixelNumber)) + 1; // n^2 na pewno wieksze od data.length
            m = n;
            while (m * n * 3 < bufforSize)
                n++;
            newWidth = n;
            newHeight = m;
            additionalBytes = newWidth * newHeight * 3 - bufforSize;

        } else { //channel == 1
            int n, m;
            n = ((int) Math.sqrt(bufforSize)) + 1; // n^2 na pewno wieksze od data.length
            if (n * (n - 1) >= bufforSize)
                m = n - 1;
            else {
                m = n;
            }
            newWidth = n;
            newHeight = m;
            additionalBytes = newWidth * newHeight - bufforSize;
        }


        return new HeaderInfo(newHeight, newWidth, channels, additionalBytes);
    }


}


class HeaderInfo {
    private int height;
    private int width;
    private int channels;
    private int additionalBytes;

    public HeaderInfo(int height, int width, int channels, int additionalInfo) {
        this.height = height;
        this.width = width;
        this.channels = channels;
        this.additionalBytes = additionalInfo;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getAdditionalBytes() {
        return additionalBytes;
    }

    public void setAdditionalBytes(int additionalBytes) {
        this.additionalBytes = additionalBytes;
    }
}