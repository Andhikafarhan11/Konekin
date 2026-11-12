package com.example.konekin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.konekin.databinding.ActivityMainBinding
import com.example.konekin.model.Data
import com.example.konekin.model.UserRequest
import com.example.konekin.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userList = ArrayList<String>()
    private val rawDataList = ArrayList<Data>()
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            userList
        )
        binding.lvNama.adapter = listAdapter

        // Ketika user diklik -> Buka UserDetailActivity membawa parameter ID
        binding.lvNama.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = rawDataList[position]
            val intent = Intent(this@MainActivity, UserDetailActivity::class.java).apply {
                putExtra("EXTRA_ID", selectedUser.id)
            }
            startActivity(intent)
        }

        // Ketika tombol Tambah User diklik -> Munculkan form ADD User (POST)
        binding.btnAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Otomatis memuat ulang daftar terbaru dari server setiap kali halaman tampil
        fetchUsers()
    }

    private fun fetchUsers() {
        ApiClient.getInstance().getAllUsers().enqueue(object : Callback<List<Data>> {
            override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    userList.clear()
                    rawDataList.clear()
                    for (item in body) {
                        rawDataList.add(item)
                        userList.add("${item.id}. ${item.employeeName}")
                    }
                    listAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Gagal memuat daftar (Kode: ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Koneksi error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showAddUserDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val edtName = EditText(this).apply { hint = "Nama Karyawan" }
        val edtAge = EditText(this).apply { hint = "Umur (Age)" }
        val edtSalary = EditText(this).apply { hint = "Gaji (Salary)" }

        layout.addView(edtName)
        layout.addView(edtAge)
        layout.addView(edtSalary)

        AlertDialog.Builder(this)
            .setTitle("Tambah User Baru (POST)")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val name = edtName.text.toString()
                val age = edtAge.text.toString().toIntOrNull() ?: 0
                val salary = edtSalary.text.toString().toIntOrNull() ?: 0
                createNewUser(name, salary, age)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createNewUser(name: String, salary: Int, age: Int) {
        val request = UserRequest(name = name, salary = salary, age = age)
        ApiClient.getInstance().createUser(request).enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Berhasil menambahkan user: $name",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchUsers() // Muat ulang daftar dari server
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Gagal menambah user (Kode: ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}