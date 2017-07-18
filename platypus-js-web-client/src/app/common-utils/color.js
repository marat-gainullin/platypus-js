define(function () {
    function Color(aRed, aGreen, aBlue, aAlpha) {
        var _red = 0, _green = 0, _blue = 0, _alpha = 0xff;
        if (arguments.length === 1) {
            var _color = parse(aRed + '');
            if (_color) {
                _red = _color.red;
                _green = _color.green;
                _blue = _color.blue;
            } else
                throw "String like '#cfcfcf' is expected.";
        } else if (arguments.length >= 3) {
            if (aRed)
                _red = aRed;
            if (aGreen)
                _green = aGreen;
            if (aBlue)
                _blue = aBlue;
            if (arguments.length > 3)
                _alpha = aAlpha;
        } else {
            throw "String like '#cfcfcf' or three color components with optional alpha is expected.";
        }
        var self = this;
        Object.defineProperty(self, "red", {get: function () {
                return _red;
            }});
        Object.defineProperty(self, "green", {get: function () {
                return _green;
            }});
        Object.defineProperty(self, "blue", {get: function () {
                return _blue;
            }});
        Object.defineProperty(self, "alpha", {get: function () {
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
    Color.LIGHT_GRAY = new Color(0xC0, 0xC0, 0xC0);
    Color.lightGray = new Color(0xC0, 0xC0, 0xC0);
    Color.magenta = new Color(0xff, 0, 0xff);
    Color.MAGENTA = new Color(0xff, 0, 0xff);
    Color.orange = new Color(0xff, 0xC8, 0);
    Color.ORANGE = new Color(0xff, 0xC8, 0);
    Color.pink = new Color(0xFF, 0xAF, 0xAF);
    Color.PINK = new Color(0xFF, 0xAF, 0xAF);
    Color.red = new Color(0xFF, 0, 0);
    Color.RED = new Color(0xFF, 0, 0);
    Color.white = new Color(0xFF, 0xff, 0xff);
    Color.WHITE = new Color(0xFF, 0xff, 0xff);
    Color.yellow = new Color(0xFF, 0xff, 0);
    Color.YELLOW = new Color(0xFF, 0xff, 0);
    return Color;
});