package com.wilin.yx.sounddetectview;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

/**
 * 
 * 
 * http://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes
 *
 */
public class SavedState extends BaseSavedState {
	
	public int stateToSave;
	public String curLevel;
	
	public SavedState(Parcelable superState) {
		super(superState);
	}

	private SavedState(Parcel in) {
		super(in);
		this.stateToSave = in.readInt();
		this.curLevel = in.readString();
	}
}
