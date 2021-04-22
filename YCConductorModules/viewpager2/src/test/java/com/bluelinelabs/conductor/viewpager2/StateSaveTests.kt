package com.bluelinelabs.conductor.viewpager2

import android.app.Activity
import android.os.Looper.getMainLooper
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction.Companion.with
import com.bluelinelabs.conductor.viewpager2.util.TestController
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class StateSaveTests {

  private val pager: ViewPager2
  private val adapter: RouterStateAdapter
  private val destroyedItems = mutableListOf<Int>()

  init {
    val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    val layout = FrameLayout(activityController.get())
    activityController.get().setContentView(layout)
    val router = Conductor.attachRouter(activityController.get(), FrameLayout(activityController.get()), null)
    val controller = TestController()
    router.setRoot(with(controller))
    pager = ViewPager2(activityController.get()).also {
      it.id = ViewCompat.generateViewId()
    }
    layout.addView(pager)
    pager.offscreenPageLimit = 1
    adapter = object : RouterStateAdapter(controller) {
      override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
          router.setRoot(with(TestController()))
        }
      }

      override fun getItemCount(): Int {
        return 20
      }

      override fun onViewDetachedFromWindow(holder: RouterViewHolder) {
        super.onViewDetachedFromWindow(holder)

        destroyedItems.add(holder.currentItemPosition)
      }
    }
    pager.adapter = adapter
    shadowOf(getMainLooper()).idle()
  }

  @Test
  fun testNoMaxSaves() {
    // Load all pages
    for (i in 0 until adapter.itemCount) {
      pager.setCurrentItem(i, false)
      shadowOf(getMainLooper()).idle()
    }

    // Ensure all non-visible pages are saved
    assertEquals(
      destroyedItems.size,
      adapter.savedPageHistory.size
    )
  }

  @Test
  fun testMaxSavedSet() {
    val maxPages = 3
    adapter.setMaxPagesToStateSave(maxPages)

    // Load all pages
    for (i in 0 until adapter.itemCount) {
      pager.setCurrentItem(i, false)
      shadowOf(getMainLooper()).idle()
    }

    val firstSelectedItem = adapter.itemCount / 2
    for (i in adapter.itemCount downTo firstSelectedItem) {
      pager.setCurrentItem(i, false)
      shadowOf(getMainLooper()).idle()
    }

    var savedPages = adapter.savedPageHistory

    // Ensure correct number of pages are saved
    assertEquals(maxPages, savedPages.size)

    // Ensure correct pages are saved
    assertEquals(destroyedItems[destroyedItems.lastIndex], savedPages[savedPages.lastIndex].toInt())
    assertEquals(destroyedItems[destroyedItems.lastIndex - 1], savedPages[savedPages.lastIndex - 1].toInt())
    assertEquals(destroyedItems[destroyedItems.lastIndex - 2], savedPages[savedPages.lastIndex - 2].toInt())

    val secondSelectedItem = 1
    for (i in adapter.itemCount downTo secondSelectedItem) {
      pager.setCurrentItem(i, false)
      shadowOf(getMainLooper()).idle()
    }

    savedPages = adapter.savedPageHistory

    // Ensure correct number of pages are saved
    assertEquals(maxPages, savedPages.size)

    // Ensure correct pages are saved
    assertEquals(destroyedItems[destroyedItems.lastIndex], savedPages[savedPages.lastIndex].toInt())
    assertEquals(destroyedItems[destroyedItems.lastIndex - 1], savedPages[savedPages.lastIndex - 1].toInt())
    assertEquals(destroyedItems[destroyedItems.lastIndex - 2], savedPages[savedPages.lastIndex - 2].toInt())
  }
}