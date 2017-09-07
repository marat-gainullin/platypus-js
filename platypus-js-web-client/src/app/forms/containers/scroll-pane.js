define([
    '../../ui',
    '../../extend',
    '../container'
    ], function (
        Ui,
        extend,
        Container
        ) {

    function Scroll(view) {
        Container.call(this);

        var self = this;

        this.element.classList.add('p-scroll');
        this.element.classList.add('p-vertical-scroll-filler');
        this.element.classList.add('p-horizontal-scroll-filler');
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

        function isHorizontalScrollFiller(w) {
            return w.element.className.indexOf('p-horizontal-scroll-filler') > -1;
        }

        function isVerticalScrollFiller(w) {
            return w.element.className.indexOf('p-vertical-scroll-filler') > -1;
        }

        function ajustWidth(w, aValue) {
            if (!isHorizontalScrollFiller(w)) {
                w.element.style.width = aValue + 'px';
            }
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustHeight(w, aValue) {
            if (!isVerticalScrollFiller(w)) {
                w.element.style.height = aValue + 'px';
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
            if (view.element.className.indexOf('p-scroll') !== -1) {
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
            if (view.element.className.indexOf('p-scroll') !== -1) {
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

        function ajustTop(w) {
        }
        Object.defineProperty(this, "ajustTop", {
            get: function () {
                return ajustTop;
            }
        });

        function ajustLeft(w) {
        }
        Object.defineProperty(this, "ajustLeft", {
            get: function () {
                return ajustLeft;
            }
        });

    }
    extend(Scroll, Container);
    return Scroll;
});