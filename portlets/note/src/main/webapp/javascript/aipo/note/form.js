/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

dojo.provide("aipo.note");

dojo.require("aipo.widget.DropdownDatepicker");

aipo.note.afterFunction = function(pid){
    aipo.note.onLoadNoteDialog(pid);
}

aipo.note.onLoadDetail = function(portlet_id){
    aipo.portletReload('whatsnew');
}

aipo.note.onLoadNoteDialog = function(portlet_id){
    var url_userlist = dojo.byId('urlUserlist'+portlet_id).value;
    var dst_user_id = dojo.byId('urlDstUser'+portlet_id).value;
    if(url_userlist){
        aipo.note.changeGroup(url_userlist, 'LoginUser', dst_user_id);   
    }    
}

aipo.note.formSwitchCategoryInput = function(button) {
    if(button.form.is_new_category.value == 'TRUE' || button.form.is_new_category.value == 'true') {
        button.value = '新しく入力する';
        aipo.note.formCategoryInputOff(button.form);
    } else {
        button.value = '一覧から選択する';
        aipo.note.formCategoryInputOn(button.form);
    }
}

aipo.note.formCategoryInputOn = function(form) {
    dojo.byId('noteCategorySelectField').style.display = "none";
    dojo.byId('noteCategoryInputField').style.display = "";

    form.is_new_category.value = 'TRUE';
}

aipo.note.formCategoryInputOff = function(form) {
    dojo.byId('noteCategoryInputField').style.display = "none";
    dojo.byId('noteCategorySelectField').style.display = "";
    
    form.is_new_category.value = 'FALSE';
}

aipo.note.changeGroup = function(link, group, sel) {
    aimluck.utils.form.createSelect("dest_user_id", "destuserDiv", link + "?mode=group&groupname=" + group + "&inc_luser=false", "userId", "aliasName", sel, '<option value="all">（ユーザー全員）<\/option>');
}

aipo.note.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('note');
        aipo.portletReload('whatsnew');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.note.oncheck0 = function(node) {
    chk = dojo.byId(node);
    chk.checked=true;
    return;
}

aipo.note.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }
    aipo.portletReload('note');
};
