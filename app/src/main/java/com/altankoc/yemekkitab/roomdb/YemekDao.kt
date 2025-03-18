package com.altankoc.yemekkitab.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.altankoc.yemekkitab.model.Yemek
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface YemekDao {

    @Query("SELECT * FROM Yemek")
    fun getAll(): Flowable<List<Yemek>>

    @Query("SELECT * FROM Yemek WHERE id= :id")
    fun findById(id: Int): Flowable<Yemek>

    @Insert
    fun insert(yemek: Yemek) : Completable

    @Delete
    fun delete(yemek: Yemek) : Completable

}