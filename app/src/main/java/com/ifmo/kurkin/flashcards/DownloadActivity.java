package com.ifmo.kurkin.flashcards;

import java.io.IOException;

/**
 * Created by kurkin on 14.12.15.
 */
public class DownloadActivity extends ProgressTaskActivity {

    @Override
    protected ProgressTask createTask() {
        return new DownloadTask(this);
    }

    static class DownloadTask extends ProgressTask {

        DownloadTask(ProgressTaskActivity activity) {
            super(activity);
        }

        @Override
        protected void runTask() throws IOException {
            DBCreator dbCreator = new DBCreator(appContext);
            dbCreator.execute();
        }
    }

}
