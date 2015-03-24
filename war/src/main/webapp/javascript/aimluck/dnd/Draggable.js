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
if(!dojo._hasResource["aimluck.dnd.DragMoveObject"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["aimluck.dnd.DragMoveObject"] = true;

dojo.provide("aimluck.dnd.DragMoveObject");
dojo.provide("aimluck.dnd.Draggable");

dojo.require("dojo.dnd.Mover");
dojo.require("dojo.dnd.Moveable");
dojo.require("dojo.parser");
dojo.require("dojo.dnd.Source");

// aimluck.dnd.Draggable

dojo.declare("aimluck.dnd.DragMoveObject", [dojo.dnd.Mover] , {
    _pageY: 0,
    _pageX: 0,
    portletId: null,
    leftTop: null,
    onFirstMove:function (e) {
	    dojo.dnd.Mover.prototype.onFirstMove.apply(this, arguments);
    },
    onMouseUp:function (e) {
        dojo.dnd.Mover.prototype.onMouseUp.apply(this, arguments);
    },
    onMouseDown: function(e){
        var m = this.marginBox;
//        this._origOpacity_  = dojo.style(this.node, "opacity");
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
        dojo.dnd.Mover.prototype.onMouseDown.apply(this, arguments);
    },
    onMouseMove: function(e){
        this._pageX = e.pageX;
        this._pageY = e.pageY;
        dojo.dnd.autoScroll(e);
        var m = this.marginBox;
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
    //  dojo.dnd.Mover.prototype.onMouseMove.apply(this, arguments);
    }
});

dojo.declare("aimluck.dnd.Draggable", dojo.dnd.Moveable , {
    DragMoveObject: aimluck.dnd.DragMoveObject,
    portletId: null,
    constructor: function(node, params){
        this.portletId = params.pid;
    },
    onMouseDown: function(e){
        // summary: event processor for onmousedown, creates a Mover for the node
        // e: Event: mouse event

        if(this.skip && dojo.dnd.isFormElement(e)){ return; }
        if(this.delay){
            this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"));
            this.events.push(dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
        }else{
            dragObj = new this.DragMoveObject(this.node, e, this);
            dragObj.dragSource=this;
            dragObj.portletId = this.portletId;
        }

        dragObj._pageX = e.pageX;
        dragObj._pageY = e.pageY;

        this._lastX = e.pageX;
        this._lastY = e.pageY;

        dojo.stopEvent(e);
    }
});

}
