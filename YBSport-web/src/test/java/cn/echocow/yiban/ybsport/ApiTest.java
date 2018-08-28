package cn.echocow.yiban.ybsport;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestOptions;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.report.ReportOptions;

/**
 * -----------------------------
 * 创建接口测试模块，模块化测试。
 *
 * @author EchoCow
 * @program YBSport
 * @description api 接口测试
 * @date 2018-08-24 18:13
 * <p>
 * -----------------------------
 **/
@SuppressWarnings("all")
public class ApiTest {
    public static void main(String[] args) {
        new ApiTest().run();
    }

    private Vertx vertx;

    public void run() {
        TestOptions options = new TestOptions().addReporter(new ReportOptions().setTo("console"));
        TestSuite suite = TestSuite.create(getClass().getName());
        suite.before(context -> vertx = Vertx.vertx());
        suite.after(context -> vertx.close(context.asyncAssertSuccess()));
        suite.test("indexApi", context -> {
            HttpClient client = vertx.createHttpClient();
            Async async = context.async();
            client.getNow(8888, "localhost", "/", resp -> {
                resp.bodyHandler(body -> System.out.println(body.toString("UTF-8")));
                client.close();
                async.complete();
            });
        });
        suite.run(options);
    }
}
