dojo._xdResourceLoaded({
depends: [["require", "dojox._sql.common"],
["provide", "dojox.sql"]],
defineResource: function(dojo){if(!dojo._hasResource["dojox.sql"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.sql"] = true;
dojo.require("dojox._sql.common");
dojo.provide("dojox.sql");

}

}});