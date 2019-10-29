package com.blackrook.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * A factory that creates actions.
 * @author Matthew Tropiano
 */
public final class ActionFactory
{
	private ActionFactory() {}

	/**
	 * Creates a new action.
	 * @param icon the icon associated with the action.
	 * @param label the action label.
	 * @param handler the code called when the action is triggered.
	 * @return a new action.
	 */
	public static Action action(Icon icon, String label, ActionEventHandler handler)
	{
		return new TreeSwingAction(icon, label, handler);
	}

	/**
	 * Creates a new action.
	 * @param label the action label.
	 * @param handler the code called when the action is triggered.
	 * @return a new action.
	 */
	public static Action action(String label, ActionEventHandler handler)
	{
		return new TreeSwingAction(null, label, handler);
	}

	/**
	 * Creates a new action.
	 * @param icon the icon associated with the action.
	 * @param handler the code called when the action is triggered.
	 * @return a new action.
	 */
	public static Action action(Icon icon, ActionEventHandler handler)
	{
		return new TreeSwingAction(icon, null, handler);
	}

	/* ==================================================================== */
	
	/**
	 * An action handler that is called when an action is performed.
	 */
	@FunctionalInterface
	public interface ActionEventHandler
	{
		/**
		 * Called when an action event happens.
		 * @param e the ActionEvent.
		 */
	    void handleActionEvent(ActionEvent e);
	}

	/**
	 * The action generated from an action call.
	 */
	private static class TreeSwingAction extends AbstractAction
	{
		private static final long serialVersionUID = 7014730121602528947L;
		
		private ActionEventHandler handler;
	
	    private TreeSwingAction(Icon icon, String label, ActionEventHandler handler)
	    {
	    	super(label, icon);
	    	this.handler = handler;
	    }
	    
		@Override
		public void actionPerformed(ActionEvent e)
	    {
	    	handler.handleActionEvent(e);
		}
		
	}

}
