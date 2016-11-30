package com.eas.widgets.boxes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MaskFormat {
	// Potential values in mask.
	private static final char DIGIT_KEY = '#';
	private static final char LITERAL_KEY = '\'';
	private static final char UPPERCASE_KEY = 'U';
	private static final char LOWERCASE_KEY = 'L';
	private static final char ALPHA_NUMERIC_KEY = 'A';
	private static final char CHARACTER_KEY = '?';
	private static final char ANYTHING_KEY = '*';
	private static final char HEX_KEY = 'H';

	private static final MaskCharacter[] EmptyMaskChars = new MaskCharacter[0];

	private boolean allowsInvalid;

	/** The user specified mask. */
	private String mask;

	private transient MaskCharacter[] maskChars;

	/** List of valid characters. */
	private String validCharacters;

	/** List of invalid characters. */
	private String invalidCharacters;

	/**
	 * String used for the passed in value if it does not completely fill the
	 * mask.
	 */
	private String placeholderString;

	/** String used to represent characters not present. */
	private char placeholder;

	/** Indicates if the value contains the literal characters. */
	private boolean containsLiteralChars;

	/**
	 * Creates a MaskFormat with no mask.
	 */
	public MaskFormat() {
		setAllowsInvalid(false);
		containsLiteralChars = true;
		maskChars = EmptyMaskChars;
		placeholder = ' ';
	}

	/**
	 * Creates a <code>MaskFormatter</code> with the specified mask. A
	 * <code>ParseException</code> will be thrown if <code>mask</code> is an
	 * invalid mask.
	 * 
	 * @throws ParseException
	 *             if mask does not contain valid mask characters
	 */
	public MaskFormat(String mask) throws ParseException {
		this();
		setMask(mask);
	}

	/**
	 * Sets the mask dictating the legal characters. This will throw a
	 * <code>ParseException</code> if <code>mask</code> is not valid.
	 * 
	 * @throws ParseException
	 *             if mask does not contain valid mask characters
	 */
	public void setMask(String aValue) throws ParseException {
		mask = aValue;
		updateInternalMask();
	}

	/**
	 * Returns the formatting mask.
	 * 
	 * @return Mask dictating legal character values.
	 */
	public String getMask() {
		return mask;
	}

	public boolean getAllowsInvalid() {
		return allowsInvalid;
	}

	public void setAllowsInvalid(boolean allowsInvalid) {
		this.allowsInvalid = allowsInvalid;
	}

	/**
	 * Allows for further restricting of the characters that can be input. Only
	 * characters specified in the mask, not in the
	 * <code>invalidCharacters</code>, and in <code>validCharacters</code> will
	 * be allowed to be input. Passing in null (the default) implies the valid
	 * characters are only bound by the mask and the invalid characters.
	 * 
	 * @param validCharacters
	 *            If non-null, specifies legal characters.
	 */
	public void setValidCharacters(String validCharacters) {
		this.validCharacters = validCharacters;
	}

	/**
	 * Returns the valid characters that can be input.
	 * 
	 * @return Legal characters
	 */
	public String getValidCharacters() {
		return validCharacters;
	}

	/**
	 * Allows for further restricting of the characters that can be input. Only
	 * characters specified in the mask, not in the
	 * <code>invalidCharacters</code>, and in <code>validCharacters</code> will
	 * be allowed to be input. Passing in null (the default) implies the valid
	 * characters are only bound by the mask and the valid characters.
	 * 
	 * @param invalidCharacters
	 *            If non-null, specifies illegal characters.
	 */
	public void setInvalidCharacters(String invalidCharacters) {
		this.invalidCharacters = invalidCharacters;
	}

	/**
	 * Returns the characters that are not valid for input.
	 * 
	 * @return illegal characters.
	 */
	public String getInvalidCharacters() {
		return invalidCharacters;
	}

	/**
	 * Sets the string to use if the value does not completely fill in the mask.
	 * A null value implies the placeholder char should be used.
	 * 
	 * @param placeholder
	 *            String used when formatting if the value does not completely
	 *            fill the mask
	 */
	public void setPlaceholder(String placeholder) {
		this.placeholderString = placeholder;
	}

	/**
	 * Returns the String to use if the value does not completely fill in the
	 * mask.
	 * 
	 * @return String used when formatting if the value does not completely fill
	 *         the mask
	 */
	public String getPlaceholder() {
		return placeholderString;
	}

	/**
	 * Sets the character to use in place of characters that are not present in
	 * the value, ie the user must fill them in. The default value is a space.
	 * <p>
	 * This is only applicable if the placeholder string has not been specified,
	 * or does not completely fill in the mask.
	 * 
	 * @param placeholder
	 *            Character used when formatting if the value does not
	 *            completely fill the mask
	 */
	public void setPlaceholderCharacter(char placeholder) {
		this.placeholder = placeholder;
	}

	/**
	 * Returns the character to use in place of characters that are not present
	 * in the value, ie the user must fill them in.
	 * 
	 * @return Character used when formatting if the value does not completely
	 *         fill the mask
	 */
	public char getPlaceholderCharacter() {
		return placeholder;
	}

	/**
	 * If true, the returned value and set value will also contain the literal
	 * characters in mask.
	 * <p>
	 * For example, if the mask is <code>'(###) ###-####'</code>, the current
	 * value is <code>'(415) 555-1212'</code>, and
	 * <code>valueContainsLiteralCharacters</code> is true
	 * <code>stringToValue</code> will return <code>'(415) 555-1212'</code>. On
	 * the other hand, if <code>valueContainsLiteralCharacters</code> is false,
	 * <code>stringToValue</code> will return <code>'4155551212'</code>.
	 * 
	 * @param containsLiteralChars
	 *            Used to indicate if literal characters in mask should be
	 *            returned in stringToValue
	 */
	public void setValueContainsLiteralCharacters(boolean containsLiteralChars) {
		this.containsLiteralChars = containsLiteralChars;
	}

	/**
	 * Returns true if <code>stringToValue</code> should return literal
	 * characters in the mask.
	 * 
	 * @return True if literal characters in mask should be returned in
	 *         stringToValue
	 */
	public boolean getValueContainsLiteralCharacters() {
		return containsLiteralChars;
	}

	/**
	 * Parses the text, returning the appropriate Object representation of the
	 * String <code>value</code>. This strips the literal characters as
	 * necessary and invokes supers <code>stringToValue</code>, so that if you
	 * have specified a value class (<code>setValueClass</code>) an instance of
	 * it will be created. This will throw a <code>ParseException</code> if the
	 * value does not match the current mask. Refer to
	 * {@link #setValueContainsLiteralCharacters} for details on how literals
	 * are treated.
	 * 
	 * @throws ParseException
	 *             if there is an error in the conversion
	 * @param value
	 *            String to convert
	 * @see #setValueContainsLiteralCharacters
	 * @return Object representation of text
	 */
	public Object parse(String value) throws ParseException {
		return stringToValue(value, false);
	}

	/**
	 * Returns a String representation of the Object <code>value</code> based on
	 * the mask. Refer to {@link #setValueContainsLiteralCharacters} for details
	 * on how literals are treated.
	 * 
	 * @throws ParseException
	 *             if there is an error in the conversion
	 * @param value
	 *            Value to convert
	 * @see #setValueContainsLiteralCharacters
	 * @return String representation of value
	 */
	public String format(Object value) throws ParseException {
		String sValue = (value == null) ? "" : value.toString();
		StringBuilder result = new StringBuilder();
		String placeholder = getPlaceholder();
		int[] valueCounter = { 0 };

		append(result, sValue, valueCounter, placeholder, maskChars);
		return result.toString().trim();
	}

	/**
	 * Actual <code>stringToValue</code> implementation. If
	 * <code>completeMatch</code> is true, the value must exactly match the
	 * mask, on the other hand if <code>completeMatch</code> is false the string
	 * must match the mask or the placeholder string.
	 */
	private Object stringToValue(String value, boolean completeMatch) throws ParseException {
		int errorOffset;

		if ((errorOffset = getInvalidOffset(value, completeMatch)) == -1) {
			if (!getValueContainsLiteralCharacters()) {
				value = stripLiteralChars(value);
			}
			return value;
		}
		throw new ParseException("Invalid value", errorOffset);
	}

	/**
	 * Returns -1 if the passed in string is valid, otherwise the index of the
	 * first bogus character is returned.
	 */
	private int getInvalidOffset(String string, boolean completeMatch) {
		int iLength = string.length();

		if (iLength != getMaxLength()) {
			// trivially false
			return iLength;
		}
		for (int counter = 0, max = string.length(); counter < max; counter++) {
			char aChar = string.charAt(counter);

			if (!isValidCharacter(counter, aChar) && (completeMatch || !isPlaceholder(counter, aChar))) {
				return counter;
			}
		}
		return -1;
	}

	/**
	 * Invokes <code>append</code> on the mask characters in <code>mask</code>.
	 */
	private void append(StringBuilder result, String value, int[] index, String placeholder, MaskCharacter[] mask) throws ParseException {
		for (int counter = 0, maxCounter = mask.length; counter < maxCounter; counter++) {
			mask[counter].append(result, value, index, placeholder);
		}
	}

	/**
	 * Updates the internal representation of the mask.
	 */
	private void updateInternalMask() throws ParseException {
		String mask = getMask();
		List<MaskCharacter> fixed = new ArrayList<MaskCharacter>();
		List<MaskCharacter> temp = fixed;

		if (mask != null) {
			for (int counter = 0, maxCounter = mask.length(); counter < maxCounter; counter++) {
				char maskChar = mask.charAt(counter);

				switch (maskChar) {
				case DIGIT_KEY:
					temp.add(new DigitMaskCharacter());
					break;
				case LITERAL_KEY:
					if (++counter < maxCounter) {
						maskChar = mask.charAt(counter);
						temp.add(new LiteralCharacter(maskChar));
					}
					// else: Could actually throw if else
					break;
				case UPPERCASE_KEY:
					temp.add(new UpperCaseCharacter());
					break;
				case LOWERCASE_KEY:
					temp.add(new LowerCaseCharacter());
					break;
				case ALPHA_NUMERIC_KEY:
					temp.add(new AlphaNumericCharacter());
					break;
				case CHARACTER_KEY:
					temp.add(new CharCharacter());
					break;
				case ANYTHING_KEY:
					temp.add(new MaskCharacter());
					break;
				case HEX_KEY:
					temp.add(new HexCharacter());
					break;
				default:
					temp.add(new LiteralCharacter(maskChar));
					break;
				}
			}
		}
		if (fixed.size() == 0) {
			maskChars = EmptyMaskChars;
		} else {
			maskChars = new MaskCharacter[fixed.size()];
			fixed.toArray(maskChars);
		}
	}

	/**
	 * Returns the MaskCharacter at the specified location.
	 */
	private MaskCharacter getMaskCharacter(int index) {
		if (index >= maskChars.length) {
			return null;
		}
		return maskChars[index];
	}

	/**
	 * Returns true if the placeholder character matches aChar.
	 */
	private boolean isPlaceholder(int index, char aChar) {
		return (getPlaceholderCharacter() == aChar);
	}

	/**
	 * Returns true if the passed in character matches the mask at the specified
	 * location.
	 */
	private boolean isValidCharacter(int index, char aChar) {
		return getMaskCharacter(index).isValidCharacter(aChar);
	}

	/**
	 * Returns true if the character at the specified location is a literal,
	 * that is it can not be edited.
	 */
	private boolean isLiteral(int index) {
		return getMaskCharacter(index).isLiteral();
	}

	/**
	 * Returns the maximum length the text can be.
	 */
	private int getMaxLength() {
		return maskChars.length;
	}

	/**
	 * Returns the literal character at the specified location.
	 */
	/*
	private char getLiteral(int index) {
		return getMaskCharacter(index).getChar((char) 0);
	}
*/
	/**
	 * Returns the character to insert at the specified location based on the
	 * passed in character. This provides a way to map certain sets of
	 * characters to alternative values (lowercase to uppercase...).
	 */
	/*
	private char getCharacter(int index, char aChar) {
		return getMaskCharacter(index).getChar(aChar);
	}
*/
	/**
	 * Removes the literal characters from the passed in string.
	 */
	private String stripLiteralChars(String string) {
		StringBuilder sb = null;
		int last = 0;

		for (int counter = 0, max = string.length(); counter < max; counter++) {
			if (isLiteral(counter)) {
				if (sb == null) {
					sb = new StringBuilder();
					if (counter > 0) {
						sb.append(string.substring(0, counter));
					}
					last = counter + 1;
				} else if (last != counter) {
					sb.append(string.substring(last, counter));
				}
				last = counter + 1;
			}
		}
		if (sb == null) {
			// Assume the mask isn't all literals.
			return string;
		} else if (last != string.length()) {
			sb.append(string.substring(last));
		}
		return sb.toString();
	}

	/**
	 * Returns true if the MaskFormatter allows invalid, or the offset is less
	 * than the max length and the character at <code>offset</code> is a
	 * literal.
	 */
	boolean isNavigatable(int offset) {
		if (!getAllowsInvalid()) {
			return (offset < getMaxLength() && !isLiteral(offset));
		}
		return true;
	}

	/*
	 * Returns true if the operation described by <code>rh</code> will result in
	 * a legal edit. This may set the <code>value</code> field of
	 * <code>rh</code>. <p> This is overriden to return true for a partial
	 * match.
	 * 
	 * boolean isValidEdit(ReplaceHolder rh) { if (!getAllowsInvalid()) { String
	 * newString = getReplaceString(rh.offset, rh.length, rh.text);
	 * 
	 * try { rh.value = stringToValue(newString, false);
	 * 
	 * return true; } catch (ParseException pe) { return false; } } return true;
	 * }
	 */
	/**
	 * This method does the following (assuming !getAllowsInvalid()): iterate
	 * over the max of the deleted region or the text length, for each
	 * character:
	 * <ol>
	 * <li>If it is valid (matches the mask at the particular position, or
	 * matches the literal character at the position), allow it
	 * <li>Else if the position identifies a literal character, add it. This
	 * allows for the user to paste in text that may/may not contain the
	 * literals. For example, in pasing in 5551212 into ###-#### when the 1 is
	 * evaluated it is illegal (by the first test), but there is a literal at
	 * this position (-), so it is used. NOTE: This has a problem that you can't
	 * tell (without looking ahead) if you should eat literals in the text. For
	 * example, if you paste '555' into #5##, should it result in '5555' or '555
	 * '? The current code will result in the latter, which feels a little
	 * better as selecting text than pasting will always result in the same
	 * thing.
	 * <li>Else if at the end of the inserted text, the replace the item with
	 * the placeholder
	 * <li>Otherwise the insert is bogus and false is returned.
	 * </ol>
	 * 
	 * boolean canReplace(ReplaceHolder rh) { // This method is rather long, but
	 * much of the burden is in // maintaining a String and swapping to a
	 * StringBuilder only if // absolutely necessary. if (!getAllowsInvalid()) {
	 * StringBuilder replace = null; String text = rh.text; int tl = (text !=
	 * null) ? text.length() : 0;
	 * 
	 * if (tl == 0 && rh.length == 1 &&
	 * getFormattedTextField().getSelectionStart() != rh.offset) { // Backspace,
	 * adjust to actually delete next non-literal. while (rh.offset > 0 &&
	 * isLiteral(rh.offset)) { rh.offset--; } } int max =
	 * Math.min(getMaxLength() - rh.offset, Math.max(tl, rh.length)); for (int
	 * counter = 0, textIndex = 0; counter < max; counter++) { if (textIndex <
	 * tl && isValidCharacter(rh.offset + counter, text.charAt(textIndex))) {
	 * char aChar = text.charAt(textIndex); if (aChar != getCharacter(rh.offset
	 * + counter, aChar)) { if (replace == null) { replace = new
	 * StringBuilder(); if (textIndex > 0) { replace.append(text.substring(0,
	 * textIndex)); } } } if (replace != null) {
	 * replace.append(getCharacter(rh.offset + counter, aChar)); } textIndex++;
	 * } else if (isLiteral(rh.offset + counter)) { if (replace != null) {
	 * replace.append(getLiteral(rh.offset + counter)); if (textIndex < tl) {
	 * max = Math.min(max + 1, getMaxLength() - rh.offset); } } else if
	 * (textIndex > 0) { replace = new StringBuilder(max);
	 * replace.append(text.substring(0, textIndex));
	 * replace.append(getLiteral(rh.offset + counter)); if (textIndex < tl) { //
	 * Evaluate the character in text again. max = Math.min(max + 1,
	 * getMaxLength() - rh.offset); } else if (rh.cursorPosition == -1) {
	 * rh.cursorPosition = rh.offset + counter; } } else { rh.offset++;
	 * rh.length--; counter--; max--; } } else if (textIndex >= tl) { //
	 * placeholder if (replace == null) { replace = new StringBuilder(); if
	 * (text != null) { replace.append(text); } }
	 * replace.append(getPlaceholderCharacter()); if (tl > 0 &&
	 * rh.cursorPosition == -1) { rh.cursorPosition = rh.offset + counter; } }
	 * else { // Bogus character. return false; } } if (replace != null) {
	 * rh.text = replace.toString(); } else if (text != null && rh.offset + tl >
	 * getMaxLength()) { rh.text = text.substring(0, getMaxLength() -
	 * rh.offset); } if (getOverwriteMode() && rh.text != null) { rh.length =
	 * rh.text.length(); } } return super.canReplace(rh); }
	 */
	//
	// Interal classes used to represent the mask.
	//
	private class MaskCharacter {
		/**
		 * Subclasses should override this returning true if the instance
		 * represents a literal character. The default implementation returns
		 * false.
		 */
		public boolean isLiteral() {
			return false;
		}

		/**
		 * Returns true if <code>aChar</code> is a valid reprensentation of the
		 * receiver. The default implementation returns true if the receiver
		 * represents a literal character and <code>getChar</code> == aChar.
		 * Otherwise, this will return true is <code>aChar</code> is contained
		 * in the valid characters and not contained in the invalid characters.
		 */
		public boolean isValidCharacter(char aChar) {
			if (isLiteral()) {
				return (getChar(aChar) == aChar);
			}

			aChar = getChar(aChar);

			String filter = getValidCharacters();

			if (filter != null && filter.indexOf(aChar) == -1) {
				return false;
			}
			filter = getInvalidCharacters();
			if (filter != null && filter.indexOf(aChar) != -1) {
				return false;
			}
			return true;
		}

		/**
		 * Returns the character to insert for <code>aChar</code>. The default
		 * implementation returns <code>aChar</code>. Subclasses that wish to do
		 * some sort of mapping, perhaps lower case to upper case should
		 * override this and do the necessary mapping.
		 */
		public char getChar(char aChar) {
			return aChar;
		}

		/**
		 * Appends the necessary character in <code>formatting</code> at
		 * <code>index</code> to <code>buff</code>.
		 */
		public void append(StringBuilder buff, String formatting, int[] index, String placeholder) throws ParseException {
			boolean inString = index[0] < formatting.length();
			char aChar = inString ? formatting.charAt(index[0]) : 0;

			if (isLiteral()) {
				buff.append(getChar(aChar));
				if (getValueContainsLiteralCharacters()) {
					if (inString && aChar != getChar(aChar)) {
						throw new ParseException("Invalid character: " + aChar, index[0]);
					}
					index[0] = index[0] + 1;
				}
			} else if (index[0] >= formatting.length()) {
				if (placeholder != null && index[0] < placeholder.length()) {
					buff.append(placeholder.charAt(index[0]));
				} else {
					buff.append(getPlaceholderCharacter());
				}
				index[0] = index[0] + 1;
			} else if (isValidCharacter(aChar)) {
				buff.append(getChar(aChar));
				index[0] = index[0] + 1;
			} else {
				throw new ParseException("Invalid character: " + aChar, index[0]);
			}
		}
	}

	/**
	 * Used to represent a fixed character in the mask.
	 */
	private class LiteralCharacter extends MaskCharacter {
		private char fixedChar;

		public LiteralCharacter(char fixedChar) {
			this.fixedChar = fixedChar;
		}

		public boolean isLiteral() {
			return true;
		}

		public char getChar(char aChar) {
			return fixedChar;
		}
	}

	/**
	 * Represents a number, uses <code>Character.isDigit</code>.
	 */
	private class DigitMaskCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return (Character.isDigit(aChar) && super.isValidCharacter(aChar));
		}
	}

	/**
	 * Represents a character, lower case letters are mapped to upper case using
	 * <code>Character.toUpperCase</code>.
	 */
	private class UpperCaseCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return (Character.isLetter(aChar) && super.isValidCharacter(aChar));
		}

		public char getChar(char aChar) {
			return Character.toUpperCase(aChar);
		}
	}

	/**
	 * Represents a character, upper case letters are mapped to lower case using
	 * <code>Character.toLowerCase</code>.
	 */
	private class LowerCaseCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return (Character.isLetter(aChar) && super.isValidCharacter(aChar));
		}

		public char getChar(char aChar) {
			return Character.toLowerCase(aChar);
		}
	}

	/**
	 * Represents either a character or digit, uses
	 * <code>Character.isLetterOrDigit</code>.
	 */
	private class AlphaNumericCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return (Character.isLetterOrDigit(aChar) && super.isValidCharacter(aChar));
		}
	}

	/**
	 * Represents a letter, uses <code>Character.isLetter</code>.
	 */
	private class CharCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return (Character.isLetter(aChar) && super.isValidCharacter(aChar));
		}
	}

	/**
	 * Represents a hex character, 0-9a-fA-F. a-f is mapped to A-F
	 */
	private class HexCharacter extends MaskCharacter {
		public boolean isValidCharacter(char aChar) {
			return ((aChar == '0' || aChar == '1' || aChar == '2' || aChar == '3' || aChar == '4' || aChar == '5' || aChar == '6' || aChar == '7' || aChar == '8' || aChar == '9' || aChar == 'a'
			        || aChar == 'A' || aChar == 'b' || aChar == 'B' || aChar == 'c' || aChar == 'C' || aChar == 'd' || aChar == 'D' || aChar == 'e' || aChar == 'E' || aChar == 'f' || aChar == 'F') && super
			            .isValidCharacter(aChar));
		}

		public char getChar(char aChar) {
			if (Character.isDigit(aChar)) {
				return aChar;
			}
			return Character.toUpperCase(aChar);
		}
	}
}
