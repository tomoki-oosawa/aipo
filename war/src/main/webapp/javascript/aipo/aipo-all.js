/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */
/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */
/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

dojo.provide("aipo.common");

aipo.common.showDialog = function(url, portlet_id, callback) {
    var arrDialog = dijit.byId("modalDialog");
    
    if(! arrDialog){
       arrDialog = new aimluck.widget.Dialog({widgetId:'modalDialog', _portlet_id: portlet_id, _callback:callback}, "modalDialog");
    }else{
       arrDialog.setCallback(portlet_id, callback);
    }
     
    if(arrDialog){
      arrDialog.setHref(url);
      arrDialog.show();
    }
};

aipo.common.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
      arrDialog.hide();
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */
/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

dojo.provide("aipo.io");

aipo.io.loadHtml = function(url, params, portletId){
    dojo.xhrGet({
        url: url,
        transport: "ScriptSrcTransport",
        jsonParamName: "callback",
        content: params,
        method: "get", 
        mimetype: "application/json",
        encoding: "utf-8",
        load: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = data.body;
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        error: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u8aad\u307f\u8fbc\u307f\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeout: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8\u3057\u307e\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeoutSeconds: 10
    });
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/
