package com.altankoc.yemekkitab.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.altankoc.yemekkitab.model.Yemek

@Database(entities = [Yemek::class], version = 1)
abstract class YemekDatabase: RoomDatabase() {

    abstract fun yemekDao(): YemekDao
}