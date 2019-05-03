package project.Controller;

import webby.utils.Result;
import webby.web.annotation.Controller;
import webby.web.annotation.Get;
import webby.web.annotation.Param;
import webby.web.annotation.Post;
import webby.web.http.HttpResponseUtil;

/**
 * program: webby
 * description:
 * author: Makise
 * create: 2019-04-17 16:10
 **/
@Controller("/demo")
public class DemoController {
    @Post("/{name}/{count}")
    public Result hello(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        StringBuilder hello = new StringBuilder("hello,").append(name).append("\n");
        for (; count > 0; count--) {
            hello.append("again\n");
        }
        hello.append(", token is ").append(token);
        return Result.success(hello.toString());
    }

    @Get("/yali/{name}")
    public Result yali(@Param("name") String name, @Param("token") String token) throws InterruptedException {
        //模拟延时操作
//        Thread.sleep(1000);
        return Result.success("hello" + name + token);
    }

    @Get("/download/{filename}")
    public HttpResponseUtil hello(@Param("filename") String filename) {
        return new HttpResponseUtil().putFile("data/" + filename);
    }
}
