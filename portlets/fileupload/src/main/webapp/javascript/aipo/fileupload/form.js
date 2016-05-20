/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
dojo.provide("aipo.fileupload");

aipo.fileupload.getFolderName = function() {
    var obj = dojo.byId("folderName");
}

aipo.fileupload.onAddFileInfo = function(foldername, fileid, filename, pid) {
    var ul = dojo.byId('attachments_' + pid);

    if(ul.nodeName.toLowerCase()=="ul")
    	aimluck.io.addFileToList(ul,fileid,filename);
    else
    	aimluck.io.addOption(ul,fileid,filename, false);//MultipeによるSelectとの互換性維持
    dojo.byId('folderName_' + pid).value =  foldername;

    var modalDialog = document.getElementById('modalDialog');
    if(modalDialog) {
    	var wrapper = document.getElementById('wrapper');
    	wrapper.style.minHeight = modalDialog.clientHeight + 'px';
    }
}

aipo.fileupload.replaceFileInfo = function(foldername, fileid, filename, pid) {
    var ul = dojo.byId('attachments_' + pid);

    if(ul.nodeName.toLowerCase()=="ul")
    	aimluck.io.replaceFileToList(ul,fileid,filename);
    else
    	aimluck.io.addOption(ul,fileid,filename, false);//MultipeによるSelectとの互換性維持
    dojo.byId('folderName_' + pid).value =  foldername;
}

aipo.fileupload.openAttachment = function(url, pid){
    var wx = 430;
    var wy = 130;
    var x = (screen.width  - wx) / 2;
    var y = (screen.height - wy) / 2;

    var ul = dojo.byId('attachments_' + pid);

    if(ul.nodeName.toLowerCase()=="ul")
   	 var ullength=ul.children.length;
   else{
   	   	var ullength = ul.options.length;
    	if(ullength == 1 && ul.options[0].value == ''){
    		ullength = 0;
    	}
   }
    var folderName = dojo.byId('folderName_' + pid).value;
    var attachment_subwin = window.open(url+'&nsize='+ullength+'&folderName='+folderName,"attachment_window","left="+x+",top="+y+",width="+wx+",height="+wy+",resizable=yes,status=yes");
    attachment_subwin.focus();
}

aipo.fileupload.ImageDialog

aipo.fileupload.showImageDialog = function(url, portlet_id, callback) {
	var dialog=dojo.byId('imageDialog');
	//dojo.query("#imageDialog").addClass("preLoadImage");
	aipo.fileupload.ImageDialog = dijit.byId("imageDialog");
    dojo.query(".auiPopup:not(.imgPopup)").addClass("mb_dialoghide");
    dojo.query("#imageDialog").addClass("mb_dialog");

    if(! aipo.fileupload.ImageDialog){
    	aipo.fileupload.ImageDialog = new aipo.fileupload.widget.FileuploadViewDialog({widgetId:'imageDialog', _portlet_id: portlet_id, _callback:callback}, "imageDialog");
    	//dojo.query("#imageDialog").addClass("preLoadImage");
    }else{
    	aipo.fileupload.ImageDialog.setCallback(portlet_id, callback);
    }
    if(aipo.fileupload.ImageDialog){
    	aipo.fileupload.ImageDialog.setHref(url);
    	aipo.fileupload.ImageDialog.show();
    }
};

aipo.fileupload.hideImageDialog = function() {
    var arrDialog = dijit.byId("imageDialog");

    if(arrDialog){
      arrDialog.hide();
      var _dialog = dojo.byId("imageDialog");
      if(_dialog){
          _dialog.style.height = "";
          _dialog.style.width = "";
      }
    }
};

aipo.fileupload.onLoadImage=function(image){
	dojo.style(image, 'display', '');
	dojo.query(".indicatorDialog").style('display', 'none');
	var dialog=dojo.byId('imageDialog');
	if(dojo.style(image, 'max-width')){
		dialog.style.width= Math.min(image.width, dojo.style(image, 'max-width'))+"px";
	} else {
		dialog.style.width= image.width + "px";
	}
	if(dojo.style(image, 'max-height')){
		dialog.style['min-height'] = Math.min(image.height, dojo.style(image, 'max-height'))+"px";
	} else {
		dialog.style['min-height'] = image.height + "px";
	}
	aipo.fileupload.ImageDialog._position();//再調整
	//dojo.query("#imageDialog").removeClass("preLoadImage");
};

aipo.fileupload.removeFileFromList=function(ul, li, pid){
	dojo.style("facephoto_" + pid, "display", "none");
	return ul.removeChild(li);
};

aipo.fileupload.YoutubeDialog

aipo.fileupload.showYoutubeDialog = function(vid, url, portlet_id, callback) {
    if(!aipo.fileupload.YoutubeDialog){
    	aipo.fileupload.YoutubeDialog = new aipo.fileupload.widget.YoutubeDialog({widgetId:'YoutubeDialog', _portlet_id: portlet_id, _callback:callback}, "YoutubeDialog");
    }else{
    	aipo.fileupload.YoutubeDialog.setCallback(portlet_id, callback);
    }
    if(aipo.fileupload.YoutubeDialog){
    	aipo.fileupload.YoutubeDialog.setHref(url);
    	aipo.fileupload.YoutubeDialog.show();
    }
};

aipo.fileupload.hideYoutubeDialog = function() {
    var arrDialog = dijit.byId("aipo_fileupload_widget_YoutubeDialog_0");

    if(arrDialog){
      arrDialog.hide();
    }
};

aipo.fileupload.onLoadYoutube=function(frame){
	var dialog=dojo.byId('aipo_fileupload_widget_YoutubeDialog_0');
	dialog.style.width=frame.width+"px";
	aipo.fileupload.YoutubeDialog._position();
	dojo.query("#youtubeDialog").removeClass("preLoadImage");
};

