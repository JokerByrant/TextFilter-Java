package service;


import pojo.KeywordInfo;
import pojo.TempWordsResultItem;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * 关键字搜索类
 *
 * @author sxh
 * @date 2023/2/13
 */
public interface IKeywordsSearch {
    void setGetMatchKeyword(Function<Integer, KeywordInfo> func);

    void findAll(char[] _ptext, int length, List<TempWordsResultItem> result);

    void load(CSharpDataInputStream dis) throws IOException;
}

