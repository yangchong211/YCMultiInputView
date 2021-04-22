package com.bluelinelabs.conductor;

import android.os.Bundle;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.internal.LifecycleHandler;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.bluelinelabs.conductor.util.AttachFakingFrameLayout;
import com.bluelinelabs.conductor.util.MockChangeHandler;
import com.bluelinelabs.conductor.util.TestController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ReattachCaseTests {

    private ActivityProxy activityProxy;
    private Router router;

    public void createActivityController(Bundle savedInstanceState) {
        activityProxy = new ActivityProxy().create(savedInstanceState).start().resume();
        router = Conductor.attachRouter(activityProxy.getActivity(), activityProxy.getView(), savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(new TestController()));
        }
    }

    @Before
    public void setup() {
        createActivityController(null);
    }

    @Test
    public void testNeedsAttachingOnPauseAndOrientation() {
        final TestController controllerA = new TestController();
        final TestController controllerB = new TestController();

        router.pushController(RouterTransaction.with(controllerA)
            .pushChangeHandler(MockChangeHandler.defaultHandler())
            .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());

        sleepWakeDevice();

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());

        router.pushController(RouterTransaction.with(controllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());

        activityProxy.rotate();
        router.rebindIfNeeded();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
    }

    @Test
    public void testChildNeedsAttachOnPauseAndOrientation() {
        final Controller controllerA = new TestController();
        final Controller childController = new TestController();
        final Controller controllerB = new TestController();

        router.pushController(RouterTransaction.with(controllerA)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        Router childRouter = controllerA.getChildRouter((ViewGroup) controllerA.getView().findViewById(TestController.VIEW_ID));
        childRouter.pushController(RouterTransaction.with(childController)
            .pushChangeHandler(MockChangeHandler.defaultHandler())
            .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertTrue(controllerA.isAttached());
        assertTrue(childController.isAttached());
        assertFalse(controllerB.isAttached());

        sleepWakeDevice();

        assertTrue(controllerA.isAttached());
        assertTrue(childController.isAttached());
        assertFalse(controllerB.isAttached());

        router.pushController(RouterTransaction.with(controllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertFalse(childController.isAttached());
        assertTrue(controllerB.isAttached());

        activityProxy.rotate();
        router.rebindIfNeeded();

        assertFalse(controllerA.isAttached());
        assertFalse(childController.isAttached());
        assertTrue(childController.getNeedsAttach());
        assertTrue(controllerB.isAttached());
    }

    @Test
    public void testChildHandleBackOnOrientation() {
        final TestController controllerA = new TestController();
        final TestController controllerB = new TestController();
        final TestController childController = new TestController();

        router.pushController(RouterTransaction.with(controllerA)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());
        assertFalse(childController.isAttached());

        router.pushController(RouterTransaction.with(controllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        Router childRouter = controllerB.getChildRouter((ViewGroup)controllerB.getView().findViewById(TestController.VIEW_ID));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(childController)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        activityProxy.rotate();
        router.rebindIfNeeded();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        router.handleBack();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertFalse(childController.isAttached());

        router.handleBack();

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());
        assertFalse(childController.isAttached());
    }

    // Attempt to test https://github.com/bluelinelabs/Conductor/issues/86#issuecomment-231381271
    @Test
    public void testReusedChildRouterHandleBackOnOrientation() {
        TestController controllerA = new TestController();
        TestController controllerB = new TestController();
        TestController childController = new TestController();

        router.pushController(RouterTransaction.with(controllerA)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());
        assertFalse(childController.isAttached());

        router.pushController(RouterTransaction.with(controllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        Router childRouter = controllerB.getChildRouter((ViewGroup)controllerB.getView().findViewById(TestController.VIEW_ID));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(childController)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        router.handleBack();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertFalse(childController.isAttached());

        childController = new TestController();
        childRouter.pushController(RouterTransaction.with(childController)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        activityProxy.rotate();
        router.rebindIfNeeded();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        router.handleBack();

        childController = new TestController();
        childRouter.pushController(RouterTransaction.with(childController)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertTrue(childController.isAttached());

        router.handleBack();

        assertFalse(controllerA.isAttached());
        assertTrue(controllerB.isAttached());
        assertFalse(childController.isAttached());

        router.handleBack();

        assertTrue(controllerA.isAttached());
        assertFalse(controllerB.isAttached());
        assertFalse(childController.isAttached());
    }

    // Attempt to test https://github.com/bluelinelabs/Conductor/issues/367
    @Test
    public void testViewIsAttachedAfterStartedActivityIsRecreated() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();

        router.setRoot(RouterTransaction.with(controller1));
        assertTrue(controller1.isAttached());

        // Lock screen
        Bundle bundle = new Bundle();
        activityProxy.pause().saveInstanceState(bundle).stop(false);

        // Push a 2nd controller, which will rotate the screen once it unlocked
        router.pushController(RouterTransaction.with(controller2));
        assertTrue(controller2.isAttached());
        assertTrue(controller2.getNeedsAttach());

        // Unlock screen and rotate
        activityProxy.start();
        activityProxy.rotate();

        assertTrue(controller2.isAttached());
    }

    @Test
    public void testPopMiddleControllerAttaches() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        Controller controller3 = new TestController();

        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));
        router.pushController(RouterTransaction.with(controller3));
        router.popController(controller2);

        assertFalse(controller1.isAttached());
        assertFalse(controller2.isAttached());
        assertTrue(controller3.isAttached());

        controller1 = new TestController();
        controller2 = new TestController();
        controller3 = new TestController();

        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));
        router.pushController(RouterTransaction.with(controller3).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler()));
        router.popController(controller2);

        assertTrue(controller1.isAttached());
        assertFalse(controller2.isAttached());
        assertTrue(controller3.isAttached());
    }

    @Test
    public void testPendingChanges() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();

        ActivityProxy activityProxy = new ActivityProxy().create(null);
        AttachFakingFrameLayout container = new AttachFakingFrameLayout(activityProxy.getActivity());
        container.setNeedDelayPost(true); // to simulate calling posts after resume

        activityProxy.setView(container);

        Router router = Conductor.attachRouter(activityProxy.getActivity(), container, null);
        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));

        activityProxy.start().resume();
        container.setNeedDelayPost(false);

        assertTrue(controller2.isAttached());
    }

    @Test
    public void testPendingChangesAfterRotation() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();

        // first activity
        ActivityProxy activityProxy = new ActivityProxy().create(null);
        AttachFakingFrameLayout container1 = new AttachFakingFrameLayout(activityProxy.getActivity());

        container1.setNeedDelayPost(true); // delay forever as view will be removed
        activityProxy.setView(container1);

        // first attachRouter: Conductor.attachRouter(activityProxy.getActivity(), container1, null)
        LifecycleHandler lifecycleHandler = LifecycleHandler.install(activityProxy.getActivity());
        Router router = lifecycleHandler.getRouter(container1, null);
        router.setRoot(RouterTransaction.with(controller1));

        // setup controllers
        router.pushController(RouterTransaction.with(controller2));

        // simulate setRequestedOrientation in activity onCreate
        activityProxy.start().resume();
        Bundle savedState = new Bundle();
        activityProxy.saveInstanceState(savedState).pause().stop(true);

        // recreate activity and view
        activityProxy = new ActivityProxy().create(savedState);
        AttachFakingFrameLayout container2 = new AttachFakingFrameLayout(activityProxy.getActivity());
        activityProxy.setView(container2);

        // second attach router with the same lifecycleHandler (do manually as Roboelectric recreates retained fragments)
        // Conductor.attachRouter(activityProxy.getActivity(), container2, savedState);
        router = lifecycleHandler.getRouter(container2, savedState);
        router.rebindIfNeeded();

        activityProxy.start().resume();

        assertTrue(controller2.isAttached());
    }

    @Test
    public void testHostAvailableDuringRotation() {
        final Controller controllerA = new TestController();
        final Controller childControllerA = new TestController();
        final Controller controllerB = new TestController();
        final Controller childControllerB = new TestController();

        router.pushController(RouterTransaction.with(controllerA)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        Router childRouterA = controllerA.getChildRouter((ViewGroup) controllerA.getView().findViewById(TestController.VIEW_ID));
        childRouterA.pushController(RouterTransaction.with(childControllerA)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertNotNull(controllerA.getActivity());
        assertNotNull(childControllerA.getActivity());

        router.pushController(RouterTransaction.with(controllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        Router childRouterB = controllerB.getChildRouter((ViewGroup) controllerB.getView().findViewById(TestController.VIEW_ID));
        childRouterB.pushController(RouterTransaction.with(childControllerB)
                .pushChangeHandler(MockChangeHandler.defaultHandler())
                .popChangeHandler(MockChangeHandler.defaultHandler()));

        assertNotNull(controllerA.getActivity());
        assertNotNull(childControllerA.getActivity());
        assertNotNull(controllerB.getActivity());
        assertNotNull(childControllerB.getActivity());

        activityProxy.rotate();

        assertNotNull(controllerA.getActivity());
        assertNotNull(childControllerA.getActivity());
        assertNotNull(controllerB.getActivity());
        assertNotNull(childControllerB.getActivity());

        router.rebindIfNeeded();

        assertNotNull(controllerA.getActivity());
        assertNotNull(childControllerA.getActivity());
        assertNotNull(controllerB.getActivity());
        assertNotNull(childControllerB.getActivity());
    }

    private void sleepWakeDevice() {
        activityProxy.saveInstanceState(new Bundle()).pause();
        activityProxy.resume();
    }

}
