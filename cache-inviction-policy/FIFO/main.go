package main

import "fmt"

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
	capacity := 10
	hashMap := map[int]bool{}
	itemsToBeInserted := []int{1, 5, 6, 7, 8, 1, 5, 6, 7, 5, 6, 7, 8, 1, 5, 5, 6, 7, 8, 1, 5, 6, 7, 5, 6, 7, 8, 1}
	queue := &Queue{}
	cacheHits := 0
	cacheMiss := 0
	for _, item := range itemsToBeInserted {
		// check if it already exists in cache
		_, exists := hashMap[item]
		if exists {
			// it exists then continue, it's a cache hit
			cacheHits++
			continue
		}

		// it's a cache miss
		cacheMiss++
		if queue.size() == capacity {
			val, _ := queue.Dequeue()
			delete(hashMap, val)
		}
		queue.Enqueue(item)
		hashMap[item] = true
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHits, cacheMiss)

}
