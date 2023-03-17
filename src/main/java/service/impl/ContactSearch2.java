package service.impl;


import pojo.*;
import service.IContactSearch;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sxh
 * @date 2023/3/3
 */
public class ContactSearch2 implements IContactSearch {
    private int[] _dict;
    private IntDictionary2[] _nextIndex;
    private byte[] _intervals;
    private byte[] _maxNextIntervals;
    private int[] _resultIndexs;
    private int[] _typeIndexs;
    private int[] _textIndexs;

    @Override
    public int[] GetContactDict() {
        return _dict;
    }

    @Override
    public List<ContactResult> findAll(List<TempWordsResultItem> txt) {
        TempMultiWords root = new TempMultiWords();
        List<TempMultiWords> tempResult = new ArrayList<TempMultiWords>();
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

        root.clearAll();
        if (tempResult.size() == 0) {
            return new ArrayList<>();
        }

        List<ContactResult> tempResults = new ArrayList<>(tempResult.size());
        int max = 0;
        for (TempMultiWords item : tempResult) {
            List<TempWordsResultItem> items = new ArrayList<TempWordsResultItem>();
            TempMultiWords temp = item;
            while (temp.getPtr() != 0) {
                if (temp.getItem() != null) {
                    items.add(temp.getItem());
                }
                temp = temp.getParent();
            }
            int type = _typeIndexs[item.getResultIndex()];
            int textIndex = items.size() - 1 - _textIndexs[item.getResultIndex()];
            TempWordsResultItem contact = items.get(textIndex);

            tempResults.add(new ContactResult(type, contact.getStart(), contact.getEnd()));
            if (contact.getEnd() > max) {
                max = contact.getEnd();
            }
        }

        if (tempResults.size() <= 1) {
            return tempResults;
        }

        TextSplit_Contact textSplit = new TextSplit_Contact(max + 1);
        for (ContactResult item : tempResults) {
            textSplit.AddWords(item);
        }
        textSplit.Calculation();
        List<ContactResult> results = textSplit.GetIllegalWords();
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
        length = dis.reverseReadInt();
        _intervals = dis.readBytes(length);

        length = dis.reverseReadInt();
        _maxNextIntervals = dis.readBytes(length);

        _resultIndexs = dis.readIntArray();
        _typeIndexs = dis.readIntArray();
        _textIndexs = dis.readIntArray();
    }
}
