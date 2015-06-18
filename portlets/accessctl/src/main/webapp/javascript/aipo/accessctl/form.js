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
dojo.provide("aipo.accessctl");

dojo.require("aipo.widget.MemberNormalSelectList");

aipo.accessctl.onLoadAccessctlDialog = function(portlet_id){
    var obj = dojo.byId("acl_role_name");
    if(obj){
        obj.focus();
    }

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

	var url_acls = dojo.byId('urlacls'+portlet_id).value;
    var featureid = dojo.byId('initfeature'+portlet_id).value;
    aipo.accessctl.changeAcls(portlet_id, url_acls, featureid);
}

aipo.accessctl.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('accessctl');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.accessctl.changeAcls = function(portlet_id, link, featureid) {
    aipo.accessctl.createCheckbox(portlet_id, "acls", "aclsDiv", link + "?featureid=" + featureid, "aclId", "aclName", "checked");
}

aipo.accessctl.createCheckbox = function(portlet_id, selectid, divid, url, key, value, checked) {

  dojo.xhrGet({
    url: url,
    timeout: 5000,
    encoding: "utf-8",
    handleAs: "json-comment-filtered",
    headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
    load: function (respodatanse, ioArgs){
      var html = "";
      dojo.forEach(respodatanse, function(p) {
        if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
        } else {
          if (p[checked] == "true") {
            html += "<input name='"+p[key]+"' id='"+p[key]+"' type='checkbox' value='1' checked='checked'/><label for='"+p[key]+"'>&nbsp;"+p[value]+"</label>";
          } else {
            html += "<input name='"+p[key]+"' id='"+p[key]+"' type='checkbox' value='1'/><label for='"+p[key]+"'>&nbsp;"+p[value]+"</label>";
          }
          html += "&nbsp;";
        }
      });
      dojo.byId(divid).innerHTML = html;

      // initialize acl check
      aipo.accessctl.setupAcl(portlet_id, 'acllist');
      aipo.accessctl.setupAcl(portlet_id, 'acldetail');
      aipo.accessctl.setupAcl(portlet_id, 'aclinsert');
      aipo.accessctl.setupAcl(portlet_id, 'aclupdate');
      aipo.accessctl.setupAcl(portlet_id, 'acldelete');
      aipo.accessctl.setupAcl(portlet_id, 'aclexport');
    }
  });
};


aipo.accessctl.setupAcl = function(portlet_id, aclname) {
  var acl = dojo.byId('init'+aclname+portlet_id);
  if(acl && acl.value == "checked"){
    dojo.byId(aclname).checked = true;
  }
}


aipo.accessctl.submitList = function(form) {
  submit_member(form.ac_users);
  submit_member(form.ac_admins);
  submit_member(form.ac_guests);
}

aipo.accessctl.changeACL = function(form_name, source_list, dest_list) {
  var s = form_name.elements[source_list].options;
  var d = form_name.elements[dest_list].options;
  var i = 0;
  for(i=0; i<s.length-1; i++) {
    var ss = s[i];
    if(!ss.selected || !ss.text) continue;

    var f  = false;
    var li = d.length - 1;
    for (j=0; j<li; j++) {
      if(d[j].text == ss.text) {
        f = true;
        break;
      }
    }
    if (f) continue;

    aipo.accessctl.addOption(d, ss.text, ss.value, li);
  }
}

aipo.accessctl.removeList = function(form_name, target_list) {
  var t = form_name.elements[target_list].options;
  for(i=0; i<t.length-1; i++) {
    if(t[i].selected) {
      t[i] = null;
      i -= 1;
    }
  }
}

aipo.accessctl.addOption = function(list_options, text, value, pos) {
  var len = list_options.length;
  if (pos < 0) {
    pos = len;
  }
  var o = new Option();
  list_options[len] = o;
  for (i=len; i>pos; i--) {
    list_options[i].text = list_options[i-1].text;
  }
  list_options[pos].text = text;
  list_options[pos].value = value;
  list_options[pos].selected = true;
}
