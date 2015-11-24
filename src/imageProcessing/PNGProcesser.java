package imageProcessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by Arnold on 2015-11-20.
 */
public class PNGProcesser {
    private Mat imageData;
    private PNGHeader pngHeader;


    public PNGProcesser(String filePath) throws IOException { //konstruktor ma zapewnic poprawnosc pliku i wczytac obiekt MAT obrazu, oraz dane?
        //sprawdzenie nazwy pliku
        if (!filePath.contains(".png"))
            throw new IOException("wrong header");
        //wczytanie danych naglowka
        pngHeader = new PNGHeader(filePath);

        //wczytanie danych do instancji klasy Mat
        imageData = Imgcodecs.imread(filePath);
        if (imageData.dataAddr() == 0)
            throw new IOException("Couldn't read data");
    }

    public static BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.
                rows(), type);
        //targetPixels reprezentuje bufor danych(tablice bajtow) obiektu image
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
                getDataBuffer()).getData();
        //szybkie kopiowanie do bufora danych obiektu image z bufora
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public static byte[] getImageValueArray(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer); // get all the pixels
        return buffer;
    }

    public String getHeaderDescription() {
        return pngHeader.toString();
    }

    public BufferedImage getImageFFT(String filePath) {
        //change imageData to one-channel(colour) matrix

        Mat grayMat = new Mat();
        if (imageData.channels() > 1)
            Imgproc.cvtColor(imageData, grayMat, Imgproc.COLOR_RGB2GRAY);
        else
            grayMat = imageData;
        //change grayMat to matrix represented with float values
        Mat floatGrayMat = new Mat();
        grayMat.convertTo(floatGrayMat, CvType.CV_32FC1);
        //create 2-channel matrix of real and imaginary parts
        ArrayList<Mat> matList = new ArrayList<>();
        Mat zeroMat = new Mat(floatGrayMat.size(), CvType.CV_32F);
        matList.add(floatGrayMat);
        matList.add(zeroMat);
        Mat complexMat = new Mat();
        Core.merge(matList, complexMat);
        //now we can do fft, we use info in complexMat and write it to the same matrix
        Core.dft(complexMat, complexMat);
        //we need to split image to calculate magnitude
        ArrayList<Mat> splitted = new ArrayList<>();
        Core.split(complexMat, splitted);
        Mat magnitude = new Mat();
        Core.magnitude(splitted.get(0), splitted.get(1), magnitude);
        //now we do logarithm, but firstly we add 1 to all values in case having no negatives
        Core.add(Mat.ones(magnitude.size(), CvType.CV_32F), magnitude, magnitude);
        Core.log(magnitude, magnitude);
        //shift image to center
        int cx = magnitude.cols() / 2;
        int cy = magnitude.rows() / 2;
        Mat q0 = new Mat(magnitude, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(magnitude, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(magnitude, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(magnitude, new Rect(cx, cy, cx, cy));
        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
        //and the last step is normalizing of  image and convert to 1-channel unsigned byte
        magnitude.convertTo(magnitude, CvType.CV_8UC1);
        Core.normalize(magnitude, magnitude, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

        return toBufferedImage(magnitude);
    }

    public BufferedImage toBufferedImage() {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (imageData.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = imageData.channels() * imageData.cols() * imageData.rows();
        byte[] buffer = new byte[bufferSize];
        imageData.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(imageData.cols(), imageData.
                rows(), type);
        //targetPixels reprezentuje bufor danych(tablice bajtow) obiektu image
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
                getDataBuffer()).getData();
        //szybkie kopiowanie do bufora danych obiektu image z bufora
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public byte[] getImageValueArray() {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (imageData.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = imageData.channels() * imageData.cols() * imageData.rows();
        byte[] buffer = new byte[bufferSize];
        imageData.get(0, 0, buffer); // get all the pixels
        return buffer;
    }

    public void setImageData(Mat mat){
        imageData = mat;
    }

    public void setImageMatValues(byte[] values) {
        imageData.put(0, 0, values);
    }

    public void saveImage(String filename) {
        Imgcodecs.imwrite(filename, imageData);
    }

}
