package service.impl;

import pojo.IntDictionary2;
import pojo.TempMultiWords;
import pojo.TempMultiWordsResult;
import pojo.TempWordsResultItem;
import service.IMultiWordsSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author sxh
 * @date 2023/3/3
 */
public class MultiWordsSearch4 implements IMultiWordsSearch {
    private int[] _dict;
    private IntDictionary2[] _nextIndex;
    private byte[] _intervals;
    private byte[] _maxNextIntervals;
    private int[] _resultIndexs;

    @Override
    public List<TempMultiWordsResult> findAll(List<TempWordsResultItem> txt) {
        TempMultiWords root = new TempMultiWords();
        List<TempMultiWords> tempResult = new ArrayList<>();
        TempMultiWords newsRoot = new TempMultiWords();

        boolean find = false;
        Integer idx = 0;
        for (int i = 0; i < txt.size(); i++) {
            TempWordsResultItem item = txt.get(i);
            int t = _dict[item.getSingleIndex()];
            if (t == 0) {
                continue;
            }

            TempMultiWords news = newsRoot;

            TempMultiWords temp = root;
            while (temp != null) {
                if (temp.getItem() != null && temp.getItem().getEnd() >= item.getStart()) {
                    temp = temp.getAfter();
                    continue;
                }

                idx = _nextIndex[temp.getPtr()].tryGetValue(t);
                if (idx != null) {
                    int interval = _intervals[idx];
                    if (interval == 0 || item.getNplIndex() - temp.getNplIndex() <= interval) {
                        TempMultiWords tmp = append(temp, idx, idx, item);
                        if (tmp.getResultIndex() != 0) {
                            tempResult.add(tmp);
                            if (_nextIndex[tmp.getPtr()].HasNoneKey()) {
                                temp = temp.getAfter();
                                continue;
                            }
                        }
                        news.setAfter(tmp);
                        news = tmp;
                        find = true;
                    }
                }
                temp = temp.getAfter();
            }

            if (find) {
                TempMultiWords parent = root;
                temp = root.getAfter();
                while (temp != null) {
                    if (item.getNplIndex() >= temp.getMaxNextIndex()) {
                        parent.setAfter(temp.getAfter());
                    } else {
                        parent = temp;
                    }
                    temp = parent.getAfter();
                }

                parent.setAfter(newsRoot.getAfter());
                newsRoot.setAfter(null);
                find = false;
            }
        }

        newsRoot = null;
        root.clearAll();

        List<TempMultiWordsResult> results = new ArrayList<>(tempResult.size());
        int lastKeywordsId = -1;
        TempMultiWordsResult lastMultiWordsResult = null;

        for (int i = 0; i < tempResult.size(); i++) {
            TempMultiWords item = tempResult.get(i);
            Stack<TempWordsResultItem> stack = new Stack<>();
            TempMultiWords temp = item;
            while (temp.getPtr() != 0) {
                if (temp.getItem() != null) {
                    stack.push(temp.getItem());
                }
                temp = temp.getParent();
            }

            int len = stack.size();

            TempWordsResultItem[] items = new TempWordsResultItem[len];
            for (int j = 0; j < len; j++) {
                items[j] = stack.pop();
            }

            if (item.getResultIndex() == lastKeywordsId) {
                if (lastMultiWordsResult.containsRange(items)) {
                    continue;
                }
            }

            TempMultiWordsResult illegalWords = new TempMultiWordsResult(item.getResultIndex(), items);
            results.add(illegalWords);
            lastKeywordsId = item.getResultIndex();
            lastMultiWordsResult = illegalWords;
        }
        tempResult = null;

        return results;
    }

    private TempMultiWords append(final TempMultiWords parent, final int ptr, final int idx, final TempWordsResultItem item) {
        final TempMultiWords temp = new TempMultiWords();
        temp.Ptr = ptr;
        temp.NplIndex = item.nplIndex;
        temp.Parent = parent;
        temp.Item = item;
        temp.ResultIndex = _resultIndexs[idx];
        temp.MaxNextIndex = item.nplIndex + _maxNextIntervals[idx];
        return temp;
    }

    @Override
    public void load(CSharpDataInputStream dis) throws IOException {
        _dict = dis.readIntArray();
        int length = dis.reverseReadInt();
        _nextIndex = new IntDictionary2[length];
        for (int i = 0; i < length; i++) {
            _nextIndex[i] = IntDictionary2.Load(dis);
        }
        _resultIndexs = dis.readIntArray();
        length = dis.reverseReadInt();
        _intervals = dis.readBytes(length);

        length = dis.reverseReadInt();
        _maxNextIntervals = dis.readBytes(length);
    }
}
