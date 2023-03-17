package pojo;

import struct.CSharpDataInputStream;

import java.io.IOException;

/**
 * @author sxh
 * @date 2023/2/13
 */
public class FenciKeywordInfo {
    public int KeywordLength;
    //词频
    public int Count;

    //0 未标注
    //1-7 程度 4，3，2，0.5，-0.3 和-0.5 -1。
    //10-19 褒义
    //20-29 贬义
    public int EmotionalColor;

    public static FenciKeywordInfo[] readList(CSharpDataInputStream dis) throws IOException {
        int len = dis.reverseReadInt();
        FenciKeywordInfo[] result = new FenciKeywordInfo[len];

        for (int i = 0; i < len; i++) {
            FenciKeywordInfo info = new FenciKeywordInfo();
            info.KeywordLength = dis.readUnsignedByte();
            info.Count = dis.reverseReadInt();
            info.EmotionalColor = dis.readUnsignedByte();
            result[i] = info;
        }
        return result;
    }

    public int getKeywordLength() {
        return KeywordLength;
    }

    public void setKeywordLength(int keywordLength) {
        KeywordLength = keywordLength;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getEmotionalColor() {
        return EmotionalColor;
    }

    public void setEmotionalColor(int emotionalColor) {
        EmotionalColor = emotionalColor;
    }
}
