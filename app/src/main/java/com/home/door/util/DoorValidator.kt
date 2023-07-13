package com.home.door.util

import android.content.Context
import com.home.door.R

object DoorValidator {
    fun validateName(name: String, context: Context): Boolean{
        if (name.isBlank())
            throw Exception(context.getString(R.string.name_empty_msg))
        return true
    }

    fun validateIp(ip: String, context: Context): Boolean{
        if (ip.isBlank())
            throw Exception(context.getString(R.string.ip_empty_msg))
        val regex = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
        if (!ip.matches(regex.toRegex()))
            throw Exception(context.getString(R.string.ip_match_msg))
        return true
    }

    fun validateUser(user: String, context: Context): Boolean{
        if (user.isBlank())
            throw Exception(context.getString(R.string.user_empty_msg))
        if (user.contains(" "))
            throw Exception(context.getString(R.string.user_spaces_msg))
        return true
    }

    fun validatePassword(password: String, context: Context): Boolean{
        if (password.isBlank())
            throw Exception(context.getString(R.string.password_empty_msg))
        if (password.contains(" "))
            throw Exception(context.getString(R.string.password_spaces_msg))
        return true
    }

}