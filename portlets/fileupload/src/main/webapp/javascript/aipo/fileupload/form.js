/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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
//    if(obj){
//        obj.focus();
//    }

}

aipo.fileupload.onAddFileInfo = function(foldername, fileid, filename) {
    var select = dojo.byId('attachments');
    var value = fileid;
    var text = filename;
    aimluck.io.addOption(select, value, text, false); 
    
    dojo.byId('folderName').value =  foldername;
}
                  
aipo.fileupload.openAttachment = function(url){
    var wx = 430;
    var wy = 175;
    var x = (screen.width  - wx) / 2;
    var y = (screen.height - wy) / 2;
    
    var select = dojo.byId('attachments');
    var select_len = select.options.length;
    if(select_len == 1 && select.options[0].value == ''){
        select_len = 0;
    }

    var attachment_subwin = window.open(url+'&nsize='+select_len,"attachment_window","left="+x+",top="+y+",width="+wx+",height="+wy+",resizable=yes,status=yes");
    attachment_subwin.focus();
}

