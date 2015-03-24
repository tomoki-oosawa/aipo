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
dojo._xdResourceLoaded({

defineResource: function(dojo){

aimluck.namespace("utils");

aimluck.utils.createCSS = function(url) {
    if(document.createStyleSheet){ //IE
        document.createStyleSheet(url);
    } else { //other browser
        var head = document.getElementsByTagName("head")[0];
        var stylesheet = document.createElement("link");
        with(stylesheet){
            rel="stylesheet";
            type="text/css";
            href=url;
        }
        head.appendChild(stylesheet);
    }
};

}});