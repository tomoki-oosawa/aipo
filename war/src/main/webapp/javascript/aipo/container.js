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

aipo.PortletLayoutManager = function() {
    shindig.LayoutManager.call(this);
};

aipo.PortletLayoutManager.inherits(shindig.LayoutManager);

aipo.PortletLayoutManager.prototype.getGadgetChrome = function(gadget) {
    var chromeId = 'gadget-chrome-' + gadget.portletId;
    return chromeId ? document.getElementById(chromeId) : null;
};

aipo.IfrGadget = {
		getMainContent: function(continuation) {
			var iframeId = this.getIframeId();
			gadgets.rpc.setRelayUrl(iframeId, this.serverBase_ + this.rpcRelay);
			gadgets.rpc.setAuthToken(iframeId, this.rpcToken);
			continuation('<div class="' + this.cssClassGadgetContent + '"><iframe id="' +
					iframeId + '" name="' + iframeId + '" class="' + this.cssClassGadget +
					'" src="about:blank' +
					'" frameborder="no" scrolling="no"' +
					(this.height ? ' height="' + this.height + '"' : '') +
					(this.width ? ' width="' + this.width + '"' : '') +
			'></iframe></div>');
		},

		finishRender: function(chrome) {
			window.frames[this.getIframeId()].location = this.getIframeUrl();
		},

		getIframeUrl: function() {
			return this.serverBase_ + 'ifr?' +
			'container=' + this.CONTAINER +
			'&mid=' +  this.id +
			'&nocache=' + aipo.container.nocache_ +
			'&country=' + aipo.container.country_ +
			'&lang=' + aipo.container.language_ +
			'&view=' + aipo.container.view_ +
			(this.specVersion ? '&v=' + this.specVersion : '') +
			(shindig.container.parentUrl_ ? '&parent=' + encodeURIComponent(shindig.container.parentUrl_) : '') +
			(this.debug ? '&debug=1' : '') +
			this.getAdditionalParams() +
			this.getUserPrefsParams() +
			(this.secureToken ? '&st=' + this.secureToken : '') +
			'&url=' + encodeURIComponent(this.specUrl) +
			'#rpctoken=' + this.rpcToken +
			(this.viewParams ?
					'&view-params=' +  encodeURIComponent(gadgets.json.stringify(this.viewParams)) : '') +
					(this.hashData ? '&' + this.hashData : '');
		}
};

aipo.IfrGadgetService = function() {
	shindig.IfrGadgetService.call(this);
	gadgets.rpc.register('requestNavigateTo', this.requestNavigateTo);
};

aipo.IfrGadgetService.inherits(shindig.IfrGadgetService);

aipo.IfrGadgetService.prototype.requestNavigateTo = function(view, opt_params) {
	var portletId = this.f.replace("remote_iframe_","");
	var url = "?js_peid=" + encodeURIComponent(portletId);
	if(view == "canvas") {
		url += '&action=controls.Maximize';
	} else if (view == "home") {
		url += '&action=controls.Restore';
	}
	if (opt_params) {
		var paramStr = gadgets.json.stringify(opt_params);
		if (paramStr.length > 0) {
			url += '&appParams=' + encodeURIComponent(paramStr);
		}
	}
	document.location.href = url;
};

aipo.IfrContainer = function() {
	shindig.Container.call(this);
	this.context = new Array();
};

aipo.IfrContainer.inherits(shindig.Container);

aipo.IfrContainer.prototype.gadgetClass = shindig.BaseIfrGadget;

aipo.IfrContainer.prototype.gadgetService = new aipo.IfrGadgetService();

aipo.IfrContainer.prototype.setParentUrl = function(url) {
	if (!url.match(/^http[s]?:\/\//)) {
		url = document.location.href.match(/^[^?#]+\//)[0] + url;
	}

	this.parentUrl_ = url;
};

aipo.IfrContainer.prototype.assign = function(context) {
	this.context.push(context);
};

aipo.IfrContainer.prototype.getContext = function() {
	return this.context;
};

aipo.IfrContainer.prototype.renderGadget = function(gadget) {
	var chrome = this.layoutManager.getGadgetChrome(gadget);
	gadget.render(chrome);
};

aipo.IfrContainer.prototype.renderGadgets = function() {
	var context = this.context;
	for (var i = 0; i < context.length; i ++) {
		var c = context[i];
		var gadget = this.createGadget(c);
		gadget.setServerBase(c.serverBase);
		this.addGadget(gadget);
		this.renderGadget(gadget);
	}	
};

shindig.BaseIfrGadget.prototype.getIframeId = function() {
	return this.GADGET_IFRAME_PREFIX_ + this.portletId;
};
	
shindig.BaseIfrGadget.prototype.queryIfrGadgetType_ = function() {
	// Get the gadget metadata and check if the gadget requires the 'pubsub-2'
	// feature.  If so, then we use OpenAjax Hub in order to create and manage
	// the iframe.  Otherwise, we create the iframe ourselves.
	var request = {
			context: {
				country: aipo.container.country_,
				language: aipo.container.language_,
				view: aipo.container.view_,
				container: "default"
			},
			gadgets: [{
				url: this.specUrl,
				moduleId: 1,
				portletId: this.portletId
			}]
	};

	var makeRequestParams = {
			"CONTENT_TYPE" : "JSON",
			"METHOD" : "POST",
			"POST_DATA" : gadgets.json.stringify(request)
	};

	var url = this.serverBase_+"metadata?st=" + this.secureToken;

	gadgets.io.makeNonProxiedRequest(url,
			handleJSONResponse,
			makeRequestParams,
			"application/javascript"
	);

	var gadget = this;
	function handleJSONResponse(obj) {
		var requiresPubSub2 = false;
		var arr = obj.data.gadgets[0].features;
		for(var i = 0; i < arr.length; i++) {
			if (arr[i] === "pubsub-2") {
				requiresPubSub2 = true;
				break;
			}
		}
		var subClass = requiresPubSub2 ? shindig.OAAIfrGadget : aipo.IfrGadget;
		for (var name in subClass) if (subClass.hasOwnProperty(name)) {
			gadget[name] = subClass[name];
		}
	}
};

shindig.Gadget.prototype.getContent = function(continuation) {
    shindig.callAsyncAndJoin(['getMainContent'], function(results) {
        continuation(results.join(''));
    }, this);
};

aipo.container = new aipo.IfrContainer();
aipo.container.layoutManager = new aipo.PortletLayoutManager();