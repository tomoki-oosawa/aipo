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
dojo.provide("aipo.menu");

aipo.menu.list = [];
aipo.menu.register = function(key) {
    aipo.menu.list.push(key);
}

aipo.menu.toggleDropdown = function(key) {
    var menubar = dojo.byId("mb_" + key);
    var dropdown = dojo.byId("dd_" + key);

    if (menubar) {
        dojo.toggleClass(menubar, "active");
    }
    if (dropdown) {
        dojo.toggleClass(dropdown, "open");
        if(dojo.hasClass(dropdown, "open")) {
            if(aipo.menu[key] && aipo.menu[key].reload && !aipo.menu[key].isLoad) {
                aipo.menu[key].reload();
                aipo.menu[key].isLoad = true;
            }
        }
    }

    for ( var i in aipo.menu.list) {
        if (aipo.menu.list[i] != key) {
            aipo.menu.hideDropdown(aipo.menu.list[i]);
        }
    }

    aipo.message.swapView();
}

aipo.menu.hideDropdown = function(key) {
    var menubar = dojo.byId("mb_" + key);
    var dropdown = dojo.byId("dd_" + key);

    if (menubar) {
        dojo.removeClass(menubar, "active");
    }
    if (dropdown) {
        dojo.removeClass(dropdown, "open");
    }
}

aipo.menu.closeDropdown = function(key) {
    if(dijit.byId(key + 'checkerContainer')) {
        dijit.byId(key + 'checkerContainer')._closeDropDown();
    }
}

aipo.menu.hideDropdownAll = function(key) {
    for ( var i in aipo.menu.list) {
        aipo.menu.hideDropdown(aipo.menu.list[i]);
    }

    aipo.message.swapView();
}

aipo.menu.updateTitle = function() {
    var num = 0;
    for (var i in aipo.menu.list) {
        if (dojo.byId(aipo.menu.list[i] + "Checker") != undefined) {
            var value = parseInt(dojo.byId(aipo.menu.list[i] + "Checker").innerHTML);
            if(value) {
                num += value;
            }
        }
    }

    if (!num) {
        document.title = djConfig.siteTitle;
    } else if (num > 99) {
        document.title = "(99+) " + djConfig.siteTitle;
    } else {
        document.title = "(" + num + ") " + djConfig.siteTitle;
    }
}

var bodyHandle = bodyHandle || {};
dojo.addOnLoad(function() {
	var body = dojo.query('body')[0];
	if(aipo.userAgent.isIphone8_4_1()){
		body = dojo.byId('wrapper');
	}
    bodyHandle = dojo.connect(body, 'onmousedown', null,
            function(e) {
                if (dojo.query('a.customizeMenuIconMouseenter').length == 0) {
                    var srcElement;
                    if (document.all) {
                      srcElement = e.srcElement;
                    } else {
                      srcElement = e.target;
                    }
                    var node = new Array();
                    node[0] = {
                        nodeName : srcElement.nodeName,
                        className : srcElement.className
                    };
                    var parent = srcElement.parentNode;
                    var tmp = "";
                    for ( var i = 1; parent; i++) {
                        node[i] = {
                            nodeName : parent.nodeName,
                            className : parent.className
                        };
                        parent = parent.parentNode;
                    }
                    var isHideDropdown = true;
                    for ( var i = 0; i < node.length; i++) {
                        if (node[i].className) {
                            if (node[i].className.indexOf('open') >= 0) {
                                var isHideDropdown = false;
                                break;
                            }
                            if (node[i].className.indexOf('hdNavigation') >= 0) {
                                var isHideDropdown = false;
                                break;
                            }
                            if (node[i].className.indexOf('modalDialogUnderlayWrapper') > 0) {
                                var isHideDropdown = false;
                                break;
                            }
                            if (node[i].className.indexOf('modalDialog') >= 0) {
                                var isHideDropdown = false;
                                break;
                            }
                            if (node[i].className.indexOf('imgPopup') >= 0) {
                                var isHideDropdown = false;
                                break;
                            }
                            if (node[i].className.indexOf('profilePopup') >= 0) {
                                var isHideDropdown = false;
                                break;
                            }
                        }
                    }
                    if (isHideDropdown) {
                        aipo.menu.hideDropdownAll();
                    }
                }
            });
});
