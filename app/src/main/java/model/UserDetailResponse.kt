package com.example.konekin.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("employee_name")
    val name: String,

    @SerializedName("employee_salary")
    val salary: Int,

    @SerializedName("employee_age")
    val age: Int,

    @SerializedName("profile_image")
    val profileImage: String = ""
)