/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.ai;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayDeque;

public class PathQueue
{
	private final Queue<PathQueue.Entry> queue = new LinkedBlockingQueue<>();
	
	private static class Entry
	{
		private PathPos pos;
		
		public Entry(PathPos pos)
		{
			this.pos = pos;
		}
	}
	
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
	
	public boolean add(PathPos pos)
	{
		return queue.add(new Entry(pos));
	}
	
	public PathPos[] toArray()
	{
		PathPos[] array = new PathPos[size()];
		Iterator<Entry> itr = queue.iterator();
		
		for(int i = 0; i < size() && itr.hasNext(); i++)
			array[i] = itr.next().pos;
		
		return array;
	}
	
	public int size()
	{
		return queue.size();
	}
	
	public void clear()
	{
		queue.clear();
	}
	
	public PathPos poll()
	{
		return queue.poll().pos;
	}
}
