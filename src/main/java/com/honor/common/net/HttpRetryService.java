package com.honor.common.net;

public class HttpRetryService {

    public static class Retry {
        private IRetry retry;
        private int count;

        public void retry() {
            if (!isFinish())
                retry.retry();
        }

        public int getCount() {
            return count;
        }

        public boolean isFinish() {
            return count == 0;
        }

        public interface IRetry {

            void retry();
        }


        public Retry(IRetry retry, int count) {
            this.count = count;
            this.retry = retry;
        }

    }

}
