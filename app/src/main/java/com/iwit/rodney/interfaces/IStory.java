package com.iwit.rodney.interfaces;

import java.util.List;

import com.iwit.rodney.entity.Story;

public interface IStory {
	public List<Story> getStories();

	public Story getStoryByMid(String mid);
}
