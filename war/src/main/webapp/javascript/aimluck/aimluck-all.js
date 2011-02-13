/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck.js
 */
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

window.aimluck = window.aimluck || {};

aimluck.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = aimluck;


    for (var i=(levels[0] == "aimluck") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

djConfig = { isDebug: false };

function getObjectById(id) {
    if(document.getElementById) return document.getElementById(id) //e5,e6,n6,m1,o6
    else if(document.all)       return document.all(id)            //e4
    else if(document.layers)    return document.layers[id]         //n4
}

function ew(button) {
  disableButton(button.form);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit(); 
}

function dw(button) {
  if(confirm('\u3053\u306e'+button.form.name+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    disableButton(button.form);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  } 
}

function ews(button) {
  disableButton(button.form);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit();
}

function dws(button) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form.name+'\u3092\u3059\u3079\u3066\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    disableButton(button.form);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  } 
}

function setHiddenValue(button) {
  if (button.name) {
    var q = document.createElement('input');
    q.type = 'hidden';
    q.name = button.name;
    q.value = button.value;
    button.form.appendChild(q);
  }
}

function disableSubmit(form) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'submit') {
      elements[i].disabled = true;
    }
  }
}

function disableButton(form) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'button') {
      elements[i].disabled = true;
    }
  }
}

function check_new_mail(button, current_page) {
  button.form.action = button.form.action + '?confirmlasttime=true&start=' + current_page;
  button.form.submit(); 
}

function createAction(button) {
   button.form.action = button.form.action + '?' + button.name + '=1';
}

function verifyCheckBox(form, action, button) {
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

function submit_member(select) {
	var t_o = select.options;
	for(i = 0 ; i < t_o.length; i++ ) {
	  t_o[i].selected = true;
	}
}

function add_option(select, value, text, is_selected) {
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
			select.options.remove(0);
	  }
	  select.add(option, select.options.length);
    //select.options[length].selected = is_selected;
  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
    	select.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);
    //select.options[length].selected = is_selected;
  }
}

function add_member(select_member_from, select_member_to) {
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
			select_member_to.insertBefore(option, t_o[t_o.length]);
		}
  }
}

function remove_member(select) {
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

function doUpOptions10(select) {
  var s_o = select.options;
  for(i = 0; i < s_o.length; i++ ) {
  	if(!s_o[i].selected) continue;
    if(i == 0) continue;
	if(s_o[i-1].selected) continue;
	up_option(select, i, 10);
  }
}

function doUpOptions(select) {
  var s_o = select.options;
  for(i = 0; i < s_o.length; i++ ) {
  	if(!s_o[i].selected) continue;
    if(i == 0) continue;
    if(s_o[i-1].selected) continue;
    up_option(select, i, 1);
  }
}

function doDownOptions10(select) {
  var s_o = select.options;
  for(i = s_o.length-1; i >= 0; i-- ) {
  	if(!s_o[i].selected) continue;
    if(i == s_o.length-1) continue;
    if(s_o[i+1].selected) continue;
    down_option(select, i, 10);
  }
}

function doDownOptions(select) {
  var s_o = select.options;
  for(i = s_o.length-1; i >= 0; i-- ) {
  	if(!s_o[i].selected) continue;
    if(i == s_o.length-1) continue;
    if(s_o[i+1].selected) continue;
    down_option(select, i, 1);
  }
}

function up_option(select, index, rate) {  
  var s_o = select.options; 
  var delta = 0;
  if(index - rate >= 0){
    delta = index - rate;
  }else{
  	for(i = 0; i < s_o.length; i++ ) {
	    if(! s_o[i].selected){
	  	  delta = i;
	  	  break;
	    }
    }
  }

  change_turn_option(select, index, delta);
}

function down_option(select, index, rate) {
  var s_o = select.options; 
  var delta = 0;
  if(s_o.length - 1 - index - rate >= 0){
  	delta = index + rate;
  }else{
  	for(i = s_o.length-1; i >= 0; i-- ) {
	    if(! s_o[i].selected){
	  	  delta = i;
	  	  break;
	    } 
    }
  }
  
  change_turn_option(select, index, delta);
}


function change_turn_option(select, index, delta) {
  var s_o = select.options;
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = s_o[index].value;
    option.text = s_o[index].text;
    option.selected = true;
    select.remove(index);
    s_o.add(option, delta);
    s_o[delta].selected = true;
  } else {
    var option = document.createElement("OPTION");
    option.value = s_o[index].value;
    option.text = s_o[index].text;
    option.selected = true;
    select.removeChild(s_o[index]);
    select.insertBefore(option, s_o[delta]);
    s_o[delta].selected = true;
  }
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/dnd/Draggable.js
 */
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

dojo.provide("aimluck.dnd.DragMoveObject");
dojo.provide("aimluck.dnd.Draggable");

dojo.require("dojo.dnd.Moveable");
dojo.require("dojo.parser");
dojo.require("dojo.dnd.Source");

// aimluck.dnd.Draggable

dojo.declare("aimluck.dnd.DragMoveObject", [dojo.dnd.Mover] , {
    _pageY: 0,
    _pageX: 0,
    portletId: null,
    leftTop: null,
    onFirstMove:function (e) {
	    dojo.dnd.Mover.prototype.onFirstMove.apply(this, arguments);
    },
    onMouseUp:function (e) {
        dojo.dnd.Mover.prototype.onMouseUp.apply(this, arguments);
    },
    onMouseDown: function(e){
        var m = this.marginBox;
//        this._origOpacity_  = dojo.style(this.node, "opacity");
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
        dojo.dnd.Mover.prototype.onMouseDown.apply(this, arguments);
    },
    onMouseMove: function(e){
        this._pageX = e.pageX;
        this._pageY = e.pageY;  
        dojo.dnd.autoScroll(e);
        var m = this.marginBox;
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
    //  dojo.dnd.Mover.prototype.onMouseMove.apply(this, arguments);
    } 
});

dojo.declare("aimluck.dnd.Draggable", dojo.dnd.Moveable , {
    DragMoveObject: aimluck.dnd.DragMoveObject,
    portletId: null,
    constructor: function(node, params){
        this.portletId = params.pid;
    },
    onMouseDown: function(e){
        // summary: event processor for onmousedown, creates a Mover for the node
        // e: Event: mouse event
                
        if(this.skip && dojo.dnd.isFormElement(e)){ return; }
        if(this.delay){
            this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"));
            this.events.push(dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
        }else{
            dragObj = new this.DragMoveObject(this.node, e, this);
            dragObj.dragSource=this;
            dragObj.portletId = this.portletId;
        }
        
        dragObj._pageX = e.pageX;
        dragObj._pageY = e.pageY;
        
        this._lastX = e.pageX;
        this._lastY = e.pageY;
        
        dojo.stopEvent(e);
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/dnd/Draggable.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/io/form.js
 */
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


/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/io/form.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/form.js
 */
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

aimluck.namespace("utils.form");

aimluck.utils.form.createSelect = function(selectid, divid, url, key, value, sel, pre, att) {
    dojo.xhrGet({
        url: url,
        timeout: 5000,
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
                if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
                } else {
                    if (p[key] == sel) {
                        html += "<option value='"+p[key]+"' selected='selected'>"+ p[value]+"</option>";
                    } else {
                        html += "<option value='"+p[key]+"'>"+ p[value]+"</option>";
                    }
                }
            });
            html += '</select>';
            dojo.byId(divid).innerHTML = html;
        }
    });
};

aimluck.utils.form.switchDisplay = function (viewId, hideId) {
    dojo.html.setDisplay(dojo.byId(hideId),"none");
    dojo.html.setDisplay(dojo.byId(viewId),"");
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/form.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/utils.js
 */
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

aimluck.namespace("utils");

aimluck.utils.createCSS = function(url) {
    if(document.createStyleSheet){ //IE
        document.createStyleSheet(url);
    } else { //other browser
        var head = document.getElementsByTagName("head")[0];
        var stylesheet = document.createElement("link");
        with(stylesheet){
            rel="stylesheet";
            type="text/css";
            href=url;
        }
        head.appendChild(stylesheet);
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/utils.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Contentpane.js
 */
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

dojo.provide("aimluck.widget.Contentpane");

dojo.require("dijit.layout.ContentPane");

dojo.declare(
	"aimluck.widget.Contentpane",
	[dijit.layout.ContentPane],
	{
        loadingMessage:"<div class='indicator'>\u8aad\u307f\u8fbc\u307f\u4e2d...</div>",
        errorMessage:"",
        extractContent: false,
        parseOnLoad: true,
        refreshOnShow: true,
        params: new Array(),
        reloadIds: new Array(),
		viewPage: function(href){
			this.href = href;
		    return this._prepareLoad(true);
		},
        setParam: function(key, value) {
            this.params[key] = value;
        },
        setReloadIds: function(values) {
            this.reloadIds = values;
        },
        clearParams: function() {
            this.params = new Array();
        },
        clearReloadIds: function() {
            this.reloadIds = new Array();
        },
		_downloadExternalContent: function(){
			this._onUnloadHandler();
	
			// display loading message
			// TODO: maybe we should just set a css class with a loading image as background?
			/*
			this._setContent(
				this.onDownloadStart.call(this)
			);
	        */
			var self = this;
			var getArgs = {
				preventCache: (this.preventCache || this.refreshOnShow),
				url: this.href,
				handleAs: "text",
                content: this.params,
			    headers: { X_REQUESTED_WITH: "XMLHttpRequest" }
			};
	
			if(dojo.isObject(this.ioArgs)){
				dojo.mixin(getArgs, this.ioArgs);
			}
	
			var hand = this._xhrDfd = (this.ioMethod || dojo.xhrPost)(getArgs);

			hand.addCallback(function(html){
                self.clearParams();
                self.clearReloadIds();
				try{
					self.onDownloadEnd.call(self);
					self._isDownloaded = true;
					self.setContent.call(self, html); // onload event is called from here
				}catch(err){
					self._onError.call(self, 'Content', err); // onContentError
				}
				delete self._xhrDfd;
				return html;
			});
	
			hand.addErrback(function(err){
				if(!hand.cancelled){
					// show error message in the pane
					self._onError.call(self, 'Download', err); // onDownloadError
				}
				delete self._xhrDfd;
				return err;
			});
		}
    }	
);

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Contentpane.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dialog.js
 */
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

dojo.provide("aimluck.widget.Dialog");
dojo.provide("aimluck.widget.DialogUnderlay");

dojo.require("dijit.Dialog");

dojo.declare(
    "aimluck.widget.DialogUnderlay",
    [dijit.DialogUnderlay],
    {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node'></div></div>"
    }

);

dojo.declare( "aimluck.widget.Timeout",  [dijit._Widget, dijit._Templated] , {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node' redirecturl=\"${redirectUrl}\"></div></div>",
       redirectUrl:"about:blank",
       postCreate: function(){
    window.location.href = this.redirectUrl;
      }
});

dojo.declare(
    "aimluck.widget.Dialog",
    [dijit.Dialog],
    {
        widgetId: null,
        loadingMessage:"<div class='indicatorDialog'><div class='indicator'>\u8aad\u307f\u8fbc\u307f\u4e2d...</div></div>",
        templateString:null,
        templateString:"<div id='modalDialog' class='modalDialog' dojoattachpoint='wrapper'><span dojoattachpoint='tabStartOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap'tabindex='0'></span><span dojoattachpoint='tabStart' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><div dojoattachpoint='containerNode' style='position: relative; z-index: 2;'></div><span dojoattachpoint='tabEnd' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><span dojoattachpoint='tabEndOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span></div>",//<div dojoAttachPoint=\"titleBar\" class=\"modalDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">&nbsp;</div>
        duration: 10,
        extractContent: false,
        parseOnLoad: true,
        refreshOnShow: true,
        isPositionLock: false,
        params: new Array(),
        reloadIds: new Array(),
        _portlet_id: null,
        _callback:null,
        _setup: function(){
            // summary:
            //      stuff we need to do before showing the Dialog for the first
            //      time (but we defer it until right beforehand, for
            //      performance reasons)

            this._modalconnects = [];
            
            if(this.titleBar){
                this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
                var _tmpnode = this.domNode;
                dojo.connect(this._moveable, "onMoving", function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
                        
                        var viewport = dijit.getViewport();
                        var w1 = parseInt(dojo.getComputedStyle(_tmpnode).width);
			            var w2 = parseInt(viewport.w);

	                    if(leftTop.l < 0){
	                       leftTop.l = 0; 
	                    }
	                    
	                    if(leftTop.l + w1 > w2){
	                       leftTop.l = w2 - w1;
	                    }
	                   
	                    if(leftTop.t < 0){
	                       leftTop.t = 0
	                    }
                });
            }

            this._underlay = new aimluck.widget.DialogUnderlay();

            var node = this.domNode;
            this._fadeIn = dojo.fx.combine(
                [dojo.fadeIn({
                    node: node,
                    duration: this.duration
                 }),
                 dojo.fadeIn({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onBegin: dojo.hitch(this._underlay, "show")
                 })
                ]
            );

            this._fadeOut = dojo.fx.combine(
                [dojo.fadeOut({
                    node: node,
                    dialog: this,
                    duration: this.duration,
                    onEnd: function(){
                        node.style.display="none";
                        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
                        if (document.all) { // for IE
                            this.dialog.fixTmpScroll();
                        }
                        //**//
                    }
                 }),
                 dojo.fadeOut({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onEnd: dojo.hitch(this._underlay, "hide")
                 })
                ]
            );
        },
        fixTmpScroll: function(){
            //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
            var _tmpNode = dojo.byId('weeklyScrollPane_'+this._portlet_id);
            if(_tmpNode){
                if (aipo.schedule.tmpScroll == "undefined") {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = ptConfig[this._portlet_id].contentScrollTop;
                } else {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = aipo.schedule.tmpScroll;
                }
            }
            //**//
        },
        onLoad: function(){
            // when href is specified we need to reposition
            // the dialog after the data is loaded
            this._position();
            dijit.Dialog.superclass.onLoad.call(this);
            this.isPositionLock = false;
            
            var focusNode = dojo.byId( this.widgetId );
            if ( focusNode ) {
                focusNode.focus();
                
                if (this._callback != null) {
                    this._callback.call(this._callback, this._portlet_id);
                }
            } 
        },
        setCallback: function(portlet_id, callback) {
            this._portlet_id = portlet_id;
            this._callback = callback;
        },
        setParam: function(key, value) {
            this.params[key] = value;
        },
        setReloadIds: function(values) {
            this.reloadIds = values;
        },
        clearParams: function() {
            this.params = new Array();
        },
        clearReloadIds: function() {
            this.reloadIds = new Array();
        },
        reload: function (url) {
            this.href = url;
            this.isPositionLock = true;
            this.refresh();
        },
        _position: function(){
            // summary: position modal dialog in center of screen
            
            if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
            var viewport = dijit.getViewport();
            var mb = dojo.marginBox(this.domNode);

            var style = this.domNode.style;
            style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
            if(Math.floor((viewport.t + (viewport.h - mb.h)/2)) > 0){
                style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
            } else {
                style.top = 0 + "px";
            }
        },
        layout: function() {
            if(this.domNode.style.display == "block"){
                this._underlay.layout();
                //this._position();
            }
        },
        _downloadExternalContent: function(){
            this._onUnloadHandler();
    
            // display loading message
            // TODO: maybe we should just set a css class with a loading image as background?
            
            this._setContent(
                this.onDownloadStart.call(this)
            );
            
            var self = this;
            var getArgs = {
                preventCache: (this.preventCache || this.refreshOnShow),
                url: this.href,
                handleAs: "text",
                content: this.params,
                headers: { X_REQUESTED_WITH: "XMLHttpRequest" }
            };
            if(dojo.isObject(this.ioArgs)){
                dojo.mixin(getArgs, this.ioArgs);
            }
    
            var hand = this._xhrDfd = (this.ioMethod || dojo.xhrPost)(getArgs);
    
            hand.addCallback(function(html){
                self.clearParams();
                self.clearReloadIds();
                try{
                    self.onDownloadEnd.call(self);
                    self._isDownloaded = true;
                    self.setContent.call(self, html); // onload event is called from here
                }catch(err){
                    self._onError.call(self, 'Content', err); // onContentError
                }
                delete self._xhrDfd;
                return html;
            });
    
            hand.addErrback(function(err){
                if(!hand.cancelled){
                    // show error message in the pane
                    self._onError.call(self, 'Download', err); // onDownloadError
                }
                delete self._xhrDfd;
                return err;
            });
        }    
    }
);

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dialog.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dropdown.js
 */
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

dojo.provide("aimluck.widget.Dropdown");

dojo.require("dijit.form.Button");

dojo.declare("aimluck.widget.Dropdown", [dijit.form.DropDownButton], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    selectId: "",
    iconURL: "",
    iconAlt: "",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",

    _openDropDown: function(){
        this.inherited(arguments);
        //For google chrome and Firefox 3.6 or higher
        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") > -1 || (dojo.isFF && (dojo.isFF >= 3.6))) {
            var pNode = this.dropDown.domNode.parentNode;
            var top = pNode.style.top.replace("px","");
            top_new = parseInt(top) + window.scrollY;
            pNode.style.top = top_new + "px";
        }
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dropdown.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Menu.js
 */
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

dojo.provide("aimluck.widget.Menu");
dojo.provide("aimluck.widget.Menuitem");
dojo.provide("aimluck.widget.Menuseparator");
dojo.provide("aimluck.widget.Menubar");
dojo.provide("aimluck.widget.DropDownButton");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.Menu");
dojo.require("dijit.Toolbar");
dojo.require("dijit.form.Button");

dojo.declare("aimluck.widget.Menuitem", [dijit.MenuItem], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
        templateString:
         '<tr class="dijitReset dijitMenuItem"'
        +'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
        +'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass} menuItemIcon" dojoAttachPoint="iconNode" ></div></td>'
        +'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem" nowrap="nowrap"></td>'
        +'<td class="dijitReset" dojoAttachPoint="arrowCell">'
            +'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
            +'<span class="dijitInline moz-inline-box dijitArrowNode dijitMenuExpandInner">+</span>'
            +'</div>'
        +'</td>'
        +'</tr>',
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.MenuButton", [dijit.form.Button], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
    itemClass:"",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box dijitButton\"\n\tdojoAttachEvent=\"onclick:_onButtonClick,onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\"><div class='dijitRight'><button class=\"dijitStretch dijitButtonNode dijitButtonContents  ${itemClass}\" dojoAttachPoint=\"focusNode,titleNode\"\n\t\t\ttype=\"${type}\" waiRole=\"button\" waiState=\"labelledby-${id}_label\"><div class=\"dijitInline ${iconClass} menuItemIcon \" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span></button></div></div>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menu", [dijit.Menu], {
//    submenuOverlap: 2,
//    submenuDelay: 0,
    templateString:
            '<table class="popupMenu dijitMenuTable" waiRole="menu" dojoAttachEvent="onkeypress:_onKeyPress">' +
                '<tbody class="dijitReset" dojoAttachPoint="containerNode"></tbody>'+
            '</table>'
});


dojo.declare("aimluck.widget.Menuseparator", [dijit.MenuSeparator], {
    templateString: "<tr class=\"menuSeparator\"><td colspan=4>" + "<div class=\"menuSeparatorTop\"></div>" + "<div class=\"menuSeparatorBottom\"></div>" + "</td></tr>"
});

dojo.declare(
    "aimluck.widget.ToolbarSeparator",
    [ dijit.ToolbarSeparator ],
{
    // summary
    //  A line between two menu items
    templateString: '<div class="dijitInline moz-inline-box">&nbsp;｜&nbsp;</div>',
    postCreate: function(){ dojo.setSelectable(this.domNode, false); },
    isFocusable: function(){ return false; }
});

dojo.declare("aimluck.widget.DropDownButton", [dijit.form.DropDownButton], {
    label: "",
    iconSrc: "",
    iconClass: "",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<button class=\"dijitStretch dijitButtonNode dijitButtonContents\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><div class=\"dijitInline ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\">${label}</span\n\t\t><span class='dijitA11yDownArrow'>&#9660;</span>\n\t</button>\n</div></div>\n"
});

dojo.declare("aimluck.widget.ComboButton", [dijit.form.ComboButton], {
    // summary
    //      left side is normal button, right side displays menu
    url: "",
    itemClass:"",
    templateString:"<table class='dijit dijitReset dijitInline dijitLeft moz-inline-box ${itemClass} '\n\tcellspacing='0' cellpadding='0'\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\">\n\t<tr>\n\t\t<td\tclass=\"dijitStretch dijitButtonContents dijitButtonNode\"\n\t\t\ttabIndex=\"${tabIndex}\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onButtonClick\"  dojoAttachPoint=\"titleNode\"\n\t\t\twaiRole=\"button\" waiState=\"labelledby-${id}_label\">\n\t\t\t<div class=\"dijitMenuItemIcon ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div>\n\t\t\t<span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span>\n\t\t</td>\n\t\t<td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton'\n\t\t\tdojoAttachPoint=\"popupStateNode,focusNode\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onArrowClick, onkeypress:_onKey\"\n\t\t\tstateModifier=\"DownArrow\"\n\t\t\ttitle=\"${optionsTitle}\" name=\"${name}\"\n\t\t\twaiRole=\"button\" waiState=\"haspopup-true\"\n\t\t><div waiRole=\"presentation\">&#9660;</div>\n\t</td></tr>\n</table>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menubar", [dijit.Toolbar], {
    selectedIndex: -1,
    templateString:
        '<div class="tundra"><div class="dijit dijitToolbar" waiRole="toolbar" tabIndex="${tabIndex}" dojoAttachPoint="containerNode">' +
        '</div></div>',
    postCreate:function () {
        dijit.Toolbar.superclass.postCreate.apply(this, arguments);
        this.makeMenu(this.items);
        this.isShowingNow = true;
    },
    makeMenu:function(items) {
        var _this = this;
        var _count = 0;
        dojo.forEach(items, function(itemJson){
                if(itemJson.submenu){ 
                    var menu = new aimluck.widget.Menu({id: itemJson.caption, style: "display: none;" });
                    dojo.forEach(itemJson.submenu, function(itemJson2){
                        if(itemJson2 != null){
                            if (itemJson2.caption) {
                                menu.addChild( new aimluck.widget.Menuitem({label: itemJson2.caption, url: itemJson2.url, iconClass: itemJson2.iconClass}) );
                            } else {
                                menu.addChild( new aimluck.widget.Menuseparator() );
                            }
                        }
                    });
                    var _itemClass = "";
                    if(_this.selectedIndex == parseInt(_count) ){
                        _itemClass += "menuBarItemSelected";
                    }  
                    var ddb = new aimluck.widget.ComboButton({ label: itemJson.caption, iconClass: itemJson.iconClass, dropDown: menu, url: itemJson.url, itemClass:_itemClass});
                    ddb.addChild(menu);
                    _this.addChild(ddb);
                } else if(itemJson.url) {
                    var _itemClass = "";
                    if(_this.selectedIndex == _count){
                        _itemClass += "menuBarItemSelected";
                    }
                    var ddb = new aimluck.widget.MenuButton({id: itemJson.caption + "_Button" + _count, label: itemJson.caption , url: itemJson.url, iconClass: itemJson.iconClass,  itemClass:_itemClass });
                    _this.addChild(ddb);
                } else {
                    _this.addChild(new aimluck.widget.ToolbarSeparator());
                }
                _count++;
               
        }); 

    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Menu.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/
