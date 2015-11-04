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
if(typeof djConfig["xDomainBasePath"] == "undefined") {
dojo.registerModulePath("dojo","../dojo");
dojo.registerModulePath("dijit","../dijit");
dojo.registerModulePath("dojox","../dojox");
dojo.registerModulePath("aimluck","../aimluck");
dojo.registerModulePath("aipo","../aipo");
} else {
djConfig.useXDomain = true;
dojo.registerModulePath("dojo", djConfig.xDomainBasePath + "/dojo");
dojo.registerModulePath("dijit", djConfig.xDomainBasePath + "/dijit");
dojo.registerModulePath("dojox", djConfig.xDomainBasePath + "/dojox");
dojo.registerModulePath("aimluck", djConfig.xDomainBasePath + "/aimluck");
dojo.registerModulePath("aipo", djConfig.xDomainBasePath + "/aipo");
}
