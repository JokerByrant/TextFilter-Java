package service;


import pojo.ContactResult;
import pojo.TempWordsResultItem;
import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author sxh
 * @date 2023/3/3
 */
public interface IContactSearch {
    int[] GetContactDict();

    List<ContactResult> findAll(List<TempWordsResultItem> txt);

    void load(CSharpDataInputStream dis) throws IOException;
}
