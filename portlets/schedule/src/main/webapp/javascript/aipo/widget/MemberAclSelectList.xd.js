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
dojo._xdResourceLoaded({
depends: [["provide", "aipo.widget.MemberAclSelectList"]],
defineResource: function(dojo){if(!dojo._hasResource["aipo.widget.MemberAclSelectList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.MemberAclSelectList"] = true;

dojo.provide("aipo.widget.MemberAclSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberAclSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberToTitle: "",
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    memberValuesStr: "",
    memberValues: [],
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none wide mb15\"><tbody><tr><td valign=\"top\"><table class=\"auiRowTable wide mb5\"><tbody name=\"${memberToId}\" id=\"${memberToId}\"><tr><th class=\"thin\"><input type=\"checkbox\" onclick=\"aimluck.io.switchCheckbox(this);\"/></th><th class=\"w50\">\u540D\u524D</th><th nowrap=\"nowrap\">\u6A29\u9650</th></tr></tbody></table><input type=\"button\" class=\"button\" value=\"\u3000\u524A\u9664\u3000\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></td><td class=\"thin\" valign=\"top\"><select style=\"width:140px\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select>\n<select size=\"8\" multiple=\"multiple\" style=\"width:140px\" class=\"mb5\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select>\n<input type=\"button\" class=\"button\" value=\"\u3000\uFF1C \u8FFD\u52A0\u3000\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></td></tr></tbody></table></div>\n",
    postCreate: function() {
        this.id = this.widgetId;
        this.memberValues = [];
        var tmpMemberValues = this.memberValuesStr.split(',');
        for (var i = 0; i < tmpMemberValues.length; i ++) {
            var tmpMemberValue = tmpMemberValues[i].split(":");
            var memberValue = {};
            memberValue.key = tmpMemberValue[0];
            memberValue.value = tmpMemberValue[1];
            this.memberValues.push(memberValue);
        }

        var params = {
            url: this.memberFromUrl,
            key: this.memberFromOptionKey,
            value: this.memberFromOptionValue,
            preOptions: { key:'ug2', value:'\u005b\u5168\u54e1\u005d' },
            callback: this.addMemberSync,
            callbackTarget: this
        };
        aimluck.io.createOptions(this.memberFromId, params);

        params = {
            url: this.memberGroupUrl,
            key: this.groupSelectOptionKey,
            value: this.groupSelectOptionValue,
            preOptions: { key:'all', value:'\u3059\u3079\u3066\u306e\u30e6\u30fc\u30b6\u30fc\u3001\u30b0\u30eb\u30fc\u30d7' }
        };
        aimluck.io.createOptions(this.groupSelectId, params);
    },
    addMemberSync: function() {
        var select_member_from = dojo.byId(this.memberFromId);
        var tbody_member_to = dojo.byId(this.memberToId);
        var f_o = select_member_from.options;
        if (f_o.length == 1 && f_o[0].value == "") return;
        for (var i = 0; i < f_o.length; i ++) {
            var iseq = false;

            var checkedValue = "1";
            for (j = 0; j < this.memberValues.length; j ++) {
                if (this.memberValues[j].key == f_o[i].value) {
                    iseq = true;
                    checkedValue = this.memberValues[j].value;
                    break;
                }
            }

            if (!iseq) continue;
            //this.memberValues.push(f_o[i].value);

            var element = document.createElement('tr');
            element.id = "tracldel" + f_o[i].value;

            var td1 = document.createElement('td');
            td1.innerHTML = this.assingValue('<input type="checkbox" id="acldel__ID__" name="acldel" value="__ID__"/>', f_o[i].value, f_o[i].text, checkedValue);

            var td2 = document.createElement('td');
            td2.innerHTML = this.assingValue('<label for="acldel__ID__">__NAME__</label>', f_o[i].value, f_o[i].text, checkedValue);

            var td3 = document.createElement('td');
            td3.setAttribute("nowrap", 'true');
            td3.nowrap = "true";
            td3.innerHTML = this.assingValue('<label><input type="radio" name="acl__ID__" value="1" __CHECKED1__/>\u95b2\u89a7\u306e\u307f</label>&nbsp;<label><input type="radio" name="acl__ID__" value="2" __CHECKED2__/>\u95b2\u89a7\u30fb\u8ffd\u52a0\u30fb\u7de8\u96c6\u30fb\u524a\u9664\u53ef\u80fd</label>', f_o[i].value, f_o[i].text, checkedValue);

            element.appendChild(td1);
            element.appendChild(td2);
            element.appendChild(td3);

            tbody_member_to.appendChild(element);

        }
        //this.memberValues = [];
    },
    assingValue: function(element,id, name, checkedValue) {
        return element.replace(/__ID__/g,id).replace(/__NAME__/g, name).replace(/__CHECKED1__/g, "1" == checkedValue ? "checked='checked'" : "").replace(/__CHECKED2__/g, "2" == checkedValue ? "checked='checked'" : "");
    },
    addMember: function(select_member_from, tbody_member_to) {
        var f_o = select_member_from.options;
        if (f_o.length == 1 && f_o[0].value == "") return;
        for (var i = 0; i < f_o.length; i ++) {
            if (!f_o[i].selected) continue;
            var iseq = false;

            for (var j = 0; j < this.memberValues.length; j ++) {
                if (this.memberValues[j].key == f_o[i].value) {
                    iseq = true;
                    break;
                }
            }

            if (iseq) continue;

            var memberValue = {};
            memberValue.key = f_o[i].value;
            memberValue.value = "1";
            this.memberValues.push(memberValue);

            var element = document.createElement('tr');
            element.id = "tracldel" + f_o[i].value;

            var td1 = document.createElement('td');
            td1.innerHTML = this.assingValue('<input type="checkbox" id="acldel__ID__" name="acldel" value="__ID__"/>', f_o[i].value, f_o[i].text, "2");

            var td2 = document.createElement('td');
            td2.innerHTML = this.assingValue('<label for="acldel__ID__">__NAME__</label>', f_o[i].value, f_o[i].text, "2");

            var td3 = document.createElement('td');
            td3.setAttribute("nowrap", 'true');
            td3.nowrap = "true";
            td3.innerHTML = this.assingValue('<label><input type="radio" name="acl__ID__" value="1" __CHECKED1__/>\u95b2\u89a7\u306e\u307f</label>&nbsp;<label><input type="radio" name="acl__ID__" value="2" __CHECKED2__/>\u95b2\u89a7\u30fb\u8ffd\u52a0\u30fb\u7de8\u96c6\u30fb\u524a\u9664\u53ef\u80fd</label>', f_o[i].value, f_o[i].text, "2");

            element.appendChild(td1);
            element.appendChild(td2);
            element.appendChild(td3);

            tbody_member_to.appendChild(element);
        }
    },
    removeMember:function(form) {
        var removeItems = [];
        for (var i = 0; i < form.elements.length; i++) {
            var element = form.elements[i];
            if ("acldel" == element.name) {
                if (element.checked) {
                    removeItems.push(element);
                }
            }
        }
        for (var i = 0; i < removeItems.length; i++) {
            var removeId = removeItems[i].id;
            var item = dojo.byId("tr" + removeId);
            item.parentNode.removeChild(item);
            for (var j = 0; j < this.memberValues.length; j++) {
                if (this.memberValues[j].key == removeId.replace("acldel", "")) {
                    this.memberValues.splice(j, 1);
                }
            }
        }
    },
    changeGroup: function(select) {
        var group_name = select.target.options[select.target.selectedIndex].value;
        var url = this.changeGroupUrl + "&groupname=" + group_name;
        var params = {
            url: url,
            key: this.memberFromOptionKey,
            value: this.memberFromOptionValue,
            indicator: this.widgetId + "-memberlist-indicator"
        };
        aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt) {
        this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
    },
    onMemberRemoveClick: function(/*Event*/ evt) {
        this.removeMember(evt.target.form);
    }
});

}

}});