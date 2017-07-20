define([
    '../extend',
    './has-scroll',
    './container',
    './box-pane',
    './horizontal-scroll-filler',
    './vertical-scroll-filler',
    '../ui'], function (
        extend,
        HasScroll,
        Container,
        Box,
        HorizontalScrollFiller,
        VerticalScrollFiller,
        Ui) {

    function Scroll(view) {
        Container.call(this);

        var self = this;
        /**
         * Used to set the horizontal scroll bar policy so that horizontal
         * scrollbars are displayed only when needed.
         */
        var SCROLLBAR_AS_NEEDED = 30;
        /**
         * Used to set the horizontal scroll bar policy so that horizontal
         * scrollbars are never displayed.
         */
        var SCROLLBAR_NEVER = 31;
        /**
         * Used to set the horizontal scroll bar policy so that horizontal
         * scrollbars are always displayed.
         */
        var SCROLLBAR_ALWAYS = 32;

        var verticalScrollBarPolicy = SCROLLBAR_AS_NEEDED;
        var horizontalScrollBarPolicy = SCROLLBAR_AS_NEEDED;

        Object.defineProperty(this, 'horizontalScrollBarPolicy', {
            get: function () {
                return verticalScrollBarPolicy;
            },
            set: function (aValue) {
                verticalScrollBarPolicy = aValue;
                applyHorizontalScrollBarPolicy();
            }
        });

        Object.defineProperty(this, 'verticalScrollBarPolicy', {
            get: function () {
                return horizontalScrollBarPolicy;
            },
            set: function (aValue) {
                horizontalScrollBarPolicy = aValue;
                applyVerticalScrollBarPolicy();
            }
        });

        function isHorizontalScrollFiller(aWidget) {
            return aWidget instanceof HorizontalScrollFiller
                    || (aWidget instanceof Box && aWidget.orientation === Ui.Orientation.VERTICAL);
        }

        function isVerticalScrollFiller(aWidget) {
            return aWidget instanceof VerticalScrollFiller
                    || (aWidget instanceof Box && aWidget.orientation === Ui.Orientation.HORIZONTAL);
        }

        function ajustWidth(w, aWidth) {
            if (!isHorizontalScrollFiller(w)) {
                w.element.style.width = aWidth + 'px';
            }
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustHeight(w, aHeight) {
            if (!isVerticalScrollFiller(w)) {
                w.element.style.height = aHeight + 'px';
            }
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });

        var superAdd = this.add;
        var superRemove = this.remove;
        function setView(w) {
            if (isHorizontalScrollFiller(w)) {
                w.element.style.width = 100 + '%';
            }
            if (isVerticalScrollFiller(w)) {
                w.element.style.height = 100 + '%';
            }
            var old = view;
            if (old) {
                superRemove(old);
            }
            view = w;
            superAdd(w);
        }

        if (view) {
            setView(view);
        }

        function applyHorizontalScrollBarPolicy() {
            var value = 'auto';
            if (horizontalScrollBarPolicy === SCROLLBAR_ALWAYS) {
                value = 'scroll';
            } else if (horizontalScrollBarPolicy === SCROLLBAR_NEVER) {
                value = 'hidden';
            }
            if (view instanceof HasScroll) {
                value = 'hidden';
            }
            self.element.style.overflowX = value;
        }

        function applyVerticalScrollBarPolicy() {
            var value = 'auto';
            if (verticalScrollBarPolicy === SCROLLBAR_ALWAYS) {
                value = 'scroll';
            } else if (verticalScrollBarPolicy === SCROLLBAR_NEVER) {
                value = 'hidden';
            }
            if (view instanceof HasScroll) {
                value = 'hidden';
            }
            self.element.style.overflowY = value;
        }

        function add(w) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget already added to this container';
                self.clear();
                setView(w);
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            if (removed === view) {
                view = null;
            }
            return removed;
        }
        Object.defineProperty(this, "remove", {
            get: function () {
                return remove;
            }
        });

        Object.defineProperty(this, "view", {
            get: function () {
                return view;
            },
            set: function (aValue) {
                if (view !== aValue) {
                    if (aValue)
                        setView(aValue);
                    else
                        self.clear();
                }
            }
        });

        function getTop(aWidget) {
            if (aWidget.parent !== this)
                throw "Widget should be a child of this container";
            return 0;
        }
        Object.defineProperty(this, "getTop", {
            get: function () {
                return getTop;
            }
        });

        function getLeft(aWidget) {
            if (aWidget.parent !== this)
                throw "Widget should be a child of this container";
            return 0;
        }
        Object.defineProperty(this, "getLeft", {
            get: function () {
                return getLeft;
            }
        });
    }
    extend(Scroll, Container);
    return Scroll;
});