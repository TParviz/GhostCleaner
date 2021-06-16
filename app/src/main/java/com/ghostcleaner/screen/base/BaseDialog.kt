package com.ghostcleaner.screen.base

import android.app.Activity
import android.app.Dialog
import com.ghostcleaner.R

open class BaseDialog(activity: Activity) : Dialog(activity, R.style.AppDialog)