define([
    'core/extend',
    './rich-text-area/i18n',
    'ui/utils',
    'core/invoke',
    'ui/widget',
    'ui/events/value-change-event'], function (
        extend,
        i18n,
        Ui,
        Invoke,
        Widget,
        ValueChangeEvent) {
    function RichTextArea(shell) {
        var box = document.createElement('div');
        if (!shell)
            shell = box;

        box.classList.add('p-rich-text-area');
        shell.classList.add('p-scroll');
        shell.classList.add('p-vertical-scroll-filler');
        shell.classList.add('p-horizontal-scroll-filler');
        
        Widget.call(this, box, shell);
        var self = this;
        var wasValue = null;

        var valueChangeHandlers = new Set();
        function addValueChangeHandler(handler) {
            valueChangeHandlers.add(handler);
            return {
                removeHandler: function () {
                    valueChangeHandlers.delete(handler);
                }
            };
        }

        Object.defineProperty(this, 'addValueChangeHandler', {
            get: function () {
                return addValueChangeHandler;
            }
        });

        function fireValueChanged(oldValue) {
            wasValue = self.value;
            var event = new ValueChangeEvent(self, oldValue, self.value);
            valueChangeHandlers.forEach(function (h) {
                Invoke.later(function () {
                    h(event);
                });
            });
        }
        Object.defineProperty(this, 'fireValueChanged', {
            get: function () {
                return fireValueChanged;
            }
        });

        var tools = document.createElement('div');
        tools.classList.add('p-rich-text-tools');
        var content = document.createElement('div');
        tools.classList.add('p-rich-text-content');
        content.setAttribute("contenteditable", "true");
        box.appendChild(tools);
        box.appendChild(content);

        function on(target, event, handler) {
            Ui.on(target, event, function (e) {
                handler(e);
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            });
        }

        Ui.on(content, Ui.Events.BLUR, function (e) {
            if (wasValue !== self.value) {
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            }
        });

        var btnBold = document.createElement('button');
        on(btnBold, Ui.Events.CLICK, function () {
            document.execCommand('bold', null, null);
        });
        btnBold.className = 'p-rich-text-tool p-rich-text-bold';
        btnBold.title = i18n['rich.text.bold'];
        tools.appendChild(btnBold);
        var btnItalic = document.createElement('button');
        on(btnItalic, Ui.Events.CLICK, function () {
            document.execCommand('italic', null, null);
        });
        btnItalic.className = 'p-rich-text-tool p-rich-text-italic';
        btnItalic.title = i18n['rich.text.italic'];
        tools.appendChild(btnItalic);
        var btnUnderline = document.createElement('button');
        on(btnUnderline, Ui.Events.CLICK, function () {
            document.execCommand('underline', null, null);
        });
        btnUnderline.className = 'p-rich-text-tool p-rich-text-underline';
        btnUnderline.title = i18n['rich.text.underline'];
        tools.appendChild(btnItalic);
        var btnStrikeThrough = document.createElement('button');
        on(btnStrikeThrough, Ui.Events.CLICK, function () {
            document.execCommand('strikeThrough', null, null);
        });
        btnStrikeThrough.className = 'p-rich-text-tool p-rich-text-strike-through';
        btnStrikeThrough.title = i18n['rich.text.strike.through'];
        tools.appendChild(btnStrikeThrough);

        var btnSub = document.createElement('button');
        on(btnSub, Ui.Events.CLICK, function () {
            document.execCommand('subscript', null, null);
        });
        btnSub.className = 'p-rich-text-tool p-rich-text-subscript';
        btnSub.title = i18n['rich.text.subscript'];
        tools.appendChild(btnSub);
        var btnSup = document.createElement('button');
        on(btnSup, Ui.Events.CLICK, function () {
            document.execCommand('superscript', null, null);
        });
        btnSup.className = 'p-rich-text-tool p-rich-text-superscript';
        btnSup.title = i18n['rich.text.superscript'];
        tools.appendChild(btnSup);

        var tglAlignLeft = document.createElement('button');
        on(tglAlignLeft, Ui.Events.CLICK, function () {
            document.execCommand('justifyLeft', null, null);
        });
        tglAlignLeft.className = 'p-rich-text-tool p-rich-text-align-left';
        tglAlignLeft.title = i18n['rich.text.align.left'];
        tools.appendChild(tglAlignLeft);
        var tglAlignCenter = document.createElement('button');
        on(tglAlignCenter, Ui.Events.CLICK, function () {
            document.execCommand('justifyCenter', null, null);
        });
        tglAlignCenter.className = 'p-rich-text-tool p-rich-text-align-center';
        tglAlignCenter.title = i18n['rich.text.align.center'];
        tools.appendChild(tglAlignCenter);
        var tglAlignRight = document.createElement('button');
        on(tglAlignRight, Ui.Events.CLICK, function () {
            document.execCommand('justifyRight', null, null);
        });
        tglAlignRight.className = 'p-rich-text-tool p-rich-text-align-right';
        tglAlignRight.title = i18n['rich.text.align.right'];
        tools.appendChild(tglAlignRight);
        var tglAlignFull = document.createElement('button');
        on(tglAlignFull, Ui.Events.CLICK, function () {
            document.execCommand('justifyFull', null, null);
        });
        tglAlignFull.className = 'p-rich-text-tool p-rich-text-align-full';
        tglAlignFull.title = i18n['rich.text.align.full'];
        tools.appendChild(tglAlignFull);

        var btnCut = document.createElement('button');
        on(btnCut, Ui.Events.CLICK, function () {
            document.execCommand('cut', null, null);
        });
        btnCut.className = 'p-rich-text-tool p-rich-text-cut';
        btnCut.title = i18n['rich.text.cut'];
        tools.appendChild(btnCut);
        var btnCopy = document.createElement('button');
        on(btnCopy, Ui.Events.CLICK, function () {
            document.execCommand('copy', null, null);
        });
        btnCopy.className = 'p-rich-text-tool p-rich-text-copy';
        btnCopy.title = i18n['rich.text.copy'];
        tools.appendChild(btnCopy);
        var btnPaste = document.createElement('button');
        on(btnPaste, Ui.Events.CLICK, function () {
            document.execCommand('paste', null, null);
        });
        btnPaste.className = 'p-rich-text-tool p-rich-text-paste';
        btnPaste.title = i18n['rich.text.paste'];
        tools.appendChild(btnPaste);

        var btnUndo = document.createElement('button');
        on(btnUndo, Ui.Events.CLICK, function () {
            document.execCommand('undo', null, null);
        });
        btnUndo.className = 'p-rich-text-tool p-rich-text-undo';
        btnUndo.title = i18n['rich.text.undo'];
        tools.appendChild(btnUndo);
        var btnRedo = document.createElement('button');
        on(btnRedo, Ui.Events.CLICK, function () {
            document.execCommand('redo', null, null);
        });
        btnRedo.className = 'p-rich-text-tool p-rich-text-redo';
        btnRedo.title = i18n['rich.text.redo'];
        tools.appendChild(btnRedo);

        var btnIdentRight = document.createElement('button');
        on(btnIdentRight, Ui.Events.CLICK, function () {
            document.execCommand('indent', null, null);
        });
        btnIdentRight.className = 'p-rich-text-tool p-rich-text-indent-right';
        btnIdentRight.title = i18n['rich.text.indent.right'];
        tools.appendChild(btnIdentRight);
        var btnIdentLeft = document.createElement('button');
        on(btnIdentLeft, Ui.Events.CLICK, function () {
            document.execCommand('outdent', null, null);
        });
        btnIdentLeft.className = 'p-rich-text-tool p-rich-text-indent-left';
        btnIdentLeft.title = i18n['rich.text.indent.left'];
        tools.appendChild(btnIdentLeft);

        var btnHorizontalRule = document.createElement('button');
        on(btnHorizontalRule, Ui.Events.CLICK, function () {
            document.execCommand('insertHorizontalRule', null, null);
        });
        btnHorizontalRule.className = 'p-rich-text-tool p-rich-text-horizontal-rule';
        btnHorizontalRule.title = i18n['rich.text.horizontal.rule'];
        tools.appendChild(btnHorizontalRule);

        var btnOrderedList = document.createElement('button');
        on(btnOrderedList, Ui.Events.CLICK, function () {
            document.execCommand('insertOrderedList', null, null);
        });
        btnOrderedList.className = 'p-rich-text-tool p-rich-text-ordered-list';
        btnOrderedList.title = i18n['rich.text.ordered.list'];
        tools.appendChild(btnOrderedList);
        var btnUnorderedList = document.createElement('button');
        on(btnUnorderedList, Ui.Events.CLICK, function () {
            document.execCommand('insertUnorderedList', null, null);
        });
        btnUnorderedList.className = 'p-rich-text-tool p-rich-text-unordered-list';
        btnUnorderedList.title = i18n['rich.text.unordered.list'];
        tools.appendChild(btnUnorderedList);

        var btnImage = document.createElement('button');
        Ui.on(btnImage, Ui.Events.CLICK, function () {
            var url = prompt(i18n['rich.text.insert.image.prompt'], "http://");
            if (url) {
                document.execCommand('insertImage', null, url);
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            }
        });
        btnImage.className = 'p-rich-text-tool p-rich-text-insert-image';
        btnImage.title = i18n['rich.text.insert.image'];
        tools.appendChild(btnImage);
        var btnUploadImage = document.createElement('button');
        Ui.on(btnUploadImage, Ui.Events.CLICK, function () {
            Ui.selectFile(function (selectedFile) {
                upload(selectedFile, function (uploaded) {
                    document.execCommand('insertImage', null, uploaded[0]);
                    self.fireActionPerformed();
                    self.fireValueChanged(wasValue);
                });
            }, null);
        });
        btnUploadImage.className = 'p-rich-text-tool p-rich-text-upload-image';
        btnUploadImage.title = i18n['rich.text.upload.image'];
        tools.appendChild(btnUploadImage);

        var btnCreateLink = document.createElement('button');
        Ui.on(btnCreateLink, Ui.Events.CLICK, function () {
            var url = prompt(i18n['rich.text.create.link.prompt'], "http://");
            if (url) {
                document.execCommand('createLink', null, url);
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            }
        });
        btnCreateLink.className = 'p-rich-text-tool p-rich-text-create-link';
        btnCreateLink.title = i18n['rich.text.create.link'];
        tools.appendChild(btnCreateLink);
        var btnRemoveLink = document.createElement('button');
        on(btnRemoveLink, Ui.Events.CLICK, function () {
            document.execCommand('unlink', null, null);
        });
        btnRemoveLink.className = 'p-rich-text-tool p-rich-text-remove-link';
        btnRemoveLink.title = i18n['rich.text.remove.link'];
        tools.appendChild(btnRemoveLink);

        var btnClearFormat = document.createElement('button');
        on(btnClearFormat, Ui.Events.CLICK, function () {
            document.execCommand('removeFormat', null, null);
        });
        btnClearFormat.className = 'p-rich-text-tool p-rich-text-clear-format';
        btnClearFormat.title = i18n['rich.text.clear.format'];
        tools.appendChild(btnClearFormat);

        var btnBackground = document.createElement('button');
        Ui.on(btnBackground, Ui.Events.CLICK, function () {
            Ui.selectColor(function (selectedColor) {
                document.execCommand('backColor', null, selectedColor);
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            }, null);
        });
        btnBackground.className = 'p-rich-text-tool p-rich-text-background';
        btnBackground.title = i18n['rich.text.background'];
        tools.appendChild(btnBackground);
        var btnForeground = document.createElement('button');
        Ui.on(btnForeground, Ui.Events.CLICK, function () {
            Ui.selectColor(function (selectedColor) {
                document.execCommand('foreColor', null, selectedColor);
                self.fireActionPerformed();
                self.fireValueChanged(wasValue);
            }, null);
        });
        btnForeground.className = 'p-rich-text-tool p-rich-text-foreground';
        btnForeground.title = i18n['rich.text.foreground'];
        tools.appendChild(btnForeground);

        var fontNames = document.createElement('select');
        on(fontNames, Ui.Events.CHANGE, function () {
            document.execCommand('fontName', null, fontNames.options[fontNames.selectedIndex].value);
        });
        fontNames.className = 'p-rich-text-tool p-rich-text-font-name';
        fontNames.title = i18n['rich.text.font.name'];
        [
            'Times New Roman',
            'Arial',
            'Courier New',
            'Georgia',
            'Trebuchet',
            'Verdana'
        ].forEach(function (fontName) {
            var nameItem = document.createElement('option');
            nameItem.innerText = nameItem.value = fontName;
            fontNames.appendChild(nameItem);
        });
        tools.appendChild(fontNames);
        fontNames.selectedIndex = 0;

        var fontSizes = document.createElement('select');
        on(fontSizes, Ui.Events.CHANGE, function () {
            document.execCommand('fontSize', null, fontSizes.options[fontSizes.selectedIndex].value);
        });
        fontSizes.className = 'p-rich-text-tool p-rich-text-font-size';
        fontSizes.title = i18n['rich.text.font.size'];
        [
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7'
        ].forEach(function (fontSize) {
            var sizeItem = document.createElement('option');
            sizeItem.value = fontSize;
            sizeItem.innerText = i18n['rich.text.font.size.' + fontSize];
            fontSizes.appendChild(sizeItem);
        });
        tools.appendChild(fontSizes);
        fontSizes.selectedIndex = 2;

        Object.defineProperty(this, 'text', {
            get: function () {
                return content.innerText;
            },
            set: function (aValue) {
                var oldValue = self.value;
                content.innerText = aValue;
                self.fireValueChanged(oldValue);
            }
        });
        Object.defineProperty(this, 'value', {
            get: function () {
                var html = content.innerHTML;
                return html !== '' ? html : null;
            },
            set: function (aValue) {
                var oldValue = self.value;
                content.innerHTML = aValue;
                self.fireValueChanged(oldValue);
            }
        });
    }
    extend(RichTextArea, Widget);
    return RichTextArea;
});
