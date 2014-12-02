/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.visitors;

import com.eas.client.forms.events.factories.SwingFactory;
import com.eas.controls.borders.BevelBorderDesignInfo;
import com.eas.controls.borders.BorderDesignInfoVisitor;
import com.eas.controls.borders.CompoundBorderDesignInfo;
import com.eas.controls.borders.EmptyBorderDesignInfo;
import com.eas.controls.borders.EtchedBorderDesignInfo;
import com.eas.controls.borders.LineBorderDesignInfo;
import com.eas.controls.borders.MatteBorderDesignInfo;
import com.eas.controls.borders.SoftBevelBorderDesignInfo;
import com.eas.controls.borders.TitledBorderDesignInfo;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author mg
 */
public class SwingBorderFactory implements BorderDesignInfoVisitor {

    protected Border border;
    protected SwingFactory factory;
    
    public SwingBorderFactory(SwingFactory aFactory){
        super();
        factory = aFactory;
    }

    public Border getBorder() {
        return border;
    }

    @Override
    public void visit(BevelBorderDesignInfo aInfo) {
        border = new BevelBorder(aInfo.getBevelType(), aInfo.getHighlightOuterColor(), aInfo.getHighlightInnerColor(), aInfo.getShadowOuterColor(), aInfo.getShadowInnerColor());
    }

    @Override
    public void visit(CompoundBorderDesignInfo aInfo) {
        SwingBorderFactory outsideFactory = new SwingBorderFactory(factory);
        if (aInfo.getOutsideBorder() != null) {
            aInfo.getOutsideBorder().accept(outsideFactory);
        }
        SwingBorderFactory insideFactory = new SwingBorderFactory(factory);
        if (aInfo.getInsideBorder() != null) {
            aInfo.getInsideBorder().accept(insideFactory);
        }
        border = new CompoundBorder(outsideFactory.getBorder(), insideFactory.getBorder());
    }

    @Override
    public void visit(EmptyBorderDesignInfo aInfo) {
        border = new EmptyBorder(aInfo.getTop(), aInfo.getLeft(), aInfo.getBottom(), aInfo.getRight());
    }

    @Override
    public void visit(EtchedBorderDesignInfo aInfo) {
        border = new EtchedBorder(aInfo.getEtchType(), aInfo.getHighlightColor(), aInfo.getShadowColor());
    }

    @Override
    public void visit(LineBorderDesignInfo aInfo) {
        border = new LineBorder(aInfo.getLineColor(), aInfo.getThickness(), aInfo.isRoundedCorners());
    }

    @Override
    public void visit(MatteBorderDesignInfo aInfo) {
        Icon tileIcon = factory != null ? factory.resolveIcon(aInfo.getTileIcon()) : null;
        if (tileIcon != null) {
            border = new MatteBorder(aInfo.getTop(), aInfo.getLeft(), aInfo.getBottom(), aInfo.getRight(), tileIcon);
        } else {
            border = new MatteBorder(aInfo.getTop(), aInfo.getLeft(), aInfo.getBottom(), aInfo.getRight(), aInfo.getMatteColor());
        }
    }

    @Override
    public void visit(SoftBevelBorderDesignInfo aInfo) {
        border = new SoftBevelBorder(aInfo.getBevelType(), aInfo.getHighlightOuterColor(), aInfo.getHighlightInnerColor(), aInfo.getShadowOuterColor(), aInfo.getShadowInnerColor());
    }

    @Override
    public void visit(TitledBorderDesignInfo aInfo) {
        SwingBorderFactory innerFactory = new SwingBorderFactory(factory);
        if (aInfo.getBorder() != null) {
            aInfo.getBorder().accept(innerFactory);
        }
        border = new TitledBorder(innerFactory.getBorder(), aInfo.getTitle(), aInfo.getTitleJustification(), aInfo.getTitlePosition(), aInfo.getTitleFont(), aInfo.getTitleColor());
    }
}
