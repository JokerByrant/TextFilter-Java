package service;

import pojo.KeywordInfo;
import pojo.TempWordsResultItem;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * 自定义正则搜索类
 *
 * @author sxh
 * @date 2023/3/3
 */
public interface IACRegexSearch {
    void setGetMatchKeyword(Function<Integer, KeywordInfo> func);

    void SetDict(byte[] skipIndexs, int[][] dicts, ISkipwordsSearch[] skipwordsSearchs, boolean[] useSkipOnce);

    void findAll(char[] text, int length, List<TempWordsResultItem> result);

    void load(CSharpDataInputStream dis) throws IOException;
}
