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
dojo.provide("aipo.system");

aipo.system.onLoadNetworkInfoDialog = function(portlet_id) {
	var obj = dojo.byId("ipaddress");
	if (obj) {
		obj.focus();
	}
	var forms = document.forms;
	for(var i=0;i<forms.length;i++){
		aimluck.io.disableForm(forms[i], false);
	}
}

aipo.system.onReceiveMessage = function(msg) {
	if (!msg) {
		var arrDialog = dijit.byId("modalDialog");
		if (arrDialog) {
			arrDialog.hide();
		}
		aipo.portletReload('system');
	}
	if (dojo.byId('messageDiv')) {
		dojo.byId('messageDiv').innerHTML = msg;
	}
}

aipo.system.hideDialog = function() {
	var arrDialog = dijit.byId("modalDialog");
	if (arrDialog) {
		arrDialog.hide();
	}
	aipo.portletReload('system');
}

aipo.system.switchAuthSendAdmin = function(check) {
	if (check.value == 2) {
		dojo.byId('smtp_auth_field').style.display = "";
		dojo.byId('pop_auth_field').style.display = "none";
	} else if (check.value == 1) {
		dojo.byId('smtp_auth_field').style.display = "none";
		dojo.byId('pop_auth_field').style.display = "";
	} else {
		dojo.byId('smtp_auth_field').style.display = "none";
		dojo.byId('pop_auth_field').style.display = "none";
	}
};
