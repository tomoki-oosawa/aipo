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
