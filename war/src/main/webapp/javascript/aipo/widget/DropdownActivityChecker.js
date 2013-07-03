if(!dojo._hasResource["aipo.widget.DropdownActivityChecker"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DropdownActivityChecker"] = true;
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

dojo.provide("aipo.widget.DropdownActivityChecker");

dojo.require("aimluck.widget.Dropdown");

dojo.require("aipo.widget.ActivityList");

/**
 * ex)
 */
dojo.declare("aipo.widget.DropdownActivityChecker", [aimluck.widget.Dropdown], {
    initValue: "",
    displayCheck: "",
    iconURL: "",
    iconAlt: "",
    iconWidth: "",
    iconHeight: "",
    extendClass: "",
    callback: function(){},
    templateString: '<div class="dijit dijitLeft dijitInline"\n\tdojoAttachEvent="onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey"\n\t><div style="outline:0" class="" type="${type}"\n\t\tdojoAttachPoint="focusNode,titleNode" waiRole="button" waiState="haspopup-true,labelledby-${id}_label"\n\t\t><div class="" \tdojoAttachPoint="containerNode,popupStateNode"\n\t\tid="${id}_label"><div id="activitychecker" class="zero activitycheckerstyle ${extendClass}"></div><span class="mb_hide">お知らせ</span></div></div></div>\n',
    postCreate: function(){
        this.inherited(arguments);
        this.dropDown = new aipo.widget.ActivityList({},'activityLiteList');
    },
	_openDropDown: function(){
        this.inherited(arguments);
		if(dojo.byId("auiMbBtnActivity"))dojo.addClass("auiMbBtnActivity","active");
        this.dropDown.reload();
    },
	_closeDropDown:function(){
        this.inherited(arguments);
        if(dojo.byId("auiMbBtnActivity"))dojo.removeClass("auiMbBtnActivity","active");
	},
    onCheckActivity: function(count) {
    	var checker = dojo.byId("activitychecker");
    	var activity = dojo.byId("auiMbBtnActivity");
        if (count > 99) {
        	checker.innerHTML = '99+';
        	dojo.removeClass("activitychecker", "zero");
        	if(activity) {
        		dojo.removeClass("auiMbBtnActivity", "zero");
        	}
        } else if (count == 0) {
        	checker.innerHTML = count;
        	dojo.addClass("activitychecker", "zero");
        	if(activity) {
        		dojo.addClass("auiMbBtnActivity", "zero");
        	}
        } else {
        	checker.innerHTML = count;
        	dojo.removeClass("activitychecker", "zero");
        	if(activity) {
        		dojo.removeClass("auiMbBtnActivity", "zero");
        	}
        }
    },
    onCheckBlank: function(/*evt*/ e){
    }
});

}
