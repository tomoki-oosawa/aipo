/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
dojo.provide("aipo.fileuploadadv");

/**
 * 一時アップロードのファイル名表示欄に追加する
 */
aipo.fileuploadadv.onAddFileInfo = function(foldername, fileid, filename, pid, keyid) {

	var attachments;
	if (keyid) {
		attachments = 'attachments_' + pid + '_' + keyid;
	} else {
		attachments = 'attachments_' + pid;
	}

	var folderKey;
	if (keyid) {
		folderKey = 'folderName_' + pid + '_' + keyid
	} else {
		folderKey = 'folderName_' + pid;
	}

    var ul = dojo.byId(attachments);

    if(ul.nodeName.toLowerCase() == "ul")
    	aimluck.io.addFileToList(ul, fileid, filename);
    else
    	aimluck.io.addOption(ul, fileid, filename, false); //MultipeによるSelectとの互換性維持
    dojo.byId(folderKey).value = foldername;
}

/**
 * 一時アップロードのファイル名表示欄を更新する
 */
aipo.fileuploadadv.replaceFileInfo = function(foldername, fileid, filename, pid, keyid) {

	var attachments;
	if (keyid) {
		attachments = 'attachments_' + pid + '_' + keyid;
	} else {
		attachments = 'attachments_' + pid;
	}

	var folderKey;
	if (keyid) {
		folderKey = 'folderName_' + pid + '_' + keyid
	} else {
		folderKey = 'folderName_' + pid;
	}

    var ul = dojo.byId(attachments);

    if(ul.nodeName.toLowerCase() == "ul")
    	aimluck.io.replaceFileToList(ul, fileid, filename);
    else
    	aimluck.io.addOption(ul, fileid, filename, false); //MultipeによるSelectとの互換性維持
    dojo.byId(folderKey).value = foldername;
}

aipo.fileuploadadv.createSelectFromFileList = function(form, pid){
	var ulList = new Array();
	var idList = new Array();
	var nmList = new Array();

	var ulElem = dojo.byId("attachments_" + pid);
	if (ulElem) {
		ulList.push(ulElem);
		idList.push("attachments_select");
		nmList.push("attachments");
	} else {
		var elems=document.all? document.all.tags("ul"):document.querySelectorAll("ul");

		for (var i = 0; i < elems.length; i++) {
			if (elems[i].id.indexOf("attachments_" + pid + "_") == 0) {
				var itemId = elems[i].id.replace("attachments_" + pid + "_", "");
				ulList.push(elems[i]);
				idList.push("attachments_select_" + itemId);
				nmList.push("attachments_" + itemId);
			}
		}
	}

    for (var i = 0; i < ulList.length; i++) {
        var select = document.createElement("select");
        select.style.display = "none";
        select.id = idList[i];
        select.multiple = "multiple";
        select.name = nmList[i];

    	var lilist = ulList[i].children;
    	for (var j = 0; j < lilist.length; j++) {
        	var option = document.createElement("option");
        	option.value = lilist[j].getAttribute("data-fileid");
        	option.text = lilist[j].getAttribute("data-filename");
        	option.selected = true;
        	select.appendChild(option);
    	}
        form.appendChild(select);
    }

}
