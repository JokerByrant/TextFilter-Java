package pojo;

public class TextFilterDetailItem {
    private String riskLevel;

    private String riskCode;

    private String position;

    private String text;

    private Range range;

    public void build(StringBuilder sb) {
        sb.append("{\"riskLevel\":\"");
        sb.append(riskLevel != null ? riskLevel : "");
        if (riskCode != null && !riskCode.isEmpty()) {
            sb.append("\",\"riskCode\":\"");
            sb.append(riskCode);
        }
        if (text != null && !text.isEmpty()) {
            sb.append("\",\"text\":\"");
            JsonCommon.addString(sb, text);
        }
        if (position != null && !position.isEmpty()) {
            sb.append("\",\"position\":\"");
            sb.append(position);
        }
        sb.append("\"}");
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskCode() {
        return riskCode;
    }

    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
}
