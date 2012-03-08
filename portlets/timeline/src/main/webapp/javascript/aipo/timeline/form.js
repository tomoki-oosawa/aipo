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

dojo.require("aipo.widget.MemberNormalSelectList");

dojo.provide("aipo.timeline");

aipo.timeline.addHiddenValue = function(form, name, value){
    if (form[name] && document.getElementsByName(name).item(0)) {
        form[name].value = value;
    } else {
        var q = document.createElement('input');
        q.type = 'hidden';
        q.name = name;
        q.value = value;
        form.appendChild(q);
    }
}

aipo.timeline.addLike = function(form, name, value){
}

aipo.timeline.showCommentField = function(pid, tid){
	dojo.byId('commentField_' + pid + '_' + tid).style.display = "";
	dojo.byId('note_' + pid + '_' + tid).focus();
	var dummy = dojo.byId('commentInputDummy_' + pid + '_' + tid);
	if(typeof dummy != "undefined" && dummy != null){
		dojo.byId('commentInputDummy_' + pid + '_' + tid).style.display = "none";
	}
}

aipo.timeline.showCommentAll = function(pid, tid){
	dojo.byId('commentCaption_' + pid + '_' + tid).style.display="none";
	dojo.query('#comments_' + pid + '_' + tid + ' .message').forEach(function(item) {
		item.style.display = "";
	});
}

aipo.timeline.onScroll = function(url, pid){
	var scrollTop = dojo.byId("timeline").scrollTop;
	var clientHeight = dojo.byId("timeline").clientHeight;
	var scrollHeight = dojo.byId("timeline").scrollHeight;
	var remain = scrollHeight - clientHeight - scrollTop;
	if(remain < 5){
		aipo.viewPage(url, pid, [['scrollTop', scrollTop]]);
	}
}

aipo.timeline.setScrollTop = function(scrollTop){
	dojo.byId("timeline").scrollTop = scrollTop;
}

aipo.timeline.onKeyDown = function(pid, tid){
	var val = dojo.byId("note_" + pid + "_" + tid).value;

//	if(tid == "0"){
//		dojo.byId("textCount_" + pid).innerHTML = 500 - val.length + "文字";
//	}

	var shadowVal = val.replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/&/g, '&amp;')
    .replace(/\n$/, '<br/>&nbsp;')
    .replace(/\n/g, '<br/>')
    .replace(/ {2,}/g, function(space) {
        return times('&nbsp;', space.length) + ' ';
    });

	var shadow = document.createElement("div");
	shadow.id="shadow"
	shadow.style.position="absolute";
	shadow.style.top="-1000";
	shadow.style.left="-1000";
	shadow.style.border="0";
	shadow.style.outline="0";
	shadow.style.lineHeight="normal";
	shadow.style.height="auto";
	shadow.style.resize="none";
	shadow.cols="10"
	//これが呼ばれる際の入力はまだ入ってこないので、適当に1文字追加
	shadow.innerHTML = shadowVal + "あ";


	var objBody = document.getElementsByTagName("body").item(0);
	objBody.appendChild(shadow);

	dojo.byId("shadow").style.width=document.getElementById("note_" + pid + "_" + tid).offsetWidth + "px";

	var shadowHeight = document.getElementById("shadow").offsetHeight;

	if(shadowHeight < 18)
		shadowHeight = 18;
	dojo.byId("note_" + pid + "_" + tid).style.height = shadowHeight + 21 + "px";
	objBody.removeChild(shadow);
}

aipo.timeline.onKeyUp = function(pid){
//	var val = dojo.byId("note_" + pid + "_0").value;
//	dojo.byId("textCount_" + pid).innerHTML = 500 - val.length + "文字";
}

aipo.timeline.onReceiveMessage = function(msg){
	var pid = dojo.byId("getTimelinePortletId").innerHTML;
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog_" + pid);
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('timeline');
    }
    if (dojo.byId("messageDiv_" + pid) ){
        dojo.byId("messageDiv_" + pid).innerHTML = msg;
    }
}

aipo.timeline.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('timeline');
    }
    if (dojo.byId('listmessageDiv')) {
        dojo.byId('listmessageDiv').innerHTML = msg;
    }
}

aipo.timeline.popupCenter = function(num) {
    var i = new Image();
    i.onload = function(){
        var image_width = i.width;
        var image_height = i.height;
        var coord = dojo.coords(dojo.byId("modalDialog"),false);
        dojo.byId("popc").innerHTML = '<a href="javascript:aipo.timeline.popupCenterHide();"><img src='+num+'></a>';
    }
    i.src = num;
}

aipo.timeline.popupCenterHide = function() {
    dojo.byId("popc").innerHTML = "";
}

aipo.timeline.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }
    aipo.portletReload('timeline');
}


aipo.timeline.ellipse_message=function(_this){
	var p=_this.parentElement;
	var body=p.parentElement;
	dojo.query(p).addClass("opened");
	dojo.query(".text_exposed_show",body).removeClass("ellipsis");
}
