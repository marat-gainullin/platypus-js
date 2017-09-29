define([
    'ui/utils',
    'core/extend',
    './menu-element'], function (
        Ui,
        extend,
        MenuElement) {
    function MenuItem(text, image, onActionPerformed) {
        if (arguments.length < 2)
            image = null;
        if (arguments.length < 1)
            text = '';
        var iconTextGap = 4;

        MenuElement.call(this);
        var self = this;

        var clickReg = Ui.on(this.element, Ui.Events.CLICK, function () {
            self.fireActionPerformed();
            Ui.closeMenuSession();
        });

        this.onActionPerformed = onActionPerformed;

        var horizontalTextPosition = Ui.HorizontalPosition.RIGHT;

        var paragraph = document.createElement('p');
        paragraph.classList.add('p-paragraph');
        this.element.appendChild(paragraph);

        this.element.classList.add('p-menu-item');

        function applyPosition() {
            if (image) {
                image.style.marginLeft = image.style.marginRight = '';
            }
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
        }

        function applyText() {
            paragraph.innerText = text;
        }

        applyPosition();
        applyText();

        Object.defineProperty(this, "icon", {
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
                applyPosition();
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
    }
    extend(MenuItem, MenuElement);
    return MenuItem;
});