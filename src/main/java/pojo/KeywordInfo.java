package pojo;


import enums.IllegalWordsMatchType;
import enums.IllegalWordsSrcRiskLevel;
import struct.CSharpDataInputStream;

import java.io.IOException;

/**
 * @author sxh
 * @date 2023/2/13
 */
public class KeywordInfo {
    public int Id;
    public int TypeId;
    public int RiskLevel;
    public int MatchType;
    public int KeywordLength;

    public IllegalWordsSrcRiskLevel getRiskLevel() {
        return IllegalWordsSrcRiskLevel.getByValue(RiskLevel);
    }

    public IllegalWordsMatchType getMatchType() {
        return IllegalWordsMatchType.getByValue(MatchType);
    }

    public static KeywordInfo[] readList(CSharpDataInputStream dis) throws IOException {
        int len = dis.reverseReadInt();
        KeywordInfo[] result = new KeywordInfo[len + 1];

        for (int i = 0; i < len; i++) {
            KeywordInfo info = new KeywordInfo();
            info.Id = dis.reverseReadInt();
            info.TypeId = dis.reverseReadUnsignedShort();
            info.RiskLevel = dis.readUnsignedByte();
            info.MatchType = dis.readUnsignedByte();
            info.KeywordLength = dis.readUnsignedByte();
            result[i + 1] = info;
        }
        return result;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int typeId) {
        TypeId = typeId;
    }

    public void setRiskLevel(int riskLevel) {
        RiskLevel = riskLevel;
    }

    public void setMatchType(int matchType) {
        MatchType = matchType;
    }

    public int getKeywordLength() {
        return KeywordLength;
    }

    public void setKeywordLength(int keywordLength) {
        KeywordLength = keywordLength;
    }
}

