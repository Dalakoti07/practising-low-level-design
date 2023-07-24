package common

type Stack []int

func (s *Stack) Push(item int) {
	*s = append(*s, item)
}

func (s *Stack) Pop() (int, bool) {
	length := s.Size()
	if length == 0 {
		return 0, false
	}
	item := (*s)[length-1]
	*s = (*s)[:length-1]
	return item, true
}

func (s *Stack) Size() int {
	return len(*s)
}

func (s *Stack) isEmpty() bool {
	return len(*s) == 0
}
