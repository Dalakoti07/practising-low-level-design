package main

import (
	"container/list"
	"fmt"
	"github/dalakoti07/cache_enviction/common"
)

var cacheHitsMRU = 0
var cacheMissMRU = 0

type MRUCache struct {
	capacity int
	cache    map[int]*list.Element
	list     *list.List
}

func getMRUCache(capacity int) MRUCache {
	return MRUCache{
		capacity: capacity,
		cache:    make(map[int]*list.Element),
		list:     list.New(),
	}
}

func (lruCache *MRUCache) Get(key int) int {
	if elem, found := lruCache.cache[key]; found {
		lruCache.list.MoveToFront(elem)
		return elem.Value.(*common.Entry).Value
	}
	return -1
}

func (lruCache *MRUCache) Put(key int, value int) {
	if elem, found := lruCache.cache[key]; found {
		elem.Value.(*common.Entry).Value = value
		cacheHitsMRU++
		lruCache.list.MoveToFront(elem)
	} else {
		if len(lruCache.cache) >= lruCache.capacity {
			// Remove the least recently used element from the cache and list
			newest := lruCache.list.Front()
			delete(lruCache.cache, newest.Value.(*common.Entry).Key)
			lruCache.list.Remove(newest)
		}
		cacheMissMRU++

		newEntry := &common.Entry{Key: key, Value: value}
		elem := lruCache.list.PushFront(newEntry)
		lruCache.cache[key] = elem
	}
}

func main() {
	mruCache := getMRUCache(common.Capacity)

	for _, item := range common.ItemsToBeInserted {
		mruCache.Put(item, item)
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHitsMRU, cacheMissMRU)
}
