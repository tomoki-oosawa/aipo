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
depends: [["provide", "aipo.widget.DropdownMemberFacilitypicker"],
["require", "aimluck.widget.Dropdown"],
["require", "aipo.widget.MemberFacilitySelectList"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.widget.DropdownMemberFacilitypicker"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DropdownMemberFacilitypicker"] = true;

dojo.provide("aipo.widget.DropdownMemberFacilitypicker");

dojo.require("aimluck.widget.Dropdown");
dojo.require("aipo.widget.MemberFacilitySelectList");

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

dojo.declare("aipo.widget.DropdownMemberFacilitypicker", [aimluck.widget.Dropdown], {
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
    tmpPortretId: "cinit",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"></span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n<span id=\"adduser-${tmpPortretId}\" class=\"small addUser\">ユーザー追加</span></div></div>\n",
    postCreate: function(){
      var userparams = {
          widgetId:this.listWidgetId,
          selectId:this.selectId,
          inputId:this.inputId,
          buttonAddId:this.buttonAddId,
          buttonRemoveId:this.buttonRemoveId,
          memberFromId:this.memberFromId,
          memberToId:this.memberToId,
          memberFromUrl:this.memberFromUrl,
          memberFromOptionKey:this.memberFromOptionKey,
          memberFromOptionValue:this.memberFromOptionValue,
          groupSelectId:this.groupSelectId,
          groupSelectOptionKey:this.groupSelectOptionKey,
          groupSelectOptionValue:this.groupSelectOptionValue,
          memberGroupUrl:this.memberGroupUrl,
          changeGroupUrl:this.changeGroupUrl,
          tmpPortretId:this.tmpPortretId
      };

      this.listWidgetId = "memberfacilitylistwidget-" + this.tmpPortretId;
      var listWidget = dijit.byId(this.listWidgetId);
      if(listWidget) {
    	 dijit.registry.remove(this.listWidgetId);
      }
      this.dropDown = new aipo.widget.MemberFacilitySelectList(userparams, this.listWidgetId);
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
      //text = text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
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
        var len = t_o.length;
        if(len <= 0) return;
        for(var i = 0 ;i < len; i ++ ) {
            if (i != 0) {
                html += ' ';
            }
            var j = i % aipo.calendar.maximum_to;
            var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            html += "<span class=\"dispUser color" + j +"\">" + text + "</span>";
        }
        var pickedList=dojo.byId("picked_memberlist-"+this.tmpPortretId);
        if(pickedList){
        	this.dropDown.removeMember(pickedList);
			var p_mo = pickedList.options;
			for(var i = 0; i < p_mo.length; i++)(function(opt, index){
			  opt.selected = true;
			})(p_mo[i], i);
			this.dropDown.addMember(dojo.byId("member_to-"+this.tmpPortretId), dojo.byId("picked_memberlist-"+this.tmpPortretId));
        }
        input.innerHTML = html;
    },
    _openDropDown: function(){
        var dropDown = this.dropDown;
        var oldWidth=dropDown.domNode.style.width;
        var self = this;

        dijit.popup.open({
            parent: this,
            popup: dropDown,
            around: this.domNode,
            orient: this.isLeftToRight() ? {'BL':'TL', 'BR':'TR', 'TL':'BL', 'TR':'BR'}
                : {'BR':'TR', 'BL':'TL', 'TR':'BR', 'TL':'BL'},
            onExecute: function(){
                self._closeDropDown(true);
            },
            onCancel: function(){
                self._closeDropDown(true);
            },
            onClose: function(){
                aipo.calendar.populateWeeklySchedule(self.tmpPortretId);
                dropDown.domNode.style.width = oldWidth;
                self.popupStateNode.removeAttribute("popupActive");
                this._opened = false;
            }
        });
        if(this.domNode.offsetWidth > dropDown.domNode.offsetWidth){
            var adjustNode = null;
            if(!this.isLeftToRight()){
                adjustNode = dropDown.domNode.parentNode;
                var oldRight = adjustNode.offsetLeft + adjustNode.offsetWidth;
            }
            // make menu at least as wide as the button
            dojo.marginBox(dropDown.domNode, {w: this.domNode.offsetWidth});
            if(adjustNode){
                adjustNode.style.left = oldRight - this.domNode.offsetWidth + "px";
            }
        }
        this.popupStateNode.setAttribute("popupActive", "true");
        this._opened=true;
        if(dropDown.focus){
            dropDown.focus();
        }
        dropDown.focus();
        //For google chrome and Firefox 3.6 or higher
        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") > -1 || (dojo.isFF && (dojo.isFF >= 3.6))) {
            var pNode = this.dropDown.domNode.parentNode;
            var top = pNode.style.top.replace("px","");
            top_new = parseInt(top) + window.scrollY;
            pNode.style.top = top_new + "px";
            var focusNode =  document.activeElement;
            document.activeElement.blur();
             focusNode.focus();
        }
        // TODO: set this.checked and call setStateClass(), to affect button look while drop down is shown
    },
    _onDropDownClick:function(e){
    	var groupSelect=dojo.byId("groupselect-"+this.tmpPortretId);

        if(groupSelect && groupSelect.value!="pickup"){
        		return false;
        }

    	//ユーザー選択になっていれば、処理を続行。
    	aimluck.widget.Dropdown.prototype._onDropDownClick.call(this,e);
    }
});

}

}});