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
dojo.provide("aipo.account_user");

dojo.require("aipo.widget.GroupNormalSelectList");

aipo.account_user.onLoadUserDialog = function(portlet_id){

    var fpicker = dijit.byId("groupnormalselect");
    if(fpicker){
        var select = dojo.byId('init_grouplist');
        var i;
        var s_o = select.options;
        if (s_o.length == 1 && s_o[0].value == "") return;
        for(i = 0 ; i < s_o.length; i ++ ) {
            fpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
        }
    }

    var username = dojo.byId("username");
    if(username && username.type == 'text'){
      username.focus();
    }
}

aipo.account_user.formSwitchPostInput = function(button) {
	var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

    if(button.form.is_new_post.value == 'TRUE' || button.form.is_new_post.value == 'true') {
        button.value = nlsStrings.NEW_INPUT_STR;
        aipo.account_user.formPostInputOff(button.form);
    } else {
        button.value = nlsStrings.SELECT_FROM_LIST_STR;
        aipo.account_user.formPostInputOn(button.form);
    }
}

aipo.account_user.formPostInputOn = function(form) {
    dojo.byId('postSelectField').style.display = "none";
    dojo.byId('postInputField').style.display = "";

    form.is_new_post.value = 'TRUE';
}

aipo.account_user.formPostInputOff = function(form) {
    dojo.byId('postInputField').style.display = "none";
    dojo.byId('postSelectField').style.display = "";

    form.is_new_post.value = 'FALSE';
}


aipo.account_user.formSwitchPositionInput = function(button) {
	var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

    if(button.form.is_new_position.value == 'TRUE' || button.form.is_new_position.value == 'true') {
        button.value = nlsStrings.NEW_INPUT_STR;
        aipo.account_user.formPositionInputOff(button.form);
    } else {
        button.value = nlsStrings.SELECT_FROM_LIST_STR;
        aipo.account_user.formPositionInputOn(button.form);
    }
}

aipo.account_user.formPositionInputOn = function(form) {
    dojo.byId('positionSelectField').style.display = "none";
    dojo.byId('positionInputField').style.display = "";

    form.is_new_position.value = 'TRUE';
}

aipo.account_user.formPositionInputOff = function(form) {
    dojo.byId('positionInputField').style.display = "none";
    dojo.byId('positionSelectField').style.display = "";

    form.is_new_position.value = 'FALSE';
}

aipo.account_user.formAdminToggle = function(chkbox) {
    dojo.byId('is_admin').value = chkbox.checked ? 'true' : 'false';
}



aipo.account_user.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('account_user');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.account_user.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('account_user');
    }
    if (dojo.byId('listMessageDiv')) {
        dojo.byId('listMessageDiv').innerHTML = msg;
    }
}

aipo.account_user.submit2 = function(form) {
  var s_o = form.member_so.options;
  var tmp = '';

  for(i = 0 ; i < s_o.length; i++ ) {
    s_o[i].selected = false;
  }

  if(s_o.length > 0) {
    for(i = 0 ; i < s_o.length-1; i++ ) {
      tmp = tmp + s_o[i].value + ',';
    }
    tmp = tmp + s_o[s_o.length-1].value;
  }
  form.positions.value = tmp;
}

aipo.account_user.toggleMenu = function(node, filter, alwaysPulldown) {
  var rect = filter.getBoundingClientRect();
  var html = document.documentElement.getBoundingClientRect();
  var footer = document.getElementById('auiWidgetsArea');
  var header = document.getElementById('mobileHeader_v3');

  if (node.style.display == "none") {
    dojo.query("div.menubar").style("display", "none");

    var scroll = {
      left : document.documentElement.scrollLeft
          || document.body.scrollLeft,
      top : document.documentElement.scrollTop || document.body.scrollTop
    };
    node.style.opacity = "0";
    setTimeout( function(){
      dojo.style(node, "display" , "block");
    }, 0);
    if (html.right - node.clientWidth > rect.left) {
      node.style.left = rect.left + scroll.left + "px";
    } else {
      node.style.left = rect.right - node.clientWidth + scroll.left
          + "px";
    }
    if (html.bottom - node.clientHeight > rect.bottom || alwaysPulldown) {
      node.style.top = rect.bottom + scroll.top + "px";
    } else {
      node.style.top = rect.top - node.clientHeight + scroll.top + "px";
    }
    node.style.opacity = "";
  } else {
    dojo.query("div.menubar").style("display", "none");
  }
}


aipo.account_user.onChangePost = function(url, portlet_id, folder_id) {
  aipo.viewPage(url, portlet_id);
}

aipo.account_user.onChangeRole = function(url, portlet_id, group_id) {
  aipo.viewPage(url, portlet_id);
}

aipo.account_user.downloadCsv = function(url){
	window.location.href=url;
}

