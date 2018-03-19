package com.quickscan.proto;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * @author Jonathan Crow
 */
public class UI
{
    //The main frame of the program which displays the hands and angles
    private static JFrame mainFrame;
    //The instance of the currently displayed MainPanel
    private static MainPanel mainPanel;
    //A list of sub frames opened by the program which will all be closed
    //when the main frame is closed
    private static ArrayList<JFrame> subFrames;
    
    /**
     * Initializes and opens the main frame of the program
     */
    public static void openMainFrame()
    {
        mainFrame = new JFrame();
        mainPanel = new MainPanel();
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation(screen.width/2-mainFrame.getWidth()/2, screen.height/2-mainFrame.getHeight()/2);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.addWindowListener(subListen);
        mainFrame.setVisible(true);
    }
    /**
     * @return the currently visible main frame
     */
    public static MainPanel getMainPanel()
    {
        return mainPanel;
    }
    /**
     * @return true if the main frame is currently visible
     */
    public static boolean isOpen()
    {
        return mainFrame != null && mainFrame.isVisible();
    }
    /**
     * Provides a way to update the display
     */
    public static void repaint()
    {
        mainFrame.repaint();
    }
    /**
     * Opens a subframe that will be disposed when the main frame is closed
     * @param con the content pane to be displayed by the frame
     * @return the subframe that has been opened
     */
    public static JFrame openSubFrame(Container con)
    {
        if(mainFrame == null || !mainFrame.isVisible()) return null;
        if(subFrames == null) subFrames = new ArrayList<>();
        JFrame sub = new JFrame();
        sub.setContentPane(con);
        sub.pack();
        sub.setLocationRelativeTo(mainFrame);
        sub.setLocation(0, 0);
        sub.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sub.setVisible(true);
        subFrames.add(sub);
        return sub;
    }
    
    //Handles the closing of he subframes.
    private static final WindowAdapter subListen = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent event)
        {
            if(subFrames != null)
            {
                for(JFrame sub : subFrames)
                    sub.dispose();
            }
        }
    };
}