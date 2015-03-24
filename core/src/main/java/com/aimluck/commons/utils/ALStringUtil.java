/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.commons.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aimluck.commons.field.ALNumberField;

/**
 * 入力フィールドで取り扱う文字列に対するユーティリティクラスです。 <br />
 * 
 */
public class ALStringUtil {

  /** 半角カナの開始コード */
  public static final int HANKAKU_KANA_FIRST = 0xff61;

  /** 半角カナの終了コード */
  public static final int HANKAKU_KANA_LAST = 0xff9f;

  /** 濁音 */
  private static final String DAKUON = "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ";

  /** 清音 */
  private static final String SEION = "かきくけこさしすせそたちつてとはひふへほはひふへほ";

  /**
   * メールアドレス形式であるかを判定します。
   * 
   * @param argStr
   *          チェック対象文字列
   * @return メールアドレス形式であればtrue、それ以外はfalse。
   */
  public static boolean isMailAddress(String str) {
    Pattern mailPattern =
      Pattern.compile(
        "[\\w\\.\\-\\+]+@([\\w\\-]+\\.)+[\\w\\-]+",
        Pattern.CASE_INSENSITIVE);
    Matcher objMch = mailPattern.matcher(str);
    return objMch.matches();
  }

  /**
   * 携帯電話のメールアドレス形式であるかを判定します。
   * 
   * @param argStr
   *          チェック対象文字列
   * @return メールアドレス形式であればtrue、それ以外はfalse。
   */
  public static boolean isCellPhoneMailAddress(String str) {
    Pattern mailPattern =
      Pattern.compile(
        "[\\w\\.\\-\\_\\+\\p{Punct}]+@([\\w\\-]+\\.)+[\\w\\-]+",
        Pattern.CASE_INSENSITIVE);
    Matcher objMch = mailPattern.matcher(str);
    return objMch.matches();
  }

  /**
   * 指定文字が半角カナかどうかを判定します。
   * 
   * @param chr
   *          チェック対象文字
   * @return 半角カナならば true,それ以外は false
   */
  public static boolean isHankakuKana(char chr) {
    return (chr >= HANKAKU_KANA_FIRST && chr <= HANKAKU_KANA_LAST);
  }

  /**
   * 指定文字列が半角数字のみかをどうかを判定します。
   * 
   * @param String
   *          str チェックする文字列
   * @return 半角数字のみであればtrue、それ以外はfalse。
   */
  public static boolean isNumber(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) < 0x0030 || str.charAt(i) > 0x0039) {
        return false;
      }
    }
    return true;
  }

  /**
   * 指定文字列に含まれるひらがなをカタカナに変換します。
   * 
   * @param str
   * @return
   */
  public static String convertHiragana2Katakana(String str) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      char code = str.charAt(i);
      if ((code >= 0x3041) && (code <= 0x3093)) {
        buf.append((char) (code + 0x60));
      } else {
        buf.append(code);
      }
    }
    return buf.toString();
  }

  public static String convertKatakana2Hiragana(String str) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      char code = str.charAt(i);
      if ((code >= 0x30a1) && (code <= 0x30f3)) {
        buf.append((char) (code - 0x60));
      } else {
        buf.append(code);
      }
    }
    return buf.toString();
  }

  /**
   * 指定文字列に含まれる半角カナを全角カナに変換します。
   * 
   * @param str
   * @return
   */
  public static String convertH2ZKana(String str) {
    if (str == null) {
      return null;
    }

    return convertH2ZKana(str, 0, str.length());
  }

  /**
   * 指定文字列に含まれる半角カナを全角カナに変換します。
   * 
   * @param str
   * @param pos
   * @param length
   * @return
   */
  public static String convertH2ZKana(String str, int pos, int length) {
    char[][][] table = ALKanaMapTable.TABLE_HANKAKU2ZENKAKU;
    Comparator<Object> comp = new ALHankakuComparator<Object>();

    int retIdx = 0;
    int maxPos = pos + length;
    char[] base = new char[1];
    char[] daku = new char[2];
    char[] nChars = str.toCharArray();
    char[] nRets = new char[length];

    while (pos < maxPos) {
      char currChar = nChars[pos++];
      if (!isHankakuKana(currChar)) {
        nRets[retIdx++] = currChar;
        continue;
      }
      char[] res;
      if (pos < maxPos) {
        char nextChar = nChars[pos];
        if (nextChar == '゛' || nextChar == '゜') {
          pos++;
          daku[ALKanaMapTable.INDEX_HANKAKU_BASE] = currChar;
          daku[ALKanaMapTable.INDEX_HANKAKU_DAKUTEN] = nextChar;
          res = convertH2ZKanaChar(daku, table, comp);
          retIdx += putCharactors(nRets, res, retIdx);
          continue;
        }
      }

      base[0] = currChar;
      res = convertH2ZKanaChar(base, table, comp);
      retIdx += putCharactors(nRets, res, retIdx);
    }
    return new String(nRets, 0, retIdx);
  }

  /**
   * HTML文字列におけるメタ文字を置き換え、無害化します。
   * 
   * @param argStr
   *          メタ文字列
   * @return 変換後文字列
   */
  public static String sanitizing(String str) {
    if (str == null) {
      return "";
    }

    StringBuffer buff = new StringBuffer();

    int len = str.length();
    for (int i = 0; i < len; i++) {
      char originalCharacter = str.charAt(i);
      switch (originalCharacter) {
        case '<':
          buff.append("&lt;");
          break;
        case '>':
          buff.append("&gt;");
          break;
        case '\'':
          buff.append("&#39;");
          break;
        case '\"':
          buff.append("&quot;");
          break;
        case '&':
          buff.append("&amp;");
          break;
        default:
          buff.append(originalCharacter);
      }
    }
    return buff.toString();
  }

  /**
   * 無害化(サニタイジング)された文字列を復元します。
   * 
   * @param str
   *          サニタイジングされた文字を含む文字列
   * @return サニタイジング前文字列
   */
  public static String unsanitizing(String str) {
    if (str == null) {
      return "";
    }

    StringBuffer buff = new StringBuffer();
    int len = str.length();

    for (int i = 0; i < len; i++) {
      char originalChar = str.charAt(i);
      if (originalChar != '&') {
        buff.append(originalChar);
        continue;
      }
      if (len > i + 5) {
        if ("&lt;".equals(str.substring(i, i + 4))) {
          buff.append('<');
          i = i + 3;
        } else if ("&gt;".equals(str.substring(i, i + 4))) {
          buff.append('>');
          i = i + 3;
        } else if ("&#39;".equals(str.substring(i, i + 5))) {
          buff.append('\'');
          i = i + 4;
        } else if ("&amp;".equals(str.substring(i, i + 5))) {
          buff.append('&');
          i = i + 4;
        } else if ("&quot;".equals(str.substring(i, i + 6))) {
          buff.append('\"');
          i = i + 5;
        } else {
          buff.append(originalChar);
          continue;
        }
      } else if (i + 4 == len) {
        if ("&lt;".equals(str.substring(i))) {
          buff.append('<');
        } else if ("&gt;".equals(str.substring(i))) {
          buff.append('>');
        } else {
          buff.append(str.substring(i));
          break;
        }
        i = i + 3;
      } else if (i + 5 == len) {
        if ("&#39;".equals(str.substring(i))) {
          buff.append('\'');
        } else if ("&amp;".equals(str.substring(i))) {
          buff.append('&');
        } else {
          buff.append(str.substring(i));
          break;
        }
        i = i + 4;
      } else if (i + 6 == len) {
        if ("&quot;".equals(str.substring(i))) {
          buff.append('\"');
        } else {
          buff.append(str.substring(i));
          break;
        }
        i = i + 5;
      } else {
        buff.append('&');
      }
    }
    return buff.toString();
  }

  private static int putCharactors(char[] chars, char[] newchars, int inpos) {
    int pos = inpos;
    for (int i = 0; i < newchars.length; i++) {
      if (newchars[i] != (char) 0) {
        chars[pos++] = newchars[i];
      }
    }
    return pos - inpos;
  }

  private static char[] convertH2ZKanaChar(char[] chars, char[][][] mapTable,
      Comparator<Object> comparator) {
    int index = Arrays.binarySearch((Object[]) mapTable, chars, comparator);

    if (index >= 0) {
      return mapTable[index][ALKanaMapTable.INDEX_ZENKAKU];
    } else if (chars.length == 1) {
      return chars;
    }

    int len = chars.length;
    for (int i = 0; i < len; i++) {
      if (chars[i] == (char) 0) {
        continue;
      }

      char[] atom = new char[] { chars[i] };
      chars[i] = convertH2ZKanaChar(atom, mapTable, comparator)[0];
    }
    return chars;
  }

  /**
   * 指定した文字が記号であるかを返します
   * 
   * @param ch
   * @return
   */
  public static boolean isSymbol(char ch) {
    byte[] chars;
    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }
    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }
  }

  public static String toTwoDigitString(ALNumberField num) {
    if (num != null) {
      return String.format("%02d", num.getValue());
    }
    return "";
  }

  public static String convertDakuon2Seion(String str) {
    int size = DAKUON.length();
    String result = str;
    for (int i = 0; i < size; i++) {
      String s1 = DAKUON.substring(i, i + 1);
      String s2 = SEION.substring(i, i + 1);
      result = result.replaceAll(s1, s2);
    }
    return result;
  }
}
