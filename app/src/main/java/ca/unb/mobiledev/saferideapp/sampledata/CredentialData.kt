package ca.unb.mobiledev.saferideapp.sampledata


data class CredentialData(private val id: String?,
                    private val name: String?,
                    val username: String? = null,
                    private val password: String?) {
    val studentName: String
        get() = "$name"
    val idNum: String
        get() = "$id"
    val pass: String
        get() = "$password"
}
