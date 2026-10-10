package com.ilactakipmobil.app.api

import com.ilactakipmobil.app.model.Ilac
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface IlacApiService {

    // 1. Tüm kayıtlı ilaçları listeleme (GET: api/ilaclar)
    @GET("api/ilaclar")
    suspend fun getIlaclar(): Response<List<Ilac>>

    // ID'ye göre tek ilaç getirme
    @GET("api/ilaclar/{id}")
    suspend fun getIlacById(@Path("id") id: Int): Response<Ilac>

    // 2. Yeni ilaç ekleme (POST: api/ilaclar)
    @POST("api/ilaclar")
    suspend fun addIlac(@Body ilac: Ilac): Response<Ilac>

    // 3. Alarm durumunu açıp/kapatma toggle (PATCH: api/ilaclar/{id}/durum)
    @PATCH("api/ilaclar/{id}/durum")
    suspend fun toggleDurum(@Path("id") id: Int): Response<Ilac>

    // 4. İlaç silme (DELETE: api/ilaclar/{id})
    @DELETE("api/ilaclar/{id}")
    suspend fun deleteIlac(@Path("id") id: Int): Response<Unit>
}

