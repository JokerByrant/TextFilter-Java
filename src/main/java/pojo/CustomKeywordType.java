package pojo;


import enums.IllegalWordsRiskLevel;

import java.time.LocalDateTime;

public final class CustomKeywordType {
    public String Code;
    public IllegalWordsRiskLevel RiskLevel_1;
    public IllegalWordsRiskLevel RiskLevel_2;
    public IllegalWordsRiskLevel RiskLevel_3;
    public boolean UseTime;
    public LocalDateTime StartTime;
    public LocalDateTime EndTime;

    public static CustomKeywordType[] build(KeywordTypeInfo[] infos) {
        int len = infos.length + 1;
        CustomKeywordType[] result = new CustomKeywordType[len];
        CustomKeywordType[] parent = new CustomKeywordType[len];
        int[] parentId = new int[len];

        for (int i = 0; i < infos.length; i++) {
            KeywordTypeInfo info = infos[i];
            if (info == null) { continue; }
            if (info.getId() == 0) { continue; }
            CustomKeywordType type = new CustomKeywordType();
            type.setCode(info.getCode());
            type.setUseTime(info.isUseTime());
            type.setStartTime(info.getStartTime());
            type.setEndTime(info.getEndTime());
            result[info.getId()] = type;
            parentId[info.getId()] = info.getParentId();
        }

        for (int i = 0; i < infos.length; i++) {
            KeywordTypeInfo info = infos[i];
            if (info == null) { continue; }
            if (info.getId() == 0) { continue; }
            parent[info.getId()] = result[info.getParentId()];
        }

        for (int i = 0; i < len; i++) {
            CustomKeywordType type = result[i];
            if (type != null) {
                type.RiskLevel_1 = getRiskLevel_1(parent, parentId, type, i);
                type.RiskLevel_2 = getRiskLevel_2(parent, parentId, type, i);
                type.RiskLevel_3 = getRiskLevel_3(parent, parentId, type, i);
            }
        }
        return result;
    }

    private static IllegalWordsRiskLevel getRiskLevel_1(CustomKeywordType[] parent, int[] parentId, CustomKeywordType type, int postion) {
        CustomKeywordType t = type;
        int p = postion;
        while (t != null) {
            if (t.RiskLevel_1 != null) {
                return t.RiskLevel_1;
            }
            t = parent[p];
            p = parentId[p];
        }
        return IllegalWordsRiskLevel.Review;
    }

    private static IllegalWordsRiskLevel getRiskLevel_2(CustomKeywordType[] parent, int[] parentId, CustomKeywordType type, int postion) {
        CustomKeywordType t = type;
        int p = postion;
        while (t != null) {
            if (t.RiskLevel_2 != null) {
                return t.RiskLevel_2;
            }
            t = parent[p];
            p = parentId[p];
        }
        return IllegalWordsRiskLevel.Reject;
    }

    private static IllegalWordsRiskLevel getRiskLevel_3(CustomKeywordType[] parent, int[] parentId, CustomKeywordType type, int postion) {
        CustomKeywordType t = type;
        int p = postion;
        while (t != null) {
            if (t.RiskLevel_3 != null) {
                return t.RiskLevel_3;
            }
            t = parent[p];
            p = parentId[p];
        }
        return IllegalWordsRiskLevel.Reject;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public boolean isUseTime() {
        return UseTime;
    }

    public void setUseTime(boolean useTime) {
        UseTime = useTime;
    }

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        StartTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        EndTime = endTime;
    }
}
