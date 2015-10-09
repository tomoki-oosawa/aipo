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
dojo.provide("aipo.account_company");

dojo.require("aipo.widget.MemberNormalSelectList");

aipo.account_company.onLoadPostDialog = function(portlet_id){
    var picker = dijit.byId("membernormalselect");
        if(picker){
        var memberlist = picker;
        var select = dojo.byId('init_memberlist');
        var i;
        var s_o = select.options;
        if (s_o.length == 1 && s_o[0].value == "") return;
        for(i = 0 ; i < s_o.length; i ++ ) {
            memberlist.addOptionSync(s_o[i].value,s_o[i].text,true);
        }

        dojo.byId("post_name").focus();
    }
}

aipo.account_company.onLoadPositionDialog = function(portlet_id){
    var obj = dojo.byId("position_name");
    if(obj){
        obj.focus();
    }
}

aipo.account_company.onLoadCompanyDialog = function(portlet_id){
    var obj = dojo.byId("company_name");
    if(obj){
        obj.focus();
    }
}

aipo.account_company.onLoadPasswdDialog = function(portlet_id){
    var obj = dojo.byId("new_passwd");
    if(obj){
        obj.focus();
    }
}

aipo.account_company.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('account_company');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

//-----------------------------------------------------------------------------------------
aipo.account_company.onLoadAccountMember = function() {
    aipo.widget.MemberFilterList.setup("memberfilterlist", "init_memberlist", "member_to");
    aipo.account_company.changeMember();
};

aipo.account_company.changeMember = function() {
    var node = dojo.byId("memberFieldDisplay");
    if (node) {
        var HTML = "";
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
        node.innerHTML = HTML;
    }

    aipo.account_company.setWrapperHeight();
}
//--------------------------------------------
aipo.account_company.toggleMemberSelect = function(bool){
    var node = dojo.byId("memberField");
    var buttonOn = dojo.byId("memberSelectButtonOn");
    var buttonOff = dojo.byId("memberSelectButtonOff");
    if(bool) {
        dojo.style(buttonOn, "display" , "none");
        dojo.style(buttonOff, "display" , "block");
        dojo.style(node, "display" , "block");
    } else {
        dojo.style(buttonOn, "display" , "block");
        dojo.style(buttonOff, "display" , "none");
        dojo.style(node, "display" , "none");
    }
    aipo.account_company.setWrapperHeight();
}
//--------------------------------------------
aipo.account_company.onReceiveAcountMember = function(msg) {
    if (!msg["error"]) {
        var arrDialog = dijit.byId("modalDialog");
        if (arrDialog) {
            arrDialog.hide();
            aipo.account_company.currentUserId = null;
            var tmpRoomId = parseInt(msg["params"]);
            if (tmpRoomId != NaN) {
                aipo.account_company.reloadRoomList(tmpRoomId);
            } else {
                aipo.account_company.reloadRoomList();
            }
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg["error"];
    }
};