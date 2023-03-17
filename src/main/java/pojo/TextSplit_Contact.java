package pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TextSplit_Contact {
    private final int len;
    private final int end;
    private final ContactResult[] MinWords;
    private final List<ContactResult>[] NextWords;

    public TextSplit_Contact(int textLength) {
        end = textLength;
        len = textLength + 1;
        MinWords = new ContactResult[len];
        NextWords = new List[len];
    }

    public void AddWords(ContactResult context) {
        List<ContactResult> nextWords = NextWords[context.start];
        if (nextWords == null) {
            nextWords = new ArrayList<>();
            NextWords[context.start] = nextWords;
        }
        nextWords.add(context);
    }

    public void Calculation() {
        int[] MinLength = new int[len];
        int[] MaxCount = new int[len];

        MinLength[0] = 1;
        for (int i = 0; i <= end; i++) {
            int minLength = MinLength[i];
            if (minLength == 0) {
                continue;
            }
            minLength++;
            List<ContactResult> nextWords = NextWords[i];
            if (nextWords != null) {
                int count = MaxCount[i];
                for (int j = 0; j < nextWords.size(); j++) {
                    ContactResult next = nextWords.get(j);

                    int endCharIndex = next.end + 1;
                    int endMinLength = MinLength[endCharIndex];
                    int endCount = count + 1;

                    if ((endMinLength == 0) || (endMinLength > minLength) || ((endMinLength == minLength)
                            && (MaxCount[endCharIndex] < endCount))) {
                        MinLength[endCharIndex] = minLength;
                        MaxCount[endCharIndex] = endCount;
                        MinWords[endCharIndex] = next;
                    }
                }
            }
            if (i < end && MinLength[i + 1] == 0) {
                MinLength[i + 1] = minLength;
                MaxCount[i + 1] = MaxCount[i] + 1;
            }
        }

        MinLength = null;
        MaxCount = null;
    }

    public List<ContactResult> GetIllegalWords() {
        Stack<ContactResult> temp = new Stack<>();
        int end = this.end;
        while (end != 0) {
            ContactResult words = MinWords[end];
            if (words == null || !words.isSet) {
                end--;
            } else {
                temp.push(words);
                end = words.start;
            }
        }
        int len = temp.size();
        List<ContactResult> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            result.add(temp.pop());
        }
        temp = null;
        return result;
    }
}
