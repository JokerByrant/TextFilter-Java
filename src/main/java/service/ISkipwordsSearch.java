package service;


import struct.CSharpDataInputStream;

import java.io.IOException;

/**
 * 跳词搜索类
 *
 * @author sxh
 * @date 2023/3/3
 */
public interface ISkipwordsSearch {
    boolean findAll(char[] _ptext, int length, boolean[] result);

    void load(CSharpDataInputStream dis) throws IOException;
}
