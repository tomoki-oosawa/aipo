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

dojo.provide("aipo.report");

dojo.require("aipo.widget.MemberNormalSelectList");
dojo.require("dijit.form.ComboBox");
dojo.require("aipo.widget.DropdownDatepicker");

aipo.report.onLoadReportDetail = function(portlet_id){
    aipo.portletReload('report');
    aipo.portletReload('whatsnew');
}

aipo.report.onLoadReportDialog = function(portlet_id){

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

    var mpicker = dijit.byId("mapnormalselect");
	if(mpicker){
	    var select = dojo.byId('init_maplist');
	    var i;
	    var s_o = select.options;
	    if (s_o.length == 1 && s_o[0].value == "") return;
	    for(i = 0 ; i < s_o.length; i ++ ) {
	        mpicker.addOptionSync(s_o[i].value,s_o[i].text,true);
	    }
    }

    var btn_ma = dojo.byId("button_member_add");
    if(btn_ma){
       dojo.connect(btn_ma, "onclick", function(){
          aipo.report.expandMember();
       });
    }

    var btn_ma = dojo.byId("button_map_add");
    if(btn_ma){
       dojo.connect(btn_ma, "onclick", function(){
          aipo.report.expandMap();
       });
    }

    var btn_mr = dojo.byId("button_member_remove");
    if(btn_mr){
       dojo.connect(btn_mr, "onclick", function(){
          var select = dojo.byId("members");
          if(select.options.length == 0){
              if((mpicker) && (aipo.report.login_aliasname != "undefined")){
                  var alias = aipo.report.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                  mpicker.addOptionSync(aipo.report.login_name, alias, true);
              }
          }
          aipo.report.expandMember();
       });
    }

    var btn_mr = dojo.byId("button_map_remove");
    if(btn_mr){
       dojo.connect(btn_mr, "onclick", function(){
          var select = dojo.byId("positions");
          if(select.options.length == 0){
              if((mpicker) && (aipo.report.login_aliasname != "undefined")){
                  var alias = aipo.report.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
                  mpicker.addOptionSync(aipo.report.login_name, alias, true);
              }
          }
          aipo.report.expandMap();
       });
    }


    aipo.report.shrinkMember();
    aipo.report.expandMap();
}

aipo.report.onReceiveMessage = function(msg){
    //送信時に作成した場合selectを削除。
	var select=dojo.byId("attachments_select");
	if(typeof select!="undefined"&& select!=null)
		select.parentNode.removeChild(select);

    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }

        aipo.portletReload('report');
        aipo.portletReload('whatsnew');
        aipo.portletReload('timeline');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }

    if(msg != '') {
    	aipo.report.setWrapperHeight();
    }
}


aipo.report.shrinkMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var m_t = dojo.byId("members");
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
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("report_val_member1")+'\" onclick=\"aipo.report.expandMember();\" />'
        HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
   aipo.report.setWrapperHeight();
}


aipo.report.shrinkMap = function(){
   var node = dojo.byId("mapFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none;\">";
       var m_t = dojo.byId("positions");
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
        HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("report_val_member2")+'\" onclick=\"aipo.report.expandMap();\" />'
        HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("mapField");
   if(_node){
       dojo.style(_node, "display" , "none")
   }
   aipo.report.setWrapperHeight();
}

aipo.report.expandMember = function(){
   var node = dojo.byId("memberFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none\">";
       var m_t = dojo.byId("members");
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
       HTML += '<input type=\"button\" class=\"alignright\" value=\"'+aimluck.io.escapeText("report_val_member3")+'\" onclick=\"aipo.report.shrinkMember();\" />'
       HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("memberField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
   aipo.report.setWrapperHeight();
}

aipo.report.expandMap = function(){
   var node = dojo.byId("mapFieldButton");
   if(node){
       var HTML = "";
       HTML += "<table style=\"width:98%;\"><tbody><tr><td style=\"width:80%; border:none\">";
       var m_t = dojo.byId("positions");
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
       HTML += "</td></tr></tbody></table>";
       node.innerHTML = HTML;
   }

   var _node = dojo.byId("mapField");
   if(_node){
       dojo.style(_node, "display" , "block");
   }
   aipo.report.setWrapperHeight();
}

aipo.report.formatNum = function(num) {
  var src = new String(num);
  var cnt = 2 - src.length;
    if (cnt <= 0) return src;
    while (cnt-- > 0) src = "0" + src; return src;
}
aipo.report.delaySelectAllOptions = function(form, func)
{
    return function(form){aimluck.io.selectAllOptions(form.attachments)};
}


aipo.report.setWrapperHeight = function() {
	var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}