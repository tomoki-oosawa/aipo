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
dojo.provide("aipo.project");

dojo.require("aipo.widget.DropdownDatepicker");

aipo.project.FLG_ON = "t";
aipo.project.FLG_OFF = "f";

/**
 * プロジェクトの子画面表示
 */
aipo.project.onLoadProjectDialog = function (portlet_id) {
  var url_userlist = dojo.byId('urlUserlist' + portlet_id).value;
  var login_user_id = dojo.byId('loginUser' + portlet_id).value;
  var project_user_id = dojo.byId('projectUser' + portlet_id).value;
  if (project_user_id == 0) {
    project_user_id = login_user_id;
  }
  if (url_userlist) {
    aipo.project.changeGroup(url_userlist, 'LoginUser', project_user_id, "admin_user_id");
  }

  //------------------------------------
  //プロジェクト編集のメンバー初期表示ここから
  var mpicker = dijit.byId("membernormalselect");
  if (mpicker) {
    var select = dojo.byId('init_memberlist');
    var i;
    var s_o = select.options;
    if (s_o.length == 1 && s_o[0].value == "") return;
    for (i = 0; i < s_o.length; i++) {
      mpicker.addOptionSync(s_o[i].value, s_o[i].text, true);
    }
  }

  var mpicker = dijit.byId("mapnormalselect");
  if (mpicker) {
    var select = dojo.byId('init_maplist');
    var i;
    var s_o = select.options;
    if (s_o.length == 1 && s_o[0].value == "") return;
    for (i = 0; i < s_o.length; i++) {
      mpicker.addOptionSync(s_o[i].value, s_o[i].text, true);
    }
  }

  var btn_ma = dojo.byId("button_member_add");
  if (btn_ma) {
    dojo.connect(btn_ma, "onclick", function () {
      aipo.report.expandMember();
    });
  }

  var btn_ma = dojo.byId("button_map_add");
  if (btn_ma) {
    dojo.connect(btn_ma, "onclick", function () {
      aipo.report.expandMap();
    });
  }

  var btn_mr = dojo.byId("button_member_remove");
  if (btn_mr) {
    dojo.connect(btn_mr, "onclick", function () {
      var select = dojo.byId("members");
      if (select.options.length == 0) {
        if ((mpicker) && (aipo.report.login_aliasname != "undefined")) {
          var alias = aipo.report.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
          mpicker.addOptionSync(aipo.report.login_name, alias, true);
        }
      }
      aipo.report.expandMember();
    });
  }

  var btn_mr = dojo.byId("button_map_remove");
  if (btn_mr) {
    dojo.connect(btn_mr, "onclick", function () {
      var select = dojo.byId("positions");
      if (select.options.length == 0) {
        if ((mpicker) && (aipo.report.login_aliasname != "undefined")) {
          var alias = aipo.report.login_aliasname.replace(/&amp;/g, "&").replace(/&quot;/g, "\"").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
          mpicker.addOptionSync(aipo.report.login_name, alias, true);
        }
      }
      aipo.report.expandMap();
    });
  }
  //プロジェクト編集のメンバー初期表示ここまで
  //------------------------------------

  var obj = dojo.byId("project_name");
  if (obj) {
    obj.focus();
  }
}

/**
 * タスクの子画面表示
 */
aipo.project.onLoadProjectTaskDialog = function (portlet_id) {
  var obj = dojo.byId("tracker");
  if (obj) {
    obj.focus();
  }
  var projectId = dojo.byId("projectId");
  var mode = dojo.byId("mode-" + portlet_id);
  if (projectId && mode && mode.value == "insert") {
    projectId.onchange();
  }
}

/**
 * ユーザーグループ変更時の処理
 */
aipo.project.changeGroup = function (link, group, sel, userId) {
  if (!userId) {
    userId = "admin_user_id";
  }
  aimluck.utils.form.createSelect(userId, "destuserDiv", link + "?mode=group&groupname=" + group + "&inc_luser=true", "userId", "aliasName", sel, '', 'class="w49"');
}

/**
 * 詳細画面でエラーメッセージを表示する
 */
aipo.project.onReceiveMessage = function (msg, portletId) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.project.reload();
  } else {
    if (dojo.byId('messageDiv')) {
      dojo.byId('messageDiv').innerHTML = msg;
      if (dojo.byId('messageDivComment')) {
        dojo.byId('messageDivComment').innerHTML = "";
      }
    }
    if (dojo.byId('messageDiv' + "-" + portletId)) {
      dojo.byId('messageDiv' + "-" + portletId).innerHTML = msg;
      if (dojo.byId('messageDivComment' + "-" + portletId)) {
        dojo.byId('messageDivComment' + "-" + portletId).innerHTML = "";
      }
    }
  }
}

/**
 * 詳細画面でエラーメッセージを表示する（コメント用）
 */
aipo.project.onReceiveMessageComment = function (msg, portlet) {
  if (!msg) {
    var arrDialog = dijit.byId("modalDialog");
    if (arrDialog) {
      arrDialog.hide();
    }
    aipo.portletReload('project');
  }
  if (dojo.byId('messageDivComment')) {
    dojo.byId('messageDiv').innerHTML = "";
    dojo.byId('messageDivComment').innerHTML = msg;
  }
}

/**
 * チェックボックスの値を設定する
 */
aipo.project.formFlgToggle = function (chkbox, id) {
  dojo.byId(id).value = chkbox.checked ? aipo.project.FLG_ON : aipo.project.FLG_OFF;
}
/**
 * 担当者の入力フォームを削除する
 */
aipo.project.removeMemberForm = function (i) {
  var tbody = dojo.byId('members_form');
  tbody.removeChild(dojo.byId("members_tr_" + i));
}

/**
 * タスク登録の担当者の入力フォームを追加する
 */
aipo.project.addMemberForm = function () {

  //選択肢のtbody要素
  var tbody = dojo.byId('members_form');
  var cntMember = tbody.children.length;
  cntMember++;

  //担当者選択
  var newMembersSelectTd = document.createElement("td");
  var newMembersSelect = dojo.clone(dojo.byId('task_member_1'));
  newMembersSelect.id = "task_member_" + cntMember;

  //作業時間タイトル
  var newTitleTd = document.createElement("td");
  newTitleTd.className = "p15";
  newTitleTd.noWrap = "true";
  var newTitleHeader = document.createTextNode("  作業時間 ");
  var newTitleFooter = document.createTextNode(" h ");

  //テキストボックス
  var newTextBox = document.createElement("input");
  newTextBox.type = "text";
  newTextBox.id = "workload_" + cntMember;
  newTextBox.name = "workload";
  newTextBox.className = "text";
  newTextBox.value = "";
  newTextBox.style.imeMode = "active";
  newTextBox.style.width = "3em";
  newTextBox.maxLength = 5;

  //削除リンク
  var newDeleteLinkIcon = document.createElement('i');
  newDeleteLinkIcon.className = "icon-remove";
  var newDeleteLink = document.createElement("a");
  newDeleteLink.appendChild(newDeleteLinkIcon);
  newDeleteLink.onclick = function () {
    aipo.project.removeMemberForm(cntMember);
  };
  newDeleteLink.href = "javascript:void(0);";

  //tr要素を追加
  var newTr = document.createElement('tr');
  newTr.id = "members_tr_" + cntMember;
  newTr.appendChild(newMembersSelectTd);
  newMembersSelectTd.appendChild(newMembersSelect);
  newTr.appendChild(newTitleTd);
  newTitleTd.appendChild(newTitleHeader);
  newTitleTd.appendChild(newTextBox);
  newTitleTd.appendChild(newTitleFooter);
  newTitleTd.appendChild(newDeleteLink);
  tbody.appendChild(newTr);
}

/**
 * 並び替えウインドウ用
 */
aipo.project.sortsubmit = function (form) {
  var s_o = form.project_so.options;
  var tmp = "";
  for (i = 0; i < s_o.length; i++) {
    s_o[i].selected = false;
  }
  if (s_o.length > 0) {
    tmp = s_o[0].value;
    for (i = 1; i < s_o.length; i++) {
      tmp = tmp + ',' + s_o[i].value;
    }
  }
  form.positions.value = tmp;
}

/**
 * 表示年月変更時の処理
 */
aipo.project.onChangeDate = function (base_url, p_id) {
  var sel_from_year = dojo.byId('base_date_from_year');
  var sel_from_month = dojo.byId('base_date_from_month');
  var sel_to_year = dojo.byId('base_date_to_year');
  var sel_to_month = dojo.byId('base_date_to_month');

  var from_year = parseInt(sel_from_year.options[sel_from_year.selectedIndex].value, 10);
  var from_month = parseInt(sel_from_month.options[sel_from_month.selectedIndex].value, 10);
  var to_year = parseInt(sel_to_year.options[sel_to_year.selectedIndex].value, 10);
  var to_month = parseInt(sel_to_month.options[sel_to_month.selectedIndex].value, 10);

  if (from_year > to_year
    || (from_year == to_year && from_month > to_month)) {
    from_year = to_year;
    from_month = to_month;
  }

  var exec_url = base_url
    + "&base_date_from_year=" + from_year
    + "&base_date_from_month=" + from_month
    + "&base_date_to_year=" + to_year
    + "&base_date_to_month=" + to_month

  aipo.project.viewPage(exec_url, p_id);
}

/**
 * 進捗率選択時の処理
 */
aipo.project.onChangeProgressRate = function (base_url, p_id, key) {
  var obj = dojo.byId(key);
  var val = obj.options[obj.selectedIndex].value;
  var exec_url = base_url + "&" + key + "=" + val;
  aipo.project.viewPage(exec_url, p_id);
}

/**
 * 進捗遅れのみ表示のチェックボックス押下時の処理
 */
aipo.project.onChangeDelay = function (checkbox, id, base_url, p_id) {
  aipo.project.formFlgToggle(checkbox, id);

  var exec_url = base_url + "&target_delay=" + dojo.byId(id).value;
  aipo.project.viewPage(exec_url, p_id);
}

/**
 * イナズマ線のチェックボックス切り替え
 */
aipo.project.onChangeProgressLine = function (checkbox, base_url, p_id) {
  aipo.project.formFlgToggle(checkbox, 'progress_line_checked');
  aipo.project.viewPage(base_url, p_id);
}

/**
 * プロジェクト管理用viewPage
 */
aipo.project.viewPage = function (exec_url, p_id) {
  var addUrl = "";
  var chkObj = dojo.byId('progress_line_checked');
  if (chkObj) {
    addUrl = "&progress_line_checked=" + chkObj.value;
  }
  aipo.viewPage(exec_url + addUrl, p_id);
  if (chkObj) {
    aipo.project.reloadProgress();
  }

}

/**
 * プロジェクト管理用reload
 */
aipo.project.reload = function () {
  aipo.portletReload('project');
  if (dojo.byId('progress_line_checked')) {
    aipo.project.reloadProgress();
  }
}

/**
 * イナズマ線再描画
 */
aipo.project.reloadProgress = function () {
  var nowTime1st = "";
  var nowTime1stObj = dojo.byId('nowTime');
  if (nowTime1stObj) {
    nowTime1st = nowTime1stObj.innerHTML;
  }
  aipo.project.reloadProgressLine(nowTime1st);
}

/**
 * チャート部分リロード後にイナズマ線を描画する。
 * DIVタグのid:nowTimeに設定されている時間が変更されたときにイナズマ線を描画する。
 * 画面の部分更新の場合にonloadが効かないため、
 * 画面が更新されたかを別スレッドで実行することにより確認している。
 */
aipo.project.reloadProgressLine = function (nowTime1st, cnt) {
  if (!cnt) {
    cnt = 1;
  } else if (cnt > 100) {
    //100回以上呼ばれないようにする
    return;
  }
  setTimeout(function () {

    var start = new Date().getTime();
    var end = start;

    var nowTime = "";
    var nowTimeObj = dojo.byId('nowTime');
    if (nowTimeObj) {
      nowTime = nowTimeObj.innerHTML;
    }

    if (nowTime != "" && nowTime1st != nowTime) {
      aipo.project.onOffProgressLine();
    } else {
      //まだロードが完了していない場合は自身を呼ぶ
      aipo.project.reloadProgressLine(nowTime1st, cnt + 1);
    }

  }, 100);
}

/**
 * イナズマ線の表示／非表示を切り替える
 */
aipo.project.onOffProgressLine = function () {
  var checkbox = dojo.byId('progress_line_checkbox')
  var obj = dojo.byId('progress_line');
  if (checkbox.checked) {
    obj.style.display = 'block';
    aipo.project.drawProgressLine();
  } else {
    obj.style.display = 'none';
  }
}

/**
 * イナズマ線を描画します。
 */
aipo.project.drawProgressLine = function () {

  var ganttObj = dojo.byId('gantt_table');
  var lineObj = dojo.byId('progress_line');

  if (lineObj.style.setProperty) {
    lineObj.style.setProperty("left", ganttObj.offsetTop + "px");
    lineObj.style.setProperty("top", ganttObj.offsetLeft + "px");
    lineObj.style.setProperty("width", ganttObj.offsetWidth + "px");
    lineObj.style.setProperty("height", ganttObj.offsetHeight + "px");
  } else {
    lineObj.style.left = ganttObj.offsetTop + "px";
    lineObj.style.top = ganttObj.offsetLeft + "px";
    lineObj.style.width = ganttObj.offsetWidth + "px";
    lineObj.style.height = ganttObj.offsetHeight + "px";
  }

  var todayDayObj = dojo.byId('todayDay');
  var countObj = dojo.byId('count');

  var today = todayDayObj.innerHTML;
  if (today == 0) {
    return;
  }

  //本日のX座標
  var todayX = 20 * today;

  //最終行の高さ
  var lastY = ganttObj.offsetTop + ganttObj.offsetHeight;

  //データ部分の高さ開始位置
  var baseY = 64;

  //１行の高さ
  var lineHeight = 30;

  //本日日付の座標
  var todayPoint = "M" + todayX + ",42";
  //データ部開始座標
  var startPoint = "L" + todayX + "," + baseY;
  //データ部最終座標
  var endPoint = "L" + todayX + "," + lastY;

  //各タスクのグラフ座標を得る
  var pointList = "";
  var x, y;
  for (var i = 1; i <= countObj.innerHTML; i++) {
    var result = dojo.byId('result' + i);			//実績
    var plan = dojo.byId('plan' + i);				//予定
    var finish = dojo.byId('progress_finish' + i);	//完了
    var pastFinish = dojo.byId('past_finish' + i);	//完了済み予定
    var noStart = dojo.byId('no_start' + i);		//未来開始予定

    x = todayX;
    y = i * lineHeight + baseY - (lineHeight / 2);

    if (plan) {
      if ((pastFinish && finish) || (noStart && !result)) {
        //完了済み予定で完了済み、
        //未来開始予定で未着手の場合は無視する
      } else {
        if (result) {
          x = result.offsetLeft + result.offsetWidth;
          y = result.offsetTop + (result.offsetHeight / 2);
        } else {
          x = plan.offsetLeft;
          y = plan.offsetTop + (plan.offsetHeight / 2);
        }
      }
    }
    pointList += " L" + x + "," + y;
  }

  //線の描画
  var paper = Raphael(lineObj);
  var line = paper.path(todayPoint + " " + startPoint + pointList + " " + endPoint);
  line.attr({
    'stroke': '#ff3333',
    'stroke-width': 3
  });

};

aipo.project.toggleMenu = function (node, filters, event) {
  var rect = filters.getBoundingClientRect();
  var html = document.documentElement.getBoundingClientRect();
  if (node.style.display == "none") {
    dojo.query("div.menubar").style("display", "none");

    var scroll = {
      left: document.documentElement.scrollLeft || document.body.scrollLeft,
      top: document.documentElement.scrollTop || document.body.scrollTop
    };
    node.style.opacity = "0";
    setTimeout(function () {
      dojo.style(node, "display", "block");
    }, 0);
    if (html.right - node.clientWidth > rect.left) {
      node.style.left = rect.left + scroll.left + "px";
    } else {
      node.style.left = rect.right - node.clientWidth + scroll.left + "px";
    }
    if (html.bottom - node.clientHeight > rect.bottom || event) {
      node.style.top = rect.bottom + scroll.top + "px";
    } else {
      node.style.top = rect.top - node.clientHeight + scroll.top + "px";
    }
    node.style.opacity = "";
  } else {
    dojo.query("div.menubar").style("display", "none");
  }
};

/**
 * 指定したフィルタにデフォルト値を設定する。(または消す)
 * @param portlet_id
 * @param thisnode
 * @param event
 */
aipo.project.filterSetDefault = function (portlet_id, type) {
  var ul = dojo.query("ul.filtertype[data-type=" + type + "]")[0];
  var defval = ul.getAttribute("data-defaultparam");
  var defaultli = dojo.query("li[data-param=" + defval + "]", ul);
  aipo.project.filterSelect(ul, defaultli);
  aipo.project.filteredSearch(portlet_id);
}

aipo.project.filterSelect = function (ul, li) {
  dojo.query("li", ul).removeClass("selected");
  dojo.query(li).addClass("selected");
}

aipo.project.filterClick = function (portlet_id, thisnode, event) {
  var li = thisnode.parentNode;
  var ul = li.parentNode;
  var param = li.getAttribute("data-param");//liのdata-param
  aipo.project.filterSelect(ul, li);
  aipo.project.filteredSearch(portlet_id);
}

aipo.project.projectFilterClick = function (portlet_id, thisnode) {
  var li = thisnode.parentNode;
  var ul = li.parentNode;
  aipo.project.filterSelect(ul, li);
  aipo.project.projectSearch(portlet_id);
}


/**
 * 検索バーの幅を調節する。
 *
 * @param portlet_id
 */
aipo.project.initFilterSearch = function (portlet_id) {
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
      if (filterOffset != 0) {
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
aipo.project.finFilterSearch = function (portlet_id) {
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
aipo.project.filteredSearch = function (portlet_id) {
  //filtertype
  var baseuri = dojo.byId("baseuri_" + portlet_id).value;

  var types = [];
  var params = [];
  dojo.query("ul.filtertype_" + portlet_id).forEach(function (ul) {
    //console.info(ul);
    var type = ul.getAttribute("data-type");
    types.push(type);

    var activeli = dojo.query("li.selected", ul)[0];
    if (activeli) {
      var param = activeli.getAttribute("data-param");
      params.push(param);
    } else {
      params.push(ul.getAttribute("data-defaultparam"));
    }
  });
  var q = dojo.byId("q" + portlet_id);

  var qs = [
    ["target_user_id", params[0]],
    ["target_tracker", params[1]],
    ["target_priority", params[2]],
    ["target_status", params[3]],
    ["keyword", q ? q.value : ""]
  ];
  aipo.viewPage(baseuri, portlet_id, qs);
}

/**
 * urlを整形して送信。
 */
aipo.project.projectSearch = function (portlet_id) {
  var baseuri = dojo.byId("baseuri_" + portlet_id).value;
  var types = [];
  var params = [];
  dojo.query("ul.filtertype_" + portlet_id).forEach(function (ul) {
      //console.info(ul);
      var type = ul.getAttribute("data-type");
      types.push(type);

      var activeli = dojo.query("li.selected", ul)[0];
      if (activeli) {
        var param = activeli.getAttribute("data-param");
        params.push(param);
      } else {
        params.push(ul.getAttribute("data-defaultparam"));
      }
    }
  );
  var q = dojo.byId("q" + portlet_id);
  var qs = [
    ["filter", params.join(",")],
    ["filtertype", types.join(",")],
    ["keyword", q ? q.value : ""]
  ];
  aipo.viewPage(baseuri, portlet_id, qs);
};

aipo.project.changeProjectMember = function (url, params) {
  aimluck.io.sendRawData(url, params, function (args, data) {
    /** data.type is response with json-comment-filtered */
    if (data.type.length <= 2) {
      return;
    }

    var selectList = dojo.query("select.sys-taskmember");
    selectList.forEach(
      function (select) {
        for (var i = select.options.length; 0 < select.options.length-1; i--) {
          select.options.remove(i - 1);
        }
      }
    );

    var count = 1;
    var jsonstring = data.type.substr(2, data.type.length - 4);
    var jsonObj = dojo.fromJson(jsonstring);

    if(dojo.isArray(jsonObj) && jsonObj.length > 0 && jsonObj[0]){
      var jsonArray = jsonObj[0];
      for (var key in jsonArray) {
        selectList.forEach(
          function (select) {
            select.options[count] = new Option(jsonArray[key], key);
          }
        );
        count++;
      }
    }
  });
};

aipo.project.setIndicator = function(portlet_id) {

    obj_content = dojo.byId('content-'+portlet_id);
    dojo.style(obj_content, "visibility" , "hidden");
    obj_indicator = dojo.byId('indicator-'+portlet_id);
    dojo.style(obj_indicator, "display" , "");
}
