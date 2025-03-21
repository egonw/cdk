/* Copyright (C) 2002  Antti S. Brax
 *
 *  FormatStringBuilder: printf style output formatter for Java
 *  All rights reserved.
 *
 *  Downloaded from: http://www.cs.helsinki.fi/u/abrax/HACK/JAVA/PRINTF.html
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  - The names of the contributors may not be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openscience.cdk.io;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A class for formatting output similar to the C <code>printf</code> command.
 *
 * <P>Some features provided by ANSI C-standard conformant <code>printfs</code>
 * are not supported because of language constraints.
 *
 * <P>Supported conversion specifiers are: 'c', 'd', 'e', 'E', 'f', 'g'
 * (works like 'f'), 'i', 'o', 's', 'x' and 'X'.
 *
 * <P>Supported conversion flags are: '#', '0', '-', ' ' (a space) and '+'.
 *
 * <P>Support for conversion flag '*' is under development.
 *
 * @author      Antti S. Brax (asb@iki.fi, base implementation)
 * @author      Fred Long (flong(AT)skcc.org, implemented 'e', 'E' and 'g')
 * @version     1.7
 * @cdk.license BSD
 */
class FormatStringBuilder {

    // ==================================================================== //

    /** Pad with zero instead of space. */
    private static final int ZEROPAD  = 1;

    /** Unsigned/signed long. */
    //private static final int SIGN = 2;

    /** Show plus sign. */
    private static final int PLUS     = 4;

    /** Space if plus. */
    private static final int SPACE    = 8;

    /** Left justified. */
    private static final int LEFT     = 16;

    /** Prepend hex digits with '0x' and octal with '0' */
    private static final int SPECIAL  = 32;

    /** Use upper case hex digits. */
    private static final int LARGE    = 64;

    /** Use scientific notation */
    private static final int SCI      = 128;

    /** Use upper case E */
    private static final int UPPER    = 256;

    /** Use grouping character */
    private static final int GROUPING = 512;

    // ==================================================================== //

    /** Format a char. */
    private static final int CHAR     = 0;

    /** Format a String. */
    private static final int STRING   = 1;

    /** Format a decimal number. */
    private static final int DECIMAL  = 2;

    /** Format a floating point number. */
    private static final int FLOAT    = 3;

    // ==================================================================== //

    /** The format string. */
    private String format = null;

    /** The builder. */
    private StringBuilder stringBuilder = null;

    /** The current index. */
    private int index = 0;

    // ==================================================================== //

    /**
     * Create a new <code>FormatStringBuilder</code>.
     *
     * @param format the format string.
     */
    FormatStringBuilder(String format) {
        reset(format);
    }

    /**
     * Reset this <code>FormatStringBuilder</code>.
     *
     * @param format the format string.
     */
    FormatStringBuilder reset(String format) {
        reset();
        this.format = format;
        return this;
    }

    /**
     * Reset this <code>FormatStringBuilder</code> with the format string
     * given in the constructor or last call to <code>reset(String)</code>.
     * This is automatically called after <code>toString()</code>.
     */
    FormatStringBuilder reset() {
        this.stringBuilder = new StringBuilder();
        this.index = 0;
        return this;
    }

    // ==================================================================== //

    /**
     * Get the next format token from the format string. Copy every
     * character from <code>format</code> to <tt>buffer</tt> between
     * <code>index</code> and the next format token.
     */
    private Format getFormat() {

        char ch;

        while (index < format.length()) {
            if ((ch = format.charAt(index)) != '%') {
                stringBuilder.append(ch);
                index++;
                continue;
            }

            Format fmt = new Format();

            // Process flags.
            boolean repeat = true;
            while (repeat) {

                if (index + 1 >= format.length()) throw new IllegalArgumentException("Malformed format");

                switch (ch = format.charAt(++index)) { // Skip the first '%'
                    case '-':
                        fmt.flags |= LEFT;
                        break;
                    case '+':
                        fmt.flags |= PLUS;
                        break;
                    case ' ':
                        fmt.flags |= SPACE;
                        break;
                    case '#':
                        fmt.flags |= SPECIAL;
                        break;
                    case '0':
                        fmt.flags |= ZEROPAD;
                        break;
                    case '\'':
                        fmt.flags |= GROUPING;
                        break;
                    default:
                        repeat = false;
                        break;
                }
            }

            // Get field width.
            if (isDigit(ch)) {
                // Explicit number.
                fmt.fieldWidth = skipDigits();
            }

            if (index >= format.length()) throw new IllegalArgumentException("Malformed format");

            // Get precision.
            if (format.charAt(index) == '.') {

                if (++index >= format.length()) throw new IllegalArgumentException("Malformed format");

                fmt.precision = skipDigits();
                if (fmt.precision < 0) {
                    fmt.precision = 0;
                }
            }

            if (index >= format.length()) throw new IllegalArgumentException("Malformed format");

            switch (ch = format.charAt(index++)) {
                case 'c':
                    fmt.type = CHAR;
                    return fmt;
                case 's':
                    fmt.type = STRING;
                    return fmt;
                case '%':
                    stringBuilder.append('%');
                    continue;

                    // Octal, hexadecimal and decimal.

                case 'o':
                    fmt.type = DECIMAL;
                    fmt.base = 8;
                    return fmt;
                case 'X':
                    fmt.flags |= LARGE;
                case 'x':
                    fmt.type = DECIMAL;
                    fmt.base = 16;
                    return fmt;
                case 'd':
                case 'i':
                    fmt.type = DECIMAL;
                    return fmt;

                    // Floating point

                case 'f':
                case 'g':
                    fmt.type = FLOAT;
                    return fmt;
                case 'e':
                    fmt.type = FLOAT;
                    fmt.flags |= SCI;
                    return fmt;
                case 'E':
                    fmt.type = FLOAT;
                    fmt.flags |= SCI;
                    fmt.flags |= UPPER;
                    return fmt;
                default:
                    stringBuilder.append('%');
                    stringBuilder.append(ch);
            }
        }

        return null;
    }

    /**
     * Checks if a given character is an ASCII digit. Do NOT replace
     * with Character.isDigit() which check the entire Unicode table/code
     * spaces.
     *
     * @param ch the character to check
     * @return true if the character is a digit, false otherwise
     */
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Skip digits and return the number they form.
     */
    private int skipDigits() {
        char ch;
        int i = 0;

        while (index < format.length()) {
            if (isDigit(ch = format.charAt(index))) {
                index++;
                i = i * 10 + (ch - '0');
            } else {
                break;
            }
        }
        return i;
    }

    // ==================================================================== //

    /**
     * Format a <code>char</code>.
     */
    FormatStringBuilder format(char ch) {

        Format fmt = getFormat();

        if (fmt.type != CHAR)
            throw new IllegalArgumentException("Expected a char format");

        if ((fmt.flags & LEFT) != LEFT) while (--fmt.fieldWidth > 0)
            stringBuilder.append(' ');
        stringBuilder.append(ch);
        while (--fmt.fieldWidth > 0)
            stringBuilder.append(' ');

        return this;
    }

    /**
     * Format a <code>float</code>.
     */
    FormatStringBuilder format(float flt) {

        return format((double) flt);

    }

    /**
     * Format a <code>double</code>.
     */
    FormatStringBuilder format(double dbl) {

        Format fmt = getFormat();

        if (fmt.type != FLOAT) throw new IllegalArgumentException("Expected a float format");

        NumberFormat nf;
        if ((fmt.flags & SCI) > 0) {
            nf = new DecimalFormat("0.#E00", DecimalFormatSymbols.getInstance(Locale.ROOT));
        } else {
            nf = NumberFormat.getInstance(Locale.ROOT);
        }
        nf.setGroupingUsed((fmt.flags & GROUPING) != 0);
        if (fmt.precision != -1) {
            nf.setMaximumFractionDigits(fmt.precision);
            nf.setMinimumFractionDigits(fmt.precision);
        } else {
            nf.setMaximumFractionDigits(Integer.MAX_VALUE);
            nf.setMinimumFractionDigits(1);
        }
        String str = nf.format(dbl);
        if ((fmt.flags & SCI) == SCI && (fmt.flags & UPPER) == 0) {
            str = str.replace('E', 'e');
        }
        if ((fmt.flags & PLUS) == PLUS && dbl >= 0.0) str = "+" + str;

        int len = str.length();
        if ((fmt.flags & LEFT) != LEFT) while (len < fmt.fieldWidth--)
            stringBuilder.append(' ');

        for (int i = 0; i < len; ++i)
            stringBuilder.append(str.charAt(i));

        while (len < fmt.fieldWidth--)
            stringBuilder.append(' ');

        return this;
    }

    /**
     * Format a <code>float</code>.
     */
    FormatStringBuilder format(int i) {

        return format((long) i);

    }

    /**
     * Format a <code>float</code>.
     */
    FormatStringBuilder format(long l) {

        Format fmt = getFormat();

        if (fmt.type != DECIMAL) throw new IllegalArgumentException("Expected a float format");

        // Decide padding character.
        char pad = ' ';
        if ((fmt.flags & ZEROPAD) == ZEROPAD) {
            pad = '0';
        }

        // Convert number to String.
        String str;
        String prefix = "";
        switch (fmt.base) {
            case 8:
                str = Long.toOctalString(l);
                if ((fmt.flags & SPECIAL) == SPECIAL) {
                    fmt.fieldWidth -= 1;
                    prefix = "0";
                }
                break;
            case 16:
                str = Long.toHexString(l);
                if ((fmt.flags & SPECIAL) == SPECIAL) {
                    fmt.fieldWidth -= 2;
                    prefix = "0x";
                }
                break;
            default:
                str = String.valueOf(Math.abs(l));
                break;
        }

        if ((fmt.flags & LARGE) == LARGE) {
            str = str.toUpperCase();
            prefix = prefix.toUpperCase();
        }

        int len = str.length();

        if (l < 0 || (fmt.flags & PLUS) == PLUS) {
            fmt.fieldWidth--;
        }

        // Place the sign character first if zero padding.
        if ((fmt.flags & ZEROPAD) == ZEROPAD) {
            if (l < 0 && fmt.base == 10) {
                stringBuilder.append('-');
            } else if ((fmt.flags & PLUS) == PLUS && fmt.base == 10) {
                stringBuilder.append('+');
            }
            stringBuilder.append(prefix);
        }

        // Pad.
        if ((fmt.flags & LEFT) != LEFT) while (len < fmt.fieldWidth--)
            stringBuilder.append(pad);

        // Place the sign character now if not zero padding.
        if ((fmt.flags & ZEROPAD) != ZEROPAD) {
            if (l < 0 && fmt.base == 10) {
                stringBuilder.append('-');
            } else if ((fmt.flags & PLUS) == PLUS && fmt.base == 10) {
                stringBuilder.append('+');
            }
            stringBuilder.append(prefix);
        }

        for (int i = 0; i < len; ++i)
            stringBuilder.append(str.charAt(i));

        while (len < fmt.fieldWidth--)
            stringBuilder.append(' ');

        return this;
    }

    /**
     * Format a <code>String</code>.
     */
    FormatStringBuilder format(String str) {

        if (str == null) str = "<NULL>";

        Format fmt = getFormat();

        if (fmt.type != STRING) throw new IllegalArgumentException("Expected a String format");

        int len = str.length();
        if (fmt.precision != -1 && len > fmt.precision) len = fmt.precision;

        if ((fmt.flags & LEFT) != LEFT) while (len < fmt.fieldWidth--)
            stringBuilder.append(' ');

        for (int i = 0; i < len; ++i)
            stringBuilder.append(str.charAt(i));

        while (len < fmt.fieldWidth--)
            stringBuilder.append(' ');

        return this;
    }

    // ==================================================================== //

    /**
     * Get the result of the formatting. <code>reset()</code> is automatically
     * called from this method.
     */
    @Override
    public String toString() {

        if (index < format.length()) stringBuilder.append(format.substring(index));

        String str = stringBuilder.toString();
        this.reset();

        return str;
    }

    // ==================================================================== //

    /**
     * A container class for several format parameters.
     */
    private static class Format {
        public int flags      = 0;
        public int fieldWidth = -1;
        public int precision  = -1;
        public int type       = -1;
        public int base       = 10;
    }
}
