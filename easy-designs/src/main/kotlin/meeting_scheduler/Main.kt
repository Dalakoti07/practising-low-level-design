package meeting_scheduler

/**
 * Let's define problem in a more detailed manner as PDF is not that clear
 * So given start and end time, scheduler would then book the room for you
 * if a room is filled, then scheduler would book a different room, or if
 * all rooms are filled then room cannot be booked
 *
 * So scheduler would be taking rooms array and when asked to schedule
 * meeting it would then return room name
 *
 */

class Room constructor(private val name: String) {

    // each room can host multiple meetings so it would contain array of meetings
    private val meetingsInThisRoom = mutableListOf<Meeting>()

    fun isBookingPossibleInThisRoom(startTime: Int, endTime: Int): Boolean {
        // iterate over all meetings in this room
        // if we found any meeting which has same
        // time range then we cannot conduct this meeting
        // for example if room for 2pm to 4pm is already booked then
        // rooms for slot 1pm to 2:30 and 2:30 to 3:00 and 3:45 to 4pm cannot be booked
        /**
         * Suppose two meetings (start1, end1) and (start2, end2)
         * Condition for meetings to occur we must have any of the following
         * - end1 <= start2 (meeting 2 would be scheduled after meeting 1)
         * - end2 <= start1 (meeting 2 would be schedules before meeting 1)
         * So condition is (cond1 || cond2)
         *
         * Now converse of above would give condition when meeting scheduling is not possible
         * converse would be !(cond1 || cond2)  = !cond1 && !cond2
         * which is end1>start2 && end2>start1
         */
        for (existingMeeting: Meeting in meetingsInThisRoom) {
            if (existingMeeting.endTime > startTime && endTime > existingMeeting.startTime) {
                return false
            }
        }
        meetingsInThisRoom.add(
            Meeting(startTime, endTime, room = this)
        )
        return true
    }

    fun getName() = name
}

class Meeting constructor(
    val startTime: Int, val endTime: Int,
    val room: Room,
)

class Scheduler constructor(
    private val rooms: List<Room>,
) {

    fun bookRoomAndGetName(startTime: Int, endTime: Int): String {
        for(room: Room in rooms){
            if(room.isBookingPossibleInThisRoom(startTime, endTime)){
                return room.getName()
            }
        }
        return "No room available"
    }

}

fun main() {
    val room1 = Room("room 1")
    val room2 = Room("room 2")
    val room3 = Room("room 3")

    val scheduler = Scheduler(listOf(room1, room2, room3))
    println(scheduler.bookRoomAndGetName(2, 5)) // room 1
    println(scheduler.bookRoomAndGetName(5, 8)) // room 1
    println(scheduler.bookRoomAndGetName(4, 8)) // room 2
    println(scheduler.bookRoomAndGetName(3, 6)) // room 3
    println(scheduler.bookRoomAndGetName(5, 8)) // no room

}
