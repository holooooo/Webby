package project.Controller;

import shitty.utils.Result;
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

    @Get("/yali/{name}")
    public HttpResponseUtil yali(@Param("name") String name, @Param("token") String token) throws InterruptedException {
        //模拟延时操作
//        Thread.sleep(1000);
        return new HttpResponseUtil().putJson(Result.success("hello" + name + token));
    }

    @Get("/download/{filename}")
    public HttpResponseUtil hello(@Param("filename") String filename) {
        return new HttpResponseUtil().putFile("data/" + filename);
    }
}
