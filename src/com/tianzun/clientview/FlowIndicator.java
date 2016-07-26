
package com.tianzun.clientview;

import com.tianzun.clientview.ViewFlow.ViewSwitchListener;

public interface FlowIndicator extends ViewSwitchListener {

	public void setViewFlow(ViewFlow view);
	public void onScrolled(int h, int v, int oldh, int oldv);
}
