package service.impl;


import pojo.KeywordInfo;
import pojo.TempWordsResultItem;
import service.IKeywordsSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * 查找匹配正常词、触线敏感词
 *
 * @author sxh
 * @date 2023/2/13
 */
public class KeywordsSearch2 implements IKeywordsSearch {
    // _firstMaxChar值是一个特殊值，经过特殊计算，字符映射值小于_firstMaxChar时必为敏感词的第一个字符，直接减少代码查询的工作量。
    private int _firstMaxChar;
    // 每个字符都先通过 _dict[字符] 映射到新的变量，_dict[字符] 返回值对应的含义如下：0.词库中没有该字符 1.字符为跳词 大于2.返回对应的key
    private int[] _dict;
    // 最小英文数字映射值
    private int _azNumMinChar;
    // 最大英文数字映射值
    private int _azNumMaxChar;

    private int[] _key;
    private int[] _next;
    private int[] _check;
    private int[] _failure;
    private int[][] _guides;

    private Function<Integer, KeywordInfo> _getKeyword;

    @Override
    public void setGetMatchKeyword(Function<Integer, KeywordInfo> func) {
        _getKeyword = func;
    }

    @Override
    public void findAll(char[] _ptext, int length, List<TempWordsResultItem> result) {
        int[] pdict = _dict;
        int[] pkey = _key;
        int[] pnext = _next;
        int[] pcheck = _check;
        int[] pfailure = _failure;

        // 初始索引位置
        int p = 0;
        for (int i = 0; i < length; i++) {
            // 获取字符
            int t1 = _ptext[i];
            // 转换为映射值
            int t = pdict[t1];
            // 映射值为1，为跳词
            if (t == 1) {
                continue;
            }
            // 映射值<=_firstMaxChar 时，为特殊敏感词字符，字符只出现在敏感词的第一位
            if (t <= _firstMaxChar) {
                p = t;
                continue;
            }
            // 下一个索引位置，只是可能，待验证
            int next = pnext[p] + t;
            if (pkey[next] == t) {
                int index = pcheck[next];
                if (index != 0 && checkNextChar(_ptext, length - 1, t, i, pdict)) {
                    int[] guides = _guides[index];
                    int start = i;
                    int tempLen = 1;
                    int tc = t;
                    for (int item : guides) {
                        KeywordInfo keyInfo = getMatchKeyword(item);
                        int len = keyInfo.getKeywordLength();
                        while (tempLen < len) {
                            if ((tc = pdict[_ptext[--start]]) != 1) {
                                tempLen++;
                            }
                        }
                        if (checkPreChar(_ptext, tc, start, pdict)) {
                            result.add(new TempWordsResultItem(start, i, keyInfo));
                        }
                    }
                }
                p = next;
            } else {
                while (p != 0) {
                    p = pfailure[p];
                    next = pnext[p] + t;
                    if (pkey[next] == t) {
                        int index = pcheck[next];
                        if (index != 0 && checkNextChar(_ptext, length - 1, t, i, pdict)) {
                            int[] guides = _guides[index];
                            int start = i;
                            int tempLen = 1;
                            int tc = t;
                            for (int item : guides) {
                                KeywordInfo keyInfo = getMatchKeyword(item);
                                int len = keyInfo.getKeywordLength();
                                while (tempLen < len) {
                                    if ((tc = pdict[_ptext[--start]]) != 1) {
                                        tempLen++;
                                    }
                                }
                                if (checkPreChar(_ptext, tc, start, pdict)) {
                                    result.add(new TempWordsResultItem(start, i, keyInfo));
                                }
                            }
                        }
                        p = next;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _firstMaxChar = dis.reverseReadUnsignedShort();
        _azNumMinChar = dis.reverseReadUnsignedShort();
        _azNumMaxChar = dis.reverseReadUnsignedShort();
        _dict = dis.readShortArray();
        _key = dis.readShortArray();

        _next = dis.readIntArray();
        _check = dis.readIntArray();
        _failure = dis.readIntArray();
        _guides = dis.readIntArray2();
    }

    private boolean checkNextChar(char[] _ptext, int length, int t, int i, int[] _pdict) {
        if (t < _azNumMinChar || t > _azNumMaxChar || i == length) {
            return true;
        }

        int tt = _pdict[_ptext[i + 1]];
        return tt < _azNumMinChar || tt > _azNumMaxChar;
    }

    private boolean checkPreChar(char[] _ptext, int t, int i, int[] _pdict) {
        if (t < _azNumMinChar || t > _azNumMaxChar || i == 0) {
            return true;
        }

        int tt = _pdict[_ptext[i - 1]];
        return tt < _azNumMinChar || tt > _azNumMaxChar;
    }

    public KeywordInfo getMatchKeyword(int resultIndex) {
        return _getKeyword.apply(resultIndex);
    }
}
