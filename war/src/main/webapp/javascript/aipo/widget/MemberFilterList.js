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
if(!dojo._hasResource["aipo.widget.MemberFilterList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.MemberFilterList"] = true;

dojo.provide("aipo.widget.MemberFilterList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

dojo.declare("aipo.widget.MemberFilterList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "name",
    memberFromOptionValue: "aliasName",
    memberFromOptionUserId: "userId",
    memberFromOptionImageFlag: "hasPhoto",
    memberFromOptionImageVersionParam: "photoModified",
    memberFromOptionDefaultImage: "themes/default/images/common/icon_user100.png",
    memberToId: "",
    authorityFromId: "",
    memberAuthorityToId: "",
    clickEvent: "",
    viewSelectId: "",
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    childTemplateString: "",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\">" +
    	"<div class=\"auiSummaryMeta auiFilter clearfix\">" +
    	"<div class=\"filters floatLeft\" id=\"filters_${widgetId}\" >" +
    	"<a href=\"javascript:void(0)\" class=\"customizeMenuIcon filterTip menubarOpenButton\" onclick=\"aipo.widget.MemberFilterList.toggleMenu(dojo.byId('menubar_auiFilter_${widgetId}'),dojo.byId('filters_${widgetId}'),true,dojo.byId('${widgetId}'));\"><span id=\"tlDisplayGroup_${widgetId}\" data-param=\"${groupSelectPreOptionKey}\">${groupSelectPreOptionValue}</span></a>" +
    	"<a href=\"javascript:void(0)\" class=\"customizeMenuIcon filterTip menubarOpenButton\" onclick=\"aipo.widget.MemberFilterList.toggleMenu(dojo.byId('menubar_auiFilter_${widgetId}'),dojo.byId('filters_${widgetId}'),true,dojo.byId('${widgetId}'));\" style=\"display:none;\"><span id=\"tlDisplayView_${widgetId}\" data-param=\"0\">未選択</span></a>" +
    	"</div>" +
    	"<div class=\"auiSummarySearch floatRight\"><div class=\"auiSearch gray\" name=\"timelineSearchForm\"><input type=\"text\" value=\"\" name=\"${widgetId}-keyword\" id=\"${widgetId}-keyword\" placeholder=\"ユーザー検索\" onkeydown=\"return aipo.widget.MemberFilterList.filteredSearchCheck(arguments[0], '${widgetId}')\"><button type=\"button\" dojoAttachEvent=\"onclick:filteredSearch\"><i class=\"icon-search\"></i></button></div></div>" +
    	"</div>" +
    	"<div class=\"menubar multi\" id=\"menubar_auiFilter_${widgetId}\" style=\"width:260px;display:none; \">" +
    	"<div><p class=\"caption\">グループ</p><ul class=\"filtertype filtertype_${widgetId}\" id=\"${groupSelectId}\"></ul></div>" +
    	"<div><p class=\"caption\">表示</p><ul class=\"filtertype filtertype_${widgetId}\" id=\"${viewSelectId}\">" +
    	"<li class=\"selected\"><a href=\"javascript:void(0);\" class=\"selected\" onclick=\"aipo.widget.MemberFilterList.filterCheckedDisplay('${widgetId}',this,'${memberFromId}')\" data-param=\"0\">すべて</a></li>" +
    	"<li><a href=\"javascript:void(0);\" onclick=\"aipo.widget.MemberFilterList.filterCheckedDisplay('${widgetId}',this,'${memberFromId}')\" data-param=\"1\">選択済み</a></li>" +
    	"<li><a href=\"javascript:void(0);\" onclick=\"aipo.widget.MemberFilterList.filterCheckedDisplay('${widgetId}',this,'${memberFromId}')\" data-param=\"2\">未選択</a></li>" +
    	"</ul></div></div>" +
    	"<div class=\"memberPopupDiv_ver3\">" +
    	"<div class=\"head\" style=\"display:none;\"><input type=\"checkbox\" id=\"tmp_head_checkbox_${widgetId}\" disabled=\"disabled\"></div>" +
    	"<div style=\"display: none;\" id=\"${widgetId}-memberlist-indicator\" class=\"indicator alignleft\">読み込み中</div>" +
    	"<ul class=\"memberPopupList\" id=\"${memberFromId}\"></ul>" +
    	"<select multiple=\"multiple\" style=\"display:none\" name=\"${memberToId}\" id=\"${memberToId}\"></select>" +
    	"<select multiple=\"multiple\" style=\"display:none\" name=\"${memberAuthorityToId}\" id=\"${memberAuthorityToId}\"></select>" +
    	"</div>" +
    	"</div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue,
          clickEvent: this.clickEvent,
          selectedId: this.memberToId,
          selectedAuthId: this.memberAuthorityToId,
          name: this.memberFromId,
          userId: this.memberFromOptionUserId,
          image_flag: this.memberFromOptionImageFlag,
          image_version: this.memberFromOptionImageVersionParam,
          default_image: this.memberFromOptionDefaultImage,
          child_html: this.childTemplateString,
          widgetId: this.widgetId,
          keyword: this.widgetId + "-keyword",
          indicator: this.widgetId + "-memberlist-indicator",
          callback: this.fixScroll
        };
        aimluck.io.createMemberLists(this.memberFromId, params);
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue },
          selectedId: this.groupSelectPreOptionKey,
          widgetId: this.widgetId,
          child_html: this.childTemplateString,
        };
        aimluck.io.createGroupLists(this.groupSelectId, params);
        aipo.widget.MemberFilterList.filterSelectDisplay(this.widgetId, this.groupSelectPreOptionKey, this.groupSelectPreOptionValue);
    },
    filteredSearch: function() {
    	var group_name = dojo.byId("tlDisplayGroup_"+this.widgetId).getAttribute("data-param");
    	var group_name_text = dojo.byId("tlDisplayGroup_"+this.widgetId).getAttribute("data-name");
    	var ul = dojo.byId(this.groupSelectId);
    	var as =dojo.query("a.selected",ul);
        if(as.length > 0){
    	  this.changeGroup(as[0], group_name, group_name_text);
        }
    },
    changeGroup: function(node, group_name, group_name_text) {
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
              url: url,
              key: this.memberFromOptionKey,
              value: this.memberFromOptionValue,
              clickEvent: this.clickEvent,
              selectedId: this.memberToId,
              selectedAuthId: this.memberAuthorityToId,
              name: this.memberFromId,
              userId: this.memberFromOptionUserId,
              image_flag: this.memberFromOptionImageFlag,
              image_version: this.memberFromOptionImageVersionParam,
              default_image: this.memberFromOptionDefaultImage,
              child_html: this.childTemplateString,
              widgetId: this.widgetId,
              keyword: this.widgetId + "-keyword",
              indicator: this.widgetId + "-memberlist-indicator",
              callback: this.fixScroll
      };
      aimluck.io.createMemberLists(this.memberFromId, params);
      var li=node.parentNode;
      var ul=li.parentNode;
      aipo.widget.MemberFilterList.filterSelect(ul, li, node);
      aipo.widget.MemberFilterList.filterSelectDisplay(this.widgetId, group_name, group_name_text);
    },
	/**
	 * 参加メンバー追加/削除チェックボックス
	 * クリック時アクション
	 */
	onMemberCheck : function(checkbox,select){
		aipo.widget.MemberFilterList.changeMember(checkbox, select, dojo.byId(this.memberToId), dojo.byId(this.memberAuthorityToId));
		aipo.widget.MemberFilterList.filterCheckedMember(dojo.byId("tmp_head_checkbox_"+this.widgetId), this.widgetId, this.memberFromId);
		aipo.widget.MemberFilterList.setWrapperHeight();
	},
	/**
	 * 参加メンバー追加/削除チェックボックス
	 * クリック時アクション
	 */
	onAuthorityCheck : function(select, checkbox){
		aipo.widget.MemberFilterList.changeAuthority(select, checkbox, dojo.byId(this.memberToId), dojo.byId(this.memberAuthorityToId));
		//aipo.widget.MemberFilterList.filterCheckedMemberAuthority(dojo.byId("tmp_head_checkbox_"+this.widgetId), this.widgetId, this.memberAuthorityToId);
		aipo.widget.MemberFilterList.setWrapperHeight();
	},
    fixScroll: function() {
    	// for chrome
      if (!dojo.isIE) {
    	dojo.forEach(dojo.query(".memberPopupDiv_ver3 .memberPopupList"), function(item){
    		setTimeout(function(){
        		item.style.overflow="hidden";
        		item.style["overflow-y"]="scroll";
    		}, 100);
    		});
    	}
    }
});


/**
 * 選択済みユーザー読み込み
 */
aipo.widget.MemberFilterList.setup = function(widgetId, memberFromId, memberToId, memberAuthorityToId){
    var picker = dojo.byId(widgetId);
    if (picker) {
        var select = dojo.byId(memberFromId);
        var i;
        var s_o = select.options;
        if (s_o.length == 1 && s_o[0].value == "")
            return;
        for (i = 0; i < s_o.length; i++) {
        	aipo.widget.MemberFilterList.addOptionSync(s_o[i].value, s_o[i].text, true, s_o[i].getAttribute('data-authority'), memberToId, memberAuthorityToId);
        }
    }
}

/**
 * 選択済みユーザー読み込み
 */
aipo.widget.MemberFilterList.addOptionSync = function(value, text, is_selected, authority, memberToId, memberAuthorityToId) {
  var select = dojo.byId(memberToId);
  var selectAuth = dojo.byId(memberAuthorityToId);
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;

    if (select.options.length == 1 && select.options[0].value == "") {
    	select.options.remove(0);
    }

    select.add(option, select.options.length);

    var optionAuth = document.createElement("OPTION");
    optionAuth.value = authority;
    optionAuth.text = text;
    optionAuth.selected = is_selected;
    if (selectAuth.options.length == 1 && selectAuth.options[0].value == ""){
    	selectAuth.options.remove(0);
    }
    selectAuth.add(optionAuth, selectAuth.options.length);

  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;

    if (select.options.length == 1 && select.options[0].value == ""){
        select.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);

    var optionAuth = document.createElement("OPTION");
    optionAuth.value = authority;
    optionAuth.text = text;
    optionAuth.selected = is_selected;
    if (selectAuth.options.length == 1 && selectAuth.options[0].value == ""){
    	selectAuth.removeChild(selectAuth.options[0]);
    }
    selectAuth.insertBefore(optionAuth, selectAuth.options[selectAuth.options.length]);
  }
}

/**
 * リサイズ
 */
aipo.widget.MemberFilterList.setWrapperHeight = function(){
    var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
  	  var wrapper = document.getElementById('wrapper');
  	  wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

/**
 * メンバー一括登録
 */
aipo.widget.MemberFilterList.addAllMember = function(checkbox, memberFromId, select_member_to) {
    if (document.all) {
          var t_o = select_member_to.options;
          for (i = 0; i < checkbox.form.elements.length; i++) {
        	  input = checkbox.form.elements[i];
              if (!input.disabled && input.type == "checkbox" && input.name == memberFromId) {
              var iseq = false;

              for( j = 0 ; j < t_o.length; j ++ ) {
              if( t_o[j].value == input.value ) {
                  iseq = true;
                  break;
              }
              }

              if(iseq) continue;
              var option = document.createElement("OPTION");
              option.value = input.value;
              option.text = input.getAttribute("data-name");
              option.selected = true;
          if (t_o.length == 1 && t_o[0].value == ""){
                  t_o.remove(0);
          }
              t_o.add(option, t_o.length);
          }
          }
    } else {
          var t_o = select_member_to.options;
          for (i = 0; i < checkbox.form.elements.length; i++) {
        	  input = checkbox.form.elements[i];
              if (!input.disabled && input.type == "checkbox" && input.name == memberFromId) {
              var iseq = false;

              for( j = 0 ; j < t_o.length; j ++ ) {
              if( t_o[j].value == input.value ) {
                  iseq = true;
                  break;
              }
              }

              if(iseq) continue;
              var option = document.createElement("OPTION");
              option.value = input.value;
              option.text = input.getAttribute("data-name");
              option.selected = true;
          if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
              select_member_to.removeChild(select_member_to.options[0]);
          }
              select_member_to.insertBefore(option, t_o[t_o.length]);
          }
          }
    }
}

/**
 * メンバー一括削除
 */
aipo.widget.MemberFilterList.removeAllMember = function(select) {
  if (document.all) {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
          t_o.remove(i);
            i -= 1;
        }
        }
  } else {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
                select.removeChild(t_o[i]);
            i -= 1;
            }
        }
  }
}

/**
 * メンバー選択状態切り替え
 * チェックボックス押下状態により、id="select_member_to"のselectタグ追加・削除処理
 *
 * @fixed
 */
aipo.widget.MemberFilterList.changeMember = function(input, select, select_member_to, select_auth_to) {
  if (document.all) {
      var t_o = select_member_to.options;
      var a_o = select_auth_to.options;
      var a_s = dojo.byId(select);
      var authority = a_s.value;
      if (input.value == "") return;
      if (input.checked){
          var iseq = false;

          for( j = 0 ; j < t_o.length; j ++ ) {
          if( t_o[j].value == input.value ) {
              iseq = true;
              break;
          }
          }

          if(iseq) return;
          var option = document.createElement("OPTION");
          option.value = input.value;
          option.text = input.getAttribute("data-name");
          option.selected = true;
          if (t_o.length == 1 && t_o[0].value == ""){
              t_o.remove(0);
              }
         if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
          t_o.add(option, t_o.length);

          var optionAuth = document.createElement("OPTION");
          optionAuth.value = authority;
          optionAuth.text = input.getAttribute("data-name");
          optionAuth.selected = true;
          if (a_o.length == 1 && a_o[0].value == ""){
        	  a_o.remove(0);
              }
          if (this.memberLimit != 0 && select_auth_to.options.length >= this.memberLimit) return;
          a_o.add(optionAuth, a_o.length);

      }else{

          for( j = 0 ; j < t_o.length; j ++ ) {
          if( t_o[j].value == input.value ) {
              t_o.remove(j);
              a_o.remove(j);
          }
          }

      }
} else {
        var t_o = select_member_to.options;
        var a_o = select_auth_to.options;
        var a_s = dojo.byId(select);
        var authority = a_s.value;
        if (input.value == "") return;
        if (input.checked){
            var iseq = false;

            for( j = 0 ; j < t_o.length; j ++ ) {
            if( t_o[j].value == input.value ) {
                iseq = true;
                break;
            }
            }

            if(iseq) return;
            var option = document.createElement("OPTION");
            option.value = input.value;
            option.text = input.getAttribute("data-name");
            option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
              select_member_to.removeChild(select_member_to.options[0]);
            }
            select_member_to.insertBefore(option, t_o[t_o.length]);

            var optionAuth = document.createElement("OPTION");
            optionAuth.value = authority;
            optionAuth.text = input.getAttribute("data-name");
            optionAuth.selected = true;

            if (select_auth_to.options.length == 1 && select_auth_to.options[0].value == ""){
            	select_auth_to.removeChild(select_auth_to.options[0]);
            }
            select_auth_to.insertBefore(optionAuth, a_o[a_o.length]);

        }else{

            for( j = 0 ; j < t_o.length; j ++ ) {
            if( t_o[j].value == input.value ) {
            	select_member_to.removeChild(t_o[j]);
            	select_auth_to.removeChild(a_o[j]);
            }
            }
        }
  }
}

/**
 * 管理者権限の選択状態切り替え
 *
 * ユーザー選択チェックボックス:ON　の状態 で
 * 管理者権限を操作した場合、
 * id="member_authority_to"のselectタグを更新
 *
 * @fixed
 */
aipo.widget.MemberFilterList.changeAuthority = function(select, input,  select_member_to, select_auth_to) {
    var t_o = select_member_to.options;
    var a_o = select_auth_to.options;
    var a_s = dojo.byId(select);
    var authority = a_s.value;
    var _input = dojo.byId(input);
    if (_input.value == "") return;
    for( j = 0 ; j < t_o.length; j ++ ) {
        if( t_o[j].value == _input.value ) {
            a_o[j].value = dojo.byId(select).value;
        }
    }
}

/**
 * フィルタ表示イベント
 */
aipo.widget.MemberFilterList.toggleMenu = function (node,filters,event, widgetId){
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
       	//	node.style.left=rect.left+scroll.left+"px";
        }else{
        //	node.style.left=rect.right-node.clientWidth+scroll.left+"px";
        }
         if(html.bottom-node.clientHeight>rect.bottom||event){
       	//	node.style.top=rect.bottom+scroll.top+"px";
        }else{
        //	node.style.top=rect.top-node.clientHeight+scroll.top+"px";
        }
        node.style.opacity="";
    } else {
        dojo.query("div.menubar").style("display", "none");
    }

	if (aipo.userAgent.isSmartPhone()) {
	    var modalDialog = document.getElementById('modalDialog');
	    if(modalDialog) {
		dojo.connect(modalDialog, 'onclick', null, function(){
			widgetId.style.height = "";
			aipo.widget.MemberFilterList.setWrapperHeight();
		});
	    }

		setTimeout( function(){
			var rect2=node.getBoundingClientRect();
			if(widgetId) {
				if(html.bottom < rect2.bottom){
					widgetId.style.height = rect2.bottom - rect.top +"px";
				}
			}
			aipo.widget.MemberFilterList.setWrapperHeight();
		}, 0);
	}
}


/**
 * フィルタを選択した時に発生させるイベント　部署/表示のフィルタの選択状態切り替え
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterSelect = function(ul,li,a){
	dojo.query("li",ul).removeClass("selected");
	dojo.query("a",ul).removeClass("selected");
	dojo.query(li).addClass("selected");
	dojo.query(a).addClass("selected");
}

/**
 * フィルタを選択した時に発生させるイベント　部署/表示のフィルタの選択状態切り替えに伴う表示用テキスト切り替え
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterSelectDisplay = function(widgetId, key, name){
	dojo.byId("tlDisplayGroup_"+widgetId).innerHTML = name;
	dojo.byId("tlDisplayGroup_"+widgetId).setAttribute("data-param", key);
	dojo.byId("tlDisplayGroup_"+widgetId).setAttribute("data-name", name);
}

/**
 * フィルタを選択した時に発生させるイベント　選択・未選択切り替え
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterCheckedDisplay = function(widgetId, node, memberFromId){
    var li=node.parentNode;
    var ul=li.parentNode;
    aipo.widget.MemberFilterList.filterSelectDisplayView(widgetId, node);
    aipo.widget.MemberFilterList.filterSelect(ul,li,node);
    aipo.widget.MemberFilterList.filterCheckedMember(dojo.byId("tmp_head_checkbox_"+widgetId), widgetId, memberFromId);
    //aipo.widget.MemberFilterList.filterCheckedMemberAuthority(dojo.byId("tmp_head_checkbox_"+widgetId), widgetId, authorityFromId);
}

/**
 * フィルタを選択した時に発生させるイベント　フィルタ選択名表示
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterSelectDisplayView = function(widgetId, node){
    var li=node.parentNode;
    var ul=li.parentNode;
	var param = node.getAttribute("data-param");
	dojo.byId("tlDisplayView_"+widgetId).innerHTML = node.innerHTML;
	dojo.byId("tlDisplayView_"+widgetId).setAttribute("data-param", param);
    var a = dojo.byId("tlDisplayView_"+widgetId).parentNode;
    if(param != 0){
        dojo.style(a, "display", "");
    }else{
        dojo.style(a, "display", "none");
    }
}

/**
 * 選択済み・未選択の表示切り替え
 * チェックボックスクリック後に選択済み・未選択でフィルタリングされている場合は一覧からの表示/非表示を切り替える
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterCheckedMemberSync = function(checkbox, param){
		var label = checkbox.parentNode;
			var li = label.parentNode;
    	if(param == 1){
  		  //選択済み
  		  if(checkbox.checked){
  			checkbox.disabled = false;
  			  dojo.style(li, "display", "block");
  		  }else{
  			checkbox.disabled = true;
  			  dojo.style(li, "display", "none");
  		  }
  	  }else if(param == 2){
  		  //未選択
  		  if(!checkbox.checked){
  			checkbox.disabled = false;
  			  dojo.style(li, "display", "block");
  		  }else{
  			checkbox.disabled =  true;
  			  dojo.style(li, "display", "none");
  		  }
  	  }else{
  		  //すべて
  		checkbox.disabled = false;
  		  dojo.style(li, "display", "block");
  	  }

}

/**
 * 選択済み・未選択の表示切り替え
 * チェックボックスクリック後に選択済み・未選択でフィルタリングされている場合は一覧からの表示/非表示を切り替える
 * @param ul
 * @param li
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filterCheckedMember = function(checkbox, widgetId, memberFromId){
	var param = dojo.byId("tlDisplayView_"+widgetId).getAttribute("data-param");
    if (document.all) {
          for (i = 0; i < checkbox.form.elements.length; i++) {
        	  input = checkbox.form.elements[i];
              if (input.type == "checkbox" && input.name == memberFromId) {
            	  aipo.widget.MemberFilterList.filterCheckedMemberSync(input,param);
              }
          }
    } else {
          for (i = 0; i < checkbox.form.elements.length; i++) {
        	  input = checkbox.form.elements[i];
              if (input.type == "checkbox" && input.name == memberFromId) {
            	  aipo.widget.MemberFilterList.filterCheckedMemberSync(input,param);
              }
          }
    }
}

/**
 * 検索キーワード入力時イベント
 *
 * @fixed
 */
aipo.widget.MemberFilterList.filteredSearchCheck =  function(e, widgetId) {
    var widget = dijit.byId(widgetId);
	var keycode;
	if (window.event){
		keycode = window.event.keyCode;
	}else if (e){
		keycode = e.which;
	}
	if ((keycode == 13) | (keycode == 32)) {
        if (widget) {
          widget.filteredSearch();
        }
        return false;
      }
    return true;
}
}