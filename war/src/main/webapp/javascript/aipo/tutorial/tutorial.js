dojo.provide('aipo.tutorial.tutorial');

var show_flag = false;
var url, portlet_id, callback;
var schedule_load_flag = false;

aipo.tutorial.showDialog=function(url_value, portlet_id_value, callback_value){
  show_flag = true;
  url = url_value;
  portlet_id = portlet_id_value;
  callback = callback_value;
  if(state != 6){
    aipo.tutorial.setTooltips();
    aipo.tutorial.showFirst();
  }else{
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
  }
};


aipo.tutorial.showFirst = function(){

//  var schedule = document.getElementById('tutorial_schedule');
//  if(typeof schedule != "undefined"){
//    schedule.style.display = "";
//  }

  var timeline1 = document.getElementById('tutorial_timeline1');
  if(typeof timeline1 != "undefined"){
    timeline1.style.display = "";
  }

  var timeline2 = document.getElementById('tutorial_timeline2');
  if(typeof timeline2 != "undefined"){
    timeline2.style.display = "";
  }
};



aipo.tutorial.scheduleLoad = function(){
  if(!show_flag)return;

  schedule_load_flag = true;
  aipo.tutorial.setTooltips();
  var schedule = document.getElementById('tutorial_schedule');
  if(typeof schedule != "undefined"){
    schedule.style.display = "";
  }
};


aipo.tutorial.setTooltips=function(){

  var match_schedule;
  var x=-100,y=-100, width = 0, height = 0;
  if(schedule_load_flag){
    selectObj = document.getElementsByTagName('table');
    match_schedule = "weeklyScrollPane_P"
      matchObj= new RegExp(match_schedule);
    for(i=0; i < selectObj.length; i++){
      if(selectObj[i].id.match(matchObj)){

        height = selectObj[i].offsetHeight;
        break;
      }
    }


    var table_height = 0;
    selectObj = document.getElementsByTagName('table');
    match_schedule = "weeklyTableHead_P"
      matchObj= new RegExp(match_schedule);
    for(i=0; i < selectObj.length; i++){
      if(selectObj[i].id.match(matchObj)){
        table_height = selectObj[i].offsetHeight;
        break;
      }
    }

    selectObj = document.getElementsByTagName('div');
    match_schedule = "weeklyScrollPane_P"
      matchObj= new RegExp(match_schedule);
    for(i=0; i < selectObj.length; i++){
      if(selectObj[i].id.match(matchObj)){
        width = selectObj[i].offsetWidth;
        table_height += selectObj[i].offsetHeight;
        break;
      }
    }

   var schedule = document.getElementById('tutorial_schedule');
    if(typeof schedule != "undefined"){
      var display_state = schedule.style.display;
      schedule.style.top = -3000 + "px";
      schedule.style.display = "";
      schedule.style.left = ((width - schedule.offsetWidth) / 2) + "px";
      schedule.style.top = display_state;
      schedule.style.top = (table_height / 3) + "px";
      schedule.parentNode.style.position = "relative";
    }
  }

  selectObj = document.getElementsByTagName('iframe');
  var match_timeline1 = "if_fileupload_P"
  matchObj= new RegExp(match_timeline1);
  x=-100;
  y=-100;
  width = 0;
  for(i=0; i < selectObj.length; i++){
    if(selectObj[i].id.match(matchObj)){
      x = selectObj[i].offsetLeft;
      width = selectObj[i].offsetWidth;
      height = selectObj[i].offsetHeight;
      y = selectObj[i].offsetTop;
      break;
    }
  }
  var timeline1 = document.getElementById('tutorial_timeline1');
  if(typeof timeline1 != "undefined"){
    timeline1.style.left = x + width + "px";
    timeline1.style.top = y + (height / 2) +  "px";
    timeline1.parentNode.style.position = "relative";
  }

  selectObj = document.getElementsByTagName('form');
  var match_timeline2 = "likeForm_P"
  matchObj= new RegExp(match_timeline2);
  x=-100;
  y=-100;
  for(i=0; i < selectObj.length; i++){
    if(selectObj[i].id.match(matchObj)){
      x = selectObj[i].offsetLeft;
      y = selectObj[i].offsetTop + 15;
      height = selectObj[i].offsetHeight;
      break;
    }
  }
  var timeline2 = document.getElementById('tutorial_timeline2');
  if(typeof timeline2 != "undefined"){
    timeline2.style.left = x + "px";
    timeline2.style.top = y + height + "px";
    timeline2.parentNode.style.position = "relative";
  }

  height = document.getElementsByClassName('hdNavi')[0].offsetHeight;
  var activity = document.getElementById('tutorial_activity');
  if(typeof activity != "undefined"){
    activity.style.top = height + "px";
  }
//  var support = document.getElementById('tutorial_support');
//  if(typeof support != "undefined"){
//    support.style.top = height + "px";
//  }
  var user = document.getElementById('tutorial_user');
  if(typeof user != "undefined"){
    user.style.top = height + "px";
  }


};

var state = 0;
aipo.tutorial.hideTip=function(id){
  document.getElementById(id).style.display = "none";
  if(id == "tutorial_schedule")
    state++;
  if(id == "tutorial_timeline1")
    state++;
  if(id == "tutorial_timeline2")
    state++;
  if(id == "tutorial_activity")
    state++;
//  if(id == "tutorial_support")
//    state++;
  if(id == "tutorial_user")
    state++;

  if(state == 3){
    document.getElementById('tutorial_activity').style.display = "";
  }
//  else if(state == 4){
//    document.getElementById('tutorial_support').style.display = "";
//  }
  else if(state == 4){
    document.getElementById('tutorial_user').style.display = "";
  }
  else if(state == 5){
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
        loadingMessage:"<div class='indicator'>読み込み中...</div>",
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