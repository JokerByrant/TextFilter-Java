package service.impl;


import pojo.FindItem;
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
 * 查找匹配违规敏感词
 * 解决敏感词变种问题，如重复词、繁体简体、异体字、同音字、模糊音字、斜音字、同形字等问题，并结合ISkipwordsSearch实现跳词、中文中间带英文检索
 *
 * @author sxh
 * @date 2023/3/3
 */
public class ACRegexSearch7 implements IACRegexSearch {
    // 最小英文数字映射值
    private int _azNumMinChar;
    // 最大英文数字映射值
    private int _azNumMaxChar;
    // 字符所在最小层级(位置索引)，从0开始
    private int[] _minLayer;
    // 字符所在最大层级(位置索引)，从0开始
    private int[] _maxLayer;
    // 结束字符的最小值
    private int _minEndKey;

    // 跳词索引
    public byte[] _skipIndexs;
    // 映射字典集
    public int[][] _dicts;
    // 跳词检验
    public ISkipwordsSearch[] _skipwordsSearchs;
    // 跳词计数，只计一次
    public boolean[] _useSkipOnce;
    // 跳词字典索引
    public int[][] _dictIndex;

    // 第一次位置
    private int[] _first;
    // 用于判断下一个位置
    private IntDictionary[] _nextIndex;
    // 敏感词 索引
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
    public void findAll(char[] ptext, int length, List<TempWordsResultItem> result) {
        List<FindItem> findItems = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        HashSet<Integer> skipSet = new HashSet<>();

        int[] pend = _end;
        int[] pfirst = _first;
        int[] dicts = _dicts[0];
        int[] minLayer = _minLayer;
        int[] maxLayer = _maxLayer;

        int min = 0;
        for (int i = 0; i < length; i++) {
            // 获取字符
            char t1 = ptext[i];
            // 获取字符对应的映射
            int t = dicts[t1];
            // 字符对应的映射不存在
            if (t == 0) {
                min = 0;
                continue;
            }
            // 字符是跳词
            if (t == 0xffff) {
                continue;
            }
            // 获取最小层级(位置索引)
            int n = minLayer[t];
            if (n == 1) {
                min = 1;
                // 检测下一个字符，防止误判英文数字
                if (t >= _minEndKey && checkNextChar(ptext, length - 1, t, i, dicts)) {
                    // 进入第二次查寻
                    Find(ptext, t, i, set, skipSet, result, dicts, pend, pfirst, findItems);
                }
            } else if (min != 0) {
                if (maxLayer[t] <= min) {
                    min = 0;
                    continue;
                }
                min = n;
                if (t >= _minEndKey && checkNextChar(ptext, length - 1, t, i, dicts)) {
                    Find(ptext, t, i, set, skipSet, result, dicts, pend, pfirst, findItems);
                }
            }
        }

        // 暂存表没有数据，直接返回
        if (findItems.size() == 0) {
            findItems = null;
            set = null;
            return;
        }

        // 获取多组词跳词搜索
        HashSet<Integer> indexSet = new HashSet<>();
        for (FindItem item : findItems) {
            indexSet.add(item.SkipIndex);
        }

        for (int index : indexSet) {
            boolean[] skip2 = new boolean[length];
            boolean[] skip = skip2;

            // 获取跳词位置标识
            if (_skipwordsSearchs[index].findAll(ptext, length, skip)) {
                if (!_useSkipOnce[index]) {// 不使用跳词计数
                    for (FindItem item : findItems) {
                        if (item.SkipIndex == index) {
                            // 匹配文本
                            Find_3(ptext, item.End, index, skip, skipSet, result, pend, pfirst);
                        }
                    }
                } else {// 使用跳词计数
                    for (FindItem item : findItems) {
                        if (item.SkipIndex == index) {
                            // 匹配文本
                            Find_4(ptext, item.End, index, skip, skipSet, result, pend, pfirst);
                        }
                    }
                }
            }
        }

        indexSet = null;
        findItems = null;
        set = null;
        skipSet = null;
    }

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _azNumMinChar = dis.reverseReadUnsignedShort();
        _azNumMaxChar = dis.reverseReadUnsignedShort();
        _minLayer = dis.readShortArray();
        _maxLayer = dis.readShortArray();
        _minEndKey = dis.reverseReadUnsignedShort();

        _first = dis.readIntArray();

        int length = dis.reverseReadInt();
        _nextIndex = new IntDictionary[length];
        for (int i = 0; i < length; i++) {
            _nextIndex[i] = IntDictionary.Load(dis);
        }
        _end = dis.readIntArray();
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

    private void Find(char[] _ptext, int tc, int end, HashSet<Integer> set, HashSet<Integer> skipSet, List<TempWordsResultItem> result,
                      int[] _pdict, int[] _pend, int[] _pfirst, List<FindItem> findItems) {
        // 进行第一遍匹配
        byte type = Find_0(_ptext, tc, _pfirst[tc], end, set, result, _pdict, _pend);
        for (int index : _dictIndex[type]) {
            ISkipwordsSearch skipwordsSearch = _skipwordsSearchs[index];
            if (skipwordsSearch == null) {
                // 判断是否使用跳词计数
                if (!_useSkipOnce[index]) {
                    Find_1(_ptext, end, index, skipSet, result, _pend, _pfirst);
                } else {
                    Find_2(_ptext, end, index, skipSet, result, _pend, _pfirst);
                }
            } else {
                findItems.add(new FindItem(end, index));
            }
        }
    }

    private byte Find_0(char[] _ptext, int tc, int first, int end, HashSet<Integer> set, List<TempWordsResultItem> result,
                        int[] _pdict, int[] _pend) {
        int index = _pend[first];
        // 检测上一个字符，防误判英文数字
        if (index != 0 && checkPreChar(_ptext, tc, end, _pdict)) {
            addToResult(end, end, index, set, result);
        }
        Integer next = first;

        byte resultType = 0;
        for (int j = end - 1; j >= 0; j--) {
            int t1 = _ptext[j];
            resultType |= _skipIndexs[t1];
            tc = _pdict[t1];
            if (tc == 0xffff) { continue; }

            // 出现敏感词内不存在的词 或  无法获取下一个字符，返回跳词搜索类型
            next = _nextIndex[next].tryGetValue(tc);
            if (tc == 0 || next == null) { return resultType; }

            index = _pend[next];
            if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                addToResult(j, end, index, set, result);
            }
        }
        return resultType;
    }

    private void Find_1(char[] ptext, int end, int index, HashSet<Integer> skipSet, List<TempWordsResultItem> result,
                        int[] pend, int[] pfirst) {
        int[] pdict = _dicts[index];
        int tc = pdict[ptext[end]];
        // 判断是否为跳词，如果是跳词，返回
        if (tc >= 0xfffe) {
            return;
        }
        Integer next = pfirst[tc];

        // 是否为跳词的标记
        boolean skip = false;
        for (int j = end - 1; j >= 0; j--) {
            int t1 = ptext[j];
            tc = pdict[t1];
            if (tc == 0xffff) {
                skip = true;
                continue;
            }
            next = _nextIndex[next].tryGetValue(tc);
            if (tc == 0 || next == null) {
                return;
            }
            // 有跳词，防重复
            if (skip) {
                index = pend[next];
                if (index != 0 && checkPreChar(ptext, tc, j, pdict)) {
                    addToResult(j, end, index, skipSet, result);
                }
            }
        }
    }

    private void Find_2(char[] ptext, int end, int index, HashSet<Integer> skipSet, List<TempWordsResultItem> result,
                        int[] pend, int[] pfirst) {
        int[] pdict = _dicts[index];
        int tc = pdict[ptext[end]];
        if (tc >= 0xfffe) {
            return;
        }
        Integer next = pfirst[tc];

        boolean skip = false;
        int len = -1;
        for (int j = end - 1; j >= 0; j--) {
            int t1 = ptext[j];
            tc = pdict[t1];
            if (tc == 0xffff) {
                skip = true;
                continue;
            }
            if (tc == 0xfffe) {
                if (len == -1) {
                    len = 1;
                    skip = true;
                    continue;
                }
                if (len == 0) {
                    return;
                }
                len--;
                skip = true;
                continue;
            }
            next = _nextIndex[next].tryGetValue(tc);
            if (tc == 0 || next == null) {
                return;
            }
            if (len != -1) {
                len = -1;
            }

            if (skip) {
                index = pend[next];
                if (index != 0 && checkPreChar(ptext, tc, j, pdict)) {
                    addToResult(j, end, index, skipSet, result);
                }
            }
        }
    }

    private void Find_3(char[] _ptext, int end, int index, boolean[] skips, HashSet<Integer> skipSet, List<TempWordsResultItem> result,
                        int[] _pend, int[] _pfirst) {
        int[] _pdict = _dicts[index];
        int tc = _pdict[_ptext[end]];
        if (tc >= 0xfffe) {
            return;
        }
        Integer next = _pfirst[tc];
        boolean skip = false;
        for (int j = end - 1; j >= 0; j--) {
            if (skips[j]) {
                skip = true;
                continue;
            }
            tc = _pdict[_ptext[j]];
            if (tc == 0xffff) {
                skip = true;
                continue;
            }
            // 出现敏感词内不存在的词或无法获取下一个字符，返回跳词搜索类型
            next = _nextIndex[next].tryGetValue(tc);
            if (tc == 0 || next == null) {
                return;
            }
            if (skip) {
                index = _pend[next];
                if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                    addToResult(j, end, index, skipSet, result);
                }
            }
        }
    }

    private void Find_4(char[] _ptext, int end, int index, boolean[] skips, HashSet<Integer> skipSet, List<TempWordsResultItem> result,
                        int[] _pend, int[] _pfirst) {
        int[] _pdict = _dicts[index];
        int tc = _pdict[_ptext[end]];
        if (tc >= 0xfffe) {
            return;
        }
        Integer next = _pfirst[tc];
        boolean skip = false;
        int len = -1;
        for (int j = end - 1; j >= 0; j--) {
            if (skips[j]) {
                skip = true;
                continue;
            }
            tc = _pdict[_ptext[j]];
            if (tc == 0xffff) {
                skip = true;
                continue;
            }
            if (tc == 0xfffe) {
                if (len == -1) {
                    len = 1;
                    skip = true;
                    continue;
                }
                if (len == 0) {
                    return;
                }
                len--;
                skip = true;
                continue;
            }
            next = _nextIndex[next].tryGetValue(tc);
            if (tc == 0 || next == null) {
                return;
            }
            if (len != -1) {
                len = -1;
            }
            if (skip) {
                index = _pend[next];
                if (index != 0 && checkPreChar(_ptext, tc, j, _pdict)) {
                    addToResult(j, end, index, skipSet, result);
                }
            }
        }
    }

    private void addToResult(int startKey, int endKey, int index, HashSet<Integer> set, List<TempWordsResultItem> result) {
        // 特征码
        int u = (startKey << 10) | (endKey & 0x3ff);
        if (set.add(u)) {
            TempWordsResultItem r = new TempWordsResultItem(startKey, endKey, getMatchKeyword(index));
            result.add(r);
        }
    }

}
