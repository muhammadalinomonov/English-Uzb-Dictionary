package uz.gita.a5.mydicrionary.data

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor

class AppDatabase private constructor(private val context: Context) :
    DBHelper(context, "dictionary_external.db", 1) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: AppDatabase? = null
        fun getInstance() = instance

        fun init(context: Context) {
            if (instance == null) {
                instance = AppDatabase(context)
            }
        }
    }


    fun getAllEnglishWords(): Cursor {
        return database().rawQuery("SELECT * FROM dictionary ORDER BY english", null)
    }

    fun getAllUzbekWords(): Cursor {
        return database().rawQuery("SELECT * FROM dictionary ORDER BY uzbek", null)
    }

    fun search(word: String, lang: String): Cursor {
        return database().rawQuery("select * from dictionary where $lang like \"$word%\" order by $lang" , null)
    }

    fun addFavourite(id: Int) {
        database().execSQL("UPDATE dictionary set favourite=1 WHERE id = $id")
    }

    fun removeFavourite(id: Int) {
        database().execSQL("update dictionary set favourite=0 where id=$id")
    }

    fun getAllFavourites(): Cursor {
        return database().rawQuery("SELECT * FROM dictionary WHERE favourite = 1", null)
    }
}