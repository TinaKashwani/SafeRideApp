package ca.unb.mobiledev.saferideapp.sampledata

data class Schedule(private val id: String?,
                    private val name: String?,
                    val location: String? = null,
                    val date: String? = null,
                    val startTime: String? = null,
                    val endTime: String? = null,) {
    // Only need to include getter for the course title
    val nameItem: String
        get() = "$name"
    val idItem: String
        get() = "$id"
}