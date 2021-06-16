@file:Suppress("DEPRECATION")

package com.ghostcleaner.screen.base

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BaseDialog(requireActivity())
    }
}