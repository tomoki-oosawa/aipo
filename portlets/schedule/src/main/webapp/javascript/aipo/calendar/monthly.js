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
dojo.provide("aipo.calendar.monthly");
/**
 * グローバルに保持しておくデータ
 */
aipo.calendar.monthly_calendar_data = {
		selected_month:'',
		selected_day:'',
		next_month:"",
		prev_month:"",
		json_url:"",
		portlet_id:"",
		oneday_link:""
};
/**
 * 指定した月を表示
 */
aipo.calendar.showMonthlyCalendar = function(month){
	var json_url = aipo.calendar.monthly_calendar_data.json_url;
	json_url += "&monthly_calendar_month=" + month;
	aipo.calendar.createMonthlyCalendar(json_url);
};
/**
 * 翌月を表示
 */
aipo.calendar.showNextMonthlyCalendar = function(){
	aipo.calendar.showMonthlyCalendar(aipo.calendar.monthly_calendar_data.next_month);
};
/**
 * 先月を表示
 */
aipo.calendar.showPreviousMonthlyCalendar = function(){
	aipo.calendar.showMonthlyCalendar(aipo.calendar.monthly_calendar_data.prev_month);
};
/**
 * 初期化
 */
aipo.calendar.initMonthlyCalendar = function(portlet_id,json_url,oneday_link,month,day){
	var mc_data = aipo.calendar.monthly_calendar_data;


	mc_data.portlet_id = portlet_id;
	mc_data.json_url = json_url;
	mc_data.oneday_link = oneday_link;
	mc_data.selected_month = month;
	mc_data.selected_day = day;

	//ここで前月を生成

	// 表示するデータをAjaxで取得

    if(ptConfig[mc_data.portlet_id].xhrUrl != json_url){
        ptConfig[mc_data.portlet_id].xhrUrl = json_url;
	    dojo.xhrGet({
	        portletId: mc_data.portlet_id,
	        url: json_url,
	        encoding: "utf-8",
	        handleAs: "json-comment-filtered",
	        /**
	         * 読み込み完了
	         */
	        load: function(data, event) {
	        	ptConfig[mc_data.portlet_id].xhrUrl = "";
	        	var result = data;
	        	/*
	        	 * 先月、翌月を保存しておく
	        	 */
	        	mc_data.next_month = result.next_month;
	        	mc_data.prev_month = result.prev_month;
	        }
	    });
    }
};
/**
 * カレンダーを再描画
 */
aipo.calendar.reloadMonthlyCalendar = function(){
	aipo.calendar.createMonthlyCalendar(aipo.calendar.monthly_calendar_data.json_url);
};
/**
 * サイドに表示する小さな月カレンダーを作成する
 */


aipo.calendar.createMonthlyCalendar = function(json_url){

	// 表示するデータをAjaxで取得
	var mc_data = aipo.calendar.monthly_calendar_data;
    if(ptConfig[mc_data.portlet_id].xhrUrl != json_url){
        ptConfig[mc_data.portlet_id].xhrUrl = json_url;
	    dojo.xhrGet({
	        portletId: mc_data.portlet_id,
	        url: json_url,
	        encoding: "utf-8",
	        handleAs: "json-comment-filtered",
	        /**
	         * 読み込み完了
	         */
	        load: function(data, event) {
	        	ptConfig[mc_data.portlet_id].xhrUrl ="";
	        	var result = data;
	        	if(result.error == 1){
	        		//セッションタイムアウトエラー発生時
	        		location.reload();
	        	}
	        	/*
	        	 * 日付表示
	        	 */
	        	if(dojo.byId("mc_year").innerText){
	            	dojo.byId("mc_year").innerText = result.year;
	            	dojo.byId("mc_month").innerText = result.month;
	        	}else{
	            	dojo.byId("mc_year").innerHTML = result.year;
	            	dojo.byId("mc_month").innerHTML = result.month;
	        	}
	        	/*
	        	 * 先月、翌月を保存しておく
	        	 */
	        	mc_data.next_month = result.next_month;
	        	mc_data.prev_month = result.prev_month;

	        	/*
	        	 * カレンダーの作成
	        	 */
	        	var table = dojo.byId("mc_table");
	        	// 前回作成したカレンダーを削除
	        	if(!aipo.calendar.monthly_calendar_data.is_first){
		        	var elems = new Array();
		        	for(var i=0; i<table.childNodes.length; i++){
		        		var elem = table.childNodes[i];
		        		if(elem.className == "monthlyCalendarAutoTr"){
		        			elems.push(elem);
		        		}
		        	}
		        	for(var i=0; i<elems.length; i++){
		    			table.removeChild(elems[i]);
		        	}

		        	// カレンダーを作成
		        	for(var i=0; i<result.monthly_container.length; i++){
		        		// 週
		        		var weekly_container = result.monthly_container[i];

		        		var elem_tr = document.createElement("tr");
		        		table.appendChild(elem_tr);
		        		elem_tr.className = "monthlyCalendarAutoTr"; // 削除用

		        		for(var j=0; j<weekly_container.length; j++){
		            		// 日
		        			var day_container = weekly_container[j];

		        			// <td>
		        			var elem_td = document.createElement("td");
		        			elem_tr.appendChild(elem_td);
		        			/*
		        			 * <td>にstyleの追加
		        			 */
		        			if(day_container.is_holiday){// 休日
		        				elem_td.className = elem_td.className+" holiday";
		        			}else{
			        			if(j==0){// 日曜
			        				elem_td.className = elem_td.className+" sunday";
			        			}
			        			if(j==6){// 土曜
			        				elem_td.className = elem_td.className+" saturday";
			        			}
		        			}
		        			if(day_container.month!=result.month){// 今月以外の日
		        				elem_td.className = elem_td.className+" out";
		        			}
		        			if(day_container.today==result.today){// 今日
		        				elem_td.className += " today";
		        			}
		        			if(day_container.month==mc_data.selected_month
		        					&& day_container.day==mc_data.selected_day){// 選択されている日
		        				elem_td.className += " selected";
		        			}

		        			// <a>
		        			var elem_a = document.createElement("a");
		        			elem_td.appendChild(elem_a);
		        			/*
		        			 * <a>にリンクの追加
		        			 */
		        			elem_a.setAttribute("href","javascript:void(0);");

		        			elem_a.setAttribute("data-date",day_container.today);
		        			elem_a.setAttribute("data-link",mc_data.oneday_link+"&view_start="+day_container.today);

		        			dojo.query(elem_a).onclick(function(){
		        				aipo.schedule.setIndicator(mc_data.portlet_id);
		        				aipo.viewPage(this.getAttribute("data-link"),mc_data.portlet_id);
		        			});

		        			//日付のテキスト
		        			if(elem_a.innerText){
		        				elem_a.innerText = day_container.day;
		        			}else{
		        				elem_a.innerHTML = day_container.day;
		        			}
		        		}
		        	}
	        	}
	            aipo.calendar.monthly_calendar_data.is_first=false;
	        }
	    });
    }
};