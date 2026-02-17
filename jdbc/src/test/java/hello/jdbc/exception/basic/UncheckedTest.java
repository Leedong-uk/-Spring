package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UncheckedTest{

    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }


    static class Repository {
        public void call () {
            throw new MyUncheckedException("ex");
        }
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나 , 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 됩니다
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 잡음 ",e);
            }
        }
    }



}
