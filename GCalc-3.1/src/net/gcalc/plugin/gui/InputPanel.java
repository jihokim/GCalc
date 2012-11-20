package net.gcalc.plugin.gui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

public class InputPanel extends JPanel {
	 private Set actionListeners = Collections.synchronizedSet(new HashSet());
	 
	 public InputPanel() {
		 this(new BorderLayout());
	 }
	 
	 InputPanel(LayoutManager n) {
		 super(n);
	 }
	 
	 protected void fireActionEvent() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				null);

		Object[] obj = actionListeners.toArray();

		for (int i = 0; i < obj.length; i++)
			((ActionListener) obj[i]).actionPerformed(event);
	}

	public void addActionListener(ActionListener l) {

		actionListeners.add(l);
	}

	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}
		
	public String[] getValues()
	{
		return null;
	}
	
	public void addCurrentValuesToHistory()
	{}
	
	public void clear()
	{
	
	}
}
