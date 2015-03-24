dojo._xdResourceLoaded({
depends: [["provide", "dojox.cometd"],
["require", "dojox._cometd.cometd"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.cometd"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.cometd"] = true;
// stub loader for the cometd module since no implementation code is allowed to live in top-level files
dojo.provide("dojox.cometd");
dojo.require("dojox._cometd.cometd");

}

}});