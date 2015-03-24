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
depends: [["provide", "aipo.widget.DropdownMemberpicker"],
["require", "aimluck.widget.Dropdown"],
["require", "aipo.widget.MemberSelectList"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.widget.DropdownMemberpicker"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DropdownMemberpicker"] = true;

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

}

}});