package com.example.konekin.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("employee_name")
    val employeeName: String = "",

    @SerializedName("employee_salary")
    val employeeSalary: Int = 0,

    @SerializedName("employee_age")
    val employeeAge: Int = 0,

    @SerializedName("profile_image")
    val profileImage: String = ""
)