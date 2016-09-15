package com.example.mediacontrols;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.example.mediacontrols.R;

public class MediaPlayerService extends Service{

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_forward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaPlayer mMediaPLayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void handleIntent(Intent intent) {
        if(intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();

        if(action.equalsIgnoreCase(ACTION_PLAY)){
            mController.getTransportControls().play();
        } else if(action.equalsIgnoreCase(ACTION_PAUSE)){
            mController.getTransportControls().pause();
        } else if(action.equalsIgnoreCase(ACTION_FAST_FORWARD)){
            mController.getTransportControls().fastForward();
        } else if(action.equalsIgnoreCase(ACTION_REWIND)){
            mController.getTransportControls().rewind();
        } else if(action.equalsIgnoreCase(ACTION_PREVIOUS)){
            mController.getTransportControls().skipToPrevious();
        } else if(action.equalsIgnoreCase(ACTION_NEXT)){
            mController.getTransportControls().skipToNext();
        } else if(action.equalsIgnoreCase(ACTION_STOP)){
            mController.getTransportControls().stop();
        }
    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {

        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification(Notification.Action action) {


        Notification.MediaStyle style = new Notification.MediaStyle();

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lakalle);
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_playing)
                .setLargeIcon(bm)
                .setContentTitle("Titulo")
                .setContentText("Contenido").setDeleteIntent(pendingIntent)
                .setStyle(style);

        //mBuilder.setLargeIcon(bm);

        MediaMetadata.Builder builder2 = new MediaMetadata.Builder();
        builder2.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bm);
        mSession.setMetadata(builder2.build());

        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Forward", ACTION_FAST_FORWARD));
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mManager == null) {
            initMediaSessions();
        }

        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {

        mMediaPLayer = new MediaPlayer();

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController = new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();

                Log.e("MediaPlayerService", "onPlay");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                // TODO Auto-generated method stub
                super.onPause();

                Log.e("MediaPlayerService", "onPause");
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            }

       /* @Override
        public void onSkipToNext() {
            // TODO Auto-generated method stub
            super.onSkipToNext();

            Log.e("MediaPlayerService", "onSkipToNext");
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        }

        @Override
        public void onSkipToPrevious() {
            // TODO Auto-generated method stub
            super.onSkipToPrevious();

            Log.e("MediaPlayerService", "onSkipToPrevious");
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
        }

        @Override
        public void onFastForward() {
            // TODO Auto-generated method stub
            super.onFastForward();

            Log.e("MediaPlayerService", "onFastForward");
        }

        @Override
        public void onRewind() {
            // TODO Auto-generated method stub
            super.onRewind();

            Log.e("MediaPlayerService", "onRewind");
        }*/

            @Override
            public void onStop() {
                // TODO Auto-generated method stub
                super.onStop();

                Log.e("MediaPlayerService", "onStop");

                NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.cancel(1);

                Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(intent);
            }

            @Override
            public void onSeekTo(long pos) {
                // TODO Auto-generated method stub
                super.onSeekTo(pos);
            }

            @Override
            public void onSetRating(Rating rating) {
                // TODO Auto-generated method stub
                super.onSetRating(rating);
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {

        mSession.release();
        return super.onUnbind(intent);
    }
}
