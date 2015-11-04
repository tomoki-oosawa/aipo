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
aimluck.namespace("utils.form");

aimluck.utils.form.createSelect = function(selectid, divid, url, key, value, sel, pre, att) {
    dojo.xhrGet({
        url: url,
        timeout: 5000,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        headers: { X_REQUESTED_WITH: "XMLHttpRequest" }, 
        load: function (respodatanse, ioArgs){
            var html = "";
            if (typeof att == "undefined") {
                html += '<select name="'+ selectid + '">';
            } else {
                html += '<select name="'+ selectid + '" ' + att + '/>';
            }
            if (typeof pre == "undefined") {
                html += '';
            } else {
                html += pre;
            }
            dojo.forEach(respodatanse, function(p) {
                if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
                } else {
                    if (p[key] == sel) {
                        html += "<option value='"+p[key]+"' selected='selected'>"+ p[value]+"</option>";
                    } else {
                        html += "<option value='"+p[key]+"'>"+ p[value]+"</option>";
                    }
                }
            });
            html += '</select>';
            dojo.byId(divid).innerHTML = html;
        }
    });
};

aimluck.utils.form.switchDisplay = function (viewId, hideId) {
    dojo.html.setDisplay(dojo.byId(hideId),"none");
    dojo.html.setDisplay(dojo.byId(viewId),"");
}
