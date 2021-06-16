package com.ghostcleaner.screen.base

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ghostcleaner.EXTRA_TITLE
import com.ghostcleaner.R
import com.ghostcleaner.service.D
import io.github.inflationx.calligraphy3.CalligraphyUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.longSnackbar
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    protected val job = SupervisorJob()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (intent.hasExtra(EXTRA_TITLE)) {
            setSubtitle(intent.getStringExtra(EXTRA_TITLE))
        }
    }

    protected fun setSubtitle(key: String?) {
        findViewById<TextView>(R.id.tv_title)?.text = D[key ?: return]
    }

    override fun setTitle(title: CharSequence?) {
        val font = Typeface.createFromAsset(assets, "font/Ubuntu-Medium.ttf")
        super.setTitle(CalligraphyUtils.applyTypefaceSpan(title, font))
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun showMessage(message: CharSequence) {
        contentView?.longSnackbar(message)
    }

    protected open fun showError(e: Throwable) {
        showMessage(e.message ?: e.toString())
    }

    override fun onDestroy() {
        job.cancelChildren()
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException && !isFinishing) {
            runOnUiThread {
                showError(e)
            }
        }
    }
}