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

aimluck.io.deleteSubmit = function(button) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

aimluck.io.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.ajaxEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.ajaxDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.deleteSubmitReturn = function(button, rtn) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1&action=' + rtn;
    button.form.submit();
  }
}

aimluck.io.multiDeleteSubmit = function(button) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

aimluck.io.ajaxMultiDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
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
    alert("\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
    return false;
  }else{
    return action(button);
  }
}

aimluck.io.ajaxVerifyCheckbox = function(form, action, button, url, indicator_id, portlet_id, receive ) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
    alert("\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
    return false;
  }else{
    return action(button, url, indicator_id, portlet_id, receive );
  }
}

aimluck.io.createOptions = function(selectId, params) {
  var sel, pre, key, value, url, ind;
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
      }
  });
}

aimluck.io.addOption = function(select, value, text, is_selected) {
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.options.remove(0);
        //selectsub.options.remove(0);
      }
      select.add(option, select.options.length);
    //selectsub.add(option, select.options.length);
    //select.options[length].selected = is_selected;
  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.removeChild(select.options[0]);
        //selectsub.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);
    //selectsub.insertBefore(option, select.options[select.options.length]);
    //select.options[length].selected = is_selected;
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
      if(! element.disabled){
        element.checked = true;
      }
    }
  }
  else {
    for (i = 0; i < checkbox.form.elements.length; i++) {
      element = checkbox.form.elements[i];
      if(! element.disabled){
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
                	ptConfig[portlet_id].reloadUrl= ptConfig[portlet_id].initUrl;
                	portlet._isDownloaded = true;
                	portlet.setContent(html);
                }
            }
        },
        error: function (error) {
        }
    });
}


}

}});