package enums;

/**
 * @author sxh
 * @date 2023/2/13
 */
public enum IllegalWordsMatchType {
    /**
     * 普通匹配
     */
    PartMatch((byte) 0),
    /**
     * 匹配句子开始
     */
    MatchTextStart((byte) 1),
    /**
     * 匹配整句话
     */
    MatchText((byte) 2),
    /**
     * 匹配句子结尾
     */
    MatchTextEnd((byte) 3),
    /**
     * 匹配句子开始 或 结尾
     */
    MatchTextStartOrEnd((byte) 4);

    private byte value;

    IllegalWordsMatchType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static IllegalWordsMatchType getByValue(int value) {
        for (IllegalWordsMatchType obj : values()) {
            if (value == obj.getValue()) {
                return obj;
            }
        }
        return null;
    }
}

