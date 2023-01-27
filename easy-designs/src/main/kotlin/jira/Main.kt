package jira

enum class TaskType { STORY, FEATURE, BUG }

enum class TaskStatus { OPEN, IN_PROGRESS, RESOLVED, DELAYED, COMPLETED }

class User {
    private val taskList = mutableListOf<Task>()
    private val sprintList = mutableListOf<Sprint>()

    fun createTask(type: TaskType): Task {
        if (type == TaskType.STORY) {
            println("Warning Task of type story is being created with no subtract!!")
        }
        val task = Task()
        task.setTaskType(type)
        task.setUser(this)
        taskList.add(task)
        return task
    }

    fun createTask(subtract: String): Task {
        val task = Task()
        task.setTaskType(TaskType.STORY)
        task.setSubtract(subtract)
        task.setUser(this)
        taskList.add(task)
        return task
    }

    fun createSprint(begin: Int, end: Int, name: String): Sprint {
        val sprint = Sprint(
            begin, end, name
        )
        sprintList.add(sprint)
        return sprint
    }

    /**
     * Verify if Kotlin uses copy by reference or not
     */
    // return bool to tell if it was success or failure
    fun addToSprint(sprint: Sprint, task: Task): Boolean {
        for (s: Sprint in sprintList) {
            if (s == sprint) {
                s.addTask(task)
                return true
            }
        }
        return false
    }

    fun removeFromSprint(sprint: Sprint, task: Task): Boolean {
        for (s: Sprint in sprintList) {
            if (sprint == s) {
                // we have found the sprint, not its responsibility of sprint to delete it
                return s.eraseTask(task.getTaskId())
            }
        }
        return false
    }

    fun printAllTasks() {
        val squezzed = taskList.map {
            "${it.getTaskId()} "
        }
        println(squezzed)
    }

    fun changeStatus(task: Task, status: TaskStatus): Boolean {
        val idx = taskList.indexOfFirst {
            it.getTaskId() == task.getTaskId()
        }
        if (idx == -1)
            return false
        taskList[idx].setStatus(status)
        return true
    }
}

class Task {
    // it would be modified as soon as its created
    /**
     * I have it initialised with zero because u cannot make primitives type as late init
     */
    private var id: Int = 0

    init {
        id = getUniqueId()
    }

    fun getTaskId() = id

    // this is task description not fancy
    private var subtract: String = ""
    private lateinit var user: User
    private lateinit var taskType: TaskType
    private lateinit var taskStatus: TaskStatus

    fun setTaskType(type: TaskType) {
        taskType = type
    }

    fun setStatus(status: TaskStatus) {
        this.taskStatus = status
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun setSubtract(subtract: String) {
        this.subtract = subtract
    }

    companion object {
        private var counter = 0
        fun getUniqueId(): Int {
            counter++
            return counter
        }
    }

}

class Sprint constructor(
    private val begin: Int,
    private val end: Int,
    private val name: String,
) {

    private val tasks = mutableListOf<Task>()

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun printDetails() {
        println("Sprint name: $name and begins: $begin and end: $end")
    }

    override fun equals(other: Any?): Boolean {
        val casted = other as? Sprint
        casted ?: return false
        return begin == casted.begin && end == casted.end && name == casted.name
    }

    override fun hashCode(): Int {
        return tasks.hashCode()
    }

    fun getTasks(): List<Task> {
        return tasks
    }

    fun eraseTask(taskNumber: Int): Boolean {
        return tasks.removeIf {
            it.getTaskId() == taskNumber
        }
    }
}

fun main() {
    val user1 = User()
    val user2 = User()

    val task1 = user1.createTask(TaskType.FEATURE)
    val task11 = user1.createTask(TaskType.BUG)

    val task2 = user2.createTask(TaskType.BUG)
    val task22 = user2.createTask("This is a subtract")

    val sprint1 = user1.createSprint(
        22, 33, "Sprint 1"
    )
    val sprint2 = user2.createSprint(
        44, 55, "Sprint 2"
    )

    println(user1.changeStatus(task11, TaskStatus.IN_PROGRESS)) // 1
    println(user1.addToSprint(sprint1,task1)) // 1
    println(user1.addToSprint(sprint1, task11)) // 1
    println(user1.addToSprint(sprint2, task1)) // 0
    println(user1.removeFromSprint(sprint1, task11)) // 1

    println(user2.addToSprint(sprint1, task1)) // 0
    println(user2.removeFromSprint(sprint1,task2)) //0
    println(user2.addToSprint(sprint2,task1)) // 1
    println(user2.addToSprint(sprint2,task2)) // 1

    println(sprint1.printDetails())
    println(user1.printAllTasks())
    println(user2.printAllTasks())
}



