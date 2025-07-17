/*******************************************************************************
 * Copyright (c) 2019-2025 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * A small application builder for Swing applications.
 * @author Matthew Tropiano
 */
public final class ApplicationFactory 
{
	/**
	 * A control receiver for communicating to its parent container.
	 */
	public interface ControlReceiver
	{
		/**
		 * Attempts to close this application instance. 
		 */
		void attemptClose();
		
		/**
		 * @return the application container.
		 */
		Container getApplicationContainer();
	}
	
	/**
	 * An application type.
	 */
	public interface ApplicationType
	{
		/**
		 * Gets this application's title for the encapsulating container.
		 * @return this application's title.
		 */
		String getTitle();
		
		/**
		 * Fetches this application instance's content pane.
		 * This is only called once, and is intended to be the function that constructs
		 * the main component for the application's GUI.
		 * <p> This should NEVER be called by the application itself.
		 * @return the application's content pane.
		 */
		Container createContentPane();

		/**
		 * Sets the application control receiver.
		 * Called when the application gets attached to a window, but before it is shown.
		 * <p> This should NEVER be called by the application itself.
		 * @param receiver the receiver instance.
		 */
		default void setControlReceiver(ControlReceiver receiver)
		{
			// Do nothing.
		}
		
		/**
		 * Fetches this application instance's window frame icons.
		 * This is only called once, and is intended to be the function that getsthe application's icon.
		 * <p> This should NEVER be called by the application itself.
		 * @return the application's icon, or <code>null</code> for default.
		 */
		default List<Image> getIcons()
		{
			return null;
		}

		/**
		 * Fetches this application instance's menu bar, if used as a desktop frame.
		 * This is only called once per application creation, and is intended to be the function that constructs
		 * the menu bar for the application's GUI.
		 * <p> This should NEVER be called by the application itself.
		 * @return the application's menu bar. May return <code>null</code> for no bar.
		 */
		default JMenuBar createDesktopMenuBar()
		{
			return null;
		}

		/**
		 * Fetches this application instance's menu bar, if used as an internal frame.
		 * This is only called once per application creation, and is intended to be the function that constructs
		 * the menu bar for the application's GUI.
		 * <p> This should NEVER be called by the application itself.
		 * @return the application's menu bar. May return <code>null</code> for no bar.
		 */
		default JMenuBar createInternalMenuBar()
		{
			return null;
		}

		/**
		 * Called when the application is about to close. 
		 * By default, this returns true. You may ask the user if they wish to close it, here.
		 * <p> This should NEVER be called by the application itself.
		 * @return true if the application should close, false if not.
		 */
		default boolean shouldClose()
		{
			return true;
		}
		
		/**
		 * Called when the application is opened.
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onOpen()
		{
			// Do nothing.
		}
		
		/**
		 * Called when the application is minimized.
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onMinimize()
		{
			// Do nothing.
		}
		
		/**
		 * Called when the application is restored from iconification.
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onRestore()
		{
			// Do nothing.
		}
		
		/**
		 * Called when the application is focused.
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onFocus()
		{
			// Do nothing.
		}
		
		/**
		 * Called when the application is unfocused.
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onBlur()
		{
			// Do nothing.
		}

		/**
		 * Called when the application is closed (after {@link #shouldClose()} is called and returns true).
		 * <p> This should NEVER be called by the application itself.
		 */
		default void onClose()
		{
			// Do nothing.
		}
		
	}
	
	/**
	 * Creates a frame for an application.
	 * This frame is still not visible until it is made visible.
	 * @param <A> the application type.
	 * @param applicationClass the application class.
	 * @return a frame that contains the application.
	 */
	public static <A extends ApplicationType> JFrame createFrame(Class<A> applicationClass)
	{
		return createFrame(create(applicationClass));
	}
	
	/**
	 * Creates a frame for an application instance.
	 * This frame is still not visible until it is made visible.
	 * @param <A> the application type.
	 * @param instance the application.
	 * @return a frame that contains the application.
	 */
	public static <A extends ApplicationType> JFrame createFrame(final A instance)
	{
		JFrame frame = new JFrame();
		frame.setIconImages(instance.getIcons());
		frame.setTitle(instance.getTitle());
		frame.setJMenuBar(instance.createDesktopMenuBar());
		frame.setContentPane(instance.createContentPane());
		frame.setLocationByPlatform(true);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e) 
			{
				instance.onOpen();
			}
			
			@Override
			public void windowIconified(WindowEvent e) 
			{
				instance.onMinimize();
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) 
			{
				instance.onRestore();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) 
			{
				instance.onFocus();
			}

			@Override
			public void windowLostFocus(WindowEvent e) 
			{
				instance.onBlur();
			}

			@Override
			public void windowClosing(WindowEvent e) 
			{
				if (instance.shouldClose())
				{
					frame.setVisible(false);
					instance.onClose();
					frame.dispose();
				}
			}
		});
		instance.setControlReceiver(new ControlReceiver() 
		{
			@Override
			public void attemptClose() 
			{
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}

			@Override
			public Container getApplicationContainer()
			{
				return frame.getContentPane();
			}
		});
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		return frame;
	}
	
	/**
	 * Creates a frame for an application.
	 * This frame is still not visible until it is made visible.
	 * @param <A> the application type.
	 * @param applicationClass the application class.
	 * @return a frame that contains the application.
	 */
	public static <A extends ApplicationType> JInternalFrame createInternalFrame(Class<A> applicationClass)
	{
		return createInternalFrame(create(applicationClass));
	}
	
	/**
	 * Creates a frame for an application instance.
	 * This frame is still not visible until it is made visible.
	 * @param <A> the application type.
	 * @param instance the application.
	 * @return a frame that contains the application.
	 */
	public static <A extends ApplicationType> JInternalFrame createInternalFrame(final A instance)
	{
		JInternalFrame frame = new JInternalFrame();
		List<Image> icons = instance.getIcons();
		frame.setFrameIcon(icons != null && !icons.isEmpty() ? new ImageIcon(icons.get(0)) : null);
		frame.setTitle(instance.getTitle());
		frame.setJMenuBar(instance.createDesktopMenuBar());
		frame.setContentPane(instance.createContentPane());
		frame.setResizable(true);
		frame.addInternalFrameListener(new InternalFrameAdapter()
		{
			@Override
			public void internalFrameOpened(InternalFrameEvent e) 
			{
				instance.onOpen();
			}
			
			@Override
			public void internalFrameIconified(InternalFrameEvent e)
			{
				instance.onMinimize();
			}
			
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e)
			{
				instance.onRestore();
			}
			
			@Override
			public void internalFrameActivated(InternalFrameEvent e)
			{
				instance.onFocus();
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e)
			{
				instance.onBlur();
			}
			
			@Override
			public void internalFrameClosing(InternalFrameEvent e)
			{
				if (instance.shouldClose())
				{
					frame.setVisible(false);
					instance.onClose();
					frame.dispose();
				}
			}
		});
		instance.setControlReceiver(new ControlReceiver() 
		{
			@Override
			public void attemptClose() 
			{
				frame.dispatchEvent(new InternalFrameEvent(frame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));
			}

			@Override
			public Container getApplicationContainer()
			{
				return frame.getContentPane();
			}
		});
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		return frame;
	}
	
	/**
	 * Creates a new instance of a class from a class type.
	 * This essentially calls {@link Class#getDeclaredConstructor(Class...)} with no arguments 
	 * and {@link Class#newInstance()}, but wraps the call in a try/catch block that only throws an exception if something goes wrong.
	 * @param <T> the return object type.
	 * @param clazz the class type to instantiate.
	 * @return a new instance of an object.
	 * @throws RuntimeException if instantiation cannot happen, either due to
	 * a non-existent constructor or a non-visible constructor.
	 */
	private static <T> T create(Class<T> clazz)
	{
		Object out = null;
		try {
			out = clazz.getDeclaredConstructor().newInstance();
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		
		return clazz.cast(out);
	}
	
}
