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
{
	// 'この'+button.form.name+'を削除してよろしいですか？'
	DW_STR:"${dw_this}${dw_name}\u3092${dw_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	DW_DEL:"\u524a\u9664",
	DW_THIS:"\u3053\u306e",
	// 'この'+button.form._name.value+'を削除してよろしいですか？'+button.form._name.value+'を削除するとメッセージがすべて削除され、参加者は閲覧できなくなります。'
	DWM_STR:"${dwm_this}${dwm_name}\u3092${dwm_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f${dwm_name}\u3092${dwm_del}\u3059\u308b\u3068\u30e1\u30c3\u30bb\u30fc\u30b8\u304c\u3059\u3079\u3066${dwm_del}\u3055\u308c\u3001\u53c2\u52a0\u8005\u306f\u95b2\u89a7\u3067\u304d\u306a\u304f\u306a\u308a\u307e\u3059\u3002",
	DWM_DEL:"\u524a\u9664",
	DWM_THIS:"\u3053\u306e",
	// '選択した'+button.form.name+'を削除してよろしいですか？'
	DWS_STR:"${dws_sel}${dws_name}\u3092${dws_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	DWS_DEL:"\u524a\u9664",
	DWS_SEL:"\u9078\u629e\u3057\u305f",
	// 'すべての'+button.form.name+'を削除してよろしいですか？'
	DWA_STR:"${dwa_sel}${dwa_name}\u3092${dwa_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	DWA_DEL:"\u524a\u9664",
	DWA_SEL:"\u3059\u3079\u3066\u306e",
	// "チェックボックスを１つ以上選択してください。"
	VERIFYCB_STR:"${verifycb_cb}\u3092${verifycb_gt_one}${verifycb_sel}\u3057\u3066\u304f\u3060\u3055\u3044\u3002",
	VERIFYCB_SEL:"\u9078\u629e",
	VERIFYCB_GT_ONE:"\uff11\u3064\u4ee5\u4e0a",
	VERIFYCB_CB:"\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9",
	// この'+button.form._name.value+'を有効化してよろしいですか？
	ENABLESUBMIT_THIS:"\u3053\u306e",
	ENABLESUBMIT_ENABLE:"\u6709\u52b9\u5316",
	ENABLESUBMIT_STR:"${enableSubmit_this}${enableSubmit_name}\u3092${enableSubmit_enable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// この'+button.form._name.value+'を無効化してよろしいですか？
	DISABLESUBMIT_THIS:"\u3053\u306e",
	DISABLESUBMIT_DISABLE:"\u7121\u52b9\u5316",
	DISABLESUBMIT_STR:"${disableSubmit_this}${disableSubmit_name}\u3092${disableSubmit_disable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// 選択した'+button.form._name.value+'を有効化してよろしいですか？
	MULTIENABLESUBMIT_SEL:"\u9078\u629e\u3057\u305f",
	MULTIENABLESUBMIT_ENABLE:"\u6709\u52b9\u5316",
	MULTIENABLESUBMIT_STR:"${multiEnableSubmit_sel}${multiEnableSubmit_name}\u3092${multiEnableSubmit_enable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// 選択した'+button.form._name.value+'を無効化してよろしいですか？
	MULTIDISABLESUBMIT_SEL:"\u9078\u629e\u3057\u305f",
	MULTIDISABLESUBMIT_DISABLE:"\u7121\u52b9\u5316",
	MULTIDISABLESUBMIT_STR:"${multiDisableSubmit_sel}${multiDisableSubmit_name}\u3092${multiDisableSubmit_disable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// "[エラー] 読み込みができませんでした。"
	XHRERROR_STR:"[${xhrError_error}] ${xhrError_loading}\u304c${xhrError_failed}。",
	XHRERROR_ERROR:"\u30a8\u30e9\u30fc",
	XHRERROR_LOADING:"\u8aad\u307f\u8fbc\u307f",
	XHRERROR_FAILED:"\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f",
	// "[エラー] タイムアウトしました。"
	XHRTIMEOUT_STR: "[${xhrTimeout_error}] ${xhrTimeout_timeout}\u3057\u307e\u3057\u305f\u3002",
	XHRTIMEOUT_ERROR:"\u30a8\u30e9\u30fc",
	XHRTIMEOUT_TIMEOUT:"\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8",
	// 年月日表示 tyear+"年"+tmonth+"月"+tdate+"日（"+tday+"）"
	DATE_FORMAT:"${tyear}\u5e74${tmonth}\u6708${tdate}\u65e5\u0028${tday}\u0029",
	DISABLED_DATE:"---- \u5e74 -- \u6708 -- \u65e5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
	// 読み込み中...
	LOADING_STR:"\u8aad\u307f\u8fbc\u307f\u4e2d...",
	// 削除
	DELETE_STR:"\u524a\u9664",
    //指定しない
	NOT_SPECIFIED_STR:"\u0020\u6307\u5b9a\u3057\u306a\u3044",
	//＜ 追加
	ADDBTN_STR:"\uff1c \u8ffd\u52a0",
	//削除
	DELETEBTN_STR:"\u524a\u9664",
	//"一覧の総数が"+max+"件を超えています。\n日付の範囲を変更してください。"
	EVENTLOG_STR:"\u4e00\u89a7\u306e\u7dcf\u6570\u304c${max}\u4ef6\u3092\u8d85\u3048\u3066\u3044\u307e\u3059\u3002\n\u65e5\u4ed8\u306e\u7bc4\u56f2\u3092\u5909\u66f4\u3057\u3066\u304f\u3060\u3055\u3044\u3002",
	// 新しく入力する
	NEW_INPUT_STR:"\u65b0\u3057\u304f\u5165\u529b\u3059\u308b",
	// 一覧から選択する
	SELECT_FROM_LIST_STR:"\u4e00\u89a7\u304b\u3089\u9078\u629e\u3059\u308b",
	// 閉じる
	CLOSE_STR:"\u9589\u3058\u308b"
}