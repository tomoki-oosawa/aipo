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
    { { '。' }, { '。' } },
    { { '「' }, { '「' } },
    { { '」' }, { '」' } },
    { { '、' }, { '、' } },
    { { '・' }, { '・' } },
    { { 'ヲ' }, { 'ヲ' } },
    { { 'ァ' }, { 'ァ' } },
    { { 'ィ' }, { 'ィ' } },
    { { 'ゥ' }, { 'ゥ' } },
    { { 'ェ' }, { 'ェ' } },
    { { 'ォ' }, { 'ォ' } },
    { { 'ャ' }, { 'ャ' } },
    { { 'ュ' }, { 'ュ' } },
    { { 'ョ' }, { 'ョ' } },
    { { 'ッ' }, { 'ッ' } },
    { { 'ー' }, { 'ー' } },
    { { 'ア' }, { 'ア' } },
    { { 'イ' }, { 'イ' } },
    { { 'ウ' }, { 'ウ' } },
    { { 'ウ', '゛' }, { 'ヴ' } },
    { { 'エ' }, { 'エ' } },
    { { 'オ' }, { 'オ' } },
    { { 'カ' }, { 'カ' } },
    { { 'カ', '゛' }, { 'ガ' } },
    { { 'キ' }, { 'キ' } },
    { { 'キ', '゛' }, { 'ギ' } },
    { { 'ク' }, { 'ク' } },
    { { 'ク', '゛' }, { 'グ' } },
    { { 'ケ' }, { 'ケ' } },
    { { 'ケ', '゛' }, { 'ゲ' } },
    { { 'コ' }, { 'コ' } },
    { { 'コ', '゛' }, { 'ゴ' } },
    { { 'サ' }, { 'サ' } },
    { { 'サ', '゛' }, { 'ザ' } },
    { { 'シ' }, { 'シ' } },
    { { 'シ', '゛' }, { 'ジ' } },
    { { 'ス' }, { 'ス' } },
    { { 'ス', '゛' }, { 'ズ' } },
    { { 'セ' }, { 'セ' } },
    { { 'セ', '゛' }, { 'ゼ' } },
    { { 'ソ' }, { 'ソ' } },
    { { 'ソ', '゛' }, { 'ゾ' } },
    { { 'タ' }, { 'タ' } },
    { { 'タ', '゛' }, { 'ダ' } },
    { { 'チ' }, { 'チ' } },
    { { 'チ', '゛' }, { 'ヂ' } },
    { { 'ツ' }, { 'ツ' } },
    { { 'ツ', '゛' }, { 'ヅ' } },
    { { 'テ' }, { 'テ' } },
    { { 'テ', '゛' }, { 'デ' } },
    { { 'ト' }, { 'ト' } },
    { { 'ト', '゛' }, { 'ド' } },
    { { 'ナ' }, { 'ナ' } },
    { { 'ニ' }, { 'ニ' } },
    { { 'ヌ' }, { 'ヌ' } },
    { { 'ネ' }, { 'ネ' } },
    { { 'ノ' }, { 'ノ' } },
    { { 'ハ' }, { 'ハ' } },
    { { 'ハ', '゛' }, { 'バ' } },
    { { 'ハ', '゜' }, { 'パ' } },
    { { 'ヒ' }, { 'ヒ' } },
    { { 'ヒ', '゛' }, { 'ビ' } },
    { { 'ヒ', '゜' }, { 'ピ' } },
    { { 'フ' }, { 'フ' } },
    { { 'フ', '゛' }, { 'ブ' } },
    { { 'フ', '゜' }, { 'プ' } },
    { { 'ヘ' }, { 'ヘ' } },
    { { 'ヘ', '゛' }, { 'ベ' } },
    { { 'ヘ', '゜' }, { 'ペ' } },
    { { 'ホ' }, { 'ホ' } },
    { { 'ホ', '゛' }, { 'ボ' } },
    { { 'ホ', '゜' }, { 'ポ' } },
    { { 'マ' }, { 'マ' } },
    { { 'ミ' }, { 'ミ' } },
    { { 'ム' }, { 'ム' } },
    { { 'メ' }, { 'メ' } },
    { { 'モ' }, { 'モ' } },
    { { 'ヤ' }, { 'ヤ' } },
    { { 'ユ' }, { 'ユ' } },
    { { 'ヨ' }, { 'ヨ' } },
    { { 'ラ' }, { 'ラ' } },
    { { 'リ' }, { 'リ' } },
    { { 'ル' }, { 'ル' } },
    { { 'レ' }, { 'レ' } },
    { { 'ロ' }, { 'ロ' } },
    { { 'ワ' }, { 'ワ' } },
    { { 'ン' }, { 'ン' } },
    { { '゛' }, { '゛' } },
    { { '゜' }, { '゜' } } };

  /**
   * コンストラクタ
   * 
   */
  private ALKanaMapTable() {
  }

}
