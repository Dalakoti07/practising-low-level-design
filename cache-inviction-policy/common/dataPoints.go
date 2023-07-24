package common

import "fmt"

var Capacity = 5
var ItemsToBeInserted = []int{1, 2, 3, 4, 5, 6, 7, 2, 1, 4, 6, 3, 2, 1, 6, 8, 9, 20, 1, 2, 3, 5, 7, 8, 9, 10, 21, 22, 23}

func PrintHashMap(hashMap *map[int]bool) {
	println("\nprinting hashmap\n")
	for key, _ := range *hashMap {
		fmt.Printf("%d, ", key)
	}
	println()
}

func PrintStack(s *Stack) {
	println("\n printing stack\n")
	for _, val := range *s {
		fmt.Printf("%d, ", val)
	}
	println("\n************************************************************")
}

func PrintQueue(q *Queue) {
	println("\n printing queue\n")
	for _, val := range *q {
		fmt.Printf("%d, ", val)
	}
	println("\n************************************************************")
}
