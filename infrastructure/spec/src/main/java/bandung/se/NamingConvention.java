package bandung.se;

import java.util.Objects;

/**
 * various name convention
 *
 * @author iMinusMinus
 * @date 2024-10-20
 */
public enum NamingConvention {

    LOWER_CAMEL_CASE(false, true, false, true, NamingConvention.NUL),
    PASCAL_CASE(true, true, false, true, NamingConvention.NUL),
    SNAKE_CASE(false, false, false, true, '_'),
    KEBAB_CASE(false, false, false, true, '-'),
    ;

    NamingConvention(boolean upperFirstWordOfCompoundWord, boolean upperFirstLetterWord,
                             boolean upper, boolean lower, char separator) {
        this.upperFirstWordOfCompoundWord = upperFirstWordOfCompoundWord;
        this.upperFirstLetterWord = upperFirstLetterWord;
        this.upper = upper;
        this.lower = lower;
        this.separator = separator;
    }

    private static final char NUL = 0x00;

    private final boolean upperFirstWordOfCompoundWord;

    private final boolean upperFirstLetterWord;

    private final boolean upper;

    private final boolean lower;

    private final char separator;

    protected boolean isWordSeparator(char c) {
        return  separator == NUL ? Character.isUpperCase(c) : c == separator;
    }

    public String translate(String original, NamingConvention namingConvention) {
        if (original == null) {
            return null;
        }
        Objects.requireNonNull(namingConvention);
        char[] cs = original.toCharArray();
        char[] ns = new char[cs.length + 6]; // 一般组合的单词个数较少
        int offset = 0;
        ns[offset++] = namingConvention.upperFirstWordOfCompoundWord ?
                Character.toUpperCase(cs[0]) :
                Character.toLowerCase(cs[0]);
        boolean firstLetter = false;
        for (int i = 1; i < cs.length; i++) {
            if (offset >= cs.length - 2) {
                ns = grow(ns, cs.length - i);
            }
            if (isWordSeparator(cs[i])) {
                if (separator == NUL && namingConvention.separator == NUL) {
                    ns[offset++] = cs[i];
                    continue;
                } else if (namingConvention.separator != NUL) {
                    firstLetter = true;
                    ns[offset++] = namingConvention.separator; // add separator
                }
                if (separator != NUL) { // treat as replace separator
                    firstLetter = true;
                    continue;
                }
            }
            if (upper || (firstLetter && namingConvention.upperFirstLetterWord)) {
                ns[offset++] = Character.toUpperCase(cs[i]);
            } else if (lower) {
                ns[offset++] = Character.toLowerCase(cs[i]);
            } else {
                ns[offset++] = cs[i];
            }
            firstLetter = false;
        }
        char[] str = new char[offset];
        System.arraycopy(ns, 0, str, 0, offset);
        return new String(str);
    }

    private char[] grow(char[] cs, int left) {
        int expectSize = (int) (cs.length + left * 1.2); // 统计上单词长度集中在3、4、5、6个字符
        char[] ns = new char[expectSize + 1];
        System.arraycopy(cs, 0, ns, 0, cs.length);
        return ns;
    }
}
