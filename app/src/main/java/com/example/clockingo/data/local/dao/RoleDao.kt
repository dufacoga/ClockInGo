package com.example.clockingo.data.local.dao

import androidx.room.*
import com.example.clockingo.data.local.model.RoleEntity

@Dao
interface RoleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: RoleEntity)

    @Update
    suspend fun update(role: RoleEntity)

    @Delete
    suspend fun delete(location: RoleEntity)

    @Query("DELETE FROM roles")
    suspend fun deleteAllRoles()

    @Query("SELECT * FROM roles WHERE id = :id")
    suspend fun getRoleById(id: Int): RoleEntity?

    @Query("SELECT * FROM roles WHERE name = :name")
    suspend fun getRoleByName(name: String): RoleEntity?

    @Query("SELECT * FROM roles")
    suspend fun getAllRoles(): List<RoleEntity>
}