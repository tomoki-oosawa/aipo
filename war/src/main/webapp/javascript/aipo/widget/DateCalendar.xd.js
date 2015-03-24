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
dojo._xdResourceLoaded({
depends: [["provide", "aipo.widget.DateCalendar"],
["require", "dijit._Calendar"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.widget.DateCalendar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DateCalendar"] = true;

dojo.provide("aipo.widget.DateCalendar");

dojo.require("dijit._Calendar");
dojo.require("dojo.string");
dojo.requireLocalization("aipo", "locale");

dojo.declare("aipo.widget.DateCalendar", [dijit._Calendar], {
    dateId: "",
    callback: function(){},
    templateString:"<table cellspacing=\"0\" cellpadding=\"0\" class=\"dijitCalendarContainer\">\n\t<thead>\n\t\t<tr class=\"dijitReset dijitCalendarMonthContainer\" valign=\"top\">\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"decrementMonth\">\n\t\t\t\t<span class=\"dijitInline dijitCalendarIncrementControl dijitCalendarDecrease\"><span dojoAttachPoint=\"decreaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarDecreaseInner\">-</span></span>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' colspan=\"5\">\n\t\t\t\t<div dojoAttachPoint=\"monthLabelSpacer\" class=\"dijitCalendarMonthLabelSpacer\"></div>\n\t\t\t\t<div dojoAttachPoint=\"monthLabelNode\" class=\"dijitCalendarMonth\"></div>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"incrementMonth\">\n\t\t\t\t<div class=\"dijitInline dijitCalendarIncrementControl dijitCalendarIncrease\"><span dojoAttachPoint=\"increaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarIncreaseInner\">+</span></div>\n\t\t\t</th>\n\t\t</tr>\n\t\t<tr>\n\t\t\t<th class=\"dijitReset dijitCalendarDayLabelTemplate\"><span class=\"dijitCalendarDayLabel\"></span></th>\n\t\t</tr>\n\t</thead>\n\t<tbody dojoAttachEvent=\"onclick: _onDayClick\" class=\"dijitReset dijitCalendarBodyContainer\">\n\t\t<tr class=\"dijitReset dijitCalendarWeekTemplate\">\n\t\t\t<td class=\"dijitReset dijitCalendarDateTemplate\"><span class=\"dijitCalendarDateLabel\"></span></td>\n\t\t</tr>\n\t</tbody>\n\t<tfoot class=\"dijitReset dijitCalendarYearContainer\">\n\t\t<tr>\n\t\t\t<td class='dijitReset' valign=\"top\" colspan=\"7\">\n\t\t\t\t<h3 class=\"dijitCalendarYearLabel\">\n\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\" class=\"dijitInline dijitCalendarPreviousYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"currentYearLabelNode\" class=\"dijitInline dijitCalendarSelectedYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" class=\"dijitInline dijitCalendarNextYear\"></span>\n\t\t\t\t</h3>\n\t\t\t</td>\n\t\t</tr>\n\t</tfoot>\n</table>\t\n",
    postCreate: function(){
      this.inherited(arguments);
    },
    onChange: function(/*date*/date){
        this.onChangeNoCallback(date);
        this.callback(date);
    },
    onValueSelected: function(/*date*/date){
    	this.onChange(date);
    },
    onChangeNoCallback: function(date){
        var tyear = date.getFullYear();
        var tmonth = 1+date.getMonth();
        var tdate = date.getDate();
        var dayNames = dojo.date.locale.getNames('days', this.dayWidth, 'standAlone', this.lang);
        var tday = dayNames[date.getDay()];

		var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
		var dateString = dojo.string.substitute(nlsStrings.DATE_FORMAT, {
			year : tyear,
			month : tmonth,
			date : tdate,
			day:tday
		});
        var viewvalue = dojo.byId(this.dateId+'_view');
        viewvalue.innerHTML=dateString;
        var hiddendate = dojo.byId(this.dateId);
        hiddendate.value = tyear+"/"+tmonth+"/"+tdate;
        var hiddendate_year = dojo.byId(this.dateId+'_year');
        hiddendate_year.value = tyear;
        var hiddendate_month = dojo.byId(this.dateId+'_month');
        hiddendate_month.value = tmonth;
        var hiddendate_day = dojo.byId(this.dateId+'_day');
        hiddendate_day.value = tdate;

        dojo.byId(this.dateId+'_flag').checked = false;
    },
    disabledCalendar: function(/*boolean*/bool) {
        if(bool){
           var viewvalue = dojo.byId(this.dateId+'_view');
           var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
           //"---- 年 -- 月 -- 日      "
           viewvalue.innerHTML =nlsStrings.DISABLED_DATE;
           var hiddendate_year = dojo.byId(this.dateId+'_year');
           hiddendate_year.value = "";
           var hiddendate_month = dojo.byId(this.dateId+'_month');
           hiddendate_month.value = "";
           var hiddendate_day = dojo.byId(this.dateId+'_day');
           hiddendate_day.value = "";

           this.value = "";
           if(! dojo.byId(this.dateId+'_flag').checked) {
              dojo.byId(this.dateId+'_flag').checked = true;
           }
        }else{
           var hiddendate = dojo.byId(this.dateId);
           if( (!hiddendate.value) || (hiddendate.value=="") ) {
              this.setValue(new Date());
           }else{
	           var tmpdate = hiddendate.value.split("/");
	           if(tmpdate.length == 3){
	               var tyear = tmpdate[0];
	               var tmonth = tmpdate[1]-1;
	               var tday = tmpdate[2];

	               var tdate = new Date(tyear, tmonth, tday);
	               this.setValue(tdate);
	           }
           }
       }
    },
    clearDate: function(){
       this.value = null;
    }
});

}

}});