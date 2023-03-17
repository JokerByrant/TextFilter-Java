package pojo;

public class ContactResult {
    public boolean isSet;
    public int contactType;
    public int start;
    public int end;

    public ContactResult(int contactType, int start, int end) {
        this.contactType = contactType;
        this.start = start;
        this.end = end;
        this.isSet = true;
    }

    public int getHashSet() {
        return (start << 10) | (end & 0x3ff);
    }

    public String getText(String txt) {
        return txt.substring(start, end + 1);
    }

    public String getText2(String txt) {
        int newStart = Math.max(0, start - 5);
        int newEnd = Math.min(txt.length() - 1, end + 5);
        return txt.substring(newStart, newEnd + 1);
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
