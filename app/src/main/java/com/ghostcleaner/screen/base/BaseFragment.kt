package com.ghostcleaner.screen.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.ghostcleaner.EXTRA_TITLE
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.service.Optimization
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment<T> : Fragment(), CoroutineScope, Optimization<T> {

    protected val job = SupervisorJob()

    protected open var titleKey = ""

    protected val args: Bundle
        get() = arguments ?: Bundle()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.nal)?.setOnClickListener {
            context?.run {
                startActivity(intentFor<OfferActivity>(EXTRA_TITLE to titleKey).newTask())
            }
        }
    }

    override fun beforeOptimize() {
    }

    override fun onOptimize(value: T) {
    }

    override fun afterOptimize() {
    }

    protected open fun showMessage(message: CharSequence) {
        view?.longSnackbar(message)
    }

    protected open fun showError(e: Throwable) {
        showMessage(e.message ?: e.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult requestCode=$requestCode resultCode=$resultCode")
        if (requestCode == REQUEST_ADS) {
            afterOptimize()
        }
    }

    override fun onDestroyView() {
        job.cancelChildren()
        super.onDestroyView()
    }

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException && view != null) {
            activity?.runOnUiThread {
                showError(e)
            }
        }
    }
}