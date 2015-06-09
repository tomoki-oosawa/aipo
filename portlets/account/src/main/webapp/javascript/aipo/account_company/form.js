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


