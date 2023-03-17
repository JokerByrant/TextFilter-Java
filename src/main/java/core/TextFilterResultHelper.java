package core;


import enums.IllegalWordsRiskLevel;
import org.apache.commons.collections4.CollectionUtils;
import pojo.*;
import util.RangeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author sxh
 * @date 2023/3/15
 */
public class TextFilterResultHelper {
    public static TextFilterResult getTextFilterResult(IllegalWordsFindAllResult temp, String text) {
        TextFilterResult result = new TextFilterResult();
        result.setSentimentScore(temp.getSentimentScore());
        if (temp.getRiskLevel() == IllegalWordsRiskLevel.Pass) {
            result.setRiskLevel("PASS");
        } else if (temp.getRiskLevel() == IllegalWordsRiskLevel.Reject) {
            result.setRiskLevel("REJECT");
            result.setRiskCode(temp.getCode());
            result.setDetails(new ArrayList<>());
            HashSet<String> positions = new HashSet<String>();
            getSingleTextFilterDetailResult(result.getDetails(), temp.getRejectSingleItems(), positions, IllegalWordsRiskLevel.Reject, text);
            getMultiTextFilterDetailResult(result.getDetails(), temp.getRejectMultiItems(), positions, IllegalWordsRiskLevel.Reject, text);
            getSingleTextFilterDetailResult(result.getDetails(), temp.getReviewSingleItems(), positions, IllegalWordsRiskLevel.Review, text);
            getMultiTextFilterDetailResult(result.getDetails(), temp.getReviewMultiItems(), positions, IllegalWordsRiskLevel.Review, text);
            setContacts(result, temp, text);
        } else {
            result.setRiskLevel("REVIEW");
            result.setRiskCode(temp.getCode());
            result.setDetails(new ArrayList<>());
            HashSet<String> positions = new HashSet<>();
            getSingleTextFilterDetailResult(result.getDetails(), temp.getReviewSingleItems(), positions, IllegalWordsRiskLevel.Review, text);
            getMultiTextFilterDetailResult(result.getDetails(), temp.getReviewMultiItems(), positions, IllegalWordsRiskLevel.Review, text);
            setContacts(result, temp, text);
        }
        getTextRanges(result, text);
        return result;
    }

    private static void getTextRanges(TextFilterResult result, String text) {
        List<Range> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result.getDetails())) {
            for (TextFilterDetailItem item : result.getDetails()) {
                list.add(item.getRange());
            }
        }
        if (CollectionUtils.isNotEmpty(result.getContacts())) {
            for (TextFilterContactItem item : result.getContacts()) {
                list.add(item.getRange());
            }
        }
        result.setRanges(RangeUtil.findIntersection(list));
        for (Range range : result.getRanges()) {
            range.setText(text.substring(range.getStart(), range.getEnd() + 1));
        }
    }

    private static void getSingleTextFilterDetailResult(List<TextFilterDetailItem> results, List<SingleWordsResult> singles, HashSet<String> positions, IllegalWordsRiskLevel riskLevel, String text) {
        for (SingleWordsResult resultItem : singles) {
            TextFilterDetailItem result = new TextFilterDetailItem();
            result.setRiskCode(resultItem.getCode());
            if (riskLevel == IllegalWordsRiskLevel.Reject) {
                result.setRiskLevel("REJECT");
            } else {
                result.setRiskLevel("REVIEW");
            }
            if (resultItem.getStart() == resultItem.getEnd()) {
                result.setPosition(String.valueOf(resultItem.getStart()));
            } else {
                result.setPosition(resultItem.getStart() + "-" + resultItem.getEnd());
            }
            result.setRange(new Range(resultItem.getStart(), resultItem.getEnd()));
            result.setText(text.substring(resultItem.getStart(), resultItem.getEnd() + 1));
            if (positions.add(result.getPosition())) {
                results.add(result);
            }
        }
    }

    private static void getMultiTextFilterDetailResult(List<TextFilterDetailItem> results, List<MultiWordsResult> multis, HashSet<String> positions,
                                                  IllegalWordsRiskLevel riskLevel, String text) {
        for (MultiWordsResult resultItem : multis) {
            TextFilterDetailItem result = new TextFilterDetailItem();
            result.setRiskCode(resultItem.getCode());
            if (riskLevel == IllegalWordsRiskLevel.Reject) {
                result.setRiskLevel("REJECT");
            } else {
                result.setRiskLevel("REVIEW");
            }
            TextRange textRange = new TextRange();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultItem.getItems().length; i++) {
                MultiWordsResultItem t = resultItem.getItems()[i];
                textRange.add(t.getStart(), t.getEnd());
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(text, t.getStart(), t.getEnd() + 1);
            }
            result.setText(sb.toString());
            result.setPosition(textRange.toString());
            result.setRange(new Range(result.getPosition()));
            if (positions.add(result.getPosition())) {
                results.add(result);
            }
        }
    }

    private static void setContacts(TextFilterResult result, IllegalWordsFindAllResult temp, String text) {
        if (temp.getContactItems().size() == 0) {
            return;
        }
        result.setContacts(new ArrayList<>());
        for (ContactResult contactItem : temp.getContactItems()) {
            TextFilterContactItem item = new TextFilterContactItem();
            item.setContactType(contactItem.getContactType() + "");
            item.setPosition(contactItem.getStart() + "-" + contactItem.getEnd());
            item.setRange(new Range(contactItem.getStart(), contactItem.getEnd()));
            item.setContactString(text.substring(contactItem.getStart(), contactItem.getEnd() + 1));
            result.getContacts().add(item);
        }
    }
}
