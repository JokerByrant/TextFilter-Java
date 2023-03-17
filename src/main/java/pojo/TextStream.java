package pojo;

public class TextStream extends ReadStreamBase {
    public TextStream(String source) {
        super(source);
    }

    public TextStream(char[] source) {
        super(source);
    }
}
