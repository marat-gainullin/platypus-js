define(function () {
    var Style = {
        NORMAL: 'normal',
        BOLD: 'bold',
        ITALIC: 'italic',
        BOLD_ITALIC: 'bold-italic'
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