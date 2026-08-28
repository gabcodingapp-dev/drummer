package com.example.autodrum.data

class DrumRepository(private val drumDao: DrumDao) {
    suspend fun getAllKits() = drumDao.getAllKits()
    suspend fun insertKit(kit: KitProfile) = drumDao.insertKit(kit)

    suspend fun getAllSheets() = drumDao.getAllSheets()
    suspend fun insertSheet(sheet: DrumSheet) = drumDao.insertSheet(sheet)
}
