define([
    '../../invoke',
    '../../ui',
    '../../extend',
    './card-pane'], function (
        Invoke,
        Ui,
        extend,
        CardPane) {

    var SCROLL_PORTION = 20;
    var AUTO_SCROLL_DELAY = 300;

    function TabbedPane() {
        var shell = document.createElement('div');
        shell.className = 'p-tabs';
        var content = document.createElement('div');
        content.className = 'p-tabs-content';
        CardPane.call(this, 0, 0, shell, content);

        var self = this;

        var captionsShell = document.createElement('div');
        captionsShell.className = 'p-tabs-captions-shell';
        var captions = document.createElement('div');
        captions.className = 'p-tabs-captions';
        var tabsOf = new Map();

        captionsShell.appendChild(captions);
        shell.appendChild(captionsShell);
        shell.appendChild(content);

        function showCaption(toShow) {
            var caption = captions.firstElementChild;
            while (caption) {
                if (caption === toShow) {
                    caption.classList.add('p-tab-caption-selected');
                } else {
                    caption.classList.remove('p-tab-caption-selected');
                }
                caption = caption.nextElementSibling;
            }
        }

        function addCaption(w, title, image, toolTip, beforeIndex) {
            if (!title) {
                title = w.name ? w.name : "Unnamed - " + captions.childElementCount;
            }
            var caption = document.createElement('div');
            caption.className = 'p-tab-caption';
            Ui.on(caption, Ui.Events.CLICK, function (event) {
                event.stopPropagation();
                self.show(w);
            });
            var labelText = document.createElement('div');
            labelText.className = 'p-tab-caption-text';
            labelText.innerText = title;
            var closeTool = document.createElement('div');
            closeTool.className = 'p-tab-caption-close-tool';
            Ui.on(closeTool, Ui.Events.CLICK, function (event) {
                event.stopPropagation();
                self.remove(w);
            });
            if (toolTip)
                caption.title = toolTip;
            if (image) {
                image.classList.add('p-tab-caption-image');
                caption.appendChild(image);
            }
            caption.appendChild(labelText);
            caption.appendChild(closeTool);

            if (isNaN(beforeIndex)) {
                captions.appendChild(caption);
            } else {
                if (beforeIndex < captions.childElementCount) {
                    captions.insertBefore(caption, captions.children[beforeIndex]);
                } else {
                    captions.appendChild(caption);
                }
            }
            tabsOf.set(w, caption);
        }

        // TODO: Add <html> prefix in tab title feature 
        var superAdd = this.add;
        function add(w, title, image, tooltip, beforeIndex) {
            superAdd(w, beforeIndex);
            addCaption(w, arguments.length < 2 ? null : title, arguments.length < 3 ? null : image, arguments.length < 4 ? '' : tooltip, beforeIndex);
            checkChevrons();
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        var superRemove = this.remove;
        function remove(widgetOrIndex) {
            var removed = superRemove(widgetOrIndex);
            if (removed) {
                captions.removeChild(tabsOf.get(removed));
                tabsOf.delete(removed);
            }
            checkChevrons();
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });
        var superClear = this.clear;
        function clear() {
            superClear();
            while (captions.firstElementChild)
                captions.removeChild(captions.firstElementChild);
            tabsOf.clear();
            checkChevrons();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        this.addSelectionHandler(function (evt) {
            showCaption(tabsOf.get(evt.item));
        });

        var leftChevron = document.createElement('div');
        leftChevron.classList.add('p-tabs-chevron');
        leftChevron.classList.add('p-tabs-chevron-left');
        var rightChevron = document.createElement('div');
        rightChevron.classList.add('p-tabs-chevron');
        rightChevron.classList.add('p-tabs-chevron-right');

        function checkChevrons() {
            if (self.count > 0) {
                var lastCaption = captions.lastElementChild;
                if (captions.scrollLeft > 0) {
                    //if (parseFloat(leftChevron.style.left) !== captions.scrollLeft)
                    //    leftChevron.style.left = captions.scrollLeft + 'px';
                    if (!leftChevron.parentElement) {
                        //leftChevron.style.marginLeft = '0px';
                        captionsShell.appendChild(leftChevron);
                    }
                } else {
                    scheduledLeft = null;
                    if (leftChevron.parentElement) {
                        leftChevron.parentElement.removeChild(leftChevron);
                    }
                }
                if (lastCaption.offsetLeft + lastCaption.offsetWidth - captions.scrollLeft > captions.offsetWidth) {
                    //if (parseFloat(rightChevron.style.right) !== -captions.scrollLeft)
                    //    rightChevron.style.right = -captions.scrollLeft + 'px';
                    if (!rightChevron.parentElement) {
                        captionsShell.appendChild(rightChevron);
                    }
                } else {
                    scheduledRight = null;
                    if (rightChevron.parentElement) {
                        rightChevron.parentElement.removeChild(rightChevron);
                    }
                }
            } else {
                scheduledLeft = null;
                scheduledRight = null;
                if (leftChevron.parentElement)
                    leftChevron.parentElement.removeChild(leftChevron);
                if (rightChevron.parentElement)
                    rightChevron.parentElement.removeChild(rightChevron);
            }
        }

        function moveRight() {
            captions.scrollLeft -= SCROLL_PORTION;
            checkChevrons();
        }

        function moveLeft() {
            captions.scrollLeft += SCROLL_PORTION;
            checkChevrons();
        }

        Ui.on(this.element, Ui.Events.MOUSEOVER, function (event) {
            checkChevrons();
        });
        Ui.on(captionsShell, Ui.Events.SCROLL, function (event) {
            checkChevrons();
        });

        var scheduledLeft = null;
        Ui.on(leftChevron, Ui.Events.MOUSEDOWN, function (event) {
            function schedule() {
                Invoke.delayed(AUTO_SCROLL_DELAY, function () {
                    if (scheduledLeft === schedule) {
                        schedule();
                        moveRight();
                    }
                });
            }
            scheduledLeft = schedule;
            schedule();
        });
        Ui.on(leftChevron, Ui.Events.MOUSEUP, function (event) {
            scheduledLeft = null;
            moveRight();
        });
        var scheduledRight = null;
        Ui.on(rightChevron, Ui.Events.MOUSEDOWN, function (event) {
            function schedule() {
                Invoke.delayed(AUTO_SCROLL_DELAY, function () {
                    if (scheduledRight === schedule) {
                        schedule();
                        moveLeft();
                    }
                });
            }
            scheduledRight = schedule;
            schedule();
        });
        Ui.on(rightChevron, Ui.Events.MOUSEUP, function (event) {
            scheduledRight = null;
            moveLeft();
        });
    }
    extend(TabbedPane, CardPane);
    return TabbedPane;
});