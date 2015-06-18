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
dojo.provide("aipo.webmail");
dojo.require("aipo.widget.DropdownDatepicker");

aipo.webmail.onLoadMailDialog = function(portlet_id){

	var obj = dojo.byId("to");
	if(obj){
	   obj.focus();
	}

}

aipo.webmail.onLoadMailAccountDialog = function(portlet_id){

  var obj = dojo.byId("account_name");
  if(obj){
     obj.focus();
  }

}

aipo.webmail.onLoadMailFolderDialog = function(portlet_id){

  var obj = dojo.byId("folder_name");
  if(obj){
     obj.focus();
  }
}

aipo.webmail.onLoadMailFilterDialog = function(portlet_id){

  var obj = dojo.byId("filter_name");
  if(obj){
     obj.focus();
  }
}

aipo.webmail.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);

    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('webmail');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }

    var modalDialog = document.getElementById('modalDialog');
    if(modalDialog && msg != '') {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

aipo.webmail.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }
    aipo.portletReload('webmail');
};

aipo.webmail.onLoadMailListDetail = function(){
  aipo.portletReload('webmail');
}

aipo.webmail.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
	if(confirm('この'+button.form._name.value+'を削除してよろしいですか？なお、フォルダに含まれるメールはすべて削除されます。\nまた、このフォルダを振り分け先として指定してあるフィルタは、振り分け先がデフォルト（フォルダリストの一番上のフォルダ）に変更されます。')) {
		aimluck.io.disableForm(button.form, true);
		aimluck.io.setHiddenValue(button);
		button.form.action = url;
		aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
	}
}

var mailReceviingTimerId;

aipo.webmail.onProcessingTimer = function(){
  if(mailReceviingTimerId){
    clearTimeout(mailReceviingTimerId);
  }
  mailReceviingTimerId = setTimeout("aipo.webmail.reloadMail()",10000);
}

aipo.webmail.reloadMailList = function(portletId){
  if( typeof ptConfig[portletId].reloadUrl != "undefined") {
    ptConfig[portletId].reloadUrl += '&updateunread=1';
  }
  aipo.reloadPage(portletId);
}

aipo.webmail.reloadMail = function(){
  var screenUrl = dojo.byId("receiving");
  if(screenUrl){
    var tmp_reload_url = screenUrl.value;

    var page_start = dojo.byId("page_start");

    if(page_start){
      tmp_reload_url += '&start=' + page_start.value;
    }
    tmp_reload_url += '&updateunread=1';

    var receivingPid = dojo.byId("receivingPid");
    aipo.viewPage(tmp_reload_url, receivingPid.value);
    aipo.webmail.onProcessingTimer();
  }
}

aipo.webmail.open_help = function(url){
    wx = 400;
    wy = 250;
    x = (screen.width  - wx) / 2;
    y = (screen.height - wy) / 2;

    help_subwin = window.open(url, "help_window","left="+x+",top="+y+",width="+wx+",height="+wy+",resizable=no");
    help_subwin.opener = self;
    help_subwin.focus();
}

aipo.webmail.switchHeader = function(button,portlet_id) {
	var is_header_tiny=dojo.byId("is_header_tiny");
    if(is_header_tiny.value == 'TRUE' || is_header_tiny.value == 'true') {
        //簡易表示の際に「詳細表示」ボタンを押した→詳細部分をonにして簡易部分をoffにする 。詳細表示になったのでis_header_tinyがfalse
        button.innerHTML = '簡易表示';
        aipo.webmail.switchHeaderDetail();
    } else {
        //詳細表示の際に「簡易表示」ボタンを押した→簡易部分をonにして詳細部分をoffにする
        button.innerHTML = '詳細表示';
        aipo.webmail.switchHeaderTiny();
    }
}

aipo.webmail.switchHeaderTiny = function() {
	var is_header_tiny=dojo.byId("is_header_tiny");
    dojo.byId('WebMailHeaderFieldTiny').style.display = "";
    dojo.byId('WebMailHeaderFieldDetail').style.display = "none";
    is_header_tiny.value = 'TRUE';
}

aipo.webmail.switchHeaderDetail = function() {
	var is_header_tiny=dojo.byId("is_header_tiny");
    dojo.byId('WebMailHeaderFieldTiny').style.display = "none";
    dojo.byId('WebMailHeaderFieldDetail').style.display = "";
    is_header_tiny.value = 'FALSE';
}

aipo.webmail.doDeleteAccount = function(url,p_id) {
    if(confirm("このメールアカウントを削除してもよろしいですか？\n保存されているメールはすべて削除されます。")) {
        aipo.viewPage(url,p_id);
    }
}

aipo.webmail.doDeleteFilter = function(url,p_id) {
    if(confirm("このフィルタを削除してもよろしいですか？")) {
        aipo.viewPage(url,p_id);
    }
}

aipo.webmail.AccountChange = function(form,url,p_id){
    var exec_url = url;
    var r = form.account_type;
    for (i=0;i < r.length;i++){
        if(r[i].checked){
            var check_id = r[i].value;
        }
    }
    exec_url += '&account_type=' + check_id;
    aipo.viewPage(exec_url, p_id);
}

aipo.webmail.onReceiveMessageAdmin = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('webmailadmin');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.webmail.onDeleteAdminAccount = function(msg){
    if(!msg) {
        aipo.portletReload('webmailadmin');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.webmail.hideDialogAdmin = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }
    aipo.portletReload('webmailadmin');
};

aipo.webmail.switchDelAtPop3 = function(check) {
  if(check.value == 0) {
    dojo.byId('del_at_pop3_flg_on_field').style.display = "";
  } else {
    dojo.byId('del_at_pop3_flg_on_field').style.display = "none";
  }
}

aipo.webmail.switchAuthSendAdmin = function(check) {
  if(check.value == 2) {
    dojo.byId('smtp_auth_field').style.display = "";
    dojo.byId('pop_auth_field').style.display  = "none";
  } else if(check.value == 1) {
    dojo.byId('smtp_auth_field').style.display = "none";
    dojo.byId('pop_auth_field').style.display  = "";
  } else {
    dojo.byId('smtp_auth_field').style.display = "none";
    dojo.byId('pop_auth_field').style.display  = "none";
  }
}

aipo.webmail.switchAuthSend = function(check) {
  if(check.value == 2) {
    dojo.byId('smtp_auth_field').style.display = "";
  } else {
    dojo.byId('smtp_auth_field').style.display = "none";
  }
}

aipo.webmail.showAddressbookDialog = function(url, portlet_id, ind, callback) {
    var arrDialog = dijit.byId("addressbookDialog");

    if(! arrDialog){
       arrDialog = new aipo.webmail.widget.AddressbookDialog({widgetId:'addressbookDialog', _portlet_id: portlet_id, _callback:callback}, "addressbookDialog");
    }else{
       arrDialog.setCallback(portlet_id, callback);
    }

    if(arrDialog){
      arrDialog.setHref(url);
      arrDialog.show();
      var popup = dojo.byId(ind + portlet_id);
      popup.parentNode.parentNode.style.display="none";
      popup.parentNode.parentNode.id="tmp_pop_id";
    }
}

aipo.webmail.onLoadAddressbookDialog = function(portlet_id){
    var url_userlist = dojo.byId('urlUserlist'+portlet_id).value;
    aipo.webmail.changeInternalGroup(url_userlist, 'LoginUser');


    var url_addrlist = dojo.byId('urlAddrlist'+portlet_id).value;
    aipo.webmail.changeExternalGroup(url_addrlist, '');

    /*
    aimluck.io.removeAllOptions(dojo.byId('detail_to_recipients'));
    aimluck.io.removeAllOptions(dojo.byId('detail_cc_recipients'));
    aimluck.io.removeAllOptions(dojo.byId('detail_bcc_recipients'));
    */

    aipo.webmail.getDataSub(dojo.byId('detail_to_recipients'), dojo.byId('to').value);
    aipo.webmail.getDataSub(dojo.byId('detail_cc_recipients'), dojo.byId('cc').value);
    aipo.webmail.getDataSub(dojo.byId('detail_bcc_recipients'), dojo.byId('bcc').value);

}

aipo.webmail.getDataSub = function(select, recipients){
    if(recipients == null || recipients.length == 0) return;

    var recipient_list = recipients.split(',');
    for(i = 0 ; i < recipient_list.length; i++ ) {
        add_option(select, aipo.webmail.trim(recipient_list[i]), aipo.webmail.trim(recipient_list[i]), false);
    }
}

aipo.webmail.insertData = function(){
    dojo.byId('to').value = aipo.webmail.getStringLine(dojo.byId('detail_to_recipients').options);
    dojo.byId('cc').value = aipo.webmail.getStringLine(dojo.byId('detail_cc_recipients').options);
    dojo.byId('bcc').value = aipo.webmail.getStringLine(dojo.byId('detail_bcc_recipients').options);

    dijit.byId('addressbookDialog').hide();
    var popup = dojo.byId('tmp_pop_id');
    popup.style.display="";
    popup.id="";
}

aipo.webmail.close = function(){
	dijit.byId('addressbookDialog').hide();
    var popup = dojo.byId('tmp_pop_id');
    popup.style.display="";
    popup.id="";
}

aipo.webmail.switchTypeCompany = function(check){
  if(check.value == '1') {
    dojo.byId('Block_Internal_Group').style.display = "block";
    dojo.byId('Block_External_Group').style.display="none";
    dojo.byId('userDiv').style.display="block";
    dojo.byId('addrDiv').style.display="none";
  } else {
    dojo.byId('Block_Internal_Group').style.display="none";
    dojo.byId('Block_External_Group').style.display="block";
    dojo.byId('userDiv').style.display="none";
    dojo.byId('addrDiv').style.display="block";
  }
}

aipo.webmail.changeInternalGroup = function(link, group) {
    aipo.webmail.createSelect("internal_member_from", "userDiv", link + "?mode=group&groupname=" + group + "&inc_luser=true", "aliasName", "email", "", '', 'size="12" multiple="multiple" style="width: 99%"', 'addresslist-indicator');
}

aipo.webmail.changeExternalGroup = function(link, group) {
    aipo.webmail.createSelect("external_member_from", "addrDiv", link + "?mode=group&groupname=" + group + "&inc_luser=true", "fullName", "email", "", '', 'size="12" multiple="multiple" style="width: 99%"', 'addresslist-indicator');
}

aipo.webmail.createSelect = function(selectid, divid, url, keyname, keymail, sel, pre, att, ind) {
    var indicator = dojo.byId(ind);
    if (indicator) {
        dojo.style(indicator, "display" , "");
    }
    dojo.xhrGet({
        url: url,
        timeout: 10000,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
        load: function (respodatanse, ioArgs){
            var html = "";
            if (typeof att == "undefined") {
                html += '<select name="'+ selectid + '">';
            } else {
                html += '<select name="'+ selectid + '" ' + att + '/>';
            }
            if (typeof pre == "undefined") {
                html += '';
            } else {
                html += pre;
            }
            dojo.forEach(respodatanse, function(p) {
                if(typeof p[keyname] == "undefined" || typeof p[keymail] == "undefined") {
                } else {
                    if (p[keyname] == sel) {
                        html += "<option value='"+p[keyname]+"' selected='selected'>"+ p[keymail]+"</option>";
                    } else {
                        html += "<option value='"+p[keyname]+"&lt;"+ p[keymail]+"&gt;'>"+ p[keyname] +"&lt;"+ p[keymail] + "&gt;</option>";
                    }
                }
            });
            html += '</select>';
            dojo.byId(divid).innerHTML = html;
            if (indicator) {
                dojo.style(indicator, "display" , "none");
            }
        }
    });
};

aipo.webmail.sendForm = function(){
    aipo.webmail.selectAll(document.WebMailAddressbook.detail_to_recipients);
    aipo.webmail.selectAll(document.WebMailAddressbook.detail_cc_recipients);
    aipo.webmail.selectAll(document.WebMailAddressbook.detail_bcc_recipients);
    document.WebMailAddressbook.submit();
}

aipo.webmail.getStringLine = function(str_list){
    var strs = '';
    if(str_list.length > 0){
        var length = str_list.length-1;
    for(i = 0 ; i < length; i ++ ) {
        strs = strs + str_list[i].value+',';
    }
    strs = strs + str_list[length].value;
  }
  return strs;
}

aipo.webmail.selectAll = function(select){
    var t_o = select.options;
    for(i = 0 ; i < t_o.length; i++ ) {
      t_o[i].selected = true;
    }
}

aipo.webmail.exAddMember = function(form, select, portletId){

  if(dojo.byId('corpId'+portletId).checked == true){
    add_member(form.internal_member_from, select);
  }else{
    add_member(form.external_member_from, select);
  }

}

aipo.webmail.removeAll = function(select){
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
}


aipo.webmail.ltrim = function(str){
  while(str.charAt(0)==" " || str.charAt(0)=="　"){
    str = str.substring(1,str.length)
  }
  return(str);
}

aipo.webmail.rtrim = function(str){
  while(str.charAt(str.length-1)==" " || str.charAt(str.length-1)=="　"){
    str = str.substring(0,str.length-1)
  }
  return(str);
}

aipo.webmail.trim = function(str){
  return aipo.webmail.ltrim(aipo.webmail.rtrim(str));
}

aipo.webmail.filter_order_submit = function(form) {
  var s_o = form.filter_so.options;
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

aipo.webmail.action=function(_this,portlet_id){
	var action=dojo.byId(portlet_id+"_action").value;
	aimluck.io.openDialog(_this,action,portlet_id, aipo.webmail.onLoadMailDialog);
}


aipo.webmail.toggleMenu = function (node,filters,event){
	var rect=filters.getBoundingClientRect();
	var html=document.documentElement.getBoundingClientRect();
	if (node.style.display == "none") {
        dojo.query("div.menubar").style("display", "none");

        var scroll={
        	left:document.documentElement.scrollLeft||document.body.scrollLeft,
        	top:document.documentElement.scrollTop||document.body.scrollTop
        };
        node.style.opacity="0";
        node.style.display="block";
        if(html.right-node.clientWidth>rect.left){
       		node.style.left=rect.left+scroll.left+"px";
        }else{
        	node.style.left=rect.right-node.clientWidth+scroll.left+"px";
        }
         if(html.bottom-node.clientHeight>rect.bottom||event){
       		node.style.top=rect.bottom+scroll.top+"px";
        }else{
        	node.style.top=rect.top-node.clientHeight+scroll.top+"px";
        }
        node.style.opacity="";
    } else {
        dojo.query("div.menubar").style("display", "none");
    }
}
