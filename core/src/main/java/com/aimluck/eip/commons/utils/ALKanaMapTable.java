/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

/**
 * 半角と全角の変換テーブルクラスです。 <br />
 * 
 */
public class ALKanaMapTable {

  /** インデックス（半角） */
  public static final int INDEX_HANKAKU = 0;

  /** インデックス（全角） */
  public static final int INDEX_ZENKAKU = 1;

  /** インデックス（半角濁点なし） */
  public static final int INDEX_HANKAKU_BASE = 0;

  /** インデックス（半角濁点） */
  public static final int INDEX_HANKAKU_DAKUTEN = 1;

  /** 半角と全角の変換テーブル */
  static final char[][][] TABLE_HANKAKU2ZENKAKU = new char[][][] {
    { { '｡' }, { '。' } },
    { { '｢' }, { '「' } },
    { { '｣' }, { '」' } },
    { { '､' }, { '、' } },
    { { '･' }, { '・' } },
    { { 'ｦ' }, { 'ヲ' } },
    { { 'ｧ' }, { 'ァ' } },
    { { 'ｨ' }, { 'ィ' } },
    { { 'ｩ' }, { 'ゥ' } },
    { { 'ｪ' }, { 'ェ' } },
    { { 'ｫ' }, { 'ォ' } },
    { { 'ｬ' }, { 'ャ' } },
    { { 'ｭ' }, { 'ュ' } },
    { { 'ｮ' }, { 'ョ' } },
    { { 'ｯ' }, { 'ッ' } },
    { { 'ｰ' }, { 'ー' } },
    { { 'ｱ' }, { 'ア' } },
    { { 'ｲ' }, { 'イ' } },
    { { 'ｳ' }, { 'ウ' } },
    { { 'ｳ', 'ﾞ' }, { 'ヴ' } },
    { { 'ｴ' }, { 'エ' } },
    { { 'ｵ' }, { 'オ' } },
    { { 'ｶ' }, { 'カ' } },
    { { 'ｶ', 'ﾞ' }, { 'ガ' } },
    { { 'ｷ' }, { 'キ' } },
    { { 'ｷ', 'ﾞ' }, { 'ギ' } },
    { { 'ｸ' }, { 'ク' } },
    { { 'ｸ', 'ﾞ' }, { 'グ' } },
    { { 'ｹ' }, { 'ケ' } },
    { { 'ｹ', 'ﾞ' }, { 'ゲ' } },
    { { 'ｺ' }, { 'コ' } },
    { { 'ｺ', 'ﾞ' }, { 'ゴ' } },
    { { 'ｻ' }, { 'サ' } },
    { { 'ｻ', 'ﾞ' }, { 'ザ' } },
    { { 'ｼ' }, { 'シ' } },
    { { 'ｼ', 'ﾞ' }, { 'ジ' } },
    { { 'ｽ' }, { 'ス' } },
    { { 'ｽ', 'ﾞ' }, { 'ズ' } },
    { { 'ｾ' }, { 'セ' } },
    { { 'ｾ', 'ﾞ' }, { 'ゼ' } },
    { { 'ｿ' }, { 'ソ' } },
    { { 'ｿ', 'ﾞ' }, { 'ゾ' } },
    { { 'ﾀ' }, { 'タ' } },
    { { 'ﾀ', 'ﾞ' }, { 'ダ' } },
    { { 'ﾁ' }, { 'チ' } },
    { { 'ﾁ', 'ﾞ' }, { 'ヂ' } },
    { { 'ﾂ' }, { 'ツ' } },
    { { 'ﾂ', 'ﾞ' }, { 'ヅ' } },
    { { 'ﾃ' }, { 'テ' } },
    { { 'ﾃ', 'ﾞ' }, { 'デ' } },
    { { 'ﾄ' }, { 'ト' } },
    { { 'ﾄ', 'ﾞ' }, { 'ド' } },
    { { 'ﾅ' }, { 'ナ' } },
    { { 'ﾆ' }, { 'ニ' } },
    { { 'ﾇ' }, { 'ヌ' } },
    { { 'ﾈ' }, { 'ネ' } },
    { { 'ﾉ' }, { 'ノ' } },
    { { 'ﾊ' }, { 'ハ' } },
    { { 'ﾊ', 'ﾞ' }, { 'バ' } },
    { { 'ﾊ', 'ﾟ' }, { 'パ' } },
    { { 'ﾋ' }, { 'ヒ' } },
    { { 'ﾋ', 'ﾞ' }, { 'ビ' } },
    { { 'ﾋ', 'ﾟ' }, { 'ピ' } },
    { { 'ﾌ' }, { 'フ' } },
    { { 'ﾌ', 'ﾞ' }, { 'ブ' } },
    { { 'ﾌ', 'ﾟ' }, { 'プ' } },
    { { 'ﾍ' }, { 'ヘ' } },
    { { 'ﾍ', 'ﾞ' }, { 'ベ' } },
    { { 'ﾍ', 'ﾟ' }, { 'ペ' } },
    { { 'ﾎ' }, { 'ホ' } },
    { { 'ﾎ', 'ﾞ' }, { 'ボ' } },
    { { 'ﾎ', 'ﾟ' }, { 'ポ' } },
    { { 'ﾏ' }, { 'マ' } },
    { { 'ﾐ' }, { 'ミ' } },
    { { 'ﾑ' }, { 'ム' } },
    { { 'ﾒ' }, { 'メ' } },
    { { 'ﾓ' }, { 'モ' } },
    { { 'ﾔ' }, { 'ヤ' } },
    { { 'ﾕ' }, { 'ユ' } },
    { { 'ﾖ' }, { 'ヨ' } },
    { { 'ﾗ' }, { 'ラ' } },
    { { 'ﾘ' }, { 'リ' } },
    { { 'ﾙ' }, { 'ル' } },
    { { 'ﾚ' }, { 'レ' } },
    { { 'ﾛ' }, { 'ロ' } },
    { { 'ﾜ' }, { 'ワ' } },
    { { 'ﾝ' }, { 'ン' } },
    { { 'ﾞ' }, { '゛' } },
    { { 'ﾟ' }, { '゜' } } };

  /**
   * コンストラクタ
   * 
   */
  private ALKanaMapTable() {
  }

}
