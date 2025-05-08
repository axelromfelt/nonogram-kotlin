//package com.example.axelnonogram
//
//package com.example.axelnonogram
//
//import android.content.Context
//import androidx.room.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import java.util.*
//
///**
// * Puzzle data model
// */
//@Serializable
//data class NonogramPuzzle(
//    val id: String,
//    val name: String,
//    val difficulty: Difficulty,
//    val size: Int,
//    val solution: Array<BooleanArray>,
//    val rowClues: List<List<Int>>,
//    val colClues: List<List<Int>>
//) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as NonogramPuzzle
//
//        if (id != other.id) return false
//        if (name != other.name) return false
//        if (difficulty != other.difficulty) return false
//        if (size != other.size) return false
//        if (!solution.contentDeepEquals(other.solution)) return false
//        if (rowClues != other.rowClues) return false
//        if (colClues != other.colClues) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = id.hashCode()
//        result = 31 * result + name.hashCode()
//        result = 31 * result + difficulty.hashCode()
//        result = 31 * result + size
//        result = 31 * result + solution.contentDeepHashCode()
//        result = 31 * result + rowClues.hashCode()
//        result = 31 * result + colClues.hashCode()
//        return result
//    }
//}
//
///**
// * In-progress puzzle state
// */
//@Entity(tableName = "puzzle_progress")
//data class PuzzleProgress(
//    @PrimaryKey val puzzleId: String,
//    val currentGrid: String, // JSON serialized 2D array of cell states
//    val timeSpentInSeconds: Int,
//    val mistakesMade: Int,
//    val lastPlayedTimestamp: Long
//)
//
///**
// * Completed puzzle data
// */
//@Entity(tableName = "completed_puzzles")
//data class CompletedPuzzle(
//    @PrimaryKey val puzzleId: String,
//    val completedTimestamp: Long,
//    val timeToCompleteInSeconds: Int,
//    val mistakesMade: Int,
//    val perfectSolve: Boolean // No mistakes
//)
//
///**
// * Player statistics
// */
//@Entity(tableName = "player_stats")
//data class PlayerStats(
//    @PrimaryKey val id: Int = 1, // Single row for stats
//    val totalPuzzlesCompleted: Int = 0,
//    val totalPerfectSolves: Int = 0,
//    val totalPlayTimeInSeconds: Int = 0,
//    val longestStreak: Int = 0,
//    val currentStreak: Int = 0,
//    val lastPlayDate: Long = 0
//)
//
///**
// * Room DAO for puzzle progress
// */
//@Dao
//interface PuzzleProgressDao {
//    @Query("SELECT * FROM puzzle_progress WHERE puzzleId = :puzzleId")
//    fun getProgressForPuzzle(puzzleId: String): PuzzleProgress?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveProgress(progress: PuzzleProgress)
//
//    @Query("DELETE FROM puzzle_progress WHERE puzzleId = :puzzleId")
//    suspend fun deletePuzzleProgress(puzzleId: String)
//
//    @Query("SELECT * FROM puzzle_progress")
//    fun getAllInProgressPuzzles(): Flow<List<PuzzleProgress>>
//}
//
///**
// * Room DAO for completed puzzles
// */
//@Dao
//interface CompletedPuzzleDao {
//    @Query("SELECT * FROM completed_puzzles")
//    fun getAllCompletedPuzzles(): Flow<List<CompletedPuzzle>>
//
//    @Query("SELECT EXISTS(SELECT 1 FROM completed_puzzles WHERE puzzleId = :puzzleId)")
//    fun isPuzzleCompleted(puzzleId: String): Boolean
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun markPuzzleAsCompleted(completedPuzzle: CompletedPuzzle)
//}
//
///**
// * Room DAO for player statistics
// */
//@Dao
//interface PlayerStatsDao {
//    @Query("SELECT * FROM player_stats WHERE id = 1")
//    fun getPlayerStats(): PlayerStats?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun updatePlayerStats(stats: PlayerStats)
//}
//
///**
// * Type converters for Room database
// */
//class Converters {
//    @TypeConverter
//    fun fromString(value: String): Array<BooleanArray> {
//        return Json.decodeFromString(value)
//    }
//
//    @TypeConverter
//    fun toString(array: Array<BooleanArray>): String {
//        return Json.encodeToString(array)
//    }
//}
//
///**
// * Room database
// */
//@Database(
//    entities = [PuzzleProgress::class, CompletedPuzzle::class, PlayerStats::class],
//    version = 1,
//    exportSchema = false
//)
//@TypeConverters(Converters::class)
//abstract class NonogramDatabase : RoomDatabase() {
//    abstract fun puzzleProgressDao(): PuzzleProgressDao
//    abstract fun completedPuzzleDao(): CompletedPuzzleDao
//    abstract fun playerStatsDao(): PlayerStatsDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: NonogramDatabase? = null
//
//        fun getDatabase(context: Context): NonogramDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    NonogramDatabase::class.java,
//                    "nonogram_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
//
///**
// * Game progress repository
// */
//class GameProgressRepository(private val context: Context) {
//    private val database = NonogramDatabase.getDatabase(context)
//    private val puzzleProgressDao = database.puzzleProgressDao()
//    private val completedPuzzleDao = database.completedPuzzleDao()
//    private val playerStatsDao = database.playerStatsDao()
//
//    // For serializing/deserializing grid state
//    private val json = Json { ignoreUnknownKeys = true }
//
//    /**
//     * Save in-progress puzzle state
//     */
//    suspend fun savePuzzleProgress(
//        puzzleId: String,
//        currentGrid: Array<Array<CellState>>,
//        timeSpentInSeconds: Int,
//        mistakesMade: Int
//    ) = withContext(Dispatchers.IO) {
//        val gridJson = json.encodeToString(currentGrid)
//        val progress = PuzzleProgress(
//            puzzleId = puzzleId,
//            currentGrid = gridJson,
//            timeSpentInSeconds = timeSpentInSeconds,
//            mistakesMade = mistakesMade,
//            lastPlayedTimestamp = System.currentTimeMillis()
//        )
//        puzzleProgressDao.saveProgress(progress)
//    }
//
//    /**
//     * Load in-progress puzzle state
//     */
//    suspend fun loadPuzzleProgress(puzzleId: String): PuzzleProgressState? = withContext(Dispatchers.IO) {
//        val progress = puzzleProgressDao.getProgressForPuzzle(puzzleId) ?: return@withContext null
//
//        val grid = json.decodeFromString<Array<Array<CellState>>>(progress.currentGrid)
//        return@withContext PuzzleProgressState(
//            grid = grid,
//            timeSpentInSeconds = progress.timeSpentInSeconds,
//            mistakesMade = progress.mistakesMade
//        )
//    }
//
//    /**
//     * Check if a puzzle is completed
//     */
//    suspend fun isPuzzleCompleted(puzzleId: String): Boolean = withContext(Dispatchers.IO) {
//        completedPuzzleDao.isPuzzleCompleted(puzzleId)
//    }
//
//    /**
//     * Mark a puzzle as completed
//     */
//    suspend fun markPuzzleAsCompleted(
//        puzzleId: String,
//        timeToCompleteInSeconds: Int,
//        mistakesMade: Int
//    ) = withContext(Dispatchers.IO) {
//        // Mark as completed
//        val completedPuzzle = CompletedPuzzle(
//            puzzleId = puzzleId,
//            completedTimestamp = System.currentTimeMillis(),
//            timeToCompleteInSeconds = timeToCompleteInSeconds,
//            mistakesMade = mistakesMade,
//            perfectSolve = mistakesMade == 0
//        )
//        completedPuzzleDao.markPuzzleAsCompleted(completedPuzzle)
//
//        // Remove from in-progress
//        puzzleProgressDao.deletePuzzleProgress(puzzleId)
//
//        // Update player stats
//        updatePlayerStats(completedPuzzle.perfectSolve, timeToCompleteInSeconds)
//    }
//
//    /**
//     * Get all puzzles in progress
//     */
//    fun getAllInProgressPuzzles(): Flow<List<PuzzleProgress>> {
//        return puzzleProgressDao.getAllInProgressPuzzles()
//    }
//
//    /**
//     * Get all completed puzzles
//     */
//    fun getAllCompletedPuzzles(): Flow<List<CompletedPuzzle>> {
//        return completedPuzzleDao.getAllCompletedPuzzles()
//    }
//
//    /**
//     * Get player statistics
//     */
//    suspend fun getPlayerStats(): PlayerStats = withContext(Dispatchers.IO) {
//        return@withContext playerStatsDao.getPlayerStats() ?: PlayerStats()
//    }
//
//    /**
//     * Update player statistics after completing a puzzle
//     */
//    private suspend fun updatePlayerStats(isPerfectSolve: Boolean, timeSpent: Int) {
//        val currentStats = playerStatsDao.getPlayerStats() ?: PlayerStats()
//
//        // Calculate streak
//        val currentTime = System.currentTimeMillis()
//        val oneDayMillis = 24 * 60 * 60 * 1000
//        val isConsecutiveDay = currentTime - currentStats.lastPlayDate < oneDayMillis
//
//        val newStreak = if (isConsecutiveDay) currentStats.currentStreak + 1 else 1
//        val newLongestStreak = maxOf(newStreak, currentStats.longestStreak)
//
//        val updatedStats = currentStats.copy(
//            totalPuzzlesCompleted = currentStats.totalPuzzlesCompleted + 1,
//            totalPerfectSolves = currentStats.totalPerfectSolves + if (isPerfectSolve) 1 else 0,
//            totalPlayTimeInSeconds = currentStats.totalPlayTimeInSeconds + timeSpent,
//            currentStreak = newStreak,
//            longestStreak = newLongestStreak,
//            lastPlayDate = currentTime
//        )
//
//        playerStatsDao.updatePlayerStats(updatedStats)
//    }
//}
//
///**
// * Data class to hold the current state of a puzzle in progress
// */
//data class PuzzleProgressState(
//    val grid: Array<Array<CellState>>,
//    val timeSpentInSeconds: Int,
//    val mistakesMade: Int
//) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as PuzzleProgressState
//
//        if (!grid.contentDeepEquals(other.grid)) return false
//        if (timeSpentInSeconds != other.timeSpentInSeconds) return false
//        if (mistakesMade != other.mistakesMade) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = grid.contentDeepHashCode()
//        result = 31 * result + timeSpentInSeconds
//        result = 31 * result + mistakesMade
//        return result
//    }
//}
//
///**
// * Cell state enum for nonogram grid
// */
//@Serializable
//enum class CellState {
//    EMPTY, FILLED, MARKED
//}
//
///**
// * Example of using the GameProgressRepository in a ViewModel
// */
//class NonogramViewModel(application: Context) {
//    private val gameProgressRepository = GameProgressRepository(application)
//
//    // Example puzzle data
//    private val examplePuzzle = NonogramPuzzle(
//        id = "puzzle_1",
//        name = "Heart",
//        difficulty = Difficulty.EASY,
//        size = 5,
//        solution = Array(5) { BooleanArray(5) },  // Your solution here
//        rowClues = listOf(listOf(1,1), listOf(3), listOf(5), listOf(3), listOf(1)),
//        colClues = listOf(listOf(1), listOf(3), listOf(5), listOf(3), listOf(1))
//    )
//
//    // Current game state properties
//    private var currentGrid = Array(examplePuzzle.size) {
//        Array(examplePuzzle.size) { CellState.EMPTY }
//    }
//    private var timeSpent = 0
//    private var mistakes = 0
//
//    // Save current progress
//    suspend fun saveProgress() {
//        gameProgressRepository.savePuzzleProgress(
//            puzzleId = examplePuzzle.id,
//            currentGrid = currentGrid,
//            timeSpentInSeconds = timeSpent,
//            mistakesMade = mistakes
//        )
//    }
//
//    // Load saved progress
//    suspend fun loadProgress() {
//        val savedProgress = gameProgressRepository.loadPuzzleProgress(examplePuzzle.id)
//        if (savedProgress != null) {
//            currentGrid = savedProgress.grid
//            timeSpent = savedProgress.timeSpentInSeconds
//            mistakes = savedProgress.mistakesMade
//        }
//    }
//
//    // Check if puzzle is already completed
//    suspend fun checkIfCompleted(): Boolean {
//        return gameProgressRepository.isPuzzleCompleted(examplePuzzle.id)
//    }
//
//    // Mark current puzzle as completed
//    suspend fun completeCurrentPuzzle() {
//        gameProgressRepository.markPuzzleAsCompleted(
//            puzzleId = examplePuzzle.id,
//            timeToCompleteInSeconds = timeSpent,
//            mistakesMade = mistakes
//        )
//    }
//}
//
///**
// * Example usage in a Composable
// */
//@Composable
//fun NonogramGameScreen(
//    puzzleId: String,
//    viewModel: NonogramViewModel,
//    onBack: () -> Unit
//) {
//    // Implementation of the game UI
//    // This would use the viewModel to manage the game state
//    // and call functions to save/load progress as needed
//}