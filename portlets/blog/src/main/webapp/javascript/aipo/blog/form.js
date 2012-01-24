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

dojo.provide("aipo.blog");

dojo.require("aipo.widget.DropdownDatepicker");

aipo.blog.onLoadBlogDialog = function(pid){
    var obj = dojo.byId("title");
    if(obj){
        obj.focus();
    }
}

aipo.blog.onLoadBlogThemaDialog = function(pid){
    var obj = dojo.byId("thema_name");
    if(obj){
        obj.focus();
    }
}

aipo.blog.onLoadBlogDetailDialog = function(portlet_id){
    aipo.portletReload('whatsnew');
}

aipo.blog.onLoadBlogCommentDialog = function(pid){
    var obj = dojo.byId("comment");
    if(obj){
        obj.focus();
    }
    aipo.portletReload('whatsnew');
}

aipo.blog.expandImageWidth = function(img) {
  var class_name = img.className;
  if(! class_name.match(/width_auto/i)) {
    img.className = img.className.replace( /\bwidth_thumbs\b/g, "width_auto");
  } else {
    img.className = img.className.replace( /\bwidth_auto\b/g, "width_thumbs");
  }
}

aipo.blog.ExpandImage = function(url) {
  var im = new Image();
  im.src = url;
  var imwidth = im.width;
  if (screen.width < im.width){
    imwidth = screen.width;
  }
  var imheight = im.height;
  if (screen.height < im.height){
    imheight = screen.height;
  }
  var x = (screen.width  - imwidth) / 2;
  var y = (screen.height - imheight) / 2;
  var popup = window.open("image","_blank","left=+x+","top=+y+","width=+imwidth+","height=+imheight+","scrollbars=yes","resizable=yes");
  popup.window.document.open();
  popup.window.document.write('<html><head><title>'+im.alt+'</title></head><body style="margin:0;padding:0;border:0;"><img src="'+im.src+'" width="100%" alt="" /></body></html>');
  popup.window.document.close();
}


aipo.blog.popupCenter = function(num) {
    var i = new Image();
    i.onload = function(){
        var image_width = i.width;
        var image_height = i.height;
        var coord = dojo.coords(dojo.byId("modalDialog"),false);
        if (document.all) {
            mX = document.documentElement.clientWidth;
            mY = document.documentElement.clientHeight;
        } else {
            mX = window.innerWidth;
            mY = window.innerHeight;
        }
        if(image_width != 0) {
            var x_posision = mX / 2 - image_width/2 - coord.x;
            dojo.byId("popc").style.left =  x_posision +'px';
            var y_posision =  mY / 2 - image_height/2 - coord.y;
            dojo.byId("popc").style.top = y_posision +'px';
        }
        dojo.byId("popc").innerHTML = '<a href="javascript:aipo.blog.popupCenterHide();"><img src='+num+'></a>';
    }
    i.src = num;
}


aipo.blog.popupCenterHide = function() {
    dojo.byId("popc").innerHTML = "";
}

aipo.blog.formSwitchThemaInput = function(button) {
    if(button.form.is_new_thema.value == 'TRUE' || button.form.is_new_thema.value == 'true') {

    	button.value = aimluck.io.escapeText("blog_val_switch1");
        aipo.blog.formThemaInputOff(button.form);
    } else {
		button.value = aimluck.io.escapeText("blog_val_switch2");
        aipo.blog.formThemaInputOn(button.form);
    }
}

aipo.blog.formThemaInputOn = function(form) {
    dojo.byId('blogThemaSelectField').style.display = "none";
    dojo.byId('blogThemaInputField').style.display = "";

    form.is_new_thema.value = 'TRUE';
}

aipo.blog.formThemaInputOff = function(form) {
    dojo.byId('blogThemaInputField').style.display = "none";
    dojo.byId('blogThemaSelectField').style.display = "";

    form.is_new_thema.value = 'FALSE';
}

aipo.blog.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('blog');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.blog.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('blog');
    }
    if (dojo.byId('listmessageDiv')) {
        dojo.byId('listmessageDiv').innerHTML = msg;
    }
}

aipo.blog.onSubmitSerchButton = function(form,url,p_id){
    var exec_url = url;
    var search_params = [["sword",form.sword.value]];
    aipo.viewPage(exec_url, p_id, search_params);

    if(form.sword.value == ""){
       return false;
    }
    aipo.viewPage(exec_url, p_id);
}

aipo.blog.delCommentReply = function(button, id, indicator_id, p_id) {

	var val1 = aimluck.io.escapeText("blog_val_confirm1");


  if(confirm(val1)) {
    disableButton(button.form);
    var url = button.form.action + '&mode=commentdel&' + button.name + '=1&comment_id='+id;
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, p_id, aipo.blog.onReceiveMessage);
  }
}

aipo.blog.delBlogEntry = function(button, indicator_id, p_id) {

	var val2 = aimluck.io.escapeText("blog_val_confirm2");
  if(confirm(val2)) {
    disableButton(button.form);
    var url = button.form.action + '&mode=delete&' + button.name + '=1';
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,p_id,aipo.blog.onReceiveMessage);
  }
}
