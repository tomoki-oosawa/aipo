dojo._xdResourceLoaded({
depends: [["provide", "aimluck.io"]],
defineResource: function(dojo){if(!dojo._hasResource["aimluck.io"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aimluck.io"] = true;
/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

aimluck.namespace("aimluck.io");
dojo.provide("aimluck.io");

dojo.require("dojo.string");
dojo.requireLocalization("aipo", "locale");

aimluck.io.submit = function(form, indicator_id, portlet_id, callback) {
    aimluck.io.disableForm(form, true);

    var obj_indicator = dojo.byId(indicator_id + portlet_id);
    if(obj_indicator){
       dojo.style(obj_indicator, "display" , "");
    }

    try{
        dojo.xhrPost({
            url: form.action,
            timeout: 30000,
            form: form,
            encoding: "utf-8",
            handleAs: "json-comment-filtered",
            headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
            load: function (response, ioArgs){
                var html = "";
                if(dojo.isArray(response) && response.length > 0) {
                    if(response[0] == "PermissionError"){
                        html += "<ul>";
                        html += "<li><span class='caution'>" + response[1] + "</span></li>";
                        html += "</ul>";
                    }else{
                        html += "<ul>";
                        dojo.forEach(response, function(msg) {
                            html += "<li><span class='caution'>" + msg + "</span></li>";
                        });
                        html += "</ul>";
                    }
                }
                callback.call(callback, html);

                obj_indicator = dojo.byId(indicator_id + portlet_id);
                if(obj_indicator){
                   dojo.style(obj_indicator, "display" , "none");
                }

                if (html != "") {
                    aimluck.io.disableForm(form, false);
                }
            },
            error: function (error) {
            }
        });
    } catch(E) {
    };

    return false;
}



aimluck.io.sendData = function(url, params, callback) {
   var callbackArgs = new Array();
   callbackArgs["callback"] = callback;
   aimluck.io.sendRawData(url,params,sendErrorData,callbackArgs)


    return false;
}
aimluck.io.sendErrorData = function(callbackArgs,rtnData){
    var html = "";
    if(dojo.isArray(rtnData["data"]) && rtnData["data"].length > 0) {
      html += "<ul>";
      dojo.forEach(rtnData["data"], function(msg) {
        html += "<li>" + msg + "</li>";
      });
      html += "</ul>";
    }

    callbackArgs["callback"].call(callbackArgs["callback"], html);

    return false;
}
aimluck.io.sendRawData = function(url, params,callback, callbackArgs) {
   var rtnData = new Array;
   try{
        dojo.xhrGet({
            url: url,
            method: "POST",
            encoding: "utf-8",
            content: params,
            mimetype: "text/json",
            sync:true,
            load: function(type, data, event, args) {
               rtnData["type"]=type;
               rtnData["data"]=data;
               rtnData["event"]=event;
               rtnData["args"]=args;
               rtnData["bool"]=true;
               callback.call(callback,callbackArgs,rtnData);
               return rtnData;
            }
        });
    } catch(E) {
        alert("error");
    };

}


aimluck.io.escapeText = function(Text){
	var val;
	if (typeof(dojo.byId(Text).innerText) != 'undefined'){
		val = dojo.byId(Text).innerText;
	}
	else if (typeof(dojo.byId(Text).value) != 'undefined'){
		val = dojo.byId(Text).value;
	}
	else if (typeof(dojo.byId(Text).textContent) != 'undefined'){
		val = dojo.byId(Text).textContent;
	}
	return val;
}

aimluck.io.disableForm = function(form, bool) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'submit' || elements[i].type == 'button') {
      elements[i].disabled = bool;
    }
  }
}

aimluck.io.actionSubmit = function(button) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit();
}

aimluck.io.ajaxActionSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = url;
  aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
}


aimluck.io.actionSubmitReturn = function(button, rtn) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = button.form.action + '?' + button.name + '=1&action=' + rtn;
  button.form.submit();
}

//webmail
aimluck.io.deleteSubmit = function(button) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.DW_STR, {
		dw_del : nlsStrings.DW_DEL,
		dw_this : nlsStrings.DW_THIS,
		dw_name : button.form._name.value
	  });
	// この'+button.form._name.value+'を削除してよろしいですか?
	if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

//timeline
aimluck.io.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
  var confirmString = dojo.string.substitute(nlsStrings.DW_STR, {
	dw_del : nlsStrings.DW_DEL,
	dw_this : nlsStrings.DW_THIS,
	dw_name : button.form._name.value
  });
  // 'この'+button.form._name.value+'を削除してよろしいですか？'
  if(confirm(confirmString)) {
	aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

//account
aimluck.io.ajaxEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
  var confirmString = dojo.string.substitute(nlsStrings.ENABLESUBMIT_STR, {
		enableSubmit_this : nlsStrings.ENABLESUBMIT_THIS,
		enableSubmit_enable : nlsStrings.ENABLESUBMIT_ENABLE,
		enablesubmit_name:button.form._name.value
  });
  // この'+button.form._name.value+'を有効化してよろしいですか？
  if(confirm(confilmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

//account
aimluck.io.ajaxDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
  var confirmString = dojo.string.substitute(nlsStrings.DISABLESUBMIT_STR, {
    disableSubmit_this : nlsStrings.DISABLESUBMIT_THIS,
    disableSubmit_disable : nlsStrings.DISABLESUBMIT_DISABLE,
    disableSubmit_name:button.form._name.value
  });
  // この'+button.form._name.value+'を無効化してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.deleteSubmitReturn = function(button, rtn) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.DW_STR, {
		dw_del : nlsStrings.DW_DEL,
		dw_this : nlsStrings.DW_THIS,
		dw_name : button.form._name.value
	  });
	// この'+button.form._name.value+'を削除してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1&action=' + rtn;
    button.form.submit();
  }
}

aimluck.io.multiDeleteSubmit = function(button) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.DWS_STR, {
		dws_del : nlsStrings.DWS_DEL,
		dws_sel : nlsStrings.DWS_SEL,
		dws_name : button.form._name.value
	  });
	// 選択した'+button.form._name.value+'を削除してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

aimluck.io.ajaxMultiDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.DWS_STR, {
		dws_del : nlsStrings.DWS_DEL,
		dws_sel : nlsStrings.DWS_SEL,
		dws_name : button.form._name.value
	  });
	// 選択した'+button.form._name.value+'を削除してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.MULTIENABLESUBMIT_STR, {
			multiEnableSubmit_sel : nlsStrings.MULTIENABLESUBMIT_SEL,
			multiEnableSubmit_enable : nlsStrings.MULTIENABLESUBMIT_ENABLE,
			multiEnableSubmit_name:button.form._name.value
	  });
	// 選択した'+button.form._name.value+'を有効化してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
	  var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
	  var confirmString = dojo.string.substitute(nlsStrings.MULTIDISABLESUBMIT_STR, {
			multiDisableSubmit_sel : nlsStrings.MULTIDISABLESUBMIT_SEL,
			multiDisableSubmit_disable : nlsStrings.MULTIDISABLESUBMIT_DISABLE,
			multiDisableSubmit_name:button.form._name.value
	  });
	// 選択した'+button.form._name.value+'を無効化してよろしいですか？
  if(confirm(confirmString)) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.setHiddenValue = function(button) {
  if (button.name) {
    var q = document.createElement('input');
    q.type = 'hidden';
    q.name = button.name;
    q.value = button.value;
    button.form.appendChild(q);
  }
}

aimluck.io.openDialog = function(button, url, portlet_id, callback) {
  aimluck.io.disableForm(button.form, true);
  aipo.common.showDialog(url, portlet_id, callback);
}

aimluck.io.checkboxActionSubmit = function(button) {
  aimluck.io.verifyCheckbox( button.form, aimluck.io.actionSubmit, button );
}

aimluck.io.ajaxCheckboxActionSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxActionSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.checkboxDeleteSubmit = function(button) {
  aimluck.io.verifyCheckbox( button.form, aimluck.io.multiDeleteSubmit, button );
}

aimluck.io.ajaxCheckboxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiDeleteSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.ajaxCheckboxEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiEnableSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.ajaxCheckboxDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiDisableSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.verifyCheckbox = function(form, action, button) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
		var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
		var alertString = dojo.string.substitute(nlsStrings.VERIFYCB_STR, {
			verifycb_sel : nlsStrings.VERIFYCB_SEL,
			verifycb_gt_one : nlsStrings.VERIFYCB_GT_ONE,
			verifycb_cb : nlsStrings.VERIFYCB_CB,
		});
		// "チェックボックスを１つ以上選択してください。"
		alert(alertString);
    return false;
  }else{
    return action(button);
  }
}

//msgboard,gadgets
aimluck.io.ajaxVerifyCheckbox = function(form, action, button, url, indicator_id, portlet_id, receive ) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
		var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");
		var alertString = dojo.string.substitute(nlsStrings.VERIFYCB_STR, {
			verifycb_sel : nlsStrings.VERIFYCB_SEL,
			verifycb_gt_one : nlsStrings.VERIFYCB_GT_ONE,
			verifycb_cb : nlsStrings.VERIFYCB_CB,
		});
		// "チェックボックスを１つ以上選択してください。"
		alert(alertString);
    return false;
  }else{
    return action(button, url, indicator_id, portlet_id, receive );
  }
}

aimluck.io.createOptions = function(selectId, params) {
  var sel, pre, key, value, url, ind, callback, callbackTarget;
  if (params["url"]) {
      url = params["url"];
  }
  if (params["key"]) {
      key = params["key"];
  }
  if (params["value"]) {
      value = params["value"];
  }
  if (typeof params["selectedId"] == "undefined") {
  } else {
      sel = params["selectedId"];
  }
  if (typeof params["preOptions"] == "undefined") {
  } else {
      pre = params["preOptions"];
  }
  if (typeof params["indicator"] == "undefined") {
  } else {
      ind = params["indicator"];
      var indicator = dojo.byId(ind);
      if (indicator) {
        dojo.style(indicator, "display" , "none");
      }
  }
  if (typeof params["callback"] == "undefined") {
  } else {
	  callback = params["callback"];
	  if (typeof params["callbackTarget"] == "undefined") {
      } else {
          callbackTarget = params["callbackTarget"];
      }
  }

  dojo.xhrGet({
      url: url,
      timeout: 10000,
      encoding: "utf-8",
      handleAs: "json-comment-filtered",
      headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
      load: function(response, ioArgs) {
          var select = dojo.byId(selectId);
          select.options.length = 0;

          if(typeof pre == "undefined") {
          } else {
              aimluck.io.addOption(select, pre["key"], pre["value"], false);
          }
          dojo.forEach(response, function(p) {
              if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
              } else {
                  if (p[key] == sel) {
                      aimluck.io.addOption(select, p[key], p[value], true);
                  } else {
                      aimluck.io.addOption(select, p[key], p[value], false);
                  }
              }
          });
          if (indicator) {
            dojo.style(indicator, "display" , "none");
          }
          if (callback) {
        	  callback.call(callbackTarget ? callbackTarget : callback, response);
          }
      }
  });
}

aimluck.io.addFileToList =function(ul,fileid,filename){
	if(ul.parentNode.style.display == "none") {
		ul.parentNode.style.display = "";
	}
	if (document.all) {
	    var li = document.createElement("li");
	    li.setAttribute('data-fileid',fileid);
	    li.setAttribute('data-filename',filename);
	    li.innerHTML="<span>"+filename+"</span><span class=\"deletebutton\" onclick=\"aimluck.io.removeFileFromList(this.parentNode.parentNode,this.parentNode);\">削除</span>";

	   return ul.appendChild(li);
  } else {
	    var li = document.createElement("li");
	    li.setAttribute('data-fileid',fileid);
	    li.setAttribute('data-filename',filename);

	    li.innerHTML="<span>"+filename+"</span><span class=\"deletebutton\"  onclick=\"aimluck.io.removeFileFromList(this.parentNode.parentNode,this.parentNode);\">削除</span>";
	    return ul.appendChild(li);
  }
}

aimluck.io.replaceFileToList =function(ul,fileid,filename){
	if (document.all) {
	    var li = document.createElement("li");
	    li.setAttribute('data-fileid',fileid);
	    li.setAttribute('data-filename',filename);
	    li.innerHTML="<span>"+filename+"</span><span class=\"deletebutton\" onclick=\"aimluck.io.removeFileFromList(this.parentNode.parentNode,this.parentNode);\">削除</span>";
	    ul.innerHTML="";
	   return ul.appendChild(li);
  } else {
	    var li = document.createElement("li");
	    li.setAttribute('data-fileid',fileid);
	    li.setAttribute('data-filename',filename);

	    li.innerHTML="<span>"+filename+"</span><span class=\"deletebutton\"  onclick=\"aimluck.io.removeFileFromList(this.parentNode.parentNode,this.parentNode);\">削除</span>";
	    ul.innerHTML="";
	    return ul.appendChild(li);
  }
}

aimluck.io.removeFileFromList = function(ul,li){
	return ul.removeChild(li);
}

aimluck.io.createSelectFromFileList = function(form, pid){
    var ul = dojo.byId("attachments_" + pid);
    var select = document.createElement("select");
    select.style.display="none";
    select.id="attachments_select";
    select.multiple="multiple";
    select.name ="attachments";

    var lilist=ul.children;
    for(var i=0;i<lilist.length;i++){
    	var option =document.createElement("option");
    	option.value=lilist[i].getAttribute("data-fileid");
    	option.text=lilist[i].getAttribute("data-filename");
    	option.selected=true;
    	select.appendChild(option);
    }
    form.appendChild(select);
}

aimluck.io.addOption = function(select, value, text, is_selected) {
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.options.remove(0);
        // selectsub.options.remove(0);
      }
      select.add(option, select.options.length);
    // selectsub.add(option, select.options.length);
    // select.options[length].selected = is_selected;
  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.removeChild(select.options[0]);
        // selectsub.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);
    // selectsub.insertBefore(option, select.options[select.options.length]);
    // select.options[length].selected = is_selected;
  }
}

aimluck.io.removeOptions = function(select){
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

  if(t_o.length == 0){
        add_option(select, '', '\u3000', false)
  }
}

aimluck.io.removeAllOptions = function(select){
  if(select.options.length == 0) return;

  aimluck.io.selectAllOptions(select);

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

  if(t_o.length == 0){
        add_option(select, '', '\u3000', false);
  }
}

aimluck.io.selectAllOptions = function (select) {
    var t_o = select.options;
    if(t_o.length == 0) return;
    for(i = 0 ; i < t_o.length; i++ ) {
      t_o[i].selected = true;
    }
}

aimluck.io.switchCheckbox = function(checkbox) {
  var element;

  if (checkbox.checked) {
    for (i = 0; i < checkbox.form.elements.length; i++) {
      element = checkbox.form.elements[i];
      if(!element.disabled && element.type=="checkbox"){
        element.checked = true;
      }
    }
  }
  else {
    for (i = 0; i < checkbox.form.elements.length; i++) {
      element = checkbox.form.elements[i];
      if(!element.disabled && element.type=="checkbox"){
        element.checked = false;
      }
    }
  }
}


aimluck.io.postViewPage = function(form, portlet_id, indicator_id){
	aimluck.io.disableForm(form, true);

    var obj_indicator = dojo.byId(indicator_id + portlet_id);
    if(obj_indicator){
       dojo.style(obj_indicator, "display" , "");
    }

    dojo.xhrPost({
        url: form.action,
        timeout: 30000,
        form: form,
        encoding: "utf-8",
        handleAs: "text",
        headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
        load: function (response, ioArgs){
            var html = response;
            obj_indicator = dojo.byId(indicator_id + portlet_id);
            if(obj_indicator){
               dojo.style(obj_indicator, "display" , "none");
            }

            if (html != "") {
                aimluck.io.disableForm(form, false);
                var portlet = dijit.byId("portlet_" + portlet_id);
                if(! portlet){
                    portlet = new aimluck.widget.Contentpane({},'portlet_' + portlet_id);
                }

                if(portlet){
                	ptConfig[portlet_id].reloadUrl=ptConfig[portlet_id].initUrl;
                	portlet._isDownloaded = true;
                	portlet.setContent(html);
                }
            }
            // スマートフォン対応用
            if(aipo.onloadSmartPhone==null){
            	aipo.onloadSmartPhone();
            }
        },
        error: function (error) {
        }
    });
}


}

}});