package androidx.fragment.app;

import androidx.annotation.CallSuper;

public class DialogFragment3 extends DialogFragment {

    public DialogFragment3() {
    }

    @CallSuper
    public int show(FragmentTransaction transaction, String tag) {
       // this.mDismissed = false;
       // this.mShownByMe = true;
        transaction.add(this, tag);
       // this.mViewDestroyed = false;
        int  mBackStackId = transaction.commitAllowingStateLoss();
        return mBackStackId;
    }

    @CallSuper
    public void show(FragmentManager manager, String tag) {
//        this.mDismissed = false;
//        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public final void showWithException(FragmentTransaction transaction, String tag) {
        super.show(transaction, tag);
    }
}

