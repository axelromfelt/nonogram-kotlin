package com.example.axelnonogram
import android.R
import androidx.room.*
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Dictionary


@Entity(tableName = "nonogram")
data class NonogramData(
//    @PrimaryKey(autoGenerate = true)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val nonogram: String,
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
            Log.d("AppDatabase", "Database Created")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Database name
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

    suspend fun insert(nonogram: String, type: String) {
        nonogramDao.insertNonogram(NonogramData(type=type,nonogram=nonogram))
    }

    suspend fun update(nonogramData: NonogramData) {
        nonogramDao.updateNonogram(nonogramData)
    }

    suspend fun delete(id: Int) {
        nonogramDao.deleteNonogram(id)
    }

    fun getById(id: Int): NonogramData{
        return nonogramDao.getById(id)
    }

    fun getAllByType(type: String): List<NonogramData> {
        return nonogramDao.getAllByType(type)
    }


}




class NonogramViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NonogramRepository

    // StateFlow for each nonogram type
    private val _defaultNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val defaultNonograms: StateFlow<List<NonogramData>> = _defaultNonograms.asStateFlow()

    private val _importedNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val importedNonograms: StateFlow<List<NonogramData>> = _importedNonograms.asStateFlow()

    private val _userNonograms = MutableStateFlow<List<NonogramData>>(emptyList())
    val userNonograms: StateFlow<List<NonogramData>> = _userNonograms.asStateFlow()



    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    init {
        val database = AppDatabase.getDatabase(application)
        repository = NonogramRepository(database.nonogramDao())
        loadAllNonograms()
    }

    fun loadAllNonograms() {
        _isLoading.value = true
        viewModelScope.launch {

            loadNonogramsByType("d")
            loadNonogramsByType("i")
            loadNonogramsByType("u")


        }
        _isLoading.value = false

    }


    private suspend fun loadNonogramsByType(type: String) {
        withContext(Dispatchers.IO) {
            val nonograms = repository.getAllByType(type)
            Log.d("NonogramLoad", "Type $type has ${nonograms.size} entries")

            when (type) {
                "d" -> _defaultNonograms.value = nonograms
                "i" -> _importedNonograms.value = nonograms
                "u" -> _userNonograms.value = nonograms
            }
        }
    }

//    fun getNonogram(id: Int): NonogramData{
//        viewModelScope.launch {
//
//            val nonogram = repository.getById(id)
//            return nonogram
//        }
//    }

    fun saveNonogram(nonogramData: NonogramData) {
        viewModelScope.launch {

                repository.update(nonogramData)

                loadNonogramsByType(nonogramData.type)


        }
    }
    fun createNonogram(nonogram: String,type:String){
        viewModelScope.launch {

            repository.insert(nonogram, type)

//            loadNonogramsByType(nonogramData.type)
            loadNonogramsByType("i")
//            loadNonogramsByType(nonogramData.type)

        }
    }

    fun updateNonogramProgress(nonogramData: NonogramData, currentState: String, isComplete: Boolean) {
        val updatedNonogram = nonogramData.copy(
            currentState = currentState,
            isComplete = isComplete
        )
        saveNonogram(updatedNonogram)
    }

    fun deleteNonogram(id: Int) {
        viewModelScope.launch {
                repository.delete(id)
                loadNonogramsByType("i")
                loadNonogramsByType("u")

        }
    }
}