/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

dojo.provide("aipo.schedule");

dojo.require("aipo.widget.DropdownDatepicker");
dojo.require("aipo.widget.MemberNormalSelectList");
dojo.require("aipo.widget.GroupNormalSelectList");

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
        params = {
            url:common_url.value,
            key:"categoryId",
            value:"categoryName",
            selectedId:common_category_id.value,
            preOptions: { key:'1', value:'（未分類）' }
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
                      mpicker.addOptionSync(aipo.schedule.login_name, aipo.schedule.login_aliasname, true);
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
    }
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
        button.value = '繰り返す';
        aipo.schedule.formRepeatOff(button.form);
    } else {
        button.value = '繰り返さない';
        aipo.schedule.formRepeatOn(button.form);
    }
}

aipo.schedule.formSwitchSpan = function(button) {
    if(button.form.is_span.value == 'TRUE' || button.form.is_span.value == 'true') {
        button.value = '期間で指定する';
        if(button.form.is_repeat.value != 'TRUE' && button.form.is_repeat.value != 'true') {
            button.form.repeat_button.value = '繰り返す';
            aipo.schedule.formRepeatOff(button.form);
        } else {
            button.form.repeat_button.value = '繰り返さない';
            aipo.schedule.formRepeatOn(button.form);
        }
        aipo.schedule.formSpanOff(button.form);
    } else {
        button.value = '時間で指定する';
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
    
    dojo.byId('facilityField').style.display = "none";
    dojo.byId('facilityFieldButton').style.display = "none";
    
    form.is_span.value = 'TRUE';
}

aipo.schedule.formSpanOff = function(form) {
    dojo.byId('spanField').style.display = "none";
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "";
    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";
    
    dojo.byId('facilityFieldButton').style.display = "block";
    aipo.schedule.shrinkFacility();
    
    form.is_repeat.value = 'FALSE';
    form.is_span.value = 'FALSE';
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
}

aipo.schedule.formEditRepeatOne = function(form) {
    dojo.byId('repeatField').style.display = "none";
    dojo.byId('timeLabelField').style.display = "none";
    dojo.byId('spanField').style.display = "none";
    dojo.byId('spanButtonField').style.display = "none";
    dojo.byId('repeatButtonField').style.display = "none";

    dojo.byId('normalField').style.display = "";
    dojo.byId('timeField').style.display = "";

    form.is_repeat.value = 'FALSE';
    form.is_span.value = 'FALSE';
}

aipo.schedule.formEditRepeatAll = function(form) {
    dojo.byId('normalField').style.display = "none";
    dojo.byId('spanField').style.display = "none";
    dojo.byId('spanButtonField').style.display = "none";
    dojo.byId('repeatField').style.display = "";
    dojo.byId('repeatField').text = '繰り返さない';
    dojo.byId('repeatButtonField').style.display = "";
  
    dojo.byId('timeLabelField').style.display = "";
    dojo.byId('timeField').style.display = "";

    form.is_repeat.value = 'TRUE';
    form.is_span.value = 'FALSE';
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
    } 
    
    if(msg != null && msg.match(/duplicate_facility/)){
    
        if(confirm('既に同じ時間帯に施設が予約されています。スケジュールを登録しますか？')) {
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
		            },
		            error: function (error) {
		            }
		        });
		        	        
		      aipo.schedule.onReceiveMessage("");
		    }
		  } 
    }else if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.schedule.shrinkMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var m_t = dojo.byId("member_to");
        if(m_t){
            var t_o = m_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              HTML += "<span>" +  t_o[i].text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            } 
        }  
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"参加メンバー選択\" onclick=\"aipo.schedule.expandMember();\" />'
        HTML += "</td></tr></tbody></table>";     
       node.innerHTML = HTML;
   }
   
   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
}

aipo.schedule.expandMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none\">";
       var m_t = dojo.byId("member_to");
       if(m_t){
            var t_o = m_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              HTML += "<span>" +  t_o[i].text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            } 
       }
       HTML += "</td><td style=\"border:none;\">";
       HTML += '<input type=\"button\" class=\"alignright\" value=\"選択画面を隠す\" onclick=\"aipo.schedule.shrinkMember();\" />'
       HTML += "</td></tr></tbody></table>";     
       node.innerHTML = HTML;
   }
   
   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
}

aipo.schedule.shrinkFacility = function(){
   var node = dojo.byId("facilityFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var f_t = dojo.byId("facility_to");
        if(f_t){
            var t_o = f_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              HTML += "<span>" +  t_o[i].text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            } 
        }  
        HTML += "</td><td style=\"border:none;\">";
        HTML += '<input type=\"button\" class=\"alignright\" value=\"施設予約\" onclick=\"aipo.schedule.expandFacility();\" />'
        HTML += "</td></tr></tbody></table>";     
       node.innerHTML = HTML;
   }
   
   var _node = dojo.byId("facilityField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
}

aipo.schedule.expandFacility = function(){
   var node = dojo.byId("facilityFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none\">";
       var f_t = dojo.byId("facility_to");
       if(f_t){
            var t_o = f_t.options;
            to_size = t_o.length;
            for(i = 0 ; i < to_size; i++ ) {
              HTML += "<span>" +  t_o[i].text + "</span>";
              if(i < to_size - 1){
                  HTML += ",<wbr/>";
              }
            } 
       }
       HTML += "</td><td style=\"border:none;\">";
       HTML += '<input type=\"button\" class=\"alignright\" value=\"選択画面を隠す\" onclick=\"aipo.schedule.shrinkFacility();\" />'
       HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }
   
   var _node = dojo.byId("facilityField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
}
