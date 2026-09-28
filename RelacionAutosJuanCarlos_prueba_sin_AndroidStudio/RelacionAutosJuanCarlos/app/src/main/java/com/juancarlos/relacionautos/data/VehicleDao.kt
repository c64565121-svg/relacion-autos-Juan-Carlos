package com.juancarlos.relacionautos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Insert
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Query("SELECT * FROM vehiculos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Vehicle>>

    @Query("""
        SELECT * FROM vehiculos
        WHERE chassis LIKE '%' || :q || '%'
           OR model LIKE '%' || :q || '%'
        ORDER BY createdAt DESC
    """)
    fun search(q: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehiculos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Vehicle?
}
