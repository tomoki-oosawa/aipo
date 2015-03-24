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
dojo.provide("aipo.exttimecard");

dojo.require("aimluck.widget.Contentpane");
dojo.require("aipo.widget.DropdownDatepicker");
dojo.require("dojo.string");
dojo.require("aimluck.io");
dojo.requireLocalization("aipo", "locale");


aipo.exttimecard.onReceiveMessage = function(msg) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.portletReload('exttimecard');
  }

  if (dojo.byId('messageDiv')) {
    dojo.byId('messageDiv').innerHTML = msg;
  }

    var modalDialog = document.getElementById('modalDialog');
    if(modalDialog && msg != '') {
      var wrapper = document.getElementById('wrapper');
      wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

aipo.exttimecard.onListReceiveMessage = function(msg) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.portletReload('exttimecard');
  }
  if (dojo.byId('exttimecardmessageDiv')) {
    dojo.byId('exttimecardmessageDiv').innerHTML = msg;
  }
}

aipo.exttimecard.removeHiddenValue = function(form, name) {
  if (form[name] && document.getElementsByName(name).item(0)) {
    form.removeChild(form[name]);
  }
}

aipo.exttimecard.addHiddenValue = function(form, name, value) {
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

aipo.exttimecard.addYearMonthDayHiddenValue = function(form, name) {
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

aipo.exttimecard.displayOutCome = function(obj) {
  var id = "";
  var rest_obj = null;
  var i = 1;
  for (i = 1; i <= 5; i++) {
    if (i == 5) {
      dojo.byId("plus").style.display = "none";
    }
    id = "rest_num" + i;
    rest_obj = dojo.byId(id);
    if (rest_obj != null && rest_obj.style.display == "none") {
      rest_obj.style.display = "block";
      break;
    }
  }
  // 外出の回数をセット
  aipo.exttimecard.setRestNum();
}

aipo.exttimecard.displayBox = function(id) {

  obj = dojo.byId(id);
  if (obj != null) {
    obj.style.display = "";
  }
}

aipo.exttimecard.hideOutCome = function(obj) {
  var id = obj.id;

  if (id == "minus1") {
    aipo.exttimecard.moveDataOutCome(1);
    aipo.exttimecard.hideOutComeBox();
  } else if (id == "minus2") {
    aipo.exttimecard.moveDataOutCome(2);
    aipo.exttimecard.hideOutComeBox();
  } else if (id == "minus3") {
    aipo.exttimecard.moveDataOutCome(3);
    aipo.exttimecard.hideOutComeBox();
  } else if (id == "minus4") {
    aipo.exttimecard.moveDataOutCome(4);
    aipo.exttimecard.hideOutComeBox();
  } else if (id == "minus5") {
    aipo.exttimecard.hideOutComeBox();
  }

  dojo.byId("plus").style.display = "block";
  aipo.exttimecard.setRestNum();
}

aipo.exttimecard.moveDataOutCome = function(num) {
  var i = num;
  for (i; i <= 4; i++) {
    var from = i + 1;
    var to = i;
    dojo.byId("outgoing_time" + to + "_hour").selectedIndex = dojo
        .byId("outgoing_time" + from + "_hour").selectedIndex;
    dojo.byId("outgoing_time" + to + "_minute").selectedIndex = dojo
        .byId("outgoing_time" + from + "_minute").selectedIndex;
    dojo.byId("comeback_time" + to + "_hour").selectedIndex = dojo
        .byId("comeback_time" + from + "_hour").selectedIndex;
    dojo.byId("comeback_time" + to + "_minute").selectedIndex = dojo
        .byId("comeback_time" + from + "_minute").selectedIndex;
  }
  // 5番目は削除
  dojo.byId("outgoing_time" + 5 + "_hour").selectedIndex = 0;
  dojo.byId("outgoing_time" + 5 + "_minute").selectedIndex = 0;
  dojo.byId("comeback_time" + 5 + "_hour").selectedIndex = 0;
  dojo.byId("comeback_time" + 5 + "_minute").selectedIndex = 0;
}

aipo.exttimecard.hideOutComeBox = function() {
  var id = "";
  var rest_obj = null;
  var i = 5;
  for (i; i >= 1; i--) {
    id = "rest_num" + i;
    rest_obj = dojo.byId(id);
    if (rest_obj != null && rest_obj.style.display != "none") {
      rest_obj.style.display = "none";
      break;
    }
  }
}

aipo.exttimecard.setRestNum = function() {
  var rest_num = 0;
  for ( var i = 1; i <= 5; i++) {
    var id = "rest_num" + i;
    var rest_obj = dojo.byId(id);
    if (rest_obj != null && rest_obj.style.display != "none") {
      rest_num++;
    }
  }
  // 外出の回数をセット
  dojo.byId("rest_num").value = rest_num;
}

aipo.exttimecard.hideBox = function(id) {

  obj = dojo.byId(id);
  if (obj != null) {
    obj.style.display = "none";
  }
}

aipo.exttimecard.hideDialog = function() {
  var arrDialog = dijit.byId("modalDialog");
  if (arrDialog) {
    arrDialog.hide();
  }
  aipo.portletReload('exttimecard');
};

aipo.exttimecard.hideTimeBox = function() {
  aipo.exttimecard.hideBox("clock_time_box");
  aipo.exttimecard.hideBox("outgoing_comeback_box");
}

aipo.exttimecard.displayTimeBox = function() {
  aipo.exttimecard.displayBox("clock_time_box");
  aipo.exttimecard.displayBox("outgoing_comeback_box");
}


aipo.exttimecard.submit = function(form, indicator_id, portlet_id, callback) {
  aimluck.io.disableForm(form, true);

  var obj_indicator = dojo.byId(indicator_id + portlet_id);
  if (obj_indicator) {
    dojo.style(obj_indicator, "display", "");
  }

  try {
    dojo
        .xhrPost({
          url : form.action,
          timeout : 30000,
          form : form,
          encoding : "utf-8",
          handleAs : "json-comment-filtered",
          headers : {
            X_REQUESTED_WITH : "XMLHttpRequest"
          },
          load : function(response, ioArgs) {
            var html = "";
            if (dojo.isArray(response) && response.length > 0) {
              if (response[0] == "PermissionError") {
                html += "<ul>";
                html += "<li><span class='caution'>" + response[1]
                    + "</span></li>";
                html += "</ul>";
              } else {
                html += "<ul>";
                dojo.forEach(response, function(msg) {
                  html += "<li><span class='caution'>" + msg + "</span></li>";
                });
                html += "</ul>";
              }
            }
            callback.call(callback, html);

            obj_indicator = dojo.byId(indicator_id + portlet_id);
            if (obj_indicator) {
              dojo.style(obj_indicator, "display", "none");
            }

            if (html != "") {
              aimluck.io.disableForm(form, false);
            }
          },
          error : function(error) {
            obj_indicator = dojo.byId(indicator_id + portlet_id);
            if (obj_indicator) {
              obj_indicator.innerHTML = "エラーが発生しました";
              obj_indicator.style.paddingLeft = "0px";
              obj_indicator.style.backgroundImage = "none";
            }
          }
        });
  } catch (E) {
  }
  ;

  return false;
}
