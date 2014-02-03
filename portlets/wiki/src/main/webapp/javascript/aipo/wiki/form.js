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
