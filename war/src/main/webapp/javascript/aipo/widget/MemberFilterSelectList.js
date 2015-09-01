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
if(!dojo._hasResource["aipo.widget.MemberFilterSelectList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.MemberFilterSelectList"] = true;

dojo.provide("aipo.widget.MemberFilterSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

dojo.declare("aipo.widget.MemberFilterSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberFromOptionUserId: "",
    memberFromOptionImageFlag: "",
    memberFromOptionImageVersionParam: "",
    memberFromOptionDefaultImage: "",
    memberToId: "",
    memberLimit: 0,
    clickEvent: "",
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    childTemplateString: "",
    /*ユーザー選択部分のdiv要素*/
//    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\" style=\"table-layout: fixed;\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >${memberToTitle}</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix mb5\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\""+nlsStrings.DELETEBTN_STR+"\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div style=\"display: none;\" id=\"${widgetId}-memberlist-indicator\" class=\"indicator alignleft\">読み込み中</div><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\""+nlsStrings.ADDBTN_STR+"\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><div class=\"memberPopupDiv_ver3\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select><div class=\"head\"><input type=\"checkbox\" onclick=\"aimluck.io.switchCheckbox(this);\" value=\"\" name=\"\" dojoAttachEvent=\"onclick:onMemberCheck\"></div><div style=\"display: none;\" id=\"${widgetId}-memberlist-indicator\" class=\"indicator alignleft\">読み込み中</div><ul class=\"memberPopupList\" id=\"${memberFromId}\"></ul><select multiple=\"multiple\" style=\"display:none\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div></div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue,
          clickEvent: this.clickEvent,
          selectedId: this.memberToId,
          name: this.memberFromId,
          userId: this.memberFromOptionUserId,
          image_flag: this.memberFromOptionImageFlag,
          image_version: this.memberFromOptionImageVersionParam,
          default_image: this.memberFromOptionDefaultImage,
          child_html: this.childTemplateString,
          indicator: this.widgetId + "-memberlist-indicator"
        };
        aimluck.io.createLists(this.memberFromId, params);
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue }
        };
        aimluck.io.createOptions(this.groupSelectId, params);
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
    changeMember:function(checkbox, select_member_to) {
        if (document.all) {
              var t_o = select_member_to.options;
              if (checkbox.value == "") return;
              if(checkbox.checked){
                  var iseq = false;

                  for( j = 0 ; j < t_o.length; j ++ ) {
                  if( t_o[j].value == checkbox.value ) {
                      iseq = true;
                      break;
                  }
                  }

                  if(iseq) return;
                  var option = document.createElement("OPTION");
                  option.value = checkbox.value;
                  option.text = checkbox.name;
                  option.selected = true;
                  if (t_o.length == 1 && t_o[0].value == ""){
                      t_o.remove(0);
                  }
                  if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                  t_o.add(option, t_o.length);
                }else{
                      for(i = 0 ;i < t_o.length; i ++ ) {
                          if( t_o[i].selected ) {
                          t_o.remove(i);
                            i -= 1;
                        }
                    }
                  }
        } else {
              var t_o = select_member_to.options;
              if (checkbox.value == "") return;
              if(checkbox.checked){
                  var iseq = false;

                  for( j = 0 ; j < t_o.length; j ++ ) {
                  if( t_o[j].value == checkbox.value ) {
                      iseq = true;
                      break;
                  }
                  }

                  if(iseq) return;
                  var option = document.createElement("OPTION");
                  option.value = checkbox.value;
                  option.text = checkbox.name;
                  option.selected = true;
              if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                  select_member_to.removeChild(select_member_to.options[0]);
              }
                  if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                  select_member_to.insertBefore(option, t_o[t_o.length]);
        }else{
            for(i = 0 ;i < t_o.length; i ++ ) {
                if( t_o[i].selected ) {
                  select.removeChild(t_o[i]);
                  i -= 1;
                }
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
        clickEvent: this.clickEvent,
        selectedId: this.memberToId,
        name: this.memberFromId,
        userId: this.memberFromOptionUserId,
        image_flag: this.memberFromOptionImageFlag,
        image_version: this.memberFromOptionImageVersionParam,
        default_image: this.memberFromOptionDefaultImage,
        child_html: this.childTemplateString,
        indicator: this.widgetId + "-memberlist-indicator"
      };
      aimluck.io.createLists(this.memberFromId, params);
    },
    onMemberCheck: function(/*Event*/ evt){
    	console.log("onMemberCheck");
    	console.log(evt.target);
	    this.changeMember(evt.target, dojo.byId(this.memberToId));
        this.setWrapperHeight();
     },
    setWrapperHeight: function() {
        var modalDialog = document.getElementById('modalDialog');
        if(modalDialog) {
      	  var wrapper = document.getElementById('wrapper');
      	  wrapper.style.minHeight = modalDialog.clientHeight + 'px';
        }
    }
});

}
