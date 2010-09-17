/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

aipo.todo.onLoadTodoDialog = function(portlet_id){

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
        button.value = '新しく入力する';
        aipo.todo.formCategoryInputOff(button.form);
    } else {
        button.value = '一覧から選択する';
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

aipo.todo.onReceiveMessage = function(msg){

    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('todo');
        aipo.portletReload('schedule');
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
