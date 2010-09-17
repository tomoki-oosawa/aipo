/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/modules.js
 */
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

dojo.registerModulePath("dojo","../dojo");
dojo.registerModulePath("dijit","../dijit");
dojo.registerModulePath("dojox","../dojox");
dojo.registerModulePath("aimluck","../aimluck");
dojo.registerModulePath("aipo","../aipo");
/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/modules.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/dojo/dojo-aipo.js
 */
/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(!dojo._hasResource["dojo.date"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.date"] = true;
dojo.provide("dojo.date");

dojo.date.getDaysInMonth = function(/*Date*/dateObject){
	//	summary:
	//		Returns the number of days in the month used by dateObject
	var month = dateObject.getMonth();
	var days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if(month == 1 && dojo.date.isLeapYear(dateObject)){ return 29; } // Number
	return days[month]; // Number
}

dojo.date.isLeapYear = function(/*Date*/dateObject){
	//	summary:
	//		Determines if the year of the dateObject is a leap year
	//	description:
	//		Leap years are years with an additional day YYYY-02-29, where the
	//		year number is a multiple of four with the following exception: If
	//		a year is a multiple of 100, then it is only a leap year if it is
	//		also a multiple of 400. For example, 1900 was not a leap year, but
	//		2000 is one.

	var year = dateObject.getFullYear();
	return !(year%400) || (!(year%4) && !!(year%100)); // Boolean
}

// FIXME: This is not localized
dojo.date.getTimezoneName = function(/*Date*/dateObject){
	//	summary:
	//		Get the user's time zone as provided by the browser
	// dateObject:
	//		Needed because the timezone may vary with time (daylight savings)
	//	description:
	//		Try to get time zone info from toString or toLocaleString method of
	//		the Date object -- UTC offset is not a time zone.  See
	//		http://www.twinsun.com/tz/tz-link.htm Note: results may be
	//		inconsistent across browsers.

	var str = dateObject.toString(); // Start looking in toString
	var tz = ''; // The result -- return empty string if nothing found
	var match;

	// First look for something in parentheses -- fast lookup, no regex
	var pos = str.indexOf('(');
	if(pos > -1){
		tz = str.substring(++pos, str.indexOf(')'));
	}else{
		// If at first you don't succeed ...
		// If IE knows about the TZ, it appears before the year
		// Capital letters or slash before a 4-digit year 
		// at the end of string
		var pat = /([A-Z\/]+) \d{4}$/;
		if((match = str.match(pat))){
			tz = match[1];
		}else{
		// Some browsers (e.g. Safari) glue the TZ on the end
		// of toLocaleString instead of putting it in toString
			str = dateObject.toLocaleString();
			// Capital letters or slash -- end of string, 
			// after space
			pat = / ([A-Z\/]+)$/;
			if((match = str.match(pat))){
				tz = match[1];
			}
		}
	}

	// Make sure it doesn't somehow end up return AM or PM
	return (tz == 'AM' || tz == 'PM') ? '' : tz; // String
}

// Utility methods to do arithmetic calculations with Dates

dojo.date.compare = function(/*Date*/date1, /*Date?*/date2, /*String?*/portion){
	//	summary:
	//		Compare two date objects by date, time, or both.
	//	description:
	//  	Returns 0 if equal, positive if a > b, else negative.
	//	date1:
	//		Date object
	//	date2:
	//		Date object.  If not specified, the current Date is used.
	//	portion:
	//		A string indicating the "date" or "time" portion of a Date object.
	//		Compares both "date" and "time" by default.  One of the following:
	//		"date", "time", "datetime"

	// Extra step required in copy for IE - see #3112
	date1 = new Date(Number(date1));
	date2 = new Date(Number(date2 || new Date()));

	if(typeof portion !== "undefined"){
		if(portion == "date"){
			// Ignore times and compare dates.
			date1.setHours(0, 0, 0, 0);
			date2.setHours(0, 0, 0, 0);
		}else if(portion == "time"){
			// Ignore dates and compare times.
			date1.setFullYear(0, 0, 0);
			date2.setFullYear(0, 0, 0);
		}
	}
	
	if(date1 > date2){ return 1; } // int
	if(date1 < date2){ return -1; } // int
	return 0; // int
};

dojo.date.add = function(/*Date*/date, /*String*/interval, /*int*/amount){
	//	summary:
	//		Add to a Date in intervals of different size, from milliseconds to years
	//	date: Date
	//		Date object to start with
	//	interval:
	//		A string representing the interval.  One of the following:
	//			"year", "month", "day", "hour", "minute", "second",
	//			"millisecond", "quarter", "week", "weekday"
	//	amount:
	//		How much to add to the date.

	var sum = new Date(Number(date)); // convert to Number before copying to accomodate IE (#3112)
	var fixOvershoot = false;
	var property = "Date";

	switch(interval){
		case "day":
			break;
		case "weekday":
			//i18n FIXME: assumes Saturday/Sunday weekend, but even this is not standard.  There are CLDR entries to localize this.
			var days, weeks;
			var adj = 0;
			// Divide the increment time span into weekspans plus leftover days
			// e.g., 8 days is one 5-day weekspan / and two leftover days
			// Can't have zero leftover days, so numbers divisible by 5 get
			// a days value of 5, and the remaining days make up the number of weeks
			var mod = amount % 5;
			if(!mod){
				days = (amount > 0) ? 5 : -5;
				weeks = (amount > 0) ? ((amount-5)/5) : ((amount+5)/5);
			}else{
				days = mod;
				weeks = parseInt(amount/5);
			}
			// Get weekday value for orig date param
			var strt = date.getDay();
			// Orig date is Sat / positive incrementer
			// Jump over Sun
			if(strt == 6 && amount > 0){
				adj = 1;
			}else if(strt == 0 && amount < 0){
			// Orig date is Sun / negative incrementer
			// Jump back over Sat
				adj = -1;
			}
			// Get weekday val for the new date
			var trgt = strt + days;
			// New date is on Sat or Sun
			if(trgt == 0 || trgt == 6){
				adj = (amount > 0) ? 2 : -2;
			}
			// Increment by number of weeks plus leftover days plus
			// weekend adjustments
			amount = 7 * weeks + days + adj;
			break;
		case "year":
			property = "FullYear";
			// Keep increment/decrement from 2/29 out of March
			fixOvershoot = true;
			break;
		case "week":
			amount *= 7;
			break;
		case "quarter":
			// Naive quarter is just three months
			amount *= 3;
			// fallthrough...
		case "month":
			// Reset to last day of month if you overshoot
			fixOvershoot = true;
			property = "Month";
			break;
		case "hour":
		case "minute":
		case "second":
		case "millisecond":
			property = "UTC" + interval.charAt(0).toUpperCase() + interval.substring(1) + "s";
	}

	if(property){
		sum["set"+property](sum["get"+property]()+amount);
	}

	if(fixOvershoot && (sum.getDate() < date.getDate())){
		sum.setDate(0);
	}

	return sum; // Date
};

dojo.date.difference = function(/*Date*/date1, /*Date?*/date2, /*String?*/interval){
	//	summary:
	//		Get the difference in a specific unit of time (e.g., number of
	//		months, weeks, days, etc.) between two dates, rounded to the
	//		nearest integer.
	//	date1:
	//		Date object
	//	date2:
	//		Date object.  If not specified, the current Date is used.
	//	interval:
	//		A string representing the interval.  One of the following:
	//			"year", "month", "day", "hour", "minute", "second",
	//			"millisecond", "quarter", "week", "weekday"
	//		Defaults to "day".

	date2 = date2 || new Date();
	interval = interval || "day";
	var yearDiff = date2.getFullYear() - date1.getFullYear();
	var delta = 1; // Integer return value

	switch(interval){
		case "quarter":
			var m1 = date1.getMonth();
			var m2 = date2.getMonth();
			// Figure out which quarter the months are in
			var q1 = Math.floor(m1/3) + 1;
			var q2 = Math.floor(m2/3) + 1;
			// Add quarters for any year difference between the dates
			q2 += (yearDiff * 4);
			delta = q2 - q1;
			break;
		case "weekday":
			var days = Math.round(dojo.date.difference(date1, date2, "day"));
			var weeks = parseInt(dojo.date.difference(date1, date2, "week"));
			var mod = days % 7;

			// Even number of weeks
			if(mod == 0){
				days = weeks*5;
			}else{
				// Weeks plus spare change (< 7 days)
				var adj = 0;
				var aDay = date1.getDay();
				var bDay = date2.getDay();

				weeks = parseInt(days/7);
				mod = days % 7;
				// Mark the date advanced by the number of
				// round weeks (may be zero)
				var dtMark = new Date(date1);
				dtMark.setDate(dtMark.getDate()+(weeks*7));
				var dayMark = dtMark.getDay();

				// Spare change days -- 6 or less
				if(days > 0){
					switch(true){
						// Range starts on Sat
						case aDay == 6:
							adj = -1;
							break;
						// Range starts on Sun
						case aDay == 0:
							adj = 0;
							break;
						// Range ends on Sat
						case bDay == 6:
							adj = -1;
							break;
						// Range ends on Sun
						case bDay == 0:
							adj = -2;
							break;
						// Range contains weekend
						case (dayMark + mod) > 5:
							adj = -2;
					}
				}else if(days < 0){
					switch(true){
						// Range starts on Sat
						case aDay == 6:
							adj = 0;
							break;
						// Range starts on Sun
						case aDay == 0:
							adj = 1;
							break;
						// Range ends on Sat
						case bDay == 6:
							adj = 2;
							break;
						// Range ends on Sun
						case bDay == 0:
							adj = 1;
							break;
						// Range contains weekend
						case (dayMark + mod) < 0:
							adj = 2;
					}
				}
				days += adj;
				days -= (weeks*2);
			}
			delta = days;
			break;
		case "year":
			delta = yearDiff;
			break;
		case "month":
			delta = (date2.getMonth() - date1.getMonth()) + (yearDiff * 12);
			break;
		case "week":
			// Truncate instead of rounding
			// Don't use Math.floor -- value may be negative
			delta = parseInt(dojo.date.difference(date1, date2, "day")/7);
			break;
		case "day":
			delta /= 24;
			// fallthrough
		case "hour":
			delta /= 60;
			// fallthrough
		case "minute":
			delta /= 60;
			// fallthrough
		case "second":
			delta /= 1000;
			// fallthrough
		case "millisecond":
			delta *= date2.getTime() - date1.getTime();
	}

	// Round for fractional values and DST leaps
	return Math.round(delta); // Number (integer)
};

}

if(!dojo._hasResource["dojo.fx"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.fx"] = true;
dojo.provide("dojo.fx");
dojo.provide("dojo.fx.Toggler");

dojo.fx.chain = function(/*dojo._Animation[]*/ animations){
	// summary: Chain a list of dojo._Animation s to run in sequence
	// example:
	//	|	dojo.fx.chain([
	//	|		dojo.fadeIn({ node:node }),
	//	|		dojo.fadeOut({ node:otherNode })
	//	|	]).play();
	//
	var first = animations.shift();
	var previous = first;
	dojo.forEach(animations, function(current){
		dojo.connect(previous, "onEnd", current, "play");
		previous = current;
	});
	return first; // dojo._Animation
};

dojo.fx.combine = function(/*dojo._Animation[]*/ animations){
	// summary: Combine a list of dojo._Animation s to run in parallel
	// example:
	//	|	dojo.fx.combine([
	//	|		dojo.fadeIn({ node:node }),
	//	|		dojo.fadeOut({ node:otherNode })
	//	|	]).play();
	var ctr = new dojo._Animation({ curve: [0, 1] });
	if(!animations.length){ return ctr; }
	// animations.sort(function(a, b){ return a.duration-b.duration; });
	ctr.duration = animations[0].duration;
	dojo.forEach(animations, function(current){
		dojo.forEach([ "play", "pause", "stop" ],
			function(e){
				if(current[e]){
					dojo.connect(ctr, e, current, e);
				}
			}
		);
	});
	return ctr; // dojo._Animation
};

dojo.declare("dojo.fx.Toggler", null, {
	// summary:
	//		class constructor for an animation toggler. It accepts a packed
	//		set of arguments about what type of animation to use in each
	//		direction, duration, etc.
	//
	// example:
	//	|	var t = new dojo.fx.Toggler({
	//	|		node: "nodeId",
	//	|		showDuration: 500,
	//	|		// hideDuration will default to "200"
	//	|		showFunc: dojo.wipeIn, 
	//	|		// hideFunc will default to "fadeOut"
	//	|	});
	//	|	t.show(100); // delay showing for 100ms
	//	|	// ...time passes...
	//	|	t.hide();

	// FIXME: need a policy for where the toggler should "be" the next
	// time show/hide are called if we're stopped somewhere in the
	// middle.

	constructor: function(args){
		var _t = this;

		dojo.mixin(_t, args);
		_t.node = args.node;
		_t._showArgs = dojo.mixin({}, args);
		_t._showArgs.node = _t.node;
		_t._showArgs.duration = _t.showDuration;
		_t.showAnim = _t.showFunc(_t._showArgs);

		_t._hideArgs = dojo.mixin({}, args);
		_t._hideArgs.node = _t.node;
		_t._hideArgs.duration = _t.hideDuration;
		_t.hideAnim = _t.hideFunc(_t._hideArgs);

		dojo.connect(_t.showAnim, "beforeBegin", dojo.hitch(_t.hideAnim, "stop", true));
		dojo.connect(_t.hideAnim, "beforeBegin", dojo.hitch(_t.showAnim, "stop", true));
	},

	// node: DomNode
	//	the node to toggle
	node: null,

	// showFunc: Function
	//	The function that returns the dojo._Animation to show the node
	showFunc: dojo.fadeIn,

	// hideFunc: Function	
	//	The function that returns the dojo._Animation to hide the node
	hideFunc: dojo.fadeOut,

	// showDuration:
	//	Time in milliseconds to run the show Animation
	showDuration: 200,

	// hideDuration:
	//	Time in milliseconds to run the hide Animation
	hideDuration: 200,

	/*=====
	_showArgs: null,
	_showAnim: null,

	_hideArgs: null,
	_hideAnim: null,

	_isShowing: false,
	_isHiding: false,
	=====*/

	show: function(delay){
		// summary: Toggle the node to showing
		return this.showAnim.play(delay || 0);
	},

	hide: function(delay){
		// summary: Toggle the node to hidden
		return this.hideAnim.play(delay || 0);
	}
});

dojo.fx.wipeIn = function(/*Object*/ args){
	// summary
	//		Returns an animation that will expand the
	//		node defined in 'args' object from it's current height to
	//		it's natural height (with no scrollbar).
	//		Node must have no margin/border/padding.
	args.node = dojo.byId(args.node);
	var node = args.node, s = node.style;

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			height: {
				// wrapped in functions so we wait till the last second to query (in case value has changed)
				start: function(){
					// start at current [computed] height, but use 1px rather than 0
					// because 0 causes IE to display the whole panel
					s.overflow="hidden";
					if(s.visibility=="hidden"||s.display=="none"){
						s.height="1px";
						s.display="";
						s.visibility="";
						return 1;
					}else{
						var height = dojo.style(node, "height");
						return Math.max(height, 1);
					}
				},
				end: function(){
					return node.scrollHeight;
				}
			}
		}
	}, args));

	dojo.connect(anim, "onEnd", function(){ 
		s.height = "auto";
	});

	return anim; // dojo._Animation
}

dojo.fx.wipeOut = function(/*Object*/ args){
	// summary
	//		Returns an animation that will shrink node defined in "args"
	//		from it's current height to 1px, and then hide it.
	var node = args.node = dojo.byId(args.node);
	var s = node.style;

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			height: {
				end: 1 // 0 causes IE to display the whole panel
			}
		}
	}, args));

	dojo.connect(anim, "beforeBegin", function(){
		s.overflow = "hidden";
		s.display = "";
	});
	dojo.connect(anim, "onEnd", function(){
		s.height = "auto";
		s.display = "none";
	});

	return anim; // dojo._Animation
}

dojo.fx.slideTo = function(/*Object?*/ args){
	// summary
	//		Returns an animation that will slide "node" 
	//		defined in args Object from its current position to
	//		the position defined by (args.left, args.top).
	// example:
	//	|	dojo.fx.slideTo({ node: node, left:"40", top:"50", unit:"px" }).play()

	var node = (args.node = dojo.byId(args.node));
	
	var top = null;
	var left = null;
	
	var init = (function(n){
		return function(){
			var cs = dojo.getComputedStyle(n);
			var pos = cs.position;
			top = (pos == 'absolute' ? n.offsetTop : parseInt(cs.top) || 0);
			left = (pos == 'absolute' ? n.offsetLeft : parseInt(cs.left) || 0);
			if(pos != 'absolute' && pos != 'relative'){
				var ret = dojo.coords(n, true);
				top = ret.y;
				left = ret.x;
				n.style.position="absolute";
				n.style.top=top+"px";
				n.style.left=left+"px";
			}
		};
	})(node);
	init();

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			top: { end: args.top||0 },
			left: { end: args.left||0 }
		}
	}, args));
	dojo.connect(anim, "beforeBegin", anim, init);

	return anim; // dojo._Animation
}

}


if(!dojo._hasResource["dojo.i18n"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.i18n"] = true;
dojo.provide("dojo.i18n");

dojo.i18n.getLocalization = function(/*String*/packageName, /*String*/bundleName, /*String?*/locale){
	//	summary:
	//		Returns an Object containing the localization for a given resource
	//		bundle in a package, matching the specified locale.
	//	description:
	//		Returns a hash containing name/value pairs in its prototypesuch
	//		that values can be easily overridden.  Throws an exception if the
	//		bundle is not found.  Bundle must have already been loaded by
	//		dojo.requireLocalization() or by a build optimization step.  NOTE:
	//		try not to call this method as part of an object property
	//		definition (var foo = { bar: dojo.i18n.getLocalization() }).  In
	//		some loading situations, the bundle may not be available in time
	//		for the object definition.  Instead, call this method inside a
	//		function that is run after all modules load or the page loads (like
	//		in dojo.adOnLoad()), or in a widget lifecycle method.
	//	packageName:
	//		package which is associated with this resource
	//	bundleName:
	//		the base filename of the resource bundle (without the ".js" suffix)
	//	locale:
	//		the variant to load (optional).  By default, the locale defined by
	//		the host environment: dojo.locale

	locale = dojo.i18n.normalizeLocale(locale);

	// look for nearest locale match
	var elements = locale.split('-');
	var module = [packageName,"nls",bundleName].join('.');
	var bundle = dojo._loadedModules[module];
	if(bundle){
		var localization;
		for(var i = elements.length; i > 0; i--){
			var loc = elements.slice(0, i).join('_');
			if(bundle[loc]){
				localization = bundle[loc];
				break;
			}
		}
		if(!localization){
			localization = bundle.ROOT;
		}

		// make a singleton prototype so that the caller won't accidentally change the values globally
		if(localization){
			var clazz = function(){};
			clazz.prototype = localization;
			return new clazz(); // Object
		}
	}

	throw new Error("Bundle not found: " + bundleName + " in " + packageName+" , locale=" + locale);
};

dojo.i18n.normalizeLocale = function(/*String?*/locale){
	//	summary:
	//		Returns canonical form of locale, as used by Dojo.
	//
	//  description:
	//		All variants are case-insensitive and are separated by '-' as specified in RFC 3066.
	//		If no locale is specified, the dojo.locale is returned.  dojo.locale is defined by
	//		the user agent's locale unless overridden by djConfig.

	var result = locale ? locale.toLowerCase() : dojo.locale;
	if(result == "root"){
		result = "ROOT";
	}
	return result; // String
};

dojo.i18n._requireLocalization = function(/*String*/moduleName, /*String*/bundleName, /*String?*/locale, /*String?*/availableFlatLocales){
	//	summary:
	//		See dojo.requireLocalization()
	//	description:
	// 		Called by the bootstrap, but factored out so that it is only
	// 		included in the build when needed.

	var targetLocale = dojo.i18n.normalizeLocale(locale);
 	var bundlePackage = [moduleName, "nls", bundleName].join(".");
	// NOTE: 
	//		When loading these resources, the packaging does not match what is
	//		on disk.  This is an implementation detail, as this is just a
	//		private data structure to hold the loaded resources.  e.g.
	//		tests/hello/nls/en-us/salutations.js is loaded as the object
	//		tests.hello.nls.salutations.en_us={...} The structure on disk is
	//		intended to be most convenient for developers and translators, but
	//		in memory it is more logical and efficient to store in a different
	//		order.  Locales cannot use dashes, since the resulting path will
	//		not evaluate as valid JS, so we translate them to underscores.
	
	//Find the best-match locale to load if we have available flat locales.
	var bestLocale = "";
	if(availableFlatLocales){
		var flatLocales = availableFlatLocales.split(",");
		for(var i = 0; i < flatLocales.length; i++){
			//Locale must match from start of string.
			if(targetLocale.indexOf(flatLocales[i]) == 0){
				if(flatLocales[i].length > bestLocale.length){
					bestLocale = flatLocales[i];
				}
			}
		}
		if(!bestLocale){
			bestLocale = "ROOT";
		}		
	}

	//See if the desired locale is already loaded.
	var tempLocale = availableFlatLocales ? bestLocale : targetLocale;
	var bundle = dojo._loadedModules[bundlePackage];
	var localizedBundle = null;
	if(bundle){
		if(djConfig.localizationComplete && bundle._built){return;}
		var jsLoc = tempLocale.replace(/-/g, '_');
		var translationPackage = bundlePackage+"."+jsLoc;
		localizedBundle = dojo._loadedModules[translationPackage];
	}

	if(!localizedBundle){
		bundle = dojo["provide"](bundlePackage);
		var syms = dojo._getModuleSymbols(moduleName);
		var modpath = syms.concat("nls").join("/");
		var parent;

		dojo.i18n._searchLocalePath(tempLocale, availableFlatLocales, function(loc){
			var jsLoc = loc.replace(/-/g, '_');
			var translationPackage = bundlePackage + "." + jsLoc;
			var loaded = false;
			if(!dojo._loadedModules[translationPackage]){
				// Mark loaded whether it's found or not, so that further load attempts will not be made
				dojo["provide"](translationPackage);
				var module = [modpath];
				if(loc != "ROOT"){module.push(loc);}
				module.push(bundleName);
				var filespec = module.join("/") + '.js';
				loaded = dojo._loadPath(filespec, null, function(hash){
					// Use singleton with prototype to point to parent bundle, then mix-in result from loadPath
					var clazz = function(){};
					clazz.prototype = parent;
					bundle[jsLoc] = new clazz();
					for(var j in hash){ bundle[jsLoc][j] = hash[j]; }
				});
			}else{
				loaded = true;
			}
			if(loaded && bundle[jsLoc]){
				parent = bundle[jsLoc];
			}else{
				bundle[jsLoc] = parent;
			}
			
			if(availableFlatLocales){
				//Stop the locale path searching if we know the availableFlatLocales, since
				//the first call to this function will load the only bundle that is needed.
				return true;
			}
		});
	}

	//Save the best locale bundle as the target locale bundle when we know the
	//the available bundles.
	if(availableFlatLocales && targetLocale != bestLocale){
		bundle[targetLocale.replace(/-/g, '_')] = bundle[bestLocale.replace(/-/g, '_')];
	}
};

(function(){
	// If other locales are used, dojo.requireLocalization should load them as
	// well, by default. 
	// 
	// Override dojo.requireLocalization to do load the default bundle, then
	// iterate through the extraLocale list and load those translations as
	// well, unless a particular locale was requested.

	var extra = djConfig.extraLocale;
	if(extra){
		if(!extra instanceof Array){
			extra = [extra];
		}

		var req = dojo.i18n._requireLocalization;
		dojo.i18n._requireLocalization = function(m, b, locale, availableFlatLocales){
			req(m,b,locale, availableFlatLocales);
			if(locale){return;}
			for(var i=0; i<extra.length; i++){
				req(m,b,extra[i], availableFlatLocales);
			}
		};
	}
})();

dojo.i18n._searchLocalePath = function(/*String*/locale, /*Boolean*/down, /*Function*/searchFunc){
	//	summary:
	//		A helper method to assist in searching for locale-based resources.
	//		Will iterate through the variants of a particular locale, either up
	//		or down, executing a callback function.  For example, "en-us" and
	//		true will try "en-us" followed by "en" and finally "ROOT".

	locale = dojo.i18n.normalizeLocale(locale);

	var elements = locale.split('-');
	var searchlist = [];
	for(var i = elements.length; i > 0; i--){
		searchlist.push(elements.slice(0, i).join('-'));
	}
	searchlist.push(false);
	if(down){searchlist.reverse();}

	for(var j = searchlist.length - 1; j >= 0; j--){
		var loc = searchlist[j] || "ROOT";
		var stop = searchFunc(loc);
		if(stop){ break; }
	}
};

dojo.i18n._preloadLocalizations = function(/*String*/bundlePrefix, /*Array*/localesGenerated){
	//	summary:
	//		Load built, flattened resource bundles, if available for all
	//		locales used in the page. Only called by built layer files.

	function preload(locale){
		locale = dojo.i18n.normalizeLocale(locale);
		dojo.i18n._searchLocalePath(locale, true, function(loc){
			for(var i=0; i<localesGenerated.length;i++){
				if(localesGenerated[i] == loc){
					dojo["require"](bundlePrefix+"_"+loc);
					return true; // Boolean
				}
			}
			return false; // Boolean
		});
	}
	preload();
	var extra = djConfig.extraLocale||[];
	for(var i=0; i<extra.length; i++){
		preload(extra[i]);
	}
};

}

if(!dojo._hasResource["dojo.cldr.supplemental"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.cldr.supplemental"] = true;
	dojo.provide("dojo.cldr.supplemental");

	dojo.cldr.supplemental.getFirstDayOfWeek = function(/*String?*/locale){
	// summary: Returns a zero-based index for first day of the week
	// description:
//			Returns a zero-based index for first day of the week, as used by the local (Gregorian) calendar.
//			e.g. Sunday (returns 0), or Monday (returns 1)

		// from http://www.unicode.org/cldr/data/common/supplemental/supplementalData.xml:supplementalData/weekData/firstDay
		var firstDay = {/*default is 1=Monday*/
			mv:5,
			ae:6,af:6,bh:6,dj:6,dz:6,eg:6,er:6,et:6,iq:6,ir:6,jo:6,ke:6,kw:6,lb:6,ly:6,ma:6,om:6,qa:6,sa:6,
			sd:6,so:6,tn:6,ye:6,
			as:0,au:0,az:0,bw:0,ca:0,cn:0,fo:0,ge:0,gl:0,gu:0,hk:0,ie:0,il:0,is:0,jm:0,jp:0,kg:0,kr:0,la:0,
			mh:0,mo:0,mp:0,mt:0,nz:0,ph:0,pk:0,sg:0,th:0,tt:0,tw:0,um:0,us:0,uz:0,vi:0,za:0,zw:0,
			et:0,mw:0,ng:0,tj:0,
			gb:0,
			sy:4
		};

		var country = dojo.cldr.supplemental._region(locale);
		var dow = firstDay[country];
		return (typeof dow == 'undefined') ? 1 : dow; /*Number*/
	};

	dojo.cldr.supplemental._region = function(/*String?*/locale){
		locale = dojo.i18n.normalizeLocale(locale);
		var tags = locale.split('-');
		var region = tags[1];
		if(!region){
			// IE often gives language only (#2269)
			// Arbitrary mappings of language-only locales to a country:
	        region = {de:"de", en:"us", es:"es", fi:"fi", fr:"fr", hu:"hu", it:"it",
	        ja:"jp", ko:"kr", nl:"nl", pt:"br", sv:"se", zh:"cn"}[tags[0]];
		}else if(region.length == 4){
			// The ISO 3166 country code is usually in the second position, unless a
			// 4-letter script is given. See http://www.ietf.org/rfc/rfc4646.txt
			region = tags[2];
		}
		return region;
	}

	dojo.cldr.supplemental.getWeekend = function(/*String?*/locale){
	// summary: Returns a hash containing the start and end days of the weekend
	// description:
//			Returns a hash containing the start and end days of the weekend according to local custom using locale,
//			or by default in the user's locale.
//			e.g. {start:6, end:0}

		// from http://www.unicode.org/cldr/data/common/supplemental/supplementalData.xml:supplementalData/weekData/weekend{Start,End}
		var weekendStart = {/*default is 6=Saturday*/
			eg:5,il:5,sy:5,
			'in':0,
			ae:4,bh:4,dz:4,iq:4,jo:4,kw:4,lb:4,ly:4,ma:4,om:4,qa:4,sa:4,sd:4,tn:4,ye:4		
		};

		var weekendEnd = {/*default is 0=Sunday*/
			ae:5,bh:5,dz:5,iq:5,jo:5,kw:5,lb:5,ly:5,ma:5,om:5,qa:5,sa:5,sd:5,tn:5,ye:5,af:5,ir:5,
			eg:6,il:6,sy:6
		};

		var country = dojo.cldr.supplemental._region(locale);
		var start = weekendStart[country];
		var end = weekendEnd[country];
		if(typeof start == 'undefined'){start=6;}
		if(typeof end == 'undefined'){end=0;}
		return {start:start, end:end}; /*Object {start,end}*/
	};

	}



if(!dojo._hasResource["dojo.regexp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.regexp"] = true;
dojo.provide("dojo.regexp");

dojo.regexp.escapeString = function(/*String*/str, /*String?*/except){
	//	summary:
	//		Adds escape sequences for special characters in regular expressions
	// except:
	//		a String with special characters to be left unescaped

//	return str.replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1"); // string
	return str.replace(/([\.$?*!=:|{}\(\)\[\]\\\/^])/g, function(ch){
		if(except && except.indexOf(ch) != -1){
			return ch;
		}
		return "\\" + ch;
	}); // String
}

dojo.regexp.buildGroupRE = function(/*Object|Array*/arr, /*Function*/re, /*Boolean?*/nonCapture){
	//	summary:
	//		Builds a regular expression that groups subexpressions
	//	description:
	//		A utility function used by some of the RE generators. The
	//		subexpressions are constructed by the function, re, in the second
	//		parameter.  re builds one subexpression for each elem in the array
	//		a, in the first parameter. Returns a string for a regular
	//		expression that groups all the subexpressions.
	// arr:
	//		A single value or an array of values.
	// re:
	//		A function. Takes one parameter and converts it to a regular
	//		expression. 
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. Defaults to false

	// case 1: a is a single value.
	if(!(arr instanceof Array)){
		return re(arr); // String
	}

	// case 2: a is an array
	var b = [];
	for(var i = 0; i < arr.length; i++){
		// convert each elem to a RE
		b.push(re(arr[i]));
	}

	 // join the REs as alternatives in a RE group.
	return dojo.regexp.group(b.join("|"), nonCapture); // String
}

dojo.regexp.group = function(/*String*/expression, /*Boolean?*/nonCapture){
	// summary:
	//		adds group match to expression
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. 
	return "(" + (nonCapture ? "?:":"") + expression + ")"; // String
}

}


if(!dojo._hasResource["dojo.string"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.string"] = true;
dojo.provide("dojo.string");

dojo.string.pad = function(/*String*/text, /*int*/size, /*String?*/ch, /*boolean?*/end){
	// summary:
	//		Pad a string to guarantee that it is at least 'size' length by
	//		filling with the character 'c' at either the start or end of the
	//		string. Pads at the start, by default.
	// text: the string to pad
	// size: length to provide padding
	// ch: character to pad, defaults to '0'
	// end: adds padding at the end if true, otherwise pads at start

	var out = String(text);
	if(!ch){
		ch = '0';
	}
	while(out.length < size){
		if(end){
			out += ch;
		}else{
			out = ch + out;
		}
	}
	return out;	// String
};

dojo.string.substitute = function(	/*String*/template, 
									/*Object or Array*/map, 
									/*Function?*/transform, 
									/*Object?*/thisObject){
	// summary:
	//		Performs parameterized substitutions on a string. Throws an
	//		exception if any parameter is unmatched.
	// description:
	//		For example,
	//		|	dojo.string.substitute("File '${0}' is not found in directory '${1}'.",["foo.html","/temp"]);
	//		|	dojo.string.substitute("File '${name}' is not found in directory '${info.dir}'.",{name: "foo.html", info: {dir: "/temp"}});
	//		both return
	//			"File 'foo.html' is not found in directory '/temp'."
	// template: 
	//		a string with expressions in the form ${key} to be replaced or
	//		${key:format} which specifies a format function.  NOTE syntax has
	//		changed from %{key}
	// map: where to look for substitutions
	// transform: 
	//		a function to process all parameters before substitution takes
	//		place, e.g. dojo.string.encodeXML
	// thisObject: 
	//		where to look for optional format function; default to the global
	//		namespace

	return template.replace(/\$\{([^\s\:\}]+)(?:\:([^\s\:\}]+))?\}/g, function(match, key, format){
		var value = dojo.getObject(key,false,map);
		if(format){ value = dojo.getObject(format,false,thisObject)(value);}
		if(transform){ value = transform(value, key); }
		return value.toString();
	}); // string
};

dojo.string.trim = function(/*String*/ str){
	// summary: trims whitespaces from both sides of the string
	// description:
	//	This version of trim() was taken from Steven Levithan's blog: 
	//	http://blog.stevenlevithan.com/archives/faster-trim-javascript.
	//	The short yet good-performing version of this function is 
	//	dojo.trim(), which is part of the base.
	str = str.replace(/^\s+/, '');
	for(var i = str.length - 1; i > 0; i--){
		if(/\S/.test(str.charAt(i))){
			str = str.substring(0, i + 1);
			break;
		}
	}
	return str;	// String
};

}


if(!dojo._hasResource["dojo.date.stamp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.date.stamp"] = true;
dojo.provide("dojo.date.stamp");

// Methods to convert dates to or from a wire (string) format using well-known conventions

dojo.date.stamp.fromISOString = function(/*String*/formattedString, /*Number?*/defaultTime){
	//	summary:
	//		Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
	//
	//	description:
	//		Accepts a string formatted according to a profile of ISO8601 as defined by
	//		RFC3339 (http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
	//		Can also process dates as specified by http://www.w3.org/TR/NOTE-datetime
	//		The following combinations are valid:
	// 			* dates only
	//				yyyy
	//				yyyy-MM
	//				yyyy-MM-dd
	// 			* times only, with an optional time zone appended
	//				THH:mm
	//				THH:mm:ss
	//				THH:mm:ss.SSS
	// 			* and "datetimes" which could be any combination of the above
	//		timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
	//		Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
	//		input may return null.  Arguments which are out of bounds will be handled
	// 		by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
	//
  	//	formattedString:
	//		A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00
	//
	//	defaultTime:
	//		Used for defaults for fields omitted in the formattedString.
	//		Uses 1970-01-01T00:00:00.0Z by default.

	if(!dojo.date.stamp._isoRegExp){
		dojo.date.stamp._isoRegExp =
//TODO: could be more restrictive and check for 00-59, etc.
			/^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;
	}

	var match = dojo.date.stamp._isoRegExp.exec(formattedString);
	var result = null;

	if(match){
		match.shift();
		match[1] && match[1]--; // Javascript Date months are 0-based
		match[6] && (match[6] *= 1000); // Javascript Date expects fractional seconds as milliseconds

		if(defaultTime){
			// mix in defaultTime.  Relatively expensive, so use || operators for the fast path of defaultTime === 0
			defaultTime = new Date(defaultTime);
			dojo.map(["FullYear", "Month", "Date", "Hours", "Minutes", "Seconds", "Milliseconds"], function(prop){
				return defaultTime["get" + prop]();
			}).forEach(function(value, index){
				if(match[index] === undefined){
					match[index] = value;
				}
			});
		}
		result = new Date(match[0]||1970, match[1]||0, match[2]||0, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

		var offset = 0;
		var zoneSign = match[7] && match[7].charAt(0);
		if(zoneSign != 'Z'){
			offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
			if(zoneSign != '-'){ offset *= -1; }
		}
		if(zoneSign){
			offset -= result.getTimezoneOffset();
		}
		if(offset){
			result.setTime(result.getTime() + offset * 60000);
		}
	}

	return result; // Date or null
}

dojo.date.stamp.toISOString = function(/*Date*/dateObject, /*Object?*/options){
	//	summary:
	//		Format a Date object as a string according a subset of the ISO-8601 standard
	//
	//	description:
	//		When options.selector is omitted, output follows RFC3339 (http://www.ietf.org/rfc/rfc3339.txt)
	//		The local time zone is included as an offset from GMT, except when selector=='time' (time without a date)
	//		Does not check bounds.
	//
	//	dateObject:
	//		A Date object
	//
	//	object {selector: string, zulu: boolean, milliseconds: boolean}
	//		selector- "date" or "time" for partial formatting of the Date object.
	//			Both date and time will be formatted by default.
	//		zulu- if true, UTC/GMT is used for a timezone
	//		milliseconds- if true, output milliseconds

	var _ = function(n){ return (n < 10) ? "0" + n : n; }
	options = options || {};
	var formattedDate = [];
	var getter = options.zulu ? "getUTC" : "get";
	var date = "";
	if(options.selector != "time"){
		date = [dateObject[getter+"FullYear"](), _(dateObject[getter+"Month"]()+1), _(dateObject[getter+"Date"]())].join('-');
	}
	formattedDate.push(date);
	if(options.selector != "date"){
		var time = [_(dateObject[getter+"Hours"]()), _(dateObject[getter+"Minutes"]()), _(dateObject[getter+"Seconds"]())].join(':');
		var millis = dateObject[getter+"Milliseconds"]();
		if(options.milliseconds){
			time += "."+ (millis < 100 ? "0" : "") + _(millis);
		}
		if(options.zulu){
			time += "Z";
		}else if(options.selector != "time"){
			var timezoneOffset = dateObject.getTimezoneOffset();
			var absOffset = Math.abs(timezoneOffset);
			time += (timezoneOffset > 0 ? "-" : "+") + 
				_(Math.floor(absOffset/60)) + ":" + _(absOffset%60);
		}
		formattedDate.push(time);
	}
	return formattedDate.join('T'); // String
}

}


if(!dojo._hasResource["dojo.parser"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.parser"] = true;
dojo.provide("dojo.parser");

dojo.parser = new function(){

	var d = dojo;

	function val2type(/*Object*/ value){
		// summary:
		//		Returns name of type of given value.

		if(d.isString(value)){ return "string"; }
		if(typeof value == "number"){ return "number"; }
		if(typeof value == "boolean"){ return "boolean"; }
		if(d.isFunction(value)){ return "function"; }
		if(d.isArray(value)){ return "array"; } // typeof [] == "object"
		if(value instanceof Date) { return "date"; } // assume timestamp
		if(value instanceof d._Url){ return "url"; }
		return "object";
	}

	function str2obj(/*String*/ value, /*String*/ type){
		// summary:
		//		Convert given string value to given type
		switch(type){
			case "string":
				return value;
			case "number":
				return value.length ? Number(value) : NaN;
			case "boolean":
				// for checked/disabled value might be "" or "checked".  interpret as true.
				return typeof value == "boolean" ? value : !(value.toLowerCase()=="false");
			case "function":
				if(d.isFunction(value)){
					// IE gives us a function, even when we say something like onClick="foo"
					// (in which case it gives us an invalid function "function(){ foo }"). 
					//  Therefore, convert to string
					value=value.toString();
					value=d.trim(value.substring(value.indexOf('{')+1, value.length-1));
				}
				try{
					if(value.search(/[^\w\.]+/i) != -1){
						// TODO: "this" here won't work
						value = d.parser._nameAnonFunc(new Function(value), this);
					}
					return d.getObject(value, false);
				}catch(e){ return new Function(); }
			case "array":
				return value.split(/\s*,\s*/);
			case "date":
				switch(value){
					case "": return new Date("");	// the NaN of dates
					case "now": return new Date();	// current date
					default: return d.date.stamp.fromISOString(value);
				}
			case "url":
				return d.baseUrl + value;
			default:
				return d.fromJson(value);
		}
	}

	var instanceClasses = {
		// map from fully qualified name (like "dijit.Button") to structure like
		// { cls: dijit.Button, params: {label: "string", disabled: "boolean"} }
	};
	
	function getClassInfo(/*String*/ className){
		// className:
		//		fully qualified name (like "dijit.Button")
		// returns:
		//		structure like
		//			{ 
		//				cls: dijit.Button, 
		//				params: { label: "string", disabled: "boolean"}
		//			}

		if(!instanceClasses[className]){
			// get pointer to widget class
			var cls = d.getObject(className);
			if(!d.isFunction(cls)){
				throw new Error("Could not load class '" + className +
					"'. Did you spell the name correctly and use a full path, like 'dijit.form.Button'?");
			}
			var proto = cls.prototype;
	
			// get table of parameter names & types
			var params={};
			for(var name in proto){
				if(name.charAt(0)=="_"){ continue; } 	// skip internal properties
				var defVal = proto[name];
				params[name]=val2type(defVal);
			}

			instanceClasses[className] = { cls: cls, params: params };
		}
		return instanceClasses[className];
	}

	this._functionFromScript = function(script){
		var preamble = "";
		var suffix = "";
		var argsStr = script.getAttribute("args");
		if(argsStr){
			d.forEach(argsStr.split(/\s*,\s*/), function(part, idx){
				preamble += "var "+part+" = arguments["+idx+"]; ";
			});
		}
		var withStr = script.getAttribute("with");
		if(withStr && withStr.length){
			d.forEach(withStr.split(/\s*,\s*/), function(part){
				preamble += "with("+part+"){";
				suffix += "}";
			});
		}
		return new Function(preamble+script.innerHTML+suffix);
	}

	this.instantiate = function(/* Array */nodes){
		// summary:
		//		Takes array of nodes, and turns them into class instances and
		//		potentially calls a layout method to allow them to connect with
		//		any children		
		var thelist = [];
		d.forEach(nodes, function(node){
			if(!node){ return; }
			var type = node.getAttribute("dojoType");
			if((!type)||(!type.length)){ return; }
			var clsInfo = getClassInfo(type);
			var clazz = clsInfo.cls;
			var ps = clazz._noScript||clazz.prototype._noScript;

			// read parameters (ie, attributes).
			// clsInfo.params lists expected params like {"checked": "boolean", "n": "number"}
			var params = {};
			var attributes = node.attributes;
			for(var name in clsInfo.params){
				var item = attributes.getNamedItem(name);
				if(!item || (!item.specified && (!dojo.isIE || name.toLowerCase()!="value"))){ continue; }
				var value = item.value;
				// Deal with IE quirks for 'class' and 'style'
				switch(name){
				case "class":
					value = node.className;
					break;
				case "style":
					value = node.style && node.style.cssText; // FIXME: Opera?
				}
				var _type = clsInfo.params[name];
				params[name] = str2obj(value, _type);
			}

			// Process <script type="dojo/*"> script tags
			// <script type="dojo/method" event="foo"> tags are added to params, and passed to
			// the widget on instantiation.
			// <script type="dojo/method"> tags (with no event) are executed after instantiation
			// <script type="dojo/connect" event="foo"> tags are dojo.connected after instantiation
			if(!ps){
				var connects = [],	// functions to connect after instantiation
					calls = [];		// functions to call after instantiation

				d.query("> script[type^='dojo/']", node).orphan().forEach(function(script){
					var event = script.getAttribute("event"),
						type = script.getAttribute("type"),
						nf = d.parser._functionFromScript(script);
					if(event){
						if(type == "dojo/connect"){
							connects.push({event: event, func: nf});
						}else{
							params[event] = nf;
						}
					}else{
						calls.push(nf);
					}
				});
			}

			var markupFactory = clazz["markupFactory"];
			if(!markupFactory && clazz["prototype"]){
				markupFactory = clazz.prototype["markupFactory"];
			}
			// create the instance
			var instance = markupFactory ? markupFactory(params, node, clazz) : new clazz(params, node);
			thelist.push(instance);

			// map it to the JS namespace if that makes sense
			var jsname = node.getAttribute("jsId");
			if(jsname){
				d.setObject(jsname, instance);
			}

			// process connections and startup functions
			if(!ps){
				dojo.forEach(connects, function(connect){
					dojo.connect(instance, connect.event, null, connect.func);
				});
				dojo.forEach(calls, function(func){
					func.call(instance);
				});
			}
		});

		// Call startup on each top level instance if it makes sense (as for
		// widgets).  Parent widgets will recursively call startup on their
		// (non-top level) children
		d.forEach(thelist, function(instance){
			if(	instance  && 
				(instance.startup) && 
				((!instance.getParent) || (!instance.getParent()))
			){
				instance.startup();
			}
		});
		return thelist;
	};

	this.parse = function(/*DomNode?*/ rootNode){
		// summary:
		//		Search specified node (or root node) recursively for class instances,
		//		and instantiate them Searches for
		//		dojoType="qualified.class.name"
		var list = d.query('[dojoType]', rootNode);
		// go build the object instances
		var instances = this.instantiate(list);
		return instances;
	};
}();

//Register the parser callback. It should be the first callback
//after the a11y test.

(function(){
	var parseRunner = function(){ 
		if(djConfig["parseOnLoad"] == true){
			dojo.parser.parse(); 
		}
	};

	// FIXME: need to clobber cross-dependency!!
	if(dojo.exists("dijit.wai.onload") && (dijit.wai.onload === dojo._loaders[0])){
		dojo._loaders.splice(1, 0, parseRunner);
	}else{
		dojo._loaders.unshift(parseRunner);
	}
})();

//TODO: ported from 0.4.x Dojo.  Can we reduce this?
dojo.parser._anonCtr = 0;
dojo.parser._anon = {}; // why is this property required?
dojo.parser._nameAnonFunc = function(/*Function*/anonFuncPtr, /*Object*/thisObj){
	// summary:
	//		Creates a reference to anonFuncPtr in thisObj with a completely
	//		unique name. The new name is returned as a String. 
	var jpn = "$joinpoint";
	var nso = (thisObj|| dojo.parser._anon);
	if(dojo.isIE){
		var cn = anonFuncPtr["__dojoNameCache"];
		if(cn && nso[cn] === anonFuncPtr){
			return anonFuncPtr["__dojoNameCache"];
		}
	}
	var ret = "__"+dojo.parser._anonCtr++;
	while(typeof nso[ret] != "undefined"){
		ret = "__"+dojo.parser._anonCtr++;
	}
	nso[ret] = anonFuncPtr;
	return ret; // String
}

}

if(!dojo._hasResource["dojo.date.locale"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.date.locale"] = true;
	dojo.provide("dojo.date.locale");

	// Localization methods for Date.   Honor local customs using locale-dependent dojo.cldr data.


	// Load the bundles containing localization information for
	// names and formats
	dojo.requireLocalization("dojo.cldr", "gregorian", null, "ko,zh-cn,zh,ja,en,it-it,en-ca,en-au,it,en-gb,es-es,fr,pt,ROOT,ko-kr,es,de,pt-br");

	//NOTE: Everything in this module assumes Gregorian calendars.
	// Other calendars will be implemented in separate modules.

	(function(){
		// Format a pattern without literals
		function formatPattern(dateObject, bundle, pattern){
			return pattern.replace(/([a-z])\1*/ig, function(match){
				var s;
				var c = match.charAt(0);
				var l = match.length;
				var pad;
				var widthList = ["abbr", "wide", "narrow"];
				switch(c){
					case 'G':
						s = bundle[(l < 4) ? "eraAbbr" : "eraNames"][dateObject.getFullYear() < 0 ? 0 : 1];
						break;
					case 'y':
						s = dateObject.getFullYear();
						switch(l){
							case 1:
								break;
							case 2:
								s = String(s); s = s.substr(s.length - 2);
								break;
							default:
								pad = true;
						}
						break;
					case 'Q':
					case 'q':
						s = Math.ceil((dateObject.getMonth()+1)/3);
//						switch(l){
//							case 1: case 2:
								pad = true;
//								break;
//							case 3: case 4: // unimplemented
//						}
						break;
					case 'M':
					case 'L':
						var m = dateObject.getMonth();
						var width;
						switch(l){
							case 1: case 2:
								s = m+1; pad = true;
								break;
							case 3: case 4: case 5:
								width = widthList[l-3];
								break;
						}
						if(width){
							var type = (c == "L") ? "standalone" : "format";
							var prop = ["months",type,width].join("-");
							s = bundle[prop][m];
						}
						break;
					case 'w':
						var firstDay = 0;
						s = dojo.date.locale._getWeekOfYear(dateObject, firstDay); pad = true;
						break;
					case 'd':
						s = dateObject.getDate(); pad = true;
						break;
					case 'D':
						s = dojo.date.locale._getDayOfYear(dateObject); pad = true;
						break;
					case 'E':
					case 'e':
					case 'c': // REVIEW: don't see this in the spec?
						var d = dateObject.getDay();
						var width;
						switch(l){
							case 1: case 2:
								if(c == 'e'){
									var first = dojo.cldr.supplemental.getFirstDayOfWeek(options.locale);
									d = (d-first+7)%7;
								}
								if(c != 'c'){
									s = d+1; pad = true;
									break;
								}
								// else fallthrough...
							case 3: case 4: case 5:
								width = widthList[l-3];
								break;
						}
						if(width){
							var type = (c == "c") ? "standalone" : "format";
							var prop = ["days",type,width].join("-");
							s = bundle[prop][d];
						}
						break;
					case 'a':
						var timePeriod = (dateObject.getHours() < 12) ? 'am' : 'pm';
						s = bundle[timePeriod];
						break;
					case 'h':
					case 'H':
					case 'K':
					case 'k':
						var h = dateObject.getHours();
						// strange choices in the date format make it impossible to write this succinctly
						switch (c) {
							case 'h': // 1-12
								s = (h % 12) || 12;
								break;
							case 'H': // 0-23
								s = h;
								break;
							case 'K': // 0-11
								s = (h % 12);
								break;
							case 'k': // 1-24
								s = h || 24;
								break;
						}
						pad = true;
						break;
					case 'm':
						s = dateObject.getMinutes(); pad = true;
						break;
					case 's':
						s = dateObject.getSeconds(); pad = true;
						break;
					case 'S':
						s = Math.round(dateObject.getMilliseconds() * Math.pow(10, l-3));
						break;
					case 'v': // FIXME: don't know what this is. seems to be same as z?
					case 'z':
						// We only have one timezone to offer; the one from the browser
						s = dojo.date.getTimezoneName(dateObject);
						if(s){break;}
						l=4;
						// fallthrough... use GMT if tz not available
					case 'Z':
						var offset = dateObject.getTimezoneOffset();
						var tz = [
							(offset<=0 ? "+" : "-"),
							dojo.string.pad(Math.floor(Math.abs(offset)/60), 2),
							dojo.string.pad(Math.abs(offset)% 60, 2)
						];
						if(l==4){
							tz.splice(0, 0, "GMT");
							tz.splice(3, 0, ":");
						}
						s = tz.join("");
						break;
//					case 'Y': case 'u': case 'W': case 'F': case 'g': case 'A':
//						console.debug(match+" modifier unimplemented");
					default:
						throw new Error("dojo.date.locale.format: invalid pattern char: "+pattern);
				}
				if(pad){ s = dojo.string.pad(s, l); }
				return s;
			});
		}

	dojo.date.locale.format = function(/*Date*/dateObject, /*Object?*/options){
		// summary:
		//		Format a Date object as a String, using locale-specific settings.
		//
		// description:
		//		Create a string from a Date object using a known localized pattern.
		//		By default, this method formats both date and time from dateObject.
		//		Formatting patterns are chosen appropriate to the locale.  Different
		//		formatting lengths may be chosen, with "full" used by default.
		//		Custom patterns may be used or registered with translations using
		//		the addCustomFormats method.
		//		Formatting patterns are implemented using the syntax described at
		//		http://www.unicode.org/reports/tr35/tr35-4.html#Date_Format_Patterns
		//
		// dateObject:
		//		the date and/or time to be formatted.  If a time only is formatted,
		//		the values in the year, month, and day fields are irrelevant.  The
		//		opposite is true when formatting only dates.
		//
		// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string}
		//		selector- choice of 'time','date' (default: date and time)
		//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
		//		datePattern,timePattern- override pattern with this string
		//		am,pm- override strings for am/pm in times
		//		locale- override the locale used to determine formatting rules

		options = options || {};

		var locale = dojo.i18n.normalizeLocale(options.locale);
		var formatLength = options.formatLength || 'short';
		var bundle = dojo.date.locale._getGregorianBundle(locale);
		var str = [];
		var sauce = dojo.hitch(this, formatPattern, dateObject, bundle);
		if(options.selector == "year"){
			// Special case as this is not yet driven by CLDR data
			var year = dateObject.getFullYear();
			if(locale.match(/^zh|^ja/)){
				year += "\u5E74";
			}
			return year;
		}
		if(options.selector != "time"){
			var datePattern = options.datePattern || bundle["dateFormat-"+formatLength];
			if(datePattern){str.push(_processPattern(datePattern, sauce));}
		}
		if(options.selector != "date"){
			var timePattern = options.timePattern || bundle["timeFormat-"+formatLength];
			if(timePattern){str.push(_processPattern(timePattern, sauce));}
		}
		var result = str.join(" "); //TODO: use locale-specific pattern to assemble date + time
		return result; // String
	};

	dojo.date.locale.regexp = function(/*Object?*/options){
		// summary:
		//		Builds the regular needed to parse a localized date
		//
		// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string, strict: boolean}
		//		selector- choice of 'time', 'date' (default: date and time)
		//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
		//		datePattern,timePattern- override pattern with this string
		//		locale- override the locale used to determine formatting rules

		return dojo.date.locale._parseInfo(options).regexp; // String
	};

	dojo.date.locale._parseInfo = function(/*Object?*/options){
		options = options || {};
		var locale = dojo.i18n.normalizeLocale(options.locale);
		var bundle = dojo.date.locale._getGregorianBundle(locale);
		var formatLength = options.formatLength || 'short';
		var datePattern = options.datePattern || bundle["dateFormat-" + formatLength];
		var timePattern = options.timePattern || bundle["timeFormat-" + formatLength];
		var pattern;
		if(options.selector == 'date'){
			pattern = datePattern;
		}else if(options.selector == 'time'){
			pattern = timePattern;
		}else{
			pattern = datePattern + ' ' + timePattern; //TODO: use locale-specific pattern to assemble date + time
		}

		var tokens = [];
		var re = _processPattern(pattern, dojo.hitch(this, _buildDateTimeRE, tokens, bundle, options));
		return {regexp: re, tokens: tokens, bundle: bundle};
	};

	dojo.date.locale.parse = function(/*String*/value, /*Object?*/options){
		// summary:
		//		Convert a properly formatted string to a primitive Date object,
		//		using locale-specific settings.
		//
		// description:
		//		Create a Date object from a string using a known localized pattern.
		//		By default, this method parses looking for both date and time in the string.
		//		Formatting patterns are chosen appropriate to the locale.  Different
		//		formatting lengths may be chosen, with "full" used by default.
		//		Custom patterns may be used or registered with translations using
		//		the addCustomFormats method.
		//		Formatting patterns are implemented using the syntax described at
		//		http://www.unicode.org/reports/tr35/#Date_Format_Patterns
		//
		// value:
		//		A string representation of a date
		//
		// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string, strict: boolean}
		//		selector- choice of 'time', 'date' (default: date and time)
		//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
		//		datePattern,timePattern- override pattern with this string
		//		am,pm- override strings for am/pm in times
		//		locale- override the locale used to determine formatting rules
		//		strict- strict parsing, off by default

		var info = dojo.date.locale._parseInfo(options);
		var tokens = info.tokens, bundle = info.bundle;
		var re = new RegExp("^" + info.regexp + "$");
		var match = re.exec(value);
		if(!match){ return null; } // null

		var widthList = ['abbr', 'wide', 'narrow'];
		//1972 is a leap year.  We want to avoid Feb 29 rolling over into Mar 1,
		//in the cases where the year is parsed after the month and day.
		var result = new Date(1972, 0);
		var expected = {};
		var amPm = "";
		dojo.forEach(match, function(v, i){
			if(!i){return;}
			var token=tokens[i-1];
			var l=token.length;
			switch(token.charAt(0)){
				case 'y':
					if(l != 2){
						//interpret year literally, so '5' would be 5 A.D.
						result.setFullYear(v);
						expected.year = v;
					}else{
						if(v<100){
							v = Number(v);
							//choose century to apply, according to a sliding window
							//of 80 years before and 20 years after present year
							var year = '' + new Date().getFullYear();
							var century = year.substring(0, 2) * 100;
							var yearPart = Number(year.substring(2, 4));
							var cutoff = Math.min(yearPart + 20, 99);
							var num = (v < cutoff) ? century + v : century - 100 + v;
							result.setFullYear(num);
							expected.year = num;
						}else{
							//we expected 2 digits and got more...
							if(options.strict){
								return null;
							}
							//interpret literally, so '150' would be 150 A.D.
							//also tolerate '1950', if 'yyyy' input passed to 'yy' format
							result.setFullYear(v);
							expected.year = v;
						}
					}
					break;
				case 'M':
					if(l>2){
						var months = bundle['months-format-' + widthList[l-3]].concat();
						if(!options.strict){
							//Tolerate abbreviating period in month part
							//Case-insensitive comparison
							v = v.replace(".","").toLowerCase();
							months = dojo.map(months, function(s){ return s.replace(".","").toLowerCase(); } );
						}
						v = dojo.indexOf(months, v);
						if(v == -1){
//							console.debug("dojo.date.locale.parse: Could not parse month name: '" + v + "'.");
							return null;
						}
					}else{
						v--;
					}
					result.setMonth(v);
					expected.month = v;
					break;
				case 'E':
				case 'e':
					var days = bundle['days-format-' + widthList[l-3]].concat();
					if(!options.strict){
						//Case-insensitive comparison
						v = v.toLowerCase();
						days = dojo.map(days, "".toLowerCase);
					}
					v = dojo.indexOf(days, v);
					if(v == -1){
//						console.debug("dojo.date.locale.parse: Could not parse weekday name: '" + v + "'.");
						return null;
					}

					//TODO: not sure what to actually do with this input,
					//in terms of setting something on the Date obj...?
					//without more context, can't affect the actual date
					//TODO: just validate?
					break;
				case 'd':
					result.setDate(v);
					expected.date = v;
					break;
				case 'D':
					//FIXME: need to defer this until after the year is set for leap-year?
					result.setMonth(0);
					result.setDate(v);
					break;
				case 'a': //am/pm
					var am = options.am || bundle.am;
					var pm = options.pm || bundle.pm;
					if(!options.strict){
						var period = /\./g;
						v = v.replace(period,'').toLowerCase();
						am = am.replace(period,'').toLowerCase();
						pm = pm.replace(period,'').toLowerCase();
					}
					if(options.strict && v != am && v != pm){
//						console.debug("dojo.date.locale.parse: Could not parse am/pm part.");
						return null;
					}

					// we might not have seen the hours field yet, so store the state and apply hour change later
					amPm = (v == pm) ? 'p' : (v == am) ? 'a' : '';
					break;
				case 'K': //hour (1-24)
					if(v==24){v=0;}
					// fallthrough...
				case 'h': //hour (1-12)
				case 'H': //hour (0-23)
				case 'k': //hour (0-11)
					//TODO: strict bounds checking, padding
					if(v > 23){
//						console.debug("dojo.date.locale.parse: Illegal hours value");
						return null;
					}

					//in the 12-hour case, adjusting for am/pm requires the 'a' part
					//which could come before or after the hour, so we will adjust later
					result.setHours(v);
					break;
				case 'm': //minutes
					result.setMinutes(v);
					break;
				case 's': //seconds
					result.setSeconds(v);
					break;
				case 'S': //milliseconds
					result.setMilliseconds(v);
//					break;
//				case 'w':
	//TODO				var firstDay = 0;
//				default:
	//TODO: throw?
//					console.debug("dojo.date.locale.parse: unsupported pattern char=" + token.charAt(0));
			}
		});

		var hours = result.getHours();
		if(amPm === 'p' && hours < 12){
			result.setHours(hours + 12); //e.g., 3pm -> 15
		}else if(amPm === 'a' && hours == 12){
			result.setHours(0); //12am -> 0
		}

		//validate parse date fields versus input date fields
		if(expected.year && result.getFullYear() != expected.year){
//			console.debug("dojo.date.locale.parse: Parsed year: '" + result.getFullYear() + "' did not match input year: '" + expected.year + "'.");
			return null;
		}
		if(expected.month && result.getMonth() != expected.month){
//			console.debug("dojo.date.locale.parse: Parsed month: '" + result.getMonth() + "' did not match input month: '" + expected.month + "'.");
			return null;
		}
		if(expected.date && result.getDate() != expected.date){
//			console.debug("dojo.date.locale.parse: Parsed day of month: '" + result.getDate() + "' did not match input day of month: '" + expected.date + "'.");
			return null;
		}

		//TODO: implement a getWeekday() method in order to test 
		//validity of input strings containing 'EEE' or 'EEEE'...
		return result; // Date
	};

	function _processPattern(pattern, applyPattern, applyLiteral, applyAll){
		//summary: Process a pattern with literals in it

		// Break up on single quotes, treat every other one as a literal, except '' which becomes '
		var identity = function(x){return x;};
		applyPattern = applyPattern || identity;
		applyLiteral = applyLiteral || identity;
		applyAll = applyAll || identity;

		//split on single quotes (which escape literals in date format strings) 
		//but preserve escaped single quotes (e.g., o''clock)
		var chunks = pattern.match(/(''|[^'])+/g); 
		var literal = false;

		dojo.forEach(chunks, function(chunk, i){
			if(!chunk){
				chunks[i]='';
			}else{
				chunks[i]=(literal ? applyLiteral : applyPattern)(chunk);
				literal = !literal;
			}
		});
		return applyAll(chunks.join(''));
	}

	function _buildDateTimeRE(tokens, bundle, options, pattern){
		pattern = dojo.regexp.escapeString(pattern);
		if(!options.strict){ pattern = pattern.replace(" a", " ?a"); } // kludge to tolerate no space before am/pm
		return pattern.replace(/([a-z])\1*/ig, function(match){
			// Build a simple regexp.  Avoid captures, which would ruin the tokens list
			var s;
			var c = match.charAt(0);
			var l = match.length;
			var p2 = '', p3 = '';
			if(options.strict){
				if(l > 1){ p2 = '0' + '{'+(l-1)+'}'; }
				if(l > 2){ p3 = '0' + '{'+(l-2)+'}'; }
			}else{
				p2 = '0?'; p3 = '0{0,2}';
			}
			switch(c){
				case 'y':
					s = '\\d{2,4}';
					break;
				case 'M':
					s = (l>2) ? '\\S+' : p2+'[1-9]|1[0-2]';
					break;
				case 'D':
					s = p2+'[1-9]|'+p3+'[1-9][0-9]|[12][0-9][0-9]|3[0-5][0-9]|36[0-6]';
					break;
				case 'd':
					s = p2+'[1-9]|[12]\\d|3[01]';
					break;
				case 'w':
					s = p2+'[1-9]|[1-4][0-9]|5[0-3]';
					break;
			    case 'E':
					s = '\\S+';
					break;
				case 'h': //hour (1-12)
					s = p2+'[1-9]|1[0-2]';
					break;
				case 'k': //hour (0-11)
					s = p2+'\\d|1[01]';
					break;
				case 'H': //hour (0-23)
					s = p2+'\\d|1\\d|2[0-3]';
					break;
				case 'K': //hour (1-24)
					s = p2+'[1-9]|1\\d|2[0-4]';
					break;
				case 'm':
				case 's':
					s = '[0-5]\\d';
					break;
				case 'S':
					s = '\\d{'+l+'}';
					break;
				case 'a':
					var am = options.am || bundle.am || 'AM';
					var pm = options.pm || bundle.pm || 'PM';
					if(options.strict){
						s = am + '|' + pm;
					}else{
						s = am + '|' + pm;
						if(am != am.toLowerCase()){ s += '|' + am.toLowerCase(); }
						if(pm != pm.toLowerCase()){ s += '|' + pm.toLowerCase(); }
					}
					break;
				default:
				// case 'v':
				// case 'z':
				// case 'Z':
					s = ".*";
//					console.debug("parse of date format, pattern=" + pattern);
			}

			if(tokens){ tokens.push(match); }

			return "(" + s + ")"; // add capture
		}).replace(/[\xa0 ]/g, "[\\s\\xa0]"); // normalize whitespace.  Need explicit handling of \xa0 for IE.
	}
	})();

	(function(){
	var _customFormats = [];
	dojo.date.locale.addCustomFormats = function(/*String*/packageName, /*String*/bundleName){
		// summary:
		//		Add a reference to a bundle containing localized custom formats to be
		//		used by date/time formatting and parsing routines.
		//
		// description:
		//		The user may add custom localized formats where the bundle has properties following the
		//		same naming convention used by dojo for the CLDR data: dateFormat-xxxx / timeFormat-xxxx
		//		The pattern string should match the format used by the CLDR.
		//		See dojo.date.format for details.
		//		The resources must be loaded by dojo.requireLocalization() prior to use

		_customFormats.push({pkg:packageName,name:bundleName});
	};

	dojo.date.locale._getGregorianBundle = function(/*String*/locale){
		var gregorian = {};
		dojo.forEach(_customFormats, function(desc){
			var bundle = dojo.i18n.getLocalization(desc.pkg, desc.name, locale);
			gregorian = dojo.mixin(gregorian, bundle);
		}, this);
		return gregorian; /*Object*/
	};
	})();

	dojo.date.locale.addCustomFormats("dojo.cldr","gregorian");

	dojo.date.locale.getNames = function(/*String*/item, /*String*/type, /*String?*/use, /*String?*/locale){
		// summary:
		//		Used to get localized strings from dojo.cldr for day or month names.
		//
		// item: 'months' || 'days'
		// type: 'wide' || 'narrow' || 'abbr' (e.g. "Monday", "Mon", or "M" respectively, in English)
		// use: 'standAlone' || 'format' (default)
		// locale: override locale used to find the names

		var label;
		var lookup = dojo.date.locale._getGregorianBundle(locale);
		var props = [item, use, type];
		if(use == 'standAlone'){
			label = lookup[props.join('-')];
		}
		props[1] = 'format';

		// return by copy so changes won't be made accidentally to the in-memory model
		return (label || lookup[props.join('-')]).concat(); /*Array*/
	};

	dojo.date.locale.isWeekend = function(/*Date?*/dateObject, /*String?*/locale){
		// summary:
		//	Determines if the date falls on a weekend, according to local custom.

		var weekend = dojo.cldr.supplemental.getWeekend(locale);
		var day = (dateObject || new Date()).getDay();
		if(weekend.end < weekend.start){
			weekend.end += 7;
			if(day < weekend.start){ day += 7; }
		}
		return day >= weekend.start && day <= weekend.end; // Boolean
	};

	// These are used only by format and strftime.  Do they need to be public?  Which module should they go in?

	dojo.date.locale._getDayOfYear = function(/*Date*/dateObject){
		// summary: gets the day of the year as represented by dateObject
		return dojo.date.difference(new Date(dateObject.getFullYear(), 0, 1), dateObject) + 1; // Number
	};

	dojo.date.locale._getWeekOfYear = function(/*Date*/dateObject, /*Number*/firstDayOfWeek){
		if(arguments.length == 1){ firstDayOfWeek = 0; } // Sunday

		var firstDayOfYear = new Date(dateObject.getFullYear(), 0, 1).getDay();
		var adj = (firstDayOfYear - firstDayOfWeek + 7) % 7;
		var week = Math.floor((dojo.date.locale._getDayOfYear(dateObject) + adj - 1) / 7);

		// if year starts on the specified day, start counting weeks at 1
		if(firstDayOfYear == firstDayOfWeek){ week++; }

		return week; // Number
	};

	}


if(!dojo._hasResource["dojo.dnd.autoscroll"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.autoscroll"] = true;
dojo.provide("dojo.dnd.autoscroll");

dojo.dnd.getViewport = function(){
	// summary: returns a viewport size (visible part of the window)

	// FIXME: need more docs!!
	var d = dojo.doc, dd = d.documentElement, w = window, b = dojo.body();
	if(dojo.isMozilla){
		return {w: dd.clientWidth, h: w.innerHeight};	// Object
	}else if(!dojo.isOpera && w.innerWidth){
		return {w: w.innerWidth, h: w.innerHeight};		// Object
	}else if (!dojo.isOpera && dd && dd.clientWidth){
		return {w: dd.clientWidth, h: dd.clientHeight};	// Object
	}else if (b.clientWidth){
		return {w: b.clientWidth, h: b.clientHeight};	// Object
	}
	return null;	// Object
};

dojo.dnd.V_TRIGGER_AUTOSCROLL = 32;
dojo.dnd.H_TRIGGER_AUTOSCROLL = 32;

dojo.dnd.V_AUTOSCROLL_VALUE = 16;
dojo.dnd.H_AUTOSCROLL_VALUE = 16;

dojo.dnd.autoScroll = function(e){
	// summary:
	//		a handler for onmousemove event, which scrolls the window, if
	//		necesary
	// e: Event:
	//		onmousemove event

	// FIXME: needs more docs!
	var v = dojo.dnd.getViewport(), dx = 0, dy = 0;
	if(e.clientX < dojo.dnd.H_TRIGGER_AUTOSCROLL){
		dx = -dojo.dnd.H_AUTOSCROLL_VALUE;
	}else if(e.clientX > v.w - dojo.dnd.H_TRIGGER_AUTOSCROLL){
		dx = dojo.dnd.H_AUTOSCROLL_VALUE;
	}
	if(e.clientY < dojo.dnd.V_TRIGGER_AUTOSCROLL){
		dy = -dojo.dnd.V_AUTOSCROLL_VALUE;
	}else if(e.clientY > v.h - dojo.dnd.V_TRIGGER_AUTOSCROLL){
		dy = dojo.dnd.V_AUTOSCROLL_VALUE;
	}
	window.scrollBy(dx, dy);
};

dojo.dnd._validNodes = {"div": 1, "p": 1, "td": 1};
dojo.dnd._validOverflow = {"auto": 1, "scroll": 1};

dojo.dnd.autoScrollNodes = function(e){
	// summary:
	//		a handler for onmousemove event, which scrolls the first avaialble
	//		Dom element, it falls back to dojo.dnd.autoScroll()
	// e: Event:
	//		onmousemove event

	// FIXME: needs more docs!
	for(var n = e.target; n;){
		if(n.nodeType == 1 && (n.tagName.toLowerCase() in dojo.dnd._validNodes)){
			var s = dojo.getComputedStyle(n);
			if(s.overflow.toLowerCase() in dojo.dnd._validOverflow){
				var b = dojo._getContentBox(n, s), t = dojo._abs(n, true);
				// console.debug(b.l, b.t, t.x, t.y, n.scrollLeft, n.scrollTop);
				b.l += t.x + n.scrollLeft;
				b.t += t.y + n.scrollTop;
				var w = Math.min(dojo.dnd.H_TRIGGER_AUTOSCROLL, b.w / 2), 
					h = Math.min(dojo.dnd.V_TRIGGER_AUTOSCROLL, b.h / 2),
					rx = e.pageX - b.l, ry = e.pageY - b.t, dx = 0, dy = 0;
				if(rx > 0 && rx < b.w){
					if(rx < w){
						dx = -dojo.dnd.H_AUTOSCROLL_VALUE;
					}else if(rx > b.w - w){
						dx = dojo.dnd.H_AUTOSCROLL_VALUE;
					}
				}
				//console.debug("ry =", ry, "b.h =", b.h, "h =", h);
				if(ry > 0 && ry < b.h){
					if(ry < h){
						dy = -dojo.dnd.V_AUTOSCROLL_VALUE;
					}else if(ry > b.h - h){
						dy = dojo.dnd.V_AUTOSCROLL_VALUE;
					}
				}
				var oldLeft = n.scrollLeft, oldTop = n.scrollTop;
				n.scrollLeft = n.scrollLeft + dx;
				n.scrollTop  = n.scrollTop  + dy;
				// if(dx || dy){ console.debug(oldLeft + ", " + oldTop + "\n" + dx + ", " + dy + "\n" + n.scrollLeft + ", " + n.scrollTop); }
				if(oldLeft != n.scrollLeft || oldTop != n.scrollTop){ return; }
			}
		}
		try{
			n = n.parentNode;
		}catch(x){
			n = null;
		}
	}
	dojo.dnd.autoScroll(e);
};

}

if(!dojo._hasResource["dojo.dnd.common"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.common"] = true;
dojo.provide("dojo.dnd.common");

dojo.dnd._copyKey = navigator.appVersion.indexOf("Macintosh") < 0 ? "ctrlKey" : "metaKey";

dojo.dnd.getCopyKeyState = function(e) {
	// summary: abstracts away the difference between selection on Mac and PC,
	//	and returns the state of the "copy" key to be pressed.
	// e: Event: mouse event
	return e[dojo.dnd._copyKey];	// Boolean
};

dojo.dnd._uniqueId = 0;
dojo.dnd.getUniqueId = function(){
	// summary: returns a unique string for use with any DOM element
	var id;
	do{
		id = "dojoUnique" + (++dojo.dnd._uniqueId);
	}while(dojo.byId(id));
	return id;
};

dojo.dnd._empty = {};

dojo.dnd.isFormElement = function(/*Event*/ e){
	// summary: returns true, if user clicked on a form element
	var t = e.target;
	if(t.nodeType == 3 /*TEXT_NODE*/){
		t = t.parentNode;
	}
	return " button textarea input select option ".indexOf(" " + t.tagName.toLowerCase() + " ") >= 0;	// Boolean
};

}

if(!dojo._hasResource["dojo.dnd.Container"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.dnd.Container"] = true;
	dojo.provide("dojo.dnd.Container");

	/*
		Container states:
			""		- normal state
			"Over"	- mouse over a container
		Container item states:
			""		- normal state
			"Over"	- mouse over a container item
	*/

	dojo.declare("dojo.dnd.Container", null, {
		// summary: a Container object, which knows when mouse hovers over it, 
		//	and know over which element it hovers
		
		// object attributes (for markup)
		skipForm: false,
		
		constructor: function(node, params){
			// summary: a constructor of the Container
			// node: Node: node or node's id to build the container on
			// params: Object: a dict of parameters, recognized parameters are:
			//	creator: Function: a creator function, which takes a data item, and returns an object like that:
			//		{node: newNode, data: usedData, type: arrayOfStrings}
			//	skipForm: Boolean: don't start the drag operation, if clicked on form elements
			//	_skipStartup: Boolean: skip startup(), which collects children, for deferred initialization
			//		(this is used in the markup mode)
			this.node = dojo.byId(node);
			if(!params){ params = {}; }
			this.creator = params.creator || null;
			this.skipForm = params.skipForm;
			this.defaultCreator = dojo.dnd._defaultCreator(this.node);

			// class-specific variables
			this.map = {};
			this.current = null;

			// states
			this.containerState = "";
			dojo.addClass(this.node, "dojoDndContainer");
			
			// mark up children
			if(!(params && params._skipStartup)){
				this.startup();
			}

			// set up events
			this.events = [
				dojo.connect(this.node, "onmouseover", this, "onMouseOver"),
				dojo.connect(this.node, "onmouseout",  this, "onMouseOut"),
				// cancel text selection and text dragging
				dojo.connect(this.node, "ondragstart",   this, "onSelectStart"),
				dojo.connect(this.node, "onselectstart", this, "onSelectStart")
			];
		},
		
		// object attributes (for markup)
		creator: function(){},	// creator function, dummy at the moment
		
		// abstract access to the map
		getItem: function(/*String*/ key){
			// summary: returns a data item by its key (id)
			return this.map[key];	// Object
		},
		setItem: function(/*String*/ key, /*Object*/ data){
			// summary: associates a data item with its key (id)
			this.map[key] = data;
		},
		delItem: function(/*String*/ key){
			// summary: removes a data item from the map by its key (id)
			delete this.map[key];
		},
		forInItems: function(/*Function*/ f, /*Object?*/ o){
			// summary: iterates over a data map skipping members, which 
			//	are present in the empty object (IE and/or 3rd-party libraries).
			o = o || dojo.global;
			var m = this.map, e = dojo.dnd._empty;
			for(var i in this.map){
				if(i in e){ continue; }
				f.call(o, m[i], i, m);
			}
		},
		clearItems: function(){
			// summary: removes all data items from the map
			this.map = {};
		},
		
		// methods
		getAllNodes: function(){
			// summary: returns a list (an array) of all valid child nodes
			return dojo.query("> .dojoDndItem", this.parent);	// NodeList
		},
		insertNodes: function(data, before, anchor){
			// summary: inserts an array of new nodes before/after an anchor node
			// data: Array: a list of data items, which should be processed by the creator function
			// before: Boolean: insert before the anchor, if true, and after the anchor otherwise
			// anchor: Node: the anchor node to be used as a point of insertion
			if(!this.parent.firstChild){
				anchor = null;
			}else if(before){
				if(!anchor){
					anchor = this.parent.firstChild;
				}
			}else{
				if(anchor){
					anchor = anchor.nextSibling;
				}
			}
			if(anchor){
				for(var i = 0; i < data.length; ++i){
					var t = this._normalizedCreator(data[i]);
					this.setItem(t.node.id, {data: t.data, type: t.type});
					this.parent.insertBefore(t.node, anchor);
				}
			}else{
				for(var i = 0; i < data.length; ++i){
					var t = this._normalizedCreator(data[i]);
					this.setItem(t.node.id, {data: t.data, type: t.type});
					this.parent.appendChild(t.node);
				}
			}
			return this;	// self
		},
		destroy: function(){
			// summary: prepares the object to be garbage-collected
			dojo.forEach(this.events, dojo.disconnect);
			this.clearItems();
			this.node = this.parent = this.current;
		},

		// markup methods
		markupFactory: function(params, node){
			params._skipStartup = true;
			return new dojo.dnd.Container(node, params);
		},
		startup: function(){
			// summary: collects valid child items and populate the map
			
			// set up the real parent node
			this.parent = this.node;
			if(this.parent.tagName.toLowerCase() == "table"){
				var c = this.parent.getElementsByTagName("tbody");
				if(c && c.length){ this.parent = c[0]; }
			}

			// process specially marked children
			dojo.query("> .dojoDndItem", this.parent).forEach(function(node){
				if(!node.id){ node.id = dojo.dnd.getUniqueId(); }
				var type = node.getAttribute("dndType"),
					data = node.getAttribute("dndData");
				this.setItem(node.id, {
					data: data ? data : node.innerHTML,
					type: type ? type.split(/\s*,\s*/) : ["text"]
				});
			}, this);
		},

		// mouse events
		onMouseOver: function(e){
			// summary: event processor for onmouseover
			// e: Event: mouse event
			var n = e.relatedTarget;
			while(n){
				if(n == this.node){ break; }
				try{
					n = n.parentNode;
				}catch(x){
					n = null;
				}
			}
			if(!n){
				this._changeState("Container", "Over");
				this.onOverEvent();
			}
			n = this._getChildByEvent(e);
			if(this.current == n){ return; }
			if(this.current){ this._removeItemClass(this.current, "Over"); }
			if(n){ this._addItemClass(n, "Over"); }
			this.current = n;
		},
		onMouseOut: function(e){
			// summary: event processor for onmouseout
			// e: Event: mouse event
			for(var n = e.relatedTarget; n;){
				if(n == this.node){ return; }
				try{
					n = n.parentNode;
				}catch(x){
					n = null;
				}
			}
			if(this.current){
				this._removeItemClass(this.current, "Over");
				this.current = null;
			}
			this._changeState("Container", "");
			this.onOutEvent();
		},
		onSelectStart: function(e){
			// summary: event processor for onselectevent and ondragevent
			// e: Event: mouse event
			if(!this.skipForm || !dojo.dnd.isFormElement(e)){
				dojo.stopEvent(e);
			}
		},
		
		// utilities
		onOverEvent: function(){
			// summary: this function is called once, when mouse is over our container
		},
		onOutEvent: function(){
			// summary: this function is called once, when mouse is out of our container
		},
		_changeState: function(type, newState){
			// summary: changes a named state to new state value
			// type: String: a name of the state to change
			// newState: String: new state
			var prefix = "dojoDnd" + type;
			var state  = type.toLowerCase() + "State";
			//dojo.replaceClass(this.node, prefix + newState, prefix + this[state]);
			dojo.removeClass(this.node, prefix + this[state]);
			dojo.addClass(this.node, prefix + newState);
			this[state] = newState;
		},
		_addItemClass: function(node, type){
			// summary: adds a class with prefix "dojoDndItem"
			// node: Node: a node
			// type: String: a variable suffix for a class name
			dojo.addClass(node, "dojoDndItem" + type);
		},
		_removeItemClass: function(node, type){
			// summary: removes a class with prefix "dojoDndItem"
			// node: Node: a node
			// type: String: a variable suffix for a class name
			dojo.removeClass(node, "dojoDndItem" + type);
		},
		_getChildByEvent: function(e){
			// summary: gets a child, which is under the mouse at the moment, or null
			// e: Event: a mouse event
			var node = e.target;
			if(node){
				for(var parent = node.parentNode; parent; node = parent, parent = node.parentNode){
					if(parent == this.parent && dojo.hasClass(node, "dojoDndItem")){ return node; }
				}
			}
			return null;
		},
		_normalizedCreator: function(item, hint){
			// summary: adds all necessary data to the output of the user-supplied creator function
			var t = (this.creator ? this.creator : this.defaultCreator)(item, hint);
			if(!dojo.isArray(t.type)){ t.type = ["text"]; }
			if(!t.node.id){ t.node.id = dojo.dnd.getUniqueId(); }
			dojo.addClass(t.node, "dojoDndItem");
			return t;
		}
	});

	dojo.dnd._createNode = function(tag){
		// summary: returns a function, which creates an element of given tag 
		//	(SPAN by default) and sets its innerHTML to given text
		// tag: String: a tag name or empty for SPAN
		if(!tag){ return dojo.dnd._createSpan; }
		return function(text){	// Function
			var n = dojo.doc.createElement(tag);
			n.innerHTML = text;
			return n;
		};
	};

	dojo.dnd._createTrTd = function(text){
		// summary: creates a TR/TD structure with given text as an innerHTML of TD
		// text: String: a text for TD
		var tr = dojo.doc.createElement("tr");
		var td = dojo.doc.createElement("td");
		td.innerHTML = text;
		tr.appendChild(td);
		return tr;	// Node
	};

	dojo.dnd._createSpan = function(text){
		// summary: creates a SPAN element with given text as its innerHTML
		// text: String: a text for SPAN
		var n = dojo.doc.createElement("span");
		n.innerHTML = text;
		return n;	// Node
	};

	// dojo.dnd._defaultCreatorNodes: Object: a dicitionary, which maps container tag names to child tag names
	dojo.dnd._defaultCreatorNodes = {ul: "li", ol: "li", div: "div", p: "div"};

	dojo.dnd._defaultCreator = function(node){
		// summary: takes a container node, and returns an appropriate creator function
		// node: Node: a container node
		var tag = node.tagName.toLowerCase();
		var c = tag == "table" ? dojo.dnd._createTrTd : dojo.dnd._createNode(dojo.dnd._defaultCreatorNodes[tag]);
		return function(item, hint){	// Function
			var isObj = dojo.isObject(item) && item;
			var data = (isObj && item.data) ? item.data : item;
			var type = (isObj && item.type) ? item.type : ["text"];
			var t = String(data), n = (hint == "avatar" ? dojo.dnd._createSpan : c)(t);
			n.id = dojo.dnd.getUniqueId();
			return {node: n, data: data, type: type};
		};
	};

	}



if(!dojo._hasResource["dojo.dnd.Selector"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Selector"] = true;
dojo.provide("dojo.dnd.Selector");

/*
	Container item states:
		""			- an item is not selected
		"Selected"	- an item is selected
		"Anchor"	- an item is selected, and is an anchor for a "shift" selection
*/

dojo.declare("dojo.dnd.Selector", dojo.dnd.Container, {
	// summary: a Selector object, which knows how to select its children
	
	constructor: function(node, params){
		// summary: a constructor of the Selector
		// node: Node: node or node's id to build the selector on
		// params: Object: a dict of parameters, recognized parameters are:
		//	singular: Boolean: allows selection of only one element, if true
		//	the rest of parameters are passed to the container
		if(!params){ params = {}; }
		this.singular = params.singular;
		// class-specific variables
		this.selection = {};
		this.anchor = null;
		this.simpleSelection = false;
		// set up events
		this.events.push(
			dojo.connect(this.node, "onmousedown", this, "onMouseDown"),
			dojo.connect(this.node, "onmouseup",   this, "onMouseUp"));
	},
	
	// object attributes (for markup)
	singular: false,	// is singular property
	
	// methods
	getSelectedNodes: function(){
		// summary: returns a list (an array) of selected nodes
		var t = new dojo.NodeList();
		var e = dojo.dnd._empty;
		for(var i in this.selection){
			if(i in e){ continue; }
			t.push(dojo.byId(i));
		}
		return t;	// Array
	},
	selectNone: function(){
		// summary: unselects all items
		return this._removeSelection()._removeAnchor();	// self
	},
	selectAll: function(){
		// summary: selects all items
		this.forInItems(function(data, id){
			this._addItemClass(dojo.byId(id), "Selected");
			this.selection[id] = 1;
		}, this);
		return this._removeAnchor();	// self
	},
	deleteSelectedNodes: function(){
		// summary: deletes all selected items
		var e = dojo.dnd._empty;
		for(var i in this.selection){
			if(i in e){ continue; }
			var n = dojo.byId(i);
			this.delItem(i);
			dojo._destroyElement(n);
		}
		this.anchor = null;
		this.selection = {};
		return this;	// self
	},
	insertNodes: function(addSelected, data, before, anchor){
		// summary: inserts new data items (see Container's insertNodes method for details)
		// addSelected: Boolean: all new nodes will be added to selected items, if true, no selection change otherwise
		// data: Array: a list of data items, which should be processed by the creator function
		// before: Boolean: insert before the anchor, if true, and after the anchor otherwise
		// anchor: Node: the anchor node to be used as a point of insertion
		var oldCreator = this._normalizedCreator;
		this._normalizedCreator = function(item, hint){
			var t = oldCreator.call(this, item, hint);
			if(addSelected){
				if(!this.anchor){
					this.anchor = t.node;
					this._removeItemClass(t.node, "Selected");
					this._addItemClass(this.anchor, "Anchor");
				}else if(this.anchor != t.node){
					this._removeItemClass(t.node, "Anchor");
					this._addItemClass(t.node, "Selected");
				}
				this.selection[t.node.id] = 1;
			}else{
				this._removeItemClass(t.node, "Selected");
				this._removeItemClass(t.node, "Anchor");
			}
			return t;
		};
		dojo.dnd.Selector.superclass.insertNodes.call(this, data, before, anchor);
		this._normalizedCreator = oldCreator;
		return this;	// self
	},
	destroy: function(){
		// summary: prepares the object to be garbage-collected
		dojo.dnd.Selector.superclass.destroy.call(this);
		this.selection = this.anchor = null;
	},

	// markup methods
	markupFactory: function(params, node){
		params._skipStartup = true;
		return new dojo.dnd.Selector(node, params);
	},

	// mouse events
	onMouseDown: function(e){
		// summary: event processor for onmousedown
		// e: Event: mouse event
		if(!this.current){ return; }
		if(!this.singular && !dojo.dnd.getCopyKeyState(e) && !e.shiftKey && (this.current.id in this.selection)){
			this.simpleSelection = true;
			dojo.stopEvent(e);
			return;
		}
		if(!this.singular && e.shiftKey){
			if(!dojo.dnd.getCopyKeyState(e)){
				this._removeSelection();
			}
			var c = dojo.query("> .dojoDndItem", this.parent);
			if(c.length){
				if(!this.anchor){
					this.anchor = c[0];
					this._addItemClass(this.anchor, "Anchor");
				}
				this.selection[this.anchor.id] = 1;
				if(this.anchor != this.current){
					var i = 0;
					for(; i < c.length; ++i){
						var node = c[i];
						if(node == this.anchor || node == this.current){ break; }
					}
					for(++i; i < c.length; ++i){
						var node = c[i];
						if(node == this.anchor || node == this.current){ break; }
						this._addItemClass(node, "Selected");
						this.selection[node.id] = 1;
					}
					this._addItemClass(this.current, "Selected");
					this.selection[this.current.id] = 1;
				}
			}
		}else{
			if(this.singular){
				if(this.anchor == this.current){
					if(dojo.dnd.getCopyKeyState(e)){
						this.selectNone();
					}
				}else{
					this.selectNone();
					this.anchor = this.current;
					this._addItemClass(this.anchor, "Anchor");
					this.selection[this.current.id] = 1;
				}
			}else{
				if(dojo.dnd.getCopyKeyState(e)){
					if(this.anchor == this.current){
						delete this.selection[this.anchor.id];
						this._removeAnchor();
					}else{
						if(this.current.id in this.selection){
							this._removeItemClass(this.current, "Selected");
							delete this.selection[this.current.id];
						}else{
							if(this.anchor){
								this._removeItemClass(this.anchor, "Anchor");
								this._addItemClass(this.anchor, "Selected");
							}
							this.anchor = this.current;
							this._addItemClass(this.current, "Anchor");
							this.selection[this.current.id] = 1;
						}
					}
				}else{
					if(!(this.current.id in this.selection)){
						this.selectNone();
						this.anchor = this.current;
						this._addItemClass(this.current, "Anchor");
						this.selection[this.current.id] = 1;
					}
				}
			}
		}
		dojo.stopEvent(e);
	},
	onMouseUp: function(e){
		// summary: event processor for onmouseup
		// e: Event: mouse event
		if(!this.simpleSelection){ return; }
		this.simpleSelection = false;
		this.selectNone();
		if(this.current){
			this.anchor = this.current;
			this._addItemClass(this.anchor, "Anchor");
			this.selection[this.current.id] = 1;
		}
	},
	onMouseMove: function(e){
		// summary: event processor for onmousemove
		// e: Event: mouse event
		this.simpleSelection = false;
	},
	
	// utilities
	onOverEvent: function(){
		// summary: this function is called once, when mouse is over our container
		this.onmousemoveEvent = dojo.connect(this.node, "onmousemove", this, "onMouseMove");
	},
	onOutEvent: function(){
		// summary: this function is called once, when mouse is out of our container
		dojo.disconnect(this.onmousemoveEvent);
		delete this.onmousemoveEvent;
	},
	_removeSelection: function(){
		// summary: unselects all items
		var e = dojo.dnd._empty;
		for(var i in this.selection){
			if(i in e){ continue; }
			var node = dojo.byId(i);
			if(node){ this._removeItemClass(node, "Selected"); }
		}
		this.selection = {};
		return this;	// self
	},
	_removeAnchor: function(){
		if(this.anchor){
			this._removeItemClass(this.anchor, "Anchor");
			this.anchor = null;
		}
		return this;	// self
	}
});

}

if(!dojo._hasResource["dojo.dnd.Avatar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.dnd.Avatar"] = true;
	dojo.provide("dojo.dnd.Avatar");

	dojo.dnd.Avatar = function(manager){
		// summary: an object, which represents transferred DnD items visually
		// manager: Object: a DnD manager object
		this.manager = manager;
		this.construct();
	};

	dojo.extend(dojo.dnd.Avatar, {
		construct: function(){
			// summary: a constructor function;
			//	it is separate so it can be (dynamically) overwritten in case of need
			var a = dojo.doc.createElement("table");
			a.className = "dojoDndAvatar";
			a.style.position = "absolute";
			a.style.zIndex = 1999;
			a.style.margin = "0px"; // to avoid dojo.marginBox() problems with table's margins
			var b = dojo.doc.createElement("tbody");
			var tr = dojo.doc.createElement("tr");
			tr.className = "dojoDndAvatarHeader";
			var td = dojo.doc.createElement("td");
			td.innerHTML = this._generateText();
			tr.appendChild(td);
			dojo.style(tr, "opacity", 0.9);
			b.appendChild(tr);
			var k = Math.min(5, this.manager.nodes.length);
			var source = this.manager.source;
			for(var i = 0; i < k; ++i){
				tr = dojo.doc.createElement("tr");
				tr.className = "dojoDndAvatarItem";
				td = dojo.doc.createElement("td");
				var node = source.creator ?
					// create an avatar representation of the node
					node = source._normalizedCreator(source.getItem(this.manager.nodes[i].id).data, "avatar").node :
					// or just clone the node and hope it works
					node = this.manager.nodes[i].cloneNode(true);
				node.id = "";
				td.appendChild(node);
				tr.appendChild(td);
				dojo.style(tr, "opacity", (9 - i) / 10);
				b.appendChild(tr);
			}
			a.appendChild(b);
			this.node = a;
		},
		destroy: function(){
			// summary: a desctructor for the avatar, called to remove all references so it can be garbage-collected
			dojo._destroyElement(this.node);
			this.node = false;
		},
		update: function(){
			// summary: updates the avatar to reflect the current DnD state
			dojo[(this.manager.canDropFlag ? "add" : "remove") + "Class"](this.node, "dojoDndAvatarCanDrop");
			// replace text
			var t = this.node.getElementsByTagName("td");
			for(var i = 0; i < t.length; ++i){
				var n = t[i];
				if(dojo.hasClass(n.parentNode, "dojoDndAvatarHeader")){
					n.innerHTML = this._generateText();
					break;
				}
			}
		},
		_generateText: function(){
			// summary: generates a proper text to reflect copying or moving of items
			return this.manager.nodes.length.toString();
		}
	});

	}


if(!dojo._hasResource["dojo.dnd.Manager"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.dnd.Manager"] = true;
	dojo.provide("dojo.dnd.Manager");

	dojo.dnd.Manager = function(){
		// summary: the manager of DnD operations (usually a singleton)
		this.avatar  = null;
		this.source = null;
		this.nodes = [];
		this.copy  = true;
		this.target = null;
		this.canDropFlag = false;
		this.events = [];
	};

	dojo.extend(dojo.dnd.Manager, {
		// avatar's offset from the mouse
		OFFSET_X: 16,
		OFFSET_Y: 16,
		// methods
		overSource: function(source){
			// summary: called when a source detected a mouse-over conditiion
			// source: Object: the reporter
			if(this.avatar){
				this.target = (source && source.targetState != "Disabled") ? source : null;
				this.avatar.update();
			}
			dojo.publish("/dnd/source/over", [source]);
		},
		outSource: function(source){
			// summary: called when a source detected a mouse-out conditiion
			// source: Object: the reporter
			if(this.avatar){
				if(this.target == source){
					this.target = null;
					this.canDropFlag = false;
					this.avatar.update();
					dojo.publish("/dnd/source/over", [null]);
				}
			}else{
				dojo.publish("/dnd/source/over", [null]);
			}
		},
		startDrag: function(source, nodes, copy){
			// summary: called to initiate the DnD operation
			// source: Object: the source which provides items
			// nodes: Array: the list of transferred items
			// copy: Boolean: copy items, if true, move items otherwise
			this.source = source;
			this.nodes  = nodes;
			this.copy   = Boolean(copy); // normalizing to true boolean
			this.avatar = this.makeAvatar();
			dojo.body().appendChild(this.avatar.node);
			dojo.publish("/dnd/start", [source, nodes, this.copy]);
			this.events = [
				dojo.connect(dojo.doc, "onmousemove", this, "onMouseMove"),
				dojo.connect(dojo.doc, "onmouseup",   this, "onMouseUp"),
				dojo.connect(dojo.doc, "onkeydown",   this, "onKeyDown"),
				dojo.connect(dojo.doc, "onkeyup",     this, "onKeyUp")
			];
			var c = "dojoDnd" + (copy ? "Copy" : "Move");
			dojo.addClass(dojo.body(), c); 
		},
		canDrop: function(flag){
			// summary: called to notify if the current target can accept items
			var canDropFlag = this.target && flag;
			if(this.canDropFlag != canDropFlag){
				this.canDropFlag = canDropFlag;
				this.avatar.update();
			}
		},
		stopDrag: function(){
			// summary: stop the DnD in progress
			dojo.removeClass(dojo.body(), "dojoDndCopy");
			dojo.removeClass(dojo.body(), "dojoDndMove");
			dojo.forEach(this.events, dojo.disconnect);
			this.events = [];
			this.avatar.destroy();
			this.avatar = null;
			this.source = null;
			this.nodes = [];
		},
		makeAvatar: function(){
			// summary: makes the avatar, it is separate to be overwritten dynamically, if needed
			return new dojo.dnd.Avatar(this);
		},
		updateAvatar: function(){
			// summary: updates the avatar, it is separate to be overwritten dynamically, if needed
			this.avatar.update();
		},
		// mouse event processors
		onMouseMove: function(e){
			// summary: event processor for onmousemove
			// e: Event: mouse event
			var a = this.avatar;
			if(a){
				//dojo.dnd.autoScrollNodes(e);
				dojo.dnd.autoScroll(e);
				dojo.marginBox(a.node, {l: e.pageX + this.OFFSET_X, t: e.pageY + this.OFFSET_Y});
				var copy = Boolean(this.source.copyState(dojo.dnd.getCopyKeyState(e)));
				if(this.copy != copy){ 
					this._setCopyStatus(copy);
				}
			}
		},
		onMouseUp: function(e){
			// summary: event processor for onmouseup
			// e: Event: mouse event
			if(this.avatar && (!("mouseButton" in this.source) || this.source.mouseButton == e.button)){
				if(this.target && this.canDropFlag){
					var params = [this.source, this.nodes, Boolean(this.source.copyState(dojo.dnd.getCopyKeyState(e))), this.target];
					dojo.publish("/dnd/drop/before", params);
					dojo.publish("/dnd/drop", params);
				}else{
					dojo.publish("/dnd/cancel");
				}
				this.stopDrag();
			}
		},
		// keyboard event processors
		onKeyDown: function(e){
			// summary: event processor for onkeydown:
			//	watching for CTRL for copy/move status, watching for ESCAPE to cancel the drag
			// e: Event: keyboard event
			if(this.avatar){
				switch(e.keyCode){
					case dojo.keys.CTRL:
						var copy = Boolean(this.source.copyState(true));
						if(this.copy != copy){ 
							this._setCopyStatus(copy);
						}
						break;
					case dojo.keys.ESCAPE:
						dojo.publish("/dnd/cancel");
						this.stopDrag();
						break;
				}
			}
		},
		onKeyUp: function(e){
			// summary: event processor for onkeyup, watching for CTRL for copy/move status
			// e: Event: keyboard event
			if(this.avatar && e.keyCode == dojo.keys.CTRL){
				var copy = Boolean(this.source.copyState(false));
				if(this.copy != copy){ 
					this._setCopyStatus(copy);
				}
			}
		},
		// utilities
		_setCopyStatus: function(copy){
			// summary: changes the copy status
			// copy: Boolean: the copy status
			this.copy = copy;
			this.source._markDndStatus(this.copy);
			this.updateAvatar();
			dojo.removeClass(dojo.body(), "dojoDnd" + (this.copy ? "Move" : "Copy"));
			dojo.addClass(dojo.body(), "dojoDnd" + (this.copy ? "Copy" : "Move"));
		}
	});

	// summary: the manager singleton variable, can be overwritten, if needed
	dojo.dnd._manager = null;

	dojo.dnd.manager = function(){
		// summary: returns the current DnD manager, creates one if it is not created yet
		if(!dojo.dnd._manager){
			dojo.dnd._manager = new dojo.dnd.Manager();
		}
		return dojo.dnd._manager;	// Object
	};

	}



if(!dojo._hasResource["dojo.dnd.Source"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Source"] = true;
dojo.provide("dojo.dnd.Source");

/*
	Container property:
		"Horizontal"- if this is the horizontal container
	Source states:
		""			- normal state
		"Moved"		- this source is being moved
		"Copied"	- this source is being copied
	Target states:
		""			- normal state
		"Disabled"	- the target cannot accept an avatar
	Target anchor state:
		""			- item is not selected
		"Before"	- insert point is before the anchor
		"After"		- insert point is after the anchor
*/

dojo.declare("dojo.dnd.Source", dojo.dnd.Selector, {
	// summary: a Source object, which can be used as a DnD source, or a DnD target
	
	// object attributes (for markup)
	isSource: true,
	horizontal: false,
	copyOnly: false,
	skipForm: false,
	withHandles: false,
	accept: ["text"],
	
	constructor: function(node, params){
		// summary: a constructor of the Source
		// node: Node: node or node's id to build the source on
		// params: Object: a dict of parameters, recognized parameters are:
		//	isSource: Boolean: can be used as a DnD source, if true; assumed to be "true" if omitted
		//	accept: Array: list of accepted types (text strings) for a target; assumed to be ["text"] if omitted
		//	horizontal: Boolean: a horizontal container, if true, vertical otherwise or when omitted
		//	copyOnly: Boolean: always copy items, if true, use a state of Ctrl key otherwise
		//	withHandles: Boolean: allows dragging only by handles
		//	the rest of parameters are passed to the selector
		if(!params){ params = {}; }
		this.isSource = typeof params.isSource == "undefined" ? true : params.isSource;
		var type = params.accept instanceof Array ? params.accept : ["text"];
		this.accept = null;
		if(type.length){
			this.accept = {};
			for(var i = 0; i < type.length; ++i){
				this.accept[type[i]] = 1;
			}
		}
		this.horizontal = params.horizontal;
		this.copyOnly = params.copyOnly;
		this.withHandles = params.withHandles;
		// class-specific variables
		this.isDragging = false;
		this.mouseDown = false;
		this.targetAnchor = null;
		this.targetBox = null;
		this.before = true;
		// states
		this.sourceState  = "";
		if(this.isSource){
			dojo.addClass(this.node, "dojoDndSource");
		}
		this.targetState  = "";
		if(this.accept){
			dojo.addClass(this.node, "dojoDndTarget");
		}
		if(this.horizontal){
			dojo.addClass(this.node, "dojoDndHorizontal");
		}
		// set up events
		this.topics = [
			dojo.subscribe("/dnd/source/over", this, "onDndSourceOver"),
			dojo.subscribe("/dnd/start",  this, "onDndStart"),
			dojo.subscribe("/dnd/drop",   this, "onDndDrop"),
			dojo.subscribe("/dnd/cancel", this, "onDndCancel")
		];
	},
	
	// methods
	checkAcceptance: function(source, nodes){
		// summary: checks, if the target can accept nodes from this source
		// source: Object: the source which provides items
		// nodes: Array: the list of transferred items
		if(this == source){ return true; }
		for(var i = 0; i < nodes.length; ++i){
			var type = source.getItem(nodes[i].id).type;
			// type instanceof Array
			var flag = false;
			for(var j = 0; j < type.length; ++j){
				if(type[j] in this.accept){
					flag = true;
					break;
				}
			}
			if(!flag){
				return false;	// Boolean
			}
		}
		return true;	// Boolean
	},
	copyState: function(keyPressed){
		// summary: Returns true, if we need to copy items, false to move.
		//		It is separated to be overwritten dynamically, if needed.
		// keyPressed: Boolean: the "copy" was pressed
		return this.copyOnly || keyPressed;	// Boolean
	},
	destroy: function(){
		// summary: prepares the object to be garbage-collected
		dojo.dnd.Source.superclass.destroy.call(this);
		dojo.forEach(this.topics, dojo.unsubscribe);
		this.targetAnchor = null;
	},

	// markup methods
	markupFactory: function(params, node){
		params._skipStartup = true;
		return new dojo.dnd.Source(node, params);
	},

	// mouse event processors
	onMouseMove: function(e){
		// summary: event processor for onmousemove
		// e: Event: mouse event
		if(this.isDragging && this.targetState == "Disabled"){ return; }
		dojo.dnd.Source.superclass.onMouseMove.call(this, e);
		var m = dojo.dnd.manager();
		if(this.isDragging){
			// calculate before/after
			var before = false;
			if(this.current){
				if(!this.targetBox || this.targetAnchor != this.current){
					this.targetBox = {
						xy: dojo.coords(this.current, true),
						w: this.current.offsetWidth,
						h: this.current.offsetHeight
					};
				}
				if(this.horizontal){
					before = (e.pageX - this.targetBox.xy.x) < (this.targetBox.w / 2);
				}else{
					before = (e.pageY - this.targetBox.xy.y) < (this.targetBox.h / 2);
				}
			}
			if(this.current != this.targetAnchor || before != this.before){
				this._markTargetAnchor(before);
				m.canDrop(!this.current || m.source != this || !(this.current.id in this.selection));
			}
		}else{
			if(this.mouseDown && this.isSource){
				var nodes = this.getSelectedNodes();
				if(nodes.length){
					m.startDrag(this, nodes, this.copyState(dojo.dnd.getCopyKeyState(e)));
				}
			}
		}
	},
	onMouseDown: function(e){
		// summary: event processor for onmousedown
		// e: Event: mouse event
		if(this._legalMouseDown(e) && (!this.skipForm || !dojo.dnd.isFormElement(e))){
			this.mouseDown = true;
			this.mouseButton = e.button;
			dojo.dnd.Source.superclass.onMouseDown.call(this, e);
		}
	},
	onMouseUp: function(e){
		// summary: event processor for onmouseup
		// e: Event: mouse event
		if(this.mouseDown){
			this.mouseDown = false;
			dojo.dnd.Source.superclass.onMouseUp.call(this, e);
		}
	},
	
	// topic event processors
	onDndSourceOver: function(source){
		// summary: topic event processor for /dnd/source/over, called when detected a current source
		// source: Object: the source which has the mouse over it
		if(this != source){
			this.mouseDown = false;
			if(this.targetAnchor){
				this._unmarkTargetAnchor();
			}
		}else if(this.isDragging){
			var m = dojo.dnd.manager();
			m.canDrop(this.targetState != "Disabled" && (!this.current || m.source != this || !(this.current.id in this.selection)));
		}
	},
	onDndStart: function(source, nodes, copy){
		// summary: topic event processor for /dnd/start, called to initiate the DnD operation
		// source: Object: the source which provides items
		// nodes: Array: the list of transferred items
		// copy: Boolean: copy items, if true, move items otherwise
		if(this.isSource){
			this._changeState("Source", this == source ? (copy ? "Copied" : "Moved") : "");
		}
		var accepted = this.accept && this.checkAcceptance(source, nodes);
		this._changeState("Target", accepted ? "" : "Disabled");
		if(accepted && this == source){
			dojo.dnd.manager().overSource(this);
		}
		this.isDragging = true;
	},
	onDndDrop: function(source, nodes, copy){
		// summary: topic event processor for /dnd/drop, called to finish the DnD operation
		// source: Object: the source which provides items
		// nodes: Array: the list of transferred items
		// copy: Boolean: copy items, if true, move items otherwise
		do{ //break box
			if(this.containerState != "Over"){ break; }
			var oldCreator = this._normalizedCreator;
			if(this != source){
				// transferring nodes from the source to the target
				if(this.creator){
					// use defined creator
					this._normalizedCreator = function(node, hint){
						return oldCreator.call(this, source.getItem(node.id).data, hint);
					};
				}else{
					// we have no creator defined => move/clone nodes
					if(copy){
						// clone nodes
						this._normalizedCreator = function(node, hint){
							var t = source.getItem(node.id);
							var n = node.cloneNode(true);
							n.id = dojo.dnd.getUniqueId();
							return {node: n, data: t.data, type: t.type};
						};
					}else{
						// move nodes
						this._normalizedCreator = function(node, hint){
							var t = source.getItem(node.id);
							source.delItem(node.id);
							return {node: node, data: t.data, type: t.type};
						};
					}
				}
			}else{
				// transferring nodes within the single source
				if(this.current && this.current.id in this.selection){ break; }
				if(this.creator){
					// use defined creator
					if(copy){
						// create new copies of data items
						this._normalizedCreator = function(node, hint){
							return oldCreator.call(this, source.getItem(node.id).data, hint);
						};
					}else{
						// move nodes
						if(!this.current){ break; }
						this._normalizedCreator = function(node, hint){
							var t = source.getItem(node.id);
							return {node: node, data: t.data, type: t.type};
						};
					}
				}else{
					// we have no creator defined => move/clone nodes
					if(copy){
						// clone nodes
						this._normalizedCreator = function(node, hint){
							var t = source.getItem(node.id);
							var n = node.cloneNode(true);
							n.id = dojo.dnd.getUniqueId();
							return {node: n, data: t.data, type: t.type};
						};
					}else{
						// move nodes
						if(!this.current){ break; }
						this._normalizedCreator = function(node, hint){
							var t = source.getItem(node.id);
							return {node: node, data: t.data, type: t.type};
						};
					}
				}
			}
			this._removeSelection();
			if(this != source){
				this._removeAnchor();
			}
			if(this != source && !copy && !this.creator){
				source.selectNone();
			}
			this.insertNodes(true, nodes, this.before, this.current);
			if(this != source && !copy && this.creator){
				source.deleteSelectedNodes();
			}
			this._normalizedCreator = oldCreator;
		}while(false);
		this.onDndCancel();
	},
	onDndCancel: function(){
		// summary: topic event processor for /dnd/cancel, called to cancel the DnD operation
		if(this.targetAnchor){
			this._unmarkTargetAnchor();
			this.targetAnchor = null;
		}
		this.before = true;
		this.isDragging = false;
		this.mouseDown = false;
		delete this.mouseButton;
		this._changeState("Source", "");
		this._changeState("Target", "");
	},
	
	// utilities
	onOverEvent: function(){
		// summary: this function is called once, when mouse is over our container
		dojo.dnd.Source.superclass.onOverEvent.call(this);
		dojo.dnd.manager().overSource(this);
	},
	onOutEvent: function(){
		// summary: this function is called once, when mouse is out of our container
		dojo.dnd.Source.superclass.onOutEvent.call(this);
		dojo.dnd.manager().outSource(this);
	},
	_markTargetAnchor: function(before){
		// summary: assigns a class to the current target anchor based on "before" status
		// before: Boolean: insert before, if true, after otherwise
		if(this.current == this.targetAnchor && this.before == before){ return; }
		if(this.targetAnchor){
			this._removeItemClass(this.targetAnchor, this.before ? "Before" : "After");
		}
		this.targetAnchor = this.current;
		this.targetBox = null;
		this.before = before;
		if(this.targetAnchor){
			this._addItemClass(this.targetAnchor, this.before ? "Before" : "After");
		}
	},
	_unmarkTargetAnchor: function(){
		// summary: removes a class of the current target anchor based on "before" status
		if(!this.targetAnchor){ return; }
		this._removeItemClass(this.targetAnchor, this.before ? "Before" : "After");
		this.targetAnchor = null;
		this.targetBox = null;
		this.before = true;
	},
	_markDndStatus: function(copy){
		// summary: changes source's state based on "copy" status
		this._changeState("Source", copy ? "Copied" : "Moved");
	},
	_legalMouseDown: function(e){
		// summary: checks if user clicked on "approved" items
		// e: Event: mouse event
		if(!this.withHandles){ return true; }
		for(var node = e.target; node && !dojo.hasClass(node, "dojoDndItem"); node = node.parentNode){
			if(dojo.hasClass(node, "dojoDndHandle")){ return true; }
		}
		return false;	// Boolean
	}
});

dojo.declare("dojo.dnd.Target", dojo.dnd.Source, {
	// summary: a Target object, which can be used as a DnD target
	
	constructor: function(node, params){
		// summary: a constructor of the Target --- see the Source constructor for details
		this.isSource = false;
		dojo.removeClass(this.node, "dojoDndSource");
	},

	// markup methods
	markupFactory: function(params, node){
		params._skipStartup = true;
		return new dojo.dnd.Target(node, params);
	}
});

}




if(!dojo._hasResource["dojo.dnd.Mover"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Mover"] = true;
dojo.provide("dojo.dnd.Mover");


dojo.declare("dojo.dnd.Mover", null, {
	constructor: function(node, e, host){
		// summary: an object, which makes a node follow the mouse, 
		//	used as a default mover, and as a base class for custom movers
		// node: Node: a node (or node's id) to be moved
		// e: Event: a mouse event, which started the move;
		//	only pageX and pageY properties are used
		// host: Object?: object which implements the functionality of the move,
		//	 and defines proper events (onMoveStart and onMoveStop)
		this.node = dojo.byId(node);
		this.marginBox = {l: e.pageX, t: e.pageY};
		this.mouseButton = e.button;
		var h = this.host = host, d = node.ownerDocument, 
			firstEvent = dojo.connect(d, "onmousemove", this, "onFirstMove");
		this.events = [
			dojo.connect(d, "onmousemove", this, "onMouseMove"),
			dojo.connect(d, "onmouseup",   this, "onMouseUp"),
			// cancel text selection and text dragging
			dojo.connect(d, "ondragstart",   dojo, "stopEvent"),
			dojo.connect(d, "onselectstart", dojo, "stopEvent"),
			firstEvent
		];
		// notify that the move has started
		if(h && h.onMoveStart){
			h.onMoveStart(this);
		}
	},
	// mouse event processors
	onMouseMove: function(e){
		// summary: event processor for onmousemove
		// e: Event: mouse event
		dojo.dnd.autoScroll(e);
		var m = this.marginBox;
		this.host.onMove(this, {l: m.l + e.pageX, t: m.t + e.pageY});
	},
	onMouseUp: function(e){
		if(this.mouseButton == e.button){
			this.destroy();
		}
	},
	// utilities
	onFirstMove: function(){
		// summary: makes the node absolute; it is meant to be called only once
		this.node.style.position = "absolute";	// enforcing the absolute mode
		var m = dojo.marginBox(this.node);
		m.l -= this.marginBox.l;
		m.t -= this.marginBox.t;
		this.marginBox = m;
		this.host.onFirstMove(this);
		dojo.disconnect(this.events.pop());
	},
	destroy: function(){
		// summary: stops the move, deletes all references, so the object can be garbage-collected
		dojo.forEach(this.events, dojo.disconnect);
		// undo global settings
		var h = this.host;
		if(h && h.onMoveStop){
			h.onMoveStop(this);
		}
		// destroy objects
		this.events = this.node = null;
	}
});

}


if(!dojo._hasResource["dojo.dnd.Moveable"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Moveable"] = true;
dojo.provide("dojo.dnd.Moveable");

dojo.declare("dojo.dnd.Moveable", null, {
	// object attributes (for markup)
	handle: "",
	delay: 0,
	skip: false,
	
	constructor: function(node, params){
		// summary: an object, which makes a node moveable
		// node: Node: a node (or node's id) to be moved
		// params: Object: an optional object with additional parameters;
		//	following parameters are recognized:
		//		handle: Node: a node (or node's id), which is used as a mouse handle
		//			if omitted, the node itself is used as a handle
		//		delay: Number: delay move by this number of pixels
		//		skip: Boolean: skip move of form elements
		//		mover: Object: a constructor of custom Mover
		this.node = dojo.byId(node);
		if(!params){ params = {}; }
		this.handle = params.handle ? dojo.byId(params.handle) : null;
		if(!this.handle){ this.handle = this.node; }
		this.delay = params.delay > 0 ? params.delay : 0;
		this.skip  = params.skip;
		this.mover = params.mover ? params.mover : dojo.dnd.Mover;
		this.events = [
			dojo.connect(this.handle, "onmousedown", this, "onMouseDown"),
			// cancel text selection and text dragging
			dojo.connect(this.handle, "ondragstart",   this, "onSelectStart"),
			dojo.connect(this.handle, "onselectstart", this, "onSelectStart")
		];
	},

	// markup methods
	markupFactory: function(params, node){
		return new dojo.dnd.Moveable(node, params);
	},

	// methods
	destroy: function(){
		// summary: stops watching for possible move, deletes all references, so the object can be garbage-collected
		dojo.forEach(this.events, dojo.disconnect);
		this.events = this.node = this.handle = null;
	},
	
	// mouse event processors
	onMouseDown: function(e){
		// summary: event processor for onmousedown, creates a Mover for the node
		// e: Event: mouse event
		if(this.skip && dojo.dnd.isFormElement(e)){ return; }
		if(this.delay){
			this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"));
			this.events.push(dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
			this._lastX = e.pageX;
			this._lastY = e.pageY;
		}else{
			new this.mover(this.node, e, this);
		}
		dojo.stopEvent(e);
	},
	onMouseMove: function(e){
		// summary: event processor for onmousemove, used only for delayed drags
		// e: Event: mouse event
		if(Math.abs(e.pageX - this._lastX) > this.delay || Math.abs(e.pageY - this._lastY) > this.delay){
			this.onMouseUp(e);
			new this.mover(this.node, e, this);
		}
		dojo.stopEvent(e);
	},
	onMouseUp: function(e){
		// summary: event processor for onmouseup, used only for delayed delayed drags
		// e: Event: mouse event
		dojo.disconnect(this.events.pop());
		dojo.disconnect(this.events.pop());
	},
	onSelectStart: function(e){
		// summary: event processor for onselectevent and ondragevent
		// e: Event: mouse event
		if(!this.skip || !dojo.dnd.isFormElement(e)){
			dojo.stopEvent(e);
		}
	},
	
	// local events
	onMoveStart: function(/* dojo.dnd.Mover */ mover){
		// summary: called before every move operation
		dojo.publish("/dnd/move/start", [mover]);
		dojo.addClass(dojo.body(), "dojoMove"); 
		dojo.addClass(this.node, "dojoMoveItem"); 
	},
	onMoveStop: function(/* dojo.dnd.Mover */ mover){
		// summary: called after every move operation
		dojo.publish("/dnd/move/stop", [mover]);
		dojo.removeClass(dojo.body(), "dojoMove");
		dojo.removeClass(this.node, "dojoMoveItem");
	},
	onFirstMove: function(/* dojo.dnd.Mover */ mover){
		// summary: called during the very first move notification,
		//	can be used to initialize coordinates, can be overwritten.
		
		// default implementation does nothing
	},
	onMove: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called during every move notification,
		//	should actually move the node, can be overwritten.
		this.onMoving(mover, leftTop);
		dojo.marginBox(mover.node, leftTop);
		this.onMoved(mover, leftTop);
	},
	onMoving: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called before every incremental move,
		//	can be overwritten.
		
		// default implementation does nothing
	},
	onMoved: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called after every incremental move,
		//	can be overwritten.
		
		// default implementation does nothing
	}
});

}

if(!dojo._hasResource["dojo.dnd.move"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.dnd.move"] = true;
	dojo.provide("dojo.dnd.move");

	dojo.declare("dojo.dnd.move.constrainedMoveable", dojo.dnd.Moveable, {
		// object attributes (for markup)
		constraints: function(){},
		within: false,
		
		// markup methods
		markupFactory: function(params, node){
			return new dojo.dnd.move.constrainedMoveable(node, params);
		},

		constructor: function(node, params){
			// summary: an object, which makes a node moveable
			// node: Node: a node (or node's id) to be moved
			// params: Object: an optional object with additional parameters;
			//	following parameters are recognized:
			//		constraints: Function: a function, which calculates a constraint box,
			//			it is called in a context of the moveable object.
			//		within: Boolean: restrict move within boundaries.
			//	the rest is passed to the base class
			if(!params){ params = {}; }
			this.constraints = params.constraints;
			this.within = params.within;
		},
		onFirstMove: function(/* dojo.dnd.Mover */ mover){
			// summary: called during the very first move notification,
			//	can be used to initialize coordinates, can be overwritten.
			var c = this.constraintBox = this.constraints.call(this, mover), m = mover.marginBox;
			c.r = c.l + c.w - (this.within ? m.w : 0);
			c.b = c.t + c.h - (this.within ? m.h : 0);
		},
		onMove: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
			// summary: called during every move notification,
			//	should actually move the node, can be overwritten.
			var c = this.constraintBox;
			leftTop.l = leftTop.l < c.l ? c.l : c.r < leftTop.l ? c.r : leftTop.l;
			leftTop.t = leftTop.t < c.t ? c.t : c.b < leftTop.t ? c.b : leftTop.t;
			dojo.marginBox(mover.node, leftTop);
		}
	});

	dojo.declare("dojo.dnd.move.boxConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {
		// object attributes (for markup)
		box: {},
		
		// markup methods
		markupFactory: function(params, node){
			return new dojo.dnd.move.boxConstrainedMoveable(node, params);
		},

		constructor: function(node, params){
			// summary: an object, which makes a node moveable
			// node: Node: a node (or node's id) to be moved
			// params: Object: an optional object with additional parameters;
			//	following parameters are recognized:
			//		box: Object: a constraint box
			//	the rest is passed to the base class
			var box = params && params.box;
			this.constraints = function(){ return box; };
		}
	});

	dojo.declare("dojo.dnd.move.parentConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {
		// object attributes (for markup)
		area: "content",

		// markup methods
		markupFactory: function(params, node){
			return new dojo.dnd.move.parentConstrainedMoveable(node, params);
		},

		constructor: function(node, params){
			// summary: an object, which makes a node moveable
			// node: Node: a node (or node's id) to be moved
			// params: Object: an optional object with additional parameters;
			//	following parameters are recognized:
			//		area: String: a parent's area to restrict the move,
			//			can be "margin", "border", "padding", or "content".
			//	the rest is passed to the base class
			var area = params && params.area;
			this.constraints = function(){
				var n = this.node.parentNode, 
					s = dojo.getComputedStyle(n), 
					mb = dojo._getMarginBox(n, s);
				if(area == "margin"){
					return mb;	// Object
				}
				var t = dojo._getMarginExtents(n, s);
				mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
				if(area == "border"){
					return mb;	// Object
				}
				t = dojo._getBorderExtents(n, s);
				mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
				if(area == "padding"){
					return mb;	// Object
				}
				t = dojo._getPadExtents(n, s);
				mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
				return mb;	// Object
			};
		}
	});

	// WARNING: below are obsolete objects, instead of custom movers use custom moveables (above)

	dojo.dnd.move.constrainedMover = function(fun, within){
		// summary: returns a constrained version of dojo.dnd.Mover
		// description: this function produces n object, which will put a constraint on 
		//	the margin box of dragged object in absolute coordinates
		// fun: Function: called on drag, and returns a constraint box
		// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
		//	otherwise the constraint is applied to the left-top corner
		var mover = function(node, e, notifier){
			dojo.dnd.Mover.call(this, node, e, notifier);
		};
		dojo.extend(mover, dojo.dnd.Mover.prototype);
		dojo.extend(mover, {
			onMouseMove: function(e){
				// summary: event processor for onmousemove
				// e: Event: mouse event
				dojo.dnd.autoScroll(e);
				var m = this.marginBox, c = this.constraintBox,
					l = m.l + e.pageX, t = m.t + e.pageY;
				l = l < c.l ? c.l : c.r < l ? c.r : l;
				t = t < c.t ? c.t : c.b < t ? c.b : t;
				this.host.onMove(this, {l: l, t: t});
			},
			onFirstMove: function(){
				// summary: called once to initialize things; it is meant to be called only once
				dojo.dnd.Mover.prototype.onFirstMove.call(this);
				var c = this.constraintBox = fun.call(this), m = this.marginBox;
				c.r = c.l + c.w - (within ? m.w : 0);
				c.b = c.t + c.h - (within ? m.h : 0);
			}
		});
		return mover;	// Object
	};

	dojo.dnd.move.boxConstrainedMover = function(box, within){
		// summary: a specialization of dojo.dnd.constrainedMover, which constrains to the specified box
		// box: Object: a constraint box (l, t, w, h)
		// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
		//	otherwise the constraint is applied to the left-top corner
		return dojo.dnd.move.constrainedMover(function(){ return box; }, within);	// Object
	};

	dojo.dnd.move.parentConstrainedMover = function(area, within){
		// summary: a specialization of dojo.dnd.constrainedMover, which constrains to the parent node
		// area: String: "margin" to constrain within the parent's margin box, "border" for the border box,
		//	"padding" for the padding box, and "content" for the content box; "content" is the default value.
		// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
		//	otherwise the constraint is applied to the left-top corner
		var fun = function(){
			var n = this.node.parentNode, 
				s = dojo.getComputedStyle(n), 
				mb = dojo._getMarginBox(n, s);
			if(area == "margin"){
				return mb;	// Object
			}
			var t = dojo._getMarginExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			if(area == "border"){
				return mb;	// Object
			}
			t = dojo._getBorderExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			if(area == "padding"){
				return mb;	// Object
			}
			t = dojo._getPadExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			return mb;	// Object
		};
		return dojo.dnd.move.constrainedMover(fun, within);	// Object
	};

	// patching functions one level up for compatibility

	dojo.dnd.constrainedMover = dojo.dnd.move.constrainedMover;
	dojo.dnd.boxConstrainedMover = dojo.dnd.move.boxConstrainedMover;
	dojo.dnd.parentConstrainedMover = dojo.dnd.move.parentConstrainedMover;

	}


if(!dojo._hasResource["dojo.io.iframe"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.io.iframe"] = true;
dojo.provide("dojo.io.iframe");

dojo.io.iframe = {
	create: function(/*String*/fname, /*String*/onloadstr, /*String?*/uri){
		//	summary:
		//		Creates a hidden iframe in the page. Used mostly for IO
		//		transports.  You do not need to call this to start a
		//		dojo.io.iframe request. Just call send().
		//	fname: String
		//		The name of the iframe. Used for the name attribute on the
		//		iframe.
		//	onloadstr: String
		//		A string of JavaScript that will be executed when the content
		//		in the iframe loads.
		//	uri: String
		//		The value of the src attribute on the iframe element. If a
		//		value is not given, then dojo/resources/blank.html will be
		//		used.
		if(window[fname]){ return window[fname]; }
		if(window.frames[fname]){ return window.frames[fname]; }
		var cframe = null;
		var turi = uri;
		if(!turi){
			if(djConfig["useXDomain"] && !djConfig["dojoBlankHtmlUrl"]){
				console.debug("dojo.io.iframe.create: When using cross-domain Dojo builds,"
					+ " please save dojo/resources/blank.html to your domain and set djConfig.dojoBlankHtmlUrl"
					+ " to the path on your domain to blank.html");
			}
			turi = (djConfig["dojoBlankHtmlUrl"]||dojo.moduleUrl("dojo", "resources/blank.html"));
		}
		var ifrstr = dojo.isIE ? '<iframe name="'+fname+'" src="'+turi+'" onload="'+onloadstr+'">' : 'iframe';
		cframe = dojo.doc.createElement(ifrstr);
		with(cframe){
			name = fname;
			setAttribute("name", fname);
			id = fname;
		}
		dojo.body().appendChild(cframe);
		window[fname] = cframe;
	
		with(cframe.style){
			if(dojo.isSafari < 3){
				//We can't change the src in Safari 2.0.3 if absolute position. Bizarro.
				position = "absolute";
			}
			left = top = "1px";
			height = width = "1px";
			visibility = "hidden";
		}

		if(!dojo.isIE){
			this.setSrc(cframe, turi, true);
			cframe.onload = new Function(onloadstr);
		}

		return cframe;
	},

	setSrc: function(/*DOMNode*/iframe, /*String*/src, /*Boolean*/replace){
		//summary:
		//		Sets the URL that is loaded in an IFrame. The replace parameter
		//		indicates whether location.replace() should be used when
		//		changing the location of the iframe.
		try{
			if(!replace){
				if(dojo.isSafari){
					iframe.location = src;
				}else{
					frames[iframe.name].location = src;
				}
			}else{
				// Fun with DOM 0 incompatibilities!
				var idoc;
				if(dojo.isIE || dojo.isSafari > 2){
					idoc = iframe.contentWindow.document;
				}else if(dojo.isSafari){
					idoc = iframe.document;
				}else{ //  if(d.isMozilla){
					idoc = iframe.contentWindow;
				}
	
				//For Safari (at least 2.0.3) and Opera, if the iframe
				//has just been created but it doesn't have content
				//yet, then iframe.document may be null. In that case,
				//use iframe.location and return.
				if(!idoc){
					iframe.location = src;
					return;
				}else{
					idoc.location.replace(src);
				}
			}
		}catch(e){ 
			console.debug("dojo.io.iframe.setSrc: ", e); 
		}
	},

	doc: function(/*DOMNode*/iframeNode){
		//summary: Returns the document object associated with the iframe DOM Node argument.
		var doc = iframeNode.contentDocument || // W3
			(
				(iframeNode.contentWindow)&&(iframeNode.contentWindow.document)
			) ||  // IE
			(
				(iframeNode.name)&&(document.frames[iframeNode.name])&&
				(document.frames[iframeNode.name].document)
			) || null;
		return doc;
	},

	/*=====
	dojo.io.iframe.__ioArgs = function(kwArgs){
		//	summary:
		//		All the properties described in the dojo.__ioArgs type, apply
		//		to this type. The following additional properties are allowed
		//		for dojo.io.iframe.send():
		//	method: String?
		//		The HTTP method to use. "GET" or "POST" are the only supported
		//		values.  It will try to read the value from the form node's
		//		method, then try this argument. If neither one exists, then it
		//		defaults to POST.
		//	handleAs: String?
		//		Specifies what format the result data should be given to the
		//		load/handle callback. Valid values are: text, html, javascript,
		//		json. IMPORTANT: For all values EXCEPT html, The server
		//		response should be an HTML file with a textarea element. The
		//		response data should be inside the textarea element. Using an
		//		HTML document the only reliable, cross-browser way this
		//		transport can know when the response has loaded. For the html
		//		handleAs value, just return a normal HTML document.  NOTE: xml
		//		or any other XML type is NOT supported by this transport.
		//	content: Object?
		//		If "form" is one of the other args properties, then the content
		//		object properties become hidden form form elements. For
		//		instance, a content object of {name1 : "value1"} is converted
		//		to a hidden form element with a name of "name1" and a value of
		//		"value1". If there is not a "form" property, then the content
		//		object is converted into a name=value&name=value string, by
		//		using dojo.objectToQuery().
	}
	=====*/

	send: function(/*dojo.io.iframe.__ioArgs*/args){
		//summary: function that sends the request to the server.
		//This transport can only process one send() request at a time, so if send() is called
		//multiple times, it will queue up the calls and only process one at a time.
		if(!this["_frame"]){
			this._frame = this.create(this._iframeName, "dojo.io.iframe._iframeOnload();");
		}

		//Set up the deferred.
		var dfd = dojo._ioSetArgs(
			args,
			function(/*Deferred*/dfd){
				//summary: canceller function for dojo._ioSetArgs call.
				dfd.canceled = true;
				dfd.ioArgs._callNext();
			},
			function(/*Deferred*/dfd){
				//summary: okHandler function for dojo._ioSetArgs call.
				var value = null;
				try{
					var ioArgs = dfd.ioArgs;
					var dii = dojo.io.iframe;
					var ifd = dii.doc(dii._frame);
					var handleAs = ioArgs.handleAs;

					//Assign correct value based on handleAs value.
					value = ifd; //html
					if(handleAs != "html"){
						value = ifd.getElementsByTagName("textarea")[0].value; //text
						if(handleAs == "json"){
							value = dojo.fromJson(value); //json
						}else if(handleAs == "javascript"){
							value = dojo.eval(value); //javascript
						}
					}
				}catch(e){
					value = e;
				}finally{
					ioArgs._callNext();				
				}
				return value;
			},
			function(/*Error*/error, /*Deferred*/dfd){
				//summary: errHandler function for dojo._ioSetArgs call.
				dfd.ioArgs._hasError = true;
				dfd.ioArgs._callNext();
				return error;
			}
		);

		//Set up a function that will fire the next iframe request. Make sure it only
		//happens once per deferred.
		dfd.ioArgs._callNext = function(){
			if(!this["_calledNext"]){
				this._calledNext = true;
				dojo.io.iframe._currentDfd = null;
				dojo.io.iframe._fireNextRequest();
			}
		}

		this._dfdQueue.push(dfd);
		this._fireNextRequest();
		
		//Add it the IO watch queue, to get things like timeout support.
		dojo._ioWatch(
			dfd,
			function(/*Deferred*/dfd){
				//validCheck
				return !dfd.ioArgs["_hasError"];
			},
			function(dfd){
				//ioCheck
				return (!!dfd.ioArgs["_finished"]);
			},
			function(dfd){
				//resHandle
				if(dfd.ioArgs._finished){
					dfd.callback(dfd);
				}else{
					dfd.errback(new Error("Invalid dojo.io.iframe request state"));
				}
			}
		);

		return dfd;
	},

	_currentDfd: null,
	_dfdQueue: [],
	_iframeName: "dojoIoIframe",

	_fireNextRequest: function(){
		//summary: Internal method used to fire the next request in the bind queue.
		try{
			if((this._currentDfd)||(this._dfdQueue.length == 0)){ return; }
			var dfd = this._currentDfd = this._dfdQueue.shift();
			var ioArgs = dfd.ioArgs;
			var args = ioArgs.args;

			ioArgs._contentToClean = [];
			var fn = args["form"];
			var content = args["content"] || {};
			if(fn){
				if(content){
					// if we have things in content, we need to add them to the form
					// before submission
					for(var x in content){
						if(!fn[x]){
							var tn;
							if(dojo.isIE){
								tn = dojo.doc.createElement("<input type='hidden' name='"+x+"'>");
							}else{
								tn = dojo.doc.createElement("input");
								tn.type = "hidden";
								tn.name = x;
							}
							tn.value = content[x];
							fn.appendChild(tn);
							ioArgs._contentToClean.push(x);
						}else{
							fn[x].value = content[x];
						}
					}
				}
				//IE requires going through getAttributeNode instead of just getAttribute in some form cases, 
				//so use it for all.  See #2844
				var actnNode = fn.getAttributeNode("action");
				var mthdNode = fn.getAttributeNode("method");
				var trgtNode = fn.getAttributeNode("target");
				if(args["url"]){
					ioArgs._originalAction = actnNode ? actnNode.value : null;
					if(actnNode){
						actnNode.value = args.url;
					}else{
						fn.setAttribute("action",args.url);
					}
				}
				if(!mthdNode || !mthdNode.value){
					if(mthdNode){
						mthdNode.value= (args["method"]) ? args["method"] : "post";
					}else{
						fn.setAttribute("method", (args["method"]) ? args["method"] : "post");
					}
				}
				ioArgs._originalTarget = trgtNode ? trgtNode.value: null;
				if(trgtNode){
					trgtNode.value = this._iframeName;
				}else{
					fn.setAttribute("target", this._iframeName);
				}
				fn.target = this._iframeName;
				fn.submit();
			}else{
				// otherwise we post a GET string by changing URL location for the
				// iframe
				var tmpUrl = args.url + (args.url.indexOf("?") > -1 ? "&" : "?") + ioArgs.query;
				this.setSrc(this._frame, tmpUrl, true);
			}
		}catch(e){
			dfd.errback(e);
		}
	},

	_iframeOnload: function(){
		var dfd = this._currentDfd;
		if(!dfd){
			this._fireNextRequest();
			return;
		}

		var ioArgs = dfd.ioArgs;
		var args = ioArgs.args;
		var fNode = args.form;
	
		if(fNode){
			// remove all the hidden content inputs
			var toClean = ioArgs._contentToClean;
			for(var i = 0; i < toClean.length; i++) {
				var key = toClean[i];
				if(dojo.isSafari < 3){
					//In Safari (at least 2.0.3), can't use form[key] syntax to find the node,
					//for nodes that were dynamically added.
					for(var j = 0; j < fNode.childNodes.length; j++){
						var chNode = fNode.childNodes[j];
						if(chNode.name == key){
							dojo._destroyElement(chNode);
							break;
						}
					}
				}else{
					dojo._destroyElement(fNode[key]);
					fNode[key] = null;
				}
			}
	
			// restore original action + target
			if(ioArgs["_originalAction"]){
				fNode.setAttribute("action", ioArgs._originalAction);
			}
			if(ioArgs["_originalTarget"]){
				fNode.setAttribute("target", ioArgs._originalTarget);
				fNode.target = ioArgs._originalTarget;
			}
		}

		ioArgs._finished = true;
	}
}

}



if(!dojo._hasResource["dojo.data.util.filter"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.data.util.filter"] = true;
	dojo.provide("dojo.data.util.filter");

	dojo.data.util.filter.patternToRegExp = function(/*String*/pattern, /*boolean?*/ ignoreCase){
		//	summary:  
		//		Helper function to convert a simple pattern to a regular expression for matching.
		//	description:
		//		Returns a regular expression object that conforms to the defined conversion rules.
		//		For example:  
		//			ca*   -> /^ca.*$/
		//			*ca*  -> /^.*ca.*$/
		//			*c\*a*  -> /^.*c\*a.*$/
		//			*c\*a?*  -> /^.*c\*a..*$/
		//			and so on.
		//
		//	pattern: string
		//		A simple matching pattern to convert that follows basic rules:
		//			* Means match anything, so ca* means match anything starting with ca
		//			? Means match single character.  So, b?b will match to bob and bab, and so on.
		//      	\ is an escape character.  So for example, \* means do not treat * as a match, but literal character *.
		//				To use a \ as a character in the string, it must be escaped.  So in the pattern it should be 
		//				represented by \\ to be treated as an ordinary \ character instead of an escape.
		//
		//	ignoreCase:
		//		An optional flag to indicate if the pattern matching should be treated as case-sensitive or not when comparing
		//		By default, it is assumed case sensitive.

		var rxp = "^";
		var c = null;
		for(var i = 0; i < pattern.length; i++){
			c = pattern.charAt(i);
			switch (c) {
				case '\\':
					rxp += c;
					i++;
					rxp += pattern.charAt(i);
					break;
				case '*':
					rxp += ".*"; break;
				case '?':
					rxp += "."; break;
				case '$':
				case '^':
				case '/':
				case '+':
				case '.':
				case '|':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
					rxp += "\\"; //fallthrough
				default:
					rxp += c;
			}
		}
		rxp += "$";
		if(ignoreCase){
			return new RegExp(rxp,"i"); //RegExp
		}else{
			return new RegExp(rxp); //RegExp
		}
		
	};

	}









if(!dojo._hasResource["dojo.data.util.sorter"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.data.util.sorter"] = true;
	dojo.provide("dojo.data.util.sorter");

	dojo.data.util.sorter.basicComparator = function(	/*anything*/ a, 
														/*anything*/ b){
		//	summary:  
		//		Basic comparision function that compares if an item is greater or less than another item
		//	description:  
		//		returns 1 if a > b, -1 if a < b, 0 if equal.
		//		undefined values are treated as larger values so that they're pushed to the end of the list.

		var ret = 0;
		if(a > b || typeof a === "undefined" || a === null){
			ret = 1;
		}else if(a < b || typeof b === "undefined" || b === null){
			ret = -1;
		}
		return ret; //int, {-1,0,1}
	};

	dojo.data.util.sorter.createSortFunction = function(	/* attributes array */sortSpec,
															/*dojo.data.core.Read*/ store){
		//	summary:  
		//		Helper function to generate the sorting function based off the list of sort attributes.
		//	description:  
		//		The sort function creation will look for a property on the store called 'comparatorMap'.  If it exists
		//		it will look in the mapping for comparisons function for the attributes.  If one is found, it will
		//		use it instead of the basic comparator, which is typically used for strings, ints, booleans, and dates.
		//		Returns the sorting function for this particular list of attributes and sorting directions.
		//
		//	sortSpec: array
		//		A JS object that array that defines out what attribute names to sort on and whether it should be descenting or asending.
		//		The objects should be formatted as follows:
		//		{
		//			attribute: "attributeName-string" || attribute,
		//			descending: true|false;   // Default is false.
		//		}
		//	store: object
		//		The datastore object to look up item values from.
		//
		var sortFunctions=[];   

		function createSortFunction(attr, dir){
			return function(itemA, itemB){
				var a = store.getValue(itemA, attr);
				var b = store.getValue(itemB, attr);
				//See if we have a override for an attribute comparison.
				var comparator = null;
				if(store.comparatorMap){
					if(typeof attr !== "string"){
						 attr = store.getIdentity(attr);
					}
					comparator = store.comparatorMap[attr]||dojo.data.util.sorter.basicComparator;
				}
				comparator = comparator||dojo.data.util.sorter.basicComparator; 
				return dir * comparator(a,b); //int
			};
		}

		for(var i = 0; i < sortSpec.length; i++){
			sortAttribute = sortSpec[i];
			if(sortAttribute.attribute){
				var direction = (sortAttribute.descending) ? -1 : 1;
				sortFunctions.push(createSortFunction(sortAttribute.attribute, direction));
			}
		}

		return function(rowA, rowB){
			var i=0;
			while(i < sortFunctions.length){
				var ret = sortFunctions[i++](rowA, rowB);
				if(ret !== 0){
					return ret;//int
				}
			}
			return 0; //int  
		};  //  Function
	};

	}


if(!dojo._hasResource["dojo.data.util.simpleFetch"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.data.util.simpleFetch"] = true;
	dojo.provide("dojo.data.util.simpleFetch");

	dojo.data.util.simpleFetch.fetch = function(/* Object? */ request){
		//	summary:
		//		The simpleFetch mixin is designed to serve as a set of function(s) that can
		//		be mixed into other datastore implementations to accelerate their development.  
		//		The simpleFetch mixin should work well for any datastore that can respond to a _fetchItems() 
		//		call by returning an array of all the found items that matched the query.  The simpleFetch mixin
		//		is not designed to work for datastores that respond to a fetch() call by incrementally
		//		loading items, or sequentially loading partial batches of the result
		//		set.  For datastores that mixin simpleFetch, simpleFetch 
		//		implements a fetch method that automatically handles eight of the fetch()
		//		arguments -- onBegin, onItem, onComplete, onError, start, count, sort and scope
		//		The class mixing in simpleFetch should not implement fetch(),
		//		but should instead implement a _fetchItems() method.  The _fetchItems() 
		//		method takes three arguments, the keywordArgs object that was passed 
		//		to fetch(), a callback function to be called when the result array is
		//		available, and an error callback to be called if something goes wrong.
		//		The _fetchItems() method should ignore any keywordArgs parameters for
		//		start, count, onBegin, onItem, onComplete, onError, sort, and scope.  
		//		The _fetchItems() method needs to correctly handle any other keywordArgs
		//		parameters, including the query parameter and any optional parameters 
		//		(such as includeChildren).  The _fetchItems() method should create an array of 
		//		result items and pass it to the fetchHandler along with the original request object 
		//		-- or, the _fetchItems() method may, if it wants to, create an new request object 
		//		with other specifics about the request that are specific to the datastore and pass 
		//		that as the request object to the handler.
		//
		//		For more information on this specific function, see dojo.data.api.Read.fetch()
		request = request || {};
		if(!request.store){
			request.store = this;
		}
		var self = this;

		var _errorHandler = function(errorData, requestObject){
			if(requestObject.onError){
				var scope = requestObject.scope || dojo.global;
				requestObject.onError.call(scope, errorData, requestObject);
			}
		};

		var _fetchHandler = function(items, requestObject){
			var oldAbortFunction = requestObject.abort || null;
			var aborted = false;

			var startIndex = requestObject.start?requestObject.start:0;
			var endIndex   = requestObject.count?(startIndex + requestObject.count):items.length;

			requestObject.abort = function(){
				aborted = true;
				if(oldAbortFunction){
					oldAbortFunction.call(requestObject);
				}
			};

			var scope = requestObject.scope || dojo.global;
			if(!requestObject.store){
				requestObject.store = self;
			}
			if(requestObject.onBegin){
				requestObject.onBegin.call(scope, items.length, requestObject);
			}
			if(requestObject.sort){
				items.sort(dojo.data.util.sorter.createSortFunction(requestObject.sort, self));
			}
			if(requestObject.onItem){
				for(var i = startIndex; (i < items.length) && (i < endIndex); ++i){
					var item = items[i];
					if(!aborted){
						requestObject.onItem.call(scope, item, requestObject);
					}
				}
			}
			if(requestObject.onComplete && !aborted){
				var subset = null;
				if (!requestObject.onItem) {
					subset = items.slice(startIndex, endIndex);
				}
				requestObject.onComplete.call(scope, subset, requestObject);   
			}
		};
		this._fetchItems(request, _fetchHandler, _errorHandler);
		return request;	// Object
	};

	}


if(!dojo._hasResource["dojo.data.ItemFileReadStore"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.data.ItemFileReadStore"] = true;
	dojo.provide("dojo.data.ItemFileReadStore");


	dojo.declare("dojo.data.ItemFileReadStore", null,{
		//	summary:
		//		The ItemFileReadStore implements the dojo.data.api.Read API and reads
		//		data from JSON files that have contents in this format --
		//		{ items: [
		//			{ name:'Kermit', color:'green', age:12, friends:['Gonzo', {_reference:{name:'Fozzie Bear'}}]},
		//			{ name:'Fozzie Bear', wears:['hat', 'tie']},
		//			{ name:'Miss Piggy', pets:'Foo-Foo'}
		//		]}
		//		Note that it can also contain an 'identifer' property that specified which attribute on the items 
		//		in the array of items that acts as the unique identifier for that item.
		//
		constructor: function(/* Object */ keywordParameters){
			//	summary: constructor
			//	keywordParameters: {url: String}
			//	keywordParameters: {data: jsonObject}
			//	keywordParameters: {typeMap: object)
			//		The structure of the typeMap object is as follows:
			//		{
			//			type0: function || object,
			//			type1: function || object,
			//			...
			//			typeN: function || object
			//		}
			//		Where if it is a function, it is assumed to be an object constructor that takes the 
			//		value of _value as the initialization parameters.  If it is an object, then it is assumed
			//		to be an object of general form:
			//		{
			//			type: function, //constructor.
			//			deserialize:	function(value) //The function that parses the value and constructs the object defined by type appropriately.
			//		}
		
			this._arrayOfAllItems = [];
			this._arrayOfTopLevelItems = [];
			this._loadFinished = false;
			this._jsonFileUrl = keywordParameters.url;
			this._jsonData = keywordParameters.data;
			this._datatypeMap = keywordParameters.typeMap || {};
			if(!this._datatypeMap['Date']){
				//If no default mapping for dates, then set this as default.
				//We use the dojo.date.stamp here because the ISO format is the 'dojo way'
				//of generically representing dates.
				this._datatypeMap['Date'] = {
												type: Date,
												deserialize: function(value){
													return dojo.date.stamp.fromISOString(value);
												}
											};
			}
			this._features = {'dojo.data.api.Read':true, 'dojo.data.api.Identity':true};
			this._itemsByIdentity = null;
			this._storeRefPropName = "_S";  // Default name for the store reference to attach to every item.
			this._itemNumPropName = "_0"; // Default Item Id for isItem to attach to every item.
			this._rootItemPropName = "_RI"; // Default Item Id for isItem to attach to every item.
			this._loadInProgress = false;	//Got to track the initial load to prevent duelling loads of the dataset.
			this._queuedFetches = [];
		},
		
		url: "",	// use "" rather than undefined for the benefit of the parser (#3539)

		_assertIsItem: function(/* item */ item){
			//	summary:
			//		This function tests whether the item passed in is indeed an item in the store.
			//	item: 
			//		The item to test for being contained by the store.
			if(!this.isItem(item)){ 
				throw new Error("dojo.data.ItemFileReadStore: Invalid item argument.");
			}
		},

		_assertIsAttribute: function(/* attribute-name-string */ attribute){
			//	summary:
			//		This function tests whether the item passed in is indeed a valid 'attribute' like type for the store.
			//	attribute: 
			//		The attribute to test for being contained by the store.
			if(typeof attribute !== "string"){ 
				throw new Error("dojo.data.ItemFileReadStore: Invalid attribute argument.");
			}
		},

		getValue: function(	/* item */ item, 
							/* attribute-name-string */ attribute, 
							/* value? */ defaultValue){
			//	summary: 
			//		See dojo.data.api.Read.getValue()
			var values = this.getValues(item, attribute);
			return (values.length > 0)?values[0]:defaultValue; // mixed
		},

		getValues: function(/* item */ item, 
							/* attribute-name-string */ attribute){
			//	summary: 
			//		See dojo.data.api.Read.getValues()

			this._assertIsItem(item);
			this._assertIsAttribute(attribute);
			return item[attribute] || []; // Array
		},

		getAttributes: function(/* item */ item){
			//	summary: 
			//		See dojo.data.api.Read.getAttributes()
			this._assertIsItem(item);
			var attributes = [];
			for(var key in item){
				// Save off only the real item attributes, not the special id marks for O(1) isItem.
				if((key !== this._storeRefPropName) && (key !== this._itemNumPropName) && (key !== this._rootItemPropName)){
					attributes.push(key);
				}
			}
			return attributes; // Array
		},

		hasAttribute: function(	/* item */ item,
								/* attribute-name-string */ attribute) {
			//	summary: 
			//		See dojo.data.api.Read.hasAttribute()
			return this.getValues(item, attribute).length > 0;
		},

		containsValue: function(/* item */ item, 
								/* attribute-name-string */ attribute, 
								/* anything */ value){
			//	summary: 
			//		See dojo.data.api.Read.containsValue()
			var regexp = undefined;
			if(typeof value === "string"){
				regexp = dojo.data.util.filter.patternToRegExp(value, false);
			}
			return this._containsValue(item, attribute, value, regexp); //boolean.
		},

		_containsValue: function(	/* item */ item, 
									/* attribute-name-string */ attribute, 
									/* anything */ value,
									/* RegExp?*/ regexp){
			//	summary: 
			//		Internal function for looking at the values contained by the item.
			//	description: 
			//		Internal function for looking at the values contained by the item.  This 
			//		function allows for denoting if the comparison should be case sensitive for
			//		strings or not (for handling filtering cases where string case should not matter)
			//	
			//	item:
			//		The data item to examine for attribute values.
			//	attribute:
			//		The attribute to inspect.
			//	value:	
			//		The value to match.
			//	regexp:
			//		Optional regular expression generated off value if value was of string type to handle wildcarding.
			//		If present and attribute values are string, then it can be used for comparison instead of 'value'
			return dojo.some(this.getValues(item, attribute), function(possibleValue){
				if(possibleValue !== null && !dojo.isObject(possibleValue) && regexp){
					if(possibleValue.toString().match(regexp)){
						return true; // Boolean
					}
				}else if(value === possibleValue){
					return true; // Boolean
				}
			});
		},

		isItem: function(/* anything */ something){
			//	summary: 
			//		See dojo.data.api.Read.isItem()
			if(something && something[this._storeRefPropName] === this){
				if(this._arrayOfAllItems[something[this._itemNumPropName]] === something){
					return true;
				}
			}
			return false; // Boolean
		},

		isItemLoaded: function(/* anything */ something){
			//	summary: 
			//		See dojo.data.api.Read.isItemLoaded()
			return this.isItem(something); //boolean
		},

		loadItem: function(/* object */ keywordArgs){
			//	summary: 
			//		See dojo.data.api.Read.loadItem()
			this._assertIsItem(keywordArgs.item);
		},

		getFeatures: function(){
			//	summary: 
			//		See dojo.data.api.Read.getFeatures()
			return this._features; //Object
		},

		getLabel: function(/* item */ item){
			//	summary: 
			//		See dojo.data.api.Read.getLabel()
			if(this._labelAttr && this.isItem(item)){
				return this.getValue(item,this._labelAttr); //String
			}
			return undefined; //undefined
		},

		getLabelAttributes: function(/* item */ item){
			//	summary: 
			//		See dojo.data.api.Read.getLabelAttributes()
			if(this._labelAttr){
				return [this._labelAttr]; //array
			}
			return null; //null
		},

		_fetchItems: function(	/* Object */ keywordArgs, 
								/* Function */ findCallback, 
								/* Function */ errorCallback){
			//	summary: 
			//		See dojo.data.util.simpleFetch.fetch()
			var self = this;
			var filter = function(requestArgs, arrayOfItems){
				var items = [];
				if(requestArgs.query){
					var ignoreCase = requestArgs.queryOptions ? requestArgs.queryOptions.ignoreCase : false; 

					//See if there are any string values that can be regexp parsed first to avoid multiple regexp gens on the
					//same value for each item examined.  Much more efficient.
					var regexpList = {};
					for(var key in requestArgs.query){
						var value = requestArgs.query[key];
						if(typeof value === "string"){
							regexpList[key] = dojo.data.util.filter.patternToRegExp(value, ignoreCase);
						}
					}

					for(var i = 0; i < arrayOfItems.length; ++i){
						var match = true;
						var candidateItem = arrayOfItems[i];
						if(candidateItem === null){
							match = false;
						}else{
							for(var key in requestArgs.query) {
								var value = requestArgs.query[key];
								if (!self._containsValue(candidateItem, key, value, regexpList[key])){
									match = false;
								}
							}
						}
						if(match){
							items.push(candidateItem);
						}
					}
					findCallback(items, requestArgs);
				}else{
					// We want a copy to pass back in case the parent wishes to sort the array. 
					// We shouldn't allow resort of the internal list, so that multiple callers 
					// can get lists and sort without affecting each other.  We also need to
					// filter out any null values that have been left as a result of deleteItem()
					// calls in ItemFileWriteStore.
					for(var i = 0; i < arrayOfItems.length; ++i){
						var item = arrayOfItems[i];
						if(item !== null){
							items.push(item);
						}
					}
					findCallback(items, requestArgs);
				}
			};

			if(this._loadFinished){
				filter(keywordArgs, this._getItemsArray(keywordArgs.queryOptions));
			}else{

				if(this._jsonFileUrl){
					//If fetches come in before the loading has finished, but while
					//a load is in progress, we have to defer the fetching to be 
					//invoked in the callback.
					if(this._loadInProgress){
						this._queuedFetches.push({args: keywordArgs, filter: filter});
					}else{
						this._loadInProgress = true;
						var getArgs = {
								url: self._jsonFileUrl, 
								handleAs: "json-comment-optional"
							};
						var getHandler = dojo.xhrGet(getArgs);
						getHandler.addCallback(function(data){
							try{
								self._getItemsFromLoadedData(data);
								self._loadFinished = true;
								self._loadInProgress = false;
								
								filter(keywordArgs, self._getItemsArray(keywordArgs.queryOptions));
								self._handleQueuedFetches();
							}catch(e){
								self._loadFinished = true;
								self._loadInProgress = false;
								errorCallback(e, keywordArgs);
							}
						});
						getHandler.addErrback(function(error){
							self._loadInProgress = false;
							errorCallback(error, keywordArgs);
						});
					}
				}else if(this._jsonData){
					try{
						this._loadFinished = true;
						this._getItemsFromLoadedData(this._jsonData);
						this._jsonData = null;
						filter(keywordArgs, this._getItemsArray(keywordArgs.queryOptions));
					}catch(e){
						errorCallback(e, keywordArgs);
					}
				}else{
					errorCallback(new Error("dojo.data.ItemFileReadStore: No JSON source data was provided as either URL or a nested Javascript object."), keywordArgs);
				}
			}
		},

		_handleQueuedFetches: function(){
			//	summary: 
			//		Internal function to execute delayed request in the store.
			//Execute any deferred fetches now.
			if (this._queuedFetches.length > 0) {
				for(var i = 0; i < this._queuedFetches.length; i++){
					var fData = this._queuedFetches[i];
					var delayedQuery = fData.args;
					var delayedFilter = fData.filter;
					if(delayedFilter){
						delayedFilter(delayedQuery, this._getItemsArray(delayedQuery.queryOptions)); 
					}else{
						this.fetchItemByIdentity(delayedQuery);
					}
				}
				this._queuedFetches = [];
			}
		},

		_getItemsArray: function(/*object?*/queryOptions){
			//	summary: 
			//		Internal function to determine which list of items to search over.
			//	queryOptions: The query options parameter, if any.
			if(queryOptions && queryOptions.deep) {
				return this._arrayOfAllItems; 
			}
			return this._arrayOfTopLevelItems;
		},

		close: function(/*dojo.data.api.Request || keywordArgs || null */ request){
			 //	summary: 
			 //		See dojo.data.api.Read.close()
		},

		_getItemsFromLoadedData: function(/* Object */ dataObject){
			//	summary:
			//		Function to parse the loaded data into item format and build the internal items array.
			//	description:
			//		Function to parse the loaded data into item format and build the internal items array.
			//
			//	dataObject:
			//		The JS data object containing the raw data to convery into item format.
			//
			// 	returns: array
			//		Array of items in store item format.
			
			// First, we define a couple little utility functions...
			
			function valueIsAnItem(/* anything */ aValue){
				// summary:
				//		Given any sort of value that could be in the raw json data,
				//		return true if we should interpret the value as being an
				//		item itself, rather than a literal value or a reference.
				// example:
				// 	|	false == valueIsAnItem("Kermit");
				// 	|	false == valueIsAnItem(42);
				// 	|	false == valueIsAnItem(new Date());
				// 	|	false == valueIsAnItem({_type:'Date', _value:'May 14, 1802'});
				// 	|	false == valueIsAnItem({_reference:'Kermit'});
				// 	|	true == valueIsAnItem({name:'Kermit', color:'green'});
				// 	|	true == valueIsAnItem({iggy:'pop'});
				// 	|	true == valueIsAnItem({foo:42});
				var isItem = (
					(aValue != null) &&
					(typeof aValue == "object") &&
					(!dojo.isArray(aValue)) &&
					(!dojo.isFunction(aValue)) &&
					(aValue.constructor == Object) &&
					(typeof aValue._reference == "undefined") && 
					(typeof aValue._type == "undefined") && 
					(typeof aValue._value == "undefined")
				);
				return isItem;
			}
			
			var self = this;
			function addItemAndSubItemsToArrayOfAllItems(/* Item */ anItem){
				self._arrayOfAllItems.push(anItem);
				for(var attribute in anItem){
					var valueForAttribute = anItem[attribute];
					if(valueForAttribute){
						if(dojo.isArray(valueForAttribute)){
							var valueArray = valueForAttribute;
							for(var k = 0; k < valueArray.length; ++k){
								var singleValue = valueArray[k];
								if(valueIsAnItem(singleValue)){
									addItemAndSubItemsToArrayOfAllItems(singleValue);
								}
							}
						}else{
							if(valueIsAnItem(valueForAttribute)){
								addItemAndSubItemsToArrayOfAllItems(valueForAttribute);
							}
						}
					}
				}
			}

			this._labelAttr = dataObject.label;

			// We need to do some transformations to convert the data structure
			// that we read from the file into a format that will be convenient
			// to work with in memory.

			// Step 1: Walk through the object hierarchy and build a list of all items
			var i;
			var item;
			this._arrayOfAllItems = [];
			this._arrayOfTopLevelItems = dataObject.items;

			for(i = 0; i < this._arrayOfTopLevelItems.length; ++i){
				item = this._arrayOfTopLevelItems[i];
				addItemAndSubItemsToArrayOfAllItems(item);
				item[this._rootItemPropName]=true;
			}

			// Step 2: Walk through all the attribute values of all the items, 
			// and replace single values with arrays.  For example, we change this:
			//		{ name:'Miss Piggy', pets:'Foo-Foo'}
			// into this:
			//		{ name:['Miss Piggy'], pets:['Foo-Foo']}
			// 
			// We also store the attribute names so we can validate our store  
			// reference and item id special properties for the O(1) isItem
			var allAttributeNames = {};
			var key;

			for(i = 0; i < this._arrayOfAllItems.length; ++i){
				item = this._arrayOfAllItems[i];
				for(key in item){
					if (key !== this._rootItemPropName)
					{
						var value = item[key];
						if(value !== null){
							if(!dojo.isArray(value)){
								item[key] = [value];
							}
						}else{
							item[key] = [null];
						}
					}
					allAttributeNames[key]=key;
				}
			}

			// Step 3: Build unique property names to use for the _storeRefPropName and _itemNumPropName
			// This should go really fast, it will generally never even run the loop.
			while(allAttributeNames[this._storeRefPropName]){
				this._storeRefPropName += "_";
			}
			while(allAttributeNames[this._itemNumPropName]){
				this._itemNumPropName += "_";
			}

			// Step 4: Some data files specify an optional 'identifier', which is 
			// the name of an attribute that holds the identity of each item. 
			// If this data file specified an identifier attribute, then build a 
			// hash table of items keyed by the identity of the items.
			var arrayOfValues;

			var identifier = dataObject.identifier;
			if(identifier){
				this._itemsByIdentity = {};
				this._features['dojo.data.api.Identity'] = identifier;
				for(i = 0; i < this._arrayOfAllItems.length; ++i){
					item = this._arrayOfAllItems[i];
					arrayOfValues = item[identifier];
					var identity = arrayOfValues[0];
					if(!this._itemsByIdentity[identity]){
						this._itemsByIdentity[identity] = item;
					}else{
						if(this._jsonFileUrl){
							throw new Error("dojo.data.ItemFileReadStore:  The json data as specified by: [" + this._jsonFileUrl + "] is malformed.  Items within the list have identifier: [" + identifier + "].  Value collided: [" + identity + "]");
						}else if(this._jsonData){
							throw new Error("dojo.data.ItemFileReadStore:  The json data provided by the creation arguments is malformed.  Items within the list have identifier: [" + identifier + "].  Value collided: [" + identity + "]");
						}
					}
				}
			}else{
				this._features['dojo.data.api.Identity'] = Number;
			}

			// Step 5: Walk through all the items, and set each item's properties 
			// for _storeRefPropName and _itemNumPropName, so that store.isItem() will return true.
			for(i = 0; i < this._arrayOfAllItems.length; ++i){
				item = this._arrayOfAllItems[i];
				item[this._storeRefPropName] = this;
				item[this._itemNumPropName] = i;
			}

			// Step 6: We walk through all the attribute values of all the items,
			// looking for type/value literals and item-references.
			//
			// We replace item-references with pointers to items.  For example, we change:
			//		{ name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
			// into this:
			//		{ name:['Kermit'], friends:[miss_piggy] } 
			// (where miss_piggy is the object representing the 'Miss Piggy' item).
			//
			// We replace type/value pairs with typed-literals.  For example, we change:
			//		{ name:['Nelson Mandela'], born:[{_type:'Date', _value:'July 18, 1918'}] }
			// into this:
			//		{ name:['Kermit'], born:(new Date('July 18, 1918')) } 
			//
			// We also generate the associate map for all items for the O(1) isItem function.
			for(i = 0; i < this._arrayOfAllItems.length; ++i){
				item = this._arrayOfAllItems[i]; // example: { name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
				for(key in item){
					arrayOfValues = item[key]; // example: [{_reference:{name:'Miss Piggy'}}]
					for(var j = 0; j < arrayOfValues.length; ++j) {
						value = arrayOfValues[j]; // example: {_reference:{name:'Miss Piggy'}}
						if(value !== null && typeof value == "object"){
							if(value._type && value._value){
								var type = value._type; // examples: 'Date', 'Color', or 'ComplexNumber'
								var mappingObj = this._datatypeMap[type]; // examples: Date, dojo.Color, foo.math.ComplexNumber, {type: dojo.Color, deserialize(value){ return new dojo.Color(value)}}
								if(!mappingObj){ 
									throw new Error("dojo.data.ItemFileReadStore: in the typeMap constructor arg, no object class was specified for the datatype '" + type + "'");
								}else if(dojo.isFunction(mappingObj)){
									arrayOfValues[j] = new mappingObj(value._value);
								}else if(dojo.isFunction(mappingObj.deserialize)){
									arrayOfValues[j] = mappingObj.deserialize(value._value);
								}else{
									throw new Error("dojo.data.ItemFileReadStore: Value provided in typeMap was neither a constructor, nor a an object with a deserialize function");
								}
							}
							if(value._reference){
								var referenceDescription = value._reference; // example: {name:'Miss Piggy'}
								if(dojo.isString(referenceDescription)){
									// example: 'Miss Piggy'
									// from an item like: { name:['Kermit'], friends:[{_reference:'Miss Piggy'}]}
									arrayOfValues[j] = this._itemsByIdentity[referenceDescription];
								}else{
									// example: {name:'Miss Piggy'}
									// from an item like: { name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
									for(var k = 0; k < this._arrayOfAllItems.length; ++k){
										var candidateItem = this._arrayOfAllItems[k];
										var found = true;
										for(var refKey in referenceDescription){
											if(candidateItem[refKey] != referenceDescription[refKey]){ 
												found = false; 
											}
										}
										if(found){ 
											arrayOfValues[j] = candidateItem; 
										}
									}
								}
							}
						}
					}
				}
			}
		},

		getIdentity: function(/* item */ item){
			//	summary: 
			//		See dojo.data.api.Identity.getIdentity()
			var identifier = this._features['dojo.data.api.Identity'];
			if(identifier === Number){
				return item[this._itemNumPropName]; // Number
			}else{
				var arrayOfValues = item[identifier];
				if(arrayOfValues){
					return arrayOfValues[0]; // Object || String
				}
			}
			return null; // null
		},

		fetchItemByIdentity: function(/* Object */ keywordArgs){
			//	summary: 
			//		See dojo.data.api.Identity.fetchItemByIdentity()

			// Hasn't loaded yet, we have to trigger the load.
			if(!this._loadFinished){
				var self = this;
				if(this._jsonFileUrl){

					if(this._loadInProgress){
						this._queuedFetches.push({args: keywordArgs});
					}else{
						this._loadInProgress = true;
						var getArgs = {
								url: self._jsonFileUrl, 
								handleAs: "json-comment-optional"
						};
						var getHandler = dojo.xhrGet(getArgs);
						getHandler.addCallback(function(data){
							var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
							try{
								self._getItemsFromLoadedData(data);
								self._loadFinished = true;
								self._loadInProgress = false;
								var item = self._getItemByIdentity(keywordArgs.identity);
								if(keywordArgs.onItem){
									keywordArgs.onItem.call(scope, item);
								}
								self._handleQueuedFetches();
							}catch(error){
								self._loadInProgress = false;
								if(keywordArgs.onError){
									keywordArgs.onError.call(scope, error);
								}
							}
						});
						getHandler.addErrback(function(error){
							self._loadInProgress = false;
							if(keywordArgs.onError){
								var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
								keywordArgs.onError.call(scope, error);
							}
						});
					}

				}else if(this._jsonData){
					// Passed in data, no need to xhr.
					self._getItemsFromLoadedData(self._jsonData);
					self._jsonData = null;
					self._loadFinished = true;
					var item = self._getItemByIdentity(keywordArgs.identity);
					if(keywordArgs.onItem){
						var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
						keywordArgs.onItem.call(scope, item);
					}
				} 
			}else{
				// Already loaded.  We can just look it up and call back.
				var item = this._getItemByIdentity(keywordArgs.identity);
				if(keywordArgs.onItem){
					var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
					keywordArgs.onItem.call(scope, item);
				}
			}
		},

		_getItemByIdentity: function(/* Object */ identity){
			//	summary:
			//		Internal function to look an item up by its identity map.
			var item = null;
			if(this._itemsByIdentity){
				item = this._itemsByIdentity[identity];
			}else{
				item = this._arrayOfAllItems[identity];
			}
			if(item === undefined){
				item = null;
			}
			return item; // Object
		},

		getIdentityAttributes: function(/* item */ item){
			//	summary: 
			//		See dojo.data.api.Identity.getIdentifierAttributes()
			 
			var identifier = this._features['dojo.data.api.Identity'];
			if(identifier === Number){
				// If (identifier === Number) it means getIdentity() just returns
				// an integer item-number for each item.  The dojo.data.api.Identity
				// spec says we need to return null if the identity is not composed 
				// of attributes 
				return null; // null
			}else{
				return [identifier]; // Array
			}
		},
		
		_forceLoad: function(){
			//	summary: 
			//		Internal function to force a load of the store if it hasn't occurred yet.  This is required
			//		for specific functions to work properly.  
			var self = this;
			if(this._jsonFileUrl){
					var getArgs = {
						url: self._jsonFileUrl, 
						handleAs: "json-comment-optional",
						sync: true
					};
				var getHandler = dojo.xhrGet(getArgs);
				getHandler.addCallback(function(data){
					try{
						//Check to be sure there wasn't another load going on concurrently 
						//So we don't clobber data that comes in on it.  If there is a load going on
						//then do not save this data.  It will potentially clobber current data.
						//We mainly wanted to sync/wait here.
						//TODO:  Revisit the loading scheme of this store to improve multi-initial
						//request handling.
						if (self._loadInProgress !== true && !self._loadFinished) {
							self._getItemsFromLoadedData(data);
							self._loadFinished = true;
						}
					}catch(e){
						console.log(e);
						throw e;
					}
				});
				getHandler.addErrback(function(error){
					throw error;
				});
			}else if(this._jsonData){
				self._getItemsFromLoadedData(self._jsonData);
				self._jsonData = null;
				self._loadFinished = true;
			} 
		}
	});
	//Mix in the simple fetch implementation to this class.
	dojo.extend(dojo.data.ItemFileReadStore,dojo.data.util.simpleFetch);

	}

if(!dojo._hasResource["dojo.data.ItemFileWriteStore"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dojo.data.ItemFileWriteStore"] = true;
	dojo.provide("dojo.data.ItemFileWriteStore");

	dojo.declare("dojo.data.ItemFileWriteStore", dojo.data.ItemFileReadStore, {
		constructor: function(/* object */ keywordParameters){
			//	keywordParameters: {typeMap: object)
			//		The structure of the typeMap object is as follows:
			//		{
			//			type0: function || object,
			//			type1: function || object,
			//			...
			//			typeN: function || object
			//		}
			//		Where if it is a function, it is assumed to be an object constructor that takes the 
			//		value of _value as the initialization parameters.  It is serialized assuming object.toString()
			//		serialization.  If it is an object, then it is assumed
			//		to be an object of general form:
			//		{
			//			type: function, //constructor.
			//			deserialize:	function(value) //The function that parses the value and constructs the object defined by type appropriately.
			//			serialize:	function(object) //The function that converts the object back into the proper file format form.
			//		}

			// ItemFileWriteStore extends ItemFileReadStore to implement these additional dojo.data APIs
			this._features['dojo.data.api.Write'] = true;
			this._features['dojo.data.api.Notification'] = true;
			
			// For keeping track of changes so that we can implement isDirty and revert
			this._pending = {
				_newItems:{}, 
				_modifiedItems:{}, 
				_deletedItems:{}
			};

			if(!this._datatypeMap['Date'].serialize){
				this._datatypeMap['Date'].serialize = function(obj){
					return dojo.date.stamp.toISOString(obj, {zulu:true});
				}
			}
			
			// this._saveInProgress is set to true, briefly, from when save() is first called to when it completes
			this._saveInProgress = false;
		}, 
		
		_assert: function(/* boolean */ condition){
			if(!condition) {
				throw new Error("assertion failed in ItemFileWriteStore");
			}
		},

		_getIdentifierAttribute: function(){
			var identifierAttribute = this.getFeatures()['dojo.data.api.Identity'];
			// this._assert((identifierAttribute === Number) || (dojo.isString(identifierAttribute)));
			return identifierAttribute;
		},
		
		
	/* dojo.data.api.Write */

		newItem: function(/* Object? */ keywordArgs, /* Object? */ parentInfo){
			// summary: See dojo.data.api.Write.newItem()

			this._assert(!this._saveInProgress);

			if (!this._loadFinished){
				// We need to do this here so that we'll be able to find out what
				// identifierAttribute was specified in the data file.
				this._forceLoad();
			}

			if(typeof keywordArgs != "object" && typeof keywordArgs != "undefined"){
				throw new Error("newItem() was passed something other than an object");
			}
			var newIdentity = null;
			var identifierAttribute = this._getIdentifierAttribute();
			if(identifierAttribute === Number){
				newIdentity = this._arrayOfAllItems.length;
			}else{
				newIdentity = keywordArgs[identifierAttribute];
				if (typeof newIdentity === "undefined"){
					throw new Error("newItem() was not passed an identity for the new item");
				}
				if (dojo.isArray(newIdentity)){
					throw new Error("newItem() was not passed an single-valued identity");
				}
			}
			
			// make sure this identity is not already in use by another item, if identifiers were 
			// defined in the file.  Otherwise it would be the item count, 
			// which should always be unique in this case.
			if(this._itemsByIdentity){
				this._assert(typeof this._itemsByIdentity[newIdentity] === "undefined");
			}
			this._assert(typeof this._pending._newItems[newIdentity] === "undefined");
			this._assert(typeof this._pending._deletedItems[newIdentity] === "undefined");
			
			var newItem = {};
			newItem[this._storeRefPropName] = this;		
			newItem[this._itemNumPropName] = this._arrayOfAllItems.length;
			if(this._itemsByIdentity){
				this._itemsByIdentity[newIdentity] = newItem;
			}
			this._arrayOfAllItems.push(newItem);

			//We need to construct some data for the onNew call too...
			var pInfo = null;
			
			// Now we need to check to see where we want to assign this thingm if any.
			if(parentInfo && parentInfo.parent && parentInfo.attribute){
				pInfo = {
					item: parentInfo.parent,
					attribute: parentInfo.attribute,
					oldValue: undefined
				};

				//See if it is multi-valued or not and handle appropriately
				//Generally, all attributes are multi-valued for this store
				//So, we only need to append if there are already values present.
				var values = this.getValues(parentInfo.parent, parentInfo.attribute);
				if(values && values.length > 0){
					var tempValues = values.slice(0, values.length);
					if(values.length === 1){
						pInfo.oldValue = values[0];
					}else{
						pInfo.oldValue = values.slice(0, values.length);
					}
					tempValues.push(newItem);
					this._setValueOrValues(parentInfo.parent, parentInfo.attribute, tempValues, false);
					pInfo.newValue = this.getValues(parentInfo.parent, parentInfo.attribute);
				}else{
					this._setValueOrValues(parentInfo.parent, parentInfo.attribute, newItem, false);
					pInfo.newValue = newItem;
				}
			}else{
				//Toplevel item, add to both top list as well as all list.
				newItem[this._rootItemPropName]=true;
				this._arrayOfTopLevelItems.push(newItem);
			}
			
			this._pending._newItems[newIdentity] = newItem;
			
			//Clone over the properties to the new item
			for(var key in keywordArgs){
				if(key === this._storeRefPropName || key === this._itemNumPropName){
					// Bummer, the user is trying to do something like
					// newItem({_S:"foo"}).  Unfortunately, our superclass,
					// ItemFileReadStore, is already using _S in each of our items
					// to hold private info.  To avoid a naming collision, we 
					// need to move all our private info to some other property 
					// of all the items/objects.  So, we need to iterate over all
					// the items and do something like: 
					//    item.__S = item._S;
					//    item._S = undefined;
					// But first we have to make sure the new "__S" variable is 
					// not in use, which means we have to iterate over all the 
					// items checking for that.
					throw new Error("encountered bug in ItemFileWriteStore.newItem");
				}
				var value = keywordArgs[key];
				if(!dojo.isArray(value)){
					value = [value];
				}
				newItem[key] = value;
			}
			this.onNew(newItem, pInfo); // dojo.data.api.Notification call
			return newItem; // item
		},
		
		_removeArrayElement: function(/* Array */ array, /* anything */ element){
			var index = dojo.indexOf(array, element);
			if (index != -1){
				array.splice(index, 1);
				return true;
			}
			return false;
		},
		
		deleteItem: function(/* item */ item){
			// summary: See dojo.data.api.Write.deleteItem()
			this._assert(!this._saveInProgress);
			this._assertIsItem(item);

			// remove this item from the _arrayOfAllItems, but leave a null value in place
			// of the item, so as not to change the length of the array, so that in newItem() 
			// we can still safely do: newIdentity = this._arrayOfAllItems.length;
			var indexInArrayOfAllItems = item[this._itemNumPropName];
			this._arrayOfAllItems[indexInArrayOfAllItems] = null;
			
			var identity = this.getIdentity(item);
			item[this._storeRefPropName] = null;
			if(this._itemsByIdentity){
				delete this._itemsByIdentity[identity];
			}
			this._pending._deletedItems[identity] = item;
			
			//Remove from the toplevel items, if necessary...
			if(item[this._rootItemPropName]){
				this._removeArrayElement(this._arrayOfTopLevelItems, item);
			}
			this.onDelete(item); // dojo.data.api.Notification call
			return true;
		},

		setValue: function(/* item */ item, /* attribute-name-string */ attribute, /* almost anything */ value){
			// summary: See dojo.data.api.Write.set()
			return this._setValueOrValues(item, attribute, value, true); // boolean
		},
		
		setValues: function(/* item */ item, /* attribute-name-string */ attribute, /* array */ values){
			// summary: See dojo.data.api.Write.setValues()
			return this._setValueOrValues(item, attribute, values, true); // boolean
		},
		
		unsetAttribute: function(/* item */ item, /* attribute-name-string */ attribute){
			// summary: See dojo.data.api.Write.unsetAttribute()
			return this._setValueOrValues(item, attribute, [], true);
		},
		
		_setValueOrValues: function(/* item */ item, /* attribute-name-string */ attribute, /* anything */ newValueOrValues, /*boolean?*/ callOnSet){
			this._assert(!this._saveInProgress);
			
			// Check for valid arguments
			this._assertIsItem(item);
			this._assert(dojo.isString(attribute));
			this._assert(typeof newValueOrValues !== "undefined");

			// Make sure the user isn't trying to change the item's identity
			var identifierAttribute = this._getIdentifierAttribute();
			if(attribute == identifierAttribute){
				throw new Error("ItemFileWriteStore does not have support for changing the value of an item's identifier.");
			}

			// To implement the Notification API, we need to make a note of what
			// the old attribute value was, so that we can pass that info when
			// we call the onSet method.
			var oldValueOrValues = this._getValueOrValues(item, attribute);

			var identity = this.getIdentity(item);
			if(!this._pending._modifiedItems[identity]){
				// Before we actually change the item, we make a copy of it to 
				// record the original state, so that we'll be able to revert if 
				// the revert method gets called.  If the item has already been
				// modified then there's no need to do this now, since we already
				// have a record of the original state.
				var copyOfItemState = {};
				for(var key in item){
					if((key === this._storeRefPropName) || (key === this._itemNumPropName) || (key === this._rootItemPropName)){
						copyOfItemState[key] = item[key];
					}else{
						var valueArray = item[key];
						var copyOfValueArray = [];
						for(var i = 0; i < valueArray.length; ++i){
							copyOfValueArray.push(valueArray[i]);
						}
						copyOfItemState[key] = copyOfValueArray;
					}
				}
				// Now mark the item as dirty, and save the copy of the original state
				this._pending._modifiedItems[identity] = copyOfItemState;
			}
			
			// Okay, now we can actually change this attribute on the item
			var success = false;
			if(dojo.isArray(newValueOrValues) && newValueOrValues.length === 0){
				// If we were passed an empty array as the value, that counts
				// as "unsetting" the attribute, so we need to remove this 
				// attribute from the item.
				success = delete item[attribute];
				newValueOrValues = undefined; // used in the onSet Notification call below
			}else{
				var newValueArray = [];
				if(dojo.isArray(newValueOrValues)){
					var newValues = newValueOrValues;
					// Unforunately, it's not safe to just do this:
					//    newValueArray = newValues;
					// Instead, we need to take each value in the values array and copy 
					// it into the new array, so that our internal data structure won't  
					// get corrupted if the user mucks with the values array *after*
					// calling setValues().
					for(var j = 0; j < newValues.length; ++j){
						newValueArray.push(newValues[j]);
					}
				}else{
					var newValue = newValueOrValues;
					newValueArray.push(newValue);
				}
				item[attribute] = newValueArray;
				success = true;
			}

			// Now we make the dojo.data.api.Notification call
			if(callOnSet){
				this.onSet(item, attribute, oldValueOrValues, newValueOrValues); 
			}
			return success; // boolean
		},

		_getValueOrValues: function(/* item */ item, /* attribute-name-string */ attribute){
			var valueOrValues = undefined;
			if(this.hasAttribute(item, attribute)){
				var valueArray = this.getValues(item, attribute);
				if(valueArray.length == 1){
					valueOrValues = valueArray[0];
				}else{
					valueOrValues = valueArray;
				}
			}
			return valueOrValues;
		},
		
		_flatten: function(/* anything */ value){
			if(this.isItem(value)){
				var item = value;
				// Given an item, return an serializable object that provides a 
				// reference to the item.
				// For example, given kermit:
				//    var kermit = store.newItem({id:2, name:"Kermit"});
				// we want to return
				//    {_reference:2}
				var identity = this.getIdentity(item);
				var referenceObject = {_reference: identity};
				return referenceObject;
			}else{
				if(typeof value === "object"){
					for(type in this._datatypeMap){
						var typeMap = this._datatypeMap[type];
						if (dojo.isObject(typeMap) && !dojo.isFunction(typeMap)){
							if(value instanceof typeMap.type){
								if(!typeMap.serialize){
									throw new Error("ItemFileWriteStore:  No serializer defined for type mapping: [" + type + "]");
								}
								return {_type: type, _value: typeMap.serialize(value)};
							}
						} else if(value instanceof typeMap){
							//SImple mapping, therefore, return as a toString serialization.
							return {_type: type, _value: value.toString()};
						}
					}
				}
				return value;
			}
		},
		
		_getNewFileContentString: function(){
			// summary: 
			//		Generate a string that can be saved to a file.
			//		The result should look similar to:
			//		http://trac.dojotoolkit.org/browser/dojo/trunk/tests/data/countries.json
			var serializableStructure = {};
			
			var identifierAttribute = this._getIdentifierAttribute();
			if(identifierAttribute !== Number){
				serializableStructure.identifier = identifierAttribute;
			}
			if(this._labelAttr){
				serializableStructure.label = this._labelAttr;
			}
			serializableStructure.items = [];
			for(var i = 0; i < this._arrayOfAllItems.length; ++i){
				var item = this._arrayOfAllItems[i];
				if(item !== null){
					serializableItem = {};
					for(var key in item){
						if(key !== this._storeRefPropName && key !== this._itemNumPropName){
							var attribute = key;
							var valueArray = this.getValues(item, attribute);
							if(valueArray.length == 1){
								serializableItem[attribute] = this._flatten(valueArray[0]);
							}else{
								var serializableArray = [];
								for(var j = 0; j < valueArray.length; ++j){
									serializableArray.push(this._flatten(valueArray[j]));
									serializableItem[attribute] = serializableArray;
								}
							}
						}
					}
					serializableStructure.items.push(serializableItem);
				}
			}
			var prettyPrint = true;
			return dojo.toJson(serializableStructure, prettyPrint);
		},
		
		save: function(/* object */ keywordArgs){
			// summary: See dojo.data.api.Write.save()
			this._assert(!this._saveInProgress);
			
			// this._saveInProgress is set to true, briefly, from when save is first called to when it completes
			this._saveInProgress = true;
			
			var self = this;
			var saveCompleteCallback = function(){
				self._pending = {
					_newItems:{}, 
					_modifiedItems:{},
					_deletedItems:{}
				};
				self._saveInProgress = false; // must come after this._pending is cleared, but before any callbacks
				if(keywordArgs && keywordArgs.onComplete){
					var scope = keywordArgs.scope || dojo.global;
					keywordArgs.onComplete.call(scope);
				}
			};
			var saveFailedCallback = function(){
				self._saveInProgress = false;
				if(keywordArgs && keywordArgs.onError){
					var scope = keywordArgs.scope || dojo.global;
					keywordArgs.onError.call(scope);
				}
			};
			
			if(this._saveEverything){
				var newFileContentString = this._getNewFileContentString();
				this._saveEverything(saveCompleteCallback, saveFailedCallback, newFileContentString);
			}
			if(this._saveCustom){
				this._saveCustom(saveCompleteCallback, saveFailedCallback);
			}
			if(!this._saveEverything && !this._saveCustom){
				// Looks like there is no user-defined save-handler function.
				// That's fine, it just means the datastore is acting as a "mock-write"
				// store -- changes get saved in memory but don't get saved to disk.
				saveCompleteCallback();
			}
		},
		
		revert: function(){
			// summary: See dojo.data.api.Write.revert()
			this._assert(!this._saveInProgress);

			var identity;
			for(identity in this._pending._newItems){
				var newItem = this._pending._newItems[identity];
				newItem[this._storeRefPropName] = null;
				// null out the new item, but don't change the array index so
				// so we can keep using _arrayOfAllItems.length.
				this._arrayOfAllItems[newItem[this._itemNumPropName]] = null;
				if(newItem[this._rootItemPropName]){
					this._removeArrayElement(this._arrayOfTopLevelItems, newItem);
				}
				if(this._itemsByIdentity){
					delete this._itemsByIdentity[identity];
				}
			}
			for(identity in this._pending._modifiedItems){
				// find the original item and the modified item that replaced it
				var originalItem = this._pending._modifiedItems[identity];
				var modifiedItem = null;
				if(this._itemsByIdentity){
					modifiedItem = this._itemsByIdentity[identity];
				}else{
					modifiedItem = this._arrayOfAllItems[identity];
				}
				
				// make the original item into a full-fledged item again
				originalItem[this._storeRefPropName] = this;
				modifiedItem[this._storeRefPropName] = null;

				// replace the modified item with the original one
				var arrayIndex = modifiedItem[this._itemNumPropName];
				this._arrayOfAllItems[arrayIndex] = originalItem;
				
				if(modifiedItem[this._rootItemPropName]){
					arrayIndex = modifiedItem[this._itemNumPropName];
					this._arrayOfTopLevelItems[arrayIndex] = originalItem;
				}
				if(this._itemsByIdentity){
					this._itemsByIdentity[identity] = originalItem;
				}			
			}
			for(identity in this._pending._deletedItems){
				var deletedItem = this._pending._deletedItems[identity];
				deletedItem[this._storeRefPropName] = this;
				var index = deletedItem[this._itemNumPropName];
				this._arrayOfAllItems[index] = deletedItem;
				if (this._itemsByIdentity) {
					this._itemsByIdentity[identity] = deletedItem;
				}
				if(deletedItem[this._rootItemPropName]){
					this._arrayOfTopLevelItems.push(deletedItem);
				}
			}
			this._pending = {
				_newItems:{}, 
				_modifiedItems:{}, 
				_deletedItems:{}
			};
			return true; // boolean
		},
		
		isDirty: function(/* item? */ item){
			// summary: See dojo.data.api.Write.isDirty()
			if(item){
				// return true if the item is dirty
				var identity = this.getIdentity(item);
				return new Boolean(this._pending._newItems[identity] || 
					this._pending._modifiedItems[identity] ||
					this._pending._deletedItems[identity]); // boolean
			}else{
				// return true if the store is dirty -- which means return true
				// if there are any new items, dirty items, or modified items
				var key;
				for(key in this._pending._newItems){
					return true;
				}
				for(key in this._pending._modifiedItems){
					return true;
				}
				for(key in this._pending._deletedItems){
					return true;
				}
				return false; // boolean
			}
		},

	/* dojo.data.api.Notification */

		onSet: function(/* item */ item, 
						/*attribute-name-string*/ attribute, 
						/*object | array*/ oldValue,
						/*object | array*/ newValue){
			// summary: See dojo.data.api.Notification.onSet()
			
			// No need to do anything. This method is here just so that the 
			// client code can connect observers to it. 
		},

		onNew: function(/* item */ newItem, /*object?*/ parentInfo){
			// summary: See dojo.data.api.Notification.onNew()
			
			// No need to do anything. This method is here just so that the 
			// client code can connect observers to it. 
		},

		onDelete: function(/* item */ deletedItem){
			// summary: See dojo.data.api.Notification.onDelete()
			
			// No need to do anything. This method is here just so that the 
			// client code can connect observers to it. 
		}

	});

	}


/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/dojo/dojo-aipo.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/dijit/dijit-aipo.js
 */
/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(!dojo._hasResource["dijit._base.focus"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.focus"] = true;
dojo.provide("dijit._base.focus");

// summary:
//		These functions are used to query or set the focus and selection.
//
//		Also, they trace when widgets become actived/deactivated,
//		so that the widget can fire _onFocus/_onBlur events.
//		"Active" here means something similar to "focused", but
//		"focus" isn't quite the right word because we keep track of
//		a whole stack of "active" widgets.  Example:  Combobutton --> Menu -->
//		MenuItem.   The onBlur event for Combobutton doesn't fire due to focusing
//		on the Menu or a MenuItem, since they are considered part of the
//		Combobutton widget.  It only happens when focus is shifted
//		somewhere completely different.

dojo.mixin(dijit,
{
	// _curFocus: DomNode
	//		Currently focused item on screen
	_curFocus: null,

	// _prevFocus: DomNode
	//		Previously focused item on screen
	_prevFocus: null,

	isCollapsed: function(){
		// summary: tests whether the current selection is empty
		var _window = dojo.global;
		var _document = dojo.doc;
		if(_document.selection){ // IE
			return !_document.selection.createRange().text; // Boolean
		}else if(_window.getSelection){
			var selection = _window.getSelection();
			if(dojo.isString(selection)){ // Safari
				return !selection; // Boolean
			}else{ // Mozilla/W3
				return selection.isCollapsed || !selection.toString(); // Boolean
			}
		}
	},

	getBookmark: function(){
		// summary: Retrieves a bookmark that can be used with moveToBookmark to return to the same range
		var bookmark, selection = dojo.doc.selection;
		if(selection){ // IE
			var range = selection.createRange();
			if(selection.type.toUpperCase()=='CONTROL'){
				bookmark = range.length ? dojo._toArray(range) : null;
			}else{
				bookmark = range.getBookmark();
			}
		}else{
			if(dojo.global.getSelection){
				selection = dojo.global.getSelection();
				if(selection){
					var range = selection.getRangeAt(0);
					bookmark = range.cloneRange();
				}
			}else{
				console.debug("No idea how to store the current selection for this browser!");
			}
		}
		return bookmark; // Array
	},

	moveToBookmark: function(/*Object*/bookmark){
		// summary: Moves current selection to a bookmark
		// bookmark: this should be a returned object from dojo.html.selection.getBookmark()
		var _document = dojo.doc;
		if(_document.selection){ // IE
			var range;
			if(dojo.isArray(bookmark)){
				range = _document.body.createControlRange();
				dojo.forEach(bookmark, range.addElement);
			}else{
				range = _document.selection.createRange();
				range.moveToBookmark(bookmark);
			}
			range.select();
		}else{ //Moz/W3C
			var selection = dojo.global.getSelection && dojo.global.getSelection();
			if(selection && selection.removeAllRanges){
				selection.removeAllRanges();
				selection.addRange(bookmark);
			}else{
				console.debug("No idea how to restore selection for this browser!");
			}
		}
	},

	getFocus: function(/*Widget*/menu, /*Window*/ openedForWindow){
		// summary:
		//	Returns the current focus and selection.
		//	Called when a popup appears (either a top level menu or a dialog),
		//	or when a toolbar/menubar receives focus
		//
		// menu:
		//	the menu that's being opened
		//
		// openedForWindow:
		//	iframe in which menu was opened
		//
		// returns:
		//	a handle to restore focus/selection

		return {
			// Node to return focus to
			node: menu && dojo.isDescendant(dijit._curFocus, menu.domNode) ? dijit._prevFocus : dijit._curFocus,

			// Previously selected text
			bookmark:
				!dojo.withGlobal(openedForWindow||dojo.global, dijit.isCollapsed) ?
				dojo.withGlobal(openedForWindow||dojo.global, dijit.getBookmark) :
				null,

			openedForWindow: openedForWindow
		}; // Object
	},

	focus: function(/*Object || DomNode */ handle){
		// summary:
		//		Sets the focused node and the selection according to argument.
		//		To set focus to an iframe's content, pass in the iframe itself.
		// handle:
		//		object returned by get(), or a DomNode

		if(!handle){ return; }

		var node = "node" in handle ? handle.node : handle,		// because handle is either DomNode or a composite object
			bookmark = handle.bookmark,
			openedForWindow = handle.openedForWindow;

		// Set the focus
		// Note that for iframe's we need to use the <iframe> to follow the parentNode chain,
		// but we need to set focus to iframe.contentWindow
		if(node){
			var focusNode = (node.tagName.toLowerCase()=="iframe") ? node.contentWindow : node;
			if(focusNode && focusNode.focus){
				try{
					// Gecko throws sometimes if setting focus is impossible,
					// node not displayed or something like that
					focusNode.focus();
				}catch(e){/*quiet*/}
			}			
			dijit._onFocusNode(node);
		}

		// set the selection
		// do not need to restore if current selection is not empty
		// (use keyboard to select a menu item)
		if(bookmark && dojo.withGlobal(openedForWindow||dojo.global, dijit.isCollapsed)){
			if(openedForWindow){
				openedForWindow.focus();
			}
			try{
				dojo.withGlobal(openedForWindow||dojo.global, moveToBookmark, null, [bookmark]);
			}catch(e){
				/*squelch IE internal error, see http://trac.dojotoolkit.org/ticket/1984 */
			}
		}
	},

	// List of currently active widgets (focused widget and it's ancestors)
	_activeStack: [],

	registerWin: function(/*Window?*/targetWindow){
		// summary:
		//		Registers listeners on the specified window (either the main
		//		window or an iframe) to detect when the user has clicked somewhere.
		//		Anyone that creates an iframe should call this function.

		if(!targetWindow){
			targetWindow = window;
		}

		dojo.connect(targetWindow.document, "onmousedown", null, function(evt){
			dijit._justMouseDowned = true;
			setTimeout(function(){ dijit._justMouseDowned = false; }, 0);
			dijit._onTouchNode(evt.target||evt.srcElement);
		});
		//dojo.connect(targetWindow, "onscroll", ???);

		// Listen for blur and focus events on targetWindow's body
		var body = targetWindow.document.body || targetWindow.document.getElementsByTagName("body")[0];
		if(body){
			if(dojo.isIE){
				body.attachEvent('onactivate', function(evt){
					if(evt.srcElement.tagName.toLowerCase() != "body"){
						dijit._onFocusNode(evt.srcElement);
					}
				});
				body.attachEvent('ondeactivate', function(evt){ dijit._onBlurNode(evt.srcElement); });
			}else{
				body.addEventListener('focus', function(evt){ dijit._onFocusNode(evt.target); }, true);
				body.addEventListener('blur', function(evt){ dijit._onBlurNode(evt.target); }, true);
			}
		}
		body = null;	// prevent memory leak (apparent circular reference via closure)
	},

	_onBlurNode: function(/*DomNode*/ node){
		// summary:
		// 		Called when focus leaves a node.
		//		Usually ignored, _unless_ it *isn't* follwed by touching another node,
		//		which indicates that we tabbed off the last field on the page,
		//		in which case every widget is marked inactive
		dijit._prevFocus = dijit._curFocus;
		dijit._curFocus = null;

		var w = dijit.getEnclosingWidget(node);
		if (w && w._setStateClass){
			w._focused = false;
			w._setStateClass();
		}
		if(dijit._justMouseDowned){
			// the mouse down caused a new widget to be marked as active; this blur event
			// is coming late, so ignore it.
			return;
		}

		// if the blur event isn't followed by a focus event then mark all widgets as inactive.
		if(dijit._clearActiveWidgetsTimer){
			clearTimeout(dijit._clearActiveWidgetsTimer);
		}
		dijit._clearActiveWidgetsTimer = setTimeout(function(){
			delete dijit._clearActiveWidgetsTimer; dijit._setStack([]); }, 100);
	},

	_onTouchNode: function(/*DomNode*/ node){
		// summary
		//		Callback when node is focused or mouse-downed

		// ignore the recent blurNode event
		if(dijit._clearActiveWidgetsTimer){
			clearTimeout(dijit._clearActiveWidgetsTimer);
			delete dijit._clearActiveWidgetsTimer;
		}

		// compute stack of active widgets (ex: ComboButton --> Menu --> MenuItem)
		var newStack=[];
		try{
			while(node){
				if(node.dijitPopupParent){
					node=dijit.byId(node.dijitPopupParent).domNode;
				}else if(node.tagName && node.tagName.toLowerCase()=="body"){
					// is this the root of the document or just the root of an iframe?
					if(node===dojo.body()){
						// node is the root of the main document
						break;
					}
					// otherwise, find the iframe this node refers to (can't access it via parentNode,
					// need to do this trick instead) and continue tracing up the document
					node=dojo.query("iframe").filter(function(iframe){ return iframe.contentDocument.body===node; })[0];
				}else{
					var id = node.getAttribute && node.getAttribute("widgetId");
					if(id){
						newStack.unshift(id);
					}
					node=node.parentNode;
				}
			}
		}catch(e){ /* squelch */ }

		dijit._setStack(newStack);
	},

	_onFocusNode: function(/*DomNode*/ node){
		// summary
		//		Callback when node is focused
		if(node && node.tagName && node.tagName.toLowerCase() == "body"){
			return;
		}
		dijit._onTouchNode(node);
		if(node==dijit._curFocus){ return; }
		dijit._prevFocus = dijit._curFocus;
		dijit._curFocus = node;
		dojo.publish("focusNode", [node]);

		// handle focus/blur styling
		var w = dijit.getEnclosingWidget(node);
		if (w && w._setStateClass){
			w._focused = true;
			w._setStateClass();
		}
	},

	_setStack: function(newStack){
		// summary
		//	The stack of active widgets has changed.  Send out appropriate events and record new stack

		var oldStack = dijit._activeStack;		
		dijit._activeStack = newStack;

		// compare old stack to new stack to see how many elements they have in common
		for(var nCommon=0; nCommon<Math.min(oldStack.length, newStack.length); nCommon++){
			if(oldStack[nCommon] != newStack[nCommon]){
				break;
			}
		}

		// for all elements that have gone out of focus, send blur event
		for(var i=oldStack.length-1; i>=nCommon; i--){
			var widget = dijit.byId(oldStack[i]);
			if(widget){
				dojo.publish("widgetBlur", [widget]);
				if(widget._onBlur){
					widget._onBlur();
				}
			}
		}

		// for all element that have come into focus, send focus event
		for(var i=nCommon; i<newStack.length; i++){
			var widget = dijit.byId(newStack[i]);
			if(widget){
				dojo.publish("widgetFocus", [widget]);
				if(widget._onFocus){
					widget._onFocus();
				}
			}
		}
	}
});

// register top window and all the iframes it contains
dojo.addOnLoad(dijit.registerWin);

}

if(!dojo._hasResource["dijit._base.manager"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.manager"] = true;
dojo.provide("dijit._base.manager");

dojo.declare("dijit.WidgetSet", null, {
	constructor: function(){
		// summary:
		//	A set of widgets indexed by id
		this._hash={};
	},

	add: function(/*Widget*/ widget){
		if(this._hash[widget.id]){
			throw new Error("Tried to register widget with id==" + widget.id + " but that id is already registered");
		}
		this._hash[widget.id]=widget;
	},

	remove: function(/*String*/ id){
		delete this._hash[id];
	},

	forEach: function(/*Function*/ func){
		for(var id in this._hash){
			func(this._hash[id]);
		}
	},

	filter: function(/*Function*/ filter){
		var res = new dijit.WidgetSet();
		this.forEach(function(widget){
			if(filter(widget)){ res.add(widget); }
		});
		return res;		// dijit.WidgetSet
	},

	byId: function(/*String*/ id){
		return this._hash[id];
	},

	byClass: function(/*String*/ cls){
		return this.filter(function(widget){ return widget.declaredClass==cls; });	// dijit.WidgetSet
	}
	});

// registry: list of all widgets on page
dijit.registry = new dijit.WidgetSet();

dijit._widgetTypeCtr = {};

dijit.getUniqueId = function(/*String*/widgetType){
	// summary
	//	Generates a unique id for a given widgetType

	var id;
	do{
		id = widgetType + "_" +
			(dijit._widgetTypeCtr[widgetType] !== undefined ?
				++dijit._widgetTypeCtr[widgetType] : dijit._widgetTypeCtr[widgetType] = 0);
	}while(dijit.byId(id));
	return id; // String
};


if(dojo.isIE){
	// Only run this for IE because we think it's only necessary in that case,
	// and because it causes problems on FF.  See bug #3531 for details.
	dojo.addOnUnload(function(){
		dijit.registry.forEach(function(widget){ widget.destroy(); });
	});
}

dijit.byId = function(/*String|Widget*/id){
	// summary:
	//		Returns a widget by its id, or if passed a widget, no-op (like dojo.byId())
	return (dojo.isString(id)) ? dijit.registry.byId(id) : id; // Widget
};

dijit.byNode = function(/* DOMNode */ node){
	// summary:
	//		Returns the widget as referenced by node
	return dijit.registry.byId(node.getAttribute("widgetId")); // Widget
};

dijit.getEnclosingWidget = function(/* DOMNode */ node){
	// summary:
	//		Returns the widget whose dom tree contains node or null if
	//		the node is not contained within the dom tree of any widget
	while(node){
		if(node.getAttribute && node.getAttribute("widgetId")){
			return dijit.registry.byId(node.getAttribute("widgetId"));
		}
		node = node.parentNode;
	}
	return null;
};

}

if(!dojo._hasResource["dijit._base.place"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.place"] = true;
dojo.provide("dijit._base.place");

// ported from dojo.html.util

dijit.getViewport = function(){
	//	summary
	//	Returns the dimensions and scroll position of the viewable area of a browser window

	var _window = dojo.global;
	var _document = dojo.doc;

	// get viewport size
	var w = 0, h = 0;
	if(dojo.isMozilla){
		// mozilla
		// _window.innerHeight includes the height taken by the scroll bar
		// clientHeight is ideal but has DTD issues:
		// #4539: FF reverses the roles of body.clientHeight/Width and documentElement.clientHeight/Width based on the DTD!
		// check DTD to see whether body or documentElement returns the viewport dimensions using this algorithm:
		var minw, minh, maxw, maxh;
		if(_document.body.clientWidth>_document.documentElement.clientWidth){
			minw = _document.documentElement.clientWidth;
			maxw = _document.body.clientWidth;
		}else{
			maxw = _document.documentElement.clientWidth;
			minw = _document.body.clientWidth;
		}
		if(_document.body.clientHeight>_document.documentElement.clientHeight){
			minh = _document.documentElement.clientHeight;
			maxh = _document.body.clientHeight;
		}else{
			maxh = _document.documentElement.clientHeight;
			minh = _document.body.clientHeight;
		}
		w = (maxw > _window.innerWidth) ? minw : maxw;
		h = (maxh > _window.innerHeight) ? minh : maxh;
	}else if(!dojo.isOpera && _window.innerWidth){
		//in opera9, dojo.body().clientWidth should be used, instead
		//of window.innerWidth/document.documentElement.clientWidth
		//so we have to check whether it is opera
		w = _window.innerWidth;
		h = _window.innerHeight;
	}else if(dojo.isIE && _document.documentElement && _document.documentElement.clientHeight){
		w = _document.documentElement.clientWidth;
		h = _document.documentElement.clientHeight;
	}else if(dojo.body().clientWidth){
		// IE5, Opera
		w = dojo.body().clientWidth;
		h = dojo.body().clientHeight;
	}

	// get scroll position
	var scroll = dojo._docScroll();

	return { w: w, h: h, l: scroll.x, t: scroll.y };	//	object
};

dijit.placeOnScreen = function(
	/* DomNode */	node,
	/* Object */		pos,
	/* Object */		corners,
	/* boolean? */		tryOnly){
	//	summary:
	//		Keeps 'node' in the visible area of the screen while trying to
	//		place closest to pos.x, pos.y. The input coordinates are
	//		expected to be the desired document position.
	//
	//		Set which corner(s) you want to bind to, such as
	//		
	//			placeOnScreen(node, {x: 10, y: 20}, ["TR", "BL"])
	//		
	//		The desired x/y will be treated as the topleft(TL)/topright(TR) or
	//		BottomLeft(BL)/BottomRight(BR) corner of the node. Each corner is tested
	//		and if a perfect match is found, it will be used. Otherwise, it goes through
	//		all of the specified corners, and choose the most appropriate one.
	//		
	//		NOTE: node is assumed to be absolutely or relatively positioned.

	var choices = dojo.map(corners, function(corner){ return { corner: corner, pos: pos }; });

	return dijit._place(node, choices);
}

dijit._place = function(/*DomNode*/ node, /* Array */ choices, /* Function */ layoutNode){
	// summary:
	//		Given a list of spots to put node, put it at the first spot where it fits,
	//		of if it doesn't fit anywhere then the place with the least overflow
	// choices: Array
	//		Array of elements like: {corner: 'TL', pos: {x: 10, y: 20} }
	//		Above example says to put the top-left corner of the node at (10,20)
	//	layoutNode: Function(node, orient)
	//		for things like tooltip, they are displayed differently (and have different dimensions)
	//		based on their orientation relative to the parent.   This adjusts the popup based on orientation.

	// get {x: 10, y: 10, w: 100, h:100} type obj representing position of
	// viewport over document
	var view = dijit.getViewport();

	// This won't work if the node is inside a <div style="position: relative">,
	// so reattach it to document.body.   (Otherwise, the positioning will be wrong
	// and also it might get cutoff)
	if(!node.parentNode || String(node.parentNode.tagName).toLowerCase() != "body"){
		dojo.body().appendChild(node);
	}

	var best=null;
	for(var i=0; i<choices.length; i++){
		var corner = choices[i].corner;
		var pos = choices[i].pos;

		// configure node to be displayed in given position relative to button
		// (need to do this in order to get an accurate size for the node, because
		// a tooltips size changes based on position, due to triangle)
		if(layoutNode){
			layoutNode(corner);
		}

		// get node's size
		var oldDisplay = node.style.display;
		var oldVis = node.style.visibility;
		node.style.visibility = "hidden";
		node.style.display = "";
		var mb = dojo.marginBox(node);
		node.style.display = oldDisplay;
		node.style.visibility = oldVis;

		// coordinates and size of node with specified corner placed at pos,
		// and clipped by viewport
		var startX = (corner.charAt(1)=='L' ? pos.x : Math.max(view.l, pos.x - mb.w)),
			startY = (corner.charAt(0)=='T' ? pos.y : Math.max(view.t, pos.y -  mb.h)),
			endX = (corner.charAt(1)=='L' ? Math.min(view.l+view.w, startX+mb.w) : pos.x),
			endY = (corner.charAt(0)=='T' ? Math.min(view.t+view.h, startY+mb.h) : pos.y),
			width = endX-startX,
			height = endY-startY,
			overflow = (mb.w-width) + (mb.h-height);

		if(best==null || overflow<best.overflow){
			best = {
				corner: corner,
				aroundCorner: choices[i].aroundCorner,
				x: startX,
				y: startY,
				w: width,
				h: height,
				overflow: overflow
			};
		}
		if(overflow==0){
			break;
		}
	}

	node.style.left = best.x + "px";
	node.style.top = best.y + "px";
	return best;
}

dijit.placeOnScreenAroundElement = function(
	/* DomNode */		node,
	/* DomNode */		aroundNode,
	/* Object */		aroundCorners,
	/* Function */		layoutNode){

	//	summary
	//	Like placeOnScreen, except it accepts aroundNode instead of x,y
	//	and attempts to place node around it.  Uses margin box dimensions.
	//
	//	aroundCorners
	//		specify Which corner of aroundNode should be
	//		used to place the node => which corner(s) of node to use (see the
	//		corners parameter in dijit.placeOnScreen)
	//		e.g. {'TL': 'BL', 'BL': 'TL'}
	//
	//	layoutNode: Function(node, orient)
	//		for things like tooltip, they are displayed differently (and have different dimensions)
	//		based on their orientation relative to the parent.   This adjusts the popup based on orientation.


	// get coordinates of aroundNode
	aroundNode = dojo.byId(aroundNode);
	var oldDisplay = aroundNode.style.display;
	aroundNode.style.display="";
	// #3172: use the slightly tighter border box instead of marginBox
	var aroundNodeW = aroundNode.offsetWidth; //mb.w;
	var aroundNodeH = aroundNode.offsetHeight; //mb.h;
	var aroundNodePos = dojo.coords(aroundNode, true);
	aroundNode.style.display=oldDisplay;

	// Generate list of possible positions for node
	var choices = [];
	for(var nodeCorner in aroundCorners){
		choices.push( {
			aroundCorner: nodeCorner,
			corner: aroundCorners[nodeCorner],
			pos: {
				x: aroundNodePos.x + (nodeCorner.charAt(1)=='L' ? 0 : aroundNodeW),
				y: aroundNodePos.y + (nodeCorner.charAt(0)=='T' ? 0 : aroundNodeH)
			}
		});
	}

	return dijit._place(node, choices, layoutNode);
}

}

if(!dojo._hasResource["dijit._base.window"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.window"] = true;
dojo.provide("dijit._base.window");

dijit.getDocumentWindow = function(doc){
	//	summary
	// 	Get window object associated with document doc

	// With Safari, there is not way to retrieve the window from the document, so we must fix it.
	if(dojo.isSafari && !doc._parentWindow){
		/*
			This is a Safari specific function that fix the reference to the parent
			window from the document object.
		*/
		var fix=function(win){
			win.document._parentWindow=win;
			for(var i=0; i<win.frames.length; i++){
				fix(win.frames[i]);
			}
		}
		fix(window.top);
	}

	//In some IE versions (at least 6.0), document.parentWindow does not return a
	//reference to the real window object (maybe a copy), so we must fix it as well
	//We use IE specific execScript to attach the real window reference to
	//document._parentWindow for later use
	if(dojo.isIE && window !== document.parentWindow && !doc._parentWindow){
		/*
		In IE 6, only the variable "window" can be used to connect events (others
		may be only copies).
		*/
		doc.parentWindow.execScript("document._parentWindow = window;", "Javascript");
		//to prevent memory leak, unset it after use
		//another possibility is to add an onUnload handler which seems overkill to me (liucougar)
		var win = doc._parentWindow;
		doc._parentWindow = null;
		return win;	//	Window
	}

	return doc._parentWindow || doc.parentWindow || doc.defaultView;	//	Window
}

}

if(!dojo._hasResource["dijit._base.popup"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.popup"] = true;
dojo.provide("dijit._base.popup");





dijit.popup = new function(){
	// summary:
	//		This class is used to show/hide widgets as popups.
	//

	var stack = [],
		beginZIndex=1000,
		idGen = 1;

	this.open = function(/*Object*/ args){
		// summary:
		//		Popup the widget at the specified position
		//
		// args: Object
		//		popup: Widget
		//			widget to display,
		//		parent: Widget
		//			the button etc. that is displaying this popup
		//		around: DomNode
		//			DOM node (typically a button); place popup relative to this node
		//		orient: Object
		//			structure specifying possible positions of popup relative to "around" node
		//		onCancel: Function
		//			callback when user has canceled the popup by
		//				1. hitting ESC or
		//				2. by using the popup widget's proprietary cancel mechanism (like a cancel button in a dialog);
		//				   ie: whenever popupWidget.onCancel() is called, args.onCancel is called
		//		onClose: Function
		//			callback whenever this popup is closed
		//		onExecute: Function
		//			callback when user "executed" on the popup/sub-popup by selecting a menu choice, etc. (top menu only)
		//
		// examples:
		//		1. opening at the mouse position
		//			dijit.popup.open({popup: menuWidget, x: evt.pageX, y: evt.pageY});
		//		2. opening the widget as a dropdown
		//			dijit.popup.open({parent: this, popup: menuWidget, around: this.domNode, onClose: function(){...}  });
		//
		//	Note that whatever widget called dijit.popup.open() should also listen to it's own _onBlur callback
		//	(fired from _base/focus.js) to know that focus has moved somewhere else and thus the popup should be closed.

		var widget = args.popup,
			orient = args.orient || {'BL':'TL', 'TL':'BL'},
			around = args.around,
			id = (args.around && args.around.id) ? (args.around.id+"_dropdown") : ("popup_"+idGen++);

		// make wrapper div to hold widget and possibly hold iframe behind it.
		// we can't attach the iframe as a child of the widget.domNode because
		// widget.domNode might be a <table>, <ul>, etc.
		var wrapper = dojo.doc.createElement("div");
		wrapper.id = id;
		wrapper.className="dijitPopup";
		wrapper.style.zIndex = beginZIndex + stack.length;
		wrapper.style.visibility = "hidden";
		if(args.parent){
			wrapper.dijitPopupParent=args.parent.id;
		}
		dojo.body().appendChild(wrapper);

		widget.domNode.style.display="";
		wrapper.appendChild(widget.domNode);

		var iframe = new dijit.BackgroundIframe(wrapper);

		// position the wrapper node
		var best = around ?
			dijit.placeOnScreenAroundElement(wrapper, around, orient, widget.orient ? dojo.hitch(widget, "orient") : null) :
			dijit.placeOnScreen(wrapper, args, orient == 'R' ? ['TR','BR','TL','BL'] : ['TL','BL','TR','BR']);

		wrapper.style.visibility = "visible";
		// TODO: use effects to fade in wrapper

		var handlers = [];

		// Compute the closest ancestor popup that's *not* a child of another popup.
		// Ex: For a TooltipDialog with a button that spawns a tree of menus, find the popup of the button.
		function getTopPopup(){
			for(var pi=stack.length-1; pi > 0 && stack[pi].parent === stack[pi-1].widget; pi--);
			return stack[pi];
		}

		// provide default escape and tab key handling
		// (this will work for any widget, not just menu)
		handlers.push(dojo.connect(wrapper, "onkeypress", this, function(evt){
			if(evt.keyCode == dojo.keys.ESCAPE && args.onCancel){
				args.onCancel();
			}else if(evt.keyCode == dojo.keys.TAB){
				dojo.stopEvent(evt);
				var topPopup = getTopPopup();
				if(topPopup && topPopup.onCancel){
					topPopup.onCancel();
				}
			}
		}));

		// watch for cancel/execute events on the popup and notify the caller
		// (for a menu, "execute" means clicking an item)
		if(widget.onCancel){
			handlers.push(dojo.connect(widget, "onCancel", null, args.onCancel));
		}

		handlers.push(dojo.connect(widget, widget.onExecute ? "onExecute" : "onChange", null, function(){
			var topPopup = getTopPopup();
			if(topPopup && topPopup.onExecute){
				topPopup.onExecute();
			}
		}));

		stack.push({
			wrapper: wrapper,
			iframe: iframe,
			widget: widget,
			parent: args.parent,
			onExecute: args.onExecute,
			onCancel: args.onCancel,
 			onClose: args.onClose,
			handlers: handlers
		});

		if(widget.onOpen){
			widget.onOpen(best);
		}

		return best;
	};

	this.close = function(/*Widget*/ popup){
		// summary:
		//		Close specified popup and any popups that it parented
		while(dojo.some(stack, function(elem){return elem.widget == popup;})){
			var top = stack.pop(),
				wrapper = top.wrapper,
				iframe = top.iframe,
				widget = top.widget,
				onClose = top.onClose;
	
			if(widget.onClose){
				widget.onClose();
			}
			dojo.forEach(top.handlers, dojo.disconnect);
	
			// #2685: check if the widget still has a domNode so ContentPane can change its URL without getting an error
			if(!widget||!widget.domNode){ return; }
			dojo.style(widget.domNode, "display", "none");
			dojo.body().appendChild(widget.domNode);
			iframe.destroy();
			dojo._destroyElement(wrapper);
	
			if(onClose){
				onClose();
			}
		}
	};
}();

dijit._frames = new function(){
	// summary: cache of iframes
	var queue = [];

	this.pop = function(){
		var iframe;
		if(queue.length){
			iframe = queue.pop();
			iframe.style.display="";
		}else{
			if(dojo.isIE){
				var html="<iframe src='javascript:\"\"'"
					+ " style='position: absolute; left: 0px; top: 0px;"
					+ "z-index: -1; filter:Alpha(Opacity=\"0\");'>";
				iframe = dojo.doc.createElement(html);
			}else{
			 	var iframe = dojo.doc.createElement("iframe");
				iframe.src = 'javascript:""';
				iframe.className = "dijitBackgroundIframe";
			}
			iframe.tabIndex = -1; // Magic to prevent iframe from getting focus on tab keypress - as style didnt work.
			dojo.body().appendChild(iframe);
		}
		return iframe;
	};

	this.push = function(iframe){
		iframe.style.display="";
		if(dojo.isIE){
			iframe.style.removeExpression("width");
			iframe.style.removeExpression("height");
		}
		queue.push(iframe);
	}
}();

// fill the queue
if(dojo.isIE && dojo.isIE < 7){
	dojo.addOnLoad(function(){
		var f = dijit._frames;
		dojo.forEach([f.pop()], f.push);
	});
}


dijit.BackgroundIframe = function(/* DomNode */node){
	//	summary:
	//		For IE z-index schenanigans. id attribute is required.
	//
	//	description:
	//		new dijit.BackgroundIframe(node)
	//			Makes a background iframe as a child of node, that fills
	//			area (and position) of node

	if(!node.id){ throw new Error("no id"); }
	if((dojo.isIE && dojo.isIE < 7) || (dojo.isFF && dojo.isFF < 3 && dojo.hasClass(dojo.body(), "dijit_a11y"))){
		var iframe = dijit._frames.pop();
		node.appendChild(iframe);
		if(dojo.isIE){
			iframe.style.setExpression("width", "document.getElementById('" + node.id + "').offsetWidth");
			iframe.style.setExpression("height", "document.getElementById('" + node.id + "').offsetHeight");
		}
		this.iframe = iframe;
	}
};

dojo.extend(dijit.BackgroundIframe, {
	destroy: function(){
		//	summary: destroy the iframe
		if(this.iframe){
			dijit._frames.push(this.iframe);
			delete this.iframe;
		}
	}
});

}

if(!dojo._hasResource["dijit._base.scroll"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.scroll"] = true;
dojo.provide("dijit._base.scroll");

dijit.scrollIntoView = function(/* DomNode */node){
	//	summary
	//	Scroll the passed node into view, if it is not.

	// don't rely on that node.scrollIntoView works just because the function is there
	// it doesnt work in Konqueror or Opera even though the function is there and probably
	// not safari either
	// dont like browser sniffs implementations but sometimes you have to use it
	if(dojo.isIE){
		//only call scrollIntoView if there is a scrollbar for this menu,
		//otherwise, scrollIntoView will scroll the window scrollbar
		if(dojo.marginBox(node.parentNode).h <= node.parentNode.scrollHeight){ //PORT was getBorderBox
			node.scrollIntoView(false);
		}
	}else if(dojo.isMozilla){
		node.scrollIntoView(false);
	}else{
		var parent = node.parentNode;
		var parentBottom = parent.scrollTop + dojo.marginBox(parent).h; //PORT was getBorderBox
		var nodeBottom = node.offsetTop + dojo.marginBox(node).h;
		if(parentBottom < nodeBottom){
			parent.scrollTop += (nodeBottom - parentBottom);
		}else if(parent.scrollTop > node.offsetTop){
			parent.scrollTop -= (parent.scrollTop - node.offsetTop);
		}
	}
};

}

if(!dojo._hasResource["dijit._base.sniff"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.sniff"] = true;
dojo.provide("dijit._base.sniff");

// ported from dojo.html.applyBrowserClass (style.js)

//	summary:
//		Applies pre-set class names based on browser & version to the
//		top-level HTML node.  Simply doing a require on this module will
//		establish this CSS.  Modified version of Morris' CSS hack.
(function(){
	var d = dojo;
	var ie = d.isIE;
	var opera = d.isOpera;
	var maj = Math.floor;
	var classes = {
		dj_ie: ie,
//		dj_ie55: ie == 5.5,
		dj_ie6: maj(ie) == 6,
		dj_ie7: maj(ie) == 7,
		dj_iequirks: ie && d.isQuirks,
// NOTE: Opera not supported by dijit
		dj_opera: opera,
		dj_opera8: maj(opera) == 8,
		dj_opera9: maj(opera) == 9,
		dj_khtml: d.isKhtml,
		dj_safari: d.isSafari,
		dj_gecko: d.isMozilla
	}; // no dojo unsupported browsers

	for(var p in classes){
		if(classes[p]){
			var html = dojo.doc.documentElement; //TODO browser-specific DOM magic needed?
			if(html.className){
				html.className += " " + p;
			}else{
				html.className = p;
			}
		}
	}
})();

}

if(!dojo._hasResource["dijit._base.bidi"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.bidi"] = true;
dojo.provide("dijit._base.bidi");

// summary: applies a class to the top of the document for right-to-left stylesheet rules

dojo.addOnLoad(function(){
	if(!dojo._isBodyLtr()){
		dojo.addClass(dojo.body(), "dijitRtl");
	}
});

}

if(!dojo._hasResource["dijit._base.typematic"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.typematic"] = true;
dojo.provide("dijit._base.typematic");

dijit.typematic = {
	// summary:
	//	These functions are used to repetitively call a user specified callback
	//	method when a specific key or mouse click over a specific DOM node is
	//	held down for a specific amount of time.
	//	Only 1 such event is allowed to occur on the browser page at 1 time.

	_fireEventAndReload: function(){
		this._timer = null;
		this._callback(++this._count, this._node, this._evt);
		this._currentTimeout = (this._currentTimeout < 0) ? this._initialDelay : ((this._subsequentDelay > 1) ? this._subsequentDelay : Math.round(this._currentTimeout * this._subsequentDelay));
		this._timer = setTimeout(dojo.hitch(this, "_fireEventAndReload"), this._currentTimeout);
	},

	trigger: function(/*Event*/ evt, /* Object */ _this, /*DOMNode*/ node, /* Function */ callback, /* Object */ obj, /* Number */ subsequentDelay, /* Number */ initialDelay){
		// summary:
		//      Start a timed, repeating callback sequence.
		//      If already started, the function call is ignored.
		//      This method is not normally called by the user but can be
		//      when the normal listener code is insufficient.
		//	Parameters:
		//	evt: key or mouse event object to pass to the user callback
		//	_this: pointer to the user's widget space.
		//	node: the DOM node object to pass the the callback function
		//	callback: function to call until the sequence is stopped called with 3 parameters:
		//		count: integer representing number of repeated calls (0..n) with -1 indicating the iteration has stopped
		//		node: the DOM node object passed in
		//		evt: key or mouse event object
		//	obj: user space object used to uniquely identify each typematic sequence
		//	subsequentDelay: if > 1, the number of milliseconds until the 3->n events occur
		//		or else the fractional time multiplier for the next event's delay, default=0.9
		//	initialDelay: the number of milliseconds until the 2nd event occurs, default=500ms
		if(obj != this._obj){
			this.stop();
			this._initialDelay = initialDelay || 500;
			this._subsequentDelay = subsequentDelay || 0.90;
			this._obj = obj;
			this._evt = evt;
			this._node = node;
			this._currentTimeout = -1;
			this._count = -1;
			this._callback = dojo.hitch(_this, callback);
			this._fireEventAndReload();
		}
	},

	stop: function(){
		// summary:
		//	  Stop an ongoing timed, repeating callback sequence.
		if(this._timer){
			clearTimeout(this._timer);
			this._timer = null;
		}
		if(this._obj){
			this._callback(-1, this._node, this._evt);
			this._obj = null;
		}
	},

	addKeyListener: function(/*DOMNode*/ node, /*Object*/ keyObject, /*Object*/ _this, /*Function*/ callback, /*Number*/ subsequentDelay, /*Number*/ initialDelay){
		// summary: Start listening for a specific typematic key.
		//	keyObject: an object defining the key to listen for.
		//		key: (mandatory) the keyCode (number) or character (string) to listen for.
		//		ctrlKey: desired ctrl key state to initiate the calback sequence:
		//			pressed (true)
		//			released (false)
		//			either (unspecified)
		//		altKey: same as ctrlKey but for the alt key
		//		shiftKey: same as ctrlKey but for the shift key
		//	See the trigger method for other parameters.
		//	Returns an array of dojo.connect handles
		return [
			dojo.connect(node, "onkeypress", this, function(evt){
				if(evt.keyCode == keyObject.keyCode && (!keyObject.charCode || keyObject.charCode == evt.charCode) &&
				(keyObject.ctrlKey === undefined || keyObject.ctrlKey == evt.ctrlKey) &&
				(keyObject.altKey === undefined || keyObject.altKey == evt.ctrlKey) &&
				(keyObject.shiftKey === undefined || keyObject.shiftKey == evt.ctrlKey)){
					dojo.stopEvent(evt);
					dijit.typematic.trigger(keyObject, _this, node, callback, keyObject, subsequentDelay, initialDelay);
				}else if(dijit.typematic._obj == keyObject){
					dijit.typematic.stop();
				}
			}),
			dojo.connect(node, "onkeyup", this, function(evt){
				if(dijit.typematic._obj == keyObject){
					dijit.typematic.stop();
				}
			})
		];
	},

	addMouseListener: function(/*DOMNode*/ node, /*Object*/ _this, /*Function*/ callback, /*Number*/ subsequentDelay, /*Number*/ initialDelay){
		// summary: Start listening for a typematic mouse click.
		//	See the trigger method for other parameters.
		//	Returns an array of dojo.connect handles
		var dc = dojo.connect;
		return [
			dc(node, "mousedown", this, function(evt){
				dojo.stopEvent(evt);
				dijit.typematic.trigger(evt, _this, node, callback, node, subsequentDelay, initialDelay);
			}),
			dc(node, "mouseup", this, function(evt){
				dojo.stopEvent(evt);
				dijit.typematic.stop();
			}),
			dc(node, "mouseout", this, function(evt){
				dojo.stopEvent(evt);
				dijit.typematic.stop();
			}),
			dc(node, "mousemove", this, function(evt){
				dojo.stopEvent(evt);
			}),
			dc(node, "dblclick", this, function(evt){
				dojo.stopEvent(evt);
				if(dojo.isIE){
					dijit.typematic.trigger(evt, _this, node, callback, node, subsequentDelay, initialDelay);
					setTimeout(dijit.typematic.stop, 50);
				}
			})
		];
	},

	addListener: function(/*Node*/ mouseNode, /*Node*/ keyNode, /*Object*/ keyObject, /*Object*/ _this, /*Function*/ callback, /*Number*/ subsequentDelay, /*Number*/ initialDelay){
		// summary: Start listening for a specific typematic key and mouseclick.
		//	This is a thin wrapper to addKeyListener and addMouseListener.
		//	mouseNode: the DOM node object to listen on for mouse events.
		//	keyNode: the DOM node object to listen on for key events.
		//	See the addMouseListener and addKeyListener methods for other parameters.
		//	Returns an array of dojo.connect handles
		return this.addKeyListener(keyNode, keyObject, _this, callback, subsequentDelay, initialDelay).concat(
			this.addMouseListener(mouseNode, _this, callback, subsequentDelay, initialDelay));
	}
};

}

if(!dojo._hasResource["dijit._base.wai"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base.wai"] = true;
dojo.provide("dijit._base.wai");

dijit.wai = {
	onload: function(){
		// summary:
		//		Function that detects if we are in high-contrast mode or not,
		//		and sets up a timer to periodically confirm the value.
		//		figure out the background-image style property
		//		and apply that to the image.src property.
		// description:
		//		This must be a named function and not an anonymous
		//		function, so that the widget parsing code can make sure it
		//		registers its onload function after this function.
		//		DO NOT USE "this" within this function.

		// create div for testing if high contrast mode is on or images are turned off
		var div = document.createElement("div");
		div.id = "a11yTestNode";
		div.style.cssText = 'border: 1px solid;'
			+ 'border-color:red green;'
			+ 'position: absolute;'
			+ 'height: 5px;'
			+ 'top: -999px;'
			+ 'background-image: url("' + dojo.moduleUrl("dijit", "form/templates/blank.gif") + '");';
		dojo.body().appendChild(div);

		// test it
		function check(){
			var cs = dojo.getComputedStyle(div);
			if(cs){
				var bkImg = cs.backgroundImage;
				var needsA11y = (cs.borderTopColor==cs.borderRightColor) || (bkImg != null && (bkImg == "none" || bkImg == "url(invalid-url:)" ));
				dojo[needsA11y ? "addClass" : "removeClass"](dojo.body(), "dijit_a11y");
			}
		}
		check();
		if(dojo.isIE){
			setInterval(check, 4000);
		}
	}
};

// Test if computer is in high contrast mode.
// Make sure the a11y test runs first, before widgets are instantiated.
if(dojo.isIE || dojo.isMoz){	// NOTE: checking in Safari messes things up
	dojo._loaders.unshift(dijit.wai.onload);
}

dojo.mixin(dijit,
{
	hasWaiRole: function(/*Element*/ elem){
		// Summary: Return true if elem has a role attribute and false if not.
		if(elem.hasAttribute){
			return elem.hasAttribute("role");
		}else{
			return elem.getAttribute("role") ? true : false;
		}
	},

	getWaiRole: function(/*Element*/ elem){
		// Summary: Return the role of elem or an empty string if
		//		elem does not have a role.
		var value = elem.getAttribute("role");
		if(value){
			var prefixEnd = value.indexOf(":");
			return prefixEnd == -1 ? value : value.substring(prefixEnd+1);
		}else{
			return "";
		}
	},

	setWaiRole: function(/*Element*/ elem, /*String*/ role){
		// Summary: Set the role on elem. On Firefox 2 and below, "wairole:" is
		//		prepended to the provided role value.
		if(dojo.isFF && dojo.isFF < 3){
			elem.setAttribute("role", "wairole:"+role);
		}else{
			elem.setAttribute("role", role);
		}
	},

	removeWaiRole: function(/*Element*/ elem){
		// Summary: Removes the role attribute from elem.
		elem.removeAttribute("role");
	},

	hasWaiState: function(/*Element*/ elem, /*String*/ state){
		// Summary: Return true if elem has a value for the given state and
		//		false if it does not.
		//		On Firefox 2 and below, we check for an attribute in namespace
		//		"http://www.w3.org/2005/07/aaa" with a name of the given state.
		//		On all other browsers, we check for an attribute called
		//		"aria-"+state.
		if(dojo.isFF && dojo.isFF < 3){
			return elem.hasAttributeNS("http://www.w3.org/2005/07/aaa", state);
		}else{
			if(elem.hasAttribute){
				return elem.hasAttribute("aria-"+state);
			}else{
				return elem.getAttribute("aria-"+state) ? true : false;
			}
		}
	},

	getWaiState: function(/*Element*/ elem, /*String*/ state){
		// Summary: Return the value of the requested state on elem
		//		or an empty string if elem has no value for state.
		//		On Firefox 2 and below, we check for an attribute in namespace
		//		"http://www.w3.org/2005/07/aaa" with a name of the given state.
		//		On all other browsers, we check for an attribute called
		//		"aria-"+state.
		if(dojo.isFF && dojo.isFF < 3){
			return elem.getAttributeNS("http://www.w3.org/2005/07/aaa", state);
		}else{
			var value =  elem.getAttribute("aria-"+state);
			return value ? value : "";
		}
	},

	setWaiState: function(/*Element*/ elem, /*String*/ state, /*String*/ value){
		// Summary: Set state on elem to value.
		//		On Firefox 2 and below, we set an attribute in namespace
		//		"http://www.w3.org/2005/07/aaa" with a name of the given state.
		//		On all other browsers, we set an attribute called
		//		"aria-"+state.
		if(dojo.isFF && dojo.isFF < 3){
			elem.setAttributeNS("http://www.w3.org/2005/07/aaa",
				"aaa:"+state, value);
		}else{
			elem.setAttribute("aria-"+state, value);
		}
	},

	removeWaiState: function(/*Element*/ elem, /*String*/ state){
		// Summary: Removes the given state from elem.
		//		On Firefox 2 and below, we remove the attribute in namespace
		//		"http://www.w3.org/2005/07/aaa" with a name of the given state.
		//		On all other browsers, we remove the attribute called
		//		"aria-"+state.
		if(dojo.isFF && dojo.isFF < 3){
			elem.removeAttributeNS("http://www.w3.org/2005/07/aaa", state);
		}else{
			elem.removeAttribute("aria-"+state);
		}
	}
});

}

if(!dojo._hasResource["dijit._base"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._base"] = true;
dojo.provide("dijit._base");












}

if(!dojo._hasResource["dojo.date.stamp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.date.stamp"] = true;
dojo.provide("dojo.date.stamp");

// Methods to convert dates to or from a wire (string) format using well-known conventions

dojo.date.stamp.fromISOString = function(/*String*/formattedString, /*Number?*/defaultTime){
	//	summary:
	//		Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
	//
	//	description:
	//		Accepts a string formatted according to a profile of ISO8601 as defined by
	//		RFC3339 (http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
	//		Can also process dates as specified by http://www.w3.org/TR/NOTE-datetime
	//		The following combinations are valid:
	// 			* dates only
	//				yyyy
	//				yyyy-MM
	//				yyyy-MM-dd
	// 			* times only, with an optional time zone appended
	//				THH:mm
	//				THH:mm:ss
	//				THH:mm:ss.SSS
	// 			* and "datetimes" which could be any combination of the above
	//		timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
	//		Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
	//		input may return null.  Arguments which are out of bounds will be handled
	// 		by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
	//
  	//	formattedString:
	//		A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00
	//
	//	defaultTime:
	//		Used for defaults for fields omitted in the formattedString.
	//		Uses 1970-01-01T00:00:00.0Z by default.

	if(!dojo.date.stamp._isoRegExp){
		dojo.date.stamp._isoRegExp =
//TODO: could be more restrictive and check for 00-59, etc.
			/^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;
	}

	var match = dojo.date.stamp._isoRegExp.exec(formattedString);
	var result = null;

	if(match){
		match.shift();
		match[1] && match[1]--; // Javascript Date months are 0-based
		match[6] && (match[6] *= 1000); // Javascript Date expects fractional seconds as milliseconds

		if(defaultTime){
			// mix in defaultTime.  Relatively expensive, so use || operators for the fast path of defaultTime === 0
			defaultTime = new Date(defaultTime);
			dojo.map(["FullYear", "Month", "Date", "Hours", "Minutes", "Seconds", "Milliseconds"], function(prop){
				return defaultTime["get" + prop]();
			}).forEach(function(value, index){
				if(match[index] === undefined){
					match[index] = value;
				}
			});
		}
		result = new Date(match[0]||1970, match[1]||0, match[2]||0, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

		var offset = 0;
		var zoneSign = match[7] && match[7].charAt(0);
		if(zoneSign != 'Z'){
			offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
			if(zoneSign != '-'){ offset *= -1; }
		}
		if(zoneSign){
			offset -= result.getTimezoneOffset();
		}
		if(offset){
			result.setTime(result.getTime() + offset * 60000);
		}
	}

	return result; // Date or null
}

dojo.date.stamp.toISOString = function(/*Date*/dateObject, /*Object?*/options){
	//	summary:
	//		Format a Date object as a string according a subset of the ISO-8601 standard
	//
	//	description:
	//		When options.selector is omitted, output follows RFC3339 (http://www.ietf.org/rfc/rfc3339.txt)
	//		The local time zone is included as an offset from GMT, except when selector=='time' (time without a date)
	//		Does not check bounds.
	//
	//	dateObject:
	//		A Date object
	//
	//	object {selector: string, zulu: boolean, milliseconds: boolean}
	//		selector- "date" or "time" for partial formatting of the Date object.
	//			Both date and time will be formatted by default.
	//		zulu- if true, UTC/GMT is used for a timezone
	//		milliseconds- if true, output milliseconds

	var _ = function(n){ return (n < 10) ? "0" + n : n; }
	options = options || {};
	var formattedDate = [];
	var getter = options.zulu ? "getUTC" : "get";
	var date = "";
	if(options.selector != "time"){
		date = [dateObject[getter+"FullYear"](), _(dateObject[getter+"Month"]()+1), _(dateObject[getter+"Date"]())].join('-');
	}
	formattedDate.push(date);
	if(options.selector != "date"){
		var time = [_(dateObject[getter+"Hours"]()), _(dateObject[getter+"Minutes"]()), _(dateObject[getter+"Seconds"]())].join(':');
		var millis = dateObject[getter+"Milliseconds"]();
		if(options.milliseconds){
			time += "."+ (millis < 100 ? "0" : "") + _(millis);
		}
		if(options.zulu){
			time += "Z";
		}else if(options.selector != "time"){
			var timezoneOffset = dateObject.getTimezoneOffset();
			var absOffset = Math.abs(timezoneOffset);
			time += (timezoneOffset > 0 ? "-" : "+") + 
				_(Math.floor(absOffset/60)) + ":" + _(absOffset%60);
		}
		formattedDate.push(time);
	}
	return formattedDate.join('T'); // String
}

}

if(!dojo._hasResource["dojo.parser"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.parser"] = true;
dojo.provide("dojo.parser");


dojo.parser = new function(){

	var d = dojo;

	function val2type(/*Object*/ value){
		// summary:
		//		Returns name of type of given value.

		if(d.isString(value)){ return "string"; }
		if(typeof value == "number"){ return "number"; }
		if(typeof value == "boolean"){ return "boolean"; }
		if(d.isFunction(value)){ return "function"; }
		if(d.isArray(value)){ return "array"; } // typeof [] == "object"
		if(value instanceof Date) { return "date"; } // assume timestamp
		if(value instanceof d._Url){ return "url"; }
		return "object";
	}

	function str2obj(/*String*/ value, /*String*/ type){
		// summary:
		//		Convert given string value to given type
		switch(type){
			case "string":
				return value;
			case "number":
				return value.length ? Number(value) : NaN;
			case "boolean":
				// for checked/disabled value might be "" or "checked".  interpret as true.
				return typeof value == "boolean" ? value : !(value.toLowerCase()=="false");
			case "function":
				if(d.isFunction(value)){
					// IE gives us a function, even when we say something like onClick="foo"
					// (in which case it gives us an invalid function "function(){ foo }"). 
					//  Therefore, convert to string
					value=value.toString();
					value=d.trim(value.substring(value.indexOf('{')+1, value.length-1));
				}
				try{
					if(value.search(/[^\w\.]+/i) != -1){
						// TODO: "this" here won't work
						value = d.parser._nameAnonFunc(new Function(value), this);
					}
					return d.getObject(value, false);
				}catch(e){ return new Function(); }
			case "array":
				return value.split(/\s*,\s*/);
			case "date":
				switch(value){
					case "": return new Date("");	// the NaN of dates
					case "now": return new Date();	// current date
					default: return d.date.stamp.fromISOString(value);
				}
			case "url":
				return d.baseUrl + value;
			default:
				return d.fromJson(value);
		}
	}

	var instanceClasses = {
		// map from fully qualified name (like "dijit.Button") to structure like
		// { cls: dijit.Button, params: {label: "string", disabled: "boolean"} }
	};
	
	function getClassInfo(/*String*/ className){
		// className:
		//		fully qualified name (like "dijit.Button")
		// returns:
		//		structure like
		//			{ 
		//				cls: dijit.Button, 
		//				params: { label: "string", disabled: "boolean"}
		//			}

		if(!instanceClasses[className]){
			// get pointer to widget class
			var cls = d.getObject(className);
			if(!d.isFunction(cls)){
				throw new Error("Could not load class '" + className +
					"'. Did you spell the name correctly and use a full path, like 'dijit.form.Button'?");
			}
			var proto = cls.prototype;
	
			// get table of parameter names & types
			var params={};
			for(var name in proto){
				if(name.charAt(0)=="_"){ continue; } 	// skip internal properties
				var defVal = proto[name];
				params[name]=val2type(defVal);
			}

			instanceClasses[className] = { cls: cls, params: params };
		}
		return instanceClasses[className];
	}

	this._functionFromScript = function(script){
		var preamble = "";
		var suffix = "";
		var argsStr = script.getAttribute("args");
		if(argsStr){
			d.forEach(argsStr.split(/\s*,\s*/), function(part, idx){
				preamble += "var "+part+" = arguments["+idx+"]; ";
			});
		}
		var withStr = script.getAttribute("with");
		if(withStr && withStr.length){
			d.forEach(withStr.split(/\s*,\s*/), function(part){
				preamble += "with("+part+"){";
				suffix += "}";
			});
		}
		return new Function(preamble+script.innerHTML+suffix);
	}

	this.instantiate = function(/* Array */nodes){
		// summary:
		//		Takes array of nodes, and turns them into class instances and
		//		potentially calls a layout method to allow them to connect with
		//		any children		
		var thelist = [];
		d.forEach(nodes, function(node){
			if(!node){ return; }
			var type = node.getAttribute("dojoType");
			if((!type)||(!type.length)){ return; }
			var clsInfo = getClassInfo(type);
			var clazz = clsInfo.cls;
			var ps = clazz._noScript||clazz.prototype._noScript;

			// read parameters (ie, attributes).
			// clsInfo.params lists expected params like {"checked": "boolean", "n": "number"}
			var params = {};
			var attributes = node.attributes;
			for(var name in clsInfo.params){
				var item = attributes.getNamedItem(name);
				if(!item || (!item.specified && (!dojo.isIE || name.toLowerCase()!="value"))){ continue; }
				var value = item.value;
				// Deal with IE quirks for 'class' and 'style'
				switch(name){
				case "class":
					value = node.className;
					break;
				case "style":
					value = node.style && node.style.cssText; // FIXME: Opera?
				}
				var _type = clsInfo.params[name];
				params[name] = str2obj(value, _type);
			}

			// Process <script type="dojo/*"> script tags
			// <script type="dojo/method" event="foo"> tags are added to params, and passed to
			// the widget on instantiation.
			// <script type="dojo/method"> tags (with no event) are executed after instantiation
			// <script type="dojo/connect" event="foo"> tags are dojo.connected after instantiation
			if(!ps){
				var connects = [],	// functions to connect after instantiation
					calls = [];		// functions to call after instantiation

				d.query("> script[type^='dojo/']", node).orphan().forEach(function(script){
					var event = script.getAttribute("event"),
						type = script.getAttribute("type"),
						nf = d.parser._functionFromScript(script);
					if(event){
						if(type == "dojo/connect"){
							connects.push({event: event, func: nf});
						}else{
							params[event] = nf;
						}
					}else{
						calls.push(nf);
					}
				});
			}

			var markupFactory = clazz["markupFactory"];
			if(!markupFactory && clazz["prototype"]){
				markupFactory = clazz.prototype["markupFactory"];
			}
			// create the instance
			var instance = markupFactory ? markupFactory(params, node, clazz) : new clazz(params, node);
			thelist.push(instance);

			// map it to the JS namespace if that makes sense
			var jsname = node.getAttribute("jsId");
			if(jsname){
				d.setObject(jsname, instance);
			}

			// process connections and startup functions
			if(!ps){
				dojo.forEach(connects, function(connect){
					dojo.connect(instance, connect.event, null, connect.func);
				});
				dojo.forEach(calls, function(func){
					func.call(instance);
				});
			}
		});

		// Call startup on each top level instance if it makes sense (as for
		// widgets).  Parent widgets will recursively call startup on their
		// (non-top level) children
		d.forEach(thelist, function(instance){
			if(	instance  && 
				(instance.startup) && 
				((!instance.getParent) || (!instance.getParent()))
			){
				instance.startup();
			}
		});
		return thelist;
	};

	this.parse = function(/*DomNode?*/ rootNode){
		// summary:
		//		Search specified node (or root node) recursively for class instances,
		//		and instantiate them Searches for
		//		dojoType="qualified.class.name"
		var list = d.query('[dojoType]', rootNode);
		// go build the object instances
		var instances = this.instantiate(list);
		return instances;
	};
}();

//Register the parser callback. It should be the first callback
//after the a11y test.

(function(){
	var parseRunner = function(){ 
		if(djConfig["parseOnLoad"] == true){
			dojo.parser.parse(); 
		}
	};

	// FIXME: need to clobber cross-dependency!!
	if(dojo.exists("dijit.wai.onload") && (dijit.wai.onload === dojo._loaders[0])){
		dojo._loaders.splice(1, 0, parseRunner);
	}else{
		dojo._loaders.unshift(parseRunner);
	}
})();

//TODO: ported from 0.4.x Dojo.  Can we reduce this?
dojo.parser._anonCtr = 0;
dojo.parser._anon = {}; // why is this property required?
dojo.parser._nameAnonFunc = function(/*Function*/anonFuncPtr, /*Object*/thisObj){
	// summary:
	//		Creates a reference to anonFuncPtr in thisObj with a completely
	//		unique name. The new name is returned as a String. 
	var jpn = "$joinpoint";
	var nso = (thisObj|| dojo.parser._anon);
	if(dojo.isIE){
		var cn = anonFuncPtr["__dojoNameCache"];
		if(cn && nso[cn] === anonFuncPtr){
			return anonFuncPtr["__dojoNameCache"];
		}
	}
	var ret = "__"+dojo.parser._anonCtr++;
	while(typeof nso[ret] != "undefined"){
		ret = "__"+dojo.parser._anonCtr++;
	}
	nso[ret] = anonFuncPtr;
	return ret; // String
}

}

if(!dojo._hasResource["dijit._Widget"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._Widget"] = true;
dojo.provide("dijit._Widget");



dojo.declare("dijit._Widget", null, {
	// summary:
	//		The foundation of dijit widgets. 	
	//
	// id: String
	//		a unique, opaque ID string that can be assigned by users or by the
	//		system. If the developer passes an ID which is known not to be
	//		unique, the specified ID is ignored and the system-generated ID is
	//		used instead.
	id: "",

	// lang: String
	//	Language to display this widget in (like en-us).
	//	Defaults to brower's specified preferred language (typically the language of the OS)
	lang: "",

	// dir: String
	//  Bi-directional support, as defined by the HTML DIR attribute. Either left-to-right "ltr" or right-to-left "rtl".
	dir: "",

	// class: String
	// HTML class attribute
	"class": "",

	// style: String
	// HTML style attribute
	style: "",

	// title: String
	// HTML title attribute
	title: "",

	// srcNodeRef: DomNode
	//		pointer to original dom node
	srcNodeRef: null,

	// domNode: DomNode
	//		this is our visible representation of the widget! Other DOM
	//		Nodes may by assigned to other properties, usually through the
	//		template system's dojoAttachPonit syntax, but the domNode
	//		property is the canonical "top level" node in widget UI.
	domNode: null,

	// attributeMap: Object
	//		A map of attributes and attachpoints -- typically standard HTML attributes -- to set
	//		on the widget's dom, at the "domNode" attach point, by default.
	//		Other node references can be specified as properties of 'this'
	attributeMap: {id:"", dir:"", lang:"", "class":"", style:"", title:""},  // TODO: add on* handlers?

	//////////// INITIALIZATION METHODS ///////////////////////////////////////

	postscript: function(params, srcNodeRef){
		this.create(params, srcNodeRef);
	},

	create: function(params, srcNodeRef){
		// summary:
		//		To understand the process by which widgets are instantiated, it
		//		is critical to understand what other methods create calls and
		//		which of them you'll want to override. Of course, adventurous
		//		developers could override create entirely, but this should
		//		only be done as a last resort.
		//
		//		Below is a list of the methods that are called, in the order
		//		they are fired, along with notes about what they do and if/when
		//		you should over-ride them in your widget:
		//			
		//			postMixInProperties:
		//				a stub function that you can over-ride to modify
		//				variables that may have been naively assigned by
		//				mixInProperties
		//			# widget is added to manager object here
		//			buildRendering
		//				Subclasses use this method to handle all UI initialization
		//				Sets this.domNode.  Templated widgets do this automatically
		//				and otherwise it just uses the source dom node.
		//			postCreate
		//				a stub function that you can over-ride to modify take
		//				actions once the widget has been placed in the UI

		// store pointer to original dom tree
		this.srcNodeRef = dojo.byId(srcNodeRef);

		// For garbage collection.  An array of handles returned by Widget.connect()
		// Each handle returned from Widget.connect() is an array of handles from dojo.connect()
		this._connects=[];

		// _attaches: String[]
		// 		names of all our dojoAttachPoint variables
		this._attaches=[];

		//mixin our passed parameters
		if(this.srcNodeRef && (typeof this.srcNodeRef.id == "string")){ this.id = this.srcNodeRef.id; }
		if(params){
			dojo.mixin(this,params);
		}
		this.postMixInProperties();

		// generate an id for the widget if one wasn't specified
		// (be sure to do this before buildRendering() because that function might
		// expect the id to be there.
		if(!this.id){
			this.id=dijit.getUniqueId(this.declaredClass.replace(/\./g,"_"));
		}
		dijit.registry.add(this);

		this.buildRendering();

		// Copy attributes listed in attributeMap into the [newly created] DOM for the widget.
		// The placement of these attributes is according to the property mapping in attributeMap.
		// Note special handling for 'style' and 'class' attributes which are lists and can
		// have elements from both old and new structures, and some attributes like "type"
		// cannot be processed this way as they are not mutable.
		if(this.domNode){
			for(var attr in this.attributeMap){
				var mapNode = this[this.attributeMap[attr] || "domNode"];
				var value = this[attr];
				if(typeof value != "object" && (value !== "" || (params && params[attr]))){
					switch(attr){
					case "class":
						dojo.addClass(mapNode, value);
						break;
					case "style":
						if(mapNode.style.cssText){
							mapNode.style.cssText += "; " + value;// FIXME: Opera
						}else{
							mapNode.style.cssText = value;
						}
						break;
					default:
						mapNode.setAttribute(attr, value);
					}
				}
			}
		}

		if(this.domNode){
			this.domNode.setAttribute("widgetId", this.id);
		}
		this.postCreate();

		// If srcNodeRef has been processed and removed from the DOM (e.g. TemplatedWidget) then delete it to allow GC.
		if(this.srcNodeRef && !this.srcNodeRef.parentNode){
			delete this.srcNodeRef;
		}	
	},

	postMixInProperties: function(){
		// summary
		//	Called after the parameters to the widget have been read-in,
		//	but before the widget template is instantiated.
		//	Especially useful to set properties that are referenced in the widget template.
	},

	buildRendering: function(){
		// summary:
		//		Construct the UI for this widget, setting this.domNode.
		//		Most widgets will mixin TemplatedWidget, which overrides this method.
		this.domNode = this.srcNodeRef || dojo.doc.createElement('div');
	},

	postCreate: function(){
		// summary:
		//		Called after a widget's dom has been setup
	},

	startup: function(){
		// summary:
		//		Called after a widget's children, and other widgets on the page, have been created.
		//		Provides an opportunity to manipulate any children before they are displayed
		//		This is useful for composite widgets that need to control or layout sub-widgets
		//		Many layout widgets can use this as a wiring phase
	},

	//////////// DESTROY FUNCTIONS ////////////////////////////////

	destroyRecursive: function(/*Boolean*/ finalize){
		// summary:
		// 		Destroy this widget and it's descendants. This is the generic
		// 		"destructor" function that all widget users should call to
		// 		cleanly discard with a widget. Once a widget is destroyed, it's
		// 		removed from the manager object.
		// finalize: Boolean
		//		is this function being called part of global environment
		//		tear-down?

		this.destroyDescendants();
		this.destroy();
	},

	destroy: function(/*Boolean*/ finalize){
		// summary:
		// 		Destroy this widget, but not its descendants
		// finalize: Boolean
		//		is this function being called part of global environment
		//		tear-down?
		this.uninitialize();
		dojo.forEach(this._connects, function(array){
			dojo.forEach(array, dojo.disconnect);
		});
		this.destroyRendering(finalize);
		dijit.registry.remove(this.id);
	},

	destroyRendering: function(/*Boolean*/ finalize){
		// summary:
		//		Destroys the DOM nodes associated with this widget
		// finalize: Boolean
		//		is this function being called part of global environment
		//		tear-down?

		if(this.bgIframe){
			this.bgIframe.destroy();
			delete this.bgIframe;
		}

		if(this.domNode){
			dojo._destroyElement(this.domNode);
			delete this.domNode;
		}

		if(this.srcNodeRef){
			dojo._destroyElement(this.srcNodeRef);
			delete this.srcNodeRef;
		}
	},

	destroyDescendants: function(){
		// summary:
		//		Recursively destroy the children of this widget and their
		//		descendants.

		// TODO: should I destroy in the reverse order, to go bottom up?
		dojo.forEach(this.getDescendants(), function(widget){ widget.destroy(); });
	},

	uninitialize: function(){
		// summary:
		//		stub function. Over-ride to implement custom widget tear-down
		//		behavior.
		return false;
	},

	////////////////// MISCELLANEOUS METHODS ///////////////////

	toString: function(){
		// summary:
		//		returns a string that represents the widget. When a widget is
		//		cast to a string, this method will be used to generate the
		//		output. Currently, it does not implement any sort of reversable
		//		serialization.
		return '[Widget ' + this.declaredClass + ', ' + (this.id || 'NO ID') + ']'; // String
	},

	getDescendants: function(){
		// summary:
		//	return all the descendant widgets
		var list = dojo.query('[widgetId]', this.domNode);
		return list.map(dijit.byNode);		// Array
	},

	nodesWithKeyClick : ["input", "button"],

	connect: function(
			/*Object|null*/ obj,
			/*String*/ event,
			/*String|Function*/ method){

		// summary:
		//		Connects specified obj/event to specified method of this object
		//		and registers for disconnect() on widget destroy.
		//		Special event: "ondijitclick" triggers on a click or enter-down or space-up
		//		Similar to dojo.connect() but takes three arguments rather than four.
		var handles =[];
		if(event == "ondijitclick"){
			var w = this;
			// add key based click activation for unsupported nodes.
			if(!this.nodesWithKeyClick[obj.nodeName]){
				handles.push(dojo.connect(obj, "onkeydown", this,
					function(e){
						if(e.keyCode == dojo.keys.ENTER){
							return (dojo.isString(method))?
								w[method](e) : method.call(w, e);
						}else if(e.keyCode == dojo.keys.SPACE){
							// stop space down as it causes IE to scroll
							// the browser window
							dojo.stopEvent(e);
						}
			 		}));
				handles.push(dojo.connect(obj, "onkeyup", this,
					function(e){
						if(e.keyCode == dojo.keys.SPACE){
							return dojo.isString(method) ?
								w[method](e) : method.call(w, e);
						}
			 		}));
			}
			event = "onclick";
		}
		handles.push(dojo.connect(obj, event, this, method));

		// return handles for FormElement and ComboBox
		this._connects.push(handles);
		return handles;
	},

	disconnect: function(/*Object*/ handles){
		// summary:
		//		Disconnects handle created by this.connect.
		//		Also removes handle from this widget's list of connects
		for(var i=0; i<this._connects.length; i++){
			if(this._connects[i]==handles){
				dojo.forEach(handles, dojo.disconnect);
				this._connects.splice(i, 1);
				return;
			}
		}
	},

	isLeftToRight: function(){
		// summary:
		//		Checks the DOM to for the text direction for bi-directional support
		// description:
		//		This method cannot be used during widget construction because the widget
		//		must first be connected to the DOM tree.  Parent nodes are searched for the
		//		'dir' attribute until one is found, otherwise left to right mode is assumed.
		//		See HTML spec, DIR attribute for more information.

		if(typeof this._ltr == "undefined"){
			this._ltr = dojo.getComputedStyle(this.domNode).direction != "rtl";
		}
		return this._ltr; //Boolean
	},

	isFocusable: function(){
		// summary:
		//		Return true if this widget can currently be focused
		//		and false if not
		return this.focus && (dojo.style(this.domNode, "display") != "none");
	}
});

}

if(!dojo._hasResource["dojo.string"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.string"] = true;
dojo.provide("dojo.string");

dojo.string.pad = function(/*String*/text, /*int*/size, /*String?*/ch, /*boolean?*/end){
	// summary:
	//		Pad a string to guarantee that it is at least 'size' length by
	//		filling with the character 'c' at either the start or end of the
	//		string. Pads at the start, by default.
	// text: the string to pad
	// size: length to provide padding
	// ch: character to pad, defaults to '0'
	// end: adds padding at the end if true, otherwise pads at start

	var out = String(text);
	if(!ch){
		ch = '0';
	}
	while(out.length < size){
		if(end){
			out += ch;
		}else{
			out = ch + out;
		}
	}
	return out;	// String
};

dojo.string.substitute = function(	/*String*/template, 
									/*Object or Array*/map, 
									/*Function?*/transform, 
									/*Object?*/thisObject){
	// summary:
	//		Performs parameterized substitutions on a string. Throws an
	//		exception if any parameter is unmatched.
	// description:
	//		For example,
	//		|	dojo.string.substitute("File '${0}' is not found in directory '${1}'.",["foo.html","/temp"]);
	//		|	dojo.string.substitute("File '${name}' is not found in directory '${info.dir}'.",{name: "foo.html", info: {dir: "/temp"}});
	//		both return
	//			"File 'foo.html' is not found in directory '/temp'."
	// template: 
	//		a string with expressions in the form ${key} to be replaced or
	//		${key:format} which specifies a format function.  NOTE syntax has
	//		changed from %{key}
	// map: where to look for substitutions
	// transform: 
	//		a function to process all parameters before substitution takes
	//		place, e.g. dojo.string.encodeXML
	// thisObject: 
	//		where to look for optional format function; default to the global
	//		namespace

	return template.replace(/\$\{([^\s\:\}]+)(?:\:([^\s\:\}]+))?\}/g, function(match, key, format){
		var value = dojo.getObject(key,false,map);
		if(format){ value = dojo.getObject(format,false,thisObject)(value);}
		if(transform){ value = transform(value, key); }
		return value.toString();
	}); // string
};

dojo.string.trim = function(/*String*/ str){
	// summary: trims whitespaces from both sides of the string
	// description:
	//	This version of trim() was taken from Steven Levithan's blog: 
	//	http://blog.stevenlevithan.com/archives/faster-trim-javascript.
	//	The short yet good-performing version of this function is 
	//	dojo.trim(), which is part of the base.
	str = str.replace(/^\s+/, '');
	for(var i = str.length - 1; i > 0; i--){
		if(/\S/.test(str.charAt(i))){
			str = str.substring(0, i + 1);
			break;
		}
	}
	return str;	// String
};

}

if(!dojo._hasResource["dijit._Templated"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._Templated"] = true;
dojo.provide("dijit._Templated");






dojo.declare("dijit._Templated",
	null,
	{
		// summary:
		//		mixin for widgets that are instantiated from a template

		// templateNode: DomNode
		//		a node that represents the widget template. Pre-empts both templateString and templatePath.
		templateNode: null,

		// templateString String:
		//		a string that represents the widget template. Pre-empts the
		//		templatePath. In builds that have their strings "interned", the
		//		templatePath is converted to an inline templateString, thereby
		//		preventing a synchronous network call.
		templateString: null,

		// templatePath: String
		//	Path to template (HTML file) for this widget
		templatePath: null,

		// widgetsInTemplate Boolean:
		//		should we parse the template to find widgets that might be
		//		declared in markup inside it? false by default.
		widgetsInTemplate: false,

		// containerNode DomNode:
		//		holds child elements. "containerNode" is generally set via a
		//		dojoAttachPoint assignment and it designates where children of
		//		the src dom node will be placed
		containerNode: null,

		// skipNodeCache Boolean:
		//		if using a cached widget template node poses issues for a
		//		particular widget class, it can set this property to ensure
		//		that its template is always re-built from a string
		_skipNodeCache: false,

		// method over-ride
		buildRendering: function(){
			// summary:
			//		Construct the UI for this widget from a template, setting this.domNode.

			// Lookup cached version of template, and download to cache if it
			// isn't there already.  Returns either a DomNode or a string, depending on
			// whether or not the template contains ${foo} replacement parameters.
			var cached = dijit._Templated.getCachedTemplate(this.templatePath, this.templateString, this._skipNodeCache);

			var node;
			if(dojo.isString(cached)){
				var className = this.declaredClass, _this = this;
				// Cache contains a string because we need to do property replacement
				// do the property replacement
				var tstr = dojo.string.substitute(cached, this, function(value, key){
					if(key.charAt(0) == '!'){ value = _this[key.substr(1)]; }
					if(typeof value == "undefined"){ throw new Error(className+" template:"+key); } // a debugging aide
					if(!value){ return ""; }

					// Substitution keys beginning with ! will skip the transform step,
					// in case a user wishes to insert unescaped markup, e.g. ${!foo}
					return key.charAt(0) == "!" ? value :
						// Safer substitution, see heading "Attribute values" in
						// http://www.w3.org/TR/REC-html40/appendix/notes.html#h-B.3.2
						value.toString().replace(/"/g,"&quot;"); //TODO: add &amp? use encodeXML method?
				}, this);

				node = dijit._Templated._createNodesFromText(tstr)[0];
			}else{
				// if it's a node, all we have to do is clone it
				node = cached.cloneNode(true);
			}

			// recurse through the node, looking for, and attaching to, our
			// attachment points which should be defined on the template node.
			this._attachTemplateNodes(node);

			var source = this.srcNodeRef;
			if(source && source.parentNode){
				source.parentNode.replaceChild(node, source);
			}

			this.domNode = node;
			if(this.widgetsInTemplate){
				var childWidgets = dojo.parser.parse(node);
				this._attachTemplateNodes(childWidgets, function(n,p){
					return n[p];
				});
			}

			this._fillContent(source);
		},

		_fillContent: function(/*DomNode*/ source){
			// summary:
			//		relocate source contents to templated container node
			//		this.containerNode must be able to receive children, or exceptions will be thrown
			var dest = this.containerNode;
			if(source && dest){
				while(source.hasChildNodes()){
					dest.appendChild(source.firstChild);
				}
			}
		},

		_attachTemplateNodes: function(rootNode, getAttrFunc){
			// summary:
			//		map widget properties and functions to the handlers specified in
			//		the dom node and it's descendants. This function iterates over all
			//		nodes and looks for these properties:
			//			* dojoAttachPoint
			//			* dojoAttachEvent	
			//			* waiRole
			//			* waiState
			// rootNode: DomNode|Array[Widgets]
			//		the node to search for properties. All children will be searched.
			// getAttrFunc: function?
			//		a function which will be used to obtain property for a given
			//		DomNode/Widget

			getAttrFunc = getAttrFunc || function(n,p){ return n.getAttribute(p); };

			var nodes = dojo.isArray(rootNode) ? rootNode : (rootNode.all || rootNode.getElementsByTagName("*"));
			var x=dojo.isArray(rootNode)?0:-1;
			for(; x<nodes.length; x++){
				var baseNode = (x == -1) ? rootNode : nodes[x];
				if(this.widgetsInTemplate && getAttrFunc(baseNode,'dojoType')){
					continue;
				}
				// Process dojoAttachPoint
				var attachPoint = getAttrFunc(baseNode, "dojoAttachPoint");
				if(attachPoint){
					var point, points = attachPoint.split(/\s*,\s*/);
					while(point=points.shift()){
						if(dojo.isArray(this[point])){
							this[point].push(baseNode);
						}else{
							this[point]=baseNode;
						}
					}
				}

				// Process dojoAttachEvent
				var attachEvent = getAttrFunc(baseNode, "dojoAttachEvent");
				if(attachEvent){
					// NOTE: we want to support attributes that have the form
					// "domEvent: nativeEvent; ..."
					var event, events = attachEvent.split(/\s*,\s*/);
					var trim = dojo.trim;
					while(event=events.shift()){
						if(event){
							var thisFunc = null;
							if(event.indexOf(":") != -1){
								// oh, if only JS had tuple assignment
								var funcNameArr = event.split(":");
								event = trim(funcNameArr[0]);
								thisFunc = trim(funcNameArr[1]);
							}else{
								event = trim(event);
							}
							if(!thisFunc){
								thisFunc = event;
							}
							this.connect(baseNode, event, thisFunc);
						}
					}
				}

				// waiRole, waiState
				var role = getAttrFunc(baseNode, "waiRole");
				if(role){
					dijit.setWaiRole(baseNode, role);
				}
				var values = getAttrFunc(baseNode, "waiState");
				if(values){
					dojo.forEach(values.split(/\s*,\s*/), function(stateValue){
						if(stateValue.indexOf('-') != -1){
							var pair = stateValue.split('-');
							dijit.setWaiState(baseNode, pair[0], pair[1]);
						}
					});
				}

			}
		}
	}
);

// key is either templatePath or templateString; object is either string or DOM tree
dijit._Templated._templateCache = {};

dijit._Templated.getCachedTemplate = function(templatePath, templateString, alwaysUseString){
	// summary:
	//		static method to get a template based on the templatePath or
	//		templateString key
	// templatePath: String
	//		the URL to get the template from. dojo.uri.Uri is often passed as well.
	// templateString: String?
	//		a string to use in lieu of fetching the template from a URL
	// Returns:
	//	Either string (if there are ${} variables that need to be replaced) or just
	//	a DOM tree (if the node can be cloned directly)

	// is it already cached?
	var tmplts = dijit._Templated._templateCache;
	var key = templateString || templatePath;
	var cached = tmplts[key];
	if(cached){
		return cached;
	}

	// If necessary, load template string from template path
	if(!templateString){
		templateString = dijit._Templated._sanitizeTemplateString(dojo._getText(templatePath));
	}

	templateString = dojo.string.trim(templateString);

	if(templateString.match(/\$\{([^\}]+)\}/g) || alwaysUseString){
		// there are variables in the template so all we can do is cache the string
		return (tmplts[key] = templateString); //String
	}else{
		// there are no variables in the template so we can cache the DOM tree
		return (tmplts[key] = dijit._Templated._createNodesFromText(templateString)[0]); //Node
	}
};

dijit._Templated._sanitizeTemplateString = function(/*String*/tString){
	// summary: 
	//		Strips <?xml ...?> declarations so that external SVG and XML
	// 		documents can be added to a document without worry. Also, if the string
	//		is an HTML document, only the part inside the body tag is returned.
	if(tString){
		tString = tString.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im, "");
		var matches = tString.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
		if(matches){
			tString = matches[1];
		}
	}else{
		tString = "";
	}
	return tString; //String
};


if(dojo.isIE){
	dojo.addOnUnload(function(){
		var cache = dijit._Templated._templateCache;
		for(var key in cache){
			var value = cache[key];
			if(!isNaN(value.nodeType)){ // isNode equivalent
				dojo._destroyElement(value);
			}
			delete cache[key];
		}
	});
}

(function(){
	var tagMap = {
		cell: {re: /^<t[dh][\s\r\n>]/i, pre: "<table><tbody><tr>", post: "</tr></tbody></table>"},
		row: {re: /^<tr[\s\r\n>]/i, pre: "<table><tbody>", post: "</tbody></table>"},
		section: {re: /^<(thead|tbody|tfoot)[\s\r\n>]/i, pre: "<table>", post: "</table>"}
	};

	// dummy container node used temporarily to hold nodes being created
	var tn;

	dijit._Templated._createNodesFromText = function(/*String*/text){
		// summary:
		//	Attempts to create a set of nodes based on the structure of the passed text.

		if(!tn){
			tn = dojo.doc.createElement("div");
			tn.style.display="none";
			dojo.body().appendChild(tn);
		}
		var tableType = "none";
		var rtext = text.replace(/^\s+/, "");
		for(var type in tagMap){
			var map = tagMap[type];
			if(map.re.test(rtext)){
				tableType = type;
				text = map.pre + text + map.post;
				break;
			}
		}

		tn.innerHTML = text;
		if(tn.normalize){
			tn.normalize();
		}

		var tag = { cell: "tr", row: "tbody", section: "table" }[tableType];
		var _parent = (typeof tag != "undefined") ?
						tn.getElementsByTagName(tag)[0] :
						tn;

		var nodes = [];
		while(_parent.firstChild){
			nodes.push(_parent.removeChild(_parent.firstChild));
		}
		tn.innerHTML="";
		return nodes;	//	Array
	}
})();

// These arguments can be specified for widgets which are used in templates.
// Since any widget can be specified as sub widgets in template, mix it
// into the base widget class.  (This is a hack, but it's effective.)
dojo.extend(dijit._Widget,{
	dojoAttachEvent: "",
	dojoAttachPoint: "",
	waiRole: "",
	waiState:""
})

}

if(!dojo._hasResource["dijit._Container"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._Container"] = true;
dojo.provide("dijit._Container");

dojo.declare("dijit._Contained",
	null,
	{
		// summary
		//		Mixin for widgets that are children of a container widget

		getParent: function(){
			// summary:
			//		returns the parent widget of this widget, assuming the parent
			//		implements dijit._Container
			for(var p=this.domNode.parentNode; p; p=p.parentNode){
				var id = p.getAttribute && p.getAttribute("widgetId");
				if(id){
					var parent = dijit.byId(id);
					return parent.isContainer ? parent : null;
				}
			}
			return null;
		},

		_getSibling: function(which){
			var node = this.domNode;
			do{
				node = node[which+"Sibling"];
			}while(node && node.nodeType != 1);
			if(!node){ return null; } // null
			var id = node.getAttribute("widgetId");
			return dijit.byId(id);
		},

		getPreviousSibling: function(){
			// summary:
			//		returns null if this is the first child of the parent,
			//		otherwise returns the next element sibling to the "left".

			return this._getSibling("previous");
		},

		getNextSibling: function(){
			// summary:
			//		returns null if this is the last child of the parent,
			//		otherwise returns the next element sibling to the "right".

			return this._getSibling("next");
		}
	}
);

dojo.declare("dijit._Container",
	null,
	{
		// summary
		//		Mixin for widgets that contain a list of children like SplitContainer

		isContainer: true,

		addChild: function(/*Widget*/ widget, /*int?*/ insertIndex){
			// summary:
			//		Process the given child widget, inserting it's dom node as
			//		a child of our dom node

			if(insertIndex === undefined){
				insertIndex = "last";
			}
			var refNode = this.containerNode || this.domNode;
			if(insertIndex && typeof insertIndex == "number"){
				var children = dojo.query("> [widgetid]", refNode);
				if(children && children.length >= insertIndex){
					refNode = children[insertIndex-1]; insertIndex = "after";
				}
			}
			dojo.place(widget.domNode, refNode, insertIndex);

			// If I've been started but the child widget hasn't been started,
			// start it now.  Make sure to do this after widget has been
			// inserted into the DOM tree, so it can see that it's being controlled by me,
			// so it doesn't try to size itself.
			if(this._started && !widget._started){
				widget.startup();
			}
		},

		removeChild: function(/*Widget*/ widget){
			// summary:
			//		removes the passed widget instance from this widget but does
			//		not destroy it
			var node = widget.domNode;
			node.parentNode.removeChild(node);	// detach but don't destroy
		},

		_nextElement: function(node){
			do{
				node = node.nextSibling;
			}while(node && node.nodeType != 1);
			return node;
		},

		_firstElement: function(node){
			node = node.firstChild;
			if(node && node.nodeType != 1){
				node = this._nextElement(node);
			}
			return node;
		},

		getChildren: function(){
			// summary:
			//		Returns array of children widgets
			return dojo.query("> [widgetId]", this.containerNode || this.domNode).map(dijit.byNode); // Array
		},

		hasChildren: function(){
			// summary:
			//		Returns true if widget has children
			var cn = this.containerNode || this.domNode;
			return !!this._firstElement(cn); // Boolean
		},

		_getSiblingOfChild: function(/*Widget*/ child, /*int*/ dir){
			// summary:
			//		get the next or previous widget sibling of child
			// dir:
			//		if 1, get the next sibling
			//		if -1, get the previous sibling
			var node = child.domNode;
			var which = (dir>0 ? "nextSibling" : "previousSibling");
			do{
				node = node[which];
			}while(node && (node.nodeType != 1 || !dijit.byNode(node)));
			return node ? dijit.byNode(node) : null;
		}
	}
);

dojo.declare("dijit._KeyNavContainer",
	[dijit._Container],
	{

		// summary:
		//		A _Container with keyboard navigation of its children.
		//		To use this mixin, call connectKeyNavHandlers() in
		//		postCreate() and call startupKeyNavChildren() in startup().

/*=====
		// focusedChild: Widget
		//		The currently focused child widget, or null if there isn't one
		focusedChild: null,
=====*/

		_keyNavCodes: {},

		connectKeyNavHandlers: function(/*Array*/ prevKeyCodes, /*Array*/ nextKeyCodes){
			// summary:
			//		Call in postCreate() to attach the keyboard handlers
			//		to the container.
			// preKeyCodes: Array
			//		Key codes for navigating to the previous child.
			// nextKeyCodes: Array
			//		Key codes for navigating to the next child.

			var keyCodes = this._keyNavCodes = {};
			var prev = dojo.hitch(this, this.focusPrev);
			var next = dojo.hitch(this, this.focusNext);
			dojo.forEach(prevKeyCodes, function(code){ keyCodes[code] = prev });
			dojo.forEach(nextKeyCodes, function(code){ keyCodes[code] = next });
			this.connect(this.domNode, "onkeypress", "_onContainerKeypress");
			if(dojo.isIE){
				this.connect(this.domNode, "onactivate", "_onContainerFocus");
				this.connect(this.domNode, "ondeactivate", "_onContainerBlur");
			}else{
				this.connect(this.domNode, "onfocus", "_onContainerFocus");
				this.connect(this.domNode, "onblur", "_onContainerBlur");
			}
		},

		startupKeyNavChildren: function(){
			// summary:
			//		Call in startup() to set child tabindexes to -1
			dojo.forEach(this.getChildren(), dojo.hitch(this, "_setTabIndexMinusOne"));
		},

		addChild: function(/*Widget*/ widget, /*int?*/ insertIndex){
			// summary: Add a child to our _Container
			dijit._KeyNavContainer.superclass.addChild.apply(this, arguments);
			this._setTabIndexMinusOne(widget);
		},

		focus: function(){
			// summary: Default focus() implementation: focus the first child.
			this.focusFirstChild();
		},

		focusFirstChild: function(){
			// summary: Focus the first focusable child in the container.
			this.focusChild(this._getFirstFocusableChild());
		},

		focusNext: function(){
			// summary: Focus the next widget or focal node (for widgets
			//		with multiple focal nodes) within this container.
			if(this.focusedChild && this.focusedChild.hasNextFocalNode
					&& this.focusedChild.hasNextFocalNode()){
				this.focusedChild.focusNext();
				return;
			}
			var child = this._getNextFocusableChild(this.focusedChild, 1);
			if(child.getFocalNodes){
				this.focusChild(child, child.getFocalNodes()[0]);
			}else{
				this.focusChild(child);
			}
		},

		focusPrev: function(){
			// summary: Focus the previous widget or focal node (for widgets
			//		with multiple focal nodes) within this container.
			if(this.focusedChild && this.focusedChild.hasPrevFocalNode
					&& this.focusedChild.hasPrevFocalNode()){
				this.focusedChild.focusPrev();
				return;
			}
			var child = this._getNextFocusableChild(this.focusedChild, -1);
			if(child.getFocalNodes){
				var nodes = child.getFocalNodes();
				this.focusChild(child, nodes[nodes.length-1]);
			}else{
				this.focusChild(child);
			}
		},

		focusChild: function(/*Widget*/ widget, /*Node?*/ node){
			// summary: Focus widget. Optionally focus 'node' within widget.
			if(widget){
				if(this.focusedChild && widget !== this.focusedChild){
					this._onChildBlur(this.focusedChild);
				}
				this.focusedChild = widget;
				if(node && widget.focusFocalNode){
					widget.focusFocalNode(node);
				}else{
					widget.focus();
				}
			}
		},

		_setTabIndexMinusOne: function(/*Widget*/ widget){
			if(widget.getFocalNodes){
				dojo.forEach(widget.getFocalNodes(), function(node){
					node.setAttribute("tabIndex", -1);
				});
			}else{
				(widget.focusNode || widget.domNode).setAttribute("tabIndex", -1);
			}
		},

		_onContainerFocus: function(evt){
			this.domNode.setAttribute("tabIndex", -1);
			if(evt.target === this.domNode){
				this.focusFirstChild();
			}else{
				var widget = dijit.getEnclosingWidget(evt.target);
				if(widget && widget.isFocusable()){
					this.focusedChild = widget;
				}
			}
		},

		_onContainerBlur: function(evt){
			if(this.tabIndex){
				this.domNode.setAttribute("tabIndex", this.tabIndex);
			}
		},

		_onContainerKeypress: function(evt){
			if(evt.ctrlKey || evt.altKey){ return; }
			var func = this._keyNavCodes[evt.keyCode];
			if(func){
				func();
				dojo.stopEvent(evt);
			}
		},

		_onChildBlur: function(/*Widget*/ widget){
			// summary:
			//		Called when focus leaves a child widget to go
			//		to a sibling widget.
		},

		_getFirstFocusableChild: function(){
			return this._getNextFocusableChild(null, 1);
		},

		_getNextFocusableChild: function(child, dir){
			if(child){
				child = this._getSiblingOfChild(child, dir);
			}
			var children = this.getChildren();
			for(var i=0; i < children.length; i++){
				if(!child){
					child = children[(dir>0) ? 0 : (children.length-1)];
				}
				if(child.isFocusable()){
					return child;
				}
				child = this._getSiblingOfChild(child, dir);
			}
		}
	}
);

}

if(!dojo._hasResource["dijit.layout._LayoutWidget"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.layout._LayoutWidget"] = true;
dojo.provide("dijit.layout._LayoutWidget");




dojo.declare("dijit.layout._LayoutWidget",
	[dijit._Widget, dijit._Container, dijit._Contained],
	{
		// summary
		//		Mixin for widgets that contain a list of children like SplitContainer.
		//		Widgets which mixin this code must define layout() to lay out the children

		isLayoutContainer: true,

		postCreate: function(){
			dojo.addClass(this.domNode, "dijitContainer");
		},

		startup: function(){
			// summary:
			//		Called after all the widgets have been instantiated and their
			//		dom nodes have been inserted somewhere under document.body.
			//
			//		Widgets should override this method to do any initialization
			//		dependent on other widgets existing, and then call
			//		this superclass method to finish things off.
			//
			//		startup() in subclasses shouldn't do anything
			//		size related because the size of the widget hasn't been set yet.

			if(this._started){ return; }
			this._started=true;

			if(this.getChildren){
				dojo.forEach(this.getChildren(), function(child){ child.startup(); });
			}

			// If I am a top level widget
			if(!this.getParent || !this.getParent()){
				// Do recursive sizing and layout of all my descendants
				// (passing in no argument to resize means that it has to glean the size itself)
				this.resize();

				// since my parent isn't a layout container, and my style is width=height=100% (or something similar),
				// then I need to watch when the window resizes, and size myself accordingly
				// (passing in no argument to resize means that it has to glean the size itself)
				this.connect(window, 'onresize', function(){this.resize();});
			}
		},

		resize: function(args){
			// summary:
			//		Explicitly set this widget's size (in pixels),
			//		and then call layout() to resize contents (and maybe adjust child widgets)
			//	
			// args: Object?
			//		{w: int, h: int, l: int, t: int}

			var node = this.domNode;

			// set margin box size, unless it wasn't specified, in which case use current size
			if(args){
				dojo.marginBox(node, args);

				// set offset of the node
				if(args.t){ node.style.top = args.t + "px"; }
				if(args.l){ node.style.left = args.l + "px"; }
			}
			// If either height or width wasn't specified by the user, then query node for it.
			// But note that setting the margin box and then immediately querying dimensions may return
			// inaccurate results, so try not to depend on it.
			var mb = dojo.mixin(dojo.marginBox(node), args||{});

			// Save the size of my content box.
			this._contentBox = dijit.layout.marginBox2contentBox(node, mb);

			// Callback for widget to adjust size of it's children
			this.layout();
		},

		layout: function(){
			//	summary
			//		Widgets override this method to size & position their contents/children.
			//		When this is called this._contentBox is guaranteed to be set (see resize()).
			//
			//		This is called after startup(), and also when the widget's size has been
			//		changed.
		}
	}
);

dijit.layout.marginBox2contentBox = function(/*DomNode*/ node, /*Object*/ mb){
	// summary:
	//		Given the margin-box size of a node, return it's content box size.
	//		Functions like dojo.contentBox() but is more reliable since it doesn't have
	//		to wait for the browser to compute sizes.
	var cs = dojo.getComputedStyle(node);
	var me=dojo._getMarginExtents(node, cs);
	var pb=dojo._getPadBorderExtents(node, cs);
	return {
		l: dojo._toPixelValue(node, cs.paddingLeft),
		t: dojo._toPixelValue(node, cs.paddingTop),
		w: mb.w - (me.w + pb.w),
		h: mb.h - (me.h + pb.h)
	};
};

(function(){
	var capitalize = function(word){
		return word.substring(0,1).toUpperCase() + word.substring(1);
	};

	var size = function(widget, dim){
		// size the child
		widget.resize ? widget.resize(dim) : dojo.marginBox(widget.domNode, dim);

		// record child's size, but favor our own numbers when we have them.
		// the browser lies sometimes
		dojo.mixin(widget, dojo.marginBox(widget.domNode));
		dojo.mixin(widget, dim);
	};

	dijit.layout.layoutChildren = function(/*DomNode*/ container, /*Object*/ dim, /*Object[]*/ children){
		/**
		 * summary
		 *		Layout a bunch of child dom nodes within a parent dom node
		 * container:
		 *		parent node
		 * dim:
		 *		{l, t, w, h} object specifying dimensions of container into which to place children
		 * children:
		 *		an array like [ {domNode: foo, layoutAlign: "bottom" }, {domNode: bar, layoutAlign: "client"} ]
		 */

		// copy dim because we are going to modify it
		dim = dojo.mixin({}, dim);

		dojo.addClass(container, "dijitLayoutContainer");

		// Move "client" elements to the end of the array for layout.  a11y dictates that the author
		// needs to be able to put them in the document in tab-order, but this algorithm requires that
		// client be last.
		children = dojo.filter(children, function(item){ return item.layoutAlign != "client"; })
			.concat(dojo.filter(children, function(item){ return item.layoutAlign == "client"; }));

		// set positions/sizes
		dojo.forEach(children, function(child){
			var elm = child.domNode,
				pos = child.layoutAlign;

			// set elem to upper left corner of unused space; may move it later
			var elmStyle = elm.style;
			elmStyle.left = dim.l+"px";
			elmStyle.top = dim.t+"px";
			elmStyle.bottom = elmStyle.right = "auto";

			dojo.addClass(elm, "dijitAlign" + capitalize(pos));

			// set size && adjust record of remaining space.
			// note that setting the width of a <div> may affect it's height.
			if(pos=="top" || pos=="bottom"){
				size(child, { w: dim.w });
				dim.h -= child.h;
				if(pos=="top"){
					dim.t += child.h;
				}else{
					elmStyle.top = dim.t + dim.h + "px";
				}
			}else if(pos=="left" || pos=="right"){
				size(child, { h: dim.h });
				dim.w -= child.w;
				if(pos=="left"){
					dim.l += child.w;
				}else{
					elmStyle.left = dim.l + dim.w + "px";
				}
			}else if(pos=="client"){
				size(child, dim);
			}
		});
	};

})();

}

if(!dojo._hasResource["dijit.form._FormWidget"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form._FormWidget"] = true;
dojo.provide("dijit.form._FormWidget");




dojo.declare("dijit.form._FormWidget", [dijit._Widget, dijit._Templated],
{
	/*
	Summary:
		FormElement widgets correspond to native HTML elements such as <input> or <button> or <select>.
		Each FormElement represents a single input value, and has a (possibly hidden) <input> element,
		to which it serializes its input value, so that form submission (either normal submission or via FormBind?)
		works as expected.

		All these widgets should have these attributes just like native HTML input elements.
		You can set them during widget construction, but after that they are read only.

		They also share some common methods.
	*/

	// baseClass: String
	//		Root CSS class of the widget (ex: dijitTextBox), used to add CSS classes of widget
	//		(ex: "dijitTextBox dijitTextBoxInvalid dijitTextBoxFocused dijitTextBoxInvalidFocused")
	//		See _setStateClass().
	baseClass: "",

	// value: String
	//		Corresponds to the native HTML <input> element's attribute.
	value: "",

	// name: String
	//		Name used when submitting form; same as "name" attribute or plain HTML elements
	name: "",

	// id: String
	//		Corresponds to the native HTML <input> element's attribute.
	//		Also becomes the id for the widget.
	id: "",

	// alt: String
	//		Corresponds to the native HTML <input> element's attribute.
	alt: "",

	// type: String
	//		Corresponds to the native HTML <input> element's attribute.
	type: "text",

	// tabIndex: Integer
	//		Order fields are traversed when user hits the tab key
	tabIndex: "0",

	// disabled: Boolean
	//		Should this widget respond to user input?
	//		In markup, this is specified as "disabled='disabled'", or just "disabled".
	disabled: false,

	// intermediateChanges: Boolean
	//		Fires onChange for each value change or only on demand
	intermediateChanges: false,

	// These mixins assume that the focus node is an INPUT, as many but not all _FormWidgets are.
	// Don't attempt to mixin the 'type', 'name' attributes here programatically -- they must be declared
	// directly in the template as read by the parser in order to function. IE is known to specifically 
	// require the 'name' attribute at element creation time.
	attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),
		{id:"focusNode", tabIndex:"focusNode", alt:"focusNode"}),

	setDisabled: function(/*Boolean*/ disabled){
		// summary:
		//		Set disabled state of widget.

		this.domNode.disabled = this.disabled = disabled;
		if(this.focusNode){
			this.focusNode.disabled = disabled;
		}
		if(disabled){
			//reset those, because after the domNode is disabled, we can no longer receive
			//mouse related events, see #4200
			this._hovering = false;
			this._active = false;
		}
		dijit.setWaiState(this.focusNode || this.domNode, "disabled", disabled);
		this._setStateClass();
	},


	_onMouse : function(/*Event*/ event){
		// summary:
		//	Sets _hovering, _active, and stateModifier properties depending on mouse state,
		//	then calls setStateClass() to set appropriate CSS classes for this.domNode.
		//
		//	To get a different CSS class for hover, send onmouseover and onmouseout events to this method.
		//	To get a different CSS class while mouse button is depressed, send onmousedown to this method.

		var mouseNode = event.target;
		if(mouseNode && mouseNode.getAttribute){
			this.stateModifier = mouseNode.getAttribute("stateModifier") || "";
		}

		if(!this.disabled){
			switch(event.type){
				case "mouseenter" :	
				case "mouseover" :
					this._hovering = true;
					break;

				case "mouseout" :	
				case "mouseleave" :	
					this._hovering = false;
					break;

				case "mousedown" :
					this._active = true;
					// set a global event to handle mouseup, so it fires properly
					//	even if the cursor leaves the button
					var self = this;
					// #2685: use this.connect and disconnect so destroy works properly
					var mouseUpConnector = this.connect(dojo.body(), "onmouseup", function(){
						self._active = false;
						self._setStateClass();
						self.disconnect(mouseUpConnector);
					});
					break;
			}
			this._setStateClass();
		}
	},

	isFocusable: function(){
		return !this.disabled && (dojo.style(this.domNode, "display") != "none");
	},

	focus: function(){
		dijit.focus(this.focusNode);
	},

	_setStateClass: function(){
		// summary
		//	Update the visual state of the widget by setting the css classes on this.domNode
		//  (or this.stateNode if defined) by combining this.baseClass with
		//	various suffixes that represent the current widget state(s).
		//
		//	In the case where a widget has multiple
		//	states, it sets the class based on all possible
		//  combinations.  For example, an invalid form widget that is being hovered
		//	will be "dijitInput dijitInputInvalid dijitInputHover dijitInputInvalidHover".
		//
		//	For complex widgets with multiple regions, there can be various hover/active states,
		//	such as "Hover" or "CloseButtonHover" (for tab buttons).
		//	This is controlled by a stateModifier="CloseButton" attribute on the close button node.
		//
		//	The widget may have one or more of the following states, determined
		//	by this.state, this.checked, this.valid, and this.selected:
		//		Error - ValidationTextBox sets this.state to "Error" if the current input value is invalid
		//		Checked - ex: a checkmark or a ToggleButton in a checked state, will have this.checked==true
		//		Selected - ex: currently selected tab will have this.selected==true
		//
		//	In addition, it may have at most one of the following states,
		//	based on this.disabled and flags set in _onMouse (this._active, this._hovering, this._focused):
		//		Disabled	- if the widget is disabled
		//		Active		- if the mouse (or space/enter key?) is being pressed down
		//		Focused		- if the widget has focus
		//		Hover		- if the mouse is over the widget
		//
		//	(even if multiple af the above conditions are true we only pick the first matching one)


		// Get original (non state related, non baseClass related) class specified in template
		if(!("staticClass" in this)){
			this.staticClass = (this.stateNode||this.domNode).className;
		}

		// Compute new set of classes
		var classes = [ this.baseClass ];

		function multiply(modifier){
			classes=classes.concat(dojo.map(classes, function(c){ return c+modifier; }));
		}

		if(this.checked){
			multiply("Checked");
		}
		if(this.state){
			multiply(this.state);
		}
		if(this.selected){
			multiply("Selected");
		}

		// Only one of these four can be applied.
		// Active trumps Focused, Focused trumps Hover, and Disabled trumps all.
		if(this.disabled){
			multiply("Disabled");
		}else if(this._active){
			multiply(this.stateModifier+"Active");
		}else{
			if(this._focused){
				multiply("Focused");
			}
			if((this.stateModifier || !this._focused) && this._hovering){
				multiply(this.stateModifier+"Hover");
			}
		}
		(this.stateNode || this.domNode).className = this.staticClass + " " + classes.join(" ");
	},

	onChange: function(newValue){
		// summary: callback when value is changed
	},

	postCreate: function(){
		this.setValue(this.value, null); // null reserved for initial value
		this.setDisabled(this.disabled);
		this._setStateClass();
	},

	setValue: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
		// summary: set the value of the widget.
		this._lastValue = newValue;
		dijit.setWaiState(this.focusNode || this.domNode, "valuenow", this.forWaiValuenow());
		if(priorityChange === undefined){ priorityChange = true; } // setValue with value only should fire onChange
		if(this._lastValueReported == undefined && priorityChange === null){ // don't report the initial value
			this._lastValueReported = newValue;
		}
		if((this.intermediateChanges || priorityChange) && 
			((newValue && newValue.toString)?newValue.toString():newValue) !== ((this._lastValueReported && this._lastValueReported.toString)?this._lastValueReported.toString():this._lastValueReported)){
			this._lastValueReported = newValue;
			this.onChange(newValue);
		}
	},

	getValue: function(){
		// summary: get the value of the widget.
		return this._lastValue;
	},

	undo: function(){
		// summary: restore the value to the last value passed to onChange
		this.setValue(this._lastValueReported, false);
	},

	_onKeyPress: function(e){
		if(e.keyCode == dojo.keys.ESCAPE && !e.shiftKey && !e.ctrlKey && !e.altKey){
			var v = this.getValue();
			var lv = this._lastValueReported;
			// Equality comparison of objects such as dates are done by reference so
			// two distinct objects are != even if they have the same data. So use
			// toStrings in case the values are objects.
			if((typeof lv != "undefined") && ((v!==null && v.toString)?v.toString():null) !== lv.toString()){	
				this.undo();
				dojo.stopEvent(e);
				return false;
			}
		}
		return true;
	},

	forWaiValuenow: function(){
		// summary: returns a value, reflecting the current state of the widget,
		//		to be used for the ARIA valuenow.
		// 		This method may be overridden by subclasses that want
		// 		to use something other than this.getValue() for valuenow
		return this.getValue();
	}
});

}

if(!dojo._hasResource["dijit.dijit"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.dijit"] = true;
dojo.provide("dijit.dijit");

// All the stuff in _base (these are the function that are guaranteed available without an explicit dojo.require)


// And some other stuff that we tend to pull in all the time anyway







}

if(!dojo._hasResource["dojo.i18n"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.i18n"] = true;
dojo.provide("dojo.i18n");

dojo.i18n.getLocalization = function(/*String*/packageName, /*String*/bundleName, /*String?*/locale){
	//	summary:
	//		Returns an Object containing the localization for a given resource
	//		bundle in a package, matching the specified locale.
	//	description:
	//		Returns a hash containing name/value pairs in its prototypesuch
	//		that values can be easily overridden.  Throws an exception if the
	//		bundle is not found.  Bundle must have already been loaded by
	//		dojo.requireLocalization() or by a build optimization step.  NOTE:
	//		try not to call this method as part of an object property
	//		definition (var foo = { bar: dojo.i18n.getLocalization() }).  In
	//		some loading situations, the bundle may not be available in time
	//		for the object definition.  Instead, call this method inside a
	//		function that is run after all modules load or the page loads (like
	//		in dojo.adOnLoad()), or in a widget lifecycle method.
	//	packageName:
	//		package which is associated with this resource
	//	bundleName:
	//		the base filename of the resource bundle (without the ".js" suffix)
	//	locale:
	//		the variant to load (optional).  By default, the locale defined by
	//		the host environment: dojo.locale

	locale = dojo.i18n.normalizeLocale(locale);

	// look for nearest locale match
	var elements = locale.split('-');
	var module = [packageName,"nls",bundleName].join('.');
	var bundle = dojo._loadedModules[module];
	if(bundle){
		var localization;
		for(var i = elements.length; i > 0; i--){
			var loc = elements.slice(0, i).join('_');
			if(bundle[loc]){
				localization = bundle[loc];
				break;
			}
		}
		if(!localization){
			localization = bundle.ROOT;
		}

		// make a singleton prototype so that the caller won't accidentally change the values globally
		if(localization){
			var clazz = function(){};
			clazz.prototype = localization;
			return new clazz(); // Object
		}
	}

	throw new Error("Bundle not found: " + bundleName + " in " + packageName+" , locale=" + locale);
};

dojo.i18n.normalizeLocale = function(/*String?*/locale){
	//	summary:
	//		Returns canonical form of locale, as used by Dojo.
	//
	//  description:
	//		All variants are case-insensitive and are separated by '-' as specified in RFC 3066.
	//		If no locale is specified, the dojo.locale is returned.  dojo.locale is defined by
	//		the user agent's locale unless overridden by djConfig.

	var result = locale ? locale.toLowerCase() : dojo.locale;
	if(result == "root"){
		result = "ROOT";
	}
	return result; // String
};

dojo.i18n._requireLocalization = function(/*String*/moduleName, /*String*/bundleName, /*String?*/locale, /*String?*/availableFlatLocales){
	//	summary:
	//		See dojo.requireLocalization()
	//	description:
	// 		Called by the bootstrap, but factored out so that it is only
	// 		included in the build when needed.

	var targetLocale = dojo.i18n.normalizeLocale(locale);
 	var bundlePackage = [moduleName, "nls", bundleName].join(".");
	// NOTE: 
	//		When loading these resources, the packaging does not match what is
	//		on disk.  This is an implementation detail, as this is just a
	//		private data structure to hold the loaded resources.  e.g.
	//		tests/hello/nls/en-us/salutations.js is loaded as the object
	//		tests.hello.nls.salutations.en_us={...} The structure on disk is
	//		intended to be most convenient for developers and translators, but
	//		in memory it is more logical and efficient to store in a different
	//		order.  Locales cannot use dashes, since the resulting path will
	//		not evaluate as valid JS, so we translate them to underscores.
	
	//Find the best-match locale to load if we have available flat locales.
	var bestLocale = "";
	if(availableFlatLocales){
		var flatLocales = availableFlatLocales.split(",");
		for(var i = 0; i < flatLocales.length; i++){
			//Locale must match from start of string.
			if(targetLocale.indexOf(flatLocales[i]) == 0){
				if(flatLocales[i].length > bestLocale.length){
					bestLocale = flatLocales[i];
				}
			}
		}
		if(!bestLocale){
			bestLocale = "ROOT";
		}		
	}

	//See if the desired locale is already loaded.
	var tempLocale = availableFlatLocales ? bestLocale : targetLocale;
	var bundle = dojo._loadedModules[bundlePackage];
	var localizedBundle = null;
	if(bundle){
		if(djConfig.localizationComplete && bundle._built){return;}
		var jsLoc = tempLocale.replace(/-/g, '_');
		var translationPackage = bundlePackage+"."+jsLoc;
		localizedBundle = dojo._loadedModules[translationPackage];
	}

	if(!localizedBundle){
		bundle = dojo["provide"](bundlePackage);
		var syms = dojo._getModuleSymbols(moduleName);
		var modpath = syms.concat("nls").join("/");
		var parent;

		dojo.i18n._searchLocalePath(tempLocale, availableFlatLocales, function(loc){
			var jsLoc = loc.replace(/-/g, '_');
			var translationPackage = bundlePackage + "." + jsLoc;
			var loaded = false;
			if(!dojo._loadedModules[translationPackage]){
				// Mark loaded whether it's found or not, so that further load attempts will not be made
				dojo["provide"](translationPackage);
				var module = [modpath];
				if(loc != "ROOT"){module.push(loc);}
				module.push(bundleName);
				var filespec = module.join("/") + '.js';
				loaded = dojo._loadPath(filespec, null, function(hash){
					// Use singleton with prototype to point to parent bundle, then mix-in result from loadPath
					var clazz = function(){};
					clazz.prototype = parent;
					bundle[jsLoc] = new clazz();
					for(var j in hash){ bundle[jsLoc][j] = hash[j]; }
				});
			}else{
				loaded = true;
			}
			if(loaded && bundle[jsLoc]){
				parent = bundle[jsLoc];
			}else{
				bundle[jsLoc] = parent;
			}
			
			if(availableFlatLocales){
				//Stop the locale path searching if we know the availableFlatLocales, since
				//the first call to this function will load the only bundle that is needed.
				return true;
			}
		});
	}

	//Save the best locale bundle as the target locale bundle when we know the
	//the available bundles.
	if(availableFlatLocales && targetLocale != bestLocale){
		bundle[targetLocale.replace(/-/g, '_')] = bundle[bestLocale.replace(/-/g, '_')];
	}
};

(function(){
	// If other locales are used, dojo.requireLocalization should load them as
	// well, by default. 
	// 
	// Override dojo.requireLocalization to do load the default bundle, then
	// iterate through the extraLocale list and load those translations as
	// well, unless a particular locale was requested.

	var extra = djConfig.extraLocale;
	if(extra){
		if(!extra instanceof Array){
			extra = [extra];
		}

		var req = dojo.i18n._requireLocalization;
		dojo.i18n._requireLocalization = function(m, b, locale, availableFlatLocales){
			req(m,b,locale, availableFlatLocales);
			if(locale){return;}
			for(var i=0; i<extra.length; i++){
				req(m,b,extra[i], availableFlatLocales);
			}
		};
	}
})();

dojo.i18n._searchLocalePath = function(/*String*/locale, /*Boolean*/down, /*Function*/searchFunc){
	//	summary:
	//		A helper method to assist in searching for locale-based resources.
	//		Will iterate through the variants of a particular locale, either up
	//		or down, executing a callback function.  For example, "en-us" and
	//		true will try "en-us" followed by "en" and finally "ROOT".

	locale = dojo.i18n.normalizeLocale(locale);

	var elements = locale.split('-');
	var searchlist = [];
	for(var i = elements.length; i > 0; i--){
		searchlist.push(elements.slice(0, i).join('-'));
	}
	searchlist.push(false);
	if(down){searchlist.reverse();}

	for(var j = searchlist.length - 1; j >= 0; j--){
		var loc = searchlist[j] || "ROOT";
		var stop = searchFunc(loc);
		if(stop){ break; }
	}
};

dojo.i18n._preloadLocalizations = function(/*String*/bundlePrefix, /*Array*/localesGenerated){
	//	summary:
	//		Load built, flattened resource bundles, if available for all
	//		locales used in the page. Only called by built layer files.

	function preload(locale){
		locale = dojo.i18n.normalizeLocale(locale);
		dojo.i18n._searchLocalePath(locale, true, function(loc){
			for(var i=0; i<localesGenerated.length;i++){
				if(localesGenerated[i] == loc){
					dojo["require"](bundlePrefix+"_"+loc);
					return true; // Boolean
				}
			}
			return false; // Boolean
		});
	}
	preload();
	var extra = djConfig.extraLocale||[];
	for(var i=0; i<extra.length; i++){
		preload(extra[i]);
	}
};

}

if(!dojo._hasResource["dojo.cldr.supplemental"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.cldr.supplemental"] = true;
dojo.provide("dojo.cldr.supplemental");



dojo.cldr.supplemental.getFirstDayOfWeek = function(/*String?*/locale){
// summary: Returns a zero-based index for first day of the week
// description:
//		Returns a zero-based index for first day of the week, as used by the local (Gregorian) calendar.
//		e.g. Sunday (returns 0), or Monday (returns 1)

	// from http://www.unicode.org/cldr/data/common/supplemental/supplementalData.xml:supplementalData/weekData/firstDay
	var firstDay = {/*default is 1=Monday*/
		mv:5,
		ae:6,af:6,bh:6,dj:6,dz:6,eg:6,er:6,et:6,iq:6,ir:6,jo:6,ke:6,kw:6,lb:6,ly:6,ma:6,om:6,qa:6,sa:6,
		sd:6,so:6,tn:6,ye:6,
		as:0,au:0,az:0,bw:0,ca:0,cn:0,fo:0,ge:0,gl:0,gu:0,hk:0,ie:0,il:0,is:0,jm:0,jp:0,kg:0,kr:0,la:0,
		mh:0,mo:0,mp:0,mt:0,nz:0,ph:0,pk:0,sg:0,th:0,tt:0,tw:0,um:0,us:0,uz:0,vi:0,za:0,zw:0,
		et:0,mw:0,ng:0,tj:0,
		gb:0,
		sy:4
	};

	var country = dojo.cldr.supplemental._region(locale);
	var dow = firstDay[country];
	return (typeof dow == 'undefined') ? 1 : dow; /*Number*/
};

dojo.cldr.supplemental._region = function(/*String?*/locale){
	locale = dojo.i18n.normalizeLocale(locale);
	var tags = locale.split('-');
	var region = tags[1];
	if(!region){
		// IE often gives language only (#2269)
		// Arbitrary mappings of language-only locales to a country:
        region = {de:"de", en:"us", es:"es", fi:"fi", fr:"fr", hu:"hu", it:"it",
        ja:"jp", ko:"kr", nl:"nl", pt:"br", sv:"se", zh:"cn"}[tags[0]];
	}else if(region.length == 4){
		// The ISO 3166 country code is usually in the second position, unless a
		// 4-letter script is given. See http://www.ietf.org/rfc/rfc4646.txt
		region = tags[2];
	}
	return region;
}

dojo.cldr.supplemental.getWeekend = function(/*String?*/locale){
// summary: Returns a hash containing the start and end days of the weekend
// description:
//		Returns a hash containing the start and end days of the weekend according to local custom using locale,
//		or by default in the user's locale.
//		e.g. {start:6, end:0}

	// from http://www.unicode.org/cldr/data/common/supplemental/supplementalData.xml:supplementalData/weekData/weekend{Start,End}
	var weekendStart = {/*default is 6=Saturday*/
		eg:5,il:5,sy:5,
		'in':0,
		ae:4,bh:4,dz:4,iq:4,jo:4,kw:4,lb:4,ly:4,ma:4,om:4,qa:4,sa:4,sd:4,tn:4,ye:4		
	};

	var weekendEnd = {/*default is 0=Sunday*/
		ae:5,bh:5,dz:5,iq:5,jo:5,kw:5,lb:5,ly:5,ma:5,om:5,qa:5,sa:5,sd:5,tn:5,ye:5,af:5,ir:5,
		eg:6,il:6,sy:6
	};

	var country = dojo.cldr.supplemental._region(locale);
	var start = weekendStart[country];
	var end = weekendEnd[country];
	if(typeof start == 'undefined'){start=6;}
	if(typeof end == 'undefined'){end=0;}
	return {start:start, end:end}; /*Object {start,end}*/
};

}

if(!dojo._hasResource["dojo.date"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.date"] = true;
dojo.provide("dojo.date");

dojo.date.getDaysInMonth = function(/*Date*/dateObject){
	//	summary:
	//		Returns the number of days in the month used by dateObject
	var month = dateObject.getMonth();
	var days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if(month == 1 && dojo.date.isLeapYear(dateObject)){ return 29; } // Number
	return days[month]; // Number
}

dojo.date.isLeapYear = function(/*Date*/dateObject){
	//	summary:
	//		Determines if the year of the dateObject is a leap year
	//	description:
	//		Leap years are years with an additional day YYYY-02-29, where the
	//		year number is a multiple of four with the following exception: If
	//		a year is a multiple of 100, then it is only a leap year if it is
	//		also a multiple of 400. For example, 1900 was not a leap year, but
	//		2000 is one.

	var year = dateObject.getFullYear();
	return !(year%400) || (!(year%4) && !!(year%100)); // Boolean
}

// FIXME: This is not localized
dojo.date.getTimezoneName = function(/*Date*/dateObject){
	//	summary:
	//		Get the user's time zone as provided by the browser
	// dateObject:
	//		Needed because the timezone may vary with time (daylight savings)
	//	description:
	//		Try to get time zone info from toString or toLocaleString method of
	//		the Date object -- UTC offset is not a time zone.  See
	//		http://www.twinsun.com/tz/tz-link.htm Note: results may be
	//		inconsistent across browsers.

	var str = dateObject.toString(); // Start looking in toString
	var tz = ''; // The result -- return empty string if nothing found
	var match;

	// First look for something in parentheses -- fast lookup, no regex
	var pos = str.indexOf('(');
	if(pos > -1){
		tz = str.substring(++pos, str.indexOf(')'));
	}else{
		// If at first you don't succeed ...
		// If IE knows about the TZ, it appears before the year
		// Capital letters or slash before a 4-digit year 
		// at the end of string
		var pat = /([A-Z\/]+) \d{4}$/;
		if((match = str.match(pat))){
			tz = match[1];
		}else{
		// Some browsers (e.g. Safari) glue the TZ on the end
		// of toLocaleString instead of putting it in toString
			str = dateObject.toLocaleString();
			// Capital letters or slash -- end of string, 
			// after space
			pat = / ([A-Z\/]+)$/;
			if((match = str.match(pat))){
				tz = match[1];
			}
		}
	}

	// Make sure it doesn't somehow end up return AM or PM
	return (tz == 'AM' || tz == 'PM') ? '' : tz; // String
}

// Utility methods to do arithmetic calculations with Dates

dojo.date.compare = function(/*Date*/date1, /*Date?*/date2, /*String?*/portion){
	//	summary:
	//		Compare two date objects by date, time, or both.
	//	description:
	//  	Returns 0 if equal, positive if a > b, else negative.
	//	date1:
	//		Date object
	//	date2:
	//		Date object.  If not specified, the current Date is used.
	//	portion:
	//		A string indicating the "date" or "time" portion of a Date object.
	//		Compares both "date" and "time" by default.  One of the following:
	//		"date", "time", "datetime"

	// Extra step required in copy for IE - see #3112
	date1 = new Date(Number(date1));
	date2 = new Date(Number(date2 || new Date()));

	if(typeof portion !== "undefined"){
		if(portion == "date"){
			// Ignore times and compare dates.
			date1.setHours(0, 0, 0, 0);
			date2.setHours(0, 0, 0, 0);
		}else if(portion == "time"){
			// Ignore dates and compare times.
			date1.setFullYear(0, 0, 0);
			date2.setFullYear(0, 0, 0);
		}
	}
	
	if(date1 > date2){ return 1; } // int
	if(date1 < date2){ return -1; } // int
	return 0; // int
};

dojo.date.add = function(/*Date*/date, /*String*/interval, /*int*/amount){
	//	summary:
	//		Add to a Date in intervals of different size, from milliseconds to years
	//	date: Date
	//		Date object to start with
	//	interval:
	//		A string representing the interval.  One of the following:
	//			"year", "month", "day", "hour", "minute", "second",
	//			"millisecond", "quarter", "week", "weekday"
	//	amount:
	//		How much to add to the date.

	var sum = new Date(Number(date)); // convert to Number before copying to accomodate IE (#3112)
	var fixOvershoot = false;
	var property = "Date";

	switch(interval){
		case "day":
			break;
		case "weekday":
			//i18n FIXME: assumes Saturday/Sunday weekend, but even this is not standard.  There are CLDR entries to localize this.
			var days, weeks;
			var adj = 0;
			// Divide the increment time span into weekspans plus leftover days
			// e.g., 8 days is one 5-day weekspan / and two leftover days
			// Can't have zero leftover days, so numbers divisible by 5 get
			// a days value of 5, and the remaining days make up the number of weeks
			var mod = amount % 5;
			if(!mod){
				days = (amount > 0) ? 5 : -5;
				weeks = (amount > 0) ? ((amount-5)/5) : ((amount+5)/5);
			}else{
				days = mod;
				weeks = parseInt(amount/5);
			}
			// Get weekday value for orig date param
			var strt = date.getDay();
			// Orig date is Sat / positive incrementer
			// Jump over Sun
			if(strt == 6 && amount > 0){
				adj = 1;
			}else if(strt == 0 && amount < 0){
			// Orig date is Sun / negative incrementer
			// Jump back over Sat
				adj = -1;
			}
			// Get weekday val for the new date
			var trgt = strt + days;
			// New date is on Sat or Sun
			if(trgt == 0 || trgt == 6){
				adj = (amount > 0) ? 2 : -2;
			}
			// Increment by number of weeks plus leftover days plus
			// weekend adjustments
			amount = 7 * weeks + days + adj;
			break;
		case "year":
			property = "FullYear";
			// Keep increment/decrement from 2/29 out of March
			fixOvershoot = true;
			break;
		case "week":
			amount *= 7;
			break;
		case "quarter":
			// Naive quarter is just three months
			amount *= 3;
			// fallthrough...
		case "month":
			// Reset to last day of month if you overshoot
			fixOvershoot = true;
			property = "Month";
			break;
		case "hour":
		case "minute":
		case "second":
		case "millisecond":
			property = interval.charAt(0).toUpperCase() + interval.substring(1) + "s";
	}

	if(property){
		sum["setUTC"+property](sum["getUTC"+property]()+amount);
	}

	if(fixOvershoot && (sum.getDate() < date.getDate())){
		sum.setDate(0);
	}

	return sum; // Date
};

dojo.date.difference = function(/*Date*/date1, /*Date?*/date2, /*String?*/interval){
	//	summary:
	//		Get the difference in a specific unit of time (e.g., number of
	//		months, weeks, days, etc.) between two dates, rounded to the
	//		nearest integer.
	//	date1:
	//		Date object
	//	date2:
	//		Date object.  If not specified, the current Date is used.
	//	interval:
	//		A string representing the interval.  One of the following:
	//			"year", "month", "day", "hour", "minute", "second",
	//			"millisecond", "quarter", "week", "weekday"
	//		Defaults to "day".

	date2 = date2 || new Date();
	interval = interval || "day";
	var yearDiff = date2.getFullYear() - date1.getFullYear();
	var delta = 1; // Integer return value

	switch(interval){
		case "quarter":
			var m1 = date1.getMonth();
			var m2 = date2.getMonth();
			// Figure out which quarter the months are in
			var q1 = Math.floor(m1/3) + 1;
			var q2 = Math.floor(m2/3) + 1;
			// Add quarters for any year difference between the dates
			q2 += (yearDiff * 4);
			delta = q2 - q1;
			break;
		case "weekday":
			var days = Math.round(dojo.date.difference(date1, date2, "day"));
			var weeks = parseInt(dojo.date.difference(date1, date2, "week"));
			var mod = days % 7;

			// Even number of weeks
			if(mod == 0){
				days = weeks*5;
			}else{
				// Weeks plus spare change (< 7 days)
				var adj = 0;
				var aDay = date1.getDay();
				var bDay = date2.getDay();

				weeks = parseInt(days/7);
				mod = days % 7;
				// Mark the date advanced by the number of
				// round weeks (may be zero)
				var dtMark = new Date(date1);
				dtMark.setDate(dtMark.getDate()+(weeks*7));
				var dayMark = dtMark.getDay();

				// Spare change days -- 6 or less
				if(days > 0){
					switch(true){
						// Range starts on Sat
						case aDay == 6:
							adj = -1;
							break;
						// Range starts on Sun
						case aDay == 0:
							adj = 0;
							break;
						// Range ends on Sat
						case bDay == 6:
							adj = -1;
							break;
						// Range ends on Sun
						case bDay == 0:
							adj = -2;
							break;
						// Range contains weekend
						case (dayMark + mod) > 5:
							adj = -2;
					}
				}else if(days < 0){
					switch(true){
						// Range starts on Sat
						case aDay == 6:
							adj = 0;
							break;
						// Range starts on Sun
						case aDay == 0:
							adj = 1;
							break;
						// Range ends on Sat
						case bDay == 6:
							adj = 2;
							break;
						// Range ends on Sun
						case bDay == 0:
							adj = 1;
							break;
						// Range contains weekend
						case (dayMark + mod) < 0:
							adj = 2;
					}
				}
				days += adj;
				days -= (weeks*2);
			}
			delta = days;
			break;
		case "year":
			delta = yearDiff;
			break;
		case "month":
			delta = (date2.getMonth() - date1.getMonth()) + (yearDiff * 12);
			break;
		case "week":
			// Truncate instead of rounding
			// Don't use Math.floor -- value may be negative
			delta = parseInt(dojo.date.difference(date1, date2, "day")/7);
			break;
		case "day":
			delta /= 24;
			// fallthrough
		case "hour":
			delta /= 60;
			// fallthrough
		case "minute":
			delta /= 60;
			// fallthrough
		case "second":
			delta /= 1000;
			// fallthrough
		case "millisecond":
			delta *= date2.getTime() - date1.getTime();
	}

	// Round for fractional values and DST leaps
	return Math.round(delta); // Number (integer)
};

}

if(!dojo._hasResource["dojo.regexp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.regexp"] = true;
dojo.provide("dojo.regexp");

dojo.regexp.escapeString = function(/*String*/str, /*String?*/except){
	//	summary:
	//		Adds escape sequences for special characters in regular expressions
	// except:
	//		a String with special characters to be left unescaped

//	return str.replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1"); // string
	return str.replace(/([\.$?*!=:|{}\(\)\[\]\\\/^])/g, function(ch){
		if(except && except.indexOf(ch) != -1){
			return ch;
		}
		return "\\" + ch;
	}); // String
}

dojo.regexp.buildGroupRE = function(/*Object|Array*/arr, /*Function*/re, /*Boolean?*/nonCapture){
	//	summary:
	//		Builds a regular expression that groups subexpressions
	//	description:
	//		A utility function used by some of the RE generators. The
	//		subexpressions are constructed by the function, re, in the second
	//		parameter.  re builds one subexpression for each elem in the array
	//		a, in the first parameter. Returns a string for a regular
	//		expression that groups all the subexpressions.
	// arr:
	//		A single value or an array of values.
	// re:
	//		A function. Takes one parameter and converts it to a regular
	//		expression. 
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. Defaults to false

	// case 1: a is a single value.
	if(!(arr instanceof Array)){
		return re(arr); // String
	}

	// case 2: a is an array
	var b = [];
	for(var i = 0; i < arr.length; i++){
		// convert each elem to a RE
		b.push(re(arr[i]));
	}

	 // join the REs as alternatives in a RE group.
	return dojo.regexp.group(b.join("|"), nonCapture); // String
}

dojo.regexp.group = function(/*String*/expression, /*Boolean?*/nonCapture){
	// summary:
	//		adds group match to expression
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. 
	return "(" + (nonCapture ? "?:":"") + expression + ")"; // String
}

}

if(!dojo._hasResource["dojo.date.locale"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.date.locale"] = true;
dojo.provide("dojo.date.locale");

// Localization methods for Date.   Honor local customs using locale-dependent dojo.cldr data.







// Load the bundles containing localization information for
// names and formats


//NOTE: Everything in this module assumes Gregorian calendars.
// Other calendars will be implemented in separate modules.

(function(){
	// Format a pattern without literals
	function formatPattern(dateObject, bundle, pattern){
		return pattern.replace(/([a-z])\1*/ig, function(match){
			var s;
			var c = match.charAt(0);
			var l = match.length;
			var pad;
			var widthList = ["abbr", "wide", "narrow"];
			switch(c){
				case 'G':
					s = bundle[(l < 4) ? "eraAbbr" : "eraNames"][dateObject.getFullYear() < 0 ? 0 : 1];
					break;
				case 'y':
					s = dateObject.getFullYear();
					switch(l){
						case 1:
							break;
						case 2:
							s = String(s); s = s.substr(s.length - 2);
							break;
						default:
							pad = true;
					}
					break;
				case 'Q':
				case 'q':
					s = Math.ceil((dateObject.getMonth()+1)/3);
//					switch(l){
//						case 1: case 2:
							pad = true;
//							break;
//						case 3: case 4: // unimplemented
//					}
					break;
				case 'M':
				case 'L':
					var m = dateObject.getMonth();
					var width;
					switch(l){
						case 1: case 2:
							s = m+1; pad = true;
							break;
						case 3: case 4: case 5:
							width = widthList[l-3];
							break;
					}
					if(width){
						var type = (c == "L") ? "standalone" : "format";
						var prop = ["months",type,width].join("-");
						s = bundle[prop][m];
					}
					break;
				case 'w':
					var firstDay = 0;
					s = dojo.date.locale._getWeekOfYear(dateObject, firstDay); pad = true;
					break;
				case 'd':
					s = dateObject.getDate(); pad = true;
					break;
				case 'D':
					s = dojo.date.locale._getDayOfYear(dateObject); pad = true;
					break;
				case 'E':
				case 'e':
				case 'c': // REVIEW: don't see this in the spec?
					var d = dateObject.getDay();
					var width;
					switch(l){
						case 1: case 2:
							if(c == 'e'){
								var first = dojo.cldr.supplemental.getFirstDayOfWeek(options.locale);
								d = (d-first+7)%7;
							}
							if(c != 'c'){
								s = d+1; pad = true;
								break;
							}
							// else fallthrough...
						case 3: case 4: case 5:
							width = widthList[l-3];
							break;
					}
					if(width){
						var type = (c == "c") ? "standalone" : "format";
						var prop = ["days",type,width].join("-");
						s = bundle[prop][d];
					}
					break;
				case 'a':
					var timePeriod = (dateObject.getHours() < 12) ? 'am' : 'pm';
					s = bundle[timePeriod];
					break;
				case 'h':
				case 'H':
				case 'K':
				case 'k':
					var h = dateObject.getHours();
					// strange choices in the date format make it impossible to write this succinctly
					switch (c) {
						case 'h': // 1-12
							s = (h % 12) || 12;
							break;
						case 'H': // 0-23
							s = h;
							break;
						case 'K': // 0-11
							s = (h % 12);
							break;
						case 'k': // 1-24
							s = h || 24;
							break;
					}
					pad = true;
					break;
				case 'm':
					s = dateObject.getMinutes(); pad = true;
					break;
				case 's':
					s = dateObject.getSeconds(); pad = true;
					break;
				case 'S':
					s = Math.round(dateObject.getMilliseconds() * Math.pow(10, l-3));
					break;
				case 'v': // FIXME: don't know what this is. seems to be same as z?
				case 'z':
					// We only have one timezone to offer; the one from the browser
					s = dojo.date.getTimezoneName(dateObject);
					if(s){break;}
					l=4;
					// fallthrough... use GMT if tz not available
				case 'Z':
					var offset = dateObject.getTimezoneOffset();
					var tz = [
						(offset<=0 ? "+" : "-"),
						dojo.string.pad(Math.floor(Math.abs(offset)/60), 2),
						dojo.string.pad(Math.abs(offset)% 60, 2)
					];
					if(l==4){
						tz.splice(0, 0, "GMT");
						tz.splice(3, 0, ":");
					}
					s = tz.join("");
					break;
//				case 'Y': case 'u': case 'W': case 'F': case 'g': case 'A':
//					console.debug(match+" modifier unimplemented");
				default:
					throw new Error("dojo.date.locale.format: invalid pattern char: "+pattern);
			}
			if(pad){ s = dojo.string.pad(s, l); }
			return s;
		});
	}

dojo.date.locale.format = function(/*Date*/dateObject, /*Object?*/options){
	// summary:
	//		Format a Date object as a String, using locale-specific settings.
	//
	// description:
	//		Create a string from a Date object using a known localized pattern.
	//		By default, this method formats both date and time from dateObject.
	//		Formatting patterns are chosen appropriate to the locale.  Different
	//		formatting lengths may be chosen, with "full" used by default.
	//		Custom patterns may be used or registered with translations using
	//		the addCustomFormats method.
	//		Formatting patterns are implemented using the syntax described at
	//		http://www.unicode.org/reports/tr35/tr35-4.html#Date_Format_Patterns
	//
	// dateObject:
	//		the date and/or time to be formatted.  If a time only is formatted,
	//		the values in the year, month, and day fields are irrelevant.  The
	//		opposite is true when formatting only dates.
	//
	// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string}
	//		selector- choice of 'time','date' (default: date and time)
	//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
	//		datePattern,timePattern- override pattern with this string
	//		am,pm- override strings for am/pm in times
	//		locale- override the locale used to determine formatting rules

	options = options || {};

	var locale = dojo.i18n.normalizeLocale(options.locale);
	var formatLength = options.formatLength || 'short';
	var bundle = dojo.date.locale._getGregorianBundle(locale);
	var str = [];
	var sauce = dojo.hitch(this, formatPattern, dateObject, bundle);
	if(options.selector == "year"){
		// Special case as this is not yet driven by CLDR data
		var year = dateObject.getFullYear();
		if(locale.match(/^zh|^ja/)){
			year += "\u5E74";
		}
		return year;
	}
	if(options.selector != "time"){
		var datePattern = options.datePattern || bundle["dateFormat-"+formatLength];
		if(datePattern){str.push(_processPattern(datePattern, sauce));}
	}
	if(options.selector != "date"){
		var timePattern = options.timePattern || bundle["timeFormat-"+formatLength];
		if(timePattern){str.push(_processPattern(timePattern, sauce));}
	}
	var result = str.join(" "); //TODO: use locale-specific pattern to assemble date + time
	return result; // String
};

dojo.date.locale.regexp = function(/*Object?*/options){
	// summary:
	//		Builds the regular needed to parse a localized date
	//
	// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string, strict: boolean}
	//		selector- choice of 'time', 'date' (default: date and time)
	//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
	//		datePattern,timePattern- override pattern with this string
	//		locale- override the locale used to determine formatting rules

	return dojo.date.locale._parseInfo(options).regexp; // String
};

dojo.date.locale._parseInfo = function(/*Object?*/options){
	options = options || {};
	var locale = dojo.i18n.normalizeLocale(options.locale);
	var bundle = dojo.date.locale._getGregorianBundle(locale);
	var formatLength = options.formatLength || 'short';
	var datePattern = options.datePattern || bundle["dateFormat-" + formatLength];
	var timePattern = options.timePattern || bundle["timeFormat-" + formatLength];
	var pattern;
	if(options.selector == 'date'){
		pattern = datePattern;
	}else if(options.selector == 'time'){
		pattern = timePattern;
	}else{
		pattern = datePattern + ' ' + timePattern; //TODO: use locale-specific pattern to assemble date + time
	}

	var tokens = [];
	var re = _processPattern(pattern, dojo.hitch(this, _buildDateTimeRE, tokens, bundle, options));
	return {regexp: re, tokens: tokens, bundle: bundle};
};

dojo.date.locale.parse = function(/*String*/value, /*Object?*/options){
	// summary:
	//		Convert a properly formatted string to a primitive Date object,
	//		using locale-specific settings.
	//
	// description:
	//		Create a Date object from a string using a known localized pattern.
	//		By default, this method parses looking for both date and time in the string.
	//		Formatting patterns are chosen appropriate to the locale.  Different
	//		formatting lengths may be chosen, with "full" used by default.
	//		Custom patterns may be used or registered with translations using
	//		the addCustomFormats method.
	//		Formatting patterns are implemented using the syntax described at
	//		http://www.unicode.org/reports/tr35/#Date_Format_Patterns
	//
	// value:
	//		A string representation of a date
	//
	// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string, strict: boolean}
	//		selector- choice of 'time', 'date' (default: date and time)
	//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'short'
	//		datePattern,timePattern- override pattern with this string
	//		am,pm- override strings for am/pm in times
	//		locale- override the locale used to determine formatting rules
	//		strict- strict parsing, off by default

	var info = dojo.date.locale._parseInfo(options);
	var tokens = info.tokens, bundle = info.bundle;
	var re = new RegExp("^" + info.regexp + "$");
	var match = re.exec(value);
	if(!match){ return null; } // null

	var widthList = ['abbr', 'wide', 'narrow'];
	//1972 is a leap year.  We want to avoid Feb 29 rolling over into Mar 1,
	//in the cases where the year is parsed after the month and day.
	var result = new Date(1972, 0);
	var expected = {};
	var amPm = "";
	dojo.forEach(match, function(v, i){
		if(!i){return;}
		var token=tokens[i-1];
		var l=token.length;
		switch(token.charAt(0)){
			case 'y':
				if(l != 2){
					//interpret year literally, so '5' would be 5 A.D.
					result.setFullYear(v);
					expected.year = v;
				}else{
					if(v<100){
						v = Number(v);
						//choose century to apply, according to a sliding window
						//of 80 years before and 20 years after present year
						var year = '' + new Date().getFullYear();
						var century = year.substring(0, 2) * 100;
						var yearPart = Number(year.substring(2, 4));
						var cutoff = Math.min(yearPart + 20, 99);
						var num = (v < cutoff) ? century + v : century - 100 + v;
						result.setFullYear(num);
						expected.year = num;
					}else{
						//we expected 2 digits and got more...
						if(options.strict){
							return null;
						}
						//interpret literally, so '150' would be 150 A.D.
						//also tolerate '1950', if 'yyyy' input passed to 'yy' format
						result.setFullYear(v);
						expected.year = v;
					}
				}
				break;
			case 'M':
				if(l>2){
					var months = bundle['months-format-' + widthList[l-3]].concat();
					if(!options.strict){
						//Tolerate abbreviating period in month part
						//Case-insensitive comparison
						v = v.replace(".","").toLowerCase();
						months = dojo.map(months, function(s){ return s.replace(".","").toLowerCase(); } );
					}
					v = dojo.indexOf(months, v);
					if(v == -1){
//						console.debug("dojo.date.locale.parse: Could not parse month name: '" + v + "'.");
						return null;
					}
				}else{
					v--;
				}
				result.setMonth(v);
				expected.month = v;
				break;
			case 'E':
			case 'e':
				var days = bundle['days-format-' + widthList[l-3]].concat();
				if(!options.strict){
					//Case-insensitive comparison
					v = v.toLowerCase();
					days = dojo.map(days, "".toLowerCase);
				}
				v = dojo.indexOf(days, v);
				if(v == -1){
//					console.debug("dojo.date.locale.parse: Could not parse weekday name: '" + v + "'.");
					return null;
				}

				//TODO: not sure what to actually do with this input,
				//in terms of setting something on the Date obj...?
				//without more context, can't affect the actual date
				//TODO: just validate?
				break;
			case 'd':
				result.setDate(v);
				expected.date = v;
				break;
			case 'D':
				//FIXME: need to defer this until after the year is set for leap-year?
				result.setMonth(0);
				result.setDate(v);
				break;
			case 'a': //am/pm
				var am = options.am || bundle.am;
				var pm = options.pm || bundle.pm;
				if(!options.strict){
					var period = /\./g;
					v = v.replace(period,'').toLowerCase();
					am = am.replace(period,'').toLowerCase();
					pm = pm.replace(period,'').toLowerCase();
				}
				if(options.strict && v != am && v != pm){
//					console.debug("dojo.date.locale.parse: Could not parse am/pm part.");
					return null;
				}

				// we might not have seen the hours field yet, so store the state and apply hour change later
				amPm = (v == pm) ? 'p' : (v == am) ? 'a' : '';
				break;
			case 'K': //hour (1-24)
				if(v==24){v=0;}
				// fallthrough...
			case 'h': //hour (1-12)
			case 'H': //hour (0-23)
			case 'k': //hour (0-11)
				//TODO: strict bounds checking, padding
				if(v > 23){
//					console.debug("dojo.date.locale.parse: Illegal hours value");
					return null;
				}

				//in the 12-hour case, adjusting for am/pm requires the 'a' part
				//which could come before or after the hour, so we will adjust later
				result.setHours(v);
				break;
			case 'm': //minutes
				result.setMinutes(v);
				break;
			case 's': //seconds
				result.setSeconds(v);
				break;
			case 'S': //milliseconds
				result.setMilliseconds(v);
//				break;
//			case 'w':
//TODO				var firstDay = 0;
//			default:
//TODO: throw?
//				console.debug("dojo.date.locale.parse: unsupported pattern char=" + token.charAt(0));
		}
	});

	var hours = result.getHours();
	if(amPm === 'p' && hours < 12){
		result.setHours(hours + 12); //e.g., 3pm -> 15
	}else if(amPm === 'a' && hours == 12){
		result.setHours(0); //12am -> 0
	}

	//validate parse date fields versus input date fields
	if(expected.year && result.getFullYear() != expected.year){
//		console.debug("dojo.date.locale.parse: Parsed year: '" + result.getFullYear() + "' did not match input year: '" + expected.year + "'.");
		return null;
	}
	if(expected.month && result.getMonth() != expected.month){
//		console.debug("dojo.date.locale.parse: Parsed month: '" + result.getMonth() + "' did not match input month: '" + expected.month + "'.");
		return null;
	}
	if(expected.date && result.getDate() != expected.date){
//		console.debug("dojo.date.locale.parse: Parsed day of month: '" + result.getDate() + "' did not match input day of month: '" + expected.date + "'.");
		return null;
	}

	//TODO: implement a getWeekday() method in order to test 
	//validity of input strings containing 'EEE' or 'EEEE'...
	return result; // Date
};

function _processPattern(pattern, applyPattern, applyLiteral, applyAll){
	//summary: Process a pattern with literals in it

	// Break up on single quotes, treat every other one as a literal, except '' which becomes '
	var identity = function(x){return x;};
	applyPattern = applyPattern || identity;
	applyLiteral = applyLiteral || identity;
	applyAll = applyAll || identity;

	//split on single quotes (which escape literals in date format strings) 
	//but preserve escaped single quotes (e.g., o''clock)
	var chunks = pattern.match(/(''|[^'])+/g); 
	var literal = false;

	dojo.forEach(chunks, function(chunk, i){
		if(!chunk){
			chunks[i]='';
		}else{
			chunks[i]=(literal ? applyLiteral : applyPattern)(chunk);
			literal = !literal;
		}
	});
	return applyAll(chunks.join(''));
}

function _buildDateTimeRE(tokens, bundle, options, pattern){
	pattern = dojo.regexp.escapeString(pattern);
	if(!options.strict){ pattern = pattern.replace(" a", " ?a"); } // kludge to tolerate no space before am/pm
	return pattern.replace(/([a-z])\1*/ig, function(match){
		// Build a simple regexp.  Avoid captures, which would ruin the tokens list
		var s;
		var c = match.charAt(0);
		var l = match.length;
		var p2 = '', p3 = '';
		if(options.strict){
			if(l > 1){ p2 = '0' + '{'+(l-1)+'}'; }
			if(l > 2){ p3 = '0' + '{'+(l-2)+'}'; }
		}else{
			p2 = '0?'; p3 = '0{0,2}';
		}
		switch(c){
			case 'y':
				s = '\\d{2,4}';
				break;
			case 'M':
				s = (l>2) ? '\\S+' : p2+'[1-9]|1[0-2]';
				break;
			case 'D':
				s = p2+'[1-9]|'+p3+'[1-9][0-9]|[12][0-9][0-9]|3[0-5][0-9]|36[0-6]';
				break;
			case 'd':
				s = p2+'[1-9]|[12]\\d|3[01]';
				break;
			case 'w':
				s = p2+'[1-9]|[1-4][0-9]|5[0-3]';
				break;
		    case 'E':
				s = '\\S+';
				break;
			case 'h': //hour (1-12)
				s = p2+'[1-9]|1[0-2]';
				break;
			case 'k': //hour (0-11)
				s = p2+'\\d|1[01]';
				break;
			case 'H': //hour (0-23)
				s = p2+'\\d|1\\d|2[0-3]';
				break;
			case 'K': //hour (1-24)
				s = p2+'[1-9]|1\\d|2[0-4]';
				break;
			case 'm':
			case 's':
				s = '[0-5]\\d';
				break;
			case 'S':
				s = '\\d{'+l+'}';
				break;
			case 'a':
				var am = options.am || bundle.am || 'AM';
				var pm = options.pm || bundle.pm || 'PM';
				if(options.strict){
					s = am + '|' + pm;
				}else{
					s = am + '|' + pm;
					if(am != am.toLowerCase()){ s += '|' + am.toLowerCase(); }
					if(pm != pm.toLowerCase()){ s += '|' + pm.toLowerCase(); }
				}
				break;
			default:
			// case 'v':
			// case 'z':
			// case 'Z':
				s = ".*";
//				console.debug("parse of date format, pattern=" + pattern);
		}

		if(tokens){ tokens.push(match); }

		return "(" + s + ")"; // add capture
	}).replace(/[\xa0 ]/g, "[\\s\\xa0]"); // normalize whitespace.  Need explicit handling of \xa0 for IE.
}
})();

(function(){
var _customFormats = [];
dojo.date.locale.addCustomFormats = function(/*String*/packageName, /*String*/bundleName){
	// summary:
	//		Add a reference to a bundle containing localized custom formats to be
	//		used by date/time formatting and parsing routines.
	//
	// description:
	//		The user may add custom localized formats where the bundle has properties following the
	//		same naming convention used by dojo for the CLDR data: dateFormat-xxxx / timeFormat-xxxx
	//		The pattern string should match the format used by the CLDR.
	//		See dojo.date.format for details.
	//		The resources must be loaded by dojo.requireLocalization() prior to use

	_customFormats.push({pkg:packageName,name:bundleName});
};

dojo.date.locale._getGregorianBundle = function(/*String*/locale){
	var gregorian = {};
	dojo.forEach(_customFormats, function(desc){
		var bundle = dojo.i18n.getLocalization(desc.pkg, desc.name, locale);
		gregorian = dojo.mixin(gregorian, bundle);
	}, this);
	return gregorian; /*Object*/
};
})();

dojo.date.locale.addCustomFormats("dojo.cldr","gregorian");

dojo.date.locale.getNames = function(/*String*/item, /*String*/type, /*String?*/use, /*String?*/locale){
	// summary:
	//		Used to get localized strings from dojo.cldr for day or month names.
	//
	// item: 'months' || 'days'
	// type: 'wide' || 'narrow' || 'abbr' (e.g. "Monday", "Mon", or "M" respectively, in English)
	// use: 'standAlone' || 'format' (default)
	// locale: override locale used to find the names

	var label;
	var lookup = dojo.date.locale._getGregorianBundle(locale);
	var props = [item, use, type];
	if(use == 'standAlone'){
		label = lookup[props.join('-')];
	}
	props[1] = 'format';

	// return by copy so changes won't be made accidentally to the in-memory model
	return (label || lookup[props.join('-')]).concat(); /*Array*/
};

dojo.date.locale.isWeekend = function(/*Date?*/dateObject, /*String?*/locale){
	// summary:
	//	Determines if the date falls on a weekend, according to local custom.

	var weekend = dojo.cldr.supplemental.getWeekend(locale);
	var day = (dateObject || new Date()).getDay();
	if(weekend.end < weekend.start){
		weekend.end += 7;
		if(day < weekend.start){ day += 7; }
	}
	return day >= weekend.start && day <= weekend.end; // Boolean
};

// These are used only by format and strftime.  Do they need to be public?  Which module should they go in?

dojo.date.locale._getDayOfYear = function(/*Date*/dateObject){
	// summary: gets the day of the year as represented by dateObject
	return dojo.date.difference(new Date(dateObject.getFullYear(), 0, 1), dateObject) + 1; // Number
};

dojo.date.locale._getWeekOfYear = function(/*Date*/dateObject, /*Number*/firstDayOfWeek){
	if(arguments.length == 1){ firstDayOfWeek = 0; } // Sunday

	var firstDayOfYear = new Date(dateObject.getFullYear(), 0, 1).getDay();
	var adj = (firstDayOfYear - firstDayOfWeek + 7) % 7;
	var week = Math.floor((dojo.date.locale._getDayOfYear(dateObject) + adj - 1) / 7);

	// if year starts on the specified day, start counting weeks at 1
	if(firstDayOfYear == firstDayOfWeek){ week++; }

	return week; // Number
};

}

if(!dojo._hasResource["dijit._Calendar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._Calendar"] = true;
dojo.provide("dijit._Calendar");








dojo.declare(
	"dijit._Calendar",
	[dijit._Widget, dijit._Templated],
	{
		/*
		summary:
			A simple GUI for choosing a date in the context of a monthly calendar.

		description:
			This widget is used internally by other widgets and is not accessible
			as a standalone widget.
			This widget can't be used in a form because it doesn't serialize the date to an
			<input> field.  For a form element, use DateTextBox instead.

			Note that the parser takes all dates attributes passed in the `RFC 3339` format:
			http://www.faqs.org/rfcs/rfc3339.html (2005-06-30T08:05:00-07:00)
			so that they are serializable and locale-independent.

		usage:
			var calendar = new dijit._Calendar({}, dojo.byId("calendarNode"));
		 	-or-
			<div dojoType="dijit._Calendar"></div>
		*/
		templatePath: dojo.moduleUrl("dijit", "templates/Calendar.html"),

		// value: Date
		// the currently selected Date
		value: new Date(),

		// dayWidth: String
		// How to represent the days of the week in the calendar header. See dojo.date.locale
		dayWidth: "narrow",

		setValue: function(/*Date*/ value){
			// summary: set the current date and update the UI.  If the date is disabled, the selection will
			//	not change, but the display will change to the corresponding month.
			if(!this.value || dojo.date.compare(value, this.value)){
				value = new Date(value);
				this.displayMonth = new Date(value);
				if(!this.isDisabledDate(value, this.lang)){
					this.value = value;
					this.value.setHours(0,0,0,0);
					this.onChange(this.value);
				}
				this._populateGrid();
			}
		},

		_setText: function(node, text){
			while(node.firstChild){
				node.removeChild(node.firstChild);
			}
			node.appendChild(document.createTextNode(text));
		},

		_populateGrid: function(){
			var month = this.displayMonth;
			month.setDate(1);
			var firstDay = month.getDay();
			var daysInMonth = dojo.date.getDaysInMonth(month);
			var daysInPreviousMonth = dojo.date.getDaysInMonth(dojo.date.add(month, "month", -1));
			var today = new Date();
			var selected = this.value;

			var dayOffset = dojo.cldr.supplemental.getFirstDayOfWeek(this.lang);
			if(dayOffset > firstDay){ dayOffset -= 7; }

			// Iterate through dates in the calendar and fill in date numbers and style info
			dojo.query(".dijitCalendarDateTemplate", this.domNode).forEach(function(template, i){
				i += dayOffset;
				var date = new Date(month);
				var number, clazz = "dijitCalendar", adj = 0;

				if(i < firstDay){
					number = daysInPreviousMonth - firstDay + i + 1;
					adj = -1;
					clazz += "Previous";
				}else if(i >= (firstDay + daysInMonth)){
					number = i - firstDay - daysInMonth + 1;
					adj = 1;
					clazz += "Next";
				}else{
					number = i - firstDay + 1;
					clazz += "Current";
				}

				if(adj){
					date = dojo.date.add(date, "month", adj);
				}
				date.setDate(number);

				if(!dojo.date.compare(date, today, "date")){
					clazz = "dijitCalendarCurrentDate " + clazz;
				}

				if(!dojo.date.compare(date, selected, "date")){
					clazz = "dijitCalendarSelectedDate " + clazz;
				}

				if(this.isDisabledDate(date, this.lang)){
					clazz = "dijitCalendarDisabledDate " + clazz;
				}

				template.className =  clazz + "Month dijitCalendarDateTemplate";
				template.dijitDateValue = date.valueOf();
				var label = dojo.query(".dijitCalendarDateLabel", template)[0];
				this._setText(label, date.getDate());
			}, this);

			// Fill in localized month name
			var monthNames = dojo.date.locale.getNames('months', 'wide', 'standAlone', this.lang);
			this._setText(this.monthLabelNode, monthNames[month.getMonth()]);

			// Fill in localized prev/current/next years
			var y = month.getFullYear() - 1;
			dojo.forEach(["previous", "current", "next"], function(name){
				this._setText(this[name+"YearLabelNode"],
					dojo.date.locale.format(new Date(y++, 0), {selector:'year', locale:this.lang}));
			}, this);

			// Set up repeating mouse behavior
			var _this = this;
			var typematic = function(nodeProp, dateProp, adj){
				dijit.typematic.addMouseListener(_this[nodeProp], _this, function(count){
					if(count >= 0){ _this._adjustDisplay(dateProp, adj); }
				}, 0.8, 500);
			};
			typematic("incrementMonth", "month", 1);
			typematic("decrementMonth", "month", -1);
			typematic("nextYearLabelNode", "year", 1);
			typematic("previousYearLabelNode", "year", -1);
		},

		postCreate: function(){
			dijit._Calendar.superclass.postCreate.apply(this);

			var cloneClass = dojo.hitch(this, function(clazz, n){
				var template = dojo.query(clazz, this.domNode)[0];
	 			for(var i=0; i<n; i++){
					template.parentNode.appendChild(template.cloneNode(true));
				}
			});

			// clone the day label and calendar day templates 6 times to make 7 columns
			cloneClass(".dijitCalendarDayLabelTemplate", 6);
			cloneClass(".dijitCalendarDateTemplate", 6);

			// now make 6 week rows
			cloneClass(".dijitCalendarWeekTemplate", 5);

			// insert localized day names in the header
			var dayNames = dojo.date.locale.getNames('days', this.dayWidth, 'standAlone', this.lang);
			var dayOffset = dojo.cldr.supplemental.getFirstDayOfWeek(this.lang);
			dojo.query(".dijitCalendarDayLabel", this.domNode).forEach(function(label, i){
				this._setText(label, dayNames[(i + dayOffset) % 7]);
			}, this);

			// Fill in spacer element with all the month names (invisible) so that the maximum width will affect layout
			var monthNames = dojo.date.locale.getNames('months', 'wide', 'standAlone', this.lang);
			dojo.forEach(monthNames, function(name){
				var monthSpacer = dojo.doc.createElement("div");
				this._setText(monthSpacer, name);
				this.monthLabelSpacer.appendChild(monthSpacer);
			}, this);

			this.value = null;
			this.setValue(new Date());
		},

		_adjustDisplay: function(/*String*/part, /*int*/amount){
			this.displayMonth = dojo.date.add(this.displayMonth, part, amount);
			this._populateGrid();
		},

		_onDayClick: function(/*Event*/evt){
			var node = evt.target;
			dojo.stopEvent(evt);
			while(!node.dijitDateValue){
				node = node.parentNode;
			}
			if(!dojo.hasClass(node, "dijitCalendarDisabledDate")){
				this.setValue(node.dijitDateValue);
				this.onValueSelected(this.value);
			}
		},

		onValueSelected: function(/*Date*/date){
			//summary: a date cell was selected.  It may be the same as the previous value.
		},

		onChange: function(/*Date*/date){
			//summary: called only when the selected date has changed
		},

		isDisabledDate: function(/*Date*/dateObject, /*String?*/locale){
			// summary:
			//	May be overridden to disable certain dates in the calendar e.g. isDisabledDate=dojo.date.locale.isWeekend
			return false; // Boolean
		}
	}
);

}



if(!dojo._hasResource["dijit.layout.ContentPane"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.layout.ContentPane"] = true;
dojo.provide("dijit.layout.ContentPane");








dojo.declare(
	"dijit.layout.ContentPane",
	dijit._Widget,
{
	// summary:
	//		A widget that acts as a Container for other widgets, and includes a ajax interface
	// description:
	//		A widget that can be used as a standalone widget
	//		or as a baseclass for other widgets
	//		Handles replacement of document fragment using either external uri or javascript
	//		generated markup or DOM content, instantiating widgets within that content.
	//		Don't confuse it with an iframe, it only needs/wants document fragments.
	//		It's useful as a child of LayoutContainer, SplitContainer, or TabContainer.
	//		But note that those classes can contain any widget as a child.
	// example:
	//		Some quick samples:
	//		To change the innerHTML use .setContent('<b>new content</b>')
	//
	//		Or you can send it a NodeList, .setContent(dojo.query('div [class=selected]', userSelection))
	//		please note that the nodes in NodeList will copied, not moved
	//
	//		To do a ajax update use .setHref('url')
	//
	// href: String
	//		The href of the content that displays now.
	//		Set this at construction if you want to load data externally when the
	//		pane is shown.  (Set preload=true to load it immediately.)
	//		Changing href after creation doesn't have any effect; see setHref();
	href: "",

	// extractContent: Boolean
	//	Extract visible content from inside of <body> .... </body>
	extractContent: false,

	// parseOnLoad: Boolean
	//	parse content and create the widgets, if any
	parseOnLoad:	true,

	// preventCache: Boolean
	//		Cache content retreived externally
	preventCache:	false,

	// preload: Boolean
	//	Force load of data even if pane is hidden.
	preload: false,

	// refreshOnShow: Boolean
	//		Refresh (re-download) content when pane goes from hidden to shown
	refreshOnShow: false,

	// loadingMessage: String
	//	Message that shows while downloading
	loadingMessage: "<span class='dijitContentPaneLoading'>${loadingState}</span>", 

	// errorMessage: String
	//	Message that shows if an error occurs
	errorMessage: "<span class='dijitContentPaneError'>${errorState}</span>", 

	// isLoaded: Boolean
	//	Tells loading status see onLoad|onUnload for event hooks
	isLoaded: false,

	// class: String
	//	Class name to apply to ContentPane dom nodes
	"class": "dijitContentPane",

	postCreate: function(){
		// remove the title attribute so it doesn't show up when i hover
		// over a node
		this.domNode.title = "";

		if(this.preload){
			this._loadCheck();
		}

		var messages = dojo.i18n.getLocalization("dijit", "loading", this.lang);
		this.loadingMessage = dojo.string.substitute(this.loadingMessage, messages);
		this.errorMessage = dojo.string.substitute(this.errorMessage, messages);

		// for programatically created ContentPane (with <span> tag), need to muck w/CSS
		// or it's as though overflow:visible is set
		dojo.addClass(this.domNode, this["class"]);
	},

	startup: function(){
		if(this._started){ return; }
		this._checkIfSingleChild();
		if(this._singleChild){
			this._singleChild.startup();
		}
		this._loadCheck();
		this._started = true;
	},

	_checkIfSingleChild: function(){
		// summary:
		// 	Test if we have exactly one widget as a child, and if so assume that we are a container for that widget,
		//	and should propogate startup() and resize() calls to it.
		var childNodes = dojo.query(">", this.containerNode || this.domNode),
			childWidgets = childNodes.filter("[widgetId]");

		if(childNodes.length == 1 && childWidgets.length == 1){
			this.isContainer = true;
			this._singleChild = dijit.byNode(childWidgets[0]);
		}else{
			delete this.isContainer;
			delete this._singleChild;
		}
	},

	refresh: function(){
		// summary:
		//	Force a refresh (re-download) of content, be sure to turn off cache

		// we return result of _prepareLoad here to avoid code dup. in dojox.layout.ContentPane
		return this._prepareLoad(true);
	},

	setHref: function(/*String|Uri*/ href){
		// summary:
		//		Reset the (external defined) content of this pane and replace with new url
		//		Note: It delays the download until widget is shown if preload is false
		//	href:
		//		url to the page you want to get, must be within the same domain as your mainpage
		this.href = href;

		// we return result of _prepareLoad here to avoid code dup. in dojox.layout.ContentPane
		return this._prepareLoad();
	},

	setContent: function(/*String|DomNode|Nodelist*/data){
		// summary:
		//		Replaces old content with data content, include style classes from old content
		//	data:
		//		the new Content may be String, DomNode or NodeList
		//
		//		if data is a NodeList (or an array of nodes) nodes are copied
		//		so you can import nodes from another document implicitly

		// clear href so we cant run refresh and clear content
		// refresh should only work if we downloaded the content
		if(!this._isDownloaded){
			this.href = "";
			this._onUnloadHandler();
		}

		this._setContent(data || "");

		this._isDownloaded = false; // must be set after _setContent(..), pathadjust in dojox.layout.ContentPane

		if(this.parseOnLoad){
			this._createSubWidgets();
		}

		this._checkIfSingleChild();
		if(this._singleChild && this._singleChild.resize){
			this._singleChild.resize(this._contentBox);
		}

		this._onLoadHandler();
	},

	cancel: function(){
		// summary:
		//		Cancels a inflight download of content
		if(this._xhrDfd && (this._xhrDfd.fired == -1)){
			this._xhrDfd.cancel();
		}
		delete this._xhrDfd; // garbage collect
	},

	destroy: function(){
		// if we have multiple controllers destroying us, bail after the first
		if(this._beingDestroyed){
			return;
		}
		// make sure we call onUnload
		this._onUnloadHandler();
		this._beingDestroyed = true;
		this.inherited("destroy",arguments);
	},

	resize: function(size){
		dojo.marginBox(this.domNode, size);

		// Compute content box size in case we [later] need to size child
		// If either height or width wasn't specified by the user, then query node for it.
		// But note that setting the margin box and then immediately querying dimensions may return
		// inaccurate results, so try not to depend on it.
		var node = this.containerNode || this.domNode,
			mb = dojo.mixin(dojo.marginBox(node), size||{});

		this._contentBox = dijit.layout.marginBox2contentBox(node, mb);

		// If we have a single widget child then size it to fit snugly within my borders
		if(this._singleChild && this._singleChild.resize){
			this._singleChild.resize(this._contentBox);
		}
	},

	_prepareLoad: function(forceLoad){
		// sets up for a xhrLoad, load is deferred until widget onShow
		// cancels a inflight download
		this.cancel();
		this.isLoaded = false;
		this._loadCheck(forceLoad);
	},

	_loadCheck: function(forceLoad){
		// call this when you change onShow (onSelected) status when selected in parent container
		// it's used as a trigger for href download when this.domNode.display != 'none'

		// sequence:
		// if no href -> bail
		// forceLoad -> always load
		// this.preload -> load when download not in progress, domNode display doesn't matter
		// this.refreshOnShow -> load when download in progress bails, domNode display !='none' AND
		//						this.open !== false (undefined is ok), isLoaded doesn't matter
		// else -> load when download not in progress, if this.open !== false (undefined is ok) AND
		//						domNode display != 'none', isLoaded must be false

		var displayState = ((this.open !== false) && (this.domNode.style.display != 'none'));

		if(this.href &&	
			(forceLoad ||
				(this.preload && !this._xhrDfd) ||
				(this.refreshOnShow && displayState && !this._xhrDfd) ||
				(!this.isLoaded && displayState && !this._xhrDfd)
			)
		){
			this._downloadExternalContent();
		}
	},

	_downloadExternalContent: function(){
		this._onUnloadHandler();

		// display loading message
		this._setContent(
			this.onDownloadStart.call(this)
		);

		var self = this;
		var getArgs = {
			preventCache: (this.preventCache || this.refreshOnShow),
			url: this.href,
			handleAs: "text"
		};
		if(dojo.isObject(this.ioArgs)){
			dojo.mixin(getArgs, this.ioArgs);
		}

		var hand = this._xhrDfd = (this.ioMethod || dojo.xhrGet)(getArgs);

		hand.addCallback(function(html){
			try{
				self.onDownloadEnd.call(self);
				self._isDownloaded = true;
				self.setContent.call(self, html); // onload event is called from here
			}catch(err){
				self._onError.call(self, 'Content', err); // onContentError
			}
			delete self._xhrDfd;
			return html;
		});

		hand.addErrback(function(err){
			if(!hand.cancelled){
				// show error message in the pane
				self._onError.call(self, 'Download', err); // onDownloadError
			}
			delete self._xhrDfd;
			return err;
		});
	},

	_onLoadHandler: function(){
		this.isLoaded = true;
		try{
			this.onLoad.call(this);
		}catch(e){
			console.error('Error '+this.widgetId+' running custom onLoad code');
		}
	},

	_onUnloadHandler: function(){
		this.isLoaded = false;
		this.cancel();
		try{
			this.onUnload.call(this);
		}catch(e){
			console.error('Error '+this.widgetId+' running custom onUnload code');
		}
	},

	_setContent: function(cont){
		this.destroyDescendants();

		try{
			var node = this.containerNode || this.domNode;
			while(node.firstChild){
				dojo._destroyElement(node.firstChild);
			}
			if(typeof cont == "string"){
				// dijit.ContentPane does only minimal fixes,
				// No pathAdjustments, script retrieval, style clean etc
				// some of these should be available in the dojox.layout.ContentPane
				if(this.extractContent){
					match = cont.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
					if(match){ cont = match[1]; }
				}
				node.innerHTML = cont;
			}else{
				// domNode or NodeList
				if(cont.nodeType){ // domNode (htmlNode 1 or textNode 3)
					node.appendChild(cont);
				}else{// nodelist or array such as dojo.Nodelist
					dojo.forEach(cont, function(n){
						node.appendChild(n.cloneNode(true));
					});
				}
			}
		}catch(e){
			// check if a domfault occurs when we are appending this.errorMessage
			// like for instance if domNode is a UL and we try append a DIV
			var errMess = this.onContentError(e);
			try{
				node.innerHTML = errMess;
			}catch(e){
				console.error('Fatal '+this.id+' could not change content due to '+e.message, e);
			}
		}
	},

	_onError: function(type, err, consoleText){
		// shows user the string that is returned by on[type]Error
		// overide on[type]Error and return your own string to customize
		var errText = this['on' + type + 'Error'].call(this, err);
		if(consoleText){
			console.error(consoleText, err);
		}else if(errText){// a empty string won't change current content
			this._setContent.call(this, errText);
		}
	},

	_createSubWidgets: function(){
		// summary: scan my contents and create subwidgets
		var rootNode = this.containerNode || this.domNode;
		try{
			dojo.parser.parse(rootNode, true);
		}catch(e){
			this._onError('Content', e, "Couldn't create widgets in "+this.id
				+(this.href ? " from "+this.href : ""));
		}
	},

	// EVENT's, should be overide-able
	onLoad: function(e){
		// summary:
		//		Event hook, is called after everything is loaded and widgetified
	},

	onUnload: function(e){
		// summary:
		//		Event hook, is called before old content is cleared
	},

	onDownloadStart: function(){
		// summary:
		//		called before download starts
		//		the string returned by this function will be the html
		//		that tells the user we are loading something
		//		override with your own function if you want to change text
		return this.loadingMessage;
	},

	onContentError: function(/*Error*/ error){
		// summary:
		//		called on DOM faults, require fault etc in content
		//		default is to display errormessage inside pane
	},

	onDownloadError: function(/*Error*/ error){
		// summary:
		//		Called when download error occurs, default is to display
		//		errormessage inside pane. Overide function to change that.
		//		The string returned by this function will be the html
		//		that tells the user a error happend
		return this.errorMessage;
	},

	onDownloadEnd: function(){
		// summary:
		//		called when download is finished
	}
});

}

if(!dojo._hasResource["dijit.form.Form"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.Form"] = true;
dojo.provide("dijit.form.Form");




dojo.declare("dijit.form._FormMixin", null,
	{
		/*
		summary:
			Widget corresponding to <form> tag, for validation and serialization

		usage:
			<form dojoType="dijit.form.Form" id="myForm">
				Name: <input type="text" name="name" />
			</form>
			myObj={name: "John Doe"};
			dijit.byId('myForm').setValues(myObj);

			myObj=dijit.byId('myForm').getValues();
		TODO:
		* Repeater
		* better handling for arrays.  Often form elements have names with [] like
		* people[3].sex (for a list of people [{name: Bill, sex: M}, ...])

		*/

		// HTML <FORM> attributes

		action: "",
		method: "",
		enctype: "",
		name: "",
		"accept-charset": "",
		accept: "",
		target: "",

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),
			{action: "", method: "", enctype: "", "accept-charset": "", accept: "", target: ""}),

		// execute: Function
		//	User defined function to do stuff when the user hits the submit button
		execute: function(/*Object*/ formContents){},

		// onCancel: Function
		//	Callback when user has canceled dialog, to notify container
		//	(user shouldn't override)
		onCancel: function(){},

		// onExecute: Function
		//	Callback when user is about to execute dialog, to notify container
		//	(user shouldn't override)
		onExecute: function(){},

		templateString: "<form dojoAttachPoint='containerNode' dojoAttachEvent='onsubmit:_onSubmit' name='${name}' enctype='multipart/form-data'></form>",

		_onSubmit: function(/*event*/e) {
			// summary: callback when user hits submit button
			dojo.stopEvent(e);
			this.onExecute();	// notify container that we are about to execute
			this.execute(this.getValues());
		},

		submit: function() {
			// summary: programatically submit form
			this.containerNode.submit();
		},

		setValues: function(/*object*/obj) {
			// summary: fill in form values from a JSON structure

			// generate map from name --> [list of widgets with that name]
			var map = {};
			dojo.forEach(this.getDescendants(), function(widget){
				if(!widget.name){ return; }
				var entry = map[widget.name] || (map[widget.name] = [] );
				entry.push(widget);
			});

			// call setValue() or setChecked() for each widget, according to obj
			for(var name in map){
				var widgets = map[name],						// array of widgets w/this name
					values = dojo.getObject(name, false, obj);	// list of values for those widgets
				if(!dojo.isArray(values)){
					values = [ values ];
				}
				if(widgets[0].setChecked){
					// for checkbox/radio, values is a list of which widgets should be checked
					dojo.forEach(widgets, function(w, i){
						w.setChecked(dojo.indexOf(values, w.value) != -1);
					});
				}else{
					// otherwise, values is a list of values to be assigned sequentially to each widget
					dojo.forEach(widgets, function(w, i){
						w.setValue(values[i]);
					});					
				}
			}

			/***
			 * 	TODO: code for plain input boxes (this shouldn't run for inputs that are part of widgets

			dojo.forEach(this.containerNode.elements, function(element){
				if (element.name == ''){return};	// like "continue"	
				var namePath = element.name.split(".");
				var myObj=obj;
				var name=namePath[namePath.length-1];
				for(var j=1,len2=namePath.length;j<len2;++j) {
					var p=namePath[j - 1];
					// repeater support block
					var nameA=p.split("[");
					if (nameA.length > 1) {
						if(typeof(myObj[nameA[0]]) == "undefined") {
							myObj[nameA[0]]=[ ];
						} // if

						nameIndex=parseInt(nameA[1]);
						if(typeof(myObj[nameA[0]][nameIndex]) == "undefined") {
							myObj[nameA[0]][nameIndex]={};
						}
						myObj=myObj[nameA[0]][nameIndex];
						continue;
					} // repeater support ends

					if(typeof(myObj[p]) == "undefined") {
						myObj=undefined;
						break;
					};
					myObj=myObj[p];
				}

				if (typeof(myObj) == "undefined") {
					return;		// like "continue"
				}
				if (typeof(myObj[name]) == "undefined" && this.ignoreNullValues) {
					return;		// like "continue"
				}

				// TODO: widget values (just call setValue() on the widget)

				switch(element.type) {
					case "checkbox":
						element.checked = (name in myObj) &&
							dojo.some(myObj[name], function(val){ return val==element.value; });
						break;
					case "radio":
						element.checked = (name in myObj) && myObj[name]==element.value;
						break;
					case "select-multiple":
						element.selectedIndex=-1;
						dojo.forEach(element.options, function(option){
							option.selected = dojo.some(myObj[name], function(val){ return option.value == val; });
						});
						break;
					case "select-one":
						element.selectedIndex="0";
						dojo.forEach(element.options, function(option){
							option.selected = option.value == myObj[name];
						});
						break;
					case "hidden":
					case "text":
					case "textarea":
					case "password":
						element.value = myObj[name] || "";
						break;
				}
	  		});
	  		*/
		},

		getValues: function() {
			// summary: generate JSON structure from form values

			// get widget values
			var obj = {};
			dojo.forEach(this.getDescendants(), function(widget){
				var value = widget.getValue ? widget.getValue() : widget.value;
				var name = widget.name;
				if(!name){ return; }

				// Store widget's value(s) as a scalar, except for checkboxes which are automatically arrays
				if(widget.setChecked){
					if(/Radio/.test(widget.declaredClass)){
						// radio button
						if(widget.checked){
							dojo.setObject(name, value, obj);
						}
					}else{
						// checkbox/toggle button
						var ary=dojo.getObject(name, false, obj);
						if(!ary){
							ary=[];
							dojo.setObject(name, ary, obj);
						}
						if(widget.checked){
							ary.push(value);
						}
					}
				}else{
					// plain input
					dojo.setObject(name, value, obj);
				}
			});

			/***
			 * code for plain input boxes (see also dojo.formToObject, can we use that instead of this code?
			 * but it doesn't understand [] notation, presumably)
			var obj = { };
			dojo.forEach(this.containerNode.elements, function(elm){
				if (!elm.name)	{
					return;		// like "continue"
				}
				var namePath = elm.name.split(".");
				var myObj=obj;
				var name=namePath[namePath.length-1];
				for(var j=1,len2=namePath.length;j<len2;++j) {
					var nameIndex = null;
					var p=namePath[j - 1];
					var nameA=p.split("[");
					if (nameA.length > 1) {
						if(typeof(myObj[nameA[0]]) == "undefined") {
							myObj[nameA[0]]=[ ];
						} // if
						nameIndex=parseInt(nameA[1]);
						if(typeof(myObj[nameA[0]][nameIndex]) == "undefined") {
							myObj[nameA[0]][nameIndex]={};
						}
					} else if(typeof(myObj[nameA[0]]) == "undefined") {
						myObj[nameA[0]]={}
					} // if

					if (nameA.length == 1) {
						myObj=myObj[nameA[0]];
					} else {
						myObj=myObj[nameA[0]][nameIndex];
					} // if
				} // for

				if ((elm.type != "select-multiple" && elm.type != "checkbox" && elm.type != "radio") || (elm.type=="radio" && elm.checked)) {
					if(name == name.split("[")[0]) {
						myObj[name]=elm.value;
					} else {
						// can not set value when there is no name
					}
				} else if (elm.type == "checkbox" && elm.checked) {
					if(typeof(myObj[name]) == 'undefined') {
						myObj[name]=[ ];
					}
					myObj[name].push(elm.value);
				} else if (elm.type == "select-multiple") {
					if(typeof(myObj[name]) == 'undefined') {
						myObj[name]=[ ];
					}
					for (var jdx=0,len3=elm.options.length; jdx<len3; ++jdx) {
						if (elm.options[jdx].selected) {
							myObj[name].push(elm.options[jdx].value);
						}
					}
				} // if
				name=undefined;
			}); // forEach
			***/
			return obj;
		},

	 	isValid: function() {
	 		// TODO: ComboBox might need time to process a recently input value.  This should be async?
	 		// make sure that every widget that has a validator function returns true
	 		return dojo.every(this.getDescendants(), function(widget){
	 			return !widget.isValid || widget.isValid();
	 		});
		}
	});

dojo.declare(
	"dijit.form.Form",
	[dijit._Widget, dijit._Templated, dijit.form._FormMixin],
	null
);

}

if(!dojo._hasResource["dijit.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Dialog"] = true;
dojo.provide("dijit.Dialog");









dojo.declare(
	"dijit.DialogUnderlay",
	[dijit._Widget, dijit._Templated],
	{
		// summary: the thing that grays out the screen behind the dialog

		// Template has two divs; outer div is used for fade-in/fade-out, and also to hold background iframe.
		// Inner div has opacity specified in CSS file.
		templateString: "<div class=dijitDialogUnderlayWrapper id='${id}_underlay'><div class=dijitDialogUnderlay dojoAttachPoint='node'></div></div>",

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.bgIframe = new dijit.BackgroundIframe(this.domNode);
		},

		layout: function(){
			// summary
			//		Sets the background to the size of the viewport (rather than the size
			//		of the document) since we need to cover the whole browser window, even
			//		if the document is only a few lines long.

			var viewport = dijit.getViewport();
			var is = this.node.style,
				os = this.domNode.style;

			os.top = viewport.t + "px";
			os.left = viewport.l + "px";
			is.width = viewport.w + "px";
			is.height = viewport.h + "px";

			// process twice since the scroll bar may have been removed
			// by the previous resizing
			var viewport2 = dijit.getViewport();
			if(viewport.w != viewport2.w){ is.width = viewport2.w + "px"; }
			if(viewport.h != viewport2.h){ is.height = viewport2.h + "px"; }
		},

		show: function(){
			this.domNode.style.display = "block";
			this.layout();
			if(this.bgIframe.iframe){
				this.bgIframe.iframe.style.display = "block";
			}
			this._resizeHandler = this.connect(window, "onresize", "layout");
		},

		hide: function(){
			this.domNode.style.display = "none";
			if(this.bgIframe.iframe){
				this.bgIframe.iframe.style.display = "none";
			}
			this.disconnect(this._resizeHandler);
		},

		uninitialize: function(){
			if(this.bgIframe){
				this.bgIframe.destroy();
			}
		}
	}
);

dojo.declare(
	"dijit.Dialog",
	[dijit.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a modal dialog window, blocking access to the screen
		//		and also graying out the screen Dialog is extended from
		//		ContentPane so it supports all the same parameters (href, etc.)

		templateString: null,
		templatePath: dojo.moduleUrl("dijit", "templates/Dialog.html"),

		// open: Boolean
		//		is True or False depending on state of dialog
		open: false,

		// duration: Integer
		//		The time in milliseconds it takes the dialog to fade in and out
		duration: 400,

		_lastFocusItem:null,

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),
			{title: "titleBar"}),

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.inherited("postCreate",arguments);
			this.domNode.style.display="none";
			this.connect(this, "onExecute", "hide");
			this.connect(this, "onCancel", "hide");
		},

		onLoad: function(){
			// summary: 
			//		when href is specified we need to reposition the dialog after the data is loaded
			this._position();
			this.inherited("onLoad",arguments);
		},

		_setup: function(){
			// summary:
			//		stuff we need to do before showing the Dialog for the first
			//		time (but we defer it until right beforehand, for
			//		performance reasons)

			this._modalconnects = [];

			if(this.titleBar){
				this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
			}

			this._underlay = new dijit.DialogUnderlay();

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
		},

		uninitialize: function(){
			if(this._underlay){
				this._underlay.destroy();
			}
		},

		_position: function(){
			// summary: position modal dialog in center of screen
			
			if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
			var viewport = dijit.getViewport();
			var mb = dojo.marginBox(this.domNode);

			var style = this.domNode.style;
			style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
			style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary:  called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to titleBar

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.titleBar.focus();
		},

		_onKey: function(/*Event*/ evt){
			if(evt.keyCode){
				var node = evt.target;
				// see if we are shift-tabbing from titleBar
				if(node == this.titleBar && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
					if(this._lastFocusItem){
						this._lastFocusItem.focus(); // send focus to last item in dialog if known
					}
					dojo.stopEvent(evt);
				}else{
					// see if the key is for the dialog
					while(node){
						if(node == this.domNode){
							if(evt.keyCode == dojo.keys.ESCAPE){
								this.hide(); 
							}else{
								return; // just let it go
							}
						}
						node = node.parentNode;
					}
					// this key is for the disabled document window
					if(evt.keyCode != dojo.keys.TAB){ // allow tabbing into the dialog for a11y
						dojo.stopEvent(evt);
					// opera won't tab to a div
					}else if (!dojo.isOpera){
						try{
							this.titleBar.focus();
						}catch(e){/*squelch*/}
					}
				}
			}
		},

		show: function(){
			// summary: display the dialog

			// first time we show the dialog, there's some initialization stuff to do			
			if(!this._alreadyInitialized){
				this._setup();
				this._alreadyInitialized=true;
			}

			if(this._fadeOut.status() == "playing"){
				this._fadeOut.stop();
			}

			this._modalconnects.push(dojo.connect(window, "onscroll", this, "layout"));
			this._modalconnects.push(dojo.connect(document.documentElement, "onkeypress", this, "_onKey"));

			// IE doesn't bubble onblur events - use ondeactivate instead
			var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
			this._modalconnects.push(dojo.connect(this.containerNode, ev, this, "_findLastFocus"));

			dojo.style(this.domNode, "opacity", 0);
			this.domNode.style.display="block";
			this.open = true;
			this._loadCheck(); // lazy load trigger

			this._position();

			this._fadeIn.play();

			this._savedFocus = dijit.getFocus(this);

			// set timeout to allow the browser to render dialog
			setTimeout(dojo.hitch(this, function(){
				dijit.focus(this.titleBar);
			}), 50);
		},

		hide: function(){
			// summary
			//		Hide the dialog

			// if we haven't been initialized yet then we aren't showing and we can just return		
			if(!this._alreadyInitialized){
				return;
			}

			if(this._fadeIn.status() == "playing"){
				this._fadeIn.stop();
			}
			this._fadeOut.play();

			if (this._scrollConnected){
				this._scrollConnected = false;
			}
			dojo.forEach(this._modalconnects, dojo.disconnect);
			this._modalconnects = [];

			this.connect(this._fadeOut,"onEnd",dojo.hitch(this,function(){
				dijit.focus(this._savedFocus);
			}));
			this.open = false;
		},

		layout: function() {
			// summary: position the Dialog and the underlay
			if(this.domNode.style.display == "block"){
				this._underlay.layout();
				this._position();
			}
		}
	}
);

dojo.declare(
	"dijit.TooltipDialog",
	[dijit.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a dialog that appears like a Tooltip
		// title: String
		// 		Description of tooltip dialog (required for a11Y)
		title: "",

		_lastFocusItem: null,

		templateString: null,
		templatePath: dojo.moduleUrl("dijit.layout", "templates/TooltipDialog.html"),

		postCreate: function(){
			this.inherited("postCreate",arguments);
			this.connect(this.containerNode, "onkeypress", "_onKey");

			// IE doesn't bubble onblur events - use ondeactivate instead
			var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
			this.connect(this.containerNode, ev, "_findLastFocus");
			this.containerNode.title=this.title;
		},

		orient: function(/*Object*/ corner){
			// summary: configure widget to be displayed in given position relative to the button
			this.domNode.className="dijitTooltipDialog " +" dijitTooltipAB"+(corner.charAt(1)=='L'?"Left":"Right")+" dijitTooltip"+(corner.charAt(0)=='T' ? "Below" : "Above");
		},

		onOpen: function(/*Object*/ pos){
			// summary: called when dialog is displayed
			this.orient(pos.corner);
			this._loadCheck(); // lazy load trigger
			this.containerNode.focus();
		},

		_onKey: function(/*Event*/ evt){
			// summary: keep keyboard focus in dialog; close dialog on escape key
			if(evt.keyCode == dojo.keys.ESCAPE){
				this.onCancel();
			}else if(evt.target == this.containerNode && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
				if (this._lastFocusItem){
					this._lastFocusItem.focus();
				}
				dojo.stopEvent(evt);
			}else if(evt.keyCode == dojo.keys.TAB){
				// we want the browser's default tab handling to move focus
				// but we don't want the tab to propagate upwards
				evt.stopPropagation();
			}
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary: called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to containerNode

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.containerNode.focus();
		}
	}	
);


}


if(!dojo._hasResource["dijit.Toolbar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.Toolbar"] = true;
	dojo.provide("dijit.Toolbar");


	dojo.declare(
		"dijit.Toolbar",
		[dijit._Widget, dijit._Templated, dijit._KeyNavContainer],
	{
		templateString:
			'<div class="dijit dijitToolbar" waiRole="toolbar" tabIndex="${tabIndex}" dojoAttachPoint="containerNode">' +
//				'<table style="table-layout: fixed" class="dijitReset dijitToolbarTable">' + // factor out style
//					'<tr class="dijitReset" dojoAttachPoint="containerNode"></tr>'+
//				'</table>' +
			'</div>',

		tabIndex: "0",

		postCreate: function(){
			this.connectKeyNavHandlers(
				this.isLeftToRight() ? [dojo.keys.LEFT_ARROW] : [dojo.keys.RIGHT_ARROW],
				this.isLeftToRight() ? [dojo.keys.RIGHT_ARROW] : [dojo.keys.LEFT_ARROW]
			);
		},

		startup: function(){
			this.startupKeyNavChildren();
		}
	}
	);

	// Combine with dijit.MenuSeparator??
	dojo.declare(
		"dijit.ToolbarSeparator",
		[ dijit._Widget, dijit._Templated ],
	{
		// summary
		//	A line between two menu items
		templateString: '<div class="dijitToolbarSeparator dijitInline"></div>',
		postCreate: function(){ dojo.setSelectable(this.domNode, false); },
		isFocusable: function(){ return false; }
	});

	}

if(!dojo._hasResource["dijit.form.Button"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.form.Button"] = true;
	dojo.provide("dijit.form.Button");

	dojo.declare("dijit.form.Button", dijit.form._FormWidget, {
	/*
	 * usage
	 *	<button dojoType="button" onClick="...">Hello world</button>
	 *
	 *  var button1 = new dijit.form.Button({label: "hello world", onClick: foo});
	 *	dojo.body().appendChild(button1.domNode);
	 */
		// summary
		//	Basically the same thing as a normal HTML button, but with special styling.

		// label: String
		//	text to display in button
		label: "",

		// showLabel: Boolean
		//	whether or not to display the text label in button
		showLabel: true,

		// iconClass: String
		//	class to apply to div in button to make it display an icon
		iconClass: "",

		type: "button",
		baseClass: "dijitButton",
		templateString:"<div class=\"dijit dijitLeft dijitInline dijitButton\"\n\tdojoAttachEvent=\"onclick:_onButtonClick,onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\"\n\t><div class='dijitRight'\n\t\t><button class=\"dijitStretch dijitButtonNode dijitButtonContents\" dojoAttachPoint=\"focusNode,titleNode\"\n\t\t\ttype=\"${type}\" waiRole=\"button\" waiState=\"labelledby-${id}_label\"\n\t\t\t><span class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\" \n \t\t\t\t><span class=\"dijitToggleButtonIconChar\">&#10003</span \n\t\t\t></span\n\t\t\t><span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span\n\t\t></button\n\t></div\n></div>\n",

		// TODO: set button's title to this.containerNode.innerText

		_onClick: function(/*Event*/ e){
			// summary: internal function to handle click actions
			if(this.disabled){ return false; }
			this._clicked(); // widget click actions
			return this.onClick(e); // user click actions
		},

		_onButtonClick: function(/*Event*/ e){
			// summary: callback when the user mouse clicks the button portion
			dojo.stopEvent(e);
			var okToSubmit = this._onClick(e) !== false; // returning nothing is same as true

			// for some reason type=submit buttons don't automatically submit the form; do it manually
			if(this.type=="submit" && okToSubmit){
				for(var node=this.domNode; node; node=node.parentNode){
					var widget=dijit.byNode(node);
					if(widget && widget._onSubmit){
						widget._onSubmit(e);
						break;
					}
					if(node.tagName.toLowerCase() == "form"){
						if(!node.onsubmit || node.onsubmit()){ node.submit(); }
						break;
					}
				}
			}
		},

		postCreate: function(){
			// summary:
			//	get label and set as title on button icon if necessary
			if (this.showLabel == false){
				var labelText = "";
				this.label = this.containerNode.innerHTML;
				labelText = dojo.trim(this.containerNode.innerText || this.containerNode.textContent);
				// set title attrib on iconNode
				this.titleNode.title=labelText;
				dojo.addClass(this.containerNode,"dijitDisplayNone");
			}
			this.inherited(arguments);
		},

		onClick: function(/*Event*/ e){
			// summary: user callback for when button is clicked
			//      if type="submit", return value != false to perform submit
			return true;
		},

		_clicked: function(/*Event*/ e){
			// summary: internal replaceable function for when the button is clicked
		},

		setLabel: function(/*String*/ content){
			// summary: reset the label (text) of the button; takes an HTML string
			this.containerNode.innerHTML = this.label = content;
			if(dojo.isMozilla){ // Firefox has re-render issues with tables
				var oldDisplay = dojo.getComputedStyle(this.domNode).display;
				this.domNode.style.display="none";
				var _this = this;
				setTimeout(function(){_this.domNode.style.display=oldDisplay;},1);
			}
			if (this.showLabel == false){
					this.titleNode.title=dojo.trim(this.containerNode.innerText || this.containerNode.textContent);
			}
		}		
	});

	/*
	 * usage
	 *	<button dojoType="DropDownButton" label="Hello world"><div dojotype=dijit.Menu>...</div></button>
	 *
	 *  var button1 = new dijit.form.DropDownButton({ label: "hi", dropDown: new dijit.Menu(...) });
	 *	dojo.body().appendChild(button1);
	 */
	dojo.declare("dijit.form.DropDownButton", [dijit.form.Button, dijit._Container], {
		// summary
		//		push the button and a menu shows up

		baseClass : "dijitDropDownButton",

		templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<button class=\"dijitStretch dijitButtonNode dijitButtonContents\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><div class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\"></div\n\t\t><span class=\"dijitButtonText\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\">${label}</span\n\t\t><span class='dijitA11yDownArrow'>&#9660;</span>\n\t</button>\n</div></div>\n",

		_fillContent: function(){
			// my inner HTML contains both the button contents and a drop down widget, like
			// <DropDownButton>  <span>push me</span>  <Menu> ... </Menu> </DropDownButton>
			// The first node is assumed to be the button content. The widget is the popup.
			if(this.srcNodeRef){ // programatically created buttons might not define srcNodeRef
				//FIXME: figure out how to filter out the widget and use all remaining nodes as button
				//	content, not just nodes[0]
				var nodes = dojo.query("*", this.srcNodeRef);
				dijit.form.DropDownButton.superclass._fillContent.call(this, nodes[0]);

				// save pointer to srcNode so we can grab the drop down widget after it's instantiated
				this.dropDownContainer = this.srcNodeRef;
			}
		},

		startup: function(){
			// the child widget from srcNodeRef is the dropdown widget.  Insert it in the page DOM,
			// make it invisible, and store a reference to pass to the popup code.
			if(!this.dropDown){
				var dropDownNode = dojo.query("[widgetId]", this.dropDownContainer)[0];
				this.dropDown = dijit.byNode(dropDownNode);
				delete this.dropDownContainer;
			}
			dojo.body().appendChild(this.dropDown.domNode);
			this.dropDown.domNode.style.display="none";
		},

		_onArrowClick: function(/*Event*/ e){
			// summary: callback when the user mouse clicks on menu popup node
			if(this.disabled){ return; }
			this._toggleDropDown();
		},

		_onDropDownClick: function(/*Event*/ e){
			// on Firefox 2 on the Mac it is possible to fire onclick
			// by pressing enter down on a second element and transferring
			// focus to the DropDownButton;
			// we want to prevent opening our menu in this situation
			// and only do so if we have seen a keydown on this button;
			// e.detail != 0 means that we were fired by mouse
			var isMacFFlessThan3 = dojo.isFF && dojo.isFF < 3
				&& navigator.appVersion.indexOf("Macintosh") != -1;
			if(!isMacFFlessThan3 || e.detail != 0 || this._seenKeydown){
				this._onArrowClick(e);
			}
			this._seenKeydown = false;
		},

		_onDropDownKeydown: function(/*Event*/ e){
			this._seenKeydown = true;
		},

		_onDropDownBlur: function(/*Event*/ e){
			this._seenKeydown = false;
		},

		_onKey: function(/*Event*/ e){
			// summary: callback when the user presses a key on menu popup node
			if(this.disabled){ return; }
			if(e.keyCode == dojo.keys.DOWN_ARROW){
				if(!this.dropDown || this.dropDown.domNode.style.display=="none"){
					dojo.stopEvent(e);
					return this._toggleDropDown();
				}
			}
		},

		_onBlur: function(){
			// summary: called magically when focus has shifted away from this widget and it's dropdown
			this._closeDropDown();
			// don't focus on button.  the user has explicitly focused on something else.
		},

		_toggleDropDown: function(){
			// summary: toggle the drop-down widget; if it is up, close it, if not, open it
			if(this.disabled){ return; }
			dijit.focus(this.popupStateNode);
			var dropDown = this.dropDown;
			if(!dropDown){ return false; }
			if(!dropDown.isShowingNow){
				// If there's an href, then load that first, so we don't get a flicker
				if(dropDown.href && !dropDown.isLoaded){
					var self = this;
					var handler = dojo.connect(dropDown, "onLoad", function(){
						dojo.disconnect(handler);
						self._openDropDown();
					});
					dropDown._loadCheck(true);
					return;
				}else{
					this._openDropDown();
				}
			}else{
				this._closeDropDown();
			}
		},

		_openDropDown: function(){
			var dropDown = this.dropDown;
			var oldWidth=dropDown.domNode.style.width;
			var self = this;

			dijit.popup.open({
				parent: this,
				popup: dropDown,
				around: this.domNode,
				orient: this.isLeftToRight() ? {'BL':'TL', 'BR':'TR', 'TL':'BL', 'TR':'BR'}
					: {'BR':'TR', 'BL':'TL', 'TR':'BR', 'TL':'BL'},
				onExecute: function(){
					self._closeDropDown(true);
				},
				onCancel: function(){
					self._closeDropDown(true);
				},
				onClose: function(){
					dropDown.domNode.style.width = oldWidth;
					self.popupStateNode.removeAttribute("popupActive");
					this._opened = false;
				}
			});
			if(this.domNode.offsetWidth > dropDown.domNode.offsetWidth){
				var adjustNode = null;
				if(!this.isLeftToRight()){
					adjustNode = dropDown.domNode.parentNode;
					var oldRight = adjustNode.offsetLeft + adjustNode.offsetWidth;
				}
				// make menu at least as wide as the button
				dojo.marginBox(dropDown.domNode, {w: this.domNode.offsetWidth});
				if(adjustNode){
					adjustNode.style.left = oldRight - this.domNode.offsetWidth + "px";
				}
			}
			this.popupStateNode.setAttribute("popupActive", "true");
			this._opened=true;
			if(dropDown.focus){
				dropDown.focus();
			}
			// TODO: set this.checked and call setStateClass(), to affect button look while drop down is shown
		},
		
		_closeDropDown: function(/*Boolean*/ focus){
			if(this._opened){
				dijit.popup.close(this.dropDown);
				if(focus){ this.focus(); }
				this._opened = false;			
			}
		}
	});

	/*
	 * usage
	 *	<button dojoType="ComboButton" onClick="..."><span>Hello world</span><div dojoType=dijit.Menu>...</div></button>
	 *
	 *  var button1 = new dijit.form.ComboButton({label: "hello world", onClick: foo, dropDown: "myMenu"});
	 *	dojo.body().appendChild(button1.domNode);
	 */
	dojo.declare("dijit.form.ComboButton", dijit.form.DropDownButton, {
		// summary
		//		left side is normal button, right side displays menu
		templateString:"<table class='dijit dijitReset dijitInline dijitLeft'\n\tcellspacing='0' cellpadding='0'\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\">\n\t<tr>\n\t\t<td\tclass=\"dijitStretch dijitButtonContents dijitButtonNode\"\n\t\t\ttabIndex=\"${tabIndex}\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onButtonClick\"  dojoAttachPoint=\"titleNode\"\n\t\t\twaiRole=\"button\" waiState=\"labelledby-${id}_label\">\n\t\t\t<div class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\"></div>\n\t\t\t<span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span>\n\t\t</td>\n\t\t<td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton'\n\t\t\tdojoAttachPoint=\"popupStateNode,focusNode\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onArrowClick, onkeypress:_onKey\"\n\t\t\tstateModifier=\"DownArrow\"\n\t\t\ttitle=\"${optionsTitle}\" name=\"${name}\"\n\t\t\twaiRole=\"button\" waiState=\"haspopup-true\"\n\t\t><div waiRole=\"presentation\">&#9660;</div>\n\t</td></tr>\n</table>\n",

		attributeMap: dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),
			{id:"", name:""}),

		// optionsTitle: String
		//  text that describes the options menu (accessibility)
		optionsTitle: "",

		baseClass: "dijitComboButton",

		_focusedNode: null,

		postCreate: function(){
			this.inherited(arguments);
			this._focalNodes = [this.titleNode, this.popupStateNode];
			dojo.forEach(this._focalNodes, dojo.hitch(this, function(node){
				if(dojo.isIE){
					this.connect(node, "onactivate", this._onNodeFocus);
				}else{
					this.connect(node, "onfocus", this._onNodeFocus);
				}
			}));
		},

		focusFocalNode: function(node){
			// summary: Focus the focal node node.
			this._focusedNode = node;
			dijit.focus(node);
		},

		hasNextFocalNode: function(){
			// summary: Returns true if this widget has no node currently
			//		focused or if there is a node following the focused one.
			//		False is returned if the last node has focus.
			return this._focusedNode !== this.getFocalNodes()[1];
		},

		focusNext: function(){
			// summary: Focus the focal node following the current node with focus
			//		or the first one if no node currently has focus.
			this._focusedNode = this.getFocalNodes()[this._focusedNode ? 1 : 0];
			dijit.focus(this._focusedNode);
		},

		hasPrevFocalNode: function(){
			// summary: Returns true if this widget has no node currently
			//		focused or if there is a node before the focused one.
			//		False is returned if the first node has focus.
			return this._focusedNode !== this.getFocalNodes()[0];
		},

		focusPrev: function(){
			// summary: Focus the focal node before the current node with focus
			//		or the last one if no node currently has focus.
			this._focusedNode = this.getFocalNodes()[this._focusedNode ? 0 : 1];
			dijit.focus(this._focusedNode);
		},

		getFocalNodes: function(){
			// summary: Returns an array of focal nodes for this widget.
			return this._focalNodes;
		},

		_onNodeFocus: function(evt){
			this._focusedNode = evt.currentTarget;
		},

		_onBlur: function(evt){
			this.inherited(arguments);
			this._focusedNode = null;
		}
	});

	dojo.declare("dijit.form.ToggleButton", dijit.form.Button, {
		// summary
		//	A button that can be in two states (checked or not).
		//	Can be base class for things like tabs or checkbox or radio buttons

		baseClass: "dijitToggleButton",

		// checked: Boolean
		//		Corresponds to the native HTML <input> element's attribute.
		//		In markup, specified as "checked='checked'" or just "checked".
		//		True if the button is depressed, or the checkbox is checked,
		//		or the radio button is selected, etc.
		checked: false,

		_clicked: function(/*Event*/ evt){
			this.setChecked(!this.checked);
		},

		setChecked: function(/*Boolean*/ checked){
			// summary
			//	Programatically deselect the button
			this.checked = checked;
			dijit.setWaiState(this.focusNode || this.domNode, "pressed", this.checked);
			this._setStateClass();		
			this.onChange(checked);
		}
	});

	}

if(!dojo._hasResource["dijit.Menu"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.Menu"] = true;
	dojo.provide("dijit.Menu");
	
	dojo.declare(
		"dijit.Menu",
		[dijit._Widget, dijit._Templated, dijit._KeyNavContainer],
	{
		constructor: function() {
			this._bindings = [];
		},

		templateString:
				'<table class="dijit dijitMenu dijitReset dijitMenuTable" waiRole="menu" dojoAttachEvent="onkeypress:_onKeyPress">' +
					'<tbody class="dijitReset" dojoAttachPoint="containerNode"></tbody>'+
				'</table>',

		// targetNodeIds: String[]
		//	Array of dom node ids of nodes to attach to.
		//	Fill this with nodeIds upon widget creation and it becomes context menu for those nodes.
		targetNodeIds: [],

		// contextMenuForWindow: Boolean
		//	if true, right clicking anywhere on the window will cause this context menu to open;
		//	if false, must specify targetNodeIds
		contextMenuForWindow: false,

		// parentMenu: Widget
		// pointer to menu that displayed me
		parentMenu: null,

		// popupDelay: Integer
		//	number of milliseconds before hovering (without clicking) causes the popup to automatically open
		popupDelay: 500,

		// _contextMenuWithMouse: Boolean
		//	used to record mouse and keyboard events to determine if a context
		//	menu is being opened with the keyboard or the mouse
		_contextMenuWithMouse: false,

		postCreate: function(){
			if(this.contextMenuForWindow){
				this.bindDomNode(dojo.body());
			}else{
				dojo.forEach(this.targetNodeIds, this.bindDomNode, this);
			}
			this.connectKeyNavHandlers([dojo.keys.UP_ARROW], [dojo.keys.DOWN_ARROW]);
		},

		startup: function(){
			dojo.forEach(this.getChildren(), function(child){ child.startup(); });
			this.startupKeyNavChildren();
		},

		onExecute: function(){
			// summary: attach point for notification about when a menu item has been executed
		},

		onCancel: function(/*Boolean*/ closeAll){
			// summary: attach point for notification about when the user cancels the current menu
		},

		_moveToPopup: function(/*Event*/ evt){
			if(this.focusedChild && this.focusedChild.popup && !this.focusedChild.disabled){
				this.focusedChild._onClick(evt);
			}
		},

		_onKeyPress: function(/*Event*/ evt){
			// summary
			//	Handle keyboard based menu navigation.
			if(evt.ctrlKey || evt.altKey){ return; }

			switch(evt.keyCode){
				case dojo.keys.RIGHT_ARROW:
					this._moveToPopup(evt);
					dojo.stopEvent(evt);
					break;
				case dojo.keys.LEFT_ARROW:
					if(this.parentMenu){
						this.onCancel(false);
					}else{
						dojo.stopEvent(evt);
					}
					break;
			}
		},

		onItemHover: function(/*MenuItem*/ item){
			this.focusChild(item);

			if(this.focusedChild.popup && !this.focusedChild.disabled && !this.hover_timer){
				this.hover_timer = setTimeout(dojo.hitch(this, "_openPopup"), this.popupDelay);
			}
		},

		_onChildBlur: function(item){
			// Close all popups that are open and descendants of this menu
			dijit.popup.close(item.popup);
			item._blur();
			this._stopPopupTimer();
		},

		onItemUnhover: function(/*MenuItem*/ item){
		},

		_stopPopupTimer: function(){
			if(this.hover_timer){
				clearTimeout(this.hover_timer);
				this.hover_timer = null;
			}
		},

		_getTopMenu: function(){
			for(var top=this; top.parentMenu; top=top.parentMenu);
			return top;
		},

		onItemClick: function(/*Widget*/ item){
			// summary: user defined function to handle clicks on an item
			// summary: internal function for clicks
			if(item.disabled){ return false; }

			if(item.popup){
				if(!this.is_open){
					this._openPopup();
				}
			}else{
				// before calling user defined handler, close hierarchy of menus
				// and restore focus to place it was when menu was opened
				this.onExecute();

				// user defined handler for click
				item.onClick();
			}
		},

		// thanks burstlib!
		_iframeContentWindow: function(/* HTMLIFrameElement */iframe_el) {
			//	summary
			//	returns the window reference of the passed iframe
			var win = dijit.getDocumentWindow(dijit.Menu._iframeContentDocument(iframe_el)) ||
				// Moz. TODO: is this available when defaultView isn't?
				dijit.Menu._iframeContentDocument(iframe_el)['__parent__'] ||
				(iframe_el.name && document.frames[iframe_el.name]) || null;
			return win;	//	Window
		},

		_iframeContentDocument: function(/* HTMLIFrameElement */iframe_el){
			//	summary
			//	returns a reference to the document object inside iframe_el
			var doc = iframe_el.contentDocument // W3
				|| (iframe_el.contentWindow && iframe_el.contentWindow.document) // IE
				|| (iframe_el.name && document.frames[iframe_el.name] && document.frames[iframe_el.name].document)
				|| null;
			return doc;	//	HTMLDocument
		},

		bindDomNode: function(/*String|DomNode*/ node){
			// summary: attach menu to given node
			node = dojo.byId(node);

			//TODO: this is to support context popups in Editor.  Maybe this shouldn't be in dijit.Menu
			var win = dijit.getDocumentWindow(node.ownerDocument);
			if(node.tagName.toLowerCase()=="iframe"){
				win = this._iframeContentWindow(node);
				node = dojo.withGlobal(win, dojo.body);
			}

			// to capture these events at the top level,
			// attach to document, not body
			var cn = (node == dojo.body() ? dojo.doc : node);

			node[this.id] = this._bindings.push([
				dojo.connect(cn, "oncontextmenu", this, "_openMyself"),
				dojo.connect(cn, "onkeydown", this, "_contextKey"),
				dojo.connect(cn, "onmousedown", this, "_contextMouse")
			]);
		},

		unBindDomNode: function(/*String|DomNode*/ nodeName){
			// summary: detach menu from given node
			var node = dojo.byId(nodeName);
			var bid = node[this.id]-1, b = this._bindings[bid];
			dojo.forEach(b, dojo.disconnect);
			delete this._bindings[bid];
		},

		_contextKey: function(e){
			this._contextMenuWithMouse = false;
			if (e.keyCode == dojo.keys.F10) {
				dojo.stopEvent(e);
				if (e.shiftKey && e.type=="keydown") {
					// FF: copying the wrong property from e will cause the system
					// context menu to appear in spite of stopEvent. Don't know
					// exactly which properties cause this effect.
					var _e = { target: e.target, pageX: e.pageX, pageY: e.pageY };
					_e.preventDefault = _e.stopPropagation = function(){};
					// IE: without the delay, focus work in "open" causes the system
					// context menu to appear in spite of stopEvent.
					window.setTimeout(dojo.hitch(this, function(){ this._openMyself(_e); }), 1);
				}
			}
		},

		_contextMouse: function(e){
			this._contextMenuWithMouse = true;
		},

		_openMyself: function(/*Event*/ e){
			// summary:
			//		Internal function for opening myself when the user
			//		does a right-click or something similar

			dojo.stopEvent(e);

			// Get coordinates.
			// if we are opening the menu with the mouse or on safari open
			// the menu at the mouse cursor
			// (Safari does not have a keyboard command to open the context menu
			// and we don't currently have a reliable way to determine
			// _contextMenuWithMouse on Safari)
			var x,y;
			if(dojo.isSafari || this._contextMenuWithMouse){
				x=e.pageX;
				y=e.pageY;
			}else{
				// otherwise open near e.target
				var coords = dojo.coords(e.target, true);
				x = coords.x + 10;
				y = coords.y + 10;
			}

			var self=this;
			var savedFocus = dijit.getFocus(this);
			function closeAndRestoreFocus(){
				// user has clicked on a menu or popup
				dijit.focus(savedFocus);
				dijit.popup.close(self);
			}
			dijit.popup.open({
				popup: this,
				x: x,
				y: y,
				onExecute: closeAndRestoreFocus,
				onCancel: closeAndRestoreFocus,
				orient: this.isLeftToRight() ? 'L' : 'R'
			});
			this.focus();

			this._onBlur = function(){
				// Usually the parent closes the child widget but if this is a context
				// menu then there is no parent
				dijit.popup.close(this);
				// don't try to restore focus; user has clicked another part of the screen
				// and set focus there
			}
		},

		onOpen: function(/*Event*/ e){
			// summary
			//		Open menu relative to the mouse
			this.isShowingNow = true;
		},

		onClose: function(){
			// summary: callback when this menu is closed
			this._stopPopupTimer();
			this.parentMenu = null;
			this.isShowingNow = false;
			this.currentPopup = null;
			if(this.focusedChild){
				this._onChildBlur(this.focusedChild);
				this.focusedChild = null;
			}
		},

		_openPopup: function(){
			// summary: open the popup to the side of the current menu item
			this._stopPopupTimer();
			var from_item = this.focusedChild;
			var popup = from_item.popup;

			if(popup.isShowingNow){ return; }
			popup.parentMenu = this;
			var self = this;
			dijit.popup.open({
				parent: this,
				popup: popup,
				around: from_item.arrowCell,
				orient: this.isLeftToRight() ? {'TR': 'TL', 'TL': 'TR'} : {'TL': 'TR', 'TR': 'TL'},
				onCancel: function(){
					// called when the child menu is canceled
					dijit.popup.close(popup);
					from_item.focus();	// put focus back on my node
					self.currentPopup = null;
				}
			});

			this.currentPopup = popup;

			if(popup.focus){
				popup.focus();
			}
		}
	}
	);

	dojo.declare(
		"dijit.MenuItem",
		[dijit._Widget, dijit._Templated, dijit._Contained],
	{
		// summary
		//	A line item in a Menu2

		// Make 3 columns
		//   icon, label, and expand arrow (BiDi-dependent) indicating sub-menu
		templateString:
			 '<tr class="dijitReset dijitMenuItem"'
			+'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
			+'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass}" dojoAttachPoint="iconNode" ></div></td>'
			+'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem"></td>'
			+'<td class="dijitReset" dojoAttachPoint="arrowCell">'
				+'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
				+'<span class="dijitInline dijitArrowNode dijitMenuExpandInner">+</span>'
				+'</div>'
			+'</td>'
			+'</tr>',

		// label: String
		//	menu text
		label: '',

		// iconClass: String
		//	class to apply to div in button to make it display an icon
		iconClass: "",

		// disabled: Boolean
		//  if true, the menu item is disabled
		//  if false, the menu item is enabled
		disabled: false,

		postCreate: function(){
			dojo.setSelectable(this.domNode, false);
			this.setDisabled(this.disabled);
			if(this.label){
				this.containerNode.innerHTML=this.label;
			}
		},

		_onHover: function(){
			// summary: callback when mouse is moved onto menu item
			this.getParent().onItemHover(this);
		},

		_onUnhover: function(){
			// summary: callback when mouse is moved off of menu item
			// if we are unhovering the currently selected item
			// then unselect it
			this.getParent().onItemUnhover(this);
		},

		_onClick: function(evt){
			this.getParent().onItemClick(this);
			dojo.stopEvent(evt);
		},

		onClick: function() {
			// summary
			//	User defined function to handle clicks
		},

		focus: function(){
			dojo.addClass(this.domNode, 'dijitMenuItemHover');
			try{
				dijit.focus(this.containerNode);
			}catch(e){
				// this throws on IE (at least) in some scenarios
			}
		},

		_blur: function(){
			dojo.removeClass(this.domNode, 'dijitMenuItemHover');
		},

		setDisabled: function(/*Boolean*/ value){
			// summary: enable or disable this menu item
			this.disabled = value;
			dojo[value ? "addClass" : "removeClass"](this.domNode, 'dijitMenuItemDisabled');
			dijit.setWaiState(this.containerNode, 'disabled', value ? 'true' : 'false');
		}
	});

	dojo.declare(
		"dijit.PopupMenuItem",
		dijit.MenuItem,
	{
		_fillContent: function(){
			// my inner HTML contains both the menu item text and a popup widget, like
			// <div dojoType="dijit.PopupMenuItem">
			//		<span>pick me</span>
			//		<popup> ... </popup>
			// </div>
			// the first part holds the menu item text and the second part is the popup
			if(this.srcNodeRef){
				var nodes = dojo.query("*", this.srcNodeRef);
				dijit.PopupMenuItem.superclass._fillContent.call(this, nodes[0]);

				// save pointer to srcNode so we can grab the drop down widget after it's instantiated
				this.dropDownContainer = this.srcNodeRef;
			}
		},

		startup: function(){
			// we didn't copy the dropdown widget from the this.srcNodeRef, so it's in no-man's
			// land now.  move it to document.body.
			if(!this.popup){
				var node = dojo.query("[widgetId]", this.dropDownContainer)[0];
				this.popup = dijit.byNode(node);
			}
			dojo.body().appendChild(this.popup.domNode);

			this.popup.domNode.style.display="none";
			dojo.addClass(this.expand, "dijitMenuExpandEnabled");
			dojo.style(this.expand, "display", "");
			dijit.setWaiState(this.containerNode, "haspopup", "true");
		}
	});

	dojo.declare(
		"dijit.MenuSeparator",
		[dijit._Widget, dijit._Templated, dijit._Contained],
	{
		// summary
		//	A line between two menu items

		templateString: '<tr class="dijitMenuSeparator"><td colspan=3>'
				+'<div class="dijitMenuSeparatorTop"></div>'
				+'<div class="dijitMenuSeparatorBottom"></div>'
				+'</td></tr>',

		postCreate: function(){
			dojo.setSelectable(this.domNode, false);
		},
		
		isFocusable: function(){
			// summary:
			//		over ride to always return false
			return false;
		}
	});

	}

if(!dojo._hasResource["dijit.Tooltip"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.Tooltip"] = true;
	dojo.provide("dijit.Tooltip");


	dojo.declare(
		"dijit._MasterTooltip",
		[dijit._Widget, dijit._Templated],
		{
			// summary
			//		Internal widget that holds the actual tooltip markup,
			//		which occurs once per page.
			//		Called by Tooltip widgets which are just containers to hold
			//		the markup

			// duration: Integer
			//		Milliseconds to fade in/fade out
			duration: 200,

			templateString:"<div class=\"dijitTooltip dijitTooltipLeft\" id=\"dojoTooltip\">\n\t<div class=\"dijitTooltipContainer dijitTooltipContents\" dojoAttachPoint=\"containerNode\" waiRole='alert'></div>\n\t<div class=\"dijitTooltipConnector\"></div>\n</div>\n",

			postCreate: function(){
				dojo.body().appendChild(this.domNode);

				this.bgIframe = new dijit.BackgroundIframe(this.domNode);

				// Setup fade-in and fade-out functions.
				this.fadeIn = dojo.fadeIn({ node: this.domNode, duration: this.duration, onEnd: dojo.hitch(this, "_onShow") });
				this.fadeOut = dojo.fadeOut({ node: this.domNode, duration: this.duration, onEnd: dojo.hitch(this, "_onHide") });

			},

			show: function(/*String*/ innerHTML, /*DomNode*/ aroundNode){
				// summary:
				//	Display tooltip w/specified contents to right specified node
				//	(To left if there's no space on the right, or if LTR==right)

				if(this.aroundNode && this.aroundNode === aroundNode){
					return;
				}

				if(this.fadeOut.status() == "playing"){
					// previous tooltip is being hidden; wait until the hide completes then show new one
					this._onDeck=arguments;
					return;
				}
				this.containerNode.innerHTML=innerHTML;

				// Firefox bug. when innerHTML changes to be shorter than previous
				// one, the node size will not be updated until it moves.
				this.domNode.style.top = (this.domNode.offsetTop + 1) + "px";

				// position the element and change CSS according to position	
				var align = this.isLeftToRight() ? {'BR': 'BL', 'BL': 'BR'} : {'BL': 'BR', 'BR': 'BL'};
				var pos = dijit.placeOnScreenAroundElement(this.domNode, aroundNode, align);
				this.domNode.className="dijitTooltip dijitTooltip" + (pos.corner=='BL' ? "Right" : "Left");//FIXME: might overwrite class

				// show it
				dojo.style(this.domNode, "opacity", 0);
				this.fadeIn.play();
				this.isShowingNow = true;
				this.aroundNode = aroundNode;
			},

			_onShow: function(){
				if(dojo.isIE){
					// the arrow won't show up on a node w/an opacity filter
					this.domNode.style.filter="";
				}
			},

			hide: function(aroundNode){
				// summary: hide the tooltip
				if(!this.aroundNode || this.aroundNode !== aroundNode){
					return;
				}
				if(this._onDeck){
					// this hide request is for a show() that hasn't even started yet;
					// just cancel the pending show()
					this._onDeck=null;
					return;
				}
				this.fadeIn.stop();
				this.isShowingNow = false;
				this.aroundNode = null;
				this.fadeOut.play();
			},

			_onHide: function(){
				this.domNode.style.cssText="";	// to position offscreen again
				if(this._onDeck){
					// a show request has been queued up; do it now
					this.show.apply(this, this._onDeck);
					this._onDeck=null;
				}
			}

		}
	);

	dijit.showTooltip = function(/*String*/ innerHTML, /*DomNode*/ aroundNode){
		// summary:
		//	Display tooltip w/specified contents to right specified node
		//	(To left if there's no space on the right, or if LTR==right)
		if(!dijit._masterTT){ dijit._masterTT = new dijit._MasterTooltip(); }
		return dijit._masterTT.show(innerHTML, aroundNode);
	};

	dijit.hideTooltip = function(aroundNode){
		// summary: hide the tooltip
		if(!dijit._masterTT){ dijit._masterTT = new dijit._MasterTooltip(); }
		return dijit._masterTT.hide(aroundNode);
	};

	dojo.declare(
		"dijit.Tooltip",
		dijit._Widget,
		{
			// summary
			//		Pops up a tooltip (a help message) when you hover over a node.

			// label: String
			//		Text to display in the tooltip.
			//		Specified as innerHTML when creating the widget from markup.
			label: "",

			// showDelay: Integer
			//		Number of milliseconds to wait after hovering over/focusing on the object, before
			//		the tooltip is displayed.
			showDelay: 400,

			// connectId: String[]
			//		Id(s) of domNodes to attach the tooltip to.
			//		When user hovers over any of the specified dom nodes, the tooltip will appear.
			connectId: [],

			postCreate: function(){
				if(this.srcNodeRef){
					this.srcNodeRef.style.display = "none";
				}

				this._connectNodes = [];
				
				dojo.forEach(this.connectId, function(id) {
					var node = dojo.byId(id);
					if (node) {
						this._connectNodes.push(node);
						dojo.forEach(["onMouseOver", "onMouseOut", "onFocus", "onBlur", "onHover", "onUnHover"], function(event){
							this.connect(node, event.toLowerCase(), "_"+event);
						}, this);
						if(dojo.isIE){
							// BiDi workaround
							node.style.zoom = 1;
						}
					}
				}, this);
			},

			_onMouseOver: function(/*Event*/ e){
				this._onHover(e);
			},

			_onMouseOut: function(/*Event*/ e){
				if(dojo.isDescendant(e.relatedTarget, e.target)){
					// false event; just moved from target to target child; ignore.
					return;
				}
				this._onUnHover(e);
			},

			_onFocus: function(/*Event*/ e){
				this._focus = true;
				this._onHover(e);
			},
			
			_onBlur: function(/*Event*/ e){
				this._focus = false;
				this._onUnHover(e);
			},

			_onHover: function(/*Event*/ e){
				if(!this._showTimer){
					var target = e.target;
					this._showTimer = setTimeout(dojo.hitch(this, function(){this.open(target)}), this.showDelay);
				}
			},

			_onUnHover: function(/*Event*/ e){
				// keep a tooltip open if the associated element has focus
				if(this._focus){ return; }
				if(this._showTimer){
					clearTimeout(this._showTimer);
					delete this._showTimer;
				}
				this.close();
			},

			open: function(/*DomNode*/ target){
	 			// summary: display the tooltip; usually not called directly.
				target = target || this._connectNodes[0];
				if(!target){ return; }

				if(this._showTimer){
					clearTimeout(this._showTimer);
					delete this._showTimer;
				}
				dijit.showTooltip(this.label || this.domNode.innerHTML, target);
				
				this._connectNode = target;
			},

			close: function(){
				// summary: hide the tooltip; usually not called directly.
				dijit.hideTooltip(this._connectNode);
				delete this._connectNode;
				if(this._showTimer){
					clearTimeout(this._showTimer);
					delete this._showTimer;
				}
			},

			uninitialize: function(){
				this.close();
			}
		}
	);

	}


if(!dojo._hasResource["dijit.form.TextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.form.TextBox"] = true;
	dojo.provide("dijit.form.TextBox");


	dojo.declare(
		"dijit.form.TextBox",
		dijit.form._FormWidget,
		{
			// summary:
			//		A generic textbox field.
			//		Serves as a base class to derive more specialized functionality in subclasses.

			//	trim: Boolean
			//		Removes leading and trailing whitespace if true.  Default is false.
			trim: false,

			//	uppercase: Boolean
			//		Converts all characters to uppercase if true.  Default is false.
			uppercase: false,

			//	lowercase: Boolean
			//		Converts all characters to lowercase if true.  Default is false.
			lowercase: false,

			//	propercase: Boolean
			//		Converts the first character of each word to uppercase if true.
			propercase: false,

			// maxLength: String
			//		HTML INPUT tag maxLength declaration.
			maxLength: "",

			templateString:"<input class=\"dojoTextBox\" dojoAttachPoint='textbox,focusNode' name=\"${name}\"\n\tdojoAttachEvent='onmouseenter:_onMouse,onmouseleave:_onMouse,onfocus:_onMouse,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress'\n\tautocomplete=\"off\" type=\"${type}\"\n\t/>\n",
			baseClass: "dijitTextBox",

			attributeMap: dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),
				{maxLength:"focusNode"}),

			getDisplayedValue: function(){
				return this.filter(this.textbox.value);
			},

			getValue: function(){
				return this.parse(this.getDisplayedValue(), this.constraints);
			},

			setValue: function(value, /*Boolean, optional*/ priorityChange, /*String, optional*/ formattedValue){
				var filteredValue = this.filter(value);
				if((typeof filteredValue == typeof value) && (formattedValue == null || formattedValue == undefined)){
					formattedValue = this.format(filteredValue, this.constraints);
				}
				if(formattedValue != null && formattedValue != undefined){
					this.textbox.value = formattedValue;
				}
				dijit.form.TextBox.superclass.setValue.call(this, filteredValue, priorityChange);
			},

			setDisplayedValue: function(/*String*/value){
				this.textbox.value = value;
				this.setValue(this.getValue(), true);
			},

			forWaiValuenow: function(){
				return this.getDisplayedValue();
			},

			format: function(/* String */ value, /* Object */ constraints){
				// summary: Replacable function to convert a value to a properly formatted string
				return ((value == null || value == undefined) ? "" : (value.toString ? value.toString() : value));
			},

			parse: function(/* String */ value, /* Object */ constraints){
				// summary: Replacable function to convert a formatted string to a value
				return value;
			},

			postCreate: function(){
				// setting the value here is needed since value="" in the template causes "undefined"
				// and setting in the DOM (instead of the JS object) helps with form reset actions
				this.textbox.setAttribute("value", this.getDisplayedValue());
				this.inherited('postCreate', arguments);

				if(this.srcNodeRef){
					dojo.style(this.textbox, "cssText", this.style);
					this.textbox.className += " " + this["class"];
				}
				this._layoutHack();
			},

			_layoutHack: function(){
				// summary: work around table sizing bugs on FF2 by forcing redraw
				if(dojo.isFF == 2 && this.domNode.tagName=="TABLE"){
					var node=this.domNode;
					var old = node.style.opacity;
					node.style.opacity = "0.999";
					setTimeout(function(){
						node.style.opacity = old;
					}, 0);
				}			
			},

			filter: function(val){
				// summary: Apply various filters to textbox value
				if(val == undefined || val == null){ return ""; }
				else if(typeof val != "string"){ return val; }
				if(this.trim){
					val = dojo.trim(val);
				}
				if(this.uppercase){
					val = val.toUpperCase();
				}
				if(this.lowercase){
					val = val.toLowerCase();
				}
				if(this.propercase){
					val = val.replace(/[^\s]+/g, function(word){
						return word.substring(0,1).toUpperCase() + word.substring(1);
					});
				}
				return val;
			},

			// event handlers, you can over-ride these in your own subclasses
			_onBlur: function(){
				this.setValue(this.getValue(), (this.isValid ? this.isValid() : true));
			},

			onkeyup: function(){
				// TODO: it would be nice to massage the value (ie: automatic uppercase, etc) as the user types
				// but this messes up the cursor position if you are typing into the middle of a word, and
				// also trimming doesn't work correctly (it prevents spaces between words too!)
				// this.setValue(this.getValue());
			}
		}
	);

	}

if(!dojo._hasResource["dijit.form.ValidationTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.form.ValidationTextBox"] = true;
	dojo.provide("dijit.form.ValidationTextBox");

	dojo.requireLocalization("dijit.form", "validate", null, "ko,zh-cn,zh,ja,zh-tw,ru,it,hu,ROOT,fr,pt,pl,es,de,cs");

	dojo.declare(
		"dijit.form.ValidationTextBox",
		dijit.form.TextBox,
		{
			// summary:
			//		A subclass of TextBox.
			//		Over-ride isValid in subclasses to perform specific kinds of validation.

			templateString:"<table style=\"display: -moz-inline-stack;\" class=\"dijit dijitReset dijitInlineTable\" cellspacing=\"0\" cellpadding=\"0\"\n\tid=\"widget_${id}\" name=\"${name}\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset dijitInputField\" width=\"100%\"\n\t\t\t><input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete=\"off\"\n\t\t\ttype='${type}' name='${name}'\n\t\t/></td\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div><div class='dijitValidationIconText'>&Chi;</div\n\t\t></td\n\t></tr\n></table>\n",
			baseClass: "dijitTextBox",

			// default values for new subclass properties
			// required: Boolean
			//		Can be true or false, default is false.
			required: false,
			// promptMessage: String
			//		Hint string
			promptMessage: "",
			// invalidMessage: String
			// 		The message to display if value is invalid.
			invalidMessage: "$_unset_$", // read from the message file if not overridden
			// constraints: Object
			//		user-defined object needed to pass parameters to the validator functions
			constraints: {},
			// regExp: String
			//		regular expression string used to validate the input
			//		Do not specify both regExp and regExpGen
			regExp: ".*",
			// regExpGen: Function
			//		user replaceable function used to generate regExp when dependent on constraints
			//		Do not specify both regExp and regExpGen
			regExpGen: function(constraints){ return this.regExp; },
			
			// state: String
			//		Shows current state (ie, validation result) of input (Normal, Warning, or Error)
			state: "",

			setValue: function(){
				this.inherited('setValue', arguments);
				this.validate(false);
			},

			validator: function(value,constraints){
				// summary: user replaceable function used to validate the text input against the regular expression.
				return (new RegExp("^(" + this.regExpGen(constraints) + ")"+(this.required?"":"?")+"$")).test(value) &&
					(!this.required || !this._isEmpty(value)) &&
					(this._isEmpty(value) || this.parse(value, constraints) !== null);
			},

			isValid: function(/* Boolean*/ isFocused){
				// summary: Need to over-ride with your own validation code in subclasses
				return this.validator(this.textbox.value, this.constraints);
			},

			_isEmpty: function(value){
				// summary: Checks for whitespace
				return /^\s*$/.test(value); // Boolean
			},

			getErrorMessage: function(/* Boolean*/ isFocused){
				// summary: return an error message to show if appropriate
				return this.invalidMessage;
			},

			getPromptMessage: function(/* Boolean*/ isFocused){
				// summary: return a hint to show if appropriate
				return this.promptMessage;
			},

			validate: function(/* Boolean*/ isFocused){
				// summary:
				//		Called by oninit, onblur, and onkeypress.
				// description:
				//		Show missing or invalid messages if appropriate, and highlight textbox field.
				var message = "";
				var isValid = this.isValid(isFocused);
				var isEmpty = this._isEmpty(this.textbox.value);
				this.state = (isValid || (!this._hasBeenBlurred && isEmpty)) ? "" : "Error";
				this._setStateClass();
				dijit.setWaiState(this.focusNode, "invalid", (isValid? "false" : "true"));
				if(isFocused){
					if(isEmpty){
						message = this.getPromptMessage(true);
					}
					if(!message && !isValid){
						message = this.getErrorMessage(true);
					}
				}
				this._displayMessage(message);
			},

			// currently displayed message
			_message: "",

			_displayMessage: function(/*String*/ message){
				if(this._message == message){ return; }
				this._message = message;
				this.displayMessage(message);
			},

			displayMessage: function(/*String*/ message){
				// summary:
				//		User overridable method to display validation errors/hints.
				//		By default uses a tooltip.
				if(message){
					dijit.showTooltip(message, this.domNode);
				}else{
					dijit.hideTooltip(this.domNode);
				}
			},

			_hasBeenBlurred: false,

			_onBlur: function(evt){
				this._hasBeenBlurred = true;
				this.validate(false);
				this.inherited('_onBlur', arguments);
			},

			onfocus: function(evt){
				// TODO: change to _onFocus?
				this.validate(true);
				this._onMouse(evt);	// update CSS classes
			},

			onkeyup: function(evt){
				this.onfocus(evt);
			},

			//////////// INITIALIZATION METHODS ///////////////////////////////////////
			constructor: function(){
				this.constraints = {};
			},

			postMixInProperties: function(){
				this.inherited('postMixInProperties', arguments);
				this.constraints.locale=this.lang;
				this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
				if(this.invalidMessage == "$_unset_$"){ this.invalidMessage = this.messages.invalidMessage; }
				var p = this.regExpGen(this.constraints);
				this.regExp = p;
				// make value a string for all types so that form reset works well
			}
		}
	);

	dojo.declare(
		"dijit.form.MappedTextBox",
		dijit.form.ValidationTextBox,
		{
			// summary:
			//		A subclass of ValidationTextBox.
			//		Provides a hidden input field and a serialize method to override

			serialize: function(val, /*Object?*/options){
				// summary: user replaceable function used to convert the getValue() result to a String
				return (val.toString ? val.toString() : "");
			},

			toString: function(){
				// summary: display the widget as a printable string using the widget's value
				var val = this.filter(this.getValue());
				return (val!=null) ? ((typeof val == "string") ? val : this.serialize(val, this.constraints)) : "";
			},

			validate: function(){
				this.valueNode.value = this.toString();
				this.inherited('validate', arguments);
			},

			postCreate: function(){
				var textbox = this.textbox;
				var valueNode = (this.valueNode = document.createElement("input"));
				valueNode.setAttribute("type", textbox.type);
				valueNode.setAttribute("value", this.toString());
				dojo.style(valueNode, "display", "none");
				valueNode.name = this.textbox.name;
				this.textbox.name = "_" + this.textbox.name + "_displayed_";
				this.textbox.removeAttribute("name");
				dojo.place(valueNode, textbox, "after");

				this.inherited('postCreate', arguments);
			}
		}
	);

	dojo.declare(
		"dijit.form.RangeBoundTextBox",
		dijit.form.MappedTextBox,
		{
			// summary:
			//		A subclass of MappedTextBox.
			//		Tests for a value out-of-range
			/*===== contraints object:
			// min: Number
			//		Minimum signed value.  Default is -Infinity
			min: undefined,
			// max: Number
			//		Maximum signed value.  Default is +Infinity
			max: undefined,
			=====*/

			// rangeMessage: String
			//		The message to display if value is out-of-range
			rangeMessage: "",

			compare: function(val1, val2){
				// summary: compare 2 values
				return val1 - val2;
			},

			rangeCheck: function(/* Number */ primitive, /* Object */ constraints){
				// summary: user replaceable function used to validate the range of the numeric input value
				var isMin = (typeof constraints.min != "undefined");
				var isMax = (typeof constraints.max != "undefined");
				if(isMin || isMax){
					return (!isMin || this.compare(primitive,constraints.min) >= 0) &&
						(!isMax || this.compare(primitive,constraints.max) <= 0);
				}else{ return true; }
			},

			isInRange: function(/* Boolean*/ isFocused){
				// summary: Need to over-ride with your own validation code in subclasses
				return this.rangeCheck(this.getValue(), this.constraints);
			},

			isValid: function(/* Boolean*/ isFocused){
				return this.inherited('isValid', arguments) &&
					((this._isEmpty(this.textbox.value) && !this.required) || this.isInRange(isFocused));
			},

			getErrorMessage: function(/* Boolean*/ isFocused){
				if(dijit.form.RangeBoundTextBox.superclass.isValid.call(this, false) && !this.isInRange(isFocused)){ return this.rangeMessage; }
				else{ return this.inherited('getErrorMessage', arguments); }
			},

			postMixInProperties: function(){
				this.inherited('postMixInProperties', arguments);
				if(!this.rangeMessage){
					this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
					this.rangeMessage = this.messages.rangeMessage;
				}
			},

			postCreate: function(){
				this.inherited('postCreate', arguments);
				if(typeof this.constraints.min != "undefined"){
					dijit.setWaiState(this.focusNode, "valuemin", this.constraints.min);
				}
				if(typeof this.constraints.max != "undefined"){
					dijit.setWaiState(this.focusNode, "valuemax", this.constraints.max);
				}
			}
		}
	);

	}

if(!dojo._hasResource["dijit.form.ComboBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
	dojo._hasResource["dijit.form.ComboBox"] = true;
	dojo.provide("dijit.form.ComboBox");

	dojo.requireLocalization("dijit.form", "ComboBox", null, "ko,zh,ja,zh-tw,ru,it,hu,ROOT,fr,pt,pl,es,de,cs");

	dojo.declare(
		"dijit.form.ComboBoxMixin",
		null,
		{
			// summary:
			//		Auto-completing text box, and base class for FilteringSelect widget.
			//
			//		The drop down box's values are populated from an class called
			//		a data provider, which returns a list of values based on the characters
			//		that the user has typed into the input box.
			//
			//		Some of the options to the ComboBox are actually arguments to the data
			//		provider.
			//
			//		You can assume that all the form widgets (and thus anything that mixes
			//		in ComboBoxMixin) will inherit from _FormWidget and thus the "this"
			//		reference will also "be a" _FormWidget.

			// item: Object
			//		This is the item returned by the dojo.data.store implementation that
			//		provides the data for this cobobox, it's the currently selected item.
			item: null,

			// pageSize: Integer
			//		Argument to data provider.
			//		Specifies number of search results per page (before hitting "next" button)
			pageSize: Infinity,

			// store: Object
			//		Reference to data provider object used by this ComboBox
			store: null,

			// query: Object
			//		A query that can be passed to 'store' to initially filter the items,
			//		before doing further filtering based on searchAttr and the key.
			query: {},

			// autoComplete: Boolean
			//		If you type in a partial string, and then tab out of the <input> box,
			//		automatically copy the first entry displayed in the drop down list to
			//		the <input> field
			autoComplete: true,

			// searchDelay: Integer
			//		Delay in milliseconds between when user types something and we start
			//		searching based on that value
			searchDelay: 100,

			// searchAttr: String
			//		Searches pattern match against this field
			searchAttr: "name",

			// ignoreCase: Boolean
			//		Set true if the ComboBox should ignore case when matching possible items
			ignoreCase: true,

			// hasDownArrow: Boolean
			//		Set this textbox to have a down arrow button.
			//		Defaults to true.
			hasDownArrow:true,

			// _hasFocus: Boolean
			//		Represents focus state of the textbox
			// TODO: get rid of this; it's unnecessary (but currently referenced in FilteringSelect)
			_hasFocus:false,

			templateString:"<table class=\"dijit dijitReset dijitInlineTable dijitLeft\" cellspacing=\"0\" cellpadding=\"0\"\n\tid=\"widget_${id}\" name=\"${name}\" dojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\n\t><tr class=\"dijitReset\"\n\t\t><td class='dijitReset dijitStretch dijitInputField' width=\"100%\"\n\t\t\t><input type=\"text\" autocomplete=\"off\" name=\"${name}\"\n\t\t\tdojoAttachEvent=\"onkeypress, onkeyup, onfocus, compositionend\"\n\t\t\tdojoAttachPoint=\"textbox,focusNode\" waiRole=\"combobox\"\n\t\t/></td\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div\n\t\t\t><div class='dijitValidationIconText'>&Chi;</div\n\t\t></td\n\t\t><td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton' width=\"0%\"\n\t\t\tdojoAttachPoint=\"downArrowNode\"\n\t\t\tdojoAttachEvent=\"onmousedown:_onArrowMouseDown,onmouseup:_onMouse,onmouseenter:_onMouse,onmouseleave:_onMouse\"\n\t\t\t><div class=\"dijitDownArrowButtonInner\" waiRole=\"presentation\"\n\t\t\t\t><div class=\"dijitDownArrowButtonChar\">&#9660;</div\n\t\t\t></div\n\t\t></td\t\n\t></tr\n></table>\n",

			baseClass:"dijitComboBox",

			_lastDisplayedValue: "",

			getValue:function(){
				// don't get the textbox value but rather the previously set hidden value
				return dijit.form.TextBox.superclass.getValue.apply(this, arguments);
			},

			setDisplayedValue:function(/*String*/ value){
				this._lastDisplayedValue = value;
				this.setValue(value, true);
			},

			_getCaretPos: function(/*DomNode*/ element){
				// khtml 3.5.2 has selection* methods as does webkit nightlies from 2005-06-22
				if(typeof(element.selectionStart)=="number"){
					// FIXME: this is totally borked on Moz < 1.3. Any recourse?
					return element.selectionStart;
				}else if(dojo.isIE){
					// in the case of a mouse click in a popup being handled,
					// then the document.selection is not the textarea, but the popup
					// var r = document.selection.createRange();
					// hack to get IE 6 to play nice. What a POS browser.
					var tr = document.selection.createRange().duplicate();
					var ntr = element.createTextRange();
					tr.move("character",0);
					ntr.move("character",0);
					try{
						// If control doesnt have focus, you get an exception.
						// Seems to happen on reverse-tab, but can also happen on tab (seems to be a race condition - only happens sometimes).
						// There appears to be no workaround for this - googled for quite a while.
						ntr.setEndPoint("EndToEnd", tr);
						return String(ntr.text).replace(/\r/g,"").length;
					}catch(e){
						return 0; // If focus has shifted, 0 is fine for caret pos.
					}
				}
			},

			_setCaretPos: function(/*DomNode*/ element, /*Number*/ location){
				location = parseInt(location);
				this._setSelectedRange(element, location, location);
			},

			_setSelectedRange: function(/*DomNode*/ element, /*Number*/ start, /*Number*/ end){
				if(!end){
					end = element.value.length;
				}  // NOTE: Strange - should be able to put caret at start of text?
				// Mozilla
				// parts borrowed from http://www.faqts.com/knowledge_base/view.phtml/aid/13562/fid/130
				if(element.setSelectionRange){
					dijit.focus(element);
					element.setSelectionRange(start, end);
				}else if(element.createTextRange){ // IE
					var range = element.createTextRange();
					with(range){
						collapse(true);
						moveEnd('character', end);
						moveStart('character', start);
						select();
					}
				}else{ //otherwise try the event-creation hack (our own invention)
					// do we need these?
					element.value = element.value;
					element.blur();
					dijit.focus(element);
					// figure out how far back to go
					var dist = parseInt(element.value.length)-end;
					var tchar = String.fromCharCode(37);
					var tcc = tchar.charCodeAt(0);
					for(var x = 0; x < dist; x++){
						var te = document.createEvent("KeyEvents");
						te.initKeyEvent("keypress", true, true, null, false, false, false, false, tcc, tcc);
						element.dispatchEvent(te);
					}
				}
			},

			onkeypress: function(/*Event*/ evt){
				// summary: handles keyboard events

				//except for pasting case - ctrl + v(118)
				if(evt.altKey || (evt.ctrlKey && evt.charCode != 118)){
					return;
				}
				var doSearch = false;
				this.item = null; // #4872
				if(this._isShowingNow){this._popupWidget.handleKey(evt);}
				switch(evt.keyCode){
					case dojo.keys.PAGE_DOWN:
					case dojo.keys.DOWN_ARROW:
						if(!this._isShowingNow||this._prev_key_esc){
							this._arrowPressed();
							doSearch=true;
						}else{
							this._announceOption(this._popupWidget.getHighlightedOption());
						}
						dojo.stopEvent(evt);
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						break;

					case dojo.keys.PAGE_UP:
					case dojo.keys.UP_ARROW:
						if(this._isShowingNow){
							this._announceOption(this._popupWidget.getHighlightedOption());
						}
						dojo.stopEvent(evt);
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						break;

					case dojo.keys.ENTER:
						// prevent submitting form if user presses enter
						// also prevent accepting the value if either Next or Previous are selected
						var highlighted;
						if(this._isShowingNow&&(highlighted=this._popupWidget.getHighlightedOption())){
							// only stop event on prev/next
							if(highlighted==this._popupWidget.nextButton){
								this._nextSearch(1);
								dojo.stopEvent(evt);
								break;
							}
							else if(highlighted==this._popupWidget.previousButton){
								this._nextSearch(-1);
								dojo.stopEvent(evt);
								break;
							}
						}else{
							this.setDisplayedValue(this.getDisplayedValue());
						}
						// default case:
						// prevent submit, but allow event to bubble
						evt.preventDefault();
						// fall through

					case dojo.keys.TAB:
						var newvalue=this.getDisplayedValue();
						// #4617: if the user had More Choices selected fall into the _onBlur handler
						if(this._popupWidget &&
							(newvalue == this._popupWidget._messages["previousMessage"] ||
								newvalue == this._popupWidget._messages["nextMessage"])){
							break;
						}
						if(this._isShowingNow){
							this._prev_key_backspace = false;
							this._prev_key_esc = false;
							if(this._popupWidget.getHighlightedOption()){
								this._popupWidget.setValue({target:this._popupWidget.getHighlightedOption()}, true);
							}
							this._hideResultList();
						}
						break;

					case dojo.keys.SPACE:
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						if(this._isShowingNow && this._popupWidget.getHighlightedOption()){
							dojo.stopEvent(evt);
							this._selectOption();
							this._hideResultList();
						}else{
							doSearch = true;
						}
						break;

					case dojo.keys.ESCAPE:
						this._prev_key_backspace = false;
						this._prev_key_esc = true;
						this._hideResultList();
						if(this._lastDisplayedValue != this.getDisplayedValue()){
							this.setDisplayedValue(this._lastDisplayedValue);
							dojo.stopEvent(evt);
						}else{
							this.setValue(this.getValue(), false);
						}
						break;

					case dojo.keys.DELETE:
					case dojo.keys.BACKSPACE:
						this._prev_key_esc = false;
						this._prev_key_backspace = true;
						doSearch = true;
						break;

					case dojo.keys.RIGHT_ARROW: // fall through

					case dojo.keys.LEFT_ARROW: // fall through
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						break;

					default:// non char keys (F1-F12 etc..)  shouldn't open list
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						if(dojo.isIE || evt.charCode != 0){
							doSearch=true;
						}
				}
				if(this.searchTimer){
					clearTimeout(this.searchTimer);
				}
				if(doSearch){
					// need to wait a tad before start search so that the event bubbles through DOM and we have value visible
					this.searchTimer = setTimeout(dojo.hitch(this, this._startSearchFromInput), this.searchDelay);
				}
			},

			_autoCompleteText: function(/*String*/ text){
				// summary:
				// Fill in the textbox with the first item from the drop down list, and
				// highlight the characters that were auto-completed.   For example, if user
				// typed "CA" and the drop down list appeared, the textbox would be changed to
				// "California" and "ifornia" would be highlighted.

				// IE7: clear selection so next highlight works all the time
				this._setSelectedRange(this.focusNode, this.focusNode.value.length, this.focusNode.value.length);
				// does text autoComplete the value in the textbox?
				// #3744: escape regexp so the user's input isn't treated as a regular expression.
				// Example: If the user typed "(" then the regexp would throw "unterminated parenthetical."
				// Also see #2558 for the autocompletion bug this regular expression fixes.
				if(new RegExp("^"+escape(this.focusNode.value), this.ignoreCase ? "i" : "").test(escape(text))){
					var cpos = this._getCaretPos(this.focusNode);
					// only try to extend if we added the last character at the end of the input
					if((cpos+1) > this.focusNode.value.length){
						// only add to input node as we would overwrite Capitalisation of chars
						// actually, that is ok
						this.focusNode.value = text;//.substr(cpos);
						// visually highlight the autocompleted characters
						this._setSelectedRange(this.focusNode, cpos, this.focusNode.value.length);
						dijit.setWaiState(this.focusNode, "valuenow", text);
					}
				}else{
					// text does not autoComplete; replace the whole value and highlight
					this.focusNode.value = text;
					this._setSelectedRange(this.focusNode, 0, this.focusNode.value.length);
					dijit.setWaiState(this.focusNode, "valuenow", text);
				}
			},

			_openResultList: function(/*Object*/ results, /*Object*/ dataObject){
				if(this.disabled || dataObject.query[this.searchAttr] != this._lastQuery){
					return;
				}
				this._popupWidget.clearResultList();
				if(!results.length){
					this._hideResultList();
					return;
				}

				// Fill in the textbox with the first item from the drop down list, and
				// highlight the characters that were auto-completed.   For example, if user
				// typed "CA" and the drop down list appeared, the textbox would be changed to
				// "California" and "ifornia" would be highlighted.

				var zerothvalue=new String(this.store.getValue(results[0], this.searchAttr));
				if(zerothvalue && this.autoComplete && !this._prev_key_backspace &&
				// when the user clicks the arrow button to show the full list,
				// startSearch looks for "*".
				// it does not make sense to autocomplete
				// if they are just previewing the options available.
					(dataObject.query[this.searchAttr] != "*")){
					this._autoCompleteText(zerothvalue);
					// announce the autocompleted value
					dijit.setWaiState(this.focusNode || this.domNode, "valuenow", zerothvalue);
				}
				this._popupWidget.createOptions(results, dataObject, dojo.hitch(this, this._getMenuLabelFromItem));

				// show our list (only if we have content, else nothing)
				this._showResultList();

				// #4091: tell the screen reader that the paging callback finished by shouting the next choice
				if(dataObject.direction){
					if(dataObject.direction==1){
						this._popupWidget.highlightFirstOption();
					}else if(dataObject.direction==-1){
						this._popupWidget.highlightLastOption();
					}
					this._announceOption(this._popupWidget.getHighlightedOption());
				}
			},

			_showResultList: function(){
				this._hideResultList();
				var items = this._popupWidget.getItems(),
					visibleCount = Math.min(items.length,this.maxListLength);
				this._arrowPressed();
				// hide the tooltip
				this._displayMessage("");
				
				// Position the list and if it's too big to fit on the screen then
				// size it to the maximum possible height
				// Our dear friend IE doesnt take max-height so we need to calculate that on our own every time
				// TODO: want to redo this, see http://trac.dojotoolkit.org/ticket/3272, http://trac.dojotoolkit.org/ticket/4108
				with(this._popupWidget.domNode.style){
					// natural size of the list has changed, so erase old width/height settings,
					// which were hardcoded in a previous call to this function (via dojo.marginBox() call) 
					width="";
					height="";
				}
				var best=this.open();
				// #3212: only set auto scroll bars if necessary
				// prevents issues with scroll bars appearing when they shouldn't when node is made wider (fractional pixels cause this)
				var popupbox=dojo.marginBox(this._popupWidget.domNode);
				this._popupWidget.domNode.style.overflow=((best.h==popupbox.h)&&(best.w==popupbox.w))?"hidden":"auto";
				// #4134: borrow TextArea scrollbar test so content isn't covered by scrollbar and horizontal scrollbar doesn't appear
				var newwidth=best.w;
				if(best.h<this._popupWidget.domNode.scrollHeight){newwidth+=16;}
				dojo.marginBox(this._popupWidget.domNode, {h:best.h,w:Math.max(newwidth,this.domNode.offsetWidth)});
			},

			_hideResultList: function(){
				if(this._isShowingNow){
					dijit.popup.close(this._popupWidget);
					this._arrowIdle();
					this._isShowingNow=false;
				}
			},

			_onBlur: function(){
				// summary: called magically when focus has shifted away from this widget and it's dropdown
				this._hasFocus=false;
				this._hasBeenBlurred = true;
				this._hideResultList();
				this._arrowIdle();
				// if the user clicks away from the textbox OR tabs away, set the value to the textbox value
				// #4617: if value is now more choices or previous choices, revert the value
				var newvalue=this.getDisplayedValue();
				if(this._popupWidget&&(newvalue==this._popupWidget._messages["previousMessage"]||newvalue==this._popupWidget._messages["nextMessage"])){
					this.setValue(this._lastValueReported, true);
				}else{
					this.setDisplayedValue(newvalue);
				}
			},

			onfocus:function(/*Event*/ evt){
				this._hasFocus=true;
				
				// update styling to reflect that we are focused
				this._onMouse(evt);
			},

			_announceOption: function(/*Node*/ node){
				// summary:
				//	a11y code that puts the highlighted option in the textbox
				//	This way screen readers will know what is happening in the menu

				if(node==null){return;}
				// pull the text value from the item attached to the DOM node
				var newValue;
				if(node==this._popupWidget.nextButton||node==this._popupWidget.previousButton){
					newValue=node.innerHTML;
				}else{
					newValue=this.store.getValue(node.item, this.searchAttr);
				}
				// get the text that the user manually entered (cut off autocompleted text)
				this.focusNode.value=this.focusNode.value.substring(0, this._getCaretPos(this.focusNode));
				// autocomplete the rest of the option to announce change
				this._autoCompleteText(newValue);
			},

			_selectOption: function(/*Event*/ evt){
				var tgt = null;
				if(!evt){
					evt ={ target: this._popupWidget.getHighlightedOption()};
				}
					// what if nothing is highlighted yet?
				if(!evt.target){
					// handle autocompletion where the the user has hit ENTER or TAB
					this.setDisplayedValue(this.getDisplayedValue());
					return;
				// otherwise the user has accepted the autocompleted value
				}else{
					tgt = evt.target;
				}
				if(!evt.noHide){
					this._hideResultList();
					this._setCaretPos(this.focusNode, this.store.getValue(tgt.item, this.searchAttr).length);
				}
				this._doSelect(tgt);
			},

			_doSelect: function(tgt){
				this.item = tgt.item;
				this.setValue(this.store.getValue(tgt.item, this.searchAttr), true);
			},

			_onArrowMouseDown: function(evt){
				// summary: callback when arrow is clicked
				if(this.disabled){
					return;
				}
				dojo.stopEvent(evt);
				this.focus();
				if(this._isShowingNow){
					this._hideResultList();
				}else{
					// forces full population of results, if they click
					// on the arrow it means they want to see more options
					this._startSearch("");
				}
			},

			_startSearchFromInput: function(){
				this._startSearch(this.focusNode.value);
			},

			_startSearch: function(/*String*/ key){
				if(!this._popupWidget){
					this._popupWidget = new dijit.form._ComboBoxMenu({
						onChange: dojo.hitch(this, this._selectOption)
					});
				}
				// create a new query to prevent accidentally querying for a hidden value from FilteringSelect's keyField
				var query=this.query;
				this._lastQuery=query[this.searchAttr]=key+"*";
				var dataObject=this.store.fetch({queryOptions:{ignoreCase:this.ignoreCase, deep:true}, query: query, onComplete:dojo.hitch(this, "_openResultList"), start:0, count:this.pageSize});
				function nextSearch(dataObject, direction){
					dataObject.start+=dataObject.count*direction;
					// #4091: tell callback the direction of the paging so the screen reader knows which menu option to shout
					dataObject.direction=direction;
					dataObject.store.fetch(dataObject);
				}
				this._nextSearch=this._popupWidget.onPage=dojo.hitch(this, nextSearch, dataObject);
			},

			_getValueField:function(){
				return this.searchAttr;
			},

			/////////////// Event handlers /////////////////////

			_arrowPressed: function(){
				if(!this.disabled&&this.hasDownArrow){
					dojo.addClass(this.downArrowNode, "dijitArrowButtonActive");
				}
			},

			_arrowIdle: function(){
				if(!this.disabled&&this.hasDownArrow){
					dojo.removeClass(this.downArrowNode, "dojoArrowButtonPushed");
				}
			},

			compositionend: function(/*Event*/ evt){
				// summary: When inputting characters using an input method, such as Asian
				// languages, it will generate this event instead of onKeyDown event
				// Note: this event is only triggered in FF (not in IE)
				this.onkeypress({charCode:-1});
			},

			//////////// INITIALIZATION METHODS ///////////////////////////////////////
			constructor: function(){
				this.query={};
			},

			postMixInProperties: function(){
				if(!this.hasDownArrow){
					this.baseClass = "dijitTextBox";
				}
				if(!this.store){
					// if user didn't specify store, then assume there are option tags
					var items = this.srcNodeRef ? dojo.query("> option", this.srcNodeRef).map(function(node){
						node.style.display="none";
						return { value: node.getAttribute("value"), name: String(node.innerHTML) };
					}) : {};
					this.store = new dojo.data.ItemFileReadStore({data: {identifier:this._getValueField(), items:items}});

					// if there is no value set and there is an option list,
					// set the value to the first value to be consistent with native Select
					if(items && items.length && !this.value){
						// For <select>, IE does not let you set the value attribute of the srcNodeRef (and thus dojo.mixin does not copy it).
						// IE does understand selectedIndex though, which is automatically set by the selected attribute of an option tag
						this.value = items[this.srcNodeRef.selectedIndex != -1 ? this.srcNodeRef.selectedIndex : 0]
							[this._getValueField()];
					}
				}
			},

			uninitialize:function(){
				if(this._popupWidget){
					this._hideResultList();
					this._popupWidget.destroy()
				};
			},

			_getMenuLabelFromItem:function(/*Item*/ item){
				return {html:false, label:this.store.getValue(item, this.searchAttr)};
			},

			open:function(){
				this._isShowingNow=true;
				return dijit.popup.open({
					popup: this._popupWidget,
					around: this.domNode,
					parent: this
				});
			}
		}
	);

	dojo.declare(
		"dijit.form._ComboBoxMenu",
		[dijit._Widget, dijit._Templated],

		{
			// summary:
			//	Focus-less div based menu for internal use in ComboBox

			templateString:"<div class='dijitMenu' dojoAttachEvent='onmousedown,onmouseup,onmouseover,onmouseout' tabIndex='-1' style='overflow:\"auto\";'>"
					+"<div class='dijitMenuItem dijitMenuPreviousButton' dojoAttachPoint='previousButton'></div>"
					+"<div class='dijitMenuItem dijitMenuNextButton' dojoAttachPoint='nextButton'></div>"
				+"</div>",
			_messages:null,

			postMixInProperties:function(){
				this._messages = dojo.i18n.getLocalization("dijit.form", "ComboBox", this.lang);
				this.inherited("postMixInProperties", arguments);
			},

			setValue:function(/*Object*/ value){
				this.value=value;
				this.onChange(value);
			},

			onChange:function(/*Object*/ value){},
			onPage:function(/*Number*/ direction){},

			postCreate:function(){
				// fill in template with i18n messages
				this.previousButton.innerHTML=this._messages["previousMessage"];
				this.nextButton.innerHTML=this._messages["nextMessage"];
				this.inherited("postCreate", arguments);
			},

			onClose:function(){
				this._blurOptionNode();
			},

			_createOption:function(/*Object*/ item, labelFunc){
				// summary: creates an option to appear on the popup menu
				// subclassed by FilteringSelect

				var labelObject=labelFunc(item);
				var menuitem = document.createElement("div");
				if(labelObject.html){menuitem.innerHTML=labelObject.label;}
				else{menuitem.appendChild(document.createTextNode(labelObject.label));}
				// #3250: in blank options, assign a normal height
				if(menuitem.innerHTML==""){
					menuitem.innerHTML="&nbsp;"
				}
				menuitem.item=item;
				return menuitem;
			},

			createOptions:function(results, dataObject, labelFunc){
				//this._dataObject=dataObject;
				//this._dataObject.onComplete=dojo.hitch(comboBox, comboBox._openResultList);
				// display "Previous . . ." button
				this.previousButton.style.display=dataObject.start==0?"none":"";
				// create options using _createOption function defined by parent ComboBox (or FilteringSelect) class
				// #2309: iterate over cache nondestructively
				var _this=this;
				dojo.forEach(results, function(item){
					var menuitem=_this._createOption(item, labelFunc);
					menuitem.className = "dijitMenuItem";
					_this.domNode.insertBefore(menuitem, _this.nextButton);
				});
				// display "Next . . ." button
				this.nextButton.style.display=dataObject.count==results.length?"":"none";
			},

			clearResultList:function(){
				// keep the previous and next buttons of course
				while(this.domNode.childNodes.length>2){
					this.domNode.removeChild(this.domNode.childNodes[this.domNode.childNodes.length-2]);
				}
			},

			// these functions are called in showResultList
			getItems:function(){
				return this.domNode.childNodes;
			},

			getListLength:function(){
				return this.domNode.childNodes.length-2;
			},

			onmousedown:function(/*Event*/ evt){
				dojo.stopEvent(evt);
			},

			onmouseup:function(/*Event*/ evt){
				if(evt.target === this.domNode){
					return;
				}else if(evt.target==this.previousButton){
					this.onPage(-1);
				}else if(evt.target==this.nextButton){
					this.onPage(1);
				}else{
					var tgt=evt.target;
					// while the clicked node is inside the div
					while(!tgt.item){
						// recurse to the top
						tgt=tgt.parentNode;
					}
					this.setValue({target:tgt}, true);
				}
			},

			onmouseover:function(/*Event*/ evt){
				if(evt.target === this.domNode){ return; }
				var tgt=evt.target;
				if(!(tgt==this.previousButton||tgt==this.nextButton)){
					// while the clicked node is inside the div
					while(!tgt.item){
						// recurse to the top
						tgt=tgt.parentNode;
					}
				}
				this._focusOptionNode(tgt);
			},

			onmouseout:function(/*Event*/ evt){
				if(evt.target === this.domNode){ return; }
				this._blurOptionNode();
			},

			_focusOptionNode:function(/*DomNode*/ node){
				// summary:
				//	does the actual highlight
				if(this._highlighted_option != node){
					this._blurOptionNode();
					this._highlighted_option = node;
					dojo.addClass(this._highlighted_option, "dijitMenuItemHover");
				}
			},

			_blurOptionNode:function(){
				// summary:
				//	removes highlight on highlighted option
				if(this._highlighted_option){
					dojo.removeClass(this._highlighted_option, "dijitMenuItemHover");
					this._highlighted_option = null;
				}
			},

			_highlightNextOption:function(){
				// because each press of a button clears the menu,
				// the highlighted option sometimes becomes detached from the menu!
				// test to see if the option has a parent to see if this is the case.
				if(!this.getHighlightedOption()){
					this._focusOptionNode(this.domNode.firstChild.style.display=="none"?this.domNode.firstChild.nextSibling:this.domNode.firstChild);
				}else if(this._highlighted_option.nextSibling&&this._highlighted_option.nextSibling.style.display!="none"){
					this._focusOptionNode(this._highlighted_option.nextSibling);
				}
				// scrollIntoView is called outside of _focusOptionNode because in IE putting it inside causes the menu to scroll up on mouseover
				dijit.scrollIntoView(this._highlighted_option);
			},

			highlightFirstOption:function(){
				// highlight the non-Previous choices option
				this._focusOptionNode(this.domNode.firstChild.nextSibling);
				dijit.scrollIntoView(this._highlighted_option);
			},

			highlightLastOption:function(){
				// highlight the noon-More choices option
				this._focusOptionNode(this.domNode.lastChild.previousSibling);
				dijit.scrollIntoView(this._highlighted_option);
			},

			_highlightPrevOption:function(){
				// if nothing selected, highlight last option
				// makes sense if you select Previous and try to keep scrolling up the list
				if(!this.getHighlightedOption()){
					this._focusOptionNode(this.domNode.lastChild.style.display=="none"?this.domNode.lastChild.previousSibling:this.domNode.lastChild);
				}else if(this._highlighted_option.previousSibling&&this._highlighted_option.previousSibling.style.display!="none"){
					this._focusOptionNode(this._highlighted_option.previousSibling);
				}
				dijit.scrollIntoView(this._highlighted_option);
			},

			_page:function(/*Boolean*/ up){
				var scrollamount=0;
				var oldscroll=this.domNode.scrollTop;
				var height=parseInt(dojo.getComputedStyle(this.domNode).height);
				// if no item is highlighted, highlight the first option
				if(!this.getHighlightedOption()){this._highlightNextOption();}
				while(scrollamount<height){
					if(up){
						// stop at option 1
						if(!this.getHighlightedOption().previousSibling||this._highlighted_option.previousSibling.style.display=="none"){break;}
						this._highlightPrevOption();
					}else{
						// stop at last option
						if(!this.getHighlightedOption().nextSibling||this._highlighted_option.nextSibling.style.display=="none"){break;}
						this._highlightNextOption();
					}
					// going backwards
					var newscroll=this.domNode.scrollTop;
					scrollamount+=(newscroll-oldscroll)*(up ? -1:1);
					oldscroll=newscroll;
				}
			},

			pageUp:function(){
				this._page(true);
			},

			pageDown:function(){
				this._page(false);
			},

			getHighlightedOption:function(){
				// summary:
				//	Returns the highlighted option.
				return this._highlighted_option&&this._highlighted_option.parentNode ? this._highlighted_option : null;
			},

			handleKey:function(evt){
				switch(evt.keyCode){
					case dojo.keys.DOWN_ARROW:
						this._highlightNextOption();
						break;
					case dojo.keys.PAGE_DOWN:
						this.pageDown();
						break;	
					case dojo.keys.UP_ARROW:
						this._highlightPrevOption();
						break;
					case dojo.keys.PAGE_UP:
						this.pageUp();
						break;	
				}
			}
		}
	);

	dojo.declare(
		"dijit.form.ComboBox",
		[dijit.form.ValidationTextBox, dijit.form.ComboBoxMixin],
		{
			postMixInProperties: function(){
				dijit.form.ComboBoxMixin.prototype.postMixInProperties.apply(this, arguments);
				dijit.form.ValidationTextBox.prototype.postMixInProperties.apply(this, arguments);
			}
		}
	);

	}


dojo.i18n._preloadLocalizations("dijit.nls.dijit-all", ["es-es", "es", "hu", "it-it", "de", "pt-br", "pl", "fr-fr", "zh-cn", "pt", "en-us", "zh", "ru", "xx", "fr", "zh-tw", "it", "cs", "en-gb", "de-de", "ja-jp", "ko-kr", "ko", "en", "ROOT", "ja"]);

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/dijit/dijit-aipo.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck-all.js
 */
/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck.js
 */
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

window.aimluck = window.aimluck || {};

aimluck.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = aimluck;


    for (var i=(levels[0] == "aimluck") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

djConfig = { isDebug: false };

function getObjectById(id) {
    if(document.getElementById) return document.getElementById(id) //e5,e6,n6,m1,o6
    else if(document.all)       return document.all(id)            //e4
    else if(document.layers)    return document.layers[id]         //n4
}

function ew(button) {
  disableButton(button.form);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit(); 
}

function dw(button) {
  if(confirm('\u3053\u306e'+button.form.name+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    disableButton(button.form);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  } 
}

function ews(button) {
  disableButton(button.form);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit();
}

function dws(button) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form.name+'\u3092\u3059\u3079\u3066\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    disableButton(button.form);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  } 
}

function setHiddenValue(button) {
  if (button.name) {
    var q = document.createElement('input');
    q.type = 'hidden';
    q.name = button.name;
    q.value = button.value;
    button.form.appendChild(q);
  }
}

function disableSubmit(form) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'submit') {
      elements[i].disabled = true;
    }
  }
}

function disableButton(form) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'button') {
      elements[i].disabled = true;
    }
  }
}

function check_new_mail(button, current_page) {
  button.form.action = button.form.action + '?confirmlasttime=true&start=' + current_page;
  button.form.submit(); 
}

function createAction(button) {
   button.form.action = button.form.action + '?' + button.name + '=1';
}

function verifyCheckBox(form, action, button) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
    alert("\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
   	return false;
  }else{
    return action(button);
  }
}

function submit_member(select) {
	var t_o = select.options;
	for(i = 0 ; i < t_o.length; i++ ) {
	  t_o[i].selected = true;
	}
}

function add_option(select, value, text, is_selected) {
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
			select.options.remove(0);
	  }
	  select.add(option, select.options.length);
    //select.options[length].selected = is_selected;
  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
    	select.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);
    //select.options[length].selected = is_selected;
  }
}

function add_member(select_member_from, select_member_to) {
  if (document.all) {
		var f_o = select_member_from.options;
		var t_o = select_member_to.options;
		if (f_o.length == 1 && f_o[0].value == "") return;
		for(i = 0 ; i < f_o.length; i ++ ) {
			if(!f_o[i].selected) continue;
			var iseq = false;
		
			for( j = 0 ; j < t_o.length; j ++ ) {
		  	if( t_o[j].value == f_o[i].value ) {
		    	iseq = true;
		    	break;
		  	}
			}
		
			if(iseq) continue;
			var option = document.createElement("OPTION");
			option.value = f_o[i].value;
			option.text = f_o[i].text;
			option.selected = true;
    	if (t_o.length == 1 && t_o[0].value == ""){
				t_o.remove(0);
	  	}
			t_o.add(option, t_o.length);
		}
  } else {
		var f_o = select_member_from.options;
		var t_o = select_member_to.options;
		if (f_o.length == 1 && f_o[0].value == "") return;
		for(i = 0 ; i < f_o.length; i ++ ) {
			if(!f_o[i].selected) continue;
			var iseq = false;
		
			for( j = 0 ; j < t_o.length; j ++ ) {
		  	if( t_o[j].value == f_o[i].value ) {
		    	iseq = true;
		    	break;
		  	}
			}
		
			if(iseq) continue;
			var option = document.createElement("OPTION");
			option.value = f_o[i].value;
			option.text = f_o[i].text;
			option.selected = true;
    	if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
    		select_member_to.removeChild(select_member_to.options[0]);
    	}
			select_member_to.insertBefore(option, t_o[t_o.length]);
		}
  }
}

function remove_member(select) {
  if (document.all) {
  	var t_o = select.options;
	  for(i = 0 ;i < t_o.length; i ++ ) {
		  if( t_o[i].selected ) {
	  	  t_o.remove(i);
	    	i -= 1;
	  	} 
		}
  } else {
  	var t_o = select.options;
	  for(i = 0 ;i < t_o.length; i ++ ) {
		  if( t_o[i].selected ) {
				select.removeChild(t_o[i]);
	    	i -= 1; 
			}
		}
  }

  if(t_o.length == 0){
		add_option(select, '', '\u3000', false)
  }
}

function doUpOptions10(select) {
  var s_o = select.options;
  for(i = 0; i < s_o.length; i++ ) {
  	if(!s_o[i].selected) continue;
    if(i == 0) continue;
	if(s_o[i-1].selected) continue;
	up_option(select, i, 10);
  }
}

function doUpOptions(select) {
  var s_o = select.options;
  for(i = 0; i < s_o.length; i++ ) {
  	if(!s_o[i].selected) continue;
    if(i == 0) continue;
    if(s_o[i-1].selected) continue;
    up_option(select, i, 1);
  }
}

function doDownOptions10(select) {
  var s_o = select.options;
  for(i = s_o.length-1; i >= 0; i-- ) {
  	if(!s_o[i].selected) continue;
    if(i == s_o.length-1) continue;
    if(s_o[i+1].selected) continue;
    down_option(select, i, 10);
  }
}

function doDownOptions(select) {
  var s_o = select.options;
  for(i = s_o.length-1; i >= 0; i-- ) {
  	if(!s_o[i].selected) continue;
    if(i == s_o.length-1) continue;
    if(s_o[i+1].selected) continue;
    down_option(select, i, 1);
  }
}

function up_option(select, index, rate) {  
  var s_o = select.options; 
  var delta = 0;
  if(index - rate >= 0){
    delta = index - rate;
  }else{
  	for(i = 0; i < s_o.length; i++ ) {
	    if(! s_o[i].selected){
	  	  delta = i;
	  	  break;
	    }
    }
  }

  change_turn_option(select, index, delta);
}

function down_option(select, index, rate) {
  var s_o = select.options; 
  var delta = 0;
  if(s_o.length - 1 - index - rate >= 0){
  	delta = index + rate;
  }else{
  	for(i = s_o.length-1; i >= 0; i-- ) {
	    if(! s_o[i].selected){
	  	  delta = i;
	  	  break;
	    } 
    }
  }
  
  change_turn_option(select, index, delta);
}


function change_turn_option(select, index, delta) {
  var s_o = select.options;
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = s_o[index].value;
    option.text = s_o[index].text;
    option.selected = true;
    select.remove(index);
    s_o.add(option, delta);
    s_o[delta].selected = true;
  } else {
    var option = document.createElement("OPTION");
    option.value = s_o[index].value;
    option.text = s_o[index].text;
    option.selected = true;
    select.removeChild(s_o[index]);
    select.insertBefore(option, s_o[delta]);
    s_o[delta].selected = true;
  }
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/dnd/Draggable.js
 */
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

dojo.provide("aimluck.dnd.DragMoveObject");
dojo.provide("aimluck.dnd.Draggable");

dojo.require("dojo.dnd.Moveable");
dojo.require("dojo.parser");
dojo.require("dojo.dnd.Source");

// aimluck.dnd.Draggable

dojo.declare("aimluck.dnd.DragMoveObject", [dojo.dnd.Mover] , {
    _pageY: 0,
    _pageX: 0,
    portletId: null,
    leftTop: null,
    onFirstMove:function (e) {
	    dojo.dnd.Mover.prototype.onFirstMove.apply(this, arguments);
    },
    onMouseUp:function (e) {
        dojo.dnd.Mover.prototype.onMouseUp.apply(this, arguments);
    },
    onMouseDown: function(e){
        var m = this.marginBox;
//        this._origOpacity_  = dojo.style(this.node, "opacity");
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
        dojo.dnd.Mover.prototype.onMouseDown.apply(this, arguments);
    },
    onMouseMove: function(e){
        this._pageX = e.pageX;
        this._pageY = e.pageY;  
        dojo.dnd.autoScroll(e);
        var m = this.marginBox;
        this.leftTop = {l: m.l + e.pageX, t: m.t + e.pageY};
    //  dojo.dnd.Mover.prototype.onMouseMove.apply(this, arguments);
    } 
});

dojo.declare("aimluck.dnd.Draggable", dojo.dnd.Moveable , {
    DragMoveObject: aimluck.dnd.DragMoveObject,
    portletId: null,
    constructor: function(node, params){
        this.portletId = params.pid;
    },
    onMouseDown: function(e){
        // summary: event processor for onmousedown, creates a Mover for the node
        // e: Event: mouse event
                
        if(this.skip && dojo.dnd.isFormElement(e)){ return; }
        if(this.delay){
            this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"));
            this.events.push(dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
        }else{
            dragObj = new this.DragMoveObject(this.node, e, this);
            dragObj.dragSource=this;
            dragObj.portletId = this.portletId;
        }
        
        dragObj._pageX = e.pageX;
        dragObj._pageY = e.pageY;
        
        this._lastX = e.pageX;
        this._lastY = e.pageY;
        
        dojo.stopEvent(e);
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/dnd/Draggable.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/io/form.js
 */
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

aimluck.namespace("aimluck.io");
dojo.provide("aimluck.io");

aimluck.io.submit = function(form, indicator_id, portlet_id, callback) {
    aimluck.io.disableForm(form, true);

    var obj_indicator = dojo.byId(indicator_id + portlet_id);
    if(obj_indicator){
       dojo.style(obj_indicator, "display" , "");
    }

    try{
        dojo.xhrPost({
            url: form.action,
            timeout: 30000,
            form: form,
            encoding: "utf-8",
            handleAs: "json-comment-filtered",
            headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
            load: function (response, ioArgs){
                var html = "";
                if(dojo.isArray(response) && response.length > 0) {
                    if(response[0] == "PermissionError"){
                        html += "<ul>";
                        html += "<li><span class='caution'>" + response[1] + "</span></li>";
                        html += "</ul>";
                    }else{
                        html += "<ul>";
                        dojo.forEach(response, function(msg) {
                            html += "<li><span class='caution'>" + msg + "</span></li>";
                        });
                        html += "</ul>";
                    }
                }
                callback.call(callback, html);

                obj_indicator = dojo.byId(indicator_id + portlet_id);
                if(obj_indicator){
                   dojo.style(obj_indicator, "display" , "none");
                }

                if (html != "") {
                    aimluck.io.disableForm(form, false);
                }
            },
            error: function (error) {
            }
        });
    } catch(E) {
    };

    return false;
}



aimluck.io.sendData = function(url, params, callback) {
   var callbackArgs = new Array();
   callbackArgs["callback"] = callback;
   aimluck.io.sendRawData(url,params,sendErrorData,callbackArgs)


    return false;
}
aimluck.io.sendErrorData = function(callbackArgs,rtnData){
    var html = "";
    if(dojo.isArray(rtnData["data"]) && rtnData["data"].length > 0) {
      html += "<ul>";
      dojo.forEach(rtnData["data"], function(msg) {
        html += "<li>" + msg + "</li>";
      });
      html += "</ul>";
    }

    callbackArgs["callback"].call(callbackArgs["callback"], html);

    return false;
}
aimluck.io.sendRawData = function(url, params,callback, callbackArgs) {
   var rtnData = new Array;
   try{
        dojo.xhrGet({
            url: url,
            method: "POST",
            encoding: "utf-8",
            content: params,
            mimetype: "text/json",
            sync:true,
            load: function(type, data, event, args) {
               rtnData["type"]=type;
               rtnData["data"]=data;
               rtnData["event"]=event;
               rtnData["args"]=args;
               rtnData["bool"]=true;
               callback.call(callback,callbackArgs,rtnData);
               return rtnData;
            }
        });
    } catch(E) {
        alert("error");
    };

}




aimluck.io.disableForm = function(form, bool) {
  var elements = form.elements;
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].type == 'submit' || elements[i].type == 'button') {
      elements[i].disabled = bool;
    }
  }
}

aimluck.io.actionSubmit = function(button) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = button.form.action + '?' + button.name + '=1';
  button.form.submit();
}

aimluck.io.ajaxActionSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = url;
  aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
}


aimluck.io.actionSubmitReturn = function(button, rtn) {
  aimluck.io.disableForm(button.form, true);
  aimluck.io.setHiddenValue(button);
  button.form.action = button.form.action + '?' + button.name + '=1&action=' + rtn;
  button.form.submit();
}

aimluck.io.deleteSubmit = function(button) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

aimluck.io.ajaxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.ajaxEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.ajaxDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form, indicator_id, portlet_id, receive);
  }
}

aimluck.io.deleteSubmitReturn = function(button, rtn) {
  if(confirm('\u3053\u306e'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1&action=' + rtn;
    button.form.submit();
  }
}

aimluck.io.multiDeleteSubmit = function(button) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = button.form.action + '?' + button.name + '=1';
    button.form.submit();
  }
}

aimluck.io.ajaxMultiDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u524a\u9664\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u6709\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.ajaxMultiDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  if(confirm('\u9078\u629e\u3057\u305f'+button.form._name.value+'\u3092\u7121\u52b9\u5316\u3057\u3066\u3088\u308d\u3057\u3044\u3067\u3059\u304b\uff1f')) {
    aimluck.io.disableForm(button.form, true);
    aimluck.io.setHiddenValue(button);
    button.form.action = url;
    aimluck.io.submit(button.form,indicator_id,portlet_id,receive);
  }
}

aimluck.io.setHiddenValue = function(button) {
  if (button.name) {
    var q = document.createElement('input');
    q.type = 'hidden';
    q.name = button.name;
    q.value = button.value;
    button.form.appendChild(q);
  }
}

aimluck.io.openDialog = function(button, url, portlet_id, callback) {
  aimluck.io.disableForm(button.form, true);
  aipo.common.showDialog(url, portlet_id, callback);
}

aimluck.io.checkboxActionSubmit = function(button) {
  aimluck.io.verifyCheckbox( button.form, aimluck.io.actionSubmit, button );
}

aimluck.io.ajaxCheckboxActionSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxActionSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.checkboxDeleteSubmit = function(button) {
  aimluck.io.verifyCheckbox( button.form, aimluck.io.multiDeleteSubmit, button );
}

aimluck.io.ajaxCheckboxDeleteSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiDeleteSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.ajaxCheckboxEnableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiEnableSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.ajaxCheckboxDisableSubmit = function(button, url, indicator_id, portlet_id, receive) {
  aimluck.io.ajaxVerifyCheckbox( button.form, aimluck.io.ajaxMultiDisableSubmit, button, url, indicator_id, portlet_id, receive );
}

aimluck.io.verifyCheckbox = function(form, action, button) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
    alert("\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
    return false;
  }else{
    return action(button);
  }
}

aimluck.io.ajaxVerifyCheckbox = function(form, action, button, url, indicator_id, portlet_id, receive ) {
  var cnt=0;
  var i;
  for(i =0; i< form.elements.length;i++){
    if(form.elements[i].checked) cnt++;
  }
  if(cnt == 0){
    alert("\u30c1\u30a7\u30c3\u30af\u30dc\u30c3\u30af\u30b9\u3092\uff11\u3064\u4ee5\u4e0a\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044\u3002");
    return false;
  }else{
    return action(button, url, indicator_id, portlet_id, receive );
  }
}

aimluck.io.createOptions = function(selectId, params) {
  var sel, pre, key, value, url, ind;
  if (params["url"]) {
      url = params["url"];
  }
  if (params["key"]) {
      key = params["key"];
  }
  if (params["value"]) {
      value = params["value"];
  }
  if (typeof params["selectedId"] == "undefined") {
  } else {
      sel = params["selectedId"];
  }
  if (typeof params["preOptions"] == "undefined") {
  } else {
      pre = params["preOptions"];
  }
  if (typeof params["indicator"] == "undefined") {
  } else {
      ind = params["indicator"];
      var indicator = dojo.byId(ind);
      if (indicator) {
        dojo.style(indicator, "display" , "none");
      }
  }

  dojo.xhrGet({
      url: url,
      timeout: 10000,
      encoding: "utf-8",
      handleAs: "json-comment-filtered",
      headers: { X_REQUESTED_WITH: "XMLHttpRequest" },
      load: function(response, ioArgs) {
          var select = dojo.byId(selectId);
          select.options.length = 0;

          if(typeof pre == "undefined") {
          } else {
              aimluck.io.addOption(select, pre["key"], pre["value"], false);
          }
          dojo.forEach(response, function(p) {
              if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
              } else {
                  if (p[key] == sel) {
                      aimluck.io.addOption(select, p[key], p[value], true);
                  } else {
                      aimluck.io.addOption(select, p[key], p[value], false);
                  }
              }
          });
          if (indicator) {
            dojo.style(indicator, "display" , "none");
          }
      }
  });
}

aimluck.io.addOption = function(select, value, text, is_selected) {
  if (document.all) {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.options.remove(0);
        //selectsub.options.remove(0);
      }
      select.add(option, select.options.length);
    //selectsub.add(option, select.options.length);
    //select.options[length].selected = is_selected;
  } else {
    var option = document.createElement("OPTION");
    option.value = value;
    option.text = text;
    option.selected = is_selected;
    if (select.options.length == 1 && select.options[0].value == ""){
        select.removeChild(select.options[0]);
        //selectsub.removeChild(select.options[0]);
    }
    select.insertBefore(option, select.options[select.options.length]);
    //selectsub.insertBefore(option, select.options[select.options.length]);
    //select.options[length].selected = is_selected;
  }
}

aimluck.io.removeOptions = function(select){
  if (document.all) {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
          t_o.remove(i);
            i -= 1;
        }
        }
  } else {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
                select.removeChild(t_o[i]);
            i -= 1;
            }
        }
  }

  if(t_o.length == 0){
        add_option(select, '', '\u3000', false)
  }
}

aimluck.io.removeAllOptions = function(select){
  if(select.options.length == 0) return;

  aimluck.io.selectAllOptions(select);

  if (document.all) {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
          t_o.remove(i);
            i -= 1;
        }
        }
  } else {
    var t_o = select.options;
      for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
                select.removeChild(t_o[i]);
            i -= 1;
            }
        }
  }

  if(t_o.length == 0){
        add_option(select, '', '\u3000', false);
  }
}

aimluck.io.selectAllOptions = function (select) {
    var t_o = select.options;
    if(t_o.length == 0) return;
    for(i = 0 ; i < t_o.length; i++ ) {
      t_o[i].selected = true;
    }
}

aimluck.io.switchCheckbox = function(checkbox) {
  var element;

  if (checkbox.checked) {
    for (i = 0; i < checkbox.form.elements.length; i++) {
      element = checkbox.form.elements[i];
      if(! element.disabled){
        element.checked = true;
      }
    }
  }
  else {
    for (i = 0; i < checkbox.form.elements.length; i++) {
      element = checkbox.form.elements[i];
      if(! element.disabled){
        element.checked = false;
      }
    }
  }
}


/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/io/form.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/form.js
 */
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

aimluck.namespace("utils.form");

aimluck.utils.form.createSelect = function(selectid, divid, url, key, value, sel, pre, att) {
    dojo.xhrGet({
        url: url,
        timeout: 5000,
        encoding: "utf-8",
        handleAs: "json-comment-filtered",
        headers: { X_REQUESTED_WITH: "XMLHttpRequest" }, 
        load: function (respodatanse, ioArgs){
            var html = "";
            if (typeof att == "undefined") {
                html += '<select name="'+ selectid + '">';
            } else {
                html += '<select name="'+ selectid + '" ' + att + '/>';
            }
            if (typeof pre == "undefined") {
                html += '';
            } else {
                html += pre;
            }
            dojo.forEach(respodatanse, function(p) {
                if(typeof p[key] == "undefined" || typeof p[value] == "undefined") {
                } else {
                    if (p[key] == sel) {
                        html += "<option value='"+p[key]+"' selected='selected'>"+ p[value]+"</option>";
                    } else {
                        html += "<option value='"+p[key]+"'>"+ p[value]+"</option>";
                    }
                }
            });
            html += '</select>';
            dojo.byId(divid).innerHTML = html;
        }
    });
};

aimluck.utils.form.switchDisplay = function (viewId, hideId) {
    dojo.html.setDisplay(dojo.byId(hideId),"none");
    dojo.html.setDisplay(dojo.byId(viewId),"");
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/form.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/utils.js
 */
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

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/utils/utils.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Contentpane.js
 */
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

dojo.provide("aimluck.widget.Contentpane");

dojo.require("dijit.layout.ContentPane");

dojo.declare(
	"aimluck.widget.Contentpane",
	[dijit.layout.ContentPane],
	{
        loadingMessage:"<div class='indicator'>\u8aad\u307f\u8fbc\u307f\u4e2d...</div>",
        errorMessage:"",
        extractContent: false,
        parseOnLoad: true,
        refreshOnShow: true,
        params: new Array(),
        reloadIds: new Array(),
		viewPage: function(href){
			this.href = href;
		    return this._prepareLoad(true);
		},
        setParam: function(key, value) {
            this.params[key] = value;
        },
        setReloadIds: function(values) {
            this.reloadIds = values;
        },
        clearParams: function() {
            this.params = new Array();
        },
        clearReloadIds: function() {
            this.reloadIds = new Array();
        },
		_downloadExternalContent: function(){
			this._onUnloadHandler();
	
			// display loading message
			// TODO: maybe we should just set a css class with a loading image as background?
			/*
			this._setContent(
				this.onDownloadStart.call(this)
			);
	        */
			var self = this;
			var getArgs = {
				preventCache: (this.preventCache || this.refreshOnShow),
				url: this.href,
				handleAs: "text",
                content: this.params,
			    headers: { X_REQUESTED_WITH: "XMLHttpRequest" }
			};
	
			if(dojo.isObject(this.ioArgs)){
				dojo.mixin(getArgs, this.ioArgs);
			}
	
			var hand = this._xhrDfd = (this.ioMethod || dojo.xhrPost)(getArgs);

			hand.addCallback(function(html){
                self.clearParams();
                self.clearReloadIds();
				try{
					self.onDownloadEnd.call(self);
					self._isDownloaded = true;
					self.setContent.call(self, html); // onload event is called from here
				}catch(err){
					self._onError.call(self, 'Content', err); // onContentError
				}
				delete self._xhrDfd;
				return html;
			});
	
			hand.addErrback(function(err){
				if(!hand.cancelled){
					// show error message in the pane
					self._onError.call(self, 'Download', err); // onDownloadError
				}
				delete self._xhrDfd;
				return err;
			});
		}
    }	
);

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Contentpane.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dialog.js
 */
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

dojo.provide("aimluck.widget.Dialog");
dojo.provide("aimluck.widget.DialogUnderlay");

dojo.require("dijit.Dialog");

dojo.declare(
    "aimluck.widget.DialogUnderlay",
    [dijit.DialogUnderlay],
    {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node'></div></div>"
    }

);

dojo.declare( "aimluck.widget.Timeout",  [dijit._Widget, dijit._Templated] , {
       templateString: "<div class=modalDialogUnderlayWrapper id='${id}_underlay'><div class=modalDialogUnderlay dojoAttachPoint='node' redirecturl=\"${redirectUrl}\"></div></div>",
       redirectUrl:"about:blank",
       postCreate: function(){
    window.location.href = this.redirectUrl;
      }
});

dojo.declare(
    "aimluck.widget.Dialog",
    [dijit.Dialog],
    {
        widgetId: null,
        loadingMessage:"<div class='indicatorDialog'><div class='indicator'>\u8aad\u307f\u8fbc\u307f\u4e2d...</div></div>",
        templateString:null,
        templateString:"<div id='modalDialog' class='modalDialog' dojoattachpoint='wrapper'><span dojoattachpoint='tabStartOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap'tabindex='0'></span><span dojoattachpoint='tabStart' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><div dojoattachpoint='containerNode' style='position: relative; z-index: 2;'></div><span dojoattachpoint='tabEnd' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span><span dojoattachpoint='tabEndOuter' dojoonfocus='trapTabs' dojoonblur='clearTrap' tabindex='0'></span></div>",//<div dojoAttachPoint=\"titleBar\" class=\"modalDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">&nbsp;</div>
        duration: 10,
        extractContent: false,
        parseOnLoad: true,
        refreshOnShow: true,
        isPositionLock: false,
        params: new Array(),
        reloadIds: new Array(),
        _portlet_id: null,
        _callback:null,
        _setup: function(){
            // summary:
            //      stuff we need to do before showing the Dialog for the first
            //      time (but we defer it until right beforehand, for
            //      performance reasons)

            this._modalconnects = [];
            
            if(this.titleBar){
                this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
                var _tmpnode = this.domNode;
                dojo.connect(this._moveable, "onMoving", function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
                        
                        var viewport = dijit.getViewport();
                        var w1 = parseInt(dojo.getComputedStyle(_tmpnode).width);
			            var w2 = parseInt(viewport.w);

	                    if(leftTop.l < 0){
	                       leftTop.l = 0; 
	                    }
	                    
	                    if(leftTop.l + w1 > w2){
	                       leftTop.l = w2 - w1;
	                    }
	                   
	                    if(leftTop.t < 0){
	                       leftTop.t = 0
	                    }
                });
            }

            this._underlay = new aimluck.widget.DialogUnderlay();

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
                    dialog: this,
                    duration: this.duration,
                    onEnd: function(){
                        node.style.display="none";
                        //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
                        if (document.all) { // for IE
                            this.dialog.fixTmpScroll();
                        }
                        //**//
                    }
                 }),
                 dojo.fadeOut({
                    node: this._underlay.domNode,
                    duration: this.duration,
                    onEnd: dojo.hitch(this._underlay, "hide")
                 })
                ]
            );
        },
        fixTmpScroll: function(){
            //** FIXME IEで追加ダイアログを閉じるとスクロールバーのｙ座標が強制的に０になってしまう現象
            var _tmpNode = dojo.byId('weeklyScrollPane_'+this._portlet_id);
            if(_tmpNode){
                if (aipo.schedule.tmpScroll == "undefined") {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = ptConfig[this._portlet_id].contentScrollTop;
                } else {
                    dojo.byId('weeklyScrollPane_'+this._portlet_id).scrollTop = aipo.schedule.tmpScroll;
                }
            }
            //**//
        },
        onLoad: function(){
            // when href is specified we need to reposition
            // the dialog after the data is loaded
            this._position();
            dijit.Dialog.superclass.onLoad.call(this);
            this.isPositionLock = false;
            
            var focusNode = dojo.byId( this.widgetId );
            if ( focusNode ) {
                focusNode.focus();
                
                if (this._callback != null) {
                    this._callback.call(this._callback, this._portlet_id);
                }
            } 
        },
        setCallback: function(portlet_id, callback) {
            this._portlet_id = portlet_id;
            this._callback = callback;
        },
        setParam: function(key, value) {
            this.params[key] = value;
        },
        setReloadIds: function(values) {
            this.reloadIds = values;
        },
        clearParams: function() {
            this.params = new Array();
        },
        clearReloadIds: function() {
            this.reloadIds = new Array();
        },
        reload: function (url) {
            this.href = url;
            this.isPositionLock = true;
            this.refresh();
        },
        _position: function(){
            // summary: position modal dialog in center of screen
            
            if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
            var viewport = dijit.getViewport();
            var mb = dojo.marginBox(this.domNode);

            var style = this.domNode.style;
            style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
            if(Math.floor((viewport.t + (viewport.h - mb.h)/2)) > 0){
                style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
            } else {
                style.top = 0 + "px";
            }
        },
        layout: function() {
            if(this.domNode.style.display == "block"){
                this._underlay.layout();
                //this._position();
            }
        },
        _downloadExternalContent: function(){
            this._onUnloadHandler();
    
            // display loading message
            // TODO: maybe we should just set a css class with a loading image as background?
            
            this._setContent(
                this.onDownloadStart.call(this)
            );
            
            var self = this;
            var getArgs = {
                preventCache: (this.preventCache || this.refreshOnShow),
                url: this.href,
                handleAs: "text",
                content: this.params,
                headers: { X_REQUESTED_WITH: "XMLHttpRequest" }
            };
            if(dojo.isObject(this.ioArgs)){
                dojo.mixin(getArgs, this.ioArgs);
            }
    
            var hand = this._xhrDfd = (this.ioMethod || dojo.xhrPost)(getArgs);
    
            hand.addCallback(function(html){
                self.clearParams();
                self.clearReloadIds();
                try{
                    self.onDownloadEnd.call(self);
                    self._isDownloaded = true;
                    self.setContent.call(self, html); // onload event is called from here
                }catch(err){
                    self._onError.call(self, 'Content', err); // onContentError
                }
                delete self._xhrDfd;
                return html;
            });
    
            hand.addErrback(function(err){
                if(!hand.cancelled){
                    // show error message in the pane
                    self._onError.call(self, 'Download', err); // onDownloadError
                }
                delete self._xhrDfd;
                return err;
            });
        }    
    }
);

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dialog.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dropdown.js
 */
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

dojo.provide("aimluck.widget.Dropdown");

dojo.require("dijit.form.Button");

dojo.declare("aimluck.widget.Dropdown", [dijit.form.DropDownButton], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    selectId: "",
    iconURL: "",
    iconAlt: "",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",

    _openDropDown: function(){
        this.inherited(arguments);
        //For google chrome and Firefox/3.6
        var userAgent = window.navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") > -1 || userAgent.indexOf("firefox/3.6") > -1) {
            var pNode = this.dropDown.domNode.parentNode;
            var top = pNode.style.top.replace("px","");
            top_new = parseInt(top) + window.scrollY;
            pNode.style.top = top_new + "px";
        }
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Dropdown.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Menu.js
 */
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

dojo.provide("aimluck.widget.Menu");
dojo.provide("aimluck.widget.Menuitem");
dojo.provide("aimluck.widget.Menuseparator");
dojo.provide("aimluck.widget.Menubar");
dojo.provide("aimluck.widget.DropDownButton");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.Menu");
dojo.require("dijit.Toolbar");
dojo.require("dijit.form.Button");

dojo.declare("aimluck.widget.Menuitem", [dijit.MenuItem], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
        templateString:
         '<tr class="dijitReset dijitMenuItem"'
        +'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
        +'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass} menuItemIcon" dojoAttachPoint="iconNode" ></div></td>'
        +'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem" nowrap="nowrap"></td>'
        +'<td class="dijitReset" dojoAttachPoint="arrowCell">'
            +'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
            +'<span class="dijitInline moz-inline-box dijitArrowNode dijitMenuExpandInner">+</span>'
            +'</div>'
        +'</td>'
        +'</tr>',
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.MenuButton", [dijit.form.Button], {
    label: "",
    iconSrc: "",
    iconClass: "",
    url: "",
    itemClass:"",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box dijitButton\"\n\tdojoAttachEvent=\"onclick:_onButtonClick,onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\"><div class='dijitRight'><button class=\"dijitStretch dijitButtonNode dijitButtonContents  ${itemClass}\" dojoAttachPoint=\"focusNode,titleNode\"\n\t\t\ttype=\"${type}\" waiRole=\"button\" waiState=\"labelledby-${id}_label\"><div class=\"dijitInline ${iconClass} menuItemIcon \" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span></button></div></div>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menu", [dijit.Menu], {
//    submenuOverlap: 2,
//    submenuDelay: 0,
    templateString:
            '<table class="popupMenu dijitMenuTable" waiRole="menu" dojoAttachEvent="onkeypress:_onKeyPress">' +
                '<tbody class="dijitReset" dojoAttachPoint="containerNode"></tbody>'+
            '</table>'
});


dojo.declare("aimluck.widget.Menuseparator", [dijit.MenuSeparator], {
    templateString: "<tr class=\"menuSeparator\"><td colspan=4>" + "<div class=\"menuSeparatorTop\"></div>" + "<div class=\"menuSeparatorBottom\"></div>" + "</td></tr>"
});

dojo.declare(
    "aimluck.widget.ToolbarSeparator",
    [ dijit.ToolbarSeparator ],
{
    // summary
    //  A line between two menu items
    templateString: '<div class="dijitInline moz-inline-box">&nbsp;｜&nbsp;</div>',
    postCreate: function(){ dojo.setSelectable(this.domNode, false); },
    isFocusable: function(){ return false; }
});

dojo.declare("aimluck.widget.DropDownButton", [dijit.form.DropDownButton], {
    label: "",
    iconSrc: "",
    iconClass: "",
    templateString:"<div class=\"dijit dijitLeft dijitInline moz-inline-box\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<button class=\"dijitStretch dijitButtonNode dijitButtonContents\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><div class=\"dijitInline ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div><span class=\"dijitButtonText\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\">${label}</span\n\t\t><span class='dijitA11yDownArrow'>&#9660;</span>\n\t</button>\n</div></div>\n"
});

dojo.declare("aimluck.widget.ComboButton", [dijit.form.ComboButton], {
    // summary
    //      left side is normal button, right side displays menu
    url: "",
    itemClass:"",
    templateString:"<table class='dijit dijitReset dijitInline dijitLeft moz-inline-box ${itemClass} '\n\tcellspacing='0' cellpadding='0'\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\">\n\t<tr>\n\t\t<td\tclass=\"dijitStretch dijitButtonContents dijitButtonNode\"\n\t\t\ttabIndex=\"${tabIndex}\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onButtonClick\"  dojoAttachPoint=\"titleNode\"\n\t\t\twaiRole=\"button\" waiState=\"labelledby-${id}_label\">\n\t\t\t<div class=\"dijitMenuItemIcon ${iconClass} menuItemIcon\" dojoAttachPoint=\"iconNode\"></div>\n\t\t\t<span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span>\n\t\t</td>\n\t\t<td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton'\n\t\t\tdojoAttachPoint=\"popupStateNode,focusNode\"\n\t\t\tdojoAttachEvent=\"ondijitclick:_onArrowClick, onkeypress:_onKey\"\n\t\t\tstateModifier=\"DownArrow\"\n\t\t\ttitle=\"${optionsTitle}\" name=\"${name}\"\n\t\t\twaiRole=\"button\" waiState=\"haspopup-true\"\n\t\t><div waiRole=\"presentation\">&#9660;</div>\n\t</td></tr>\n</table>\n",
    onClick: function() {
        location.href = this.url;
    }
});

dojo.declare("aimluck.widget.Menubar", [dijit.Toolbar], {
    selectedIndex: -1,
    templateString:
        '<div class="tundra"><div class="dijit dijitToolbar" waiRole="toolbar" tabIndex="${tabIndex}" dojoAttachPoint="containerNode">' +
        '</div></div>',
    postCreate:function () {
        dijit.Toolbar.superclass.postCreate.apply(this, arguments);
        this.makeMenu(this.items);
        this.isShowingNow = true;
    },
    makeMenu:function(items) {
        var _this = this;
        var _count = 0;
        dojo.forEach(items, function(itemJson){
                if(itemJson.submenu){ 
                    var menu = new aimluck.widget.Menu({id: itemJson.caption, style: "display: none;" });
                    dojo.forEach(itemJson.submenu, function(itemJson2){
                        if(itemJson2 != null){
                            if (itemJson2.caption) {
                                menu.addChild( new aimluck.widget.Menuitem({label: itemJson2.caption, url: itemJson2.url, iconClass: itemJson2.iconClass}) );
                            } else {
                                menu.addChild( new aimluck.widget.Menuseparator() );
                            }
                        }
                    });
                    var _itemClass = "";
                    if(_this.selectedIndex == parseInt(_count) ){
                        _itemClass += "menuBarItemSelected";
                    }  
                    var ddb = new aimluck.widget.ComboButton({ label: itemJson.caption, iconClass: itemJson.iconClass, dropDown: menu, url: itemJson.url, itemClass:_itemClass});
                    ddb.addChild(menu);
                    _this.addChild(ddb);
                } else if(itemJson.url) {
                    var _itemClass = "";
                    if(_this.selectedIndex == _count){
                        _itemClass += "menuBarItemSelected";
                    }
                    var ddb = new aimluck.widget.MenuButton({id: itemJson.caption + "_Button" + _count, label: itemJson.caption , url: itemJson.url, iconClass: itemJson.iconClass,  itemClass:_itemClass });
                    _this.addChild(ddb);
                } else {
                    _this.addChild(new aimluck.widget.ToolbarSeparator());
                }
                _count++;
               
        }); 

    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/widget/Menu.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aimluck/aimluck-all.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo-all.js
 */
/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */
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

window.aipo = window.aipo || {};

aipo.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = aipo;


    for (var i=(levels[0] == "aipo") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

djConfig = { isDebug: false };

var ptConfig = [];

aipo.onReceiveMessage = function(msg, group){
    if(!msg) {
        var arrDialog = dijit.byId("modalDialog");
        arrDialog.hide();
        aipo.portletReload(group);
    }
    if (dojo.byId('messageDiv')) {
        dojo.byId('messageDiv').innerHTML = msg;
    }
}

aipo.portletReload = function(group, portletId) {
    for(var index in ptConfig) {
        if (index != portletId) {
            if(ptConfig[index].group == group) {
                ptConfig[index].reloadFunction.call(ptConfig[index].reloadFunction, index);
            }
        }
    }
};

aipo.reloadPage = function(portletId) {
    if( typeof ptConfig[portletId].reloadUrl == "undefined") {
      aipo.viewPage(ptConfig[portletId].initUrl, portletId);
    } else {
      aipo.viewPage(ptConfig[portletId].reloadUrl, portletId);
    }
};

aipo.viewPage = function(url, portletId, params) {
     var portlet = dijit.byId('portlet_' + portletId);
     if(! portlet){
       portlet = new aimluck.widget.Contentpane({},'portlet_' + portletId);
     }
     
     if(portlet){
       ptConfig[portletId].reloadUrl= url;
       
       if(params){
       	 for(i = 0 ; i < params.length; i++ ) {
       		portlet.setParam(params[i][0], params[i][1]);
       	 }
       }
       
       portlet.viewPage(url);
     }
};

aipo.errorTreatment = function(jsondata, url) {
    if (jsondata["error"]) {
        if(jsondata["error"]== 1) {
           window.location.href = url;
        } else {
            return true;
        }
        return false;
    } else {
        return true;
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */
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

dojo.provide("aipo.common");

aipo.common.showDialog = function(url, portlet_id, callback) {
    var arrDialog = dijit.byId("modalDialog");
    
    if(! arrDialog){
       arrDialog = new aimluck.widget.Dialog({widgetId:'modalDialog', _portlet_id: portlet_id, _callback:callback}, "modalDialog");
    }else{
       arrDialog.setCallback(portlet_id, callback);
    }
     
    if(arrDialog){
      arrDialog.setHref(url);
      arrDialog.show();
    }
};

aipo.common.hideDialog = function() {
    var arrDialog = dijit.byId("modalDialog");
    if(arrDialog){
      arrDialog.hide();
    }
};

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/common/dialog.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */
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

dojo.provide("aipo.io");

aipo.io.loadHtml = function(url, params, portletId){
    dojo.xhrGet({
        url: url,
        transport: "ScriptSrcTransport",
        jsonParamName: "callback",
        content: params,
        method: "get", 
        mimetype: "application/json",
        encoding: "utf-8",
        load: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = data.body;
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        error: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u8aad\u307f\u8fbc\u307f\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeout: function(type, data, event, args) {
            dojo.byId('content-'+portletId).innerHTML = "\u005b\u30a8\u30e9\u30fc\u005d\u0020\u30bf\u30a4\u30e0\u30a2\u30a6\u30c8\u3057\u307e\u3057\u305f\u3002";
            dojo.html.setVisibility(dojo.byId('content-'+portletId), true);
            dojo.html.setDisplay(dojo.byId('indicator-'+portletId), false);
        },
        timeoutSeconds: 10
    });
}

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/io/jsonp.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DateCalendar.js
 */
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

dojo.provide("aipo.widget.DateCalendar");

dojo.require("dijit._Calendar");

dojo.declare("aipo.widget.DateCalendar", [dijit._Calendar], {
    dateId: "",
    templateString:"<table cellspacing=\"0\" cellpadding=\"0\" class=\"dijitCalendarContainer\">\n\t<thead>\n\t\t<tr class=\"dijitReset dijitCalendarMonthContainer\" valign=\"top\">\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"decrementMonth\">\n\t\t\t\t<span class=\"dijitInline dijitCalendarIncrementControl dijitCalendarDecrease\"><span dojoAttachPoint=\"decreaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarDecreaseInner\">-</span></span>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' colspan=\"5\">\n\t\t\t\t<div dojoAttachPoint=\"monthLabelSpacer\" class=\"dijitCalendarMonthLabelSpacer\"></div>\n\t\t\t\t<div dojoAttachPoint=\"monthLabelNode\" class=\"dijitCalendarMonth\"></div>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"incrementMonth\">\n\t\t\t\t<div class=\"dijitInline dijitCalendarIncrementControl dijitCalendarIncrease\"><span dojoAttachPoint=\"increaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarIncreaseInner\">+</span></div>\n\t\t\t</th>\n\t\t</tr>\n\t\t<tr>\n\t\t\t<th class=\"dijitReset dijitCalendarDayLabelTemplate\"><span class=\"dijitCalendarDayLabel\"></span></th>\n\t\t</tr>\n\t</thead>\n\t<tbody dojoAttachEvent=\"onclick: _onDayClick\" class=\"dijitReset dijitCalendarBodyContainer\">\n\t\t<tr class=\"dijitReset dijitCalendarWeekTemplate\">\n\t\t\t<td class=\"dijitReset dijitCalendarDateTemplate\"><span class=\"dijitCalendarDateLabel\"></span></td>\n\t\t</tr>\n\t</tbody>\n\t<tfoot class=\"dijitReset dijitCalendarYearContainer\">\n\t\t<tr>\n\t\t\t<td class='dijitReset' valign=\"top\" colspan=\"7\">\n\t\t\t\t<h3 class=\"dijitCalendarYearLabel\">\n\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\" class=\"dijitInline dijitCalendarPreviousYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"currentYearLabelNode\" class=\"dijitInline dijitCalendarSelectedYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" class=\"dijitInline dijitCalendarNextYear\"></span>\n\t\t\t\t</h3>\n\t\t\t</td>\n\t\t</tr>\n\t</tfoot>\n</table>\t\n",
    //templateString:"<table cellspacing=\"0\" cellpadding=\"0\" class=\"calendarBodyContainer\">\n\t<thead>\n\t\t<tr class=\"dijitReset monthLabelContainer\" valign=\"top\">\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"decrementMonth\">\n\t\t\t\t<span class=\"dijitInline dijitCalendarIncrementControl dijitCalendarDecrease\"><span dojoAttachPoint=\"decreaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarDecreaseInner\">-</span></span>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' colspan=\"5\">\n\t\t\t\t<div dojoAttachPoint=\"monthLabelSpacer\" class=\"dijitCalendarMonthLabelSpacer\"></div>\n\t\t\t\t<div dojoAttachPoint=\"monthLabelNode\" class=\"dijitCalendarMonth\"></div>\n\t\t\t</th>\n\t\t\t<th class='dijitReset' dojoAttachPoint=\"incrementMonth\">\n\t\t\t\t<div class=\"dijitInline dijitCalendarIncrementControl dijitCalendarIncrease\"><span dojoAttachPoint=\"increaseArrowNode\" class=\"dijitA11ySideArrow dijitCalendarIncrementControl dijitCalendarIncreaseInner\">+</span></div>\n\t\t\t</th>\n\t\t</tr>\n\t\t<tr>\n\t\t\t<th class=\"dijitReset dijitCalendarDayLabelTemplate\"><span class=\"dijitCalendarDayLabel\"></span></th>\n\t\t</tr>\n\t</thead>\n\t<tbody dojoAttachEvent=\"onclick: _onDayClick\" class=\"dijitReset dijitCalendarBodyContainer\">\n\t\t<tr class=\"dijitReset dijitCalendarWeekTemplate\">\n\t\t\t<td class=\"dijitReset dijitCalendarDateTemplate\"><span class=\"dijitCalendarDateLabel\"></span></td>\n\t\t</tr>\n\t</tbody>\n\t<tfoot class=\"dijitReset dijitCalendarYearContainer\">\n\t\t<tr>\n\t\t\t<td class='dijitReset' valign=\"top\" colspan=\"7\">\n\t\t\t\t<h3 class=\"dijitCalendarYearLabel\">\n\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\" class=\"dijitInline dijitCalendarPreviousYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"currentYearLabelNode\" class=\"dijitInline dijitCalendarSelectedYear\"></span>\n\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" class=\"dijitInline dijitCalendarNextYear\"></span>\n\t\t\t\t</h3>\n\t\t\t</td>\n\t\t</tr>\n\t</tfoot>\n</table>\t\n",
    postCreate: function(){
      this.inherited(arguments);

    },
    onChange: function(/*date*/date){
       var tyear = date.getFullYear();
       var tmonth = 1+date.getMonth();
       var tdate = date.getDate();
       var dayNames = dojo.date.locale.getNames('days', this.dayWidth, 'standAlone', this.lang);
       var tday = dayNames[date.getDay()];
    
       var viewvalue = dojo.byId(this.dateId+'_view');
       viewvalue.innerHTML = tyear+"\u5e74"+tmonth+"\u6708"+tdate+"\u65e5\uff08"+tday+"\uff09&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";  
       var hiddendate = dojo.byId(this.dateId);
       hiddendate.value = tyear+"/"+tmonth+"/"+tdate;
       var hiddendate_year = dojo.byId(this.dateId+'_year');
       hiddendate_year.value = tyear;
       var hiddendate_month = dojo.byId(this.dateId+'_month');
       hiddendate_month.value = tmonth;
       var hiddendate_day = dojo.byId(this.dateId+'_day');
       hiddendate_day.value = tdate;
       
       dojo.byId(this.dateId+'_flag').checked = false;
    },
    disabledCalendar: function(/*boolean*/bool) {
        if(bool){
           var viewvalue = dojo.byId(this.dateId+'_view');
           viewvalue.innerHTML = "---- \u5e74 -- \u6708 -- \u65e5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

           var hiddendate_year = dojo.byId(this.dateId+'_year');
           hiddendate_year.value = "";
           var hiddendate_month = dojo.byId(this.dateId+'_month');
           hiddendate_month.value = "";
           var hiddendate_day = dojo.byId(this.dateId+'_day');
           hiddendate_day.value = "";
           
           this.value = "";
           if(! dojo.byId(this.dateId+'_flag').checked) {
        	   dojo.byId(this.dateId+'_flag').checked = true;
           }
        }else{
           var hiddendate = dojo.byId(this.dateId);
           if( (!hiddendate.value) || (hiddendate.value=="") ) {
        	   this.setValue(new Date());
           }else{
	           var tmpdate = hiddendate.value.split("/");
	           if(tmpdate.length == 3){
	               var tyear = tmpdate[0];
	               var tmonth = tmpdate[1]-1;
	               var tday = tmpdate[2];
	   
	               var tdate = new Date(tyear, tmonth, tday);
	               this.setValue(tdate);
	           }
           }
       }
    },
    clearDate: function(){
       this.value = null;
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DateCalendar.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupSelectList.js
 */
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

dojo.provide("aipo.widget.GroupSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.GroupSelectList", [dijit._Widget, dijit._Templated], {
    inputWidth: "95%",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    widgetId:"",
    selectId:"",
    inputId:"",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"groupPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">${memberToTitle}</div><div class=\"memberlistFromTop\">${memberFromTitle}</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",  
//    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"facilityPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">所属施設一覧</div><div class=\"memberlistFromTop\">施設一覧</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"tmp_facility_to\" id=\"tmp_facility_to\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"tmp_facility_from\" id=\"tmp_facility_from\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"button_facility_remove\" name=\"button_facility_remove\" type=\"button\" class=\"button\" value=\"　削除　\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"button_facility_add\" name=\"button_facility_add\" type=\"button\" class=\"button\" value=\"　＜ 追加　\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",    
    postCreate: function(){
        this.id = this.widgetId;
        
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (document.all) {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
              f_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                    selectsub.removeChild(f_o[i]);
                i -= 1;
                }
            }
      }
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.selectId));
      this.inputMemberSync();
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
       this.inputMemberSync();
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberSelectList.js
 */
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

dojo.provide("aipo.widget.MemberSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    inputWidth: "95%",
    hiddenId: "",
    hiddenValue: "",
    inputId: "",
    inputValue: "",
    selectId: "",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberToTitle: "",
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >${memberToTitle}</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",    
    //templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:300px\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >参加メンバー一覧</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"10\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"　削除　\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"　＜ 追加　\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",    
    postCreate: function(){
        this.id = this.widgetId;
    
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
        
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue }
        };
        aimluck.io.createOptions(this.groupSelectId, params); 
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (document.all) {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
              f_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
        var f_o = selectsub.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                    selectsub.removeChild(f_o[i]);
                i -= 1;
                }
            }
      }
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.selectId));
      this.inputMemberSync();
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
       this.inputMemberSync();
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownDatepicker.js
 */
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

dojo.provide("aipo.widget.DropdownDatepicker");

dojo.require("aimluck.widget.Dropdown");

dojo.require("aipo.widget.DateCalendar");
dojo.require("dojo.date.locale");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 */
dojo.declare("aipo.widget.DropdownDatepicker", [aimluck.widget.Dropdown], {
    dateId: "",
    dateValue: "",
    initValue: "",
    displayCheck: "",
    iconURL: "",
    iconAlt: "",
    listWidgetId:"datewidget",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"><div dojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t style=\"float:left;\"><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span></div></div><div class=\"alignleft\"><span name=\"${dateId}_view\" id=\"${dateId}_view\" dojoAttachPoint=\"inputNode\" style=\"vertical-align:middle;background:#ffffff ;border:0px;\" autocomplete=\"off\" readonly=\"readonly\"></span><span style=\"display:${displayCheck}\"><input name=\"${dateId}_check\" type=\"checkbox\" value=\"TRUE\" id=\"${dateId}_flag\" dojoAttachEvent=\"onclick:onCheckBlank\" /><label for=\"${dateId}_flag\"> 指定しない</label></span><input type=\"hidden\" id=\"${dateId}\" name=\"${dateId}\" value=\"${dateValue}\" dojoAttachPoint=\"valueNode\" /><input type=\"hidden\" id=\"${dateId}_year\" name=\"${dateId}_year\" value=\"\" dojoAttachPoint=\"valueYearNode\" /><input type=\"hidden\" id=\"${dateId}_month\" name=\"${dateId}_month\" value=\"\" dojoAttachPoint=\"valueMonthNode\" /><input type=\"hidden\" id=\"${dateId}_day\" name=\"${dateId}_day\" value=\"\" dojoAttachPoint=\"valueDayNode\" /></div></div>\n",
    postCreate: function(){
        this.inherited(arguments);

        var params = {
          widgetId:this.listWidgetId,
          dateId:this.dateId
        };
        this.dropDown = new aipo.widget.DateCalendar(params, this.listWidgetId);

        if(this.initValue != ""){
            var tmpdate = this.initValue.split("/");
            if(tmpdate.length == 3){
                var tyear = tmpdate[0];
                var tmonth = tmpdate[1]-1;
                var tday = tmpdate[2];

	            var datevalue = dojo.byId(this.dateId);
	            datevalue.value = this.initValue;

                this.dropDown.clearDate();
                this.dropDown.setValue(new Date(tyear, tmonth, tday));
	        }
        }else{
          this.dropDown.disabledCalendar(true);
        }
    },
    onCheckBlank: function(/*evt*/ e){
        this.dropDown.disabledCalendar(dojo.byId(this.dateId+'_flag').checked);
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownDatepicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownGrouppicker.js
 */
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

dojo.provide("aipo.widget.DropdownGrouppicker");

dojo.require("aimluck.widget.Dropdown");
dojo.require("aipo.widget.GroupSelectList");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 * buttonAddId:"button_member_add",
 * buttonRemoveId:"button_member_remove",
 * memberFromId:"tmp_member_from",
 * memberToId:"tmp_member_to",
 * memberFromUrl:this.memberFromUrl,
 * memberFromOptionKey:"name",
 * memberFromOptionValue:"aliasName",
 * groupSelectId:"tmp_group",
 * groupSelectOptionKey:"groupId",
 * groupSelectOptionValue:"name",
 * memberGroupUrl:this.memberGroupUrl,
 * changeGroupUrl:this.changeGroupUrl
 */
dojo.declare("aipo.widget.DropdownGrouppicker", [aimluck.widget.Dropdown], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    iconURL: "",
    iconAlt: "",
    selectId:"",
    inputId:"",
    inputValue: "",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    groupSelectId:"",
    groupSelectOptionKey:"",
    groupSelectOptionValue:"",
    memberGroupUrl:"",
    changeGroupUrl:"",
    listWidgetId:"",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",
    postCreate: function(){      

      var fparams = {
          widgetId:this.listWidgetId,
          selectId:this.selectId,
          inputId:this.inputId,
          buttonAddId:this.buttonAddId,
          buttonRemoveId:this.buttonRemoveId,
          memberFromTitle:this.memberFromTitle,
          memberFromId:this.memberFromId,
          memberToTitle:this.memberToTitle,
          memberToId:this.memberToId,
          memberFromUrl:this.memberFromUrl,
          memberFromOptionKey:this.memberFromOptionKey,
          memberFromOptionValue:this.memberFromOptionValue
      };

      var listWidget = dijit.byId(this.listWidgetId);
      if(listWidget){
        this.dropDown = listWidget;
        var select = dojo.byId(listWidget.selectId);
        this.removeAllOptions(select);
        select = dojo.byId(listWidget.memberToId);
        this.removeAllOptions(select);
      }else{
        this.dropDown = new aipo.widget.GroupSelectList(fparams, this.listWidgetId);
      }
      
      this.inherited(arguments);
    },
	removeAllOptions:function(select){
	  var i;
	  if (document.all) {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      t_o.remove(i);
	      i -= 1;
	    }
	  } else {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      select.removeChild(t_o[i]);
	      i -= 1; 
	    }
	  }
	},
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        var i = 0;
        var len = t_o.length;
        if(len <= 0) return;
        for(i = 0 ;i < len; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownGrouppicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownMemberpicker.js
 */
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

dojo.provide("aipo.widget.DropdownMemberpicker");

dojo.require("aimluck.widget.Dropdown");
dojo.require("aipo.widget.MemberSelectList");

/**
 * ex)
 * selectId:"member_to",
 * inputId:"member_to_input",
 * buttonAddId:"button_member_add",
 * buttonRemoveId:"button_member_remove",
 * memberFromId:"tmp_member_from",
 * memberToId:"tmp_member_to",
 * memberFromUrl:this.memberFromUrl,
 * memberFromOptionKey:"name",
 * memberFromOptionValue:"aliasName",
 * groupSelectId:"tmp_group",
 * groupSelectOptionKey:"groupId",
 * groupSelectOptionValue:"name",
 * memberGroupUrl:this.memberGroupUrl,
 * changeGroupUrl:this.changeGroupUrl
 */
dojo.declare("aipo.widget.DropdownMemberpicker", [aimluck.widget.Dropdown], {
    inputWidth: "250px",
    hiddenId: "",
    hiddenValue: "",
    iconURL: "",
    iconAlt: "",
    selectId:"",
    inputId:"",
    inputValue: "",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromId:"",
    memberToTitle: "",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    groupSelectId:"",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey:"",
    groupSelectOptionValue:"",
    memberGroupUrl:"",
    changeGroupUrl:"",
    listWidgetId:"memberlistwidget",
    templateString:"<div class=\"dijit dijitLeft dijitInline\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\n\t><div class='dijitRight'>\n\t<span class=\"\" type=\"${type}\"\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\n\t\t><span class=\"\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\n\t\tid=\"${id}_label\"><img src=\"${iconURL}\" alt=\"${iconAlt}\" style=\"cursor:pointer;cursor:hand;padding-right:2px\" align=\"top\" />\n\t</span><select name=\"${selectId}\" id=\"${selectId}\" size=\"10\" multiple=\"multiple\" style=\"display:none\" dojoAttachPoint=\"selectNode\"></select><input type=\"hidden\" id=\"${hiddenId}\" name=\"${hiddenId}\" value=\"${hiddenValue}\" dojoAttachPoint=\"valueNode\" /><span name=\"${inputId}\" id=\"${inputId}\" dojoAttachPoint=\"inputNode\">${inputValue}</span>\n</div></div>\n",
    postCreate: function(){      
      var userparams = {
          widgetId:this.listWidgetId,
          selectId:this.selectId,
          inputId:this.inputId,
          buttonAddId:this.buttonAddId,
          buttonRemoveId:this.buttonRemoveId,
          memberFromId:this.memberFromId,
          memberToTitle:this.memberToTitle,
          memberToId:this.memberToId,
          memberFromUrl:this.memberFromUrl,
          memberFromOptionKey:this.memberFromOptionKey,
          memberFromOptionValue:this.memberFromOptionValue,
          groupSelectId:this.groupSelectId,
          groupSelectPreOptionKey:this.groupSelectPreOptionKey,
          groupSelectPreOptionValue:this.groupSelectPreOptionValue,
          groupSelectOptionKey:this.groupSelectOptionKey,
          groupSelectOptionValue:this.groupSelectOptionValue,
          memberGroupUrl:this.memberGroupUrl,
          changeGroupUrl:this.changeGroupUrl
      };
     
      var listWidget = dijit.byId(this.listWidgetId);
      if(listWidget){
        this.dropDown = listWidget;
        var select = dojo.byId(listWidget.selectId);
        this.removeAllOptions(select);
        select = dojo.byId(listWidget.memberToId);
        this.removeAllOptions(select);
      }else{
        this.dropDown = new aipo.widget.MemberSelectList(userparams, this.listWidgetId);
      }

      this.inherited(arguments);
    },
	removeAllOptions:function(select){
	  var i;
	  if (document.all) {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      t_o.remove(i);
	      i -= 1;
	    }
	  } else {
	    var t_o = select.options;
	    for(i = 0 ;i < t_o.length; i ++ ) {
	      select.removeChild(t_o[i]);
	      i -= 1; 
	    }
	  }
	},
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      var selectsub = dojo.byId(this.selectId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
                selectsub.options.remove(0);
          }
          select.add(option, select.options.length);
          selectsub.add(option2, selectsub.options.length);
        //select.options[length].selected = is_selected;
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        var option2 = document.createElement("OPTION");
        option2.value = value;
        option2.text = text;
        option2.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
            selectsub.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
        selectsub.insertBefore(option2, selectsub.options[selectsub.options.length]);
        //select.options[length].selected = is_selected;
      }
      this.inputMemberSync();
    },
    inputMemberSync:function() {
        var select = dojo.byId(this.selectId);
        var input = dojo.byId(this.inputId);
        var html = "";
        var t_o = select.options;
        var i = 0;
        var len = t_o.length;
        if(len <= 0) return;
        for(i = 0 ;i < len; i ++ ) {
            if (i != 0) {
                html += ', ';
            }
            html += t_o[i].text;
        }
        input.innerHTML = html;
    }

});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/DropdownMemberpicker.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupNormalSelectList.js
 */
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

dojo.provide("aipo.widget.GroupNormalSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.GroupNormalSelectList", [dijit._Widget, dijit._Templated], {
    inputWidth: "95%",
    memberLimit: 0,
    changeGroupUrl: "",
    widgetId:"",
    buttonAddId:"",
    buttonRemoveId:"",
    memberFromTitle:"",
    memberFromId:"",
    memberToTitle:"",
    memberToId:"",
    memberFromUrl:"",
    memberFromOptionKey:"",
    memberFromOptionValue:"",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"groupPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:335px\"><div class=\"clearfix\"><div class=\"memberlistToTop\">${memberToTitle}</div><div class=\"memberlistFromTop\">${memberFromTitle}</div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u005cu524a\u005cu9664\u3000\" dojoAttachEvent=\"onclick:onMemberRemoveClick\"/></div></div><div class=\"memberlistFromBottom\"><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c \u8ffd\u52a0\u3000\" dojoAttachEvent=\"onclick:onMemberAddClick\"/></div></div></div></div></div></div></td></tr></table></div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue
        };
        aimluck.io.createOptions(this.memberFromId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
          select.options.remove(0);
        }
        select.add(option, select.options.length);  
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
      }
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
	                if( t_o[j].value == f_o[i].value ) {
	                    iseq = true;
	                    break;
	                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
	            if (t_o.length == 1 && t_o[0].value == ""){
	                    t_o.remove(0);
	            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
	                if( t_o[j].value == f_o[i].value ) {
	                    iseq = true;
	                    break;
	                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
	            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
	                select_member_to.removeChild(select_member_to.options[0]);
	            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              t_o.remove(i);
              i -= 1;
            }
          }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              select.removeChild(t_o[i]);
              i -= 1;
            }
          }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              t_o.remove(i);
              i -= 1;
            }
          }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
            if( t_o[i].selected ) {
              select.removeChild(t_o[i]);
              i -= 1;
            }
          }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      if (document.all) {
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
            t_o.remove(i);
            i -= 1;
          }
        }
      } else {
        var t_o = select.options;
        for(i = 0 ;i < t_o.length; i ++ ) {
          if( t_o[i].selected ) {
            select.removeChild(t_o[i]);
            i -= 1;
          }
        }
      }
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
    }
  
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/GroupNormalSelectList.js
 */

/*
 * START OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberNormalSelectList.js
 */
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

dojo.provide("aipo.widget.MemberNormalSelectList");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("aipo.widget.MemberNormalSelectList", [dijit._Widget, dijit._Templated], {
    widgetId:"",
    memberFromId: "",
    memberFromUrl: "",
    memberFromOptionKey: "",
    memberFromOptionValue: "",
    memberToTitle: "",
    memberToId: "",
    buttonAddId: "",
    buttonRemoveId: "",
    memberLimit: 0,
    groupSelectId: "",
    groupSelectPreOptionKey: "",
    groupSelectPreOptionValue: "",
    groupSelectOptionKey: "",
    groupSelectOptionValue: "",
    memberGroupUrl: "",
    changeGroupUrl: "",
    templateString:"<div id=\"${widgetId}\" widgetId=\"${widgetId}\"><table class=\"none\"><tr><td><div id=\"memberPopupDiv\"><div class=\"outer\"><div class=\"popup\" style=\"width:335px;\"><div class=\"clearfix\"><div class=\"memberlistToTop\" >${memberToTitle}</div><div class=\"memberlistFromTop\"><select size=\"1\" style=\"width:100%\" name=\"${groupSelectId}\" id=\"${groupSelectId}\" dojoAttachEvent=\"onchange:changeGroup\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberToId}\" id=\"${memberToId}\"></select></div><div class=\"memberlistFromBody\"><select size=\"5\" multiple=\"multiple\" style=\"width:100%\" name=\"${memberFromId}\" id=\"${memberFromId}\"></select></div></div><div class=\"clearfix\"><div class=\"memberlistToBottom\"><div class=\"alignright\"><input id=\"${buttonRemoveId}\" name=\"${buttonRemoveId}\" type=\"button\" class=\"button\" value=\"\u3000\u524a\u9664\u3000\"/ dojoAttachEvent=\"onclick:onMemberRemoveClick\"></div></div><div class=\"memberlistFromBottom\"><div style=\"display: none;\" id=\"${widgetId}-memberlist-indicator\" class=\"indicator alignleft\">読み込み中</div><div class=\"alignright\"><input id=\"${buttonAddId}\" name=\"${buttonAddId}\" type=\"button\" class=\"button\" value=\"\u3000\uff1c\u0020\u8ffd\u52a0\u3000\"/ dojoAttachEvent=\"onclick:onMemberAddClick\"></div></div></div></div></div></div></td></tr></table></div>\n",
    postCreate: function(){
        this.id = this.widgetId;
        params = {
          url: this.memberFromUrl,
          key: this.memberFromOptionKey,
          value: this.memberFromOptionValue,
          indicator: this.widgetId + "-memberlist-indicator"
        };
        aimluck.io.createOptions(this.memberFromId, params);
        params = {
          url: this.memberGroupUrl,
          key: this.groupSelectOptionKey,
          value: this.groupSelectOptionValue,
          preOptions: { key:this.groupSelectPreOptionKey, value:this.groupSelectPreOptionValue }
        };
        aimluck.io.createOptions(this.groupSelectId, params);
    },
    addOption:function(select, value, text, is_selected) {
      aimluck.io.addOption(select, value, text, is_selected);
    },
    addOptionSync:function(value, text, is_selected) {
      var select = dojo.byId(this.memberToId);
      if (this.memberLimit != 0 && select.options.length >= this.memberLimit) return;
      if (document.all) {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
                select.options.remove(0);
          }
          select.add(option, select.options.length);
      } else {
        var option = document.createElement("OPTION");
        option.value = value;
        option.text = text;
        option.selected = is_selected;
        if (select.options.length == 1 && select.options[0].value == ""){
            select.removeChild(select.options[0]);
        }
        select.insertBefore(option, select.options[select.options.length]);
      }
    },
    addMember:function(select_member_from, select_member_to) {
      if (document.all) {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (t_o.length == 1 && t_o[0].value == ""){
                    t_o.remove(0);
            }
               if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                t_o.add(option, t_o.length);
            }
      } else {
            var f_o = select_member_from.options;
            var t_o = select_member_to.options;
            if (f_o.length == 1 && f_o[0].value == "") return;
            for(i = 0 ; i < f_o.length; i ++ ) {
                if(!f_o[i].selected) continue;
                var iseq = false;

                for( j = 0 ; j < t_o.length; j ++ ) {
                if( t_o[j].value == f_o[i].value ) {
                    iseq = true;
                    break;
                }
                }

                if(iseq) continue;
                var option = document.createElement("OPTION");
                option.value = f_o[i].value;
                option.text = f_o[i].text;
                option.selected = true;
            if (select_member_to.options.length == 1 && select_member_to.options[0].value == ""){
                select_member_to.removeChild(select_member_to.options[0]);
            }
                if (this.memberLimit != 0 && select_member_to.options.length >= this.memberLimit) return;
                select_member_to.insertBefore(option, t_o[t_o.length]);
            }
      }
    },
    removeAllMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMember:function(select) {
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
            }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                    select.removeChild(t_o[i]);
                i -= 1;
                }
            }
      }
    },
    removeMemberSync:function() {
      var select = dojo.byId(this.memberToId);
      if (document.all) {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
              t_o.remove(i);
                i -= 1;
            }
        }
      } else {
        var t_o = select.options;
          for(i = 0 ;i < t_o.length; i ++ ) {
              if( t_o[i].selected ) {
                select.removeChild(t_o[i]);
                i -= 1;
              }
          }
      }
    },
    changeGroup: function(select) {
      var group_name = select.target.options[select.target.selectedIndex].value;
      var url = this.changeGroupUrl+"&groupname="+group_name;
      var params = {
        url: url,
        key: this.memberFromOptionKey,
        value: this.memberFromOptionValue,
        indicator: this.widgetId + "-memberlist-indicator"
      };
      aimluck.io.createOptions(this.memberFromId, params);
    },
    onMemberAddClick: function(/*Event*/ evt){
      this.addMember(dojo.byId(this.memberFromId), dojo.byId(this.memberToId));
    },
    onMemberRemoveClick: function(/*Event*/ evt){
       this.removeMemberSync();
    }
});

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/widget/MemberNormalSelectList.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/

/*
 * END OF FILE - /aipo/war/src/main/webapp/javascript/aipo/aipo-all.js
 */

/*
 * JavaScript file created by Rockstarapps Concatenation
*/
