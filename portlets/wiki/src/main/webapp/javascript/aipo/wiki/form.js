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

dojo.provide("aipo.wiki");

aipo.wiki.onLoadWikiDialog = function(portlet_id) {
  var obj = dojo.byId("wiki_name");
  if (obj) {
    obj.focus();
  }
}

aipo.wiki.toggleMenu=function (node,filters,event){
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
}

/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.wiki.initFilterSearch = function(portlet_id) {
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
aipo.wiki.finFilterSearch = function(portlet_id) {
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
aipo.wiki.filterSetDefault=function(portlet_id,type){
  var ul=dojo.query("ul.filtertype[data-type="+type+"]")[0];
  var defval=ul.getAttribute("data-defaultparam");
  var defaultli=dojo.query("li[data-param="+defval+"]",ul);
  aipo.wiki.filterSelect(ul,defaultli);
  aipo.wiki.filteredSearch(portlet_id);
}

aipo.wiki.filterSelect=function(ul,li){
  dojo.query("li",ul).removeClass("selected");
  dojo.query(li).addClass("selected");
}

aipo.wiki.filterClick=function(portlet_id,thisnode,event){
  var li=thisnode.parentNode;
  var ul=li.parentNode;
  var param=li.getAttribute("data-param");//liのdata-param
  aipo.wiki.filterSelect(ul,li);
  aipo.wiki.filteredSearch(portlet_id);
}

aipo.wiki.onReceiveMessage = function(msg) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.portletReload('wiki');
  }
  if (dojo.byId('messageDiv')) {
    dojo.byId('messageDiv').innerHTML = msg;
  }
}

/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.wiki.initFilterSearch = function(portlet_id) {
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
aipo.wiki.finFilterSearch = function(portlet_id) {
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
aipo.wiki.filteredSearch=function(portlet_id){
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
}

aipo.wiki.formSwitchCategoryInput = function(button) {
  if (button.form.is_new_category.value == 'TRUE'
      || button.form.is_new_category.value == 'true') {
    button.value = aimluck.io.escapeText("wiki_val_switch1");
    aipo.wiki.formCategoryInputOff(button.form);
  } else {
    button.value = aimluck.io.escapeText("wiki_val_switch2");
    aipo.wiki.formCategoryInputOn(button.form);
  }
}

aipo.wiki.formCategoryInputOn = function(form) {
  dojo.byId('wikiCategorySelectField').style.display = "none";
  dojo.byId('wikiCategoryInputField').style.display = "";

  form.is_new_category.value = 'TRUE';
}

aipo.wiki.formCategoryInputOff = function(form) {
  dojo.byId('wikiCategoryInputField').style.display = "none";
  dojo.byId('wikiCategorySelectField').style.display = "";

  form.is_new_category.value = 'FALSE';
}

aipo.wiki.onLoadCategoryDialog = function(portlet_id){

  var obj = dojo.byId("category_name");
  if(obj){
     obj.focus();
  }
 
}
aipo.wiki.insertTag=function(textarea,tag,text){
      var index = textarea.selectionStart;

       var length = textarea.selectionEnd - index;
      if(length==0){
       textarea.value = textarea.value.substr(0, index) +
       tag +text+tag+ textarea.value.substr(index + length);
      }else{
        textarea.value = textarea.value.substr(0, index) +tag+textarea.value.substr(index, length)
                         +tag+textarea.value.substr(index + length);
      }
       textarea.focus();
       var newCaretPosition = index + tag.length;
       textarea.setSelectionRange(newCaretPosition, newCaretPosition);
}
aipo.wiki.bold=function(textarea){
    tag="'''";
    text="太文字文";
    aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.italic=function(textarea){
  tag="''";
  text="斜体文";
  aipo.wiki.insertTag(textarea,tag,text);
  
}
aipo.wiki.underline=function(textarea){
  tag="%%%";
  text="下線";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.strikethrough=function(textarea){
  tag="%%";
  text="取り消し線";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.header=function(textarea){
  tag="=";
  text="見出し１";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.headerTwo=function(textarea){
  tag="==";
  text="見出し２";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.headerThree=function(textarea){
  tag="===";
  text="見出し３";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.ulist=function(textarea){
  tag="* ";
  text="番号なし項目";
  aipo.wiki.insertTag(textarea,tag,text);
}
aipo.wiki.olist=function(textarea){
  tag="#";
  text="番号付き項目";
  aipo.wiki.insertTag(textarea,tag,text);
}

