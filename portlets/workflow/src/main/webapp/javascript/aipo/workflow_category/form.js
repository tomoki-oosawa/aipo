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
dojo.provide("aipo.workflow_category");

aipo.workflow_category.onLoadWorkflowCategoryDialog = function(portlet_id){

    var obj = dojo.byId("category_name");
    if(obj){
       obj.focus();
    }

}

aipo.workflow_category.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('workflow_category');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.workflow_category.onChangeSelecter = function(portletId , url , values, named , flgName){

	dojo.byId(flgName).checked = false;
    var callbackArgs = new Array();
    callbackArgs["named"] = "workflow_" + named;

    aimluck.io.sendRawData(url + "&value=" + values,values,aipo.workflow_category.setTemplate,callbackArgs);

    return false;

}

aipo.workflow_category.setTemplate = function(array,rtnData){
	
	var cStartIdx = rtnData["type"].indexOf("\/*");
    var cEndIdx = rtnData["type"].lastIndexOf("*\/");
    var rawData = dojo.eval(rtnData["type"].substring(cStartIdx+2, cEndIdx));
        
    var jsonData = "";
        
    if(dojo.isArray(rawData) && rawData.length > 0) {
        jsonData = rawData[0];
    }

    if ( jsonData != "") {
        dojo.byId(array["named"]).style.display = "";
    } else {
        dojo.byId(array["named"]).style.display = "none";
    }
    dojo.byId(array["named"]).value = jsonData;
}
