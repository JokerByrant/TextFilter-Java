package pojo;

public class WordsInfo {
    // 开始位置
    public int Start;
    // 结束位置
    public int End;
    // 值
    public int Count;
    // 单组敏感词
    public TempWordsResultItem Context_012;
    // 多组敏感词部分
    public TempWordsResultItem Context_34;

    public WordsInfo(int start, int end, int count, TempWordsResultItem item_012, TempWordsResultItem item_34) {
        Start = start;
        End = end;
        Count = count;
        Context_012 = item_012;
        Context_34 = item_34;
    }

    @Override
    public String toString() {
        return Start + "-" + End;
    }
}
