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

dojo.provide("aipo.customize");

aipo.customize.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(!!arrDialog){
            arrDialog.hide();
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.customize.showMenu = function(portlet_id) {
    var menuNode = dojo.query('#menubar_' + portlet_id);
    if (menuNode.style('display') == 'none') {
        dojo.query('div.menubar').style('display', 'none');
        menuNode.style('display', 'block');
    } else {
        aipo.customize.hideMenu(portlet_id);
    }
}

aipo.customize.hideMenu = function(portlet_id) {
    var menuNode = dojo.query('div.menubar').style('display', 'none');
//    if (menuNode.style('display') == 'block') {
//        menuNode.style('display', 'none');
//    }
}

aipo.customize.setController = function(portlet_id, sender) {
    var controller = sender.parentNode.id;
    dojo.query('form#form' + portlet_id + ' input[name="controller"]')[0].value = controller;

    var tds = dojo.query('form#form' + portlet_id + ' table.controllerTable td');
    var length = tds.length;
    for (var i = 0 ; i < length ; i++) {
    	dojo.removeClass(tds[i], 'selected');
    }

    var td = dojo.query('form#form' + portlet_id + ' td#' + controller)[0];
    dojo.addClass(td, "selected");
}

aipo.customize.deletesubmit = function(url, portlet_id, callback) {
    if (confirm('このアプリを削除してもよろしいですか？')) {
        aipo.customize.submit(url, portlet_id, callback);
    }
}

aipo.customize.submit = function(url, portlet_id, callback) {
    try{
        dojo.xhrPost({
            url: url,
            timeout: 30000,
            content: {portlet_id: portlet_id},
            encoding: "utf-8",
            handleAs: "json-comment-filtered",
            headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
            load: function (response, ioArgs){
                var html = "";
                if(dojo.isArray(response) && response.length > 0) {
                    if(response[0] == "PermissionError"){
                        html += "<ul>";
                        html += "<li><span class='caution'>" + response[1] + "</span></li>";
                        html += "</ul>";
                    }else{
                        html += "<ul>";
                        dojo.forEach(response, function(msg) {
                            html += "<li><span class='caution'>" + msg + "</span></li>";
                        });
                        html += "</ul>";
                    }
                }
                callback.call(callback, html);
                if (html != "") {
                    aimluck.io.disableForm(form, false);
                }
            },
            error: function (error) {
            }
        });
    } catch(E) {
    };
}
