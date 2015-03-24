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
dojo.provide("aipo.todo");

dojo.require("aipo.widget.DropdownDatepicker");

aipo.todo.toggleMenu=function (node,filters,event){
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
aipo.todo.initFilterSearch = function(portlet_id) {
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
aipo.todo.finFilterSearch = function(portlet_id) {
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
aipo.todo.filteredSearch=function(portlet_id){
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
aipo.todo.filterSetDefault=function(portlet_id,type){
	var ul=dojo.query("ul.filtertype[data-type="+type+"]")[0];
	var defval=ul.getAttribute("data-defaultparam");
	var defaultli=dojo.query("li[data-param="+defval+"]",ul);
	aipo.todo.filterSelect(ul,defaultli);
	aipo.todo.filteredSearch(portlet_id);
};

aipo.todo.filterSelect=function(ul,li){
	dojo.query("li",ul).removeClass("selected");
	dojo.query(li).addClass("selected");
};

/**
 * フィルタを選択した時に発生させるイベント　クリックされたノードをフィルタに追加
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.todo.filterClick=function(portlet_id,thisnode,event){
	var li=thisnode.parentNode;
	var ul=li.parentNode;
	var param=li.getAttribute("data-param");//liのdata-param
	aipo.todo.filterSelect(ul,li);
	aipo.todo.filteredSearch(portlet_id);
};

aipo.todo.onLoadTodoDialog = function(portlet_id){
  var url_userlist = dojo.byId('urlUserlist'+portlet_id).value;
  var login_user_id = dojo.byId('loginUser'+portlet_id).value;
  var todo_user_id = dojo.byId('todoUser'+portlet_id).value;

  if(todo_user_id == 0) {
      todo_user_id = login_user_id;
  }
  if(url_userlist){
      aipo.todo.changeGroup(url_userlist, 'LoginUser', todo_user_id);
  }

  var obj = dojo.byId("todo_name");
  if(obj){
     obj.focus();
  }
}

aipo.todo.onLoadCategoryDialog = function(portlet_id){

  var obj = dojo.byId("category_name");
  if(obj){
     obj.focus();
  }
}

aipo.todo.formSwitchCategoryInput = function(button) {
    if(button.form.is_new_category.value == 'TRUE' || button.form.is_new_category.value == 'true') {
        button.value = aimluck.io.escapeText("todo_val_switch1");
        aipo.todo.formCategoryInputOff(button.form);
    } else {
        button.value = aimluck.io.escapeText("todo_val_switch2");
        aipo.todo.formCategoryInputOn(button.form);
    }
}

aipo.todo.formCategoryInputOn = function(form) {
    dojo.byId('todoCategorySelectField').style.display = "none";
    dojo.byId('todoCategoryInputField').style.display = "";

    form.is_new_category.value = 'TRUE';
}

aipo.todo.formCategoryInputOff = function(form) {
    dojo.byId('todoCategoryInputField').style.display = "none";
    dojo.byId('todoCategorySelectField').style.display = "";

    form.is_new_category.value = 'FALSE';
}

aipo.todo.changeGroup = function(link, group, sel) {
    aimluck.utils.form.createSelect("user_id", "destuserDiv", link + "?mode=group&groupname=" + group + "&inc_luser=true", "userId", "aliasName", sel, '', 'class="w49"');
}

aipo.todo.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('todo');
        aipo.portletReload('schedule');
        aipo.portletReload('timeline');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }

}

aipo.todo.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('todo');
        aipo.portletReload('schedule');
    }
    if (dojo.byId('listmessageDiv')) {
        dojo.byId('listmessageDiv').innerHTML = msg;
    }
}

aipo.todo.doKeywordSearch = function(baseuri, portlet_id) {
    var params = new Array(2);
    params[0] = ["template", "ToDoListScreen"];
    params[1] = ["keyword", dojo.byId("q"+portlet_id).value];
    aipo.viewPage(baseuri, portlet_id, params);
}