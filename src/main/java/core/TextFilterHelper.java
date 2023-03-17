package core;


import enums.IllegalWordsMatchType;
import enums.IllegalWordsRiskLevel;
import enums.IllegalWordsSrcRiskLevel;
import org.apache.commons.collections4.CollectionUtils;
import pojo.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文本检测步骤：先获取通用字、正常词、触线敏感词、违规敏感词，再进行分词排除误杀，检索出单组敏感词、检索出多组敏感词、检索出联系方式词
 *
 * @author sxh
 * @date 2023/3/6
 */
public class TextFilterHelper {
    private final static String _skipList = " 　\u00A0\t\r\n~!@#$%^&*()_+-=【】、[]{}|;':\"，。、《》？。，、；：？！…—·ˉ¨‘’“”～‖∶＂＇｀｜〃〔〕〈〉《》「」『』．〖〗【】（）［］｛｝≈≡≠＝≤≥＜＞≮≯∷±＋－×÷／∫∮∝∞∧∨∑∏∪∩∈∵∴⊥∥∠⌒⊙≌∽√§☆★○●◎◇◆□℃‰€■△▲※→←↑↓〓¤°＃＆＠＼︿＿￣―┌┍┎┐┑┒┓─┄┈├┝┞┟┠┡┢┣│┆┊┬┭┮┯┰┱┲┳┼┽┾┿╀╁╂╃└┕┖┗┘┙┚┛━┅┉┤┥┦┧┨┩┪┫┃┇┋┴┵┶┷┸┹┺┻╋╊╉╈╇╆╅╄";
    private static boolean[] _skipBitArray;

    static {
        _skipBitArray = new boolean[0x10000];
        for (int i = 0; i < _skipList.length(); i++) {
            _skipBitArray[_skipList.charAt(i)] = true;
        }
    }

    public static boolean containsAny(ReadStreamBase stream) {
        return false;
    }

    /**
     * 查找给定的字符串中的所有敏感词
     * @param stream
     * @return
     */
    public static IllegalWordsFindAllResult findAll(ReadStreamBase stream) {
        // 获取匹配字符，获取通用字、正常词、触线敏感词、违规敏感词
        List<TempWordsResultItem> illegalWords1 = findIllegalWords(stream.testingText);
        for (TempWordsResultItem item : illegalWords1) {
            item.start = stream.start[item.start];
            item.end = stream.end[item.end];
        }

        // 使用动态规则进行NLP技术分词
        TextSplit4 textSplit = buildTextSplit(stream.source, illegalWords1, MemoryCache.getInstance().Keyword_34_Index_Start);
        // 分词后的全部敏感词
        List<TempWordsResultItem> fenciwords = textSplit.getWordsContext();
        textSplit.setNplIndex(fenciwords);
        // 分词后的单组敏感词
        List<TempWordsResultItem> illegalWords2 = textSplit.getIllegalWords();
        // 分词后的多组敏感词
        List<TempWordsResultItem> illegalWords3 = textSplit.getIllegalWords2();

        if (CollectionUtils.isEmpty(illegalWords2) && CollectionUtils.isEmpty(illegalWords3)) {
            // 分词后的单组敏感词和多组敏感词都不存在，表示给定文本没有触限词汇
            IllegalWordsFindAllResult illegalWordsFindAllResult = new IllegalWordsFindAllResult();
            // 计算分词后词组的情感值后，将结果返回
            illegalWordsFindAllResult.SentimentScore = calcEmotionScore(fenciwords);
            return illegalWordsFindAllResult;
        } else {
            IllegalWordsFindAllResult result = new IllegalWordsFindAllResult();
            // 解析单组敏感词
            analysisSingleWordsResult(stream.source, result, illegalWords2);
            List<TempMultiWordsResult> items = MemoryCache.getInstance().MultiWordsSearch.findAll(illegalWords3);
            // 解析多组敏感词
            analysisMultiWordsResult(stream.source, result, items);
            // 解析获取联系方式
            result.setContactItems(MemoryCache.getInstance().ContactSearch.findAll(illegalWords3));
            // 获取并计算词汇的情感值
            result.setSentimentScore(calcEmotionScore(fenciwords));
            if (result.getRejectSingleItems().size() > 0 || result.getRejectMultiItems().size() > 0) {
                result.setRiskLevel(IllegalWordsRiskLevel.Reject);
                result.setCode(getCode(result.getRejectSingleItems(), result.getRejectMultiItems()));
            } else if (result.getReviewSingleItems().size() > 0 || result.getReviewMultiItems().size() > 0) {
                result.setRiskLevel(IllegalWordsRiskLevel.Review);
                result.setCode(getCode(result.getReviewSingleItems(), result.getReviewMultiItems()));
            } else if (result.getContactItems().size() > 0) {
                result.setRiskLevel(IllegalWordsRiskLevel.Review);
                result.setCode("Contact");
            }

            return result;
        }
    }

    /**
     * 获取匹配字符，获取通用字、正常词、触线敏感词、违规敏感词
     * @param txt
     * @return
     */
    private static List<TempWordsResultItem> findIllegalWords(char[] txt) {
        List<TempWordsResultItem> illegalWordsResults = new ArrayList<>();
        int len = txt.length;
        // 1.查找匹配违规敏感词
        if (MemoryCache.getInstance().UseBig) {
            if (len < 5000) {
                MemoryCache.getInstance().KeywordSearch_34.findAll(txt, len, illegalWordsResults); // 2
            } else {
                MemoryCache.getInstance().BigKeywordSearch_34.findAll(txt, len, illegalWordsResults); // 这个是联系方式为主的
                MemoryCache.getInstance().BigACTextFilterSearch_34.findAll(txt, len, illegalWordsResults); // 2
            }
        } else {
            MemoryCache.getInstance().KeywordSearch_34.findAll(txt, len, illegalWordsResults); // 2
        }
        // 2.查找正常词、触线敏感词
        MemoryCache.getInstance().KeywordSearch_012.findAll(txt, len, illegalWordsResults); // 1
        // 3.查找通用字
        MemoryCache.getInstance().FenciSearch.findAll(txt, len, illegalWordsResults); // 5
        return illegalWordsResults;
    }

    /**
     * 使用动态规划进行NLP技术分词
     * @param txt
     * @param illegalWordsResults
     * @param keyword34_start_index
     * @return
     */
    public static TextSplit4 buildTextSplit(char[] txt, List<TempWordsResultItem> illegalWordsResults, int keyword34_start_index) {
        TextSplit4 line = new TextSplit4(txt.length);
        for (TempWordsResultItem illegalWordsResult : illegalWordsResults) {
            line.AddWords(illegalWordsResult, keyword34_start_index);
        }
        int[] contactDict = MemoryCache.getInstance().ContactSearch.GetContactDict();
        line.removeMaxLengthContact(contactDict, txt);

        line.Calculation(txt, _skipBitArray);
        return line;
    }

    /**
     * 计算各个词汇的情感值，情感值分为 [中性]、[普通]、[褒义]、[贬义]，并根据设定的数值来确定程度
     * @param keywordInfos
     * @return
     */
    private static double calcEmotionScore(List<TempWordsResultItem> keywordInfos) {
        double emotionScore = 0.0;
        double preEmotionScore = 1.0;

        for (TempWordsResultItem info : keywordInfos) {
            if (info.emotionalColor == 0) {
                continue;
            }
            if (info.emotionalColor >= 10) {
                preEmotionScore = preEmotionScore * info.getEmotionScore();
                emotionScore += preEmotionScore;
                preEmotionScore = 1.0;
                continue;
            }
            preEmotionScore = preEmotionScore * info.getEmotionScore();
        }
        return emotionScore;
    }

    /**
     * 解析单组敏感词
     * @param txt 给定的待检测文本
     * @param result 结果集
     * @param items 单组敏感词
     */
    private static void analysisSingleWordsResult(char[] txt, IllegalWordsFindAllResult result, List<TempWordsResultItem> items) {
        Set<Integer> passSet = new HashSet<>();
        Set<Integer> reviewSet = new HashSet<>();

        //优先采用自定义 1）加 违规 ；2）加 触线 ；3）加 正常
        for (TempWordsResultItem item : items) {
            if (item.getDiyIndex() == 0) {
                continue;
            }
            if (item.getRiskLevel() == IllegalWordsRiskLevel.Reject) {
                // 违规词汇
                result.getRejectSingleItems().add(new SingleWordsResult(item.getStart(), item.getEnd(), item.getDiyIndex()));
            } else if (item.getRiskLevel() == IllegalWordsRiskLevel.Review) {
                // 触限词汇
                result.getReviewSingleItems().add(new SingleWordsResult(item.getStart(), item.getEnd(), item.getDiyIndex()));
                reviewSet.add(item.getPosition());
            } else if (item.getRiskLevel() == IllegalWordsRiskLevel.Pass) {
                // 正常词汇
                passSet.add(item.getPosition());
            }
        }

        List<TempWordsResultItem> reviewDict = new ArrayList<TempWordsResultItem>();
        List<TempWordsResultItem> rejectDict = new ArrayList<TempWordsResultItem>();

        //内置敏感词 1）分 违规、触线
        for (TempWordsResultItem item : items) {
            if (item.getDiyIndex() > 0) {
                continue;
            }
            if (item.getSrcRiskLevel() == IllegalWordsSrcRiskLevel.Part) {
                continue;
            }
            if (matchText(txt, item)) {// 检测词组是否满足匹配规则
                IllegalWordsRiskLevel riskLevel = MemoryCache.getInstance().getRiskLevel(item.getTypeId(), item.getSrcRiskLevel());
                if (riskLevel == IllegalWordsRiskLevel.Reject) {
                    rejectDict.add(item);
                } else if (riskLevel == IllegalWordsRiskLevel.Review) {
                    reviewDict.add(item);
                } else if (riskLevel == IllegalWordsRiskLevel.Pass) {
                    passSet.add(item.getPosition());
                }
            }
        }

        CustomKeywordType[] types = MemoryCache.getInstance().KeywordTypes;
        //内置敏感词 2）先加触线
        for (TempWordsResultItem review : reviewDict) {
            int positions = review.getPosition();
            if (passSet.contains(positions)) {
                continue;
            } else if (reviewSet.add(positions)) {
                result.getReviewSingleItems().add(new SingleWordsResult(review.getTypeId(), review.getStart(), review.getEnd(), types[review.getTypeId()].getCode(), review.getSingleIndex()));
            }
        }

        reviewDict = null;

        HashSet<Integer> rejectSet = new HashSet<Integer>();
        //内置敏感词 3）加 违规
        for (TempWordsResultItem reject : rejectDict) {
            int positions = reject.getPosition();
            if (passSet.contains(positions)) {
                continue;
            } else if (reviewSet.contains(positions)) {
                continue;
            } else if (rejectSet.add(positions)) {
                result.getRejectSingleItems().add(new SingleWordsResult(reject.getTypeId(), reject.getStart(), reject.getEnd(), types[reject.getTypeId()].getCode(), reject.getSingleIndex()));
            }
        }
    }

    /**
     * 解析多组敏感词
     * @param txt
     * @param result
     * @param multiWords
     */
    public static void analysisMultiWordsResult(char[] txt, IllegalWordsFindAllResult result, List<TempMultiWordsResult> multiWords) {
        KeywordInfo[] keywordInfos = MemoryCache.getInstance().KeywordInfos;
        CustomKeywordType[] keywordTypes = MemoryCache.getInstance().KeywordTypes;

        for (TempMultiWordsResult multi : multiWords) {
            KeywordInfo mutliKeyword = keywordInfos[multi.resultIndex];

            if (matchText(txt, multi, mutliKeyword)) {
                IllegalWordsSrcRiskLevel srcRiskLevel = mutliKeyword.getRiskLevel();
                IllegalWordsRiskLevel riskLevel = MemoryCache.getInstance().getRiskLevel(mutliKeyword.TypeId, srcRiskLevel);
                if (riskLevel == IllegalWordsRiskLevel.Pass) {
                    continue;
                }

                MultiWordsResultItem[] items = new MultiWordsResultItem[multi.getKeywordIndexs().length];
                for (int i = 0; i < items.length; i++) {
                    TempWordsResultItem item = multi.getKeywordIndexs()[i];
                    items[i] = new MultiWordsResultItem(item.start, item.end);
                }
                MultiWordsResult r = new MultiWordsResult(multi.getResultIndex(), mutliKeyword.TypeId, keywordTypes[(mutliKeyword.TypeId)].Code, items);

                if (riskLevel == IllegalWordsRiskLevel.Review) {
                    result.ReviewMultiItems.add(r);
                } else {
                    result.RejectMultiItems.add(r);
                }
            }

        }
    }

    /**
     * 根据词组的匹配模式判断词组是否满足触限条件
     * matchType => 敏感词库中为每个词汇定义的匹配类型
     * @param txt
     * @param result
     * @param item
     * @return
     */
    private static boolean matchText(char[] txt, TempMultiWordsResult result, KeywordInfo item) {
        IllegalWordsMatchType matchType = item.getMatchType();
        if (matchType == IllegalWordsMatchType.PartMatch) {
            return true;
        } else if (matchType == IllegalWordsMatchType.MatchTextStart) {
            return isSymbolStart(txt, result.getKeywordIndexs()[0].getStart());
        } else if (matchType == IllegalWordsMatchType.MatchTextEnd) {
            return isSymbolEnd(txt, result.getKeywordIndexs()[result.getKeywordIndexs().length - 1].getEnd());
        } else if (matchType == IllegalWordsMatchType.MatchTextStartOrEnd) {
            if (isSymbolStart(txt, result.getKeywordIndexs()[0].getStart())) {
                return true;
            }
            return isSymbolEnd(txt, result.getKeywordIndexs()[result.getKeywordIndexs().length - 1].getEnd());
        } else {
            int start = result.getKeywordIndexs()[0].getStart();
            int end = result.getKeywordIndexs()[result.getKeywordIndexs().length - 1].getEnd();
            return isSymbolStart(txt, start) && isSymbolEnd(txt, end);
        }
    }

    /**
     * 根据词组的匹配模式判断词组是否满足触限条件
     * matchType => 敏感词库中为每个词汇定义的匹配类型
     * @param txt
     * @param item
     * @return
     */
    private static boolean matchText(char[] txt, TempWordsResultItem item) {
        if (item.getMatchType() == IllegalWordsMatchType.PartMatch) {
            return true;
        } else if (item.getMatchType() == IllegalWordsMatchType.MatchTextStart) {
            return isSymbolStart(txt, item.getStart());
        } else if (item.getMatchType() == IllegalWordsMatchType.MatchTextEnd) {
            return isSymbolEnd(txt, item.getEnd());
        } else if (item.getMatchType() == IllegalWordsMatchType.MatchTextStartOrEnd) {
            if (isSymbolStart(txt, item.getStart())) {
                return true;
            }
            return isSymbolEnd(txt, item.getEnd());
        }
        return isSymbolStart(txt, item.getStart()) && isSymbolEnd(txt, item.getEnd());
    }

    private static boolean isSymbolStart(char[] txt, int start) {
        if (start == 0) {
            return true;
        }
        return MemoryCache.getInstance().TxtEndChars[txt[start - 1]];
    }

    private static boolean isSymbolEnd(char[] txt, int end) {
        if (end == txt.length - 1) {
            return true;
        }
        return MemoryCache.getInstance().TxtEndChars[txt[end + 1]];
    }

    public static String getCode(List<SingleWordsResult> singles, List<MultiWordsResult> multis) {
        int min = Integer.MAX_VALUE;
        String code = "";
        for (int i = 0; i < singles.size(); i++) {
            SingleWordsResult singleWordsResultItem = singles.get(i);
            if (singleWordsResultItem.getTypeId() < min) {
                min = singleWordsResultItem.getTypeId();
                code = singleWordsResultItem.getCode();
            }
        }
        for (int i = 0; i < multis.size(); i++) {
            MultiWordsResult multiIllegalWordsResult = multis.get(i);
            if (multiIllegalWordsResult.getTypeId() < min) {
                min = multiIllegalWordsResult.getTypeId();
                code = multiIllegalWordsResult.getCode();
            }
        }
        return code;
    }


}
