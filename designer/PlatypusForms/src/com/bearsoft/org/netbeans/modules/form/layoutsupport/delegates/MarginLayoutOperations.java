/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstants;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport.MarginLayoutConstraints;
import com.eas.client.forms.layouts.Margin;
import com.eas.client.forms.layouts.MarginConstraints;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lkolesnikov, mg
 */
public class MarginLayoutOperations {

    private final List<RADVisualComponent<?>> selected;          //выбранные компоненты
    private final RADVisualContainer<?> container;               //контейнер
    private List<MarginLayoutConstraints> selectedConstraints;   //Constraints выбранных компонентов
    final static private String DATA_MISSING_MSG = "Data for calculating missing {0}";

    public List<MarginLayoutConstraints> getSelectedConstraints() {
        return selectedConstraints;
    }

    public boolean canAlign() {
        return selected != null && selected.size() > 1 && container != null;
    }

    public RADVisualContainer<?> getContainer() {
        return container;
    }

    public List<RADVisualComponent<?>> getSelected() {
        return selected;
    }
    //признак того что в контейнере используется MarginLayout

    public MarginLayoutOperations(List<RADVisualComponent<?>> aSelected) {
        super();
        selected = aSelected;
        container = FormUtils.getSameParent(aSelected);
        gatherConstraints();
    }

    /**
     * Поверяем наличие отмеченной группы при назначении компоненту
     * фиксированного размера в MarginLayout контейнере
     *
     * @return
     */
    public boolean canResize() {
        return selected != null && !selected.isEmpty();
    }

    /**
     * Выравнивание выбранных компонентов в выбранном направлении.
     *
     * @param dimension направление выравнивания
     * @param alignment требуемое выравнивание
     * @throws java.lang.Exception
     */
    public void align(int dimension, int alignment) throws Exception {
        if (selected != null && selected.size() > 1) {//выбрано по крайней мере 2 компонента
            assert selected.size() == selectedConstraints.size();
            assert container != null;
            Dimension parentSize = container.getBeanInstance().getSize();
            if (dimension == 0) {//выравнирание в горизонтальном направлении
                if (alignment == LayoutConstants.LEADING) {//выравнивание влево
                    //ищем самый левый компонент
                    MarginConstraints first = findLeftCompConstraints(parentSize.width);
                    int leftNew = calcLeft(first, parentSize.width);//новая левая координата
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);
                        if (mc != first) {
                            int leftOld = calcLeft(mc, parentSize.width);  //старая левая координата
                            int rightOld = calcRight(mc, parentSize.width); //старая правая координата
                            if (mc.getLeft() != null) {
                                Margin newLeft = mc.getLeft().copy();
                                newLeft.setPlainValue(leftNew, parentSize.width);//выравниваем левую координату
                                mlc.getMLeft().setValue(newLeft);
                                if (mc.getRight() != null) {//ширина не используется. нужно устанавливать правую координату
                                    Margin newRight = mc.getRight().copy();
                                    newRight.setPlainValue(rightOld + (leftOld - leftNew), parentSize.width);//изменяем правую координату
                                    mlc.getMRight().setValue(newRight);
                                }
                            } else {
                                Margin newRight = mc.getRight().copy();
                                newRight.setPlainValue(rightOld + (leftOld - leftNew), parentSize.width);//изменяем правую координату
                                mlc.getMRight().setValue(newRight);
                            }
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
                if (alignment == LayoutConstants.TRAILING) {//выравнивание вправо
                    //ищем самый правый компонент
                    MarginConstraints first = findRightCompConstraints(parentSize.width);
                    int rightNew = calcRight(first, parentSize.width);//новая правая координата
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);
                        if (mc != first) {
                            int leftOld = calcLeft(mc, parentSize.width); //старая левая координата
                            int rightOld = calcRight(mc, parentSize.width); //старая правая координата
                            //выравнивание
                            if (mc.getLeft() != null) {
                                Margin newLeft = mc.getLeft().copy();
                                newLeft.setPlainValue(leftOld + (rightOld - rightNew), parentSize.width);//изменяем левую координату
                                mlc.getMLeft().setValue(newLeft);
                                if (mc.getRight() != null)//ширина не используется
                                {
                                    Margin newRight = mc.getRight().copy();
                                    newRight.setPlainValue(rightNew, parentSize.width);//выравниваем правая координату                                    }
                                    mlc.getMRight().setValue(newRight);
                                }
                            } else {
                                Margin newRight = mc.getRight().copy();
                                newRight.setPlainValue(rightNew, parentSize.width);//выравниваем правая координату
                                mlc.getMRight().setValue(newRight);
                            }
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
                if (alignment == LayoutConstants.CENTER) {//выравнивание по центру
                    //ищем самый левый компонент
                    MarginConstraints firstL = findLeftCompConstraints(parentSize.width);
                    int leftNew = calcLeft(firstL, parentSize.width);//новая левая координата
                    //ищем самый правый компонент
                    MarginConstraints firstR = findRightCompConstraints(parentSize.width);
                    int rightNew = calcRight(firstR, parentSize.width);//новая правая координата;
                    int centerNew = (parentSize.width - rightNew + leftNew) / 2; //новый центр
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);

                        int leftOld = calcLeft(mc, parentSize.width); //старая левая координата
                        int rightOld = calcRight(mc, parentSize.width); //старая правая координата
                        int centerOld = (parentSize.width - rightOld + leftOld) / 2;
                        //выравнивание
                        int sm = centerNew - centerOld;
                        if (mc.getLeft() != null) {
                            Margin newLeft = mc.getLeft().copy();
                            newLeft.setPlainValue(leftOld + sm, parentSize.width);//изменяем левую координату
                            mlc.getMLeft().setValue(newLeft);
                            if (mc.getRight() != null)//ширина не используется
                            {
                                Margin newRight = mc.getRight().copy();
                                newRight.setPlainValue(rightOld - sm, parentSize.width);//выравниваем правая координату
                                mlc.getMRight().setValue(newRight);
                            }
                        } else {
                            Margin newRight = mc.getRight().copy();
                            newRight.setPlainValue(rightOld - sm, parentSize.width);//выравниваем правая координату
                            mlc.getMRight().setValue(newRight);
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
            }
            if (dimension == 1) {//выравнивание в вертикальном направлении
                if (alignment == LayoutConstants.LEADING) {//выравнивание вверх
                    //ищем самый верхний компонент
                    MarginConstraints first = findTopCompConstraints(parentSize.height);
                    int topNew = calcTop(first, parentSize.height);//новая верхняя координата
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);
                        if (mc != first) {
                            int topOld = calcTop(mc, parentSize.height); //старая левая координата
                            int bottomOld = calcBottom(mc, parentSize.height); //старая правая координата
                            if (mc.getTop() != null) {
                                Margin newTop = mc.getTop().copy();
                                newTop.setPlainValue(topNew, parentSize.height);//выравниваем левую координату
                                mlc.getMTop().setValue(newTop);
                                if (mc.getBottom() != null) {//ширина не используется. нужно устанавливать правую координату
                                    Margin newBottom = mc.getBottom().copy();
                                    newBottom.setPlainValue(bottomOld + (topOld - topNew), parentSize.height);//изменяем правую координату
                                    mlc.getMBottom().setValue(newBottom);
                                }
                            } else {
                                Margin newBottom = mc.getBottom().copy();
                                newBottom.setPlainValue(bottomOld + (topOld - topNew), parentSize.height);//изменяем правую координату
                                mlc.getMBottom().setValue(newBottom);
                            }
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
                if (alignment == LayoutConstants.TRAILING) {//выравнивание вниз
                    //ищем самый нижный компонент
                    MarginConstraints first = findBottomCompConstraints(parentSize.height);
                    int bottomNew = calcBottom(first, parentSize.height);//новая нижняя координата
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);
                        if (mc != null) {
                            int topOld = calcTop(mc, parentSize.height); //старая верхняя координата
                            int bottomOld = calcBottom(mc, parentSize.height); //старая нижняя координата
                            //выравнивание
                            if (mc.getTop() != null) {
                                Margin newTop = mc.getTop().copy();
                                newTop.setPlainValue(topOld + (bottomOld - bottomNew), parentSize.height);//изменяем левую координату
                                mlc.getMTop().setValue(newTop);
                                if (mc.getBottom() != null) {//ширина не используется
                                    Margin newBottom = mc.getBottom().copy();
                                    newBottom.setPlainValue(bottomNew, parentSize.height);//выравниваем правая координату
                                    mlc.getMBottom().setValue(newBottom);
                                }
                            } else {
                                Margin newBottom = mc.getBottom().copy();
                                newBottom.setPlainValue(bottomNew, parentSize.height);//выравниваем правая координату
                                mlc.getMBottom().setValue(newBottom);
                            }
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
                if (alignment == LayoutConstants.CENTER) {//выравнивание по центру
                    //ищем самый верхний компонент
                    MarginConstraints firstT = findTopCompConstraints(parentSize.height);
                    int topNew = calcTop(firstT, parentSize.height);//новая верхняя координата
                    //ищем самый нижний компонент
                    MarginConstraints firstR = findBottomCompConstraints(parentSize.height);
                    int bottomNew = calcBottom(firstR, parentSize.height);//новая верхняя координата
                    int centerNew = (parentSize.height - bottomNew + topNew) / 2; //новый центр
                    //выравниваем остальные компоненты по найденому
                    for (int i = 0; i < selectedConstraints.size(); i++) {
                        MarginLayoutConstraints mlc = selectedConstraints.get(i);
                        MarginConstraints mc = mlc.getConstraintsObject();
                        RADVisualComponent<?> vc = selected.get(i);
                        int topOld = calcTop(mc, parentSize.height); //старая верхняя координата
                        int bottomOld = calcBottom(mc, parentSize.height); //старая нижняя координата
                        int centerOld = (parentSize.height - bottomOld + topOld) / 2;
                        //выравнивание
                        int sm = centerNew - centerOld;
                        if (mc.getTop() != null) {
                            Margin newTop = mc.getTop().copy();
                            newTop.setPlainValue(topOld + sm, parentSize.height);//изменяем верхняя координату
                            mlc.getMTop().setValue(newTop);
                            if (mc.getBottom() != null)//ширина не используется
                            {
                                Margin newBottom = mc.getBottom().copy();
                                newBottom.setPlainValue(bottomOld - sm, parentSize.height);//выравниваем нижняя координату
                                mlc.getMBottom().setValue(newBottom);
                            }
                        } else {
                            Margin newBottom = mc.getBottom().copy();
                            newBottom.setPlainValue(bottomOld - sm, parentSize.height);//выравниваем нижняя координату
                            mlc.getMBottom().setValue(newBottom);
                        }
                        //отметить компонент как invalid
                        vc.getBeanInstance().invalidate();
                    }
                }
            }
        }
    }
    //ищем свойство по имени

    //Формирует список MarginConstraints
    protected final void gatherConstraints() {
        selectedConstraints = new ArrayList<>();
        for (RADVisualComponent<?> vc : selected) {
            LayoutSupportDelegate lsd = vc.getParentLayoutSupport().getLayoutDelegate();
            assert lsd instanceof MarginLayoutSupport;
            MarginLayoutConstraints mlc = (MarginLayoutConstraints) lsd.getConstraints(vc.getComponentIndex());
            selectedConstraints.add(mlc);
        }
    }

    /**
     * Эти функции используются при выравнивании. Они возвращают
     * MarginConstraints компонента по которому будут выравниваться остальные
     */
    /**
     * Возвращает самый левый компонент
     *
     * @param aContainerWidth
     * @return 
     */
    public MarginConstraints findLeftCompConstraints(int aContainerWidth) {
        MarginConstraints leftConstraints = null;
        int min = Integer.MAX_VALUE;
        //ищем компонент с наименьшей верхней координатой
        for (MarginLayoutConstraints mlc : selectedConstraints) {
            MarginConstraints mc = mlc.getConstraintsObject();
            Rectangle rect = mc.toRectangle(aContainerWidth, 0);
            if (rect.x < min) {
                min = rect.x;
                leftConstraints = mc;
            }
        }
        return leftConstraints;
    }

    /**
     * Возвращает самый верхний компонент
     * @param aContainerHeight
     * @return 
     */
    public MarginConstraints findTopCompConstraints(int aContainerHeight) {
        MarginConstraints topConstraints = null;
        int min = Integer.MAX_VALUE;
        //ищем компонент с наименьшей верхней координатой
        for (MarginLayoutConstraints mlc : selectedConstraints) {
            MarginConstraints mc = mlc.getConstraintsObject();
            Rectangle rect = mc.toRectangle(0, aContainerHeight);
            if (rect.y < min) {
                min = rect.y;
                topConstraints = mc;
            }
        }
        return topConstraints;
    }

    /**
     * Возвращает самый правый компонент
     * @param aContainerWidth
     * @return 
     */
    public MarginConstraints findRightCompConstraints(int aContainerWidth) {
        MarginConstraints rightConstraints = null;
        int max = -Integer.MAX_VALUE;
        for (MarginLayoutConstraints mlc : selectedConstraints) {
            MarginConstraints mc = mlc.getConstraintsObject();
            Rectangle rect = mc.toRectangle(aContainerWidth, 0);
            if (rect.x + rect.width > max) {
                max = rect.x + rect.width;
                rightConstraints = mc;
            }
        }
        return rightConstraints;
    }

    /**
     * Возвращает самый нижний компонент
     * @param aContainerHeight
     * @return 
     */
    public MarginConstraints findBottomCompConstraints(int aContainerHeight) {
        MarginConstraints bottomConstraints = null;
        int max = -Integer.MAX_VALUE;
        for (MarginLayoutConstraints mlc : selectedConstraints) {
            MarginConstraints mc = mlc.getConstraintsObject();
            Rectangle rect = mc.toRectangle(0, aContainerHeight);
            if (rect.y + rect.height > max) {
                max = rect.y + rect.height;
                bottomConstraints = mc;
            }
        }
        return bottomConstraints;
    }

    protected Dimension parentSize(RADVisualComponent<?> aVisualComponent) {
        RADVisualContainer<?> radParent = aVisualComponent.getParentComponent();
        Container parentComp = radParent.getBeanInstance();
        Dimension parentSize = parentComp.getSize();
        return parentSize;
    }

    /**
     * Установка или сброс ширины/высоты компонента
     *
     * @param dimension направление: 0 - ширина, 1 - высота
     * @throws java.lang.Exception
     *
     */
    public void resize(int dimension) throws Exception {
        if (selectedConstraints != null && !selectedConstraints.isEmpty()) {//выбрано по крайней мере 1 компонент
            assert selectedConstraints.size() == selected.size();
            for (int i = 0; i < selectedConstraints.size(); i++) {
                MarginLayoutConstraints mlc = selectedConstraints.get(i);
                MarginConstraints mc = mlc.getConstraintsObject();
                RADVisualComponent<?> vc = selected.get(i);
                Dimension parentSize = parentSize(vc);
                if (dimension == 0) {//горизонталь
                    if (mc.getWidth() != null) {//Ширина используется. Cбрасываем.
                        int w = mc.getWidth().calcPlainValue(parentSize.width);
                        if (mc.getLeft() == null) {
                            assert mc.getRight() != null;
                            // Устанавливаем лево
                            Margin newLeft = new Margin(0, true);
                            newLeft.setPlainValue(parentSize.width - mc.getRight().calcPlainValue(parentSize.width) - w, parentSize.width);
                            mlc.getMLeft().setValue(newLeft);
                        } else {
                            // Устанавливаем право
                            Margin oldRight = mc.getRight();
                            Margin newRight = new Margin(0, oldRight != null ? oldRight.absolute : true);
                            newRight.setPlainValue(parentSize.width - mc.getLeft().calcPlainValue(parentSize.width) - w, parentSize.width);
                            if (!newRight.isEqual(oldRight)) {
                                mlc.getMRight().setValue(newRight);
                            }
                        }
                        //сбрасываем ширину
                        mlc.getMWidth().setValue(null);
                    } else {//Ширина не используется. Устанавливаем
                        int left = mc.getLeft().calcPlainValue(parentSize.width); //считываем левую координату
                        int right = mc.getRight().calcPlainValue(parentSize.width); //считываем правую координату
                        int w = parentSize.width - left - right;
                        //устанавливаем ширину
                        mlc.getMWidth().setValue(new Margin(w, true));
                        mlc.getMRight().setValue(null);
                    }
                } else if (dimension == 1) {
                    if (mc.getHeight() != null) {//высота используется. сбрасываем
                        int h = mc.getHeight().calcPlainValue(parentSize.height); //считываем высоту 
                        if (mc.getTop() == null) {
                            Margin newTop = new Margin(0, true);
                            newTop.setPlainValue(parentSize.height - mc.getBottom().calcPlainValue(parentSize.height) - h, parentSize.height);
                            mlc.getMTop().setValue(newTop);
                        } else {
                            Margin oldBottom = mc.getBottom();
                            Margin newBottom = new Margin(0, oldBottom != null ? oldBottom.absolute : true);
                            newBottom.setPlainValue(parentSize.height - mc.getTop().calcPlainValue(parentSize.height) - h, parentSize.height);
                            if (!newBottom.isEqual(oldBottom)) {
                                mlc.getMBottom().setValue(newBottom);
                            }
                        }
                        //сбрасываем высоту
                        mlc.getMHeight().setValue(null);
                    } else {//Высота не используется. Устанавливаем
                        assert mc.getTop() != null && mc.getBottom() != null;
                        int top = mc.getTop().calcPlainValue(parentSize.height); //считываем верхнюю координату
                        int bottom = mc.getBottom().calcPlainValue(parentSize.height); //считываем нижнюю координату
                        int h = parentSize.height - top - bottom;
                        //устанавливаем высоту
                        mlc.getMHeight().setValue(new Margin(h, true));
                        mlc.getMBottom().setValue(null);
                    }
                }
                //отметить компонент как invalid
                vc.getBeanInstance().invalidate();
            }
        }
    }

    /**
     * Эти функции возвращают значения координат в пикселах. Если координата не
     * используется то ее значение вычисляется. Считаем что из трех координат
     * должно существовать две.
     *
     * Параметры: mc - MarginConstraints w1/h1 - ширина или высота контейнера
     * @param aConstraints
     * @param aWidth
     * @return 
     */
    public int calcWidth(MarginConstraints aConstraints, int aWidth) {
        int w = 0;
        if (aConstraints.getWidth() != null) {
            Margin mrg = aConstraints.getWidth();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                w = Math.round(k * aWidth);
            } else {
                w = mrg.value;
            }
        } else {
            if (aConstraints.getLeft() != null && aConstraints.getRight() != null) {
                w = aWidth - calcLeft(aConstraints, aWidth) - calcRight(aConstraints, aWidth);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "ширины"));
            }
        }
        return w;

    }

    public int calcHeight(MarginConstraints mc, int aHeight) {
        int h = 0;
        if (mc.getHeight() != null) {
            Margin mrg = mc.getHeight();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                h = Math.round(k * aHeight);
            } else {
                h = mrg.value;
            }
        } else {
            if (mc.getTop() != null && mc.getBottom() != null) {
                h = aHeight - calcTop(mc, aHeight) - calcBottom(mc, aHeight);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "высоты"));
            }
        }
        return h;
    }

    public int calcLeft(MarginConstraints mc, int aWidth) {
        int left = 0;
        if (mc.getLeft() != null) {
            Margin mrg = mc.getLeft();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                left = Math.round(k * aWidth);
            } else {
                left = mrg.value;
            }
        } else {
            if (mc.getRight() != null && mc.getWidth() != null) {
                left = aWidth - calcRight(mc, aWidth) - calcWidth(mc, aWidth);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "левой координаты"));
            }
        }
        return left;
    }

    public int calcTop(MarginConstraints mc, int aHeight) {
        int top = 0;
        if (mc.getTop() != null) {
            Margin mrg = mc.getTop();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                top = Math.round(k * aHeight);
            } else {
                top = mrg.value;
            }
        } else {
            if (mc.getBottom() != null && mc.getHeight() != null) {
                top = aHeight - calcBottom(mc, aHeight) - calcHeight(mc, aHeight);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "верхней координаты"));
            }
        }
        return top;
    }

    public int calcRight(MarginConstraints mc, int aWidth) {
        int right = 0;
        if (mc.getRight() != null) {
            Margin mrg = mc.getRight();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                right = Math.round(k * aWidth);
            } else {
                right = mrg.value;
            }
            if (mc.getLeft() != null && mc.getWidth() != null) {//высота перекрывает
                right = aWidth - calcLeft(mc, aWidth) - calcWidth(mc, aWidth);
            }
        } else {
            if (mc.getLeft() != null && mc.getWidth() != null) {
                right = aWidth - calcLeft(mc, aWidth) - calcWidth(mc, aWidth);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "правой координаты"));
            }
        }
        return right;
    }

    public int calcBottom(MarginConstraints mc, int aHeight) {
        int bottom = 0;
        int top = 0;
        if (mc.getBottom() != null) {
            Margin mrg = mc.getBottom();
            if (!mrg.absolute) {//параметр в %
                float k = ((float) mrg.value) / 100;
                bottom = Math.round(k * aHeight);
            } else {
                bottom = mrg.value;
            }
            if (mc.getTop() != null && mc.getHeight() != null) {//высота перекрывает
                bottom = aHeight - calcTop(mc, aHeight) - calcHeight(mc, aHeight);
            }
        } else {
            if (mc.getTop() != null && mc.getHeight() != null) {
                bottom = aHeight - calcTop(mc, aHeight) - calcHeight(mc, aHeight);
            } else {
                throw new IllegalStateException(String.format(DATA_MISSING_MSG, "нижней координаты"));
            }
            //если есть высота bottom неважен
        }
        return bottom;
    }

    //Групповые операции для Якоря
    public boolean isAllLeft() {
        return selectedConstraints.stream().allMatch((mlc) -> (mlc.getConstraintsObject().getLeft() != null));
    }

    public boolean isAllTop() {
        return selectedConstraints.stream().allMatch((mlc) -> (mlc.getConstraintsObject().getTop() != null));
    }

    public boolean isAllRight() {
        return selectedConstraints.stream().allMatch((mlc) -> (mlc.getConstraintsObject().getRight() != null));
    }

    public boolean isAllBottom() {
        return selectedConstraints.stream().allMatch((mlc) -> (mlc.getConstraintsObject().getBottom() != null));
    }

    public void setAllLeft() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getLeft() == null && mc.getRight() != null && mc.getWidth() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                mlc.getMLeft().setValue(new Margin(calcLeft(mc, parentSize(vc).width), true));
                if (mc.getLeft() != null && mc.getWidth() != null && mc.getRight() != null) {
                    mlc.getMWidth().setValue(null);
                }
            }
        }
    }

    public void setAllRight() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getRight() == null && mc.getLeft() != null && mc.getWidth() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                Margin newRight = new Margin(calcRight(mc, parentSize(vc).width), true);
                mlc.getMRight().setValue(newRight);
                if (mc.getLeft() != null && mc.getWidth() != null && mc.getRight() != null) {
                    mlc.getMWidth().setValue(null);
                }
            }
        }
    }

    public void setAllTop() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getTop() == null && mc.getBottom() != null && mc.getHeight() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                mlc.getMTop().setValue(new Margin(calcTop(mc, parentSize(vc).height), true));
                if (mc.getTop() != null && mc.getHeight() != null && mc.getBottom() != null) {
                    mlc.getMHeight().setValue(null);
                }
            }
        }
    }

    public void setAllBottom() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getBottom() == null && mc.getTop() != null && mc.getHeight() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                Margin newBottom = new Margin(calcBottom(mc, parentSize(vc).height), true);
                mlc.getMBottom().setValue(newBottom);
                if (mc.getTop() != null && mc.getHeight() != null && mc.getBottom() != null) {
                    mlc.getMHeight().setValue(null);
                }
            }
        }
    }

    public void clearAllLeft() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getLeft() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                if (mc.getWidth() == null && mc.getRight() != null) {
                    mlc.getMWidth().setValue(new Margin(calcWidth(mc, parentSize(vc).width), true));
                }
                if (mc.getRight() != null && mc.getWidth() != null) {
                    mlc.getMLeft().setValue(null);
                }
            }
        }
    }

    public void clearAllRight() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getRight() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                if (mc.getWidth() == null && mc.getLeft() != null) {
                    mlc.getMWidth().setValue(new Margin(calcWidth(mc, parentSize(vc).width), true));
                }
                if (mc.getLeft() != null && mc.getWidth() != null) {
                    mlc.getMRight().setValue(null);
                }
            }
        }
    }

    public void clearAllTop() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            if (mc.getTop() != null) {
                RADVisualComponent<?> vc = selected.get(i);
                if (mc.getBottom() != null && mc.getHeight() == null) {
                    mlc.getMHeight().setValue(new Margin(calcHeight(mc, parentSize(vc).height), true));
                }
                if (mc.getBottom() != null && mc.getHeight() != null) {
                    mlc.getMTop().setValue(null);
                }
            }
        }
    }

    public void clearAllBottom() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < selectedConstraints.size(); i++) {
            MarginLayoutConstraints mlc = selectedConstraints.get(i);
            MarginConstraints mc = mlc.getConstraintsObject();
            RADVisualComponent<?> vc = selected.get(i);
            if (mc.getBottom() != null) {
                if (mc.getTop() != null && mc.getHeight() == null) {
                    mlc.getMHeight().setValue(new Margin(calcHeight(mc, parentSize(vc).height), true));
                }
                if (mc.getTop() != null && mc.getHeight() != null) {
                    mlc.getMBottom().setValue(null);
                }
            }
        }
    }
}
