package pojo;

public class TempMultiWords {
    public int Ptr;
    public int NplIndex;
    public int MaxNextIndex;
    public int ResultIndex;
    public TempMultiWords Parent;
    public TempWordsResultItem Item;
    public TempMultiWords After;

    public void clearAll() {
        TempMultiWords temp = After;
        while (temp != null) {
            TempMultiWords after = temp.After;
            temp.After = null;
            temp = after;
        }
    }

    public int getPtr() {
        return Ptr;
    }

    public void setPtr(int ptr) {
        Ptr = ptr;
    }

    public int getNplIndex() {
        return NplIndex;
    }

    public void setNplIndex(int nplIndex) {
        NplIndex = nplIndex;
    }

    public int getMaxNextIndex() {
        return MaxNextIndex;
    }

    public void setMaxNextIndex(int maxNextIndex) {
        MaxNextIndex = maxNextIndex;
    }

    public int getResultIndex() {
        return ResultIndex;
    }

    public void setResultIndex(int resultIndex) {
        ResultIndex = resultIndex;
    }

    public TempMultiWords getParent() {
        return Parent;
    }

    public void setParent(TempMultiWords parent) {
        Parent = parent;
    }

    public TempWordsResultItem getItem() {
        return Item;
    }

    public void setItem(TempWordsResultItem item) {
        Item = item;
    }

    public TempMultiWords getAfter() {
        return After;
    }

    public void setAfter(TempMultiWords after) {
        After = after;
    }
}
