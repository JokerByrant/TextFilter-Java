package pojo;

public class ReadStreamBase {
    public char[] source;
    public int[] start;
    public int[] end;
    public char[] testingText;

    public ReadStreamBase(String source) {
        this.source = source.toCharArray();
    }

    public ReadStreamBase(char[] source) {
        this.source = source;
    }
}
