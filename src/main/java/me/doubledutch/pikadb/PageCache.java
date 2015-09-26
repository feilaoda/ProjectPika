package me.doubledutch.pikadb;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

interface PageCache{
	void Set(int id);
}

class NoopPageCache implements PageCache{
	public void Set(int id) {
		// Noop
	}
}

class LRUPageCache implements PageCache{
	private int size;
	private Map<Integer, Page> pageMap;
	private Deque<Integer> pageCache;

	public LRUPageCache(Map<Integer, Page> map, int size){
		this.size=size;
		this.pageMap=map;
		this.pageCache=new ArrayDeque<Integer>();
	}

	public void Set(int id){
		if (!pageMap.containsKey(id)){
			return;
		}
		if (pageCache.contains(id)){
			pageCache.remove(id);
		}
		pageCache.addFirst(id);
		if (pageCache.size() > size){
			Integer evictID=pageCache.removeLast();
			Page evict=pageMap.get(evictID);
			evict.unloadRawData();
		}
	}
}

class SLRUPageCache implements PageCache{
	private int size;
	private Map<Integer, Page> pageMap;
	private Deque<Integer> l1, l2;

	public SLRUPageCache(Map<Integer, Page> map, int size){
		this.size=size;
		this.pageMap=map;
		this.l1=new ArrayDeque<Integer>();
		this.l2=new ArrayDeque<Integer>();
	}

	public void Set(int id){
		if (!pageMap.containsKey(id)){
			return;
		}
		// If the page exists in L1, move it to L2
		// If L2 at size, move end to back of L1
		if (l1.contains(id)){
			l1.remove(id);
			l2.addLast(id);
			if (l2.size() > size){
				l1.addFirst(l2.removeLast());
			}
			return;
		}
		// If page exists in L2, move to L2 front
		if (l2.contains(id)){
			l2.remove(id);
			l2.addFirst(id);
			return;
		}
		// Put at front of L1 cache
		l1.addFirst(id);
		// If L1 size is at maximum, evict last page
		if (l1.size() > size){
			Integer evictID=l1.removeLast();
			Page evict=pageMap.get(evictID);
			evict.unloadRawData();
		}
	}
}