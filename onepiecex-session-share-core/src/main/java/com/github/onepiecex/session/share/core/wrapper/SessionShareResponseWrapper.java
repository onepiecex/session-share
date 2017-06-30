package com.github.onepiecex.session.share.core.wrapper;

import com.github.onepiecex.session.share.core.HttpSessionImpl;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by wangziqing on 17/6/23.
 */
public class SessionShareResponseWrapper extends HttpServletResponseWrapper {

    private final SessionShareRequestWrapper sessionShareRequestWrapper;

    public SessionShareResponseWrapper(ServletResponse response, SessionShareRequestWrapper sessionShareRequestWrapper) {
        super((HttpServletResponse) response);
        this.sessionShareRequestWrapper = sessionShareRequestWrapper;
    }

    private void saveSession() {
        HttpSession httpSession;
        if (null != (httpSession = sessionShareRequestWrapper.getSession(false))) {
            ((HttpSessionImpl) httpSession).save(this);
        }
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        saveSession();
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        saveSession();
        super.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        saveSession();
        super.sendRedirect(location);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new SaveContextServletOutputStream(super.getOutputStream());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new SaveContextPrintWriter(super.getWriter());
    }

    @Override
    public void flushBuffer() throws IOException {
        saveSession();
        super.flushBuffer();
    }

    private class SaveContextPrintWriter extends PrintWriter {
        private final PrintWriter delegate;

        SaveContextPrintWriter(PrintWriter delegate) {
            super(delegate);
            this.delegate = delegate;
        }

        @Override
        public void flush() {
            saveSession();
            delegate.flush();
        }

        @Override
        public void close() {
            saveSession();
            delegate.close();
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        public boolean checkError() {
            return delegate.checkError();
        }


        @Override
        public void write(int c) {
            delegate.write(c);
        }

        @Override
        public void write(char[] buf, int off, int len) {
            delegate.write(buf, off, len);
        }

        @Override
        public void write(char[] buf) {
            delegate.write(buf);
        }

        @Override
        public void write(String s, int off, int len) {
            delegate.write(s, off, len);
        }

        @Override
        public void write(String s) {
            delegate.write(s);
        }

        @Override
        public void print(boolean b) {
            delegate.print(b);
        }

        @Override
        public void print(char c) {
            delegate.print(c);
        }

        @Override
        public void print(int i) {
            delegate.print(i);
        }

        @Override
        public void print(long l) {
            delegate.print(l);
        }

        @Override
        public void print(float f) {
            delegate.print(f);
        }

        @Override
        public void print(double d) {
            delegate.print(d);
        }

        @Override
        public void print(char[] s) {
            delegate.print(s);
        }

        @Override
        public void print(String s) {
            delegate.print(s);
        }

        @Override
        public void print(Object obj) {
            delegate.print(obj);
        }

        @Override
        public void println() {
            delegate.println();
        }

        @Override
        public void println(boolean x) {
            delegate.println(x);
        }

        @Override
        public void println(char x) {
            delegate.println(x);
        }

        @Override
        public void println(int x) {
            delegate.println(x);
        }

        @Override
        public void println(long x) {
            delegate.println(x);
        }

        @Override
        public void println(float x) {
            delegate.println(x);
        }

        @Override
        public void println(double x) {
            delegate.println(x);
        }

        @Override
        public void println(char[] x) {
            delegate.println(x);
        }

        @Override
        public void println(String x) {
            delegate.println(x);
        }

        @Override
        public void println(Object x) {
            delegate.println(x);
        }

        @Override
        public PrintWriter printf(String format, Object... args) {
            return delegate.printf(format, args);
        }

        @Override
        public PrintWriter printf(Locale l, String format, Object... args) {
            return delegate.printf(l, format, args);
        }

        @Override
        public PrintWriter format(String format, Object... args) {
            return delegate.format(format, args);
        }

        @Override
        public PrintWriter format(Locale l, String format, Object... args) {
            return delegate.format(l, format, args);
        }

        @Override
        public PrintWriter append(CharSequence csq) {
            return delegate.append(csq);
        }

        @Override
        public PrintWriter append(CharSequence csq, int start, int end) {
            return delegate.append(csq, start, end);
        }

        @Override
        public PrintWriter append(char c) {
            return delegate.append(c);
        }

        public String toString() {
            return getClass().getName() + "[delegate=" + delegate.toString() + "]";
        }
    }

    private class SaveContextServletOutputStream extends ServletOutputStream {
        private final ServletOutputStream delegate;
        SaveContextServletOutputStream(ServletOutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void flush() throws IOException {
            saveSession();
            delegate.flush();
        }

        @Override
        public void close() throws IOException {
            saveSession();
            delegate.close();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            delegate.setWriteListener(writeListener);
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
        }

        @Override
        public void print(String s) throws IOException {
            delegate.print(s);
        }

        @Override
        public void print(boolean b) throws IOException {
            delegate.print(b);
        }

        @Override
        public void print(char c) throws IOException {
            delegate.print(c);
        }

        @Override
        public void print(int i) throws IOException {
            delegate.print(i);
        }

        @Override
        public void print(long l) throws IOException {
            delegate.print(l);
        }

        @Override
        public void print(float f) throws IOException {
            delegate.print(f);
        }

        @Override
        public void print(double d) throws IOException {
            delegate.print(d);
        }

        @Override
        public void println() throws IOException {
            delegate.println();
        }

        @Override
        public void println(String s) throws IOException {
            delegate.println(s);
        }

        @Override
        public void println(boolean b) throws IOException {
            delegate.println(b);
        }

        @Override
        public void println(char c) throws IOException {
            delegate.println(c);
        }

        @Override
        public void println(int i) throws IOException {
            delegate.println(i);
        }

        @Override
        public void println(long l) throws IOException {
            delegate.println(l);
        }

        @Override
        public void println(float f) throws IOException {
            super.println(f);
        }

        @Override
        public void println(double d) throws IOException {
            delegate.println(d);
        }

        @Override
        public void write(byte[] b) throws IOException {
            delegate.write(b);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            delegate.write(b, off, len);
        }

        public String toString() {
            return getClass().getName() + "[delegate=" + delegate.toString() + "]";
        }
    }
}
