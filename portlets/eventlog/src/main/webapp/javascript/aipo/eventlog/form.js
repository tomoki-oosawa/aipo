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
dojo.provide("aipo.eventlog");
dojo.require("dojo.string");
dojo.requireLocalization("aipo", "locale");

aipo.eventlog.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('eventlog');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.eventlog.downloadCvn = function(flag_over_size,eventlog_max,url){
	var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	var confirmString = dojo.string.substitute(nlsStrings.EVENTLOG_STR, {
		max : eventlog_max
	  });

	if(flag_over_size){
		alert(confirmString);
	}else{
		window.location.href=url;
	}
}
aipo.eventlog.onChangeDate = function(base_url, p_id){
    var select_year = dojo.byId('start_date_year');
    var select_month = dojo.byId('start_date_month');
    var select_day = dojo.byId('start_date_day');
    var s_year = select_year.options[select_year.selectedIndex].value;
    var s_month = select_month.options[select_month.selectedIndex].value;
    var s_day = select_day.options[select_day.selectedIndex].value;

    select_year = dojo.byId('end_date_year');
    select_month = dojo.byId('end_date_month');
    select_day = dojo.byId('end_date_day');
    var e_year = select_year.options[select_year.selectedIndex].value;
    var e_month = select_month.options[select_month.selectedIndex].value;
    var e_day = select_day.options[select_day.selectedIndex].value;
    var exec_url = base_url+"&start_date_year="+s_year+"&start_date_month="+s_month+"&start_date_day="+s_day
    +"&end_date_year="+e_year+"&end_date_month="+e_month+"&end_date_day="+e_day;

    aipo.viewPage(exec_url, p_id);
}