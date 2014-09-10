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

aipo.message.messagePane = null;
aipo.message.reloadMessageList = function() {
    if (!aipo.message.messagePane) {
        aipo.message.messagePane = dijit.byId("messagePane");
        aipo.message.messagePane = new aimluck.widget.Contentpane({},
                'messagePane');
    }

    dojo.byId("messagePane").innerHTML = "";
    aipo.message.messagePane.viewPage("?template=MessageListScreen&r="
            + aipo.message.currentRoomId);
}

aipo.message.messageRoomPane = null;
aipo.message.reloadRoomList = function() {
    if (!aipo.message.messageRoomPane) {
        aipo.message.messageRoomPane = dijit.byId("messageRoomPane");
        aipo.message.messageRoomPane = new aimluck.widget.Contentpane({},
                'messageRoomListPane');
    }

    aipo.message.messageRoomPane.viewPage("?template=MessageRoomListScreen&r="
            + aipo.message.currentRoomId);
}

aipo.message.swapView = function() {
    if (dojo.byId("portletsBody") && dojo.byId("dd_message")) {
        if (dojo.hasClass("dd_message", "open")) {
            dojo.byId("portletsBody").style.display = "none";
            aipo.message.fixMessageWindow();
        } else {
            dojo.byId("portletsBody").style.display = "";
        }
    }
}

aipo.message.inputHistory = {};
aipo.message.currentRoomId = null;
aipo.message.selectRoom = function(room_id) {
    var messageMainBlock = dojo.byId("messageMainBlock");
    var messageMainBlockEmpty = dojo.byId("messageMainBlockEmpty");
    var messageForm = dojo.byId("messageForm");
    var messageRoom = dojo.byId("messageRoom" + room_id);
    var messageRoomAvatar = dojo.byId("messageRoomAvatar");
    var messageRoomName = dojo.byId("messageRoomName");
    if (messageForm && messageRoom) {
        messageMainBlock.style.display="";
        messageMainBlockEmpty.style.display="none";
        aipo.message.inputHistory[aipo.message.currentRoomId] = messageForm.message.value;
        aipo.message.currentRoomId = room_id;
        dojo.query(".messageSummary li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });
        dojo.addClass(messageRoom, "active");
        if (aipo.message.inputHistory[aipo.message.currentRoomId]) {
            messageForm.message.value = aipo.message.inputHistory[aipo.message.currentRoomId];
        } else {
            aipo.message.clearInput();
        }
        messageRoomAvatar.innerHTML = dojo.query("#messageRoom" + room_id + " .avatar")[0].innerHTML;
        messageRoomName.innerHTML = dojo.query("#messageRoom" + room_id + " .name")[0].innerHTML;

        messageForm.roomId.value = aipo.message.currentRoomId;

        aipo.message.reloadMessageList();
    }
}

aipo.message.clearInput = function() {
    var messageForm = dojo.byId("messageForm");
    if (messageForm) {
        messageForm.message.value = "";
    }
}

aipo.message.fixMessageWindow = function() {
    if (dojo.byId("dd_message") != null) {
        var minusH = 55 + 40 + 45;
        var w = document.documentElement.clientWidth - 20;
        var h = document.documentElement.clientHeight - minusH + 45;
        var tabh = document.documentElement.clientHeight - minusH;
        dojo.byId("dd_message").style.width = w + "px";
        if (dojo.byId("messageSideBlock") != null) {
            dojo.byId("messageSideBlock").style.height = h + "px";
        }
        if (dojo.byId("messageSideBlock1") != null) {
            dojo.byId("messageSideBlock1").style.height = h + "px";
        }
        if (dojo.byId("messageSideBlock2") != null) {
            dojo.byId("messageSideBlock2").style.height = h + "px";
        }
    }
    if(dojo.byId("messagePane") != null) {
        var minusH = 55 + 40 + 145;
        var h = document.documentElement.clientHeight - minusH;
        dojo.byId("messagePane").style.height = h + "px";
    }
};

aipo.message.onLoadMessageRoomDialog = function() {

};

aipo.message.shrinkMember = function() {
    var node = dojo.byId("memberFieldButton");
    if (node) {
        var HTML = "";
        HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none;\">";
        var m_t = dojo.byId("member_to");
        if (m_t) {
            var t_o = m_t.options;
            to_size = t_o.length;
            for (i = 0; i < to_size; i++) {
                var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g,
                        "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
                HTML += "<span>" + text + "</span>";
                if (i < to_size - 1) {
                    HTML += ",<wbr/>";
                }
            }
        }
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'
                + aimluck.io.escapeText("message_val_member1")
                + '\" onclick=\"aipo.message.expandMember();\" />'
        HTML += "</td></tr></tbody></table>";
        node.innerHTML = HTML;
    }

    var _node = dojo.byId("memberField");
    if (_node) {
        dojo.style(_node, "display", "none")
    }
    aipo.message.setWrapperHeight();
}

aipo.message.expandMember = function() {
    var node = dojo.byId("memberFieldButton");
    if (node) {
        var HTML = "";
        HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none\">";
        var m_t = dojo.byId("member_to");
        if (m_t) {
            var t_o = m_t.options;
            to_size = t_o.length;
            for (i = 0; i < to_size; i++) {
                var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g,
                        "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
                HTML += "<span>" + text + "</span>";
                if (i < to_size - 1) {
                    HTML += ",<wbr/>";
                }
            }
        }
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'
                + aimluck.io.escapeText("message_val_member2")
                + '\" onclick=\"aipo.message.shrinkMember();\" />'
        HTML += "</td></tr></tbody></table>";
        node.innerHTML = HTML;
    }

    var _node = dojo.byId("memberField");
    if (_node) {
        dojo.style(_node, "display", "block");
    }
    aipo.message.setWrapperHeight();
}

aipo.message.onReceiveMessage = function(msg) {
    if (!msg) {
        aimluck.io.disableForm(dojo.byId("messageForm"), false);
        aipo.message.reloadMessageList();
        aipo.message.reloadRoomList();
        aipo.message.clearInput();
    }
};

aipo.message.onReceiveMessageRoom = function(msg) {
    if (!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if (arrDialog) {
            arrDialog.hide();
            aipo.message.reloadRoomList();
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
};

aipo.message.setWrapperHeight = function() {
    var modalDialog = document.getElementById('modalDialog');
    if (modalDialog) {
        var wrapper = document.getElementById('wrapper');
        wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

dojo.addOnLoad(function() {
    dojo.connect(window, "onresize", null, function(e) {
        aipo.message.fixMessageWindow();
    });
});
