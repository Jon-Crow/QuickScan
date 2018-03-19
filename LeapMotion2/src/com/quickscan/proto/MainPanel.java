package com.quickscan.proto;

import com.leapmotion.leap.Finger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import net.miginfocom.swing.MigLayout;

/**
 * @author Jonathan Crow
 */
public class MainPanel extends JPanel
{
    private VectorPlotter vp;
    private JButton freezeBtn, capBtn;
    
    public MainPanel()
    {
        setLayout(new MigLayout());
        add(new JLabel("Finger:"));
        ButtonGroup fingers = new ButtonGroup(), joints = new ButtonGroup();
        for(Finger.Type type : Finger.Type.values())
        {
            JRadioButton btn = new JRadioButton(type.toString().substring(5).toLowerCase());
            btn.addActionListener(new TypeButton(type));
            fingers.add(btn);
            add(btn);
            if(type == Finger.Type.TYPE_INDEX) btn.setSelected(true);
        }
        add(new JLabel("Joint:"), "newline");
        for(JointAngle joint : JointAngle.values())
        {
            JRadioButton btn = new JRadioButton(joint.toString().toLowerCase().replace("_", " "));
            btn.addActionListener(new JointButton(joint));
            joints.add(btn);
            add(btn);
            if(joint == JointAngle.JOINT_1) btn.setSelected(true);
        }
        vp = new VectorPlotter(2);
        add(vp, "newline, span 6, wrap");
        add(freezeBtn = initButton("Freeze Frame"));
        add(capBtn = initButton("Capture Angles"));
    }
    private final JButton initButton(String txt)
    {
        JButton btn = new JButton(txt);
        btn.addActionListener(buttons);
        return btn;
    }
    public VectorPlotter getVectorPlotter()
    {
        return vp;
    }
    private class TypeButton implements ActionListener
    {
        private Finger.Type type;
        
        private TypeButton(Finger.Type type)
        {
            this.type = type;
        }
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            vp.setMeasureType(type);
            vp.clearAngles();
        }
    }
    private class JointButton implements ActionListener
    {
        private JointAngle joint;
        
        private JointButton(JointAngle joint)
        {
            this.joint = joint;
        }
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            vp.setMeasureJoint(joint);
            vp.clearAngles();
        }
    }
    private final ActionListener buttons = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource() == freezeBtn) vp.setFrozen(true);
            else if(event.getSource() == capBtn) vp.capture();
        }
    };
}