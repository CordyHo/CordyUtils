package com.cordyho.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cordyho.utils.ActivityStackManager
import com.cordyho.utils.R

/*自定义BaseActivity继承此Activity才可以使用堆栈管理 ActivityStackManager */
abstract class CordyBaseActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var srlRefresh: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStackManager.addToStack(this)
    }

    open fun initView() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        srlRefresh = findViewById(R.id.srl_refresh)
        srlRefresh?.let {
            it.setOnRefreshListener(this)
            it.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
            it.setProgressViewOffset(true, 0, 100)
        }
    }

    fun stopRefresh() {
        srlRefresh?.post { srlRefresh?.isRefreshing = false }
    }

    fun startRefresh() {
        srlRefresh?.post { srlRefresh?.isRefreshing = true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStackManager.onDestroyRemove(this)
    }
}