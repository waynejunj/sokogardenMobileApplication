package com.example.sokogardenonline
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("api/add_product")
    fun uploadProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_description") productDescription: RequestBody,
        @Part("product_cost") productCost: RequestBody,
        @Part("product_category") productCategory: RequestBody,
        @Part product_photo: MultipartBody.Part
    ): Call<ResponseBody>
}
