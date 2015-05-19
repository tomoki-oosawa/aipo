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
dojo.provide("aipo.notification");

aipo.notification.setUp = function() {
    var notificationRequestField = dojo.byId("notificationRequestField");
    var notificationConfirmField = dojo.byId("notificationConfirmField");
    var notificationSuccessField = dojo.byId("notificationSuccessField");
    if(window.Notification && window.Notification.permission!="granted") {
	    notificationRequestField.style.display="";
    }
}

aipo.notification.requestPermission = function() {
    var notificationRequestField = dojo.byId("notificationRequestField");
    var notificationConfirmField = dojo.byId("notificationConfirmField");
    var notificationSuccessField = dojo.byId("notificationSuccessField");
    window.Notification.requestPermission(function() {
    	if(window.Notification.permission == "granted") {
            notificationRequestField.style.display = "none";
            notificationConfirmField.style.display = "none";
            notificationSuccessField.style.display = "";
        } else if(window.Notification.permission == "default") {
            notificationSuccessField.style.display = "none";
            notificationConfirmField.style.display = "none";
            notificationRequestField.style.display = "";
        }
    });
    notificationRequestField.style.display = "none";
    notificationSuccessField.style.display = "none";
    notificationConfirmField.style.display = "";
}