package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author sxh
 * @date 2023/3/6
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static byte[] readFile(String filePath) {
        try {
            return readFile(new FileInputStream(filePath));
        } catch (Exception e) {
            logger.error("文件解析失败！", e);
        }
        return null;
    }

    public static byte[] readFile(InputStream is) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] ch = new byte[1024];
            int readLen = 0;
            while ((readLen = is.read(ch)) != -1) {
                baos.write(ch, 0, readLen);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("文件解析失败！", e);
        }
        return null;
    }

    public static ByteArrayOutputStream gzipDecompress(byte[] data, int start) throws IOException {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data, start, data.length - start)) {
            try (GZIPInputStream zStream = new GZIPInputStream(stream)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zStream.read(buffer)) > 0) {
                    resultStream.write(buffer, 0, len);
                }
            }
        }
        return resultStream;
    }
}
