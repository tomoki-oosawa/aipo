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
if(!dojo._hasResource["aimluck.widget.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aimluck.widget.Dialog"] = true;

dojo.provide("aimluck.widget.Dialog");
dojo.provide("aimluck.widget.DialogSub");
dojo.provide("aimluck.widget.DialogUnderlay");
dojo.provide("aimluck.widget.Timeout");

dojo.require("dijit.Dialog");
dojo.requireLocalization("aipo", "locale");
var nlsStrings = dojo.i18n.getLocalization("aipo", "locale");

dojo.declare(
    "aimluck.widget.DialogUnderlay",
    [dijit.DialogUnderlay],
    {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node'></div></div>",
       layout: function(){
			// summary
			//		Sets the background to the size of the viewport (rather than the size
			//		of the document) since we need to cover the whole browser window, even
			//		if the document is only a few lines long.

			var viewport = "";//dijit.getViewport();
			var is = "";//this.node.style,
				os = "";//this.domNode.style;

			os.top = "";//viewport.t + "px";
			os.left = "";//viewport.l + "px";
			is.width = "";//viewport.w + "px";
			is.height = "";//viewport.h + "px";*/

			// process twice since the scroll bar may have been removed
			// by the previous resizing
			var viewport2 = "";//dijit.getViewport();
			//if(viewport.w != viewport2.w){ is.width = viewport2.w + "px"; }
			//if(viewport.h != viewport2.h){ is.height = viewport2.h + "px"; }*/
		}
    }

);

dojo.declare( "aimluck.widget.Timeout",  [dijit._Widget, dijit._Templated] , {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node' redirecturl=\"${redirectUrl}\"></div></div>",
       redirectUrl:"about:blank",
       postCreate: function(){
    window.location.href = this.redirectUrl;
      }
});

dojo.declare(
    "aimluck.widget.DialogSub",
    [aimluck.widget.Dialog,dijit.Dialog],
    {
    	templateString:"<div id='modalDialog' class='modalDialog' dojoattachpoint='wrapper'><span dojoattachpoint='tabStartOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap'tabindex='0'></span><span dojoattachpoint='tabStart' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><div dojoattachpoint='containerNode' style='position: relative; z-index: 2;'></div><span dojoattachpoint='tabEnd' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><span dojoattachpoint='tabEndOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span></div>"
    }
);


dojo.declare(
    "aimluck.widget.Dialog",
    [dijit.Dialog],
    {
    	//読み込み中...
    	loadingMessage:"<div class='auiPopup indicatorDialog center'><i class='auiIcon auiIconIndicator'></i> "+nlsStrings.LOADING_STR+"</div>",
    	errorMessage: "<div class='auiPopup indicatorDialog center'>${errorState}</div>",
    	templateString:null,
        templateString:"<div id='modalDialog' class='modalDialog' dojoattachpoint='wrapper'><span dojoattachpoint='tabStartOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap'tabindex='0'></span><span dojoattachpoint='tabStart' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><div dojoattachpoint='containerNode' style='position: relative; z-index: 2;'></div><span dojoattachpoint='tabEnd' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><span dojoattachpoint='tabEndOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span></div>",//<div dojoAttachPoint=\"titleBar\" class=\"modalDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">&nbsp;</div>
        duration: 10,
        extractContent: false,
        parseOnLoad: true,
        refreshOnShow: true,
        isPositionLock: false,
        params: new Array(),
        reloadIds: new Array(),
        _portlet_id: null,
        _callback:null,
        eventList:[],
        _setup: function(){
            // summary:
            //      stuff we need to do before showing the Dialog for the first
            //      time (but we defer it until right beforehand, for
            //      performance reasons)

            this._modalconnects = [];

            if(this.titleBar){
                this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
                var _tmpnode = this.domNode;
                dojo.connect(this._moveable, "onMoving", function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){

                        var viewport = dijit.getViewport();
                        var w1 = parseInt(dojo.getComputedStyle(_tmpnode).width);
			            var w2 = parseInt(viewport.w);

	                    if(leftTop.l < 0){
	                       leftTop.l = 0;
	                    }

	                    if(leftTop.l + w1 > w2){
	                       leftTop.l = w2 - w1;
	                    }

	                    if(leftTop.t < 0){
	                       leftTop.t = 0
	                    }
                });
            }


            this._underlay = new aimluck.widget.DialogUnderlay();

            var node = this.domNode;
            this._fadeIn = dojo.fx.combine(
                [dojo.fadeIn({
                    node: node,
                    duration: this.duration
                 }),
                 dojo.fadeIn({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onBegin: dojo.hitch(this._underlay, "show")
                 })
                ]
            );

            this._fadeOut = dojo.fx.combine(
                [dojo.fadeOut({
                    node: node,
                    dialog: this,
                    duration: this.duration,
                    onEnd: function(){
                        node.style.display="none";
                        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
                        if (document.all) { // for IE
                            this.dialog.fixTmpScroll();
                        }
                        //**//
                    }
                 }),
                 dojo.fadeOut({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onEnd: dojo.hitch(this._underlay, "hide")
                 })
                ]
            );
        },
        fixTmpScroll: function(){
            //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
            var _tmpNode = dojo.byId('weeklyScrollPane_'+this._portlet_id);
            if(_tmpNode){
                if (typeof aipo.schedule.tmpScroll == "undefined") {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = ptConfig[this._portlet_id].contentScrollTop;
                } else {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = aipo.schedule.tmpScroll;
                }
            }
            //**//
        },
        onLoad: function(){
            // when href is specified we need to reposition
            // the dialog after the data is loaded
            this._position();
            dijit.Dialog.superclass.onLoad.call(this);
            this.isPositionLock = false;

            var userAgent = window.navigator.userAgent.toLowerCase();
            if (userAgent.indexOf("iphone") > -1||userAgent.indexOf("android") > -1 ){
            	//一番上へスクロール
            	if(!!document.documentElement.scrollTop) document.documentElement.scrollTop=0;
            	else if(!!document.body.scrollTop)document.body.scrollTop=0;
            }

            //android2の時、テキストエリアへの書き込み時に画面が激しくスクロールするの対策
            if(aipo.userAgent.isAndroid2()){
               var wrapper=dojo.byId("wrapper");
               // wrapper非表示はhiddenではなくdisplay:none;で
               // dojo.style(wrapper, "visibility", "hidden");
                dojo.style(wrapper, "display", "none");
                var myModal=dojo.byId("modalDialog");
                dojo.style(myModal, "top", "0px");
                dojo.style(myModal, "margin", "0");
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
            			dojo.addClass(node,"disabled-by-dialog");
            			node.disabled=true;
            		}
            	});
        	}
        	//android時、長い詳細画面を最後までスクロールできない問題の対策
        	if(aipo.userAgent.isAndroid()){
        		var modalDialog = document.getElementById('modalDialog');
        		if(modalDialog) {
        			var wrapper = document.getElementById('wrapper');
        			wrapper.style.minHeight = modalDialog.clientHeight + 'px';
        		}

        	}

            var focusNode = dojo.byId( this.widgetId );
            if ( focusNode ) {
                focusNode.focus();

                if (this._callback != null) {
                    this._callback.call(this._callback, this._portlet_id);
                }
            }

        	dojo.query("#modalDialog input, #modalDialog textarea").forEach( function (item) {
        		dojo.connect(item, "onfocus", aimluck.io.onTextFieldFocus)
        		dojo.connect(item, "onblur", aimluck.io.onTextFieldBlur)
        	})
        	dojo.query("#modalDialog form").forEach( function (item) {
        		dojo.connect(item, "onsubmit", aimluck.io.onTextFieldBlur)
        	})
        },
        setCallback: function(portlet_id, callback) {
            this._portlet_id = portlet_id;
            this._callback = callback;
        },
        setParam: function(key, value) {
            this.params[key] = value;
        },
        setReloadIds: function(values) {
            this.reloadIds = values;
        },
        clearParams: function() {
            this.params = new Array();
        },
        clearReloadIds: function() {
            this.reloadIds = new Array();
        },
        reload: function (url) {
            this.href = url;
            this.isPositionLock = true;
            this.refresh();
        },
        _position: function(){
            // summary: position modal dialog in center of screen

            if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
            var viewport = dijit.getViewport();
            var mb = dojo.marginBox(this.domNode);

            var style = this.domNode.style;
            if(this.id=="imageDialog"){
                if(style.width == 0) {
                    //loading
                    style.marginLeft = "-150px";
                }else{
                    style.marginLeft = Math.floor(-style.width.replace("px","")/2) + "px";
                }
            }
            if(Math.floor((viewport.t + (viewport.h - mb.h)/2)) > 0){
                style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
            } else {
                style.top = 0 + "px";
            }
        },
        layout: function() {
            if(this.domNode.style.display == "block"){
                this._underlay.layout();
                //this._position();
            }
        },
        _downloadExternalContent: function(){
            this._onUnloadHandler();

            // display loading message
            // TODO: maybe we should just set a css class with a loading image as background?

            this._setContent(
                this.onDownloadStart.call(this)
            );

            var self = this;
            var getArgs = {
                preventCache: (this.preventCache || this.refreshOnShow),
                url: this.href,
                handleAs: "text",
                content: this.params,
                headers: { X_REQUESTED_WITH: "XMLHttpRequest" }
            };
            if(dojo.isObject(this.ioArgs)){
                dojo.mixin(getArgs, this.ioArgs);
            }

            var hand = this._xhrDfd = (this.ioMethod || dojo.xhrPost)(getArgs);

            hand.addCallback(function(html){
                self.clearParams();
                self.clearReloadIds();
                try{
                    self.onDownloadEnd.call(self);
                    self._isDownloaded = true;
                    self.setContent.call(self, html); // onload event is called from here
                }catch(err){
                    self._onError.call(self, 'Content', err); // onContentError
                }
                delete self._xhrDfd;
                return html;
            });

            hand.addErrback(function(err){
                if(!hand.cancelled){
                    // show error message in the pane
                    self._onError.call(self, 'Download', err); // onDownloadError
                }
                delete self._xhrDfd;
                return err;
            });
        },
        _onKey: function(/*Event*/ evt){
		},
        hide:function(){
        	var wrapper = document.getElementById('wrapper');
        	if(wrapper) {
        		wrapper.style.minHeight = '';
        	}
        	dijit.Dialog.prototype.hide.apply(this);
        	dojo.query(".mb_dialoghide").removeClass("mb_dialoghide");
        	dojo.query("#modalDialog").removeClass("mb_dialog");
        	if(aipo && aipo.timeline && aipo.timeline.activeFileAttachments)aipo.timeline.activeFileAttachments(this._portlet_id);

        	if(aipo.userAgent.isAndroid()){
	        	for(var i=0;i<this.eventList.length;i++){
	        		dojo.disconnect(this.eventList[i]);
	        	}
	        	this.eventList=[];
	        	dojo.query("input.disabled-by-dialog,select.disabled-by-dialog,button.disabled-by-dialog").forEach(function(node,index){
	        		dojo.removeClass(node,"disabled-by-dialog");
	        		node.disabled=false;
	        	});
        	}
            //android2の時、テキストエリアへの書き込み時に画面が激しくスクロールするの対策
            if(aipo.userAgent.isAndroid2()){
               var wrapper=dojo.byId("wrapper");
               // wrapper再表示
               // wrapperの非表示はdisplay:none;で行う
               // dojo.style(wrapper, "visibility", "visible");
               dojo.style(wrapper, "display", "");
            }
        }
    }
);

}
