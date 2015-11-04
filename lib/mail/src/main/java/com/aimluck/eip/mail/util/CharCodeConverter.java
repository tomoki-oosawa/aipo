/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.mail.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 文字関係のコンバータ用クラスです。 <br />
 * 
 */
public class CharCodeConverter {

  public static final byte[] SJIS_KANA;

  static {
    try {
      // 全角への変換テーブル
      SJIS_KANA =
        "。「」、・ヲァィゥェォャュョッーアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワン゛゜"
          .getBytes("Shift_JIS");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("CANT HAPPEN");
    }
  }

  /**
   * Shift_JIS エンコーディングスキームに基づくバイト列を ISO-2022-JP エンコーディングスキームに変換する．
   * 「半角カナ」は対応する全角文字に変換する．
   */
  public static byte[] sjisToJis(byte[] sjisBytes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean nonAscii = false;
    int len = sjisBytes.length;
    for (int i = 0; i < len; i++) {
      if (sjisBytes[i] >= 0) {
        if (nonAscii) {
          nonAscii = false;
          out.write(0x1b);
          out.write('(');
          out.write('B');
        }
        out.write(sjisBytes[i]);
      } else {
        if (!nonAscii) {
          nonAscii = true;
          out.write(0x1b);
          out.write('$');
          out.write('B');
        }
        int b = sjisBytes[i] & 0xff;
        if (b >= 0xa1 && b <= 0xdf) {
          // 半角カナは全角に変換
          int kanaIndex = (b - 0xA1) * 2;
          sjisToJis(out, SJIS_KANA[kanaIndex], SJIS_KANA[kanaIndex + 1]);
        } else {
          i++;
          if (i == len) {
            break;
          }
          sjisToJis(out, sjisBytes[i - 1], sjisBytes[i]);
        }
      }
    }
    if (nonAscii) {
      out.write(0x1b);
      out.write('(');
      out.write('B');
    }
    return out.toByteArray();
  }

  /**
   * １文字の２バイト Shift_JIS コードを JIS コードに変換して書き出す．
   */
  private static void sjisToJis(ByteArrayOutputStream out, byte bh, byte bl) {
    int h = (bh << 1) & 0xFF;
    int l = bl & 0xFF;
    if (l < 0x9F) {
      if (h < 0x3F) {
        h += 0x1F;
      } else {
        h -= 0x61;
      }
      if (l > 0x7E) {
        l -= 0x20;
      } else {
        l -= 0x1F;
      }
    } else {
      if (h < 0x3F) {
        h += 0x20;
      } else {
        h -= 0x60;
      }
      l -= 0x7E;
    }
    out.write(h);
    out.write(l);
  }
}
