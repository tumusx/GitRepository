package com.devtides.githubrepos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtides.githubrepos.model.GithubToken
import com.devtides.githubrepos.model.ServiceGithub
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    private val configureComposite = CompositeDisposable()

    val tokenLD = MutableLiveData<String>()
    val errorLD = MutableLiveData<String>()

    fun configureToken(clienId: String, clientSecret: String, code: String) {
        configureComposite.add(
            ServiceGithub.getNoAuth().getAuthToken(clienId, clientSecret, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GithubToken>() {
                    override fun onSuccess(t: GithubToken) {
                        tokenLD.value = t.accessToken
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        errorLD.value = "Cannot sucess loading token"
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        configureComposite.clear()
    }
}