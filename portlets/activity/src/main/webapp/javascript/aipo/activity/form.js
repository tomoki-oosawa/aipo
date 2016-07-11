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
dojo.provide("aipo.activity");

aipo.activity.setListSize = function(){
	if(dojo.isIE){
		dojo.query(".activityList li").forEach(function(elem){
			elem.style.width = "394px";
		});
	}
}

aipo.activity.toggleMenu = function (node, filters, event) {
	  var rect = filters.getBoundingClientRect();
	  var html = document.documentElement.getBoundingClientRect();
	  if (node.style.display == "none") {
	    dojo.query("div.menubar").style("display", "none");

	    var scroll = {
	      left: document.documentElement.scrollLeft || document.body.scrollLeft,
	      top: document.documentElement.scrollTop || document.body.scrollTop
	    };
	    node.style.opacity = "0";
	    setTimeout( function(){
			dojo.style(node, "display" , "block");
		}, 0);
	    if (html.right - node.clientWidth > rect.left) {
	      node.style.left = rect.left + scroll.left + "px";
	    } else {
	      node.style.left = rect.right - node.clientWidth + scroll.left + "px";
	    }
	    if (html.bottom - node.clientHeight > rect.bottom || event) {
	      node.style.top = rect.bottom + scroll.top + "px";
	    } else {
	      node.style.top = rect.top - node.clientHeight + scroll.top + "px";
	    }
	    node.style.opacity = "";
	  } else {
	    dojo.query("div.menubar").style("display", "none");
	  }
	}

	/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.activity.initFilterSearch = function(portlet_id) {
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
			if (filterOffset != 0) {
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
aipo.activity.finFilterSearch = function(portlet_id) {
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
 * 指定したフィルタにデフォルト値を設定する。(または消す)
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.activity.filterSetDefault = function(portlet_id, type) {
	var ul = dojo.query("ul.filtertype[data-type=" + type + "]")[0];
	var defval = ul.getAttribute("data-defaultparam");
	var defaultli = dojo.query("li[data-param=" + defval + "]", ul);
	aipo.activity.filterSelect(ul, defaultli);
	aipo.activity.filteredSearch(portlet_id);
}

aipo.activity.filterSelect = function(ul, li) {
	dojo.query("li", ul).removeClass("selected");
	dojo.query(li).addClass("selected");
}

aipo.activity.filterClick = function(portlet_id, thisnode, event) {
	var li = thisnode.parentNode;
	var ul = li.parentNode;
	var param = li.getAttribute("data-param");//liのdata-param
	aipo.activity.filterSelect(ul, li);
	aipo.activity.filteredSearch(portlet_id);
}

/**
 * urlを整形して送信。
 */
aipo.activity.filteredSearch = function(portlet_id) {
	//filtertype
	var baseuri = dojo.byId("baseuri_" + portlet_id).value;

	var types = [];
	var params = [];
	dojo.query("ul.filtertype_" + portlet_id).forEach(function(ul) {
		//console.info(ul);
		var type = ul.getAttribute("data-type");
		types.push(type);

		var activeli = dojo.query("li.selected", ul)[0];
		if (activeli) {
			var param = activeli.getAttribute("data-param");
			params.push(param);
		} else {
			params.push(ul.getAttribute("data-defaultparam"));
		}
	});
	var q = dojo.byId("q" + portlet_id);
	var qs = [ [ "filter", params.join(",") ],
			[ "filtertype", types.join(",") ], [ "keyword", q ? q.value : "" ] ];
	aipo.viewPage(baseuri, portlet_id, qs);
}

/**
 * フィルタを選択した時に発生させるイベント　クリックされたノードをフィルタに追加
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.activity.filterClick=function(portlet_id,thisnode,event){
	var li=thisnode.parentNode;
	var ul=li.parentNode;
	var param=li.getAttribute("data-param");//liのdata-param
	aipo.activity.filterSelect(ul,li);
	aipo.activity.filteredSearch(portlet_id);
}

/**
 * widgetからアプリケーションを選択した際にグループ情報を追加
 * @param url
 */
aipo.activity.setPostFilter = function(baseuri,filter,filtertype,portlet_id) {
	var types = [];
	var params = [];
	types.push(filtertype);
	params.push(filter);
	dojo.query("ul.filtertype_" + portlet_id).forEach(function(ul) {
		var type = ul.getAttribute("data-type");
		types.push(type);

		var activeli = dojo.query("li.selected", ul)[0];
		if (activeli) {
			var param = activeli.getAttribute("data-param");
			params.push(param);
		} else {
			params.push(ul.getAttribute("data-defaultparam"));
		}
	});
	var q = dojo.byId("q" + portlet_id);
	var qs = [ [ "filter", params.join(",") ],
			[ "filtertype", types.join(",") ], [ "keyword", q ? q.value : "" ] ];
	aipo.viewPage(baseuri, portlet_id, qs);
}


aipo.activity.reminderoff = function(){
    dojo.byId('remindernotifytype').style.display = "none";
    dojo.byId('remindernotifytiming').style.display = "none";

    aipo.activity.setWrapperHeight();
}
aipo.activity.reminderon = function(){
	dojo.byId('remindernotifytype').style.display = "";
	dojo.byId('remindernotifytiming').style.display = "";

    aipo.activity.setWrapperHeight();
}

aipo.activity.setWrapperHeight = function() {
	var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}