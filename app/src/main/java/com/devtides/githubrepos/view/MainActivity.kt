package com.devtides.githubrepos.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.devtides.githubrepos.R
import com.devtides.githubrepos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.internal.http.RequestLine.get
import java.lang.reflect.Array.get
import java.nio.file.Paths.get


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    var token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        repositoriesSpinner.isEnabled = false
        repositoriesSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("No repositories available")
        )
        repositoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Load PullRequests
            }
        }


        prsSpinner.isEnabled = false
        prsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select repository")
        )
        prsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }


        commentsSpinner.isEnabled = false
        commentsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please semalect PR")
        )


        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.tokenLD.observe(this, Observer { token ->
            if (token.isNotEmpty()) {
                this.token = token
                loadReposButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Sucess Auth", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Failed Auth", Toast.LENGTH_SHORT).show()

            }
        })

        viewModel.reposLD.observe(this, Observer { repoList ->
            if (!repoList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    repoList
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf("User no repositories")
                )
                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = false
            }
        })

        viewModel.errorLD.observe(this, Observer { messsage ->
            Toast.makeText(this@MainActivity, messsage, Toast.LENGTH_SHORT).show()
        })


    }

    fun onAuthenticate(view: View) {
        val urlOath: String = getString(R.string.oauthUrl)
        val clientId = getString(R.string.clientId)
        val callbackUrl = getString(R.string.callbackUrl)
        val openRepos = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$urlOath?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl")
        )
        startActivity(openRepos)
    }

    override fun onResume() {
        super.onResume()
        val uriU: Uri? = intent.data
        val callback: String = getString(R.string.callbackUrl)
        if (uriU != null && uriU.toString().startsWith(callback)) {
            val codeUrl: String? = uriU.getQueryParameter("code")
            codeUrl?.let {
                val clientId = getString(R.string.clientId)
                val clientSecret = getString(R.string.clientSecret)
                viewModel.configureToken(clientId, clientSecret, codeUrl)
            }
        }
    }

    fun onLoadRepos(view: View) {
        token?.let {
            viewModel.onLoadRepositories(it)
        }

    }

    fun onPostComment(view: View) {

    }

}
