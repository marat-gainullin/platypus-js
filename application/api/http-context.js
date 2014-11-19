(function() {
    /**
     * Http context of current http request.
     * @constructor
     */
    P.HttpContext = function() {
        var BinaryUtils = Java.type("com.eas.util.BinaryUtils");
        var JavaString = Java.type("java.lang.String");
        var HttpCookie = Java.type("javax.servlet.http.Cookie");
        var CharsetClass = Java.type("java.nio.charset.Charset");
        var ByteArray = Java.type("byte[]");
        var ScriptUtilsClass = Java.type('com.eas.script.ScriptUtils');

        Object.defineProperty(this, "request", {
            value: ScriptUtilsClass.getRequest() ? new Request(ScriptUtilsClass.getRequest()) : null
        });

        Object.defineProperty(this, "response", {
            value: ScriptUtilsClass.getResponse() ? new Response(ScriptUtilsClass.getResponse()) : null
        });

        function Request(aHttpRequest) {

            /**
             * The name of the protection authentication scheme.
             */
            this.authType = function() {
                return aHttpRequest.getAuthType();
            };

            /**
             * The name of the character encoding used in the body of this request.
             * <code>null</code> if the request does not specify a character encoding.
             */
            this.characterEncoding = function() {
                return aHttpRequest.getCharacterEncoding();
            };

            /**
             * The length, in bytes, of the request body and made available by the input stream, or -1 if the length is not known.
             */
            this.contentLength = function() {
                return aHttpRequest.getContentLength();
            };

            /**
             * The MIME type of the body of the request, or <code>null</code> if the type is not known.
             */
            this.contentType = function() {
                return aHttpRequest.getContentType();
            };

            /**
             * The request body.
             */
            this.body = function() {
                var encoding = aHttpRequest.getCharacterEncoding();
                if (!encoding || encoding.isEmpty()) {
                    P.Logger.warning("Missing character encoding. Falling back to utf-8.");
                    encoding = "utf-8";
                }
                if (CharsetClass.isSupported(encoding)) {
                    var is = aHttpRequest.getInputStream();
                    try {
                        return new JavaString(BinaryUtils.readStream(is, -1), encoding);
                    } finally {
                        is.close();
                    }
                } else {
                    throw "Character encoding " + encoding + " is not supported.";
                }
            };

            /**
             * The request body as a binary array.
             */
            this.bodyBuffer = function() {
                var is = aHttpRequest.getInputStream();
                try {
                    return BinaryUtils.readStream(is, -1);
                } finally {
                    is.close();
                }
            };

            /**
             * The portion of the request URI that indicates the context of the request.
             * The context path always comes first in a request URI. The path starts with a "/" character but does not end with a "/" character.
             * For the default (root) context, this method returns "".
             */
            this.contextPath = function() {
                return aHttpRequest.getContextPath();
            };

            /**
             * The cookies for the request.
             */
            var cookies = new Cookies(aHttpRequest);
            Object.defineProperty(this, "cookies", {
                value: cookies
            });

            /**
             * The headers object for the request (read only).
             * A header data is avaliable as a JavaScript property of this object.
             */
            var headers = new RequestHeaders(aHttpRequest);
            Object.defineProperty(this, "headers", {
                value: headers
            });

            /**
             * The request parameters.
             */
            var params = new Params(aHttpRequest);
            Object.defineProperty(this, "params", {
                value: params
            });

            /**
             * The Internet Protocol (IP) address of the interface on which the request was received.
             */
            this.localAddr = function() {
                return aHttpRequest.getLocalAddr();
            };

            /**
             * The host name of the Internet Protocol (IP) interface on which the request was received.
             */
            this.localName = function() {
                return aHttpRequest.getLocalName();
            };

            /**
             * The Internet Protocol (IP) port number of the interface on which the request was received.
             */
            this.localPort = function() {
                return aHttpRequest.getLocalPort();
            };

            /**
             * The name of the HTTP method with which this request was made, for example, GET, POST, or PUT.
             */
            this.method = function() {
                return aHttpRequest.getMethod();
            };

            /**
             * Any extra path information associated with the URL the client sent when it made this request.
             * The extra path information follows the servlet path but precedes the query string and will start with a "/" character.
             */
            this.pathInfo = function() {
                return aHttpRequest.getPathInfo();
            };

            /**
             * Any extra path information after the servlet name but before the query string, and translates it to a real path.
             */
            this.pathTranslated = function() {
                return aHttpRequest.getPathTranslated();
            };

            /**
             * The name and version of the protocol the request uses in the form protocol/majorVersion.minorVersion, for example, HTTP/1.1.
             */
            this.protocol = function() {
                return aHttpRequest.getProtocol();
            };

            /**
             * The query string that is contained in the request URL after the path.
             */
            this.queryString = function() {
                return aHttpRequest.getQueryString();
            };

            /**
             * The Internet Protocol (IP) address of the client or last proxy that sent the request.
             */
            this.remoteAddr = function() {
                return aHttpRequest.getRemoteAddr();
            };

            /**
             * The fully qualified name of the client or the last proxy that sent the request.
             * If the engine cannot or chooses not to resolve the hostname (to improve performance), this method returns the dotted-string form of the IP address.
             */
            this.remoteHost = function() {
                return aHttpRequest.getRemoteHost();
            };

            /**
             * The Internet Protocol (IP) source port of the client or last proxy that sent the request.
             */
            this.remotePort = function() {
                return aHttpRequest.getRemotePort();
            };

            /**
             * The part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
             * The web container does not decode this String.
             */
            this.requestURI = function() {
                return aHttpRequest.getRequestURI();
            };
            /**
             * Reconstructs the URL the client used to make the request.
             * The returned URL contains a protocol, server name, port nmber, and server path, but it does not include query string parameters.
             */
            this.requestURL = function() {
                return aHttpRequest.getRequestURL().toString();
            };
            /**
             * The name of the scheme used to make this request, for example, http, https, or ftp.
             */

            this.scheme = function() {
                return aHttpRequest.getScheme();
            };
            /**
             * The host name of the server to which the request was sent. It is the value of the part before ":".
             */
            this.serverName = function() {
                return aHttpRequest.getServerName();
            };
            /**
             * The port number to which the request was sent. It is the value of the part after ":".
             */
            this.serverPort = function() {
                return aHttpRequest.getServerPort();
            };
            /**
             * A boolean indicating whether this request was made using a secure channel, such as HTTPS.
             */
            this.secure = function() {
                return aHttpRequest.isSecure();
            };
        }

        function Cookies(aHttpRequest) {
            var self = this;
            var httpCookies = aHttpRequest.getCookies();
            if (httpCookies) {
                for (var i = 0; i < httpCookies.length; i++) {
                    var aCookie = httpCookies[i];
                    var cookie = new Cookie(aCookie);
                    Object.defineProperty(self, aCookie.getName(), {
                        value: cookie
                    });
                }
            }
        }

        function Cookie(aNativeCookie) {
            /**
             * The comment describing the purpose of this cookie, or <code>null</code> if the cookie has no comment.
             */
            Object.defineProperty(this, "comment", {
                get: function() {
                    return aNativeCookie.getComment();
                },
                set: function(aValue) {
                    aNativeCookie.setComment(aValue);
                }
            });

            /**
             * The domain name of this Cookie.
             */
            Object.defineProperty(this, "domain", {
                get: function() {
                    aNativeCookie.getDomain();
                },
                set: function(aValue) {
                    aNativeCookie.setDomain(aValue);
                }
            });

            /**
             * The maximum age in seconds for this Cookie.
             */
            Object.defineProperty(this, "maxAge", {
                get: function() {
                    return aNativeCookie.getMaxAge();
                },
                set: function(aValue) {
                    aNativeCookie.setMaxAge(aValue);
                }
            });

            /**
             * The name of the cookie.
             */
            Object.defineProperty(this, "name", {
                get : function() {
                    return aNativeCookie.getName();
                }
            });

            /**
             * The path on the server to which the browser returns this cookie. The cookie is visible to all subpaths on the server.
             */
            Object.defineProperty(this, "path", {
                get: function() {
                    return aNativeCookie.getPath();
                },
                set: function(aValue) {
                    aNativeCookie.setPath(aValue);
                }
            });

            /**
             * Indicates to the browser whether the cookie should only be sent using a secure protocol, such as HTTPS or SSL.
             */
            Object.defineProperty(this, "secure", {
                get: function() {
                    return aNativeCookie.getSecure();
                },
                set: function(aValue) {
                    aNativeCookie.setSecure(aValue);
                }
            });

            /**
             * The current value of this Cookie.
             */
            Object.defineProperty(this, "value", {
                get: function() {
                    return aNativeCookie.getValue();
                },
                set: function(aValue) {
                    aNativeCookie.setValue(aValue);
                }
            });

            /**
             * The version of the protocol this cookie complies with.
             */
            Object.defineProperty(this, "version", {
                get: function() {
                    return aNativeCookie.getVersion();
                },
                set: function(aValue) {
                    aNativeCookie.setVersion(aValue);
                }
            });
        }

        function Params(aHttpRequest) {
            var self = this;
            var paramNames = aHttpRequest.getParameterMap().keySet();
            if (paramNames) {
                for (var i = 0; i < paramNames.length; i++) {
                    var aParamName = paramNames[i];
                    var paramValues = aHttpRequest.getParameterValues(aParamName);
                    if (paramValues.length == 1) {
                        Object.defineProperty(self, aParamName, {
                            value: aHttpRequest.getParameter(aParamName)
                        });
                    } else {
                        Object.defineProperty(self, aParamName, {
                            value: P.boxAsJs(paramValues)
                        });
                    }
                }
            }
        }

        function RequestHeaders(aHttpRequest) {
            var self = this;
            var headerNames = aHttpRequest.getHeaderNames();
            if (headerNames) {
                for (var i = 0; i < headerNames.length; i++) {
                    var aHeaderName = headerNames[i];
                    Object.defineProperty(self, aHeaderName, {
                        value: aHttpRequest.getHeader(aHeaderName)
                    });
                }
            }
        }

        function Response(aHttpResponse) {
            var self = this;

            /**
             * The current status code of this response.
             */
            Object.defineProperty(this, "status", {
                get: function() {
                    return aHttpResponse.getStatus();
                },
                set: function(aValue) {
                    aHttpResponse.setStatus(aValue);
                }
            });

            /**
             * The content type used for the MIME body sent in this response.
             * Content type must be set prior to body or bodyBuffer.
             */
            Object.defineProperty(this, "contentType", {
                get: function() {
                    return aHttpResponse.getContentType();
                },
                set: function(aValue) {
                    aHttpResponse.setContentType(aValue);
                }
            });

            Object.defineProperty(this, "characterEncoding", {
                get: function() {
                    return aHttpResponse.getCharacterEncoding();
                },
                set: function(aValue) {
                    aHttpResponse.setCharacterEncoding(aValue);
                }
            });

            this.reset = function() {
                aHttpResponse.reset();
            };

            var body;
            var bodyBuffer;

            /**
             * The text body sent in this response. The body must be set after content type.
             * Note that Content-Length is set automatically.
             */
            Object.defineProperty(this, "body", {
                get: function() {
                    return body;
                },
                set: function(aValue) {
                    body = aValue;
                    var encoding = aHttpResponse.getCharacterEncoding();
                    if (!encoding || encoding.isEmpty()) {
                        P.Logger.warning("Missing character encoding. Falling back to utf-8.");
                        encoding = "utf-8";
                    }
                    if (CharsetClass.isSupported(encoding)) {
                        aHttpResponse.setCharacterEncoding(encoding);
                        self.bodyBuffer = aValue.getBytes(encoding);
                    } else {
                        throw "Character encoding " + encoding + " is not supported.";
                    }
                }
            });

            /**
             * The binary body sent in this response.
             * Note that Content-Length is set automatically.
             */
            Object.defineProperty(this, "bodyBuffer", {
                get: function() {
                    return bodyBuffer;
                },
                set: function(aValue) {
                    if (aValue instanceof ByteArray) {
                        bodyBuffer = aValue;
                        aHttpResponse.setContentLength(bodyBuffer.length);
                        aHttpResponse.resetBuffer();
                        if (bodyBuffer) {
                            var os = aHttpResponse.getOutputStream();
                            try {
                                os.write(bodyBuffer);
                                os.flush();
                            } finally {
                                os.close();
                            }
                        }
                    } else {
                        throw "Only byte[] can be setted as bodyBuffer.";
                    }
                }
            });
            
            /**
             * The response headers object.
             * A header data is avaliable as a JavaScript property of this object.
             */
            var headers = new ResponseHeaders(aHttpResponse);
            Object.defineProperty(this, "headers", {
                value: headers
            });

            /**
             * Adds a new cookie to the response.\n"
             * Use a key-value object with the following properties:
             * <code>name</code>, <code>value</code>, <code>comment</code>, <code>domain</code>, <code>maxAge</code>e, <code>path</code>, <code>secure</code>, <code>version</code>.
             * @param cookie the cookie object, for example <code>{name: 'platypus', value: 'test', maxAge: 60*60}</code>
             */
            this.addCookie = function(aValue) {
                var name = aValue.name;
                var value = aValue.value;
                if (name != null && value != null) {
                    var httpCookie = new HttpCookie(name, value);
                    var comment = aValue.comment;
                    if (comment != null) {
                        httpCookie.setComment(comment);
                    }
                    var domain = aValue.domain;
                    if (domain != null) {
                        httpCookie.setDomain(domain);
                    }
                    var maxAge = aValue.maxAge;
                    if (maxAge != null) {
                        var maxAgeInt = parseInt(maxAge);
                        if (maxAgeInt != null) {
                            httpCookie.setMaxAge(maxAgeInt);
                        }
                    }
                    var path = aValue.path;
                    if (path != null) {
                        httpCookie.setPath(path);
                    }
                    var secure = aValue.secure;
                    if (secure != null) {
                        httpCookie.setSecure(new Boolean(secure));
                    }
                    var version = aValue.version;
                    if (version != null) {
                        var versionInt = parseInt(version);
                        if (versionInt != null) {
                            httpCookie.setVersion(versionInt);
                        }
                    }
                    aHttpResponse.addCookie(httpCookie);
                }
            };
        }

        function ResponseHeaders(aHttpResponse) {
            var self = this;

            var headerNames = aHttpResponse.getHeaderNames();
            headerNames.forEach(function(aHeaderName) {
                Object.defineProperty(self, aHeaderName, {
                    get: function() {
                        return aHttpResponse.getHeader(aHeaderName);
                    },
                    set: function(aValue) {
                        aHttpResponse.setHeader(aHeaderName, aValue);
                    }
                });
            });

            /**
             * Adds the new header to the response.
             * @param aName the header name
             * @param aDefaultValue the header value
             */
            this.add = function(aName, aDefaultValue) {
                Object.defineProperty(self, aName, {
                    get: function() {
                        return aHttpResponse.getHeader(aName);
                    },
                    set: function(aValue) {
                        aHttpResponse.setHeader(aName, aValue);
                    }
                });
                aHttpResponse.addHeader(aName, aDefaultValue);
            };
        }
    };
})();