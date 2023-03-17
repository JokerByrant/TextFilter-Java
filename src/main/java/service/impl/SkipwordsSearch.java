package service.impl;


import service.ISkipwordsSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;

/**
 * 检测跳词
 *
 * @author sxh
 * @date 2023/3/3
 */
public class SkipwordsSearch implements ISkipwordsSearch {
    private int _firstMaxChar;
    private int[] _dict;

    private int[] _key;
    private int[] _next;
    // 跳词长度
    private int[] _checkLen;
    private int[] _failure;


    @Override
    public boolean findAll(char[] _ptext, int length, boolean[] result) {
        boolean find = false;
        int p = 0;
        for (int i = 0; i < length; i++) {
            int t1 = _ptext[i];
            int t = _dict[t1];
            if (t <= _firstMaxChar) {
                p = t;
                continue;
            }
            if (t == 0xffff) {
                continue;
            }
            int next = _next[p] + t;
            if (_key[next] == t) {
                int len = _checkLen[next];
                if (len > 0) {
                    find = true;
                    int idx = i;
                    while (len != 0) {
                        if (_dict[_ptext[idx]] != 0xffff) {
                            len--;
                        }
                        result[idx] = true;
                        idx--;
                    }
                }
                p = next;
            } else {
                while (p != 0) {
                    p = _failure[p];
                    next = _next[p] + t;
                    if (_key[next] == t) {
                        int len = _checkLen[next];
                        if (len > 0) {
                            find = true;
                            int idx = i;
                            while (len != 0) {
                                if (_dict[_ptext[idx]] != 0xffff) {
                                    len--;
                                }
                                result[idx] = true;
                                idx--;
                            }
                        }
                        p = next;
                        break;
                    }
                }
            }
        }
        return find;
    }

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _firstMaxChar = dis.reverseReadUnsignedShort();
        _dict = dis.readShortArray();
        _key = dis.readShortArray();
        _next = dis.readIntArray();
        _checkLen = dis.readIntArray();
        _failure = dis.readIntArray();
    }
}
