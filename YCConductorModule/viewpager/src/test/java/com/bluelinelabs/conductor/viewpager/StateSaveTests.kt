package com.bluelinelabs.conductor.viewpager

import android.app.Activity
import android.os.Looper.getMainLooper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction.Companion.with
import com.bluelinelabs.conductor.viewpager.util.TestController
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

  private val pager: ViewPager
  private val pagerAdapter: RouterPagerAdapter
  private val destroyedItems = mutableListOf<Int>()

  init {
    val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    val layout = FrameLayout(activityController.get())
    activityController.get().setContentView(layout)
    val router = Conductor.attachRouter(activityController.get(), FrameLayout(activityController.get()), null)
    val controller = TestController()
    router.setRoot(with(controller))
    pager = ViewPager(activityController.get()).also {
      it.id = ViewCompat.generateViewId()
    }
    layout.addView(pager)
    pager.offscreenPageLimit = 1
    pagerAdapter = object : RouterPagerAdapter(controller) {
      override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
          router.setRoot(with(TestController()))
        }
      }

      override fun getCount(): Int {
        return 20
      }

      override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        destroyedItems.add(position)
      }
    }
    pager.adapter = pagerAdapter
    shadowOf(getMainLooper()).idle()
  }

  @Test
  fun testNoMaxSaves() {
    // Load all pages
    for (i in 0 until pagerAdapter.count) {
      pager.currentItem = i
      shadowOf(getMainLooper()).idle()
    }

    // Ensure all non-visible pages are saved
    assertEquals(
      destroyedItems.size,
      pagerAdapter.savedPages.size()
    )
  }

  @Test
  fun testMaxSavedSet() {
    val maxPages = 3
    pagerAdapter.setMaxPagesToStateSave(maxPages)

    // Load all pages
    for (i in 0 until pagerAdapter.count) {
      pager.currentItem = i
      shadowOf(getMainLooper()).idle()
    }

    val firstSelectedItem = pagerAdapter.count / 2
    for (i in pagerAdapter.count downTo firstSelectedItem) {
      pager.currentItem = i
      shadowOf(getMainLooper()).idle()
    }

    var savedPages = pagerAdapter.savedPages

    // Ensure correct number of pages are saved
    assertEquals(maxPages, savedPages.size())

    // Ensure correct pages are saved
    assertEquals(destroyedItems[destroyedItems.lastIndex], savedPages.keyAt(0))
    assertEquals(destroyedItems[destroyedItems.lastIndex - 1], savedPages.keyAt(1))
    assertEquals(destroyedItems[destroyedItems.lastIndex - 2], savedPages.keyAt(2))

    val secondSelectedItem = 1
    for (i in firstSelectedItem downTo secondSelectedItem) {
      pager.currentItem = i
      shadowOf(getMainLooper()).idle()
    }

    savedPages = pagerAdapter.savedPages

    // Ensure correct number of pages are saved
    assertEquals(maxPages, savedPages.size())

    // Ensure correct pages are saved
    assertEquals(destroyedItems[destroyedItems.lastIndex], savedPages.keyAt(0))
    assertEquals(destroyedItems[destroyedItems.lastIndex - 1], savedPages.keyAt(1))
    assertEquals(destroyedItems[destroyedItems.lastIndex - 2], savedPages.keyAt(2))
  }
}