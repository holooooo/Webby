package project.Controller;

import shitty.web.annotation.Controller;
import shitty.web.annotation.Get;
import shitty.web.annotation.Param;
import shitty.web.annotation.Post;
import shitty.web.http.HttpResponseUtil;

/**
 * program: shitty
 * description:
 * author: Makise
 * create: 2019-04-17 16:10
 **/
@Controller("/demo")
public class DemoController {
    @Post("/{name}/{count}")
    public HttpResponseUtil hello(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        StringBuilder hello = new StringBuilder("hello,").append(name).append("\n");
        for (; count > 0; count--) {
            hello.append("again\n");
        }
        hello.append(", token is ").append(token);
        return new HttpResponseUtil().putText(hello.toString());
    }

    @Post("/{name}")
    public HttpResponseUtil get(@Param("name") String name, @Param("token") String token) {
        //do something
        return new HttpResponseUtil().putText("hello" + name + token);
    }

    @Get("/download/{filename}")
    public HttpResponseUtil hello(@Param("filename") String filename) {
        return new HttpResponseUtil().putFile("data/" + filename);
    }
}
