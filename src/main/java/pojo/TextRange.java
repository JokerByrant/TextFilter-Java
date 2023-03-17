package pojo;

import java.util.ArrayList;
import java.util.List;

public class TextRange {
    private List<Integer> starts = new ArrayList<Integer>();
    private List<Integer> ends = new ArrayList<Integer>();
    private int start;
    private int end;
    private boolean first = true;

    public TextRange(int start, int end) {
        this.start = start;
        this.end = end;
        first = false;
    }

    public TextRange() {
    }

    public void add(int start, int end) {
        if (first) {
            this.start = start;
            this.end = end;
            first = false;
        } else if (start > end + 1) {
            starts.add(this.start);
            ends.add(this.end);
            this.start = start;
            this.end = end;
        } else {
            this.end = end;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < starts.size(); i++) {
            int s = starts.get(i);
            int e = ends.get(i);
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(s);
            if (s != e) {
                sb.append("-");
                sb.append(e);
            }
        }
        if (sb.length() > 0) {
            sb.append(",");
        }
        sb.append(start);
        if (start != end) {
            sb.append("-");
            sb.append(end);
        }
        return sb.toString();
    }
}
