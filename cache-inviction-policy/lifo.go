package main

import "fmt"
import "github/dalakoti07/cache_enviction/common"

type Stack []int

func (s *Stack) Push(item int) {
	*s = append(*s, item)
}

func (s *Stack) Pop() (int, bool) {
	length := s.size()
	if length == 0 {
		return 0, false
	}
	item := (*s)[length-1]
	*s = (*s)[:length-1]
	return item, true
}

func (s *Stack) size() int {
	return len(*s)
}

func (s *Stack) isEmpty() bool {
	return len(*s) == 0
}

func main() {
	hashMap := map[int]bool{}
	stack := &Stack{}
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
		if stack.size() == common.Capacity {
			val, _ := stack.Pop()
			delete(hashMap, val)
		}
		stack.Push(item)
		hashMap[item] = true
	}

	fmt.Printf("cache hits: %d, cache miss: %d\n", cacheHits, cacheMiss)

}
