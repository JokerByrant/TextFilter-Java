package pojo;


import enums.IllegalWordsSrcRiskLevel;

import java.util.*;

/**
 * 将一句话分词，使词组的个数最小，并且权重之合最大
 * 分词介绍：https://easyai.tech/ai-definition/tokenization/
 *
 * @author sxh
 * @date 2023/3/4
 */
public class TextSplit4 {
    //  MinWords长度
    private final int _len;
    //  文本长度，
    private final int _end;
    // 最小长度字符
    private final WordsInfo[] MinWords;
    // 下一个词
    private final List<WordsInfo>[] NextWords;
    // 空白符号、换行符
    private static HashSet<Character> _check = new HashSet<>(Arrays.asList('\r', '\n', '\t', ' ', '　', '\u00A0'));

    public TextSplit4(int textLength) {
        _end = textLength;
        _len = textLength + 1;
        MinWords = new WordsInfo[_len];
        NextWords = new List[_len];
    }

    public void AddWords(TempWordsResultItem context, int keyword34_startIndex) {
        List<WordsInfo> nextWords = NextWords[context.start];
        if (nextWords == null) {
            nextWords = new ArrayList<>();
            NextWords[context.start] = nextWords;
        } else {
            for (WordsInfo first : nextWords) {
                if (first.End == context.end) {
                    if (context.diyIndex > 0) { // 自定义
                        first.Context_012 = context;
                    } else if (context.srcRiskLevel == IllegalWordsSrcRiskLevel.Part) { // 风险类型为多组敏感词部分
                        first.Context_34 = context;
                    } else if (context.singleIndex > 0) { // 为单组敏感词
                        first.Context_012 = context;
                        if (context.singleIndex > keyword34_startIndex) { // 多组敏感词
                            first.Context_34 = context;
                        }
                    } else {
                        first.Count = first.Count + context.count; // 添加单词权重
                        if (first.Context_012 == null) {
                            // 设置单组敏感词
                            first.Context_012 = context;
                        } else {
                            // 设置情感值
                            first.Context_012.emotionalColor = context.emotionalColor;
                        }
                    }
                    return;
                }
            }
        }
        if (context.srcRiskLevel == IllegalWordsSrcRiskLevel.Part) {
            // 风险类型为多组敏感词部分
            nextWords.add(new WordsInfo(context.start, context.end, context.count, null, context));
        } else {
            if (context.singleIndex > keyword34_startIndex) {
                // 为多组敏感词
                nextWords.add(new WordsInfo(context.start, context.end, context.count, context, context));
            } else {
                // 为单组敏感词
                nextWords.add(new WordsInfo(context.start, context.end, context.count, context, null));
            }
        }
    }

    /**
     * 去除较长的联系方式
     * @param dict
     * @param chs
     */
    public void removeMaxLengthContact(final int[] dict, final char[] chs) {
        for (List<WordsInfo> nexts : NextWords) {
            if (nexts == null) {
                continue;
            }
            if (nexts.size() == 1) {
                continue;
            }

            WordsInfo wordsInfo = null;
            int key = 0;
            for (int j = nexts.size() - 1; j >= 0; j--) {
                WordsInfo next = nexts.get(j);
                int val;
                // 当前词汇包含单组敏感词或多组敏感词，进入循环
                if ((next.Context_012 != null && (val = dict[next.Context_012.singleIndex]) > 0) || (next.Context_34 != null && (val = dict[next.Context_34.singleIndex]) > 0)) {
                    if (wordsInfo == null) {
                        wordsInfo = next;
                        key = val;
                    } else if (key != val) {
                        wordsInfo = next;
                        key = val;
                    } else if (next.End < wordsInfo.End) {
                        boolean find = false;
                        for (int k = next.End + 1; k <= wordsInfo.End; k++) {
                            // 判断有无 空白符号、换行符
                            if (_check.contains(chs[k])) {
                                find = true;
                                break;
                            }
                        }
                        if (find) {
                            // 去除较长的联系方式
                            nexts.remove(wordsInfo);
                            // 保留最短的联系方式
                            wordsInfo = next;
                        }
                    } else {
                        boolean find = false;
                        for (int k = next.Start; k <= next.End; k++) {
                            // 判断有无 空白符号、换行符
                            if (_check.contains(chs[k])) {
                                find = true;
                                break;
                            }
                        }
                        if (find) {
                            // 去除较长的联系方式
                            nexts.remove(next);
                        }
                    }
                }
            }
        }
    }

    /**
     * 提取给定文本中的信息
     * @param txt
     * @param _skipBitArray
     */
    public void Calculation(char[] txt, boolean[] _skipBitArray) {
        int[] MinLength = new int[_len];
        int[] MaxCount = new int[_len];

        // 初始长度
        MinLength[0] = 1;
        for (int i = 0; i <= _end; i++) {
            // 获取当前最小长度值
            int minLength = MinLength[i];
            if (minLength == 0) {
                continue;
            }
            minLength++;
            // 下一个字符
            List<WordsInfo> nextWords = NextWords[i];
            if (nextWords != null) {
                // 获取最大权重
                int count = MaxCount[i];
                for (WordsInfo next : nextWords) {
                    // 下一个字符位置
                    int endCharIndex = next.End + 1;
                    int endMinLength = MinLength[endCharIndex];
                    // 获取结束位置的权重
                    int endCount = count + next.Count;

                    if ((endMinLength == 0) || (endMinLength > minLength) || ((endMinLength == minLength) && (MaxCount[endCharIndex] < endCount))) {
                        MinLength[endCharIndex] = minLength; // 设置最小长度
                        MaxCount[endCharIndex] = endCount; // 设置权重
                        MinWords[endCharIndex] = next; // 设置最小位置
                    }
                }
            }
            if (i < _end && MinLength[i + 1] == 0) { // 下一个位置长度为0
                if (_skipBitArray[txt[i]]) { // 跳词时，
                    minLength--; // 还原最小长度
                }
                MinLength[i + 1] = minLength; // 设置最小长度
                MaxCount[i + 1] = MaxCount[i] + 1; // 设置权重
            }
        }
    }

    /**
     * 获取分词，将之前区分的单组敏感词和多组敏感词同时返回
     * @return
     */
    public List<TempWordsResultItem> getWordsContext() {
        Stack<TempWordsResultItem> temp = new Stack<>();
        // 获取最后索引
        int end = _end;
        // 从右到左遍历
        while (end != 0) {
            // 获取当前位置
            WordsInfo words = MinWords[end];
            // 字符为空，继续遍历下一个位置
            if (words == null) {
                end--;
            } else {
                if (words.Context_012 != null) { // 单组敏感词
                    temp.push(words.Context_012);
                } else if (words.Context_34 != null) { // 多组敏感词
                    temp.push(words.Context_34);
                }
                end = words.Start;
            }
        }
        // 添加到结果集，将上面的临时集合中的数据反转回来
        List<TempWordsResultItem> result = new ArrayList<>(temp.size());
        TempWordsResultItem item;
        while (!temp.empty()) {
            item = temp.pop();
            result.add(item);
        }
        temp = null;
        return result;
    }

    /**
     * 给多组敏感词设置npl索引
     * @param items
     */
    public void setNplIndex(List<TempWordsResultItem> items) {
        for (int i = 1; i < items.size(); i++) {
            int start = items.get(i).start;
            int end = items.get(i).end;
            for (int j = start; j <= end; j++) {
                List<WordsInfo> nextWords = NextWords[j];
                if (nextWords == null) {
                    continue;
                }
                for (WordsInfo item : nextWords) {
                    // 给多组敏感词设置npl索引
                    if (item.Context_34 != null) {
                        item.Context_34.nplIndex = i;
                    }
                }
            }
        }
    }

    /**
     * 获取单组敏感词组
     * @return
     */
    public List<TempWordsResultItem> getIllegalWords() {
        Stack<TempWordsResultItem> temp = new Stack<TempWordsResultItem>();
        // 获取最后索引
        int end = _end;
        // 从右到左遍历
        while (end != 0) {
            // 获取当前位置
            WordsInfo words = MinWords[end];
            // 字符为空，继续遍历下一个位置
            if (words == null) {
                end--;
            } else {
                // 拿到文本中的单组敏感词
                TempWordsResultItem context = words.Context_012;
                if (context != null) {
                    // 排除分词
                    if (!context.isFenci) {
                        temp.push(context);
                    }
                } else {
                    // 拿到【多组敏感词】内的【单组的敏感词】
                    // 有些多组敏感词的部分由多个【单组敏感词】组成，但在检测时不会检测出这个多组敏感词，会掩盖有用的【单组的敏感词】。
                    if (words.Context_34 != null) {
                        int start = words.Start;
                        // 拆分出多组敏感词中的单组敏感词
                        for (int i = end - 1; i >= start; i--) {
                            List<WordsInfo> nextwords = NextWords[i];
                            if (nextwords == null) { continue; }

                            for (WordsInfo nextword : nextwords) {
                                // 使用单组的敏感词
                                TempWordsResultItem context2 = nextword.Context_012;
                                if (context2 != null && context2.srcRiskLevel != null && context2.end < end) {
                                    temp.push(context2);
                                }
                            }
                        }
                    }
                }
                end = words.Start;
            }
        }

        // 添加到结果集，将上面的临时集合中的数据反转回来
        int len = temp.size();
        List<TempWordsResultItem> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            result.add(temp.pop());
        }
        return result;
    }

    /**
     * 获取多组敏感词组
     * @return
     */
    public List<TempWordsResultItem> getIllegalWords2() {
        Stack<TempWordsResultItem> temp = new Stack<TempWordsResultItem>();
        // 获取最后索引
        int end = _end;
        // 从右到左遍历
        while (end != 0) {
            // 获取当前位置
            WordsInfo words = MinWords[end];
            // 字符为空，继续遍历下一个位置
            if (words == null) {
                end--;
            } else {
                // 拿到文本中的多组敏感词
                TempWordsResultItem context = words.Context_34;
                if (context != null) {
                    int start = words.Start;
                    // 拆分出多组敏感词中的多组敏感词
                    // 【字符串较长】的【多组敏感词部分】可能覆盖了【字符串较短】的【多组敏感词部分】，所以这里进行了二次拆分
                    for (int i = end - 1; i >= start; i--) {
                        List<WordsInfo> nextwords = NextWords[i];
                        if (nextwords == null) { continue; }

                        for (WordsInfo nextword : nextwords) {
                            TempWordsResultItem context2 = nextword.Context_34;
                            if (context2 != null && context2.end < end) {
                                temp.push(context2);
                            }
                        }
                    }
                }
                end = words.Start;
            }
        }

        // 添加到结果集，将上面的临时集合中的数据反转回来
        int len = temp.size();
        List<TempWordsResultItem> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            result.add(temp.pop());
        }
        return result;
    }
}
