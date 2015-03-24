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
if(!dojo._hasResource["aipo.widget.ActivityList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.ActivityList"] = true;

dojo.provide("aipo.widget.ActivityList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("aimluck.widget.Contentpane");

dojo.declare("aipo.widget.ActivityList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\" style=\"width: 420px;\"><div class=\"activityPopup\"><div class=\"clearfix\"><div id=\"activityListPane\" widgetId=\"activityListPane\"></div></div></div></div>\n",
    postCreate: function(){

    },
    reload: function() {
    	var content = dijit.byId("activityListPane");
    	if(!content) {
            content = new aimluck.widget.Contentpane({},'activityListPane');
    	}
    	if(window.webkitNotifications) {
           content.viewPage("?template=ActivityListScreen&s=1&p=" + window.webkitNotifications.checkPermission());
    	}else if(window.Notification){
    		content.viewPage("?template=ActivityListScreen&s=1&p=" + window.Notification.permission);
    	}else {
           content.viewPage("?template=ActivityListScreen&s=0");
        }
    }
});

}
