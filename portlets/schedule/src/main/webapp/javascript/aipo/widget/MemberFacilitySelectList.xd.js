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
if(!dojo._hasResource["aipo.widget.MemberFacilitySelectList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.MemberFacilitySelectList"] = true;

dojo.provide("aipo.widget.MemberFacilitySelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberFacilitySelectList", [dijit._Widget, dijit._Templated], {
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
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    tmpPortretId: "",
    templateString: "<div id=\"${widgetId}\" widgetId=\"${widgetId}\" style=\"display: none\"><div class=\"auiPopup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >閲覧ユーザー一覧</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix mb5\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"削除\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"＜ 追加\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div> </div></div>\n",
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
          preOptions: { key:'LoginUser', value:'（全体）' }
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
               if( aipo.calendar.maximum_to  > t_o.length){
                    t_o.add(option, t_o.length);
               }
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
                if( aipo.calendar.maximum_to  > t_o.length){
                    select_member_to.insertBefore(option, t_o[t_o.length]);
                }
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
                html += ' ';
            }
            var j = i %  aipo.calendar.maximum_to;
            var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            html += "<span class=\"dispUser color" + j +"\">" + text + "</span>";
        }
        var pickedList=dojo.byId("picked_memberlist-"+this.tmpPortretId);
        if(pickedList){
        	this.removeMember(pickedList);
			var p_mo = pickedList.options;
			for(var i = 0; i < p_mo.length; i++)(function(opt, index){
			  opt.selected = true;
			})(p_mo[i], i);
			this.addMember(dojo.byId("member_to-"+this.tmpPortretId), dojo.byId("picked_memberlist-"+this.tmpPortretId));
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
      var select = dojo.byId(this.memberToId);
        if(select.options.length == 0){
            if(aipo.schedule.login_aliasname != "undefined"){
                var alias = aipo.schedule.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                this.addOptionSync(aipo.schedule.login_id, alias, true);
            }
        }
    },
    onCloseClick: function(){
        dojo.style(this.domNode, "display", "none");
        aipo.calendar.populateWeeklySchedule(this.tmpPortretId);
    }
});

}
