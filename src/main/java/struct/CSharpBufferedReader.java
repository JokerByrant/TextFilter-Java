package struct;

import java.io.BufferedReader;
import java.io.Reader;

/**
 * BufferedReader解析见：https://wangkuiwu.github.io/2012/05/23/BufferedReader/
 *
 * @author sxh
 * @date 2023/2/16
 */
public class CSharpBufferedReader extends BufferedReader {

    /**
     * @param in
     * @param sz 缓冲区大小
     */
    public CSharpBufferedReader(Reader in, int sz) {
        super(in, sz);
    }

    public CSharpBufferedReader(Reader in) {
        super(in);
    }
}
