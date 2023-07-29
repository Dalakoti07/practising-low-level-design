package main

import (
	"container/list"
	"fmt"
	"github/dalakoti07/cache_enviction/common"
)

var cacheHits = 0
var cacheMiss = 0

type LRUCache struct {
	capacity int
	cache    map[int]*list.Element
	list     *list.List
}

type entry struct {
	key   int
	value int
}

func getLRUCache(capacity int) LRUCache {
	return LRUCache{
		capacity: capacity,
		cache:    make(map[int]*list.Element),
		list:     list.New(),
	}
}

func (lruCache *LRUCache) Get(key int) int {
	if elem, found := lruCache.cache[key]; found {
		lruCache.list.MoveToFront(elem)
		return elem.Value.(*entry).value
	}
	return -1
}

func (lruCache *LRUCache) Put(key int, value int) {
	if elem, found := lruCache.cache[key]; found {
		elem.Value.(*entry).value = value
		cacheHits++
		lruCache.list.MoveToFront(elem)
	} else {
		if len(lruCache.cache) >= lruCache.capacity {
			// Remove the least recently used element from the cache and list
			oldest := lruCache.list.Back()
			delete(lruCache.cache, oldest.Value.(*entry).key)
			lruCache.list.Remove(oldest)
		}
		cacheMiss++

		newEntry := &entry{key, value}
		elem := lruCache.list.PushFront(newEntry)
		lruCache.cache[key] = elem
	}
}

func main() {
	lruCache := getLRUCache(common.Capacity)

	for _, item := range common.ItemsToBeInserted {
		lruCache.Put(item, item)
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHits, cacheMiss)
}
