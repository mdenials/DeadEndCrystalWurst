/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.WurstClient;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.components.FileComponent;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.util.json.JsonException;
import net.wurstclient.util.json.JsonUtils;

public final class FileSetting extends Setting
{
	private final Path folder;
	private String selectedFile = "";
	
	public FileSetting(String name, String description, String folderName)
	{
		super(name, description);
		folder = WurstClient.INSTANCE.getWurstFolder().resolve(folderName);
		setSelectedFileToDefault();
	}
	
	public Path getFolder()
	{
		return folder;
	}
	
	public String getSelectedFileName()
	{
		return selectedFile;
	}
	
	public Path getSelectedFile()
	{
		return folder.resolve(selectedFile);
	}
	
	public void setSelectedFile(String selectedFile)
	{
		Objects.requireNonNull(selectedFile);
		
		Path newSelectedFile = folder.resolve(selectedFile);
		if(!Files.exists(newSelectedFile))
			return;
		
		this.selectedFile = selectedFile;
		WurstClient.INSTANCE.saveSettings();
	}
	
	private void setSelectedFileToDefault()
	{
		ArrayList<Path> files = listFiles();
		if (!files.isEmpty())
			selectedFile = "" + files.get(0).getFileName();
	}
	
	public void resetFolder()
	{
		for(Path path : listFiles())
			try
			{
				Files.delete(path);
				
			}catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		
		setSelectedFileToDefault();
		WurstClient.INSTANCE.saveSettings();
	}
	
	public ArrayList<Path> listFiles()
	{
		if(!Files.isDirectory(folder)) return new ArrayList<>();
		
		try(Stream<Path> files = Files.list(folder))
		{
			return files.filter(Files::isRegularFile).collect(Collectors.toCollection(ArrayList::new));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Component getComponent()
	{
		return new FileComponent(this);
	}
	
	@Override
	public void fromJson(JsonElement json)
	{
		try
		{
			String newFile = JsonUtils.getAsString(json);
			
			if(newFile.isEmpty() || !Files.exists(folder.resolve(newFile)))
				return;
			
			selectedFile = newFile;
			
		}catch(JsonException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public JsonElement toJson()
	{
		return new JsonPrimitive(selectedFile);
	}
	
	@Override
	public JsonObject exportWikiData()
	{
		JsonObject json = new JsonObject();
		json.addProperty("name", getName());
		json.addProperty("descriptionKey", getDescriptionKey());
		json.addProperty("type", "File");
		
		Path mcFolder = WurstClient.INSTANCE.getWurstFolder().getParent();
		if(folder.startsWith(mcFolder))
			json.addProperty("folder", mcFolder.relativize(folder).toString());
		
		return json;
	}
	
	@Override
	public Set<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		return new LinkedHashSet<>();
	}
}
