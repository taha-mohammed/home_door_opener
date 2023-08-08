package com.home.door.util

import com.home.door.R
import com.home.door.data.room.DoorEntity
import com.home.door.main.FieldErrorState

object DoorValidator {

    fun validateDoor(door: DoorEntity): FieldErrorState {
        var result = FieldErrorState()

        try { validateName(door.name) }
        catch (e: IllegalArgumentException) {
            result = result.copy(nameError = e.message?.toInt())
        }

        // validate ip
        try { validateIp(door.ip) }
        catch (e: IllegalArgumentException) {
            result = result.copy(ipError = e.message?.toInt())
        }

        // validate username
        try { validateUser(door.user) }
        catch (e: IllegalArgumentException) {
            result = result.copy(userError = e.message?.toInt())
        }

        // validate password
        try { validatePassword(door.password) }
        catch (e: IllegalArgumentException) {
            result = result.copy(passwordError = e.message?.toInt())
        }

        return result
    }
    private fun validateName(name: String): Boolean{
        require (name.isNotBlank()) {
            R.string.name_empty_msg
        }
        return true
    }

    private fun validateIp(ip: String): Boolean{
        require (ip.isNotBlank()) {
            R.string.ip_empty_msg
        }
        val regex = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
        require (ip.matches(regex.toRegex())) {
            R.string.ip_match_msg
        }
        return true
    }

    private fun validateUser(user: String): Boolean{
        require (user.isNotBlank()) {
            R.string.user_empty_msg
        }
        require (!user.contains(" ")) {
            R.string.user_spaces_msg
        }
        return true
    }

    private fun validatePassword(password: String): Boolean{
        require (password.isNotBlank()) {
            R.string.password_empty_msg
        }
        require (!password.contains(" ")) {
            R.string.password_spaces_msg
        }
        return true
    }

}