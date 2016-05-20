package the.bytecode.club.bytecodeviewer.api;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Resources;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A simple console GUI.
 *
 * @author Konloch
 */

public class PluginConsole extends JFrame
{

    JTextArea textArea = new JTextArea();
    JPanel panel = new JPanel(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane();
    public JCheckBox check = new JCheckBox("Exact");
    final JTextField field = new JTextField();

    public PluginConsole(String pluginName)
    {
        this.setIconImages(Resources.iconList);
        setTitle("Java DisAssembler - Plugin Console - " + pluginName);
        setSize(new Dimension(542, 316));

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(textArea);
        textArea.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                {
                    field.requestFocus();
                }

                BytecodeViewer.checkHotKey(e);
            }

            @Override
            public void keyReleased(KeyEvent arg0)
            {
            }

            @Override
            public void keyTyped(KeyEvent arg0)
            {
            }
        });

        JButton searchNext = new JButton();
        JButton searchPrev = new JButton();
        JPanel buttonPane = new JPanel(new BorderLayout());
        buttonPane.add(searchNext, BorderLayout.WEST);
        buttonPane.add(searchPrev, BorderLayout.EAST);
        searchNext.setIcon(Resources.nextIcon);
        searchPrev.setIcon(Resources.prevIcon);
        panel.add(buttonPane, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(check, BorderLayout.EAST);
        searchNext.addActionListener(arg0 -> search(field.getText(), true));
        searchPrev.addActionListener(arg0 -> search(field.getText(), false));
        field.addKeyListener(new KeyListener()
        {
            @Override
            public void keyReleased(KeyEvent arg0)
            {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
                    search(field.getText(), true);
            }

            @Override
            public void keyPressed(KeyEvent arg0)
            {
            }

            @Override
            public void keyTyped(KeyEvent arg0)
            {
            }
        });
        scrollPane.setColumnHeaderView(panel);
        this.setLocationRelativeTo(null);
    }

    /**
     * This was really interesting to write.
     *
     * @author Konloch
     */
    public void search(String search, boolean next)
    {
        try
        {
            JTextArea area = textArea;
            if (search.isEmpty())
            {
                highlight(area, "");
                return;
            }

            int startLine = area.getDocument().getDefaultRootElement().getElementIndex(area.getCaretPosition()) + 1;
            int currentLine = 1;
            boolean canSearch = false;
            String[] test = null;
            if (area.getText().split("\n").length >= 2)
                test = area.getText().split("\n");
            else
                test = area.getText().split("\r");
            int lastGoodLine = -1;
            int firstPos = -1;
            boolean found = false;

            if (next)
            {
                for (String s : test)
                {
                    if (!check.isSelected())
                    {
                        s = s.toLowerCase();
                        search = search.toLowerCase();
                    }

                    if (currentLine == startLine)
                    {
                        canSearch = true;
                    }
                    else if (s.contains(search))
                    {
                        if (canSearch)
                        {
                            area.setCaretPosition(area.getDocument().getDefaultRootElement().getElement(currentLine - 1).getStartOffset());
                            canSearch = false;
                            found = true;
                        }

                        if (firstPos == -1)
                            firstPos = currentLine;
                    }

                    currentLine++;
                }

                if (!found && firstPos != -1)
                {
                    area.setCaretPosition(area.getDocument().getDefaultRootElement().getElement(firstPos - 1).getStartOffset());
                }
            }
            else
            {
                canSearch = true;
                for (String s : test)
                {
                    if (!check.isSelected())
                    {
                        s = s.toLowerCase();
                        search = search.toLowerCase();
                    }

                    if (s.contains(search))
                    {
                        if (lastGoodLine != -1 && canSearch)
                            area.setCaretPosition(area.getDocument().getDefaultRootElement().getElement(lastGoodLine - 1).getStartOffset());

                        lastGoodLine = currentLine;

                        if (currentLine >= startLine)
                            canSearch = false;
                    }
                    currentLine++;
                }

                if (lastGoodLine != -1 && area.getDocument().getDefaultRootElement().getElementIndex(area.getCaretPosition()) + 1 == startLine)
                {
                    area.setCaretPosition(area.getDocument().getDefaultRootElement().getElement(lastGoodLine - 1).getStartOffset());
                }
            }
            highlight(area, search);
        }
        catch (Exception e)
        {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    private DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 62, 150));

    public void highlight(JTextComponent textComp, String pattern)
    {
        if (pattern.isEmpty())
        {
            textComp.getHighlighter().removeAllHighlights();
            return;
        }

        try
        {
            Highlighter hilite = textComp.getHighlighter();
            hilite.removeAllHighlights();
            javax.swing.text.Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            if (!check.isSelected())
            {
                pattern = pattern.toLowerCase();
                text = text.toLowerCase();
            }

            // Search for pattern
            while ((pos = text.indexOf(pattern, pos)) >= 0)
            {
                // Create highlighter using private painter and apply around
                // pattern
                hilite.addHighlight(pos, pos + pattern.length(), painter);
                pos += pattern.length();
            }
        }
        catch (Exception e)
        {
            new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
        }
    }

    /**
     * Appends \r\n to the end of your string, then it puts it on the top.
     *
     * @param t the string you want to append
     */
    public void appendText(String t)
    {
        textArea.append(t + "\r\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    /**
     * Sets the text
     *
     * @param t the text you want set
     */
    public void setText(String t)
    {
        textArea.setText(t);
        textArea.setCaretPosition(0);
    }

    private static final long serialVersionUID = -6556940545421437508L;

}
