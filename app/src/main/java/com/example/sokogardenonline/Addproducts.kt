package com.example.sokogardenonline
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Addproducts : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var descEdit: EditText
    private lateinit var costEdit: EditText
    private lateinit var sellEdit: EditText
    private lateinit var categoryEdit: EditText
    private lateinit var uploadBtn: Button
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addproducts)

        // Initialize Views
        nameEdit = findViewById(R.id.name)
        descEdit = findViewById(R.id.description)
        costEdit = findViewById(R.id.cost)
        categoryEdit = findViewById(R.id.category)
        sellEdit = findViewById(R.id.sell)
        uploadBtn = findViewById(R.id.btnUpload)

        // Open file picker when EditText clicked
        sellEdit.setOnClickListener { openFileChooser() }

        // Upload product when button clicked
        uploadBtn.setOnClickListener {
            if (nameEdit.text.isEmpty() || descEdit.text.isEmpty() || categoryEdit.text.isEmpty()  ||
                costEdit.text.isEmpty() || imageUri == null
            ) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            } else {
                uploadProduct()
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            imageUri = data.data
            val fileName = getFileNameFromUri(imageUri!!)
            sellEdit.setText(fileName ?: "Image Selected")
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File(cacheDir, getFileNameFromUri(uri) ?: "upload_image")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return tempFile
    }

    private fun uploadProduct() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait as we add your products...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Match backend key names exactly
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), nameEdit.text.toString())
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), descEdit.text.toString())
        val cost = RequestBody.create("text/plain".toMediaTypeOrNull(), costEdit.text.toString())
        val category = RequestBody.create("text/plain".toMediaTypeOrNull(), categoryEdit.text.toString())

        val file = getFileFromUri(imageUri!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("product_photo", file.name, requestFile)

        val call = RetrofitClient.instance.uploadProduct(name, description, cost,category, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(this@Addproducts, "Product added successfully!", Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    Toast.makeText(this@Addproducts, "Upload failed: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@Addproducts, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearFields() {
        nameEdit.text.clear()
        descEdit.text.clear()
        costEdit.text.clear()
        categoryEdit.text.clear()
        sellEdit.text.clear()
        imageUri = null
    }
}
