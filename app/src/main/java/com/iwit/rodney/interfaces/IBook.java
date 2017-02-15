package com.iwit.rodney.interfaces;

import java.util.List;

import android.content.Context;

import com.iwit.rodney.entity.Book;

public interface IBook {
	List<Book> getLocalBooks(Context context);

	List<Book> getStoreBooks();
}
