package com.iwit.rodney.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.iwit.rodney.entity.Book;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.adapter.BookAdapter;

public class BookFragment extends RecyclerViewFragment {
	private static boolean firstToast = true;
	private List<Book> books = new ArrayList<Book>();

	public BookFragment() {
		super(RecyclerViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	@Override
	protected Adapter<?> getRecyclerViewAdapter() {
		updateData();
		return new BookAdapter(getActivity(), books, true);
	}

	private void updateData() {
		List<Book> bs = IManager.getIBookBuss().getLocalBooks(getActivity());
		books.clear();
		if (bs != null && bs.size() > 0) {
			books.addAll(bs);
		} else {
			if (firstToast) {
				Toast.makeText(getActivity(), "没有已下载的书籍，请到书城下载",
						Toast.LENGTH_LONG).show();
				firstToast = false;
			}
		}
	}

	@Override
	protected void onFragmentVisible() {
		super.onFragmentVisible();
		updateData();
		notifyDataSetChanged();
	}
	

	@Override
	public void deleteCancel() {
		//如果有delete 为1 的 就更新
		boolean  isFinish = true;
		for(Book book : books){
			if(book.getDeleted() ==1){
				book.setDeleted(0);
				isFinish = false;
			}
		}
		if(isFinish){
			getActivity().finish();
		}else{
			notifyDataSetChanged();
		}
	}
}
