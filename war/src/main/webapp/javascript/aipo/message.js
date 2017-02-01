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
dojo.provide("aipo.message");

aipo.message.currentRoomId = null;
aipo.message.tmpMessageId = null;
aipo.message.tmpRoomId = null;
aipo.message.tmpPortletTitle = null;
aipo.message.currentUserId = null;
aipo.message.currentGroupName = "all";
aipo.message.currentRoomSearchKeyword = null;
aipo.message.currentUserSearchKeyword = null;
aipo.message.currentMessageSearchKeyword = null;
aipo.message.moreMessageLock = false;
aipo.message.isActive = true;
aipo.message.portletId = null;
aipo.message.jslink = null;
aipo.message.isMobile = false;
aipo.message.isInit = false;
aipo.message.isDirect = false;
aipo.message.jumpCursor = null;
aipo.message.isSearched = false;
aipo.message.transactionIdList = [];


aipo.message.setup = function(portletId, jslink, isMobile) {
    aipo.message.portletId = portletId;
    aipo.message.jslink = jslink;
    aipo.message.isMobile = isMobile;
}

aipo.message.init = function(portletId, jslink, isMobile) {
    aipo.message.portletId = portletId;
    aipo.message.jslink = jslink;
    aipo.message.isMobile = isMobile;
    if(!aipo.message.isMobile) {
       dojo.connect(window, "onresize", null, function(e) {
           aipo.message.fixMessageWindow();
       });
    }
    dojo.connect(window, "onfocus", null, function(e) {
        aipo.message.isActive = true;
        if (aipo.message.isOpenWindow()
                && aipo.message.currentRoomId && !aipo.message.moreMessageLock) {
            aipo.message.latestMessageList();
        }
    });
    dojo.connect(window, "onblur", null, function(e) {
        aipo.message.isActive = false;
        //
    });
    var messagePane = dojo.byId("messagePane");
    if (messagePane) {
        dojo
                .connect(aipo.message.isMobile ? window : messagePane, "onscroll", null,
                        function(e) {
		                	if (aipo.message.isMobile && window.scrollY + document.documentElement.clientHeight
                                    + 50 + 44 >= document.body.scrollHeight && !aipo.message.moreMessageLock) {
		        				aipo.message.moreMessageList();
		        			}
                			else if (e.target.scrollTop + messagePane.clientHeight
                                    + 100 >= e.target.scrollHeight
                                    && !aipo.message.moreMessageLock) {
                                aipo.message.moreMessageList();
                            }
                        });
    }
    var messageRightPane = dojo.byId("messageRightPane");
    if (messageRightPane) {
        dojo
                .connect(aipo.message.isMobile ? window : messageRightPane, "onscroll", null,
                        function(e) {
		                	if (aipo.message.isMobile && window.scrollY + document.documentElement.clientHeight
                                    + 50 + 44 >= document.body.scrollHeight && !aipo.message.moreMessageLock) {
		        				aipo.message.moreMessageRightList();
		        			}
                			else if (e.target.scrollTop + messageRightPane.clientHeight
                                    + 100 >= e.target.scrollHeight
                                    && !aipo.message.moreMessageLock) {
                                aipo.message.moreMessageRightList();
                            }
                        });
    }
    if ("" != aipo.message.getCookieIsLastRoomOrUser()) {
    	if ("User" == aipo.message.getCookieIsLastRoomOrUser()) {

    	    //Userを選んだ時もCookieから最後に選んだUserへのフォームが開いたままにしておくことができます。
    	    aipo.message.currentUserId = aipo.message.getCookieRoomId("targetUserId=");
    	    aipo.message.reloadRoomList(null, aipo.message.currentUserId);
    	    aipo.message.selectTab("user");
    	    aipo.message.selectUser(aipo.message.currentUserId);
    	    currentRoomId = aipo.message.getCookieRoomId("targetUserId=");

    	    }else if ("Room" == aipo.message.getCookieIsLastRoomOrUser()){

    		//以下2行によりルームをリロード後もエラーなしで表示したままにできます。
    	    aipo.message.currentRoomId = aipo.message.getCookieRoomId("lastRoomId=");
    	    aipo.message.reloadRoomList(null,aipo.message.currentRoomId);
    	    aipo.message.selectTab("room");
    	    aipo.message.selectRoom(aipo.message.currentRoomId);
    	    currentRoomId = aipo.message.getCookieRoomId("lastRoomId=");

    	    }
    }else{
        aipo.message.reloadRoomList();
    }
    aipo.message.isInit = true;
}

aipo.message.messagePane = null;
aipo.message.reloadMessageList = function() {
    if (!aipo.message.messagePane) {
        aipo.message.messagePane = dijit.byId("messagePane");
        aipo.message.messagePane = new aimluck.widget.Contentpane({},
                'messagePane');
        aipo.message.messagePane.onLoad = function() {
        	if(aipo.message.jumpCursor) {
        		var pane = dojo.byId("messagePane");
        		var active = dojo.byId("message" + aipo.message.jumpCursor);
        		if(pane && active) {
        			if(aipo.message.isMobile) {
        			    window.scrollTo(0, active.offsetTop);
        			} else {
        				var max = pane.scrollHeight - pane.clientHeight;
        			    aipo.message.scrollTo(pane, max < active.offsetTop - 50 ? max : active.offsetTop - 50, 100);
        			}
        		    dojo.addClass(active, "active");
        		}
                aipo.message.jumpCursor = null;
        	} else {
        		//メッセージのタブを開いている場合と、スマホ表示のメッセージ画面の場合
        		if(!dojo.byId("dd_message") || dojo.hasClass("dd_message", "open")){
        			aipo.message.read(aipo.message.currentRoomId);
        		}
        	}
            aipo.message.fixDateLine();
        }
    }

    dojo.byId("messagePane").innerHTML = '<div class="loader"><i class="indicator"></i></div>';
    var screen = aipo.message.jslink + "?template=MessageListScreen";
    if (aipo.message.currentRoomId) {
        screen += "&r=" + aipo.message.currentRoomId;
    } else if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }
    if(aipo.message.jumpCursor) {
        screen += "&c=" + aipo.message.jumpCursor;
        screen += "&jump=1";
    }
    //メッセージのタブを開いている場合と、スマホ表示のメッセージ画面の場合はデータを既読に更新する
   if (!dojo.byId("dd_message") || dojo.hasClass("dd_message", "open") ){
    	screen += "&is_read=T";
    }else{
    	screen += "&is_read=F";
    }


    aipo.message.moreMessageLock = false;
    aipo.message.messagePane.viewPage(screen);

}

aipo.message.roomMemberPane = null;
aipo.message.reloadRoomMemberList = function() {
    if (!aipo.message.roomMemberPane) {
        aipo.message.roomMemberPane = dijit.byId("roomMemberPane");
        aipo.message.roomMemberPane = new aimluck.widget.Contentpane({},
                'roomMemberPane');
        aipo.message.roomMemberPane.onLoad = function() {

        }
    }

    dojo.byId("roomMemberPane").innerHTML = '';
    var screen = aipo.message.jslink + "?template=MessageRoomMemberListScreen";
    if (aipo.message.currentRoomId) {
        screen += "&r=" + aipo.message.currentRoomId;
    } else if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }
    aipo.message.roomMemberPane.viewPage(screen);
}
aipo.message.moreMessageList = function() {
    var screen = aipo.message.jslink + "?template=MessageListScreen";
    if (aipo.message.currentRoomId) {
        screen += "&r=" + aipo.message.currentRoomId;
    } else if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }
    var cursor = aipo.message.getLastMessageId();
    if (cursor) {
        aipo.message.moreMessageLock = true;
        screen += "&c=" + cursor;
        screen += "&js_peid=" + aipo.message.portletId;
        dojo.xhrGet({
            url : screen,
            timeout : 30000,
            encoding : "utf-8",
            handleAs : "text",
            headers : {
                X_REQUESTED_WITH : "XMLHttpRequest"
            },
            load : function(response, ioArgs) {
                var messagePane = dojo.byId("messagePane");
                if(messagePane) {
                    messagePane.innerHTML += response;
                    if(messagePane.children.length > 1) {
                        var emptyMessage = dojo.query("#messagePane .emptyMessage");
                        if(emptyMessage.length == 1) {
                            emptyMessage[0].parentNode.removeChild(emptyMessage[0]);
                        }
                    }
                    aipo.message.fixDateLine();
                }
                aipo.message.moreMessageLock = false;
            },
            error : function(error) {
                aipo.message.moreMessageLock = false;
            }
        });
    }
}

aipo.message.latestMessageList = function() {
    var screen = aipo.message.jslink + "?template=MessageListScreen";
    if (aipo.message.currentRoomId) {
        screen += "&r=" + aipo.message.currentRoomId;
    } else if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }
    var cursor = aipo.message.getFirstMessageId();
    if(!cursor) {
        cursor = 0;
    }
    aipo.message.moreMessageLock = true;
    screen += "&c=" + cursor;
    screen += "&latest=1";
    screen += "&js_peid=" + aipo.message.portletId;
    dojo.xhrGet({
        url : screen,
        timeout : 30000,
        encoding : "utf-8",
        handleAs : "text",
        headers : {
            X_REQUESTED_WITH : "XMLHttpRequest"
        },
        load : function(response, ioArgs) {
            var messagePane = dojo.byId("messagePane");
            if(messagePane) {
                messagePane.innerHTML = response + messagePane.innerHTML;
                if(messagePane.children.length > 1) {
                    var emptyMessage = dojo.query("#messagePane .emptyMessage");
                    if(emptyMessage.length == 1) {
                        emptyMessage[0].parentNode.removeChild(emptyMessage[0]);
                    }
                }
                aipo.message.fixDateLine();
            }
            aipo.message.moreMessageLock = false;
            aipo.message.reloadRoomList();
        },
        error : function(error) {
            aipo.message.moreMessageLock = false;
        }
    });
}

aipo.message.moreMessageRightList = function() {
    var screen = aipo.message.jslink + "?template=MessageSearchListScreen";
    var cursor = aipo.message.getLastMessageRightId();
    if (cursor) {
        aipo.message.moreMessageLock = true;
        screen += "&c=" + cursor;
        screen += "&js_peid=" + aipo.message.portletId;
        screen += "&k=" + encodeURIComponent(aipo.message.currentMessageSearchKeyword);
        dojo.xhrGet({
            url : screen,
            timeout : 30000,
            encoding : "utf-8",
            handleAs : "text",
            headers : {
                X_REQUESTED_WITH : "XMLHttpRequest"
            },
            load : function(response, ioArgs) {
                var messageRightPane = dojo.byId("messageRightPane");
                if(messageRightPane) {
                	messageRightPane.innerHTML += response;
                    if(messageRightPane.children.length > 1) {
                        var emptyMessage = dojo.query("#messageRightPane .emptyMessage");
                        if(emptyMessage.length == 1) {
                            emptyMessage[0].parentNode.removeChild(emptyMessage[0]);
                        }
                    }
                    //aipo.message.fixDateLine();
                }
                aipo.message.moreMessageLock = false;
            },
            error : function(error) {
                aipo.message.moreMessageLock = false;
            }
        });
    }
}

aipo.message.messageRoomListPane = null;
aipo.message.reloadRoomList = function(roomId, userId) {
    if (!aipo.message.messageRoomListPane) {
        aipo.message.messageRoomListPane = dijit.byId("messageRoomListPane");
        aipo.message.messageRoomListPane = new aimluck.widget.Contentpane({},
                'messageRoomListPane');
        aipo.message.messageRoomListPane.onLoad = function() {
            aipo.message.fixMessageWindow();
            var messageTotalUnreadCountValue = dojo
                    .byId("messageTotalUnreadCountValue");
            var count = parseInt(messageTotalUnreadCountValue.innerHTML);
            if (count != NaN) {
                aipo.menu.message.count(count);
                aipo.menu.updateTitle();
            }
            if (aipo.message.messageRoomListPane.roomId) {
                aipo.message
                        .selectRoom(aipo.message.messageRoomListPane.roomId);
                aipo.message.messageRoomListPane.roomId = null;
            }
            if (aipo.message.messageRoomListPane.userId) {
                var messageCurrentRoomValue = dojo
                        .byId("messageCurrentRoomValue");
                var currentRoomId = parseInt(messageCurrentRoomValue.innerHTML);
                if (currentRoomId != NaN) {
                    aipo.message.selectRoom(currentRoomId);
                } else {
                    aipo.message.selectRoom(0);
                }
                aipo.message.messageRoomListPane.userId = null;
            }
            if(aipo.message.isDirect) {
            	aipo.message.reloadUserList();
            }
        }
    }

    if (roomId) {
        aipo.message.messageRoomListPane.roomId = roomId;
    }
    if (userId) {
        aipo.message.messageRoomListPane.userId = userId;
    }

    var screen = aipo.message.jslink + "?template=MessageRoomListScreen";
    if (aipo.message.currentRoomId) {
        screen += "&r=" + aipo.message.currentRoomId;
    } else if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }
    if (aipo.message.currentRoomSearchKeyword) {
        aipo.message.messageRoomListPane.setParam("k", aipo.message.currentRoomSearchKeyword);
    }
    aipo.message.messageRoomListPane.viewPage(screen);
}

aipo.message.messageRightPane = null;
aipo.message.reloadSearchMessageList = function(roomId, userId) {
    if (!aipo.message.messageRightPane) {
        aipo.message.messageRightPane = dijit.byId("messageRightPane");
        aipo.message.messageRightPane = new aimluck.widget.Contentpane({},
                'messageRightPane');
        aipo.message.messageRightPane.onLoad = function() {
            aipo.message.fixMessageWindow();
        }
    }

    var screen = aipo.message.jslink + "?template=MessageSearchListScreen";
    if (aipo.message.currentMessageSearchKeyword) {
        aipo.message.messageRightPane.setParam("k", aipo.message.currentMessageSearchKeyword);
    }
    aipo.message.messageRightPane.viewPage(screen);
}

aipo.message.searchRoomList = function(form) {
    aipo.message.currentRoomSearchKeyword = form.keyword.value;
    aipo.message.reloadRoomList();
}
aipo.message.searchMessageList = function(form) {
	if(!form.keyword.value) {
		return;
	}
    aipo.message.currentMessageSearchKeyword = form.keyword.value;
    dojo.byId("messageRightPane").innerHTML = '<div class="loader"><i class="indicator"></i></div>';
    aipo.message.openRightBlock();
    aipo.message.reloadSearchMessageList();
    if (aipo.message.isMobile) {
    	form.keyword.blur();
    }
}

aipo.message.openRightBlock = function(){
    var messageSideBlock = dojo.byId("messageSideBlock");
    var messageMainBlock = dojo.byId("messageMainBlock");
    var messageMainBlockEmpty = dojo.byId("messageMainBlockEmpty");
    var messageRightBlock = dojo.byId("messageRightPane");
    if (messageMainBlock　&& !aipo.message.isMobile) {
        messageMainBlock.style["margin"] = "0 360px 0 280px";
    }
    if (messageMainBlockEmpty && !aipo.message.isMobile) {
    	messageMainBlockEmpty.style["margin"] = "0 360px 0 280px";
    }
    if(messageRightBlock) {
    	messageRightBlock.style.display="";
    }
    if(messageSideBlock && aipo.message.isMobile) {
    	messageSideBlock.style.display="none";
    }
    aipo.message.isSearched = true;
}

aipo.message.swapRightRoom = function(){
    var icon = dojo.byId("messageRightRoomSwapIcon");
    if(dojo.hasClass(icon,"icon-chevron-down")) {
    	dojo.removeClass(icon, "icon-chevron-down");
    	dojo.addClass(icon, "icon-chevron-up");
    	dojo.forEach(dojo.query(".messageRightRoom"), function(item) {item.style.display="none"});
    } else {
    	dojo.removeClass(icon, "icon-chevron-up");
        dojo.addClass(icon, "icon-chevron-down");
    	dojo.forEach(dojo.query(".messageRightRoom"), function(item) {item.style.display=""});
    }
}

aipo.message.swapRightUser = function(){
    var icon = dojo.byId("messageRightUserSwapIcon");
    if(dojo.hasClass(icon,"icon-chevron-down")) {
    	dojo.removeClass(icon, "icon-chevron-down");
    	dojo.addClass(icon, "icon-chevron-up");
    	dojo.forEach(dojo.query(".messageRightUser"), function(item) {item.style.display="none"});
    } else {
    	dojo.removeClass(icon, "icon-chevron-up");
        dojo.addClass(icon, "icon-chevron-down");
    	dojo.forEach(dojo.query(".messageRightUser"), function(item) {item.style.display=""});
    }
}

aipo.message.swapRightMessage = function(){
    var icon = dojo.byId("messageRightMessageSwapIcon");
    if(dojo.hasClass(icon,"icon-chevron-down")) {
    	dojo.removeClass(icon, "icon-chevron-down");
    	dojo.addClass(icon, "icon-chevron-up");
    	dojo.forEach(dojo.query(".messageRightMessage"), function(item) {item.style.display="none"});
    } else {
    	dojo.removeClass(icon, "icon-chevron-up");
        dojo.addClass(icon, "icon-chevron-down");
    	dojo.forEach(dojo.query(".messageRightMessage"), function(item) {item.style.display=""});
    }
}
aipo.message.closeRightBlock = function(){
    var messageSideBlock = dojo.byId("messageSideBlock");
    var messageMainBlock = dojo.byId("messageMainBlock");
    var messageMainBlockEmpty = dojo.byId("messageMainBlockEmpty");
    var messageRightBlock = dojo.byId("messageRightPane");
    if (messageMainBlock && !aipo.message.isMobile) {
        messageMainBlock.style["margin"] = "0px 0px 0px 280px";
    }
    if (messageMainBlockEmpty && !aipo.message.isMobile) {
    	messageMainBlockEmpty.style["margin"] = "0px 0px 0px 280px";
    }
    if(messageRightBlock) {
    	messageRightBlock.style.display="none"
    }
    if(messageSideBlock && aipo.message.isMobile) {
    	messageSideBlock.style.display=""
    }
    aipo.message.isSearched = false;
}

aipo.message.clearSearchMessageList = function() {
    var messageSearchForm = dojo.byId("messageSearchForm");
    if(messageSearchForm) {
    	messageSearchForm.keyword.value = "";
    	aipo.message.currentMessageSearchKeyword = null;
    	aipo.message.closeRightBlock();
    	aipo.message.onBlurSearch();
    }
}

aipo.message.clearSearchRoomList = function() {
    var messageRoomSearchForm = dojo.byId("messageRoomSearchForm");
    if(messageRoomSearchForm) {
        messageRoomSearchForm.keyword.value = "";
        aipo.message.searchRoomList(messageRoomSearchForm);
    }
}

aipo.message.messageUserListPane = null;
aipo.message.reloadUserList = function(group_name) {
    if (!aipo.message.messageUserListPane) {
        aipo.message.messageUserListPane = dijit.byId("messageUserListPane");
        aipo.message.messageUserListPane = new aimluck.widget.Contentpane({},
                'messageUserListPane');
        aipo.message.messageUserListPane.onLoad = function() {
            aipo.message.fixMessageWindow();
            if(aipo.message.isDirect) {
            	aipo.message.isDirect = false;
            	aipo.message.selectUser(aipo.message.currentUserId);
            	dojo.byId("messageUserlist").scrollTop = dojo.byId("messageUser" + aipo.message.currentUserId).offsetTop - dojo.byId("messageUserlist").offsetTop
            }
        }
    }

    if (group_name) {
        aipo.message.currentGroupName = group_name;
    }

    var screen = aipo.message.jslink + "?template=MessageUserListScreen&target_group_name="
            + aipo.message.currentGroupName;
    if (aipo.message.currentUserSearchKeyword) {
        aipo.message.messageUserListPane.setParam("k", aipo.message.currentUserSearchKeyword);
    }
    if (aipo.message.currentUserId) {
        screen += "&u=" + aipo.message.currentUserId;
    }

    aipo.message.messageUserListPane.viewPage(screen);
}

aipo.message.searchUserList = function() {
    var messageUserGroupSelect = dojo.byId("messageUserGroupSelect");
    var messageUserSearchForm = dojo.byId("messageUserSearchForm");
    //aipo.message.currentUserSearchKeyword = messageUserSearchForm.keyword.value;
    aipo.message
            .reloadUserList(messageUserGroupSelect.options[messageUserGroupSelect.selectedIndex].value);
}

aipo.message.clearSearchUserList = function() {
    var messageUserSearchForm = dojo.byId("messageUserSearchForm");
    if(messageUserSearchForm) {
        messageUserSearchForm.keyword.value = "";
        aipo.message.searchUserList();
    }
}

aipo.message.updateReadCount = function(roomId) {
    if(roomId == aipo.message.currentRoomId) {
        var url = aipo.message.jslink + "?template=MessageReadCountListJSONScreen";
        url += "&r=" + roomId;
        url += "&max=" + aipo.message.getFirstMessageId();
        url += "&min=" + aipo.message.getLastMessageId();
        url += "&js_peid=" + aipo.message.portletId;
        dojo.xhrGet({
            url : url,
            timeout : 30000,
            encoding: "utf-8",
            handleAs: "json-comment-filtered",
            headers : {
                X_REQUESTED_WITH : "XMLHttpRequest"
            },
            load : function(response, ioArgs) {
                for(read in response) {
                    var messageReadCount = dojo.byId("messageReadCount" + read);
                    if(messageReadCount) {
                        if(response[read] == -1 || response[read] == "-1") {
                            messageReadCount.innerHTML = aimluck.io.escapeText("message_val_all_read");
                        } else {
                            messageReadCount.innerHTML = response[read] + aimluck.io.escapeText("message_val_read");
                        }
                    }
                }
            },
            error : function(error) {

            }
        });
    }
}

aipo.message.updateUnreadCount = function() {
    var url = aipo.message.jslink + "?template=MessageUnreadCountJSONScreen";
    dojo.xhrGet({
        url : url,
        timeout : 30000,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        headers : {
            X_REQUESTED_WITH : "XMLHttpRequest"
        },
        load : function(response, ioArgs) {
            var unreadCount = response.unreadCount;
            if(!isNaN(unreadCount)) {
                aipo.menu.message.count(unreadCount);
                aipo.menu.updateTitle();
            }
        },
        error : function(error) {

        }
    });
}

aipo.message.swapView = function() {
    if (dojo.byId("portletsBody") && dojo.byId("dd_message")) {
        if (dojo.hasClass("dd_message", "open")) {
            for(var key in aipo.schedule.scrollPositions){
            	aipo.schedule.scrollPositions[key] = parseInt(dojo.byId('weeklyScrollPane_'+key)["scrollTop"]);
            }
            dojo.byId("portletsBody").style.display = "none";
            var copyright = dojo.byId("copyright");
            if(copyright) {
            	copyright.style.display = "none";
            }
            aipo.message.fixMessageWindow();
            aipo.message.focusInput();
            if (aipo.message.isOpenWindow()
                    && aipo.message.currentRoomId && !aipo.message.moreMessageLock) {
                aipo.message.latestMessageList();
            }
             aipo.message.selectRoom(aipo.message.currentRoomId);
        } else {
            dojo.byId("portletsBody").style.display = "";
            var copyright = dojo.byId("copyright");
            if(copyright) {
            	copyright.style.display = "";
            }
            for(var key in aipo.schedule.scrollPositions) {
            	if(aipo.schedule.scrollPositions[key] != null) {
            	    dojo.byId('weeklyScrollPane_'+key).scrollTop = aipo.schedule.scrollPositions[key];
            	}
            }
            for(var key in aipo.schedule.scrollNeeds) {
            	aipo.calendar.populateWeeklySchedule(aipo.schedule.scrollNeeds[key]);
            }
            while(aipo.schedule.scrollNeeds.length > 0){
             	aipo.schedule.scrollNeeds.pop();
            }
        }
    }
}

//ルームかユーザーか選択している
aipo.message.selectTab = function(tab) {
    var messageRoomTab = dojo.byId("messageRoomTab");
    var messageUserTab = dojo.byId("messageUserTab");
    var messageRoomContents = dojo.byId("messageRoomContents");
    var messageUserContents = dojo.byId("messageUserContents");

    if ("room" == tab) {
        if(aipo.message.currentUserId && !aipo.message.tmpRoomId) {
            aipo.message.unselectRoom();
        }
        aipo.message.currentUserId = null;
        if(aipo.message.tmpRoomId) {
            aipo.message.selectRoom(aipo.message.tmpRoomId);
            aipo.message.tmpRoomId = null;
        }

        dojo.query(".messageUserlist li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });

        dojo.addClass(messageRoomTab, "active");
        dojo.removeClass(messageUserTab, "active");
        dojo.addClass(messageRoomContents, "active");
        dojo.removeClass(messageUserContents, "active");
        dojo.byId("messageRoomSetting").style.display = "";
    }

    if ("user" == tab) {
        dojo.query(".messageUserlist li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });

        dojo.addClass(messageUserTab, "active");
        dojo.removeClass(messageRoomTab, "active");
        dojo.addClass(messageUserContents, "active");
        dojo.removeClass(messageRoomContents, "active");

        aipo.message.reloadUserList();

    }
}

//Messageの右側のRoomの列からRoomを選択されたときに呼ばれています。
aipo.message.inputHistory = {};
aipo.message.selectRoom = function(room_id, scroll) {
    var messageSideBlock = dojo.byId("messageSideBlock");
    var messageMainBlock = dojo.byId("messageMainBlock");
    var messageMainBlockEmpty = dojo.byId("messageMainBlockEmpty");
    var messageRightBlock = dojo.byId("messageRightPane");
    var messageForm = dojo.byId("messageForm");
    var messageRoom = dojo.byId("messageRoom" + room_id);
    var messageRoomType = dojo.byId("messageRoomType" + room_id);
    var messageRoomAuthority = dojo.byId("messageRoomAuthority" + room_id);
    var messageRoomAvatar = dojo.byId("messageRoomAvatar");
    var messageRoomName = dojo.byId("messageRoomName");
    var messageSearchForm = dojo.byId("messageSearchForm");

    if (messageForm && messageRoom) {
        if(aipo.message.isMobile) {
            dojo.removeClass(document.body, "messageRoomList");
        }
        messageMainBlock.style.display = "";
        if(messageMainBlockEmpty) {
            messageMainBlockEmpty.style.display = "none";
        } else {
            messageSideBlock.style.display = "none";
        }
        aipo.message.inputHistory[aipo.message.currentRoomId] = messageForm.message.value;
        aipo.message.currentRoomId = room_id;
        dojo.query(".messageSummary li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });
        if(!aipo.message.isMobile) {
            dojo.addClass(messageRoom, "active");
        }
        if (aipo.message.inputHistory[aipo.message.currentRoomId]) {
            aipo.message
                    .changeInput(aipo.message.inputHistory[aipo.message.currentRoomId]);
        } else {
            aipo.message.clearInput();
        }

        if(aipo.message.isMobile) {
            var roomTitle = dojo.query("#messageRoom" + room_id + " .name");
            if(roomTitle && roomTitle[0]) {
                if(!aipo.message.tmpPortletTitle) {
                    aipo.message.tmpPortletTitle = dojo.byId("portletTitle").innerHTML;
                }
                dojo.byId("portletTitle").innerHTML = roomTitle[0].innerHTML;
            }
            if(messageRightBlock) {
            	messageRightBlock.style.display="none"
            }
            if(messageSearchForm) {
            	messageSearchForm.style.display="none"
            }
        }

        dojo.style(dojo.byId("messageInputAttachment"), "display", "none");
        dojo.byId("attachments_global-" + aipo.message.portletId).innerHTML="";

        if (room_id == 0 && aipo.message.currentUserId) {
            messageForm.roomId.value = 0;
            messageForm.userId.value = aipo.message.currentUserId;
            dojo.byId("messageRoomSetting").style.display = "none";
        } else {
            messageForm.userId.value = 0;
            messageForm.roomId.value = aipo.message.currentRoomId;
            dojo.byId("messageRoomSetting").style.display = "";
        }

		var pane = dojo.byId("messageSummary");
		if(scroll && pane) {
			var max = pane.scrollHeight - pane.clientHeight;
		    aipo.message.scrollTo(pane, max < messageRoom.offsetTop ? max : messageRoom.offsetTop, 100);
		}

        aipo.message.fixMessageWindow();
        aipo.message.reloadRoomMemberList();
        aipo.message.reloadMessageList();
        if (dojo.hasClass("messageRoomTab", "active")) {
        	aipo.message.saveCookieIsLastRoomOrUser("Room");
        } else if (dojo.hasClass("messageUserTab", "active")) {
        	aipo.message.saveCookieIsLastRoomOrUser("User");
        }
        //cookieへ保存する名前とIDを引数に取ってと保存する。
        aipo.message.saveCookieTargetId("lastRoomId",aipo.message.currentRoomId);
    }
}

aipo.message.unselectRoom = function() {
    var messageMainBlock = dojo.byId("messageMainBlock");
    var messageMainBlockEmpty = dojo.byId("messageMainBlockEmpty");
    var messageRightBlock = dojo.byId("messageRightPane");
    var messageForm = dojo.byId("messageForm");
    var messageSearchForm = dojo.byId("messageSearchForm");
    if (messageForm) {
        if(aipo.message.isMobile) {
            dojo.addClass(document.body, "messageRoomList");
            if(messageSearchForm) {
            	messageSearchForm.style.display=""
            }
        }
        dojo.query(".messageSummary li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });
        dojo.query(".messageUserlist li").forEach(function(item) {
            dojo.removeClass(item, "active")
        });
        aipo.message.clearInput();
        messageMainBlock.style.display = "none";

        if(aipo.message.isMobile) {
            if(aipo.message.tmpPortletTitle) {
                dojo.byId("portletTitle").innerHTML = aipo.message.tmpPortletTitle;
            }
            if(messageMainBlockEmpty) {
                messageMainBlockEmpty.style.display = aipo.message.isSearched ? "none" : "";
            } else {
                messageSideBlock.style.display = aipo.message.isSearched ? "none" : "";
            }
            if(messageRightBlock) {
            	messageRightBlock.style.display = aipo.message.isSearched ? "" : "none";
            }
        } else {
            if(messageMainBlockEmpty) {
                messageMainBlockEmpty.style.display = "";
            }
        }

        aipo.message.currentRoomId = null;
        aipo.message.currentUserId = null;

    }
}

aipo.message.selectUser = function(user_id) {
    var messageForm = dojo.byId("messageForm");
    var messageUser = dojo.byId("messageUser" + user_id);
    if (messageForm && messageUser) {
        if(aipo.message.currentRoomId) {
            aipo.message.tmpRoomId = aipo.message.currentRoomId;
        }
        aipo.message.currentRoomId = 0;
        aipo.message.currentUserId = user_id;
        aipo.message.inputHistory[aipo.message.currentRoomId] = messageForm.message.value;
        aipo.message.inputHistory[0] = "";
        dojo.query(".messageUserlist li").forEach(function(item) {
            dojo.removeClass(item, "active");
        });
        if(!aipo.message.isMobile) {
            dojo.addClass(messageUser, "active");
        }
        aipo.message.reloadRoomList(null, user_id);
        //ここでUserIdを保存
        aipo.message.saveCookieTargetId("targetUserId", user_id);
    }
}

aipo.message.changeInput = function(value) {
    var messageForm = dojo.byId("messageForm");
    if (messageForm) {
        messageForm.message.value = value;
        aipo.message.resizeInput(messageForm.message);
        aipo.message.focusInput();
    }
}

aipo.message.clearInput = function() {
    var messageForm = dojo.byId("messageForm");
    if (messageForm) {
        messageForm.message.value = "";
        aipo.message.resizeInput(messageForm.message);
        aipo.message.focusInput();
        dojo.byId('messageFormDiv').innerHTML = "";
    }
}

aipo.message.focusInput = function() {
    var messageForm = dojo.byId("messageForm");
    if (messageForm && !aipo.message.isMobile) {
    	try{
    		messageForm.message.focus();
    	}catch(e){
    		//ignore
    	}
    }
}

aipo.message.fixMessageWindow = function() {
    if(aipo.message.isMobile) {
        return;
    }
    var messageForm = dojo.byId("messageForm");
    if (dojo.byId("dd_message") != null) {
        var minusH = 55 + 40 + 35 + 10 + 12;
        var w = dojo.byId("wrapper").clientWidth - 20;
        var h = document.documentElement.clientHeight - minusH;
        var tabh = document.documentElement.clientHeight - (minusH + 55);
        dojo.byId("dd_message").style.width = w + "px";
        if(dojo.byId("messageSideBlock1") != null) {
            dojo.byId("messageSideBlock1").style.height = h + "px";
        }
        if(dojo.byId("messageSideBlock2") != null) {
            dojo.byId("messageSideBlock2").style.height = h + "px";
        }
        if(dojo.byId("messageSummary") != null) {
            dojo.byId("messageSummary").style.height = tabh + "px";
        }
        if(dojo.byId("messageUserlist") != null) {
            dojo.byId("messageUserlist").style.height = tabh + "px";
        }
    }
    if (dojo.byId("messagePane") != null) {
        var minusH = 173;
        if(messageForm) {
        	minusH += messageForm.clientHeight;
        }
        var h = document.documentElement.clientHeight - minusH;
        dojo.byId("messagePane").style.height = h + "px";
    }
};

aipo.message.onLoadMessageRoomDialog = function() {
    aipo.widget.MemberFilterList.setup("memberfilterlist", "init_memberlist", "member_to", "member_authority_to");
    aipo.message.changeMember();
};

aipo.message.changeMember = function() {
    var node = dojo.byId("memberFieldDisplay");
    if (node) {
        var HTML = "";
        var m_t = dojo.byId("member_to");
        if (m_t) {
            var t_o = m_t.options;
            to_size = t_o.length;
            for (i = 0; i < to_size; i++) {
                var text = t_o[i].text.replace(/&/g, "&amp;").replace(/"/g,
                        "&quot;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
                HTML += "<span>" + text + "</span>";
                if (i < to_size - 1) {
                    HTML += ",<wbr/>";
                }
            }
        }
        node.innerHTML = HTML;
    }

    aipo.message.setWrapperHeight();
}

aipo.message.toggleMemberSelect = function(bool){
    var node = dojo.byId("memberField");
    var buttonOn = dojo.byId("memberSelectButtonOn");
    var buttonOff = dojo.byId("memberSelectButtonOff");
    if(bool) {
        dojo.style(buttonOn, "display" , "none");
        dojo.style(buttonOff, "display" , "block");
        dojo.style(node, "display" , "block");
    } else {
        dojo.style(buttonOn, "display" , "block");
        dojo.style(buttonOff, "display" , "none");
        dojo.style(node, "display" , "none");
    }
    aipo.message.setWrapperHeight();
}


aipo.message.onReceiveMessage = function(msg) {
    if (!msg["error"]) {
        aimluck.io.disableForm(dojo.byId("messageForm"), false);
        if(!aipo.message.moreMessageLock){
            aipo.message.latestMessageList();
        }
        aipo.message.clearInput();
        dojo.byId("messagePane").scrollTop = 0;
        dojo.style(dojo.byId("messageInputAttachment"), "display", "none");
        dojo.byId("attachments_global-" + aipo.message.portletId).innerHTML="";
        if(dojo.hasClass("messageUserTab","active")) {
            var tmpRoomId = parseInt(msg["params"]);
            if (tmpRoomId != NaN) {
                aipo.message.currentRoomId = tmpRoomId;
                aipo.message.currentUserId = null;
                aipo.message.tmpRoomId = null;
                aipo.message.reloadRoomList();
            }
            aipo.message.selectTab("room");
            aipo.message.getCookieIsLastRoomOrUser("Room");
            aipo.message.saveCookieIsLastRoomOrUser("Room");
            aipo.message.saveCookieTargetId("lastRoomId",aipo.message.currentRoomId);
        }
    }
    else if (dojo.byId('messageFormDiv')){
    	dojo.byId('messageFormDiv').innerHTML = msg["error"];
    }
};

aipo.message.onReceiveMessageRoom = function(msg) {
    if (!msg["error"]) {
        var arrDialog = dijit.byId("modalDialog");
        if (arrDialog) {
            arrDialog.hide();
            aipo.message.currentUserId = null;
            var tmpRoomId = parseInt(msg["params"]);
            if (tmpRoomId != NaN) {
                aipo.message.reloadRoomList(tmpRoomId);
            } else {
                aipo.message.reloadRoomList();
            }
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg["error"];
    }
};

aipo.message.onReceiveMessageRoomDelete = function(msg) {
    if (!msg) {
        var arrDialog = dijit.byId("modalDialog");
        if (arrDialog) {
            arrDialog.hide();
            aipo.message.unselectRoom();
            aipo.message.reloadRoomList();
        }
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML =msg;
    }
};

aipo.message.removeNode = function(id) {
	var message = dojo.byId("message" + id);
	if(message) {
		message.parentNode.removeChild(message);
	}
}

aipo.message.onReceiveMessageDelete = function(msg) {
	if (!msg) {
		if (aipo.message.tmpMessageId) {
			aipo.message.removeNode(aipo.message.tmpMessageId);
			aipo.message.tmpMessageId = null;
		} else {
			aipo.message.reloadMessageList();
		}
	}
    if (dojo.byId('messageListDiv')) {
        dojo.byId('messageListDiv').innerHTML =msg;
	}
};

aipo.message.setWrapperHeight = function() {
    var modalDialog = document.getElementById('modalDialog');
    if (modalDialog) {
        var wrapper = document.getElementById('wrapper');
        wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

aipo.message.resizeInput = function(input) {
    var shadowVal = input.value.replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/&/g, '&amp;').replace(/\n$/, '<br/>&nbsp;').replace(
                    /\n/g, '<br/>').replace(/ {2,}/g, function(space) {
                var result = "";
                var i = 0;
                while (i < space.length) {
                    result += '&nbsp;';
                    i++;
                }
                return result;
            });

    var shadowDiv = document.createElement("div");
    shadowDiv.id = "shadow-message"
    shadowDiv.style.position = "absolute";
    shadowDiv.style.top = "-1000";
    shadowDiv.style.left = "-1000";
    shadowDiv.style.border = "0";
    shadowDiv.style.outline = "0";
    shadowDiv.style.fontSize = "14px";
    shadowDiv.style.lineHeight = "1.38";
    shadowDiv.style.height = "auto";
    shadowDiv.style.resize = "none";
    shadowDiv.cols = "10";
    shadowDiv.innerHTML = shadowVal + "あ";

    var objBody = document.getElementsByTagName("body").item(0);

    objBody.appendChild(shadowDiv);
    var objShadow = dojo.byId("shadow-message");
    objShadow.style.width = input.offsetWidth + "px";

    var shadowHeight = objShadow.offsetHeight;
    // 文字サイズを14pxに設定したため、"あ"の高さが18→20に変更
    if (shadowHeight < 20) {
        shadowHeight = 20;
    }
    // 最大10行
    input.style.overflow = "hidden";
    if (shadowHeight > 20*10) {
        shadowHeight = 20*10;
        input.style.overflow = "auto";
        input.style.overflowX = "hidden";
    }
    var changed = input.style.height != shadowHeight * 1.0 + 23 + "px";
    input.style.height = shadowHeight * 1.0 + 23 + "px";
    objBody.removeChild(shadowDiv);

    if(changed) {
        aipo.message.fixMessageWindow();
    }
}

aipo.message.onPaste = function(input) {
    setTimeout(function() {
        aipo.message.resizeInput(input);
    }, 100);
}

aipo.message.inputHandle = {};
aipo.message.onFocus = function(input) {
    if(!aipo.message.inputHandle[input.form.id]) {
        aipo.message.inputHandle[input.form.id] = dojo.connect(input, "onkeydown", null, function(e) {
            if ((e.metaKey || e.ctrlKey) && e.keyCode == 13) {
                this.form.onsubmit();
            }
        });
	}
}

/**
 * 指定されたルームの未読を消します。
 * ただし、データベースにはアクセスしていないので、これとは別にデータベースも書き換える必要があります。
 */
aipo.message.read = function(room_id) {
    var messageRoomUnreadCount = dojo.byId("messageRoomUnreadCount" + room_id);
    if (messageRoomUnreadCount) {
        messageRoomUnreadCount.parentNode.removeChild(messageRoomUnreadCount);
    }
    aipo.message.refreshUnreadCount();
}
aipo.message.refreshUnreadCount = function() {
    var total = 0;
    dojo.query(".messageSummary .nrCount").forEach(function(item) {
        var value = parseInt(item.innerHTML);
        if (value != NaN) {
            total += value;
        }
    });
    aipo.menu.message.count(total);
    aipo.menu.updateTitle();
}

aipo.message.onFocusPlaceholder = function(input) {
    input.nextSibling.style.display = "none";
}

aipo.message.onBlurPlaceholder = function(input) {
    if (!input.value) {
        input.nextSibling.style.display = "";
    }
}

aipo.message.getFirstMessageId = function() {
    var messagePane = dojo.byId("messagePane");
    if(messagePane) {
        if(messagePane.children.length > 0) {
            var id = messagePane.children[0].id;
            if(id) {
                return id.replace("message", "");
            }
        }
    }
    return null;
}

aipo.message.getLastMessageId = function() {
    var messagePane = dojo.byId("messagePane");
    if(messagePane) {
        if(messagePane.children.length > 0) {
            var id = messagePane.children[messagePane.children.length-1].id;
            if(id) {
                return id.replace("message", "").replace("Line","");
            }
        }
    }
    return null;
}

aipo.message.getLastMessageRightId = function() {
    var messageRightPane = dojo.byId("messageRightPane");
    if(messageRightPane) {
    	if (messageRightPane.children.length > 3) {
       	    var messages = messageRightPane.children[messageRightPane.children.length-1];
            if(messages.children.length > 0) {
                var id = messages.children[messages.children.length-1].id;
                if(id) {
                    return id.replace("messageRight", "").replace("Line","");
                }
            }
    	}
    }
    return null;
}

aipo.message.isOpenWindow = function() {
    return aipo.message.isMobile ? true : dojo.hasClass("dd_message", "open");
}

aipo.message.onLoadMessageReadUserList = function() {

}

aipo.message.openDirect = function(user_id) {
	aipo.message.hideProfile();
	if(aipo.message.isMobile ? aipo.message.isInit : dojo.hasClass("dd_message", "open")) {
		aipo.message.openDirectMessage(user_id);
		return;
	}
	if(aipo.message.isMobile) {
		location.href = aipo.message.jslink +"?action=controls.Maximize&u=" + user_id
	} else {
	    if(aipo.message.isDirect) {
		    return;
	    }
        var dialog = dijit.byId("modalDialog");
	    if(dialog) {
	    	dialog.hide();
	    }
	    aipo.message.isDirect = true;
	    if(aipo.message.currentRoomId) {
            aipo.message.tmpRoomId = aipo.message.currentRoomId;
        }
        aipo.message.currentRoomId = null;
	    aipo.message.currentRoomSearchKeyword = null;
	    aipo.message.currentUserSearchKeyword = null;
	    aipo.message.currentGroupName = "all";
	    aipo.message.currentUserId = user_id;
	    aipo.menu.toggleDropdown("message");
	    aipo.message.selectTab("user");
	    aipo.message.getCookieIsLastRoomOrUser("User");
	}
}

aipo.message.openDirectMessage = function(user_id) {
	    if(aipo.message.isDirect) {
		    return;
	    }
        var dialog = dijit.byId("modalDialog");
	    if(dialog) {
	    	dialog.hide();
	    }
	    aipo.message.isDirect = true;
	    if(aipo.message.currentRoomId) {
            aipo.message.tmpRoomId = aipo.message.currentRoomId;
        }
        aipo.message.currentRoomId = null;
	    aipo.message.currentRoomSearchKeyword = null;
	    aipo.message.currentUserSearchKeyword = null;
	    aipo.message.currentGroupName = "all";
	    aipo.message.currentUserId = user_id;
		if(aipo.message.isMobile) {
			aipo.message.reloadUserList();
		} else {
		    aipo.message.selectTab("user");
		    aipo.message.getCookieIsLastRoomOrUser("User");
		}
}

aipo.message.hideProfile = function() {
	dojo.query('.profilePopup').style('display', 'none');
	if(aipo.message.mobileUnderlay) {
		aipo.message.mobileUnderlay.hide();
	}
}

var profileHandle = {};
profileHandle['body'] = null;
profileHandle['linkEnter'] = null;
profileHandle['linkLeave'] = null;
profileHandle['profileEnter'] = null;
profileHandle['profileLeave'] = null;
aipo.message.profileCurrentUserId = null;
aipo.message.mobileUnderlay = null;
aipo.message.popupProfile = function(userId, event) {
	if(aipo.message.isMobile && !aipo.message.mobileUnderlay) {
		aipo.message.mobileUnderlay = new aimluck.widget.DialogUnderlay();
		dojo.byId(aipo.message.mobileUnderlay.domNode.id).style["z-index"] = 999;
		dojo.connect(aipo.message.mobileUnderlay.domNode, "onmousedown", aipo.message.mobileUnderlay.domNode, function(){
			 aipo.message.hideProfile();
				//android2の時、テキストエリアへの書き込み時に画面が激しくスクロールするの対策
			    if(aipo.userAgent.isAndroid2()){
			        var wrapper=dojo.byId("wrapper");
			        // wrapper再表示
			        // wrapperの非表示はdisplay:none;で行う
			        // dojo.style(wrapper, "visibility", "visible");
			        dojo.style(wrapper, "display", "block");
			    }
		 });
	}
	if(aipo.message.isMobile) {
	    aipo.message.mobileUnderlay.show();
	}
	if(!profileHandle['body']) {
		var body = dojo.query('body')[0];
		if(aipo.userAgent.isIphone8_4_1()){
			body = dojo.byId('wrapper');
		}
		profileHandle['body'] = dojo.connect(body, 'onmousedown', null, function(){
			if (dojo.query('.profileMouseenter').length == 0) {
				aipo.message.hideProfile();
			}
		});
	}
	var popup = dojo.byId("popupProfile_" + userId);
	if(!popup) {
		var div = document.createElement("div");
		div.id = "popupProfile_" + userId;
		div.className = "profilePopupWrap";
		div.style.display = "block";
		document.body.appendChild(div);
		dojo.byId("popupProfile_" + userId).innerHTML = '<div id="popupProfileInner_' + userId + '" class="profilePopup" style="display: none;">';
	}
	event = event || window.event;
	var eventTarget = event.srcElement || event.target;
    var popupProfile = dojo.byId("popupProfileInner_" + userId);
	dojo.disconnect(profileHandle['linkEnter']);
	profileHandle['linkEnter'] = dojo.connect(eventTarget, 'onmouseenter', null, function(){
		dojo.addClass(this, 'profileMouseenter');
	});
	dojo.disconnect(profileHandle['linkLeave']);
	profileHandle['linkLeave'] = dojo.connect(eventTarget, 'onmouseleave', null, function(){
		dojo.removeClass(this, 'profileMouseenter');
	});
	dojo.disconnect(profileHandle['profileEnter']);
	profileHandle['profileEnter'] = dojo.connect(popupProfile, 'onmouseenter', null, function(){
		dojo.addClass(this, 'profileMouseenter');
	});
	dojo.disconnect(profileHandle['profileLeave']);
	profileHandle['profileLeave'] = dojo.connect(popupProfile, 'onmouseleave', null, function(){
		dojo.removeClass(this, 'profileMouseenter');
	});
	dojo.addClass(eventTarget, 'profileMouseenter');

	var popupInner = dijit.byId("popupProfileInner_" + userId);/*プロフィールカード*/
	if (!popupInner) {
		popupInner = dijit.byId("popupProfileInner_" + userId);
		popupInner = new aimluck.widget.Contentpane({}, "popupProfileInner_" + userId);
		popupInner.eventTarget = eventTarget;
	}
	popupInner.onLoad = function() {
		var rect = eventTarget.getBoundingClientRect();
		var html = document.documentElement.getBoundingClientRect();
		var objBody = document.getElementsByTagName("body").item(0);
		var node = dojo.query("#popupProfileInner_" + userId); /*プロフィールカード本体の表示・非表示*/
		var popupNode = dojo.query("#popupProfile_" + userId); /*プロフィールカードの出現位置を指定*/
		var wrapper=dojo.byId("wrapper");

		if (node.style('display') == 'none' || popupInner.eventTarget !== eventTarget) {
			dojo.query('.profilePopup').style('display', 'none');
			var scroll={
					left:document.documentElement.scrollLeft||document.body.scrollLeft,
					top:document.documentElement.scrollTop||document.body.scrollTop
			};

			node.style("display","block");
			popupNode.style("position","absolute");
			node.style("opacity","0");

			var width = scroll.left + html.right;
			var bottom = node[0].clientWidth + rect.right;
			if(aipo.message.isMobile) {
				popupNode.style("left","0px");
			} else if(bottom < width){
				popupNode.style("left",10+rect.right+scroll.left+"px");
			}else{
				popupNode.style("left",-10+rect.left-node[0].clientWidth+scroll.left+"px");
			}
			var height = scroll.top + html.bottom;
			var top = node[0].clientHeight + rect.bottom;
			if(top < height){
				popupNode.style("top",rect.top+scroll.top+"px");
			}else {
				popupNode.style("top",rect.bottom-node[0].clientHeight+scroll.top+"px");
			}
			//　 Android2系でプロフィールカードポップアップからメッセージを送信しようとすると
		    // 　画面が上下に激しく動くバグを回避
			if(aipo.userAgent.isAndroid2()){
				// wrapper非表示はhiddenではなくdisplay:none;で
				// dojo.style(wrapper, "visibility", "hidden");
				dojo.style(wrapper, "display", "none");
				popupNode.style("top", "0px");
				popupNode.style("margin", "0");
				}
			node.style("opacity","1");
		} else {
			node.style('display','none');
		}
		popupInner.eventTarget = eventTarget;
		popupInner.currentUserId = userId;
		aipo.message.profileCurrentUserId = userId;
	}
	if(userId == popupInner.currentUserId) {
		popupInner.onLoad();
	} else {
		popupInner.viewPage("?template=UserPopupScreen&entityid=" + userId);
	}
}

aipo.message.onReceiveProfileMessage = function(msg) {
    if (!msg["error"]) {
    	aipo.message.openDirect(aipo.message.profileCurrentUserId);
    	 var messageForm = dojo.byId("messageForm" + aipo.message.profileCurrentUserId);
    	 if(messageForm) {
    		 messageForm.message.value = "";
    	 }
    	 aipo.message.closeProifleTextarea(aipo.message.profileCurrentUserId);
    }
}

aipo.message.focusProfileInput = function(userId) {
    var messageForm = dojo.byId("messageForm" + userId);
    if (messageForm) {
    	try{
    		messageForm.message.focus();
    	}catch(e){
    		//ignore
    	}

    }
}

aipo.message.openProfileTextarea = function(userId) {
	var card = dojo.byId("profile_card_" + userId);
	var cardDummy = dojo.byId("profile_card_dummy_" + userId);
	card.style.display="block";
	cardDummy.style.display="none";
	aipo.message.focusProfileInput(userId);
}

aipo.message.closeProifleTextarea  = function(userId) {
	var card = dojo.byId("profile_card_" + userId);
	var cardDummy = dojo.byId("profile_card_dummy_" + userId);
	cardDummy.style.display="block";
	card.style.display="none";
}

aipo.message.jumpMessage = function(roomId, messageId){
	aipo.message.jumpCursor = messageId;
	aipo.message.selectTab('room');
	aipo.message.selectRoom(roomId, true);
	aipo.message.getCookieIsLastRoomOrUser("Room");
}

aipo.message.onFocusSearch = function() {
	var messageSearchForm = dojo.byId("messageSearchForm");
	if(messageSearchForm) {
		dojo.addClass(messageSearchForm, "focus");
	}
}

aipo.message.onBlurSearch = function() {
	var messageSearchForm = dojo.byId("messageSearchForm");
	if(messageSearchForm) {
		if(!messageSearchForm.keyword.value) {
		    dojo.removeClass(messageSearchForm, "focus");
		}
	}
}

aipo.message.fixDateLine = function() {
	var messagePane = dojo.byId("messagePane");
	var last = dojo.query(".messageLineLast", messagePane);
	if(last.length > 0) {
		var lastDom = last[0];
		var className = lastDom.className.replace("date_line","").replace("messageLineLast","").replace("  ","");
		var lineList = dojo.query("." + className, messagePane);
		for(var i=0; i<lineList.length; i++) {
			if(i == lineList.length-1) {
				lineList[i].style.display = "";
			} else {
				dojo.removeClass(lineList[i], "messageLineLast");
				lineList[i].style.display = "none";
			}
		}
	}
}

aipo.message.scrollTo = function(element, to, duration) {
	  if (duration < 0) return;
	  var difference = to - element.scrollTop;
	  var perTick = difference / duration * 10;

	  setTimeout(function() {
	    element.scrollTop = element.scrollTop + perTick;
	    if (element.scrollTop == to) return;
	    aipo.message.scrollTo(element, to, duration - 10);
	  }, 10);
}

aipo.message.insertTransactionId = function(targetUserId){
	function guid() {
		  function s4() {
		    return Math.floor((1 + Math.random()) * 0x10000)
		      .toString(16)
		      .substring(1).
		      toUpperCase();
		  }
		  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		    s4() + '-' + s4() + s4() + s4();
		}
	var transactionId=guid();
	dojo.query("#messageForm"+targetUserId+" [name='transactionId']")[0].setAttribute('value', transactionId);
	aipo.message.transactionIdList.push(transactionId);
}

aipo.message.removeTransactionId = function(transactionId){
	var transactionIdPos = aipo.message.transactionIdList.indexOf(transactionId);
	if(transactionIdPos==-1){
		return false;
	}
	else{
		aipo.message.transactionIdList.splice(transactionIdPos, 1);
		return true;
	}
}

//CookieにcurrentRoomIdあるいはUserIdを保存する
// Room -> lastRoomId / User -> targetUserId
aipo.message.saveCookieTargetId = function(cookieName, targetId){
		var cookieValue = cookieName + 	"=" + targetId　+ ";" + "path=/;";
		document.cookie = cookieValue;
}

//最後に選ばれたのが、RoomなのかUserなのかCookieへ保存する
aipo.message.saveCookieIsLastRoomOrUser = function(cookieTarget){
	var cookieValue = "lastIsRoomOrUser=" + cookieTarget + ";" + "path=/;";
	document.cookie = cookieValue;
}

//最後に選ばれたのが、RoomなのかUserなのかCookieから取得する
aipo.message.getCookieIsLastRoomOrUser = function(){
	var allCookies = document.cookie;
	var cookieName = "lastIsRoomOrUser="
	var userOrRoom = "";

	if (allCookies.indexOf(cookieName) != -1) {
		var number = allCookies.indexOf(cookieName) + cookieName.length;
		var number2 = allCookies.indexOf(";", number);
		if (number2 == -1) {
			number2 = allCookies.length;
		}
		userOrRoom = decodeURIComponent(allCookies.substring(number, number2));
	}
	return userOrRoom;
}

//CookieをからRoomのIdを取得する
aipo.message.getCookieRoomId = function(cookieName) {
	var allCookies = document.cookie;

	if (allCookies.indexOf(cookieName) != -1) {
		var number = allCookies.indexOf(cookieName) + cookieName.length;
		var number2 = allCookies.indexOf(";", number);
		if (number2 == -1) {
			number2 = allCookies.length;
		}
		var room_id = decodeURIComponent(allCookies.substring(number, number2));
	}
	return room_id;
}
