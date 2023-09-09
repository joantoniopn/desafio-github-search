package br.com.igorbag.githubsearch.data.local

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import br.com.igorbag.githubsearch.data.local.UserContract.UsuarioEntry.COLUMN_NAME_NOME
import br.com.igorbag.githubsearch.domain.User

class UserRepository(private val context: Context) {
    private fun save(user: User) : Boolean {
        return try {
            val dbHelper = UserDbHelper(context)
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_NAME_NOME, user.nome)
            }
            db?.insert(UserContract.UsuarioEntry.TABLE_NAME, null, values) != null
        }catch (ex: Exception) {
            ex.message?.let {
                Log.e("ERRO_SAVE_USER", it)
            }
            false
        }
    }

    fun findUserById(nome: String): User {
        val dbHelper = UserDbHelper(context)
        val db = dbHelper.readableDatabase
        val columns = arrayOf(
            BaseColumns._ID,
            COLUMN_NAME_NOME
        )
        val filtro = "$COLUMN_NAME_NOME LIKE ?"
        val filterValues = arrayOf(nome)
        val cursor = db.query(
            UserContract.UsuarioEntry.TABLE_NAME,
            columns,
            filtro,
            filterValues,
            null,
            null,
            null
        )

        var itemId : Long = 0
        var nome : String = ""

        with(cursor) {
            while(moveToNext()) {
                itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                nome = getString(getColumnIndexOrThrow(COLUMN_NAME_NOME))
            }
        }
        cursor.close()
        return User(
            id = itemId.toInt(),
            nome = nome
        )
    }

    fun getFirst(): User {
        val dbHelper = UserDbHelper(context)
        val db = dbHelper.readableDatabase
        val columns = arrayOf(
            BaseColumns._ID,
            COLUMN_NAME_NOME
        )

        val cursor = db.query(
            UserContract.UsuarioEntry.TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            null
        )

        var user = User(nome = "", id = 0)
        if (cursor.moveToFirst()) {
            user = User(
                nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NOME)),
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)).toInt()
            )
        }

        cursor.close()
        db.close();
        return user
    }

    fun saveIfNotExists(user: User): Boolean {
        val userFind = findUserById(user.nome)
        if(userFind.id == ID_WHEN_NO_USER) {
            return save(user)
        }
        return false
    }

    companion object {
        const val ID_WHEN_NO_USER = 0
    }
}