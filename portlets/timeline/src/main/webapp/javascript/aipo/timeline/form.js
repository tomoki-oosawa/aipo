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

aipo.timeline.addGood = function(form, name, value){
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

aipo.timeline.showCommentField = function(tid){
	dojo.byId('commentField' + tid).style.display = "";
	dojo.byId('commentInputDummy' + tid).style.display = "none";
}

aipo.timeline.showCommentAll = function(tid){
	dojo.byId('commentCaption' + tid).style.display="none";
	dojo.query('#comments' + tid + ' .message').forEach(function(item) {
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
	var val = dojo.byId("note_" + pid + "_0").value;
	if(tid == "0"){
		dojo.byId("textCount_" + pid).innerHTML = 500 - val.length + "文字";
	}
	var num = 0;
	if(val.match(/\n|\r\n/g) != null){
		num = val.match(/\n|\r\n/g).length;
	}
	var splitval = val.split(/\n|\r\n/g);
	for(i in splitval){
		if(splitval[i].length > 72){
			num += Math.floor(splitval[i].length / 72);
		}
	}
	dojo.byId("note_" + pid + "_" + tid).style.height = num * 18 + 39 + "px";
}

aipo.timeline.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('timeline');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
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
        dojo.byId("popc").innerHTML = '<a href="javascript:aipo.timeline.popupCenterHide();"><img src='+num+'></a>';
    }
    i.src = num;
}

aipo.timeline.popupCenterHide = function() {
    dojo.byId("popc").innerHTML = "";
}

