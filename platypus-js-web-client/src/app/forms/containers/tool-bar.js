define([
    '../../ui',
    '../../invoke',
    '../../extend',
    '../box-pane'], function (
        Ui,
        Invoke,
        extend,
        Box) {

    var SCROLL_PORTION = 20;
    var AUTO_SCROLL_DELAY = 300;

    function Toolbar(hgap) {
        if (arguments.length < 1)
            hgap = 0;

        Box.call(this, Ui.Orientation.HORIZONTAL, hgap, 0);

        var self = this;

        delete this.orientation;
        delete this.vgap;

        this.element.classList.add('p-toolbar');
        this.element.classList.add('p-btn-group');

        var leftChevron = document.createElement('div');
        leftChevron.classList.add('p-toolbar-chevron');
        leftChevron.classList.add('p-toolbar-chevron-left');
        var rightChevron = document.createElement('div');
        rightChevron.classList.add('p-toolbar-chevron');
        rightChevron.classList.add('p-toolbar-chevron-right');

        this.addAddHandler(function () {
            checkChevrons();
        });
        this.addRemoveHandler(function () {
            checkChevrons();
        });

        function checkChevrons() {
            if (self.count > 0) {
                var lastTool = self.child(self.count - 1);
                if (self.element.scrollLeft > 0) {
                    if (parseFloat(leftChevron.style.left) !== self.element.scrollLeft)
                        leftChevron.style.left = self.element.scrollLeft + 'px';
                    if (!leftChevron.parentElement) {
                        self.element.appendChild(leftChevron);
                    }
                } else {
                    scheduledLeft = null;
                    if (leftChevron.parentElement) {
                        leftChevron.parentElement.removeChild(leftChevron);
                    }
                }
                if (lastTool.element.offsetLeft + lastTool.element.offsetWidth - self.element.scrollLeft > self.element.offsetWidth) {
                    if (parseFloat(rightChevron.style.right) !== -self.element.scrollLeft)
                        rightChevron.style.right = -self.element.scrollLeft + 'px';
                    if (!rightChevron.parentElement) {
                        self.element.appendChild(rightChevron);
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
            self.element.scrollLeft -= SCROLL_PORTION;
            checkChevrons();
        }

        function moveLeft() {
            self.element.scrollLeft += SCROLL_PORTION;
            checkChevrons();
        }

        Ui.on(this.element, Ui.Events.MOUSEOVER, function () {
            checkChevrons();
        });
        Ui.on(this.element, Ui.Events.SCROLL, function (event) {
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
    extend(Toolbar, Box);
    return Toolbar;
});