package enums;

/**
 * @author sxh
 * @date 2023/2/13
 */
public enum IllegalWordsRiskLevel {
    Reject(0),
    Review(1),
    Pass(2);

    private int value;

    private IllegalWordsRiskLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
