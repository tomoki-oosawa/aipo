dojo._xdResourceLoaded({

defineResource: function(dojo){//This file is the command-line entry point for running the tests in
//Rhino and Spidermonkey.

load("dojo.js");
load("tests/runner.js");
tests.run();

}});