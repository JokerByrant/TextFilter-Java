package pojo;

public class MultiWordsResultItem {
    // 开始位置
    public int Start;
    // 结束位置
    public int End;

    public MultiWordsResultItem(int start, int end) {
        Start = start;
        End = end;
    }

    public String getText(String txt) {
        return txt.substring(Start, End + 1);
    }

    public String getText2(String txt) {
        int start = Start - 5;
        if (start < 0) {
            start = 0;
        }
        int end = End + 5;
        if (end > txt.length() - 1) {
            end = txt.length() - 1;
        }
        return txt.substring(start, end + 1);
    }

    public int getStart() {
        return Start;
    }

    public void setStart(int start) {
        Start = start;
    }

    public int getEnd() {
        return End;
    }

    public void setEnd(int end) {
        End = end;
    }
}
