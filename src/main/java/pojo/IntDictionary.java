package pojo;

import struct.CSharpDataInputStream;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sxh
 * @date 2023/3/3
 */
public class IntDictionary {
    private int[] _keys;
    private int[] _values;
    private int last;

    public IntDictionary(int[] keys, int[] values) {
        _keys = keys;
        _values = values;
        last = keys.length - 1;
    }

    public IntDictionary(Map<Integer, Integer> dict) {
        List<Integer> keySet = dict.keySet().stream().sorted().collect(Collectors.toList());
        for(int i = 0; i < keySet.size(); i++) {
            _keys[i] = keySet.get(i);
        }
        int[] values = new int[_keys.length];
        for (int i = 0; i < _keys.length; i++) {
            values[i] = dict.get(_keys[i]);
        }
        _values = values;
        last = _keys.length - 1;
    }

    public Integer tryGetValue(int key) {
        if (last == -1) {
            return null;
        }
        if (_keys[0] == key) {
            return _values[0];
        }
        if (_keys[0] > key) {
            return null;
        }
        if (_keys[last] < key) {
            return null;
        }
        if (_keys[last] == key) {
            return _values[last];
        }

        int left = 1;
        int right = last - 1;

        while (left <= right) {
            int mid = (left + right) >> 1;
            int d = _keys[mid] - key;
            if (d == 0) {
                return _values[mid];
            } else if (d > 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return null;
    }


    public static IntDictionary Load(CSharpDataInputStream dis) throws IOException {
        int len = dis.reverseReadInt();
        if (len == 0) {
            return new IntDictionary(new int[0], new int[0]);
        }
        int[] keys = dis.readShortArray();
        int[] values = dis.readIntArray();
        return new IntDictionary(keys, values);
    }
}
