package com.bluelinelabs.conductor.viewpager2

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Router

class RouterViewHolder private constructor(val container: ChangeHandlerFrameLayout) : ViewHolder(container) {
  var currentRouter: Router? = null
  var currentItemPosition = 0
  var currentItemId = 0L
  var attached = false

  companion object {
    operator fun invoke(parent: ViewGroup): RouterViewHolder {
      val container = ChangeHandlerFrameLayout(parent.context)
      container.id = ViewCompat.generateViewId()
      container.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      container.isSaveEnabled = false
      return RouterViewHolder(container)
    }
  }
}