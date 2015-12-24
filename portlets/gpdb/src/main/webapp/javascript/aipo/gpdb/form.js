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
dojo.provide("aipo.gpdb");

dojo.require("aipo.widget.DropdownDatepicker");

/** 項目定義形式：テキスト */
aipo.gpdb.ITEM_TYPE_TEXT = "01";

/** 項目定義形式：テキストエリア */
aipo.gpdb.ITEM_TYPE_TEXTAREA = "02";

/** 項目定義形式：リンク */
aipo.gpdb.ITEM_TYPE_LINK = "03";

/** 項目定義形式：選択式（択一） */
aipo.gpdb.ITEM_TYPE_SELECT = "04";

/** 項目定義形式：選択式（複数） */
aipo.gpdb.ITEM_TYPE_SELECT_MULTI = "05";

/** 項目定義形式：ファイル */
aipo.gpdb.ITEM_TYPE_FILE = "06";

/** 項目定義形式：画像 */
aipo.gpdb.ITEM_TYPE_IMAGE = "07";

/** 項目定義形式：自動採番 */
aipo.gpdb.ITEM_TYPE_SEQ = "08";

/** 項目定義形式：メール */
aipo.gpdb.ITEM_TYPE_MAIL = "09";

/** 項目定義形式：日付 */
aipo.gpdb.ITEM_TYPE_DATE = "10";

/** 項目定義形式：作成日 */
aipo.gpdb.ITEM_TYPE_CREATE_DATE = "11";

/** 項目定義形式：更新日時 */
aipo.gpdb.ITEM_TYPE_UPDATE_DATE = "12";

/** 項目定義形式：登録者 */
aipo.gpdb.ITEM_TYPE_CREATE_USER = "13";

/** 項目定義形式：更新者 */
aipo.gpdb.ITEM_TYPE_UPDATE_USER = "14";

aipo.gpdb.onLoadGpdbDialog = function(portlet_id){
  var url_userlist = dojo.byId('urlUserlist'+portlet_id).value;
  var login_user_id = dojo.byId('loginUser'+portlet_id).value;
  var gpdb_user_id = dojo.byId('gpdbUser'+portlet_id).value;

  if(gpdb_user_id == 0) {
      gpdb_user_id = login_user_id;
  }
  if(url_userlist){
      aipo.gpdb.changeGroup(url_userlist, 'LoginUser', gpdb_user_id);
  }

  var obj = dojo.byId("gpdb_name");
  if(obj){
     obj.focus();
  }
}

aipo.gpdb.onLoadCategoryDialog = function(portlet_id){

  var obj = dojo.byId("category_name");
  if(obj){
     obj.focus();
  }
}


/**
 * 区分入力フォームと区分選択フォームの表示を切り替える
 */
aipo.gpdb.formSwitchKubunInput = function(button) {
    if(button.form.is_new_kubun.value == 'TRUE' || button.form.is_new_kubun.value == 'true') {
        button.value = aimluck.io.escapeText("gpdb_val_switch1");
        aipo.gpdb.formKubunInputOff(button.form);
    } else {
        button.value = aimluck.io.escapeText("gpdb_val_switch2");
        aipo.gpdb.formKubunInputOn(button.form);
    }
}

/**
 * 区分入力フォームを有効にする
 * 区分選択フォームを無効にする
 */
aipo.gpdb.formKubunInputOn = function(form) {
    dojo.byId('gpdbKubunSelectField').style.display = "none";
    dojo.byId('gpdbKubunInputField').style.display = "";

    form.is_new_kubun.value = 'TRUE';
}

/**
 * 区分選択フォームを有効にする
 * 区分入力フォームを無効にする
 */
aipo.gpdb.formKubunInputOff = function(form) {
    dojo.byId('gpdbKubunInputField').style.display = "none";
    dojo.byId('gpdbKubunSelectField').style.display = "";

    form.is_new_kubun.value = 'FALSE';
}

aipo.gpdb.changeGroup = function(link, group, sel) {
    aimluck.utils.form.createSelect("user_id", "destuserDiv", link + "?mode=group&groupname=" + group + "&inc_luser=true", "userId", "aliasName", sel, '', 'class="w49"');
}

/**
 * 詳細画面でエラーメッセージを表示する
 */
aipo.gpdb.onReceiveMessage = function(msg, portlet){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('gpdb');
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

/**
 * 一覧画面でエラーメッセージを表示する
 */
aipo.gpdb.onListReceiveMessage = function(msg){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if(arrDialog){
            arrDialog.hide();
        }
        aipo.portletReload('gpdb');
    }
    if (dojo.byId('listmessageDiv')) {
        dojo.byId('listmessageDiv').innerHTML = msg;
    }
}

aipo.gpdb.doKeywordSearch = function(baseuri, portlet_id) {
    var params = new Array(2);
    params[0] = ["template", "GpdbListScreen"];
    params[1] = ["keyword", dojo.byId("q"+portlet_id).value];
    aipo.viewPage(baseuri, portlet_id, params);
}

/**
 * チェックボックスの値を設定する
 */
aipo.gpdb.formFlgToggle = function(chkbox, id) {
    dojo.byId(id).value = chkbox.checked ? 't' : 'f';
}

/**
 * タイトル指定チェックオン時に必須などの項目も連動してチェックさせる
 */
aipo.gpdb.checkOnWithTitle = function(chkbox, portletId) {
	if (chkbox.checked) {
		dojo.byId(portletId + 'required_flg_id').checked = true;
		dojo.byId('required_flg').value = 't';
		dojo.byId(portletId + 'list_flg_id').checked = true;
		dojo.byId('list_flg').value = 't';
		dojo.byId(portletId + 'detail_flg_id').checked = true;
		dojo.byId('detail_flg').value = 't';
	}
}

/**
 * 入力形式が選択式の場合に追加フォームを表示する
 * 項目定義登録用
 */
aipo.gpdb.formTypeChanged = function(form) {
	dojo.byId('type_select').style.display = "none";
	dojo.byId('kubun_select').style.display = "none";
	dojo.byId('tr_size_col').style.display = "none";
	dojo.byId('tr_size_row').style.display = "none";
	dojo.byId('tr_line').style.display = "none";
	dojo.byId('tr_required').style.display = "none";

	if (form.value == aipo.gpdb.ITEM_TYPE_SELECT					//選択式（択一）
			|| form.value == aipo.gpdb.ITEM_TYPE_SELECT_MULTI) {	//選択式（複数）
		dojo.byId('type_select').style.display = "";
		dojo.byId('kubun_select').style.display = "";
		dojo.byId('tr_required').style.display = "";
		if (form.value == aipo.gpdb.ITEM_TYPE_SELECT_MULTI) {
		//	dojo.byId('tr_line').style.display = "";
		}

	} else if (form.value == aipo.gpdb.ITEM_TYPE_TEXT		//テキスト
			|| form.value == aipo.gpdb.ITEM_TYPE_LINK		//リンク
			|| form.value == aipo.gpdb.ITEM_TYPE_MAIL) {	//メール
//		dojo.byId('tr_size_col').style.display = "";
		dojo.byId('tr_required').style.display = "";

	} else if (form.value == aipo.gpdb.ITEM_TYPE_TEXTAREA) {	//テキストエリア
//		dojo.byId('tr_size_col').style.display = "";
//		dojo.byId('tr_size_row').style.display = "";
		dojo.byId('tr_required').style.display = "";

	} else if (form.value == aipo.gpdb.ITEM_TYPE_FILE		//ファイル
			|| form.value == aipo.gpdb.ITEM_TYPE_IMAGE		//画像
			|| form.value == aipo.gpdb.ITEM_TYPE_DATE) {	//日付
		dojo.byId('tr_required').style.display = "";
	}
}

/**
 * 区分の選択肢の入力フォームを削除する
 */
aipo.gpdb.removeSelectItem = function(i) {
	var ul = dojo.byId('select_item');
	ul.removeChild(dojo.byId("gpdb_item_kubun_li_" + i));
}

/**
 * 区分の選択肢の入力フォームを追加する
 */
aipo.gpdb.addSelectItem = function() {

	//選択肢のul要素
	var ul = dojo.byId('select_item');
	var cntGpdbItemKubun = ul.children.length;
	cntGpdbItemKubun++;

	//テキストボックス
	var newTextBox = document.createElement("input");
	newTextBox.type = "text";
	newTextBox.id = "gpdb_item_kubun_" + cntGpdbItemKubun;
	newTextBox.name = "gpdb_item_kubun";
	newTextBox.className = "text";
	newTextBox.value = "";
	newTextBox.style.imeMode = "active";
	newTextBox.style.width = "50%";
	newTextBox.maxLength = 50;

	//削除リンク
    var newDeleteLinkText = document.createTextNode(" 削除");
	var newDeleteLink = document.createElement("a");
	newDeleteLink.appendChild(newDeleteLinkText);
	newDeleteLink.onclick = function() {
		aipo.gpdb.removeSelectItem(cntGpdbItemKubun);
	};
	newDeleteLink.href = "javascript:void(0);";

	//li要素を追加
	var newLi = document.createElement('li');
	newLi.id = "gpdb_item_kubun_li_" + cntGpdbItemKubun;
	newLi.appendChild(newTextBox);
	newLi.appendChild(newDeleteLink);
	ul.appendChild(newLi);
}

/**
 * 並び替えウインドウ用
 */
aipo.gpdb.sortsubmit = function(form){
  var s_o = form.gpdb_so.options;
  var tmp = "";
  for(i = 0 ; i < s_o.length; i++ ) {
    s_o[i].selected = false;
  }
  if(s_o.length > 0) {
	  tmp = s_o[0].value;
	    for(i = 1 ; i < s_o.length; i++ ) {
	      tmp = tmp+','+ s_o[i].value ;
	    }
  }
  form.positions.value = tmp;
}

/**
 * デフォルトソートにチェックオン時に表示させる
 */
aipo.gpdb.checkOnSort = function(chkbox) {
	if (chkbox.checked) {
		dojo.byId('tr_asc_desc').style.display = "";
	} else {
		dojo.byId('tr_asc_desc').style.display = "none";
	}
}

/**
 * アップロードファイルを置き換える。
 */
aipo.gpdb.replaceFileInfo = function(foldername, fileid, filename, pid) {

    var ul = dojo.byId('attachments_' + pid);

    if(ul.nodeName.toLowerCase()=="ul")
    	aimluck.io.replaceFileToList(ul,fileid,filename);
    else
    	aimluck.io.addOption(ul,fileid,filename, false);//MultipeによるSelectとの互換性維持
    dojo.byId('folderName_' + pid).value =  foldername;
}

aipo.gpdb.toggleMenu = function (node,filters,event){
	var rect=filters.getBoundingClientRect();
	var html=document.documentElement.getBoundingClientRect();
	if (node.style.display == "none") {
        dojo.query("div.menubar").style("display", "none");

        var scroll={
        	left:document.documentElement.scrollLeft||document.body.scrollLeft,
        	top:document.documentElement.scrollTop||document.body.scrollTop
        };
        node.style.opacity="0";
        node.style.display="block";
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
}

aipo.gpdb.downloadCsv = function(url){
	window.location.href = url;
}