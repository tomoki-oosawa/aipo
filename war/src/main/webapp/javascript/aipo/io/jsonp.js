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
dojo.provide("aipo.io");

dojo.require("dojo.string");
dojo.requireLocalization("aipo", "locale");

aipo.io.loadHtml = function(url, params, portletId) {
	dojo.xhrGet({
		url : url,
		transport : "ScriptSrcTransport",
		jsonParamName : "callback",
		content : params,
		method : "get",
		mimetype : "application/json",
		encoding : "utf-8",
		load : function(type, data, event, args) {
			dojo.byId('content-' + portletId).innerHTML = data.body;
			dojo.html.setVisibility(dojo.byId('content-' + portletId), true);
			dojo.html.setDisplay(dojo.byId('indicator-' + portletId), false);
		},
		error : function(type, data, event, args) {
			var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
			var errorString = dojo.string.substitute(nlsStrings.XHRERROR_STR, {
				xhrError_error : nlsStrings.XHRERROR_ERROR,
				xhrError_loading : nlsStrings.XHRERROR_LOADING,
				xhrError_failed : nlsStrings.XHRERROR_FAILED
			});
			// "[エラー] 読み込みができませんでした。"
			dojo.byId('content-' + portletId).innerHTML = errorString;
			dojo.html.setVisibility(dojo.byId('content-' + portletId), true);
			dojo.html.setDisplay(dojo.byId('indicator-' + portletId), false);
		},
		timeout : function(type, data, event, args) {
			var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
			var errorString = dojo.string.substitute(nlsStrings.XHRTIMEOUT_STR,
					{
						xhrTimeout_error : nlsStrings.XHRTIMEOUT_ERROR,
						xhrTimeout_timeout : nlsStrings.XHRTIMEOUT_TIMEOUT
					});
			// "[エラー] タイムアウトしました。"
			dojo.byId('content-' + portletId).innerHTML = errorString;
			dojo.html.setVisibility(dojo.byId('content-' + portletId), true);
			dojo.html.setDisplay(dojo.byId('indicator-' + portletId), false);
		},
		timeoutSeconds : 10
	});
}
