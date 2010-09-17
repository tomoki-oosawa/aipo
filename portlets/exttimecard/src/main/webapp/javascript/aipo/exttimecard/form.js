/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

dojo.provide("aipo.exttimecard");

dojo.require("aimluck.widget.Contentpane");
dojo.require("aipo.widget.DropdownDatepicker");

aipo.exttimecard.onLoadTimecardDialog = function(portlet_id){
  var obj = dojo.byId("reason");
  if(obj){
     obj.focus();
  }
}

aipo.exttimecard.formSwitchCategoryInput = function(button) {
    if(button.form.is_new_category.value == 'TRUE' || button.form.is_new_category.value == 'true') {
        button.value = '新しく入力する';
        aipo.timecard.formCategoryInputOff(button.form);
    } else {
        button.value = '一覧から選択する';
        aipo.timecard.formCategoryInputOn(button.form);
    }
}

aipo.exttimecard.formCategoryInputOn = function(form) {
    dojo.html.setDisplay(dojo.byId('timecardCategorySelectField'), false);
    dojo.html.setDisplay(dojo.byId('timecardCategoryInputField'), true);

    form.is_new_category.value = 'TRUE';
}

aipo.exttimecard.formCategoryInputOff = function(form) {
    dojo.html.setDisplay(dojo.byId('timecardCategoryInputField'), false);
    dojo.html.setDisplay(dojo.byId('timecardCategorySelectField'), true);

    form.is_new_category.value = 'FALSE';
}

aipo.exttimecard.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('exttimecard');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.exttimecard.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('exttimecard');
    }
    if (dojo.byId('exttimecardmessageDiv')) {
        dojo.byId('exttimecardmessageDiv').innerHTML = msg;
    }
}

aipo.exttimecard.removeHiddenValue = function(form, name){
    if (form[name] && document.getElementsByName(name).item(0)) {
        form.removeChild(form[name]);
    }
}

aipo.exttimecard.addHiddenValue = function(form, name, value){
    if (form[name] && document.getElementsByName(name).item(0)) {
        form[name].value = value;
    } else {
        var q = document.createElement('input');
        q.type = 'hidden';
        q.name = name;
        q.value = value;
        form.appendChild(q);
    }
}

aipo.exttimecard.addYearMonthDayHiddenValue = function(form, name){
    var hour_str = name + "_hour";
    var minute_str = name + "_minute";
    var year_str = name + "_year";
    var month_str = name + "_month";
    var day_str = name + "_day";
    if (form[hour_str].value != "-1" && form[minute_str].value != "-1") {
        var year = form.punch_date_year.value;
        var month = form.punch_date_month.value;
        var day = form.punch_date_day.value;
        aipo.exttimecard.addHiddenValue(form, year_str, year);
        aipo.exttimecard.addHiddenValue(form, month_str, month);
        aipo.exttimecard.addHiddenValue(form, day_str, day);
    } else {
        aipo.exttimecard.removeHiddenValue(form, year_str);
        aipo.exttimecard.removeHiddenValue(form, month_str);
        aipo.exttimecard.removeHiddenValue(form, day_str);
    }
}

aipo.exttimecard.onSubmit = function(form) {
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'clock_in_time');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'clock_out_time');

    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'outgoing_time1');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'outgoing_time2');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'outgoing_time3');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'outgoing_time4');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'outgoing_time5');

    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'comeback_time1');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'comeback_time2');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'comeback_time3');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'comeback_time4');
    aipo.exttimecard.addYearMonthDayHiddenValue(form, 'comeback_time5');
}

aipo.exttimecard.displayOutCome = function(obj){
	var id = obj.id;

	if(id == "plus1"){
		aipo.exttimecard.moveButton(id, "plus2");
		aipo.exttimecard.displayBox("rest_num2");
	}
	else if(id == "plus2"){
		aipo.exttimecard.moveButton(id, "plus3");
		aipo.exttimecard.displayBox("rest_num3");
	}
	else if(id == "plus3"){
		aipo.exttimecard.moveButton(id, "plus4");
		aipo.exttimecard.displayBox("rest_num4");
	}
	else if(id == "plus4"){
		aipo.exttimecard.moveButton(id, "dummy");
		aipo.exttimecard.displayBox("rest_num5");
	}
	else if(id == "plus5"){

	}
}

aipo.exttimecard.moveButton = function(id1, id2){

	obj1 = dojo.byId(id1);
	obj2 = dojo.byId(id2);

	if(obj1 != null){
		obj1.style.display = "none";
	}

	if(obj2 != null){
		obj2.style.display = "";
	}
}

aipo.exttimecard.displayBox = function(id){

	obj = dojo.byId(id);
	if(obj != null){
		obj.style.display = "";
	}
}

aipo.exttimecard.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
      arrDialog.hide();
    }
    aipo.portletReload('exttimecard');
};

