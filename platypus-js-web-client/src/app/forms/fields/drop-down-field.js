define([
    '../../invoke',
    '../../logger',
    '../../extend',
    '../i18n',
    '../events/item-event',
    './box-field'], function (
        Invoke,
        Logger,
        extend,
        i18n,
        SelectionEvent,
        BoxField) {
    function DropDownField(shell) {
        var box = document.createElement('select');
        if (!shell) {
            shell = box;
        }
        BoxField.call(this, box, shell);

        var self = this;
        var value = null;
        var nullItem = document.createElement('option');
        nullItem.innerText = '< . >';
        nullItem.className = 'p-indeterminate';
        nullItem.value = '';
        nullItem['js-value'] = null;
        box.appendChild(nullItem);

        function itemChanged() {
            var oldValue = value;
            var selectedItem;
            if (box.selectedIndex === -1) {
                value = null;
                selectedItem = null;
            } else {
                selectedItem = itemAt(box.selectedIndex);
                if (selectedItem)
                    value = selectedItem['js-value'];
                else
                    value = null;
            }
            fireSelected(selectedItem);
            self.fireValueChanged(oldValue);
        }
        
        var selectionHandlers = new Set();

        function addSelectionHandler(handler) {
            selectionHandlers.add(handler);
            return {
                removeHandler: function () {
                    selectionHandlers.delete(handler);
                }
            };
        }

        Object.defineProperty(this, 'addSelectionHandler', {
            get: function () {
                return addSelectionHandler;
            }
        });
        
        function fireSelected(aItem) {
            var event = new SelectionEvent(self, aItem);
            selectionHandlers.forEach(function (h) {
                Invoke.later(function(){
                    h(event);
                });
            });
        }

        Object.defineProperty(this, 'textChanged', {
            enumerable: false,
            get: function () {
                return itemChanged;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                if (box.selectedIndex === -1) {
                    return '';
                } else {
                    var item = itemAt(box.selectedIndex);
                    if (item)
                        return item.innerText;
                    else
                        return '';
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return value;
            },
            set: function (aValue) {
                if (value !== aValue) {
                    if (aValue !== null) {
                        var index = indexOf(aValue);
                        if (index !== -1) {
                            box.selecteIndex = index;
                        } else {
                            box.selecteIndex = indexOf(null);
                        }
                    } else {
                        box.selecteIndex = indexOf(null);
                    }
                    itemChanged();
                }
            }
        });

        Object.defineProperty(this, 'selectedIndex', {
            get: function () {
                return box.selectedIndex;
            },
            set: function (index) {
                if (index >= 0 && index < getCount()) {
                    box.selectedIndex = index;
                } else {
                    box.selectedIndex = -1;
                }
                itemChanged();
            }
        });

        Object.defineProperty(this, 'count', {
            get: function () {
                return box.options.length;
            }
        });

        Object.defineProperty(this, 'visibleItemCount', {
            get: function () {
                return box.size;
            },
            set: function (aValue) {
                box.size = aValue;
            }
        });

        function itemAt(index) {
            if (index >= 0 && index < box.options.length) {
                return box.options[index];
            } else {
                return null;
            }
        }

        Object.defineProperty(this, "emptyText", {
            configurable: true,
            get: function () {
                return nullItem.innerText;
            },
            set: function (aValue) {
                nullItem.innerText = aValue ? aValue : '< . >';
            }
        });

        function addValue(aLabel, aValue) {
            if (aValue !== null) {
                var index = indexOf(aValue);
                if (index !== -1) {
                    var item = itemAt(index);
                    item.innerText = aLabel;
                } else {
                    addItem(box.options.length, aLabel, aValue);
                }
            }
        }

        Object.defineProperty(this, 'addValue', {
            get: function () {
                return;
            }
        });

        function removeValue(aValue) {
            var index = indexOf(aValue);
            var removed = removeItem(index);
            return removed ? true : false;
        }

        Object.defineProperty(this, 'removeValue', {
            get: function () {
                return;
            }
        });

        function clear() {
            for (var i = box.options.length; i >= 0; i--) {
                removeItem(i);
            }
        }

        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        function addItem(index, aLabel, aValue) {
            if (aValue !== null && index >= 0 && index <= box.options.length) {
                if (index === 0)
                    Logger.warning(i18n['null.item.index']);
                var item = document.get().createElement('option');
                item.innerText = aLabel;
                item['js-value'] = aValue;
                var wasUnselected = box.selectedIndex === -1;
                if (index === box.options.length) {
                    box.appendChild(item);
                } else {
                    box.insertBefore(itemAt(index), item);
                }
                if (wasUnselected) {
                    box.seelectedIndex = -1;
                }
            }
        }
        function removeItem(index) {
            if (index >= 0 && index < box.options.length) {
                var item = box.options[index];
                if (item !== nullItem) {
                    box.removeChild(item);
                    return item;
                }
            }
        }

        function indexOf(aValue) {
            for (var i = 0; i < box.options.length; i++) {
                if (box.options[i]['js-value'] === aValue) {
                    return i;
                }
            }
            return -1;
        }

    }
    extend(DropDownField, BoxField);
    return DropDownField;
});
