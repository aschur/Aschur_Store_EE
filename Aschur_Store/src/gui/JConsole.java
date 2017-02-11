package gui;

/**
 * Created by Aschur on 29.12.2016.
 */
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class JConsole extends JTextPane {

    class ConsoleOutputStream extends OutputStream {

        /** Устанавливает цвет вывода содержимого потока */
        public void setColor(Color color) {
            this.color = color;
        }

        /** Возвращает цвет вывода содержимого потока */
        public Color getColor() {
            return color;
        }

        @Override
        public void write(int b) throws IOException {
            old.write(b);
            insertText("" + (char) b, color);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            old.write(b, off, len);
            insertText(new String(b, off, len), color);
        }

        @Override
        public void write(byte[] b) throws IOException {
            old.write(b);
            insertText(new String(b), color);
        }

        /**
         * Конструктор класса
         *
         * @param old -
         *            заменяемый поток.
         * @param color -
         *            цвет, которым будет выводиться содержимое потока.
         */
        public ConsoleOutputStream(PrintStream old, Color color) {
            if (old == null) {
                throw new NullPointerException(
                        "Ссылка на заменяемый поток не указана");
            }
            if (color == null) {
                throw new NullPointerException("Ссылка на цвет не указана");
            }
            this.color = color;
            this.old = old;
        }

        private Color color;

        private PrintStream old;
    }

    /** Устанавливает цвет вывода содержимого потока out */
    public void setColorOut(Color colorOut) {
        this.out.setColor(colorOut);
    }

    /** Возвращает цвет вывода содержимого потока out */
    public Color getColorOut() {
        return out.getColor();
    }

    /** Устанавливает цвет вывода содержимого потока err */
    public void setColorErr(Color colorErr) {
        this.err.setColor(colorErr);
    }

    /** Возвращает цвет вывода содержимого потока err */
    public Color getColorErr() {
        return err.getColor();
    }

    public JConsole() {
        setEditable(false);
        err = new ConsoleOutputStream(System.err, Color.RED);
        out = new ConsoleOutputStream(System.out, Color.BLACK);
        System.setErr(new PrintStream(err));
        System.setOut(new PrintStream(out));
    }

    private void insertText(final String text, final Color color) {
        SimpleAttributeSet att = new SimpleAttributeSet();
        StyleConstants.setForeground(att, color);
        try {
            int offset = getStyledDocument().getLength();
            getStyledDocument().insertString(offset, text, att);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private ConsoleOutputStream err;

    private ConsoleOutputStream out;

    private static final long serialVersionUID = -6128923427636817383L;
}
