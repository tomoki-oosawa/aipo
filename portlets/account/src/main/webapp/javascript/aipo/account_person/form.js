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
dojo.provide("aipo.account_person");

aipo.account_person.onLoadPersonInfoDialog = function(portlet_id){

    var obj = dojo.byId("lastname");
    if(obj){
        obj.focus();
    }

}

aipo.account_person.onLoadPersonPasswdDialog = function(portlet_id){

    var obj = dojo.byId("new_passwd");
    if(obj){
        obj.focus();
    }

}

aipo.account_person.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        location.reload();
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.account_person.onChangePasswdReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.account_person.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
            arrDialog.hide();
    }
    aipo.portletReload('account_person');
};

aipo.account_person.setDeletePhotoValue = function(pid) {
    var obj = dojo.byId("delete_photo_" + pid);
    obj.value = true;
};