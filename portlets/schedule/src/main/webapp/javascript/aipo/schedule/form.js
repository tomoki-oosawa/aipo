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
dojo.provide("aipo.schedule");

dojo.require("aipo.widget.ToolTip");
dojo.require("aipo.widget.DropdownDatepicker");
dojo.require("aipo.widget.MemberNormalSelectList");
dojo.require("aipo.widget.GroupNormalSelectList");

aipo.schedule.setupTooltip = function(url, entityids, portlet_id) {
    ptConfig[portlet_id].isTooltipEnable = true;

    var obj_content = dojo.byId('content-'+portlet_id);
    dojo.style(obj_content, "visibility" , "visible");
    obj_indicator = dojo.byId('indicator-'+portlet_id);
    dojo.style(obj_indicator, "display" , "none");

    if (entityids.length <= 0) {
        return;
    }

    if (typeof scheduleTooltipEnable == "undefined") {
    	scheduleTooltipEnable  = true;
    }

    // IPADではツールチップ非表示
    if (scheduleTooltipEnable != true) {
    	return;
    }

    function unique(array) {
        var storage = {};
        var uniqueArray = [];
        var i,value;
        for (i=0; i<array.length; i++) {
            value = array[i];
            if (!(value in storage)) {
                storage[value] = true;
                uniqueArray.push(value);
            }
        }
        return uniqueArray;
    }

    var entity_ids = entityids.split(",");
    entity_ids.pop();
    entity_ids = unique(entity_ids);

    for (var i in entity_ids) {
        entity_ids[i] = dojo.trim(entity_ids[i]);

        var nodeList = new Array();
        dojo.query('div.schedule-' + portlet_id + '-' + entity_ids[i], obj_content).forEach(function(node, index, arr){
            nodeList.push(node);
        });

        var tooltipObject = new aipo.widget.ToolTip({
            label: "<div class='indicator'>"+aimluck.io.escapeText("schedule_val_tooltip1")+"</div>",
            connectId: nodeList
        }, portlet_id, function(containerNode, node){
            var regExp = new RegExp("schedule-" + portlet_id + "-([0-9]+)");
            var className = node.className.match(regExp);
            if (className) {
                var request_url = url + '&scheduleid=' + className[1];
                aipo.schedule.showTooltip(node, request_url, className[1], portlet_id, containerNode);
            }
        });

        for(var j in nodeList) {
        	nodeList[j].setAttribute('widget_id', tooltipObject.id);
        };
    }
}

aipo.schedule.showTooltip = function(obj, url, entityid, portlet_id, containerNode) {
    var tooltipObject;

    var datehtml = "";
    var mbhtml = "";
    var mbfhtml = "";
    var placehtml = "";

    var widget = dijit.byId(obj.getAttribute('widget_id'))
    if (widget.processed) {
        return;
    }

    if(ptConfig[portlet_id].xhrUrl != url){
        ptConfig[portlet_id].xhrUrl = url;
	    dojo.xhrGet({
	        portletId: portlet_id,
	        url: url,
	        encoding: "utf-8",
	        handleAs: "json-comment-filtered",
	        load: function(schedule, event) {
	        	ptConfig[portlet_id].xhrUrl = "";
	            if (!schedule.id) {
	                widget._onHover = function(){};
	                widget.close();
	                widget.processed = true;
	                return;
	            }

	            if (!schedule.isSpan) {
	                datehtml = "<span style=\"font-size: 0.90em;\">" + schedule.date + "</span><br/>";
	            }

	            if (schedule.memberList) {
	                var memberSize = schedule.memberList.length;
	                for (var i = 0 ; i < memberSize ; i++) {
	                    mbhtml += "<li>" + schedule.memberList[i].aliasName.value + "</li>";
	                }
	            }

	            if (schedule.facilityList) {
	                var facilitySize = schedule.facilityList.length;
	                for (var i = 0 ; i < facilitySize ; i++) {
	                    mbfhtml += "<li>" + schedule.facilityList[i].facilityName.value + "</li>";
	                }
	            }

	            if(schedule.place != ""){
	                placehtml = "<span style=\"font-size: 0.90em;\">"+aimluck.io.escapeText("schedule_val_tooltip2")+"</span><br/><ul><li>" + schedule.place + "</li></ul>";
	            }

	            if(mbhtml != ""){
	                mbhtml = "<span style=\"font-size: 0.90em;\">"+aimluck.io.escapeText("schedule_val_tooltip3")+"</span><br/><ul>" + mbhtml + "</ul>";
	            }

	            if(mbfhtml != ""){
	                mbfhtml = "<span style=\"font-size: 0.90em;\">"+aimluck.io.escapeText("schedule_val_tooltip4")+"</span><br/><ul>" + mbfhtml + "</ul>";
	            }

	            var tooltiphtml = "<h4>" + schedule.name + "</h4>" + datehtml + mbhtml + mbfhtml + placehtml;
	            widget.label = tooltiphtml;
	            widget.processed = true;
	            containerNode.innerHTML = tooltiphtml;
	        }
	    });
    }
}


aipo.schedule.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }

    aipo.portletReload('schedule');
};


aipo.schedule.onLoadScheduleDetail = function(portlet_id){
    aipo.portletReload('whatsnew');
}

aipo.schedule.onLoadScheduleDialog = function(portlet_id){

    // 共有カテゴリ連携
    var common_url = dojo.byId('commonUrl'+portlet_id);
    if(common_url){
        var common_category_id = dojo.byId('commonCategoryid'+portlet_id);
        var val1 = aimluck.io.escapeText("schedule_val_category1");
        params = {
            url:common_url.value,
            key:"categoryId",
            value:"categoryName",
            selectedId:common_category_id.value,
            preOptions: { key:'1', value:val1 }
        };
        aimluck.io.createOptions("common_category_id", params);

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

        var fpicker = dijit.byId("facilityselect");
        if(fpicker){
            var select = dojo.byId('init_facilitylist');
            var i;
            var s_o = select.options;
            if (s_o.length == 1 && s_o[0].value == "") return;
            for(i = 0 ; i < s_o.length; i ++ ) {
                fpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
            }
        }

        var obj = dojo.byId("name");
        if(obj){
           obj.focus();
        }

        var btn_ma = dojo.byId("button_member_add");
        if(btn_ma){
           dojo.connect(btn_ma, "onclick", function(){
              aipo.schedule.expandMember();
           });
        }

        var btn_mr = dojo.byId("button_member_remove");
        if(btn_mr){
           dojo.connect(btn_mr, "onclick", function(){
              var select = dojo.byId("member_to");
              if(select.options.length == 0){
                  if((mpicker) && (aipo.schedule.login_aliasname != "undefined")){
                      var alias = aipo.schedule.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                      mpicker.addOptionSync(aipo.schedule.login_name, alias, true);
                  }
              }
              aipo.schedule.expandMember();
           });
        }

        var btn_fa = dojo.byId("button_facility_add");
        if(btn_fa){
           dojo.connect(btn_fa, "onclick", function(){
              aipo.schedule.expandFacility();
           });
        }

        var btn_fr = dojo.byId("button_facility_remove");
        if(btn_fr){
           dojo.connect(btn_fr, "onclick", function(){
              aipo.schedule.expandFacility();
           });
        }

        var form = dojo.byId('_scheduleForm');
        if(form){
          form.ignore_duplicate_facility.value = "false";
        }

        aipo.schedule.shrinkMember();
        aipo.schedule.shrinkFacility();

        var spanStart = dijit.byId('startDateSpan');
        var spanEnd = dijit.byId('endDateSpan');
        if (spanStart != null && spanEnd != null) {
            var sDate = spanStart.dropDown.value;
            var eDate = spanEnd.dropDown.value;
            aipo.schedule.spanLength = (eDate - sDate)/86400000;
        } else {
            aipo.schedule.spanLength = 0;
        }
    }

    //参加ユーザー設定のtoggleイベント
    var setToggleClickEvent=function (id_head,displayedstyle){
    	if(dojo.byId(id_head+"_title_"+portlet_id)!=null){
    	 dojo.connect(dojo.byId(id_head+"_title_"+portlet_id), "onclick", function(){
    	    	var defaultstyle=displayedstyle;//変更
    	    	var f=function(){
    	    		var item=dojo.byId(id_head+"_context_"+portlet_id);
    	    		item.style.display=(item.style.display!="none")?"none":defaultstyle;
    	    	}
    	    	f();
    	    	aipo.schedule.setWrapperHeight();
    	     });
    	}
    };
    setToggleClickEvent("edit_control","block");
    setToggleClickEvent("change_tmpreserve","block");
    setToggleClickEvent("mail","block");
}

aipo.schedule.formPreSubmit = function (form) {
    var member_to = dojo.byId('member_to');
    var facility_to = dojo.byId('facility_to');
    if(member_to) {
        var t_o = member_to.options;
        for(i = 0 ; i < t_o.length; i++ ) {
            t_o[i].selected = true;
        }
    }
    if(facility_to) {
        var f_o = facility_to.options;
        for(i = 0 ; i < f_o.length; i++ ) {
            f_o[i].selected = form.public_flag[0].checked;
        }
    }
    if(form.is_span.value == 'TRUE' || form.is_span.value == 'true') {
        form.start_date_hour.value = 0;
        form.start_date_minute.value = 0;
        form.end_date_hour.value = 0;
        form.end_date_minute.value = 0;
    } else {
        form.end_date_year.value = form.start_date_year.value;
        form.end_date_month.value = form.start_date_month.value;
        form.end_date_day.value = form.start_date_day.value;
    }
}

aipo.schedule.formSwitchRepeat = function(button) {
    if(button.form.is_repeat.value == 'TRUE' || button.form.is_repeat.value == 'true') {
    	var val = aimluck.io.escapeText("schedule_val_repeat1");
        button.value = val;
        aipo.schedule.formRepeatOff(button.form);
    } else {
    	var val = aimluck.io.escapeText("schedule_val_repeat2");
        button.value = val;
        aipo.schedule.formRepeatOn(button.form);
    }
}


aipo.schedule.isShowFacility = function(scheduleform) {
    var public_flag = scheduleform.public_flag;
    for(var i = 0 ; i < public_flag.length; i++) {
        if (public_flag[i].checked && (public_flag[i].value == 'O' || public_flag[i].value == "C")) {
            return true;
        }
    }
    return false;
}

aipo.schedule.formSwitchAllDay = function(checkbox) {
    if(checkbox.checked) {
        aipo.schedule.formAllDayOn(checkbox);
    } else {
        aipo.schedule.formAllDayOff(checkbox);
    }
}

aipo.schedule.formSwitchSpan = function(button) {
    if(button.form.is_span.value == 'TRUE' || button.form.is_span.value == 'true') {
        button.value = aimluck.io.escapeText("schedule_val_span1");
        if(button.form.is_repeat.value != 'TRUE' && button.form.is_repeat.value != 'true') {
            button.form.repeat_button.value = aimluck.io.escapeText("schedule_val_repeat1");
            aipo.schedule.formRepeatOff(button.form);
        } else {
            button.form.repeat_button.value = aimluck.io.escapeText("schedule_val_repeat2");
            aipo.schedule.formRepeatOn(button.form);
        }
        aipo.schedule.formSpanOff(button.form);
    } else {
        button.value = aimluck.io.escapeText("schedule_val_span2");
        aipo.schedule.formSpanOn(button.form);
    }
}

aipo.schedule.formSpanOn = function(form) {
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('timeField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "none";
    dojo.byId('normalField').style.display = "";
    dojo.byId('spanField').style.display = "";
    dojo.byId('allDayField').style.display = "none";

    dojo.byId('facilityField').style.display = "none";
    dojo.byId('facilityFieldButton').style.display = "none";

    form.is_span.value = 'TRUE';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formSpanOff = function(form) {
    dojo.byId('spanField').style.display = "none";
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "";
    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";
    dojo.byId('allDayField').style.display = "";

    if (aipo.schedule.isShowFacility(form)) {
        dojo.byId('facilityFieldButton').style.display = "block";
        aipo.schedule.shrinkFacility();
    }

    form.is_repeat.value = 'FALSE';
    form.is_span.value = 'FALSE';

    aipo.schedule.setWrapperHeight();
}


aipo.schedule.formRepeatOff = function(form) {
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('spanField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "";

    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";

    dojo.byId('spanButtonField').style.display = "";

    form.is_repeat.value = 'FALSE';
    form.is_span.value = 'FALSE';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formEditRepeatOne = function(form) {
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('spanField').style.display = "none";
    dojo.byId('spanButtonField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "none";
    dojo.byId('allDayField').style.display = "none";

    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";

    form.is_repeat.value = 'FALSE';
    form.is_span.value = 'FALSE';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formEditRepeatAll = function(form) {
    dojo.byId('normalField').style.display = "none";
    dojo.byId('spanField').style.display = "none";
    dojo.byId('spanButtonField').style.display = "none";
    dojo.byId('repeatField').style.display = "";
    dojo.byId('repeatField').text = dojo.byId('schedule_val_repeat2').innerText;
    dojo.byId('repeatButtonField').style.display = "";
    dojo.byId('allDayField').style.display = "none";

    dojo.byId('timeLabelField').style.display = "";
    dojo.byId('timeField').style.display = "";

    form.is_repeat.value = 'TRUE';
    form.is_span.value = 'FALSE';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formRepeatOn = function(form) {
    dojo.byId('normalField').style.display = "none";
    dojo.byId('spanField').style.display = "none";

    dojo.byId('spanButtonField').style.display = "none";
    dojo.byId('repeatField').style.display = "";
    dojo.byId('repeatButtonField').style.display = "";

    dojo.byId('timeLabelField').style.display = "";
    dojo.byId('timeField').style.display = "";

    form.is_repeat.value = 'TRUE';
    form.is_span.value = 'FALSE';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formAllDayOn = function(checkbox) {
    dojo.byId('spanField').style.display = "none";
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "none";
    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "none";
    dojo.byId('spanButtonField').style.display = "none";

    dojo.byId('facilityFieldButton').style.display = "none";
    aipo.schedule.shrinkFacility();

    checkbox.form.is_repeat.value = 'FALSE';
    checkbox.form.is_span.value = 'TRUE';
    checkbox.form.all_day_flag.value = 'ON';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formAllDayOff = function(checkbox) {
    dojo.byId('spanField').style.display = "none";
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "";
    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";
    dojo.byId('spanButtonField').style.display = "";

    if (aipo.schedule.isShowFacility(checkbox.form)) {
        dojo.byId('facilityFieldButton').style.display = "block";
    }

    checkbox.form.is_repeat.value = 'FALSE';
    checkbox.form.is_span.value = 'FALSE';
    checkbox.form.all_day_flag.value = 'OFF';

    aipo.schedule.setWrapperHeight();
}

aipo.schedule.formPublicOn = function(form) {
    if(form.is_span.value != 'TRUE' && form.is_span.value != 'true'){
        form.is_facility.value = "TRUE";
    }
    dojo.byId('facilityFieldButton').style.display = "block";
    aipo.schedule.shrinkFacility();
}

aipo.schedule.formPublicOff = function(form) {
    if(form.is_span.value != 'TRUE' && form.is_span.value != 'true'){
        form.is_facility.value = "FALSE";
    }
    dojo.byId('facilityField').style.display = "none";
    dojo.byId('facilityFieldButton').style.display = "none";
    aipo.schedule.setWrapperHeight();
}

aipo.schedule.enablePerWeek = function(form){
    form.repeat_type[1].checked = true;
}

aipo.schedule.enableMonth = function(form){
    if(! form.repeat_type[2].checked){
        form.repeat_type[2].checked = true;
    }
}

aipo.schedule.buttonEdit = function(form, editurl) {
    aimluck.io.disableForm(form, true);
    aipo.common.showDialog(editurl);
}

aipo.schedule.buttonChangeStatus = function(form, changeurl, status, indicator_id, portlet_id) {
    form.action = changeurl  + "&status=" + status;
    aimluck.io.submit(form, indicator_id, portlet_id, aipo.schedule.onReceiveMessage);
}

aipo.schedule.delFlag0 = function(form) {
    form.del_member_flag.value = "0";
    form.del_range_flag.value = "0";
}

aipo.schedule.delFlag1 = function(form) {
    form.del_member_flag.value = "0";
    form.del_range_flag.value = "1";
}

aipo.schedule.delFlag2 = function(form) {
    form.del_member_flag.value = "1";
    form.del_range_flag.value = "0";
}

aipo.schedule.delFlag3 = function(form) {
    form.del_member_flag.value = "1";
    form.del_range_flag.value = "1";
}

aipo.schedule.changeEnd = function(form) {
  if(form.end_date_hour.value == 24) {
    form.end_date_minute.value = 0;
  }
}

aipo.schedule.onSubmit = function(form) {
    if((form.is_span.value != "TRUE") && (form.is_span.value != "true")
      && (form.is_repeat.value != "TRUE") && (form.is_repeat.value != "true")){
        form.end_date.value = form.start_date.value;
        form.end_date_day.value = form.start_date_day.value;
        form.end_date_month.value = form.start_date_month.value;
        form.end_date_year.value = form.start_date_year.value;
        form.limit_end_date.value = form.limit_start_date.value;
        form.limit_end_date_day.value = form.limit_start_date_day.value;
        form.limit_end_date_month.value = form.limit_start_date_month.value;
        form.limit_end_date_year.value = form.limit_start_date_year.value;
    }
}

aipo.schedule.onReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }

        aipo.portletReload('schedule');
        aipo.portletReload('timeline');
    }

    if(msg != null && msg.match(/duplicate_facility/)){

        if(confirm(aimluck.io.escapeText("schedule_val_confirm1"))) {
		    var form = dojo.byId('_scheduleForm');
		    if(form){
		      form.ignore_duplicate_facility.value = "true";

		       dojo.xhrPost({
		            url: form.action,
		            timeout: 30000,
		            form: form,
		            encoding: "utf-8",
		            handleAs: "json-comment-filtered",
		            headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
		            load: function (response, ioArgs){
		  		      aipo.schedule.onReceiveMessage("");
		            },
		            error: function (error) {
		            }
		        });

		    }
		  }
    }else if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }

	if(msg != '') {
		aipo.schedule.setWrapperHeight();
	}
}

aipo.schedule.shrinkMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var m_t = dojo.byId("member_to");
        if(m_t){
            var t_o = m_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
              HTML += "<span>" + text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            }
        }
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("schedule_val_member1")+'\" onclick=\"aipo.schedule.expandMember();\" />'
        HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
   aipo.schedule.setWrapperHeight();
}

aipo.schedule.expandMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none\">";
       var m_t = dojo.byId("member_to");
       if(m_t){
            var t_o = m_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
              HTML += "<span>" +  text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            }
       }
       HTML += "</td><td style=\"border:none;\">";
       HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("schedule_val_member2")+'\" onclick=\"aipo.schedule.shrinkMember();\" />'
       HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
   aipo.schedule.setWrapperHeight();
}

aipo.schedule.shrinkFacility = function(){
   var node = dojo.byId("facilityFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var f_t = dojo.byId("facility_to");
        if(f_t){
            var t_o = f_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              HTML += "<span>" +  aipo.escapeHTML(t_o[i].text) + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            }
        }
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("schedule_val_facility1")+'\" onclick=\"aipo.schedule.expandFacility();\" />'
        HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("facilityField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
   aipo.schedule.setWrapperHeight();
}

aipo.schedule.expandFacility = function(){
   var node = dojo.byId("facilityFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none\">";
       var f_t = dojo.byId("facility_to");
       if(f_t){
            var t_o = f_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
              HTML += "<span>" +  text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            }
       }
       HTML += "</td><td style=\"border:none;\">";
       HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("schedule_val_member2")+'\" onclick=\"aipo.schedule.shrinkFacility();\" />'
       HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("facilityField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
   aipo.schedule.setWrapperHeight();
}

aipo.schedule.onSpanStartChange = function(){
    var spanStart = dijit.byId('startDateSpan');
    var spanEnd = dijit.byId('endDateSpan');
    if (spanStart != null && spanEnd != null) {
        var newDateMillis = spanStart.dropDown.value.getTime() + 86400000 * aipo.schedule.spanLength;
        var newDate = new Date();
        newDate.setTime(newDateMillis);
        spanEnd.dropDown.onChangeNoCallback(newDate);
        spanEnd.dropDown.setValue(newDate);
    }
}

aipo.schedule.onSpanEndChange = function(){
    var spanStart = dijit.byId('startDateSpan');
    var spanEnd = dijit.byId('endDateSpan');
    if (spanStart != null && spanEnd != null && spanStart.dropDown != null && spanEnd.dropDown != null) {
        var spanStartDate = spanStart.dropDown.value;
        var spanEndDate = spanEnd.dropDown.value;
        if(spanStartDate >= spanEndDate) {
            aipo.schedule.spanLength = 0;
            spanStart.dropDown.onChangeNoCallback(spanEndDate);
            spanStart.dropDown.setValue(spanEndDate);
        } else {
            aipo.schedule.spanLength = (spanEndDate - spanStartDate) / 86400000;
        }
    }
}

aipo.schedule.setIndicator = function(portlet_id) {

    obj_content = dojo.byId('content-'+portlet_id);
    dojo.style(obj_content, "visibility" , "hidden");
    var obj_garage = dojo.byId('scheduleGarage-'+portlet_id);
    if(obj_garage){
	    var child_num = obj_garage.childNodes.length;
	    for(var i=0;i<child_num;i++){
		    var obj_schedule = dojo.byId('schedule-'+i+'-'+portlet_id);
		    if(obj_schedule){
		    	dojo.style(obj_schedule, "visibility" , "hidden");
		    }
		}
	}
    obj_indicator = dojo.byId('indicator-'+portlet_id);
    dojo.style(obj_indicator, "display" , "");
}

//tdタグクリック時にaタグと重複していないか判定を行います。onclickイベント専用
aipo.schedule.showScheduleAddDialog=function(td,event,url,portlet_id,callback){
    if (!event) {event = window.event; }
    var pos={x:event.clientX,y:event.clientY};

	var isCollapsed=false;

	dojo.query("a",td).forEach(function(item){
		if(!isCollapsed){
			var rect=item.getBoundingClientRect();
			isCollapsed=(rect.left<=pos.x && pos.x<=rect.right && rect.top<=pos.y && pos.y<=rect.bottom);
		}
	});
	if(isCollapsed){
		return true;//aタグの追加処理
	}else{
		aipo.common.showDialog(url,portlet_id,callback);
		return false;
	}
}

aipo.schedule.setWrapperHeight = function() {
	var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}
