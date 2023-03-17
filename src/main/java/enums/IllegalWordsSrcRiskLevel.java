package enums;

/**
 * @author sxh
 * @date 2023/2/13
 */
public enum IllegalWordsSrcRiskLevel {
    Part(255),
    ContactPart(254),
    Violation(3),
    Dangerous(2),
    Sensitive(1),
    Normal(0);

    private int value;

    private IllegalWordsSrcRiskLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static IllegalWordsSrcRiskLevel getByValue(int value) {
        for (IllegalWordsSrcRiskLevel illegalWordsSrcRiskLevel : values()) {
            if (value == illegalWordsSrcRiskLevel.getValue()) {
                return illegalWordsSrcRiskLevel;
            }
        }
        return null;
    }
}

