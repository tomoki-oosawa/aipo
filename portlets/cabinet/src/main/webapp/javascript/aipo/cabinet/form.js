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
dojo.require("aipo.widget.MemberNormalSelectList");

dojo.provide("aipo.cabinet");

aipo.cabinet.onLoadCabinetFileDialog = function(pid) {

	var obj = dojo.byId("file_title");
	if (obj) {
		obj.focus();
	}

}

aipo.cabinet.onLoadCabinetFolderDialog = function(pid) {

	var obj = dojo.byId("folder_name");
	if (obj) {
		obj.focus();
	}

	var mpicker = dijit.byId("membernormalselect");
	if (mpicker) {
		var select = dojo.byId('init_memberlist');
		var i;
		var s_o = select.options;
		if (s_o.length == 1 && s_o[0].value == "")
			return;
		for (i = 0; i < s_o.length; i++) {
			mpicker.addOptionSync(s_o[i].value, s_o[i].text, true);
		}
	}

}

aipo.cabinet.onReceiveMessage = function(msg) {
	// 送信時に作成した場合selectを削除。
	var select = dojo.byId("attachments_select");
	if (typeof select != "undefined" && select != null)
		select.parentNode.removeChild(select);

	if (!msg) {
		var arrDialog = dijit.byId("modalDialog");
		if (arrDialog) {
			arrDialog.hide();
		}
		aipo.portletReload('cabinet');
		aipo.portletReload('schedule');
		aipo.portletReload('timeline');
	}
	if (dojo.byId('messageDiv')) {
		dojo.byId('messageDiv').innerHTML = msg;
	}

	var modalDialog = document.getElementById('modalDialog');
	if (modalDialog && msg != '') {
		var wrapper = document.getElementById('wrapper');
		wrapper.style.minHeight = modalDialog.clientHeight + 'px';
	}
}

aipo.cabinet.onListReceiveMessage = function(msg) {
	if (!msg) {
		var arrDialog = dijit.byId("modalDialog");
		if (arrDialog) {
			arrDialog.hide();
		}
		aipo.portletReload('cabinet');
	}
	if (dojo.byId('listmessageDiv')) {
		dojo.byId('listmessageDiv').innerHTML = msg;
	}
}

aipo.cabinet.onSubmitSerchButton = function(form, url, p_id) {
	var exec_url = url;
	var search_params = [ [ "sword", form.sword.value ] ];
	aipo.viewPage(exec_url, p_id, search_params);
}

aipo.cabinet.viewpageByFolderId = function(url, p_id, f_id) {
	url = url + '&folder_id=' + f_id;
	aipo.viewPage(url, p_id);
}

aipo.cabinet.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id,
		receive) {
	if (confirm('この' + button.form._name.value
			+ 'を削除してよろしいですか？なお、フォルダに含まれるファイルやフォルダはすべて削除されます。')) {
		aimluck.io.disableForm(button.form, true);
		aimluck.io.setHiddenValue(button);
		button.form.action = url;
		aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
	}
}

aipo.cabinet.showMember = function(button) {
	dojo.byId('Block-GroupMember-Show').style.display = "";
	dojo.byId('is_member').value = "TRUE";
}

aipo.cabinet.hideMember = function(button) {
	dojo.byId('Block-GroupMember-Show').style.display = "none";
	dojo.byId('member_to').options.length = 0;
	dojo.byId('is_member').value = "FALSE";
}

aipo.cabinet.toggleMenu = function(node, filter, alwaysPulldown) {
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

aipo.cabinet.onChangeFolder = function(url, portlet_id, folder_id) {
	aipo.viewPage(url, portlet_id);
}

aipo.cabinet.onChangeGroup = function(url, portlet_id, group_id) {
	aipo.viewPage(url, portlet_id);
}
