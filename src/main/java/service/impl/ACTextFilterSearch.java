package service.impl;


import pojo.IntDictionary;
import pojo.KeywordInfo;
import pojo.TempWordsResultItem;
import service.IACRegexSearch;
import service.ISkipwordsSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

/**
 * @author sxh
 * @date 2023/3/3
 */
public class ACTextFilterSearch implements IACRegexSearch {
    public byte[] _skipIndexs;
    public int[][] _dicts;
    public ISkipwordsSearch[] _skipwordsSearchs;
    public boolean[] _useSkipOnce;
    public int[][] _dictIndex;

    private int _firstMaxChar;
    private int _maxLength;
    private int[] _first_first;
    private IntDictionary[] _nextIndex_first;
    private int[] _failure;
    private boolean[] _check;

    private int _azNumMinChar;
    private int _azNumMaxChar;
    private int[] _first;
    private IntDictionary[] _nextIndex;
    private int[] _end;

    private Function<Integer, KeywordInfo> _getKeyword;

    public KeywordInfo getMatchKeyword(int resultIndex) {
        return _getKeyword.apply(resultIndex);
    }

    @Override
    public void setGetMatchKeyword(Function<Integer, KeywordInfo> func) {
        _getKeyword = func;
    }

    @Override
    public void SetDict(byte[] skipIndexs, int[][] dicts, ISkipwordsSearch[] skipwordsSearchs, boolean[] useSkipOnce) {
        _skipIndexs = skipIndexs;
        _dicts = dicts;
        _skipwordsSearchs = skipwordsSearchs;
        _useSkipOnce = useSkipOnce;

        int max = (int) Math.pow(2, dicts.length - 1);
        int[][] list = new int[max][];

        for (int type = 0; type < max; type++) {
            List<Integer> ls = new ArrayList<>();
            for (int j = 0; j < dicts.length - 1; j++) {
                int indexFlag = 1 << j;
                if ((type & indexFlag) == indexFlag) {
                    ls.add(j + 1);
                }
            }
            list[type] = ls.stream().mapToInt(i -> i).toArray();
        }
        _dictIndex = list;
    }

    @Override
    public void findAll(char[] _ptext, int length, List<TempWordsResultItem> result) {
        HashSet<Integer> set = new HashSet<>();
        int[] _pfailure = _failure;
        boolean[] _pcheck = _check;
        int[] _pend = _end;

        FindAll_0(_ptext, length, set, result, _pfailure, _pcheck, _pend);
        for (int index = 1; index < _dicts.length; index++) {
            ISkipwordsSearch skipwordsSearch = _skipwordsSearchs[index];
            if (skipwordsSearch == null) {
                if (_useSkipOnce[index] == false) {
                    FindAll_1(_ptext, length, set, result, index, _pfailure, _pcheck, _pend);
                } else {
                    FindAll_2(_ptext, length, set, result, index, _pfailure, _pcheck, _pend);
                }
            } else {
                boolean[] skip2 = new boolean[length];
                boolean[] skip = skip2;
                if (skipwordsSearch.findAll(_ptext, length, skip)) {
                    if (_useSkipOnce[index] == false) {
                        FindAll_3(_ptext, length, set, result, index, skip, _pfailure, _pcheck, _pend);
                    } else {
                        FindAll_4(_ptext, length, set, result, index, skip, _pfailure, _pcheck, _pend);
                    }
                }
            }
        }
        set = null;
    }

    public void FindAll_0(char[] _ptext, int length, HashSet<Integer> set, List<TempWordsResultItem> result,
                          int[] _pfailure, boolean[] _pcheck, int[] _pend) {
        int[] _pdict = _dicts[0];
        byte[] _pskipIndexs = _skipIndexs;
        int p = 0;
        for (int i = 0; i < length; i++) {
            char t1 = _ptext[i];
            int t = _pdict[t1];
            if (t == 0xffff) {
                continue;
            }
            if (t <= _firstMaxChar) {
                p = _first_first[t];
                continue;
            }

            Integer next = _nextIndex_first[p].tryGetValue(t);
            if (next != null) {
                if (_pcheck[next] && checkNextChar(_ptext, length - 1, t, i, _pdict)) {
                    Find_0(_ptext, t, i, set, result, _pdict, _pend);
                }
            } else if (p != 0) {
                do {
                    p = _pfailure[p];
                    next = _nextIndex_first[p].tryGetValue(t);
                    if (next != null) {
                        if (_pcheck[next] && checkNextChar(_ptext, length - 1, t, i, _pdict)) {
                            Find_0(_ptext, t, i, set, result, _pdict, _pend);
                        }
                        break;
                    }
                } while (p != 0);
            }
            p = (next == null) ? 0 : next;
        }
    }

    public void FindAll_1(char[] _ptext, int length, HashSet<Integer> set, List<TempWordsResultItem> result, int index,
                          int[] _pfailure, boolean[] _pcheck, int[] _pend) {
        int[] _pdict = _dicts[index];
        int p = 0;
        int len = length - 1;
        for (int i = 0; i < length; i++) {
            char t1 = _ptext[i];
            int t = _pdict[t1];
            if (t >= 0xfffe) {
                continue;
            }
            if (t <= _firstMaxChar) {
                p = _first_first[t];
                continue;
            }

            Integer next = _nextIndex_first[p].tryGetValue(t);
            if (next != null) {
                if (_pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                    Find_1(_ptext, t, i, set, result, _pdict, _pend);
                }
            } else if (p != 0) {
                do {
                    p = _pfailure[p];
                    next = _nextIndex_first[p].tryGetValue(t);
                    if (next != null) {
                        if (p != 0 && _pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                            Find_1(_ptext, t, i, set, result, _pdict, _pend);
                        }
                        break;
                    }
                } while (p != 0);
            }
            p = (next == null) ? 0 : next;
        }
    }

    public void FindAll_2(char[] _ptext, int length, HashSet<Integer> set, List<TempWordsResultItem> result, int index
            , int[] _pfailure, boolean[] _pcheck, int[] _pend) {
        int[] _pdict = _dicts[index];
        int p = 0;
        int len = length - 1;
        for (int i = 0; i < length; i++) {
            char t1 = _ptext[i];
            int t = _pdict[t1];
            if (t >= 0xfffe) {
                continue;
            }
            if (t <= _firstMaxChar) {
                p = _first_first[t];
                continue;
            }

            Integer next = _nextIndex_first[p].tryGetValue(t);
            if (next != null) {
                if (_pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                    Find_2(_ptext, t, i, set, result, _pdict, _pend);
                }
            } else if (p != 0) {
                do {
                    p = _pfailure[p];
                    next = _nextIndex_first[p].tryGetValue(t);
                    if (next != null) {
                        if (p != 0 && _pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                            Find_2(_ptext, t, i, set, result, _pdict, _pend);
                        }
                        break;
                    }
                } while (p != 0);
            }
            p = (next == null) ? 0 : next;
        }
    }

    public void FindAll_3(char[] _ptext, int length, HashSet<Integer> set, List<TempWordsResultItem> result, int index, boolean[] skips
            , int[] _pfailure, boolean[] _pcheck, int[] _pend) {
        int[] _pdict = _dicts[index];
        int p = 0;
        int len = length - 1;
        for (int i = 0; i < length; i++) {
            if (skips[i]) {
                continue;
            }
            char t1 = _ptext[i];
            int t = _pdict[t1];
            if (t >= 0xfffe) {
                continue;
            }
            if (t <= _firstMaxChar) {
                p = _first_first[t];
                continue;
            }

            Integer next = _nextIndex_first[p].tryGetValue(t);
            if (next != null) {
                if (_pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                    Find_3(_ptext, t, i, skips, set, result, _pdict, _pend);
                }
            } else if (p != 0) {
                do {
                    p = _pfailure[p];
                    next = _nextIndex_first[p].tryGetValue(t);
                    if (next != null) {
                        if (p != 0 && _pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                            Find_3(_ptext, t, i, skips, set, result, _pdict, _pend);
                        }
                        break;
                    }
                } while (p != 0);
            }
            p = (next == null) ? 0 : next;
        }
    }

    public void FindAll_4(char[] _ptext, int length, HashSet<Integer> set, List<TempWordsResultItem> result, int index, boolean[] skips, int[] _pfailure, boolean[] _pcheck, int[] _pend) {
        int[] _pdict = _dicts[index];
        int p = 0;
        int len = length - 1;
        for (int i = 0; i < length; i++) {
            if (skips[i]) { continue; }
            char t1 = _ptext[i];
            int t = _pdict[t1];
            if (t == 0xffff) { continue; }
            if (t <= _firstMaxChar) { p = _first_first[t]; continue; }

            Integer next = _nextIndex_first[p].tryGetValue(t);
            if (next != null) {
                if (_pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                    Find_4(_ptext, t, i, skips, set, result, _pdict, _pend);
                }
            } else if (p != 0) {
                do {
                    p = _pfailure[p];
                    next = _nextIndex_first[p].tryGetValue(t);
                    if (next != null) {
                        if (p != 0 && _pcheck[next] && checkNextChar(_ptext, len, t, i, _pdict)) {
                            Find_4(_ptext, t, i, skips, set, result, _pdict, _pend);
                        }
                        break;
                    }
                } while (p != 0);
            }
            p = (next == null) ? 0 : next;
        }
    }

    private void Find_0(char[] _ptext, int tc, final int end, HashSet<Integer> set, List<TempWordsResultItem> result, int[] _pdict, int[] _pend) {
        Integer next = _first[tc];
        int index = _pend[next];
        if (index != 0 && checkPreChar(_ptext, tc, end, _pdict)) {
            addToResult(end, end, index, set, result);
        }

        for (int j = end - 1; j >= 0; j--) {
            int t1 = _ptext[j];
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }
            next = _nextIndex_first[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return; }

            index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
    }

    private void Find_1(char[] _ptext, int tc, final int end, HashSet<Integer> set, List<TempWordsResultItem> result, int[] _pdict, int[] _pend) {
        Integer next = _first[tc];

        for (int j = end - 1; j >= 0; j--) {
            int t1 = _ptext[j];
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }
            next = _nextIndex_first[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return; }

            int index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
    }

    private void Find_2(char[] _ptext, int tc, int end, HashSet<Integer> set, List<TempWordsResultItem> result
            , int[] _pdict, int[] _pend)
    {
        Integer next = _first[tc];

        int len = -1;
        for (int j = end - 1; j >= 0; j--) {
            char t1 = _ptext[j];
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }
            if (tc == 0xfffe) {
                if (len == -1) { len = 1; continue; }
                if (len == 0) { return; }
                len--;
                continue;
            }
            next = _nextIndex_first[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return; }
            if (len != -1) { len = -1; }

            int index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
    }

    private void Find_3(char[] _ptext, int tc, int end, boolean[] skips, HashSet<Integer> set, List<TempWordsResultItem> result
            , int[] _pdict, int[] _pend)
    {
        Integer next = _first[tc];

        for (int j = end - 1; j >= 0; j--) {
            char t1 = _ptext[j];
            if (skips[j]) { continue; }
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }
            next = _nextIndex_first[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return; }

            int index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
    }

    private void Find_4(char[] _ptext, int tc, int end, boolean[] skips, HashSet<Integer> set, List<TempWordsResultItem> result
            , int[] _pdict, int[] _pend)
    {
        Integer next = _first[tc];

        int len = -1;
        for (int j = end - 1; j >= 0; j--) {
            char t1 = _ptext[j];
            if (skips[j]) { continue; }
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }
            if (tc == 0xfffe) {
                if (len == -1) { len = 1; continue; }
                if (len == 0) { return; }
                len--;
                continue;
            }
            next = _nextIndex_first[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return; }
            if (len != -1) { len = -1; }

            int index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
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
        if (tt < _azNumMinChar || tt > _azNumMaxChar) {
            return true;
        }
        return false;
    }

    private void addToResult(int startKey, int endKey, int index, HashSet<Integer> set, List<TempWordsResultItem> result) {
        int u = (startKey << 10) | (endKey & 0x3ff);
        if (set.add(u)) {
            TempWordsResultItem r = new TempWordsResultItem(startKey, endKey, getMatchKeyword(index));
            result.add(r);
        }
    }

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _firstMaxChar = dis.reverseReadUnsignedShort();
        _maxLength = dis.reverseReadInt();
        _first_first = dis.readIntArray();
        int length = dis.reverseReadInt();
        _nextIndex_first = new IntDictionary[length];
        for (int i = 0; i < length; i++) {
            _nextIndex_first[i] = IntDictionary.Load(dis);
        }
        _failure = dis.readIntArray();
        _check = dis.readBooleanArray();

        _azNumMinChar = dis.reverseReadUnsignedShort();
        _azNumMaxChar = dis.reverseReadUnsignedShort();
        _first = dis.readIntArray();

        length = dis.reverseReadInt();
        _nextIndex = new IntDictionary[length];
        for (int i = 0; i < length; i++) {
            _nextIndex[i] = IntDictionary.Load(dis);
        }
        _end = dis.readIntArray();
    }
}
