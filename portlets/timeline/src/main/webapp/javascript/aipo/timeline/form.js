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

aipo.timeline.addHiddenValue = function(form, name, value) {
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

aipo.timeline.addLike = function(form, name, value) {
}

aipo.timeline.showCommentField = function(pid, tid) {
  dojo.byId('commentField_' + pid + '_' + tid).style.display = "";
  dojo.byId('note_' + pid + '_' + tid).focus();
  dojo.byId('note_' + pid + '_' + tid).style.color = 'black';
  var dummy = dojo.byId('commentInputDummy_' + pid + '_' + tid);
  if (typeof dummy != "undefined" && dummy != null) {
    dojo.byId('commentInputDummy_' + pid + '_' + tid).style.display = "none";
  }
}

aipo.timeline.showCommentAll = function(pid, tid) {
  dojo.byId('commentCaption_' + pid + '_' + tid).style.display = "none";
  dojo.query('#comments_' + pid + '_' + tid + ' .message').forEach(
      function(item) {
        item.style.display = "";
      });
}

aipo.timeline.onScroll = function(url, pid, page) {
  var scrollTop = dojo.byId("timeline").scrollTop;
  var clientHeight = dojo.byId("timeline").clientHeight;
  var scrollHeight = dojo.byId("timeline").scrollHeight;
  var remain = scrollHeight - clientHeight - scrollTop;
  try {
    dojo
        .xhrPost({
          portletId : pid,
          url : url,
          encoding : "utf-8",
          handleAs : "text",
          headers : {
            X_REQUESTED_WITH : "XMLHttpRequest"
          },
          load : function(data, event) {
            var content = [];
            page++;
            content = data.split("<div id=" + '"' + "content_"
                + pid + '_1"' + ">");
            content = content[1].split("<div id=" + '"'
                + "content_end_" + pid + '"' + ">");
            dojo.byId("content_" + pid + "_" + page).innerHTML = content[0];
            var more = [];
            more = data.split("<div id=" + '"' + "more_" + pid
                + '"' + ">");
            more = more[1].split("<div id=" + '"' + "more_end_"
                + pid + '"' + ">");
            dojo.byId("more_" + pid).innerHTML = more[0];
          }
        });
  } catch (e) {
    alert(e);
  }
}

aipo.timeline.getUrl = function(url, pid) {
  try {
    dojo.xhrPost({
      portletId : pid,
      url : dojo.byId("TimelineUrl_" + pid).value,
      content : {
        "url" : url
      },
      encoding : "utf-8",
      handleAs : "text",
      headers : {
        X_REQUESTED_WITH : "XMLHttpRequest"
      },
      load : function(data, event) {
        dojo.byId("tlInputClip_" + pid).innerHTML = data;
        dojo.byId("flag_" + pid).value = "exist"
      }
    });
  } catch (e) {
    alert(e);
  }

}

aipo.timeline.setScrollTop = function(scrollTop) {
  dojo.byId("timeline").scrollTop = scrollTop;
}

aipo.timeline.onKeyUp = function(pid, tid) {
  var val = dojo.byId("note_" + pid + "_" + tid).value;

  if (tid == 0 && dojo.byId("flag_" + pid).value == "none") {
    var spritval = val.split(/\r\n|\n/g);

    for (i in spritval) {
      if (spritval[i].match(/^http/i)) {
        aipo.timeline.getUrl(spritval[i], pid)
      }
    }
  }

  var shadowVal = val.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(
      /&/g, '&amp;').replace(/\n$/, '<br/>&nbsp;')
      .replace(/\n/g, '<br/>').replace(/ {2,}/g, function(space) {
        return times('&nbsp;', space.length) + ' ';
      });

  var shadow = document.createElement("div");
  shadow.id = "shadow"
  shadow.style.position = "absolute";
  shadow.style.top = "-1000";
  shadow.style.left = "-1000";
  shadow.style.border = "0";
  shadow.style.outline = "0";
  shadow.style.lineHeight = "normal";
  shadow.style.height = "auto";
  shadow.style.resize = "none";
  shadow.cols = "10"
  // これが呼ばれる際の入力はまだ入ってこないので、適当に1文字追加
  shadow.innerHTML = shadowVal + "あ";

  var objBody = document.getElementsByTagName("body").item(0);
  objBody.appendChild(shadow);

  dojo.byId("shadow").style.width = document.getElementById("note_" + pid
      + "_" + tid).offsetWidth
      + "px";

  var shadowHeight = document.getElementById("shadow").offsetHeight;

  if (shadowHeight < 18)
    shadowHeight = 18;
  dojo.byId("note_" + pid + "_" + tid).style.height = shadowHeight + 21
      + "px";
  objBody.removeChild(shadow);
}

aipo.timeline.onReceiveMessage = function(msg) {
  var pid = dojo.byId("getTimelinePortletId").innerHTML;
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog_" + pid);
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.portletReload('timeline');
  }
  if (dojo.byId("messageDiv_" + pid)) {
    dojo.byId("messageDiv_" + pid).innerHTML = msg;
  }
}

aipo.timeline.onListReceiveMessage = function(msg) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
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
  i.onload = function() {
    var image_width = i.width;
    var image_height = i.height;
    // var coord = dojo.coords(dojo.byId("modalDialog"),false);

    var coord = {
      x : document.documentElement.scrollLeft || document.body.scrollLeft,
      y : document.documentElement.scrollTop || document.body.scrollTop
    }
    if (document.all) {
      mX = document.documentElement.clientWidth;
      mY = document.documentElement.clientHeight;
    } else {
      mX = window.innerWidth;
      mY = window.innerHeight;
    }
    if (image_width != 0 && image_height != 0) {
      var x_posision = mX / 2 - image_width / 2 + coord.x;
      dojo.byId("popc_timeline").style.left = x_posision + 'px';
      var y_posision = mY / 2 - image_height / 2 + coord.y;
      dojo.byId("popc_timeline").style.top = y_posision + 'px';
    }
    dojo.byId("popc_timeline").style.maxWidth = Math.min(800,
        (image_width + 10))
        + 'px';
    dojo.query("#popc_timeline").removeClass("hide");
    dojo.byId("popc_timeline").children[0].innerHTML = '<a href="javascript:aipo.timeline.popupCenterHide();"><img src='
        + num + '></a>';
  }
  i.src = num;
}

aipo.timeline.popupCenterHide = function() {
  dojo.query("#popc_timeline").addClass("hide");
  dojo.byId("popc_timeline").children[0].innerHTML = "";
}

aipo.timeline.hideDialog = function() {
  var arrDialog = dijit.byId("modalDialog");
  if (arrDialog) {
    arrDialog.hide();
  }
  aipo.portletReload('timeline');
}

aipo.timeline.ellipse_message = function(_this) {
  var p = _this.parentElement;
  var body = p.parentElement;
  dojo.query(p).addClass("opened");
  dojo.query(".text_exposed_show", body).removeClass("ellipsis");
}

aipo.timeline.onForcus = function(pid, tid) {
  var note = dojo.byId("note_" + pid + "_" + tid);
  if (note.value == note.defaultValue) {
    note.value = '';
    note.style.color = 'black';
  }
}

aipo.timeline.onBlur = function(pid, tid) {
  var note = dojo.byId("note_" + pid + "_" + tid);
  if (note.value == '') {
    note.value = dojo.byId("note_" + pid + "_" + tid).defaultValue;
    note.style.color = '#999999';
  }
}

aipo.timeline.onBlurCommentField = function(pid, tid) {
  var note = dojo.byId("note_" + pid + "_" + tid);
  var dummy = dojo.byId('commentInputDummy_' + pid + '_' + tid);
  var field = dojo.byId('commentField_' + pid + '_' + tid);

  if (note.value == '') {
    note.value = dojo.byId("note_" + pid + "_" + tid).defaultValue;
    note.style.color = '#999999';
    dummy.style.display = "";
    field.style.display = "none";
  }
}

aipo.timeline.nextThumbnail = function(pid, max) {
  var page = dojo.byId("TimelinePage_" + pid);
  var value = parseInt(page.value);
  var maxval = parseInt(max);
  if(value < maxval){
    dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "none";
    value++;
    page.value = value;
    dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "";
    dojo.byId("count_" + pid).innerHTML = max + " 件中 " + page.value + " 件";
  }
}

aipo.timeline.prevThumbnail = function(pid, max) {
  var page = dojo.byId("TimelinePage_" + pid);
  var value = parseInt(page.value);
  if(value > 1){
    dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "none";
    value--;
    page.value = value;
    dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "";
    dojo.byId("count_" + pid).innerHTML = max + " 件中 " + page.value + " 件";
  }
}

aipo.timeline.addText = function(form, pid){
	  if(dojo.byId("tlInputClip_" + pid).innerHTML.length > 1){
	    var page = dojo.byId("TimelinePage_" + pid);
	    if(dojo.byId("tlClipImage_" + pid + "_" + page.value) != null && dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display != "none"){
		    aipo.timeline.addHiddenValue(form, "tlClipImage", dojo.byId("tlClipImage_" + pid + "_" + page.value).children[0].src);
	    }
	    aipo.timeline.addHiddenValue(form, "tlClipTitle", dojo.byId("tlClipTitle_" + pid).children[0].innerHTML);
	    aipo.timeline.addHiddenValue(form, "tlClipUrl", dojo.byId("tlClipUrl_" + pid).children[0].innerHTML);
	    aipo.timeline.addHiddenValue(form, "tlClipBody", dojo.byId("tlClipBody_" + pid).innerHTML);
	  }
}

aipo.timeline.viewThumbnail = function(pid){
	var page = dojo.byId("TimelinePage_" + pid);
	var value = parseInt(page.value);
	if(dojo.byId("checkbox_" + pid).checked){
		dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "none";
		dojo.byId("auiSummaryMeta_" + pid).style.display = "none";
	}
	else{
		dojo.byId("tlClipImage_" + pid + "_" + page.value).style.display = "";
		dojo.byId("auiSummaryMeta_" + pid).style.display = "";
	}
}

aipo.timeline.deleteClip = function(pid){
	dojo.byId("tlInputClip_" + pid).innerHTML = "";
	dojo.byId("flag_" + pid).value = "forbidden";
}

aipo.timeline.submit = function(form, indicator_id, pid, callback){
	if(dojo.byId('note_' + pid + '_0').value != dojo.byId('note_' + pid + '_0').defaultValue){
		aimluck.io.submit(form, indicator_id, pid, callback);
	}
}
