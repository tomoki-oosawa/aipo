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

dojo.provide("aipo.message");

aipo.message.init = function() {
    aipo.message.reloadRoomList();
}

aipo.message.reloadRoomList = function() {
    var content = dijit.byId("messageRoomPane");
    if (!content) {
        content = new aimluck.widget.Contentpane({}, 'messageRoomListPane');
    }

    content.viewPage("?template=MessageRoomListScreen");
}

aipo.message.swapView = function() {
    if(dojo.hasClass("dd_message","open")) {
        dojo.byId("portletsBody").style.display="none";
        aipo.message.fixMessageWindow();
   } else {
       dojo.byId("portletsBody").style.display="";
   }
}

aipo.message.fixMessageWindow = function() {
    if(dojo.byId("dd_message") != null) {
        var minusH = 55 + 40 + 45;
        var w = document.documentElement.clientWidth - 20;
        var h = document.documentElement.clientHeight - minusH + 45;
        var tabh = document.documentElement.clientHeight - minusH;
        dojo.byId("dd_message").style.width = w + "px";
        if(dojo.byId("messageSideBlock") != null) {
            dojo.byId("messageSideBlock") .style.height = h + "px";
        }
        if(dojo.byId("messageSideBlock1") != null) {
            dojo.byId("messageSideBlock1") .style.height = h + "px";
        }
        if(dojo.byId("messageSideBlock2") != null) {
            dojo.byId("messageSideBlock2") .style.height = h + "px";
        }
    }
};

dojo.addOnLoad(function() {
    dojo.connect(window, "onresize", null, function(e) { aipo.message.fixMessageWindow(); });
});