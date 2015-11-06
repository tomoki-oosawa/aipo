/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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

dojo.provide("aipo.msgboard");

/*通常画面用*/
//aipo.msgboard.addMouseListener=function(portlet_id){
//	dojo.query(".menubarOpenButton",dojo.byId("filters_"+portlet_id))
//		.forEach(
//	function(button){
//			 addMouseListener(filter);
//	});
//};

//aipo.js setMouseLisnerを利用

aipo.msgboard.toggleMenu=function (node,filters,event){
	var rect=filters.getBoundingClientRect();
	var html=document.documentElement.getBoundingClientRect();
	if (node.style.display == "none") {
        dojo.query("div.menubar").style("display", "none");

        var scroll={
        	left:document.documentElement.scrollLeft||document.body.scrollLeft,
        	top:document.documentElement.scrollTop||document.body.scrollTop
        };
        node.style.opacity="0";
        setTimeout( function(){
			dojo.style(node, "display" , "block");
		}, 0);
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
};

/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.msgboard.initFilterSearch = function(portlet_id) {
	var q = dojo.byId("q" + portlet_id);
	var filters = dojo.byId('filters_' + portlet_id);
	if (filters && q) {
		var filterOffset = filters.offsetWidth;
		if (aipo.userAgent.isAndroid4_0()) {
			var searchForm = dojo.query("div.filterInputField")[0];
			var fieldlength = parseInt(dojo.getComputedStyle(q).width);
			searchForm.style.left = filterOffset + "px";
			filters.style.left = -filterOffset + "px";
			q.style.width = fieldlength - filterOffset + "px";
			searchForm.style.width = fieldlength - filterOffset + "px";
			q.style.paddingLeft = "2px";
		} else {
			if(filterOffset != 0) {
				q.style.paddingLeft = filterOffset + "px";
			}
		}
	}
}

/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.msgboard.finFilterSearch = function(portlet_id) {
	if (aipo.userAgent.isAndroid4_0()) {
		var q = dojo.byId("q" + portlet_id);
		var filters = dojo.byId('filters_' + portlet_id);
		if (filters && q) {
			var filterOffset = filters.offsetWidth;
			var searchForm = dojo.query("div.filterInputField")[0];
			var fieldlength = parseInt(dojo.getComputedStyle(q).width);
			searchForm.style.left = "0px";
			filters.style.left = "0px";
			q.style.width = fieldlength + filterOffset + "px";
			searchForm.style.width = fieldlength + filterOffset + "px";
			q.style.paddingLeft = filterOffset + 2 + "px";
		}
	}
}

/**
 * urlを整形して送信。
 */
aipo.msgboard.filteredSearch=function(portlet_id){
	//filtertype

	var baseuri=dojo.byId("baseuri_"+portlet_id).value;

	var types=[];
	var params=[];
	dojo.query("ul.filtertype_"+portlet_id).forEach(function(ul){
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
	var qs=[["filter",params.join(",")],
	        ["filtertype",types.join(",")],
		["keyword",q?q.value:""]
	];
	aipo.viewPage(baseuri,portlet_id,qs);
};

/**
 * 指定したフィルタにデフォルト値を設定する。(または消す)
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.msgboard.filterSetDefault=function(portlet_id,type){
	var ul=dojo.query("ul.filtertype[data-type="+type+"]")[0];
	var defval=ul.getAttribute("data-defaultparam");
	var defaultli=dojo.query("li[data-param="+defval+"]",ul);
	aipo.msgboard.filterSelect(ul,defaultli);
	aipo.msgboard.filteredSearch(portlet_id);
};

aipo.msgboard.filterSelect=function(ul,li){
	dojo.query("li",ul).removeClass("selected");
	dojo.query(li).addClass("selected");
};
/**
 * フィルタを選択した時に発生させるイベント　クリックされたノードをフィルタに追加
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.msgboard.filterClick=function(portlet_id,thisnode,event){
	var li=thisnode.parentNode;
	var ul=li.parentNode;
	var param=li.getAttribute("data-param");//liのdata-param
	aipo.msgboard.filterSelect(ul,li);
	aipo.msgboard.filteredSearch(portlet_id);
};
aipo.msgboard.onLoadMsgboardDetail = function(portlet_id){
  aipo.portletReload('whatsnew');
}

aipo.msgboard.onLoadMsgboardDialog = function(portlet_id){
  var obj = dojo.byId("topic_name");
  if(obj){
     obj.focus();
  }
}

aipo.msgboard.onChangeFilter=aipo.msgboard.onChangeSearch=function (baseuri,portlet_id){
	var search = encodeURIComponent(dojo.byId("q").value);
	baseuri+="?template=MsgboardTopicListScreen";
	baseuri+="&filter="+dojo.byId("topic").value;
	baseuri+="&filtertype=category";
	baseuri+="&search="+search;
	aipo.viewPage(baseuri,portlet_id);
}

aipo.msgboard.onLoadCategoryDialog = function(portlet_id){
  var obj = dojo.byId("category_name");
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



aipo.msgboard.showMember = function(button) {
  dojo.byId('Block-GroupMember-Show').style.display="";
  dojo.byId('is_member').value = "TRUE";
}

aipo.msgboard.hideMember = function(button) {
  dojo.byId('Block-GroupMember-Show').style.display="none";
  dojo.byId('member_to').options.length = 0;
  dojo.byId('is_member').value = "FALSE";
}

aipo.msgboard.expandImageWidth = function(img) {
  var class_name = img.className;
  if(! class_name.match(/width_auto/i)) {
    img.className = img.className.replace( /\bwidth_thumbs\b/g, "width_auto");
  } else {
    img.className = img.className.replace( /\bwidth_auto\b/g, "width_thumbs");
  }
}

aipo.msgboard.formSwitchCategoryInput = function(button) {
    if(button.form.is_new_category.value == 'TRUE' || button.form.is_new_category.value == 'true') {
        button.value = aimluck.io.escapeText("msgboard_val_switch1");
        aipo.msgboard.formCategoryInputOff(button.form);
    } else {
        button.value = aimluck.io.escapeText("msgboard_val_switch2");
        aipo.msgboard.formCategoryInputOn(button.form);
    }
}

aipo.msgboard.formCategoryInputOn = function(form) {
    dojo.byId('msgboardCategorySelectField').style.display = "none";
    dojo.byId('msgboardCategoryInputField').style.display = "";

    form.is_new_category.value = 'TRUE';
}

aipo.msgboard.formCategoryInputOff = function(form) {
    dojo.byId('msgboardCategoryInputField').style.display = "none";
    dojo.byId('msgboardCategorySelectField').style.display = "";

    form.is_new_category.value = 'FALSE';
}

aipo.msgboard.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('msgboard');
        aipo.portletReload('timeline');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.msgboard.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('msgboard');
    }
    if (dojo.byId('listmessageDiv')) {
        dojo.byId('listmessageDiv').innerHTML = msg;
    }
}

aipo.msgboard.ajaxCheckboxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aipo.msgboard.ajaxMultiDeleteSubmit, button, url, indicator_id, portlet_id, receive );
}

aipo.msgboard.ajaxMultiDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('選択した'+button.form._name.value+'を削除してよろしいですか？なお、カテゴリに含まれるトピックはすべて削除されます。')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aipo.msgboard.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('この'+button.form._name.value+'を削除してよろしいですか？なお、カテゴリに含まれるトピックはすべて削除されます。')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}
