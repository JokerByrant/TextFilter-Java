package pojo;

public class SingleWordsResult {
    public String code;
    public int typeId;
    public int start;
    public int end;
    public int index;

    public SingleWordsResult(int start, int end, int index) {
        this.start = start;
        this.end = end;
        this.code = "Custom";
        this.typeId = 0;
        this.index = index;
    }

    public SingleWordsResult(int typeId, int start, int end, String code, int index) {
        this.typeId = typeId;
        this.start = start;
        this.end = end;
        this.code = code;
        this.index = index;
    }

    public long getHashSet() {
        return ((long)start << 10) | ((long)end & 0x3ff);
    }

    public String getText(String txt) {
        return txt.substring(start, end + 1);
    }

    public String getText2(String txt) {
        int s = Math.max(0, start - 5);
        int e = Math.min(txt.length() - 1, end + 5);
        return txt.substring(s, e + 1);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
