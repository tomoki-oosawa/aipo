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

dojo.provide("aipo.io");

aipo.io.loadHtml = function(url, params, portletId){
    dojo.xhrGet({
        url: url,
        transport: "ScriptSrcTransport",
        jsonParamName: "callback",
        content: params,
        method: "get", 
        mimetype: "application/json",
        encoding: "utf-8",
        load: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = data.body;
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        error: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u8aad\u307f\u8fbc\u307f\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeout: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8\u3057\u307e\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeoutSeconds: 10
    });
}
