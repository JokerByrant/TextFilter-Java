package pojo;

import java.util.List;

public class TextFilterResult {
    private int code;
    private String message;
    private String requestId;
    private String riskLevel;
    private String riskCode;
    private Double sentimentScore;
    private List<TextFilterDetailItem> details;
    private List<TextFilterContactItem> contacts;
    private List<Range> ranges;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public List<TextFilterDetailItem> getDetails() {
        return details;
    }

    public void setDetails(List<TextFilterDetailItem> details) {
        this.details = details;
    }

    public List<TextFilterContactItem> getContacts() {
        return contacts;
    }

    public void setContacts(List<TextFilterContactItem> contacts) {
        this.contacts = contacts;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"code\":");
        sb.append(code);

        if (message != null && !message.trim().isEmpty()) {
            sb.append(",\"message\":\"");
            JsonCommon.addString(sb, message);
            sb.append("\"");
        }
        if (requestId != null && !requestId.trim().isEmpty()) {
            sb.append(",\"requestId\":\"");
            JsonCommon.addString(sb, requestId);
            sb.append("\"");
        }
        if (riskLevel != null && !riskLevel.trim().isEmpty()) {
            sb.append(",\"riskLevel\":\"");
            JsonCommon.addString(sb, riskLevel);
            sb.append("\"");
        }
        if (riskCode != null && !riskCode.trim().isEmpty()) {
            sb.append(",\"riskCode\":\"");
            JsonCommon.addString(sb, riskCode);
            sb.append("\"");
        }
        if (sentimentScore != null) {
            sb.append(",\"sentimentScore\":");
            sb.append(sentimentScore);
        }
        if (details != null && !details.isEmpty()) {
            sb.append(",\"details\":[");
            for (int i = 0; i < details.size(); i++) {
                if (i > 0) { sb.append(","); }
                details.get(i).build(sb);
            }
            sb.append("]");
        }
        if (contacts != null && !contacts.isEmpty()) {
            sb.append(",\"contacts\":[");
            for (int i = 0; i < contacts.size(); i++) {
                if (i > 0) { sb.append(","); }
                contacts.get(i).build(sb);
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
