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
dojo.provide("aipo.manhour");

aipo.manhour.onLoadManhourDialog = function(portlet_id){

    var obj = dojo.byId("name");
    if(obj){
        obj.focus();
    }

}

aipo.manhour.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(!!arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('manhour');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.manhour.onReceiveMessageDiag = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(!!arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('manhour');
    }
    if (dojo.byId('messageDivDiag')) {
        dojo.byId('messageDivDiag').innerHTML = msg;
    }
}


aipo.manhour.onChangeGroup = function(base_url, p_id){
    var select = dojo.byId('target_group_name');
    var group_name = select.options[select.selectedIndex].value;
    var exec_url = base_url+"&target_group_name="+group_name+"&target_user_id=";

    aipo.viewPage(exec_url, p_id);
}

aipo.manhour.onChangeUser = function(base_url, p_id){
    var select_group = dojo.byId('target_group_name');
    var select_user = dojo.byId('target_user_id');
    var group_name = select_group.options[select_group.selectedIndex].value;
    var user_id = select_user.options[select_user.selectedIndex].value;
    var exec_url = base_url+"&target_group_name="+group_name+"&target_user_id="+user_id;

    aipo.viewPage(exec_url, p_id);
}

aipo.manhour.onChangeCategory = function(base_url, p_id){
    var select_category = dojo.byId('commoncategory');
    var category_id = select_category.options[select_category.selectedIndex].value;
    var exec_url = base_url+"&category_id="+category_id;

    aipo.viewPage(exec_url, p_id);
}

aipo.manhour.onChangeDate = function(base_url, p_id){
    var select_year = dojo.byId('view_date_year');
    var select_month = dojo.byId('view_date_month');
    var year = select_year.options[select_year.selectedIndex].value;
    var month = select_month.options[select_month.selectedIndex].value;
    var exec_url = base_url+"&view_date_year="+year+"&view_date_month="+month;

    aipo.viewPage(exec_url, p_id);
}
