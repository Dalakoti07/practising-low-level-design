package main

import "fmt"
import "github/dalakoti07/cache_enviction/common"

type Queue []int

func (q *Queue) Enqueue(item int) {
	*q = append(*q, item)
}

func (q *Queue) Dequeue() (int, bool) {
	if len(*q) == 0 {
		return 0, false
	}
	item := (*q)[0]
	*q = (*q)[1:]
	return item, true
}

func (q *Queue) size() int {
	return len(*q)
}

func (q *Queue) isEmpty() bool {
	return len(*q) == 0
}

func main() {
	hashMap := map[int]bool{}
	queue := &Queue{}
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
		if queue.size() == common.Capacity {
			val, _ := queue.Dequeue()
			delete(hashMap, val)
		}
		queue.Enqueue(item)
		hashMap[item] = true
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHits, cacheMiss)

}