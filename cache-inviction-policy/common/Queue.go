package common

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

func (q *Queue) Size() int {
	return len(*q)
}

func (q *Queue) isEmpty() bool {
	return len(*q) == 0
}
