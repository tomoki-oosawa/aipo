dojo.provide('aipo.tutorial');

aipo.tutorial.showDialog = function(url, portlet_id, callback) {
	var dialog = dijit.byId("imageDialog");
    dojo.query(".roundBlockContent").addClass("mb_dialoghide");
    dojo.query("#imageDialog").addClass("mb_dialog");

    if(!dialog){
    	dialog = new aipo.widget.TutorialDialog({widgetId:'imageDialog', _portlet_id: portlet_id, _callback:callback}, "imageDialog");
    }else{
    	dialog.setCallback(portlet_id, callback);
    }
    if(dialog){
    	dialog.setHref(url);
    	dialog.show();
    }
};

aipo.tutorial.hideDialog = function() {
    var dialog = dijit.byId("imageDialog");

    if(dialog){
    	dialog.hide();
    }
};

aipo.tutorial.onLoadImage=function(image){
	var dialog=dojo.byId('imageDialog');
	dialog.style.visibility="hidden";
	dialog.style.width=1050+"px";
	dialog.style.height=650+"px";
	dijit.byId("imageDialog")._position();//再調整
	dialog.style.visibility="visible";
};

aipo.tutorial.nextPage=function(){
	var page = dojo.byId('page_tutorial');
	var val = page.value - 0;
	dojo.byId('popupImage' + val).style.display = "none";
	if(val == 1){
		dojo.byId('tutorial_prev').style.display = "";
	}
	val++;
	dojo.byId('popupImage' + val).style.display = "";
	if(val == 3){
		dojo.byId('tutorial_next').style.display = "none";
	}
	page.value = val + "";
};

aipo.tutorial.prevPage=function(){
	var page = dojo.byId('page_tutorial');
	var val = page.value - 0;
	dojo.byId('popupImage' + val).style.display = "none";
	if(val == 3){
		dojo.byId('tutorial_next').style.display = "";
	}
	val--;
	dojo.byId('popupImage' + val).style.display = "";
	if(val == 1){
		dojo.byId('tutorial_prev').style.display = "none";
	}
	page.value = val + "";
};

dojo.provide("aipo.widget.TutorialDialog");
dojo.provide("aipo.widget.TutorialDialogUnderlay");

dojo.require("aimluck.widget.Dialog");

dojo.declare(
    "aipo.widget.TutorialDialogUnderlay",
    [aimluck.widget.DialogUnderlay],
    {
       templateString: "<div class='tutorialDialogUnderlayWrapper modalDialogUnderlayWrapper' id='${id}_underlay'><div class='tutorialDialogUnderlay modalDialogUnderlay' dojoAttachPoint='node'></div></div>"
    }

);

dojo.declare(
    "aipo.widget.TutorialDialog",
    [aimluck.widget.Dialog],
    {
        loadingMessage:"<div class='indicator'>$l10n.SCHEDULE_READING</div>",
        templateCssString:"tutorialDialog",
        templateString:"<div id='tutorialDialog' class='${templateCssString}' dojoattachpoint='wrapper'><span dojoattachpoint='tabStartOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap'tabindex='0'></span><span dojoattachpoint='tabStart' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><div dojoattachpoint='containerNode' style='position: relative; z-index: 2;'></div><span dojoattachpoint='tabEnd' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><span dojoattachpoint='tabEndOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span></div>",
        _setup: function(){

            this._modalconnects = [];

            if(this.titleBar){
                this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
            }

            this._underlay = new aipo.widget.TutorialDialogUnderlay();

            var node = this.domNode;
            this._fadeIn = dojo.fx.combine(
                [dojo.fadeIn({
                    node: node,
                    duration: this.duration
                 }),
                 dojo.fadeIn({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onBegin: dojo.hitch(this._underlay, "show")
                 })
                ]
            );

            this._fadeOut = dojo.fx.combine(
                [dojo.fadeOut({
                    node: node,
                    duration: this.duration,
                    onEnd: function(){
                        node.style.display="none";
                    }
                 }),
                 dojo.fadeOut({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onEnd: dojo.hitch(this._underlay, "hide")
                 })
                ]
            );
            node.style.visibility="hidden";
        },
        onLoad: function(){
            // when href is specified we need to reposition
            // the dialog after the data is loaded
            this._position();
            aimluck.widget.Dialog.superclass.onLoad.call(this);
        }
    }
);