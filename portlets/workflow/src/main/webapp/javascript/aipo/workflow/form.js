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

dojo.provide("aipo.workflow");

var before = 0;

aipo.workflow.onLoadWorkflowDetail = function(portlet_id){
    aipo.portletReload('whatsnew');
}

aipo.workflow.onLoadWorkflowDialog = function(portlet_id){
    var picker = dijit.byId("membernormalselect");
    if(picker){
        var memberlist = picker;
        var select = dojo.byId('init_memberlist');
        var i;
        var s_o = select.options;
        if (s_o.length == 1 && s_o[0].value == "") return;
        for(i = 0 ; i < s_o.length; i ++ ) {
            memberlist.addOptionSync(s_o[i].value,s_o[i].text,true);
        }
    }

    var obj = dojo.byId("route_name");
    if(obj){
       obj.focus();
    }

    if(dojo.byId("mode_"+portlet_id).value=="insert")
    	dojo.byId("category_id").onchange();
}

aipo.workflow.onChangeSelecter = function(portletId , url , values, named , flgName){

    dojo.byId(flgName).checked = false;
    var callbackArgs = new Array();
    callbackArgs["named"] = "workflow_" + named;

    aimluck.io.sendRawData(url + "&value=" + values,values,aipo.workflow.setTemplate,callbackArgs);

    return false;

}

aipo.workflow.setTemplate = function(array,rtnData){

        var jsonData = aipo.workflow.getJsonDataOne(rtnData);
        var routeH = jsonData.route_h;
        var route = jsonData.route;

        var routeArray = route.split(",");
        var routeLength = (routeArray.length-1)/2;

        if ( route == null || route == "" ) {
            dojo.byId(array["named"]).style.display = "none";
        } else {
            dojo.byId(array["named"]).style.display = "";
        }

        if ( route == null || route == "") {
            dojo.byId(array["named"]).innerHTML = "";
        } else {
            dojo.byId(array["named"]).innerHTML = routeH;
        }

        memberFrom = dojo.byId('tmp_member_from');
        memberFromOpts = memberFrom.options;
        for ( i = 0; i < memberFromOpts.length ; i++ ){
            memberFromOpts[i].selected = false;
        }
        memberTo = dojo.byId('positions');
        while(memberTo.lastChild) {
             memberTo.removeChild(memberTo.lastChild);
       }
        var opt;
        for ( i = 0; i < routeLength ; i++ ){
            memberTo.options[i] = new Option(routeArray[2*i+1], routeArray[2*i]);
        }
}



aipo.workflow.categoryOnChangeSelecter = function(portletId , url , values, named , flgName, namedRoute, selectRoute){
    if(aipo.workflow.NoteChangeConfirm(flgName)){

    	before = dojo.byId('category_id').selectedIndex;

        dojo.byId(flgName).checked = false;
        var callbackArgs = new Array();
        callbackArgs["named"] = "workflow_" + named;
        callbackArgs["namedRoute"] = "workflow_" + namedRoute;
        callbackArgs["selectRoute"] = selectRoute;

        aimluck.io.sendRawData(url + "&value=" + values,values,aipo.workflow.categorySetTemplate,callbackArgs);
    }else{
    	dojo.byId('category_id').selectedIndex = before;
    }
    return false;

}

aipo.workflow.categorySetTemplate = function(array,rtnData){

        var jsonData = aipo.workflow.getJsonDataOne(rtnData);
        var template = jsonData.template;
        var routeId = jsonData.route_id.toString();
        var routeH = jsonData.route_h;
        var route = jsonData.route;

        var routeArray = route.split(",");
        var routeLength = (routeArray.length-1)/2;

        if ( routeH == null || routeH == "" ) {
            dojo.byId(array["namedRoute"]).style.display = "none";
        } else {
            dojo.byId(array["namedRoute"]).style.display = "";
        }

        if (null != template) {
          dojo.byId(array["named"]).value = template;
        } else {
          dojo.byId(array["named"]).value = "";
        }
        dojo.byId(array["namedRoute"]).value = "";
        var selectRoute = dojo.byId(array["selectRoute"]);
        var selectRouteOpts = selectRoute.options;
        selectRouteOpts[0].selected = true;

        if(!(routeId.match(/[^0-9]/g) || parseInt(routeId, 10) + "" != routeId)){
            for ( i = 0; i < selectRoute.length; i++ ) {
                if ( selectRouteOpts[i].value == routeId ) {
                    selectRouteOpts[i].selected = true;
                }
            }
            dojo.byId(array["namedRoute"]).value = routeH;

            dojo.byId('is_saved_route_button').value = aimluck.io.escapeText("workflow_val_route1");
            dojo.byId('workflowRouteSelectField').style.display = "";
            dojo.byId('workflowRouteInputField').style.display = "none";
            dojo.byId('is_saved_route').value = 'TRUE';

            memberTo = dojo.byId('positions');
            while(memberTo.lastChild) {
                memberTo.removeChild(memberTo.lastChild);
            }
            memberFrom = dojo.byId('tmp_member_from');
            memberFromOpts = memberFrom.options;
            for ( i = 0; i < memberFromOpts.length ; i++ ){
                memberFromOpts[i].selected = false;
            }
            memberTo = dojo.byId('positions');
            var opt;
            for ( i = 0; i < routeLength ; i++ ){
                memberTo.options[i] = new Option(routeArray[2*i+1], routeArray[2*i]);
            }
        }
}

aipo.workflow.onFocusComment = function(portlet_id){

}

aipo.workflow.onChangeNote = function(){
	dojo.byId("isChangedNote").checked = true;
}

aipo.workflow.NoteChangeConfirm = function(flgName){

    if(dojo.byId(flgName).checked){
      if(!confirm(aimluck.io.escapeText("workflow_val_confirm1"))){
        return false;
      }
    }
    return true;
}

aipo.workflow.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);

	if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }

        aipo.portletReload('workflow');
        aipo.portletReload('whatsnew');
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

aipo.workflow.onAccept = function(portletId){
	dojo.query("input[name='eventSubmit_doWorkflow_accept']").forEach(function(e){dojo.removeClass(e, 'auiButtonAction')});
    dojo.query("input[name='eventSubmit_doWorkflow_accept']").forEach(function(e){dojo.addClass(e, 'auiButtonDisabled')});
    var form = dojo.byId("workflowForm"+portletId);
    aipo.workflow._portletId = portletId;
    form.mode.value = "accept";
}

aipo.workflow.onDenial = function(portletId){
    dojo.query('.auiButtonAction').forEach(function(e){dojo.removeClass(e, 'auiButtonAction')});
    dojo.query("input[name='eventSubmit_doWorkflow_accept']").forEach(function(e){dojo.addClass(e, 'auiButtonDisabled')});
    var form = dojo.byId("workflowForm"+portletId);
    aipo.workflow._portletId = portletId;
    form.mode.value = "denial";
}

aipo.workflow.onDelete = function(portletId){
    var form = dojo.byId("workflowForm"+portletId);
    aipo.workflow._portletId = portletId;
    form.mode.value = "delete";
}

aipo.workflow.submit_list = function(form) {
  var s_o = form.member_to.options;
  var tmp = '';

  for(i = 0 ; i < s_o.length; i++ ) {
    s_o[i].selected = false;
  }

  if(s_o.length > 0) {
    for(i = 0 ; i < s_o.length-1; i++ ) {
      tmp = tmp + s_o[i].value + ',';
    }
    tmp = tmp + s_o[s_o.length-1].value;
  }
  form.positions.value = tmp;
}

aipo.workflow.formSwitchRouteSelect = function(button) {
    if(button.form.is_saved_route.value == 'TRUE' || button.form.is_saved_route.value == 'true') {
        button.value = aimluck.io.escapeText("workflow_val_route2");
        aipo.workflow.formRouteSelectOff(button.form);
    } else {
        button.value = aimluck.io.escapeText("workflow_val_route1");
        aipo.workflow.formRouteSelectOn(button.form);
    }
}

aipo.workflow.formRouteSelectOn = function(form) {
    dojo.byId('workflowRouteSelectField').style.display = "";
    dojo.byId('workflowRouteInputField').style.display = "none";

    form.is_saved_route.value = 'TRUE';
}

aipo.workflow.formRouteSelectOff = function(form) {
    dojo.byId('workflowRouteSelectField').style.display = "none";
    dojo.byId('workflowRouteInputField').style.display = "";

    form.is_saved_route.value = 'FALSE';
}

aipo.workflow.getJsonDataOne = function(rtnData) {

	var cStartIdx = rtnData["type"].indexOf("\/*");
    var cEndIdx = rtnData["type"].lastIndexOf("*\/");
    var rawData = dojo.eval(rtnData["type"].substring(cStartIdx+2, cEndIdx));

    var jsonData = "";

    if(dojo.isArray(rawData) && rawData.length > 0) {
        jsonData = rawData[0];
    }

	return jsonData;
}

aipo.workflow.onChangeFilter=aipo.workflow.onChangeSearch=function (baseuri,portlet_id){
	var search = encodeURIComponent(dojo.byId("q").value);
	baseuri+="?template=WorkflowListScreen";
	baseuri+="&filter="+dojo.byId("topic").value;
	baseuri+="&filtertype=category";
	baseuri+="&search="+search;
	aipo.viewPage(baseuri,portlet_id);
}
