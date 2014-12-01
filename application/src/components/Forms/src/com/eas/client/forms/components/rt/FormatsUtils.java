/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author mg
 */
public class FormatsUtils {

    /**
     * Number format (type).
     */
    public static final int NUMBER = 0;
    /**
     * Date format (type).
     */
    public static final int DATE = 1;
    /**
     * Time format (type).
     */
    public static final int TIME = 2;
    /**
     * Percent format (type).
     */
    public static final int PERCENT = 3;
    /**
     * Currency format (type).
     */
    public static final int CURRENCY = 4;
    /**
     * Mask format (type).
     */
    public static final int MASK = 5;

    public static JFormattedTextField.AbstractFormatterFactory formatterFactoryByFormat(String aFormat, int aType) {
        JFormattedTextField.AbstractFormatter value = null;
        if (aType == MASK) {
            try {
                OptimisticMaskFormatter maskFormatter = new OptimisticMaskFormatter(aFormat);
                maskFormatter.setValueContainsLiteralCharacters(true);
                value = maskFormatter;
            } catch (ParseException pex) {
                Logger.getLogger(FormatsUtils.class.getName()).log(Level.INFO, pex.getMessage(), pex);
                value = new OptimisticMaskFormatter();
            }
        } else if (aType == DATE) {
            DateFormat dateFormat = new SimpleDateFormat(aFormat);
            value = new DateFormatter(dateFormat);
        } else if (aType == TIME) {
            DateFormat timeFormat = new SimpleDateFormat(aFormat);
            value = new DateFormatter(timeFormat);
        } else if (aType == NUMBER) {
            NumberFormat numberFormat = new DecimalFormat(aFormat);
            value = new AdaptiveDecimalFormatter(numberFormat);
        } else if (aType == PERCENT) {
            NumberFormat percentFormat = new DecimalFormat(aFormat);
            value = new AdaptiveDecimalFormatter(percentFormat);
        } else if (aType == CURRENCY) {
            NumberFormat currencyFormat = new DecimalFormat(aFormat);
            value = new AdaptiveDecimalFormatter(currencyFormat);
        } else {
            assert false;
        }
        return new DefaultFormatterFactory(value);
    }

    public static String formatByFormatter(JFormattedTextField.AbstractFormatter aFormatter) {
        if (aFormatter instanceof InternationalFormatter) {
            InternationalFormatter iFormatter = (InternationalFormatter) aFormatter;
            if (iFormatter.getFormat() instanceof SimpleDateFormat) {
                return ((SimpleDateFormat) iFormatter.getFormat()).toPattern();
            } else if (iFormatter.getFormat() instanceof DecimalFormat) {
                return ((DecimalFormat) iFormatter.getFormat()).toPattern();
            }
        } else if (aFormatter instanceof MaskFormatter) {
            MaskFormatter mFormatter = (MaskFormatter) aFormatter;
            return mFormatter.getMask();
        }
        return null;
    }

    public static void applyFormat(JFormattedTextField.AbstractFormatter aFormatter, String aPattern) throws ParseException {
        if (aFormatter instanceof InternationalFormatter) {
            InternationalFormatter iFormatter = (InternationalFormatter) aFormatter;
            if (iFormatter.getFormat() instanceof SimpleDateFormat) {
                ((SimpleDateFormat) iFormatter.getFormat()).applyPattern(aPattern);
            } else if (iFormatter.getFormat() instanceof DecimalFormat) {
                ((DecimalFormat) iFormatter.getFormat()).applyPattern(aPattern);
            }
        } else if (aFormatter instanceof MaskFormatter) {
            MaskFormatter mFormatter = (MaskFormatter) aFormatter;
            mFormatter.setMask(aPattern);
        }
    }
}
