package pojo;


import enums.IllegalWordsMatchType;
import enums.IllegalWordsRiskLevel;
import enums.IllegalWordsSrcRiskLevel;

/**
 * @author sxh
 * @date 2023/2/13
 */
public class TempWordsResultItem {
    public int end;
    public int start;
    public FenciKeywordInfo fenciKeyInfo;
    public KeywordInfo keyInfo;
    public int nplIndex;
    public IllegalWordsSrcRiskLevel srcRiskLevel;
    public IllegalWordsRiskLevel riskLevel;
    public IllegalWordsMatchType matchType;
    public boolean isFenci;
    public int singleIndex;
    public int diyIndex;
    public int typeId;
    public int count;
    public int emotionalColor;
    public static final double[] score = new double[]{
            1, 4, 3, 2, 0.5, -0.3, -0.5, -1, 0, 0,
            1.0, 1.05, 1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45,
            -2.5, -2.52, -2.54, -2.56, -2.58, -2.6, -2.62, -2.64, -2.66, -2.68,
    };

    public TempWordsResultItem(int start, int end, FenciKeywordInfo keyInfo) {
        this.end = end;
        this.start = start;
        this.count = keyInfo.Count;
        this.emotionalColor = keyInfo.EmotionalColor;
        this.isFenci = true;
    }

    public TempWordsResultItem(int start, int end, KeywordInfo keyInfo) {
        this.end = end;
        this.start = start;
        this.singleIndex = keyInfo.Id;
        this.srcRiskLevel = keyInfo.getRiskLevel();
        this.matchType = keyInfo.getMatchType();
        this.typeId = keyInfo.TypeId;
        this.count = 1;
    }

    public double getEmotionScore() {
        return score[emotionalColor];
    }

    public boolean containsRange(TempWordsResultItem item) {
        if (this.start <= item.start) {
            if (this.end >= item.end) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + start + "-" + end + "]";
    }

    public int getPosition() {
        return (int)((((long)start) << 10) | ((long)end & 0x3ff));
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public FenciKeywordInfo getFenciKeyInfo() {
        return fenciKeyInfo;
    }

    public void setFenciKeyInfo(FenciKeywordInfo fenciKeyInfo) {
        this.fenciKeyInfo = fenciKeyInfo;
    }

    public KeywordInfo getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(KeywordInfo keyInfo) {
        this.keyInfo = keyInfo;
    }

    public int getNplIndex() {
        return nplIndex;
    }

    public void setNplIndex(int nplIndex) {
        this.nplIndex = nplIndex;
    }

    public IllegalWordsSrcRiskLevel getSrcRiskLevel() {
        return srcRiskLevel;
    }

    public void setSrcRiskLevel(IllegalWordsSrcRiskLevel srcRiskLevel) {
        this.srcRiskLevel = srcRiskLevel;
    }

    public IllegalWordsRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(IllegalWordsRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public IllegalWordsMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(IllegalWordsMatchType matchType) {
        this.matchType = matchType;
    }

    public boolean isFenci() {
        return isFenci;
    }

    public void setFenci(boolean fenci) {
        isFenci = fenci;
    }

    public int getSingleIndex() {
        return singleIndex;
    }

    public void setSingleIndex(int singleIndex) {
        this.singleIndex = singleIndex;
    }

    public int getDiyIndex() {
        return diyIndex;
    }

    public void setDiyIndex(int diyIndex) {
        this.diyIndex = diyIndex;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getEmotionalColor() {
        return emotionalColor;
    }

    public void setEmotionalColor(int emotionalColor) {
        this.emotionalColor = emotionalColor;
    }
}
