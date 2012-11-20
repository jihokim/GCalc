package net.gcalc.plugin.gui;

import java.awt.BorderLayout;

import net.gcalc.calc.main.AbstractPlugin;

abstract public class AbstractCartesianGraphPlugin extends AbstractPlugin
{
	protected PropertiesPanel propertiesPanel;
	
	protected AbstractCartesianGraphPlugin() {
		super();
		
		setPropertiesPanel(new PropertiesPanel(this));
	}
	
	public PropertiesPanel getPropertiesPanel()
	{   
		return propertiesPanel;
	}
	
	public void setPropertiesPanel(PropertiesPanel p)
	{
	    propertiesPanel = p;
	}
	
	protected String[] getModelLabels()
	{
		return new String[] {"f(x)="};
	}
	
	public void showPropertiesPanel(boolean show) {
		if (propertiesPanel==null)
			return;
		
		if (show) {
			this.getContentPane().add(BorderLayout.EAST, propertiesPanel);
		}
		else {
			remove(propertiesPanel);
		}
		
		pack();
	}
}