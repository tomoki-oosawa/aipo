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
dojo.provide("aipo.blog");

dojo.require("aipo.widget.DropdownDatepicker");
dojo.require("aipo.widget.MemberNormalSelectList");


aipo.blog.toggleMenu=function (node,filters,event){
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
         if(html.bottom-node.clientHeight>rect.bottom){
       		node.style.top=rect.bottom+scroll.top+"px";
        }else{
        	node.style.top=rect.top-node.clientHeight+scroll.top+"px";
        }
        node.style.opacity="";
    } else {
        dojo.query("div.menubar").style("display", "none");
    }
};

/**
 * 検索バーの幅を調節する。
 * @param portlet_id
 */
aipo.blog.initFilterSearch=function(portlet_id){
	var q=dojo.byId("q"+portlet_id);
	var filters=dojo.byId('filters_'+portlet_id);
	if(filters && q){
		q.style.paddingLeft=filters.offsetWidth+"px";
	}
};


/**
 * urlを整形して送信。
 */
aipo.blog.filteredSearch=function(portlet_id){
	//filtertype

	var baseuri=dojo.byId("baseuri_"+portlet_id).value;

	var types=[];
	var params=[];
	dojo.query("ul.filtertype_"+portlet_id,dojo.byId("searchForm_"+portlet_id)).forEach(function(ul){
			//console.info(ul);
			var type=ul.getAttribute("data-type");
			types.push(type);

			var activeli=dojo.query("li.selected",ul)[0];
			if(activeli){
				var param=activeli.getAttribute("data-param");
				params.push(param);
			}else{
				params.push(ul.getAttribute("data-defaultparam"));
			}
		}
	);
	var q=dojo.byId("q"+portlet_id);
	var search =q?encodeURIComponent(q.value):"";
	baseuri+="&filter="+params.join(",");
	baseuri+="&filtertype="+types.join(",");
	baseuri+="&keyword="+search;
	aipo.viewPage(baseuri,portlet_id);
};

/**
 * 指定したフィルタにデフォルト値を設定する。(または消す)
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.blog.filterSetDefault=function(portlet_id,type){
	var ul=dojo.query("ul.filtertype[data-type="+type+"]",dojo.byId("searchForm_"+portlet_id))[0];
	var defval=ul.getAttribute("data-defaultparam");
	var defaultli=dojo.query("li[data-param="+defval+"]",ul);
	aipo.blog.filterSelect(ul,defaultli);
	aipo.blog.filteredSearch(portlet_id);
};

aipo.blog.filterSelect=function(ul,li){
	dojo.query("li",ul).removeClass("selected");
	dojo.query(li).addClass("selected");
};
/**
 * フィルタを選択した時に発生させるイベント　クリックされたノードをフィルタに追加
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.blog.filterClick=function(portlet_id,thisnode,event){
	var li=thisnode.parentNode;
	var ul=li.parentNode;
	var param=li.getAttribute("data-param");//liのdata-param
	aipo.blog.filterSelect(ul,li);
	aipo.blog.filteredSearch(portlet_id);
};

aipo.blog.onLoadMsgboardDialog = function(portlet_id){
  var obj = dojo.byId("topic_name");
  if(obj){
     obj.focus();
  }
}

aipo.blog.onChangeFilter=aipo.blog.onChangeSearch=function (baseuri,portlet_id){
	var search = encodeURIComponent(dojo.byId("q").value);
	baseuri+="?template=MsgboardTopicListScreen";
	baseuri+="&filter="+dojo.byId("topic").value;
	baseuri+="&filtertype=category";
	baseuri+="&search="+search;
	aipo.viewPage(baseuri,portlet_id);
}

aipo.blog.showMember = function(button) {
  dojo.byId('Block-GroupMember-Show').style.display="";
  dojo.byId('is_member').value = "TRUE";
}

aipo.blog.hideMember = function(button) {
  dojo.byId('Block-GroupMember-Show').style.display="none";
  dojo.byId('member_to').options.length = 0;
  dojo.byId('is_member').value = "FALSE";
}

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
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);

    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('blog');
        aipo.portletReload('timeline');
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
