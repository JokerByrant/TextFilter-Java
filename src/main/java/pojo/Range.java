package pojo;

/**
 * @author sxh
 * @date 2023/3/15
 */
public class Range {
    private final int start;
    private final int end;
    private String text;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Range(String position) {
        String[] split = position.split("-");
        this.start = Integer.parseInt(split[0]);
        if (split.length > 1) {
            this.end = Integer.parseInt(split[1]);
        } else {
            this.end = this.start;
        }
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
