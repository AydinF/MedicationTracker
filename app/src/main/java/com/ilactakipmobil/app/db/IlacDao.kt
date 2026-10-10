package com.ilactakipmobil.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IlacDao {

    @Query("SELECT * FROM ilaclar ORDER BY id DESC")
    suspend fun getTumIlaclar(): List<IlacEntity>

    @Query("SELECT * FROM ilaclar WHERE id = :id LIMIT 1")
    suspend fun getIlacById(id: Int): IlacEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIlac(ilac: IlacEntity): Long

    @Update
    suspend fun updateIlac(ilac: IlacEntity)

    @Query("DELETE FROM ilaclar WHERE id = :id")
    suspend fun deleteIlacById(id: Int)
}
