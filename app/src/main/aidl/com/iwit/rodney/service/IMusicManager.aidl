package com.iwit.rodney.service;
interface IMusicManager{
	void playMusic(int pos);
	void updateMusic();
	void pauseMusic();
	void nextMusic();
	void priorMusic();
	void resumeMusic();
	void seekMusic(int progress);
	void setPlayMode(int model);
	void releaseMedia();
	void preMusicNotification();
	void nextMusisNotification();
	void resumeMusicNotification();
	void releaseMusicNotificaton();
	void stopMusic();
	void exit();
} 