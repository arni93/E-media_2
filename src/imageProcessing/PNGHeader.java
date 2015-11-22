package imageProcessing;

import java.io.*;

/**
 * Created by Arnold on 2015-11-20.
 */
public class PNGHeader {
    private int[] signature;
    private int headerDataLength;
    private int width;
    private int height;
    private int bitDepth;
    private int colourType;
    private int compressionMethod;
    private int filterMethod;
    private int interlaceMethod;

    private String filePath;

    public PNGHeader(String filePath) throws IOException {
        this.filePath = filePath;
        signature = new int[8];
        getHeaderInfo(this.filePath);
    }

    private void getHeaderInfo(String filePath) throws IOException {
        DataInputStream dataStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
        for (int i = 0; i < 8; i++) {
            signature[i] = 0xFF & dataStream.readByte();
        }
        System.out.println();
        headerDataLength = dataStream.readInt();
        dataStream.skipBytes(4);

        width = dataStream.readInt();
        height = dataStream.readInt();
        bitDepth = dataStream.readByte();
        colourType = dataStream.readByte();
        compressionMethod = dataStream.readByte();
        filterMethod = dataStream.readByte();
        interlaceMethod = dataStream.readByte();
        dataStream.close();
    }

    public int[] getSignature() {
        return signature;
    }

    public void setSignature(int[] signature) {
        this.signature = signature;
    }

    public int getHeaderDataLength() {
        return headerDataLength;
    }

    public void setHeaderDataLength(int headerDataLength) {
        this.headerDataLength = headerDataLength;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public int getColourType() {
        return colourType;
    }

    public void setColourType(int colourType) {
        this.colourType = colourType;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    public void setCompressionMethod(int compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    public int getFilterMethod() {
        return filterMethod;
    }

    public void setFilterMethod(int filterMethod) {
        this.filterMethod = filterMethod;
    }

    public int getInterlaceMethod() {
        return interlaceMethod;
    }

    public void setInterlaceMethod(int interlaceMethod) {
        this.interlaceMethod = interlaceMethod;
    }

    public String getColourDescription() {
        String desc = "";
        if (colourType == 0)
            desc = "Each pixel is greyscale sample";
        if (colourType == 2)
            desc = "Each pixel is R,G,B triple";
        if (colourType == 3)
            desc = "Each pixel is a palette index";
        if (colourType == 4)
            desc = "Each pixel is a greyscale sample followed by an alpha sample";
        if (colourType == 6)
            desc = "Each pixel is an R,G,B triple followed by an alpha sample";
        return desc;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(100);
        builder.append("Signature: ");
        for (int b : signature) {
            builder.append(b);
            builder.append(" ");
        }
        builder.append('\n');
        builder.append("Header length: ");
        builder.append(headerDataLength);
        builder.append(" bits");
        builder.append('\n');
        builder.append("Width: ");
        builder.append(width);
        builder.append('\n');
        builder.append("Height: ");
        builder.append(height);
        builder.append('\n');
        builder.append("Bit Depth: ");
        builder.append(bitDepth);
        builder.append('\n');
        builder.append("Colour type: ");
        builder.append(colourType);
        builder.append('\n');
        builder.append("Description:");
        builder.append('\n');
        builder.append(getColourDescription());
        builder.append('\n');
        builder.append("Compression method: ");
        builder.append(compressionMethod);
        builder.append('\n');
        builder.append("Filter method:");
        builder.append(filterMethod);
        builder.append('\n');
        builder.append("Interlace method: ");
        builder.append(interlaceMethod);
        builder.append('\n');
        return builder.toString();
    }
}
