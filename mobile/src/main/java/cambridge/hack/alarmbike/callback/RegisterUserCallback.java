package cambridge.hack.alarmbike.callback;

/**
 * Created by Duffman on 30/1/16.
 */
public interface RegisterUserCallback {
    void onRegisterFinish();
    void onError(Throwable throwable);
}
