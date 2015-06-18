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
if(!dojo._hasResource["aipo.widget.DropdownDatepicker"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DropdownDatepicker"] = true;

dojo.provide("aipo.widget.DropdownDatepicker");

dojo.require("aimluck.widget.Dropdown");

dojo.require("aipo.widget.DateCalendar");
dojo.require("dojo.date.locale");
dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 */
dojo.declare("aipo.widget.DropdownDatepicker", [aimluck.widget.Dropdown], {
    dateId: "",
    dateValue: "",
    initValue: "",
    displayCheck: "",
    iconURL: "",
    iconAlt: "",
    callback: function(){},
    listWidgetId:"datewidget",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"><div dojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t style=\"float:left;\"><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><a href=\"javascript:void(0)\" class=\"auiButtonIcon\"><span><i class=\"icon-calendar\"></i></span></a>\n\t</span></div></div><div class=\"alignleft\"><span name=\"${dateId}_view\" id=\"${dateId}_view\" dojoAttachPoint=\"inputNode\" style=\"vertical-align:middle;background:#ffffff ;border:0px;\" autocomplete=\"off\" readonly=\"readonly\"></span> <span style=\"display:${displayCheck}\"><input name=\"${dateId}_check\" type=\"checkbox\" value=\"TRUE\" id=\"${dateId}_flag\" dojoAttachEvent=\"onclick:onCheckBlank\" /><label for=\"${dateId}_flag\">"+nlsStrings.NOT_SPECIFIED_STR+"</label></span><input type=\"hidden\" id=\"${dateId}\" name=\"${dateId}\" value=\"${dateValue}\" dojoAttachPoint=\"valueNode\" /><input type=\"hidden\" id=\"${dateId}_year\" name=\"${dateId}_year\" value=\"\" dojoAttachPoint=\"valueYearNode\" /><input type=\"hidden\" id=\"${dateId}_month\" name=\"${dateId}_month\" value=\"\" dojoAttachPoint=\"valueMonthNode\" /><input type=\"hidden\" id=\"${dateId}_day\" name=\"${dateId}_day\" value=\"\" dojoAttachPoint=\"valueDayNode\" /></div></div>\n",

    _openDropDown:function(){
    	//android時に他のフォームを無効にする。

    	aimluck.widget.Dropdown.prototype._openDropDown.apply(this);
    	if(aipo.userAgent.isAndroid()){
        	//input,select,buttonをdisable化
        	dojo.query("input,select,button").forEach(function(val,index){
        		val.disabled=true;
        	});
    	}
    },
    _closeDropDown:function(){
    	aimluck.widget.Dropdown.prototype._closeDropDown.apply(this);
    	if(aipo.userAgent.isAndroid()){
        	//input,select,buttonをdisable化
        	dojo.query("input,select,button:not(.disabled)").forEach(function(val,index){
        		val.disabled=false;
        	});
    	}
    },
    postCreate: function(){
        this.inherited(arguments);
        var params = {
          widgetId:this.listWidgetId,
          dateId:this.dateId,
          callback:this.callback
        };
        this.dropDown = new aipo.widget.DateCalendar(params, this.listWidgetId);

        if(this.initValue != ""){
            var tmpdate = this.initValue.split("/");
            if(tmpdate.length == 3){
                var tyear = tmpdate[0];
                var tmonth = tmpdate[1]-1;
                var tday = tmpdate[2];

                var datevalue = dojo.byId(this.dateId);
                datevalue.value = this.initValue;

                this.dropDown.clearDate();
                this.dropDown.setValue(new Date(tyear, tmonth, tday));
            }
        }else{
          this.dropDown.disabledCalendar(true);
        }
    },
    onCheckBlank: function(/*evt*/ e){
        this.dropDown.disabledCalendar(dojo.byId(this.dateId+'_flag').checked);
    }
});

}
