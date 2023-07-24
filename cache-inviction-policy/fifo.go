package main

import "fmt"
import "github/dalakoti07/cache_enviction/common"

func main() {
	hashMap := map[int]bool{}
	queue := &common.Queue{}
	cacheHits := 0
	cacheMiss := 0
	for _, item := range common.ItemsToBeInserted {
		// check if it already exists in cache
		_, exists := hashMap[item]
		if exists {
			// it exists then continue, it's a cache hit
			cacheHits++
			continue
		}

		// it's a cache miss
		cacheMiss++
		if queue.Size() == common.Capacity {
			val, _ := queue.Dequeue()
			delete(hashMap, val)
		}
		queue.Enqueue(item)
		hashMap[item] = true
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHits, cacheMiss)

}
