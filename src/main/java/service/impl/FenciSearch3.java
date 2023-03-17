package service.impl;


import pojo.FenciKeywordInfo;
import pojo.TempWordsResultItem;
import service.IFenciSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * 查找匹配通用字
 *
 * @author sxh
 * @date 2023/2/13
 */
public class FenciSearch3 implements IFenciSearch {
    private int _firstMaxChar;
    private int _azNumMinChar;
    private int _azNumMaxChar;
    private int[] _dict;

    private int[] _key;
    private int[] _next;
    private int[] _check;
    private int[] _failure;
    private int[][] _guides;

    private Function<Integer, FenciKeywordInfo> _getKeyword;

    @Override
    public void setGetMatchKeyword(Function<Integer, FenciKeywordInfo> func) {
        _getKeyword = func;
    }

    @Override
    public void findAll(char[] _ptext, int length, List<TempWordsResultItem> result) {
        int[] pdict = _dict;
        int[] pkey = _key;
        int[] pnext = _next;
        int[] pcheck = _check;
        int[] pfailure = _failure;
        int p = 0;
        for (int i = 0; i < length; i++) {
            char t1 = _ptext[i];
            int t = pdict[t1];
            if (t == 1) {
                continue;
            }
            if (t <= _firstMaxChar) {
                p = t;
                continue;
            }
            int next = pnext[p] + t;
            if (pkey[next] == t) {
                int index = pcheck[next];
                if (index != 0 && checkNextChar(_ptext, length - 1, t, i, pdict)) {
                    int[] guides = _guides[index];
                    int start = i;
                    int tempLen = 1;
                    int tc = t;
                    for (int ij = 0; ij < guides.length; ij++) {
                        int item = guides[ij];
                        FenciKeywordInfo keyInfo = getMatchKeyword(item);
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
                            for (int ij = 0; ij < guides.length; ij++) {
                                int item = guides[ij];
                                FenciKeywordInfo keyInfo = getMatchKeyword(item);
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

    public FenciKeywordInfo getMatchKeyword(int resultIndex) {
        return _getKeyword.apply(resultIndex);
    }
}
