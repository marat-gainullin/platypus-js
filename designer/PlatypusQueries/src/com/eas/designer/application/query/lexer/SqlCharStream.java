/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.query.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import net.sf.jsqlparser.parser.SimpleCharStream;
import org.netbeans.spi.lexer.LexerInput;

/**
 *
 * @author mg
 */
public class SqlCharStream extends SimpleCharStream{

    private LexerInput input;

    public SqlCharStream (LexerInput aInput) {
        super((Reader)null);
        input = aInput;
    }

    SqlCharStream (Reader stream, int i, int i0) {
        super((Reader)null);
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    SqlCharStream (InputStream stream, String encoding, int i, int i0) throws UnsupportedEncodingException {
        super((Reader)null);
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    @Override
    public char BeginToken () throws IOException {
        char result = (char)input.read();
        if (result == 0xFFFF) {
            throw new IOException("LexerInput EOF");
        }
        return result;
    }

    @Override
    public String GetImage () {
        return input.readText().toString();
    }

    @Override
     public char[] GetSuffix (int len) {
        if (len > input.readLength ())
            throw new IllegalArgumentException ();
        return input.readText (input.readLength () - len, input.readLength ()).toString ().toCharArray ();
     }

    @Override
    public void ReInit (Reader stream, int i, int i0) {
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    @Override
    public void ReInit (InputStream stream, String encoding, int i, int i0) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    @Override
    public void backup (int i) {
        input.backup (i);
    }

    @Override
    public int getBeginColumn () {
        return 0;
    }

    @Override
    public int getBeginLine () {
        return 0;
    }

    @Override
    public int getEndColumn () {
        return 0;
    }

    @Override
    public int getEndLine () {
        return 0;
    }

    @Override
    public char readChar () throws IOException {
        char result = (char)input.read();
        if (result == 0xFFFF) {
            throw new IOException("LexerInput EOF");
        }
        return result;
    }
}