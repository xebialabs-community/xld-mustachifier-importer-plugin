# Mustachifier Importer plugin #

# Overview #

The mustachifier importer plugins translates any placeholders into a
mustache placeholder managed by XLD during the import.

# Requirements #

* **Deployit requirements**
	* **Deployit**: version 4.5.+

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.

# Usage #

A `smoketest.Runner` CI is a container from which the test will be performed.

3 Deployables are provided that will be deployed onto a `smoketest.Runner`

* `smoketest.HttpRequestTest` for a HTTP request using the GET verb
* `smoketest.HttpPostRequestTest` for a HTTP request using the POST verb
* `smoketest.HttpPostRequestFileTest` for a HTTP request using the POST verb and a file that contains the post data.


# Note #

The configuraiton should be put the mustachier.properties, copied in the
conf/ directory.


# TODO #
* Allow to put describe the configuration in the manifest file
* Allow to put describe the configuration in the XLD repository

