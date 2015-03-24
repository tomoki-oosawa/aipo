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
depends: [["provide", "aipo.workflow.MemberNormalSelectList"],
["require", "aipo.widget.MemberNormalSelectList"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.workflow.MemberNormalSelectList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.workflow.MemberNormalSelectList"] = true;

dojo.provide("aipo.workflow.MemberNormalSelectList");

dojo.require("aipo.widget.MemberNormalSelectList");

dojo.declare("aipo.workflow.MemberNormalSelectList", [aipo.widget.MemberNormalSelectList], {

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

               var optionflow = document.createElement("OPTION");
               optionflow.value = f_o[i].value;
               optionflow.text = (j+1) + ". " + f_o[i].text;
               optionflow.selected = true;

               t_o.add(optionflow, t_o.length);
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

                var optionflow = document.createElement("OPTION");
            	optionflow.value = f_o[i].value;
                optionflow.text = (j+1) + ". " + f_o[i].text;
                optionflow.selected = true;

                select_member_to.insertBefore(optionflow, t_o[t_o.length]);
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
                  if(i+1<t_o.length){
              		for(j = i+1 ; j < t_o.length; j ++){
              		if(j<9){
              			t_o[j].text = t_o[j].text.slice(3);
              		}else{
              			t_o[j].text = t_o[j].text.slice(4);
              		}
              		t_o[j].text = (j+1) + ". " + t_o[j].text;
              		}
              	}
              }
          }
        } else {
        	var t_o = select.options;
        	for(i = 0 ;i < t_o.length; i ++ ) {
                if( t_o[i].selected ) {
                	select.removeChild(t_o[i]);
                	i -= 1;
                	if(i+1<t_o.length){
                		for(j = i+1 ; j < t_o.length; j ++){
                		if(j<9){
                			t_o[j].text = t_o[j].text.slice(3);
                		}else{
                			t_o[j].text = t_o[j].text.slice(4);
                		}
                		t_o[j].text = (j+1) + ". " + t_o[j].text;
                		}
                	}
                }
        	}
        }
      }

});

}

}});