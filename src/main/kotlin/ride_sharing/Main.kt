package ride_sharing

import java.lang.Error

open class Person(val name: String)

enum class RideStatus { IDLE, CREATED, WITHDRAWN, COMPLETED }

class Ride(
    var id: Int,
    private var source: Int,
    private var destination: Int,
    private var seats: Int,
    var rideStatus: RideStatus = RideStatus.IDLE
) {
    companion object {
        const val CHARGE_PER_KM = 20
    }

    fun calculateFare(wasSpecialRider: Boolean): Double {
        val difference = destination - source
        if (seats < 2) {
            if (wasSpecialRider) {
                return difference * CHARGE_PER_KM * 0.75
            }
            return (difference * CHARGE_PER_KM).toDouble()
        }
        if (wasSpecialRider) {
            return difference * seats * CHARGE_PER_KM * 0.5
        }
        return difference * seats * CHARGE_PER_KM * 0.75
    }

}

class Driver(private val _name: String) : Person(_name)

class Rider(private val id: Int, private val _name: String) : Person(name = _name) {

    // we want to make a rider as preferred rider
    // so we have to make vector of how many rides they have taken
    // and formulae changes on that as well

    private val completedRides = mutableListOf<Ride>()
    private lateinit var currentRide: Ride

    fun getId() = id

    fun createRide(id: Int, source: Int, destination: Int, seats: Int) {
        if (source >= destination) {
            throw Error("source cannot be > destination")
        }
        currentRide = Ride(
            id, source, destination, seats
        )
        currentRide.rideStatus = RideStatus.CREATED
    }

    fun updateRide(id: Int, source: Int, destination: Int, seats: Int) {
        if (currentRide.rideStatus == RideStatus.WITHDRAWN)
            throw Error("Cannot change status of withdrawn ride")
        else if (currentRide.rideStatus == RideStatus.COMPLETED) {
            throw Error("Cannot change status of completed ride")
        }
        createRide(
            id, source, destination, seats
        )
    }

    fun withdrawRide(id: Int) {
        if (currentRide.id != id)
            throw Error("Given ride ID is wrong, cannot withdraw it")
        if (currentRide.rideStatus != RideStatus.CREATED) {
            throw Error("cannot withdraw ride is not in progress")
        }
        currentRide.rideStatus = RideStatus.WITHDRAWN
    }

    fun getCurrentRide() = currentRide.id

    fun closeRide(): Double {
        if (currentRide.rideStatus != RideStatus.CREATED)
            throw Error("Ride was not in progress so cannot mark it as closed")
        currentRide.rideStatus = RideStatus.COMPLETED
        completedRides.add(currentRide)
        return currentRide.calculateFare(
            wasSpecialRider = completedRides.size > 10
        )
    }
}

class SystemUnderTest(
    private var drivers: Int,
    private val riders: List<Rider>
) {
    init {
        if (drivers < 2 || riders.size < 2)
            throw Error("Not enough drivers or riders")
    }

    fun createRide(riderId: Int, rideId: Int, origin: Int, destination: Int, seats: Int) {
        if (drivers == 0)
            throw Error("No drivers around cannot create ride")
        for (rider: Rider in riders) {
            if (rider.getId() == riderId) {
                rider.createRide(
                    id = rideId, source = origin, destination = destination, seats = seats,
                )
                drivers--
                break
            }
        }
    }

    fun updateRide(riderId: Int, rideId: Int, origin: Int, destination: Int, seats: Int) {
        for (rider: Rider in riders) {
            if (rider.getId() == riderId) {
                rider.updateRide(
                    id = rideId, source = origin, destination = destination, seats = seats,
                )
                break
            }
        }
    }

    fun widthDrawRide(riderId: Int, rideId: Int) {
        for (rider: Rider in riders) {
            if (rider.getId() == riderId) {
                rider.withdrawRide(rideId)
                drivers++
                break
            }
        }
    }

    fun closeRide(riderId: Int): Double {
        for (rider: Rider in riders) {
            if (rider.getId() == riderId) {
                drivers++
                return rider.closeRide()
            }
        }
        return 0.0
    }
}


fun main() {
    val rider1 = Rider(id = 1, _name = "Rider 1")
    val driver = Driver(
        _name = "driver 1"
    )
    val rider2 = Rider(id = 2, _name = "Rider 2")
    val rider3 = Rider(id = 3, _name = "Rider 3")
    val listOfAllRiders = listOf(rider1, rider2, rider3)

    /**
     *     rider1.createRide(1,50, 60, 1)
     *     println("amount from 1st ride: ${rider1.closeRide()}")
     *     rider1.createRide(2,50, 55, 1)
     *     rider1.updateRide(2,50,100,2)
     *     println("amount from 2nd ride: ${rider1.closeRide()}")
     *     println("*******************************************************")
     */

    // since we have created system under
    // test so we would be delegating
    // ride creation and everything to it rather than doing
    // rider.createRide or rider.updateRide
    val systemUnderTest = SystemUnderTest(
        drivers = 3,
        riders = listOfAllRiders,
    )
    systemUnderTest.createRide(
        1,1,50,60,1
    )
    systemUnderTest.updateRide(1,1,50,60,2)
    println("ride cost from 3rd ride: ${systemUnderTest.closeRide(1)}")
}
