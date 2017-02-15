package com.iwit.rodney.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iwit.rodney.entity.Book;
import com.iwit.rodney.event.EventBook;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.adapter.BookAdapter;

import de.greenrobot.event.EventBus;

public class BookStoreFragment extends RecyclerViewFragment {
	private List<Book> books = new ArrayList<Book>();

	public BookStoreFragment() {
		super(RecyclerViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	@Override
	protected Adapter<?> getRecyclerViewAdapter() {
		getDataFromDb();
		return new BookAdapter(getActivity(), books,false);
	}

	private void getDataFromDb() {
		books.clear();
		List<Book> bs = IManager.getIBookBuss().getStoreBooks();
		if (bs != null) {
			books.addAll(bs);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onPause() {
		IManager.getIDownload(getActivity().getApplicationContext()).pauseAllDownloads();
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	public void onEventMainThread(EventBook event) {
		if (!event.isResult()) {
			Toast.makeText(getActivity(), event.getMsg(), Toast.LENGTH_SHORT)
					.show();
		} else {
			getDataFromDb();
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onFragmentVisible() {
		super.onFragmentVisible();
		notifyDataSetChanged();
	}

}
