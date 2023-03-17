package pojo;

import enums.IllegalWordsRiskLevel;

import java.util.ArrayList;
import java.util.List;

public class IllegalWordsFindAllResult {
    public IllegalWordsRiskLevel RiskLevel;
    public String Code;
    public Double SentimentScore;
    public List<SingleWordsResult> RejectSingleItems;
    public List<MultiWordsResult> RejectMultiItems;
    public List<SingleWordsResult> ReviewSingleItems;
    public List<MultiWordsResult> ReviewMultiItems;
    public List<ContactResult> ContactItems;

    public IllegalWordsFindAllResult() {
        RiskLevel = IllegalWordsRiskLevel.Pass;
        Code = null;
        SentimentScore = null;
        RejectSingleItems = new ArrayList<SingleWordsResult>();
        RejectMultiItems = new ArrayList<MultiWordsResult>();
        ReviewSingleItems = new ArrayList<SingleWordsResult>();
        ReviewMultiItems = new ArrayList<MultiWordsResult>();
        ContactItems = new ArrayList<ContactResult>();
    }

    public IllegalWordsRiskLevel getRiskLevel() {
        return RiskLevel;
    }

    public void setRiskLevel(IllegalWordsRiskLevel riskLevel) {
        RiskLevel = riskLevel;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Double getSentimentScore() {
        return SentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        SentimentScore = sentimentScore;
    }

    public List<SingleWordsResult> getRejectSingleItems() {
        return RejectSingleItems;
    }

    public void setRejectSingleItems(List<SingleWordsResult> rejectSingleItems) {
        RejectSingleItems = rejectSingleItems;
    }

    public List<MultiWordsResult> getRejectMultiItems() {
        return RejectMultiItems;
    }

    public void setRejectMultiItems(List<MultiWordsResult> rejectMultiItems) {
        RejectMultiItems = rejectMultiItems;
    }

    public List<SingleWordsResult> getReviewSingleItems() {
        return ReviewSingleItems;
    }

    public void setReviewSingleItems(List<SingleWordsResult> reviewSingleItems) {
        ReviewSingleItems = reviewSingleItems;
    }

    public List<MultiWordsResult> getReviewMultiItems() {
        return ReviewMultiItems;
    }

    public void setReviewMultiItems(List<MultiWordsResult> reviewMultiItems) {
        ReviewMultiItems = reviewMultiItems;
    }

    public List<ContactResult> getContactItems() {
        return ContactItems;
    }

    public void setContactItems(List<ContactResult> contactItems) {
        ContactItems = contactItems;
    }

    @Override
    public String toString() {
        return "结果：【" + "RiskLevel=" + RiskLevel + ", Code='" + Code + '\'' + ", SentimentScore=" + SentimentScore + '】';
    }
}
