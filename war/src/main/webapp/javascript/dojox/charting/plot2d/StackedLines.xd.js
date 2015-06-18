dojo._xdResourceLoaded({
depends: [["provide", "dojox.charting.plot2d.StackedLines"],
["require", "dojox.charting.plot2d.Stacked"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.charting.plot2d.StackedLines"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.charting.plot2d.StackedLines"] = true;
dojo.provide("dojox.charting.plot2d.StackedLines");

dojo.require("dojox.charting.plot2d.Stacked");

dojo.declare("dojox.charting.plot2d.StackedLines", dojox.charting.plot2d.Stacked, {
	constructor: function(){
		this.opt.lines = true;
	}
});

}

}});