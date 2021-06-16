 package com.ghostcleaner.screen.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.SKU_ADS
import com.ghostcleaner.SUB_ADS2
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.*
import com.ghostcleaner.screen.SuccessActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.GPayClient
import kotlinx.android.synthetic.main.fragment_noads.*

class NoAdsFragment : BaseFragment<Int>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_noads, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_disable_1.setOnClickListener {
//            val intent = Intent(context, DoneActivity::class.java)
//            startActivityForResult(intent, REQUEST_ADS)
            GPayClient.getInstance(requireContext()).subscribe(requireActivity(), SUB_ADS2)
            Log.d("TAG_ERROR", "AFTER GPAYCLIENT SUBSCRIBE")

        }

        Log.d("TAG_ERROR", "STEP BEFORE IF")

        GPayClient.getInstance(requireContext()).purchases.observeFreshly(this, {
            if (it == SKU_ADS || it == SUB_ADS2) {
//                startActivity(intentFor<SuccessActivity>().putExtras(intent))
                Log.d("TAG_ERROR", "SKUADSSUBADS true")
                val intent = Intent(requireContext(), SuccessActivity::class.java)
                intent.putExtras(intent)
                startActivity(intent)
                requireActivity().finish()
            }
        })

        Log.d("TAG_ERROR", "STEP AFTER IF")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GPayClient.getInstance(requireContext()).onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        fun newInstance(): NoAdsFragment {
            return NoAdsFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}