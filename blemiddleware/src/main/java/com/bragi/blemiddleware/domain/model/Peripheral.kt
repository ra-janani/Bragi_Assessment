package com.bragi.blemiddleware.domain.model

data class Peripheral(
    val name: String?,
    val address: String,
    val manufacturerData: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Peripheral

        if (name != other.name) return false
        if (address != other.address) return false
        if (!manufacturerData.contentEquals(other.manufacturerData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + address.hashCode()
        result = 31 * result + (manufacturerData?.contentHashCode() ?: 0)
        return result
    }
}