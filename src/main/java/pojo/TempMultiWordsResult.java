package pojo;

public class TempMultiWordsResult {
    public int resultIndex;
    public TempWordsResultItem[] keywordIndexs;

    public TempMultiWordsResult(int resultIndex, TempWordsResultItem[] keywordIndexs) {
        this.resultIndex = resultIndex;
        this.keywordIndexs = keywordIndexs;
    }

    public boolean containsRange(TempWordsResultItem[] newIndexs) {
        if (keywordIndexs.length != newIndexs.length) {
            return false;
        }

        for (int i = 0; i < keywordIndexs.length; i++) {
            if (!keywordIndexs[i].containsRange(newIndexs[i])) {
                return false;
            }
        }

        return true;
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    public TempWordsResultItem[] getKeywordIndexs() {
        return keywordIndexs;
    }

    public void setKeywordIndexs(TempWordsResultItem[] keywordIndexs) {
        this.keywordIndexs = keywordIndexs;
    }
}
