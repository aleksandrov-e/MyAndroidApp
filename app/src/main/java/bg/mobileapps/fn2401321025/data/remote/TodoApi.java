package bg.mobileapps.fn2401321025.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

/** REST услугата, от която се взима предложение за нова задача. */
public interface TodoApi {

    @GET("todos/random")
    Call<TodoDto> randomTodo();
}
