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
var gadgets=gadgets||{};
var shindig=shindig||{};
var osapi=osapi||{};;
gadgets.config=function(){var A={};
var B;
return{register:function(E,D,C){var F=A[E];
if(!F){F=[];
A[E]=F
}F.push({validators:D||{},callback:C})
},get:function(C){if(C){return B[C]||{}
}return B
},init:function(E,L){B=E;
for(var C in A){if(A.hasOwnProperty(C)){var D=A[C],I=E[C];
for(var H=0,G=D.length;
H<G;
++H){var J=D[H];
if(I&&!L){var F=J.validators;
for(var K in F){if(F.hasOwnProperty(K)){if(!F[K](I[K])){throw new Error('Invalid config value "'+I[K]+'" for parameter "'+K+'" in component "'+C+'"')
}}}}if(J.callback){J.callback(E)
}}}}},EnumValidator:function(F){var E=[];
if(arguments.length>1){for(var D=0,C;
(C=arguments[D]);
++D){E.push(C)
}}else{E=F
}return function(H){for(var G=0,I;
(I=E[G]);
++G){if(H===E[G]){return true
}}return false
}
},RegExValidator:function(C){return function(D){return C.test(D)
}
},ExistsValidator:function(C){return typeof C!=="undefined"
},NonEmptyStringValidator:function(C){return typeof C==="string"&&C.length>0
},BooleanValidator:function(C){return typeof C==="boolean"
},LikeValidator:function(C){return function(E){for(var F in C){if(C.hasOwnProperty(F)){var D=C[F];
if(!D(E[F])){return false
}}}return true
}
}}
}();;
gadgets.config.isGadget=false;
gadgets.config.isContainer=true;;
gadgets.util=function(){function G(K){var L;
var I=K.indexOf("?");
var J=K.indexOf("#");
if(J===-1){L=K.substr(I+1)
}else{L=[K.substr(I+1,J-I-1),"&",K.substr(J+1)].join("")
}return L.split("&")
}var E=null;
var D={};
var C={};
var F=[];
var A={0:false,10:true,13:true,34:true,39:true,60:true,62:true,92:true,8232:true,8233:true};
function B(I,J){return String.fromCharCode(J)
}function H(I){D=I["core.util"]||{}
}if(gadgets.config){gadgets.config.register("core.util",null,H)
}return{getUrlParameters:function(R){var J=typeof R==="undefined";
if(E!==null&&J){return E
}var N={};
var K=G(R||document.location.href);
var P=window.decodeURIComponent?decodeURIComponent:unescape;
for(var M=0,L=K.length;
M<L;
++M){var O=K[M].indexOf("=");
if(O===-1){continue
}var I=K[M].substring(0,O);
var Q=K[M].substring(O+1);
Q=Q.replace(/\+/g," ");
N[I]=P(Q)
}if(J){E=N
}return N
},makeClosure:function(L,N,M){var K=[];
for(var J=2,I=arguments.length;
J<I;
++J){K.push(arguments[J])
}return function(){var O=K.slice();
for(var Q=0,P=arguments.length;
Q<P;
++Q){O.push(arguments[Q])
}return N.apply(L,O)
}
},makeEnum:function(J){var K,I,L={};
for(K=0;
(I=J[K]);
++K){L[I]=I
}return L
},getFeatureParameters:function(I){return typeof D[I]==="undefined"?null:D[I]
},hasFeature:function(I){return typeof D[I]!=="undefined"
},getServices:function(){return C
},registerOnLoadHandler:function(I){F.push(I)
},runOnLoadHandlers:function(){for(var J=0,I=F.length;
J<I;
++J){F[J]()
}},escape:function(I,M){if(!I){return I
}else{if(typeof I==="string"){return gadgets.util.escapeString(I)
}else{if(typeof I==="array"){for(var L=0,J=I.length;
L<J;
++L){I[L]=gadgets.util.escape(I[L])
}}else{if(typeof I==="object"&&M){var K={};
for(var N in I){if(I.hasOwnProperty(N)){K[gadgets.util.escapeString(N)]=gadgets.util.escape(I[N],true)
}}return K
}}}}return I
},escapeString:function(M){if(!M){return M
}var J=[],L,N;
for(var K=0,I=M.length;
K<I;
++K){L=M.charCodeAt(K);
N=A[L];
if(N===true){J.push("&#",L,";")
}else{if(N!==false){J.push(M.charAt(K))
}}}return J.join("")
},unescapeString:function(I){if(!I){return I
}return I.replace(/&#([0-9]+);/g,B)
},attachBrowserEvent:function(K,J,L,I){if(typeof K.addEventListener!="undefined"){K.addEventListener(J,L,I)
}else{if(typeof K.attachEvent!="undefined"){K.attachEvent("on"+J,L)
}else{gadgets.warn("cannot attachBrowserEvent: "+J)
}}},removeBrowserEvent:function(K,J,L,I){if(K.removeEventListener){K.removeEventListener(J,L,I)
}else{if(K.detachEvent){K.detachEvent("on"+J,L)
}else{gadgets.warn("cannot removeBrowserEvent: "+J)
}}}}
}();
gadgets.util.getUrlParameters();;
var tamings___=tamings___||[];
tamings___.push(function(A){caja___.whitelistFuncs([[gadgets.util,"escapeString"],[gadgets.util,"getFeatureParameters"],[gadgets.util,"getUrlParameters"],[gadgets.util,"hasFeature"],[gadgets.util,"registerOnLoadHandler"],[gadgets.util,"unescapeString"]])
});;
if(window.JSON&&window.JSON.parse&&window.JSON.stringify){gadgets.json=(function(){var A=/___$/;
return{parse:function(C){try{return window.JSON.parse(C)
}catch(B){return false
}},stringify:function(C){try{return window.JSON.stringify(C,function(E,D){return !A.test(E)?D:null
})
}catch(B){return null
}}}
})()
}else{gadgets.json=function(){function f(n){return n<10?"0"+n:n
}Date.prototype.toJSON=function(){return[this.getUTCFullYear(),"-",f(this.getUTCMonth()+1),"-",f(this.getUTCDate()),"T",f(this.getUTCHours()),":",f(this.getUTCMinutes()),":",f(this.getUTCSeconds()),"Z"].join("")
};
var m={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};
function stringify(value){var a,i,k,l,r=/["\\\x00-\x1f\x7f-\x9f]/g,v;
switch(typeof value){case"string":return r.test(value)?'"'+value.replace(r,function(a){var c=m[a];
if(c){return c
}c=a.charCodeAt();
return"\\u00"+Math.floor(c/16).toString(16)+(c%16).toString(16)
})+'"':'"'+value+'"';
case"number":return isFinite(value)?String(value):"null";
case"boolean":case"null":return String(value);
case"object":if(!value){return"null"
}a=[];
if(typeof value.length==="number"&&!value.propertyIsEnumerable("length")){l=value.length;
for(i=0;
i<l;
i+=1){a.push(stringify(value[i])||"null")
}return"["+a.join(",")+"]"
}for(k in value){if(k.match("___$")){continue
}if(value.hasOwnProperty(k)){if(typeof k==="string"){v=stringify(value[k]);
if(v){a.push(stringify(k)+":"+v)
}}}}return"{"+a.join(",")+"}"
}return""
}return{stringify:stringify,parse:function(text){if(/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/b-u]/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){return eval("("+text+")")
}return false
}}
}()
}gadgets.json.flatten=function(C){var D={};
if(C===null||C===undefined){return D
}for(var A in C){if(C.hasOwnProperty(A)){var B=C[A];
if(null===B||undefined===B){continue
}D[A]=(typeof B==="string")?B:gadgets.json.stringify(B)
}}return D
};;
var tamings___=tamings___||[];
tamings___.push(function(A){___.tamesTo(gadgets.json.stringify,safeJSON.stringify);
___.tamesTo(gadgets.json.parse,safeJSON.parse)
});;
shindig.Auth=function(){var authToken=null;
var trusted=null;
function addParamsToToken(urlParams){var args=authToken.split("&");
for(var i=0;
i<args.length;
i++){var nameAndValue=args[i].split("=");
if(nameAndValue.length===2){var name=nameAndValue[0];
var value=nameAndValue[1];
if(value==="$"){value=encodeURIComponent(urlParams[name]);
args[i]=name+"="+value
}}}authToken=args.join("&")
}function init(configuration){var urlParams=gadgets.util.getUrlParameters();
var config=configuration["shindig.auth"]||{};
if(config.authToken){authToken=config.authToken
}else{if(urlParams.st){authToken=urlParams.st
}}if(authToken!==null){addParamsToToken(urlParams)
}if(config.trustedJson){trusted=eval("("+config.trustedJson+")")
}}gadgets.config.register("shindig.auth",null,init);
return{getSecurityToken:function(){return authToken
},updateSecurityToken:function(newToken){authToken=newToken
},getTrustedData:function(){return trusted
}}
};;
shindig.auth=new shindig.Auth();;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.wpm){gadgets.rpctx.wpm=function(){var D,C;
function B(G,H,F){if(typeof window.addEventListener!="undefined"){window.addEventListener(G,H,F)
}else{if(typeof window.attachEvent!="undefined"){window.attachEvent("on"+G,H)
}}}function A(G,H,F){if(window.removeEventListener){window.removeEventListener(G,H,F)
}else{if(window.detachEvent){window.detachEvent("on"+G,H)
}}}function E(G){var H=gadgets.json.parse(G.data);
if(!H||!H.f){return 
}var F=gadgets.rpc.getTargetOrigin(H.f);
if(typeof G.origin!=="undefined"?G.origin!==F:G.domain!==/^.+:\/\/([^:]+).*/.exec(F)[1]){return 
}D(H,G.origin)
}return{getCode:function(){return"wpm"
},isParentVerifiable:function(){return true
},init:function(F,G){D=F;
C=G;
B("message",E,false);
C("..",true);
return true
},setup:function(G,F){C(G,true);
return true
},call:function(G,J,I){var F=gadgets.rpc.getTargetOrigin(G);
var H=gadgets.rpc._getTargetWin(G);
if(F){window.setTimeout(function(){H.postMessage(gadgets.json.stringify(I),F)
},0)
}else{gadgets.error("No relay set (used as window.postMessage targetOrigin), cannot send cross-domain message")
}return true
}}
}()
};;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.flash){gadgets.rpctx.flash=function(){var Z="___xpcswf";
var Q=null;
var J=false;
var K=null;
var g=null;
var U=null;
var h=100;
var R=50;
var a=[];
var i=null;
var A=0;
var V="_scr";
var F="_pnt";
var I=100;
var P=50;
var M=0;
var E=null;
var Y={};
var c=window.location.protocol+"//"+window.location.host;
var N="___jsl";
var D="_fm";
var H;
function T(){window[N]=window[N]||{};
var k=window[N];
var l=k[D]={};
H=N+"."+D;
return l
}var L=T();
function j(m,k){var l=function(){m.apply({},arguments)
};
L[k]=L[k]||l;
return H+"."+k
}function O(k){return k===".."?gadgets.rpc.RPC_ID:k
}function d(k){return k===".."?"INNER":"OUTER"
}function f(k){if(J){Q=k.rpc["commSwf"]||"/xpc.swf"
}}gadgets.config.register("rpc",null,f);
function W(){if(U===null&&document.body&&Q){var m=Q+"?cb="+Math.random()+"&origin="+c+"&jsl=1";
var l=document.createElement("div");
l.style.height="1px";
l.style.width="1px";
var k='<object height="1" width="1" id="'+Z+'" type="application/x-shockwave-flash"><param name="allowScriptAccess" value="always"></param><param name="movie" value="'+m+'"></param><embed type="application/x-shockwave-flash" allowScriptAccess="always" src="'+m+'" height="1" width="1"></embed></object>';
document.body.appendChild(l);
l.innerHTML=k;
U=l.firstChild
}++A;
if(i!==null&&(U!==null||A>=R)){window.clearTimeout(i)
}else{i=window.setTimeout(W,h)
}}function b(){if(Y[".."]){return 
}X("..");
M++;
if(M>=P&&E!==null){window.clearTimeout(E);
E=null
}else{E=window.setTimeout(b,I)
}}function e(){if(U!==null){while(a.length>0){var l=a.shift();
var k=l.targetId;
U.setup(l.token,O(k),d(k))
}}}function G(){e();
if(i!==null){window.clearTimeout(i)
}i=null
}j(G,"ready");
function B(){if(!Y[".."]&&E===null){E=window.setTimeout(b,I)
}}j(B,"setupDone");
function C(m,q,o){var l=gadgets.rpc.getTargetOrigin(m);
var p=gadgets.rpc.getAuthToken(m);
var k="sendMessage_"+O(m)+"_"+p+"_"+d(m);
var n=U[k];
n.call(U,gadgets.json.stringify(o),l);
return true
}function S(m,o,n){var k=gadgets.json.parse(m);
var l=k[V];
if(l){g(l,true);
Y[l]=true;
if(l!==".."){X(l,true)
}return 
}window.setTimeout(function(){K(k,o)
},0)
}j(S,"receiveMessage");
function X(n,m){var k=gadgets.rpc.RPC_ID;
var l={};
l[V]=m?"..":k;
l[F]=k;
C(n,k,l)
}return{getCode:function(){return"flash"
},isParentVerifiable:function(){return true
},init:function(l,k){K=l;
g=k;
J=true;
return true
},setup:function(l,k){a.push({token:k,targetId:l});
if(U===null&&i===null){i=window.setTimeout(W,h)
}e();
return true
},call:C,_receiveMessage:S,_ready:G,_setupDone:B}
}()
};;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.frameElement){gadgets.rpctx.frameElement=function(){var E="__g2c_rpc";
var B="__c2g_rpc";
var D;
var C;
function A(G,K,J){try{if(K!==".."){var F=window.frameElement;
if(typeof F[E]==="function"){if(typeof F[E][B]!=="function"){F[E][B]=function(L){D(gadgets.json.parse(L))
}
}F[E](gadgets.json.stringify(J));
return true
}}else{var I=document.getElementById(G);
if(typeof I[E]==="function"&&typeof I[E][B]==="function"){I[E][B](gadgets.json.stringify(J));
return true
}}}catch(H){}return false
}return{getCode:function(){return"fe"
},isParentVerifiable:function(){return false
},init:function(F,G){D=F;
C=G;
return true
},setup:function(J,F){if(J!==".."){try{var I=document.getElementById(J);
I[E]=function(K){D(gadgets.json.parse(K))
}
}catch(H){return false
}}if(J===".."){C("..",true);
var G=function(){window.setTimeout(function(){gadgets.rpc.call(J,gadgets.rpc.ACK)
},500)
};
gadgets.util.registerOnLoadHandler(G)
}return true
},call:function(F,H,G){return A(F,H,G)
}}
}()
};;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.nix){gadgets.rpctx.nix=function(){var C="GRPC____NIXVBS_wrapper";
var D="GRPC____NIXVBS_get_wrapper";
var F="GRPC____NIXVBS_handle_message";
var B="GRPC____NIXVBS_create_channel";
var A=10;
var J=500;
var I={};
var H;
var G=0;
function E(){var L=I[".."];
if(L){return 
}if(++G>A){gadgets.warn("Nix transport setup failed, falling back...");
H("..",false);
return 
}if(!L&&window.opener&&"GetAuthToken" in window.opener){L=window.opener;
if(L.GetAuthToken()==gadgets.rpc.getAuthToken("..")){var K=gadgets.rpc.getAuthToken("..");
L.CreateChannel(window[D]("..",K),K);
I[".."]=L;
window.opener=null;
H("..",true);
return 
}}window.setTimeout(function(){E()
},J)
}return{getCode:function(){return"nix"
},isParentVerifiable:function(){return false
},init:function(L,M){H=M;
if(typeof window[D]!=="unknown"){window[F]=function(O){window.setTimeout(function(){L(gadgets.json.parse(O))
},0)
};
window[B]=function(O,Q,P){if(gadgets.rpc.getAuthToken(O)===P){I[O]=Q;
H(O,true)
}};
var K="Class "+C+"\n Private m_Intended\nPrivate m_Auth\nPublic Sub SetIntendedName(name)\n If isEmpty(m_Intended) Then\nm_Intended = name\nEnd If\nEnd Sub\nPublic Sub SetAuth(auth)\n If isEmpty(m_Auth) Then\nm_Auth = auth\nEnd If\nEnd Sub\nPublic Sub SendMessage(data)\n "+F+"(data)\nEnd Sub\nPublic Function GetAuthToken()\n GetAuthToken = m_Auth\nEnd Function\nPublic Sub CreateChannel(channel, auth)\n Call "+B+"(m_Intended, channel, auth)\nEnd Sub\nEnd Class\nFunction "+D+"(name, auth)\nDim wrap\nSet wrap = New "+C+"\nwrap.SetIntendedName name\nwrap.SetAuth auth\nSet "+D+" = wrap\nEnd Function";
try{window.execScript(K,"vbscript")
}catch(N){return false
}}return true
},setup:function(O,K){if(O===".."){E();
return true
}try{var M=document.getElementById(O);
var N=window[D](O,K);
M.contentWindow.opener=N
}catch(L){return false
}return true
},call:function(K,N,M){try{if(I[K]){I[K].SendMessage(gadgets.json.stringify(M))
}}catch(L){return false
}return true
}}
}()
};;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.rmr){gadgets.rpctx.rmr=function(){var H=500;
var F=10;
var I={};
var A=gadgets.util.getUrlParameters()["parent"];
var C;
var J;
function L(Q,O,P,N){var R=function(){document.body.appendChild(Q);
Q.src="about:blank";
if(N){Q.onload=function(){M(N)
}
}Q.src=O+"#"+P
};
if(document.body){R()
}else{gadgets.util.registerOnLoadHandler(function(){R()
})
}}function D(Q){if(typeof I[Q]==="object"){return 
}var R=document.createElement("iframe");
var O=R.style;
O.position="absolute";
O.top="0px";
O.border="0";
O.opacity="0";
O.width="10px";
O.height="1px";
R.id="rmrtransport-"+Q;
R.name=R.id;
var P=gadgets.rpc.getRelayUrl(Q);
var N=gadgets.rpc.getOrigin(A);
if(!P){P=N+"/robots.txt"
}I[Q]={frame:R,receiveWindow:null,relayUri:P,relayOrigin:N,searchCounter:0,width:10,waiting:true,queue:[],sendId:0,recvId:0,verifySendToken:String(Math.random()),verifyRecvToken:null,originVerified:false};
if(Q!==".."){L(R,P,B(Q))
}E(Q)
}function E(P){var R=null;
I[P].searchCounter++;
try{var O=gadgets.rpc._getTargetWin(P);
if(P===".."){R=O.frames["rmrtransport-"+gadgets.rpc.RPC_ID]
}else{R=O.frames["rmrtransport-.."]
}}catch(Q){}var N=false;
if(R){N=G(P,R)
}if(!N){if(I[P].searchCounter>F){return 
}window.setTimeout(function(){E(P)
},H)
}}function K(O,Q,U,T){var P=null;
if(U!==".."){P=I[".."]
}else{P=I[O]
}if(P){if(Q!==gadgets.rpc.ACK){P.queue.push(T)
}if(P.waiting||(P.queue.length===0&&!(Q===gadgets.rpc.ACK&&T&&T.ackAlone===true))){return true
}if(P.queue.length>0){P.waiting=true
}var N=P.relayUri+"#"+B(O);
try{P.frame.contentWindow.location=N;
var R=P.width==10?20:10;
P.frame.style.width=R+"px";
P.width=R
}catch(S){return false
}}return true
}function B(O){var P=I[O];
var N={id:P.sendId};
if(P){N.d=Array.prototype.slice.call(P.queue,0);
var Q={s:gadgets.rpc.ACK,id:P.recvId};
if(!P.originVerified){Q.sendToken=P.verifySendToken
}if(P.verifyRecvToken){Q.recvToken=P.verifyRecvToken
}N.d.push(Q)
}return gadgets.json.stringify(N)
}function M(Y){var V=I[Y];
var R=V.receiveWindow.location.hash.substring(1);
var Z=gadgets.json.parse(decodeURIComponent(R))||{};
var O=Z.d||[];
var P=false;
var U=false;
var W=0;
var N=(V.recvId-Z.id);
for(var Q=0;
Q<O.length;
++Q){var T=O[Q];
if(T.s===gadgets.rpc.ACK){J(Y,true);
V.verifyRecvToken=T.sendToken;
if(!V.originVerified&&T.recvToken&&String(T.recvToken)==String(V.verifySendToken)){V.originVerified=true
}if(V.waiting){U=true
}V.waiting=false;
var S=Math.max(0,T.id-V.sendId);
V.queue.splice(0,S);
V.sendId=Math.max(V.sendId,T.id||0);
continue
}P=true;
if(++W<=N){continue
}++V.recvId;
C(T,V.originVerified?V.relayOrigin:undefined)
}if(P||(U&&V.queue.length>0)){var X=(Y==="..")?gadgets.rpc.RPC_ID:"..";
K(Y,gadgets.rpc.ACK,X,{ackAlone:P})
}}function G(Q,T){var P=I[Q];
try{var O=false;
O="document" in T;
if(!O){return false
}O=typeof T.document=="object";
if(!O){return false
}var S=T.location.href;
if(S==="about:blank"){return false
}}catch(N){return false
}P.receiveWindow=T;
function R(){M(Q)
}if(typeof T.attachEvent==="undefined"){T.onresize=R
}else{T.attachEvent("onresize",R)
}if(Q===".."){L(P.frame,P.relayUri,B(Q),Q)
}else{M(Q)
}return true
}return{getCode:function(){return"rmr"
},isParentVerifiable:function(){return true
},init:function(N,O){C=N;
J=O;
return true
},setup:function(P,N){try{D(P)
}catch(O){gadgets.warn("Caught exception setting up RMR: "+O);
return false
}return true
},call:function(N,P,O){return K(N,O.s,P,O)
}}
}()
};;
gadgets.rpctx=gadgets.rpctx||{};
if(!gadgets.rpctx.ifpc){gadgets.rpctx.ifpc=function(){var H=[];
var E=0;
var D;
var A=2000;
var G={};
function C(K){var I=[];
for(var L=0,J=K.length;
L<J;
++L){I.push(encodeURIComponent(gadgets.json.stringify(K[L])))
}return I.join("&")
}function B(L){var J;
for(var I=H.length-1;
I>=0;
--I){var M=H[I];
try{if(M&&(M.recyclable||M.readyState==="complete")){M.parentNode.removeChild(M);
if(window.ActiveXObject){H[I]=M=null;
H.splice(I,1)
}else{M.recyclable=false;
J=M;
break
}}}catch(K){}}if(!J){J=document.createElement("iframe");
J.style.border=J.style.width=J.style.height="0px";
J.style.visibility="hidden";
J.style.position="absolute";
J.onload=function(){this.recyclable=true
};
H.push(J)
}J.src=L;
window.setTimeout(function(){document.body.appendChild(J)
},0)
}function F(I,K){for(var J=K-1;
J>=0;
--J){if(typeof I[J]==="undefined"){return false
}}return true
}return{getCode:function(){return"ifpc"
},isParentVerifiable:function(){return true
},init:function(I,J){D=J;
D("..",true);
return true
},setup:function(J,I){D(J,true);
return true
},call:function(S,R,Q){var L=gadgets.rpc.getRelayUrl(S);
++E;
if(!L){gadgets.warn("No relay file assigned for IFPC");
return false
}var I=null,J=[];
if(Q.l){var O=Q.a;
I=[L,"#",C([R,E,1,0,C([R,Q.s,"","",R].concat(O))])].join("");
J.push(I)
}else{I=[L,"#",S,"&",R,"@",E,"&"].join("");
var T=encodeURIComponent(gadgets.json.stringify(Q)),N=A-I.length,P=Math.ceil(T.length/N),M=0,K;
while(T.length>0){K=T.substring(0,N);
T=T.substring(N);
J.push([I,P,"&",M,"&",K].join(""));
M+=1
}}do{B(J.shift())
}while(J.length>0);
return true
},_receiveMessage:function(I,N){var O=I[1],M=parseInt(I[2],10),K=parseInt(I[3],10),L=I[I.length-1],J=M===1;
if(M>1){if(!G[O]){G[O]=[]
}G[O][K]=L;
if(F(G[O],M)){L=G[O].join("");
delete G[O];
J=true
}}if(J){N(gadgets.json.parse(decodeURIComponent(L)))
}}}
}()
};;
if(!window.gadgets["rpc"]){gadgets.rpc=function(){var l="__cb";
var s="";
var t="__ack";
var F=500;
var f=10;
var B="|";
var T="callback";
var G="origin";
var R="referer";
var Q={};
var w={};
var c={};
var a={};
var Y=0;
var L={};
var M={};
var q={};
var D={};
var N={};
var d={};
var E=null;
var P=null;
var Z=(window.top!==window.self);
var U=window.name;
var i=function(){};
var p=0;
var y=1;
var A=2;
var W=window.console;
var v=W&&W.log&&function(AD){W.log(AD)
}||function(){};
var r=(function(){function AD(AE){return function(){v(AE+": call ignored")
}
}return{getCode:function(){return"noop"
},isParentVerifiable:function(){return true
},init:AD("init"),setup:AD("setup"),call:AD("call")}
})();
if(gadgets.util){D=gadgets.util.getUrlParameters()
}function j(){if(D.rpctx=="flash"){return gadgets.rpctx.flash
}if(D.rpctx=="rmr"){return gadgets.rpctx.rmr
}return typeof window.postMessage==="function"?gadgets.rpctx.wpm:typeof window.postMessage==="object"?gadgets.rpctx.wpm:window.ActiveXObject?(gadgets.rpctx.flash?gadgets.rpctx.flash:gadgets.rpctx.nix):navigator.userAgent.indexOf("WebKit")>0?gadgets.rpctx.rmr:navigator.product==="Gecko"?gadgets.rpctx.frameElement:gadgets.rpctx.ifpc
}function K(AI,AG){if(N[AI]){return 
}var AE=g;
if(!AG){AE=r
}N[AI]=AE;
var AD=d[AI]||[];
for(var AF=0;
AF<AD.length;
++AF){var AH=AD[AF];
AH.t=e(AI);
AE.call(AI,AH.f,AH)
}d[AI]=[]
}var h=false,u=false;
function n(){if(u){return 
}function AD(){h=true
}if(typeof window.addEventListener!="undefined"){window.addEventListener("unload",AD,false)
}else{if(typeof window.attachEvent!="undefined"){window.attachEvent("onunload",AD)
}}u=true
}function J(AD,AH,AE,AG,AF){if(!a[AH]||a[AH]!==AE){gadgets.error("Invalid auth token. "+a[AH]+" vs "+AE);
i(AH,A)
}AF.onunload=function(){if(M[AH]&&!h){i(AH,y);
gadgets.rpc.removeReceiver(AH)
}};
n();
AG=gadgets.json.parse(decodeURIComponent(AG))
}function z(AH,AE){if(AH&&typeof AH.s==="string"&&typeof AH.f==="string"&&AH.a instanceof Array){if(a[AH.f]){if(a[AH.f]!==AH.t){gadgets.error("Invalid auth token. "+a[AH.f]+" vs "+AH.t);
i(AH.f,A)
}}if(AH.s===t){window.setTimeout(function(){K(AH.f,true)
},0);
return 
}if(AH.c){AH[T]=function(AI){gadgets.rpc.call(AH.f,l,null,AH.c,AI)
}
}if(AE){var AF=S(AE);
AH[G]=AE;
var AG=AH.r;
if(!AG||S(AG)!=AF){AG=AE
}AH[R]=AG
}var AD=(Q[AH.s]||Q[s]).apply(AH,AH.a);
if(AH.c&&typeof AD!=="undefined"){gadgets.rpc.call(AH.f,l,null,AH.c,AD)
}}}function S(AF){if(!AF){return""
}AF=AF.toLowerCase();
if(AF.indexOf("//")==0){AF=window.location.protocol+AF
}if(AF.indexOf("://")==-1){AF=window.location.protocol+"//"+AF
}var AG=AF.substring(AF.indexOf("://")+3);
var AD=AG.indexOf("/");
if(AD!=-1){AG=AG.substring(0,AD)
}var AI=AF.substring(0,AF.indexOf("://"));
var AH="";
var AJ=AG.indexOf(":");
if(AJ!=-1){var AE=AG.substring(AJ+1);
AG=AG.substring(0,AJ);
if((AI==="http"&&AE!=="80")||(AI==="https"&&AE!=="443")){AH=":"+AE
}}return AI+"://"+AG+AH
}function b(AE,AD){return"/"+AE+(AD?B+AD:"")
}function X(AG){if(AG.charAt(0)=="/"){var AE=AG.indexOf(B);
var AF=AE>0?AG.substring(1,AE):AG.substring(1);
var AD=AE>0?AG.substring(AE+1):null;
return{id:AF,origin:AD}
}else{return null
}}function AC(AF){if(typeof AF==="undefined"||AF===".."){return window.parent
}var AE=X(AF);
if(AE){return window.top.frames[AE.id]
}AF=String(AF);
var AD=window.frames[AF];
if(AD){return AD
}AD=document.getElementById(AF);
if(AD&&AD.contentWindow){return AD.contentWindow
}return null
}function k(AG){var AF=null;
var AD=o(AG);
if(AD){AF=AD
}else{var AE=X(AG);
if(AE){AF=AE.origin
}else{if(AG==".."){AF=D.parent
}else{AF=document.getElementById(AG).src
}}}return S(AF)
}var g=j();
Q[s]=function(){v("Unknown RPC service: "+this.s)
};
Q[l]=function(AE,AD){var AF=L[AE];
if(AF){delete L[AE];
AF.call(this,AD)
}};
function x(AF,AD){if(M[AF]===true){return 
}if(typeof M[AF]==="undefined"){M[AF]=0
}var AE=AC(AF);
if(AF===".."||AE!=null){if(g.setup(AF,AD)===true){M[AF]=true;
return 
}}if(M[AF]!==true&&M[AF]++<f){window.setTimeout(function(){x(AF,AD)
},F)
}else{N[AF]=r;
M[AF]=true
}}function m(AE,AH){if(typeof q[AE]==="undefined"){q[AE]=false;
var AG=o(AE);
if(S(AG)!==S(window.location.href)){return false
}var AF=AC(AE);
try{var AI=AF.gadgets;
q[AE]=AI.rpc.receiveSameDomain
}catch(AD){}}if(typeof q[AE]==="function"){q[AE](AH);
return true
}return false
}function o(AE){var AD=w[AE];
if(AD&&AD.substring(0,1)==="/"){if(AD.substring(1,2)==="/"){AD=document.location.protocol+AD
}else{AD=document.location.protocol+"//"+document.location.host+AD
}}return AD
}function AB(AE,AD,AF){if(!/http(s)?:\/\/.+/.test(AD)){if(AD.indexOf("//")==0){AD=window.location.protocol+AD
}else{if(AD.charAt(0)=="/"){AD=window.location.protocol+"//"+window.location.host+AD
}else{if(AD.indexOf("://")==-1){AD=window.location.protocol+"//"+AD
}}}}w[AE]=AD;
c[AE]=!!AF
}function e(AD){return a[AD]
}function C(AD,AE){AE=AE||"";
a[AD]=String(AE);
x(AD,AE)
}function AA(AE){var AD=AE.passReferrer||"";
var AF=AD.split(":",2);
E=AF[0]||"none";
P=AF[1]||"origin"
}function H(AE,AD){function AF(AI){var AH=AI?AI.rpc:{};
var AJ=String(AH.useLegacyProtocol)==="true";
AA(AH);
var AG=AH.parentRelayUrl||"";
AG=S(D.parent||AD)+AG;
AB("..",AG,AJ);
if(AJ){g=gadgets.rpctx.ifpc;
g.init(z,K)
}C("..",AE)
}if(!D.parent&&AD){AF({});
return 
}gadgets.config.register("rpc",null,AF)
}function O(AE,AI,AK){if(AE.charAt(0)!="/"){if(!gadgets.util){return 
}var AH=document.getElementById(AE);
if(!AH){throw new Error("Cannot set up gadgets.rpc receiver with ID: "+AE+", element not found.")
}}var AD=AH&&AH.src;
var AF=AI||gadgets.rpc.getOrigin(AD);
AB(AE,AF);
var AJ=gadgets.util.getUrlParameters(AD);
var AG=AK||AJ.rpctoken;
C(AE,AG)
}function I(AD,AF,AG){if(AD===".."){var AE=AG||D.rpctoken||D.ifpctok||"";
H(AE,AF)
}else{O(AD,AF,AG)
}}function V(AF){if(E==="bidir"||(E==="c2p"&&AF==="..")||(E==="p2c"&&AF!=="..")){var AE=window.location.href;
var AG="?";
if(P==="query"){AG="#"
}else{if(P==="hash"){return AE
}}var AD=AE.lastIndexOf(AG);
AD=AD===-1?AE.length:AD;
return AE.substring(0,AD)
}return null
}return{config:function(AD){if(typeof AD.securityCallback==="function"){i=AD.securityCallback
}},register:function(AE,AD){if(AE===l||AE===t){throw new Error("Cannot overwrite callback/ack service")
}if(AE===s){throw new Error("Cannot overwrite default service: use registerDefault")
}Q[AE]=AD
},unregister:function(AD){if(AD===l||AD===t){throw new Error("Cannot delete callback/ack service")
}if(AD===s){throw new Error("Cannot delete default service: use unregisterDefault")
}delete Q[AD]
},registerDefault:function(AD){Q[s]=AD
},unregisterDefault:function(){delete Q[s]
},forceParentVerifiable:function(){if(!g.isParentVerifiable()){g=gadgets.rpctx.ifpc
}},call:function(AD,AF,AK,AI){AD=AD||"..";
var AJ="..";
if(AD===".."){AJ=U
}else{if(AD.charAt(0)=="/"){AJ=b(U,gadgets.rpc.getOrigin(window.location.href))
}}++Y;
if(AK){L[Y]=AK
}var AH={s:AF,f:AJ,c:AK?Y:0,a:Array.prototype.slice.call(arguments,3),t:a[AD],l:c[AD]};
var AE=V(AD);
if(AE){AH.r=AE
}if(AD!==".."&&X(AD)==null&&!document.getElementById(AD)){return 
}if(m(AD,AH)){return 
}var AG=N[AD];
if(!AG&&X(AD)!==null){AG=g
}if(!AG){if(!d[AD]){d[AD]=[AH]
}else{d[AD].push(AH)
}return 
}if(c[AD]){AG=gadgets.rpctx.ifpc
}if(AG.call(AD,AJ,AH)===false){N[AD]=r;
g.call(AD,AJ,AH)
}},getRelayUrl:o,setRelayUrl:AB,setAuthToken:C,setupReceiver:I,getAuthToken:e,removeReceiver:function(AD){delete w[AD];
delete c[AD];
delete a[AD];
delete M[AD];
delete q[AD];
delete N[AD]
},getRelayChannel:function(){return g.getCode()
},receive:function(AE,AD){if(AE.length>4){g._receiveMessage(AE,z)
}else{J.apply(null,AE.concat(AD))
}},receiveSameDomain:function(AD){AD.a=Array.prototype.slice.call(AD.a);
window.setTimeout(function(){z(AD)
},0)
},getOrigin:S,getTargetOrigin:k,init:function(){if(g.init(z,K)===false){g=r
}if(Z){I("..")
}else{gadgets.config.register("rpc",null,function(AD){AA(AD.rpc||{})
})
}},_getTargetWin:AC,_parseSiblingId:X,ACK:t,RPC_ID:U||"..",SEC_ERROR_LOAD_TIMEOUT:p,SEC_ERROR_FRAME_PHISH:y,SEC_ERROR_FORGED_MSG:A}
}();
gadgets.rpc.init()
};;
gadgets.io=function(){var config={};
var oauthState;
function makeXhr(){var x;
if(typeof shindig!="undefined"&&shindig.xhrwrapper&&shindig.xhrwrapper.createXHR){return shindig.xhrwrapper.createXHR()
}else{if(typeof ActiveXObject!="undefined"){x=new ActiveXObject("Msxml2.XMLHTTP");
if(!x){x=new ActiveXObject("Microsoft.XMLHTTP")
}return x
}else{if(typeof XMLHttpRequest!="undefined"||window.XMLHttpRequest){return new window.XMLHttpRequest()
}else{throw ("no xhr available")
}}}}function hadError(xobj,callback){if(xobj.readyState!==4){return true
}try{if(xobj.status!==200){var error=(""+xobj.status);
if(xobj.responseText){error=error+" "+xobj.responseText
}callback({errors:[error],rc:xobj.status,text:xobj.responseText});
return true
}}catch(e){callback({errors:[e.number+" Error not specified"],rc:e.number,text:e.description});
return true
}return false
}function processNonProxiedResponse(url,callback,params,xobj){if(hadError(xobj,callback)){return 
}var data={body:xobj.responseText};
callback(transformResponseData(params,data))
}var UNPARSEABLE_CRUFT="throw 1; < don't be evil' >";
function processResponse(url,callback,params,xobj){if(hadError(xobj,callback)){return 
}var txt=xobj.responseText;
var offset=txt.indexOf(UNPARSEABLE_CRUFT)+UNPARSEABLE_CRUFT.length;
if(offset<UNPARSEABLE_CRUFT.length){return 
}txt=txt.substr(offset);
var data=eval("("+txt+")");
data=data[url];
if(data.oauthState){oauthState=data.oauthState
}if(data.st){shindig.auth.updateSecurityToken(data.st)
}callback(transformResponseData(params,data))
}function transformResponseData(params,data){var resp={text:data.body,rc:data.rc||200,headers:data.headers,oauthApprovalUrl:data.oauthApprovalUrl,oauthError:data.oauthError,oauthErrorText:data.oauthErrorText,errors:[]};
if(resp.rc<200||resp.rc>=400){resp.errors=[resp.rc+" Error"]
}else{if(resp.text){if(resp.rc>=300&&resp.rc<400){params.CONTENT_TYPE="TEXT"
}switch(params.CONTENT_TYPE){case"JSON":case"FEED":resp.data=gadgets.json.parse(resp.text);
if(!resp.data){resp.errors.push("500 Failed to parse JSON");
resp.rc=500;
resp.data=null
}break;
case"DOM":var dom;
if(typeof ActiveXObject!="undefined"){dom=new ActiveXObject("Microsoft.XMLDOM");
dom.async=false;
dom.validateOnParse=false;
dom.resolveExternals=false;
if(!dom.loadXML(resp.text)){resp.errors.push("500 Failed to parse XML");
resp.rc=500
}else{resp.data=dom
}}else{var parser=new DOMParser();
dom=parser.parseFromString(resp.text,"text/xml");
if("parsererror"===dom.documentElement.nodeName){resp.errors.push("500 Failed to parse XML");
resp.rc=500
}else{resp.data=dom
}}break;
default:resp.data=resp.text;
break
}}}return resp
}function makeXhrRequest(realUrl,proxyUrl,callback,paramData,method,params,processResponseFunction,opt_contentType){var xhr=makeXhr();
if(proxyUrl.indexOf("//")==0){proxyUrl=document.location.protocol+proxyUrl
}xhr.open(method,proxyUrl,true);
if(callback){xhr.onreadystatechange=gadgets.util.makeClosure(null,processResponseFunction,realUrl,callback,params,xhr)
}if(paramData!==null){xhr.setRequestHeader("Content-Type",opt_contentType||"application/x-www-form-urlencoded");
xhr.send(paramData)
}else{xhr.send(null)
}}function respondWithPreload(postData,params,callback){if(gadgets.io.preloaded_&&postData.httpMethod==="GET"){for(var i=0;
i<gadgets.io.preloaded_.length;
i++){var preload=gadgets.io.preloaded_[i];
if(preload&&(preload.id===postData.url)){delete gadgets.io.preloaded_[i];
if(preload.rc!==200){callback({rc:preload.rc,errors:[preload.rc+" Error"]})
}else{if(preload.oauthState){oauthState=preload.oauthState
}var resp={body:preload.body,rc:preload.rc,headers:preload.headers,oauthApprovalUrl:preload.oauthApprovalUrl,oauthError:preload.oauthError,oauthErrorText:preload.oauthErrorText,errors:[]};
callback(transformResponseData(params,resp))
}return true
}}}return false
}function init(configuration){config=configuration["core.io"]||{}
}var requiredConfig={proxyUrl:new gadgets.config.RegExValidator(/.*%(raw)?url%.*/),jsonProxyUrl:gadgets.config.NonEmptyStringValidator};
gadgets.config.register("core.io",requiredConfig,init);
return{makeRequest:function(url,callback,opt_params){var params=opt_params||{};
var httpMethod=params.METHOD||"GET";
var refreshInterval=params.REFRESH_INTERVAL;
var auth,st;
if(params.AUTHORIZATION&&params.AUTHORIZATION!=="NONE"){auth=params.AUTHORIZATION.toLowerCase();
st=shindig.auth.getSecurityToken()
}else{if(httpMethod==="GET"&&refreshInterval===undefined){refreshInterval=3600
}}var signOwner=true;
if(typeof params.OWNER_SIGNED!=="undefined"){signOwner=params.OWNER_SIGNED
}var signViewer=true;
if(typeof params.VIEWER_SIGNED!=="undefined"){signViewer=params.VIEWER_SIGNED
}var headers=params.HEADERS||{};
if(httpMethod==="POST"&&!headers["Content-Type"]){headers["Content-Type"]="application/x-www-form-urlencoded"
}var urlParams=gadgets.util.getUrlParameters();
var paramData={url:url,httpMethod:httpMethod,headers:gadgets.io.encodeValues(headers,false),postData:params.POST_DATA||"",authz:auth||"",st:st||"",contentType:params.CONTENT_TYPE||"TEXT",numEntries:params.NUM_ENTRIES||"3",getSummaries:!!params.GET_SUMMARIES,signOwner:signOwner,signViewer:signViewer,gadget:urlParams.url,container:urlParams.container||urlParams.synd||"default",bypassSpecCache:gadgets.util.getUrlParameters().nocache||"",getFullHeaders:!!params.GET_FULL_HEADERS};
if(auth==="oauth"||auth==="signed"){if(gadgets.io.oauthReceivedCallbackUrl_){paramData.OAUTH_RECEIVED_CALLBACK=gadgets.io.oauthReceivedCallbackUrl_;
gadgets.io.oauthReceivedCallbackUrl_=null
}paramData.oauthState=oauthState||"";
for(var opt in params){if(params.hasOwnProperty(opt)){if(opt.indexOf("OAUTH_")===0){paramData[opt]=params[opt]
}}}}var proxyUrl=config.jsonProxyUrl.replace("%host%",document.location.host);
if(!respondWithPreload(paramData,params,callback,processResponse)){if(httpMethod==="GET"&&refreshInterval>0){var extraparams="?refresh="+refreshInterval+"&"+gadgets.io.encodeValues(paramData);
makeXhrRequest(url,proxyUrl+extraparams,callback,null,"GET",params,processResponse)
}else{makeXhrRequest(url,proxyUrl,callback,gadgets.io.encodeValues(paramData),"POST",params,processResponse)
}}},makeNonProxiedRequest:function(relativeUrl,callback,opt_params,opt_contentType){var params=opt_params||{};
makeXhrRequest(relativeUrl,relativeUrl,callback,params.POST_DATA,params.METHOD,params,processNonProxiedResponse,opt_contentType)
},clearOAuthState:function(){oauthState=undefined
},encodeValues:function(fields,opt_noEscaping){var escape=!opt_noEscaping;
var buf=[];
var first=false;
for(var i in fields){if(fields.hasOwnProperty(i)&&!/___$/.test(i)){if(!first){first=true
}else{buf.push("&")
}buf.push(escape?encodeURIComponent(i):i);
buf.push("=");
buf.push(escape?encodeURIComponent(fields[i]):fields[i])
}}return buf.join("")
},getProxyUrl:function(url,opt_params){return url
}}
}();
gadgets.io.RequestParameters=gadgets.util.makeEnum(["METHOD","CONTENT_TYPE","POST_DATA","HEADERS","AUTHORIZATION","NUM_ENTRIES","GET_SUMMARIES","GET_FULL_HEADERS","REFRESH_INTERVAL","OAUTH_SERVICE_NAME","OAUTH_USE_TOKEN","OAUTH_TOKEN_NAME","OAUTH_REQUEST_TOKEN","OAUTH_REQUEST_TOKEN_SECRET","OAUTH_RECEIVED_CALLBACK"]);
gadgets.io.MethodType=gadgets.util.makeEnum(["GET","POST","PUT","DELETE","HEAD"]);
gadgets.io.ContentType=gadgets.util.makeEnum(["TEXT","DOM","JSON","FEED"]);
gadgets.io.AuthorizationType=gadgets.util.makeEnum(["NONE","SIGNED","OAUTH"]);;
var tamings___=tamings___||[];
tamings___.push(function(A){caja___.whitelistFuncs([[gadgets.io,"encodeValues"],[gadgets.io,"getProxyUrl"],[gadgets.io,"makeRequest"]])
});;
gadgets.log=(function(){var E=1;
var A=2;
var F=3;
var C=4;
var D=function(I){B(E,I)
};
gadgets.warn=function(I){B(A,I)
};
gadgets.error=function(I){B(F,I)
};
gadgets.setLogLevel=function(I){H=I
};
function B(J,I){if(J<H||!G){return 
}if(J===A&&G.warn){G.warn(I)
}else{if(J===F&&G.error){G.error(I)
}else{if(G.log){G.log(I)
}}}}D.INFO=E;
D.WARNING=A;
D.NONE=C;
var H=E;
var G=window.console?window.console:window.opera?window.opera.postError:undefined;
return D
})();;
var tamings___=tamings___||[];
tamings___.push(function(A){___.grantRead(gadgets.log,"INFO");
___.grantRead(gadgets.log,"WARNING");
___.grantRead(gadgets.log,"ERROR");
___.grantRead(gadgets.log,"NONE");
caja___.whitelistFuncs([[gadgets,"log"],[gadgets,"warn"],[gadgets,"error"],[gadgets,"setLogLevel"]])
});;
shindig.uri=(function(){var A=new RegExp("^(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");
return function(Y){var R="";
var N="";
var C="";
var H="";
var D=null;
var I="";
var J=null;
var L=window.decodeURIComponent?decodeURIComponent:unescape;
var X=window.encodeURIComponent?encodeURIComponent:escape;
var K=null;
function U(a){if(a.match(A)===null){throw"Malformed URL: "+a
}R=RegExp.$1;
N=RegExp.$2;
C=RegExp.$3;
H=RegExp.$4;
I=RegExp.$5
}function T(f){var e=[];
for(var c=0,a=f.length;
c<a;
++c){var b=f[c][0];
var d=f[c][1];
if(d===undefined){continue
}e.push(X(b)+(d!==null?"="+X(d):""))
}return e.join("&")
}function Q(){if(D){H=T(D);
D=null
}return H
}function Z(){if(J){I=T(J);
J=null
}return I
}function O(a){D=D||F(H);
return S(D,a)
}function W(a){J=J||F(I);
return S(J,a)
}function B(b,a){D=M(D||F(H),b,a);
return K
}function G(b,a){J=M(J||F(I),b,a);
return K
}function V(){return[R,R!==""?":":"",N!==""?"//":"",N].join("")
}function P(){var b=Q();
var a=Z();
return[V(),C,b!==""?"?":"",b,a!==""?"#":"",a].join("")
}function F(h){var g=[];
var f=h.split("&");
for(var c=0,a=f.length;
c<a;
++c){var e=f[c].split("=");
var b=e.shift();
var d=null;
if(e.length>0){d=e.join("").replace(/\+/g," ")
}g.push([b,d!=null?L(d):null])
}return g
}function S(a,d){for(var c=0,b=a.length;
c<b;
++c){if(a[c][0]==d){return a[c][1]
}}return undefined
}function M(e,f,d){var h=f;
if(typeof f==="string"){h={};
h[f]=d
}for(var c in h){var g=false;
for(var b=0,a=e.length;
!g&&b<a;
++b){if(e[b][0]==c){e[b][1]=h[c];
g=true
}}if(!g){e.push([c,h[c]])
}}return e
}function E(b,a){b=b||"";
if(b[0]===a){b=b.substr(a.length)
}return b
}if(typeof Y==="object"&&typeof Y.toString==="function"){U(Y.toString())
}else{if(Y){U(Y)
}}K={getSchema:function(){return R
},getAuthority:function(){return N
},getOrigin:V,getPath:function(){return C
},getQuery:Q,getFragment:Z,getQP:O,getFP:W,setSchema:function(a){R=a;
return K
},setAuthority:function(a){N=a;
return K
},setPath:function(a){C=(a[0]==="/"?"":"/")+a;
return K
},setQuery:function(a){D=null;
H=E(a,"?");
return K
},setFragment:function(a){J=null;
I=E(a,"#");
return K
},setQP:B,setFP:G,setExistingP:function(a,b){if(O(a,b)!==undefined){B(a,b)
}if(W(a,b)!==undefined){G(a,b)
}return K
},toString:P};
return K
}
})();;
(function(){osapi._registerMethod=function(G,F){var A=typeof ___!=="undefined";
if(G=="newBatch"){return 
}var D=G.split(".");
var C=osapi;
for(var B=0;
B<D.length-1;
B++){C[D[B]]=C[D[B]]||{};
C=C[D[B]]
}var E=function(J){var I=osapi.newBatch();
var H={};
H.execute=function(M){var K=A?___.untame(M):M;
var L=A?___.USELESS:this;
I.add(G,this);
I.execute(function(N){if(N.error){K.call(L,N.error)
}else{K.call(L,N[G])
}})
};
if(A){___.markInnocent(H.execute,"execute")
}J=J||{};
J.userId=J.userId||"@viewer";
J.groupId=J.groupId||"@self";
H.method=G;
H.transport=F;
H.rpc=J;
return H
};
if(A&&typeof ___.markInnocent!=="undefined"){___.markInnocent(E,G)
}if(C[D[D.length-1]]){}else{C[D[D.length-1]]=E
}}
})();;
(function(){var A=function(){var C={};
var B=[];
var F=function(G,H){if(H&&G){B.push({key:G,request:H})
}return C
};
var E=function(H){var G={method:H.request.method,id:H.key};
if(H.request.rpc){G.params=H.request.rpc
}return G
};
var D=function(G){var H={};
var O={};
var J=0;
var K=[];
for(var M=0;
M<B.length;
M++){var I=B[M].request.transport;
if(!O[I.name]){K.push(I);
J++
}O[I.name]=O[I.name]||[];
O[I.name].push(E(B[M]))
}var N=function(S){if(S.error){H.error=S.error
}for(var R=0;
R<B.length;
R++){var Q=B[R].key;
var P=S[Q];
if(P){if(P.error){H[Q]=P
}else{H[Q]=P.data||P.result
}}}J--;
if(J===0){G(H)
}};
for(var L=0;
L<K.length;
L++){K[L].execute(O[K[L].name],N)
}if(J==0){window.setTimeout(function(){G(H)
},0)
}};
C.execute=D;
C.add=F;
return C
};
osapi.newBatch=A
})();;
(function(){function A(H,G){function F(J){if(J.errors[0]){G({error:{code:J.rc,message:J.text}})
}else{var K=J.result||J.data;
if(K.error){G(K)
}else{var I={};
for(var L=0;
L<K.length;
L++){I[K[L].id]=K[L]
}G(I)
}}}var E={POST_DATA:gadgets.json.stringify(H),CONTENT_TYPE:"JSON",METHOD:"POST",AUTHORIZATION:"SIGNED"};
var C=this.name;
var D=shindig.auth.getSecurityToken();
if(D){C+="?st=";
C+=encodeURIComponent(D)
}gadgets.io.makeNonProxiedRequest(C,F,E,"application/json")
}function B(F){var H=F["osapi.services"];
if(H){for(var E in H){if(H.hasOwnProperty(E)){if(E.indexOf("http")==0||E.indexOf("//")==0){var C=E.replace("%host%",document.location.host);
var I={name:C,execute:A};
var D=H[E];
for(var G=0;
G<D.length;
G++){osapi._registerMethod(D[G],I)
}}}}}}if(gadgets.config){gadgets.config.register("osapi.services",null,B)
}})();;
if(gadgets&&gadgets.rpc){(function(){function A(E,D){var C=function(G){if(!G){D({code:500,message:"Container refused the request"})
}else{if(G.error){D(G)
}else{var F={};
for(var H=0;
H<G.length;
H++){F[G[H].id]=G[H]
}D(F)
}}};
gadgets.rpc.call("..","osapi._handleGadgetRpcMethod",C,E)
}function B(C){var F={name:"gadgets.rpc",execute:A};
var K=C["osapi.services"];
if(K){for(var D in K){if(K.hasOwnProperty(D)){if(D==="gadgets.rpc"){var E=K[D];
for(var H=0;
H<E.length;
H++){osapi._registerMethod(E[H],F)
}}}}}if(osapi.container&&osapi.container.listMethods){var G=gadgets.util.runOnLoadHandlers;
var I=2;
var J=function(){I--;
if(I==0){G()
}};
gadgets.util.runOnLoadHandlers=J;
osapi.container.listMethods({}).execute(function(L){if(!L.error){for(var M=0;
M<L.length;
M++){if(L[M]!="container.listMethods"){osapi._registerMethod(L[M],F)
}}}J()
});
window.setTimeout(J,500)
}}if(gadgets.config&&gadgets.config.isGadget){gadgets.config.register("osapi.services",null,B)
}})()
};;
gadgets.util.registerOnLoadHandler(function(){if(osapi&&osapi.people&&osapi.people.get){osapi.people.getViewer=function(A){A=A||{};
A.userId="@viewer";
A.groupId="@self";
return osapi.people.get(A)
};
osapi.people.getViewerFriends=function(A){A=A||{};
A.userId="@viewer";
A.groupId="@friends";
return osapi.people.get(A)
};
osapi.people.getOwner=function(A){A=A||{};
A.userId="@owner";
A.groupId="@self";
return osapi.people.get(A)
};
osapi.people.getOwnerFriends=function(A){A=A||{};
A.userId="@owner";
A.groupId="@friends";
return osapi.people.get(A)
}
}});;
var tamings___=tamings___||[];
tamings___.push(function(A){___.tamesTo(osapi.newBatch,___.markFuncFreeze(function(){var C=osapi.newBatch();
___.markInnocent(C.add,"add");
___.markInnocent(C.execute,"execute");
return ___.tame(C)
}));
A.outers.osapi=___.tame(osapi);
___.grantRead(A.outers,"osapi");
var B=A;
gadgets.util.registerOnLoadHandler(function(){if(osapi&&osapi.people&&osapi.people.get){caja___.whitelistFuncs([[osapi.people,"getViewer"],[osapi.people,"getViewerFriends"],[osapi.people,"getOwner"],[osapi.people,"getOwnerFriends"]]);
B.outers.osapi.people.getViewer=___.tame(osapi.people.getViewer);
B.outers.osapi.people.getViewerFriends=___.tame(osapi.people.getViewerFriends);
B.outers.osapi.people.getOwner=___.tame(osapi.people.getOwner);
B.outers.osapi.people.getOwnerFriends=___.tame(osapi.people.getOwnerFriends)
}})
});;
shindig._uri=shindig.uri;
shindig.uri=(function(){var C=shindig._uri;
shindig._uri=null;
function A(E,D){return E.getOrigin()==D.getOrigin()
}function B(E,G){if(E.getSchema()==""){E.setSchema(G.getSchema())
}if(E.getAuthority()==""){E.setAuthority(G.getAuthority())
}var F=E.getPath();
if(F==""||F.charAt(0)!="/"){var H=G.getPath();
var D=H.lastIndexOf("/");
if(D!=-1){H=H.substring(0,D+1)
}E.setPath(G.getPath()+F)
}}return function(D){var E=C(D);
E.hasSameOrigin=function(F){return A(E,F)
};
E.resolve=function(F){return B(E,F)
};
return E
}
})();;
Function.prototype.inherits=function(A){function B(){}B.prototype=A.prototype;
this.superClass_=A.prototype;
this.prototype=new B();
this.prototype.constructor=this
};;
shindig.cookies={};
shindig.cookies.JsType_={UNDEFINED:"undefined"};
shindig.cookies.isDef=function(A){return typeof A!=shindig.cookies.JsType_.UNDEFINED
};
shindig.cookies.set=function(C,I,H,B,D){if(/;=/g.test(C)){throw new Error('Invalid cookie name "'+C+'"')
}if(/;/g.test(I)){throw new Error('Invalid cookie value "'+I+'"')
}if(!shindig.cookies.isDef(H)){H=-1
}var F=D?";domain="+D:"";
var J=B?";path="+B:"";
var E;
if(H<0){E=""
}else{if(H===0){var G=new Date(1970,1,1);
E=";expires="+G.toUTCString()
}else{var A=new Date((new Date).getTime()+H*1000);
E=";expires="+A.toUTCString()
}}document.cookie=C+"="+I+F+J+E
};
shindig.cookies.get=function(B,G){var F=B+"=";
var D=String(document.cookie);
for(var H=-1;
(H=D.indexOf(F,H+1))>=0;
){var C=H;
while(--C>=0){var E=D.charAt(C);
if(E==";"){C=-1;
break
}}if(C==-1){var A=D.indexOf(";",H);
if(A<0){A=D.length
}return D.substring(H+F.length,A)
}}return G
};
shindig.cookies.remove=function(B,A,C){var D=shindig.cookies.containsKey(B);
shindig.cookies.set(B,"",0,A,C);
return D
};
shindig.cookies.getKeyValues_=function(){var E=String(document.cookie);
var G=E.split(/\s*;\s*/);
var F=[],A=[],C,B;
for(var D=0;
B=G[D];
D++){C=B.indexOf("=");
if(C==-1){F.push("");
A.push(B)
}else{F.push(B.substring(0,C));
A.push(B.substring(C+1))
}}return{keys:F,values:A}
};
shindig.cookies.getKeys=function(){return shindig.cookies.getKeyValues_().keys
};
shindig.cookies.getValues=function(){return shindig.cookies.getKeyValues_().values
};
shindig.cookies.isEmpty=function(){return document.cookie===""
};
shindig.cookies.getCount=function(){var A=String(document.cookie);
if(A===""){return 0
}var B=A.split(/\s*;\s*/);
return B.length
};
shindig.cookies.containsKey=function(B){var A={};
return shindig.cookies.get(B,A)!==A
};
shindig.cookies.containsValue=function(C){var A=shindig.cookies.getKeyValues_().values;
for(var B=0;
B<A.length;
B++){if(A[B]==C){return true
}}return false
};
shindig.cookies.clear=function(){var B=shindig.cookies.getKeyValues_().keys;
for(var A=B.length-1;
A>=0;
A--){shindig.cookies.remove(B[A])
}};
shindig.cookies.MAX_COOKIE_LENGTH=3950;;
shindig.errors={};
shindig.errors.SUBCLASS_RESPONSIBILITY="subclass responsibility";
shindig.errors.TO_BE_DONE="to be done";
shindig.callAsyncAndJoin=function(E,A,D){var F=E.length;
var C=[];
for(var B=0;
B<E.length;
B++){var G=function(H){var I=E[H];
if(typeof I==="string"){I=D[I]
}I.call(D,function(J){C[H]=J;
if(--F===0){A(C)
}})
};
G(B)
}};
shindig.Extensible=function(){};
shindig.Extensible.prototype.setDependencies=function(A){for(var B in A){this[B]=A[B]
}};
shindig.Extensible.prototype.getDependencies=function(A){return this[A]
};
shindig.UserPrefStore=function(){};
shindig.UserPrefStore.prototype.getPrefs=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.UserPrefStore.prototype.savePrefs=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.DefaultUserPrefStore=function(){shindig.UserPrefStore.call(this)
};
shindig.DefaultUserPrefStore.inherits(shindig.UserPrefStore);
shindig.DefaultUserPrefStore.prototype.getPrefs=function(A){};
shindig.DefaultUserPrefStore.prototype.savePrefs=function(A){};
shindig.GadgetService=function(){};
shindig.GadgetService.prototype.setHeight=function(B,A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.GadgetService.prototype.setTitle=function(A,B){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.GadgetService.prototype.setUserPref=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.IfrGadgetService=function(){shindig.GadgetService.call(this);
gadgets.rpc.register("resize_iframe",this.setHeight);
gadgets.rpc.register("set_pref",this.setUserPref);
gadgets.rpc.register("set_title",this.setTitle);
gadgets.rpc.register("requestNavigateTo",this.requestNavigateTo);
gadgets.rpc.register("requestSendMessage",this.requestSendMessage)
};
shindig.IfrGadgetService.inherits(shindig.GadgetService);
shindig.IfrGadgetService.prototype.setHeight=function(A){if(A>shindig.container.maxheight_){A=shindig.container.maxheight_
}var B=document.getElementById(this.f);
if(B){B.style.height=A+"px"
}};
shindig.IfrGadgetService.prototype.setTitle=function(B){var A=document.getElementById(this.f+"_title");
if(A){A.innerHTML=B.replace(/&/g,"&amp;").replace(/</g,"&lt;")
}};
shindig.IfrGadgetService.prototype.setUserPref=function(G,B,D){var F=shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
var E=shindig.container.getGadget(F);
for(var C=1,A=arguments.length;
C<A;
C+=2){this.userPrefs[arguments[C]].value=arguments[C+1]
}E.saveUserPrefs()
};
shindig.IfrGadgetService.prototype.requestSendMessage=function(A,D,B,C){if(B){window.setTimeout(function(){B(new opensocial.ResponseItem(null,null,opensocial.ResponseItem.Error.NOT_IMPLEMENTED,null))
},0)
}};
shindig.IfrGadgetService.prototype.requestNavigateTo=function(A,D){var E=shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
var B=shindig.container.gadgetService.getUrlForView(A);
if(D){var C=gadgets.json.stringify(D);
if(C.length>0){B+="&appParams="+encodeURIComponent(C)
}}if(B&&document.location.href.indexOf(B)==-1){document.location.href=B
}};
shindig.IfrGadgetService.prototype.getUrlForView=function(A){if(A==="canvas"){return"/canvas"
}else{if(A==="profile"){return"/profile"
}else{return null
}}};
shindig.IfrGadgetService.prototype.getGadgetIdFromModuleId=function(A){return parseInt(A.match(/_([0-9]+)$/)[1],10)
};
shindig.LayoutManager=function(){};
shindig.LayoutManager.prototype.getGadgetChrome=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.StaticLayoutManager=function(){shindig.LayoutManager.call(this)
};
shindig.StaticLayoutManager.inherits(shindig.LayoutManager);
shindig.StaticLayoutManager.prototype.setGadgetChromeIds=function(A){this.gadgetChromeIds_=A
};
shindig.StaticLayoutManager.prototype.getGadgetChrome=function(B){var A=this.gadgetChromeIds_[B.id];
return A?document.getElementById(A):null
};
shindig.FloatLeftLayoutManager=function(A){shindig.LayoutManager.call(this);
this.layoutRootId_=A
};
shindig.FloatLeftLayoutManager.inherits(shindig.LayoutManager);
shindig.FloatLeftLayoutManager.prototype.getGadgetChrome=function(C){var B=document.getElementById(this.layoutRootId_);
if(B){var A=document.createElement("div");
A.className="gadgets-gadget-chrome";
A.style.cssFloat="left";
B.appendChild(A);
return A
}else{return null
}};
shindig.Gadget=function(B){this.userPrefs={};
if(B){for(var A in B){if(B.hasOwnProperty(A)){this[A]=B[A]
}}}if(!this.secureToken){this.secureToken="john.doe:john.doe:appid:cont:url:0:default"
}};
shindig.Gadget.prototype.getUserPrefs=function(){return this.userPrefs
};
shindig.Gadget.prototype.saveUserPrefs=function(){shindig.container.userPrefStore.savePrefs(this)
};
shindig.Gadget.prototype.getUserPrefValue=function(B){var A=this.userPrefs[B];
return typeof (A.value)!="undefined"&&A.value!=null?A.value:A["default"]
};
shindig.Gadget.prototype.render=function(A){if(A){var B=this;
this.getContent(function(C){A.innerHTML=C;
B.finishRender(A)
})
}};
shindig.Gadget.prototype.getContent=function(A){shindig.callAsyncAndJoin(["getTitleBarContent","getUserPrefsDialogContent","getMainContent"],function(B){A(B.join(""))
},this)
};
shindig.Gadget.prototype.getTitleBarContent=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.Gadget.prototype.getUserPrefsDialogContent=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.Gadget.prototype.getMainContent=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.Gadget.prototype.finishRender=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.Gadget.prototype.getAdditionalParams=function(){return""
};
shindig.BaseIfrGadget=function(A){shindig.Gadget.call(this,A);
this.serverBase_="/gadgets/";
this.queryIfrGadgetType_()
};
shindig.BaseIfrGadget.inherits(shindig.Gadget);
shindig.BaseIfrGadget.prototype.GADGET_IFRAME_PREFIX_="remote_iframe_";
shindig.BaseIfrGadget.prototype.CONTAINER="default";
shindig.BaseIfrGadget.prototype.cssClassGadget="gadgets-gadget";
shindig.BaseIfrGadget.prototype.cssClassTitleBar="gadgets-gadget-title-bar";
shindig.BaseIfrGadget.prototype.cssClassTitle="gadgets-gadget-title";
shindig.BaseIfrGadget.prototype.cssClassTitleButtonBar="gadgets-gadget-title-button-bar";
shindig.BaseIfrGadget.prototype.cssClassGadgetUserPrefsDialog="gadgets-gadget-user-prefs-dialog";
shindig.BaseIfrGadget.prototype.cssClassGadgetUserPrefsDialogActionBar="gadgets-gadget-user-prefs-dialog-action-bar";
shindig.BaseIfrGadget.prototype.cssClassTitleButton="gadgets-gadget-title-button";
shindig.BaseIfrGadget.prototype.cssClassGadgetContent="gadgets-gadget-content";
shindig.BaseIfrGadget.prototype.rpcToken=(2147483647*Math.random())|0;
shindig.BaseIfrGadget.prototype.rpcRelay="../container/rpc_relay.html";
shindig.BaseIfrGadget.prototype.getTitleBarContent=function(A){var B=this.hasViewablePrefs_()?'<a href="#" onclick="shindig.container.getGadget('+this.id+').handleOpenUserPrefsDialog();return false;" class="'+this.cssClassTitleButton+'">settings</a> ':"";
A('<div id="'+this.cssClassTitleBar+"-"+this.id+'" class="'+this.cssClassTitleBar+'"><span id="'+this.getIframeId()+'_title" class="'+this.cssClassTitle+'">'+(this.title?this.title:"Title")+'</span> | <span class="'+this.cssClassTitleButtonBar+'">'+B+'<a href="#" onclick="shindig.container.getGadget('+this.id+').handleToggle();return false;" class="'+this.cssClassTitleButton+'">toggle</a></span></div>')
};
shindig.BaseIfrGadget.prototype.getUserPrefsDialogContent=function(A){A('<div id="'+this.getUserPrefsDialogId()+'" class="'+this.cssClassGadgetUserPrefsDialog+'"></div>')
};
shindig.BaseIfrGadget.prototype.setServerBase=function(A){this.serverBase_=A
};
shindig.BaseIfrGadget.prototype.getServerBase=function(){return this.serverBase_
};
shindig.BaseIfrGadget.prototype.getMainContent=function(A){var B=this;
window.setTimeout(function(){B.getMainContent(A)
},0)
};
shindig.BaseIfrGadget.prototype.getIframeId=function(){return this.GADGET_IFRAME_PREFIX_+this.id
};
shindig.BaseIfrGadget.prototype.getUserPrefsDialogId=function(){return this.getIframeId()+"_userPrefsDialog"
};
shindig.BaseIfrGadget.prototype.getUserPrefsParams=function(){var B="";
for(var A in this.getUserPrefs()){B+="&up_"+encodeURIComponent(A)+"="+encodeURIComponent(this.getUserPrefValue(A))
}return B
};
shindig.BaseIfrGadget.prototype.handleToggle=function(){var B=document.getElementById(this.getIframeId());
if(B){var A=B.parentNode;
var C=A.style.display;
A.style.display=C?"":"none"
}};
shindig.BaseIfrGadget.prototype.hasViewablePrefs_=function(){for(var B in this.getUserPrefs()){var A=this.userPrefs[B];
if(A.type!="hidden"){return true
}}return false
};
shindig.BaseIfrGadget.prototype.handleOpenUserPrefsDialog=function(){if(this.userPrefsDialogContentLoaded){this.showUserPrefsDialog()
}else{var C=this;
var B="ig_callback_"+this.id;
window[B]=function(D){C.userPrefsDialogContentLoaded=true;
C.buildUserPrefsDialog(D);
C.showUserPrefsDialog()
};
var A=document.createElement("script");
A.src="http://www.gmodules.com/ig/gadgetsettings?mid="+this.id+"&output=js"+this.getUserPrefsParams()+"&url="+this.specUrl;
document.body.appendChild(A)
}};
shindig.BaseIfrGadget.prototype.buildUserPrefsDialog=function(A){var B=document.getElementById(this.getUserPrefsDialogId());
B.innerHTML=A+'<div class="'+this.cssClassGadgetUserPrefsDialogActionBar+'"><input type="button" value="Save" onclick="shindig.container.getGadget('+this.id+').handleSaveUserPrefs()"> <input type="button" value="Cancel" onclick="shindig.container.getGadget('+this.id+').handleCancelUserPrefs()"></div>';
B.childNodes[0].style.display=""
};
shindig.BaseIfrGadget.prototype.showUserPrefsDialog=function(A){var B=document.getElementById(this.getUserPrefsDialogId());
B.style.display=(A||A===undefined)?"":"none"
};
shindig.BaseIfrGadget.prototype.hideUserPrefsDialog=function(){this.showUserPrefsDialog(false)
};
shindig.BaseIfrGadget.prototype.handleSaveUserPrefs=function(){this.hideUserPrefsDialog();
var A=document.getElementById("m_"+this.id+"_numfields").value;
for(var D=0;
D<A;
D++){var B=document.getElementById("m_"+this.id+"_"+D);
var F="m_"+this.id+"_up_";
var C=B.name.substring(F.length);
var E=B.value;
this.userPrefs[C].value=E
}this.saveUserPrefs();
this.refresh()
};
shindig.BaseIfrGadget.prototype.handleCancelUserPrefs=function(){this.hideUserPrefsDialog()
};
shindig.BaseIfrGadget.prototype.refresh=function(){var A=this.getIframeId();
document.getElementById(A).src=this.getIframeUrl()
};
shindig.BaseIfrGadget.prototype.queryIfrGadgetType_=function(){var C={context:{country:"default",language:"default",view:"default",container:"default"},gadgets:[{url:this.specUrl,moduleId:1}]};
var B={CONTENT_TYPE:"JSON",METHOD:"POST",POST_DATA:gadgets.json.stringify(C)};
var A=this.serverBase_+"metadata?st="+this.secureToken;
gadgets.io.makeNonProxiedRequest(A,D,B,"application/javascript");
var E=this;
function D(J){var K=false;
var F=J.data.gadgets[0].features;
for(var H=0;
H<F.length;
H++){if(F[H]==="pubsub-2"){K=true;
break
}}var I=K?shindig.OAAIfrGadget:shindig.IfrGadget;
for(var G in I){if(I.hasOwnProperty(G)){E[G]=I[G]
}}}};
shindig.IfrGadget={getMainContent:function(A){var B=this.getIframeId();
gadgets.rpc.setRelayUrl(B,this.serverBase_+this.rpcRelay);
gadgets.rpc.setAuthToken(B,this.rpcToken);
A('<div class="'+this.cssClassGadgetContent+'"><iframe id="'+B+'" name="'+B+'" class="'+this.cssClassGadget+'" src="about:blank" frameborder="no" scrolling="no"'+(this.height?' height="'+this.height+'"':"")+(this.width?' width="'+this.width+'"':"")+"></iframe></div>")
},finishRender:function(A){window.frames[this.getIframeId()].location=this.getIframeUrl()
},getIframeUrl:function(){return this.serverBase_+"ifr?container="+this.CONTAINER+"&mid="+this.id+"&nocache="+shindig.container.nocache_+"&country="+shindig.container.country_+"&lang="+shindig.container.language_+"&view="+shindig.container.view_+(this.specVersion?"&v="+this.specVersion:"")+(shindig.container.parentUrl_?"&parent="+encodeURIComponent(shindig.container.parentUrl_):"")+(this.debug?"&debug=1":"")+this.getAdditionalParams()+this.getUserPrefsParams()+(this.secureToken?"&st="+this.secureToken:"")+"&url="+encodeURIComponent(this.specUrl)+"#rpctoken="+this.rpcToken+(this.viewParams?"&view-params="+encodeURIComponent(gadgets.json.stringify(this.viewParams)):"")+(this.hashData?"&"+this.hashData:"")
}};
shindig.OAAIfrGadget={getMainContent:function(A){A('<div id="'+this.cssClassGadgetContent+"-"+this.id+'" class="'+this.cssClassGadgetContent+'"></div>')
},finishRender:function(A){var B={className:this.cssClassGadget,frameborder:"no",scrolling:"no"};
if(this.height){B.height=this.height
}if(this.width){B.width=this.width
}new OpenAjax.hub.IframeContainer(gadgets.pubsub2router.hub,this.getIframeId(),{Container:{onSecurityAlert:function(D,C){gadgets.error("Security error for container "+D.getClientID()+" : "+C);
D.getIframe().src="about:blank"
}},IframeContainer:{parent:document.getElementById(this.cssClassGadgetContent+"-"+this.id),uri:this.getIframeUrl(),tunnelURI:shindig.uri(this.serverBase_+this.rpcRelay).resolve(shindig.uri(window.location.href)),iframeAttrs:B}})
},getIframeUrl:function(){return this.serverBase_+"ifr?container="+this.CONTAINER+"&mid="+this.id+"&nocache="+shindig.container.nocache_+"&country="+shindig.container.country_+"&lang="+shindig.container.language_+"&view="+shindig.container.view_+(this.specVersion?"&v="+this.specVersion:"")+(this.debug?"&debug=1":"")+this.getAdditionalParams()+this.getUserPrefsParams()+(this.secureToken?"&st="+this.secureToken:"")+"&url="+encodeURIComponent(this.specUrl)+(this.viewParams?"&view-params="+encodeURIComponent(gadgets.json.stringify(this.viewParams)):"")+(this.hashData?"#"+this.hashData:"")
}};
shindig.Container=function(){this.gadgets_={};
this.parentUrl_=document.location.href+"://"+document.location.host;
this.country_="ALL";
this.language_="ALL";
this.view_="default";
this.nocache_=1;
this.maxheight_=2147483647
};
shindig.Container.inherits(shindig.Extensible);
shindig.Container.prototype.gadgetClass=shindig.Gadget;
shindig.Container.prototype.userPrefStore=new shindig.DefaultUserPrefStore();
shindig.Container.prototype.gadgetService=new shindig.GadgetService();
shindig.Container.prototype.layoutManager=new shindig.StaticLayoutManager();
shindig.Container.prototype.setParentUrl=function(A){this.parentUrl_=A
};
shindig.Container.prototype.setCountry=function(A){this.country_=A
};
shindig.Container.prototype.setNoCache=function(A){this.nocache_=A
};
shindig.Container.prototype.setLanguage=function(A){this.language_=A
};
shindig.Container.prototype.setView=function(A){this.view_=A
};
shindig.Container.prototype.setMaxHeight=function(A){this.maxheight_=A
};
shindig.Container.prototype.getGadgetKey_=function(A){return"gadget_"+A
};
shindig.Container.prototype.getGadget=function(A){return this.gadgets_[this.getGadgetKey_(A)]
};
shindig.Container.prototype.createGadget=function(A){return new this.gadgetClass(A)
};
shindig.Container.prototype.addGadget=function(A){A.id=this.getNextGadgetInstanceId();
this.gadgets_[this.getGadgetKey_(A.id)]=A
};
shindig.Container.prototype.addGadgets=function(A){for(var B=0;
B<A.length;
B++){this.addGadget(A[B])
}};
shindig.Container.prototype.renderGadgets=function(){for(var A in this.gadgets_){this.renderGadget(this.gadgets_[A])
}};
shindig.Container.prototype.renderGadget=function(A){throw Error(shindig.errors.SUBCLASS_RESPONSIBILITY)
};
shindig.Container.prototype.nextGadgetInstanceId_=0;
shindig.Container.prototype.getNextGadgetInstanceId=function(){return this.nextGadgetInstanceId_++
};
shindig.Container.prototype.refreshGadgets=function(){for(var A in this.gadgets_){this.gadgets_[A].refresh()
}};
shindig.IfrContainer=function(){shindig.Container.call(this)
};
shindig.IfrContainer.inherits(shindig.Container);
shindig.IfrContainer.prototype.gadgetClass=shindig.BaseIfrGadget;
shindig.IfrContainer.prototype.gadgetService=new shindig.IfrGadgetService();
shindig.IfrContainer.prototype.setParentUrl=function(A){if(!A.match(/^http[s]?:\/\//)){A=document.location.href.match(/^[^?#]+\//)[0]+A
}this.parentUrl_=A
};
shindig.IfrContainer.prototype.renderGadget=function(B){var A=this.layoutManager.getGadgetChrome(B);
B.render(A)
};
shindig.container=new shindig.IfrContainer();;
if(gadgets&&gadgets.rpc){osapi._handleGadgetRpcMethod=function(A){var F=new Array(A.length);
var E=0;
var H=this.callback;
var B=function(K,J){J({})
};
for(var D=0;
D<A.length;
D++){var G=osapi;
if(A[D].method.indexOf("_")==-1){var I=A[D].method.split(".");
for(var C=0;
C<I.length;
C++){if(G.hasOwnProperty(I[C])){G=G[I[C]]
}else{G=B;
break
}}}else{G=B
}G(A[D].params,function(J){return function(K){F[J]={id:A[J].id,data:K};
E++;
if(E==A.length){H(F)
}}
}(D))
}};
osapi.container={};
osapi.container.listMethods=function(A,C){var B=[];
recurseNames(osapi,"",5,B);
C(B)
};
function recurseNames(C,D,E,B){if(E==0){return 
}for(var F in C){if(C.hasOwnProperty(F)){if(F.indexOf("_")==-1){var A=typeof (C[F]);
if(A=="function"){B.push(D+F)
}else{if(A=="object"){recurseNames(C[F],D+F+".",E-1,B)
}}}}}}gadgets.rpc.register("osapi._handleGadgetRpcMethod",osapi._handleGadgetRpcMethod)
};;
gadgets.config.init({"shindig.auth":{},"osapi":{"endPoints":["https://%host%/rpc"]},"osapi.services":{"gadgets.rpc":["container.listMethods"],"https://%host%/rpc":["activities.supportedFields","activities.update","gadgets.metadata","activities.delete","activities.get","appdata.update","http.put","http.post","gadgets.tokenSupportedFields","appdata.get","activities.create","system.listMethods","cache.invalidate","groups.get","people.supportedFields","http.get","http.head","appdata.delete","http.delete","aipo.version","gadgets.token","appdata.create","people.get","gadgets.supportedFields"]},"rpc":{"parentRelayUrl":"/gadgets/files/container/rpc_relay.html","useLegacyProtocol":false},"core.io":{"proxyUrl":"//%host%/gadgets/proxy?container=default&refresh=%refresh%&url=%url%%rewriteMime%","jsonProxyUrl":"//%host%/gadgets/makeRequest"}});