package com.iwit.rodney.ui.view;

import java.util.Arrays;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Navigation implements OnCheckedChangeListener {
	/**
	 * 导航栏选择监听器
	 */
	public static interface OnNavigationChangeListener {
		/**
		 * 导航栏被选择时的回调方法
		 * 
		 * @param itemIndex
		 *            表示第几项被选中。项的顺序由Navigation构造方法中的ids参数决定。
		 */
		void onNavigationChanged(int itemIndex);
	}

	private int[] ids;
	private OnNavigationChangeListener lis;
	private RadioButton rbs[];

	/**
	 * 初始化导航栏，ids和titles的长度必须与itemCount相等且大于1
	 * 
	 * @param rg
	 *            导航栏RadioGroup
	 * @param itemCount
	 *            导航栏项数
	 * @param ids
	 *            导航栏各项RadioButton的id
	 * @param titles
	 *            导航栏各项的标题
	 */
	public Navigation(RadioGroup rg, int itemCount, final int[] ids,
			String[] titles, final OnNavigationChangeListener lis) {
		if (itemCount < 2 || ids.length != itemCount
				|| titles.length != itemCount) {
			throw new IllegalArgumentException();
		}
		this.ids = ids;
		this.lis = lis;
		rbs = new RadioButton[itemCount];
		// init RadioButton
		RadioButton rb = null;
		View v = null;
		for (int i = 0; i < itemCount; i++) {
			v = rg.findViewById(ids[i]);
			if (v == null || !(v instanceof RadioButton)) {
				throw new RuntimeException("找不到RadioButton");
			}
			rb = (RadioButton) v;
			if (i == 0) {
				rb.setChecked(true);
			}
			rb.setText(titles[i]);
			rbs[i] = rb;
		}
		// init RadioGroup
		rg.setOnCheckedChangeListener(this);
	}

	/**
	 * 选中导航栏的某一项
	 * 
	 * @param index
	 *            选中项的索引。顺序由Navigation构造方法中的ids参数决定。
	 */
	public void checkItem(int index) {
		if (index < 0 || index >= ids.length) {
			throw new IllegalArgumentException();
		}
		rbs[index].setChecked(true);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (lis != null) {
			int index = Arrays.binarySearch(ids, checkedId);
			if (index < 0) {
				throw new RuntimeException("index 必须大于0");
			}
			lis.onNavigationChanged(index);
		}
	}
}
