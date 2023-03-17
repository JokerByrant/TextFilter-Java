package service;


import pojo.TempMultiWordsResult;
import pojo.TempWordsResultItem;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;

/**
 * 多组词搜索类
 *
 * @author sxh
 * @date 2023/3/3
 */
public interface IMultiWordsSearch {
    List<TempMultiWordsResult> findAll(List<TempWordsResultItem> txt);

    void load(CSharpDataInputStream dis) throws IOException;
}
