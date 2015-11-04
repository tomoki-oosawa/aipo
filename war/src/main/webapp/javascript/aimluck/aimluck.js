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
window.aimluck = window.aimluck || {};
dojo.require("dojo.string");
dojo.requireLocalization("aipo", "locale");

aimluck.namespace = function(ns) {

	if (!ns || !ns.length) {
		return null;
	}

	var levels = ns.split(".");
	var nsobj = aimluck;

	for ( var i = (levels[0] == "aimluck") ? 1 : 0; i < levels.length; ++i) {
		nsobj[levels[i]] = nsobj[levels[i]] || {};
		nsobj = nsobj[levels[i]];
	}

	return nsobj;
};

function getObjectById(id) {
	if (document.getElementById)
		return document.getElementById(id) // e5,e6,n6,m1,o6
	else if (document.all)
		return document.all(id) // e4
	else if (document.layers)
		return document.layers[id] // n4
}

function ew(button) {
	disableButton(button.form);
	button.form.action = button.form.action + '?' + button.name + '=1';
	button.form.submit();
}

function dw(button) {
	var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	var confirmString = dojo.string.substitute(nlsStrings.DW_STR, {
		dw_del : nlsStrings.DW_DEL,
		dw_this : nlsStrings.DW_THIS,
		dw_name : button.form.name
	});
	// 'この'+button.form.name+'を削除してよろしいですか？'
	if (confirm(confirmString)) {
		disableButton(button.form);
		button.form.action = button.form.action + '?' + button.name + '=1';
		button.form.submit();
	}
}

function ews(button) {
	disableButton(button.form);
	button.form.action = button.form.action + '?' + button.name + '=1';
	button.form.submit();
}

function dws(button) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.DWS_STR, {
		dws_del : nlsStrings.DWS_DEL,
		dws_sel : nlsStrings.DWS_SEL,
		dws_name : button.form._name.value
	  });
	// 選択した'+button.form._name+'を削除してよろしいですか？
	if (confirm(confirmString)) {
		disableButton(button.form);
		button.form.action = button.form.action + '?' + button.name + '=1';
		button.form.submit();
	}
}

function setHiddenValue(button) {
	if (button.name) {
		var q = document.createElement('input');
		q.type = 'hidden';
		q.name = button.name;
		q.value = button.value;
		button.form.appendChild(q);
	}
}

function disableSubmit(form) {
	var elements = form.elements;
	for ( var i = 0; i < elements.length; i++) {
		if (elements[i].type == 'submit') {
			elements[i].disabled = true;
		}
	}
}

function disableButton(form) {
	var elements = form.elements;
	for ( var i = 0; i < elements.length; i++) {
		if (elements[i].type == 'button') {
			elements[i].disabled = true;
		}
	}
}

function check_new_mail(button, current_page) {
	button.form.action = button.form.action + '?confirmlasttime=true&start='
			+ current_page;
	button.form.submit();
}

function createAction(button) {
	button.form.action = button.form.action + '?' + button.name + '=1';
}

function verifyCheckBox(form, action, button) {
	var cnt = 0;
	var i;
	for (i = 0; i < form.elements.length; i++) {
		if (form.elements[i].checked)
			cnt++;
	}
	if (cnt == 0) {
		var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
		var alertString = dojo.string.substitute(nlsStrings.VERIFYCB_STR, {
			verifycb_sel : nlsStrings.VERIFYCB_SEL,
			verifycb_gt_one : nlsStrings.VERIFYCB_GT_ONE,
			verifycb_cb : nlsStrings.VERIFYCB_CB
		});
		//"チェックボックスを１つ以上選択してください。"
		alert(alertString);
		return false;
	} else {
		return action(button);
	}
}

function submit_member(select) {
	var t_o = select.options;
	for (i = 0; i < t_o.length; i++) {
		t_o[i].selected = true;
	}
}

function add_option(select, value, text, is_selected) {
	if (document.all) {
		var option = document.createElement("OPTION");
		option.value = value;
		option.text = text;
		option.selected = is_selected;
		if (select.options.length == 1 && select.options[0].value == "") {
			select.options.remove(0);
		}
		select.add(option, select.options.length);
		// select.options[length].selected = is_selected;
	} else {
		var option = document.createElement("OPTION");
		option.value = value;
		option.text = text;
		option.selected = is_selected;
		if (select.options.length == 1 && select.options[0].value == "") {
			select.removeChild(select.options[0]);
		}
		select.insertBefore(option, select.options[select.options.length]);
		// select.options[length].selected = is_selected;
	}
}

function add_member(select_member_from, select_member_to) {
	if (document.all) {
		var f_o = select_member_from.options;
		var t_o = select_member_to.options;
		if (f_o.length == 1 && f_o[0].value == "")
			return;
		for (i = 0; i < f_o.length; i++) {
			if (!f_o[i].selected)
				continue;
			var iseq = false;

			for (j = 0; j < t_o.length; j++) {
				if (t_o[j].value == f_o[i].value) {
					iseq = true;
					break;
				}
			}

			if (iseq)
				continue;
			var option = document.createElement("OPTION");
			option.value = f_o[i].value;
			option.text = f_o[i].text;
			option.selected = true;
			if (t_o.length == 1 && t_o[0].value == "") {
				t_o.remove(0);
			}
			t_o.add(option, t_o.length);
		}
	} else {
		var f_o = select_member_from.options;
		var t_o = select_member_to.options;
		if (f_o.length == 1 && f_o[0].value == "")
			return;
		for (i = 0; i < f_o.length; i++) {
			if (!f_o[i].selected)
				continue;
			var iseq = false;

			for (j = 0; j < t_o.length; j++) {
				if (t_o[j].value == f_o[i].value) {
					iseq = true;
					break;
				}
			}

			if (iseq)
				continue;
			var option = document.createElement("OPTION");
			option.value = f_o[i].value;
			option.text = f_o[i].text;
			option.selected = true;
			if (select_member_to.options.length == 1
					&& select_member_to.options[0].value == "") {
				select_member_to.removeChild(select_member_to.options[0]);
			}
			select_member_to.insertBefore(option, t_o[t_o.length]);
		}
	}
}

function remove_member(select) {
	if (document.all) {
		var t_o = select.options;
		for (i = 0; i < t_o.length; i++) {
			if (t_o[i].selected) {
				t_o.remove(i);
				i -= 1;
			}
		}
	} else {
		var t_o = select.options;
		for (i = 0; i < t_o.length; i++) {
			if (t_o[i].selected) {
				select.removeChild(t_o[i]);
				i -= 1;
			}
		}
	}

	if (t_o.length == 0) {
		add_option(select, '', '\u3000', false)
	}
}

function doUpOptions10(select) {
	var s_o = select.options;
	for (i = 0; i < s_o.length; i++) {
		if (!s_o[i].selected)
			continue;
		if (i == 0)
			continue;
		if (s_o[i - 1].selected)
			continue;
		up_option(select, i, 10);
	}
	selectedOptionIntoView(select);
}

function doUpOptions(select) {
	var s_o = select.options;
	for (i = 0; i < s_o.length; i++) {
		if (!s_o[i].selected)
			continue;
		if (i == 0)
			continue;
		if (s_o[i - 1].selected)
			continue;
		up_option(select, i, 1);
	}
	selectedOptionIntoView(select);
}

function doDownOptions10(select) {
	var s_o = select.options;
	for (i = s_o.length - 1; i >= 0; i--) {
		if (!s_o[i].selected)
			continue;
		if (i == s_o.length - 1)
			continue;
		if (s_o[i + 1].selected)
			continue;
		down_option(select, i, 10);
	}
	selectedOptionIntoView(select);
}

function doDownOptions(select) {
	var s_o = select.options;
	for (i = s_o.length - 1; i >= 0; i--) {
		if (!s_o[i].selected)
			continue;
		if (i == s_o.length - 1)
			continue;
		if (s_o[i + 1].selected)
			continue;
		down_option(select, i, 1);
	}
	selectedOptionIntoView(select);
}

function selectedOptionIntoView(select){
	var s_o = select.options;
	var firstSelected=select.options[0];
	var i=0;
	for (i = 0; i < s_o.length; i++) {
		if (s_o[i].selected){
			firstSelected=s_o[i];
			break;
		}
	}
	if(dojo.isIE==8){
		if(select.scrollTop>16+18*(i-1))select.scrollTop=16+18*(i-1);
		if(select.scrollTop<16+18*(i-20))select.scrollTop=16+18*(i-20);
	}else{
		if(firstSelected.offsetTop<select.scrollTop)select.scrollTop=firstSelected.offsetTop-firstSelected.offsetHeight/2;
		if(firstSelected.offsetTop>select.scrollTop+select.offsetHeight)select.scrollTop=firstSelected.offsetTop-select.offsetHeight+firstSelected.offsetHeight/2;
	}
}

function up_option(select, index, rate) {
	var s_o = select.options;
	var delta = 0;
	if (index - rate >= 0) {
		delta = index - rate;
	} else {
		for (i = 0; i < s_o.length; i++) {
			if (!s_o[i].selected) {
				delta = i;
				break;
			}
		}
	}

	change_turn_option(select, index, delta);
}

function down_option(select, index, rate) {
	var s_o = select.options;
	var delta = 0;
	if (s_o.length - 1 - index - rate >= 0) {
		delta = index + rate;
	} else {
		for (i = s_o.length - 1; i >= 0; i--) {
			if (!s_o[i].selected) {
				delta = i;
				break;
			}
		}
	}

	change_turn_option(select, index, delta);
}

function change_turn_option(select, index, delta) {
	var s_o = select.options;
	if (document.all) {
		var option = document.createElement("OPTION");
		option.value = s_o[index].value;
		option.text = s_o[index].text;
		option.selected = true;
		select.remove(index);
		s_o.add(option, delta);
		s_o[delta].selected = true;
	} else {
		var option = document.createElement("OPTION");
		option.value = s_o[index].value;
		option.text = s_o[index].text;
		option.selected = true;
		select.removeChild(s_o[index]);
		select.insertBefore(option, s_o[delta]);
		s_o[delta].selected = true;
	}
}
