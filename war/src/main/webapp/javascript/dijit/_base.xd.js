dojo._xdResourceLoaded({
depends: [["provide", "dijit._base"],
["require", "dijit._base.focus"],
["require", "dijit._base.manager"],
["require", "dijit._base.place"],
["require", "dijit._base.popup"],
["require", "dijit._base.scroll"],
["require", "dijit._base.sniff"],
["require", "dijit._base.bidi"],
["require", "dijit._base.typematic"],
["require", "dijit._base.wai"],
["require", "dijit._base.window"]],
defineResource: function(dojo){if(!dojo._hasResource["dijit._base"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base"] = true;
dojo.provide("dijit._base");

dojo.require("dijit._base.focus");
dojo.require("dijit._base.manager");
dojo.require("dijit._base.place");
dojo.require("dijit._base.popup");
dojo.require("dijit._base.scroll");
dojo.require("dijit._base.sniff");
dojo.require("dijit._base.bidi");
dojo.require("dijit._base.typematic");
dojo.require("dijit._base.wai");
dojo.require("dijit._base.window");

}

}});