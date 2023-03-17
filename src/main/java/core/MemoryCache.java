package core;


import enums.IllegalWordsRiskLevel;
import enums.IllegalWordsSrcRiskLevel;
import pojo.CustomKeywordType;
import pojo.FenciKeywordInfo;
import pojo.KeywordInfo;
import pojo.KeywordTypeInfo;
import service.*;
import service.impl.*;
import struct.CSharpBufferedReader;
import struct.CSharpDataInputStream;
import util.FileUtil;
import util.Rcy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 缓存所有的敏感词检测的工具类，以及敏感词信息
 *
 * @author sxh
 * @date 2023/2/13
 */
public class MemoryCache {
    public FenciKeywordInfo[] FenciKeywords;
    public KeywordTypeInfo[] KeywordTypeInfos;
    public KeywordInfo[] KeywordInfos;
    public boolean[] TxtEndChars;
    public int Keyword_34_Index_Start;
    public ITranslateSearch TranslateSearch;
    public IFenciSearch FenciSearch;
    public IKeywordsSearch KeywordSearch_012;
    public IACRegexSearch KeywordSearch_34;
    public boolean UseBig;
    public IACRegexSearch BigACTextFilterSearch_34;
    public IACRegexSearch BigKeywordSearch_34;
    public IMultiWordsSearch MultiWordsSearch;
    public IContactSearch ContactSearch;
    public boolean LoadTextFilterSuccess;
    public CustomKeywordType[] KeywordTypes;

    private static MemoryCache memoryCache = null;

    public static MemoryCache getInstance() {
        if (memoryCache == null) {
            memoryCache = new MemoryCache();
        }
        return memoryCache;
    }

    /**
     * 从磁盘中加载敏感词词库
     * @param filePath
     * @throws IOException
     */
    public void initTextFilterData(String filePath) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        byte[] bytes = FileUtil.readFile(is);
        Rcy.encrypt(bytes, "012345679");
        ByteArrayOutputStream bos = FileUtil.gzipDecompress(bytes, 0);
        CSharpDataInputStream br = new CSharpDataInputStream(new ByteArrayInputStream(bos.toByteArray()));

        String phone = br.readString();
        String version = br.readString();
        int year = br.reverseReadInt();
        int month = br.reverseReadInt();
        int day = br.reverseReadInt();
        loadTextFilter(br);
        KeywordTypes = CustomKeywordType.build(KeywordTypeInfos);
    }

    /**
     * 读取敏感词词库文件
     * @param dis
     * @throws IOException
     */
    private void loadTextFilter(CSharpDataInputStream dis) throws IOException {
        FenciKeywordInfo[] fenciKeywordInfos = FenciKeywordInfo.readList(dis);
        KeywordTypeInfo[] keywordTypeInfos = KeywordTypeInfo.readList(dis);
        KeywordInfo[] keywordInfos = KeywordInfo.readList(dis);
        int length = dis.reverseReadInt();

        // Java中BufferedReader方法会提前开辟缓冲区，长度为8192的倍数，下面要读取的字符占用的字节为8213，那么就会开辟16384个字节的缓冲区
        // 正常情况下，占用8213字节的字符读取完毕后，pos的位置为 n + 8213，但实际上读取结束后，pos的位置会变为 n + 16384。
        // 因此这里手动标记一下读取字符前pos的位置，待读取结束通过 reset() 归位，然后通过 skip(8213) 将 pos 置为 n + 8213。
        dis.mark(8213);
        CSharpBufferedReader reader = new CSharpBufferedReader(new InputStreamReader(dis, StandardCharsets.UTF_8), length);
        char[] chs = new char[length];
        for (int i = 0; i < length; i++) {
            chs[i] = (char) reader.read();
        }
        dis.reset();
        dis.skip(8213);

        boolean[] txtEndChars = new boolean[0x10000];
        for (char item : chs) {
            txtEndChars[item] = true;
        }
        int keyword_34_Index_Start = dis.reverseReadInt();

        ITranslateSearch translateSearch = new TranslateSearch5();
        translateSearch.load(dis);

        IFenciSearch fenciSearch = new FenciSearch3();
        fenciSearch.load(dis);
        fenciSearch.setGetMatchKeyword(i -> fenciKeywordInfos[i]);

        IKeywordsSearch keywordSearch_012 = new KeywordsSearch2();
        keywordSearch_012.load(dis);
        keywordSearch_012.setGetMatchKeyword(i -> keywordInfos[i]);

        length = dis.reverseReadInt();
        byte[] skipIndexs = dis.readBytes(length);
        boolean[] useSkipOnce = dis.readBooleanArray();

        length = dis.reverseReadInt();
        int[][] dicts1 = new int[length][];
        int[][] dicts2 = new int[length][];
        for (int i = 0; i < length; i++) {
            dicts1[i] = dis.readShortArray();
            dicts2[i] = dis.readShortArray();
        }
        length = dis.reverseReadInt();
        ISkipwordsSearch[] skipwordsSearchs = new ISkipwordsSearch[length];
        for (int i = 0; i < length; i++) {
            byte has = dis.readByte();
            if (has == 1) {
                ISkipwordsSearch skipwordsSearch = new SkipwordsSearch();
                skipwordsSearch.load(dis);
                skipwordsSearchs[i] = skipwordsSearch;
            }
        }

        int[] dicts = dis.readShortArray();
        int[][] d_34 = Build(dicts, dicts1, dicts2);
        IACRegexSearch keywordSearch_34 = new ACRegexSearch7();
        keywordSearch_34.load(dis);
        keywordSearch_34.SetDict(skipIndexs, d_34, skipwordsSearchs, useSkipOnce);
        keywordSearch_34.setGetMatchKeyword((i) -> { return keywordInfos[i]; });

        boolean useBig = dis.readBoolean();
        IACRegexSearch bigSearch_1 = null;
        IACRegexSearch bigSearch_2 = null;
        if (useBig) {
            dicts = dis.readShortArray();
            int[][] d_34_big_1 = Build(dicts, dicts1, dicts2);
            bigSearch_1 = new ACTextFilterSearch();
            bigSearch_1.load(dis);
            bigSearch_1.SetDict(skipIndexs, d_34_big_1, skipwordsSearchs, useSkipOnce);
            bigSearch_1.setGetMatchKeyword((i) -> { return keywordInfos[i]; });

            dicts = dis.readShortArray();
            int[][] d_34_big_2 = Build(dicts, dicts1, dicts2);
            bigSearch_2 = new ACRegexSearch7();
            bigSearch_2.load(dis);
            bigSearch_2.SetDict(skipIndexs, d_34_big_2, skipwordsSearchs, useSkipOnce);
            bigSearch_2.setGetMatchKeyword((i) -> { return keywordInfos[i]; });
        }
        dicts1 = null;
        dicts2 = null;

        IMultiWordsSearch multiWordsSearch = new MultiWordsSearch4();
        multiWordsSearch.load(dis);

        IContactSearch contactSearch = new ContactSearch2();
        contactSearch.load(dis);

        // 基础数据
        FenciKeywords = fenciKeywordInfos;
        KeywordTypeInfos = keywordTypeInfos;
        KeywordInfos = keywordInfos;
        TxtEndChars = txtEndChars;
        Keyword_34_Index_Start = keyword_34_Index_Start;

        // 内置数据
        TranslateSearch = translateSearch;
        FenciSearch = fenciSearch;
        KeywordSearch_012 = keywordSearch_012;
        KeywordSearch_34 = keywordSearch_34;
        UseBig = useBig;
        BigACTextFilterSearch_34 = bigSearch_1;
        BigKeywordSearch_34 = bigSearch_2;
        MultiWordsSearch = multiWordsSearch;
        ContactSearch = contactSearch;

        // 返回成功
        LoadTextFilterSuccess = true;
    }

    private int[][] Build(int[] old, int[][] dicts1, int[][] dicts2) {
        int[][] result = new int[dicts1.length][];
        for (int i = 0; i < dicts1.length; i++) {
            int[] n = Arrays.copyOf(old, old.length);
            int[] d1 = dicts1[i];
            int[] d2 = dicts2[i];

            for (int k : d1) {
                n[k] = 0xFFFF;
            }
            for (int k : d2) {
                n[k] = 0xFFFE;
            }
            result[i] = n;
        }
        return result;
    }

    /**
     * 根据词库中为词汇定义敏感等级来获取实际的敏感等级
     * @param typeId
     * @param srcRiskLevel 词库中定义的敏感等级 0.正常 1.触线 2.危险 3.违规 4.指向性违规
     * @return
     */
    public IllegalWordsRiskLevel getRiskLevel(int typeId, IllegalWordsSrcRiskLevel srcRiskLevel) {
        if (typeId == 0) {
            return IllegalWordsRiskLevel.Pass;
        }
        if (srcRiskLevel == IllegalWordsSrcRiskLevel.Normal) {
            return IllegalWordsRiskLevel.Pass;
        }
        if (KeywordTypes == null || KeywordTypes.length <= typeId) {
            if (srcRiskLevel == IllegalWordsSrcRiskLevel.Sensitive) {
                return IllegalWordsRiskLevel.Review;
            }
            return IllegalWordsRiskLevel.Reject;
        }
        CustomKeywordType type = KeywordTypes[typeId];
        if (type.UseTime) {
            int month = LocalDateTime.now().getMonthValue();
            int day = LocalDateTime.now().getDayOfMonth();
            if (type.StartTime != null) {
                if (type.EndTime != null) {
                    if (type.StartTime.compareTo(type.EndTime) <= 0) {
                        if (month < type.StartTime.getMonthValue() || (month == type.StartTime.getMonthValue() && day < type.StartTime.getDayOfMonth())) {
                            return IllegalWordsRiskLevel.Pass;
                        } else if (month > type.EndTime.getMonthValue() || (month == type.EndTime.getMonthValue() && day > type.EndTime.getDayOfMonth())) {
                            return IllegalWordsRiskLevel.Pass;
                        }
                    } else if ((month < type.StartTime.getMonthValue() || (month == type.StartTime.getMonthValue() && day < type.StartTime.getDayOfMonth())) && (month > type.EndTime.getMonthValue() || (month == type.EndTime.getMonthValue() && day > type.EndTime.getDayOfMonth()))) {
                        return IllegalWordsRiskLevel.Pass;
                    }
                } else if (month < type.StartTime.getMonthValue() || (month == type.StartTime.getMonthValue() && day < type.StartTime.getDayOfMonth())) {
                    return IllegalWordsRiskLevel.Pass;
                }
            } else if (type.EndTime != null) {
                if (month > type.EndTime.getMonthValue() || (month == type.EndTime.getMonthValue() && day > type.EndTime.getDayOfMonth())) {
                    return IllegalWordsRiskLevel.Pass;
                }
            }
        }
        if (srcRiskLevel == IllegalWordsSrcRiskLevel.Sensitive) {
            return type.RiskLevel_1 != null ? type.RiskLevel_1 : IllegalWordsRiskLevel.Review;
        }
        if (srcRiskLevel == IllegalWordsSrcRiskLevel.Dangerous) {
            return type.RiskLevel_2 != null ? type.RiskLevel_2 : IllegalWordsRiskLevel.Reject;
        }
        return type.RiskLevel_3 != null ? type.RiskLevel_3 : IllegalWordsRiskLevel.Reject;
    }

}
