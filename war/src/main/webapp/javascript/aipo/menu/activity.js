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
dojo.provide("aipo.menu.activity");

aipo.menu.activity.reload = function() {
    var content = dijit.byId("activityListPane");
    if (!content) {
        content = new aimluck.widget.Contentpane({}, 'activityListPane');

    }
    if (window.webkitNotifications) {
        content.viewPage("?template=ActivityListScreen&s=1&p="
                + window.webkitNotifications.checkPermission());
    } else if (window.Notification) {
        content.viewPage("?template=ActivityListScreen&s=1&p="
                + window.Notification.permission);
    } else {
        content.viewPage("?template=ActivityListScreen&s=0");
    }
}

aipo.menu.activity.count = function(count) {
    var checker = dojo.byId("activityChecker");
    if (checker > 99) {
        checker.innerHTML = '99+';
        dojo.addClass("activityChecker", "num");
    } else if (count == 0) {
        checker.innerHTML = '';
        dojo.removeClass("activityChecker", "num");
    } else {
        checker.innerHTML = count;
        dojo.addClass("activityChecker", "num");
    }
}