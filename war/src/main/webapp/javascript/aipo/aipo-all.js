/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */
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

window.aipo = window.aipo || {};

aipo.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = aipo;


    for (var i=(levels[0] == "aipo") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

djConfig = { isDebug: false };

var ptConfig = [];

aipo.onReceiveMessage = function(msg, group){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        arrDialog.hide();
        aipo.portletReload(group);
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.portletReload = function(group, portletId) {
    for(var index in ptConfig) {
        if (index != portletId) {
            if(ptConfig[index].group == group) {
                ptConfig[index].reloadFunction.call(ptConfig[index].reloadFunction, index);
            }
        }
    }
};

aipo.reloadPage = function(portletId) {
    if( typeof ptConfig[portletId].reloadUrl == "undefined") {
      aipo.viewPage(ptConfig[portletId].initUrl, portletId);
    } else {
      aipo.viewPage(ptConfig[portletId].reloadUrl, portletId);
    }
};

aipo.viewPage = function(url, portletId, params) {
     var portlet = dijit.byId('portlet_' + portletId);
     if(! portlet){
       portlet = new aimluck.widget.Contentpane({},'portlet_' + portletId);
     }
     
     if(portlet){
       ptConfig[portletId].reloadUrl= url;
       
       if(params){
       	 for(i = 0 ; i < params.length; i++ ) {
       		portlet.setParam(params[i][0], params[i][1]);
       	 }
       }
       
       portlet.viewPage(url);
     }
};

aipo.errorTreatment = function(jsondata, url) {
    if (jsondata["error"]) {
        if(jsondata["error"]== 1) {
           window.location.href = url;
        } else {
            return true;
        }
        return false;
    } else {
        return true;
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */
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

dojo.provide("aipo.common");

aipo.common.showDialog = function(url, portlet_id, callback) {
    var arrDialog = dijit.byId("modalDialog");
    
    if(! arrDialog){
       arrDialog = new aimluck.widget.Dialog({widgetId:'modalDialog', _portlet_id: portlet_id, _callback:callback}, "modalDialog");
    }else{
       arrDialog.setCallback(portlet_id, callback);
    }
     
    if(arrDialog){
      arrDialog.setHref(url);
      arrDialog.show();
    }
};

aipo.common.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
      arrDialog.hide();
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */
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

dojo.provide("aipo.io");

aipo.io.loadHtml = function(url, params, portletId){
    dojo.xhrGet({
        url: url,
        transport: "ScriptSrcTransport",
        jsonParamName: "callback",
        content: params,
        method: "get", 
        mimetype: "application/json",
        encoding: "utf-8",
        load: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = data.body;
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        error: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u8aad\u307f\u8fbc\u307f\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeout: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8\u3057\u307e\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeoutSeconds: 10
    });
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DateCalendar.js
 */
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

dojo.provide("aipo.widget.DateCalendar");

dojo.require("dijit._Calendar");

dojo.declare("aipo.widget.DateCalendar", [dijit._Calendar], {
    dateId: "",
    templateString:"<table cellspacing=\"0\" cellpadding=\"0\" class=\"dijitCalendarContainer\">\n\t<thead>\n\t\t<tr class=\"dijitReset dijitCalendarMonthContainer\" valign=\"top\">\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"decrementMonth\">\n\t\t\t\t<span class=\"dijitInline dijitCalendarIncrementControl dijitCalendarDecrease\"><span dojoAttachPoint=\"decreaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarDecreaseInner\">-</span></span>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' colspan=\"5\">\n\t\t\t\t<div dojoAttachPoint=\"monthLabelSpacer\" class=\"dijitCalendarMonthLabelSpacer\"></div>\n\t\t\t\t<div dojoAttachPoint=\"monthLabelNode\" class=\"dijitCalendarMonth\"></div>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"incrementMonth\">\n\t\t\t\t<div class=\"dijitInline dijitCalendarIncrementControl dijitCalendarIncrease\"><span dojoAttachPoint=\"increaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarIncreaseInner\">+</span></div>\n\t\t\t</th>\n\t\t</tr>\n\t\t<tr>\n\t\t\t<th class=\"dijitReset dijitCalendarDayLabelTemplate\"><span class=\"dijitCalendarDayLabel\"></span></th>\n\t\t</tr>\n\t</thead>\n\t<tbody dojoAttachEvent=\"onclick: _onDayClick\" class=\"dijitReset dijitCalendarBodyContainer\">\n\t\t<tr class=\"dijitReset dijitCalendarWeekTemplate\">\n\t\t\t<td class=\"dijitReset dijitCalendarDateTemplate\"><span class=\"dijitCalendarDateLabel\"></span></td>\n\t\t</tr>\n\t</tbody>\n\t<tfoot class=\"dijitReset dijitCalendarYearContainer\">\n\t\t<tr>\n\t\t\t<td class='dijitReset' valign=\"top\" colspan=\"7\">\n\t\t\t\t<h3 class=\"dijitCalendarYearLabel\">\n\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\" class=\"dijitInline dijitCalendarPreviousYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"currentYearLabelNode\" class=\"dijitInline dijitCalendarSelectedYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" class=\"dijitInline dijitCalendarNextYear\"></span>\n\t\t\t\t</h3>\n\t\t\t</td>\n\t\t</tr>\n\t</tfoot>\n</table>\t\n",
    //templateString:"<table cellspacing=\"0\" cellpadding=\"0\" class=\"calendarBodyContainer\">\n\t<thead>\n\t\t<tr class=\"dijitReset monthLabelContainer\" valign=\"top\">\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"decrementMonth\">\n\t\t\t\t<span class=\"dijitInline dijitCalendarIncrementControl dijitCalendarDecrease\"><span dojoAttachPoint=\"decreaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarDecreaseInner\">-</span></span>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' colspan=\"5\">\n\t\t\t\t<div dojoAttachPoint=\"monthLabelSpacer\" class=\"dijitCalendarMonthLabelSpacer\"></div>\n\t\t\t\t<div dojoAttachPoint=\"monthLabelNode\" class=\"dijitCalendarMonth\"></div>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"incrementMonth\">\n\t\t\t\t<div class=\"dijitInline dijitCalendarIncrementControl dijitCalendarIncrease\"><span dojoAttachPoint=\"increaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarIncreaseInner\">+</span></div>\n\t\t\t</th>\n\t\t</tr>\n\t\t<tr>\n\t\t\t<th class=\"dijitReset dijitCalendarDayLabelTemplate\"><span class=\"dijitCalendarDayLabel\"></span></th>\n\t\t</tr>\n\t</thead>\n\t<tbody dojoAttachEvent=\"onclick: _onDayClick\" class=\"dijitReset dijitCalendarBodyContainer\">\n\t\t<tr class=\"dijitReset dijitCalendarWeekTemplate\">\n\t\t\t<td class=\"dijitReset dijitCalendarDateTemplate\"><span class=\"dijitCalendarDateLabel\"></span></td>\n\t\t</tr>\n\t</tbody>\n\t<tfoot class=\"dijitReset dijitCalendarYearContainer\">\n\t\t<tr>\n\t\t\t<td class='dijitReset' valign=\"top\" colspan=\"7\">\n\t\t\t\t<h3 class=\"dijitCalendarYearLabel\">\n\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\" class=\"dijitInline dijitCalendarPreviousYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"currentYearLabelNode\" class=\"dijitInline dijitCalendarSelectedYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" class=\"dijitInline dijitCalendarNextYear\"></span>\n\t\t\t\t</h3>\n\t\t\t</td>\n\t\t</tr>\n\t</tfoot>\n</table>\t\n",
    postCreate: function(){
      this.inherited(arguments);

    },
    onChange: function(/*date*/date){
       var tyear = date.getFullYear();
       var tmonth = 1+date.getMonth();
       var tdate = date.getDate();
       var dayNames = dojo.date.locale.getNames('days', this.dayWidth, 'standAlone', this.lang);
       var tday = dayNames[date.getDay()];
    
       var viewvalue = dojo.byId(this.dateId+'_view');
       viewvalue.innerHTML = tyear+"\u5e74"+tmonth+"\u6708"+tdate+"\u65e5\uff08"+tday+"\uff09&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";  
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
           viewvalue.innerHTML = "---- \u5e74 -- \u6708 -- \u65e5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

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

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DateCalendar.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupSelectList.js
 */
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

dojo.provide("aipo.widget.GroupSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.GroupSelectList", [dijit._Widget, dijit._Templated], {
    inputWidth: "95%",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    widgetId:"",
    selectId:"",
    inputId:"",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"groupPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">${memberToTitle}</div><div class=\"memberlistFromTop\">${memberFromTitle}</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",  
//    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"facilityPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">所属施設一覧</div><div class=\"memberlistFromTop\">施設一覧</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"tmp_facility_to\" id=\"tmp_facility_to\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"tmp_facility_from\" id=\"tmp_facility_from\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"button_facility_remove\" name=\"button_facility_remove\" type=\"button\" class=\"button\" value=\"　削除　\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"button_facility_add\" name=\"button_facility_add\" type=\"button\" class=\"button\" value=\"　＜ 追加　\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",    
    postCreate: function(){
        this.id = this.widgetId;
        
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (document.all) {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
              f_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                    selectsub.removeChild(f_o[i]);
                i -= 1;
                }
            }
      }
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.selectId));
      this.inputMemberSync();
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
       this.inputMemberSync();
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberSelectList.js
 */
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

dojo.provide("aipo.widget.MemberSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    inputWidth: "95%",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    selectId: "",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberToTitle: "",
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >${memberToTitle}</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",    
    //templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >参加メンバー一覧</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"　削除　\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"　＜ 追加　\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",    
    postCreate: function(){
        this.id = this.widgetId;
    
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
        
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue }
        };
        aimluck.io.createOptions(this.groupSelectId, params); 
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (document.all) {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
              f_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                    selectsub.removeChild(f_o[i]);
                i -= 1;
                }
            }
      }
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.selectId));
      this.inputMemberSync();
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
       this.inputMemberSync();
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownDatepicker.js
 */
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

dojo.provide("aipo.widget.DropdownDatepicker");

dojo.require("aimluck.widget.Dropdown");

dojo.require("aipo.widget.DateCalendar");
dojo.require("dojo.date.locale");

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
    listWidgetId:"datewidget",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"><div dojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t style=\"float:left;\"><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span></div></div><div class=\"alignleft\"><span name=\"${dateId}_view\" id=\"${dateId}_view\" dojoAttachPoint=\"inputNode\" style=\"vertical-align:middle;background:#ffffff ;border:0px;\" autocomplete=\"off\" readonly=\"readonly\"></span><span style=\"display:${displayCheck}\"><input name=\"${dateId}_check\" type=\"checkbox\" value=\"TRUE\" id=\"${dateId}_flag\" dojoAttachEvent=\"onclick:onCheckBlank\" /><label for=\"${dateId}_flag\"> 指定しない</label></span><input type=\"hidden\" id=\"${dateId}\" name=\"${dateId}\" value=\"${dateValue}\" dojoAttachPoint=\"valueNode\" /><input type=\"hidden\" id=\"${dateId}_year\" name=\"${dateId}_year\" value=\"\" dojoAttachPoint=\"valueYearNode\" /><input type=\"hidden\" id=\"${dateId}_month\" name=\"${dateId}_month\" value=\"\" dojoAttachPoint=\"valueMonthNode\" /><input type=\"hidden\" id=\"${dateId}_day\" name=\"${dateId}_day\" value=\"\" dojoAttachPoint=\"valueDayNode\" /></div></div>\n",
    postCreate: function(){
        this.inherited(arguments);

        var params = {
          widgetId:this.listWidgetId,
          dateId:this.dateId
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

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownDatepicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownGrouppicker.js
 */
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

dojo.provide("aipo.widget.DropdownGrouppicker");

dojo.require("aimluck.widget.Dropdown");
dojo.require("aipo.widget.GroupSelectList");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 * buttonAddId:"button_member_add",
 * buttonRemoveId:"button_member_remove",
 * memberFromId:"tmp_member_from",
 * memberToId:"tmp_member_to",
 * memberFromUrl:this.memberFromUrl,
 * memberFromOptionKey:"name",
 * memberFromOptionValue:"aliasName",
 * groupSelectId:"tmp_group",
 * groupSelectOptionKey:"groupId",
 * groupSelectOptionValue:"name",
 * memberGroupUrl:this.memberGroupUrl,
 * changeGroupUrl:this.changeGroupUrl
 */
dojo.declare("aipo.widget.DropdownGrouppicker", [aimluck.widget.Dropdown], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    iconURL: "",
    iconAlt: "",
    selectId:"",
    inputId:"",
    inputValue: "",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    groupSelectId:"",
    groupSelectOptionKey:"",
    groupSelectOptionValue:"",
    memberGroupUrl:"",
    changeGroupUrl:"",
    listWidgetId:"",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",
    postCreate: function(){      

      var fparams = {
          widgetId:this.listWidgetId,
          selectId:this.selectId,
          inputId:this.inputId,
          buttonAddId:this.buttonAddId,
          buttonRemoveId:this.buttonRemoveId,
          memberFromTitle:this.memberFromTitle,
          memberFromId:this.memberFromId,
          memberToTitle:this.memberToTitle,
          memberToId:this.memberToId,
          memberFromUrl:this.memberFromUrl,
          memberFromOptionKey:this.memberFromOptionKey,
          memberFromOptionValue:this.memberFromOptionValue
      };

      var listWidget = dijit.byId(this.listWidgetId);
      if(listWidget){
        this.dropDown = listWidget;
        var select = dojo.byId(listWidget.selectId);
        this.removeAllOptions(select);
        select = dojo.byId(listWidget.memberToId);
        this.removeAllOptions(select);
      }else{
        this.dropDown = new aipo.widget.GroupSelectList(fparams, this.listWidgetId);
      }
      
      this.inherited(arguments);
    },
	removeAllOptions:function(select){
	  var i;
	  if (document.all) {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      t_o.remove(i);
	      i -= 1;
	    }
	  } else {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      select.removeChild(t_o[i]);
	      i -= 1; 
	    }
	  }
	},
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        var i = 0;
        var len = t_o.length;
        if(len <= 0) return;
        for(i = 0 ;i < len; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownGrouppicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownMemberpicker.js
 */
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

dojo.provide("aipo.widget.DropdownMemberpicker");

dojo.require("aimluck.widget.Dropdown");
dojo.require("aipo.widget.MemberSelectList");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 * buttonAddId:"button_member_add",
 * buttonRemoveId:"button_member_remove",
 * memberFromId:"tmp_member_from",
 * memberToId:"tmp_member_to",
 * memberFromUrl:this.memberFromUrl,
 * memberFromOptionKey:"name",
 * memberFromOptionValue:"aliasName",
 * groupSelectId:"tmp_group",
 * groupSelectOptionKey:"groupId",
 * groupSelectOptionValue:"name",
 * memberGroupUrl:this.memberGroupUrl,
 * changeGroupUrl:this.changeGroupUrl
 */
dojo.declare("aipo.widget.DropdownMemberpicker", [aimluck.widget.Dropdown], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    iconURL: "",
    iconAlt: "",
    selectId:"",
    inputId:"",
    inputValue: "",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromId:"",
    memberToTitle: "",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    groupSelectId:"",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey:"",
    groupSelectOptionValue:"",
    memberGroupUrl:"",
    changeGroupUrl:"",
    listWidgetId:"memberlistwidget",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",
    postCreate: function(){      
      var userparams = {
          widgetId:this.listWidgetId,
          selectId:this.selectId,
          inputId:this.inputId,
          buttonAddId:this.buttonAddId,
          buttonRemoveId:this.buttonRemoveId,
          memberFromId:this.memberFromId,
          memberToTitle:this.memberToTitle,
          memberToId:this.memberToId,
          memberFromUrl:this.memberFromUrl,
          memberFromOptionKey:this.memberFromOptionKey,
          memberFromOptionValue:this.memberFromOptionValue,
          groupSelectId:this.groupSelectId,
          groupSelectPreOptionKey:this.groupSelectPreOptionKey,
          groupSelectPreOptionValue:this.groupSelectPreOptionValue,
          groupSelectOptionKey:this.groupSelectOptionKey,
          groupSelectOptionValue:this.groupSelectOptionValue,
          memberGroupUrl:this.memberGroupUrl,
          changeGroupUrl:this.changeGroupUrl
      };
     
      var listWidget = dijit.byId(this.listWidgetId);
      if(listWidget){
        this.dropDown = listWidget;
        var select = dojo.byId(listWidget.selectId);
        this.removeAllOptions(select);
        select = dojo.byId(listWidget.memberToId);
        this.removeAllOptions(select);
      }else{
        this.dropDown = new aipo.widget.MemberSelectList(userparams, this.listWidgetId);
      }

      this.inherited(arguments);
    },
	removeAllOptions:function(select){
	  var i;
	  if (document.all) {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      t_o.remove(i);
	      i -= 1;
	    }
	  } else {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      select.removeChild(t_o[i]);
	      i -= 1; 
	    }
	  }
	},
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        var i = 0;
        var len = t_o.length;
        if(len <= 0) return;
        for(i = 0 ;i < len; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownMemberpicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupNormalSelectList.js
 */
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

dojo.provide("aipo.widget.GroupNormalSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.GroupNormalSelectList", [dijit._Widget, dijit._Templated], {
    inputWidth: "95%",
    memberLimit: 0,
    changeGroupUrl: "",
    widgetId:"",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"groupPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:335px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">${memberToTitle}</div><div class=\"memberlistFromTop\">${memberFromTitle}</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c \u8ffd\u52a0\u3000\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
          select.options.remove(0);
        }
        select.add(option, select.options.length);  
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
      }
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
	                if( t_o[j].value == f_o[i].value ) {
	                    iseq = true;
	                    break;
	                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
	            if (t_o.length == 1 && t_o[0].value == ""){
	                    t_o.remove(0);
	            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
	                if( t_o[j].value == f_o[i].value ) {
	                    iseq = true;
	                    break;
	                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
	            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
	                select_member_to.removeChild(select_member_to.options[0]);
	            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              t_o.remove(i);
              i -= 1;
            }
          }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              select.removeChild(t_o[i]);
              i -= 1;
            }
          }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              t_o.remove(i);
              i -= 1;
            }
          }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              select.removeChild(t_o[i]);
              i -= 1;
            }
          }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      if (document.all) {
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
            t_o.remove(i);
            i -= 1;
          }
        }
      } else {
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
            select.removeChild(t_o[i]);
            i -= 1;
          }
        }
      }
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
    }
  
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupNormalSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberNormalSelectList.js
 */
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

dojo.provide("aipo.widget.MemberNormalSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberNormalSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberToTitle: "",
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:335px;\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >${memberToTitle}</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div style=\"display: none;\" id=\"${widgetId}-memberlist-indicator\" class=\"indicator alignleft\">読み込み中</div><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue,
          indicator: this.widgetId + "-memberlist-indicator"
        };
        aimluck.io.createOptions(this.memberFromId, params);
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue }
        };
        aimluck.io.createOptions(this.groupSelectId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
          }
          select.add(option, select.options.length);
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
      }
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
        }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                select.removeChild(t_o[i]);
                i -= 1;
              }
          }
      }
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue,
        indicator: this.widgetId + "-memberlist-indicator"
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberNormalSelectList.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/
