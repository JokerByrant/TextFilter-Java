package struct;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 敏感词词库文件是通过 C# 的 I/O 流写入的，C# 中的 I/O 流操作与 Java 有所不同，这个类用来模拟 C# 的 I/O 流操作，主要是读操作
 *
 * @author sxh
 * @date 2023/2/16
 */
public class CSharpDataInputStream extends DataInputStream {
    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public CSharpDataInputStream(InputStream in) {
        super(in);
    }

    public int reverseReadInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        return ((ch1) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
    }

    public int reverseReadLong() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        int ch5 = in.read();
        int ch6 = in.read();
        int ch7 = in.read();
        int ch8 = in.read();
        return ((ch1) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24) + (ch5 << 32) + (ch6 << 40) + (ch7 << 48) + (ch8 << 56));
    }

    public int reverseReadUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        return (ch1) + (ch2 << 8);
    }

    public String readString() throws IOException {
        int length = readUnsignedByte();
        byte[] data = new byte[length];
        readFully(data, 0, length);
        return new String(data, StandardCharsets.UTF_8);
    }

    public final char readCharForCSharp() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int i = (ch1) + (ch2 << 8);
        return (char) ch1;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] arr = new byte[length];
        readFully(arr);
        return arr;
    }


    public int[] readShortArray() throws IOException {
        int size = reverseReadInt();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = reverseReadUnsignedShort();
        }
        return arr;
    }

    public boolean[] readBooleanArray() throws IOException {
        int size = reverseReadInt();
        boolean[] arr = new boolean[size];
        for (int i = 0; i < size; i++) {
            arr[i] = readBoolean();
        }
        return arr;
    }

    public int[] readIntArray() throws IOException {
        int size = reverseReadInt();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = reverseReadInt();
        }
        return arr;
    }

    public int[][] readIntArray2() throws IOException {
        int size = reverseReadInt();
        int[][] arr = new int[size][];
        for (int i = 0; i < size; i++) {
            int size2 = reverseReadInt();
            arr[i] = new int[size2];
            for (int j = 0; j < size2; j++) {
                arr[i][j] = reverseReadInt();
            }
        }
        return arr;
    }
}
