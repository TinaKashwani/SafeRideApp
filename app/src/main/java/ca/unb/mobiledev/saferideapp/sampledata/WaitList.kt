package ca.unb.mobiledev.saferideapp.sampledata

data class WaitList(private val id: String?,
                  private val name: String?,
                  val location: String? = null,
                    val pickupLocation: String? = null) {
    val nameItem: String
        get() = "$name"
    val idItem: String
        get() = "$id"
}
