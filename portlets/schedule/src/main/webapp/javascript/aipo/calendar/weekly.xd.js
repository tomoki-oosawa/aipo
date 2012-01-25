dojo._xdResourceLoaded({
depends: [["provide", "aipo.calendar.weekly"],
["require", "aimluck.dnd.Draggable"],
["require", "aipo.widget.ToolTip"],
["require", "aipo.widget.MemberNormalSelectList"],
["require", "aipo.widget.GroupNormalSelectList"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.calendar.weekly"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.calendar.weekly"] = true;
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

dojo.provide("aipo.calendar.weekly");

dojo.require("aimluck.dnd.Draggable");
dojo.require("aipo.widget.ToolTip");
dojo.require("aipo.widget.MemberNormalSelectList");
dojo.require("aipo.widget.GroupNormalSelectList");

aipo.calendar.objectlist = Array();
aipo.calendar.maximum_to = 30;

aipo.calendar.populateWeeklySchedule = function(_portletId, params) {
    var _params;
    var member_to = dojo.byId('member_to-' + _portletId);
    if (typeof params == "undefined" || typeof ptConfig[_portletId].jsonData == "undefined") {
       _params = "";
    } else {
       _params = params;
    }

    /* セキュリティIDを追加 */
    var secid = dojo.byId('secid-' + _portletId);
    if(secid){
       _params += "&secid=" + secid.value;
    }

    /*設備重複時はパラメータを追加しない*/
    if( _params.match(/ign_dup_f/) == null){
	     if(member_to) {
	       var t_o = member_to.options;
	       to_size = t_o.length;
	       if(to_size == 0){
	           _params += "&m_id=" + aipo.schedule.login_id;
	       }
	       for(i = 0 ; i < to_size; i++ ) {
	           t_o[i].selected = true;
	           _params += "&m_id=" + t_o[i].value;
	       }
	    }
	    var chk_all = dojo.byId('showAll-' + _portletId);
	    if(chk_all) { _params += "&s_all=" + chk_all.value;}
    }

    djConfig.usePlainJson=true;
    ptConfig[_portletId].reloadFunction = aipo.calendar.populateWeeklySchedule;

    ptConfig[_portletId].isTooltipEnable = false;
    if(aipo.calendar.dummyDivObj){
         aipo.calendar.dummyDivObj.destroy();
         aipo.calendar.dummyDivObj = null;
    }

    dojo.xhrGet({
        portletId: _portletId,
        url: ptConfig[_portletId].jsonUrl + _params,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        load: function(data, event) {
            //権限チェック
            obj_error = dojo.byId('error-'+_portletId);
            dojo.style(obj_error, "display" , "none");
            if("PermissionError" == data[0]){
               dojo.style(obj_error, "display" , "block");
               obj_error.innerHTML = data[1];
               obj_content = dojo.byId('content-'+_portletId);
               dojo.style(obj_content,  "display" , "none");
               obj_indicator = dojo.byId('indicator-'+_portletId);
               dojo.style(obj_indicator, "display" , "none")
               return;
            } else if(data["errList"]){
               if("duplicate_facility" == data.errList[0]){
                    if(confirm('既に同じ時間帯に設備が予約されています。スケジュールを登録しますか？')) {
                        var new_param = _params + '&ign_dup_f=true'
                        aipo.calendar.populateWeeklySchedule(_portletId, new_param);
                        aipo.portletReload('schedule', _portletId);
                        return;
                    }
               }

               if("UpdateError" == data.errList[0]){
                     dojo.style(obj_error, "display" , "block");
                     obj_error.innerHTML = "<ul><li><span class=\"caution\">"+data.errList[1]+"</span></li></ul>";
                     obj_content = dojo.byId('content-'+_portletId);
                     dojo.style(obj_content, "visibility" , "visible");
                     obj_indicator = dojo.byId('indicator-'+_portletId);
                     dojo.style(obj_indicator, "display" , "none")
                }
            }
            //オブジェクト削除
            var i;
            if(!!aipo.calendar.objectlist){
                var o_size = aipo.calendar.objectlist.length;
                for(i = 0; i < o_size; i++){
                    var obj = aipo.calendar.objectlist[i];
                    if(obj.portletId == _portletId){
                        obj.destroy();
                    }
                }
            }

            if (!aipo.errorTreatment(data, ptConfig[_portletId].thisUrl)) {
                return;
            }
            ptConfig[_portletId].jsonData = data;
            var scheduleDiv = Array(ptConfig[_portletId].scheduleDivDaySum);
            for(var i=0;i<ptConfig[_portletId].scheduleDivDaySum;i++) {
                scheduleDiv[i] = Array();
            }
            var count = 0;
            var l_count = 0;
            var html = '';
            var termHtml = '';
            var termTableHtml = '';
            var tmpHeight = [];
            var tmpNode1, tmpNode2, tmpNode3, tmpNode4;
            var startEnd = data.startDate.substring(0,4) + "年" + parseInt(data.startDate.substring(5,7),10) + "月" + parseInt(data.startDate.substring(8,10),10) + "日" + data.dayOfWeek[0];
            dojo.byId('viewWeekly-' + _portletId).innerHTML = startEnd;

            termTableHtml += "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"><tbody>"
            dojo.forEach(data.termSchedule, function(itemList) {
                var item = null;
                termTableHtml += '<tr><td width="50"><div class="weeklyTermLeft" id="weeklyTermLeft"><div class="weeklyTermLeftTop">&nbsp;</div></div></td><td nowrap="nowrap" width="100%" valign="top"><div class="weeklyTermRights">';
                termTableHtml += '<div class="weeklyTermRight weeklyTermRightL" id="termDay0-'+ l_count + '-' +_portletId+'" style="left: 0%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight" id="termDay1-'+ l_count + '-' +_portletId+'" style="left: 14.2857%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight" id="termDay2-'+ l_count + '-' +_portletId+'" style="left: 28.5714%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight" id="termDay3-'+ l_count + '-' +_portletId+'" style="left: 42.8571%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight" id="termDay4-'+ l_count + '-' +_portletId+'" style="left: 57.1429%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight" id="termDay5-'+ l_count + '-' +_portletId+'" style="left: 71.4286%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div class="weeklyTermRight weeklyTermRightR" id="termDay6-'+ l_count + '-' +_portletId+'" style="left: 85.7143%;"><div class="weeklyTermRightTop">&nbsp;</div></div>';
                termTableHtml += '<div id="termScheduleItemGarage-' + l_count + '-' + _portletId + '" class="termScheduleGarage"> </div>'
                termTableHtml += "</div></td><td width=\"18\"><div class=\"weeklyTermTail\">&nbsp;</div></td></tr>"
                l_count++;
            });
           termTableHtml += "</tbody></table>"
           dojo.byId('termScheduleGarage-'+_portletId).innerHTML=termTableHtml;
           dojo.byId('termScheduleDivAdd_' + _portletId).style.height = (18 * (l_count + 1)) + "px";

            for(var i = 0;i < ptConfig[_portletId].scheduleDivDaySum; i++) {
               tmpNode1 = dojo.byId('weeklyDay' + i + '-' + _portletId);
               tmpNode2 = dojo.byId('weeklyHoliday' + i + '-' + _portletId);
               tmpNode3 = dojo.byId('weeklyRight' + i + '-' + _portletId);
               tmpNode4 = dojo.byId('termDay' + i + '-' + _portletId);
               tmpNode1.innerHTML = parseInt(data.date[i].substring(8,10),10) + data.dayOfWeek[i];
               tmpNode2.innerHTML = data.holiday[i];
               if (data.dayOfWeek[i] == "（土）") {
                   dojo.addClass(tmpNode1, "saturday");
                   dojo.addClass(tmpNode2, "saturday");
                   dojo.addClass(tmpNode3, "saturday");
                   dojo.addClass(tmpNode4, "saturday");
               } else {
                   dojo.removeClass(tmpNode1, "saturday");
                   dojo.removeClass(tmpNode2, "saturday");
                   dojo.removeClass(tmpNode3, "saturday");
                   dojo.removeClass(tmpNode4, "saturday");
               }
               if (data.dayOfWeek[i] == "（日）") {
                   dojo.addClass(tmpNode1, "sunday");
                   dojo.addClass(tmpNode2, "sunday");
                   dojo.addClass(tmpNode3, "sunday");
                   dojo.addClass(tmpNode4, "sunday");
               } else {
                   dojo.removeClass(tmpNode1, "sunday");
                   dojo.removeClass(tmpNode2, "sunday");
                   dojo.removeClass(tmpNode3, "sunday");
                   dojo.removeClass(tmpNode4, "sunday");
               }
               if (data.holiday[i]) {
                   dojo.addClass(tmpNode1, "holiday");
                   dojo.addClass(tmpNode2, "holiday");
                   dojo.addClass(tmpNode3, "holiday");
                   dojo.addClass(tmpNode4, "holiday");
               } else {
                   dojo.removeClass(tmpNode1, "holiday");
                   dojo.removeClass(tmpNode2, "holiday");
                   dojo.removeClass(tmpNode3, "holiday");
                   dojo.removeClass(tmpNode4, "holiday");
               }
            }

            dojo.forEach(data.schedule, function(item) {
                var rowHeight = ptConfig[_portletId].rowHeight;
                var top = item.startDateHour * rowHeight * 2 + item.startDateMinute * rowHeight / 30;
                var height = item.endDateHour * rowHeight * 2 + item.endDateMinute * rowHeight / 30 - top;
                if(height <= rowHeight) {
                    tmpHeight[count] = height;
                    height = rowHeight;
                } else {
                    tmpHeight[count] = -1;
                }
                var left = 100 /  ptConfig[_portletId].scheduleDivDaySum * item.index;
                var width = 100 / ptConfig[_portletId].scheduleDivDaySum * 0.99;
                var name = item.name;
                var startDate = tmpHeight[count] == -1 ? ((item.startDateHour > 9) ? item.startDate : "0" +item.startDate) : item.name;
                var endDate = tmpHeight[count] == -1 ? ((item.endDateHour > 9) ? item.endDate : "0" +item.endDate) : '';
                var sepalater = tmpHeight[count] == -1 ? '-' : '';
                var scheduleId = item.scheduleId;

                var str_tmp = "0";
                var str_tmpflgmb = "";
                var member_to = dojo.byId('member_to-' + _portletId);
                if(member_to) {
                     var t_o = member_to.options;
                     for(i = 0 ; i < t_o.length; i++ ) {
                       if(((item.type == "U") && (item.ownerId == t_o[i].value)) || ((item.type == "F") && (item.ownerId == t_o[i].value))){
                           str_tmp = i %  aipo.calendar.maximum_to;
                       }
                       if(item.memberList){
                           var ucount = 0;
                           var fcount = 0;
                           for (j = 0 ; j < item.memberList.length ; j ++){
                               if(item.memberList[j].charAt(0) == 'f'){
                                  fcount++;
                               }else{
                                  ucount++;
                               }
                           }
                       }
                    }
                    var str_tmpflgmb;
                    if(item.userCount > 1){
                     str_tmpflgmb   =  "[共有]";
                    }
                    if(item.facilityCount > 0){
                     str_tmpflgmb   += "[設備]";
                    }

                }

                if(!item['public']) {
                    name += '<img src="images/schedule/schedule_secret.gif" border="0" width="16" height="16" alt="非公開" title="非公開" align="top" class="icon" />';
                }
                if(item.duplicate) {
                    name += '<img src="images/schedule/schedule_duplicate.gif" border="0" width="16" height="16" alt="重複スケジュール" title="重複スケジュール" align="top" class="icon" />';
                }
                if(item.repeat) {
                    name += '<img src="images/schedule/schedule_repeat.gif" border="0" width="16" height="16" alt="繰り返し" title="繰り返し" align="top" class="icon" />';
                }
                if(item.tmpreserve) {
                    name += '<img src="images/schedule/schedule_tmpreserve.gif" border="0" width="16" height="16" alt="仮スケジュール" title="仮スケジュール" align="top" class="icon" />';
                }
                html += '<div id="schedule-' + count + '-' + _portletId+'" class="scheduleDiv color'+str_tmp+'" style="top: '+ top +'px; left: ' + left + '%; height: '+ (height-1) + 'px; width: '+ width + '%; z-index: 0; visibility: hidden;"><div class="scheduleDivFirstLine color'+str_tmp+'"><span id="scheduleDivStartTime-'+ count + '-' + _portletId + '" class="scheduleDivTime color'+str_tmp+'">' + str_tmpflgmb + startDate + '</span><span id="scheduleDivSepalater-'+ count + '-' + _portletId + '"  class="scheduleDivSepalater color'+str_tmp+'">' + sepalater + '</span><span id="scheduleDivEndTime-'+ count + '-' + _portletId + '" class="scheduleDivTime color'+str_tmp+'">' + endDate + '</span></div><div class="scheduleDivName color'+str_tmp+'">'  + name  + '</div><div class="scheduleDivLastLine color'+str_tmp+'"><center><div class="handleDiv color'+str_tmp+'" align="center">&nbsp;</div></center></div></div>';
                count++;
            });
            html += "<div id=\"dummy_div_" +  _portletId + "\" class=\"scheduleDivAdd dummy_div\" style=\" position:absolute; width: 0px; height : 0px; left: 0px; top: -10000px; Filter: Alpha(Opacity=10);opacity:.10; background-color:#FFFFFF; \">&nbsp;</div>"
            dojo.byId('scheduleGarage-' + _portletId).innerHTML = html;

            var tmpDraggable = null;
            var draggable, draggable2;
            var objs = [];
            count = 0;
            dojo.forEach(data.schedule, function(item) {
                draggable = dojo.byId('schedule-' + count + '-' + _portletId);
                var scheduleId = item.scheduleId;
                tmpDraggable = new aipo.calendar.WeeklyScheduleDraggable(draggable, {pid:_portletId , sid:'"schedule-' + count + '-' + _portletId +'"' , handle: '"dummy_div_-' + _portletId +'"' });
                aipo.calendar.objectlist.push(tmpDraggable);

                if(item.member || item.loginuser || item.owner || item.public){
                    tmpDraggable.setDraggable(true);
                } else {
                    tmpDraggable.setDraggable(false);
                }

                tmpDraggable.schedule = item;
                tmpDraggable.tmpIndex = item.index;
                tmpDraggable.count = count;
                tmpDraggable.tmpHeight = tmpHeight[count];
                tmpDraggable.position = 0;
                tmpDraggable.division = 1;
                tmpDraggable.portletId = _portletId;

                scheduleDiv[item.index].push(draggable);
                if(item.public || item.member){
                    dojo.connect(draggable,"onclick", tmpDraggable, "onScheduleClick");
                }

                dojo.connect(draggable,"onmouseover", tmpDraggable, "onScheduleOver");

                count++;
            });


            for(var i=0;i<ptConfig[_portletId].scheduleDivDaySum;i++) {
                aipo.calendar.relocation(scheduleDiv[i].length, scheduleDiv[i], 100 /  ptConfig[_portletId].scheduleDivDaySum * i);
                scheduleDiv[i] = Array();
            }
            count = 0;
             l_count = 0;
            dojo.forEach(data.termSchedule, function(itemList) {
                var item = null;
                termHtml = "";

                for(var i = 0;i < ptConfig[_portletId].scheduleDivDaySum; i++) {
                   tmpNode5 = dojo.byId('termDay' + i + '-' + l_count + '-' + _portletId);
                   if (data.dayOfWeek[i] == "（土）") {
                       dojo.addClass(tmpNode5, "saturday");
                   } else {
                       dojo.removeClass(tmpNode5, "saturday");
                   }
                   if (data.dayOfWeek[i] == "（日）") {
                       dojo.addClass(tmpNode5, "sunday");
                   } else {
                       dojo.removeClass(tmpNode5, "sunday");
                   }
                   if (data.holiday[i]) {
                       dojo.addClass(tmpNode5, "holiday");
                   } else {
                       dojo.removeClass(tmpNode5, "holiday");
                   }
                }

                for (k = 0; k < itemList.length ; k++){
                    item = itemList[k];
                    var width = 100 / ptConfig[_portletId].scheduleDivDaySum * item.rowspan;
                    var left = 100 / ptConfig[_portletId].scheduleDivDaySum * item.index;
                    var name = item.name;
                    var scheduleId = item.scheduleId;

                    var str_tmp = "0";
                    var str_tmpflgmb = "";
                    var member_to = dojo.byId('member_to-' + _portletId);
                    if(member_to) {
                         var t_o = member_to.options;
                         for(i = 0 ; i < t_o.length; i++ ) {
                           if(((item.type == "U") && (item.ownerId == t_o[i].value)) || ((item.type == "F") && (item.ownerId == t_o[i].value))){
                               str_tmp = i %  aipo.calendar.maximum_to;
                           }
                           if(item.memberList){
                               var ucount = 0;
                               var fcount = 0;
                               for (j = 0 ; j < item.memberList.length ; j ++){
                                   if(item.memberList[j].charAt(0) == 'f'){
                                      fcount++;
                                   }else{
                                      ucount++;
                                   }
                               }
                           }
                        }
                        var str_tmpflgmb;
                        if(ucount > 1){
                         str_tmpflgmb   =  "[共有]";
                        }
                        if(fcount > 0){
                         str_tmpflgmb   += "[設備]";
                        }
                    }

                    if(!item['public']) {
                        name += '<img src="images/schedule/schedule_secret.gif" border="0" width="16" height="16" alt="非公開" title="非公開" align="top" class="icon" />';
                    }
                    if(item.duplicate) {
                        name += '<img src="images/schedule/schedule_duplicate.gif" border="0" width="16" height="16" alt="重複スケジュール" title="重複スケジュール" align="top" class="icon" />';
                    }
                    if(item.repeat) {
                        name += '<img src="images/schedule/schedule_repeat.gif" border="0" width="16" height="16" alt="繰り返し" title="繰り返し" align="top" class="icon" />';
                    }
                    if(item.tmpreserve) {
                        name += '<img src="images/schedule/schedule_tmpreserve.gif" border="0" width="16" height="16" alt="仮スケジュール" title="仮スケジュール" align="top" class="icon" />';
                    }
                    if(width==100)width='99.99999';
                    termHtml += '<div id="termSchedule-' + count + '-' + _portletId +'" class="termScheduleDiv termColor'+str_tmp+'" style="left: ' + left + '%; width: '+ width + '%;"><div class="termScheduleDivHandleLeft" id="termScheduleDivHandleLeft-' + count + '-' + _portletId +'">&nbsp;</div><div class="termScheduleDivNameDiv">' + str_tmpflgmb + name + '</div><div class="termScheduleDivHandleRight" id="termScheduleDivHandleRight-' + count + '-' + _portletId +'">&nbsp;</div></div>';
                    count++;
                }
                dojo.byId('termScheduleItemGarage-' + l_count + '-' + _portletId).innerHTML = termHtml;
                l_count++;
            });

            tableLeft = dojo.byId('weeklyTermLeft_'+_portletId);

            tmpDraggable = null;
            count = 0;
            l_count = 0;
            dojo.forEach(data.termSchedule, function(itemList) {
                var item = null;
                    for (k = 0; k < itemList.length ; k++){
                        item = itemList[k];
                        var scheduleId = item.scheduleId;
                        draggable = dojo.byId('termSchedule-' + count + '-' + _portletId);
                        draggable2 = dojo.byId('termScheduleDivHandleLeft-' + count + '-' + _portletId);
                        draggable3 = dojo.byId('termScheduleDivHandleRight-' + count + '-' + _portletId);
                        tmpDraggable = new aipo.calendar.WeeklyTermScheduleDraggable(draggable,{pid:_portletId , sid:  'termSchedule-' + count + '-' + _portletId});

                        aipo.calendar.objectlist.push(tmpDraggable);
                        tmpDraggable.schedule = item;
                        tmpDraggable.scheduleNode = draggable;
                        tmpDraggable.portletId = _portletId;
                        tmpDraggable.termType = 'center';

                        dojo.connect(draggable,"onclick", tmpDraggable, "onScheduleClick");
                        draggable.style.zIndex = 1;

                        if (item.indexReal >= 0) {
                            tmpDraggable2 = new aipo.calendar.WeeklyTermScheduleDraggable(draggable2, {pid:_portletId , sid: 'termScheduleDivHandleLeft-' + count + '-' + _portletId});
                            aipo.calendar.objectlist.push(tmpDraggable2);
                            tmpDraggable2.schedule = item;
                            tmpDraggable2.scheduleNode = draggable;
                            tmpDraggable2.portletId = _portletId;
                            tmpDraggable2.termType = 'left';
                            if(item.member || item.loginuser || item.owner || item.public){
                                tmpDraggable2.setDraggable(true);
                            } else {
                                tmpDraggable2.setDraggable(false);
                            }
                        } else {
                            dojo.style(draggable2, "cursor", "pointer");
                            draggable2.style.zIndex = 1;
                        }
                        dojo.connect(draggable2,"onclick", tmpDraggable, "onScheduleClick");
                        if (item.indexReal + item.colspanReal <= ptConfig[_portletId].scheduleDivDaySum ) {
                            tmpDraggable3 = new aipo.calendar.WeeklyTermScheduleDraggable(draggable3, {pid:_portletId , sid: 'termScheduleDivHandleRight-' + count + '-' + _portletId});
                            aipo.calendar.objectlist.push(tmpDraggable3);
                            tmpDraggable3.schedule = item;
                            tmpDraggable3.scheduleNode = draggable;
                            tmpDraggable3.portletId = _portletId;
                            tmpDraggable3.termType = 'right';
                            if(item.member || item.loginuser || item.owner || item.public){
                                tmpDraggable3.setDraggable(true);
                            } else {
                                tmpDraggable3.setDraggable(false);
                            }
                        } else {
                            dojo.style(draggable3, "cursor", "pointer");
                            draggable3.style.zIndex = 1;
                        }
                        dojo.connect(draggable3,"onclick", tmpDraggable, "onScheduleClick");
                        dojo.connect(draggable,"onmouseover", tmpDraggable, "onScheduleOver");

                        if(item.member || item.loginuser || item.owner || item.public){
                            tmpDraggable.setDraggable(true);
                        } else {
                            tmpDraggable.setDraggable(false);
                        }
                        count++;
                    }
                l_count++;
            });

            obj_content = dojo.byId('content-'+_portletId);
            dojo.style(obj_content, "visibility" , "visible");
            obj_indicator = dojo.byId('indicator-'+_portletId);
            dojo.style(obj_indicator, "display" , "none")

            if (!ptConfig[_portletId].isScroll) {
                dojo.byId('weeklyScrollPane_'+_portletId).scrollTop = ptConfig[_portletId].contentScrollTop;
                ptConfig[_portletId].isScroll = true;
            }
            ptConfig[_portletId].isTooltipEnable = true;
        }
    });
};

// aipo.calendar.relocation
aipo.calendar.relocation = function(sum,scheduleDiv,scheduleDivLeft) {
    var i,j;
    var offsetW = 0.99;
    var scheduleDivWidth = 100 / 7;
    var endoverlapSchedule=0;
    var overlapNumArrayMax=0;
    var bottomLineMax=0;
    var targetNum=0;
    var overlapNumArray = new Array(sum);
    var positionLeftArray = new Array(sum);
    var resizeWidthArray = new Array(sum);

    scheduleDiv.sort(aipo.calendar.sortByRegion);


    for (i=0; i<sum; i++) {
        scheduleDiv[i].style.zIndex = i+1;
    }
    for (i=0; i<sum; i=endoverlapSchedule) {
        endoverlapSchedule = aipo.calendar.overlapSchedule(scheduleDiv,i,i,++endoverlapSchedule,sum);
        if (bottomLineMax < parseInt(dojo.getComputedStyle(scheduleDiv[i]).top)) {
            targetNum = i;
            bottomLineMax = parseInt(dojo.getComputedStyle(scheduleDiv[targetNum]).top);
            overlapNumArrayMax = 0;
        }

        for (j=targetNum; j<endoverlapSchedule; j++)
            var divBottom = parseInt(dojo.getComputedStyle(scheduleDiv[j]).top) + parseInt(dojo.getComputedStyle(scheduleDiv[j]).height);
            if (bottomLineMax < divBottom)
                bottomLineMax = divBottom;

        for (j=targetNum; j<endoverlapSchedule; j++) {
            positionLeftArray[j] = aipo.calendar.positionLeft(scheduleDiv,positionLeftArray,targetNum,j,0);
            if (positionLeftArray[j] > overlapNumArrayMax) overlapNumArrayMax = positionLeftArray[j];
        }

        for (j=targetNum; j<endoverlapSchedule; j++)
            resizeWidthArray[j] = aipo.calendar.positionRight(scheduleDiv,positionLeftArray,overlapNumArrayMax,targetNum,j);

        for (j=targetNum; j<endoverlapSchedule; j++)
            overlapNumArray[j] = overlapNumArrayMax;
    }


    for (i=0; i<sum; i++) {
        if (overlapNumArray[i] != 0) {
               if (positionLeftArray[i] < positionLeftArray[i+1])
                dojo.style(scheduleDiv[i], "width", (scheduleDivWidth * 2 / (overlapNumArray[i]+1))*0.8 * offsetW + "%");
            else if (resizeWidthArray[i]==0)
                dojo.style(scheduleDiv[i], "width", (scheduleDivWidth - (scheduleDivWidth/(overlapNumArray[i]+1))*positionLeftArray[i]) * offsetW +"%");
            else
                dojo.style(scheduleDiv[i], "width", (scheduleDivWidth - (scheduleDivWidth/(overlapNumArray[i]+1))*positionLeftArray[i] - (scheduleDivWidth*2/(overlapNumArray[i]+1))*0.2 - (scheduleDivWidth/(overlapNumArray[i]+1))*(resizeWidthArray[i]-1)) * offsetW +"%");
        }
        else
            dojo.style(scheduleDiv[i], "width", scheduleDivWidth * offsetW +"%");

        dojo.style(scheduleDiv[i], "left", (scheduleDivLeft + ((scheduleDivWidth/(overlapNumArray[i]+1))*positionLeftArray[i]))+"%");
        dojo.style(scheduleDiv[i], "visibility", "visible" );
     }
}

// aipo.calendar.overlapSchedule
aipo.calendar.overlapSchedule = function(scheduleDiv,mostLeftDiv,startNum,endNum,sum) {
    var mostLeftDivBottom = parseInt(dojo.getComputedStyle(scheduleDiv[mostLeftDiv]).top) + parseInt(dojo.getComputedStyle(scheduleDiv[mostLeftDiv]).height);
    var startNumBottom = parseInt(dojo.getComputedStyle(scheduleDiv[startNum]).top) + parseInt(dojo.getComputedStyle(scheduleDiv[startNum]).height);

    var endNumTop;
    if(scheduleDiv[endNum]){
        endNumTop = parseInt(dojo.getComputedStyle(scheduleDiv[endNum]).top);
    }else {
        endNumTop = 'NaN';
    }

    if ((endNum > sum-1) ||
        (mostLeftDivBottom < endNumTop) ||
            (startNumBottom < endNumTop))
        return endNum;
    else endNum = aipo.calendar.overlapSchedule(scheduleDiv,mostLeftDiv,endNum,++endNum,sum);
    endNum = aipo.calendar.overlapSchedule(scheduleDiv,mostLeftDiv,startNum,endNum,sum);
    return endNum;
}


// aipo.calendar.positionLeft
aipo.calendar.positionLeft = function(scheduleDiv,positionLeftArray,startNum,endNum,positionLeft) {
    var endNumTop = parseInt(dojo.getComputedStyle(scheduleDiv[endNum]).top);
    for (i=startNum; i<endNum; i++) {
        var tmpDivTop = parseInt(dojo.getComputedStyle(scheduleDiv[i]).top);
        var tmpDivBottom = tmpDivTop + parseInt(dojo.getComputedStyle(scheduleDiv[i]).height);
        if ((tmpDivTop <= endNumTop) &&
                (tmpDivBottom > endNumTop) &&
                (positionLeftArray[i]==positionLeft)) {
                positionLeft = aipo.calendar.positionLeft(scheduleDiv,positionLeftArray,startNum,endNum,++positionLeft);
        }
    }
    return positionLeft;
}

// aipo.calendar.positionRight
aipo.calendar.positionRight = function(scheduleDiv,positionLeftArray,overlapNumArrayMax,startNum,endNum) {
    var resizeWidth=0;
    var endNumTop = parseInt(dojo.getComputedStyle(scheduleDiv[endNum]).top);
    for (i=startNum; i<endNum; i++) {
        var tmpDivTop = parseInt(dojo.getComputedStyle(scheduleDiv[i]).top);
        var tmpDivBottom = tmpDivTop + parseInt(dojo.getComputedStyle(scheduleDiv[i]).height);
        if ((tmpDivTop <= endNumTop) &&
                (tmpDivBottom > endNumTop) &&
                (positionLeftArray[i]>positionLeftArray[endNum]) &&
                ((overlapNumArrayMax-positionLeftArray[i]+1)>resizeWidth)) {
                resizeWidth=overlapNumArrayMax-positionLeftArray[i]+1;
        }
    }
    return resizeWidth;
}

// aipo.calendar.sortByRegion
aipo.calendar.sortByRegion = function(a,b) {
    var aTop = parseInt(dojo.getComputedStyle(a).top);
    var bTop = parseInt(dojo.getComputedStyle(b).top);
    var aBottom = aTop + parseInt(dojo.getComputedStyle(a).height);
    var bBottom = aBottom + parseInt(dojo.getComputedStyle(b).height);
    if (aTop == bTop)
        return bBottom - aBottom;
    else return aTop - bTop;
}

// aipo.calendar.getDate
aipo.calendar.getDate = function (thisDate, days) {
    // ex: inputDate = "2007-01-01"
    //     days = "3"
    //     return = "2007-01-04"
    tmpYear = parseInt(thisDate.substring(0,4),10);
    tmpMonth = parseInt(thisDate.substring(5,7),10);
    tmpDay = parseInt(thisDate.substring(8,10),10);
    if (days > 0) {
        do {
            tmpMonthDays = aipo.calendar.getDay(tmpYear,tmpMonth);
            if (tmpDay + days <= tmpMonthDays) {
                tmpDay = tmpDay + days;
                if ((tmpMonth < 10) && (tmpDay < 10))
                    date = tmpYear + "-0" + tmpMonth + "-0" + tmpDay;
                else if ((tmpMonth < 10) && !(tmpDay < 10))
                    date = tmpYear + "-0" + tmpMonth + "-" + tmpDay;
                else if (!(tmpMonth < 10) && (tmpDay < 10))
                    date = tmpYear + "-" + tmpMonth + "-0" + tmpDay;
                else date = tmpYear + "-" + tmpMonth + "-" + tmpDay;
                days = -1;
            } else {
                days = days - (tmpMonthDays - tmpDay) - 1;
                if (tmpMonth == 12) {
                    tmpYear++;
                    tmpMonth=1;
                } else {
                    tmpMonth++;
                }
                tmpDay = 1;
            }
        } while (days >= 0)
    } else if (days < 0) {
        do {
            if (tmpDay + days > 0) {
                tmpDay = tmpDay + days;
                if ((tmpMonth < 10) && (tmpDay < 10))
                    date = tmpYear + "-0" + tmpMonth + "-0" + tmpDay;
                else if ((tmpMonth < 10) && !(tmpDay < 10))
                    date = tmpYear + "-0" + tmpMonth + "-" + tmpDay;
                else if (!(tmpMonth < 10) && (tmpDay < 10))
                    date = tmpYear + "-" + tmpMonth + "-0" + tmpDay;
                else date = tmpYear + "-" + tmpMonth + "-" + tmpDay;
                days = 1;
            } else {
                if (tmpMonth == 1) {
                    tmpYear--;
                    tmpMonth=12;
                } else {
                    tmpMonth--;
                }
                tmpMonthDays = aipo.calendar.getDay(tmpYear,tmpMonth);
                days = days + tmpDay;
                tmpDay = tmpMonthDays;
            }
        } while (days <= 0)
    } else date = thisDate;
    return date;
}

// aipo.calendar.getDay
aipo.calendar.getDay = function (year, month) {
    if ( month == 2 ) {
        if ( !(year % 4)  && ( (year % 100) || !(year % 400) ) ) return 29;
        else return 28;
    } else if ( month == 4 || month == 6 || month == 9 || month == 11 ) return 30;
    else return 31;
}

//aipo.calendar.setGridArray
aipo.calendar.setGridArray = function(_portletId, _colSize) {
    var tmpX = 0;
    if(aipo.calendar.gridArray) delete(aipo.calendar.gridArray);
    aipo.calendar.gridArray = new Array(_colSize);
    for(i = 0 ; i < _colSize ; i++){
        tmpX = dojo._abs(dojo.byId("weeklyDay"+ i +"-" + _portletId), true).x;
        aipo.calendar.gridArray[i]=tmpX;
    }
}

//aipo.calendar.getCurrentMouseX
aipo.calendar.getCurrentMouseX = function(e){
       if(aipo.calendar.gridArray == null) return {index: -1, x: 0};
       var startX = aipo.calendar.gridArray[0];
       var _tmpIndex= 0 ;
       var i;
       if(e.pageX > startX){
       var max = parseInt(aipo.calendar.gridArray.length) - 1;
           for(i = max; i > -1 ; i-- ) {
                 if(e.pageX > aipo.calendar.gridArray[i]){
                    _tmpIndex = i;
                    break;
                 }
           }
       }else {
           _tmpIndex = 0;
       }
       var _tmpX = aipo.calendar.gridArray[_tmpIndex] - startX;
       return {index: _tmpIndex, x: _tmpX};
}

aipo.calendar.onCloseMemberpicker = function( _portletId ){
    aipo.calendar.populateWeeklySchedule(_portletId);
}

aipo.calendar.showTooltip = function(url, portlet_id, containerNode) {
    var datehtml = "";
    var mbhtml = "";
    var mbfhtml = "";
    var placehtml = "";

    var escapeHTML = function(value) {
        var replaceChars = function(ch) {
            switch (ch) {
                case "<":
                    return "&lt;";
                case ">":
                    return "&gt;";
                case "&":
                    return "&amp;";
                case "'":
                    return "&#39;";
                case '"':
                    return "&quot;";
            }
            return "?";
        };
        return String(value).replace(/[<>&"']/g, replaceChars);
    };
    dojo.style(containerNode, "display", "block");
    dojo.xhrGet({
        portletId: portlet_id,
        url: url,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        load: function(data, event) {
            if (!data.id) {
                dojo.style(containerNode, "display", "none");
                return;
            }

            if (!data.isSpan) {
                datehtml = "<span style=\"font-size: 0.90em;\">" + data.date + "</span><br/>";
            }

            if (data.memberList) {
                var memberSize = data.memberList.length;
                for (var i = 0 ; i < memberSize ; i++) {
                    mbhtml += "<li>" + escapeHTML(data.memberList[i].aliasName.value) + "</li>";
                }
            }

            if (data.facilityList) {
                var facilitySize = data.facilityList.length;
                for (var i = 0 ; i < facilitySize ; i++) {
                    mbfhtml += "<li>" + escapeHTML(data.facilityList[i].facilityName.value) + "</li>";
                }
            }

            if(data.place != ""){
                placehtml = "<span style=\"font-size: 0.90em;\">場所</span><br/><ul><li>" + data.place + "</li></ul>";
            }

            if(mbhtml != ""){
                mbhtml = "<span style=\"font-size: 0.90em;\">参加者</span><br/><ul>" + mbhtml + "</ul>";
            }

            if(mbfhtml != ""){
                mbfhtml = "<span style=\"font-size: 0.90em;\">設備</span><br/><ul>" + mbfhtml + "</ul>";
            }

            var tooltiphtml = "<h4>" + data.name + "</h4>" + datehtml + mbhtml + mbfhtml + placehtml;

            containerNode.innerHTML = tooltiphtml;
        }
    });
};

dojo.declare("aipo.calendar.DummyDivObject", null, {
     portletId: null,
     parentnode: null,
     draggable: null,
     TooltipObject: null,
     constructor: function(node, params){
        this.portletId = params.pid;
        this.parentnode = params.node;
        this.node = dojo.byId(node);
        this.events = [
            dojo.connect(this.node, "onmousedown", this, "onMouseDown"),
            dojo.connect(this.node, "onmouseover", this, "onMouseOver"),
        ];
     },
     onMouseDown: function(e){
        this.hide();
        if(this.parentnode == null || this.parentnode == "undefined"){return;}
        if(this.draggable){this.draggable.onMouseDown(e);}
     },
     onMouseOver: function(e){
        if(this.parentnode == null || this.parentnode == "undefined"){return;}
     },
     destroy: function(){
        dojo.forEach(this.events, dojo.disconnect);
        this.events = this.node = this.handle = null;
    },
    hide: function(){
        dojo.marginBox (this.node,{ l: 0, t: -10000, w: 0, h: 0 });
    }
});

// aipo.calendar.WeeklyScheduleDragMoveObject
dojo.declare("aipo.calendar.WeeklyScheduleDragMoveObject", [aimluck.dnd.DragMoveObject], {
    _rowHeight_: 18,
    isResize: false,
    distance: 3,
    lastScroll: 0,
    onFirstMove: function(e){
        if(this.dragSource.TooltipObject != null){
           this.dragSource.TooltipObject.uninitialize();
        }

        var tmpDraggable = dojo.clone(this.node);
        tmpDraggable.id = 'schedule-dummy-' + this.portletId;
        tmpDraggable.style.zIndex = 998;
        dojo.style(tmpDraggable, "opacity", 0.0);
        var garage = dojo.byId('scheduleGarage-' + this.portletId);
        garage.appendChild(tmpDraggable);

        this.tmpDraggable = tmpDraggable;

        dojo.connect(this.node, "onmousedown", this, "onMouseDown");

        if (dojo.isIE) {
            document.onkeydown = function(e) {
                dojo.style(tmpDraggable, "opacity", 0.3);
            };
            document.onkeyup = function(e) {
                dojo.style(tmpDraggable, "opacity", 0.0);
            };
        } else {
            dojo.connect(null, "onkeydown", this, "onKeyPress");
            dojo.connect(null, "onkeyup", this, "onKeyPress");
        }

        aimluck.dnd.DragMoveObject.prototype.onFirstMove.apply(this, arguments);
        dojo.style(this.node, "opacity", 0.5);
        this.node.style.zIndex = 999;
        this.startY = this._pageY;
        this.startAbsoluteY = dojo._abs(dojo.byId(this.node), true).y;

        //Google Chrome及びSafari、Firefox3.6以降ではdojo._absの挙動が異なるので、AbsoluteYを修正する
        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") > -1 || (dojo.isFF && (dojo.isFF >= 3.6))) {
            this.startAbsoluteY += window.scrollY;     // ページスクロール分を修正
        } else if(userAgent.indexOf("safari") > -1) {
            this.startAbsoluteY -= dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop;     // DIVタグスクロール分を修正
        }

        this.startHeight = parseInt(dojo.getComputedStyle(this.node).height);
        this.startTop = parseInt(dojo.getComputedStyle(this.node).top);
        if(this.startHeight - 6 < this.startY-this.startAbsoluteY) {
            this.isResize = true;
        }
        aipo.calendar.setGridArray(this.portletId, parseInt(ptConfig[this.portletId].scheduleDivDaySum));
        lastScroll = dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop;
    },
    onKeyPress: function(e){
        if(e.ctrlKey) {
            dojo.style(this.tmpDraggable, "opacity", 0.3);
        } else {
            dojo.style(this.tmpDraggable, "opacity", 0.0);
        }
    },
    onMouseMove: function(e){
        if(this.dragSource.isDraggable == false) return;

        aimluck.dnd.DragMoveObject.prototype.onMouseMove.apply(this, arguments);
        this.dragSource.schedule.isDrag = true;
        if(this.dragSource.tmpHeight > 3) {
            dojo.style(this.node, "height", this.dragSource.tmpHeight + "px");
            this.dragSource.tmpHeight = 3;
        }
        var distance = ptConfig[this.portletId].distance;
        var distance_scr = dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop - lastScroll;

        this.leftTop.t = Math.floor((this.leftTop.t + distance_scr )/distance)*distance;
        if (this.isResize) {
            if(- this.startTop + this.leftTop.t + this.startHeight < 0) {
                dojo.style(this.node, "height", "0px");
                this.leftTop.t += this.startHeight;
            } else {
                var rh;
                if (this.leftTop.t + this.startHeight > 864 ) {
                    rh = 864 - this.startTop - 3;
                } else {
                    rh =  - this.startTop + this.leftTop.t + this.startHeight;
                }
                this.leftTop.t = this.startTop;
                this.leftTop.h = parseInt(rh) -1;
            }
        } else {
            if(!this.disableY) {
              if ( this.leftTop.t < 0 ) this.leftTop.t = 0;
              if ( this.leftTop.t + this.startHeight > 864)
                this.leftTop.t = 864 - this.startHeight - 6;
            }
        }
        if(!this.disableX) {
           mouseX = aipo.calendar.getCurrentMouseX(e);
           this.leftTop.l = mouseX.x;
           this.dragSource.schedule.index = mouseX.index;
        }
        dojo.marginBox(this.node, this.leftTop);
        var tmpTop = parseInt(dojo.getComputedStyle(this.node).top);
        var tmpHeight = parseInt(dojo.getComputedStyle(this.node).height)+1;
        var quotient = tmpTop/distance;
        var hour = Math.floor(quotient/12);
        var minute = Math.floor(quotient%12);



        hour = (hour > 9)? hour : "0" + hour;
        minute = (minute > 1) ? minute*(60/12): "0" + minute*(60/12);
        var id = this.dragSource.count;
        dojo.byId('scheduleDivStartTime-'+ id + '-' + this.portletId).innerHTML = hour + ':'+ minute;
        this.dragSource.schedule.startDateHour = hour;
        this.dragSource.schedule.startDateMinute = minute;
        this.dragSource.schedule.startDate = hour + ':'+ minute;

        quotient += tmpHeight/distance;
        hour = Math.floor(quotient/12);
        minute = Math.floor(quotient%12);

        hour = (hour > 9)? hour : "0" + hour;
        minute = (minute > 1) ? minute*(60/12): "0" + minute*(60/12);

        dojo.byId('scheduleDivEndTime-'+ id + '-' + this.portletId).innerHTML = hour + ':'+ minute;
        this.dragSource.schedule.endDateHour = hour;
        this.dragSource.schedule.endDateMinute = minute;
        this.dragSource.schedule.endDate = hour + ':'+ minute;
        dojo.byId('scheduleDivSepalater-'+ id + '-' + this.portletId).innerHTML = '-';
        return;
    },
    onMouseUp: function (e) {
        ptConfig[this.portletId].isTooltipEnable = true;

        if (dojo.isIE) {
            document.onkeydown = "";
            document.onkeyup = "";
        }

        if(this.dragSource.schedule.isDrag != true){
            dojo.style(this.node, "opacity", 1.0 );
            aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
            if(this.dragSource){this.dragSource.onScheduleClick(e);}
            return;
        }

        var tmpHeight = parseInt(dojo.getComputedStyle(this.node).height);
        if(tmpHeight < ptConfig[this.portletId].rowHeight) {
            dojo.style(this.node, "height", ptConfig[this.portletId].rowHeight+ "px");
            this.dragSource.tmpHeight = tmpHeight;
        } else {
            this.dragSource.tmpHeight = -1;
        }

        var params = "";
        if(e.ctrlKey) {
            params += "&mode=insert";
        } else {
            params += "&mode=update";
        }
        params += "&entityid="+ this.dragSource.schedule.scheduleId;
        params += "&view_start=" + ptConfig[this.portletId].jsonData.date[0].substring(0, 10);

        if(this.dragSource.schedule.repeat) {
            params += "&edit_repeat_flag=1";
            params += "&view_date=" + ptConfig[this.portletId].jsonData.date[this.dragSource.tmpIndex].substring(0, 10);
        }

        params += "&start_date=" + ptConfig[this.portletId].jsonData.date[this.dragSource.schedule.index].substring(0, 11) + this.dragSource.schedule.startDateHour + '-' + this.dragSource.schedule.startDateMinute;
        params += "&end_date=" + ptConfig[this.portletId].jsonData.date[this.dragSource.schedule.index].substring(0, 11) + this.dragSource.schedule.endDateHour + '-' + this.dragSource.schedule.endDateMinute;

        aipo.calendar.populateWeeklySchedule(this.portletId, params);
        aipo.portletReload('schedule', this.portletId);

        aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
         this.dragSource.destroy();
    }
});

// aipo.calendar.WeeklyScheduleDraggable
dojo.declare("aipo.calendar.WeeklyScheduleDraggable", [aimluck.dnd.Draggable], {
    DragMoveObject: aipo.calendar.WeeklyScheduleDragMoveObject,
    isDraggable: false,
    scheduleObjId: null,
    constructor: function(node, params){
        this.scheduleObjId = params.sid;
    },
    onMouseDown: function(e){
        ptConfig[this.portletId].isTooltipEnable = false;
        if(!!aipo.calendar.dummyDivObj && !!aipo.calendar.dummyDivObj.TooltipObject)aipo.calendar.dummyDivObj.TooltipObject.close();
        aimluck.dnd.Draggable.prototype.onMouseDown.apply(this, arguments);
    },
    onScheduleClick: function(e) {
        if(this.schedule.isDrag || !this.isDraggable) {
            return;
        }
        var uid = this.schedule.ownerId;

        aipo.common.showDialog(ptConfig[this.portletId].detailUrl + "&entityId=" + this.schedule.scheduleId + "&view_date=" + ptConfig[this.portletId].jsonData.date[this.schedule.index] + "&userid=" + uid, this.portletId, aipo.schedule.onLoadScheduleDetail );
        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
        aipo.schedule.tmpScroll = parseInt(dojo.byId('weeklyScrollPane_'+this.portletId)["scrollTop"]);
        //**//
    },
    onScheduleOver: function(e) {
        if(ptConfig[this.portletId].isTooltipEnable == false){return;}
        var ttdiv = dojo.byId("dummy_div_" + this.portletId);
        var left =  dojo.getComputedStyle(this.node).left ;
        var top =  dojo.getComputedStyle(this.node).top;
        var width =  dojo.getComputedStyle(this.node).width;
        var height =  dojo.getComputedStyle(this.node).height;
        dojo.marginBox (ttdiv,dojo._getMarginBox(this.node,{ l: left, t: top, w: width, h: height }));
        ttdiv.style.zIndex = this.node.style.zIndex ;
        ttdiv.style.height = (parseInt(height) - 6) + "px";
        if(!aipo.calendar.dummyDivObj){
            aipo.calendar.dummyDivObj = new aipo.calendar.DummyDivObject(ttdiv , {pid: this.scheduleObjId , node: this.node});
        }else{
            aipo.calendar.dummyDivObj.parentnode = this.node;
        }
        aipo.calendar.dummyDivObj.draggable = this;

        if(aipo.calendar.dummyDivObj.TooltipObject){
            aipo.calendar.dummyDivObj.TooltipObject.destroyRecursive();
            aipo.calendar.dummyDivObj.TooltipObject = null;
        }
        // IPADではツールチップ非表示
        if (scheduleTooltipEnable) {
          this.setupTooltip();
        }
    },
    setupTooltip: function() {
        var schedule_id = this.schedule.scheduleId;
        var view_date = ptConfig[this.portletId].jsonData.endDate;
        aipo.calendar.dummyDivObj.TooltipObject = new aipo.widget.ToolTip({
            label: "<div class='indicator'>読み込み中...</div>",
            connectId: ["dummy_div_" + this.portletId]
        }, this.portletId, function(containerNode, node){
            var request_url = ptConfig[this.portletId].jsonUrl.split("?")[0] + "?template=ScheduleDetailJSONScreen&view_date="+view_date+"&scheduleid="+schedule_id;

            aipo.calendar.showTooltip(request_url, this.portletId, containerNode);
        });
    },
    setDraggable: function(flag){
        this.isDraggable = flag;
    }
});

/*
*/

// aipo.calendar.WeeklyTermScheduleDragMoveObject
dojo.declare("aipo.calendar.WeeklyTermScheduleDragMoveObject", [aimluck.dnd.DragMoveObject], {
    positionFrom: -1,
    positionTo: -1,
    moveIndex: 0,
    onFirstMove: function(e){
        if(this.dragSource.TooltipObject != null){
               this.dragSource.TooltipObject.uninitialize();
        }
       aimluck.dnd.DragMoveObject.prototype.onFirstMove.apply(this, arguments);
       dojo.style(this.node, "opacity", 0.5);
       aipo.calendar.setGridArray(this.portletId, parseInt(ptConfig[this.portletId].scheduleDivDaySum));

       var tmpDraggable = dojo.clone(this.node);
       tmpDraggable.id = 'schedule-dummy-' + this.portletId;
       tmpDraggable.style.zIndex = 998;
       dojo.style(tmpDraggable, "opacity", 0.0);

       var garage = dojo.byId(this.node.parentNode.id);
       garage.appendChild(tmpDraggable);

       this.tmpDraggable = tmpDraggable;

       if (dojo.isIE) {
           document.onkeydown = function(e) {
               dojo.style(tmpDraggable, "opacity", 0.3);
           };
           document.onkeyup = function(e) {
               dojo.style(tmpDraggable, "opacity", 0.0);
           };
       } else {
           dojo.connect(null, "onkeydown", this, "onKeyPress");
           dojo.connect(null, "onkeyup", this, "onKeyPress");
       }
    },
    onKeyPress: function(e){
        if(e.ctrlKey) {
            dojo.style(this.tmpDraggable, "opacity", 0.3);
        } else {
            dojo.style(this.tmpDraggable, "opacity", 0.0);
        }
    },
    onMouseMove: function(e){
        if(this.dragSource.isDraggable == false) return;

        aimluck.dnd.DragMoveObject.prototype.onMouseMove.apply(this, arguments);
        this.dragSource.schedule.isDrag = true;
        var distance = ptConfig[this.portletId].distance;
        var mouseX = aipo.calendar.getCurrentMouseX(e);
        _tmpIndex = mouseX.index;
        /*y = Math.floor(y/distance)*distance;
        if(!this.disableY) { this.node.style.top = y + "px"; }*/
        if(!this.disableX) {
            var tmpSchedule = this.dragSource.schedule;
            var type = this.dragSource.termType;
            var scheduleNode = this.dragSource.scheduleNode;
            var tmpW, tmpL;
            if (type == "center") {
                if(this.positionFrom == -1 && _tmpIndex != -1) {
                    this.positionFrom = _tmpIndex;
                    this.positionTo = this.positionFrom;
                }
                if(this.positionTo != -1 && _tmpIndex != -1) {
                    this.positionTo = _tmpIndex;
                }
                this.moveIndex = - this.positionFrom + this.positionTo;
                tmpL = tmpSchedule.indexReal + this.moveIndex;
                tmpW = tmpSchedule.colspanReal;
                var tmpS = ptConfig[this.portletId].scheduleDivDaySum;
                if (tmpW + tmpL > tmpS) {
                    if (tmpL < 0) {
                        tmpW = tmpS;
                    } else {
                        tmpW = tmpS - tmpL;
                    }
                } else if (tmpL < 0) {
                    tmpW = tmpW + tmpL;
                }

                if (tmpL < 0) {
                    tmpL = 0;
                }
            } else if(type == "left") {
                if(this.positionFrom == -1) {
                    this.positionFrom = tmpSchedule.index;
                    this.positionTo = tmpSchedule.index;
                }
                if(this.positionTo != -1 && _tmpIndex != -1) {
                    this.positionTo = _tmpIndex;
                }
                this.moveIndex = - this.positionFrom + this.positionTo;
                if (this.positionTo >= this.positionFrom + tmpSchedule.colspanReal) {
                    tmpL = tmpSchedule.indexReal+ tmpSchedule.rowspan - 1;
                    tmpW = this.positionTo - this.positionFrom - tmpSchedule.colspanReal + 2;
                } else {
                    tmpL = this.positionTo;
                    tmpW = tmpSchedule.rowspan + this.positionFrom - this.positionTo;
                }

            } else {
                if(this.positionFrom == -1) {
                    this.positionFrom = tmpSchedule.index;
                    this.positionTo = tmpSchedule.index;
                }
                if(this.positionTo != -1 && _tmpIndex != -1 && this._tmpIndex != -1) {
                    this.positionTo = _tmpIndex;
                }
                this.moveIndex =   - tmpSchedule.index - tmpSchedule.rowspan + this.positionTo + 1;
                if (this.positionTo <= this.positionFrom) {
                    tmpL = this.positionTo;
                    tmpW = this.positionFrom - this.positionTo + 1;
                } else {
                    tmpL = tmpSchedule.index;
                    tmpW = this.positionTo - tmpSchedule.index + 1;
                }
            }
            var width = 100 / ptConfig[this.portletId].scheduleDivDaySum * tmpW;
            var left = 100 / ptConfig[this.portletId].scheduleDivDaySum * tmpL;
            dojo.style(scheduleNode, "left",  left + "%");
            dojo.style(scheduleNode, "width", width + "%");
        }
    },
    onMouseUp: function (e) {
        ptConfig[this.portletId].isTooltipEnable = true;

        if (dojo.isIE) {
            document.onkeydown = "";
            document.onkeyup = "";
        }

        if(this.dragSource.schedule.isDrag != true){
            dojo.style(this.node, "opacity", 1.0 );
            aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
            return;
        }
        var tmpSchedule = this.dragSource.schedule;
        var viewStart = ptConfig[this.portletId].jsonData.date[0].substring(0, 10);
        var type = this.dragSource.termType;
        var scheduleNode = this.dragSource.scheduleNode;
        var startDate, endDate;
        if (type == 'center') {
            startDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + this.moveIndex) + "-00-00";
            endDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + this.moveIndex + tmpSchedule.colspanReal-1) + "-00-00";
        } else if (type == 'left') {
            if (tmpSchedule.colspanReal - this.moveIndex > 0) {
                startDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + this.moveIndex) + "-00-00";
                endDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + tmpSchedule.colspanReal - 1) + "-00-00";
            } else {
                startDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + tmpSchedule.colspanReal - 1) + "-00-00";
                endDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + this.moveIndex) + "-00-00";
            }
        } else {
            if (tmpSchedule.colspanReal + this.moveIndex > 0) {
                startDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal) + "-00-00";
                endDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + tmpSchedule.colspanReal + this.moveIndex-1) + "-00-00";
            } else {
                startDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal + tmpSchedule.colspanReal + this.moveIndex-1) + "-00-00";
                endDate = aipo.calendar.getDate(viewStart, tmpSchedule.indexReal) + "-00-00";

            }
        }

        this.positionFrom = -1;
        this.positionTo = -1;
        this.moveIndex = 0;
        this.tmpIndex = 0;

        var params = "";
        if(e.ctrlKey) {
            params += "&mode=insert";
        } else {
            params += "&mode=update";
        }
        params += "&is_span=TRUE";
        params += "&entityid="+ this.dragSource.schedule.scheduleId;
        params += "&view_start=" + viewStart;
        params += "&start_date=" + startDate;
        params += "&end_date=" + endDate;

        aipo.calendar.populateWeeklySchedule(this.portletId, params);

        aipo.portletReload('schedule', this.portletId);
        aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
    }
});

// aipo.calendar.WeeklyTermScheduleDraggable
dojo.declare("aipo.calendar.WeeklyTermScheduleDraggable", [aimluck.dnd.Draggable], {
    DragMoveObject: aipo.calendar.WeeklyTermScheduleDragMoveObject,
    isDraggable: false,
    TooltipObject: null,
    scheduleObjId: null,
    isDraggable: false,
    constructor: function(node, params){
        this.scheduleObjId = params.sid;
    },
    onMouseDown: function(e){
        ptConfig[this.portletId].isTooltipEnable = false;
        if(this.TooltipObject)this.TooltipObject.close();
        aimluck.dnd.Draggable.prototype.onMouseDown.apply(this, arguments);
    },
    onScheduleClick: function(e) {
        if(this.schedule.isDrag || !this.isDraggable) {
            return;
        }
        var uid = this.schedule.ownerId;
        aipo.common.showDialog(ptConfig[this.portletId].detailUrl + "&entityId=" + this.schedule.scheduleId + "&view_date=" + ptConfig[this.portletId].jsonData.date[this.schedule.index]  + "&userid=" + uid, this.portletId, aipo.schedule.onLoadScheduleDetail);
        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
        aipo.schedule.tmpScroll = parseInt(dojo.byId('weeklyScrollPane_'+this.portletId)["scrollTop"]);
        //**//
    },
    onScheduleOver: function(e) {
        if(ptConfig[this.portletId].isTooltipEnable == false){return;}
        /*
        var ttdiv = dojo.byId("dummy_div_" + this.portletId);
        var left =  dojo.getComputedStyle(this.node).left ;
        var top =  dojo.getComputedStyle(this.node).top;
        var width =   dojo.getComputedStyle(this.node).width;
        var height =  dojo.getComputedStyle(this.node).height;
        dojo.marginBox (ttdiv,dojo._getMarginBox(this.node,{ l: left, t: top, w: width, h: height }));
        ttdiv.style.zIndex = this.node.style.zIndex ;

        if(!aipo.calendar.dummyDivObj){
            aipo.calendar.dummyDivObj = new aipo.calendar.DummyDivObject(ttdiv , {pid: this.portletId , node: this.node});
        }else{
            aipo.calendar.dummyDivObj.parentnode = this.node;
        }
        aipo.calendar.dummyDivObj.draggable = this;
        */
        // IPADではツールチップ非表示
        if (scheduleTooltipEnable) {
          this.setupTooltip();
        }
    },
    setupTooltip: function() {
        var schedule_id = this.schedule.scheduleId;
        var view_date = ptConfig[this.portletId].jsonData.endDate;
        if(!this.TooltipObject){
            this.TooltipObject = new aipo.widget.ToolTip({
                label: "<div class='indicator'>読み込み中...</div>",
                connectId: [this.node.id]
            }, this.portletId, function(containerNode, node){
                var request_url = ptConfig[this.portletId].jsonUrl.split("?")[0] + "?template=ScheduleDetailJSONScreen&view_date="+view_date+"&scheduleid="+schedule_id;

                aipo.calendar.showTooltip(request_url, this.portletId, containerNode);
            });
        }
        aipo.calendar.objectlist.push(this.TooltipObject);
    },
    setDraggable: function(flag){
        this.isDraggable = flag;
    }
});


// aipo.calendar.WeeklyScheduleAddDragMoveObject
dojo.declare("aipo.calendar.WeeklyScheduleAddDragMoveObject", [aimluck.dnd.DragMoveObject], {
   _rowHeight_: 18,
    positionFrom: 0,
    positionTo: 0,
    _isDragging: false,
    lastScroll: 0,
    onMouseDown: function(e){
       this._isDragging = false;
       aimluck.dnd.DragMoveObject.prototype.onMouseDown.apply(this, arguments);
    },
    onFirstMove: function(e){
        this.startY = this.dragSource._lastY;
        this.startAbsoluteY = dojo._abs(dojo.byId(this.node), true).y;
        this.startX =  dojo.getComputedStyle(this.node).left;

        //Google Chrome及びSafari、Firefox/3.6ではdojo._absの挙動が異なるので、AbsoluteYを修正する
        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") > -1 || (dojo.isFF && (dojo.isFF >= 3.6))) {
            this.startAbsoluteY += window.scrollY;     // ページスクロール分を修正
        } else if(userAgent.indexOf("safari") > -1) {
            this.startAbsoluteY -= dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop;     // DIVタグスクロール分を修正
        }

        lastScroll = dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop;
        aimluck.dnd.DragMoveObject.prototype.onFirstMove.apply(this, arguments);
    },
    onMouseMove: function(e){
        aimluck.dnd.DragMoveObject.prototype.onMouseMove.apply(this, arguments);
        this._isDragging = true;
        var distance_scr = dojo.byId('weeklyScrollPane_'+this.portletId).scrollTop - lastScroll;

        var quotient = Math.floor((this.startY-this.startAbsoluteY) / this._rowHeight_);
        var currentQuotient = Math.floor((this.startY+this.leftTop.t-this.startAbsoluteY+distance_scr) / this._rowHeight_);
        var nextTop = 0;
        var nextHeight = 0;
        if( currentQuotient < quotient) {
            nextTop = currentQuotient*this._rowHeight_  +1;
            nextHeight = (quotient - currentQuotient+1)*this._rowHeight_;
            this.positionFrom = currentQuotient;
            this.positionTo= quotient+1;
        } else {
            nextTop = quotient*this._rowHeight_  +1;
            nextHeight = (currentQuotient - quotient+1)*this._rowHeight_;
            this.positionTo = currentQuotient+1;
            this.positionFrom = quotient;
        }

        if (nextTop + nextHeight > 864 ) {
             nextHeight = 864 - nextTop - this._rowHeight_;
             this.positionTo = 47;
        }

        this.leftTop.t = nextTop;
        this.leftTop.l = this.startX;
        this.leftTop.h = nextHeight;
        dojo.marginBox(this.node, this.leftTop);

        dojo.style(this.node, "opacity", 0.5);
    },
    onMouseUp: function(e) {
        if(!this._isDragging){
            /** ドラッグでなく、クリックされた場合 */
            this.onFirstMove(e);
            this.onMouseMove(e);
        }
        var hour = Math.floor(this.positionFrom/2);
        hour = (hour > 9) ? hour : "0" + hour;
        var minute = Math.floor(this.positionFrom%2)*30;
        var date = ptConfig[this.portletId].jsonData.date[this.dragSource.index].substring(0, 10);
        var startTime = date + '-' + hour + '-' + minute;

        hour = Math.floor(this.positionTo/2);
        hour = (hour > 9) ? hour : "0" + hour;
        minute = Math.floor(this.positionTo%2)*30;
        var endTime = date + '-' + hour + '-' + minute;

        this.node.style.top = "0px";
        this.node.style.height = "864px";
        dojo.style(this.node, "opacity", 0.0 );
        if(this._isDragging == true){
            aipo.common.showDialog(ptConfig[this.portletId].formUrl + "&entityid=new&mode=form"  + "&form_start=" + startTime + '&form_end=' + endTime , this.portletId , aipo.schedule.onLoadScheduleDialog);
        }
        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
        aipo.schedule.tmpScroll = parseInt(dojo.byId('weeklyScrollPane_'+this.portletId)["scrollTop"]);

       this._isDragging = false;
       aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
    }
});

// aipo.calendar.WeeklyScheduleAddDraggable
dojo.declare("aipo.calendar.WeeklyScheduleAddDraggable", [aimluck.dnd.Draggable], {
    DragMoveObject: aipo.calendar.WeeklyScheduleAddDragMoveObject,
    constructor: function(node, params){
        this.index = params.idx;
    }
});

/*
*/

// aipo.calendar.WeeklyTermScheduleAddDragMoveObject
dojo.declare("aipo.calendar.WeeklyTermScheduleAddDragMoveObject", [aimluck.dnd.DragMoveObject], {
    _rowHeight_: 18,
    positionFrom: -1,
    positionTo: -1,
    _isDragging: false,
    scheduleObjId: null,
    onMouseDown: function(e){
       this._isDragging = false;
       aimluck.dnd.DragMoveObject.prototype.onMouseDown.apply(this, arguments);
    },
    onFirstMove: function(e){
           aimluck.dnd.DragMoveObject.prototype.onFirstMove.apply(this, arguments);
           aipo.calendar.setGridArray(this.portletId, parseInt(ptConfig[this.portletId].scheduleDivDaySum));
    },
    onMouseMove: function(e){
        aimluck.dnd.DragMoveObject.prototype.onMouseMove.apply(this, arguments);
        this._isDragging = true;
        dojo.style(this.node, "opacity", 0.5);
        var mouseX = aipo.calendar.getCurrentMouseX(e);
        var tmpIndex = mouseX.index;
        if(this.positionFrom == -1 && tmpIndex != -1) {
            this.positionFrom = tmpIndex;
            this.positionTo = this.positionFrom;
        }
        if(this.positionTo != -1 && tmpIndex != -1) {
            this.positionTo = tmpIndex;
        }
        if(this.positionTo != -1 && this.positionFrom != -1) {
            var tmpW, tmpL;
            if(this.positionTo > this.positionFrom) {
                tmpL = this.positionFrom;
                tmpW = this.positionTo - this.positionFrom + 1;
            } else {
                tmpL = this.positionTo;
                tmpW = this.positionFrom - this.positionTo + 1;
            }
            var width = 100 / ptConfig[this.portletId].scheduleDivDaySum * tmpW;
            var left = 100 / ptConfig[this.portletId].scheduleDivDaySum * tmpL;
            dojo.style(this.node, "left",  left + "%");
            dojo.style(this.node, "width", width + "%");

        } else {
            dojo.style(this.node, "left",  0 + "%");
            dojo.style(this.node, "width", 0 + "%");
        }
    },
    onMouseUp: function (e) {
        if(!this._isDragging){
            /** ドラッグでなく、クリックされた場合 */
            this.onFirstMove(e);
            this.onMouseMove(e);
        }
        var left1, left2;
        if (this.positionTo != -1 && this.positionFrom != -1) {
            if(this.positionTo > this.positionFrom) {
                left1 = this.positionFrom;
                left2 = this.positionTo;
            } else {
                left2 = this.positionFrom;
                left1 = this.positionTo;
            }
            var date1 = ptConfig[this.portletId].jsonData.date[left1];
            var date2 = ptConfig[this.portletId].jsonData.date[left2];
            if(this._isDragging == true){
                aipo.common.showDialog(ptConfig[this.portletId].formUrl + "&entityid=new&mode=form&is_span=TRUE"  + "&form_start=" + date1 + '&form_end=' + date2 ,this.portletId , aipo.schedule.onLoadScheduleDialog );
            }
            //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
            aipo.schedule.tmpScroll = parseInt(dojo.byId('weeklyScrollPane_'+this.portletId)["scrollTop"]);
        }
        this.positionFrom = -1;
        this.positionTo = -1;
        dojo.style(this.node, "left",  0 + "%");
        dojo.style(this.node, "width", 100 + "%");
        dojo.style(this.node, "opacity", 0.0 );

        aimluck.dnd.DragMoveObject.prototype.onMouseUp.apply(this, arguments);
    }
});

// aipo.calendar.WeeklyTermScheduleAddDraggable
dojo.declare("aipo.calendar.WeeklyTermScheduleAddDraggable", [aimluck.dnd.Draggable], {
    DragMoveObject: aipo.calendar.WeeklyTermScheduleAddDragMoveObject,
    constructor: function(node, params){
        this.index = params.idx;
    }
});

}

}});