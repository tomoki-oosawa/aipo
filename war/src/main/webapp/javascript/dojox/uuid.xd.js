dojo._xdResourceLoaded({
depends: [["provide", "dojox.uuid"],
["require", "dojox.uuid._base"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.uuid"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.uuid"] = true;
dojo.provide("dojox.uuid");
dojo.require("dojox.uuid._base");

}

}});