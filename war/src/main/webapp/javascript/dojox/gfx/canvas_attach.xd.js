dojo._xdResourceLoaded({
depends: [["require", "dojox.gfx.canvas"]],
defineResource: function(dojo){dojo.require("dojox.gfx.canvas");

dojo.experimental("dojox.gfx.canvas_attach");

// not implemented
dojox.gfx.attachNode = function(){
	return null;	// for now
};

}});