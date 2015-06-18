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
depends: [["provide", "aipo.widget.GroupNormalSelectList"],
["require", "dijit._Widget"],
["require", "dijit._Templated"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.widget.GroupNormalSelectList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.GroupNormalSelectList"] = true;

dojo.provide("aipo.widget.GroupNormalSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

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
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"groupPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:335px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">${memberToTitle}</div><div class=\"memberlistFromTop\">${memberFromTitle}</div></div><div class=\"clearfix mb5\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\""+nlsStrings.DELETEBTN_STR+"\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\""+nlsStrings.ADDBTN_STR+"\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",
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

}

}});