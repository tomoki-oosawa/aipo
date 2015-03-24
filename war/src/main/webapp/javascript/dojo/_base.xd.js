dojo._xdResourceLoaded({
depends: [["provide", "dojo._base"],
["require", "dojo._base.lang"],
["require", "dojo._base.declare"],
["require", "dojo._base.connect"],
["require", "dojo._base.Deferred"],
["require", "dojo._base.json"],
["require", "dojo._base.array"],
["require", "dojo._base.Color"],
["requireIf", dojo.isBrowser, "dojo._base.window"],
["requireIf", dojo.isBrowser, "dojo._base.event"],
["requireIf", dojo.isBrowser, "dojo._base.html"],
["requireIf", dojo.isBrowser, "dojo._base.NodeList"],
["requireIf", dojo.isBrowser, "dojo._base.query"],
["requireIf", dojo.isBrowser, "dojo._base.xhr"],
["requireIf", dojo.isBrowser, "dojo._base.fx"]],
defineResource: function(dojo){if(!dojo._hasResource["dojo._base"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo._base"] = true;
dojo.provide("dojo._base");
dojo.require("dojo._base.lang");
dojo.require("dojo._base.declare");
dojo.require("dojo._base.connect");
dojo.require("dojo._base.Deferred");
dojo.require("dojo._base.json");
dojo.require("dojo._base.array");
dojo.require("dojo._base.Color");
dojo.requireIf(dojo.isBrowser, "dojo._base.window");
dojo.requireIf(dojo.isBrowser, "dojo._base.event");
dojo.requireIf(dojo.isBrowser, "dojo._base.html");
dojo.requireIf(dojo.isBrowser, "dojo._base.NodeList");
dojo.requireIf(dojo.isBrowser, "dojo._base.query");
dojo.requireIf(dojo.isBrowser, "dojo._base.xhr");
dojo.requireIf(dojo.isBrowser, "dojo._base.fx");

(function(){
	if(djConfig.require){
		for(var x=0; x<djConfig.require.length; x++){
			dojo["require"](djConfig.require[x]);
		}
	}
})();

}

}});