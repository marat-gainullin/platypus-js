/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;

/**
 * Number formatter descandant class intended to solve "eaten decimal separator" and some other problems.
 * This problem occurs with allowsInvalid flag setted to false.
 * The problem is: when you type in a control string like this: "412,", it transforms into "412".
 * Thus, you are unable to type integer part and than fraction part of a number digit by digit.
 * In this situation user is forced to type all digits from both parts of a number and than choose
 * where decimal separator is to be placed. It's ugly and terrible for any user.
 * There are two other approachs to solve this problem. The first is to set mask like this: "#.0##".
 * Second is to call setDecimalSeparatorAlwaysShown(true) on the decimal format.
 * These approachs lead to decimal separator be allways shown. It's strongly undesireable!
 * I whant the control to input all digits, i've type in it, if they are all valid and in the order of my input!
 * So, this class allows such behavior to be applied.
 * @author mg
 */
public class AdaptiveDecimalFormatter extends NumberFormatter {

    private static DummyMinusValue DUMMY_MINUS = new DummyMinusValue();

    public AdaptiveDecimalFormatter() {
        super();
    }

    public AdaptiveDecimalFormatter(NumberFormat aFormat) {
        super(aFormat);
    }
    
    @Override
    public Object stringToValue(String aText) throws ParseException {
        if (aText == null || aText.isEmpty()) {
            return null;
        } else {
            Format f = getFormat();
            if (f instanceof DecimalFormat) {
                DecimalFormat df = (DecimalFormat) f;
                if (getFormattedTextField().hasFocus()) {
                    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                    String userString = prepareText(aText, dfs);
                    if (!userString.isEmpty()) {
                        int sepIndex = userString.indexOf(dfs.getDecimalSeparator());
                        if (sepIndex == -1) {
                            sepIndex = userString.indexOf(dfs.getMonetaryDecimalSeparator());
                        }
                        int intCount = 0;
                        int fracCount = 0;
                        if (sepIndex != -1) {
                            intCount = sepIndex;
                            fracCount = userString.length() - (sepIndex + 1);
                            int realIntCount = ajustIntPart(intCount, userString, sepIndex);

                            df.setDecimalSeparatorAlwaysShown(true);
                            df.setMaximumIntegerDigits(Math.max(realIntCount, df.getMaximumIntegerDigits()));
                            df.setMinimumIntegerDigits(realIntCount);
                            df.setMaximumFractionDigits(Math.max(fracCount, df.getMaximumFractionDigits()));
                            df.setMinimumFractionDigits(fracCount);
                        } else {
                            df.setDecimalSeparatorAlwaysShown(false);
                            String userMeaningString = userString.replaceFirst("0+", "");
                            if (userString.isEmpty()) {
                                df.setMinimumIntegerDigits(0);
                            } else {
                                if (userMeaningString.length() < userString.length()) {
                                    df.setMinimumIntegerDigits(userMeaningString.length() + 1);
                                } else {
                                    df.setMinimumIntegerDigits(0);
                                }
                            }
                            df.setMinimumFractionDigits(0);
                        }
                    } else {
                        if (aText.length() == 1 && aText.toCharArray()[0] == dfs.getMinusSign()) {
                            return DUMMY_MINUS;
                        }
                    }
                } else {
                    df.setDecimalSeparatorAlwaysShown(false);
                    df.setMinimumIntegerDigits(1);
                    df.setMinimumFractionDigits(0);
                }
            }
            return super.stringToValue(aText);
        }
    }

    protected int ajustIntPart(int intCount, String userString, int sepIndex) {
        int realIntCount = intCount;
        String userIntString = userString.substring(0, sepIndex);
        String userIntMeaningString = userIntString.replaceFirst("0+", "");
        if (userIntString.isEmpty()) {
            realIntCount = intCount;
        } else {
            if (userIntMeaningString.length() < userIntString.length()) {
                realIntCount = userIntMeaningString.length() + 1;
            } else {
                realIntCount = intCount;
            }
        }
        return realIntCount;
    }

    protected String prepareText(String aText, DecimalFormatSymbols dfs) {
        String userString = aText.replaceAll(dfs.getCurrencySymbol(), "");
        userString = userString.replaceAll(dfs.getInternationalCurrencySymbol(), "");
        userString = userString.replaceAll(dfs.getExponentSeparator(), "");
        userString = userString.replaceAll(dfs.getInfinity(), "");
        userString = userString.replaceAll(dfs.getNaN(), "");
        userString = userString.replaceAll(new String(new char[]{dfs.getGroupingSeparator()}), "");
        userString = userString.replaceAll(new String(new char[]{dfs.getMinusSign()}), "");
        userString = userString.replaceAll(new String(new char[]{dfs.getPerMill()}), "");
        userString = userString.replaceAll(new String(new char[]{dfs.getPercent()}), "");
        return userString;
    }

    @Override
    public String valueToString(Object aValue) throws ParseException {
        if (aValue == null) {
            return "";
        } else {
            if (aValue == DUMMY_MINUS) {
                return "-";
            } else {
                return super.valueToString(aValue);
            }
        }
    }
}
