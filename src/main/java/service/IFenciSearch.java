package service;


import pojo.FenciKeywordInfo;
import pojo.TempWordsResultItem;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * 分词搜索类
 *
 * @author sxh
 * @date 2023/2/13
 */
public interface IFenciSearch {
    void setGetMatchKeyword(Function<Integer, FenciKeywordInfo> func);

    void findAll(char[] text, int length, List<TempWordsResultItem> result);

    void load(CSharpDataInputStream dis) throws IOException;
}
