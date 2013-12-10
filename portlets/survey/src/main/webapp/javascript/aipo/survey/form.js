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

dojo.provide("aipo.survey");

dojo.require("aipo.widget.MemberNormalSelectList");
dojo.require("dijit.form.ComboBox");
dojo.require("aipo.widget.DropdownDatepicker");

aipo.survey.optionMin = 3;
aipo.survey.optionMax = 10;

aipo.survey.onLoadSurveyDetail = function(portlet_id){
    aipo.portletReload('survey');
};

aipo.survey.onLoadSurveyDialog = function(portlet_id){
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
	
	var currentNum = dojo.query('#surveyFormOptions-' + portlet_id + ' .surveyFormOption').length;
	if(aipo.survey.optionMin > currentNum){
		for( var i = currentNum ;  i < aipo.survey.optionMin; i++) {
			aipo.survey.addOptionField(portlet_id);
		}		
	}
};

aipo.survey.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('survey');
        aipo.portletReload('timeline');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }

    if(msg != '') {
    	aipo.survey.setWrapperHeight();
    }
};

aipo.survey.setWrapperHeight = function() {
	var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
};

aipo.survey.allocateId = function(portlet_id){
	function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
                 .toString(16)
                 .substring(1);
    };
	return portlet_id + '-' + s4() + s4() + '-' + s4() + '-' + s4() + '-' +
			s4() + '-' + s4() + s4() + s4();
};

aipo.survey.addOptionField = function(portlet_id, value){
	var currentNum = dojo.query('#surveyFormOptions-' + portlet_id + ' .surveyFormOption').length;
	if(aipo.survey.optionMax > currentNum){
		var container = dojo.byId('surveyFormOptions-' + portlet_id);
		if(container) {
			if(!value){
				value = '';
			}
			var oid = 'surveyFormOption-' + aipo.survey.allocateId(portlet_id);
			container.innerHTML += 
				'<div class="mb5 surveyFormOption" id="' + oid + '" >'
				+ '<input type="text" name="options[]" value="' + value + '" class="w90 mr10">'
				+ '<a href="javascript:void(0)" onclick="aipo.survey.removeOptionField(\'' + portlet_id + '\', \'' + oid + '\');return false;" title="項目を削除"><span><i class="icon-remove"></i></span></a>'
				+ '</div>';
			aipo.survey.onChangeOptionField(portlet_id);
		}
	}
};

aipo.survey.removeOptionField = function(portlet_id, oid){
	if(dojo.byId(oid)){
		dojo.byId(oid).remove();
		aipo.survey.onChangeOptionField(portlet_id);
	}
};

aipo.survey.onChangeOptionField = function(portlet_id){
	var currentNum = dojo.query('#surveyFormOptions-' + portlet_id + ' .surveyFormOption').length;
	var link = dojo.byId('surveyLinkOptions-' + portlet_id);
	if(currentNum >= aipo.survey.optionMax) {		
		dojo.style(link, 'display', 'none');
	} else {
		dojo.style(link, 'display', '');
	}
	
	if(currentNum <= 1) {	
		dojo.query('#surveyFormOptions-' + portlet_id + ' .surveyFormOption a').style('display', 'none');
	} else {
		dojo.query('#surveyFormOptions-' + portlet_id + ' .surveyFormOption a').style('display', '');
	}
	aipo.survey.setWrapperHeight();
};

aipo.survey.toggleRespondentType = function(option, portlet_id) {
	if(option.value == 'A') {
		dojo.query('.respondentForm-' + portlet_id).style('display', 'none');
	} else {
		dojo.query('.respondentForm-' + portlet_id).style('display', '');
	}
	aipo.survey.setWrapperHeight();
};

aipo.survey.toggleCheck = function(check, targetId){
	if(dojo.byId(targetId)) {
		if(check.checked) {
			dojo.byId(targetId).value = 'T';
		} else {
			dojo.byId(targetId).value = 'F';
		}
	}	
};