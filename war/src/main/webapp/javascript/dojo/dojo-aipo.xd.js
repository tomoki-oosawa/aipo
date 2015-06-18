dojo._xdResourceLoaded({
depends: [["provide", "dojo.date"],
["provide", "dojo.fx"],
["provide", "dojo.fx.Toggler"],
["provide", "dojo.i18n"],
["provide", "dojo.cldr.supplemental"],
["provide", "dojo.regexp"],
["provide", "dojo.string"],
["provide", "dojo.date.stamp"],
["provide", "dojo.parser"],
["provide", "dojo.date.locale"],
["requireLocalization", "dojo.cldr", "gregorian", null, "ko,zh-cn,zh,ja,en,it-it,en-ca,en-au,it,en-gb,es-es,fr,pt,ROOT,ko-kr,es,de,pt-br", "de,en,en-au,en-ca,en-gb,es,es-es,fr,ROOT,it,it-it,ja,ko,ko-kr,pt,pt-br,zh,zh-cn", "de,en,en-au,en-ca,en-gb,es,es-es,fr,ROOT,it,it-it,ja,ko,ko-kr,pt,pt-br,zh,zh-cn"],
["provide", "dojo.dnd.autoscroll"],
["provide", "dojo.dnd.common"],
["provide", "dojo.dnd.Container"],
["provide", "dojo.dnd.Selector"],
["provide", "dojo.dnd.Avatar"],
["provide", "dojo.dnd.Manager"],
["provide", "dojo.dnd.Source"],
["provide", "dojo.dnd.Mover"],
["provide", "dojo.dnd.Moveable"],
["provide", "dojo.dnd.move"],
["provide", "dojo.io.iframe"],
["provide", "dojo.data.util.filter"],
["provide", "dojo.data.util.sorter"],
["provide", "dojo.data.util.simpleFetch"],
["provide", "dojo.data.ItemFileReadStore"],
["provide", "dojo.data.ItemFileWriteStore"]],
defineResource: function(dojo){
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
	//		 or by a build optimization step.  NOTE:
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
	//		See
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
	;

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
		//		The resources must be loaded by  prior to use

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
				node.style.width=(this.manager.nodes[i].clientWidth || this.manager.nodes[i].offsetWidth)+"px";
				node.style.height="auto";
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
		OFFSET_X: -10,
		OFFSET_Y: 5,
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
			if(dojo.hasClass(node, "dojoDndHandle")){
				//aタグを同時にクリックした場合には、aタグを優先させる
				var event=e;
				if (!event) {event = window.event; }
				var pos={x:event.clientX,y:event.clientY};
				var isCollapsed=false;
				dojo.query("a",node).forEach(function(item){
					if(!isCollapsed){
						var rect=item.getBoundingClientRect();
						isCollapsed=(rect.left<=pos.x && pos.x<=rect.right && rect.top<=pos.y && pos.y<=rect.bottom);
					}
				});
				if(isCollapsed){
					return false;
				}
				//----------------

				return true;
			}
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



}});