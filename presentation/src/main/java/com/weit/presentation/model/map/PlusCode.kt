package com.weit.presentation.model

import java.io.Serializable


class PlusCode : Serializable {

    var globalCode: String? = null
    var compoundCode: String? = null

    override fun toString(): String {
        val sb = StringBuilder("[PlusCode: ")
        sb.append(globalCode)
        if (compoundCode != null) {
            sb.append(", compoundCode=").append(compoundCode)
        }
        sb.append("]")
        return sb.toString()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}