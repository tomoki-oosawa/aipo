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
if(!dojo._hasResource["aipo.widget.DropdownActivityChecker"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.DropdownActivityChecker"] = true;

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
    eventList:[],
    callback: function(){},
    templateString: '<div class="dijit dijitLeft dijitInline"\n\tdojoAttachEvent="onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey"\n\t><div class="" type="${type}"\n\t\tdojoAttachPoint="focusNode,titleNode" waiRole="button" waiState="haspopup-true,labelledby-${id}_label"\n\t\t><div class="" \tdojoAttachPoint="containerNode,popupStateNode"\n\t\tid="${id}_label"><div id="activityChecker" class="zero activitycheckerstyle counter ${extendClass}"></div><span class="mb_hide">お知らせ</span><span class="pc_hide"><i class="icon-bell-alt"></i></span></div></div></div>\n',
    postCreate: function(){
        this.inherited(arguments);
        this.dropDown = new aipo.widget.ActivityList({},'activityLiteList');
    },
	_openDropDown: function(){
        this.inherited(arguments);
        if(dojo.byId('activitycheckerContainer')) {
        	dojo.addClass(dojo.byId('activitycheckerContainer'), 'active');
        }
        this.dropDown.reload();

        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("iphone") > -1||userAgent.indexOf("android") > -1 ){
          	//一番上へスクロール
        	if(!!document.documentElement.scrollTop) document.documentElement.scrollTop=0;
        	else if(!!document.body.scrollTop)document.body.scrollTop=0;
        }
        //android時 下位レイヤーを無効にする。
    	if(aipo.userAgent.isAndroid()){
    		var _this=this;

    		var f=function(e){
    			e.preventDefault();
            	//e.stopImmediatePropagation();
            	return false;
            };

            //モーダルダイアログ
            var dialogNodes=dojo.query("input,select,button,a",this.domNode);

            //ダイアログ表示時に機能しなくなるので、
            //ヘッダとフッタ以下もpreventDefaultしない。
            var navigationElems=dojo.byId("appsNavigation_v2");
            var navigationNodes=dojo.query("input,select,button,a",navigationElems);
            var auiWidgetsElems=dojo.byId("auiWidgetsArea");
            var auiWidgetsNodes=dojo.query("input,select,button,a",auiWidgetsElems);

            //preventDefaultしないノード達
            var excludeNodes=dialogNodes;
            excludeNodes=excludeNodes.concat(navigationNodes);
            excludeNodes=excludeNodes.concat(auiWidgetsNodes);

    		dojo.query("input,select,button,a").forEach(function(node){
    			if(!aipo.arrayContains(excludeNodes,node))
    				_this.eventList.push(dojo.connect(node,'click',f));
    		});
    		dojo.query("input,select,button").forEach(function(node,index){
        		if(!aipo.arrayContains(excludeNodes,node) && !node.disabled){
        			dojo.addClass(node,"disabled-by-activity");
        			node.disabled=true;
        		}
        	});
    	}
    },
	_closeDropDown:function(){
        this.inherited(arguments);
        if(dojo.byId('activitycheckerContainer')) {
        	dojo.removeClass(dojo.byId('activitycheckerContainer'), 'active');
        }
    	if(document.getElementById('wrapper')) {
    		document.getElementById('wrapper').style.minHeight = '';
    	}

        if(aipo.userAgent.isAndroid()){
        	for(var i=0;i<this.eventList.length;i++){
        		dojo.disconnect(this.eventList[i]);
        	}
        	this.eventList=[];
        	dojo.query("input.disabled-by-activity,select.disabled-by-activity,button.disabled-by-activity").forEach(function(node,index){
        		dojo.removeClass(node,"disabled-by-activity");
        		node.disabled=false;
        	});
    	}
	},
    onCheckActivity: function(count) {
    	var checker = dojo.byId("activitychecker");
        if (count > 99) {
        	checker.innerHTML = '99+';
        	dojo.removeClass("activitychecker", "zero");
        } else if (count == 0) {
        	checker.innerHTML = count;
        	dojo.addClass("activitychecker", "zero");
        } else {
        	checker.innerHTML = count;
        	dojo.removeClass("activitychecker", "zero");
        }
    	dojo.addClass("activitychecker", "counter");
    },
    onCheckBlank: function(/*evt*/ e){
    }
});

}
