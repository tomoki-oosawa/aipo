/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
dojo.provide("aipo.workflow_route");

dojo.require("aipo.workflow.MemberNormalSelectList");
dojo.require("dijit.form.ComboBox");

aipo.workflow_route.onLoadWorkflowRouteDialog = function(portlet_id){

    var mpicker = dijit.byId("membernormalselect");
    if(mpicker){
       var select = dojo.byId('init_memberlist');
       var i;
       var s_o = select.options;
       if (s_o.length == 1 && s_o[0].value == "") return;
       for(i = 0 ; i < s_o.length; i ++ ) {
          mpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
       }
    }
    var obj = dojo.byId("route_name");
    if(obj){
       obj.focus();
    }

}

aipo.workflow_route.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('workflow_route');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.workflow_route.submit_list = function(form) {
  var s_o = form.member_to.options;
  var tmp = '';

  for(i = 0 ; i < s_o.length; i++ ) {
    s_o[i].selected = false;
  }

  if(s_o.length > 0) {
    for(i = 0 ; i < s_o.length-1; i++ ) {
      tmp = tmp + s_o[i].value + ',';
    }
    tmp = tmp + s_o[s_o.length-1].value;
  }
  form.positions.value = tmp;
}