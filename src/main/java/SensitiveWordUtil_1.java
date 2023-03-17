import core.MemoryCache;
import core.TextFilterHelper;
import core.TextFilterResultHelper;
import enums.IllegalWordsRiskLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.IllegalWordsFindAllResult;
import pojo.ReadStreamBase;
import pojo.TextFilterResult;

/**
 * @author sxh
 * @date 2023/3/6
 */
public class SensitiveWordUtil_1 {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordUtil_1.class);

    public void initTextFilterData() {
        try {
            MemoryCache.getInstance().initTextFilterData("BOOT-INF/lib/TextFilter-20210923.data");
        } catch (Exception e) {
            logger.error("敏感词文件加载失败！", e);
        }
    }

    /**
     * 查找给定文本中的所有敏感词
     * @param text
     * @return
     */
    public TextFilterResult findAll(String text) {
        ReadStreamBase streamBase = MemoryCache.getInstance().TranslateSearch.replace(text, false);
        IllegalWordsFindAllResult result = TextFilterHelper.findAll(streamBase);
        return TextFilterResultHelper.getTextFilterResult(result, text);
    }

    /**
     * 判断给定文本中是否包含敏感词
     * @param text
     * @return
     */
    public boolean containsAny(String text) {
        ReadStreamBase streamBase = MemoryCache.getInstance().TranslateSearch.replace(text, false);
        IllegalWordsFindAllResult result = TextFilterHelper.findAll(streamBase);
        return !result.getRiskLevel().equals(IllegalWordsRiskLevel.Pass);
    }

    public static void main(String[] args) {
        SensitiveWordUtil_1 sensitiveWordUtil_1 = new SensitiveWordUtil_1();
        sensitiveWordUtil_1.initTextFilterData();
        String[] strList = {"加我5588774112", "毛泽东", "哈哈哈哈大傻子"};
        for (String str : strList) {
            System.out.println("==========================================================================================");
            TextFilterResult all = sensitiveWordUtil_1.findAll(str);
            System.out.println("文本内容：【" + str + "】。" + all);
        }
    }
}
