package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.data.local.UserRepository
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.domain.User
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        val nome = nomeUsuario.text.toString()
        val user = User(nome = nome)
        if(UserRepository(this).saveIfNotExists(user)) {
            getAllReposByUserName()
        }
    }

    private fun showUserName() {
        val user : User = UserRepository(this).getFirst()
        if(user.id > 0) {
            nomeUsuario.setText(user.nome)
        }
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    private fun getAllReposByUserName() {
        val contexto : Context = this
        githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString()).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if(response.isSuccessful) {
                    response.body()?.let{
                        setupAdapter(it, contexto)
                        return
                    }
                }
                Toast.makeText(contexto, R.string.response_error, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(contexto, R.string.response_error, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>, context: Context) {
        val repositoryAdapter = RepositoryAdapter(list, context)
        listaRepositories.apply {
            visibility = View.VISIBLE
            adapter = repositoryAdapter
        }
    }
}