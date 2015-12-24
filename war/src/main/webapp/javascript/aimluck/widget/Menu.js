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
if(!dojo._hasResource["aimluck.widget.Menu"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aimluck.widget.Menu"] = true;

dojo.provide("aimluck.widget.Menu");
dojo.provide("aimluck.widget.Menuitem");
dojo.provide("aimluck.widget.Menuseparator");
dojo.provide("aimluck.widget.Menubar");
dojo.provide("aimluck.widget.DropDownButton");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.Menu");
dojo.require("dijit.Toolbar");
dojo.require("dijit.form.Button");

dojo.declare("aimluck.widget.Menuitem", [dijit.MenuItem], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
        templateString:
         '<tr class="dijitReset dijitMenuItem"'
        +'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
        +'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass} menuItemIcon" dojoAttachPoint="iconNode" ></div></td>'
        +'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem" nowrap="nowrap"></td>'
        +'<td class="dijitReset" dojoAttachPoint="arrowCell">'
            +'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
            +'<span class="dijitInline moz-inline-box dijitArrowNode dijitMenuExpandInner">+</span>'
            +'</div>'
        +'</td>'
        +'</tr>',
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.MenuButton", [dijit.form.Button], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
    itemClass:"",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box dijitButton\"\n\tdojoAttachEvent=\"onclick:_onButtonClick,onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\"><div class='dijitRight'><button class=\"dijitStretch dijitButtonNode dijitButtonContents  ${itemClass}\" dojoAttachPoint=\"focusNode,titleNode\"\n\t\t\ttype=\"${type}\" waiRole=\"button\" waiState=\"labelledby-${id}_label\"><div class=\"dijitInline ${iconClass} menuItemIcon \" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span></button></div></div>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menu", [dijit.Menu], {
//    submenuOverlap: 2,
//    submenuDelay: 0,
    templateString:
            '<table class="popupMenu dijitMenuTable" waiRole="menu" dojoAttachEvent="onkeypress:_onKeyPress">' +
                '<tbody class="dijitReset" dojoAttachPoint="containerNode"></tbody>'+
            '</table>'
});


dojo.declare("aimluck.widget.Menuseparator", [dijit.MenuSeparator], {
    templateString: "<tr class=\"menuSeparator\"><td colspan=4>" + "<div class=\"menuSeparatorTop\"></div>" + "<div class=\"menuSeparatorBottom\"></div>" + "</td></tr>"
});

dojo.declare(
    "aimluck.widget.ToolbarSeparator",
    [ dijit.ToolbarSeparator ],
{
    // summary
    //  A line between two menu items
    templateString: '<div class="dijitInline moz-inline-box">&nbsp;｜&nbsp;</div>',
    postCreate: function(){ dojo.setSelectable(this.domNode, false); },
    isFocusable: function(){ return false; }
});

dojo.declare("aimluck.widget.DropDownButton", [dijit.form.DropDownButton], {
    label: "",
    iconSrc: "",
    iconClass: "",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<button class=\"dijitStretch dijitButtonNode dijitButtonContents\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><div class=\"dijitInline ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\">${label}</span\n\t\t><span class='dijitA11yDownArrow'>&#9660;</span>\n\t</button>\n</div></div>\n"
});

dojo.declare("aimluck.widget.ComboButton", [dijit.form.ComboButton], {
    // summary
    //      left side is normal button, right side displays menu
    url: "",
    itemClass:"",
    templateString:"<table class='dijit dijitReset dijitInline dijitLeft moz-inline-box ${itemClass} '\n\tcellspacing='0' cellpadding='0'\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\">\n\t<tr>\n\t\t<td\tclass=\"dijitStretch dijitButtonContents dijitButtonNode\"\n\t\t\ttabIndex=\"${tabIndex}\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onButtonClick\"  dojoAttachPoint=\"titleNode\"\n\t\t\twaiRole=\"button\" waiState=\"labelledby-${id}_label\">\n\t\t\t<div class=\"dijitMenuItemIcon ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div>\n\t\t\t<span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span>\n\t\t</td>\n\t\t<td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton'\n\t\t\tdojoAttachPoint=\"popupStateNode,focusNode\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onArrowClick, onkeypress:_onKey\"\n\t\t\tstateModifier=\"DownArrow\"\n\t\t\ttitle=\"${optionsTitle}\" name=\"${name}\"\n\t\t\twaiRole=\"button\" waiState=\"haspopup-true\"\n\t\t><div waiRole=\"presentation\">&#9660;</div>\n\t</td></tr>\n</table>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menubar", [dijit.Toolbar], {
    selectedIndex: -1,
    templateString:
        '<div class="tundra"><div class="dijit dijitToolbar" waiRole="toolbar" tabIndex="${tabIndex}" dojoAttachPoint="containerNode">' +
        '</div></div>',
    postCreate:function () {
        dijit.Toolbar.superclass.postCreate.apply(this, arguments);
        this.makeMenu(this.items);
        this.isShowingNow = true;
    },
    makeMenu:function(items) {
        var _this = this;
        var _count = 0;
        dojo.forEach(items, function(itemJson){
                if(itemJson.submenu){
                    var menu = new aimluck.widget.Menu({id: itemJson.caption, style: "display: none;" });
                    dojo.forEach(itemJson.submenu, function(itemJson2){
                        if(itemJson2 != null){
                            if (itemJson2.caption) {
                                menu.addChild( new aimluck.widget.Menuitem({label: itemJson2.caption, url: itemJson2.url, iconClass: itemJson2.iconClass}) );
                            } else {
                                menu.addChild( new aimluck.widget.Menuseparator() );
                            }
                        }
                    });
                    var _itemClass = "";
                    if(_this.selectedIndex == parseInt(_count) ){
                        _itemClass += "menuBarItemSelected";
                    }
                    var ddb = new aimluck.widget.ComboButton({ label: itemJson.caption, iconClass: itemJson.iconClass, dropDown: menu, url: itemJson.url, itemClass:_itemClass});
                    ddb.addChild(menu);
                    _this.addChild(ddb);
                } else if(itemJson.url) {
                    var _itemClass = "";
                    if(_this.selectedIndex == _count){
                        _itemClass += "menuBarItemSelected";
                    }
                    var ddb = new aimluck.widget.MenuButton({id: itemJson.caption + "_Button" + _count, label: itemJson.caption , url: itemJson.url, iconClass: itemJson.iconClass,  itemClass:_itemClass });
                    _this.addChild(ddb);
                } else {
                    _this.addChild(new aimluck.widget.ToolbarSeparator());
                }
                _count++;

        });

    }
});

}
