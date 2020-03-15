package com.blackrook.swing.canvas;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

/**
 * An image loading and caching mechanism.
 * The loader is provided a set of {@link LoaderFunction}s.
 * <p>The policy for each function is: each loader takes a {@link String} and either returns an {@link Image}, 
 * or null if the provided name does not correspond to a loaded image. If no functions return an image, it is
 * added to a set of non-existent images. The functions are assumed to be thread-safe.
 * @author Matthew Tropiano
 */
public class ImageLoader
{
	/** The set of paths not loaded. */
	private Set<String> errorNames;
	/** A map of paths to loaded images. */
	private Map<String, BufferedImage> nameMap;
	/** Loader functions. */
	private LoaderFunction[] loaderFunctions;
	
	/**
	 * A loader function for loading an image via a name.
	 */
	@FunctionalInterface
	public interface LoaderFunction
	{
		/**
		 * Loads an image using a name.
		 * @param name the name to use for loading an image.
		 * @return a loaded image or null if not loaded.
		 * @throws IOException if a read error occurs.
		 */
		BufferedImage load(String name) throws IOException;
	}
	
	/**
	 * Creates an image loader that always returns a single image. 
	 * @param image the image to always return.
	 * @return a new loader function that uses the working directory as a root.
	 */
	public static LoaderFunction createStaticImageLoader(BufferedImage image)
	{
		return (path) -> image;
	}

	/**
	 * Creates an image loader that treats the image name as a path to a file from the working directory. 
	 * @return a new loader function that uses the working directory as a root.
	 */
	public static LoaderFunction createFileLoader()
	{
		return createFileLoader(new File("."));
	}

	/**
	 * Creates an image loader that treats the image name as a path to a file. 
	 * @param directoryPath the root directory path for loading images.
	 * @return a new loader function that uses the provided directory as a root.
	 * @throws IllegalArgumentException if the provided file is null or not a directory. 
	 */
	public static LoaderFunction createFileLoader(final String directoryPath)
	{
		return createFileLoader(new File(directoryPath));
	}

	/**
	 * Creates an image loader that treats the image name as a path to a file. 
	 * @param directory the root directory for loading images.
	 * @return a new loader function that uses the provided directory as a root.
	 * @throws IllegalArgumentException if the provided file is null or not a directory. 
	 */
	public static LoaderFunction createFileLoader(final File directory)
	{
		if (directory == null || directory.isDirectory())
			throw new IllegalArgumentException("Provided file is not a directory.");
		
		return (path) ->
		{
			File file = new File(directory.getPath() + "/" + path);
			if (!file.exists() || file.isDirectory())
				return null;
			return ImageIO.read(file);
		};
	}
	
	/**
	 * Creates an image loader that treats the image name as a path to a classpath resource (from the current classloader). 
	 * @return a new loader function that uses the provided string as a resource path prefix.
	 * @throws IllegalArgumentException if the provided file is null or not a directory. 
	 */
	public static LoaderFunction createResourceLoader()
	{
		return createResourceLoader(Thread.currentThread().getContextClassLoader(), "");
	}
	
	/**
	 * Creates an image loader that treats the image name as a path to a classpath resource (from the current classloader). 
	 * @param prefix the root path for loading from classpath resources.
	 * @return a new loader function that uses the provided string as a resource path prefix.
	 * @throws IllegalArgumentException if the provided file is null or not a directory. 
	 */
	public static LoaderFunction createResourceLoader(final String prefix)
	{
		return createResourceLoader(Thread.currentThread().getContextClassLoader(), prefix);
	}
	
	/**
	 * Creates an image loader that treats the image name as a path to a classpath resource. 
	 * @param loader the classloader to use.
	 * @param prefix the root path for loading from classpath resources.
	 * @return a new loader function that uses the provided string as a resource path prefix.
	 * @throws IllegalArgumentException if the provided file is null or not a directory. 
	 */
	public static LoaderFunction createResourceLoader(final ClassLoader loader, final String prefix)
	{
		return (path) ->
		{
			InputStream in = null;
			try {
				in = loader.getResourceAsStream(prefix + path);
				return ImageIO.read(in);
			} finally {
				if (in != null) in.close();
			}
		};
	}
	
	/**
	 * Creates a new image loader.
	 * @param functions the functions to use for loading images, in the order specified.
	 * @throws IllegalArgumentException if functions is length 0.
	 */
	public ImageLoader(LoaderFunction... functions)
	{
		if (functions.length == 0)
			throw new IllegalArgumentException("No provided loaders.");
		
		this.errorNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		this.nameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		this.loaderFunctions = new LoaderFunction[functions.length];
		for (int i = 0; i < functions.length; i++)
			loaderFunctions[i] = functions[i];
	}
	
	/**
	 * Get or load an image by name.
	 * The name is case-insensitive.
	 * @param name the name of the image.
	 * @return the corresponding loaded image, or null if not loaded.
	 */
	public Image getImage(String name)
	{
		if (errorNames.contains(name))
			return null;
		if (nameMap.containsKey(name))
			return nameMap.get(name);
		
		BufferedImage image = null;
		for (int i = 0; i < loaderFunctions.length; i++)
		{
			try
			{
				if ((image = loaderFunctions[i].load(name)) != null)
					break;
			} 
			catch (Exception e) 
			{
				continue;
			}
		}
		
		if (image == null)
		{
			synchronized (errorNames)
			{
				errorNames.add(name);
			}
		}
		else
		{
			synchronized (nameMap)
			{
				nameMap.put(name, image);
			}
		}
		
		return image;
	}
	
	/**
	 * @return the set of cached image names.
	 * @see Map#keySet()
	 */
	public Set<String> getCachedNameSet()
	{
		return nameMap.keySet();
	}

	/**
	 * Removes a cached image from the cache, and clears a name from the error names set.
	 * @param name the name of the image to remove.
	 * @return true if the file was removed from cache, false if it was never cached.
	 */
	public boolean uncache(String name)
	{
		synchronized (errorNames)
		{
			errorNames.remove(name);
		}
		synchronized (nameMap)
		{
			return nameMap.remove(name) != null;
		}
	}
	
}
