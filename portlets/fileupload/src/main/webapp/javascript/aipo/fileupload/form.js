/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
	aipo.fileupload.ImageDialog = dijit.byId("imageDialog");
    dojo.query(".roundBlockContent").addClass("mb_dialoghide");
    dojo.query("#imageDialog").addClass("mb_dialog");

    if(! aipo.fileupload.ImageDialog){
    	aipo.fileupload.ImageDialog = new aipo.fileupload.widget.FileuploadViewDialog({widgetId:'imageDialog', _portlet_id: portlet_id, _callback:callback}, "imageDialog");
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
    }
};

aipo.fileupload.onLoadImage=function(image){
	var dialog=dojo.byId('imageDialog');
	dialog.style.visibility="hidden";
	dialog.style.width=image.width+"px";
	dialog.style.height=image.height+"px";
	aipo.fileupload.ImageDialog._position();//再調整
	dialog.style.visibility="visible";
};


