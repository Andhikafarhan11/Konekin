package com.example.konekin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.konekin.databinding.ActivityUserDetailBinding
import com.example.konekin.model.Data
import com.example.konekin.model.UserRequest
import com.example.konekin.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailBinding
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Menerima parameter ID dari MainActivity
        userId = intent.getIntExtra("EXTRA_ID", -1)
        binding.tvUserId.text = "ID: $userId"

        // 2. Fetch data dari API /employees/{id}
        fetchUserDetail(userId)

        // 3. Aksi tombol PUT / Update User
        binding.btnUpdate.setOnClickListener {
            updateUserData()
        }

        // 4. Aksi tombol DELETE / Hapus User
        binding.btnDelete.setOnClickListener {
            deleteUserData()
        }
    }

    private fun fetchUserDetail(id: Int) {
        ApiClient.getInstance().getUserById(id).enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                val userData = response.body()
                if (response.isSuccessful && userData != null) {
                    binding.tvUserId.text = "ID: ${userData.id}"
                    binding.edtDetailName.setText(userData.employeeName)
                    binding.edtDetailAge.setText(userData.employeeAge.toString())
                    binding.edtDetailSalary.setText(userData.employeeSalary.toString())
                } else {
                    Toast.makeText(
                        this@UserDetailActivity,
                        "Gagal memuat detail (Kode: ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(
                    this@UserDetailActivity,
                    "Gagal mengambil detail: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateUserData() {
        val request = UserRequest(
            name = binding.edtDetailName.text.toString(),
            age = binding.edtDetailAge.text.toString().toIntOrNull() ?: 0,
            salary = binding.edtDetailSalary.text.toString().toIntOrNull() ?: 0
        )

        ApiClient.getInstance().updateUser(userId, request)
            .enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@UserDetailActivity,
                            "User ID $userId berhasil di-update!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Kembali ke halaman utama dan daftar otomatis ter-refresh
                    } else {
                        Toast.makeText(
                            this@UserDetailActivity,
                            "Gagal update (Kode: ${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    Toast.makeText(
                        this@UserDetailActivity,
                        "Error update: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun deleteUserData() {
        ApiClient.getInstance().deleteUser(userId)
            .enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@UserDetailActivity,
                            "User ID $userId berhasil dihapus!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish() // Kembali ke halaman utama dan daftar otomatis ter-refresh
                    } else {
                        Toast.makeText(
                            this@UserDetailActivity,
                            "Gagal hapus (Kode: ${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    Toast.makeText(
                        this@UserDetailActivity,
                        "Error hapus: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}