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

aipo.fileupload.onAddFileInfo = function(foldername, fileid, filename) {
    var ul = dojo.byId('attachments');
    
    if(ul.nodeName.toLowerCase()=="ul")
    	aimluck.io.addFileToList(ul,fileid,filename);
    else
    	aimluck.io.addOption(ul,fileid,filename, false);//MultipeによるSelectとの互換性維持
    dojo.byId('folderName').value =  foldername;
}

aipo.fileupload.openAttachment = function(url){
    var wx = 430;
    var wy = 130;
    var x = (screen.width  - wx) / 2;
    var y = (screen.height - wy) / 2;

    var ul = dojo.byId('attachments');
    
    if(ul.nodeName.toLowerCase()=="ul")
   	 var ullength=ul.children.length;
   else{
   	   	var ullength = ul.options.length;
    	if(ullength == 1 && ul.options[0].value == ''){
    		ullength = 0;
    	}  
   } 
    var folderName = dojo.byId('folderName').value;
    var attachment_subwin = window.open(url+'&nsize='+ullength+'&folderName='+folderName,"attachment_window","left="+x+",top="+y+",width="+wx+",height="+wy+",resizable=yes,status=yes");
    attachment_subwin.focus();
}

