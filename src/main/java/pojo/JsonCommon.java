package pojo;

public class JsonCommon {
    public static void addString(StringBuilder sb, String text) {
        if (text == null) {
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\\') {
                sb.append("\\\\");
            } else if (ch == '\"') {
                sb.append("\\\"");
            } else if (ch == '\r') {
                sb.append("\\r");
            } else if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\t') {
                sb.append("\\t");
            } else if (ch == '\b') {
                sb.append("\\b");
            } else if (ch == '\f') {
                sb.append("\\f");
            } else if (ch == '\u000B') {
                sb.append("\\v");
            } else {
                sb.append(ch);
            }
        }
    }
}
