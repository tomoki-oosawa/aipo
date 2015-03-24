dojo._xdResourceLoaded({
depends: [["provide", "dojox.storage"],
["require", "dojox.storage._common"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.storage"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.storage"] = true;
dojo.provide("dojox.storage");
dojo.require("dojox.storage._common");

}

}});