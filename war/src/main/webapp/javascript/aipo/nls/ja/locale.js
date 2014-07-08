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

{
	// '\u3053\u306e'+button.form.name+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f'
	DW_STR:"${dw_this}${dw_name}\u3092${dw_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	DW_DEL:"\u524a\u9664",
	DW_THIS:"\u3053\u306e",
	// '\u9078\u629e\u3057\u305f'+button.form.name+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f'
	DWS_STR:"${dws_sel}${dws_name}\u3092${dws_del}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	DWS_DEL:"\u524a\u9664",
	DWS_SEL:"\u9078\u629e\u3057\u305f",
	// "\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002"
	VERIFYCB_STR:"${verifycb_cb}\u3092${verifycb_gt_one}${verifycb_sel}\u3057\u3066\u304f\u3060\u3055\u3044\u3002",
	VERIFYCB_SEL:"\u9078\u629e",
	VERIFYCB_GT_ONE:"\uff11\u3064\u4ee5\u4e0a",
	VERIFYCB_CB:"\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9",
	// \u3053\u306e'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f
	ENABLESUBMIT_THIS:"\u3053\u306e",
	ENABLESUBMIT_ENABLE:"\u6709\u52b9\u5316",
	ENABLESUBMIT_STR:"${enableSubmit_this}${enableSubmit_name}\u3092${enableSubmit_enable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// \u3053\u306e'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f
	DISABLESUBMIT_THIS:"\u3053\u306e",
	DISABLESUBMIT_DISABLE:"\u7121\u52b9\u5316",
	DISABLESUBMIT_STR:"${disableSubmit_this}${disableSubmit_name}\u3092${disableSubmit_disable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// \u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f
	MULTIENABLESUBMIT_SEL:"\u9078\u629e\u3057\u305f",
	MULTIENABLESUBMIT_ENABLE:"\u6709\u52b9\u5316",
	MULTIENABLESUBMIT_STR:"${multiEnableSubmit_sel}${multiEnableSubmit_name}\u3092${multiEnableSubmit_enable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// \u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f
	MULTIDISABLESUBMIT_SEL:"\u9078\u629e\u3057\u305f",
	MULTIDISABLESUBMIT_DISABLE:"\u7121\u52b9\u5316",
	MULTIDISABLESUBMIT_STR:"${multiDisableSubmit_sel}${multiDisableSubmit_name}\u3092${multiDisableSubmit_disable}\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f",
	// "[\u30a8\u30e9\u30fc] \u8aad\u307f\u8fbc\u307f\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002"
	XHRERROR_STR:"[${xhrError_error}] ${xhrError_loading}\u304c${xhrError_failed}\u3002",
	XHRERROR_ERROR:"\u30a8\u30e9\u30fc",
	XHRERROR_LOADING:"\u8aad\u307f\u8fbc\u307f",
	XHRERROR_FAILED:"\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f",
	// "[\u30a8\u30e9\u30fc] \u30bf\u30a4\u30e0\u30a2\u30a6\u30c8\u3057\u307e\u3057\u305f\u3002"
	XHRTIMEOUT_STR: "[${xhrTimeout_error}] ${xhrTimeout_timeout}\u3057\u307e\u3057\u305f\u3002",
	XHRTIMEOUT_ERROR:"\u30a8\u30e9\u30fc",
	XHRTIMEOUT_TIMEOUT:"\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8",
	// \u5e74\u6708\u65e5\u8868\u793a tyear+"\u5e74"+tmonth+"\u6708"+tdate+"\u65e5\uff08"+tday+"\uff09"
	DATE_FORMAT:"${tyear}\u5e74${tmonth}\u6708${tdate}\u65e5(${tday})",
	DISABLED_DATE:"---- \u5e74 -- \u6708 -- \u65e5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",
	// \u8aad\u307f\u8fbc\u307f\u4e2d...
	LOADING_STR:"\u8aad\u307f\u8fbc\u307f\u4e2d...",
	// \u524a\u9664
	DELETE_STR:"\u524a\u9664",
    //\u6307\u5b9a\u3057\u306a\u3044
	NOT_SPECIFIED_STR:" \u6307\u5b9a\u3057\u306a\u3044",
	//\uff1c \u8ffd\u52a0
	ADDBTN_STR:"\uff1c \u8ffd\u52a0",
	//\u524a\u9664
	DELETEBTN_STR:"\u524a\u9664",
	//"\u4e00\u89a7\u306e\u7dcf\u6570\u304c"+max+"\u4ef6\u3092\u8d85\u3048\u3066\u3044\u307e\u3059\u3002\n\u65e5\u4ed8\u306e\u7bc4\u56f2\u3092\u5909\u66f4\u3057\u3066\u304f\u3060\u3055\u3044\u3002"
	EVENTLOG_STR:"\u4e00\u89a7\u306e\u7dcf\u6570\u304c${max}\u4ef6\u3092\u8d85\u3048\u3066\u3044\u307e\u3059\u3002\n\u65e5\u4ed8\u306e\u7bc4\u56f2\u3092\u5909\u66f4\u3057\u3066\u304f\u3060\u3055\u3044\u3002",
	// \u65b0\u3057\u304f\u5165\u529b\u3059\u308b
	NEW_INPUT_STR:"\u65b0\u3057\u304f\u5165\u529b\u3059\u308b",
	// \u4e00\u89a7\u304b\u3089\u9078\u629e\u3059\u308b
	SELECT_FROM_LIST_STR:"\u4e00\u89a7\u304b\u3089\u9078\u629e\u3059\u308b",
	// \u9589\u3058\u308b
	CLOSE_STR:"\u9589\u3058\u308b",
	// "maxval+\u4ef6\u4e2d+value+\u4ef6"
	NUMBER_STR:"${maxval} \u4ef6\u4e2d ${value} \u4ef6",
	// "\u3044\u3044\u306d\uff01\u3092\u53d6\u308a\u6d88\u3059"
	REMOVE_LIKE_STR:"\u3044\u3044\u306d\uff01\u3092\u53d6\u308a\u6d88\u3059",
	// "\u3044\u3044\u306d\uff01"
	LIKE_STR:"\u3044\u3044\u306d\uff01"
}