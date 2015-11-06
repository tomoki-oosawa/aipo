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
dojo.provide("aipo.facility");

aipo.facility.onLoadFacilityDialog = function(portlet_id){

  var obj = dojo.byId("facility_name");
  if(obj){
     obj.focus();
  }

	var fpicker = dijit.byId("facilitygroupselect");
	if(fpicker){
	    var select = dojo.byId('init_grouplist');
	    var i;
	    var s_o = select.options;
	    if (s_o.length == 1 && s_o[0].value == "") return;
	    for(i = 0 ; i < s_o.length; i ++ ) {
	        fpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
	    }
  }
}
aipo.facility.onLoadFacilityGroupDialog = function(portlet_id){
	  var obj = dojo.byId("facility_group_name");
	  if(obj){
	     obj.focus();
	  }
		var fpicker = dijit.byId("facilityselect");
		if(fpicker){
		    var select = dojo.byId('init_facilitylist');
		    var i;
		    var s_o = select.options;
		    if (s_o.length == 1 && s_o[0].value == "") return;
		    for(i = 0 ; i < s_o.length; i ++ ) {
		        fpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
		    }
	  }

	}

aipo.facility.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('facility');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.facility.sortsubmit=function(form){
  var s_o = form.member_so.options;
  var tmp = "";
  for(i = 0 ; i < s_o.length; i++ ) {
    s_o[i].selected = false;
  }
  if(s_o.length > 0) {
	  tmp=s_o[0].value;
	    for(i = 1 ; i < s_o.length; i++ ) {
	      tmp =tmp+','+ s_o[i].value ;
	    }
  }
  form.positions.value =tmp;
}
