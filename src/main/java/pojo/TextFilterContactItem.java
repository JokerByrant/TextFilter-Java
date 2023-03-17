package pojo;

public class TextFilterContactItem {
    /**
     * 联系方式类型 0)手机号 1)qq号 2)微信号 3) 微博号 4)微信号公众号
     */
    private String contactType;

    /**
     * 联系方式串
     */
    private String contactString;

    /**
     * 联系方式串位置
     */
    private String position;

    private Range range;

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactString() {
        return contactString;
    }

    public void setContactString(String contactString) {
        this.contactString = contactString;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void build(StringBuilder sb) {
        sb.append("{\"contactType\":\"");
        JsonCommon.addString(sb, contactType);
        sb.append("\",\"contactString\":\"");
        JsonCommon.addString(sb, contactString);
        sb.append("\",\"position\":\"");
        JsonCommon.addString(sb, position);
        sb.append("\"}");
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
}
