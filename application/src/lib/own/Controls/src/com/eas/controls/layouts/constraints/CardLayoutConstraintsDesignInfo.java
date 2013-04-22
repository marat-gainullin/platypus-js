/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.constraints;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class CardLayoutConstraintsDesignInfo extends LayoutConstraintsDesignInfo {

    protected String cardName;

    public CardLayoutConstraintsDesignInfo()
    {
        super();
    }

    @Serial
    public String getCardName() {
        return cardName;
    }

    @Serial
    public void setCardName(String aValue) {
        String oldValue = cardName;
        cardName = aValue;
        firePropertyChange("cardName", oldValue, cardName);
    }

    @Override
    public void accept(ConstraintsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        CardLayoutConstraintsDesignInfo other = (CardLayoutConstraintsDesignInfo) obj;
        if ((this.cardName == null) ? (other.cardName != null) : !this.cardName.equals(other.cardName)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof CardLayoutConstraintsDesignInfo) {
            CardLayoutConstraintsDesignInfo source = (CardLayoutConstraintsDesignInfo) aSource;
            cardName = source.cardName != null ? new String(source.cardName.toCharArray()) : null;
        }
    }
}
