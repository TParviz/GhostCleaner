package com.ghostcleaner.screen.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.AdmobClient
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_app_manager.*
import kotlinx.coroutines.*
import org.jetbrains.anko.find
import java.lang.ref.WeakReference

class AppsManagerFragment : BaseFragment<Int>(), CoroutineScope by MainScope(){

    private var adapter: AppsAdapter? = null
    override var titleKey = "titleAppManager"
    lateinit var mAdView12 : AdView



    internal lateinit var appList: MutableList<AppInfo>
    private lateinit var appListAlternate: MutableList<AppInfo>
    internal lateinit var userAppList: MutableList<AppInfo>
    internal lateinit var systemAppList: MutableList<AppInfo>


    private var appManOb: AppManager? = null

    private lateinit var activityContext: Context

    private var apkInfoExtractor: ApkInfoExtractor? = null

    internal var arrAppType: Array<String>? = null
    private var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    internal var numberOfUserApps: String? = Constants.STRING_EMPTY
    internal var numberOfSystemApps: String? = Constants.STRING_EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        junkManager = JunkManager(requireContext())


    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {

        return inflater.inflate(R.layout.fragment_app_manager, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGoogleBanner()

        mAdView12 = view.findViewById(R.id.ads_banner_12)

        val preffs = Preferences(requireContext())
        if (preffs.enableAds) {
            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
            mAdView12.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))
        }

        appList = ArrayList()
        arrAppType = arrayOf("User Apps", "System Apps")

        appManOb = AppManager()

        userAppList = ArrayList()
        systemAppList = ArrayList()
        appListAlternate = ArrayList()

        activityContext = requireContext()
        apkInfoExtractor = ApkInfoExtractor(requireContext())
        recyclerViewLayoutManager = GridLayoutManager(activityContext, 1)
        getApps(activityContext)
    }


    fun getApps(context: Context) {
        val contextRef: WeakReference<Context> = WeakReference(context)

        //Coroutine
        launch(Dispatchers.Default) {
            try {
                val context1 = contextRef.get()

                appManOb = ApkInfoExtractor(context).appManagerInitValues()

                if (appManOb != null) {
                    numberOfUserApps = Constants.STRING_EMPTY + appManOb!!.userAppSize
                    numberOfSystemApps = Constants.STRING_EMPTY + appManOb!!.systemAppSize

                    userAppList.addAll(appManOb!!.userApps)
                    systemAppList.addAll(appManOb!!.systemApps)

                    appListAlternate.addAll(userAppList)
                    appList.addAll(userAppList)

                    adapter = AppsAdapter(context, appListAlternate)

                } else {
                    numberOfUserApps = Constants.STRING_EMPTY + "0"
                    numberOfSystemApps = Constants.STRING_EMPTY + "0"

                    userAppList.clear()
                    systemAppList.clear()
                    appListAlternate.clear()
                    appList.clear()

                    adapter = AppsAdapter(context, appListAlternate)
                }

                //UI Thread
                withContext(Dispatchers.Main) {

                    recycler_view_Apps.layoutManager = recyclerViewLayoutManager

                    if (adapter!!.itemCount > 0) {
                        recycler_view_Apps.adapter = adapter
                        val text = "$numberOfUserApps User apps"
                        app_Counter_App_Manager.text = text

                        val textUser = "$numberOfUserApps"
                        val textModeType = "User apps"
                        app_Counter_App_Manager.text = textUser
                        tv_modes_apps.text = textModeType
                        appList.clear()
                        appList.addAll(userAppList)
                        adapter?.updateList(userAppList)

//                        val swithcerMode = findViewById<Switch>(R.id.switcher_modes)

                        switcher_modes.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked)  {
                                val textSystem = "$numberOfSystemApps"
                                val textModeType = "System apps"
                                app_Counter_App_Manager.text = textSystem
                                tv_modes_apps.text = textModeType
                                appList.clear()
                                appList.addAll(systemAppList)
                                adapter?.updateList(systemAppList)
                            } else{
                                val textUser = "$numberOfUserApps"
                                val textModeType = "User apps"
                                app_Counter_App_Manager.text = textUser
                                tv_modes_apps.text = textModeType
                                appList.clear()
                                appList.addAll(userAppList)
                                adapter?.updateList(userAppList)

                            }
                        }
/*                        spinner_App_Type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                                val selectedItem = parent.getItemAtPosition(position).toString()

                                if (selectedItem == arrAppType!![0]) {
                                    //User Apps
                                    val textUser = "$numberOfUserApps"
                                    val textModeType = "User apps"
                                    app_Counter_App_Manager.text = textUser
                                    tv_modes_apps.text = textModeType
                                    appList.clear()
                                    appList.addAll(userAppList)
                                    adapter?.updateList(userAppList)
                                } else if (selectedItem == arrAppType!![1]) {
                                    //System Apps
                                    val textSystem = "$numberOfSystemApps"
                                    val textModeType = "System apps"
                                    app_Counter_App_Manager.text = textSystem
                                    tv_modes_apps.text = textModeType
                                    appList.clear()
                                    appList.addAll(systemAppList)
                                    adapter?.updateList(systemAppList)
                                }
                            } // to close the onItemSelected

                            override fun onNothingSelected(parent: AdapterView<*>) {

                            }
                        }




                        spinner_App_Type.isEnabled = true
                        spinner_App_Type.setSelection(0, true)*/
                    } else {
                        app_Counter_App_Manager.text = getString(R.string.No_Apps)
                        apps_recycler_layoout_ll.visibility = View.GONE
                        recycler_view_Apps.visibility = View.GONE
//                        spinner_App_Type.isEnabled = false
                        list_empty_Apps_Appmanager.visibility = View.VISIBLE
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showGoogleBanner(){
        val mAdView7: AdView = view!!.findViewById(R.id.ads_banner_7)
        val preffs = Preferences(requireContext())
        if (!preffs.enableAds) {
            mAdView7.visibility = View.GONE
//            mAdView5 = findViewById(R.id.ads_banner_5)
        } else {
            MobileAds.initialize(requireContext())
            val adRequest = AdRequest.Builder().build()
            mAdView7.loadAd(adRequest)
        }
    }

    companion object {

        fun newInstance(): AppsManagerFragment {
            return AppsManagerFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }


}
