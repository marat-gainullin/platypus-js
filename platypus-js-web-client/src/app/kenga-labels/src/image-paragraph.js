define([
    'ui/utils',
    'core/extend',
    'ui/widget'], function (
        Ui,
        extend,
        Widget) {
    function ImageParagraph(aElement, text, image, iconTextGap) {
        if (arguments.length < 4)
            iconTextGap = 4;
        if (arguments.length < 3)
            icon = null;
        if (arguments.length < 2)
            text = '';

        Widget.call(this, aElement);

        var self = this;

        var horizontalTextPosition = Ui.HorizontalPosition.RIGHT;
        var verticalTextPosition = Ui.VerticalPosition.CENTER;

        var paragraph = document.createElement('p');
        paragraph.classList.add('p-paragraph');
        this.element.appendChild(paragraph);

        this.element.classList.add('p-image-paragraph');

        function applyPosition() {
            self.element.classList.remove('p-image-paragraph-column');
            self.element.classList.remove('p-image-paragraph-row');
            self.element.classList.remove('p-image-paragraph-row-top');
            self.element.classList.remove('p-image-paragraph-row-bottom');
            if (image) {
                image.style.marginLeft = '';
                image.style.marginRight = '';
                image.style.marginTop = '';
                image.style.marginBottom = '';
            }
            if (horizontalTextPosition === Ui.HorizontalPosition.CENTER) {
                self.element.classList.add('p-image-paragraph-column');
                if (verticalTextPosition === Ui.VerticalPosition.TOP) {
                    if (image) {
                        self.element.insertBefore(paragraph, image);
                        if (iconTextGap > 0 && text)
                            image.style.marginTop = iconTextGap + 'px';
                    }
                } else if (verticalTextPosition === Ui.VerticalPosition.BOTTOM || verticalTextPosition === Ui.VerticalPosition.CENTER) {
                    if (image) {
                        self.element.insertBefore(image, paragraph);
                        if (iconTextGap > 0 && text)
                            image.style.marginBottom = iconTextGap + 'px';
                    }
                }// else // value of 'verticalTextPosition' is unknown
            } else {
                self.element.classList.add('p-image-paragraph-row');
                if (horizontalTextPosition === Ui.HorizontalPosition.LEFT) {
                    if (image) {
                        self.element.insertBefore(paragraph, image);
                        if (iconTextGap > 0 && text)
                            image.style.marginLeft = iconTextGap + 'px';
                    }
                } else if (horizontalTextPosition === Ui.HorizontalPosition.RIGHT) {
                    if (image) {
                        self.element.insertBefore(image, paragraph);
                        if (iconTextGap > 0 && text)
                            image.style.marginRight = iconTextGap + 'px';
                    }
                } // else // value of 'horizontalTextPosition' is unknown
                if (verticalTextPosition === Ui.VerticalPosition.TOP) {
                    self.element.classList.add('p-image-paragraph-row-top');
                } else if (verticalTextPosition === Ui.VerticalPosition.BOTTOM) {
                    self.element.classList.add('p-image-paragraph-row-bottom');
                } // else // value of 'verticalTextPosition' is unknown
            }
        }

        function applyText(){
            paragraph.innerText = text;
        }

        applyPosition();
        applyText();

        Object.defineProperty(this, 'icon', {
            get: function () {
                return image;
            },
            set: function (aValue) {
                if (image !== aValue) {
                    if (image) {
                        image.classList.remove('p-image');
                        self.element.removeChild(image);
                    }
                    image = aValue;
                    if (image) {
                        self.element.appendChild(image);
                        image.classList.add('p-image');
                        applyPosition();
                    }
                }
            }
        });
        Object.defineProperty(this, "text", {
            get: function () {
                return text;
            },
            set: function (aValue) {
                if (text !== aValue) {
                    text = aValue;
                    applyText();
                }
            }
        });
        Object.defineProperty(this, "iconTextGap", {
            get: function () {
                return iconTextGap;
            },
            set: function (aValue) {
                iconTextGap = aValue;
            }
        });
        /**
         * Horizontal position of the text relative to the icon.
         */
        Object.defineProperty(this, "horizontalTextPosition", {
            get: function () {
                return horizontalTextPosition;
            },
            set: function (aValue) {
                if (horizontalTextPosition !== aValue) {
                    horizontalTextPosition = aValue;
                    applyPosition();
                }
            }
        });

        /**
         * Vertical position of the text relative to the icon.
         */
        Object.defineProperty(this, "verticalTextPosition", {
            get: function () {
                return verticalTextPosition;
            },
            set: function (aValue) {
                if (verticalTextPosition !== aValue) {
                    verticalTextPosition = aValue;
                    applyPosition();
                }
            }
        });
    }
    extend(ImageParagraph, Widget);
    return ImageParagraph;
});