package splitwise

enum class TypeOfSplit { EQUAL, EXACT, PERCENTAGE }

class User constructor(private val name: String) {
    companion object {
        private var uniqueCompanionId = 0
        fun getUniqueId(): Int {
            uniqueCompanionId++
            return uniqueCompanionId
        }
    }

    private var id = 0
    private var totalExpenseSoFar = 0.0
    private val expenseSheet = HashMap<Int, Double>()

    init {
        id = getUniqueId()
        expenseSheet.clear()
    }

    fun getId() = id

    fun getName() = name

    fun getUserExpenseSheet() = expenseSheet

    fun printTotalBalance() = totalExpenseSoFar

    override fun equals(other: Any?): Boolean {
        val casted = other as? User
        casted ?: return false
        return casted.name == name && casted.id == id
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id
        result = 31 * result + totalExpenseSoFar.hashCode()
        return result
    }

    fun doSettlementForEqualSplitExpenseAsCreditor(expense: Expense) {
        val amountInvolved = expense.amount
        val peopleInvolved = expense.defaulters.size
        val eachShare = amountInvolved / peopleInvolved
        if (expense.creditor.getId() == this.id) {
            // since he contributed he would get this in return
            totalExpenseSoFar -= (eachShare * (peopleInvolved - 1))
        }
        // updating sheet
        expense.defaulters.forEach {
            if (it.id == expense.creditor.id) {
                // nothing
            } else {
                if (expenseSheet.containsKey(it.id)) {
                    // !! is safe here
                    expenseSheet[it.id] = expenseSheet[it.id]!!.plus(eachShare)
                } else {
                    expenseSheet[it.id] = eachShare
                }
            }
        }
    }

    fun doSettlementForEqualSplitExpenseAsDebitor(expense: Expense) {
        val amountInvolved = expense.amount
        val peopleInvolved = expense.defaulters.size
        val eachShare = amountInvolved / peopleInvolved
        totalExpenseSoFar += eachShare

        // updating sheet
        if (expenseSheet.containsKey(expense.creditor.id)) {
            // !! is safe here
            expenseSheet[expense.creditor.id] = expenseSheet[expense.creditor.id]!!.plus(eachShare)
        } else {
            expenseSheet[expense.creditor.id] = eachShare
        }
    }
}

class Expense constructor(
    val creditor: User,
    val split: TypeOfSplit,
    val defaulters: List<User>,
    val amount: Double,
) {
    private var description = ""
    private var id = 0
    private val percentageDistribution = mutableListOf<Int>()
    private val exactDistribution = mutableListOf<Int>()

    init {
        id = getUniqueId()
    }

    companion object {
        private var uniqueCompanionId = 0
        fun getUniqueId(): Int {
            return uniqueCompanionId++
        }
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getExactDistribution(): List<Int> = exactDistribution

    fun setExactDistribution(
        distribution: List<Int>
    ) {
        exactDistribution.clear()
        exactDistribution.addAll(distribution)
    }

    fun getPercentageDistribution(): List<Int> {
        return percentageDistribution
    }

    fun setPercentageDistribution(
        distribution: List<Int>
    ) {
        percentageDistribution.clear()
        percentageDistribution.addAll(distribution)
    }

}

class SplitWise {
    private val users = mutableListOf<User>()

    // making a hashmap so that we can ensure only unique users signs up
    private val userAndIdMapping = HashMap<Int, User>()

    fun getNameFromUserId(userId: Int): String {
        val user = users.first {
            it.getId() == userId
        }
        return user.getName()
    }

    fun registerUser(user: User) {
        if (userAndIdMapping.containsKey(user.getId()))
            return
        userAndIdMapping[user.getId()] = user
        users.add(user)
    }

    fun addExpense(expense: Expense) {
        when (expense.split) {
            TypeOfSplit.PERCENTAGE -> {
                throw Error("Not implemented")
            }

            TypeOfSplit.EXACT -> {
                throw Error("Not implemented")
            }

            TypeOfSplit.EQUAL -> {
                expense.creditor.doSettlementForEqualSplitExpenseAsCreditor(expense)
                expense.defaulters.forEach { person ->
                    if (person.getId() != expense.creditor.getId())
                        person.doSettlementForEqualSplitExpenseAsDebitor(expense)
                }
            }
        }
    }

    fun printBalanceForAllUsers() {
        println("Printing balance for all users ...")
        users.forEach {
            println("${it.getName()}: ${it.printTotalBalance()}")
        }
    }

    fun getUsers(): List<User> = users

    fun simplifyExpenses() {

    }
}


fun main() {
    val user1 = User("Jitu")
    val user2 = User("Navin")
    val user3 = User("Yogi")
    val user4 = User("Mandal")

    val usersArray = listOf(user1, user2, user3, user4)
    val splitWise = SplitWise()
    usersArray.forEach {
        splitWise.registerUser(it)
    }
    val expenseOne = Expense(
        creditor = user1,
        split = TypeOfSplit.EQUAL,
        defaulters = usersArray,
        amount = 2000.0,
    )
    splitWise.addExpense(
        expenseOne,
    )
    splitWise.printBalanceForAllUsers()

    val expenseTwo = Expense(
        creditor = user2,
        split = TypeOfSplit.EQUAL,
        defaulters = usersArray,
        amount = 2000.0,
    )
    splitWise.addExpense(expenseTwo)
    splitWise.printBalanceForAllUsers()

    println("\n")

    usersArray.forEach { usr ->
        usr.getUserExpenseSheet().forEach { exp ->
            if (exp.value > 0) {
                println("${usr.getName()} owes a total of ${exp.value} to ${splitWise.getNameFromUserId(exp.key)}")
            } else {
                println("${usr.getName()} will get back a total of ${exp.value} from ${splitWise.getNameFromUserId(exp.key)}")
            }
        }
    }
    println()
}
