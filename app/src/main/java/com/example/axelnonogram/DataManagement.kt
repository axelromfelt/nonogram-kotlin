package com.example.axelnonogram

import androidx.room.*
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Entity(tableName = "nonogram")
data class NonogramData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    var nonogram: String,
    var currentState: String? = null,
    var isComplete: Boolean = false
)


@Dao
interface NonogramDao {
    @Upsert
    suspend fun insertNonogram(nonogram: NonogramData)

    @Update
    suspend fun updateNonogram(nonogram: NonogramData)


    @Query("DELETE FROM nonogram WHERE id = :id")
    suspend fun deleteNonogram(id: Int)

    @Query("SELECT * FROM nonogram WHERE id = :id")
    fun getById(id: Int): NonogramData

    @Query("SELECT * FROM nonogram WHERE type = :type ORDER BY id ASC")
    fun getAllByType(type: String): List<NonogramData>

}

@Database(entities = [NonogramData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nonogramDao(): NonogramDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .createFromAsset("default_database.db")
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class NonogramRepository(private val nonogramDao: NonogramDao) {

    suspend fun insert(nonogramData: NonogramData) {
        nonogramDao.insertNonogram(nonogramData)
    }

    suspend fun update(nonogramData: NonogramData) {
        nonogramDao.updateNonogram(nonogramData)
    }

    suspend fun delete(id: Int) {
        nonogramDao.deleteNonogram(id)
    }

    fun getAllByType(type: String): List<NonogramData> {
        return nonogramDao.getAllByType(type)
    }


}




class NonogramViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NonogramRepository

    private val _defaultNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val defaultNonograms: StateFlow<List<NonogramData>> = _defaultNonograms.asStateFlow()

    private val _importedNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val importedNonograms: StateFlow<List<NonogramData>> = _importedNonograms.asStateFlow()

    private val _userNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val userNonograms: StateFlow<List<NonogramData>> = _userNonograms.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NonogramRepository(database.nonogramDao())
        loadAllNonograms()
    }

    fun loadAllNonograms() {
        viewModelScope.launch {
            loadNonogramsByType("d")
            loadNonogramsByType("i")
            loadNonogramsByType("u")
        }
    }


    private suspend fun loadNonogramsByType(type: String) {
        withContext(Dispatchers.IO) {
            val nonograms = repository.getAllByType(type)
            when (type) {
                "d" -> _defaultNonograms.value = nonograms
                "i" -> _importedNonograms.value = nonograms
                "u" -> _userNonograms.value = nonograms
            }
        }
    }

    fun saveNonogram(nonogramData: NonogramData) {
        viewModelScope.launch {
                repository.update(nonogramData)
                loadNonogramsByType(nonogramData.type)
        }
    }

    fun createNonogram(nonogramData: NonogramData){
        viewModelScope.launch {
            repository.insert(nonogramData)
            loadNonogramsByType(nonogramData.type)

        }
    }

    fun deleteNonogram(id: Int) {
        viewModelScope.launch {
                repository.delete(id)
                loadNonogramsByType("i")
                loadNonogramsByType("u")

        }
    }
}