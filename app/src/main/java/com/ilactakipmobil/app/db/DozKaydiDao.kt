package com.ilactakipmobil.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DozKaydiDao {

    @Query("SELECT * FROM doz_kayitlari WHERE planlananTarih = :tarih ORDER BY planlananSaat ASC")
    suspend fun getDozlarByTarih(tarih: String): List<DozKaydiEntity>

    @Query("SELECT * FROM doz_kayitlari WHERE planlananTarih BETWEEN :baslangic AND :bitis ORDER BY planlananTarih DESC, planlananSaat ASC")
    suspend fun getDozlarByTarihAraligi(baslangic: String, bitis: String): List<DozKaydiEntity>

    @Query("SELECT * FROM doz_kayitlari WHERE ilacAdi = :ilacAdi AND (planlananTarih BETWEEN :baslangic AND :bitis) ORDER BY planlananTarih DESC, planlananSaat ASC")
    suspend fun getDozlarByIlacAndTarihAraligi(ilacAdi: String, baslangic: String, bitis: String): List<DozKaydiEntity>

    @Query("SELECT * FROM doz_kayitlari WHERE ilacId = :ilacId AND planlananTarih = :tarih AND planlananSaat = :saat LIMIT 1")
    suspend fun getDozByIlacAndTarihAndSaat(ilacId: Int, tarih: String, saat: String): DozKaydiEntity?

    @Query("SELECT * FROM doz_kayitlari WHERE id = :id LIMIT 1")
    suspend fun getDozById(id: Int): DozKaydiEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDozKaydi(doz: DozKaydiEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDozKayitlari(dozlar: List<DozKaydiEntity>)

    @Update
    suspend fun updateDozKaydi(doz: DozKaydiEntity)

    @Query("DELETE FROM doz_kayitlari WHERE ilacId = :ilacId")
    suspend fun deleteDozlarByIlacId(ilacId: Int)
}
