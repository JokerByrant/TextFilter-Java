package pojo;

public class MultiWordsResult {
    public int Index;
    public int TypeId;
    public String Code;
    public MultiWordsResultItem[] Items;

    public MultiWordsResult(int index, int typeId, String code, MultiWordsResultItem[] items) {
        Index = index;
        TypeId = typeId;
        Code = code;
        Items = items;
    }

    public String getHashSet() {
        StringBuilder sb = new StringBuilder();
        for (MultiWordsResultItem item : Items) {
            sb.append(item.Start);
            sb.append('-');
            sb.append(item.End);
            sb.append(',');
        }
        return sb.toString();
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int typeId) {
        TypeId = typeId;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public MultiWordsResultItem[] getItems() {
        return Items;
    }

    public void setItems(MultiWordsResultItem[] items) {
        Items = items;
    }
}
