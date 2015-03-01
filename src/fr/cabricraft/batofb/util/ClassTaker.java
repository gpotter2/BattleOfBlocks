package fr.cabricraft.batofb.util;

import org.bukkit.Bukkit;

public class ClassTaker {
	public static String getVersion()
	  {
	    String name = Bukkit.getServer().getClass().getPackage().getName();
	    String version = name.substring(name.lastIndexOf('.') + 1) + ".";
	    return version;
	  }
	  
	  public static Class<?> getNMSClass(String className)
	  {
	    String fullName = "net.minecraft.server." + getVersion() + className;
	    Class<?> clazz = null;
	    try
	    {
	      clazz = Class.forName(fullName);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return clazz;
	  }

	  public static Class<?> getOBCClass(String className)
	  {
	    String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
	    Class<?> clazz = null;
	    try
	    {
	      clazz = Class.forName(fullName);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return clazz;
	  }
}
