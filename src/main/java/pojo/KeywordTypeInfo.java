package pojo;

import struct.CSharpDataInputStream;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author sxh
 * @date 2023/2/13
 */
public class KeywordTypeInfo {
    public int id;
    public int parentId;
    public String code;
    public String name;
    public boolean useTime;
    public LocalDateTime startTime;
    public LocalDateTime endTime;

    public static KeywordTypeInfo[] readList(CSharpDataInputStream dis) throws IOException {
        int len = dis.reverseReadInt();
        KeywordTypeInfo[] temps = new KeywordTypeInfo[len];
        for (int i = 0; i < len; i++) {
            KeywordTypeInfo info = new KeywordTypeInfo();
            info.id = dis.reverseReadUnsignedShort();
            info.parentId = dis.reverseReadUnsignedShort();
            info.code = dis.readString();
            info.name = dis.readString();
            info.useTime = dis.readBoolean();
            long bt = dis.reverseReadLong();
            if (bt != 0) {
                info.startTime = LocalDateTime.of(2000, 1, 1, 0, 0).plusSeconds(bt);
            }
            bt = dis.reverseReadLong();
            if (bt != 0) {
                info.endTime = LocalDateTime.of(2000, 1, 1, 0, 0).plusSeconds(bt);
            }
            temps[i] = info;
        }
        int maxId = temps[temps.length - 1].id;
        KeywordTypeInfo[] result = new KeywordTypeInfo[maxId + 1];
        for (KeywordTypeInfo temp : temps) {
            result[temp.id] = temp;
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUseTime() {
        return useTime;
    }

    public void setUseTime(boolean useTime) {
        this.useTime = useTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

