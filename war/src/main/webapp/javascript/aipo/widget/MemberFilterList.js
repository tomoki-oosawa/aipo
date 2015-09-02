/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
 * http://www.aipo.com
 *
 * aipo.widget.MemberFilterList program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * aipo.widget.MemberFilterList program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with aipo.widget.MemberFilterList program.  If not, see <http://www.gnu.org/licenses/>.
 */

dojo.provide("aipo.widget.MemberFilterList");

dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

aipo.widget.MemberFilterList.init = function(memberFromId, params1, groupSelectId, params2){
    aimluck.io.createLists(memberFromId, params1);
    aimluck.io.createOptions(groupSelectId, params2);
}

aipo.widget.MemberFilterList.setup = function(widgetId, memberFromId, memberToId){
    var picker = dojo.byId(widgetId);
    if (picker) {
        var select = dojo.byId(memberFromId);
        var i;
        var s_o = select.options;
        if (s_o.length == 1 && s_o[0].value == "")
            return;
        for (i = 0; i < s_o.length; i++) {
        	aipo.widget.MemberFilterList.addOptionSync(s_o[i].value, s_o[i].text, true, memberToId);
        }
    }
}

aipo.widget.MemberFilterList.addOptionSync = function(value, text, is_selected, memberToId) {
  var select = dojo.byId(memberToId);
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
}

aipo.widget.MemberFilterList.changeGroup = function(select, memberFromId) {
  var group_name = select.options[select.selectedIndex].value;
  var url = aipo.widget.MemberFilterList.changeGroupUrl+"&groupname="+group_name;
  var params = {
    url: url,
    key: aipo.widget.MemberFilterList.memberFromOptionKey,
    value: aipo.widget.MemberFilterList.memberFromOptionValue,
    clickEvent: aipo.widget.MemberFilterList.clickEvent,
    selectedId: aipo.widget.MemberFilterList.memberToId,
    name: aipo.widget.MemberFilterList.memberFromId,
    userId: aipo.widget.MemberFilterList.memberFromOptionUserId,
    image_flag: aipo.widget.MemberFilterList.memberFromOptionImageFlag,
    image_version: aipo.widget.MemberFilterList.memberFromOptionImageVersionParam,
    default_image: aipo.widget.MemberFilterList.memberFromOptionDefaultImage,
    child_html: aipo.widget.MemberFilterList.childTemplateString,
    indicator: aipo.widget.MemberFilterList.widgetId + "-memberlist-indicator"
  };
  aimluck.io.createLists(memberFromId, params);
}

aipo.widget.MemberFilterList.setWrapperHeight = function(){
    var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
  	  var wrapper = document.getElementById('wrapper');
  	  wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

aipo.widget.MemberFilterList.onMemberCheck = function(/*Event*/ evt){
	aipo.widget.MemberFilterList.changeMember(evt.target, dojo.byId(aipo.widget.MemberFilterList.memberToId));
    aipo.widget.MemberFilterList.setWrapperHeight();
}

aipo.widget.MemberFilterList.changeMember = function(select_member_from, select_member_to) {
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
           if (aipo.widget.MemberFilterList.memberLimit != 0 && select_member_to.options.length >= aipo.widget.MemberFilterList.memberLimit) return;
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
            if (aipo.widget.MemberFilterList.memberLimit != 0 && select_member_to.options.length >= aipo.widget.MemberFilterList.memberLimit) return;
            select_member_to.insertBefore(option, t_o[t_o.length]);
        }
  }
}
