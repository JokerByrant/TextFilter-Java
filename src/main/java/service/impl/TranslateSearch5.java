package service.impl;

/**
 * @author sxh
 * @date 2023/2/13
 */

import pojo.ReadStreamBase;
import pojo.TextStream;
import service.ITranslateSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.Arrays;

public class TranslateSearch5 implements ITranslateSearch {
    private int _firstMaxChar;
    private int[] _dict;
    private int[] _bidiDict;

    private int[] _key;
    private int[] _next;
    private int[] _check;
    private int[] _failure;
    private byte[] _len;
    private String[] _keywords;

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _firstMaxChar = dis.reverseReadUnsignedShort();
        _dict = dis.readShortArray();
        _bidiDict = dis.readShortArray();
        _key = dis.readShortArray();
        _next = dis.readIntArray();
        _check = dis.readIntArray();
        _failure = dis.readIntArray();
        int len = dis.reverseReadInt();
        this._len = new byte[len];
        for (int i = 0; i < len; i++) {
            this._len[i] = dis.readByte();
        }
        len = dis.reverseReadInt();
        _keywords = new String[len];
        for (int i = 0; i < len; i++) {
            _keywords[i] = dis.readString();
        }
    }

    @Override
    public ReadStreamBase replace(String text, boolean skipBidi) {
        if (skipBidi) {
            return replace1(text);
        }
        return replace2(text);
    }

    private ReadStreamBase replace1(final String text) {
        final char[] sbTemp = new char[text.length()];
        final int[] startsTemp = new int[text.length()];
        final int[] endsTemp = new int[text.length()];
        int idx = 0;

        final char[] chs = text.toCharArray();

        final char[] sb = sbTemp;
        int p = 0;
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final char t1 = chs[i];

            final int t = _bidiDict[t1];
            if (t == 1) {
                p = 0;
                continue;
            }
            if (t <= _firstMaxChar) {
                p = t;
                sb[idx] = t1;
                startsTemp[idx] = i;
                endsTemp[idx] = i;
                idx++;
                continue;
            }

            int nextIndex = _next[p] + t;

            boolean find = _key[nextIndex] == t;
            if (!find && p != 0) {
                do {
                    p = _failure[p];
                    nextIndex = _next[p] + t;
                    if (_key[nextIndex] == t) {
                        find = true;
                        break;
                    }
                    if (p == 0) {
                        nextIndex = 0;
                        break;
                    }
                } while (true);
            }

            if (find) {
                final int index = _check[nextIndex];
                if (index != 0) {
                    final int len = _len[index];
                    final String keyword = _keywords[index];
                    if (len == 2 && keyword.length() == 2) {
                        sb[idx - 1] = keyword.charAt(0);
                        sb[idx] = keyword.charAt(1);
                    } else if (len == 2) {
                        sb[idx - 1] = keyword.charAt(0);
                        endsTemp[idx - 1] = i;
                        continue;
                    } else {
                        sb[idx] = keyword.charAt(0);
                    }
                } else {
                    sb[idx] = t1;
                }
                p = nextIndex;
            } else {
                sb[idx] = t1;
            }
            startsTemp[idx] = i;
            endsTemp[idx] = i;
            idx++;
        }

        final TextStream stream = new TextStream(chs);
        stream.testingText = Arrays.copyOf(sbTemp, idx);
        stream.start = Arrays.copyOf(startsTemp, idx);
        stream.end = Arrays.copyOf(endsTemp, idx);
        return stream;
    }

    private ReadStreamBase replace2(final String text) {
        char[] sb_temp = new char[text.length()];
        int[] starts_temp = new int[text.length()];
        int[] ends_temp = new int[text.length()];
        int idx = 0;

        char[] chs = text.toCharArray();
        int p = 0;
        for (int i = 0; i < text.length(); i++) {
            char t1 = chs[i];
            int t = _dict[t1];
            if (t <= _firstMaxChar) {
                p = t;
                sb_temp[idx] = t1;
                starts_temp[idx] = i;
                ends_temp[idx] = i;
                idx++;
                continue;
            }

            int next = _next[p] + t;

            boolean find = (_key[next] == t);
            if (!find && p != 0) {
                do {
                    p = _failure[p];
                    next = _next[p] + t;
                    if (_key[next] == t) {
                        find = true;
                        break;
                    }
                    if (p == 0) {
                        next = 0;
                        break;
                    }
                } while (true);
            }

            if (find) {
                int index = _check[next];
                if (index != 0) {
                    int len = _len[index];
                    char[] key = _keywords[index].toCharArray();
                    if (len == 2 && key.length == 2) {
                        sb_temp[idx - 1] = key[0];
                        sb_temp[idx] = key[1];
                    } else if (len == 2) {
                        sb_temp[idx - 1] = key[0];
                        ends_temp[idx - 1] = i;
                        continue;
                    } else {
                        sb_temp[idx] = key[0];
                    }
                } else {
                    sb_temp[idx] = t1;
                }
                p = next;
            } else {
                sb_temp[idx] = t1;
            }
            starts_temp[idx] = i;
            ends_temp[idx] = i;
            idx++;
        }

        TextStream stream = new TextStream(chs);
        stream.testingText = Arrays.copyOf(sb_temp, idx);
        stream.start = Arrays.copyOf(starts_temp, idx);
        stream.end = Arrays.copyOf(ends_temp, idx);
        return stream;
    }
}

