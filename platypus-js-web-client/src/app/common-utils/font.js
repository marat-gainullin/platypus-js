define(function () {
    var Style = {
        NORMAL: 0,
        BOLD: 1,
        ITALIC: 2,
        BOLD_ITALIC: 3
    };

    function Font(aFamily, aStyle, aSize) {
        var self = this;
        Object.defineProperty(self, "family", {
            get: function () {
                return aFamily;
            }
        });
        Object.defineProperty(self, "style", {
            get: function () {
                return aStyle;
            }
        });
        Object.defineProperty(self, "size", {
            get: function () {
                return aSize;
            }
        });
        Object.defineProperty(self, "bold", {
            get: function () {
                return aStyle === Style.BOLD || aStyle === Style.BOLD_ITALIC;
            }
        });
        Object.defineProperty(self, "italic", {
            get: function () {
                return Style === Style.ITALIC || aStyle === Style.BOLD_ITALIC;
            }
        });
    }
    Font.prototype.toString = function () {
        return this.family + ' ' + (this.style === Style.ITALIC ? 'Italic' : this.style === Style.BOLD ? 'Bold' : this.style === Style.BOLD_ITALIC ? 'Bold Italic' : 'Normal') + ' ' + this.size;
    };
    Object.defineProperty(Font, 'Style', {
        get: function () {
            return Style;
        }
    });
    return Font;
});