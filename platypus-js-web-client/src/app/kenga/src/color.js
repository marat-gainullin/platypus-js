define([
    './i18n'], function (
        i18n) {
    var rgbPattern = /^\s*rgb\s*\(\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*\)\s*$/;
    var rgbaPattern = /^\s*rgba\s*\(\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]*\.?[0-9]*)\s*\)\s*$/;

    function parse(colorText) {
        if (colorText) {
            if (colorText.startsWith("#")) {
                var colorText = colorText.substring(1);
                if (colorText.length === 3) {
                    return {
                        red: parseInt(colorText.substring(0, 1) + colorText.substring(0, 1), 16),
                        green: parseInt(colorText.substring(1, 2) + colorText.substring(1, 2), 16),
                        blue: parseInt(colorText.substring(2, 3) + colorText.substring(2, 3), 16),
                        alpha: 0xff
                    };
                } else if (colorText.length === 6) {
                    return {
                        red: parseInt(colorText.substring(0, 2), 16),
                        green: parseInt(colorText.substring(2, 4), 16),
                        blue: parseInt(colorText.substring(4, 6), 16),
                        alpha: 0xff
                    };
                } else {
                    throw i18n['not.a.color'];
                }
            } else {
                var m = colorText.match(rgbPattern);
                if (m) {
                    return {
                        red: parseInt(m[1]), // r
                        green: parseInt(m[2]), // g
                        blue: parseInt(m[3]), // b
                        alpha: 0xff // a
                    };
                } else {
                    var m1 = colorText.match(rgbaPattern);
                    if (m1) {
                        var alpha;
                        if (m1[4] === '.')
                            alpha = 0;
                        else
                            alpha = Math.floor(parseFloat(m1[4]) * 0xff);
                        return {
                            red: parseInt(m1[1]), // r
                            green: parseInt(m1[2]), // g
                            blue: parseInt(m1[3]), // b
                            alpha: alpha // a
                        };
                    } else {
                        throw i18n['not.a.color'];
                    }
                }
            }
        } else {
            return null;
        }
    }

    function Color(aRed, aGreen, aBlue, aAlpha) {
        var _red = 0, _green = 0, _blue = 0, _alpha = 0xff;
        if (arguments.length === 1) {
            var _color = parse(aRed + '');
            if (_color) {
                _red = _color.red;
                _green = _color.green;
                _blue = _color.blue;
                _alpha = _color.alpha;
            } else
                throw "String like '#cfcfcf' is expected.";
        } else if (arguments.length >= 3) {
            _red = aRed;
            _green = aGreen;
            _blue = aBlue;
            if (arguments.length > 3)
                _alpha = aAlpha;
        } else {
            throw "String like '#cfcfcf' or three color components with optional alpha is expected.";
        }
        var self = this;
        Object.defineProperty(self, "red", {
            get: function () {
                return _red;
            }});
        Object.defineProperty(self, "green", {
            get: function () {
                return _green;
            }});
        Object.defineProperty(self, "blue", {
            get: function () {
                return _blue;
            }});
        Object.defineProperty(self, "alpha", {
            get: function () {
                return _alpha;
            }});

        self.toStyled = function () {
            return "rgba(" + self.red + "," + self.green + "," + self.blue + "," + self.alpha / 255.0 + ")";
        };

        self.toString = function () {
            var sred = (new Number(self.red)).toString(16);
            if (sred.length === 1)
                sred = "0" + sred;
            var sgreen = (new Number(self.green)).toString(16);
            if (sgreen.length === 1)
                sgreen = "0" + sgreen;
            var sblue = (new Number(self.blue)).toString(16);
            if (sblue.length === 1)
                sblue = "0" + sblue;
            return "#" + sred + sgreen + sblue;
        };
    }
    ;
    Color.black = new Color(0, 0, 0);
    Color.BLACK = new Color(0, 0, 0);
    Color.blue = new Color(0, 0, 0xff);
    Color.BLUE = new Color(0, 0, 0xff);
    Color.cyan = new Color(0, 0xff, 0xff);
    Color.CYAN = new Color(0, 0xff, 0xff);
    Color.DARK_GRAY = new Color(0x40, 0x40, 0x40);
    Color.darkGray = new Color(0x40, 0x40, 0x40);
    Color.gray = new Color(0x80, 0x80, 0x80);
    Color.GRAY = new Color(0x80, 0x80, 0x80);
    Color.green = new Color(0, 0xff, 0);
    Color.GREEN = new Color(0, 0xff, 0);
    Color.LIGHT_GRAY = new Color(0xc0, 0xc0, 0xc0);
    Color.lightGray = new Color(0xc0, 0xc0, 0xc0);
    Color.magenta = new Color(0xff, 0, 0xff);
    Color.MAGENTA = new Color(0xff, 0, 0xff);
    Color.orange = new Color(0xff, 0xc8, 0);
    Color.ORANGE = new Color(0xff, 0xc8, 0);
    Color.pink = new Color(0xff, 0xaf, 0xaf);
    Color.PINK = new Color(0xff, 0xaf, 0xaf);
    Color.red = new Color(0xff, 0, 0);
    Color.RED = new Color(0xff, 0, 0);
    Color.white = new Color(0xff, 0xff, 0xff);
    Color.WHITE = new Color(0xff, 0xff, 0xff);
    Color.yellow = new Color(0xff, 0xff, 0);
    Color.YELLOW = new Color(0xff, 0xff, 0);
    Color.parse = parse;
    return Color;
});