package br.com.igorbag.githubsearch.data.local

import android.provider.BaseColumns

object UserContract {
    object UsuarioEntry : BaseColumns {
        const val TABLE_NAME = "user"
        const val COLUMN_NAME_NOME = "nome"
    }

    const val TABLE_USER =
        "CREATE TABLE ${UsuarioEntry.TABLE_NAME} ( " +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${UsuarioEntry.COLUMN_NAME_NOME} VARCHAR(200))"


    const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS ${UsuarioEntry.TABLE_NAME}"
}