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

dojo.provide("aipo.holiday");

dojo.require("aipo.widget.GroupNormalSelectList");

aipo.holiday.onLoadHolidayDialog = function(portlet_id){

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
    var holiday_name = dojo.byId("holiday_name");
    if(holiday_name && holiday_name.type == 'text'){
      holiday_name.focus();
    }
}

aipo.holiday.formSwitchPostInput = function(button) {
    if(button.form.is_new_post.value == 'TRUE' || button.form.is_new_post.value == 'true') {
        button.value = '新しく入力する';
        aipo.holiday.formPostInputOff(button.form);
    } else {
        button.value = '一覧から選択する';
        aipo.holiday.formPostInputOn(button.form);
    }
}

aipo.holiday.formPostInputOn = function(form) {
    dojo.byId('postSelectField').style.display = "none";
    dojo.byId('postInputField').style.display = "";

    form.is_new_post.value = 'TRUE';
}

aipo.holiday.formPostInputOff = function(form) {
    dojo.byId('postInputField').style.display = "none";
    dojo.byId('postSelectField').style.display = "";

    form.is_new_post.value = 'FALSE';
}


aipo.holiday.formSwitchPositionInput = function(button) {
    if(button.form.is_new_position.value == 'TRUE' || button.form.is_new_position.value == 'true') {
        button.value = '新しく入力する';
        aipo.holiday.formPositionInputOff(button.form);
    } else {
        button.value = '一覧から選択する';
        aipo.holiday.formPositionInputOn(button.form);
    }
}

aipo.holiday.formPositionInputOn = function(form) {
    dojo.byId('positionSelectField').style.display = "none";
    dojo.byId('positionInputField').style.display = "";

    form.is_new_position.value = 'TRUE';
}

aipo.holiday.formPositionInputOff = function(form) {
    dojo.byId('positionInputField').style.display = "none";
    dojo.byId('positionSelectField').style.display = "";

    form.is_new_position.value = 'FALSE';
}

aipo.holiday.formAdminToggle = function(chkbox) {
    dojo.byId('is_admin').value = chkbox.checked ? 'true' : 'false';
}

aipo.holiday.defaultFormat = function(button, url, indicator_id, p_id){
	if(confirm('祝日をデフォルトに戻してもよろしいですか？')) {
	    disableButton(button.form);
	    aimluck.io.disableForm(button.form, true);
	    aimluck.io.setHiddenValue(button);
	    button.form.action = url;
	    aimluck.io.submit(button.form, indicator_id, p_id, aipo.holiday.onReceiveMessage);
	}
}

aipo.holiday.changeYear = function(button, url, indicator_id, p_id){
    disableButton(button.form);
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, p_id, aipo.holiday.onReceiveMessage);
}

aipo.holiday.onReceiveMessage = function(msg){
	if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('holiday');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.holiday.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('holiday');
    }
    if (dojo.byId('listMessageDiv')) {
        dojo.byId('listMessageDiv').innerHTML = msg;
    }
}

