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

window.aipo = window.aipo || {};

aipo.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = aipo;


    for (var i=(levels[0] == "aipo") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

djConfig = { isDebug: false };

var ptConfig = [];

aipo.onReceiveMessage = function(msg, group){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        arrDialog.hide();
        aipo.portletReload(group);
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.portletReload = function(group, portletId) {
    for(var index in ptConfig) {
        if (index != portletId) {
            if(ptConfig[index].group == group) {
                ptConfig[index].reloadFunction.call(ptConfig[index].reloadFunction, index);
            }
        }
    }
};

aipo.reloadPage = function(portletId) {
    if( typeof ptConfig[portletId].reloadUrl == "undefined") {
      aipo.viewPage(ptConfig[portletId].initUrl, portletId);
    } else {
      aipo.viewPage(ptConfig[portletId].reloadUrl, portletId);
    }
};

aipo.viewPage = function(url, portletId, params) {
     var portlet = dijit.byId('portlet_' + portletId);
     if(! portlet){
       portlet = new aimluck.widget.Contentpane({},'portlet_' + portletId);
     }
     
     if(portlet){
       ptConfig[portletId].reloadUrl= url;
       
       if(params){
       	 for(i = 0 ; i < params.length; i++ ) {
       		portlet.setParam(params[i][0], params[i][1]);
       	 }
       }
       
       portlet.viewPage(url);
     }
};

aipo.errorTreatment = function(jsondata, url) {
    if (jsondata["error"]) {
        if(jsondata["error"]== 1) {
           window.location.href = url;
        } else {
            return true;
        }
        return false;
    } else {
        return true;
    }
};
