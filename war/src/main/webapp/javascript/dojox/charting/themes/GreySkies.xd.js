dojo._xdResourceLoaded({
depends: [["provide", "dojox.charting.themes.GreySkies"],
["require", "dojox.charting.Theme"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.charting.themes.GreySkies"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.charting.themes.GreySkies"] = true;
dojo.provide("dojox.charting.themes.GreySkies");
dojo.require("dojox.charting.Theme");

(function(){
	var dxc=dojox.charting;
	dxc.themes.GreySkies=new dxc.Theme(dxc.Theme._def);
})();

}

}});