dojo._xdResourceLoaded({
depends: [["provide", "aimluck.widget.Dialog"],
["provide", "aimluck.widget.DialogUnderlay"],
["provide", "aimluck.widget.Timeout"],
["require", "dijit.Dialog"]],
defineResource: function(dojo){if(!dojo._hasResource["aimluck.widget.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aimluck.widget.Dialog"] = true;
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

dojo.provide("aimluck.widget.Dialog");
dojo.provide("aimluck.widget.DialogUnderlay");
dojo.provide("aimluck.widget.Timeout");

dojo.require("dijit.Dialog");

dojo.declare(
    "aimluck.widget.DialogUnderlay",
    [dijit.DialogUnderlay],
    {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node'></div></div>"
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
    "aimluck.widget.Dialog",
    [dijit.Dialog],
    {
        widgetId: null,
        loadingMessage:"<div class='indicatorDialog'><div class='indicator'>\u8aad\u307f\u8fbc\u307f\u4e2d...</div></div>",
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
                if (aipo.schedule.tmpScroll == "undefined") {
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
            
            var focusNode = dojo.byId( this.widgetId );
            if ( focusNode ) {
                focusNode.focus();
                
                if (this._callback != null) {
                    this._callback.call(this._callback, this._portlet_id);
                }
            } 
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
            style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
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
        }    
    }
);

}

}});