package com.quickscan.proto;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        Controller ctr = new Controller();
        
        JFrame win = new JFrame();
        VectorPlotter plot = new VectorPlotter(2);
        win.setContentPane(plot);
        win.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        win.setLocation(screen.width/2-win.getWidth()/2, screen.height/2-win.getHeight()/2);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setVisible(true);
        
        Frame frame;
        HandList hands;
        while(win.isVisible())
        {
            frame = ctr.frame();
            if(frame.isValid())
            {
                hands = frame.hands();
                if(hands.count() > 0)
                {
                    plot.plotHand(hands);
                    plot.setMessage("");
                }
                else plot.setMessage("No Hands");
            }
            else plot.setMessage("Invalid Frame");
            win.repaint();
            long wait = System.currentTimeMillis()+50;
            while(System.currentTimeMillis() < wait);
        }
    }
}