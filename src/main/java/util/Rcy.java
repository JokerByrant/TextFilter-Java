package util;

/**
 * @author sxh
 * @date 2023/2/13
 */
import java.nio.charset.StandardCharsets;

public class Rcy {
    private static final int keyLen = 256;

    public static void encrypt(byte[] data, String pass) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (data.length == 0) {
            throw new IllegalArgumentException("data cannot be empty");
        }
        if (pass == null || pass.isEmpty()) {
            throw new IllegalArgumentException("pass cannot be null or empty");
        }

        byte[] mBox = getKey(pass.getBytes(StandardCharsets.UTF_8), keyLen);
        int i = 0;
        int j = 0;
        for (int offset = 0; offset < data.length; offset++) {
            i = (i + 1) & 0xff;
            j = (j + byte2Int(mBox[i])) & 0xff;

            int a = byte2Int(data[offset]);
            byte c = (byte) (a ^ byte2Int(mBox[byte2Int(mBox[i]) & byte2Int(mBox[j])]));
            data[offset] = c;

            j = (j + a + byte2Int(c)) & 0xff;
        }
    }

    private static byte[] getKey(byte[] pass, int kLen) {
        byte[] mBox = new byte[kLen];
        for (int i = 0; i < kLen; i++) {
            mBox[i] = (byte) i;
        }

        int j = 0;
        for (int i = 0; i < kLen; i++) {
            j = (j + byte2Int(mBox[i]) + byte2Int(pass[i % pass.length])) % kLen;
            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
        }
        return mBox;
    }

    private static int byte2Int(byte num) {
        return 0xff & num;
    }

}

