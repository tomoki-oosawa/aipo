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
    callback: function(){},
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" />\n\t</span><div style=\"display: none;\" id=\"activitychecker\"><span class=\"activitychecker-right\"><span id=\"activitychecker-counter\" class=\"activitychecker-left\"></span></span></div><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",
    postCreate: function(){
        this.inherited(arguments);
        this.dropDown = new aipo.widget.ActivityList({},'activityLiteList');
    },
	_openDropDown: function(){
        this.inherited(arguments);
        this.dropDown.reload();
    },
    onCheckActivity: function(count) {
    	var checker = dojo.byId("activitychecker");
    	var counter = dojo.byId("activitychecker-counter");
        if (count > 99) {
        	counter.innerHTML = '99+';
        	counter.style.display = 'block';
        	checker.style.display = 'block';
        	/*counter.style.width = '18px';*/
        	favicon.change('images/favicon/50.ico');
        } else if (count == 0) {
            counter.innerHTML = '';
        	counter.style.display = 'none';
        	checker.style.display = 'none';
        	/*counter.style.width = '15px';*/
        	favicon.change('images/favicon.ico');
        } else {
        	counter.innerHTML = count;
        	counter.style.display = 'block';
        	checker.style.display = 'block';
        	/*counter.style.width = '15px';*/
        	if(count >= 50) {
              favicon.change('images/favicon/50.ico');
        	} else if(count >= 20) {
          	  favicon.change('images/favicon/20.ico');
        	} else {
        	  favicon.change('images/favicon/' + count + '.ico');
        	}
        }
    },
    onCheckBlank: function(/*evt*/ e){
    }
});

}
