/*******************************************************************************
 * Copyright (c) 2019 Black Rook Software
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.blackrook.swing.ComponentFactory.ComponentActionHandler;

/**
 * A factory that creates menus.
 * @author Matthew Tropiano
 */
public final class MenuFactory
{
	private MenuFactory() {}
	
	/**
	 * Creates a new JMenuBar.
	 * @param menus the menus to add.
	 * @return a new JMenuBar.
	 */
	public static JMenuBar menuBar(JMenu ... menus)
	{
		JMenuBar out = new JMenuBar();
		for (JMenu m : menus)
			out.add(m);
		return out;
	}
	
	/**
	 * Creates a new Pop-up Menu.
	 * @param name the menu heading.
	 * @param nodes the menu nodes to add.
	 * @return a new JPopupMenu.
	 */
	public static JPopupMenu popupMenu(String name, MenuNode ... nodes)
	{
		JPopupMenu out = new JPopupMenu(name);
		for (MenuNode mn : nodes)
			mn.addTo(out);
		return out;
	}
	
	/**
	 * Creates a new Pop-up Menu tree.
	 * @param nodes the menu nodes to add.
	 * @return a new JPopupMenu.
	 */
	public static JPopupMenu popupMenu(MenuNode ... nodes)
	{
		return popupMenu(null, nodes);
	}
	
	/**
	 * Creates a new menu tree.
	 * @param icon the icon for the menu entry.
	 * @param name the name of the menu.
	 * @param mnemonic the key mnemonic for accessing (VK).
	 * @param nodes the menu nodes to add.
	 * @return a new JMenu.
	 */
	public static JMenu menu(Icon icon, String name, int mnemonic, MenuNode ... nodes)
	{
		JMenu out = new JMenu(name);
		out.setMnemonic(mnemonic);
		for (MenuNode mn : nodes)
			mn.addTo(out);
		return out;
	}
	
	/**
	 * Creates a new menu tree.
	 * @param name the name of the menu.
	 * @param mnemonic the key mnemonic for accessing (VK).
	 * @param nodes the menu nodes to add.
	 * @return a new JMenu.
	 */
	public static JMenu menu(String name, int mnemonic, MenuNode ... nodes)
	{
		JMenu out = new JMenu(name);
		out.setMnemonic(mnemonic);
		for (MenuNode mn : nodes)
			mn.addTo(out);
		return out;
	}
	
	/**
	 * Creates a new menu tree.
	 * @param mnemonic the key mnemonic for accessing (VK).
	 * @param action the action for the menu.
	 * @return a new JMenu.
	 */
	public static JMenu menu(int mnemonic, Action action)
	{
		JMenu out = new JMenu(action);
		out.setMnemonic(mnemonic);
		return out;
	}
	
	/**
	 * Creates a new menu tree.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for accessing (VK).
	 * @param handler the code called when the action is triggered.
	 * @return a new JMenu.
	 */
	public static JMenu menu(Icon icon, String label, int mnemonic, ComponentActionHandler<JMenu> handler)
	{
		JMenu out = new JMenu(ActionFactory.action(icon, label, handler));
		out.setMnemonic(mnemonic);
		return out;
	}
	
	/**
	 * Creates a menu item node.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode item(int mnemonic, KeyStroke accelerator, Action action)
	{
		return new MenuItemNode(action, mnemonic, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode item(KeyStroke accelerator, Action action)
	{
		return new MenuItemNode(action, 0, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param mnemonic the key mnemonic for the item.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode item(int mnemonic, Action action)
	{
		return new MenuItemNode(action, mnemonic, null);
	}
	
	/**
	 * Creates a menu item node.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode item(Action action)
	{
		return new MenuItemNode(action, 0, null);
	}
	
	/**
	 * Creates a menu item node.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, int mnemonic, KeyStroke accelerator, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(icon, label, handler), mnemonic, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, int mnemonic, KeyStroke accelerator, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(label, handler), mnemonic, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, int mnemonic, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(icon, label, handler), mnemonic, null);
	}
	
	/**
	 * Creates a menu item node.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, int mnemonic, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(label, handler), mnemonic, null);
	}
	
	/**
	 * Creates a menu item node.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, KeyStroke accelerator, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(icon, label, handler), 0, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param label the label for the menu entry.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, KeyStroke accelerator, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(label, handler), 0, accelerator);
	}
	
	/**
	 * Creates a menu item node.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(icon, label, handler), 0, null);
	}
	
	/**
	 * Creates a menu item node.
	 * @param label the label for the menu entry.
	 * @param handler the code called when the action is triggered.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, ComponentActionHandler<JMenuItem> handler)
	{
		return new MenuItemNode(ActionFactory.action(label, handler), 0, null);
	}
	
	/**
	 * Creates a menu item submenu.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param nodes the menu nodes to add.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, int mnemonic, MenuNode ... nodes)
	{
		return new MenuBranchNode(icon, label, mnemonic, nodes);
	}
	
	/**
	 * Creates a menu item submenu.
	 * @param label the label for the menu entry.
	 * @param mnemonic the key mnemonic for the item.
	 * @param nodes the menu nodes to add.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, int mnemonic, MenuNode ... nodes)
	{
		return new MenuBranchNode(null, label, mnemonic, nodes);
	}
	
	/**
	 * Creates a menu item submenu.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param nodes the menu nodes to add.
	 * @return a menu node.
	 */
	public static MenuNode item(Icon icon, String label, MenuNode ... nodes)
	{
		return new MenuBranchNode(icon, label, 0, nodes);
	}
	
	/**
	 * Creates a menu item submenu.
	 * @param label the label for the menu entry.
	 * @param nodes the menu nodes to add.
	 * @return a menu node.
	 */
	public static MenuNode item(String label, MenuNode ... nodes)
	{
		return new MenuBranchNode(null, label, 0, nodes);
	}

	/**
	 * Creates a check box menu item node.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param selected the state of the checkbox.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(int mnemonic, KeyStroke accelerator, boolean selected, Action action)
	{
		return new MenuCheckBoxNode(action, selected, mnemonic, accelerator);
	}
	
	/**
	 * Creates a check box menu item node.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param selected the state of the checkbox.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(KeyStroke accelerator, boolean selected, Action action)
	{
		return new MenuCheckBoxNode(action, selected, 0, accelerator);
	}
	
	/**
	 * Creates a check box menu item node.
	 * @param mnemonic the key mnemonic for the item.
	 * @param selected the state of the checkbox.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(int mnemonic, boolean selected, Action action)
	{
		return new MenuCheckBoxNode(action, selected, mnemonic, null);
	}
	
	/**
	 * Creates a check box menu item node.
	 * @param selected the state of the checkbox.
	 * @param action the action for the menu item.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(boolean selected, Action action)
	{
		return new MenuCheckBoxNode(action, selected, 0, null);
	}
	
	/**
	 * Creates a check box menu item node.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(Icon icon, String label, boolean selected, int mnemonic, KeyStroke accelerator, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(icon, label, handler), selected, mnemonic, accelerator);
	}

	/**
	 * Creates a check box menu item.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param mnemonic the key mnemonic for the item.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(String label, boolean selected, int mnemonic, KeyStroke accelerator, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(label, handler), selected, mnemonic, accelerator);
	}

	/**
	 * Creates a check box menu item.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param mnemonic the key mnemonic for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(Icon icon, String label, boolean selected, int mnemonic, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(icon, label, handler), selected, mnemonic, null);
	}

	/**
	 * Creates a check box menu item.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param mnemonic the key mnemonic for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(String label, boolean selected, int mnemonic, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(label, handler), selected, mnemonic, null);
	}

	/**
	 * Creates a check box menu item.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(Icon icon, String label, boolean selected, KeyStroke accelerator, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(icon, label, handler), selected, 0, accelerator);
	}

	/**
	 * Creates a check box menu item.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param accelerator the keystroke shortcut for the item.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(String label, boolean selected, KeyStroke accelerator, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(label, handler), selected, 0, accelerator);
	}

	/**
	 * Creates a check box menu item.
	 * @param icon the icon for the menu entry.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(Icon icon, String label, boolean selected, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(icon, label, handler), selected, 0, null);
	}

	/**
	 * Creates a check box menu item.
	 * @param label the label for the menu entry.
	 * @param selected the state of the checkbox.
	 * @param handler the change handler to add.
	 * @return a menu node.
	 */
	public static MenuNode checkBoxItem(String label, boolean selected, ComponentActionHandler<JCheckBoxMenuItem> handler)
	{
		return new MenuCheckBoxNode(ActionFactory.action(label, handler), selected, 0, null);
	}

	/**
	 * Creates a menu separator item.
	 * @return a menu node.
	 */
	public static MenuNode separator()
	{
		return new MenuSeparatorNode();
	}

	/**
	 * A single menu node.
	 */
	public static abstract class MenuNode
	{
	    protected abstract void addTo(JMenu menu);
	    protected abstract void addTo(JPopupMenu menu);
	}

	/** Menu item node. */
	private static class MenuItemNode extends MenuNode
	{
		/** The item action. */
		protected Action action;
		/** The mnemonic for the item. */
		protected int mnemonic;
		/** The accelerator for the item. */
		protected KeyStroke accelerator;
		
		private MenuItemNode(Action action, int mnemonic, KeyStroke accelerator)
		{
			this.action = action;
			this.mnemonic = mnemonic;
			this.accelerator = accelerator;
		}
	
		@Override
		protected void addTo(JMenu menu)
		{
			JMenuItem item = new JMenuItem(action);
			if (mnemonic > 0)
				item.setMnemonic(mnemonic);
			item.setAccelerator(accelerator);
			menu.add(item);
		}
	
		@Override
		protected void addTo(JPopupMenu menu)
		{
			JMenuItem item = new JMenuItem(action);
			if (mnemonic > 0)
				item.setMnemonic(mnemonic);
			item.setAccelerator(accelerator);
			menu.add(item);
		}
		
	}

	/** Menu checkbox node. */
	private static class MenuCheckBoxNode extends MenuNode
	{
		/** The item action. */
		protected Action action;
		/** Starts selected. */
		protected boolean selected;
		/** The mnemonic for the item. */
		protected int mnemonic;
		/** The accelerator for the item. */
		protected KeyStroke accelerator;
		
		private MenuCheckBoxNode(Action action, boolean selected, int mnemonic, KeyStroke accelerator)
		{
			this.action = action;
			this.selected = selected;
			this.mnemonic = mnemonic;
			this.accelerator = accelerator;
		}
	
		@Override
		protected void addTo(JMenu menu)
		{
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
			item.setState(selected);
			if (mnemonic > 0)
				item.setMnemonic(mnemonic);
			item.setAccelerator(accelerator);
			menu.add(item);
		}
	
		@Override
		protected void addTo(JPopupMenu menu)
		{
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
			item.setState(selected);
			if (mnemonic > 0)
				item.setMnemonic(mnemonic);
			item.setAccelerator(accelerator);
			menu.add(item);
		}
		
	}

	/** Menu branch node. */
	private static class MenuBranchNode extends MenuNode
	{
		/** The icon for the item. */
		protected Icon icon;
		/** The label for the item. */
		protected String label;
		/** The mnemonic for the item. */
		protected int mnemonic;
		/** The additional nodes. */
		protected MenuNode[] nodes;
		
		private MenuBranchNode(Icon icon, String label, int mnemonic, MenuNode[] nodes)
		{
			this.icon = icon;
			this.label = label;
			this.mnemonic = mnemonic;
			this.nodes = nodes;
		}
	
		@Override
		protected void addTo(JMenu menu)
		{
			JMenu next = new JMenu(label);
			next.setIcon(icon);
			if (mnemonic > 0)
				next.setMnemonic(mnemonic);
			
			for (MenuNode mn : nodes)
				mn.addTo(next);
			
			menu.add(next);
		}
	
		@Override
		protected void addTo(JPopupMenu menu)
		{
			JMenu next = new JMenu(label);
			next.setIcon(icon);
			if (mnemonic > 0)
				next.setMnemonic(mnemonic);
			
			for (MenuNode mn : nodes)
				mn.addTo(next);
			
			menu.add(next);
		}
		
	}

	/** Menu separator node. */
	private static class MenuSeparatorNode extends MenuNode
	{
		private MenuSeparatorNode() {}
		
		@Override
		protected void addTo(JMenu menu)
		{
			menu.addSeparator();
		}
	
		@Override
		protected void addTo(JPopupMenu menu)
		{
			menu.addSeparator();
		}
		
	}

}
