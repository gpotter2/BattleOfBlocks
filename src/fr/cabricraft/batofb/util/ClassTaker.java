/*
 *  Copyright (C) 2015 Gabriel POTTER (gpotter2)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
