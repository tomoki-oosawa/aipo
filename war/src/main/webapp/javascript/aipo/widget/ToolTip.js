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
if(!dojo._hasResource["aipo.widget.ToolTip"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aipo.widget.ToolTip"] = true;

dojo.provide("aipo.widget.ToolTip");
dojo.require("dijit.Tooltip");

dojo.declare( "aipo.widget._MasterToolTip", [dijit._MasterTooltip], {
   duration: 100,
   postCreate: function(){
            dojo.body().appendChild(this.domNode);
            this.bgIframe = new dijit.BackgroundIframe(this.domNode);
   },
   show: function(/*String*/ innerHTML, /*DomNode*/ aroundNode, callback, targetNode){
            // summary:
            //  Display tooltip w/specified contents to right specified node
            //  (To left if there's no space on the right, or if LTR==right)

            if(this.aroundNode && this.aroundNode === aroundNode){
                return;
            }

            if( aroundNode  == null || aroundNode == "undefined" ){
                return;
            }

            if( this.domNode ==null || this.domNode == "undefined" ){
                return;
            }

            this.containerNode.innerHTML=innerHTML;

           // Firefox bug. when innerHTML changes to be shorter than previous
           // one, the node size will not be updated until it moves.
            this.domNode.style.width = "150px";
            this.domNode.style.top = (this.domNode.offsetTop + 1) + "px";
            try {
	            // position the element and change CSS according to position
	            var align = this.isLeftToRight() ? {'BR': 'BL', 'BL': 'BR'} : {'BL': 'BR', 'BR': 'BL'};
	            var pos = dijit.placeOnScreenAroundElement(this.domNode, aroundNode, align);
	            this.domNode.className="dijitTooltip dijitTooltip" + (pos.corner=='BL' ? "Right" : "Left");//FIXME: might overwrite class
            }catch (e) {
                this.hide(aroundNode);
                return;
            }
            if(parseInt(this.domNode.style.left ) < 1){
                this.domNode.style.top = -10000 + "px";
            }else{
                var y = parseInt(aipo.widget.tmpY) - 36 ;
                this.domNode.style.top = y + "px";
            }
            // show it
            dojo.style(this.domNode, "opacity", 1.0);
            //this.fadeIn.play();
            this.isShowingNow = true;
            this.aroundNode = aroundNode;

            if (callback) {
                callback(this.containerNode, targetNode);
            }
        },
        hide: function(aroundNode){
            // summary: hide the tooltip
            if(this.domNode)this.domNode.zIndex = 0;
            if(!this.aroundNode || this.aroundNode !== aroundNode){
                return;
            }
            if(this._onDeck){
                // this hide request is for a show() that hasn't even started yet;
                // just cancel the pending show()
                this._onDeck=null;
                return;
            }
            //this.fadeIn.stop();
            this.domNode.style.top = -10000 + "px";
            dojo.style(this.domNode, "opacity", 0.0);
            this.isShowingNow = false;
            this.aroundNode = null;
            //this.fadeOut.play();
        }
});

aipo.widget._masterTT = null;

aipo.widget.showTooltip = function(/*String*/ innerHTML, /*DomNode*/ aroundNode, callback, targetNode){
    // summary:
    //  Display tooltip w/specified contents to right specified node
    //  (To left if there's no space on the right, or if LTR==right)
    if(!aipo.widget._masterTT){ aipo.widget._masterTT = new aipo.widget._MasterToolTip(); }
    return aipo.widget._masterTT.show(innerHTML, aroundNode, callback, targetNode);
};

aipo.widget.hideTooltip = function(aroundNode){
    // summary: hide the tooltip
    //if(!aipo.widget._masterTT){ aipo.widget._masterTT = new aipo.widget._MasterTooltip(); }
    if(!aipo.widget._masterTT){ return;}
    return aipo.widget._masterTT.hide(aroundNode);
};

dojo.declare( "aipo.widget.ToolTip", [dijit.Tooltip], {
        origZIndex: 0,
        _portletId: null,
        _callback: null,
        constructor: function(params, pid, callback){
            this._portletId = pid;
            this._callback = callback;
        },
        open: function(/*DomNode*/ target){
            // summary: display the tooltip; usually not called directly.
            target = target || this._connectNodes[0];
            if(!target){ return; }
            if(this._showTimer){
                clearTimeout(this._showTimer);
                delete this._showTimer;
            }
             aipo.widget.showTooltip(this.label || this.domNode.innerHTML, target, this._callback, this._connectNodes[0]);
             this._connectNode = target;
        },
        close: function(){
            // summary: hide the tooltip; usually not called directly.
            aipo.widget.hideTooltip(this._connectNode);
            delete this._connectNode;
            if(this._showTimer){
                clearTimeout(this._showTimer);
                delete this._showTimer;
            }
        },
        _onHover: function(/*Event*/ e){
            if(ptConfig[this._portletId].isTooltipEnable != true){return;}
            if(!this._showTimer){
                var target = e.target;
                aipo.widget.tmpX = e.pageX;
                aipo.widget.tmpY = e.pageY;
                this._showTimer = setTimeout(dojo.hitch(this, function(){this.open(target)}), this.showDelay);
            }
       },_onUnHover: function(/*Event*/ e){
            // keep a tooltip open if the associated element has focus
            if(this._focus){ return; }
            if(this._showTimer){
                clearTimeout(this._showTimer);
                delete this._showTimer;
            }
            this.close();
        }
});


}
