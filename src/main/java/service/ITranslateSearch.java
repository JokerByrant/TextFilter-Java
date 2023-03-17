package service;


import pojo.ReadStreamBase;
import struct.CSharpDataInputStream;

import java.io.IOException;

/**
 * 转义类
 *
 * @author sxh
 * @date 2023/2/13
 */
public interface ITranslateSearch {
    void load(CSharpDataInputStream dis) throws IOException;

    ReadStreamBase replace(String text, boolean skipBidi);
}
