package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>, val contexto: Context) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repositoryItemLister: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            nomeRepositorio.text = repositories[position].name

            // Exemplo de click no item
            btnShare.setOnClickListener {
                val repository = repositories[position]
                repositoryItemLister(repository)
                shareRepositoryLink(repository.htmlUrl)
            }

            // Exemplo de click no btn Share
            nomeRepositorio.setOnClickListener {
                val repository = repositories[position]
                btnShareLister(repository)
                openBrowser(repository.htmlUrl)
            }
        }
    }

    // Pega a quantidade de repositorios da lista
    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeRepositorio: TextView
        val btnShare: ImageView
        init {
            view.apply {
                nomeRepositorio = findViewById(R.id.tv_nome_repositorio)
                btnShare = findViewById(R.id.iv_share)
            }
        }
    }

    private fun  shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        contexto.startActivity(shareIntent)
    }
    private fun openBrowser(urlRepository: String) {
        contexto.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }
}


