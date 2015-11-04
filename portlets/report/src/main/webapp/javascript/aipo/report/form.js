/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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

aipo.report.toggleMenu=function (node,filters,event){
	var rect=filters.getBoundingClientRect();
	var html=document.documentElement.getBoundingClientRect();
	if (node.style.display == "none") {
        dojo.query("div.menubar").style("display", "none");

        var scroll={
        	left:document.documentElement.scrollLeft||document.body.scrollLeft,
        	top:document.documentElement.scrollTop||document.body.scrollTop
        };
        node.style.opacity="0";
        setTimeout( function(){
			dojo.style(node, "display" , "block");
		}, 0);
        if(html.right-node.clientWidth>rect.left){
       		node.style.left=rect.left+scroll.left+"px";
        }else{
        	node.style.left=rect.right-node.clientWidth+scroll.left+"px";
        }
         if(html.bottom-node.clientHeight>rect.bottom||event){
       		node.style.top=rect.bottom+scroll.top+"px";
        }else{
        	node.style.top=rect.top-node.clientHeight+scroll.top+"px";
        }
        node.style.opacity="";
    } else {
        dojo.query("div.menubar").style("display", "none");
    }
};

/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.report.initFilterSearch = function(portlet_id) {
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
aipo.report.finFilterSearch = function(portlet_id) {
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

/**
 * urlを整形して送信。
 */
aipo.report.filteredSearch=function(portlet_id){
	//filtertype

	var baseuri=dojo.byId("baseuri_"+portlet_id).value;

	var types=[];
	var params=[];
	dojo.query("ul.filtertype_"+portlet_id).forEach(function(ul){
			//console.info(ul);
			var type=ul.getAttribute("data-type");
			types.push(type);

			var activeli=dojo.query("li.selected",ul)[0];
			if(activeli){
				var param=activeli.getAttribute("data-param");
				params.push(param);
			}else{
				params.push(ul.getAttribute("data-defaultparam"));
			}
		}
	);
	var q=dojo.byId("q"+portlet_id);
	var qs=[["filter",params.join(",")],
	        ["filtertype",types.join(",")],
		["keyword",q?q.value:""]
	];
	aipo.viewPage(baseuri,portlet_id,qs);
};


/**
 * 指定したフィルタにデフォルト値を設定する。(または消す)
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.report.filterSetDefault=function(portlet_id,type){
	var ul=dojo.query("ul.filtertype[data-type="+type+"]")[0];
	var defval=ul.getAttribute("data-defaultparam");
	var defaultli=dojo.query("li[data-param="+defval+"]",ul);
	aipo.report.filterSelect(ul,defaultli);
	aipo.report.filteredSearch(portlet_id);
};

aipo.report.filterSelect=function(ul,li){
	dojo.query("li",ul).removeClass("selected");
	dojo.query(li).addClass("selected");
};

aipo.report.onLoadReportDetail = function(portlet_id){
    aipo.portletReload('report');
    aipo.portletReload('whatsnew');
}

/**
 * フィルタを選択した時に発生させるイベント　クリックされたノードをフィルタに追加
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.report.filterClick=function(portlet_id,thisnode,event){
	var li=thisnode.parentNode;
	var ul=li.parentNode;
	var param=li.getAttribute("data-param");//liのdata-param
	aipo.report.filterSelect(ul,li);
	aipo.report.filteredSearch(portlet_id);
};

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
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none;\">";
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
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none;\">";
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
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none\">";
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
       HTML += "<table class=\"w100\"><tbody><tr><td style=\"width:80%; border:none\">";
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

aipo.report.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
       arrDialog.hide();
    }
    aipo.portletReload('report');
}