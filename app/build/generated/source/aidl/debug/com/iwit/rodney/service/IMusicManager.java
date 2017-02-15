/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/xiaokai/Documents/project/Rodney1/app/src/main/aidl/com/iwit/rodney/service/IMusicManager.aidl
 */
package com.iwit.rodney.service;
public interface IMusicManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.iwit.rodney.service.IMusicManager
{
private static final java.lang.String DESCRIPTOR = "com.iwit.rodney.service.IMusicManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.iwit.rodney.service.IMusicManager interface,
 * generating a proxy if needed.
 */
public static com.iwit.rodney.service.IMusicManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.iwit.rodney.service.IMusicManager))) {
return ((com.iwit.rodney.service.IMusicManager)iin);
}
return new com.iwit.rodney.service.IMusicManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_playMusic:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playMusic(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateMusic:
{
data.enforceInterface(DESCRIPTOR);
this.updateMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_pauseMusic:
{
data.enforceInterface(DESCRIPTOR);
this.pauseMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_nextMusic:
{
data.enforceInterface(DESCRIPTOR);
this.nextMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_priorMusic:
{
data.enforceInterface(DESCRIPTOR);
this.priorMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeMusic:
{
data.enforceInterface(DESCRIPTOR);
this.resumeMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_seekMusic:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seekMusic(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPlayMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPlayMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_releaseMedia:
{
data.enforceInterface(DESCRIPTOR);
this.releaseMedia();
reply.writeNoException();
return true;
}
case TRANSACTION_preMusicNotification:
{
data.enforceInterface(DESCRIPTOR);
this.preMusicNotification();
reply.writeNoException();
return true;
}
case TRANSACTION_nextMusisNotification:
{
data.enforceInterface(DESCRIPTOR);
this.nextMusisNotification();
reply.writeNoException();
return true;
}
case TRANSACTION_resumeMusicNotification:
{
data.enforceInterface(DESCRIPTOR);
this.resumeMusicNotification();
reply.writeNoException();
return true;
}
case TRANSACTION_releaseMusicNotificaton:
{
data.enforceInterface(DESCRIPTOR);
this.releaseMusicNotificaton();
reply.writeNoException();
return true;
}
case TRANSACTION_stopMusic:
{
data.enforceInterface(DESCRIPTOR);
this.stopMusic();
reply.writeNoException();
return true;
}
case TRANSACTION_exit:
{
data.enforceInterface(DESCRIPTOR);
this.exit();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.iwit.rodney.service.IMusicManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void playMusic(int pos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(pos);
mRemote.transact(Stub.TRANSACTION_playMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_updateMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void pauseMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pauseMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void nextMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_nextMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void priorMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_priorMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resumeMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void seekMusic(int progress) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(progress);
mRemote.transact(Stub.TRANSACTION_seekMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setPlayMode(int model) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(model);
mRemote.transact(Stub.TRANSACTION_setPlayMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void releaseMedia() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_releaseMedia, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void preMusicNotification() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_preMusicNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void nextMusisNotification() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_nextMusisNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resumeMusicNotification() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resumeMusicNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void releaseMusicNotificaton() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_releaseMusicNotificaton, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopMusic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopMusic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void exit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_playMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_updateMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_pauseMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_nextMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_priorMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_resumeMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_seekMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setPlayMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_releaseMedia = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_preMusicNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_nextMusisNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_resumeMusicNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_releaseMusicNotificaton = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_stopMusic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_exit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
}
public void playMusic(int pos) throws android.os.RemoteException;
public void updateMusic() throws android.os.RemoteException;
public void pauseMusic() throws android.os.RemoteException;
public void nextMusic() throws android.os.RemoteException;
public void priorMusic() throws android.os.RemoteException;
public void resumeMusic() throws android.os.RemoteException;
public void seekMusic(int progress) throws android.os.RemoteException;
public void setPlayMode(int model) throws android.os.RemoteException;
public void releaseMedia() throws android.os.RemoteException;
public void preMusicNotification() throws android.os.RemoteException;
public void nextMusisNotification() throws android.os.RemoteException;
public void resumeMusicNotification() throws android.os.RemoteException;
public void releaseMusicNotificaton() throws android.os.RemoteException;
public void stopMusic() throws android.os.RemoteException;
public void exit() throws android.os.RemoteException;
}
